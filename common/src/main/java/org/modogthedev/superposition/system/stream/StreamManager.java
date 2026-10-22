package org.modogthedev.superposition.system.stream;

import foundry.veil.api.network.VeilPacketManager;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.core.SuperpositionConstants;
import org.modogthedev.superposition.networking.packet.StreamFramesS2CPacket;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.system.signal.SignalManager;
import org.modogthedev.superposition.system.signal.data.EncodedData;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Relays the frames of byte streams to the players that need them.
 */
public class StreamManager {
    private static final int VALIDATION_INTERVAL = 10;
    private static final int TOKEN_BURST = 10;

    private static final Map<UUID, Stream> streams = new HashMap<>();
    private static final Map<UUID, Endpoint> sources = new HashMap<>();
    private static final Map<UUID, Endpoint> sinks = new HashMap<>();
    private static final Map<UUID, Subscription> subscriptions = new HashMap<>();
    private static final Queue<PendingFrame> pending = new ConcurrentLinkedQueue<>();
    private static int received = 0;
    private static int relayed = 0;

    public static synchronized void tick(ServerLevel level) {
        MinecraftServer server = level.getServer();
        if (level != server.overworld()) {
            return;
        }
        int ticks = server.getTickCount();

        sources.values().removeIf(endpoint -> expired(endpoint.tick, ticks));
        sinks.values().removeIf(endpoint -> expired(endpoint.tick, ticks));
        streams.values().removeIf(stream -> expired(stream.lastFrame, ticks));

        Iterator<Map.Entry<UUID, Subscription>> iterator = subscriptions.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, Subscription> entry = iterator.next();
            ServerPlayer player = server.getPlayerList().getPlayer(entry.getKey());
            if (player == null) {
                iterator.remove();
                continue;
            }
            Subscription subscription = entry.getValue();
            if (Math.abs(ticks - subscription.lastValidation) >= VALIDATION_INTERVAL) {
                validate(player, subscription, ticks);
            }
        }

        Map<UUID, List<StreamFramesS2CPacket.Frame>> toSend = new HashMap<>();
        PendingFrame frame;
        while ((frame = pending.poll()) != null) {
            received++;
            ServerPlayer sender = server.getPlayerList().getPlayer(frame.sender);
            if (sender == null) {
                continue;
            }
            // A frame is heard by every source in range of the sender, and is quieter the further away they are
            Vec3 pos = sender.getEyePosition();
            for (Endpoint source : sources.values()) {
                if (source.dimension != sender.level().dimension()) {
                    continue;
                }
                double distance = source.pos.distanceTo(pos);
                if (distance >= SuperpositionConstants.microphoneRange) {
                    continue;
                }
                int gain = (int) Math.round(255 * (1 - distance / SuperpositionConstants.microphoneRange));
                relay(source.stream, frame, gain, ticks, toSend);
            }
        }

        toSend.forEach((uuid, frames) -> {
            ServerPlayer player = server.getPlayerList().getPlayer(uuid);
            if (player != null) {
                VeilPacketManager.player(player).sendPacket(new StreamFramesS2CPacket(frames));
            }
        });

        if (Superposition.DEBUG && ticks % 20 == 0 && (received > 0 || !sources.isEmpty() || !sinks.isEmpty())) {
            int valid = 0;
            for (Subscription subscription : subscriptions.values()) {
                valid += subscription.valid.size();
            }
            Superposition.LOGGER.info("Streams: {} frames received, {} relayed, {} sources, {} sinks, {} valid subscriptions", received, relayed, sources.size(), sinks.size(), valid);
        }
        if (ticks % 20 == 0) {
            received = 0;
            relayed = 0;
        }
    }

    private static void relay(UUID id, PendingFrame frame, int gain, int ticks, Map<UUID, List<StreamFramesS2CPacket.Frame>> toSend) {
        Stream stream = streams.computeIfAbsent(id, uuid -> new Stream(ticks));
        if (stream.talker != null && !stream.talker.equals(frame.sender) && ticks - stream.lastFrame < SuperpositionConstants.streamTalkerTimeout) {
            return;
        }
        stream.talker = frame.sender;

        stream.tokens = Math.min(TOKEN_BURST, stream.tokens + (ticks - stream.lastRefill) * SuperpositionConstants.maxStreamFramesPerSecond / 20f);
        stream.lastRefill = ticks;
        if (stream.tokens < 1) {
            return;
        }
        stream.tokens--;
        stream.lastFrame = ticks;

        StreamFramesS2CPacket.Frame out = new StreamFramesS2CPacket.Frame(id, stream.sequence++, gain, frame.data);
        for (Map.Entry<UUID, Subscription> entry : subscriptions.entrySet()) {
            if (entry.getValue().valid.contains(id)) {
                toSend.computeIfAbsent(entry.getKey(), uuid -> new ArrayList<>()).add(out);
                relayed++;
            }
        }
    }

    /**
     * Queues a frame of a players voice to be relayed on every stream that has a source in range of them. This is safe to call from any thread.
     *
     * @param sender The player the frame originates from
     * @param data   The frame, this is dropped if it is larger than {@link SuperpositionConstants#maxStreamFrameSize}
     */
    public static void pushFrame(UUID sender, byte[] data) {
        if (data.length == 0 || data.length > SuperpositionConstants.maxStreamFrameSize) {
            return;
        }
        pending.add(new PendingFrame(sender, data));
    }

    /**
     * Marks a position that players may send frames to the given stream from. This must be called every tick the source is active.
     */
    public static synchronized void markSource(Level level, UUID stream, Vec3 pos) {
        if (level.getServer() != null) {
            sources.put(stream, new Endpoint(stream, level.dimension(), pos, level.getServer().getTickCount()));
        }
    }

    /**
     * Marks a position that players may receive frames of the given stream from. This must be called every tick the sink is active.
     *
     * @param sink A unique id for the sink
     */
    public static synchronized void markSink(Level level, UUID stream, UUID sink, Vec3 pos) {
        if (level.getServer() != null) {
            sinks.put(sink, new Endpoint(stream, level.dimension(), pos, level.getServer().getTickCount()));
        }
    }

    public static synchronized void subscribe(ServerPlayer player, Set<UUID> wanted) {
        Subscription subscription = subscriptions.computeIfAbsent(player.getUUID(), uuid -> new Subscription());
        subscription.wanted.clear();
        for (UUID stream : wanted) {
            if (subscription.wanted.size() >= SuperpositionConstants.maxStreamSubscriptions) {
                break;
            }
            subscription.wanted.add(stream);
        }
        validate(player, subscription, player.server.getTickCount());
    }

    // The tick is also checked for being in the future, as the tick count restarts with the server
    private static boolean expired(int tick, int ticks) {
        return tick > ticks || ticks - tick > SuperpositionConstants.streamExpiry;
    }

    private static void validate(ServerPlayer player, Subscription subscription, int ticks) {
        subscription.lastValidation = ticks;
        subscription.valid.clear();
        for (UUID stream : subscription.wanted) {
            if (canReceive(player, stream)) {
                subscription.valid.add(stream);
            }
        }
    }

    private static boolean canReceive(ServerPlayer player, UUID stream) {
        Vec3 pos = player.position();
        float range = SuperpositionConstants.speakerRange + 8;
        for (Endpoint sink : sinks.values()) {
            if (sink.stream.equals(stream) && sink.dimension == player.level().dimension() && sink.pos.distanceToSqr(pos) < range * range) {
                return true;
            }
        }

        Map<UUID, Signal> signals = SignalManager.transmittedSignals.get(player.level());
        if (signals == null) {
            return false;
        }
        for (Signal signal : signals.values()) {
            if (signal.getEncodedData() instanceof EncodedData.StreamData data && data.id().equals(stream)) {
                if (signal.getPos().distanceSquared(pos.x, pos.y, pos.z) < (signal.getMaxDist() + 25) * (signal.getMaxDist() + 25)) {
                    return true;
                }
            }
        }
        return false;
    }

    private record PendingFrame(UUID sender, byte[] data) {
    }

    private record Endpoint(UUID stream, ResourceKey<Level> dimension, Vec3 pos, int tick) {
    }

    private static class Subscription {
        private final Set<UUID> wanted = new HashSet<>();
        private final Set<UUID> valid = new HashSet<>();
        private int lastValidation;
    }

    private static class Stream {
        private UUID talker;
        private int sequence;
        private int lastFrame;
        private int lastRefill;
        private float tokens = TOKEN_BURST;

        private Stream(int ticks) {
            this.lastFrame = ticks;
            this.lastRefill = ticks;
        }
    }
}

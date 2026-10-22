package org.modogthedev.superposition.system.stream;

import foundry.veil.api.network.VeilPacketManager;
import org.modogthedev.superposition.core.SuperpositionConstants;
import org.modogthedev.superposition.networking.packet.StreamSubscribeC2SPacket;

import java.util.*;

/**
 * Holds the most recent frames of every stream the client is subscribed to.
 * Frames are kept for {@link SuperpositionConstants#maxStreamDelay} ticks so that they can be read behind the source.
 */
public class ClientStreamManager {
    private static final Map<UUID, ArrayDeque<Frame>> streams = new HashMap<>();
    private static final Set<UUID> subscribed = new HashSet<>();
    private static long nextOrder = 0;
    private static volatile int ticks = 0;

    /**
     * @param order    Increases by one for every frame the client receives
     * @param tick     The client tick the frame was received on
     * @param sequence The index of the frame in the stream
     * @param gain     How loud the frame is, from 0 to 1
     */
    public record Frame(long order, int tick, int sequence, float gain, byte[] data) {
    }

    public static synchronized void tick() {
        ticks++;
        Iterator<ArrayDeque<Frame>> iterator = streams.values().iterator();
        while (iterator.hasNext()) {
            ArrayDeque<Frame> frames = iterator.next();
            while (!frames.isEmpty() && ticks - frames.peekFirst().tick > SuperpositionConstants.maxStreamDelay + SuperpositionConstants.streamExpiry) {
                frames.removeFirst();
            }
            if (frames.isEmpty()) {
                iterator.remove();
            }
        }
    }

    public static synchronized void receive(UUID stream, int sequence, int gain, byte[] data) {
        if (!subscribed.contains(stream)) {
            return;
        }
        streams.computeIfAbsent(stream, uuid -> new ArrayDeque<>()).addLast(new Frame(nextOrder++, ticks, sequence, gain / 255f, data));
    }

    /**
     * Collects every frame that should be read by now for a reader that is the given amount of ticks behind the stream.
     *
     * @param after The order of the last frame that was read
     * @return The order of the last frame that was collected, or after if there were none
     */
    public static synchronized long collect(UUID stream, int delay, long after, List<Frame> out) {
        ArrayDeque<Frame> frames = streams.get(stream);
        if (frames == null) {
            return after;
        }
        for (Frame frame : frames) {
            if (frame.tick + delay > ticks) {
                break;
            }
            if (frame.order > after) {
                out.add(frame);
                after = frame.order;
            }
        }
        return after;
    }

    /**
     * Informs the server of the streams the client should receive, if they have changed.
     */
    public static synchronized void subscribe(Set<UUID> wanted) {
        if (subscribed.equals(wanted)) {
            return;
        }
        subscribed.clear();
        subscribed.addAll(wanted);
        streams.keySet().retainAll(subscribed);
        VeilPacketManager.server().sendPacket(new StreamSubscribeC2SPacket(new HashSet<>(subscribed)));
    }

    public static synchronized void clear() {
        streams.clear();
        subscribed.clear();
    }
}

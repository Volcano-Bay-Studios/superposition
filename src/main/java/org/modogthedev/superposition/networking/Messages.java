package org.modogthedev.superposition.networking;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.networking.packet.BlockEntityModificationC2SPacket;
import org.modogthedev.superposition.networking.packet.ParticleSyncS2CPacket;
import org.modogthedev.superposition.networking.packet.SignalSyncS2CPacket;

public class Messages {
    private static SimpleChannel INSTANCE;

    private static int packetId = 0;
    private static int id() {
        return packetId++;
    }

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(Superposition.MODID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        net.messageBuilder(ParticleSyncS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ParticleSyncS2CPacket::new)
                .encoder(ParticleSyncS2CPacket::toBytes)
                .consumerMainThread(ParticleSyncS2CPacket::handle)
                .add()
        ;
        net.messageBuilder(SignalSyncS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SignalSyncS2CPacket::new)
                .encoder(SignalSyncS2CPacket::toBytes)
                .consumerMainThread(SignalSyncS2CPacket::handle)
                .add();

        net.messageBuilder(BlockEntityModificationC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(BlockEntityModificationC2SPacket::new)
                .encoder(BlockEntityModificationC2SPacket::toBytes)
                .consumerMainThread(BlockEntityModificationC2SPacket::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static <MSG> void sendToClients(MSG message) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), message);
    }
}

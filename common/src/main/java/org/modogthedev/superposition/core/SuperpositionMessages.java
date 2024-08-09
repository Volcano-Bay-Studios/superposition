package org.modogthedev.superposition.core;

import dev.architectury.networking.NetworkChannel;
import dev.architectury.networking.NetworkManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.networking.packet.BlockEntityModificationC2SPacket;
import org.modogthedev.superposition.networking.packet.FilterItemModificationC2SPacket;
import org.modogthedev.superposition.networking.packet.SignalSyncS2CPacket;

public class SuperpositionMessages {
    private static NetworkChannel INSTANCE;

    public static void register() {
        NetworkChannel net = NetworkChannel.create(new ResourceLocation(Superposition.MODID, "messages"));

        INSTANCE = net;

        net.register(SignalSyncS2CPacket.class, SignalSyncS2CPacket::toBytes, SignalSyncS2CPacket::new, SignalSyncS2CPacket::handle);

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, Superposition.id("block_entity_modification"), (buf, ctx) -> new BlockEntityModificationC2SPacket(buf).handle(() -> ctx));
        net.register(BlockEntityModificationC2SPacket.class, BlockEntityModificationC2SPacket::toBytes, BlockEntityModificationC2SPacket::new, BlockEntityModificationC2SPacket::handle);

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, Superposition.id("filter_item_modification"), (buf, ctx) -> new FilterItemModificationC2SPacket(buf).handle(() -> ctx));
        net.register(FilterItemModificationC2SPacket.class, FilterItemModificationC2SPacket::toBytes, FilterItemModificationC2SPacket::new, FilterItemModificationC2SPacket::handle);
    }
    public static void registerClient() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, Superposition.id("signal_sync"), (buf, ctx) -> new SignalSyncS2CPacket(buf).handle(() -> ctx));
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.sendToPlayer(player, message);
    }

    public static <MSG> void sendToClients(MSG message, MinecraftServer server) {
        INSTANCE.sendToPlayers(server.getPlayerList().getPlayers(), message);
    }
}

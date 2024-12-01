package org.modogthedev.superposition.networking;

import foundry.veil.api.network.VeilPacketManager;
import org.modogthedev.superposition.networking.handler.SuperpositionClientPacketHandler;
import org.modogthedev.superposition.networking.handler.SuperpositionServerPacketHandler;
import org.modogthedev.superposition.networking.packet.*;

public class SuperpositionMessages {

    private static final VeilPacketManager INSTANCE = VeilPacketManager.create("1");

    public static void register() {
        INSTANCE.registerClientbound(SignalSyncS2CPacket.TYPE,SignalSyncS2CPacket.CODEC, SuperpositionClientPacketHandler::handleSignalSync);
        INSTANCE.registerClientbound(CableSyncS2CPacket.TYPE, CableSyncS2CPacket.CODEC, SuperpositionClientPacketHandler::handleCableSync);

        INSTANCE.registerServerbound(BlockEntityModificationC2SPacket.TYPE, BlockEntityModificationC2SPacket.CODEC, SuperpositionServerPacketHandler::handleBlockEntityModification);
        INSTANCE.registerServerbound(FilterItemModificationC2SPacket.TYPE, FilterItemModificationC2SPacket.CODEC, SuperpositionServerPacketHandler::handleFilterItemModification);
        INSTANCE.registerServerbound(PlayerDropCableC2SPacket.TYPE, PlayerDropCableC2SPacket.CODEC, SuperpositionServerPacketHandler::handleDropCable);
        INSTANCE.registerServerbound(PlayerGrabCableC2SPacket.TYPE, PlayerGrabCableC2SPacket.CODEC, SuperpositionServerPacketHandler::handleGrabCable);
    }

//    public static void registerClient() {
//        NetworkManager.registerReceiver(NetworkManager.Side.S2C, Superposition.id("signal_sync"), (buf, ctx) -> new SignalSyncS2CPacket(buf).handle(() -> ctx));
//        NetworkManager.registerReceiver(NetworkManager.Side.S2C, Superposition.id("cable_sync"), (buf, ctx) -> new CableSyncS2CPacket(buf).handle(() -> ctx));
//    }
}

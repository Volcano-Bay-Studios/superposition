package org.modogthedev.superposition.networking.handler;

import foundry.veil.api.network.handler.ClientPacketContext;
import net.minecraft.world.level.Level;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.networking.packet.*;
import org.modogthedev.superposition.screens.ScreenManager;
import org.modogthedev.superposition.system.cable.Cable;
import org.modogthedev.superposition.system.cable.CableManager;
import org.modogthedev.superposition.system.cable.SuperpositionClientInterpolationState;
import org.modogthedev.superposition.system.card.Card;
import org.modogthedev.superposition.system.signal.ClientSignalManager;

import java.util.Map;
import java.util.UUID;

public class SuperpositionClientPacketHandler {

    public static void handleSignalSync(SignalSyncS2CPacket packet, ClientPacketContext ctx) {
        Level level = ctx.level();
        if (level == null) {
            Superposition.LOGGER.warn("Server sent signal sync for unknown level");
            return;
        }

        ClientSignalManager.processSignal(level, packet.getBuf());
    }

    public static void handleInscriberScreen(InscriberScreenS2CPacket packet, ClientPacketContext ctx) {
        Level level = ctx.level();
        if (level == null) {
            Superposition.LOGGER.warn("Server sent inscriber screen for unknown level");
        }

        ScreenManager.openInscriber(new Card(packet.tag().getCompound("card")), packet.pos());
    }

    public static void handleBlockSignalSync(BlockSignalSyncS2CPacket packet, ClientPacketContext ctx) {
        Level level = ctx.level();
        if (level == null) {
            Superposition.LOGGER.warn("Server sent block signal for unknown level");
            return;
        }

        ClientSignalManager.processBlockBoundSignal(level, packet.getBuf());
    }

    public static void handleCableSync(CableSyncS2CPacket packet, ClientPacketContext ctx) {
        Level level = ctx.level();
        if (level == null) {
            Superposition.LOGGER.warn("Server sent cable sync for unknown level");
            return;
        }

        UUID id = packet.getId();
        if (packet.isRemove()) {
            CableManager.removeCable(level, id);
            return;
        }
        Map<UUID, Cable> cables = CableManager.getCables(level);

        if (cables != null) {
            Cable cable = cables.get(id);
            if (cable != null) {
                cable.read(id,packet.getBuffer(),level);
                return;
            }
        }
        Cable cable = Cable.readNew(id,packet.getBuffer(),level,false);

        CableManager.addCable(cable, level);
    }

    public static void handleInterpolationState(InterpolationStateS2CPacket packet, ClientPacketContext ctx) {
        Level level = ctx.level();
        SuperpositionClientInterpolationState.INSTANCE.receiveInfo(packet.msSinceLast(), packet.gameTick(), packet.stopped());

    }
}

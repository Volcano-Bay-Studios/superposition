package org.modogthedev.superposition.networking.handler;

import foundry.veil.api.network.handler.ClientPacketContext;
import net.minecraft.world.level.Level;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.networking.packet.BlockSignalSyncS2CPacket;
import org.modogthedev.superposition.networking.packet.CableSyncS2CPacket;
import org.modogthedev.superposition.networking.packet.InscriberScreenS2CPacket;
import org.modogthedev.superposition.networking.packet.SignalSyncS2CPacket;
import org.modogthedev.superposition.screens.ScreenManager;
import org.modogthedev.superposition.system.cable.Cable;
import org.modogthedev.superposition.system.cable.CableManager;
import org.modogthedev.superposition.system.card.Card;
import org.modogthedev.superposition.system.signal.ClientSignalManager;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class SuperpositionClientPacketHandler {

    public static void handleSignalSync(SignalSyncS2CPacket packet, ClientPacketContext ctx) {
        Level level = ctx.level();
        if (level == null) {
            Superposition.LOGGER.warn("Server sent signal sync for unknown level");
            return;
        }

        ClientSignalManager.processTag(level, packet.getBuf());
    }

    public static void handleInscriberScreen(InscriberScreenS2CPacket packet, ClientPacketContext ctx) {
        Level level = ctx.level();
        if (level == null) {
            Superposition.LOGGER.warn("Server sent inscriber screen for unknown level");
            return;
        }

        ScreenManager.openInscriber(new Card(packet.tag().getCompound("card")),packet.pos());
    }

    public static void handleBlockSignalSync(BlockSignalSyncS2CPacket packet, ClientPacketContext ctx) {
        Level level = ctx.level();
        if (level == null) {
            Superposition.LOGGER.warn("Server sent block signal for unknown level");
            return;
        }

        ClientSignalManager.processBlockBoundTag(level, packet.getBuf());
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

        Cable cable = Objects.requireNonNull(packet.getCable());
        cable.setLevel(level);
        Map<UUID, Cable> cables = CableManager.getCables(level);
        if (cables != null) {
            Cable old = cables.get(id);
            if (old != null) {
                old.updateFromCable(cable, false);
                return;
            }
        }

        CableManager.addCable(cable, level);
    }
}

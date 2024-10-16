package org.modogthedev.superposition.networking.packet;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import org.modogthedev.superposition.core.SuperpositionMessages;
import org.modogthedev.superposition.system.cable.Cable;
import org.modogthedev.superposition.system.cable.CableClipResult;
import org.modogthedev.superposition.system.cable.CableManager;
import oshi.util.tuples.Pair;

import java.util.function.Supplier;

public class PlayerGrabCableC2SPacket {

    public PlayerGrabCableC2SPacket() {
    }

    public PlayerGrabCableC2SPacket(FriendlyByteBuf buf) {
    }

    public void toBytes(FriendlyByteBuf buf) {
    }

    public void handle(Supplier<NetworkManager.PacketContext> supplier) {
        var context = supplier.get();

        context.queue(() -> {
            var player = context.getPlayer();

            CableClipResult cableClipResult = new CableClipResult(player.getEyePosition(), 8, player.level());
            Pair<Cable, Cable.Point> rayCast = cableClipResult.rayCastForClosest(player.getEyePosition().add(player.getEyePosition().add(player.getForward().subtract(player.getEyePosition())).scale(5)), .7f);
            if (rayCast != null) {
                Cable cable = rayCast.getA();
                cable.addPlayerHoldingPoint(player.getUUID(), cable.getPointIndex(rayCast.getB()));
                if (!player.level().isClientSide) {
                    CableSyncS2CPacket packet = new CableSyncS2CPacket(cable, CableManager.getCableUUID(player.level(), cable), false);
                    for (ServerPlayer player1 : player.level().getServer().getPlayerList().getPlayers()) {
                        float maxDistance = cable.getPoints().size() + 100;
                        if (cable.getPoints().get(0).getPosition().distanceTo(player.position()) < maxDistance)
                            SuperpositionMessages.sendToPlayer(packet, player1);
                    }
                }
            }
        });
    }
}

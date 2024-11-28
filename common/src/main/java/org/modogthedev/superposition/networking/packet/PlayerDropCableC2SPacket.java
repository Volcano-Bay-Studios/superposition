package org.modogthedev.superposition.networking.packet;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import org.modogthedev.superposition.core.SuperpositionMessages;
import org.modogthedev.superposition.system.cable.Cable;
import org.modogthedev.superposition.system.cable.CableManager;

import java.util.function.Supplier;

public class PlayerDropCableC2SPacket {

    public PlayerDropCableC2SPacket() {
    }

    public PlayerDropCableC2SPacket(FriendlyByteBuf buf) {
    }

    public void toBytes(FriendlyByteBuf buf) {
    }

    public void handle(Supplier<NetworkManager.PacketContext> supplier) {
        var context = supplier.get();

        context.queue(() -> {
            var player = context.getPlayer();

            for (Cable cable : CableManager.getLevelCables(player.level())) {
                if (cable.hasPlayerHolding(player.getId())) {
                    cable.stopPlayerDrag(player.getId());
                    CableSyncS2CPacket packet = new CableSyncS2CPacket(cable, CableManager.getCableUUID(player.level(), cable), false);
                    for (ServerPlayer player1 : player.level().getServer().getPlayerList().getPlayers()) {
                        float maxDistance = cable.getPoints().size() + 100;
                        if (cable.getPoints().getFirst().getPosition().distanceTo(player.position()) < maxDistance)
                            SuperpositionMessages.sendToPlayer(packet, player1);
                    }
                    break;
                }
            }
        });
    }
}

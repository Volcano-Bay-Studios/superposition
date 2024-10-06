package org.modogthedev.superposition.networking.packet;

import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.modogthedev.superposition.core.SuperpositionMessages;
import org.modogthedev.superposition.system.cable.Cable;
import org.modogthedev.superposition.system.cable.CableManager;
import org.modogthedev.superposition.util.SyncedBlockEntity;

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

            for (Cable cable : CableManager.getCables(player.level())) {
                if (cable.hasPlayerHolding(player.getUUID())) {
                    cable.stopPlayerDrag(player.getUUID());
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

package org.modogthedev.superposition.networking.packet;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import org.modogthedev.superposition.system.cable.Cable;
import org.modogthedev.superposition.system.cable.CableManager;

import java.util.UUID;
import java.util.function.Supplier;

public class CableSyncS2CPacket {

    private Cable ourCable;
    private UUID ourUUID;
    private boolean isPlayerTracked;
    public CableSyncS2CPacket(FriendlyByteBuf buf) {
        isPlayerTracked = buf.readBoolean();
        ourUUID = buf.readUUID();
        ourCable = Cable.fromBytes(buf,null);
    }

    public CableSyncS2CPacket(Cable cable, UUID uuid, boolean isPlayerTracked) {
        this.ourCable = cable;
        this.ourUUID = uuid;
        this.isPlayerTracked = isPlayerTracked;
    }


    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(isPlayerTracked);
        buf.writeUUID(ourUUID);
        ourCable.toBytes(buf);
    }

    public void handle(Supplier<NetworkManager.PacketContext> supplier) {
        var ctx = supplier.get();

        ctx.queue(() -> {
            ourCable.setLevel(ctx.getPlayer().level());
            if (!isPlayerTracked) {
                if (CableManager.getCablesMap(ctx.getPlayer().level()).get(ctx.getPlayer().level()).containsKey(ourUUID)) {
                    Cable cable = CableManager.getCablesMap(ctx.getPlayer().level()).get(ctx.getPlayer().level()).get(ourUUID); //TODO: Network Cables
                    cable.updateFromCable(ourCable);
                } else {
                    CableManager.addCable(ourCable, ctx.getPlayer().level(), ourUUID);
                }
            } else {
                Player player = ctx.getPlayer().level().getPlayerByUUID(ourUUID);
                if (CableManager.getPlayersDraggingCablesMap(ctx.getPlayer().level()).containsKey(player)) {
                    CableManager.getPlayersDraggingCablesMap(ctx.getPlayer().level()).get(player).updateFromCable(ourCable);
                } else {
                    CableManager.getPlayersDraggingCablesMap(ctx.getPlayer().level()).put(player,ourCable);
                }
            }
        });
    }
}

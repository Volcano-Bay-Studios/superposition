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
    private boolean remove = false;

    public CableSyncS2CPacket(FriendlyByteBuf buf) {
        ourUUID = buf.readUUID();
        remove = buf.readBoolean();
        if (!remove)
            ourCable = Cable.fromBytes(buf, null);
    }

    public CableSyncS2CPacket(Cable cable, UUID uuid, boolean remove) {
        this.ourCable = cable;
        this.ourUUID = uuid;
        this.remove = remove;
    }


    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUUID(ourUUID);
        buf.writeBoolean(remove);
        if (!remove)
            ourCable.toBytes(buf);
    }

    public void handle(Supplier<NetworkManager.PacketContext> supplier) {
        var ctx = supplier.get();

        ctx.queue(() -> {
            if (!remove)
                ourCable.setLevel(ctx.getPlayer().level());
            if (!remove) {
                if (CableManager.getCablesMap(ctx.getPlayer().level()).get(ctx.getPlayer().level()).containsKey(ourUUID)) {
                    Cable cable = CableManager.getCablesMap(ctx.getPlayer().level()).get(ctx.getPlayer().level()).get(ourUUID);
                    cable.updateFromCable(ourCable);
                } else {
                    CableManager.addCable(ourCable, ctx.getPlayer().level(), ourUUID);
                }
            } else {
                CableManager.getCablesMap(ctx.getPlayer().level()).get(ctx.getPlayer().level()).remove(ourUUID);
            }
        });
    }
}

package org.modogthedev.superposition.networking.packet;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.modogthedev.superposition.system.cable.Cable;
import org.modogthedev.superposition.system.cable.CableManager;

import java.util.Map;
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
            Level level = ctx.getPlayer().level();
            if (remove) {
                CableManager.removeCable(ourUUID);
                return;
            }

            ourCable.setLevel(level);
            Map<UUID, Cable> cables = CableManager.getCables(level);
            if (cables != null) {
                Cable cable = cables.get(ourUUID);
                if (cable != null) {
                    cable.updateFromCable(ourCable);
                    return;
                }
            }

            CableManager.addCable(ourCable, level, ourUUID);
        });
    }
}

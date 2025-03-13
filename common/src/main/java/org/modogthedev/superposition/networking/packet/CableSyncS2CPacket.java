package org.modogthedev.superposition.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.Nullable;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.system.cable.Cable;

import java.util.UUID;

public class CableSyncS2CPacket implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<CableSyncS2CPacket> TYPE = new CustomPacketPayload.Type<>(Superposition.id("cable_sync"));
    public static final StreamCodec<FriendlyByteBuf, CableSyncS2CPacket> CODEC = StreamCodec.of((buffer, value) -> value.toBytes(buffer), CableSyncS2CPacket::new);

    private final UUID id;
    private final Cable cable;
    private final boolean remove;

    public CableSyncS2CPacket(UUID id) {
        this.id = id;
        this.cable = null;
        this.remove = true;
    }

    public CableSyncS2CPacket(Cable cable) {
        this.id = cable.getId();
        this.cable = cable;
        this.remove = false;
    }

    private CableSyncS2CPacket(FriendlyByteBuf buf) {
        this.id = buf.readUUID();
        this.remove = buf.readBoolean();
        this.cable = this.remove ? null : Cable.fromBytes(this.id, buf, null, false);
    }

    private void toBytes(FriendlyByteBuf buf) {
        buf.writeUUID(this.id);
        buf.writeBoolean(this.remove);
        if (!this.remove) {
            this.cable.toBytes(buf);
        }
    }

    public UUID getId() {
        return this.id;
    }

    public boolean isRemove() {
        return this.remove;
    }

    public @Nullable Cable getCable() {
        return this.cable;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

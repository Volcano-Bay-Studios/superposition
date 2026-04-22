package org.modogthedev.superposition.networking.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.system.cable.Cable;

import java.util.UUID;

public class CableSyncS2CPacket implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<CableSyncS2CPacket> TYPE = new CustomPacketPayload.Type<>(Superposition.id("cable_sync"));
    public static final StreamCodec<ByteBuf, CableSyncS2CPacket> CODEC =
            ByteBufCodecs.BYTE_ARRAY.map(CableSyncS2CPacket::new, CableSyncS2CPacket::toBytes);
    private final UUID id;
    private final boolean remove;
    private final FriendlyByteBuf buffer;

    private Cable cable;

    public CableSyncS2CPacket(UUID id) {
        this.id = id;
        this.remove = true;
        this.buffer = null;
    }

    public CableSyncS2CPacket(Cable cable) {
        this.id = cable.getId();
        this.remove = false;
        this.buffer = null;
        this.cable = cable;
    }

    private CableSyncS2CPacket(byte[] data) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.wrappedBuffer(data));
        this.id = buf.readUUID();
        this.remove = buf.readBoolean();
        this.buffer = buf;
    }

    private byte[] toBytes() {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeUUID(this.id);
        buf.writeBoolean(this.remove);
        if (cable != null) {
            cable.write(buf);
        }
        byte[] data = new byte[buf.writerIndex()];
        buf.readBytes(data);
        return data;
    }

    public FriendlyByteBuf getBuffer() {
        return buffer;
    }

    public UUID getId() {
        return this.id;
    }

    public boolean isRemove() {
        return this.remove;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

package org.modogthedev.superposition.networking.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.system.signal.Signal;

import java.util.Collection;

public class SignalSyncS2CPacket implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SignalSyncS2CPacket> TYPE = new CustomPacketPayload.Type<>(Superposition.id("signal_sync"));
    public static final StreamCodec<ByteBuf, SignalSyncS2CPacket> CODEC = ByteBufCodecs.BYTE_ARRAY.map(SignalSyncS2CPacket::new, SignalSyncS2CPacket::getData);
    private final byte[] data;

    public SignalSyncS2CPacket(Collection<Signal> signals) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeVarInt(signals.size());
        for (Signal signal : signals) {
            signal.write(buf);
        }
        this.data = new byte[buf.writerIndex()];
        buf.readBytes(this.data);
    }

    private SignalSyncS2CPacket(byte[] data) {
        this.data = data;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public FriendlyByteBuf getBuf() {
        return new FriendlyByteBuf(Unpooled.wrappedBuffer(this.data));
    }

    public byte[] getData() {
        return this.data;
    }
}

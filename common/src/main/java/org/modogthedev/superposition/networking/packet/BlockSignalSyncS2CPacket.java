package org.modogthedev.superposition.networking.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.system.signal.Signal;

import java.util.Collection;

/**
 * This packet will be used in the future to synchronize some block signals that may behave differently on the server. Currently, it is used to synchronise peripheral signals in computers.
 */
public class BlockSignalSyncS2CPacket implements CustomPacketPayload {

    public static final Type<BlockSignalSyncS2CPacket> TYPE = new Type<>(Superposition.id("block_signal_sync"));
    public static final StreamCodec<ByteBuf, BlockSignalSyncS2CPacket> CODEC = ByteBufCodecs.BYTE_ARRAY.map(BlockSignalSyncS2CPacket::new, BlockSignalSyncS2CPacket::getData);
    private final byte[] data;

    public BlockSignalSyncS2CPacket(Collection<Signal> signals, BlockPos pos) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeBlockPos(pos);
        buf.writeVarInt(signals.size());
        for (Signal signal : signals) {
            signal.write(buf);
        }
        this.data = new byte[buf.writerIndex()];
        buf.readBytes(this.data);
    }

    private BlockSignalSyncS2CPacket(byte[] data) {
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

package org.modogthedev.superposition.networking.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.modogthedev.superposition.Superposition;

/**
 * This packet will be used in the future to synchronize some block signals that may behave differently on the server. Currently, it is used to synchronise peripheral signals in computers.
 */
public class BlockDataSyncS2CPacket implements CustomPacketPayload {

    public static final Type<BlockDataSyncS2CPacket> TYPE = new Type<>(Superposition.id("block_data_sync"));
    public static final StreamCodec<ByteBuf, BlockDataSyncS2CPacket> CODEC = ByteBufCodecs.COMPOUND_TAG.map(BlockDataSyncS2CPacket::new, BlockDataSyncS2CPacket::getData);
    private final CompoundTag data;
    private final BlockPos pos;
    public BlockDataSyncS2CPacket(CompoundTag tag) {
        this.data = tag.getCompound("tag");
        this.pos = new BlockPos(tag.getInt("x"),tag.getInt("y"),tag.getInt("z"));
    }

    public BlockDataSyncS2CPacket(CompoundTag tag, BlockPos pos) {
        CompoundTag data = new CompoundTag();
        data.put("tag",tag);
        data.putInt("x",pos.getX());
        data.putInt("y",pos.getY());
        data.putInt("z",pos.getZ());
        this.data = data;
        this.pos = pos;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public CompoundTag getData() {
        return this.data;
    }

    public BlockPos getPos() {
        return pos;
    }
}

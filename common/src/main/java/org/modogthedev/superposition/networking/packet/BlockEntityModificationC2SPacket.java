package org.modogthedev.superposition.networking.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.modogthedev.superposition.Superposition;

public record BlockEntityModificationC2SPacket(CompoundTag tag, BlockPos pos) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<BlockEntityModificationC2SPacket> TYPE = new CustomPacketPayload.Type<>(Superposition.id("block_entity_modification"));
    public static final StreamCodec<FriendlyByteBuf, BlockEntityModificationC2SPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.COMPOUND_TAG,
            BlockEntityModificationC2SPacket::tag,
            BlockPos.STREAM_CODEC,
            BlockEntityModificationC2SPacket::pos,
            BlockEntityModificationC2SPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

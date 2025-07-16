package org.modogthedev.superposition.networking.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.modogthedev.superposition.Superposition;

public record InscriberScreenS2CPacket(CompoundTag tag, BlockPos pos) implements CustomPacketPayload {

    public static final Type<InscriberScreenS2CPacket> TYPE = new Type<>(Superposition.id("inscriber_screen_data"));
    public static final StreamCodec<FriendlyByteBuf, InscriberScreenS2CPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.COMPOUND_TAG,
            InscriberScreenS2CPacket::tag,
            BlockPos.STREAM_CODEC,
            InscriberScreenS2CPacket::pos,
            InscriberScreenS2CPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

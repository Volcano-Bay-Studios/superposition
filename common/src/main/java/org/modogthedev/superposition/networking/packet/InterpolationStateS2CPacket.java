package org.modogthedev.superposition.networking.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.modogthedev.superposition.Superposition;

public record InterpolationStateS2CPacket(int msSinceLast, int gameTick, boolean stopped) implements CustomPacketPayload {

    public static final Type<InterpolationStateS2CPacket> TYPE = new Type<>(Superposition.id("interpolation_state"));
    public static final StreamCodec<FriendlyByteBuf, InterpolationStateS2CPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            InterpolationStateS2CPacket::msSinceLast,
            ByteBufCodecs.INT,
            InterpolationStateS2CPacket::gameTick,
            ByteBufCodecs.BOOL,
            InterpolationStateS2CPacket::stopped,
            InterpolationStateS2CPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

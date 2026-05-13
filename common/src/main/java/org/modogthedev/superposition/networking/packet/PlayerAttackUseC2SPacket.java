package org.modogthedev.superposition.networking.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;
import org.modogthedev.superposition.Superposition;

public record PlayerAttackUseC2SPacket(BlockPos blockPos, Vec3 location) implements CustomPacketPayload {
    public static final Type<PlayerAttackUseC2SPacket> TYPE = new Type<>(Superposition.id("player_attack_use"));
    public static final StreamCodec<FriendlyByteBuf, PlayerAttackUseC2SPacket> CODEC = StreamCodec.of((buffer, value) -> {
                buffer.writeBlockPos(value.blockPos);
                buffer.writeVec3(value.location);
            },
            buffer -> new PlayerAttackUseC2SPacket(
                    buffer.readBlockPos(),buffer.readVec3()));


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

package org.modogthedev.superposition.networking.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.modogthedev.superposition.Superposition;

public record PlayerPlaceWidgetC2SPacket(BlockPos pos, int x, int y, ResourceLocation location) implements CustomPacketPayload {

    public static final Type<PlayerPlaceWidgetC2SPacket> TYPE = new Type<>(Superposition.id("player_place_widget"));
    public static final StreamCodec<FriendlyByteBuf, PlayerPlaceWidgetC2SPacket> CODEC = StreamCodec.of((buffer, value) -> {
                buffer.writeBlockPos(value.pos);
                buffer.writeVarInt(value.x);
                buffer.writeVarInt(value.y);
                buffer.writeResourceLocation(value.location);
            },
            buffer -> new PlayerPlaceWidgetC2SPacket(
                    buffer.readBlockPos(),
                    buffer.readVarInt(),
                    buffer.readVarInt(),
                    buffer.readResourceLocation()));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

package org.modogthedev.superposition.networking.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.modogthedev.superposition.Superposition;

public enum PlayerDropCableC2SPacket implements CustomPacketPayload {

    INSTANCE;

    public static final CustomPacketPayload.Type<PlayerDropCableC2SPacket> TYPE = new CustomPacketPayload.Type<>(Superposition.id("player_drop_cable"));
    public static final StreamCodec<ByteBuf, PlayerDropCableC2SPacket> CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

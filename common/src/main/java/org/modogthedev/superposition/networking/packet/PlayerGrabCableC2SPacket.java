package org.modogthedev.superposition.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.modogthedev.superposition.Superposition;

import java.util.UUID;

public record PlayerGrabCableC2SPacket(UUID id) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<PlayerGrabCableC2SPacket> TYPE = new CustomPacketPayload.Type<>(Superposition.id("player_grab_cable"));
    public static final StreamCodec<FriendlyByteBuf, PlayerGrabCableC2SPacket> CODEC = StreamCodec.of((buffer, value) -> buffer.writeUUID(value.id()), buffer -> new PlayerGrabCableC2SPacket(buffer.readUUID()));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

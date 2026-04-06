package org.modogthedev.superposition.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.Nullable;
import org.modogthedev.superposition.Superposition;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public record PlayerPlugCableC2SPacket(UUID id, @Nullable String port, boolean isOut) implements CustomPacketPayload {

    public static final Type<PlayerPlugCableC2SPacket> TYPE = new Type<>(Superposition.id("player_plug_cable"));
    public static final StreamCodec<FriendlyByteBuf, PlayerPlugCableC2SPacket> CODEC = StreamCodec.of((buffer, value) -> {
                buffer.writeUUID(value.id());
                if (value.port != null) {
                    buffer.writeBoolean(true);
                    buffer.writeInt(value.port.length());
                    buffer.writeCharSequence(value.port, StandardCharsets.UTF_8);
                } else {
                    buffer.writeBoolean(false);
                }
                buffer.writeBoolean(value.isOut);
            },
            buffer -> new PlayerPlugCableC2SPacket(
                    buffer.readUUID(),
                    buffer.readBoolean() ? (String) buffer.readCharSequence(buffer.readInt(), StandardCharsets.UTF_8) : null,
                    buffer.readBoolean()));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

package org.modogthedev.superposition.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.core.SuperpositionConstants;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public record StreamSubscribeC2SPacket(Set<UUID> streams) implements CustomPacketPayload {

    public static final Type<StreamSubscribeC2SPacket> TYPE = new Type<>(Superposition.id("stream_subscribe"));
    public static final StreamCodec<FriendlyByteBuf, StreamSubscribeC2SPacket> CODEC = StreamCodec.of((buffer, value) -> {
                buffer.writeVarInt(value.streams.size());
                for (UUID stream : value.streams) {
                    buffer.writeUUID(stream);
                }
            },
            buffer -> {
                int count = Math.min(buffer.readVarInt(), SuperpositionConstants.maxStreamSubscriptions);
                Set<UUID> streams = new HashSet<>();
                for (int i = 0; i < count; i++) {
                    streams.add(buffer.readUUID());
                }
                return new StreamSubscribeC2SPacket(streams);
            });

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

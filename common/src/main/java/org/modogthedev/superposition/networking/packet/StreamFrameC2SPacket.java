package org.modogthedev.superposition.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.core.SuperpositionConstants;

/**
 * A frame of the sending players microphone. The server decides which streams it is heard by.
 */
public record StreamFrameC2SPacket(byte[] frame) implements CustomPacketPayload {

    public static final Type<StreamFrameC2SPacket> TYPE = new Type<>(Superposition.id("stream_frame"));
    public static final StreamCodec<FriendlyByteBuf, StreamFrameC2SPacket> CODEC = StreamCodec.of((buffer, value) -> buffer.writeByteArray(value.frame),
            buffer -> new StreamFrameC2SPacket(buffer.readByteArray(SuperpositionConstants.maxStreamFrameSize)));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

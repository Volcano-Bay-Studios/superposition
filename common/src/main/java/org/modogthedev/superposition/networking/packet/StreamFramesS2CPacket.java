package org.modogthedev.superposition.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.core.SuperpositionConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record StreamFramesS2CPacket(List<Frame> frames) implements CustomPacketPayload {

    /**
     * @param gain How loud the frame is, from 0 to 255
     */
    public record Frame(UUID stream, int sequence, int gain, byte[] data) {
    }

    public static final Type<StreamFramesS2CPacket> TYPE = new Type<>(Superposition.id("stream_frames"));
    public static final StreamCodec<FriendlyByteBuf, StreamFramesS2CPacket> CODEC = StreamCodec.of((buffer, value) -> {
                buffer.writeVarInt(value.frames.size());
                for (Frame frame : value.frames) {
                    buffer.writeUUID(frame.stream);
                    buffer.writeVarInt(frame.sequence);
                    buffer.writeByte(frame.gain);
                    buffer.writeByteArray(frame.data);
                }
            },
            buffer -> {
                int count = buffer.readVarInt();
                List<Frame> frames = new ArrayList<>(count);
                for (int i = 0; i < count; i++) {
                    frames.add(new Frame(buffer.readUUID(), buffer.readVarInt(), buffer.readUnsignedByte(), buffer.readByteArray(SuperpositionConstants.maxStreamFrameSize)));
                }
                return new StreamFramesS2CPacket(frames);
            });

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

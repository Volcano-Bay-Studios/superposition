package org.modogthedev.superposition.networking.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.modogthedev.superposition.Superposition;

import java.nio.charset.StandardCharsets;

public record ChangeWidgetC2SPacket(ResourceLocation location) implements CustomPacketPayload {
    public static final Type<ChangeWidgetC2SPacket> TYPE = new Type<>(Superposition.id("player_change_widget"));
    public static final StreamCodec<FriendlyByteBuf, ChangeWidgetC2SPacket> CODEC = StreamCodec.of((buffer, value) -> {
                String namespace = value.location.getNamespace();
                String path = value.location.getPath();
                buffer.writeInt(namespace.length());
                buffer.writeCharSequence(namespace, StandardCharsets.UTF_8);
                buffer.writeInt(path.length());
                buffer.writeCharSequence(path, StandardCharsets.UTF_8);
            },
            buffer -> {
                int namespaceLength = buffer.readInt();
                CharSequence namespace = buffer.readCharSequence(namespaceLength, StandardCharsets.UTF_8);
                int pathLength = buffer.readInt();
                CharSequence path = buffer.readCharSequence(pathLength, StandardCharsets.UTF_8);

                return new ChangeWidgetC2SPacket(ResourceLocation.fromNamespaceAndPath((String) namespace, (String) path));
            });


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

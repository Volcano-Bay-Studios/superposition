package org.modogthedev.superposition.networking.packet;

import dev.architectury.networking.NetworkManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import org.modogthedev.superposition.system.signal.ClientSignalManager;

import java.util.function.Supplier;

public class SignalSyncS2CPacket {
    private final CompoundTag signals;


    public SignalSyncS2CPacket(FriendlyByteBuf buf) {
        signals = buf.readNbt();
    }

    public SignalSyncS2CPacket(CompoundTag signals) {
        this.signals = signals;

    }


    public void toBytes(FriendlyByteBuf buf) {
        buf.writeNbt(signals);
    }

    public void handle(Supplier<NetworkManager.PacketContext> supplier) {
        var ctx = supplier.get();

        ctx.queue(() -> {
            // Here we are client side.
            // Be very careful not to access client-only classes here! (like Minecraft) because
            // this packet needs to be available server-side too
            ClientSignalManager.processTag(signals);
        });
    }
}

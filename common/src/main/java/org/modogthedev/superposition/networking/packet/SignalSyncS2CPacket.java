package org.modogthedev.superposition.networking.packet;

import dev.architectury.networking.NetworkManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import org.modogthedev.superposition.system.signal.ClientSignalManager;
import org.modogthedev.superposition.system.signal.Signal;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SignalSyncS2CPacket {
    private List<Signal> signals = new ArrayList<>();


    public SignalSyncS2CPacket(FriendlyByteBuf buf) {
        int size = buf.readInt();
        for (int i = 0; i<size; i++)
            signals.add(new Signal(buf));
    }

    public SignalSyncS2CPacket(List<Signal> signals) {
        this.signals = signals;
    }


    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(signals.size());
        for (Signal signal : signals) {
            signal.save(buf);
        }
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

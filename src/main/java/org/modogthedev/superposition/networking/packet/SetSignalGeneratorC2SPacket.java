package org.modogthedev.superposition.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SetSignalGeneratorC2SPacket {
    private final boolean swapSides;
    private final int frequency;

    public SetSignalGeneratorC2SPacket(int frequency, boolean swapSides) {
        this.frequency = frequency;
        this.swapSides = swapSides;
    }

    public SetSignalGeneratorC2SPacket(FriendlyByteBuf buf) {
        this.frequency = buf.readInt();
        this.swapSides = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(swapSides);
        buf.writeInt(frequency);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {

        });
        return true;
    }
}

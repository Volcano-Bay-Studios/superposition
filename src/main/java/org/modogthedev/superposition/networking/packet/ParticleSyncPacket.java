package org.modogthedev.superposition.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ParticleSyncPacket {
    private final int stealth;

    public ParticleSyncPacket(int stealth) {
        this.stealth = stealth;
    }

    public ParticleSyncPacket(FriendlyByteBuf buf) {
        this.stealth = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(stealth);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            // HERE WE ARE ON THE CLIENT!

        });
        return true;
    }
}

package org.modogthedev.superposition.networking.packet;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;

public class ParticleSyncS2CPacket {
    private final int stealth;

    public ParticleSyncS2CPacket(int stealth) {
        this.stealth = stealth;
    }

    public ParticleSyncS2CPacket(FriendlyByteBuf buf) {
        this.stealth = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(stealth);
    }

    public void handle(Supplier<NetworkManager.PacketContext> supplier) {
        var context = supplier.get();

        context.queue(() -> {
            // HERE WE ARE ON THE CLIENT!
        });
    }
}

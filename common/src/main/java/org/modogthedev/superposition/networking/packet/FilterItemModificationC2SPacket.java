package org.modogthedev.superposition.networking.packet;

import dev.architectury.networking.NetworkManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.modogthedev.superposition.item.FilterItem;
import org.modogthedev.superposition.platform.PlatformHelper;
import org.modogthedev.superposition.util.SyncedBlockEntity;

import java.util.function.Supplier;

public class FilterItemModificationC2SPacket {
    float value1;
    float value2;
    public FilterItemModificationC2SPacket(FriendlyByteBuf buf) {
        value1 = buf.readFloat();
        value2 = buf.readFloat();
    }

    public FilterItemModificationC2SPacket(float value1, float value2) {
        this.value1 = value1;
        this.value2 = value2;
    }
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeFloat(value1);
        buf.writeFloat(value2);
    }

    public void handle(Supplier<NetworkManager.PacketContext> supplier) {
        var context = supplier.get();

        context.queue(() -> { //TODO: Finish filter item data application
            var player = context.getPlayer();
            ItemStack itemStack;
            itemStack = player.getMainHandItem();
            if (!(itemStack.getItem() instanceof FilterItem))
                itemStack = player.getOffhandItem();
            if (itemStack.getItem() instanceof FilterItem filterItem) {
                CompoundTag tag = new CompoundTag();
                tag.putFloat("value1",value1);
                tag.putFloat("value2",value2);
                itemStack.addTagElement("filter",tag);
            }
        });
    }
}

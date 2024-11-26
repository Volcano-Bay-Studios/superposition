package org.modogthedev.superposition.networking.packet;

import dev.architectury.networking.NetworkManager;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.modogthedev.superposition.item.FilterItem;
import org.modogthedev.superposition.platform.PlatformHelper;
import org.modogthedev.superposition.system.filter.Filter;
import org.modogthedev.superposition.util.SyncedBlockEntity;

import java.util.function.Supplier;

public class FilterItemModificationC2SPacket {
    CompoundTag tag;
    public FilterItemModificationC2SPacket(FriendlyByteBuf buf) {
        tag = buf.readNbt();
    }

    public FilterItemModificationC2SPacket(Filter filter) {
        tag = new CompoundTag();
        filter.save(tag);
    }
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeNbt(tag);
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
                itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
            }
        });
    }
}

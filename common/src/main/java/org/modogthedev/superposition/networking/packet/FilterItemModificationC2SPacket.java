package org.modogthedev.superposition.networking.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.system.filter.Filter;

public record FilterItemModificationC2SPacket(CompoundTag tag) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<FilterItemModificationC2SPacket> TYPE = new CustomPacketPayload.Type<>(Superposition.id("filter_item_modification"));
    public static final StreamCodec<ByteBuf, FilterItemModificationC2SPacket> CODEC = ByteBufCodecs.COMPOUND_TAG.map(FilterItemModificationC2SPacket::new, FilterItemModificationC2SPacket::tag);

    public FilterItemModificationC2SPacket(Filter filter) {
        this(new CompoundTag());
        filter.save(this.tag);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

//    public void handle(Supplier<NetworkManager.PacketContext> supplier) {
//        var context = supplier.get();
//
//        context.queue(() -> { //TODO: Finish filter item data application
//            var player = context.getPlayer();
//            ItemStack itemStack;
//            itemStack = player.getMainHandItem();
//            if (!(itemStack.getItem() instanceof FilterItem)) {
//                itemStack = player.getOffhandItem();
//            }
//            if (itemStack.getItem() instanceof FilterItem filterItem) {
//                itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
//            }
//        });
//    }
}

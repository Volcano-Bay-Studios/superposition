package org.modogthedev.superposition.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.modogthedev.superposition.blockentity.FilterBlockEntity;
import org.modogthedev.superposition.screens.ScreenManager;
import org.modogthedev.superposition.system.filter.Filter;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class FilterItem extends Item {

    public Supplier<Filter> filter;

    public FilterItem(Supplier<Filter> filter, Item.Properties properties) {
        super(properties);
        this.filter = filter;
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack itemStack = super.getDefaultInstance();
        CompoundTag tag = new CompoundTag();
        this.filter.get().save(tag);
        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        return super.getDefaultInstance();
    }

    public Filter readFilterData(ItemStack itemStack) {
        CustomData data = itemStack.get(DataComponents.CUSTOM_DATA);
        if (data != null) {
//            CompoundTag tag = data.copyTag();
//            type.load(tag);
//            return type;
        }
        return null;
    }

    public void putData(ItemStack itemStack, Filter filter) {
        CompoundTag tag = new CompoundTag();
        filter.save(tag);
        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public void runIfHasData(ItemStack stack, Consumer<CompoundTag> tagConsumer) {
        CompoundTag tag = this.getTagElement(stack);
        if (tag != null) {
            tagConsumer.accept(tag);
        }
    }

    public CompoundTag getTagElement(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            return customData.copyTag();
        }
        return null;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        Filter type = this.filter.get();
        InteractionResultHolder<ItemStack> result = super.use(level, player, usedHand);
        if (result.getObject().getItem() instanceof FilterItem) {
            this.runIfHasData(result.getObject(), type::load);
        }
        if (result.getResult() == InteractionResult.PASS) {
            if (level.isClientSide) {
                ItemStack itemStack = player.getItemInHand(usedHand);
                if (isPassFilter(type)) {
                    ScreenManager.openFilterScreen(type, null, false);
                    return InteractionResultHolder.success(itemStack);
                }
            }
        }
        return result;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Filter type = this.filter.get();
        if (context.getItemInHand().getItem() instanceof FilterItem) {
            this.runIfHasData(context.getItemInHand(), type::load);
        }
        if (!context.getPlayer().isShiftKeyDown()) {
            if (context.getLevel().getBlockEntity(context.getClickedPos()) instanceof FilterBlockEntity filterBlockEntity) {
                boolean creative = context.getPlayer().getAbilities().instabuild;
                if (filterBlockEntity.getFilterType() == null || creative) {
                    filterBlockEntity.setFilter(type);
                    if (context.getLevel().isClientSide) {
                        return InteractionResult.SUCCESS;
                    }
                    if (!creative) {
                        context.getPlayer().getItemInHand(context.getHand()).shrink(1);
                    }
                    return InteractionResult.CONSUME;
                }
            }
        }
        if (context.getLevel().isClientSide) {
            if (context.getLevel().getBlockEntity(context.getClickedPos()) instanceof FilterBlockEntity) {
                ScreenManager.openFilterScreen(type, context.getClickedPos(), false);
                return InteractionResult.SUCCESS;
            }
            ScreenManager.openFilterScreen(type, context.getClickedPos(), false);
            return InteractionResult.SUCCESS;
        }
        return super.useOn(context);
    }

    public static boolean isPassFilter(Filter type) {
        return true;
    }

    public enum FilterType {
        LOW_PASS,
        HIGH_PASS,
        BAND_PASS,
        NONE
    }
}

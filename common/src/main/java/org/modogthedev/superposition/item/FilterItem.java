package org.modogthedev.superposition.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.modogthedev.superposition.blockentity.FilterBlockEntity;
import org.modogthedev.superposition.blockentity.SignalActorBlockEntity;
import org.modogthedev.superposition.core.SuperpositionFilters;
import org.modogthedev.superposition.screens.ScreenManager;
import org.modogthedev.superposition.system.filter.Filter;

public class FilterItem extends Item {
    public Filter type;

    public FilterItem(Properties pProperties, Item.Properties properties) {
        super(properties);
        type = pProperties.type;
    }

    public Filter readFilterData(ItemStack itemStack) {
        CompoundTag tag = itemStack.getTagElement("filter");
        type.load(tag);
        return type;
    }

    public void putData(ItemStack itemStack, Filter filter) {
        CompoundTag tag = new CompoundTag();
        filter.save(tag);
        itemStack.addTagElement("filter", tag);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        InteractionResultHolder<ItemStack> result = super.use(level, player, usedHand);
        if (result.getObject().getItem() instanceof FilterItem)
            type.load(result.getObject().getTagElement("filter"));
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
        if (context.getItemInHand().getItem() instanceof FilterItem)
            type.load(context.getItemInHand().getTagElement("filter"));
        if (!context.getPlayer().isCrouching()) {
            if (context.getLevel().getBlockEntity(context.getClickedPos()) instanceof FilterBlockEntity filterBlockEntity) {
                boolean creative = context.getPlayer().getAbilities().instabuild;
                if (filterBlockEntity.getFilterType() == null || creative) {
                    type.load(context.getItemInHand().getTagElement("filter"));
                    filterBlockEntity.setFilter(type);
                    if (context.getLevel().isClientSide)
                        return InteractionResult.SUCCESS;
                    if (!creative)
                        context.getPlayer().getItemInHand(context.getHand()).shrink(1);
                    return InteractionResult.CONSUME;
                }
            }
        } else if (context.getLevel().getBlockEntity(context.getClickedPos()) instanceof SignalActorBlockEntity) {
        } if (context.getLevel().isClientSide) {
            if (context.getLevel().getBlockEntity(context.getClickedPos()) instanceof FilterBlockEntity) {
                    ScreenManager.openFilterScreen(type, context.getClickedPos(), false);
                    return InteractionResult.SUCCESS;
            }
            ScreenManager.openFilterScreen(type, context.getClickedPos(), false);
            return InteractionResult.SUCCESS;
        }
        return super.useOn(context);
    }

    public static class Properties {
        public Filter type;

        public FilterItem.Properties type(Filter type) {
            this.type = type;
            return this;
        }
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

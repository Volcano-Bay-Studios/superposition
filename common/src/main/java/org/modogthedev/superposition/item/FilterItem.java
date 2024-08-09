package org.modogthedev.superposition.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.modogthedev.superposition.blockentity.FilterBlockEntity;
import org.modogthedev.superposition.screens.ScreenManager;

public class FilterItem extends Item {
    public FilterType type;

    public FilterItem(Properties pProperties, Item.Properties properties) {
        super(properties);
        type = pProperties.type;
    }

    public float[] readFilterData(ItemStack itemStack) {
        CompoundTag tag = itemStack.getTagElement("filter");
        if (tag != null) {
            float value1 = tag.getFloat("value1");
            float value2 = tag.getFloat("value2");
            return new float[]{value1, value2};
        }
        return new float[]{0, 0};
    }

    public void putData(ItemStack itemStack, float value1, float value2) {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("value1", value1);
        tag.putFloat("value2", value2);
        itemStack.addTagElement("filter", tag);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        InteractionResultHolder<ItemStack> result = super.use(level, player, usedHand);
        if (result.getResult() == InteractionResult.PASS) {
            if (level.isClientSide) {
                ItemStack itemStack = player.getItemInHand(usedHand);
                float[] floats = readFilterData(itemStack);
                if (isPassFilter(type)) {
                    ScreenManager.openFilterScreen(type, floats[0], floats[1], null);
                    return InteractionResultHolder.success(itemStack);
                }
            }
        }
        return result;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getLevel().getBlockEntity(context.getClickedPos()) instanceof FilterBlockEntity filterBlockEntity) {
            if (context.getPlayer().isCrouching()) {
                if (context.getLevel().isClientSide) {
                    float[] floats = readFilterData(context.getPlayer().getItemInHand(context.getHand()));
                    if (isPassFilter(type)) {
                        ScreenManager.openFilterScreen(type, floats[0], floats[1], context.getClickedPos());
                        return InteractionResult.SUCCESS;
                    }
                }
            } else {
                boolean creative = context.getPlayer().getAbilities().instabuild;
                if (filterBlockEntity.getFilterType() == FilterType.NONE || creative) {
                    float[] floats = readFilterData(context.getPlayer().getItemInHand(context.getHand()));
                    filterBlockEntity.setFilter(floats[0], floats[1], type);
                    if (context.getLevel().isClientSide)
                        return InteractionResult.SUCCESS;
                    if (!creative)
                        context.getPlayer().getItemInHand(context.getHand()).shrink(1);
                    return InteractionResult.CONSUME;
                }
            }
        }
        return super.useOn(context);
    }

    public static class Properties {
        public FilterType type;

        public FilterItem.Properties type(FilterType type) {
            this.type = type;
            return this;
        }
    }

    public static boolean isPassFilter(FilterType type) {
        return type == FilterType.LOW_PASS || type == FilterType.HIGH_PASS || type == FilterType.BAND_PASS;
    }

    public enum FilterType {
        LOW_PASS,
        HIGH_PASS,
        BAND_PASS,
        NONE
    }
}

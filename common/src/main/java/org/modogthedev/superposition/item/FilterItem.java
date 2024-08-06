package org.modogthedev.superposition.item;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
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
            float value1 =  tag.getFloat("value1");
            float value2 =  tag.getFloat("value2");
            return new float[]{value1,value2};
        }
        return new float[]{0,0};
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        InteractionResultHolder<ItemStack> result = super.use(level, player, usedHand);
        if (result.getResult() == InteractionResult.PASS) {
            if (level.isClientSide) {
                ScreenManager.openFilterScreen(type);
            }
        }
        return result;
    }

    public static class Properties {
        public FilterType type;
        public FilterItem.Properties type(FilterType type) {
            this.type = type;
            return this;
        }
    }
    public enum FilterType {
        LOW_PASS,
        HIGH_PASS,
        BAND_PASS
    }
}

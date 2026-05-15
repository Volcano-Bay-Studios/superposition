package org.modogthedev.superposition.item;

import foundry.veil.api.network.VeilPacketManager;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import org.modogthedev.superposition.blockentity.PanelBlockEntity;
import org.modogthedev.superposition.core.SuperpositionWidgets;
import org.modogthedev.superposition.networking.packet.PlayerPlaceWidgetC2SPacket;
import org.modogthedev.superposition.screens.ScreenManager;
import org.modogthedev.superposition.system.widget.Widget;

public class WidgetItem extends Item {
    public static Vector2i target = new Vector2i();
    public static boolean fail = false;
    public WidgetItem(Properties properties) {
        super(properties);
    }

    @Override
    public void verifyComponentsAfterLoad(ItemStack stack) {
        if (getTagElement(stack) == null) {
            putType(stack, new CompoundTag(), SuperpositionWidgets.GAUGE.getId());
        }
        super.verifyComponentsAfterLoad(stack);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (level.isClientSide) {
            ScreenManager.openWidgetScreen(usedHand);
        }
        return super.use(level, player, usedHand);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (fail) {
            return super.useOn(context);
        }
        Level level = context.getLevel();
        BlockEntity blockEntity = level.getBlockEntity(context.getClickedPos());
        ResourceLocation type = getType(context.getItemInHand());
        if (blockEntity instanceof PanelBlockEntity panel && type != null && level.isClientSide) {
            Widget widget = SuperpositionWidgets.WIDGET.asVanillaRegistry().get(type);
            if (widget != null) {
                panel.placeWidget(target, widget);
                VeilPacketManager.server().sendPacket(new PlayerPlaceWidgetC2SPacket(context.getClickedPos(),target.x,target.y,widget.getLocation()));
                return InteractionResult.CONSUME;
            }
        } else if (level.isClientSide) {
            ScreenManager.openWidgetScreen(context.getHand());
        }
        return super.useOn(context);
    }

    public void openWidgetPicker(Player player) {
        //TODO: PICKER
    }

    @Override
    public @NotNull ItemStack getDefaultInstance() {
        ItemStack itemStack = super.getDefaultInstance();
        putType(itemStack, new CompoundTag(), SuperpositionWidgets.GAUGE.getId());
        return itemStack;
    }

    public static ResourceLocation getType(ItemStack stack) {
        CompoundTag tag = getTagElement(stack);
        if (tag != null) {
            return ResourceLocation.fromNamespaceAndPath(tag.getString("namespace"), tag.getString("path"));
        } else {
            return null;
        }
    }

    public static void putType(ItemStack stack, CompoundTag tag, ResourceLocation type) {
        CompoundTag widgetTag = new CompoundTag();
        widgetTag.putString("namespace",type.getNamespace());
        widgetTag.putString("path",type.getPath());
        tag.put("widget", widgetTag);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }


    public static CompoundTag getCustomData(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            return customData.copyTag();
        }
        return new CompoundTag();
    }
    public static CompoundTag getTagElement(ItemStack stack) {
        CompoundTag tag = getCustomData(stack);
        if (tag.contains("widget")) {;
            return tag.getCompound("widget");
        }
        return null;
    }
}

package org.modogthedev.superposition.client.renderer.ui;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import foundry.veil.api.network.VeilPacketManager;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.*;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.networking.packet.BlockEntityModificationC2SPacket;
import org.modogthedev.superposition.screens.WidgetScreen;
import org.modogthedev.superposition.util.EditableTooltip;
import org.modogthedev.superposition.util.SPTooltipable;
import org.modogthedev.superposition.util.SyncedBlockEntity;

import java.util.ArrayList;
import java.util.List;

public class SuperpositionUITooltipRenderer {
    private static final ResourceLocation SAVE = ResourceLocation.fromNamespaceAndPath(Superposition.MODID, "textures/screen/save.png");
    public static int hoverTicks = 0;
    public static Vec3 lastHoveredPos = null;
    public static Vec3 currentPos = null;
    public static Vec3 desiredPos = null;
    public static EditableTooltip editableTooltip;
    public static boolean editingEditable = false;
    public static int cursorPos = 0;
    public static int flash;
    public static BlockPos.MutableBlockPos editPos = new BlockPos.MutableBlockPos();
    public static boolean selected;

    public static void keyPress(long windowPointer, int key, int scanCode, int action, int modifiers) {
        if (editableTooltip != null && action != GLFW.GLFW_RELEASE) {
            cursorPos = Mth.clamp(cursorPos, 0, editableTooltip.getText().length());
            switch (key) {
                case 256 -> {
                    if (selected)
                        selected = false;
                    else
                        editingEditable = false;
                }
                case 259, 261 -> {
                    if (editingEditable) {
                        if (selected) {
                            editableTooltip.replaceText("");
                            selected = false;
                        } else {
                            editableTooltip.replaceText(editableTooltip.getText().substring(0, Math.max(0, cursorPos - 1)) + editableTooltip.getText().substring(cursorPos));
                            cursorPos--;
                        }
                    }
                }

                case 263 -> {
                    if (editingEditable) {
                        if (Screen.hasControlDown()) {
                            cursorPos = 0;
                        } else {
                            cursorPos--;
                        }
                    }
                }

                case 262 -> {
                    if (editingEditable) {
                        if (Screen.hasControlDown()) {
                            cursorPos = editableTooltip.getText().length() - 1;
                        } else {
                            cursorPos++;
                        }
                    }
                }

                case 268 -> {
                    if (editingEditable)
                        cursorPos = 0;
                }
                case 269 -> {
                    if (editingEditable)
                        cursorPos = editableTooltip.getText().length() - 1;
                }
                default -> {
                    if (Screen.isCopy(key)) {
                        Minecraft.getInstance().keyboardHandler.setClipboard(editableTooltip.getText());
                    } else if (Screen.isPaste(key)) {
                        if (editingEditable) {
                            editableTooltip.addText(Minecraft.getInstance().keyboardHandler.getClipboard());
                            cursorPos += Minecraft.getInstance().keyboardHandler.getClipboard().length();
                        } else {
                            editableTooltip.replaceText(Minecraft.getInstance().keyboardHandler.getClipboard());
                        }
                    } else if (Screen.isCut(key)) {
                        Minecraft.getInstance().keyboardHandler.setClipboard(editableTooltip.getText());
                        editableTooltip.replaceText("");
                    } else if (Screen.isSelectAll(key)) {
                        selected = true;
                    } else {
                        return;
                    }
                }
            }
            updateEditableBlockEntity(editPos);
            cursorPos = Mth.clamp(cursorPos, 0, editableTooltip.getText().length());
        }
    }

    private static void updateEditableBlockEntity(BlockPos pos) {
        CompoundTag tag = new CompoundTag();
        tag.putString("output", editableTooltip.getText());
        Level level = Minecraft.getInstance().level;
        if (level.getBlockEntity(editPos) instanceof SyncedBlockEntity syncedBlockEntity) {
            syncedBlockEntity.loadSyncedData(tag);
        }
        VeilPacketManager.server().sendPacket(new BlockEntityModificationC2SPacket(tag, editPos));
    }

    public static void charTyped(long windowPointer, char key, int modifiers) {
        if (editingEditable && editableTooltip != null) {
            if (selected) {
                editableTooltip.replaceText(String.valueOf(key));
                selected = false;
            } else {
                editableTooltip.replaceText(editableTooltip.getText().substring(0, cursorPos) + (key) + editableTooltip.getText().substring(Math.min(cursorPos, editableTooltip.getText().length())));
            }
            CompoundTag tag = new CompoundTag();
            tag.putInt("modifiedPosition",cursorPos);
            tag.putString("changedChar", String.valueOf(key));
            tag.putInt("stringLength",editableTooltip.getText().length());
            Level level = Minecraft.getInstance().level;
            if (level.getBlockEntity(editPos) instanceof SyncedBlockEntity syncedBlockEntity) {
                syncedBlockEntity.loadSyncedData(tag);
            }
            VeilPacketManager.server().sendPacket(new BlockEntityModificationC2SPacket(tag, editPos));
            cursorPos++;
            cursorPos = Mth.clamp(cursorPos, -1, editableTooltip.getText().length());
            flash = -1;
        }
    }

    public static void clientTick(Level level) {
        if (editableTooltip == null) {
            cursorPos = 400;
            editingEditable = false;
            editPos = null;
            selected = false;
        } else {
            cursorPos = Mth.clamp(cursorPos, 0, editableTooltip.getText().length());
        }
    }

    public static void renderOverlay(GuiGraphics graphics, DeltaTracker deltaTracker) {
        renderOverlay(graphics, deltaTracker.getRealtimeDeltaTicks());
    }

    public static void renderOverlay(GuiGraphics graphics, float partialTicks) {
        int width = graphics.guiWidth();
        int height = graphics.guiHeight();
        PoseStack stack = graphics.pose();
        stack.pushPose();
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.options.hideGui || mc.gameMode.getPlayerMode() == GameType.SPECTATOR) {
            return;
        }


        HitResult result = mc.hitResult;
        Vec3 pos = null;
        SPTooltipable tooltippable = null;
        if (result instanceof EntityHitResult entityHitResult) {
            if (entityHitResult.getEntity() instanceof SPTooltipable tooltippable1) {
                tooltippable = tooltippable1;
                pos = entityHitResult.getEntity().getPosition(0f).add(0.0, entityHitResult.getEntity().getEyeHeight() / 2f, 0.0);
            }
        }
        editableTooltip = null;
        if (result instanceof BlockHitResult blockHitResult) {
            pos = Vec3.atCenterOf(blockHitResult.getBlockPos());
            BlockEntity blockEntity = mc.level.getBlockEntity(BlockPos.containing(pos));
            if (blockEntity instanceof SPTooltipable tooltippable1) {
                tooltippable = tooltippable1;
            }
            if (blockEntity instanceof EditableTooltip editableTooltip1) {
                editableTooltip = editableTooltip1;
                if (editPos != null)
                    editPos.set(BlockPos.containing(pos));
                else
                    editPos = new BlockPos.MutableBlockPos().set(BlockPos.containing(pos));
            }
        }
        if (tooltippable == null && mc.screen instanceof WidgetScreen widgetScreen) {
            BlockPos pos1 = widgetScreen.getBlockPos();
            if (pos1 != null && mc.level.isLoaded(pos1) && mc.level.getBlockEntity(pos1) instanceof SPTooltipable tooltippable1) {
                pos = Vec3.atCenterOf(pos1);
                tooltippable = tooltippable1;
            }
        }
        if (tooltippable == null || !tooltippable.isSuperpositionTooltipEnabled()) {
            hoverTicks = 0;
            lastHoveredPos = null;
            return;
        }

        hoverTicks++;
        lastHoveredPos = pos;
        List<Component> tooltip = new ArrayList<>(tooltippable.getTooltip());
        if (editableTooltip != null) {
            tooltip.add(Component.literal(editableTooltip.prefix() + editableTooltip.getText()));
        }

        if (tooltip.isEmpty()) {
            hoverTicks = 0;
            return;
        }

        stack.pushPose();
        int tooltipTextWidth = 0;
        for (FormattedText line : tooltip) {
            int textLineWidth = mc.font.width(line);
            if (textLineWidth > tooltipTextWidth) {
                tooltipTextWidth = textLineWidth;
            }
        }

        int tooltipHeight = 8;
        if (tooltip.size() > 1) {
            tooltipHeight += 2 + (tooltip.size() - 1) * 10;
        }
        int tooltipX = (width / 2) + 20;
        int tooltipY = (height / 2);
        int desiredX = tooltipX;
        int desiredY = tooltipY;

        tooltipX = Math.min(tooltipX, width - tooltipTextWidth - 20);
        tooltipY = Math.min(tooltipY, height - tooltipHeight - 20);

        float fade = Mth.clamp((hoverTicks + partialTicks) / 24f, 0, 1);
        int background = Superposition.SUPERPOSITION_THEME.get("background");
        int borderTop = Superposition.SUPERPOSITION_THEME.get("topBorder");
        int borderBottom = Superposition.SUPERPOSITION_THEME.get("bottomBorder");
//        background = resetAlpha(background).multiply(1,1,1,.7f);
//        borderBottom = resetAlpha(borderBottom);
//        borderTop = resetAlpha(borderTop);
        float heightBonus = tooltippable.getTooltipHeight();
        float widthBonus = tooltippable.getTooltipWidth();
        float textXOffset = tooltippable.getTooltipXOffset();
        float textYOffset = tooltippable.getTooltipYOffset();
        ItemStack istack = tooltippable.getStack() == null ? ItemStack.EMPTY : tooltippable.getStack();
        if (pos != lastHoveredPos) {
            currentPos = null;
            desiredPos = null;
        }

        if (tooltippable.getWorldspace()) {
            currentPos = currentPos == null ? pos : currentPos;
            Vec3 playerPos = mc.gameRenderer.getMainCamera().getPosition();
            Vec3i playerPosInt = new Vec3i((int) Math.round(result.getLocation().x), (int) result.getLocation().y, (int) Math.round(result.getLocation().z + 1));
            Vec3i cornerInt = new Vec3i((int) pos.x, (int) pos.y, (int) pos.z);
            Vec3i diff = playerPosInt.subtract(cornerInt);
            desiredPos = pos.add(Math.round(Mth.clamp(diff.getX(), -1, 1) * 0.5f) - 0.5f, 0.5, Math.round(Mth.clamp(diff.getZ(), -1, 1) * 0.5f) - 0.5f);
            if (hoverTicks == 1) {
                currentPos = desiredPos.add(0, -0.15f, 0);
            }
            currentPos = currentPos.lerp(desiredPos, 0.05f);
            Vector3f screenSpacePos = worldToScreenSpace(currentPos, partialTicks);
            Vector3f desiredScreenSpacePos = worldToScreenSpace(desiredPos, partialTicks);
            screenSpacePos = new Vector3f(Mth.clamp(screenSpacePos.x(), 0, width), Mth.clamp(screenSpacePos.y(), 0, height - (mc.font.lineHeight * tooltip.size())), screenSpacePos.z());
            desiredScreenSpacePos = new Vector3f(Mth.clamp(desiredScreenSpacePos.x(), 0, width), Mth.clamp(desiredScreenSpacePos.y(), 0, height - (mc.font.lineHeight * tooltip.size())), desiredScreenSpacePos.z());
            tooltipX = (int) screenSpacePos.x() - (tooltipTextWidth / 2);
            tooltipY = (int) screenSpacePos.y();
            desiredX = (int) desiredScreenSpacePos.x();
            desiredY = (int) desiredScreenSpacePos.y();
        }
        if (mc.screen instanceof WidgetScreen widgetScreen) {
            Vec2 tooltipPosition = widgetScreen.getTooltipPosition(width, height);
            tooltipX = (int) tooltipPosition.x;
            tooltipY = (int) tooltipPosition.y;
        }
        tooltippable.drawExtra();
        if (editableTooltip != null && editingEditable && flash < 40) {
            cursorPos = Mth.clamp(cursorPos, 0, editableTooltip.getText().length());
            int xOffset = (int) (tooltipX - tooltipTextWidth / 2f + textXOffset + ((Minecraft.getInstance().font.width(editableTooltip.prefix() + editableTooltip.getText().substring(0, cursorPos)) + 12)));
            graphics.fill(xOffset, tooltipY + (int) textYOffset, xOffset + 1, tooltipY + (int) textYOffset + 10, -3092272);
            if (selected) {
                xOffset = (int) (tooltipX - tooltipTextWidth / 2f + textXOffset + ((Minecraft.getInstance().font.width(editableTooltip.prefix()) + 12)));
                int xOffset2 = (int) (tooltipX - tooltipTextWidth / 2f + textXOffset + ((Minecraft.getInstance().font.width(editableTooltip.prefix())) + (Minecraft.getInstance().font.width(editableTooltip.getText()) + 12)));
                graphics.fill(xOffset, tooltipY + (int) textYOffset, xOffset2, tooltipY + (int) textYOffset + 10, -3092272);
            }
        }
        if (editableTooltip != null && editingEditable) {
            String focusText = "[PRESS ESC TO UNFOCUS]";
            RenderSystem.setShaderColor(.5f, 1, .5f, 1f);
            graphics.drawString(Minecraft.getInstance().font, focusText, (tooltipX + tooltipTextWidth / 2 - Minecraft.getInstance().font.width(focusText) / 2 + 8) - tooltipTextWidth / 2, tooltipY + tooltipHeight / 2 + 10, 0xFFFFFF);
            RenderSystem.setShaderColor(1, 1, 1, 1);
        }
        flash++;
        if (flash > 80 || selected) {
            flash = 0;
        }
        SPUIUtils.drawHoverText(tooltippable, partialTicks, istack, stack, tooltip, tooltipX + (int) textXOffset - tooltipTextWidth / 2, tooltipY + (int) textYOffset, width, height, -1, background, borderTop, borderBottom, mc.font, (int) widthBonus, (int) heightBonus, desiredX, desiredY);
        graphics.blit(SAVE, 64, 64, 0, 0, 16, 16);
        stack.popPose();
    }


    public static Vector3f worldToScreenSpace(Vec3 pos, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        Camera camera = mc.gameRenderer.getMainCamera();
        Vec3 cameraPosition = camera.getPosition();

        Vector3f position = new Vector3f((float) (cameraPosition.x - pos.x), (float) (cameraPosition.y - pos.y), (float) (cameraPosition.z - pos.z));
        Quaternionf cameraRotation = camera.rotation();
        cameraRotation.conjugate();
//        cameraRotation = restrictAxis(new Vec3(1, 1, 0), cameraRotation);
        cameraRotation.transform(position);
        position.y = -position.y;

        // Account for view bobbing
        if (mc.options.bobView().get() && mc.getCameraEntity() instanceof Player player) {
            float playerStep = player.walkDist - player.walkDistO;
            float stepSize = -(player.walkDist + playerStep * partialTicks);
            float viewBob = Mth.lerp(partialTicks, player.oBob, player.bob);

            Quaternionf bobXRotation = Axis.XP.rotationDegrees(Math.abs(Mth.cos(stepSize * (float) Math.PI - 0.2f) * viewBob) * 5f);
            Quaternionf bobZRotation = Axis.ZP.rotationDegrees(Mth.sin(stepSize * (float) Math.PI) * viewBob * 3f);
            bobXRotation.conjugate();
            bobZRotation.conjugate();
            bobXRotation.transform(position);
            bobZRotation.transform(position);
            position.add(Mth.sin(stepSize * (float) Math.PI) * viewBob * 0.5f, Math.abs(Mth.cos(stepSize * (float) Math.PI) * viewBob), 0f);
        }

        Window window = mc.getWindow();
        float screenSize = window.getGuiScaledHeight() / 2f / position.z() / (float) Math.tan(Math.toRadians(mc.gameRenderer.getFov(camera, partialTicks, true) / 2f));
        position.mul(-screenSize, -screenSize, 1f);
        position.add(window.getGuiScaledWidth() / 2f, window.getGuiScaledHeight() / 2f, 0f);

        return position;
    }
}

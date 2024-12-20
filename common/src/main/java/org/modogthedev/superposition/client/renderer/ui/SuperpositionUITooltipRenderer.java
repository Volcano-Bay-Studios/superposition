package org.modogthedev.superposition.client.renderer.ui;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import foundry.veil.api.client.color.Color;
import foundry.veil.api.client.color.theme.NumberThemeProperty;
import foundry.veil.api.client.tooltip.VeilUIItemTooltipDataHolder;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.modogthedev.superposition.util.SPTooltipable;

import java.util.List;

public class SuperpositionUITooltipRenderer {
    public static int hoverTicks = 0;
    public static Vec3 lastHoveredPos = null;
    public static Vec3 currentPos = null;
    public static Vec3 desiredPos = null;

    public static void renderOverlay(GuiGraphics graphics, DeltaTracker deltaTracker) {
        int width = graphics.guiWidth();
        int height = graphics.guiHeight();
        PoseStack stack = graphics.pose();
        stack.pushPose();
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.options.hideGui || mc.gameMode.getPlayerMode() == GameType.SPECTATOR) {
            return;
        }

        float partialTicks = deltaTracker.getRealtimeDeltaTicks();
        HitResult result = mc.hitResult;
        Vec3 pos = null;
        SPTooltipable tooltippable = null;
        if (result instanceof EntityHitResult entityHitResult) {
            if (entityHitResult.getEntity() instanceof SPTooltipable tooltippable1) {
                tooltippable = tooltippable1;
                pos = entityHitResult.getEntity().getPosition(0f).add(0.0, entityHitResult.getEntity().getEyeHeight() / 2f, 0.0);
            }
        }
        if (result instanceof BlockHitResult blockHitResult) {
            pos = Vec3.atCenterOf(blockHitResult.getBlockPos());
            BlockEntity blockEntity = mc.level.getBlockEntity(BlockPos.containing(pos));
            if (blockEntity instanceof SPTooltipable tooltippable1) {
                tooltippable = tooltippable1;
            }
        }
        if (tooltippable == null) {

        }
        if (tooltippable == null || !tooltippable.isSuperpositionTooltipEnabled()) {
            hoverTicks = 0;
            lastHoveredPos = null;
            return;
        }

        hoverTicks++;
        lastHoveredPos = pos;
        List<Component> tooltip = tooltippable.getTooltip();
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
        Color background = new Color(50, 168, 82, 150);
        Color borderTop = new Color(60, 186, 94, 255);
        Color borderBottom = new Color(44, 150, 72, 255);
//        background = resetAlpha(background).multiply(1,1,1,.7f);
//        borderBottom = resetAlpha(borderBottom);
//        borderTop = resetAlpha(borderTop);
        float heightBonus = tooltippable.getTooltipHeight();
        float widthBonus = tooltippable.getTooltipWidth();
        float textXOffset = tooltippable.getTooltipXOffset();
        float textYOffset = tooltippable.getTooltipYOffset();
        List<VeilUIItemTooltipDataHolder> items = tooltippable.getItems();
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
            background = background.multiply(1, 1, 1, fade);
            borderTop = borderTop.multiply(1, 1, 1, fade);
            borderBottom = borderBottom.multiply(1, 1, 1, fade);
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
        tooltippable.drawExtra();
        SPUIUtils.drawHoverText(tooltippable, partialTicks, istack, stack, tooltip, tooltipX + (int) textXOffset, tooltipY + (int) textYOffset, width, height, -1, background.getHex(), borderTop.getHex(), borderBottom.getHex(), mc.font, (int) widthBonus, (int) heightBonus, items, desiredX, desiredY);
        stack.popPose();
    }

    public static Color resetAlpha(Color color) {
        return (color.lightenCopy(.1f).multiply(1, 1, 1, 10f).lightenCopy(-.1f));
    }

    public static void drawConnectionLine(PoseStack stack, SPTooltipable tooltippable, int tooltipX, int tooltipY, int desiredX, int desiredY) {
        if (tooltippable.getTheme().getColor("connectingLine") != null) {
            stack.pushPose();
            Color color = tooltippable.getTheme().getColor("connectingLine");
            float thickness = ((NumberThemeProperty) tooltippable.getTheme().getProperty("connectingLineThickness")).getValue(Float.class);
//            stack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
//            stack.mulPose(Vector3f.YP.rotationDegrees(180));
            Matrix4f mat = stack.last().pose();
            RenderSystem.enableDepthTest();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.lineWidth(2);
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            // draw a quad of thickness thickness from desiredX, desiredY to tooltipX, tooltipY with a z value of 399, starting from the top right corner and going anti-clockwise
            buffer.addVertex(mat, desiredX + thickness, desiredY, 399).setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
            buffer.addVertex(mat, desiredX - thickness, desiredY, 399).setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
            buffer.addVertex(mat, tooltipX - thickness, tooltipY + 3 - (tooltippable.getTooltipHeight() / 2f), 399).setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
            buffer.addVertex(mat, tooltipX + thickness, tooltipY + 3 - (tooltippable.getTooltipHeight() / 2f), 399).setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
            BufferUploader.drawWithShader(buffer.buildOrThrow());
            RenderSystem.disableBlend();
            stack.popPose();
        }
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
        if (mc.options.bobView().get() && mc.getCameraEntity() instanceof Player) {
            Player player = (Player) mc.getCameraEntity();
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

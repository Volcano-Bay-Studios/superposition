package org.modogthedev.superposition.client.renderer.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import foundry.veil.Veil;
import foundry.veil.api.client.tooltip.VeilUIItemTooltipDataHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.modogthedev.superposition.util.SPTooltipable;

import java.util.ArrayList;
import java.util.List;

import static foundry.veil.api.client.util.UIUtils.*;

public class SPUIUtils {
    public static void drawHoverText(SPTooltipable tooltippable, float pticks, final ItemStack stack, PoseStack pStack, List<? extends FormattedText> textLines, int mouseX, int mouseY, int screenWidth, int screenHeight,
                                     int maxTextWidth, int backgroundColor, int borderColorStart, int borderColorEnd, Font font,
                                     int tooltipTextWidthBonus, int tooltipTextHeightBonus, List<VeilUIItemTooltipDataHolder> items,
                                     int desiredX, int desiredY) {
        if (textLines.isEmpty())
            return;

        List<ClientTooltipComponent> list = gatherTooltipComponents(stack, textLines, stack.getTooltipImage().orElse(null), mouseX, screenWidth, screenHeight, font, font);
        // RenderSystem.disableRescaleNormal();
        RenderSystem.disableDepthTest();
        int tooltipTextWidth = 0;

        for (FormattedText textLine : textLines) {
            int textLineWidth = font.width(textLine);
            if (textLineWidth > tooltipTextWidth)
                tooltipTextWidth = textLineWidth;
        }

        boolean needsWrap = false;

        int titleLinesCount = 1;
        int tooltipX = mouseX + 12;
        if (tooltipX + tooltipTextWidth + 4 > screenWidth) {
            tooltipX = mouseX - 16 - tooltipTextWidth;
            if (tooltipX < 4) // if the tooltip doesn't fit on the screen
            {
                if (mouseX > screenWidth / 2)
                    tooltipTextWidth = mouseX - 12 - 8;
                else
                    tooltipTextWidth = screenWidth - 16 - mouseX;
                needsWrap = true;
            }
        }

        if (maxTextWidth > 0 && tooltipTextWidth > maxTextWidth) {
            tooltipTextWidth = maxTextWidth;
            needsWrap = true;
        }

        if (needsWrap) {
            int wrappedTooltipWidth = 0;
            List<FormattedText> wrappedTextLines = new ArrayList<>();
            for (int i = 0; i < textLines.size(); i++) {
                FormattedText textLine = textLines.get(i);
                List<FormattedText> wrappedLine = font.getSplitter()
                        .splitLines(textLine, tooltipTextWidth, Style.EMPTY);
                if (i == 0)
                    titleLinesCount = wrappedLine.size();

                for (FormattedText line : wrappedLine) {
                    int lineWidth = font.width(line);
                    if (lineWidth > wrappedTooltipWidth)
                        wrappedTooltipWidth = lineWidth;
                    wrappedTextLines.add(line);
                }
            }
            tooltipTextWidth = wrappedTooltipWidth;
            textLines = wrappedTextLines;

            if (mouseX > screenWidth / 2)
                tooltipX = mouseX - 16 - tooltipTextWidth;
            else
                tooltipX = mouseX + 12;
        }

        int tooltipY = mouseY - 12;
        int tooltipHeight = 8;

        if (textLines.size() > 1) {
            tooltipHeight += (textLines.size() - 1) * 10;
            if (textLines.size() > titleLinesCount)
                tooltipHeight += 2; // gap between title lines and next lines
        }
        if (tooltipY < 4)
            tooltipY = 4;
        else if (tooltipY + tooltipHeight + 4 > screenHeight)
            tooltipY = screenHeight - tooltipHeight - 4;

        final int zLevel = 400;
        tooltipTextWidth += tooltipTextWidthBonus;
        tooltipHeight += tooltipTextHeightBonus;


        SuperpositionUITooltipRenderer.drawConnectionLine(pStack, tooltippable, tooltipX, tooltipY, desiredX, desiredY);
        drawTooltipRects(pticks, pStack, zLevel, backgroundColor, borderColorStart, borderColorEnd, font, list, tooltipTextWidth, titleLinesCount, tooltipX, tooltipY, tooltipHeight, items);
    }

    private static void drawTooltipRects(float pticks, PoseStack pStack, int z, int backgroundColor, int borderColorStart, int borderColorEnd, Font font, List<ClientTooltipComponent> list, int tooltipTextWidth, int titleLinesCount, int tooltipX, int tooltipY, int tooltipHeight, List<VeilUIItemTooltipDataHolder> items) {
        pStack.pushPose();
        Matrix4f mat = pStack.last()
                .pose();
        drawGradientRect(mat, z, tooltipX - 3, tooltipY - 4, tooltipX + tooltipTextWidth + 3, tooltipY - 3, backgroundColor, backgroundColor);
        drawGradientRect(mat, z, tooltipX - 3, tooltipY + tooltipHeight + 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 4, backgroundColor, backgroundColor);
        drawGradientRect(mat, z, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
        drawGradientRect(mat, z, tooltipX - 4, tooltipY - 3, tooltipX - 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
        drawGradientRect(mat, z, tooltipX + tooltipTextWidth + 3, tooltipY - 3, tooltipX + tooltipTextWidth + 4, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
        drawGradientRect(mat, z, tooltipX - 3, tooltipY - 3 + 1, tooltipX - 3 + 1, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
        drawGradientRect(mat, z, tooltipX + tooltipTextWidth + 2, tooltipY - 3 + 1, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
        drawGradientRect(mat, z, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY - 3 + 1, borderColorStart, borderColorStart);
        drawGradientRect(mat, z, tooltipX - 3, tooltipY + tooltipHeight + 2, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, borderColorEnd, borderColorEnd);

        int itemY = tooltipY;
        for (int lineNumber = 0; lineNumber < list.size(); ++lineNumber) {
            if (lineNumber + 1 == titleLinesCount) {
                itemY += 2;
            }

            itemY += 10;
        }
        pStack.pushPose();
        pStack.translate(0, 0, 300);
        if (items != null && !items.isEmpty()) {
            for (VeilUIItemTooltipDataHolder item : items) {
                renderAndDecorateItem(item.getItemStack(), tooltipX + item.getX().apply(pticks), itemY + item.getY().apply(pticks));
                drawTexturedRect(pStack.last().pose(), z + 100, tooltipX + item.getX().apply(pticks), itemY + item.getY().apply(pticks), 16, 16, 0, 0, 0, 0, 16, 16, Veil.veilPath("textures/gui/item_shadow.png"));
            }
        }
        pStack.popPose();

        MultiBufferSource.BufferSource renderType = Minecraft.getInstance().renderBuffers().bufferSource();
        pStack.translate(0.0D, 0.0D, z);

        for (int lineNumber = 0; lineNumber < list.size(); ++lineNumber) {
            ClientTooltipComponent line = list.get(lineNumber);
            RenderSystem.setShaderColor(.5f, 1, .5f, 1f);
            if (line != null) {
                line.renderText(font, tooltipX, tooltipY, mat, renderType);
            }

            if (lineNumber + 1 == titleLinesCount) {
                tooltipY += 2;
            }

            tooltipY += 10;
        }


        renderType.endBatch();
        pStack.popPose();

        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1, 1, 1, 1);
    }

    public static void drawGradientRect(Matrix4f mat, int zLevel, int left, int top, int right, int bottom, int startColor, int endColor) {
        float startAlpha = (float) (startColor >> 24 & 255) / 255.0F;
        float startRed = (float) (startColor >> 16 & 255) / 255.0F;
        float startGreen = (float) (startColor >> 8 & 255) / 255.0F;
        float startBlue = (float) (startColor & 255) / 255.0F;
        float endAlpha = (float) (endColor >> 24 & 255) / 255.0F;
        float endRed = (float) (endColor >> 16 & 255) / 255.0F;
        float endGreen = (float) (endColor >> 8 & 255) / 255.0F;
        float endBlue = (float) (endColor & 255) / 255.0F;

        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        buffer.addVertex(mat, right, top, zLevel).setColor(startRed, startGreen, startBlue, startAlpha);
        buffer.addVertex(mat, left, top, zLevel).setColor(startRed, startGreen, startBlue, startAlpha);
        buffer.addVertex(mat, left, bottom, zLevel).setColor(endRed, endGreen, endBlue, endAlpha);
        buffer.addVertex(mat, right, bottom, zLevel).setColor(endRed, endGreen, endBlue, endAlpha);
        BufferUploader.drawWithShader(buffer.buildOrThrow());

        RenderSystem.disableBlend();
    }
}

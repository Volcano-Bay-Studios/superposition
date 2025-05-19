package org.modogthedev.superposition.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import foundry.veil.api.client.color.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.client.renderer.ui.SPUIUtils;
import org.modogthedev.superposition.system.cards.Card;
import org.modogthedev.superposition.system.cards.Node;

public class InscriberScreen extends Screen {
    public Card card;
    public Vector2f camera = null;
    public Vector2f zoomPos = new Vector2f();
    public float zoom = 1;
    public float zoomTarget = 1f;
    public float width = 0;
    public float height = 0;
    public float scroll = 0;

    public InscriberScreen(Card card) {
        super(Component.literal("Inscriber"));
        this.card = card;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        PoseStack poseStack = guiGraphics.pose();
        width = guiGraphics.guiWidth();
        height = guiGraphics.guiHeight();
        Vector3f mouse = new Vector3f(mouseX, mouseY, 0);

        if (camera == null) {
            camera = new Vector2f(width/2f, height/2f);
        }

        int topBorder = Superposition.SUPERPOSITION_THEME.getColor("topBorder").argb();
        int bottomBorder = Superposition.SUPERPOSITION_THEME.getColor("bottomBorder").argb();

        int topBackground = new Color().setInt(60, 186, 94, 60).argb();
        int bottomBackground = new Color().setInt(44, 150, 72, 40).argb();

        poseStack.pushPose();
        poseStack.translate(camera.x,camera.y,0); // Draw Nodes
        poseStack.scale(zoom, zoom, 1);

        zoomTarget += (float) scroll/5f;
        zoomTarget = Mth.clamp(zoomTarget, 1f, 5f);
        zoom = (zoom+zoomTarget)/2f;
        zoom = Mth.clamp(zoom, 1f, 5f);
        scroll = 0;

        Matrix4f mat = poseStack.last().pose();

        for (int i = 0; i < 300; i += 10) {
            SPUIUtils.drawGradientRect(poseStack.last().pose(), 0, 0, i, 300, i +10, topBackground, bottomBackground);
        }

        for (Node node : card.getNodes().values()) {
            float x = node.getPosition().x;
            float y = node.getPosition().y;
            float xLength = node.getSize().x / 2;
            float yLength = node.getSize().y / 2;
            if (node.isColliding(mouseX, mouseY)) {
                SPUIUtils.drawGradientRect(poseStack.last().pose(), 5, (int) (x - xLength), (int) (y - yLength), (int) (x + xLength), (int) (y + yLength), bottomBorder, bottomBorder);
            } else {
                SPUIUtils.drawGradientRect(poseStack.last().pose(), 5, (int) (x - xLength), (int) (y - yLength), (int) (x + xLength), (int) (y + yLength), topBorder, topBorder);
            }
            Node targetNode = node.getTarget();
//            guiGraphics.fill((int) (x-xLength), (int) y, (int) (x+xLength), (int) (y+yLength),topBorder);
        }
        poseStack.popPose();
1        guiGraphics.drawString(Minecraft.getInstance().font,"HI", (int) mouse.x, (int) mouse.y, 0xFFFFFF);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (button == 1 && camera != null) {
            camera.add((float) dragX, (float) dragY);
        }
        if (button == 0) {

        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        scroll = (float) scrollY;
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }
}

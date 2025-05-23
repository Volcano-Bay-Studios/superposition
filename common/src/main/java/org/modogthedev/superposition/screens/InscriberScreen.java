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
    public Node selectedNode = null;
    int animation = 0;

    public InscriberScreen(Card card) {
        super(Component.literal("Inscriber"));
        this.card = card;
    }

    @Override
    public void tick() {
        animation++;
        if (animation >= 360) {
            animation = 0;
        }
        super.tick();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        PoseStack poseStack = guiGraphics.pose();
        width = guiGraphics.guiWidth();
        height = guiGraphics.guiHeight();

        if (camera == null) {
            camera = new Vector2f(width/2f, height/2f);
        }

        Vector3f mouse = new Vector3f((mouseX/zoom-camera.x), (mouseY/zoom-camera.y), 0);

        int topBorder = Superposition.SUPERPOSITION_THEME.getColor("topBorder").argb();
        int bottomBorder = Superposition.SUPERPOSITION_THEME.getColor("bottomBorder").argb();

        int topBackground = new Color().setInt(60, 186, 94, 60).argb();
        int bottomBackground = new Color().setInt(44, 150, 72, 40).argb();

        zoomTarget += (float) scroll/3f;
        zoomTarget = Mth.clamp(zoomTarget, 1f, 5f);
        float oldZoom = zoom;
        zoom = (zoom+zoomTarget)/2f;
        zoom = Mth.clamp(zoom, 1f, 5f);
        scroll = 0;

        Vector3f adjustedMouse = new Vector3f((mouseX/zoom-camera.x), (mouseY/zoom-camera.y), 0);

        camera.x += adjustedMouse.x - mouse.x;

        camera.y += adjustedMouse.y - mouse.y;

        poseStack.pushPose();
        poseStack.scale(zoom, zoom, 1);
        poseStack.translate(camera.x,camera.y,0); // Draw Nodes

//        guiGraphics.drawString(Minecraft.getInstance().font,"HI", (int) mouse.x, (int) mouse.y, 0xFFFFFF);

        Matrix4f mat = poseStack.last().pose();


        for (int i = 0; i < 300; i += 10) {
            SPUIUtils.drawGradientRect(poseStack.last().pose(), 0, 0, i, 300, i +10, topBackground, bottomBackground);
        }

        dragNode(guiGraphics,mouseX, mouseY);

        for (Node node : card.getNodes().values()) {
            float x = node.getPosition().x;
            float y = node.getPosition().y;
            float xLength = node.getSize().x / 2;
            float yLength = node.getSize().y / 2;
            if (node.isColliding(adjustedMouse.x, adjustedMouse.y)) {
                SPUIUtils.drawGradientRect(poseStack.last().pose(), 5, (int) (x - xLength), (int) (y - yLength), (int) (x + xLength), (int) (y + yLength), topBorder, topBorder);
            } else {
                SPUIUtils.drawGradientRect(poseStack.last().pose(), 5, (int) (x - xLength), (int) (y - yLength), (int) (x + xLength), (int) (y + yLength), bottomBorder, bottomBorder);
            }
            Node targetNode = node.getTarget();
//            guiGraphics.fill((int) (x-xLength), (int) y, (int) (x+xLength), (int) (y+yLength),topBorder);
        }
        poseStack.popPose();
    }

    private void dragNode(GuiGraphics guiGraphics,float mouseX, float mouseY) {
        if (selectedNode == null) {
            return;
        }
        int topBorder = Superposition.SUPERPOSITION_THEME.getColor("topBorder").argb();
        PoseStack poseStack = guiGraphics.pose();
        Vector3f mouse = new Vector3f((float) (mouseX/zoom-camera.x), (float) (mouseY/zoom-camera.y), 0);
        selectedNode.getPosition().set(mouse.x,mouse.y);
        for (Node node2 : card.getNodes().values()) {
            if (node2 != selectedNode) {
                if (Math.abs(node2.getPosition().x-selectedNode.getPosition().x) < 5f) {
                    selectedNode.getPosition().set(node2.getPosition().x,selectedNode.getPosition().y);
                    SPUIUtils.drawGradientRect(poseStack.last().pose(), 4, (int) (selectedNode.getPosition().x-1), (int) Math.min(node2.getPosition().y,selectedNode.getPosition().y), (int) (selectedNode.getPosition().x+1), (int) Math.max(selectedNode.getPosition().y,node2.getPosition().y), topBorder, topBorder);
                }
                if (Math.abs(node2.getPosition().y-selectedNode.getPosition().y) < 5f) {
                    selectedNode.getPosition().set(selectedNode.getPosition().x,node2.getPosition().y);
                    SPUIUtils.drawGradientRect(poseStack.last().pose(), 4, (int) Math.min(selectedNode.getPosition().x,node2.getPosition().x), (int) selectedNode.getPosition().y-1, (int) Math.max(node2.getPosition().x,selectedNode.getPosition().x), (int) selectedNode.getPosition().y+1, topBorder, topBorder);
                }
            }
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (button == 1 && camera != null) {
            camera.add((float) dragX/zoom, (float) dragY/zoom);
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            selectedNode = null;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Vector3f mouse = new Vector3f((float) (mouseX/zoom-camera.x), (float) (mouseY/zoom-camera.y), 0);
        if (button == 0 && selectedNode == null) {
            boolean found = false;
            for (Node node : card.getNodes().values()) {
                if (node.isColliding(mouse.x, mouse.y)) {
                    selectedNode = node;
                    found = true;
                    break;

                }
            }
            if (!found) {
                selectedNode = null;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        scroll = (float) scrollY;
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }
}

package org.modogthedev.superposition.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import foundry.veil.api.client.color.Color;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.client.renderer.ui.SPUIUtils;
import org.modogthedev.superposition.system.cards.Attachment;
import org.modogthedev.superposition.system.cards.Card;
import org.modogthedev.superposition.system.cards.Node;

import java.util.ArrayList;
import java.util.List;

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
    public Attachment connectingAttachment = null;
    public Vector2f offset = new Vector2f();
    int animation = 0;
    private Vector2f windowPos = null;

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
            camera = new Vector2f(width / 2f, height / 2f);
        }

        Vector3f mouse = new Vector3f((mouseX / zoom - camera.x), (mouseY / zoom - camera.y), 0);

        int topBorder = Superposition.SUPERPOSITION_THEME.getColor("topBorder").argb();
        int bottomBorder = Superposition.SUPERPOSITION_THEME.getColor("bottomBorder").argb();

        int topBackground = new Color().setInt(60, 186, 94, 60).argb();
        int bottomBackground = new Color().setInt(44, 150, 72, 40).argb();
        int background = new Color().setInt(34, 120, 62, 255).argb();

        zoomTarget += (float) scroll / 3f;
        zoomTarget = Mth.clamp(zoomTarget, 1f, 5f);
        zoom = (zoom + zoomTarget) / 2f;
        zoom = Mth.clamp(zoom, 1f, 5f);
        scroll = 0;

        Vector3f adjustedMouse = new Vector3f((mouseX / zoom - camera.x), (mouseY / zoom - camera.y), 0);

        camera.x += adjustedMouse.x - mouse.x;

        camera.y += adjustedMouse.y - mouse.y;

        poseStack.pushPose();
        poseStack.scale(zoom, zoom, 1);
        poseStack.translate(camera.x, camera.y, 0); // Draw Nodes

//        guiGraphics.drawString(Minecraft.getInstance().font,"HI", (int) mouse.x, (int) mouse.y, 0xFFFFFF);

        Matrix4f mat = poseStack.last().pose();


        for (int i = 0; i < 300; i += 10) {
            SPUIUtils.drawGradientRect(poseStack.last().pose(), 0, 0, i, 300, i + 10, topBackground, bottomBackground);
        }

        dragNode(guiGraphics, mouseX, mouseY);

        List<Attachment> attachments = new ArrayList<>();

//        if (connectingAttachment != null) {
//            Vector2f pos1 = connectingAttachment.getAbsolutePosition();
//            drawConnection(guiGraphics, pos1.x, pos1.y, adjustedMouse.x, adjustedMouse.y, connectingAttachment.getSnapMode());
//            SPUIUtils.drawGradientRect(poseStack.last().pose(), 10, (int) (adjustedMouse.x - 2), (int) (adjustedMouse.y  - 2), (int) (adjustedMouse.x  + 2), (int) (adjustedMouse.y + 2), topBorder, topBorder);
//            SPUIUtils.drawGradientRect(poseStack.last().pose(), 10, (int) (adjustedMouse.x  - 1), (int) (adjustedMouse.y - 1), (int) (adjustedMouse.x  + 1), (int) (adjustedMouse.y + 1), background, background);
//        }

        if (connectingAttachment != null) {
            connectingAttachment.setSegment(new Vector2f(mouse.x, mouse.y));
        }

        if (windowPos != null) {
            SPUIUtils.drawGradientRect(poseStack.last().pose(), 10, (int) (windowPos.x - 1), (int) (windowPos.y - 1), (int) (windowPos.x + 10), (int) (windowPos.y + 10), background, background);
        }

        for (Node node : card.getNodes().values()) {
            float x = node.getPosition().x;
            float y = node.getPosition().y;
            float xLength = node.getSize().x / 2;
            float yLength = node.getSize().y / 2;

            for (Attachment attachment : node.getAttachments()) {
                exploreAttachment(attachment, attachments);
            }

            for (Attachment attachment : attachments) {
                if (attachment.getTarget() != null) {
//                    drawConnection(guiGraphics, attachment.getAbsolutePosition().x, attachment.getAbsolutePosition().y, attachment.getTarget().getAbsolutePosition().x, attachment.getTarget().getAbsolutePosition().y, attachment.getSnapMode(),2f, bottomBorder, bottomBorder);
//                    drawConnection(guiGraphics, attachment.getAbsolutePosition().x, attachment.getAbsolutePosition().y, attachment.getTarget().getAbsolutePosition().x, attachment.getTarget().getAbsolutePosition().y, attachment.getSnapMode(), 2f, bottomBackground, bottomBackground);
                }
            }

            for (Attachment attachment : attachments) {

                if (attachment instanceof Attachment.SegmentAttachment segmentAttachment && segmentAttachment.getParent() != null) { // Auto path snap mode
                    float yDiff = segmentAttachment.getParent().getAbsolutePosition().y - segmentAttachment.getAbsolutePosition().y;
                    float xDiff = segmentAttachment.getParent().getAbsolutePosition().x - segmentAttachment.getAbsolutePosition().x;
                    if (segmentAttachment.getParent().getSnapMode() == 2) {
                        if (yDiff < 0) {
//                            segmentAttachment.setSnapMode(2);
                        } else {
//                            segmentAttachment.setSnapMode(3);
                        }
                    } else if (segmentAttachment.getParent().getSnapMode() == 3) {
                        if (xDiff < 0) {
//                            segmentAttachment.setSnapMode(2);
                        } else {
//                            segmentAttachment.setSnapMode(3);
                        }
                    }
                }

                if (attachment.getTarget() != null) {
//                    drawConnection(guiGraphics, attachment.getAbsolutePosition().x, attachment.getAbsolutePosition().y, attachment.getTarget().getAbsolutePosition().x, attachment.getTarget().getAbsolutePosition().y, attachment.getSnapMode(),2f, bottomBorder, bottomBorder);
//                    drawConnection(guiGraphics, attachment.getAbsolutePosition().x, attachment.getAbsolutePosition().y, attachment.getTarget().getAbsolutePosition().x, attachment.getTarget().getAbsolutePosition().y, attachment.getSnapMode(), 2f, topBackground, topBackground);
                    drawConnection(guiGraphics, attachment.getAbsolutePosition().x, attachment.getAbsolutePosition().y, attachment.getTarget().getAbsolutePosition().x, attachment.getTarget().getAbsolutePosition().y, attachment.getSnapMode(), 1f, topBorder, topBorder);
                }
            }

            for (Attachment attachment : attachments) {
                float attachX = attachment.getPosition().x;
                float attachY = attachment.getPosition().y;
                if (attachment.isColliding(adjustedMouse.x, adjustedMouse.y) && !(attachment instanceof Attachment.SegmentAttachment && connectingAttachment != null)) {
                    SPUIUtils.drawGradientRect(poseStack.last().pose(), 10, (int) (x + attachX - 2), (int) (y + attachY - 2), (int) (x + attachX + 2), (int) (y + attachY + 2), topBorder, topBorder);
                    SPUIUtils.drawGradientRect(poseStack.last().pose(), 10, (int) (x + attachX - 1), (int) (y + attachY - 1), (int) (x + attachX + 1), (int) (y + attachY + 1), background, background);
                } else {
                    SPUIUtils.drawGradientRect(poseStack.last().pose(), 10, (int) (x + attachX - 2), (int) (y + attachY - 2), (int) (x + attachX + 2), (int) (y + attachY + 2), bottomBorder, bottomBorder);
                    SPUIUtils.drawGradientRect(poseStack.last().pose(), 10, (int) (x + attachX - 1), (int) (y + attachY - 1), (int) (x + attachX + 1), (int) (y + attachY + 1), background, background);
                }

            }
            attachments.clear();

            if (node.isColliding(adjustedMouse.x, adjustedMouse.y)) {
                SPUIUtils.drawGradientRect(poseStack.last().pose(), 5, (int) (x - xLength), (int) (y - yLength), (int) (x + xLength), (int) (y + yLength), topBorder, topBorder);
                SPUIUtils.drawGradientRect(poseStack.last().pose(), 5, (int) (x - xLength + 1), (int) (y - yLength + 1), (int) (x + xLength - 1), (int) (y + yLength - 1), background, background);
            } else {
                SPUIUtils.drawGradientRect(poseStack.last().pose(), 5, (int) (x - xLength), (int) (y - yLength), (int) (x + xLength), (int) (y + yLength), bottomBorder, bottomBorder);
                SPUIUtils.drawGradientRect(poseStack.last().pose(), 5, (int) (x - xLength + 1), (int) (y - yLength + 1), (int) (x + xLength - 1), (int) (y + yLength - 1), background, background);
            }


//            guiGraphics.fill((int) (x-xLength), (int) y, (int) (x+xLength), (int) (y+yLength),topBorder);
        }
        poseStack.popPose();
    }

    private void exploreAttachment(Attachment attachment, List<Attachment> attachments) {
        attachments.add(attachment);
        if (attachment.getTarget() != null && attachment.getTarget() instanceof Attachment.SegmentAttachment) {
            exploreAttachment(attachment.getTarget(), attachments);
        }
    }

    private void dragNode(GuiGraphics guiGraphics, float mouseX, float mouseY) {
        if (selectedNode == null) {
            return;
        }
        int topBorder = Superposition.SUPERPOSITION_THEME.getColor("topBorder").argb();
        PoseStack poseStack = guiGraphics.pose();
        Vector3f mouse = new Vector3f((float) (mouseX / zoom - camera.x), (float) (mouseY / zoom - camera.y), 0);
        selectedNode.getPosition().set(mouse.x + offset.x, mouse.y + offset.y);
        for (Node node2 : card.getNodes().values()) {
            if (node2 != selectedNode) {
                if (Math.abs(node2.getPosition().x - selectedNode.getPosition().x) < 5f) {
                    selectedNode.getPosition().set(node2.getPosition().x, selectedNode.getPosition().y);
                    SPUIUtils.drawGradientRect(poseStack.last().pose(), 4, (int) (selectedNode.getPosition().x - 1), (int) Math.min(node2.getPosition().y, selectedNode.getPosition().y), (int) (selectedNode.getPosition().x + 1), (int) Math.max(selectedNode.getPosition().y, node2.getPosition().y), topBorder, topBorder);
                }
                if (Math.abs(node2.getPosition().y - selectedNode.getPosition().y) < 5f) {
                    selectedNode.getPosition().set(selectedNode.getPosition().x, node2.getPosition().y);
                    SPUIUtils.drawGradientRect(poseStack.last().pose(), 4, (int) Math.min(selectedNode.getPosition().x, node2.getPosition().x), (int) selectedNode.getPosition().y - 1, (int) Math.max(node2.getPosition().x, selectedNode.getPosition().x), (int) selectedNode.getPosition().y + 1, topBorder, topBorder);
                }
            }
        }
    }

    private void drawConnection(GuiGraphics guiGraphics, float x1, float y1, float x2, float y2, int snapMode, float width, int color1, int color2) {
        PoseStack poseStack = guiGraphics.pose();
        int xMin = (int) Math.min(x1, x2);
        int xMax = (int) Math.max(x1, x2);
        int yMin = (int) Math.min(y1, y2);
        int yMax = (int) Math.max(y1, y2);
        float xMidpoint = ((xMax - xMin) / 2) + xMin;
        float yMidpoint = ((yMax - yMin) / 2) + yMin;
        switch (snapMode) {
            case 0, 2 -> {
                if (y2 - y1 > 0) {
                    SPUIUtils.drawGradientRect(poseStack.last().pose(), 10, xMin, yMin - width, xMax, yMin + width, color1, color2);
                } else {
                    SPUIUtils.drawGradientRect(poseStack.last().pose(), 10, xMin, yMax - width, xMax, yMax + width, color1, color2);
                }
                if (x2 - x1 > 0) {
                    SPUIUtils.drawGradientRect(poseStack.last().pose(), 10, xMax - width, yMin, xMax + width, yMax, color1, color2);
                } else {
                    SPUIUtils.drawGradientRect(poseStack.last().pose(), 10, xMin - width, yMin, xMin + width, yMax, color1, color2);
                }
            }
            case 1, 3 -> {
                if (y2 - y1 > 0) {
                    SPUIUtils.drawGradientRect(poseStack.last().pose(), 10, xMin, yMax - width, xMax, yMax + width, color1, color2);
                } else {
                    SPUIUtils.drawGradientRect(poseStack.last().pose(), 10, xMin, yMin - width, xMax, yMin + width, color1, color2);
                }
                if (x2 - x1 > 0) {
                    SPUIUtils.drawGradientRect(poseStack.last().pose(), 10, xMin - width, yMin, xMin + width, yMax, color1, color2);
                } else {
                    SPUIUtils.drawGradientRect(poseStack.last().pose(), 10, xMax - width, yMin, xMax + width, yMax, color1, color2);
                }
            }
//            case 2 -> {
//                if (y2 - y1 > 0) {
//                    SPUIUtils.drawGradientRect(poseStack.last().pose(), 10, (int) x1 - 1, (int) yMin, (int) x1 + 1, (int) yMidpoint, Superposition.SUPERPOSITION_THEME.getColor("topBorder").argb(), Superposition.SUPERPOSITION_THEME.getColor("topBorder").argb());
//                    SPUIUtils.drawGradientRect(poseStack.last().pose(), 10, (int) x2 - 1, (int) yMidpoint, (int) x2 + 1, (int) yMax, Superposition.SUPERPOSITION_THEME.getColor("topBorder").argb(), Superposition.SUPERPOSITION_THEME.getColor("topBorder").argb());
//                } else {
//                    SPUIUtils.drawGradientRect(poseStack.last().pose(), 10, (int) x2 - 1, (int) yMin, (int) x2 + 1, (int) yMidpoint, Superposition.SUPERPOSITION_THEME.getColor("topBorder").argb(), Superposition.SUPERPOSITION_THEME.getColor("topBorder").argb());
//                    SPUIUtils.drawGradientRect(poseStack.last().pose(), 10, (int) x1 - 1, (int) yMidpoint, (int) x1 + 1, (int) yMax, Superposition.SUPERPOSITION_THEME.getColor("topBorder").argb(), Superposition.SUPERPOSITION_THEME.getColor("topBorder").argb());
//                }
//                SPUIUtils.drawGradientRect(poseStack.last().pose(), 10, (int) xMin, (int) yMidpoint - 1, (int) xMax, (int) yMidpoint + 1, Superposition.SUPERPOSITION_THEME.getColor("topBorder").argb(), Superposition.SUPERPOSITION_THEME.getColor("topBorder").argb());
//            }
            default -> {
                if (y2 - y1 > 0) {
                    SPUIUtils.drawGradientRect(poseStack.last().pose(), 10, xMin, yMin - width, xMax, yMin + width, color1, color2);
                } else {
                    SPUIUtils.drawGradientRect(poseStack.last().pose(), 10, xMin, yMax - width, xMax, yMax + width, color1, color2);
                }
                if (x2 - x1 > 0) {
                    SPUIUtils.drawGradientRect(poseStack.last().pose(), 10, xMax - width, yMin, xMax + width, yMax, color1, color2);
                } else {
                    SPUIUtils.drawGradientRect(poseStack.last().pose(), 10, xMin - width, yMin, xMin + width, yMax, color1, color2);
                }
//                if (x2 - x1 > 0) {
//                    SPUIUtils.drawGradientRect(poseStack.last().pose(), 10, (int) x1, (int) y1 - 1, (int) xMidpoint, (int) y1 + 1, Superposition.SUPERPOSITION_THEME.getColor("topBorder").argb(), Superposition.SUPERPOSITION_THEME.getColor("topBorder").argb());
//                    SPUIUtils.drawGradientRect(poseStack.last().pose(), 10, (int) xMidpoint, (int) y2 - 1, (int) x2, (int) y2 + 1, Superposition.SUPERPOSITION_THEME.getColor("topBorder").argb(), Superposition.SUPERPOSITION_THEME.getColor("topBorder").argb());
//                } else {
//                    SPUIUtils.drawGradientRect(poseStack.last().pose(), 10, (int) x2, (int) y2 - 1, (int) xMidpoint, (int) y2 + 1, Superposition.SUPERPOSITION_THEME.getColor("topBorder").argb(), Superposition.SUPERPOSITION_THEME.getColor("topBorder").argb());
//                    SPUIUtils.drawGradientRect(poseStack.last().pose(), 10, (int) xMidpoint, (int) y1 - 1, (int) x1, (int) y1 + 1, Superposition.SUPERPOSITION_THEME.getColor("topBorder").argb(), Superposition.SUPERPOSITION_THEME.getColor("topBorder").argb());
//                }
//                SPUIUtils.drawGradientRect(poseStack.last().pose(), 10, (int) xMidpoint - 1, (int) yMin, (int) xMidpoint + 1, (int) yMax, Superposition.SUPERPOSITION_THEME.getColor("topBorder").argb(), Superposition.SUPERPOSITION_THEME.getColor("topBorder").argb());
            }
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if ((button == 2 || (button == 0 && selectedNode == null && connectingAttachment == null)) && camera != null) {
            camera.add((float) dragX / zoom, (float) dragY / zoom);
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        Vector2f mouse = new Vector2f((float) (mouseX / zoom - camera.x), (float) (mouseY / zoom - camera.y));
        if (button == 0) {
            if (connectingAttachment != null && connectingAttachment.getAbsolutePosition().distance(mouse) < 3f && connectingAttachment instanceof Attachment.SegmentAttachment segmentAttachment) {
                segmentAttachment.clearTarget();
                connectingAttachment.getPosition().x = (float) Math.floor(connectingAttachment.getPosition().x);
                connectingAttachment.getPosition().y = (float) Math.floor(connectingAttachment.getPosition().y);
            }

            selectedNode = null;
            connectingAttachment = null;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Vector3f mouse = new Vector3f((float) (mouseX / zoom - camera.x), (float) (mouseY / zoom - camera.y), 0);
        if (button == 0 && selectedNode == null) {
            boolean found = false;
            List<Attachment> attachments = new ArrayList<>();
            for (Node node : card.getNodes().values()) {
                for (Attachment attachment : node.getAttachments()) {
                    exploreAttachment(attachment, attachments);
                }
                for (Attachment attachment : attachments) {
                    if (attachment.isColliding(mouse.x, mouse.y)) {
                        if (Screen.hasShiftDown() && attachment instanceof Attachment.SegmentAttachment segmentAttachment) {
                            connectingAttachment = segmentAttachment.getParent();
                        } else {
                            if (attachment.getTarget() != null) {
                                Attachment attachment1 = attachment.getTarget();
                                attachment.clearTarget();
                                attachment.setSegment(new Vector2f(mouse.x, mouse.y));
                                attachment.getTarget().setTarget(attachment1);
                                if (attachment1 instanceof Attachment.SegmentAttachment segmentAttachment) {
                                    segmentAttachment.setParent(attachment.getTarget());
                                }
                                connectingAttachment = attachment;
                            } else {
                                connectingAttachment = attachment;
                                attachment.clearTarget();
                            }
                        }
                        return true;
                    }
                }
                attachments.clear();
                if (node.isColliding(mouse.x, mouse.y)) {
                    offset.set(node.getPosition().x - mouse.x, node.getPosition().y - mouse.y);
                    selectedNode = node;
                    found = true;
                    return true;
                }
            }
            selectedNode = null;
            connectingAttachment = null;
        }
        if (button == 1) {
            windowPos = new Vector2f((float) mouseX, (float) mouseY);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (connectingAttachment != null && Screen.hasShiftDown()) {
            connectingAttachment.incrementSnapMode((int) Math.ceil(scrollY));
            return true;
        }
        scroll = (float) scrollY;
        return true;
    }


}

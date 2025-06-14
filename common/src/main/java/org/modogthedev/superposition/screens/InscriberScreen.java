package org.modogthedev.superposition.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import foundry.veil.api.client.color.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.client.renderer.ui.SPUIUtils;
import org.modogthedev.superposition.core.SuperpositionActions;
import org.modogthedev.superposition.screens.utils.ActionSpritesheet;
import org.modogthedev.superposition.screens.utils.Bounds;
import org.modogthedev.superposition.system.cards.Action;
import org.modogthedev.superposition.system.cards.Attachment;
import org.modogthedev.superposition.system.cards.Card;
import org.modogthedev.superposition.system.cards.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    public float windowScrollTarget = 0;
    public float windowScroll = 0;
    public Bounds windowBounds = new Bounds();
    public Action selectedAction = null;
    public boolean scrolling = false;

    public int windowWidth = 300;
    public static int windowHeight = 180;

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
        int transparentBackground = new Color().setInt(34, 120, 62, 150).argb();
        int errorTopBorder = new Color().setInt(186, 60, 94, 60).argb();
        int errorBackground = new Color().setInt(150, 44, 72, 40).argb();

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


        for (Node node : card.getNodes().values()) {
            float x = node.getPosition().x;
            float y = node.getPosition().y;
            float xLength = node.getSize().x / 2;
            float yLength = node.getSize().y / 2;

            for (Attachment attachment : node.getAttachments()) {
                exploreAttachment(attachment, attachments);
            }

            if (node.isColliding(adjustedMouse.x, adjustedMouse.y)) {
                SPUIUtils.drawGradientRect(poseStack.last().pose(), 0, (int) (x - xLength), (int) (y - yLength), (int) (x + xLength), (int) (y + yLength), topBorder, topBorder);
                SPUIUtils.drawGradientRect(poseStack.last().pose(), 0, (int) (x - xLength + 1), (int) (y - yLength + 1), (int) (x + xLength - 1), (int) (y + yLength - 1), background, background);
            } else {
                SPUIUtils.drawGradientRect(poseStack.last().pose(), 0, (int) (x - xLength), (int) (y - yLength), (int) (x + xLength), (int) (y + yLength), bottomBorder, bottomBorder);
                SPUIUtils.drawGradientRect(poseStack.last().pose(), 0, (int) (x - xLength + 1), (int) (y - yLength + 1), (int) (x + xLength - 1), (int) (y + yLength - 1), background, background);
            }

            Action action = node.getAction();
            if (action != null) {
                ItemStack item = action.getThumbnailItem();
                if (item != null) {
                    guiGraphics.renderItem(item, (int) (node.getPosition().x - node.getSize().x / 2f) + 2, (int) (node.getPosition().y - node.getSize().y / 2f) + 2);
                } else {
                    ActionSpritesheet spritesheet = SuperpositionActions.SPRITESHEET;
                    ActionSpritesheet.SpriteInformation sprite = spritesheet.get(action.getSelfReference());
                    guiGraphics.blit(spritesheet.spritesheetLocation, (int) (node.getPosition().x - node.getSize().x / 2f) + 2, (int) (node.getPosition().y - node.getSize().y / 2f) + 2, sprite.u1(), sprite.u2(), sprite.v1(), sprite.v2(), spritesheet.scale, spritesheet.scale);
                }
            }

            for (Attachment attachment : attachments) {

                if (attachment.getTarget() != null) {
                    drawConnection(guiGraphics, attachment.getAbsolutePosition().x, attachment.getAbsolutePosition().y, attachment.getTarget().getAbsolutePosition().x, attachment.getTarget().getAbsolutePosition().y, attachment.getSnapMode(), 1f, topBorder, topBorder);
                }
            }

            for (Attachment attachment : attachments) {
                float attachX = attachment.getPosition().x;
                float attachY = attachment.getPosition().y;
                if (connectingAttachment != null && connectingAttachment == attachment && connectingAttachment instanceof Attachment.SegmentAttachment segmentAttachment && connectingAttachment.getAbsolutePosition().distance(segmentAttachment.getParent().getAbsolutePosition()) < 5) {
                    SPUIUtils.drawGradientRect(poseStack.last().pose(), 0, (int) (x + attachX - 2), (int) (y + attachY - 2), (int) (x + attachX + 2), (int) (y + attachY + 2), errorTopBorder, errorTopBorder);
                    SPUIUtils.drawGradientRect(poseStack.last().pose(), 0, (int) (x + attachX - 1), (int) (y + attachY - 1), (int) (x + attachX + 1), (int) (y + attachY + 1), errorBackground, errorBackground);
                } else if (attachment.isColliding(adjustedMouse.x, adjustedMouse.y) && !(attachment instanceof Attachment.SegmentAttachment && connectingAttachment != null) || (connectingAttachment != null && connectingAttachment.getTarget() == attachment)) {
                    SPUIUtils.drawGradientRect(poseStack.last().pose(), 0, (int) (x + attachX - 2), (int) (y + attachY - 2), (int) (x + attachX + 2), (int) (y + attachY + 2), topBorder, topBorder);
                    SPUIUtils.drawGradientRect(poseStack.last().pose(), 0, (int) (x + attachX - 1), (int) (y + attachY - 1), (int) (x + attachX + 1), (int) (y + attachY + 1), background, background);
                } else {
                    SPUIUtils.drawGradientRect(poseStack.last().pose(), 0, (int) (x + attachX - 2), (int) (y + attachY - 2), (int) (x + attachX + 2), (int) (y + attachY + 2), bottomBorder, bottomBorder);
                    SPUIUtils.drawGradientRect(poseStack.last().pose(), 0, (int) (x + attachX - 1), (int) (y + attachY - 1), (int) (x + attachX + 1), (int) (y + attachY + 1), background, background);
                }

            }
            attachments.clear();
        }

        // Render add window
        poseStack.popPose();
        poseStack.pushPose();
        if (windowPos != null) {
            Vector2f storedPos = new Vector2f(windowPos);

            int windowWidth = 2;
            windowPos.x = Math.min(width - this.windowWidth - windowWidth, windowPos.x);
            windowPos.y = Math.min(height - windowHeight - windowWidth, windowPos.y);
            SPUIUtils.drawGradientRect(poseStack.last().pose(), 0, (int) (windowPos.x - windowWidth), (int) (windowPos.y), (int) (windowPos.x), (int) (windowPos.y + windowHeight), transparentBackground, transparentBackground);
            SPUIUtils.drawGradientRect(poseStack.last().pose(), 0, (int) (windowPos.x + this.windowWidth), (int) (windowPos.y), (int) (windowPos.x + this.windowWidth + windowWidth), (int) (windowPos.y + windowHeight), transparentBackground, transparentBackground);
            SPUIUtils.drawGradientRect(poseStack.last().pose(), 0, (int) (windowPos.x), (int) (windowPos.y - windowWidth), (int) (windowPos.x + this.windowWidth), (int) (windowPos.y + windowHeight + windowWidth), transparentBackground, transparentBackground);

            SPUIUtils.drawGradientRect(poseStack.last().pose(), 0, (int) (windowPos.x), (int) (windowPos.y), (int) (windowPos.x + windowWidth), (int) (windowPos.y + windowHeight), topBorder, bottomBorder);
            SPUIUtils.drawGradientRect(poseStack.last().pose(), 0, (int) (windowPos.x + this.windowWidth - windowWidth), (int) (windowPos.y), (int) (windowPos.x + this.windowWidth), (int) (windowPos.y + windowHeight), topBorder, bottomBorder);
            SPUIUtils.drawGradientRect(poseStack.last().pose(), 0, (int) (windowPos.x + windowWidth), (int) (windowPos.y), (int) (windowPos.x + this.windowWidth - windowWidth), (int) (windowPos.y + windowWidth), topBorder, topBorder);
            SPUIUtils.drawGradientRect(poseStack.last().pose(), 0, (int) (windowPos.x + windowWidth), (int) (windowPos.y + windowHeight - windowWidth), (int) (windowPos.x + this.windowWidth - windowWidth), (int) (windowPos.y + windowHeight), bottomBorder, bottomBorder);


            windowScrollTarget = Math.min(0, windowScrollTarget);
            windowScrollTarget = Math.max(-SuperpositionActions.getAllRegisteredActions().size() * 4, windowScrollTarget);
            windowScroll = windowScrollTarget + windowScroll / 2;


            Vector2f textPosition = new Vector2f(windowPos.x + 25, windowPos.y + windowScroll);
            guiGraphics.enableScissor((int) (windowPos.x + windowWidth), (int) (windowPos.y + windowWidth), (int) (windowPos.x + this.windowWidth - windowWidth), (int) (windowPos.y + windowHeight - windowWidth));
            if (!Bounds.isColliding(windowBounds.getMinX(), windowBounds.getMinY(), windowBounds.getMaxX(), windowBounds.getMaxY(), (float) mouseX, (float) mouseY)) {
                selectedAction = null;
            }
            for (Action action : SuperpositionActions.getAllRegisteredActions()) {
                if (textPosition.y > windowPos.y + windowHeight) {
                    break;
                }
                if (Bounds.isColliding(windowBounds.getMinX(), (int) textPosition.y + 12, windowBounds.getMinX() + 150, (int) (textPosition.y + 30), (float) mouseX, (float) mouseY)) {
                    selectedAction = action;
                }
                textPosition.add(0, 18);
                ItemStack item = action.getThumbnailItem();
                if (item != null) {
                    guiGraphics.renderItem(item, (int) (textPosition.x - 20), (int) textPosition.y - 4);
                } else {
                    ActionSpritesheet spritesheet = SuperpositionActions.SPRITESHEET;
                    ActionSpritesheet.SpriteInformation sprite = spritesheet.get(action.getSelfReference());
                    guiGraphics.blit(spritesheet.spritesheetLocation, (int) textPosition.x - 20, (int) textPosition.y - 4, sprite.u1(), sprite.u2(), sprite.v1(), sprite.v2(), spritesheet.scale, spritesheet.scale);
                }
                if (selectedAction == action || selectedAction == null) {
                    guiGraphics.drawString(Minecraft.getInstance().font, action.getInfo().name(), (int) textPosition.x, (int) textPosition.y, topBorder);
                } else {
                    guiGraphics.drawString(Minecraft.getInstance().font, action.getInfo().name(), (int) textPosition.x, (int) textPosition.y, bottomBorder);
                }
            }

            if (Bounds.isColliding(windowBounds.getMinX() + 130, (int) (windowBounds.getMinY() - windowScroll), windowBounds.getMinX() + 150, (int) (windowBounds.getMinY() - windowScroll + (SuperpositionActions.getAllRegisteredActions().size() * 18 - windowHeight)), (float) mouseX, (float) mouseY)) {
                SPUIUtils.drawGradientRect(poseStack.last().pose(), 0, (int) (windowPos.x + 145), (int) (windowPos.y - windowScroll), (int) (windowPos.x + 148), (int) (windowPos.y - windowScroll + (SuperpositionActions.getAllRegisteredActions().size() * 18 - windowHeight)), topBorder, topBorder);
            } else {
                SPUIUtils.drawGradientRect(poseStack.last().pose(), 0, (int) (windowPos.x + 145), (int) (windowPos.y - windowScroll), (int) (windowPos.x + 148), (int) (windowPos.y - windowScroll + (SuperpositionActions.getAllRegisteredActions().size() * 18 - windowHeight)), bottomBorder, bottomBorder);
            }
            if (selectedAction != null) {
                Action action = selectedAction;
                ItemStack item = action.getThumbnailItem();
                poseStack.pushPose();
                poseStack.scale(5, 5, 1);
                if (item != null) {
                    guiGraphics.renderItem(item, (int) (windowPos.x + 180) / 5, (int) (windowPos.y + 10) / 5);
                } else {
                    ActionSpritesheet spritesheet = SuperpositionActions.SPRITESHEET;
                    ActionSpritesheet.SpriteInformation sprite = spritesheet.get(action.getSelfReference());
                    guiGraphics.blit(spritesheet.spritesheetLocation, (int) (windowPos.x + 180) / 5, (int) (windowPos.y + 10) / 5, sprite.u1(), sprite.u2(), sprite.v1(), sprite.v2(), spritesheet.scale, spritesheet.scale);
                }
                poseStack.popPose();
                guiGraphics.drawString(Minecraft.getInstance().font, action.getInfo().name(), (int) (windowPos.x + 150), (int) windowPos.y + 100, topBorder);
                int textWrapY = (int) windowPos.y + 115;
                for (FormattedCharSequence formattedcharsequence : font.split(action.getInfo().description(), 140)) {
                    guiGraphics.drawString(font, formattedcharsequence, (int) (windowPos.x + 150), textWrapY, topBorder, true);
                    textWrapY += 9;
                }
                this.windowWidth = 300;
            } else {
                this.windowWidth = 150;
            }
            windowBounds.setMinX((int) windowPos.x);
            windowBounds.setMaxX((int) windowPos.x + this.windowWidth);
            windowBounds.setMinY((int) windowPos.y);
            windowBounds.setMaxY((int) windowPos.y + windowHeight);
            guiGraphics.disableScissor();

            windowPos.set(storedPos);
        }

        // Cleanup
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
                    SPUIUtils.drawGradientRect(poseStack.last().pose(), 0, xMin, yMin - width, xMax, yMin + width, color1, color2);
                } else {
                    SPUIUtils.drawGradientRect(poseStack.last().pose(), 0, xMin, yMax - width, xMax, yMax + width, color1, color2);
                }
                if (x2 - x1 > 0) {
                    SPUIUtils.drawGradientRect(poseStack.last().pose(), 0, xMax - width, yMin, xMax + width, yMax, color1, color2);
                } else {
                    SPUIUtils.drawGradientRect(poseStack.last().pose(), 0, xMin - width, yMin, xMin + width, yMax, color1, color2);
                }
            }
            case 1, 3 -> {
                if (y2 - y1 > 0) {
                    SPUIUtils.drawGradientRect(poseStack.last().pose(), 0, xMin, yMax - width, xMax, yMax + width, color1, color2);
                } else {
                    SPUIUtils.drawGradientRect(poseStack.last().pose(), 0, xMin, yMin - width, xMax, yMin + width, color1, color2);
                }
                if (x2 - x1 > 0) {
                    SPUIUtils.drawGradientRect(poseStack.last().pose(), 0, xMin - width, yMin, xMin + width, yMax, color1, color2);
                } else {
                    SPUIUtils.drawGradientRect(poseStack.last().pose(), 0, xMax - width, yMin, xMax + width, yMax, color1, color2);
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
                    SPUIUtils.drawGradientRect(poseStack.last().pose(), 0, xMin, yMin - width, xMax, yMin + width, color1, color2);
                } else {
                    SPUIUtils.drawGradientRect(poseStack.last().pose(), 0, xMin, yMax - width, xMax, yMax + width, color1, color2);
                }
                if (x2 - x1 > 0) {
                    SPUIUtils.drawGradientRect(poseStack.last().pose(), 0, xMax - width, yMin, xMax + width, yMax, color1, color2);
                } else {
                    SPUIUtils.drawGradientRect(poseStack.last().pose(), 0, xMin - width, yMin, xMin + width, yMax, color1, color2);
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
        if (button == 0 && scrolling) {
            windowScrollTarget += (float) -dragY / 2;
            return true;
        }
        if ((button == 2 || (button == 0 && selectedNode == null && connectingAttachment == null)) && camera != null) {

            camera.add((float) dragX / zoom, (float) dragY / zoom);
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        Vector2f mouse = new Vector2f((float) (mouseX / zoom - camera.x), (float) (mouseY / zoom - camera.y));
        if (button == 0) {
            if (connectingAttachment != null && connectingAttachment instanceof Attachment.SegmentAttachment segmentAttachment && connectingAttachment.getAbsolutePosition().distance(segmentAttachment.getParent().getAbsolutePosition()) < 5) {
                segmentAttachment.clearTarget();
                connectingAttachment.getPosition().x = (float) Math.floor(connectingAttachment.getPosition().x);
                connectingAttachment.getPosition().y = (float) Math.floor(connectingAttachment.getPosition().y);
            }

            selectedNode = null;
            connectingAttachment = null;
            scrolling = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Vector3f mouse = new Vector3f((float) (mouseX / zoom - camera.x), (float) (mouseY / zoom - camera.y), 0);
        if (button == 0 && selectedNode == null) {
            if (windowPos != null && windowBounds.isColliding((float) mouseX, (float) mouseY)) {
                if (Bounds.isColliding(windowBounds.getMinX() + 130, (int) (windowBounds.getMinY() - windowScroll), windowBounds.getMinX() + 150, (int) (windowBounds.getMinY() - windowScroll + (SuperpositionActions.getAllRegisteredActions().size() * 18 - windowHeight)), (float) mouseX, (float) mouseY)) {
                    scrolling = true;
                    return true;
                }
                Vector2f textPosition = new Vector2f(windowPos.x + 25, windowPos.y + windowScroll);
                for (Action action : SuperpositionActions.getAllRegisteredActions()) {
                    if (Bounds.isColliding(windowBounds.getMinX(), (int) textPosition.y + 12, windowBounds.getMinX() + 150, (int) (textPosition.y + 30), (float) mouseX, (float) mouseY)) {
                        Node node = new Node(card);
                        node.updateAction(action.copy());
                        node.getPosition().set(mouse.x, mouse.y);
                        card.getNodes().put(UUID.randomUUID(), node);
                        if (!Screen.hasShiftDown()) {
                            windowPos = null;
                        }
                        return true;
                    }
                    textPosition.add(0, 18);
                }
            }

            boolean found = false;
            List<Attachment> attachments = new ArrayList<>();
            for (Node node : card.getNodes().values()) {
                for (Attachment attachment : node.getAttachments()) {
                    exploreAttachment(attachment, attachments);
                }
                for (Attachment attachment : attachments) {
                    if (attachment.isColliding(mouse.x, mouse.y)) {
                        if ((!Screen.hasShiftDown()) && attachment instanceof Attachment.SegmentAttachment segmentAttachment) {
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
            if (windowPos == null) {
                windowPos = new Vector2f(Math.round(mouseX), Math.round(mouseY));
            } else {
                windowPos.set(Math.round(mouseX), Math.round(mouseY));
            }
            for (Node node : card.getNodes().values()) {
                if (node.isColliding(mouse.x, mouse.y)) {
                    Action action = node.getAction();
                    if (action != null) {
                        selectedAction = action;
                    }
                }
            }

            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (windowPos != null && windowBounds.isColliding((float) mouseX, (float) mouseY)) {
            windowScrollTarget += (float) scrollY * 9;
            return true;
        }
        if (connectingAttachment != null && Screen.hasShiftDown()) {
            connectingAttachment.incrementSnapMode((int) Math.ceil(scrollY));
            return true;
        }
        scroll = (float) scrollY;
        return true;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}

package org.modogthedev.superposition.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import foundry.veil.api.network.VeilPacketManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import org.joml.Vector3f;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.core.SuperpositionWidgets;
import org.modogthedev.superposition.item.WidgetItem;
import org.modogthedev.superposition.networking.packet.ChangeWidgetC2SPacket;
import org.modogthedev.superposition.system.widget.Widget;
import org.modogthedev.superposition.system.widget.WidgetRenderer;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WidgetSelectionScreen extends Screen {
    private static final ResourceLocation SEARCH = ResourceLocation.fromNamespaceAndPath(Superposition.MODID, "textures/screen/search.png");
    protected final InteractionHand hand;
    protected float xScrollPosition = 0;
    protected int xScroll = 0;
    protected float focusX = -1;
    protected float focusY = -1;
    protected int lastMouseX = -1;
    protected int lastMouseY= -1;
    protected int target = 0;
    protected List<ResourceLocation> currentWidgets = new ArrayList<>();
    protected String search = "";
    protected EditBox searchField = new EditBox(Minecraft.getInstance().font,100,16,Component.empty());

    protected WidgetSelectionScreen(InteractionHand hand) {
        super(Component.empty());
        this.hand = hand;


        searchField.setBordered(false);
        searchField.setTextColor(Superposition.SUPERPOSITION_THEME.get("topBorder"));
        searchField.setVisible(false);
        addRenderableWidget(searchField);
        setInitialFocus(searchField);
    }

    @Override
    protected void init() {
        super.init();
        ItemStack itemInHand = Minecraft.getInstance().player.getItemInHand(hand);
        ResourceLocation type = WidgetItem.getType(itemInHand);
        Registry<Widget> widgetRegistry = SuperpositionWidgets.WIDGET.asVanillaRegistry();
        currentWidgets.addAll(widgetRegistry.keySet());
        for (int i = 0; i < currentWidgets.size(); i++) {
            if (currentWidgets.get(i).equals(type)) {
                xScroll = i;
                xScrollPosition = getIndexOffset(xScroll);
                break;
            }
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Minecraft mc = Minecraft.getInstance();
        int xSize = (this.width) / 2;
        int ySize = (this.height) / 2;
        if (searchField.isVisible()) {
            searchField.setPosition(xSize-48,ySize-48);
        }
        Integer bottomBorder = Superposition.SUPERPOSITION_THEME.get("bottomBorder");
        Integer topBorder = Superposition.SUPERPOSITION_THEME.get("topBorder");
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        if (searchField.isVisible()) {
            guiGraphics.blit(SEARCH,xSize-64,ySize-52,0,0,16,16,16,16);
            guiGraphics.fill(xSize-62,ySize-34,xSize+62,ySize-36, bottomBorder);
        }
        Registry<Widget> widgetRegistry = SuperpositionWidgets.WIDGET.asVanillaRegistry();
        PoseStack poseStack = guiGraphics.pose();
        float x = -Mth.lerp(partialTick/2f,xScrollPosition,getIndexOffset(xScroll));
        poseStack.pushPose();
        poseStack.translate(xSize + 0, ySize + 0, 50);
        poseStack.scale(100.0F, -100.0F, 100.0F); // Scale up to view model, negate Y for screen orientation
        poseStack.mulPose(Axis.XP.rotationDegrees(90));

        float thisFocusX = Mth.lerp(partialTick/2f,focusX,mouseX);
        float thisFocusY = Mth.lerp(partialTick/2f,focusY,mouseY);


        MultiBufferSource.BufferSource bufferSource = guiGraphics.bufferSource();
        float divide = 4f;
        for (ResourceLocation location : currentWidgets) {
            Widget widget = widgetRegistry.get(location);
            if (widget != null) {
                WidgetRenderer<Widget> renderer = Widget.getRenderer(widget);
                if (renderer != null) {
                    Vector3f bounds = widget.getBounds();
                    poseStack.pushPose();
                    poseStack.translate(x, 0,0);
                    poseStack.mulPose(Axis.ZP.rotationDegrees(((x*16)-(thisFocusX-xSize))/(divide*2)));
                    poseStack.mulPose(Axis.XP.rotationDegrees((thisFocusY-ySize)/divide));
                    poseStack.translate(-bounds.x/2f, 0, -bounds.z/2f);
                    Color color = new Color(1f, 1f, 1f, 1f);
                    renderer.render(widget, Blocks.AIR.defaultBlockState(),partialTick, poseStack, bufferSource, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, color);


                    poseStack.popPose();

                    x += bounds.x + 5/16f;
                    divide += 1;
                }
            }
        }
        poseStack.popPose();
        if (currentWidgets.size() > xScroll) {
            ResourceLocation location = currentWidgets.get(xScroll);
            guiGraphics.drawCenteredString(mc.font, Component.translatable( "widget." + location.getNamespace() + "." + location.getPath()), xSize, ySize + 30, topBorder);
        }


        lastMouseX = mouseX;
        lastMouseY = mouseY;
    }

    public float getIndexOffset(int index) {
        Registry<Widget> widgetRegistry = SuperpositionWidgets.WIDGET.asVanillaRegistry();
        float x = 0;
        int currentIndex= 0;
        for (ResourceLocation location : currentWidgets) {
            currentIndex++;
            if (currentIndex > index) {
                break;
            }

            Widget widget = widgetRegistry.get(location);
            if (widget != null) {
                WidgetRenderer<Widget> renderer = Widget.getRenderer(widget);
                if (renderer != null) {
                    Vector3f bounds = widget.getBounds();
                    x += bounds.x + 5/16f;
                }
            }
        }
        return x;
    }

    @Override
    public void tick() {
        super.tick();
        search = searchField.getValue();
        focusX = Mth.lerp(0.5f,focusX,lastMouseX);
        focusY = Mth.lerp(0.5f,focusY,lastMouseY);
        xScrollPosition = Mth.lerp(0.5f,xScrollPosition,getIndexOffset(xScroll));
        Registry<Widget> widgetRegistry = SuperpositionWidgets.WIDGET.asVanillaRegistry();
        currentWidgets.clear();
        for (ResourceLocation resourceLocation : widgetRegistry.keySet()) {
            currentWidgets.add(resourceLocation);
        }
        if (!Objects.equals(search,"")) {
            currentWidgets.sort((s1, s2) -> Double.compare(getSimilarity(s2.getPath(), search), getSimilarity(s1.getPath(), search)));
        }
    }

    private static double getSimilarity(String text, String target) {
        int i = 0;
        while (i < text.length() && i < target.length() && text.charAt(i) == target.charAt(i)) {
            i++;
        }
        return i;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        setFocused(searchField);
        searchField.setVisible(true);
        boolean consume = super.charTyped(codePoint, modifiers);
        if (consume) {
            xScroll = 0;
        }
        return consume;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        setFocused(searchField);
        return super.keyPressed(keyCode,scanCode,modifiers);
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean consume = super.mouseClicked(mouseX, mouseY, button);
        if (!consume && searchField.getValue().isBlank()) {
            searchField.setVisible(false);
        }
        return consume;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        xScroll += (int) Math.round(scrollY);
        xScroll = Mth.clamp(xScroll,0,currentWidgets.size()-1);
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public void onClose() {
        if (currentWidgets.size() > xScroll) {
            VeilPacketManager.server().sendPacket(new ChangeWidgetC2SPacket(currentWidgets.get(xScroll)));
        }
        super.onClose();
    }
}

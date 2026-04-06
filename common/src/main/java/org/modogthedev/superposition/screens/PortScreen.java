package org.modogthedev.superposition.screens;

import com.mojang.blaze3d.vertex.VertexConsumer;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.api.network.VeilPacketManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.blockentity.SignalActorBlockEntity;
import org.modogthedev.superposition.core.SuperpositionRenderTypes;
import org.modogthedev.superposition.networking.packet.PlayerPlugCableC2SPacket;
import org.modogthedev.superposition.system.cable.Cable;
import org.modogthedev.superposition.system.cable.CableManager;
import org.modogthedev.superposition.system.cable.PortConfig;
import org.modogthedev.superposition.system.cable.rope_system.AnchorConstraint;

import java.util.*;

public class PortScreen extends Screen {
    private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(Superposition.MODID, "textures/screen/ports.png");
    private static final ResourceLocation CABLE_PORT = ResourceLocation.fromNamespaceAndPath(Superposition.MODID, "textures/misc/screen_cable_port.png");
    private static final ResourceLocation CABLE_PLUG = ResourceLocation.fromNamespaceAndPath(Superposition.MODID, "textures/misc/screen_cable_plug.png");
    private final BlockPos pos;
    private SignalActorBlockEntity signalActor;
    private float scroll = 0;
    private float maxScroll = 0;
    private PortConfig.ScreenCable focusedCable = null;
    private List<PortConfig.ScreenCable> screenCables = new ArrayList<>();

    protected PortScreen(BlockPos pos) {
        super(Component.empty());
        this.pos = pos;

        Map<UUID, Cable> cables = CableManager.getCables(Minecraft.getInstance().level);
        for (Cable cable : cables.values()) {
            AnchorConstraint anchor = cable.getPoints().getFirst().getAnchor();
            if (anchor != null && anchor.getAnchorBlock().equals(pos)) {
                screenCables.addFirst(new PortConfig.ScreenCable(cable, true));
            }
            anchor = cable.getPoints().getLast().getAnchor();
            if (anchor != null && anchor.getAnchorBlock().equals(pos)) {
                screenCables.addFirst(new PortConfig.ScreenCable(cable, false));
            }
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        if (signalActor == null) {
            return;
        }

        Vector2f start = new Vector2f();
        Vector2f end = new Vector2f();

        ShaderProgram cableShader = VeilRenderSystem.renderer().getShaderManager().getShader(Superposition.id("screen_cable"));
        if (cableShader != null) {
            cableShader.getOrCreateUniform("Screen").setVector(this.width, this.height);
        }

        PortConfig portConfig = signalActor.getPortConfig();
        Collection<PortConfig.Port> values = portConfig.getAll().values();
        int i = (this.width - 176) / 2;
        int j = (int) ((float) (this.height - values.size() * 32 - 16) / 2 + scroll);
        blit(guiGraphics, i, j, 0, 11, 0, 1);
        j += 16;
        int k = 0;
        Iterator<PortConfig.Port> iterator = values.iterator();
        Minecraft instance = Minecraft.getInstance();
        while (iterator.hasNext()) {
            PortConfig.Port port = iterator.next();
            if (iterator.hasNext()) {
                blit(guiGraphics, i, j + (k * 32), 0, 11, 1, 3);
            } else {
                blit(guiGraphics, i, j + (k * 32), 0, 11, 2, 4);
            }
            switch (port.getIO()) {
                case IN -> {
                    blit(guiGraphics, i, j + (k * 32), 0, 11, 4, 6);
                    guiGraphics.drawString(instance.font, port.getName(), i + 34, j + (k * 32) + 12, 0xFF22DD11);
                }
                case OUT -> {
                    blit(guiGraphics, i, j + (k * 32), 0, 11, 6, 8);
                    guiGraphics.drawString(instance.font, port.getName(), i + 140 - instance.font.width(port.getName()), j + (k * 32) + 12, 0xFF22DD11);
                }
                case BOTH -> {
                    blit(guiGraphics, i, j + (k * 32), 0, 11, 8, 10);
                    guiGraphics.drawCenteredString(instance.font, port.getName(), i + 88, j + (k * 32) + 12, 0xFF22DD11);
                }
                default -> {
                }
            }
            k++;
        }
        maxScroll = Math.max(0, (k * 32)/2f - 64);

        int l = 0;
        int m = 0;
        int height = k * 32;
        int inCount = 0;
        int outCount = 0;

        for (PortConfig.ScreenCable screenCable : screenCables) {
            if (screenCable.isOut()) {
                outCount += 1;
            } else {
                inCount += 1;
            }
        }
        int n = 0;
        for (PortConfig.ScreenCable screenCable : screenCables) {
            Vector2f startPosition = screenCable.getStartPosition();
            Vector2f focusPosition = new Vector2f(screenCable.getFocusPosition());
            if (screenCable.isOut()) {
                startPosition.set(this.width + 10, j + height / 2f + (m - outCount / 2f) * 25);
                focusPosition.set(this.width-20,startPosition.y);
                m++;
            } else {
                startPosition.set(-10, j + height / 2f + (l - inCount / 2f) * 25);
                focusPosition.set(20,startPosition.y);
                l++;
            }
            if (focusedCable == screenCable) {
                focusPosition.set(mouseX, mouseY);
            }
            if (screenCable.getFocusPosition().x == 0 && screenCable.getFocusPosition().y == 0) {
                screenCable.getFocusPosition().set(focusPosition);
            }
            focusPosition = screenCable.getFocusPosition().lerp(focusPosition,0.15f);
            k = 0;
            iterator = values.iterator();
            while (iterator.hasNext()) {
                PortConfig.Port port = iterator.next();
                if (port.getName().equals(screenCable.getBind()) || port.getName().equals(screenCable.tempPort)) {
                    focusPosition.set(i + 20 + (screenCable.isOut() ? 135 : 0), j + k * 32 + 16);
                    break;
                }

                k++;
            }
            if (cableShader != null) {
                cableShader.getOrCreateUniform("BoxMin").setVector(startPosition.x/this.width, startPosition.y/this.height);
                cableShader.getOrCreateUniform("BoxMax").setVector(focusPosition.x/this.width, focusPosition.y/this.height);
            }
            int rgb = screenCable.getCable().getColor().getRGB();
            fill(guiGraphics,SuperpositionRenderTypes.screenCable(), (int) startPosition.x, (int) startPosition.y, (int) focusPosition.x, (int) focusPosition.y, 1 + n * 2, rgb, screenCable.getBind() != null);
            guiGraphics.fill((int) (startPosition.x - 10), (int) (startPosition.y - 10 + k), (int) (startPosition.x + 10), (int) (startPosition.y + 10 + k),2, rgb);
            if (screenCable.getBind() == null) {
                if (screenCable.isOut()) {
                    guiGraphics.innerBlit(CABLE_PLUG, (int) focusPosition.x - 16, (int) focusPosition.x, (int) focusPosition.y - 7, (int) focusPosition.y + 9, 2 + n * 2, 1, 0, 0, 1);
                } else {
                    guiGraphics.innerBlit(CABLE_PLUG, (int) focusPosition.x, (int) focusPosition.x + 16, (int) focusPosition.y - 7, (int) focusPosition.y + 9, 2 + n * 2, 0, 1, 0, 1);
                }
            } else {
                guiGraphics.blit(CABLE_PORT, (int) (focusPosition.x-8), (int) (focusPosition.y-8),0,0,16,16,16,16);
            }
            n++;
//            guiGraphics.fill((int) (focusPosition.x - 10), (int) (focusPosition.y - 10 + k), (int) (focusPosition.x + 10), (int) (focusPosition.y + 10 + k),2, rgb);
        }
    }

    public void fill(GuiGraphics guiGraphics,RenderType renderType, int minX, int minY, int maxX, int maxY, int z, int color, boolean wrap) {
        Matrix4f matrix4f = guiGraphics.pose().last().pose();
        boolean flipX = false;
        boolean flipY = false;
        if (minX < maxX) {
            int i = minX;
            minX = maxX;
            maxX = i;
            flipX = true;
        }

        if (minY < maxY) {
            int j = minY;
            minY = maxY;
            maxY = j;
            flipY = true;
        }
        minY += 10;
        maxY -= 10;
        if (wrap) {
            minX += 10;
            maxX -= 10;
        }

        VertexConsumer vertexconsumer = guiGraphics.bufferSource().getBuffer(renderType);
        vertexconsumer.addVertex(matrix4f, (float)minX, (float)minY - 0.5f, (float)z).setColor(color).setNormal(flipX ? 1 : 0,flipY ? 1 : 0,0);
        vertexconsumer.addVertex(matrix4f, (float)minX, (float)maxY - 0.5f, (float)z).setColor(color).setNormal(flipX ? 1 : 0,flipY ? 0 : 1,0);
        vertexconsumer.addVertex(matrix4f, (float)maxX, (float)maxY - 0.5f, (float)z).setColor(color).setNormal(flipX ? 0 : 1,flipY ? 0 : 1,0);
        vertexconsumer.addVertex(matrix4f, (float)maxX, (float)minY - 0.5f, (float)z).setColor(color).setNormal(flipX ? 0 : 1,flipY ? 1 : 0,0);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        scroll += (float) scrollY * 10;
        scroll = Mth.clamp(scroll, -maxScroll, maxScroll);
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (signalActor == null) {
            return false;
        }
        PortConfig portConfig = signalActor.getPortConfig();
        Vector2f mouse = new Vector2f((float) mouseX, (float) mouseY);

        for (PortConfig.ScreenCable screenCable : screenCables) {
            if (mouse.distance(screenCable.getFocusPosition()) < 10) {
                focusedCable = screenCable;
                screenCable.bind(null);
                VeilPacketManager.server().sendPacket(new PlayerPlugCableC2SPacket(focusedCable.getCable().getId(),null,focusedCable.isOut()));
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (focusedCable == null) {
            return false;
        }
        PortConfig portConfig = signalActor.getPortConfig();
        Collection<PortConfig.Port> values = portConfig.getAll().values();
        int i = (this.width - 176) / 2;
        int j = (int) ((float) (this.height - values.size() * 32 - 16) / 2 + scroll);
        j += 16;
        int k = 0;
        Iterator<PortConfig.Port> iterator = values.iterator();
        Minecraft instance = Minecraft.getInstance();
        while (iterator.hasNext()) {
            PortConfig.Port port = iterator.next();
            int y;
            int x;
            if (focusedCable.isOut()) {
               y = j + k * 32;
               x = i + 135;
               if (port.getIO() == PortConfig.IO.IN) {
                   k++;
                   continue;
               }
            } else {
                y = j + k * 32;
                x = i;
                if (port.getIO() == PortConfig.IO.OUT) {
                    k++;
                    continue;
                }
            }
            if (mouseY > y && mouseY < y + 32 && mouseX > x && mouseX < x + 32) {
                focusedCable.bind(port.getName());
                VeilPacketManager.server().sendPacket(new PlayerPlugCableC2SPacket(focusedCable.getCable().getId(),port.getName(),focusedCable.isOut()));
            }
            k++;
        }
        focusedCable = null;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void tick() {
        super.tick();
        BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(pos);
        if (blockEntity instanceof SignalActorBlockEntity signalActorBlockEntity) {
            signalActor = signalActorBlockEntity;
        } else {
            onClose();
        }
        for (PortConfig.ScreenCable screenCable : screenCables) {
            if (screenCable.ticksInTemp > 0) {
                screenCable.ticksInTemp --;
                if (screenCable.ticksInTemp <= 0) {
                    screenCable.tempPort = null;
                }
            }
        }
    }

    public void blit(GuiGraphics guiGraphics, int x, int y, int minX, int maxX, int minY, int maxY) {
        guiGraphics.blit(BACKGROUND, x, y, minX * 16, minY * 16, (maxX - minX) * 16, (maxY - minY) * 16);
    }

    @Override
    public void onClose() {
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public static boolean shouldOpen(BlockPos pos) {
        return Minecraft.getInstance().level.getBlockEntity(pos) instanceof SignalActorBlockEntity;
    }
}

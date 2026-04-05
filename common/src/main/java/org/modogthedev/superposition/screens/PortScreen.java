package org.modogthedev.superposition.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.joml.Vector2f;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.blockentity.SignalActorBlockEntity;
import org.modogthedev.superposition.system.cable.PortConfig;

import java.util.Collection;
import java.util.Iterator;

public class PortScreen extends Screen {
    private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(Superposition.MODID, "textures/screen/ports.png");
    private final BlockPos pos;
    private SignalActorBlockEntity signalActor;
    private float scroll = 0;
    private float maxScroll = 0;
    private PortConfig.ScreenCable focusedCable = null;

    protected PortScreen(BlockPos pos) {
        super(Component.empty());
        this.pos = pos;

    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        if (signalActor == null) {
            return;
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
        maxScroll = Math.max(0, k * 32 - 256);

        int l = 0;
        int m = 0;
        int height = k * 32;
        int inCount = 0;
        int outCount = 0;

        for (PortConfig.ScreenCable screenCable : portConfig.getScreenCables()) {
            if (screenCable.isOut()) {
                outCount += 1;
            } else {
                inCount += 1;
            }
        }
        for (PortConfig.ScreenCable screenCable : portConfig.getScreenCables()) {
            Vector2f startPosition = screenCable.getStartPosition();
            if (screenCable.isOut()) {
                startPosition.set(this.width, j + height / 2f + (m - outCount / 2f) * 25);
                m++;
            } else {
                startPosition.set(0, j + height / 2f + (l - inCount / 2f) * 25);
                l++;
            }
            Vector2f focusPosition = screenCable.getFocusPosition();
            focusPosition.set(startPosition);
            if (focusedCable == screenCable) {
                focusedCable.getFocusPosition().set(mouseX, mouseY);
            }
            k = 0;
            iterator = values.iterator();
            while (iterator.hasNext()) {
                PortConfig.Port port = iterator.next();
                if (port.getConnections().contains(screenCable.getCable().getId())) {
                    focusPosition.set(i + 20 + (screenCable.isOut() ? 135 : 0), j + k * 32 + 16);
                    break;
                }

                k++;
            }
            guiGraphics.fill((int) (startPosition.x - 10), (int) (startPosition.y - 10 + k), (int) (startPosition.x + 10), (int) (startPosition.y + 10 + k), screenCable.getCable().getColor().getRGB());
            guiGraphics.fill((int) (focusPosition.x - 10), (int) (focusPosition.y - 10 + k), (int) (focusPosition.x + 10), (int) (focusPosition.y + 10 + k), screenCable.getCable().getColor().getRGB());
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        scroll += (float) scrollY;
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

        for (PortConfig.ScreenCable screenCable : portConfig.getScreenCables()) {
            if (mouse.distance(screenCable.getFocusPosition()) < 10) {
                focusedCable = screenCable;
                for (PortConfig.Port value : portConfig.getAll().values()) {
                    value.getConnections().remove(screenCable.getCable().getId());
                    screenCable.bind("");
                }
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
                port.getConnections().add(focusedCable.getCable().getId());
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

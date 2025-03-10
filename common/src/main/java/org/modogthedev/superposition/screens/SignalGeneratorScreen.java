package org.modogthedev.superposition.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import foundry.veil.api.network.VeilPacketManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.block.SignalGeneratorBlock;
import org.modogthedev.superposition.blockentity.SignalGeneratorBlockEntity;
import org.modogthedev.superposition.networking.packet.BlockEntityModificationC2SPacket;
import org.modogthedev.superposition.system.sound.ClientAudioManager;

public class SignalGeneratorScreen extends WidgetScreen {
    private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(Superposition.MODID, "textures/screen/signal_generator_background.png");
    private static final ResourceLocation PIXEL = ResourceLocation.fromNamespaceAndPath(Superposition.MODID, "textures/screen/pixel.png");
    private static final ResourceLocation WARN_ON = ResourceLocation.fromNamespaceAndPath(Superposition.MODID, "textures/screen/warn_on.png");
    private static final ResourceLocation WARN_OFF = ResourceLocation.fromNamespaceAndPath(Superposition.MODID, "textures/screen/warn_off.png");
    private static final ResourceLocation SWITCH_ON = ResourceLocation.fromNamespaceAndPath(Superposition.MODID, "textures/screen/switch_on.png");
    private static final ResourceLocation SWITCH_OFF = ResourceLocation.fromNamespaceAndPath(Superposition.MODID, "textures/screen/switch_off.png");
    public static final int imageWidth = 176;
    public static final int imageHeight = 224;

    private final BlockPos pos;
    private int ticks = 0;
    private float frequency;
    private float startFrequency = 1;
    private boolean mute = true;
    private boolean swap = false;
    private VertexConsumer lineConsumer;

    public SignalGeneratorScreen(Component pTitle, BlockPos pos) {
        super(pTitle);
        freeSpin = true;
        this.pos = pos;
        ticks = 0;
        this.addDial(-25, 0);
        this.addDial(25, 0);
        BlockState state = Minecraft.getInstance().level.getBlockState(pos);
        BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(pos);
        if (blockEntity instanceof SignalGeneratorBlockEntity generatorBlockEntity) {
            startFrequency = generatorBlockEntity.getFrequency();
        }
        swap = state.getValue(SignalGeneratorBlock.SWAP_SIDES);
    }

    public void playDownSound(SoundManager pHandler) {
        pHandler.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    public void drawPixel(GuiGraphics pGuiGraphics, int x, int y) {

        pGuiGraphics.blit(PIXEL, x, y, 0, 0, 1, 1);
    }

    public void fill(GuiGraphics graphics, float pMinX, float pMinY, float pMaxX, float pMaxY, int pColor) {
        if (pMinY > pMaxY) {
            float minY = pMinY;
            pMinY = pMaxY;
            pMaxY = minY + 3;
        } else {
            pMaxY += 3;
        }
        // In ryan we trust
        float f3 = (float) FastColor.ARGB32.alpha(pColor) / 255.0F;
        float f = (float) FastColor.ARGB32.red(pColor) / 255.0F;
        float f1 = (float) FastColor.ARGB32.green(pColor) / 255.0F;
        float f2 = (float) FastColor.ARGB32.blue(pColor) / 255.0F;

        Matrix4f matrix4f = graphics.pose().last().pose();

        this.lineConsumer.addVertex(matrix4f, pMinX, pMinY, 0.0f).setColor(f, f1, f2, f3);
        this.lineConsumer.addVertex(matrix4f, pMinX, pMaxY, 0.0f).setColor(f, f1, f2, f3);
        this.lineConsumer.addVertex(matrix4f, pMaxX, pMaxY, 0.0f).setColor(f, f1, f2, f3);
        this.lineConsumer.addVertex(matrix4f, pMaxX, pMinY, 0.0f).setColor(f, f1, f2, f3);
    }

    public void renderSine(GuiGraphics pGuiGraphics) {
        this.lineConsumer = pGuiGraphics.bufferSource().getBuffer(RenderType.gui()); // In ryan we trust
        int startPos = (this.width - 158) / 2;
        int j = (this.height - imageHeight) / 2;
        int width = this.width;
        float resolution = 0.5f;
        for (float i = 0; i < 158; i += resolution) {
            float calculatedPosition = (float) (Math.sin((i + ticks) * (frequency / 80)) * 25);
            float nextCalculatedPosition = (float) (Math.sin(((i + resolution) + ticks) * (frequency / 80)) * 25);
            this.fill(pGuiGraphics, (i + (startPos)), (j + 45 + calculatedPosition), (i + (startPos)) + 1, (j + 45 + nextCalculatedPosition) + 1, 0xFF56d156);
        }
        this.flush(pGuiGraphics);
        if (frequency < .72f || frequency > 150) {
            pGuiGraphics.blit(WARN_ON, width / 2 - 81, height / 2 - 20, 0, 0, 14, 14, 14, 14);
        } else {
            pGuiGraphics.blit(WARN_OFF, width / 2 - 81, height / 2 - 20, 0, 0, 14, 14, 14, 14);
        }
    }

    public void calculateWavelength() {
        frequency = Math.abs((dials.get(0).scrolledAmount / 10) + (dials.get(1).scrolledAmount) + (startFrequency));
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if ((double) width / 2 + 72 > pMouseX - 10 && (double) width / 2 + 72 < pMouseX && (double) height / 2 - 20 > pMouseY - 24 && (double) height / 2 - 20 < pMouseY) {
            this.playSwitchSound(Minecraft.getInstance().getSoundManager(), mute);
            mute = !mute;
        }
        if ((double) width / 2 + 58 > pMouseX - 10 && (double) width / 2 + 60 < pMouseX && (double) height / 2 - 20 > pMouseY - 24 && (double) height / 2 - 20 < pMouseY) {
            this.playSwitchSound(Minecraft.getInstance().getSoundManager(), swap);
            swap = !swap;
            this.updateBlock();
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    private void flush(GuiGraphics graphics) { // In ryan we trust
        RenderSystem.disableDepthTest();
        graphics.bufferSource().endBatch();
        RenderSystem.enableDepthTest();
        this.lineConsumer = null;
    }

    @Override
    public void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.calculateWavelength();
        int i = (this.width - imageWidth) / 2;
        int j = (this.height - imageHeight) / 2;
        pGuiGraphics.blit(BACKGROUND, i, j, 0, 0, imageWidth, imageHeight);
        this.renderSine(pGuiGraphics);
//        frequency = 5;
        if (mute) {
            pGuiGraphics.blit(SWITCH_ON, width / 2 + 72, height / 2 - 20, 0, 0, 10, 24, 10, 24);

        } else {
            pGuiGraphics.blit(SWITCH_OFF, width / 2 + 72, height / 2 - 20, 0, 0, 10, 24, 10, 24);
        }
        if (swap) {
            pGuiGraphics.blit(SWITCH_ON, width / 2 + 58, height / 2 - 20, 0, 0, 10, 24, 10, 24);

        } else {
            pGuiGraphics.blit(SWITCH_OFF, width / 2 + 58, height / 2 - 20, 0, 0, 10, 24, 10, 24);
        }
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public void tick() {
        super.tick();
        if (!mute && frequency > .72f) {
            float pitch = 1.0F / Mth.map(0, 30, 2, .72f, frequency);
            // TODO make it work pls
            ClientAudioManager.playSine(frequency*100);
        }
        ticks++;
    }

    @Override
    public void onClose() {
        super.onClose();
        this.updateBlock();
    }

    @Override
    public void dialUpdated() {
        super.dialUpdated();
        this.updateBlock();
    }

    public void updateBlock() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("frequency", frequency);
        tag.putBoolean("swap", swap);
        VeilPacketManager.server().sendPacket(new BlockEntityModificationC2SPacket(tag, pos));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

}

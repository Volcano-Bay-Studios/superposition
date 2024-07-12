package org.modogthedev.superposition.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.block.SignalGeneratorBlock;
import org.modogthedev.superposition.blockentity.ModulatorBlockEntity;
import org.modogthedev.superposition.core.SuperpositionSounds;
import org.modogthedev.superposition.networking.Messages;
import org.modogthedev.superposition.networking.packet.BlockEntityModificationC2SPacket;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.util.Mth;

public class ModulatorScreen extends DialScreen {
    private static final ResourceLocation BACKGROUND = new ResourceLocation(Superposition.MODID, "textures/screen/modulator_screen.png");
    private static final ResourceLocation SWITCH_ON = new ResourceLocation(Superposition.MODID, "textures/screen/switch_on.png");
    private static final ResourceLocation SWITCH_OFF = new ResourceLocation(Superposition.MODID, "textures/screen/switch_off.png");
    public static final int imageWidth = 176;
    public static final int imageHeight = 224;
    public static BlockPos pos;
    public static int ticks = 0;
    public float frequency = 0;
    public float signalAmplitude = 0;
    public float amplitude;
    public float modRate;
    public boolean mute = true;
    public boolean swap;
    public VertexConsumer lineConsumer;
    float readAmplitude = 0;

    public ModulatorScreen(Component pTitle, BlockPos pos) {
        super(pTitle);
        ModulatorScreen.pos = pos;
        ticks = 0;
        addDial(-72, 0, 76);
        addDial(-50, 0, 76);
        assert Minecraft.getInstance().level != null : "Level was null in Modulator Screen";
        BlockState state = Minecraft.getInstance().level.getBlockState(pos);
        BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(pos);
        if (blockEntity instanceof ModulatorBlockEntity generatorBlockEntity) {
            dials.get(0).scrolledAmount = generatorBlockEntity.redstoneMod;
            dials.get(1).scrolledAmount = generatorBlockEntity.modRate;
        }
        swap = state.getValue(SignalGeneratorBlock.SWAP_SIDES);
    }

    public void playDownSound(SoundManager pHandler) {
        pHandler.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    public void renderSine(GuiGraphics pGuiGraphics) {
        this.lineConsumer = pGuiGraphics.bufferSource().getBuffer(RenderType.gui()); // In ryan we trust
        int startPos = (this.width - 70) / 2;
        int j = (this.height - imageHeight) / 2;
        float resolution = 0.5f;
        for (float i = 0; i < 61; i += resolution) {
            float calculatedPosition = (float) (Math.sin((i + ticks) / frequency) * (5 + ((readAmplitude) / 5) + signalAmplitude));
            float nextCalculatedPosition = (float) (Math.sin(((i+resolution) + ticks) / frequency) * (5 + ((readAmplitude) / 5) + signalAmplitude));
            calculatedPosition = net.minecraft.util.Mth.clamp(calculatedPosition,-35,42);
            nextCalculatedPosition = net.minecraft.util.Mth.clamp(nextCalculatedPosition,-35,42);
            fill(pGuiGraphics, (i + (startPos)), (j + 45 + calculatedPosition), (i + (startPos)) + resolution, (j + 45 + nextCalculatedPosition), 0xFF56d156);
        }
    }

    public void renderSine2(GuiGraphics pGuiGraphics) {
        this.lineConsumer = pGuiGraphics.bufferSource().getBuffer(RenderType.gui()); // In ryan we trust
        int startPos = (this.width + 68) / 2;
        int j = (this.height - imageHeight) / 2;
        float resolution = 0.5f;
        for (float i = 0; i < 45; i += resolution) {
            float calculatedPosition = (float) (Math.sin((i + ticks) / frequency) * (5 + ((readAmplitude-amplitude) / 5) + signalAmplitude));
            float nextCalculatedPosition = (float) (Math.sin(((i+resolution) + ticks) / frequency) * (5 + ((readAmplitude-amplitude) / 5) + signalAmplitude));
            calculatedPosition = net.minecraft.util.Mth.clamp(calculatedPosition,-35,42);
            nextCalculatedPosition = net.minecraft.util.Mth.clamp(nextCalculatedPosition,-35,42);
            fill(pGuiGraphics, (i + (startPos)), (j + 45 + calculatedPosition), (i + (startPos)) + resolution, (j + 45 + nextCalculatedPosition), 0xFF56d156);
        }
    }

    public void renderBars(GuiGraphics guiGraphics) {
        this.lineConsumer = guiGraphics.bufferSource().getBuffer(RenderType.gui()); // In ryan we trust
        int width = this.width; // Redundant call?
        int barHeight = Math.min(76, Math.abs((int) dials.get(0).scrolledAmount));
        int barHeight2 = Math.min(76, Math.abs((int) dials.get(1).scrolledAmount));
        fill(guiGraphics, width / 2f - 79, height / 2f - 25 - barHeight, width / 2f - 65, height / 2f - 25, 0xFF56d156);
        fill(guiGraphics, width / 2f - 57, height / 2f - 25 - barHeight2, width / 2f - 43, height / 2f - 25, 0xFF56d156);
        modRate = barHeight;
        assert Minecraft.getInstance().level != null : "Tried accessing screen from server";
        amplitude = barHeight2 + (ModulatorBlockEntity.getRedstoneOffset(Minecraft.getInstance().level, pos) * ((float) barHeight / 15));
        flush(guiGraphics);
    }

    public void fill(GuiGraphics graphics, float pMinX, float pMinY, float pMaxX, float pMaxY, int pColor) {
        if (pMinY > pMaxY) {
            float minY = pMinY;
            pMinY = pMaxY;
            pMaxY = minY+3;
        } else {
            pMaxY += 3;
        }
            // In ryan we trust
        float f3 = (float) FastColor.ARGB32.alpha(pColor) / 255.0F;
        float f = (float) FastColor.ARGB32.red(pColor) / 255.0F;
        float f1 = (float) FastColor.ARGB32.green(pColor) / 255.0F;
        float f2 = (float) FastColor.ARGB32.blue(pColor) / 255.0F;

        Matrix4f matrix4f = graphics.pose().last().pose();

        this.lineConsumer.vertex(matrix4f, pMinX, pMinY, 0.0f).color(f, f1, f2, f3).endVertex();
        this.lineConsumer.vertex(matrix4f, pMinX, pMaxY, 0.0f).color(f, f1, f2, f3).endVertex();
        this.lineConsumer.vertex(matrix4f, pMaxX, pMaxY, 0.0f).color(f, f1, f2, f3).endVertex();
        this.lineConsumer.vertex(matrix4f, pMaxX, pMinY, 0.0f).color(f, f1, f2, f3).endVertex();
    }


    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if ((double) width / 2 + 72 > pMouseX - 10 && (double) width / 2 + 72 < pMouseX && (double) height / 2 + 12 > pMouseY - 24 && (double) height / 2 + 12 < pMouseY) {
            this.playDownSound(Minecraft.getInstance().getSoundManager());
            mute = !mute;
        }
        if ((double) width / 2 + 58 > pMouseX - 10 && (double) width / 2 + 60 < pMouseX && (double) height / 2 + 12 > pMouseY - 24 && (double) height / 2 + 12 < pMouseY) {
            this.playDownSound(Minecraft.getInstance().getSoundManager());
            swap = !swap;
            updateBlock();
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        return super.mouseScrolled(pMouseX, pMouseY, pDelta);
    }

    private void flush(GuiGraphics graphics) { // In ryan we trust
        RenderSystem.disableDepthTest();
        graphics.bufferSource().endBatch();
        RenderSystem.enableDepthTest();
        this.lineConsumer = null;
    }

    @Override
    public void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        int i = (this.width - imageWidth) / 2;
        int j = (this.height - imageHeight) / 2;
        pGuiGraphics.blit(BACKGROUND, i, j, 0, 0, imageWidth, imageHeight);
//        frequency = 5;
        renderSine(pGuiGraphics);
        renderSine2(pGuiGraphics);
        renderBars(pGuiGraphics);

        if (mute) {
            pGuiGraphics.blit(SWITCH_ON, width / 2 + 72, height / 2 + 12, 0, 0, 10, 24, 10, 24);

        } else {
            pGuiGraphics.blit(SWITCH_OFF, width / 2 + 72, height / 2 + 12, 0, 0, 10, 24, 10, 24);
        }
        if (swap) {
            pGuiGraphics.blit(SWITCH_ON, width / 2 + 58, height / 2 + 12, 0, 0, 10, 24, 10, 24);

        } else {
            pGuiGraphics.blit(SWITCH_OFF, width / 2 + 58, height / 2 + 12, 0, 0, 10, 24, 10, 24);
        }
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public void tick() {
        super.tick();
        if (!mute && frequency > .72f) {
            float pitch = Mth.getFromRange(0, 30, 2, .72f, frequency);
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SuperpositionSounds.SINE.get(), pitch));
        }
        ticks++;
        assert Minecraft.getInstance().level != null : "Tried to access screen from server!";

        BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(pos);
        if (blockEntity instanceof ModulatorBlockEntity signalActorBlockEntity) {
            Signal blockSignal = signalActorBlockEntity.getSignal();
            if (blockSignal != null) {
                this.frequency = blockSignal.frequency; //TODO Explode if signal to high
                this.readAmplitude = signalActorBlockEntity.lastAmplitude;
//                this.signalAmplitude = blockSignal.amplitude;
            } else {
                frequency = 0;
                readAmplitude = 0;
            }
        } else {
            this.frequency = 0;
            readAmplitude = 0;
        }
    }

    @Override
    public void onClose() {
        updateBlock();
        super.onClose();
    }

    @Override
    public void dialUpdated() {
        super.dialUpdated();
        updateBlock();
    }

    public void updateBlock() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("modRate", Math.min(76, Math.abs((int) dials.get(1).scrolledAmount)));
        tag.putFloat("redstoneMod", Math.min(76, Math.abs((int) dials.get(0).scrolledAmount)));
        tag.putBoolean("swap", swap);
        Messages.sendToServer(new BlockEntityModificationC2SPacket(tag, pos));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}

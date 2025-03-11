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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.block.SignalGeneratorBlock;
import org.modogthedev.superposition.blockentity.AmplifierBlockEntity;
import org.modogthedev.superposition.networking.packet.BlockEntityModificationC2SPacket;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.util.SuperpositionMth;

public class AmplifierScreen extends WidgetScreen {
    private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(Superposition.MODID, "textures/screen/amplifier_screen.png");
    private static final ResourceLocation SWITCH_ON = ResourceLocation.fromNamespaceAndPath(Superposition.MODID, "textures/screen/switch_on.png");
    private static final ResourceLocation SWITCH_OFF = ResourceLocation.fromNamespaceAndPath(Superposition.MODID, "textures/screen/switch_off.png");
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

    public AmplifierScreen(Component pTitle, BlockPos pos) {
        super(pTitle);
        AmplifierScreen.pos = pos;
        ticks = 0;
        addDial(-72, 0, 76);
        addDial(-50, 0, 76);
        assert Minecraft.getInstance().level != null : "Level was null in Amplifier Screen";
        BlockState state = Minecraft.getInstance().level.getBlockState(pos);
        BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(pos);
        if (blockEntity instanceof AmplifierBlockEntity generatorBlockEntity) {
            dials.get(0).scrolledAmount = generatorBlockEntity.redstoneAmplification;
            dials.get(1).scrolledAmount = generatorBlockEntity.amplification;
        }
        swap = state.getValue(SignalGeneratorBlock.SWAP_SIDES);
    }

    public void playDownSound(SoundManager pHandler) {
        pHandler.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    public void renderSine(GuiGraphics pGuiGraphics) {
        int startPos = (this.width - 70) / 2;
        int j = (this.height - imageHeight) / 2;
        float resolution = 0.5f;
        for (float i = 0; i < 61; i += resolution) {
            float calculatedPosition = (float) (Math.sin((i + ticks) * (frequency / 80))) * (5 + ((readAmplitude) / 5) + signalAmplitude);
            float nextCalculatedPosition = (float) (Math.sin(((i + resolution) + ticks) * (frequency / 80))) * (5 + ((readAmplitude) / 5) + signalAmplitude);
            calculatedPosition = net.minecraft.util.Mth.clamp(calculatedPosition, -35, 42);
            nextCalculatedPosition = net.minecraft.util.Mth.clamp(nextCalculatedPosition, -35, 42);
            this.fill(pGuiGraphics, (i + (startPos)), (j + 45 + calculatedPosition), (i + (startPos)) + 1, (j + 45 + nextCalculatedPosition) + 1, 0xFF56d156);
        }
    }

    public void renderSine2(GuiGraphics pGuiGraphics) {
        int startPos = (this.width + 68) / 2;
        int j = (this.height - imageHeight) / 2;
        float resolution = 0.5f;
        for (float i = 0; i < 45; i += resolution) {
            float calculatedPosition = (float) (Math.sin((i + ticks) * (frequency / 80))) * (5 + ((readAmplitude - amplitude) / 5) + signalAmplitude);
            float nextCalculatedPosition = (float) (Math.sin(((i + resolution) + ticks) * (frequency / 80))) * (5 + ((readAmplitude - amplitude) / 5) + signalAmplitude);
            calculatedPosition = net.minecraft.util.Mth.clamp(calculatedPosition, -35, 42);
            nextCalculatedPosition = net.minecraft.util.Mth.clamp(nextCalculatedPosition, -35, 42);
            this.fill(pGuiGraphics, (i + (startPos)), (j + 45 + calculatedPosition), (i + (startPos)) + 1, (j + 45 + nextCalculatedPosition) + 1, 0xFF56d156);
        }
    }

    public void renderBars(GuiGraphics guiGraphics) {
        int width = this.width; // Redundant call?
        int barHeight = Math.min(76, Math.abs((int) dials.get(0).scrolledAmount));
        int barHeight2 = Math.min(76, Math.abs((int) dials.get(1).scrolledAmount));
        fillExact(guiGraphics, width / 2f - 79, height / 2f - 25 - barHeight, width / 2f - 65, height / 2f - 25, 0xFF56d156);
        fillExact(guiGraphics, width / 2f - 57, height / 2f - 25 - barHeight2, width / 2f - 43, height / 2f - 25, 0xFF56d156);
        modRate = barHeight;
        assert Minecraft.getInstance().level != null : "Tried accessing screen from server";
        amplitude = barHeight2 + (AmplifierBlockEntity.getRedstoneOffset(Minecraft.getInstance().level, pos) * ((float) barHeight / 15));
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

    public void fillExact(GuiGraphics graphics, float pMinX, float pMinY, float pMaxX, float pMaxY, int pColor) {
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


    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if ((double) width / 2 + 72 > pMouseX - 10 && (double) width / 2 + 72 < pMouseX && (double) height / 2 + 12 > pMouseY - 24 && (double) height / 2 + 12 < pMouseY) {
            this.playSwitchSound(Minecraft.getInstance().getSoundManager(), mute);
            mute = !mute;
        }
        if ((double) width / 2 + 58 > pMouseX - 10 && (double) width / 2 + 60 < pMouseX && (double) height / 2 + 12 > pMouseY - 24 && (double) height / 2 + 12 < pMouseY) {
            this.playSwitchSound(Minecraft.getInstance().getSoundManager(), swap);
            swap = !swap;
            updateBlock();
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    private void flush(GuiGraphics guiGraphics) { // In ryan we trust
        RenderSystem.disableDepthTest();
        guiGraphics.bufferSource().endBatch();
        RenderSystem.enableDepthTest();
        this.lineConsumer = null;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        int i = (this.width - imageWidth) / 2;
        int j = (this.height - imageHeight) / 2;
        this.lineConsumer = guiGraphics.bufferSource().getBuffer(RenderType.gui()); // In ryan we trust
        guiGraphics.blit(BACKGROUND, i, j, 0, 0, imageWidth, imageHeight);
//        frequency = 5;
        renderSine(guiGraphics);
        renderSine2(guiGraphics);
        renderBars(guiGraphics);

        if (mute) {
            guiGraphics.blit(SWITCH_ON, width / 2 + 72, height / 2 + 12, 0, 0, 10, 24, 10, 24);

        } else {
            guiGraphics.blit(SWITCH_OFF, width / 2 + 72, height / 2 + 12, 0, 0, 10, 24, 10, 24);
        }
        if (swap) {
            guiGraphics.blit(SWITCH_ON, width / 2 + 58, height / 2 + 12, 0, 0, 10, 24, 10, 24);

        } else {
            guiGraphics.blit(SWITCH_OFF, width / 2 + 58, height / 2 + 12, 0, 0, 10, 24, 10, 24);
        }
        super.render(guiGraphics, pMouseX, pMouseY, pPartialTick);
        flush(guiGraphics);
    }

    @Override
    public void tick() {
        super.tick();
        if (!mute && frequency > .72f) {
            float pitch = SuperpositionMth.getFromRange(30, 0, 2, .72f, frequency);
//            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SuperpositionSounds.SINE.get(), pitch)); TODO: use new thing
        }
        ticks++;
        assert Minecraft.getInstance().level != null : "Tried to access screen from server!";

        BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(pos);
        if (blockEntity instanceof AmplifierBlockEntity signalActorBlockEntity) {
            Signal blockSignal = signalActorBlockEntity.getSignal();
            if (blockSignal != null) {
                this.frequency = blockSignal.getSourceFrequency(); //TODO Explode if signal to high
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
        tag.putFloat("amplification", Math.min(76, Math.abs((int) dials.get(1).scrolledAmount)));
        tag.putFloat("redstoneAmplification", Math.min(76, Math.abs((int) dials.get(0).scrolledAmount)));
        tag.putBoolean("swap", swap);
        VeilPacketManager.server().sendPacket(new BlockEntityModificationC2SPacket(tag, pos));
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}

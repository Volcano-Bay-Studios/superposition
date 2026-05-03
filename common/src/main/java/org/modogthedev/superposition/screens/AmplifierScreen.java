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
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.block.SignalGeneratorBlock;
import org.modogthedev.superposition.blockentity.AmplifierBlockEntity;
import org.modogthedev.superposition.networking.packet.BlockEntityModificationC2SPacket;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.util.SignalHelper;
import org.modogthedev.superposition.util.SuperpositionMth;

import java.awt.*;

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
        addDial(-67, -3, 68);
        assert Minecraft.getInstance().level != null : "Level was null in AmplifierScreen";
        BlockState state = Minecraft.getInstance().level.getBlockState(pos);
        BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(pos);
        if (blockEntity instanceof AmplifierBlockEntity generatorBlockEntity) {
            dials.getFirst().scrolledAmount = generatorBlockEntity.amplification;
        }
        swap = state.getValue(SignalGeneratorBlock.SWAP_SIDES);
    }

    public void playDownSound(SoundManager pHandler) {
        pHandler.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    public void renderSine(GuiGraphics pGuiGraphics) {
        int startPos = (this.width - 115) / 2;
        int j = (this.height - imageHeight) / 2;
        float resolution = 0.5f;
        for (float i = 0; i < 131; i += resolution) {
            float calculatedPosition = (float) (Math.sin((i + ticks) * (frequency / 80))) * (signalAmplitude / 5);
            float nextCalculatedPosition = (float) (Math.sin(((i + resolution) + ticks) * (frequency / 80))) * (signalAmplitude / 5);
            Color color = new Color(0.12f, 0.9f, 0.25f, (float) Math.min(1,Math.pow(60/(Math.abs(65-i)),5)));
            calculatedPosition = net.minecraft.util.Mth.clamp(calculatedPosition, -35, 42);
            nextCalculatedPosition = net.minecraft.util.Mth.clamp(nextCalculatedPosition, -35, 42);
            this.fill(pGuiGraphics, (i + (startPos)), (j + 52 + calculatedPosition), (i + (startPos)) + 1, (j + 52 + nextCalculatedPosition) + 1, color.getRGB());
        }
    }

    public void renderBars(GuiGraphics guiGraphics) {
        int barHeight2 = Math.min(68, Math.abs((int) dials.get(0).scrolledAmount));
        Color color = new Color(0.12f, 0.9f, 0.25f, 1f);
        fillExact(guiGraphics, width / 2f - 79, height / 2f - 24 - barHeight2, width / 2f - 67, height / 2f - 24, color.getRGB());
    }

    public void fill(GuiGraphics graphics, float pMinX, float pMinY, float pMaxX, float pMaxY, int pColor) {
        if (pMinY > pMaxY) {
            float minY = pMinY;
            pMinY = pMaxY;
            pMaxY = minY + 3;
        } else {
            pMaxY += 3;
        }
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
        renderBars(guiGraphics);

        if (mute) {
            guiGraphics.blit(SWITCH_ON, width / 2 + 62, height / 2 - 15, 0, 0, 10, 24, 10, 24);

        } else {
            guiGraphics.blit(SWITCH_OFF, width / 2 + 62, height / 2 - 15, 0, 0, 10, 24, 10, 24);
        }
        if (swap) {
            guiGraphics.blit(SWITCH_ON, width / 2 + 48, height / 2 - 15, 0, 0, 10, 24, 10, 24);

        } else {
            guiGraphics.blit(SWITCH_OFF, width / 2 + 48, height / 2 - 15, 0, 0, 10, 24, 10, 24);
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
        assert Minecraft.getInstance().level != null : "Tried to access screen with no level!";

        BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(pos);
        if (blockEntity instanceof AmplifierBlockEntity signalActorBlockEntity) {
            Signal blockSignal = SignalHelper.randomSignal(signalActorBlockEntity.getOutputSignals());
            if (blockSignal != null) {
                this.frequency = blockSignal.getSourceFrequency(); //TODO Explode if signal to high
                this.signalAmplitude = blockSignal.getAmplitude();
            } else {
                frequency = 0;
            }
        } else {
            this.frequency = 0;
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
        tag.putFloat("amplification", Math.min(76, Math.abs((int) dials.get(0).scrolledAmount)));
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

    @Override
    public Vec2 getTooltipPosition(int w, int h) {
        return new Vec2(w / 2f, (h / 10f) * 7f);
    }

    @Override
    public @Nullable BlockPos getBlockPos() {
        return pos;
    }
}

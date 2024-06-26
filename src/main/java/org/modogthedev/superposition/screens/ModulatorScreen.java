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
import org.joml.Matrix4f;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.block.ModulatorBlock;
import org.modogthedev.superposition.block.SignalGeneratorBlock;
import org.modogthedev.superposition.blockentity.ModulatorBlockEntity;
import org.modogthedev.superposition.core.SuperpositionSounds;
import org.modogthedev.superposition.networking.Messages;
import org.modogthedev.superposition.networking.packet.BlockEntityModificationC2SPacket;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.util.Mth;
import org.modogthedev.superposition.util.SignalActorBlockEntity;

public class ModulatorScreen extends DialScreen {
    private static final ResourceLocation BACKGROUND = new ResourceLocation(Superposition.MODID, "textures/screen/modulator_screen.png");
    private static final ResourceLocation PIXEL = new ResourceLocation(Superposition.MODID, "textures/screen/pixel.png");
    private static final ResourceLocation WARN_ON = new ResourceLocation(Superposition.MODID, "textures/screen/warn_on.png");
    private static final ResourceLocation WARN_OFF = new ResourceLocation(Superposition.MODID, "textures/screen/warn_off.png");
    private static final ResourceLocation SWITCH_ON = new ResourceLocation(Superposition.MODID, "textures/screen/switch_on.png");
    private static final ResourceLocation SWITCH_OFF = new ResourceLocation(Superposition.MODID, "textures/screen/switch_off.png");
    public static final int imageWidth = 176;
    public static final int imageHeight = 224;
    public static BlockPos pos;
    public static int ticks = 0;
    public float frequency = 10;
    public float signalAmplitude = 0;
    public float amplitude;
    public float modRate;
    public boolean mute = true;
    public boolean swap = false;
    public VertexConsumer lineConsumer;
    float readAmplitude = 0;

    public ModulatorScreen(Component pTitle, BlockPos pos) {
        super(pTitle);
        ModulatorScreen.pos = pos;
        ticks = 0;
        addDial(-72, 0, 76);
        addDial(-50, 0,76);
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

    public void drawPixel(GuiGraphics pGuiGraphics, int x, int y) {
        fill(pGuiGraphics,x,y,x+1,y+1,0xFF56d156);
    }
    public void renderSine(GuiGraphics pGuiGraphics) {
        this.lineConsumer = pGuiGraphics.bufferSource().getBuffer(RenderType.gui()); // In ryan we trust
        int startPos = (this.width - 70) / 2;
        int j = (this.height - imageHeight) / 2;
        int width = this.width;
        for (float i = 0; i < 61; i += .05f) {
            int calculatedPosition = (int) (Math.sin((double) (i + ticks) / frequency) * (5+((readAmplitude)/5)+signalAmplitude));
            if (calculatedPosition>-35 && calculatedPosition < 42)
                fill(pGuiGraphics, (int) (i + (startPos)), (j + 45 + calculatedPosition), (int) (i + (startPos)) + 1, (j + 45 + calculatedPosition) + 1, 0xFF56d156);
        }
    }
    public void renderSine2(GuiGraphics pGuiGraphics) {
        this.lineConsumer = pGuiGraphics.bufferSource().getBuffer(RenderType.gui()); // In ryan we trust
        int startPos = (this.width + 68) / 2;
        int j = (this.height - imageHeight) / 2;
        int width = this.width;
        for (float i = 0; i < 45; i += .05f) {
            int calculatedPosition = (int) (Math.sin((double) (i + ticks+20) / frequency) * (5+((readAmplitude)/5)+signalAmplitude));
            if (calculatedPosition>-35 && calculatedPosition < 42)
                fill(pGuiGraphics, (int) (i + (startPos)), (j + 45 + calculatedPosition), (int) (i + (startPos)) + 1, (j + 45 + calculatedPosition) + 1, 0xFF56d156);
        }
    }
    public void renderBars(GuiGraphics guiGraphics) {
        this.lineConsumer = guiGraphics.bufferSource().getBuffer(RenderType.gui()); // In ryan we trust
        int width = this.width; // Redundant call?
        int barHeight = Math.min(76,Math.abs((int) dials.get(0).scrolledAmount));
        int barHeight2 = Math.min(76,Math.abs((int) dials.get(1).scrolledAmount));
        fill(guiGraphics,width/2-79,height/2-25-barHeight,width/2-65,height/2-25,0xFF56d156);
        fill(guiGraphics,width/2-57,height/2-25-barHeight2,width/2-43,height/2-25,0xFF56d156);
        modRate = barHeight;
        assert Minecraft.getInstance().level != null : "Tried accessing screen from server";
        amplitude = barHeight2+(ModulatorBlockEntity.getRedstoneOffset(Minecraft.getInstance().level, pos)*((float) barHeight /15));
        flush(guiGraphics);
    }

    public void fill(GuiGraphics graphics, int pMinX, int pMinY, int pMaxX, int pMaxY, int pColor) { // In ryan we trust
        float f3 = (float) FastColor.ARGB32.alpha(pColor) / 255.0F;
        float f = (float) FastColor.ARGB32.red(pColor) / 255.0F;
        float f1 = (float) FastColor.ARGB32.green(pColor) / 255.0F;
        float f2 = (float) FastColor.ARGB32.blue(pColor) / 255.0F;

        Matrix4f matrix4f = graphics.pose().last().pose();

        this.lineConsumer.vertex(matrix4f, (float) pMinX, (float) pMinY, 0.0f).color(f, f1, f2, f3).endVertex();
        this.lineConsumer.vertex(matrix4f, (float) pMinX, (float) pMaxY, 0.0f).color(f, f1, f2, f3).endVertex();
        this.lineConsumer.vertex(matrix4f, (float) pMaxX, (float) pMaxY, 0.0f).color(f, f1, f2, f3).endVertex();
        this.lineConsumer.vertex(matrix4f, (float) pMaxX, (float) pMinY, 0.0f).color(f, f1, f2, f3).endVertex();
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
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
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
            float pitch = Mth.getFromRange(0,30,2,.72f,frequency);
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SuperpositionSounds.SINE.get(),pitch));
        }
        ticks++;
        BlockPos sidedPos;
        assert Minecraft.getInstance().level != null : "Tried to access screen from server!";
        if (!swap) {
            sidedPos = pos.relative(Minecraft.getInstance().level.getBlockState(pos).getValue(ModulatorBlock.FACING).getClockWise(), 1);
        } else {
            sidedPos = pos.relative(Minecraft.getInstance().level.getBlockState(pos).getValue(ModulatorBlock.FACING).getCounterClockWise(),1);
        }
        BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(pos);
        if (blockEntity instanceof SignalActorBlockEntity signalActorBlockEntity) {
            Signal blockSignal = signalActorBlockEntity.getSignal(new Object(), false);
            if (blockSignal != null) {
                this.frequency = blockSignal.frequency; //TODO Explode if signal to high
                this.readAmplitude = blockSignal.amplitude;
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
        tag.putFloat("modRate",Math.min(76,Math.abs((int) dials.get(1).scrolledAmount)));
        tag.putFloat("redstoneMod",Math.min(76,Math.abs((int) dials.get(0).scrolledAmount)));
        tag.putBoolean("swap",swap);
        Messages.sendToServer(new BlockEntityModificationC2SPacket(tag,pos));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}

package org.modogthedev.superposition.util;

import foundry.veil.api.client.color.Color;
import foundry.veil.api.client.color.ColorTheme;
import foundry.veil.api.client.tooltip.Tooltippable;
import foundry.veil.api.client.tooltip.VeilUIItemTooltipDataHolder;
import foundry.veil.api.client.tooltip.anim.TooltipTimeline;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.system.antenna.Antenna;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.system.signal.SignalManager;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SignalActorBlockEntity extends SyncedBlockEntity implements TickableBlockEntity, SPTooltipable {
    @Unique
    private List<Component> veil$tooltip = new ArrayList();
    @Unique
    private ColorTheme veil$theme = Superposition.SUPERPOSITION_THEME;
    @Unique
    private List<VeilUIItemTooltipDataHolder> veil$tooltipDataHolder = new ArrayList();
    @Unique
    private TooltipTimeline veil$timeline = null;
    @Unique
    private boolean veil$worldspace = true;
    @Unique
    private boolean veil$tooltipEnabled = false;
    @Unique
    private int veil$tooltipY = 0;

    public List<Component> getTooltip() {
        return this.veil$tooltip;
    }

    public boolean isTooltipEnabled() {
        return this.veil$tooltipEnabled;
    }

    @Override
    public boolean isSuperpositionTooltipEnabled() {
        return true;
    }

    public CompoundTag saveTooltipData() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("tooltipEnabled", this.veil$tooltipEnabled);
        tag.putInt("tooltipY", this.veil$tooltipY);
        tag.putBoolean("worldspace", this.veil$worldspace);
        if (this.veil$theme != null) {
            CompoundTag themeTag = new CompoundTag();
            Iterator var3 = this.veil$theme.getColorsMap().entrySet().iterator();

            while(var3.hasNext()) {
                Map.Entry<String, Color> entry = (Map.Entry)var3.next();
                String key = entry.getKey() != null ? (String)entry.getKey() : "";
                themeTag.putInt(key, ((Color)entry.getValue()).getRGB());
            }

            tag.put("theme", themeTag);
        }

        return tag;
    }

    public void loadTooltipData(CompoundTag tag) {
        this.veil$tooltipEnabled = tag.getBoolean("tooltipEnabled");
        this.veil$tooltipY = tag.getInt("tooltipY");
        this.veil$worldspace = tag.getBoolean("worldspace");
        if (this.veil$theme != null) {
            this.veil$theme.clear();
        }

        if (tag.contains("theme", 10)) {
            if (this.veil$theme == null) {
                this.veil$theme = new ColorTheme();
            }

            CompoundTag themeTag = tag.getCompound("theme");
            Iterator var3 = themeTag.getAllKeys().iterator();

            while(var3.hasNext()) {
                String key = (String)var3.next();
                this.veil$theme.addColor(key, Color.of(themeTag.getInt(key)));
            }
        }

    }

    public void setTooltip(List<Component> tooltip) {
        this.veil$tooltip = tooltip;
    }
    public void setTooltipEnabled(boolean enabled) { this.veil$tooltipEnabled = enabled;}

    public void addTooltip(Component tooltip) {
        this.veil$tooltip.add(tooltip);
    }

    public void addTooltip(List<Component> tooltip) {
        this.veil$tooltip.addAll(tooltip);
    }

    public void addTooltip(String tooltip) {
        this.veil$tooltip.add(Component.nullToEmpty(tooltip));
    }

    public ColorTheme getTheme() {
        return this.veil$theme;
    }

    public void setTheme(ColorTheme theme) {
        this.veil$theme = theme;
    }

    public void setBackgroundColor(int color) {
        this.veil$theme.addColor("background", Color.of(color));
    }

    public void setTopBorderColor(int color) {
        this.veil$theme.addColor("topBorder", Color.of(color));
    }

    public void setBottomBorderColor(int color) {
        this.veil$theme.addColor("bottomBorder", Color.of(color));
    }

    @Override
    public void drawExtra() {

    }

    public boolean getWorldspace() {
        return this.veil$worldspace;
    }

    public TooltipTimeline getTimeline() {
        return this.veil$timeline;
    }

    public ItemStack getStack() {
        return ItemStack.EMPTY;
    }

    public int getTooltipWidth() {
        return 0;
    }

    public int getTooltipHeight() {
        return 0;
    }

    public int getTooltipXOffset() {
        return -5;
    }

    public int getTooltipYOffset() {
        return 5;
    }

    public List<VeilUIItemTooltipDataHolder> getItems() {
        return this.veil$tooltipDataHolder;
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("tooltipData", this.saveTooltipData());
    }
    @Override
    public void load(CompoundTag pTag) {
        this.loadTooltipData(pTag.getCompound("tooltipData"));
    }
    public Antenna antenna;
    Object lastCall;
    Object lastCallList;
    List<Signal> putSignals = new ArrayList<>();
    public SignalActorBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }
    public BlockPos getDataPos() {
        return getSwappedPos();
    }
    public BlockPos getSwappedPos() {
        BlockPos sidedPos2 = new BlockPos(0, 0, 0);
        if (!this.getBlockState().getValue(SignalActorTickingBlock.SWAP_SIDES)) {
            sidedPos2 = getBlockPos().relative(level.getBlockState(getBlockPos()).getValue(SignalActorTickingBlock.FACING).getClockWise(), 1);
        } else {
            sidedPos2 = getBlockPos().relative(level.getBlockState(getBlockPos()).getValue(SignalActorTickingBlock.FACING).getCounterClockWise(), 1);
        }
        return sidedPos2;
    }
    public Direction getSwappedSide() {
        if (!this.getBlockState().getValue(SignalActorTickingBlock.SWAP_SIDES)) {
            return  level.getBlockState(getBlockPos()).getValue(SignalActorTickingBlock.FACING).getClockWise();
        } else {
            return  level.getBlockState(getBlockPos()).getValue(SignalActorTickingBlock.FACING).getCounterClockWise();
        }
    }
    public Direction getInvertedSwappedSide() {
        if (this.getBlockState().getValue(SignalActorTickingBlock.SWAP_SIDES)) {
            return  level.getBlockState(getBlockPos()).getValue(SignalActorTickingBlock.FACING).getClockWise();
        } else {
            return  level.getBlockState(getBlockPos()).getValue(SignalActorTickingBlock.FACING).getCounterClockWise();
        }
    }

    public BlockPos getInvertedSwappedPos() {
        BlockPos sidedPos2 = new BlockPos(0, 0, 0);
        if (this.getBlockState().getValue(SignalActorTickingBlock.SWAP_SIDES)) {
            sidedPos2 = getBlockPos().relative(level.getBlockState(getBlockPos()).getValue(SignalActorTickingBlock.FACING).getClockWise(), 1);
        } else {
            sidedPos2 = getBlockPos().relative(level.getBlockState(getBlockPos()).getValue(SignalActorTickingBlock.FACING).getCounterClockWise(), 1);
        }
        return sidedPos2;
    }
    public void postSignal(Signal signal) {

    }
    public Signal getSignal(Object nextCall, boolean selfModulate) {
        BlockPos sidedPos = getSwappedPos();
        BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(sidedPos);
        if (blockEntity instanceof SignalActorBlockEntity signalActorBlockEntity && (lastCall == null || !lastCall.equals(nextCall))) {
            Signal refSignal;
            List<Signal> signals = getSignalList(nextCall);
            if (signals != null && !signals.isEmpty())
                refSignal = SignalManager.randomSignal(signals);
            else
                refSignal = signalActorBlockEntity.getSignal(nextCall, true);
            lastCall = nextCall;
            if (selfModulate)
                refSignal = signalActorBlockEntity.modulateSignal(refSignal,false);
            return refSignal;
        } else {
            return null;
        }
    }
    public List<Signal> getSignalList(Object nextCall) {
        if (!(lastCallList == null || !lastCallList.equals(nextCall))) {
            return null;
        }
        BlockPos sidedPos = getSwappedPos();
        BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(sidedPos);
        if (blockEntity instanceof SignalActorBlockEntity signalActorBlockEntity) {
            lastCallList = nextCall;
            if (signalActorBlockEntity instanceof AntennaActorBlockEntity antennaActorBlockEntity && antennaActorBlockEntity.antenna == null)
                antennaActorBlockEntity.updateAntenna();
            if (signalActorBlockEntity.antenna != null && signalActorBlockEntity.antenna.signals != null)
                return signalActorBlockEntity.modulateSignals(signalActorBlockEntity.antenna.signals);
            else
                return signalActorBlockEntity.modulateSignals(signalActorBlockEntity.getSignalList(nextCall));
        }
        return null;
    }
    public void putSignalList(Object nextCall, List<Signal> list) {
        if (!(lastCallList == null || !lastCallList.equals(nextCall))) {
            return;
        }
        BlockPos sidedPos = getInvertedSwappedPos();
        BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(sidedPos);
        if (blockEntity instanceof SignalActorBlockEntity signalActorBlockEntity) {
            list = signalActorBlockEntity.modulateSignals(list);
            lastCallList = nextCall;
            putSignals = list;
            signalActorBlockEntity.putSignalList(nextCall, list);
        }
    }
    public List<Signal> modulateSignals(List<Signal> signalList) {
        for (Signal signal: signalList) {
            signal = this.modulateSignal(signal,true);
        }
        return signalList;
    }
    public Signal modulateSignal(Signal signal, boolean updateTooltip) {
        return signal;
    }
    public Signal createSignal(Object nextCall) {
        BlockPos sidedPos = getSwappedPos();
        BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(sidedPos);
        if (blockEntity instanceof SignalActorBlockEntity signalActorBlockEntity && (lastCall == null || !lastCall.equals(nextCall))) {
            lastCall = nextCall;
            return this.modulateSignal(signalActorBlockEntity.createSignal(nextCall),false);
        } else {
            return null;
        }
    }
    public SignalActorBlockEntity topBE(Object nextCall) {
        BlockPos sidedPos = getSwappedPos();
        BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(sidedPos);
        if (blockEntity instanceof SignalActorBlockEntity signalActorBlockEntity && (lastCall == null || !lastCall.equals(nextCall))) {
            lastCall = nextCall;
            return signalActorBlockEntity.topBE(nextCall);
        } else {
            return null;
        }
    }

    @Override
    public void tick() {
    }
}

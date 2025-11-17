package org.modogthedev.superposition.blockentity;

import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.light.data.AreaLightData;
import foundry.veil.api.client.render.light.data.LightData;
import foundry.veil.api.client.render.light.data.PointLightData;
import foundry.veil.api.client.render.light.renderer.LightRenderHandle;
import foundry.veil.api.network.VeilPacketManager;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.modogthedev.superposition.block.SignalGeneratorBlock;
import org.modogthedev.superposition.core.SuperpositionSounds;
import org.modogthedev.superposition.item.ScrewdriverItem;
import org.modogthedev.superposition.networking.packet.BlockEntityModificationC2SPacket;
import org.modogthedev.superposition.networking.packet.BlockSignalSyncS2CPacket;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.util.*;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class SignalActorBlockEntity extends SyncedBlockEntity implements TickableBlockEntity, SPTooltipable {

    @Unique
    private List<Component> veil$tooltip = new ArrayList<>();
    @Unique
    private final boolean veil$worldspace = true;
    @Unique
    private boolean veil$tooltipEnabled = false;
    @Unique
    private final int veil$tooltipY = 0;
    private int configSelection = 0;
    private boolean interactNext = false;
    private boolean stepNext = false;
    private boolean signalsDirty = false;
    private final List<String> configurationTooltipString = new ArrayList<>();
    private final List<ConfigurationTooltip> configurationTooltipExecutable = new ArrayList<>();
    int signalsReceived = 0;

    Object lastCall;
    private Object lastCallList;
    protected final List<Signal> lastSignals = new ArrayList<>();
    protected final List<Signal> putSignals = new ArrayList<>();
    LightRenderHandle<?> light;

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

    public void resetTooltip() {
        this.veil$tooltip.clear();
    }

    public void setTooltip(List<Component> tooltip) {
        this.veil$tooltip = tooltip;
    }

    public void setTooltipEnabled(boolean enabled) {
        this.veil$tooltipEnabled = enabled;
    }

    public void addTooltip(Component tooltip) {
        this.veil$tooltip.add(tooltip);
    }

    public void addTooltip(List<Component> tooltip) {
        this.veil$tooltip.addAll(tooltip);
    }

    public void addTooltip(String tooltip) {
        this.veil$tooltip.add(Component.nullToEmpty(tooltip));
    }


    public void setBackgroundColor(int color) {
    }

    public void setTopBorderColor(int color) {
    }

    public void setBottomBorderColor(int color) {
    }

    @Override
    public void drawExtra() {

    }

    public boolean getWorldspace() {
        return this.veil$worldspace;
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


    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
    }

    public SignalActorBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    private Direction getClockWise(Direction direction) {
        if (direction == Direction.UP || direction == Direction.DOWN) {
            return direction;
        }
        return direction.getClockWise();
    }

    private Direction getCounterClockWise(Direction direction) {
        if (direction == Direction.UP || direction == Direction.DOWN) {
            return direction;
        }
        return direction.getCounterClockWise();
    }


    public BlockPos getSwappedPos() {
        BlockPos sidedPos2;
        if (!this.getBlockState().getValue(SignalActorTickingBlock.SWAP_SIDES)) {
            sidedPos2 = this.getBlockPos().relative(getClockWise(getBlockState().getValue(SignalActorTickingBlock.FACING)), 1);
        } else {
            sidedPos2 = this.getBlockPos().relative(getCounterClockWise(getBlockState().getValue(SignalActorTickingBlock.FACING)), 1);
        }
        return sidedPos2;
    }

    public Direction getSwappedSide() {
        if (!this.getBlockState().getValue(SignalActorTickingBlock.SWAP_SIDES)) {
            return getClockWise(getBlockState().getValue(SignalActorTickingBlock.FACING));
        } else {
            return getCounterClockWise(getBlockState().getValue(SignalActorTickingBlock.FACING));
        }
    }

    public Direction getInvertedSwappedSide() {
        if (this.getBlockState().getValue(SignalActorTickingBlock.SWAP_SIDES)) {
            return getClockWise(getBlockState().getValue(SignalActorTickingBlock.FACING));
        } else {
            return getCounterClockWise(getBlockState().getValue(SignalActorTickingBlock.FACING));
        }
    }

    @Nullable
    public BlockPos getInvertedSwappedPos() {
        BlockPos sidedPos2 = new BlockPos(0, 0, 0);
        if (!level.isLoaded(getBlockPos()) || level.getBlockState(getBlockPos()).is(Blocks.AIR)) {
            return null;
        }
        if (this.getBlockState().getValue(SignalActorTickingBlock.SWAP_SIDES)) {
            sidedPos2 = this.getBlockPos().relative(getClockWise(getBlockState().getValue(SignalActorTickingBlock.FACING)), 1);
        } else {
            sidedPos2 = this.getBlockPos().relative(getCounterClockWise(getBlockState().getValue(SignalActorTickingBlock.FACING)), 1);
        }
        return sidedPos2;
    }

    public void postSignal(Signal signal) {

    }

    public void markDirty() {
        signalsDirty = true;
    }

    public Signal getSignal() {
        return SignalHelper.randomSignal(getSignals());
    }

    public Signal getSideSignal(Direction face) {
        return getSignal();
    }

    /**
     * Returns the list of signals inside the block. It should be deep cloned if you are going to modify it elsewhere.
     * You should always use {@link SignalActorBlockEntity#getSideSignals(Direction)} if your operation exists in the world.
     *
     * @return the putSignals field
     */
    public List<Signal> getSignals() {
        return putSignals;
    }

    public List<Signal> getSideSignals(Direction face) {
        return getSignals();
    }

    /**
     * If you want special behavior when signals are inserted on difference faces, override this method and return true. You must also clear signals each tick if you use this, or use an update signals method. This method does not catch signals that are inserted without a face.
     *
     * @param signals The signals that were inserted
     * @param face    The face they were added too
     * @return This tells the method that called it not to continue its default behavior
     */
    public boolean specialAddSignals(List<Signal> signals, Direction face) {
        return false;
    }

    public void addSignals(Object lastCall, List<Signal> signals, Direction face) {
        if (lastCall == this.lastCall) {
            return;
        }
        this.lastCall = lastCall;
        if (specialAddSignals(signals, face)) {
            return;
        }
        List<Signal> signals1 = new ArrayList<>();
        for (Signal signal : signals) {
            if (signal != null) {
                signals1.add(new Signal(signal));
            }
        }
        this.modulateSignals(signals1, true);
        BlockEntity blockEntity = level.getBlockEntity(getSwappedPos());
        if (blockEntity instanceof SignalActorBlockEntity signalActorBlockEntity) {
            signalActorBlockEntity.addSignals(lastCall, new ArrayList<>(signals1), face);
        }
        if (signalsReceived == 0) {
            this.updatePutSignals(signals1);
        } else {
            for (Signal signal : signals1) {
                if (signal != null) {
                    putSignals.add(new Signal(signal));
                }
            }
            signalsReceived++;
        }
    }

    public void updatePutSignals(List<Signal> signals) {
        signalsReceived++;
        if (putSignals.size() == signals.size()) {
            for (int i = 0; i < signals.size(); i++) {
                putSignals.get(i).copy(signals.get(i));
            }
        } else if (putSignals.size() > signals.size()) {
            ListIterator<Signal> iterator = putSignals.listIterator();
            while (iterator.hasNext()) {
                int i = iterator.nextIndex();
                Signal signal = iterator.next();
                if (i >= signals.size()) {
                    iterator.remove();
                    continue;
                }
                signal.copy(signals.get(i));
            }
        } else {
            for (int i = 0; i < signals.size(); i++) {
                Signal signal = signals.get(i);
                if (i >= putSignals.size()) {
                    putSignals.add(new Signal(signal));
                    continue;
                }
                putSignals.get(i).copy(signal);
            }
        }
        modulateSignals(putSignals, true);
    }

    /**
     * Puts signals into a block entity.
     *
     * @param nextCall
     * @param signals
     * @param face
     */
    public void putSignalsFace(Object nextCall, List<Signal> signals, Direction face) {
        if (specialAddSignals(signals, face)) {
            return;
        }
        putSignalList(nextCall, signals);
    }

    /**
     * This method should not be called on another blockEntity, this method exists as an easy way for block entities to start signal propagation
     *
     * @param nextCall
     * @param list
     */
    protected void putSignalList(Object nextCall, List<Signal> list) {
        if ((lastCallList != null && lastCallList.equals(nextCall)) || level == null) {
            return;
        }
        this.updatePutSignals(list);
        BlockPos sidedPos = this.getSwappedPos();
        BlockEntity blockEntity = level.getBlockEntity(sidedPos);
        if (!level.getBlockState(sidedPos).is(Blocks.AIR) && blockEntity instanceof SignalActorBlockEntity signalActorBlockEntity) {
            BlockPos invertedSwappedPos = signalActorBlockEntity.getInvertedSwappedPos();
            if (invertedSwappedPos != null && invertedSwappedPos.equals(this.getBlockPos())) {
                list = signalActorBlockEntity.modulateSignals(list, true);
                lastCallList = nextCall;
                signalActorBlockEntity.putSignalsFace(nextCall, list, getInvertedSwappedSide());
            }
        }
    }

    /**
     * This method has no special behavior. All it does is call { @link {@link #putSignalsFace(Object, List, Direction)}} but encapsulates the provided signal into a list
     *
     * @param signal Signal to be encapsulated into a list
     * @param face   The face it should be inserted into
     */
    public void putSignalFace(Signal signal, Direction face) {
        putSignalsFace(new Object(), SignalHelper.listOf(signal), face);
    }

    /**
     * This method has no special behavior. It calls { @link {@link #putSignalList(Object, List)}} but encapsulates the provided signal into a list, it does this to start signal propagation of a signal
     *
     * @param signal Signal to be encapsulated into a list
     */
    public void putSignal(Signal signal) {
        putSignalList(new Object(), SignalHelper.listOf(signal));
    }


    public List<Signal> modulateSignals(List<Signal> signalList, boolean updateTooltip) {
        List<Signal> safeList = new ArrayList<>();
        for (Signal signal : signalList) {
            Signal signal1 = modulateSignal(signal, updateTooltip);
            if (signal1 != null) {
                safeList.add(signal1);
            }
        }
        return safeList;
    }

    public Signal modulateSignal(Signal signal, boolean updateTooltip) {
        return signal;
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (light != null) {
            light.free();
        }
    }

    @Override
    public void loadSyncedData(CompoundTag tag) {
        super.loadSyncedData(tag);
        if (tag.contains("swap"))
            this.level.setBlock(this.getBlockPos(), this.getBlockState().setValue(SignalGeneratorBlock.SWAP_SIDES, tag.getBoolean("swap")), 2);
    }

    public void addConfigTooltip(String name, ConfigurationTooltip configurationTooltip) {
        configurationTooltipString.add(name);
        configurationTooltipExecutable.add(configurationTooltip);
    }

    public void setupConfigTooltips() {
        configurationTooltipString.clear();
        configurationTooltipExecutable.clear();
        if (!this.getTooltip().isEmpty()) {
            this.addTooltip("");
        }
        this.addTooltip("Configuration: ");
        this.addConfigTooltip("Signal Direction - " + (this.getBlockState().getValue(SignalActorTickingBlock.SWAP_SIDES) ? "Right" : "Left"), () -> {
            CompoundTag tag = new CompoundTag();
            tag.putBoolean("swap", !SignalActorBlockEntity.this.getBlockState().getValue(SignalActorTickingBlock.SWAP_SIDES));
            VeilPacketManager.server().sendPacket(new BlockEntityModificationC2SPacket(tag, SignalActorBlockEntity.this.getBlockPos()));
        });
    }

    private void finaliseConfigTooltips() {
        int i = 0;
        for (String string : configurationTooltipString) {
            if (i == configSelection) {
                this.addTooltip(string + " ←");
            } else {
                this.addTooltip(string);
            }
            i++;
        }
    }

    public void incrementConfigSelection() {
        stepNext = true;
        assert level != null;
        level.playLocalSound(this.getBlockPos(), SuperpositionSounds.DOWN.get(), SoundSource.BLOCKS, 3, 1, false);
    }

    public void interactConfig() {
        interactNext = true;
        assert level != null;
        level.playLocalSound(this.getBlockPos(), SuperpositionSounds.SCREWDRIVER.get(), SoundSource.BLOCKS, 1, 1, false);
    }

    private void checkEvents() {
        if (stepNext) {
            if (configurationTooltipString.size() > 1) {
                configSelection++;
            } else {
                configurationTooltipExecutable.get(configSelection).execute();
            }
            stepNext = false;
        }
        if (configSelection >= configurationTooltipString.size()) {
            configSelection = 0;
        }
        if (interactNext) {
            configurationTooltipExecutable.get(configSelection).execute();
            interactNext = false;
        }
    }

    @Override
    public void tick() {
        if (this.level != null && this.level.isClientSide) {
            if (this.lightEnabled() && light == null && !isRemoved()) {
                this.createLight();
                if (light.getLightData() instanceof AreaLightData areaLight) {
                    this.configureAreaLight(areaLight);
                }
                if (light.getLightData() instanceof PointLightData pointLight) {
                    this.configurePointLight(pointLight);
                }
                light.markDirty();
            }
            if (lightEnabled() && shouldUpdateLight() && light != null && !isRemoved()) {
                if (light.getLightData() instanceof AreaLightData areaLight) {
                    this.configureAreaLight(areaLight);
                }
                if (light.getLightData() instanceof PointLightData pointLight) {
                    this.configurePointLight(pointLight);
                }
                light.markDirty();
            }
            if (Minecraft.getInstance().player.getMainHandItem().getItem() instanceof ScrewdriverItem) {
                this.setupConfigTooltips();
                this.checkEvents();
                this.finaliseConfigTooltips();
            }
        }
        if (getLevel() != null && !getLevel().isClientSide) {
            if (signalsReceived == 0) {
                putSignals.clear();
                markDirty();
            }
            if (lastSignals.size() != putSignals.size()) {
                markDirty();
            } else {
                for (int i = 0; i < putSignals.size(); i++) {
                    if (!putSignals.get(i).equals(lastSignals.get(i))) {
                        markDirty();
                        break;
                    }
                }
            }

            lastSignals.clear();
            SignalHelper.updateSignalList(lastSignals, putSignals);
            lastSignals.addAll(putSignals);
            if (signalsDirty) {
                VeilPacketManager.around(null, (ServerLevel) level, getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), 100).sendPacket(new BlockSignalSyncS2CPacket(putSignals, getBlockPos()));
            }
            signalsDirty = false;
        }
        signalsReceived = 0;
    }


    public boolean lightEnabled() {
        return false;
    }

    public boolean shouldUpdateLight() {
        return false;
    }

    public LightData prepareLight() {
        return new AreaLightData();
    }

    public void createLight() {
        light = VeilRenderSystem.renderer().getLightRenderer().addLight(prepareLight());
    }

    public void configureAreaLight(AreaLightData light) {
        Vec3 center = this.getBlockPos().getCenter();
        Direction facing = this.getBlockState().getValue(SignalActorTickingBlock.FACING);
        light.getPosition().set(center.x, center.y, center.z);
        light.getOrientation().set(facing.getRotation().rotateX((float) (Math.PI / 2f)).rotateY((float) Math.PI));
    }

    public void configurePointLight(PointLightData light) {

    }
    //TODO: Weak power checking method PLEASE
    // This needs a new method for checking the power!
}

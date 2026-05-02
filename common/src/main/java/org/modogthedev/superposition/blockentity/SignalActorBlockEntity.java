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
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.modogthedev.superposition.block.SignalGeneratorBlock;
import org.modogthedev.superposition.compat.sable.SableCompat;
import org.modogthedev.superposition.core.SuperpositionSounds;
import org.modogthedev.superposition.item.ScrewdriverItem;
import org.modogthedev.superposition.networking.packet.BlockEntityModificationC2SPacket;
import org.modogthedev.superposition.networking.packet.BlockSignalSyncS2CPacket;
import org.modogthedev.superposition.system.cable.PortConfig;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.util.*;
import org.spongepowered.asm.mixin.Unique;

import java.util.*;

public abstract class SignalActorBlockEntity extends SyncedBlockEntity implements TickableBlockEntity, SPTooltipable, PortBehavior {

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
    private PortConfig portConfig;
    protected Vector3d lightPosition = null;
    int signalsReceived = 0;

    Object lastCall;
    private Object lastCallList;

    protected Map<String, SignalList> putSignals = new HashMap<>();
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
        if (lightPosition != null) {
            tag.putDouble("lightX", lightPosition.x);
            tag.putDouble("lightY", lightPosition.y);
            tag.putDouble("lightZ", lightPosition.z);
        }
        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
    }

    public SignalActorBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
        PortConfig.Builder builder = buildPorts(PortConfig.create());
        portConfig = builder.build();
    }

    public PortConfig.Builder buildPorts(PortConfig.Builder builder) {
        return builder.addInputPort(inPortName()).addOutputPort(outPortName());
    }

    @Override
    public PortConfig getPortConfig() {
        return portConfig;
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

    @ApiStatus.Internal
    public SignalList getSignalList(String port) {
        return putSignals.computeIfAbsent(port, string -> new SignalList());
    }

    @Override
    public @Unmodifiable List<Signal> getPortSignals(String port) {
        if (!portConfig.getPorts().containsKey(port)) {
            return List.of();
        }
        return getSignalList(port).getSignals();
    }

    @Override
    public boolean putPortSignals(String port, List<Signal> signals) {
        if (port == null || !portConfig.getPorts().containsKey(port)) {
            return false;
        }
        return getSignalList(port).addAll(signals);
    }



    public void markDirty() {
        signalsDirty = true;
    }


    /**
     * This is called for all the signals that exist in the "in" port.
     * This only happens if the block has an "in" port.
     * @param signalList The signals that come from the "in" port
     * @return The signals that will go to the "out" port. This only happens if the block has an "out" port. Return null to cancel this signal returning to out.
     */
    public List<Signal> manipulateSignals(List<Signal> signalList) {
        List<Signal> outList = new ArrayList<>();
        for (Signal inSignal : signalList) {
            Signal outSignal = manipulateSignal(inSignal);
            if (outSignal != null) {
                outList.add(outSignal);
            }
        }
        return outList;
    }

    /**
     * This is called for each of the signals that exist in the "in" port.
     * This only happens if the block has an "in" port.
     * @return The signal that will go to the "out" port. This only happens if the block has an "out" port. Return null to cancel this signal returning to out.
     */
    public @Nullable Signal manipulateSignal(Signal signal) {
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
        this.addConfigTooltip("Out Direction - " + (this.getBlockState().getValue(SignalActorTickingBlock.SWAP_SIDES) ? "Right" : "Left"), () -> {
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
                if (SableCompat.isSublevel(level,getBlockPos().getCenter())) {
                    Vec3 pos = SableCompat.tryTransform(level, getBlockPos().getCenter());
                    lightPosition = new Vector3d(pos.x,pos.y,pos.z);
                } else {
                    Vec3 center = getBlockPos().getCenter();
                    lightPosition = new Vector3d(center.x,center.y,center.z);
                }
                if (light.getLightData() instanceof AreaLightData areaLight) {
                    this.configureAreaLight(areaLight);
                }
                if (light.getLightData() instanceof PointLightData pointLight) {
                    this.configurePointLight(pointLight);
                }
                light.markDirty();
            }
            if (lightEnabled() && shouldUpdateLight() && light != null && !isRemoved()) {
                if (SableCompat.isSublevel(level,getBlockPos().getCenter())) {
                    Vec3 pos = SableCompat.tryTransform(level, getBlockPos().getCenter());
                    lightPosition = new Vector3d(pos.x,pos.y,pos.z);
                } else {
                    Vec3 center = getBlockPos().getCenter();
                    lightPosition = new Vector3d(center.x,center.y,center.z);
                }
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
            for (SignalList value : putSignals.values()) {
                boolean change = value.flush();
                if (change) {
                    markDirty();
                }
            }

//            if (getInputSignals().isEmpty() && !getOutputSignals().isEmpty())

            boolean dirty = processPorts();
            if (dirty) {
                markDirty();
            }

            if (level.getBlockEntity(getSwappedPos()) instanceof PortBehavior portBehavior && Objects.equals(outPortName(), "out")) {
                if (portBehavior.getPortConfig().getPorts().containsKey(portBehavior.inPortName())) {
                    portBehavior.putPortSignals(portBehavior.inPortName(),getPortSignals(outPortName()));
                }
            }

            if (signalsDirty) {
                VeilPacketManager.around(null, (ServerLevel) level, getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), 100).sendPacket(new BlockSignalSyncS2CPacket(putSignals, getBlockPos()));
                sendData();
            }
            signalsDirty = false;
        }
        signalsReceived = 0;
    }

    public boolean processPorts() {
        if (portConfig.getPorts().containsKey(inPortName())) {
            List<Signal> manipulatedSignals = getPortSignals(inPortName());
            if (portConfig.getPorts().containsKey(outPortName())) {
                return putPortSignals(outPortName(), manipulateSignals(manipulatedSignals));
            }
        }
        return false;
    }

    /**
     * Fetches signals from the input port
     * @return The signals in the input port
     */
    public List<Signal> getInputSignals() {
        return getPortSignals(inPortName());
    }

    /**
     * Fetches signals from the output port
     * @return The signals in the output port
     */
    public List<Signal> getOutputSignals() {
        return getPortSignals(outPortName());
    }

    public boolean lightEnabled() {
        return false;
    }

    public boolean shouldUpdateLight() {
        return SableCompat.isSublevel(level,getBlockPos().getCenter());
    }

    public LightData prepareLight() {
        return new AreaLightData();
    }

    public void createLight() {
        light = VeilRenderSystem.renderer().getLightRenderer().addLight(prepareLight());
    }

    public void configureAreaLight(AreaLightData light) {
        Direction facing = this.getBlockState().getValue(SignalActorTickingBlock.FACING);
        light.getPosition().set(lightPosition.x, lightPosition.y, lightPosition.z);
        Quaternionf rotation = facing.getRotation();
        Quaternionf poseRotation = SableCompat.getRotation(level, getBlockPos().getCenter());
        if (poseRotation != null) {
            rotation = poseRotation.mul(rotation);
        }
        light.getOrientation().set(rotation.mul(new Quaternionf().fromAxisAngleDeg(1,0,0,-90)).conjugate());
        light.setOcclusionEnabled(true);
    }

    public void configurePointLight(PointLightData light) {

    }
    //TODO: Weak power checking method PLEASE
    // This needs a new method for checking the power!
}

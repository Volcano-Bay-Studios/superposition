package org.modogthedev.superposition.blockentity;

import foundry.veil.api.network.VeilPacketManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.modogthedev.superposition.core.SuperpositionBlockEntities;
import org.modogthedev.superposition.core.SuperpositionBlocks;
import org.modogthedev.superposition.networking.packet.BlockEntityModificationC2SPacket;
import org.modogthedev.superposition.system.widget.Widget;
import org.modogthedev.superposition.system.widget.WidgetSurface;
import org.modogthedev.superposition.util.block.CableAttachmentOffset;
import org.modogthedev.superposition.util.block.DynamicShapedBlockEntity;
import org.modogthedev.superposition.util.block.SignalActorTickingBlock;

import java.util.ArrayList;
import java.util.List;

import static org.modogthedev.superposition.util.block.SignalActorTickingBlock.FACING;

/**
 * A panel that stands on legs that can be adjusted to tilt its surface. Panels next to each other share their surface.
 */
public class PanelBlockEntity extends WidgetPanelBlockEntity implements DynamicShapedBlockEntity, CableAttachmentOffset {
    private static final float SURFACE_HEIGHT = 9 / 16f;

    private float frontHeight;
    private float backHeight;
    private float angle;

    public PanelBlockEntity(BlockPos pos, BlockState state) {
        super(SuperpositionBlockEntities.PANEL.get(), pos, state);
    }

    @Override
    public Vec3 getCableOffset(Direction direction) {
        if (direction.getAxis().isHorizontal()) {
            return new Vec3(0,-4/16f,0);
        }
        return Vec3.ZERO;
    }

    @Override
    public WidgetSurface getSurface() {
        return new WidgetSurface(getPanelMatrix().translate(0, SURFACE_HEIGHT, 0), getLast() != null ? -16 : 0, getNext() != null ? 32 : 16, 0, 16);
    }

    @Override
    public List<Joined> getJoined() {
        List<Joined> joined = new ArrayList<>();
        PanelBlockEntity next = getNext();
        if (next != null) {
            joined.add(new Joined(next, 16, 0));
        }
        PanelBlockEntity last = getLast();
        if (last != null) {
            joined.add(new Joined(last, -16, 0));
        }
        return joined;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putFloat("front_height", frontHeight);
        tag.putFloat("back_height", backHeight);
        updateAngle();
        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("front_height")) {
            frontHeight = tag.getFloat("front_height");
        }
        if (tag.contains("back_height")) {
            backHeight = tag.getFloat("back_height");
        }
        updateAngle();
    }

    @Override
    public void loadSyncedData(CompoundTag tag) {
        if (tag.contains("front_height")) {
            frontHeight = tag.getFloat("front_height");
            frontHeight = Mth.clamp(frontHeight, 0, 5);
        }
        if (tag.contains("back_height")) {
            backHeight = tag.getFloat("back_height");
            backHeight = Mth.clamp(backHeight, 0, 5);
        }
        updateAngle();
        updateHeight(true);
        updateHeight(false);
        super.loadSyncedData(tag);
    }

    @Override
    protected void addPanelConfiguration() {
        this.addConfigTooltip("Front - " + frontHeight, () -> {
            CompoundTag tag = new CompoundTag();
            frontHeight += 1;
            if (frontHeight > 5) {
                frontHeight = 0;
            }
            updateAngle();
            updateHeight(true);
            updateHeight(false);
            tag.putFloat("front_height", frontHeight);
            VeilPacketManager.server().sendPacket(new BlockEntityModificationC2SPacket(tag, this.getBlockPos()));
        });
        this.addConfigTooltip("Back - " + backHeight, () -> {
            CompoundTag tag = new CompoundTag();
            backHeight += 1;
            if (backHeight > 5) {
                backHeight = 0;
            }
            updateAngle();
            updateHeight(true);
            updateHeight(false);
            tag.putFloat("back_height", backHeight);
            VeilPacketManager.server().sendPacket(new BlockEntityModificationC2SPacket(tag, this.getBlockPos()));
        });
    }

    public void updateHeight(boolean right) {
        Direction dir = getBlockState().getValue(SignalActorTickingBlock.FACING);
        BlockEntity blockEntity = level.getBlockEntity(getBlockPos().relative(right ? dir.getClockWise() : dir.getCounterClockWise()));
        if (blockEntity instanceof PanelBlockEntity panelBlockEntity) {
            if (panelBlockEntity.getBlockState().getValue(SignalActorTickingBlock.FACING).equals(dir)) {
                panelBlockEntity.frontHeight = this.frontHeight;
                panelBlockEntity.backHeight = this.backHeight;
                panelBlockEntity.markDirty();
                panelBlockEntity.updateHeight(right);
            }
        }
        updateAngle();
    }

    public Matrix4f getPanelMatrix() {
        Direction dir = getBlockState().getValue(FACING);
        Matrix4f ms = new Matrix4f();

        ms.translate(0.5f, 0, 0.5f);
        ms.rotate((float) Math.atan2(-dir.getStepX(), -dir.getStepZ()), 0, 1, 0);
        ms.translate(-0.5f, 0, -0.5f);

        boolean shift = getFrontHeight() < getBackHeight();

        ms.translate(0, 10 / 16f, shift ? 1 : 0);
        ms.rotate(getAngle(), 1, 0, 0);
        ms.translate(0, -10 / 16f, shift ? -1 : 0);
        ms.translate(0, (shift ? getBackHeight() : getFrontHeight()) / 16f, 0);

        return ms;
    }

    @Override
    public List<DynamicShape> getShapes(boolean forRendering) {
        if (forRendering) {
            Matrix4f mat = getPanelMatrix();
            return List.of(new DynamicShape(mat, getShape(getBlockState(), getLevel(), getBlockPos(), mat)));
        } else {
            List<DynamicShape> shapes = new ArrayList<>();
            Matrix4f mat = getPanelMatrix();

            VoxelShape shape = getShape(getBlockState(), getLevel(), getBlockPos(), mat);

            Vector3f min = new Vector3f();
            Vector3f max = new Vector3f();

            for (Widget widget : getWidgets()) {
                Vector2i widgetPos = widget.getPosition();
                min.set(widgetPos.x, 0, widgetPos.y);
                max.set(min);
                max.add(widget.getBounds());
                max.add(0, 9 / 16f, 0);
                min.add(0, 9 / 16f, 0);

                min.mul(16);
                max.mul(16);

                VoxelShape box = Block.box(min.x, min.y, min.z, max.x, max.y, max.z);

                shape = Shapes.join(shape, box, BooleanOp.OR);
            }

            shapes.add(new DynamicShape(mat, shape));
            return shapes;
        }
    }

    public void exploreShapes(BlockGetter level, BlockPos pos, BlockPos offset, List<VoxelShape> shapes, Direction dir, Direction trueRotation) {
        if (level.getBlockState(pos).is(SuperpositionBlocks.PANEL.get())) {
            Rotation rotation = Rotation.NONE;
            switch (trueRotation) {
                case NORTH -> {
                    rotation = Rotation.CLOCKWISE_180;
                    break;
                }
                case SOUTH -> {
                    rotation = Rotation.CLOCKWISE_90;
                    break;
                }
                case WEST -> {
                    rotation = Rotation.COUNTERCLOCKWISE_90;
                    break;
                }
                case EAST -> {
                    rotation = Rotation.NONE;
                    break;
                }
            }
            BlockPos ourOffest = BlockPos.ZERO.relative(trueRotation == Direction.EAST || trueRotation == Direction.NORTH ? Direction.EAST : Direction.WEST, offset.getX() + offset.getY() + offset.getZ());
            shapes.add(Block.box(0, 7, 0, 16, 9, 16).move(ourOffest.getX(), ourOffest.getY(), ourOffest.getZ()));
            BlockPos relative = pos.relative(dir);
            exploreShapes(level, relative, relative.subtract(pos).offset(offset), shapes, dir, trueRotation);
        }
    }

    protected VoxelShape getOurShape() {
        return Block.box(0, 7, 0, 16, 9, 16);
    }

    /**
     * @return The panel that continues the surface of this one in the positive direction
     */
    protected PanelBlockEntity getNext() {
        Direction facing = getBlockState().getValue(FACING);
        return getJoined(facing.getClockWise());
    }

    /**
     * @return The panel that continues the surface of this one in the negative direction
     */
    protected PanelBlockEntity getLast() {
        Direction facing = getBlockState().getValue(FACING);
        return getJoined(facing.getCounterClockWise());
    }

    private PanelBlockEntity getJoined(Direction dir) {
        if (getLevel() != null && getLevel().getBlockEntity(getBlockPos().relative(dir)) instanceof PanelBlockEntity panel && panel.getBlockState().getValue(FACING).equals(getBlockState().getValue(FACING))) {
            return panel;
        }
        return null;
    }

    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, Matrix4f mat) {
        VoxelShape ourShape = getOurShape();
        Direction facing = state.getValue(FACING);
        Direction dir = facing.getClockWise();
        BlockPos forward = pos.relative(dir);
        BlockPos back = pos.relative(dir.getOpposite());

        List<VoxelShape> shapes = new ArrayList<>();

        exploreShapes(level, forward, forward.subtract(pos), shapes, dir, facing);
        exploreShapes(level, back, back.subtract(pos), shapes, dir.getOpposite(), facing);

        for (VoxelShape shape : shapes) {
            ourShape = Shapes.join(ourShape, shape, BooleanOp.OR);
        }

        return ourShape;
    }

    public void updateAngle() {
        angle = (float) Math.asin((getFrontHeight() - getBackHeight()) / 12);
    }

    public float getFrontHeight() {
        return frontHeight;
    }

    public float getBackHeight() {
        return backHeight;
    }

    public float getAngle() {
        return angle;
    }

}

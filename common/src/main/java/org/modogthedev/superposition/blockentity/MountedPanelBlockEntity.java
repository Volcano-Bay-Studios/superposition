package org.modogthedev.superposition.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.modogthedev.superposition.block.MountedPanelBlock;
import org.modogthedev.superposition.core.SuperpositionBlockEntities;
import org.modogthedev.superposition.system.widget.WidgetSurface;

import java.util.ArrayList;
import java.util.List;

import static org.modogthedev.superposition.util.block.SignalActorTickingBlock.FACING;

public class MountedPanelBlockEntity extends WidgetPanelBlockEntity {

    public MountedPanelBlockEntity(BlockPos pos, BlockState state) {
        super(SuperpositionBlockEntities.MOUNTED_PANEL.get(), pos, state);
    }

    @Override
    public WidgetSurface getSurface() {
        Matrix4f transform = getTransform();
        int minX = 0;
        int maxX = 16;
        int minY = 0;
        int maxY = 16;
        for (Joined joined : getJoined(transform)) {
            minX = Math.min(minX, joined.offsetX());
            maxX = Math.max(maxX, joined.offsetX() + 16);
            minY = Math.min(minY, joined.offsetY());
            maxY = Math.max(maxY, joined.offsetY() + 16);
        }
        return new WidgetSurface(transform, minX, maxX, minY, maxY);
    }

    @Override
    public List<Joined> getJoined() {
        return getJoined(getTransform());
    }

    /**
     * Finds the panels around this one that are mounted the same way, including the ones on its corners.
     */
    private List<Joined> getJoined(Matrix4f transform) {
        List<Joined> joined = new ArrayList<>();
        if (getLevel() == null) {
            return joined;
        }
        Vector3f across = new Vector3f(1, 0, 0).mulDirection(transform);
        Vector3f along = new Vector3f(0, 0, 1).mulDirection(transform);
        Direction acrossDir = Direction.getNearest(across.x, across.y, across.z);
        Direction alongDir = Direction.getNearest(along.x, along.y, along.z);

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                if (x == 0 && y == 0) {
                    continue;
                }
                BlockPos pos = getBlockPos().relative(acrossDir, x).relative(alongDir, y);
                if (getLevel().getBlockEntity(pos) instanceof MountedPanelBlockEntity panel
                        && panel.getBlockState().getValue(FACING) == getBlockState().getValue(FACING)
                        && panel.getBlockState().getValue(MountedPanelBlock.FACE) == getBlockState().getValue(MountedPanelBlock.FACE)) {
                    joined.add(new Joined(panel, x * 16, y * 16));
                }
            }
        }
        return joined;
    }

    private Matrix4f getTransform() {
        Direction dir = getBlockState().getValue(FACING);
        float tilt = switch (getBlockState().getValue(MountedPanelBlock.FACE)) {
            case FLOOR -> 0;
            case WALL -> (float) -Math.PI / 2;
            case CEILING -> (float) Math.PI;
        };

        Matrix4f ms = new Matrix4f();
        ms.translate(0.5f, 0.5f, 0.5f);
        ms.rotate((float) Math.atan2(-dir.getStepX(), -dir.getStepZ()), 0, 1, 0);
        ms.rotate(tilt, 1, 0, 0);
        ms.translate(-0.5f, -0.5f, -0.5f);
        ms.translate(0, MountedPanelBlock.THICKNESS / 16f, 0);

        return ms;
    }
}

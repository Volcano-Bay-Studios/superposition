package org.modogthedev.superposition.system.cable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class CableClipContext extends ClipContext {
    private final Vec3 from;
    private final Vec3 to;
    private final Fluid fluid;
    private final CableCollides block;
    private final CollisionContext collisionContext;

    public CableClipContext(Vec3 from, Vec3 to, CableCollides block, Fluid fluid, @Nullable Entity entity, ClipContext.Block block1) {
        super(from, to, block1, fluid, entity);
        this.from = from;
        this.to = to;
        this.block = block;
        this.fluid = fluid;
        this.collisionContext = entity == null ? CollisionContext.empty() : CollisionContext.of(entity);
    }

    @Override
    public VoxelShape getBlockShape(BlockState blockState, BlockGetter level, BlockPos pos) {
        return this.block.get(blockState, level, pos, this.collisionContext);
    }

    @Override
    public Vec3 getTo() {
        return this.to;
    }

    @Override
    public Vec3 getFrom() {
        return this.from;
    }

    @Override
    public VoxelShape getFluidShape(FluidState state, BlockGetter level, BlockPos pos) {
        return this.fluid.canPick(state) ? state.getShape(level, pos) : Shapes.empty();
    }

    public enum CableCollides implements ShapeGetter {

        CABLE_COLLIDES(BlockBehaviour.BlockStateBase::getCollisionShape);
//      (state, world, pos, context) -> state.is(Blocks.AIR) ? Shapes.empty() : Shapes.block()
        private final ShapeGetter shapeGetter;

        CableCollides(ShapeGetter shapeGetter) {
            this.shapeGetter = shapeGetter;
        }

        public VoxelShape get(BlockState arg, BlockGetter arg2, BlockPos arg3, CollisionContext arg4) {
            return this.shapeGetter.get(arg, arg2, arg3, arg4);
        }
    }
}

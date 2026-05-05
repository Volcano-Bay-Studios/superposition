package org.modogthedev.superposition.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.modogthedev.superposition.util.DynamicShapedBlockEntity;
import org.modogthedev.superposition.util.DynamicVoxel;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(VoxelShape.class)
public class VoxelShapeMixin implements DynamicVoxel {
    private DynamicShapedBlockEntity.DynamicShape superposition$dynamicShape = null;

    @Override
    public DynamicShapedBlockEntity.DynamicShape getDynamicShape() {
        return superposition$dynamicShape;
    }

    @Override
    public void setDynamicShape(DynamicShapedBlockEntity.DynamicShape dynamicShape) {
        superposition$dynamicShape = dynamicShape;
    }

    @WrapMethod(method = "clip")
    public BlockHitResult clip(Vec3 startVec, Vec3 endVec, BlockPos pos, Operation<BlockHitResult> original) {
        return original.call(startVec,endVec,pos);
    }
}

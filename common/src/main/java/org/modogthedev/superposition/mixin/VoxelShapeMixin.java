package org.modogthedev.superposition.mixin;

import net.minecraft.world.phys.shapes.VoxelShape;
import org.modogthedev.superposition.util.DelegateVoxelShape;
import org.modogthedev.superposition.util.DynamicShapedBlockEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(VoxelShape.class)
public class VoxelShapeMixin implements DelegateVoxelShape {
    private DynamicShapedBlockEntity superposition$dynamicShapedBlockEntity = null;

    @Override
    public void setDynamicShape(DynamicShapedBlockEntity dynamicShapedBlockEntity) {
        superposition$dynamicShapedBlockEntity = dynamicShapedBlockEntity;
    }

    @Override
    public DynamicShapedBlockEntity getDynamicShape() {
        return superposition$dynamicShapedBlockEntity;
    }
}

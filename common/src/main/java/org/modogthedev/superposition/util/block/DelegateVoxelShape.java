package org.modogthedev.superposition.util.block;

public interface DelegateVoxelShape {
    DynamicShapedBlockEntity getDynamicShape();
    void setDynamicShape(DynamicShapedBlockEntity dynamicShapedBlockEntity);
}

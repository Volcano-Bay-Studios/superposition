package org.modogthedev.superposition.util;

public interface DelegateVoxelShape {
    DynamicShapedBlockEntity getDynamicShape();
    void setDynamicShape(DynamicShapedBlockEntity dynamicShapedBlockEntity);
}

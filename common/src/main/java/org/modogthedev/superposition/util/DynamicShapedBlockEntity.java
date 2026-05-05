package org.modogthedev.superposition.util;

import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public interface DynamicShapedBlockEntity {

    List<DynamicShape> getShapes();

    record DynamicShape(Matrix4f transformation, VoxelShape shape) {}
}

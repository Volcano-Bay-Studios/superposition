package org.modogthedev.superposition.util.block;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

public interface CableAttachmentOffset {
    Vec3 getCableOffset(Direction direction);
}

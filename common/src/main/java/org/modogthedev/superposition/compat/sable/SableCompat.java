package org.modogthedev.superposition.compat.sable;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.modogthedev.superposition.compat.CompatabilityHandler;

public class SableCompat {
    public static Vec3 tryTransform(Level level, Vec3 pos) {
        if (CompatabilityHandler.Mod.SABLE.isLoaded) {
            SuperpositionSableHelper.transformPosition(level,pos);
        }
        return pos;
    }
}

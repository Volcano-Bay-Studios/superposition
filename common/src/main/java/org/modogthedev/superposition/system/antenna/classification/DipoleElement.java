package org.modogthedev.superposition.system.antenna.classification;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.modogthedev.superposition.system.antenna.AntennaElement;
import org.modogthedev.superposition.system.antenna.type.PhysicalAntenna;
import org.modogthedev.superposition.util.SuperpositionMth;

import java.util.List;

public class DipoleElement extends AntennaElement {

    final float size;

    public DipoleElement(float size, Vector3d position) {
        super(position);
        this.size = size;
    }

    @Override
    public String getClassificationName() {
        return "Dipole";
    }

    @Override
    public float getAntennaFrequency() {
        return SuperpositionMth.antennaSizeToHz(size);
    }
}

package org.modogthedev.superposition.system.antenna.classification;

import org.joml.Vector3d;
import org.modogthedev.superposition.system.antenna.AntennaElement;
import org.modogthedev.superposition.util.SuperpositionMth;

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

    @Override
    public double getQValue() {
        return 11;
    }
}

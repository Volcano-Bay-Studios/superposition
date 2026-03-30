package org.modogthedev.superposition.system.antenna.classification;

import org.joml.Vector3d;
import org.modogthedev.superposition.system.antenna.AntennaElement;
import org.modogthedev.superposition.util.SuperpositionMth;

public class MonopoleElement extends AntennaElement {
    final float size;
    public MonopoleElement(float size, Vector3d position) {
        super(position);
        this.size = size;
    }

    @Override
    public String getClassificationName() {
        return "Monopole";
    }

    @Override
    public double getQValue() {
        return 6;
    }

    @Override
    public float getAntennaFrequency() {
        return SuperpositionMth.antennaSizeToHz(size);
    }
}

package org.modogthedev.superposition.system.antenna;

import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.modogthedev.superposition.system.antenna.type.PhysicalAntenna;
import org.modogthedev.superposition.system.signal.Signal;

public class AntennaElement {
    protected Vector3d position;
    public AntennaElement(Vector3d position) {
        this.position = position;
    }

    public String getClassificationName() {
        return "Unknown";
    }

    public Vector3d getPosition() {
        return position;
    }

    public Signal sendSignal(Signal signal) {
        Signal returnSignal = new Signal(signal);

        return returnSignal;
    }

    public float getAntennaFrequency() {
        return 0;
    }
}

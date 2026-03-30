package org.modogthedev.superposition.system.antenna;

import org.joml.Vector3d;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.system.signal.SignalManager;
import org.modogthedev.superposition.util.SuperpositionMth;

public class AntennaElement {
    protected Vector3d position;
    protected Vector3d antennaPositionOffset = new Vector3d();
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
        returnSignal.changeUUID();
        returnSignal.getPos().set(position).add(antennaPositionOffset);
        returnSignal.setEmitting(true);
        double multiplier = SuperpositionMth.calculateAntennaAmplitude(SuperpositionMth.hzToAntennaSize(getAntennaFrequency()),SuperpositionMth.hzToAntennaSize(returnSignal.getFrequency()));
        returnSignal.mulAmplitude((float) multiplier);

        if (returnSignal.getAmplitude() > 0.5f) {
            SignalManager.updateSignal(returnSignal);
        }
        return returnSignal;
    }

    public void updatePosition(double x, double y, double z) {
        position.set(x,y,z);
    }

    public void updateOffsetPosition(double x, double y, double z) {
        antennaPositionOffset.set(x,y,z);
    }
    public float getAntennaFrequency() {
        return 0;
    }
}

package org.modogthedev.superposition.system.antenna;

import org.joml.Vector3d;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.system.signal.SignalManager;
import org.modogthedev.superposition.util.SuperpositionMth;

public abstract class AntennaElement {
    protected Vector3d position;
    protected Vector3d antennaPositionOffset = new Vector3d();
    public AntennaElement(Vector3d position) {
        this.position = position;
    }

    public String getClassificationName() {
        return "Unknown";
    }

    public Vector3d getPosition() {
        return new Vector3d(position).add(antennaPositionOffset);
    }

    /**
     * This collects the changes in the signal and broadcasts it.
     * @param signal
     * @return
     */
    public Signal sendSignal(Signal signal) {
        Signal returnSignal = new Signal(signal);
        returnSignal.changeUUID();
        returnSignal.getPos().set(position).add(antennaPositionOffset);
        returnSignal.setEmitting(true);
        double multiplier = getAmplitudeScalar(signal);
        returnSignal.mulAmplitude((float) multiplier);

        if (returnSignal.getAmplitude() > 0.5f) {
            SignalManager.updateSignal(returnSignal);
        }
        return returnSignal;
    }

    /**
     * Returns a scalar for the amplitude mismatch of this antenna.
     */
    public double getAmplitudeScalar(Signal signal) {
        return SuperpositionMth.getAmplitudeScalar(getAntennaFrequency(),signal.getFrequency(),getQValue());
    }

    public void updatePosition(double x, double y, double z) {
        position.set(x,y,z);
    }

    public void updateOffsetPosition(double x, double y, double z) {
        antennaPositionOffset.set(x,y,z);
    }

    /**
     * The current frequency of the antenna part.
     */
    public abstract float getAntennaFrequency();

    /**
     * The Q value of the antenna
     * @return
     */
    public abstract double getQValue();
}

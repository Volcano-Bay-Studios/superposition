package org.modogthedev.superposition.system.antenna;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.system.signal.SignalManager;
import org.modogthedev.superposition.util.LongRaycast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Antenna {
    public Level level;
    public List<Signal> signals = new ArrayList<>();
    public HashMap<UUID, Signal> signalsSentLastTick = new HashMap<>();
    public BlockPos antennaActor;
    public boolean isReceiving;
    private Vector3d position = new Vector3d();
    protected List<AntennaElement> antennaElements = new ArrayList<>();
    //TODO: radiation pattern

    public Antenna(BlockPos antennaActor, Level level) {
        this.antennaActor = antennaActor;
        this.level = level;
    }

    /**
     * Sends a new list of signals.
     * This method terminates any signals being broadcast if the list does not include them.
     *
     * @param signals The signals to be broadcast
     */
    public void sendSignals(List<Signal> signals) {
        HashMap<UUID, Signal> oldSignalsSentLastTick = new HashMap<>(signalsSentLastTick);
        signalsSentLastTick.clear();
        for (Signal signal : signals) {
            signal.level = this.level;
            Signal oldSignal = oldSignalsSentLastTick.get(signal.getUuid());
            if (signal.equals(oldSignal)) {
                SignalManager.markSignalUpdate(signal);
                signalsSentLastTick.put(signal.getUuid(), new Signal(signal));
            } else {
                sendSignal(signal);
                signalsSentLastTick.put(signal.getUuid(), new Signal(signal));
            }
        }
    }

    /**
     * Broadcasts the given signal through the antenna
     *
     * @param signal The signal to send
     * @return Each signal sent, after the original has been sent through each AntennaElement
     */
    public List<Signal> sendSignal(Signal signal) {
        List<Signal> returnSignals = new ArrayList<>();
        for (AntennaElement antennaElement : antennaElements) {
            Signal returnSignal = antennaElement.sendSignal(signal);
            if (returnSignal != null) {
                returnSignals.add(returnSignal);
            }
        }
        return returnSignals;
    }

    public void receiveSignal(final Signal signal) {
        for (AntennaElement antennaElement : antennaElements) {
            double amplitude = signal.getAmplitude();
            float dist = (float) antennaElement.getPosition().distance(signal.getPos());

            if (dist < signal.getMaxDist() && dist > signal.getMinDist()) {

                amplitude *= (1.0F / Math.max(1, dist / (1000000000 / signal.getFrequency())));
                Vec3 to = antennaActor.getCenter().add(antennaElement.getPosition().x, antennaElement.getPosition().y, antennaElement.getPosition().z);
                float penetration = LongRaycast.getPenetration(signal.level, signal.getPos(), new Vector3d(to.x, to.y, to.z));
                amplitude *= (Mth.map(penetration, 0, signal.getFrequency() / 200000, 1, 0));
                amplitude *= antennaElement.getAmplitudeScalar(signal);
                if (amplitude > 0.5f) {
                    Signal receiving = new Signal(signal);
                    receiving.addTraversalDistance((float) signal.getPos().distance(new Vector3d(to.x, to.y, to.z)));
                    receiving.setAmplitude((float) amplitude);
                    signals.add(receiving);
                }
            }
        }
    }

    public boolean isPos(BlockPos pos) {
        return antennaActor.equals(pos);
    }

    protected void setPosition(Vector3d position) {
        this.position = position;
    }

    public Vector3d getPosition() {
        return position;
    }

    public void updateTooltip(List<Component> tooltip) {

    }

    public List<AntennaElement> getAntennaElements() {
        return antennaElements;
    }
}
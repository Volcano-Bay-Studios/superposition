package org.modogthedev.superposition.system.antenna;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.system.signal.SignalManager;

import java.util.*;

public class Antenna {
    public Level level;
    public List<Signal> signals = new ArrayList<>();
    public HashMap<UUID,List<Signal>> signalsSentLastTick = new HashMap<>();
    public HashMap<UUID,List<Signal>> signalsSentThisTick = new HashMap<>();
    public BlockPos antennaActor;
    public boolean isReceiving;
    private Vec3 position = new Vec3(0,0,0);
    protected List<AntennaElement> antennaElements = new ArrayList<>();
    //TODO: radiation pattern

    public Antenna(BlockPos antennaActor, Level level) {
        this.antennaActor = antennaActor;
        this.level = level;
    }

    /**
     * Sends a new list of signals.
     * This method terminates any signals being broadcast if the list does not include them.
     * @param signals The signals to be broadcast
     */
    public void sendSignals(List<Signal> signals) {
        signalsSentThisTick.clear();
        for (Signal signal : signals) {
            signal.level = this.level;
            signalsSentThisTick.getOrDefault(signal.getBroadcastUuid(),new ArrayList<>()).add(signal);
        }

        for (UUID uuid : signalsSentThisTick.keySet()) {
            List<Signal> thisSignalList = signalsSentThisTick.getOrDefault(uuid,new ArrayList<>());
            List<Signal> oldSignalList = signalsSentLastTick.getOrDefault(uuid, new  ArrayList<>());
            try {
                if (!oldSignalList.getFirst().equals(thisSignalList.getFirst())) {
                    for (Signal signal : oldSignalList) {
                        signal.changeUUID();
                        signal.stop();
                    }
                    oldSignalList.clear();
                    oldSignalList.addAll(sendSignal(thisSignalList.getFirst()));
                }
            } catch (NoSuchElementException ignored) {}
        }
    }

    /**
     * Broadcasts the given signal through the antenna
     * @param signal The signal to send
     * @return Each signal sent, after the original has been sent through each AntennaElement
     */
    public List<Signal> sendSignal(Signal signal) {
        List<Signal> returnSignals = new ArrayList<>();
        for (AntennaElement antennaElement : antennaElements) {
            Signal broadcastSignal = new Signal(signal);
            broadcastSignal.changeUUID();
            Signal returnSignal = antennaElement.sendSignal(broadcastSignal);
            if (returnSignal != null) {
                returnSignals.add(returnSignal);
            }
        }
        return returnSignals;
    }

    public void receiveSignal(Signal signal) {

    }

    public boolean isPos(BlockPos pos) {
        return antennaActor.equals(pos);
    }

    protected void setPosition(Vec3 position) {
        this.position = position;
    }

    public Vec3 getPosition() {
        return position;
    }

    public void updateTooltip(List<Component> tooltip) {

    }

    public List<AntennaElement> getAntennaElements() {
        return antennaElements;
    }

    public void updateResonantAmplitude(Signal signal) {

    }
}
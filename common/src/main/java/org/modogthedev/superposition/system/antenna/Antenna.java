package org.modogthedev.superposition.system.antenna;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.util.SuperpositionMth;

import java.util.ArrayList;
import java.util.List;

public class Antenna {
    public Level level;
    public List<Signal> signals = new ArrayList<>();
    public BlockPos antennaActor;
    public boolean receiver;
    private float frequency = 0;
    private Vec3 position = new Vec3(0,0,0);

    public Antenna(BlockPos antennaActor, Level level) {
        this.antennaActor = antennaActor;
        this.level = level;
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

    public float getFrequency() {
        return frequency;
    }

    public void setFrequency(float frequency) {
        this.frequency = frequency;
    }
}
package org.modogthedev.superposition.compat.cc;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.modogthedev.superposition.blockentity.SignalActorBlockEntity;
import org.modogthedev.superposition.core.SuperpositionConstants;
import org.modogthedev.superposition.system.signal.Signal;

import java.util.List;

public class SignalActorPeripheral implements IPeripheral {
    private SignalActorBlockEntity signalActorBlockEntity;
    private Direction direction;

    public SignalActorPeripheral(SignalActorBlockEntity signalActorBlockEntity, Direction direction) {
        this.signalActorBlockEntity = signalActorBlockEntity;
        this.direction = direction;
    }


    @Override
    public String getType() {
        return "cable";
    }

    @LuaFunction
    public final void putString(String string) {
        Signal signal = createSignal();
        signal.encode(string);
        sendSignal(signal);
    }

    @LuaFunction
    public final void putInteger(int i) {
        Signal signal = createSignal();
        signal.encode(i);
        sendSignal(signal);
    }

    @LuaFunction
    public final void putFloat(float f) {
        Signal signal = createSignal();
        signal.encode(f);
        sendSignal(signal);
    }

    @LuaFunction
    public final String getString() {
        Signal signal = getSignal();
        if (signal != null && signal.getEncodedData() != null) {
            return signal.getEncodedData().stringValue();
        }
        return null;
    }

    @LuaFunction
    public final int getInt() {
        Signal signal = getSignal();
        if (signal != null && signal.getEncodedData() != null) {
            return signal.getEncodedData().intValue();
        }
        return 0;
    }

    @LuaFunction
    public final float getFloat() {
        Signal signal = getSignal();
        if (signal != null && signal.getEncodedData() != null) {
            return signal.getEncodedData().floatValue();
        }
        return 0;
    }

    @LuaFunction
    public final long getLong() {
        Signal signal = getSignal();
        if (signal != null && signal.getEncodedData() != null) {
            return signal.getEncodedData().longValue();
        }
        return 0;
    }

    @LuaFunction
    public final double getDouble() {
        Signal signal = getSignal();
        if (signal != null && signal.getEncodedData() != null) {
            return signal.getEncodedData().doubleValue();
        }
        return 0;
    }

    @LuaFunction
    public final boolean getBoolean() {
        Signal signal = getSignal();
        if (signal != null && signal.getEncodedData() != null) {
            return signal.getEncodedData().booleanValue();
        }
        return false;
    }

    @LuaFunction
    public Signal getSignal() {
        return signalActorBlockEntity.getSideSignal(direction);
    }

    public final Signal createSignal() {
        Vec3 center = signalActorBlockEntity.getBlockPos().getCenter();
        return new Signal(new Vector3d(center.x, center.y, center.z), signalActorBlockEntity.getLevel(), SuperpositionConstants.periphrealFrequency, 1, SuperpositionConstants.periphrealFrequency / 100000);
    }

    public final void sendSignal(Signal signal) {
        signalActorBlockEntity.putSignalsFace(new Object(), List.of(signal), direction.getOpposite());
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        if (other instanceof SignalActorPeripheral cablePeripheral) {
            return cablePeripheral.signalActorBlockEntity == signalActorBlockEntity;
        }
        return false;
    }
}

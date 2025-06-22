package org.modogthedev.superposition.compat.cc;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.modogthedev.superposition.system.cable.CablePassthroughManager;
import org.modogthedev.superposition.system.signal.Signal;

public class CablePeripheral implements IPeripheral {
    private final Level level;
    private final BlockPos pos;

    public CablePeripheral(Level level, BlockPos pos) {
        this.level = level;
        this.pos = pos;
    }

    public BlockPos getPos() {
        return pos;
    }

    @Override
    public String getType() {
        return "cable";
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
    public final boolean getBoolean() {
        Signal signal = getSignal();
        if (signal != null && signal.getEncodedData() != null) {
            return signal.getEncodedData().booleanValue();
        }
        return false;
    }

    @LuaFunction
    public final byte[] getByteArray() {
        Signal signal = getSignal();
        if (signal != null && signal.getEncodedData() != null) {
            return signal.getEncodedData().byteArrayValue();
        }
        return new byte[0];
    }

    @LuaFunction
    public final CompoundTag getCompoundTag() {
        Signal signal = getSignal();
        if (signal != null && signal.getEncodedData() != null) {
            return signal.getEncodedData().compoundTagData();
        }
        return new CompoundTag();
    }

    public Signal getSignal() {
        if (CablePassthroughManager.getSignalsFromBlock(level, pos) != null) {
            return CablePassthroughManager.getSignalsFromBlock(level, pos).getFirst();
        }
        return null;
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        if (other instanceof CablePeripheral cablePeripheral) {
            return cablePeripheral.getPos() == pos && cablePeripheral.level == level;
        }
        return false;
    }
}

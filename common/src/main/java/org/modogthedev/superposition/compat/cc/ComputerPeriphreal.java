package org.modogthedev.superposition.compat.cc;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.modogthedev.superposition.blockentity.ComputerBlockEntity;
import org.modogthedev.superposition.system.signal.data.EncodedData;

public class ComputerPeriphreal implements IPeripheral {
    private final Level level;
    private final BlockPos pos;
    private final ComputerBlockEntity computerBlockEntity;

    public ComputerPeriphreal(ComputerBlockEntity computerBlockEntity) {
        level = computerBlockEntity.getLevel();
        pos = computerBlockEntity.getBlockPos();
        this.computerBlockEntity = computerBlockEntity;
    }

    @LuaFunction
    public final void setString(String string) {
        setEncodedData(EncodedData.of(string));
    }

    @LuaFunction
    public final void setInt(int i) {
        setEncodedData(EncodedData.of(i));
    }

    @LuaFunction
    public final void setFloat(double f) {
        setEncodedData(EncodedData.of((float) f));
    }

    @LuaFunction
    public final void setBoolean(boolean bool) {
        setEncodedData(EncodedData.of(bool));
    }

    @LuaFunction
    public final void setByteArray(byte[] byteArray) {
        setEncodedData(EncodedData.of(byteArray));
    }

    @LuaFunction
    public final boolean setCompoundTag(String string) {
        CompoundTag tag = EncodedData.of(string).compoundTagData();
        if (tag != null) {
            setEncodedData(EncodedData.of(tag));
            return true;
        }
        setEncodedData(EncodedData.of(string));
        return false;
    }

    public void setEncodedData(EncodedData<?> encodedData) { // TODO: make it real
//        if (computerBlockEntity.getCard() instanceof SlaveCard card) {
//            card.setEncodedData(encodedData);
//        }
    }

    @Override
    public String getType() {
        return "slave_computer";
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        if (other instanceof ComputerPeriphreal computerPeriphreal) {
            return computerPeriphreal.pos == pos && computerPeriphreal.level == level;
        }
        return false;
    }
}

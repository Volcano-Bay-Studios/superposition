package org.modogthedev.superposition.util;

import net.minecraft.nbt.CompoundTag;
import org.modogthedev.superposition.system.signal.Signal;

import javax.annotation.Nullable;

public class DataHelper {
    @Nullable
    public static CompoundTag getTagValue(Signal signal) {
        if (signal != null && signal.getEncodedData() != null) {
            return signal.getEncodedData().compoundTagData();
        }
        return null;
    }

    @Nullable
    public static String getStringValue(Signal signal) {
        if (signal != null && signal.getEncodedData() != null) {
            return signal.getEncodedData().stringValue();
        }
        return null;
    }

    public static int getIntValue(Signal signal) {
        if (signal != null && signal.getEncodedData() != null) {
            return signal.getEncodedData().intValue();
        }
        return -1;
    }

    public static int getIntKey(Signal signal, String key) {
        CompoundTag tag = getTagValue(signal);
        if (tag != null && tag.contains(key, 3)) {
            return tag.getInt(key);
        }
        return -1;
    }

    @Nullable
    public static String getStringKey(Signal signal, String key) {
        CompoundTag tag = getTagValue(signal);
        if (tag != null && tag.contains(key, 8)) {
            return tag.getString(key);
        }
        return null;
    }
}

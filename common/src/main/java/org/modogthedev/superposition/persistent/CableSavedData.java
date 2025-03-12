package org.modogthedev.superposition.persistent;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;

public class CableSavedData extends SavedData {
    public static final String ID = "superposition_cable_data";

    public CableSavedData() {
        super();
    }



    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {

        // save here
        tag.putInt("hi",1);

        return tag;
    }

    public static CableSavedData load(CompoundTag tag) {
        CableSavedData data = new CableSavedData();
        System.out.println(data);
        // load here

        return data;
    }

    public static CableSavedData get(ServerLevel level) {
        CableSavedData data = level.getChunkSource().getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(CableSavedData::new,
                        (f, c) -> CableSavedData.load(f),
                        DataFixTypes.LEVEL),
                ID);
        return data;
    }
}

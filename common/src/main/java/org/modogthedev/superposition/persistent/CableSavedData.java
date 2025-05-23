package org.modogthedev.superposition.persistent;

import io.netty.buffer.Unpooled;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.system.cable.Cable;
import org.modogthedev.superposition.system.cable.CableManager;

import java.util.Map;
import java.util.UUID;

public class CableSavedData extends SavedData {
    public static final String ID = "superposition_cable_data";

    private ServerLevel level = null;

    public CableSavedData() {
        super();
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) { // This is cursed
        Superposition.LOGGER.info("Saving cables for level '"+ level+"'");
        Map<UUID, Cable> cableHashMap = CableManager.getCables(level);
        if (cableHashMap != null) {
            tag.putInt("count", cableHashMap.size());
            int i = 0;
            for (UUID uuid : cableHashMap.keySet()) {
                FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
                cableHashMap.get(uuid).toBytes(buf);
                tag.putUUID("uuid_"+i,uuid);
                byte[] bytes = new byte[buf.writerIndex()];
                buf.readBytes(bytes);
                tag.putByteArray(String.valueOf(i), bytes);
                i++;
            }
            // save here
        }
        return tag;
    }

    public static CableSavedData load(CompoundTag tag, ServerLevel level) {
        Superposition.LOGGER.info("Loading cables for level '"+ level+"'");
        CableSavedData data = new CableSavedData();
        int size = tag.getInt("count");
        for (int i = 0; i < size; i++) {
            UUID uuid = tag.getUUID("uuid_"+i);
            Cable cable = Cable.fromBytes(uuid,new FriendlyByteBuf(Unpooled.wrappedBuffer(tag.getByteArray(String.valueOf(i)))),level,true);
            CableManager.addCable(cable,level);
        }
        return data;
    }

    public static CableSavedData get(ServerLevel level) {
        CableSavedData data = level.getDataStorage().computeIfAbsent(new SavedData.Factory<>(CableSavedData::new,
                (c,f) -> load(c,level),
                DataFixTypes.LEVEL),
                ID);

        data.level = level;
        data.setDirty(true); //TODO: only make cable data dirty if cables have changed

        return data;
    }
}

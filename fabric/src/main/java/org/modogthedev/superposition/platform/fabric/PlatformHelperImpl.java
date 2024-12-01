package org.modogthedev.superposition.platform.fabric;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.world.item.CreativeModeTab;
import org.modogthedev.superposition.platform.PlatformHelper;

public class PlatformHelperImpl implements PlatformHelper {

    @Override
    public CreativeModeTab.Builder creativeTabBuilder() {
        return FabricItemGroup.builder();
    }
}

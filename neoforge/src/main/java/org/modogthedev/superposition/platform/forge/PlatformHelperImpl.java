package org.modogthedev.superposition.platform.forge;

import net.minecraft.world.item.CreativeModeTab;
import org.modogthedev.superposition.platform.PlatformHelper;

public class PlatformHelperImpl implements PlatformHelper {

    @Override
    public CreativeModeTab.Builder creativeTabBuilder() {
        return CreativeModeTab.builder();
    }
}

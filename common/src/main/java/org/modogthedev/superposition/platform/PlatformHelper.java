package org.modogthedev.superposition.platform;

import net.minecraft.world.item.CreativeModeTab;

import java.util.ServiceLoader;

public interface PlatformHelper {

    PlatformHelper INSTANCE = ServiceLoader.load(PlatformHelper.class).findFirst().orElseThrow(() -> new RuntimeException("Expected platform"));

    CreativeModeTab.Builder creativeTabBuilder();
}

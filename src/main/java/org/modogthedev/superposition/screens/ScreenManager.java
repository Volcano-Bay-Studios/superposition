package org.modogthedev.superposition.screens;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import org.modogthedev.superposition.SuperpositionClient;

public class ScreenManager {
    private static SignalGeneratorScreen signalGeneratorScreen;
    private static ModulatorScreen modulatorScreen;
    public static void openSignalGenerator(BlockPos pos) {
        signalGeneratorScreen = new SignalGeneratorScreen(Component.literal("Signal Generator"), pos);
        SuperpositionClient.setScreen(signalGeneratorScreen);
    }
    public static void openModulatorScreen(BlockPos pos) {
        modulatorScreen = new ModulatorScreen(Component.literal("Modulator"), pos);
        SuperpositionClient.setScreen(modulatorScreen);
    }
}

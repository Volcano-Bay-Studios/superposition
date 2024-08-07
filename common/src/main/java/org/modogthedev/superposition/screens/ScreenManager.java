package org.modogthedev.superposition.screens;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import org.modogthedev.superposition.SuperpositionClient;
import org.modogthedev.superposition.item.FilterItem;

public class ScreenManager {
    private static SignalGeneratorScreen signalGeneratorScreen;
    private static AmplifierScreen amplifierScreen;
    private static FilterScreen filterScreen;

    public static void openSignalGenerator(BlockPos pos) {
        signalGeneratorScreen = new SignalGeneratorScreen(Component.literal("Signal Generator"), pos);
        SuperpositionClient.setScreen(signalGeneratorScreen);
    }

    public static void openModulatorScreen(BlockPos pos) {
        amplifierScreen = new AmplifierScreen(Component.literal("Modulator"), pos);
        SuperpositionClient.setScreen(amplifierScreen);
    }
    public static void openFilterScreen(FilterItem.FilterType type, float value1, float value2) {
        filterScreen = new FilterScreen(Component.literal("Filter"), type,value1,value2);
        SuperpositionClient.setScreen(filterScreen);
    }
}

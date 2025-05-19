package org.modogthedev.superposition.screens;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import org.modogthedev.superposition.SuperpositionClient;
import org.modogthedev.superposition.system.cards.Card;
import org.modogthedev.superposition.system.filter.Filter;

public class ScreenManager {
    private static SignalGeneratorScreen signalGeneratorScreen;
    private static AmplifierScreen amplifierScreen;
    private static FilterScreen filterScreen;
    private static InscriberScreen inscriberScreen;

    public static void openSignalGenerator(BlockPos pos) {
        signalGeneratorScreen = new SignalGeneratorScreen(Component.literal("Signal Generator"), pos);
        SuperpositionClient.setScreen(signalGeneratorScreen);
    }

    public static void openModulatorScreen(BlockPos pos) {
        amplifierScreen = new AmplifierScreen(Component.literal("Modulator"), pos);
        SuperpositionClient.setScreen(amplifierScreen);
    }

    public static void openFilterScreen(Filter type, BlockPos pos, boolean editMode) {
        if (!type.openCustomScreen()) {
            filterScreen = new FilterScreen(Component.literal("Filter"), type, pos, editMode);
            SuperpositionClient.setScreen(filterScreen);
        }
    }

    public static void openInscriber(Card card) {
        inscriberScreen = new InscriberScreen(card);
        SuperpositionClient.setScreen(inscriberScreen);
    }
}

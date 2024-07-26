package org.modogthedev.superposition.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import org.modogthedev.superposition.SuperpositionClient;

public class SuperpositionFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        SuperpositionClient.init();
    }
}

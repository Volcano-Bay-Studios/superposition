package org.modogthedev.superposition.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.server.level.ServerPlayer;

public class PlatformHelper {
    @ExpectPlatform
    public static void register() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static double getPlayerReach(ServerPlayer player) {
        throw new AssertionError();
    }
}

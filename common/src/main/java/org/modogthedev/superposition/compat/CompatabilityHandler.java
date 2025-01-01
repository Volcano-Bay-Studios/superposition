package org.modogthedev.superposition.compat;

import java.util.function.Supplier;

public class CompatabilityHandler {
    public enum Mods {
        COMPUTERCRAFT;

        public boolean isLoaded = false;

        public void executeIfInstalled(Supplier<Runnable> toRun) {
            if (isLoaded) {
                toRun.get().run();
            }
        }
    }
}

package org.modogthedev.superposition.compat;

public class CompatabilityHandler {
    public enum Mods {
        COMPUTERCRAFT;

        public boolean isLoaded = false;

        public void executeIfInstalled(Runnable toRun) {
            if (isLoaded) {
                toRun.run();
            }
        }
    }
}

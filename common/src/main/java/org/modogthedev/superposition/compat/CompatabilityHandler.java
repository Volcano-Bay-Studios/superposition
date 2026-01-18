package org.modogthedev.superposition.compat;

public class CompatabilityHandler {
    public enum Mod {
        COMPUTERCRAFT,
        SABLE;

        public boolean isLoaded = false;

        public void executeIfInstalled(Runnable toRun) {
            if (isLoaded) {
                toRun.run();
            }
        }
    }
}

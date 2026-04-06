package org.modogthedev.superposition.system.card.actions.configuration;

import net.minecraft.network.chat.Component;

public class PortConfiguration extends StringConfiguration {
    public PortConfiguration(Component title) {
        super(title);
    }

    @Override
    public int maxLength() {
        return 20;
    }
}

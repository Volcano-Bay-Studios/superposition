package org.modogthedev.superposition.system.cards.config;

import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class CardConfig {
    public List<CardConfigPart> parts = new ArrayList<>();
    public void addComment(Component component) {
        parts.add(new CardConfigPart(component.getString()));
    }
}

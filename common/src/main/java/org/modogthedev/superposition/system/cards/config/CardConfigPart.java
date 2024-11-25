package org.modogthedev.superposition.system.cards.config;

public class CardConfigPart {
    public CardConfigPart(String name) {
        this.part = name;
    }
    public String part;
    public boolean isSelectable() {
        return false;
    }
}

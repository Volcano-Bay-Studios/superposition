package org.modogthedev.superposition.util;

public interface EditableTooltip {
    String getText();
    void replaceText(String string);
    default void addText(String string) { replaceText(getText()+string); }
    default String prefix() {return "Output - ";}
}

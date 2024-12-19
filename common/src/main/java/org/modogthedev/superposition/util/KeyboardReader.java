package org.modogthedev.superposition.util;

public interface KeyboardReader {
    void acceptString(String s);
    void addString(String s);
    String getCurrentString(String s);
}

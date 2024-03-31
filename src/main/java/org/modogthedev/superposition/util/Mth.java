package org.modogthedev.superposition.util;

public class Mth {
    public static float getFromRange(float OldMax, float OldMin, float NewMax, float NewMin, float OldValue) {
        float OldRange = (OldMax - OldMin);
        float NewRange = (NewMax - NewMin);
        return  (((OldValue - OldMin) * NewRange) / OldRange) + NewMin;
    }
}

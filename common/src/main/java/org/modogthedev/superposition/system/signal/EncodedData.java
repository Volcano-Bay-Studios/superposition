package org.modogthedev.superposition.system.signal;

public class EncodedData {
    DataTypes type;
    String stringData;
    int intData;
    double doubleData;
    float floatData;

    public enum DataTypes {
        STRING,
        INT,
        DOUBLE,
        FLOAT
    }
}

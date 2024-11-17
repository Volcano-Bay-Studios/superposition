package org.modogthedev.superposition.system.signal.data;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class EncodedData extends ArrayList<Serializable> {

    public byte[] encode() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (ObjectOutputStream out = new ObjectOutputStream(outputStream)) {
            out.writeInt(size());
            for (Serializable signalData : this) {
                out.writeObject(signalData);
            }
            out.flush();
            return outputStream.toByteArray();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static EncodedData deserialize(byte[] bytes) {
        EncodedData readData = new EncodedData();
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);

        try (ObjectInputStream in = new ObjectInputStream(bis)) {
            int size = in.readInt();
            for (int i = 0; i < size; i++) {
                readData.add((Serializable) in.readObject());
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        return readData;
    }

}

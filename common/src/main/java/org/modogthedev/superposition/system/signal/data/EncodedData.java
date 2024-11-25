package org.modogthedev.superposition.system.signal.data;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class EncodedData<T extends Serializable> {
    public EncodedData(T obj) {
        this.obj = obj;
    }

    T obj;

    public T getObj() {
        return obj;
    }

    public byte[] encode() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (ObjectOutputStream out = new ObjectOutputStream(outputStream)) {
            out.writeObject(obj);
            out.flush();
            return outputStream.toByteArray();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static EncodedData deserialize(byte[] bytes) {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);

        try (ObjectInputStream in = new ObjectInputStream(bis)) {
            Serializable object = (Serializable) in.readObject();
            return new EncodedData<>(object);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}

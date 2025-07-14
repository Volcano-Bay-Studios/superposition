package org.modogthedev.superposition.system.signal.data;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.apache.commons.lang3.math.NumberUtils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

public sealed interface EncodedData<T> extends Cloneable {

    final class BoolData implements EncodedData<Boolean> {

        private final boolean value;
        private String asString;

        public BoolData(boolean value) {
            this.value = value;
        }

        @Override
        public boolean booleanValue() {
            return this.value;
        }

        @Override
        public Boolean value() {
            return this.value;
        }

        @Override
        public Number numberValue() {
            return this.value ? 1 : 0;
        }

        @Override
        public String stringValue() {
            if (this.asString == null) {
                this.asString = Boolean.toString(this.value);
            }
            return this.asString;
        }

        @Override
        public byte[] byteArrayValue() {
            byte[] bytes = new byte[1];
            bytes[0] = (byte) (this.value ? 1 : 0);
            return bytes;
        }

        @Override
        public Type type() {
            return Type.BOOL;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }

            BoolData boolData = (BoolData) o;
            return this.value == boolData.value;
        }

        @Override
        public int hashCode() {
            return Boolean.hashCode(this.value);
        }

        @Override
        public String toString() {
            return "BoolData[value=" + this.value + ']';
        }

        @Override
        public void writeTag(CompoundTag tag, String key) {
            tag.putBoolean(key, value);
        }
    }

    final class IntData implements EncodedData<Integer> {

        private final int value;
        private String asString;

        public IntData(int value) {
            this.value = value;
        }

        @Override
        public Integer value() {
            return this.value;
        }

        @Override
        public boolean booleanValue() {
            return this.value != 0;
        }

        @Override
        public int intValue() {
            return this.value;
        }

        @Override
        public Number numberValue() {
            return this.value;
        }

        @Override
        public String stringValue() {
            if (this.asString == null) {
                this.asString = Integer.toString(this.value);
            }
            return this.asString;
        }

        @Override
        public byte[] byteArrayValue() {
            return ByteBuffer.allocate(4).putInt(this.value).array();
        }

        @Override
        public Type type() {
            return Type.INT;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj == null || obj.getClass() != this.getClass()) {
                return false;
            }
            var that = (IntData) obj;
            return Objects.equals(this.value, that.value);
        }

        @Override
        public int hashCode() {
            return this.value;
        }

        @Override
        public String toString() {
            return "IntData[value=" + this.value + ']';
        }

        @Override
        public void writeTag(CompoundTag tag, String key) {
            tag.putInt(key, value);
        }
    }

    final class FloatData implements EncodedData<Float> {

        private final float value;
        private String asString;

        public FloatData(float value) {
            this.value = value;
        }

        @Override
        public Float value() {
            return this.value;
        }

        @Override
        public boolean booleanValue() {
            return Float.compare(this.value, 0.0F) != 0;
        }

        @Override
        public float floatValue() {
            return this.value;
        }

        @Override
        public Number numberValue() {
            return this.value;
        }

        @Override
        public String stringValue() {
            if (this.asString == null) {
                this.asString = Float.toString(this.value);
            }
            return this.asString;
        }

        @Override
        public byte[] byteArrayValue() {
            return ByteBuffer.allocate(4).putFloat(this.value).array();
        }

        @Override
        public Type type() {
            return Type.FLOAT;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }

            FloatData floatData = (FloatData) o;
            return Float.compare(this.value, floatData.value) == 0;
        }

        @Override
        public int hashCode() {
            return Float.hashCode(this.value);
        }

        @Override
        public String toString() {
            return "FloatData[value=" + this.value + ']';
        }

        @Override
        public void writeTag(CompoundTag tag, String key) {
            tag.putFloat(key, value);
        }
    }

    final class StringData implements EncodedData<String> {

        private final String value;
        private Number asNumber;

        public StringData(String value) {
            this.value = value;
        }

        @Override
        public String value() {
            return this.value;
        }

        @Override
        public boolean booleanValue() {
            return !this.value.isBlank();
        }

        @Override
        public Number numberValue() {
            if (this.asNumber == null) {
                try {
                    this.asNumber = NumberUtils.createNumber(this.value);
                } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                    this.asNumber = 0;
                }
            }
            return this.asNumber;
        }

        @Override
        public String stringValue() {
            return this.value;
        }

        @Override
        public byte[] byteArrayValue() {
            return this.value.getBytes(StandardCharsets.UTF_8);
        }

        @Override
        public Type type() {
            return Type.STRING;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj == null || obj.getClass() != this.getClass()) {
                return false;
            }
            var that = (StringData) obj;
            return Objects.equals(this.value, that.value);
        }

        @Override
        public int hashCode() {
            return this.value.hashCode();
        }

        @Override
        public String toString() {
            return "StringData[value=" + this.value + ']';
        }

        @Override
        public void writeTag(CompoundTag tag, String key) {
            tag.putString(key, value);
        }
    }

    final class CompoundTagData implements EncodedData<CompoundTag> {

        private final CompoundTag value;
        private Number asNumber;

        public CompoundTagData(CompoundTag value) {
            this.value = value;
        }

        @Override
        public CompoundTag value() {
            return this.value;
        }

        @Override
        public boolean booleanValue() {
            return !this.value.getAsString().isBlank();
        }

        @Override
        public Number numberValue() {
            if (this.asNumber == null) {
                try {
                    this.asNumber = NumberUtils.createNumber(this.value.getAsString());
                } catch (NumberFormatException e) {
                    this.asNumber = 0;
                }
            }
            return this.asNumber;
        }

        @Override
        public String stringValue() {
            return this.value.getAsString();
        }

        @Override
        public byte[] byteArrayValue() {
            return stringValue().getBytes(StandardCharsets.UTF_8);
        }

        @Override
        public CompoundTag compoundTagData() {
            return value.copy();
        }

        @Override
        public Type type() {
            return Type.COMPOUND_TAG;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj == null || obj.getClass() != this.getClass()) {
                return false;
            }
            var that = (CompoundTagData) obj;
            return this.value.equals(that.value);
        }

        @Override
        public int hashCode() {
            return this.value.hashCode();
        }

        @Override
        public String toString() {
            return "CompoundTagData[value=" + this.value + ']';
        }

        @Override
        public void writeTag(CompoundTag tag, String key) {
            tag.put(key, value);
        }
    }

    final class ByteArrayData implements EncodedData<byte[]> {

        private final byte[] value;

        public ByteArrayData(byte[] value) {
            this.value = value;
        }

        @Override
        public byte[] value() {
            return this.value;
        }

        @Override
        public boolean booleanValue() {
            return false;
        }

        @Override
        public int intValue() {
            return 0;
        }

        @Override
        public Number numberValue() {
            return 0;
        }

        @Override
        public String stringValue() {
            return new String(this.value, StandardCharsets.UTF_8);
        }

        @Override
        public byte[] byteArrayValue() {
            return value;
        }

        @Override
        public Type type() {
            return Type.INT;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj == null || obj.getClass() != this.getClass()) {
                return false;
            }
            var that = (ByteArrayData) obj;
            return Arrays.equals(this.value, that.value);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(this.value);
        }

        @Override
        public String toString() {
            return "IntData[value=" + Arrays.toString(this.value) + ']';
        }

        public void writeTag(CompoundTag tag, String key) {
            tag.putByteArray(key, value);
        }
    }

    static EncodedData<Boolean> of(boolean value) {
        return new BoolData(value);
    }

    static EncodedData<Integer> of(int value) {
        return new IntData(value);
    }

    static EncodedData<Float> of(float value) {
        return new FloatData(value);
    }

    static EncodedData<String> of(String value) {
        return new StringData(value);
    }

    static EncodedData<CompoundTag> of(CompoundTag value) {
        return new CompoundTagData(value);
    }

    static EncodedData<byte[]> of(byte[] value) {
        return new ByteArrayData(value);
    }

    T value();

    boolean booleanValue();

    Number numberValue();

    byte[] byteArrayValue();

    /**
     * Dumps the data into the given tag with the given key
     *
     * @param tag
     * @param key
     */
    void writeTag(CompoundTag tag, String key);

    default CompoundTag compoundTagData() {
        String string = this.stringValue();
        CompoundTag tag;
        try {
            tag = TagParser.parseTag(string);
        } catch (CommandSyntaxException e) {
            tag = new CompoundTag();
        }
        return tag;
    }

    default EncodedData<?> getTagKey(String key) {
        CompoundTag tag = compoundTagData();
        if (tag != null && tag.contains(key)) {
            if (tag.contains(key, 8)) {
                return EncodedData.of(tag.getString(key));
            }
            if (tag.contains(key, 99)) {
                return EncodedData.of(tag.getInt(key));
            }
            if (tag.contains(key, 99)) {
                return EncodedData.of(tag.getFloat(key));
            }
            if (tag.contains(key, 10)) {
                return EncodedData.of(tag.getCompound(key));
            }
            if (tag.contains(key)) {
                return EncodedData.of(tag.getBoolean(key));
            }
        }
        return EncodedData.of("null");
    }

    default int intValue() {
        return this.numberValue().intValue();
    }

    default long longValue() {
        return this.numberValue().longValue();
    }

    default float floatValue() {
        return this.numberValue().floatValue();
    }

    default double doubleValue() {
        return this.numberValue().doubleValue();
    }

    String stringValue();

    Type type();


    enum Type {
        BOOL(ByteBufCodecs.BOOL.map(BoolData::new, data -> ((BoolData) data).value)),
        INT(ByteBufCodecs.INT.map(IntData::new, data -> ((IntData) data).value)),
        FLOAT(ByteBufCodecs.FLOAT.map(FloatData::new, data -> ((FloatData) data).value)),
        STRING(ByteBufCodecs.STRING_UTF8.map(StringData::new, data -> ((StringData) data).value)),
        COMPOUND_TAG(ByteBufCodecs.COMPOUND_TAG.map(CompoundTagData::new, data -> ((CompoundTagData) data).value)),
        BYTE_ARRAY(ByteBufCodecs.BYTE_ARRAY.map(ByteArrayData::new, data -> ((ByteArrayData) data).value));

        private final StreamCodec<ByteBuf, EncodedData<?>> codec;

        Type(StreamCodec<ByteBuf, EncodedData<?>> codec) {
            this.codec = codec;
        }

        public StreamCodec<ByteBuf, EncodedData<?>> getCodec() {
            return this.codec;
        }
    }
}

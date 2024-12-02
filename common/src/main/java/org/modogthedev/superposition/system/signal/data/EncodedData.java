package org.modogthedev.superposition.system.signal.data;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Objects;

public sealed interface EncodedData<T> {

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
                } catch (NumberFormatException e) {
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

    T value();

    boolean booleanValue();

    Number numberValue();

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
        STRING(ByteBufCodecs.STRING_UTF8.map(StringData::new, data -> ((StringData) data).value));

        private final StreamCodec<ByteBuf, EncodedData<?>> codec;

        Type(StreamCodec<ByteBuf, EncodedData<?>> codec) {
            this.codec = codec;
        }

        public StreamCodec<ByteBuf, EncodedData<?>> getCodec() {
            return this.codec;
        }
    }
}

package org.modogthedev.superposition.system.signal.data;

import com.ezylang.evalex.Expression;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.math.NumberUtils;
import org.modogthedev.superposition.core.SuperpositionConstants;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

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
        public void asExpressionVariable(String param, Expression expression) {
            expression.with(param, this.value);
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
                if (Objects.equals(value, "false")) {
                    this.asNumber = 0;
                } else if (Objects.equals(value, "true")) {
                    this.asNumber = 1;
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
        public void asExpressionVariable(String param, Expression expression) {
            try {
                float number = Float.parseFloat(this.value);
                if (Float.toString(number).equals(this.value) || Integer.toString((int) number).equals(this.value)) {
                    expression.with(param, number);
                    return;
                }
            } catch (Exception ignored) {
            }
            expression.with(param, this.value);
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
        public void asExpressionVariable(String param, Expression expression) {
            expression.with(param, stringValue());
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
            try {
                return ByteBuffer.wrap(value).getInt();
            } catch (Exception ignored) {
            }
            return 0;
        }

        @Override
        public Number numberValue() {
            try {
                return ByteBuffer.wrap(value).getInt();
            } catch (Exception ignored) {
            }
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
            return Type.BYTE_ARRAY;
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
            return "ByteArrayData[value=" + Arrays.toString(this.value) + ']';
        }

        public void writeTag(CompoundTag tag, String key) {
            tag.putByteArray(key, value);
        }
    }

    /**
     * A handle to a byte stream. The frames of the stream are networked separately from the signal, see {@link org.modogthedev.superposition.system.stream.StreamManager}.
     */
    final class StreamData implements EncodedData<StreamData.Handle> {

        /**
         * @param id    The stream this handle points to
         * @param delay How many ticks behind the source this handle should be read
         */
        public record Handle(UUID id, int delay) {
        }

        private final Handle value;
        private String asString;

        public StreamData(Handle value) {
            this.value = value;
        }

        @Override
        public Handle value() {
            return this.value;
        }

        public UUID id() {
            return this.value.id;
        }

        public int delay() {
            return this.value.delay;
        }

        /**
         * @param ticks The amount of ticks to delay this stream by
         * @return A new handle to the same stream that is read further behind the source
         */
        public StreamData delayed(int ticks) {
            return new StreamData(new Handle(this.value.id, Mth.clamp(this.value.delay + ticks, 0, SuperpositionConstants.maxStreamDelay)));
        }

        @Override
        public boolean booleanValue() {
            return true;
        }

        @Override
        public Number numberValue() {
            return this.value.delay;
        }

        @Override
        public String stringValue() {
            if (this.asString == null) {
                this.asString = "stream:" + this.value.id;
            }
            return this.asString;
        }

        @Override
        public byte[] byteArrayValue() {
            return ByteBuffer.allocate(16).putLong(this.value.id.getMostSignificantBits()).putLong(this.value.id.getLeastSignificantBits()).array();
        }

        @Override
        public Type type() {
            return Type.STREAM;
        }

        @Override
        public void asExpressionVariable(String param, Expression expression) {
            expression.with(param, stringValue());
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj == null || obj.getClass() != this.getClass()) {
                return false;
            }
            var that = (StreamData) obj;
            return this.value.equals(that.value);
        }

        @Override
        public int hashCode() {
            return this.value.hashCode();
        }

        @Override
        public String toString() {
            return "StreamData[value=" + this.value + ']';
        }

        @Override
        public void writeTag(CompoundTag tag, String key) {
            tag.putString(key, stringValue());
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

    static EncodedData<StreamData.Handle> of(UUID id, int delay) {
        return new StreamData(new StreamData.Handle(id, delay));
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

    default void asExpressionVariable(String param, Expression expression) {
        expression.with(param, floatValue());
    }

    String stringValue();

    Type type();


    enum Type {
        BOOL(ByteBufCodecs.BOOL.map(BoolData::new, data -> ((BoolData) data).value)),
        INT(ByteBufCodecs.INT.map(IntData::new, data -> ((IntData) data).value)),
        FLOAT(ByteBufCodecs.FLOAT.map(FloatData::new, data -> ((FloatData) data).value)),
        STRING(ByteBufCodecs.STRING_UTF8.map(StringData::new, data -> ((StringData) data).value)),
        COMPOUND_TAG(ByteBufCodecs.COMPOUND_TAG.map(CompoundTagData::new, data -> ((CompoundTagData) data).value)),
        BYTE_ARRAY(ByteBufCodecs.BYTE_ARRAY.map(ByteArrayData::new, data -> ((ByteArrayData) data).value)),
        STREAM(StreamCodec.composite(UUIDUtil.STREAM_CODEC, StreamData.Handle::id, ByteBufCodecs.VAR_INT, StreamData.Handle::delay, StreamData.Handle::new).map(StreamData::new, data -> ((StreamData) data).value));

        private final StreamCodec<ByteBuf, EncodedData<?>> codec;

        Type(StreamCodec<ByteBuf, EncodedData<?>> codec) {
            this.codec = codec;
        }

        public StreamCodec<ByteBuf, EncodedData<?>> getCodec() {
            return this.codec;
        }
    }
}

package org.modogthedev.superposition.system.signal.data;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public sealed interface EncodedData<T> {

    record IntData(Integer value) implements EncodedData<Integer> {

        @Override
        public Type type() {
            return Type.INT;
        }
    }

    record StringData(String value) implements EncodedData<String> {

        @Override
        public Type type() {
            return Type.STRING;
        }
    }

    static EncodedData<Integer> of(int value) {
        return new IntData(value);
    }

    static EncodedData<String> of(String value) {
        return new StringData(value);
    }

    T value();

    Type type();

    enum Type {
        INT(ByteBufCodecs.INT.map(IntData::new, data -> ((IntData) data).value)),
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

package org.modogthedev.superposition.system.sound;

import io.github.jaredmdobson.concentus.OpusApplication;
import io.github.jaredmdobson.concentus.OpusDecoder;
import io.github.jaredmdobson.concentus.OpusEncoder;
import io.github.jaredmdobson.concentus.OpusException;
import io.github.jaredmdobson.concentus.OpusSignal;

import javax.sound.sampled.AudioFormat;

/**
 * The format every audio stream uses. One frame is 20 milliseconds of audio.
 */
public class OpusCodec {
    public static final int SAMPLE_RATE = 48000;
    public static final int FRAME_SIZE = 960;
    public static final int BITRATE = 24000;
    public static final AudioFormat FORMAT = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);

    public static OpusEncoder createEncoder() throws OpusException {
        OpusEncoder encoder = new OpusEncoder(SAMPLE_RATE, 1, OpusApplication.OPUS_APPLICATION_VOIP);
        encoder.setBitrate(BITRATE);
        encoder.setSignalType(OpusSignal.OPUS_SIGNAL_VOICE);
        return encoder;
    }

    public static OpusDecoder createDecoder() throws OpusException {
        return new OpusDecoder(SAMPLE_RATE, 1);
    }
}

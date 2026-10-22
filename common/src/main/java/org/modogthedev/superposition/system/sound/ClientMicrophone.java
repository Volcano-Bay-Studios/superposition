package org.modogthedev.superposition.system.sound;

import foundry.veil.api.network.VeilPacketManager;
import io.github.jaredmdobson.concentus.OpusEncoder;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALC11;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.core.SuperpositionConstants;
import org.modogthedev.superposition.networking.packet.StreamFrameC2SPacket;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

/**
 * Captures the players microphone and sends it to the server while something in range has requested it this tick.
 */
public class ClientMicrophone {
    private static final double SILENCE_THRESHOLD = 250;
    private static final int SILENCE_HANGOVER = 15;

    public static volatile boolean voicechatConnected = false;

    private static boolean requested = false;
    private static volatile boolean capturing = false;
    private static Thread thread;
    private static boolean failed = false;
    private static volatile int captured = 0;
    private static volatile int sent = 0;
    private static volatile int level = 0;

    /**
     * Requests that the microphone is captured if the player is in range of the given position. This must be called every tick.
     *
     * @param pos The position of something that listens to the player
     */
    public static void request(Vec3 pos) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null && minecraft.player.getEyePosition().distanceTo(pos) < SuperpositionConstants.microphoneRange) {
            requested = true;
        }
    }

    public static void tick() {
        capturing = requested && !voicechatConnected && !failed;
        requested = false;
        if (capturing && (thread == null || !thread.isAlive())) {
            thread = new Thread(ClientMicrophone::capture, "Superposition Microphone");
            thread.setDaemon(true);
            thread.start();
        }
    }

    public static void stop() {
        requested = false;
        capturing = false;
    }

    public static String getDebugInfo() {
        String info = "microphone " + (capturing ? "capturing" : voicechatConnected ? "left to voice chat" : failed ? "failed" : "closed") + ", " + captured + " frames captured, " + sent + " sent, level " + level;
        captured = 0;
        sent = 0;
        return info;
    }

    private static void capture() {
        long device = ALC11.alcCaptureOpenDevice((ByteBuffer) null, OpusCodec.SAMPLE_RATE, AL10.AL_FORMAT_MONO16, OpusCodec.FRAME_SIZE * 10);
        if (device == 0) {
            Superposition.LOGGER.warn("Failed to open a microphone, audio will not be captured");
            failed = true;
            return;
        }
        try {
            OpusEncoder encoder = OpusCodec.createEncoder();
            ShortBuffer buffer = BufferUtils.createShortBuffer(OpusCodec.FRAME_SIZE);
            short[] samples = new short[OpusCodec.FRAME_SIZE];
            byte[] encoded = new byte[SuperpositionConstants.maxStreamFrameSize];
            int hangover = 0;

            ALC11.alcCaptureStart(device);
            while (capturing) {
                if (ALC10.alcGetInteger(device, ALC11.ALC_CAPTURE_SAMPLES) < OpusCodec.FRAME_SIZE) {
                    Thread.sleep(5);
                    continue;
                }
                buffer.clear();
                ALC11.alcCaptureSamples(device, buffer, OpusCodec.FRAME_SIZE);
                buffer.get(samples);

                double sum = 0;
                for (short sample : samples) {
                    sum += sample * sample;
                }
                captured++;
                level = (int) Math.sqrt(sum / samples.length);
                if (level > SILENCE_THRESHOLD) {
                    hangover = SILENCE_HANGOVER;
                } else if (hangover-- <= 0) {
                    continue;
                }

                int length = encoder.encode(samples, 0, OpusCodec.FRAME_SIZE, encoded, 0, encoded.length);
                StreamFrameC2SPacket packet = new StreamFrameC2SPacket(Arrays.copyOf(encoded, length));
                sent++;
                Minecraft.getInstance().execute(() -> {
                    if (Minecraft.getInstance().getConnection() != null) {
                        VeilPacketManager.server().sendPacket(packet);
                    }
                });
            }
        } catch (InterruptedException ignored) {
        } catch (Exception e) {
            Superposition.LOGGER.error("Failed to capture microphone", e);
            failed = true;
        } finally {
            ALC11.alcCaptureStop(device);
            ALC11.alcCaptureCloseDevice(device);
        }
    }
}

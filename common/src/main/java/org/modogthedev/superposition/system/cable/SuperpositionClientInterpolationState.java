package org.modogthedev.superposition.system.cable;

import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.ApiStatus;
import org.modogthedev.superposition.core.SuperpositionConstants;
import org.modogthedev.superposition.system.cable.rope_system.RopeNode;

import java.util.function.Consumer;

/**
 * TODO: there's too large of a window that we're okay with being within for latency / delay.
 */
public class SuperpositionClientInterpolationState {
    public static final SuperpositionClientInterpolationState INSTANCE = new SuperpositionClientInterpolationState();
    public static final boolean RENDER_INTERPOLATION_BOUNDS = false;

    private final Minecraft minecraft = Minecraft.getInstance();

    /**
     * The most recent tick we have received from the server
     */
    private double mostRecentTick = -1;

    /**
     * If we have received any update so far
     */
    private boolean receivedFirstUpdate;

    /**
     * The current stepping interpolation tick. This *should* be as aligned as possible with the server tick,
     * so that we can step back by the delay in ticks to use as a pointer for the snapshot interpolation.
     */
    private double interpolationTick;

    /**
     * The running estimate we have of the server tick speed, as a multiplier of the 20tps expected
     */
    private double estimatedServerTickSpeed;

    /**
     * The latest information from the server we have on the spacing between the latest server update and the previous one
     */
    private float serverMsFromLastUpdate;

    /**
     * If we should be receiving consistent updates from the server regarding the interpolation tick
     */
    private boolean stopped = true;

    private double latestDelay;
    public double mostRecentInterpolationTick;
    public double lastInterpolationTick;

    public void tick() {
        if (!this.receivedFirstUpdate) {
            return;
        }

        final float rate = this.minecraft.level.tickRateManager().tickrate();
        final float expectedMsBetween = 1000.0f / rate;

        if (!this.stopped) {
            this.estimatedServerTickSpeed = Mth.lerp(0.05, this.estimatedServerTickSpeed, expectedMsBetween / Math.max(1, this.serverMsFromLastUpdate));
        }

        this.interpolationTick += this.estimatedServerTickSpeed;
        this.interpolationTick = Mth.clamp(this.interpolationTick, this.mostRecentTick - this.getInterpolationDelay(), this.mostRecentTick + 1.5);

        this.latestDelay = this.mostRecentTick - this.interpolationTick + this.getInterpolationDelay();

        this.lastInterpolationTick = this.mostRecentInterpolationTick;
        this.mostRecentInterpolationTick = this.getTickPointer();
    }

    /**
     * The interpolation tick at which we are sampling from the snapshot buffers at
     */
    public double getTickPointer() {
        return this.interpolationTick - this.getInterpolationDelay();
    }

    @ApiStatus.Internal
    public void addDebugInfo(final Consumer<String> consumer) {
        consumer.accept(String.format("Delay: %.2ft", this.latestDelay));
        consumer.accept(String.format("Estimated Send-rate: %.2ft", this.estimatedServerTickSpeed));

        if (this.interpolationTick - this.getInterpolationDelay() > this.mostRecentTick) {
            consumer.accept(ChatFormatting.RED + "Past most-recent tick");
        }

        consumer.accept("Interpolation " + (this.stopped ? "stopped" : "running"));
    }

    public double getInterpolationDelay() {
        return SuperpositionConstants.interpolationDelay;
    }

    public void receiveInfo(final int msSinceLast, final int gameTick, final boolean stopped) {
        if (gameTick < this.mostRecentTick) return;

        if (!this.receivedFirstUpdate || this.stopped && !stopped) {
            this.interpolationTick = gameTick;
            this.estimatedServerTickSpeed = 1.0f;

            this.receivedFirstUpdate = true;
        }

        this.stopped = stopped;
        this.mostRecentTick = gameTick;
        this.serverMsFromLastUpdate = msSinceLast;
    }

    public boolean isStopped() {
        return this.stopped;
    }
}

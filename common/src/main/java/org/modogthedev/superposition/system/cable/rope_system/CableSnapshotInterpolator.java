package org.modogthedev.superposition.system.cable.rope_system;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.Mth;
import org.joml.Vector3d;
import org.joml.Vector3dc;


/**
 * Manages snapshot interpolation for client sub-levels.
 */
public class CableSnapshotInterpolator {

    /**
     * The buffer of snapshots to interpolate between
     */
    public final ObjectArrayList<Snapshot> buffer = new ObjectArrayList<>();
    /**
     * The current running interpolated snapshot
     */
    private final Vector3d runningSnapshot = new Vector3d();
    private boolean stopped;

    public CableSnapshotInterpolator(final Vector3d pose) {
        this.runningSnapshot.set(pose);
    }

    public void getSampleAt(final double gameTick, final Vector3d dest) {
        // Find the two snapshots to interpolate between
        int beforeIndex = -1;
        Snapshot before = null;
        Snapshot after = null;

        for (int i = 0; i < this.buffer.size(); i++) {
            final Snapshot snapshot = this.buffer.get(i);
            if (snapshot.gameTick == gameTick) {
                dest.set(snapshot.pose);
                return;
            }

            if (snapshot.gameTick < gameTick) {
                beforeIndex = i;
                before = snapshot;
            } else if (snapshot.gameTick > gameTick) {
                after = snapshot;
                break;
            }
        }

        // If we don't have two snapshots to interpolate between, we can't interpolate
        if (before == null || after == null) {
            if (before != null) {
                dest.set(before.pose);

                // dead reckon for a single tick max
                final int beforeBeforeIndex = beforeIndex - 1;
                if (beforeBeforeIndex >= 0 && !this.stopped) {
                    final Snapshot beforeBefore = this.buffer.get(beforeBeforeIndex);

                    final double deadReckoningTicks = Mth.clamp(gameTick - before.gameTick, 0, 1);
                    final double fraction = deadReckoningTicks / (before.gameTick - beforeBefore.gameTick);

                    dest.set(beforeBefore.pose)
                            .lerp(before.pose, 1.0 + fraction);
                }
            } else if (after != null) {
                dest.set(after.pose);
            }
        } else {
            // Calculate the interpolation factor
            final double factor = (gameTick - before.gameTick) / (double) (after.gameTick - before.gameTick);

            // Apply the interpolated snapshot
            before.pose.lerp(after.pose, factor, dest);
        }
    }

    public void receiveSnapshot(final int gameTick, final Vector3dc data) {
        synchronized (this.buffer) {
            if (this.buffer.isEmpty() || this.buffer.getLast().gameTick != gameTick)
                this.buffer.add(new Snapshot(gameTick, data));
        }

        this.stopped = false;
    }

    public Vector3dc getInterpolatedPose() {
        return this.runningSnapshot;
    }

    public void receiveStop() {
        this.stopped = true;
    }


    /**
     * Ticks the snapshot interpolator
     */
    public void tick(final double backTick) {
        final int bufferStartTime = (int) (backTick - 6);
        while (!this.buffer.isEmpty() && this.buffer.getFirst().gameTick < bufferStartTime) {
            this.buffer.removeFirst();
        }

        if (this.buffer.isEmpty()) {
            return;
        }

        this.getSampleAt(backTick, this.runningSnapshot);
    }

    public record Snapshot(int gameTick, Vector3dc pose) {
    }
}

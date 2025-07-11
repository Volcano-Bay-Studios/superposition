package org.modogthedev.superposition.system.behavior.behaviors;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.modogthedev.superposition.blockentity.AnalyserBlockEntity;
import org.modogthedev.superposition.system.behavior.Behavior;
import org.modogthedev.superposition.system.behavior.types.ManipulateBehavior;
import org.modogthedev.superposition.system.behavior.types.ScanBehavior;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.system.world.RedstoneWorld;
import org.modogthedev.superposition.util.DataHelper;

public class RedstoneBehavior extends Behavior implements ScanBehavior, ManipulateBehavior {
    public RedstoneBehavior(ResourceLocation selfReference) {
        super(selfReference);
    }

    @Override
    public void scan(CompoundTag tag, AnalyserBlockEntity analyserBlockEntity, Level level, BlockPos pos, BlockState state) {
        int value = level.getBestNeighborSignal(pos);
        tag.putInt(getSelfReference().getPath(),value);
    }

    @Override
    public void manipulate(Signal signal, Level level, BlockPos pos) {
        int power = DataHelper.getIntKey(signal, "power");
        if (power > 0) {
            RedstoneWorld.setPower(level, pos, Mth.clamp(power, 0, 15));
        }
    }
}

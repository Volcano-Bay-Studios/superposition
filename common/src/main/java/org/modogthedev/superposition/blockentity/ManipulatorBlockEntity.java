package org.modogthedev.superposition.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.modogthedev.superposition.core.SuperpositionBehaviors;
import org.modogthedev.superposition.core.SuperpositionBlockEntities;
import org.modogthedev.superposition.system.behavior.types.ManipulateBehavior;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.util.SignalActorTickingBlock;

public class ManipulatorBlockEntity extends PeripheralBlockEntity {

    public ManipulatorBlockEntity(BlockPos pos, BlockState state) {
        super(SuperpositionBlockEntities.MANIPULATOR.get(), pos, state);
    }

    private final String lastUpdate = "";

    @Override
    public void tick() {
        resetTooltip();
        addTooltip("Manipulator Status:");
        addTooltip("Manipulating " + level.getBlockState(getFrontPos()).getBlock().getName().getString() + "...");
        Signal signal = getSignal();

        for (ManipulateBehavior behavior : SuperpositionBehaviors.manipulateBehaviors) {
            behavior.manipulate(signal,getLevel(),getBlockPos());
        }
//        if (card != null) {
//            Signal signal = SignalHelper.randomSignal(putSignals);
//            EncodedData<?> value = signal.getEncodedData();
//            if (value != null && !value.stringValue().equals(lastUpdate)) { //TODO: make it good
//                level.updateNeighborsAt(getFrontPos(), level.getBlockState(getFrontPos()).getBlock());
//            }
//        }
        super.tick();
    }

    public BlockPos getFrontPos() {
        return getBlockPos().relative(getBlockState().getValue(SignalActorTickingBlock.FACING));
    }
}

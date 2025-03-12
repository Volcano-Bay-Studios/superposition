package org.modogthedev.superposition.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.modogthedev.superposition.core.SuperpositionBlockEntities;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.system.signal.SignalManager;
import org.modogthedev.superposition.util.SignalActorTickingBlock;

public class ManipulatorBlockEntity extends PeripheralBlockEntity {

    public ManipulatorBlockEntity(BlockPos pos, BlockState state) {
        super(SuperpositionBlockEntities.MANIPULATOR.get(), pos, state);
    }

    private String lastUpdate = "";

    @Override
    public void tick() {
        resetTooltip();
        addTooltip("Manipulator Status:");
        addTooltip("Manipulating " + level.getBlockState(getFrontPos()).getBlock().getName().getString() + "...");
        if (card != null) {
            Signal signal = SignalManager.randomSignal(putSignals);
            String value = signal.getEncodedData().stringValue();
            if (!value.equals(lastUpdate)) {
                card.affectBlock(signal,level,getFrontPos());
                level.updateNeighborsAt(getFrontPos(),level.getBlockState(getFrontPos()).getBlock());
            }
        }
        super.tick();
    }

    public BlockPos getFrontPos() {
        return getBlockPos().relative(getBlockState().getValue(SignalActorTickingBlock.FACING));
    }
}

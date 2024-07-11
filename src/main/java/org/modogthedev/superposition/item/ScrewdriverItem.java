package org.modogthedev.superposition.item;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import org.modogthedev.superposition.block.AntennaBlock;
import org.modogthedev.superposition.block.SignalGeneratorBlock;
import org.modogthedev.superposition.core.SuperpositionBlocks;
import org.modogthedev.superposition.util.SignalActorBlockEntity;

public class ScrewdriverItem extends Item {
    public ScrewdriverItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if (pContext.getLevel().getBlockState(pContext.getClickedPos()).is(SuperpositionBlocks.ANTENNA.get())) {
            BlockState state = pContext.getLevel().getBlockState(pContext.getClickedPos());
            BlockState stateUp = pContext.getLevel().getBlockState(pContext.getClickedPos().above());
            BlockState stateDown = pContext.getLevel().getBlockState(pContext.getClickedPos().below());
            if (stateUp.is(SuperpositionBlocks.ANTENNA.get()))
                return InteractionResult.FAIL;
            if (stateDown.is(SuperpositionBlocks.ANTENNA.get()))
                return InteractionResult.FAIL;
            pContext.getLevel().setBlock(pContext.getClickedPos(),pContext.getLevel().getBlockState(pContext.getClickedPos()).setValue(AntennaBlock.SHORT,!state.getValue(AntennaBlock.SHORT)),2);
            if (pContext.getLevel().isClientSide)
                return InteractionResult.SUCCESS;
        }
        if (pContext.getLevel().getBlockEntity(pContext.getClickedPos()) instanceof SignalActorBlockEntity signalActorBlockEntity) {
            if (pContext.getPlayer().isCrouching())
                signalActorBlockEntity.interactConfig();
            else
                signalActorBlockEntity.incrementConfigSelection();
            return InteractionResult.SUCCESS;
        }
        return super.useOn(pContext);
    }
}

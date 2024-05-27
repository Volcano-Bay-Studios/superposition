package org.modogthedev.superposition.item;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import org.modogthedev.superposition.core.SuperpositionBlocks;

public class ScrewdriverItem extends Item {
    public ScrewdriverItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if (pContext.getLevel().isClientSide)
            return InteractionResult.SUCCESS;
        if (pContext.getLevel().getBlockState(pContext.getClickedPos()).is(SuperpositionBlocks.ANTENNA.get())) {

        }
        return super.useOn(pContext);
    }
}

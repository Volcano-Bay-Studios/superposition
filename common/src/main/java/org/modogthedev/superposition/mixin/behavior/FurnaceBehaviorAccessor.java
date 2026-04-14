package org.modogthedev.superposition.mixin.behavior;

import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractFurnaceBlockEntity.class)
public interface FurnaceBehaviorAccessor {
    @Accessor
    int getLitTime();
    @Accessor
    int getLitDuration();
    @Accessor
    int getCookingProgress();
}

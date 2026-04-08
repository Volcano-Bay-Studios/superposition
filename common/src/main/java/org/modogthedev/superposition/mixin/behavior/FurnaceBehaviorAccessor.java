package org.modogthedev.superposition.mixin.behavior;

import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractFurnaceBlockEntity.class)
public interface FurnaceBehaviorAccessor {
    @Accessor
    int litTime();
    @Accessor
    int litDuration();
    @Accessor
    int cookingProgress();
}

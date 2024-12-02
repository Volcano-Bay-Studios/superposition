package org.modogthedev.superposition.core;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class SuperpositionTags {
    public static final TagKey<Block> GLASS_BLOCKS = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "glass_blocks"));
}

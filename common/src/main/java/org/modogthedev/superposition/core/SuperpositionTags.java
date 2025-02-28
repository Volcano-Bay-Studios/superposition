package org.modogthedev.superposition.core;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class SuperpositionTags {
    public static final TagKey<Block> GLASS_BLOCKS = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "glass_blocks"));
    public static final TagKey<Block> PLACEABLE = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("superposition", "placeable"));
    public static final TagKey<Block> SIGNAL_OFFSET = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("superposition", "signal_offset"));

    public static final TagKey<Block> HARD_PENETRATE = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("superposition", "hard_penetrate"));
    public static final TagKey<Block> MEDIUM_PENETRATE = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("superposition", "medium_penetrate"));
    public static final TagKey<Block> VERY_EASY_PENETRATE = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("superposition", "very_easy_penetrate"));

    public static final TagKey<Block> COOL = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("superposition", "cool"));
    public static final TagKey<Block> COLD = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("superposition", "cold"));
    public static final TagKey<Block> VERY_COLD = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("superposition", "very_cold"));

}
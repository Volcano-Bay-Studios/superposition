package org.modogthedev.superposition.core.data;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class SuperpositionBlockTagsProvider extends IntrinsicHolderTagsProvider<Block> {

    private static final ListMultimap<TagKey<Block>, Supplier<? extends Block>> BLOCK_TAGS = Multimaps.newListMultimap(new Object2ObjectArrayMap<>(), ObjectArrayList::new);

    public SuperpositionBlockTagsProvider(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> lookupProvider
    ) {
        super(output, Registries.BLOCK, lookupProvider, (block) -> BuiltInRegistries.BLOCK.getResourceKey(block).orElseThrow());
    }

    @Override
    protected void addTags(@NotNull HolderLookup.Provider provider) {
        for (TagKey<Block> tag : BLOCK_TAGS.keySet()) {
            IntrinsicTagAppender<Block> tagAppender = this.tag(tag);
            for (Supplier<? extends Block> block : BLOCK_TAGS.get(tag)) {
                tagAppender.add(block.get());
            }
        }
    }

    public static void addBlockTag(TagKey<Block> tag, Supplier<? extends Block> block) {
        BLOCK_TAGS.put(tag, block);
    }
}

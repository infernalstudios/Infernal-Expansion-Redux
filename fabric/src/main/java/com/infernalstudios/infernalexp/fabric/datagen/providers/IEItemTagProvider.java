package com.infernalstudios.infernalexp.fabric.datagen.providers;

import com.infernalstudios.infernalexp.compat.ShroomlightCompatRegistry;
import com.infernalstudios.infernalexp.module.ModBlocks;
import com.infernalstudios.infernalexp.module.ModTags;
import com.infernalstudios.infernalexp.registration.holders.ItemDataHolder;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class IEItemTagProvider extends FabricTagProvider.ItemTagProvider {
    private final List<TagKey<Item>> generatedTags = new ArrayList<>();

    public IEItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture, BlockTagProvider blockTagProvider) {
        super(output, registriesFuture, blockTagProvider);
    }

    private FabricTagProvider<Item>.FabricTagBuilder trackTag(TagKey<Item> tagKey) {
        generatedTags.add(tagKey);
        return getOrCreateTagBuilder(tagKey);
    }

    private void copyTag(TagKey<Block> blockTag) {
        TagKey<Item> itemTag = TagKey.create(Registries.ITEM, blockTag.location());
        generatedTags.add(itemTag);
        this.copy(blockTag, itemTag);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        for (Map.Entry<TagKey<Item>, List<ItemDataHolder<?>>> entry : ItemDataHolder.getItemTags().entrySet()) {
            FabricTagProvider<Item>.FabricTagBuilder tagBuilder = trackTag(entry.getKey());

            entry.getValue().forEach(b -> tagBuilder.add(b.get()));
        }

        FabricTagProvider<Item>.FabricTagBuilder temptationBuilder = trackTag(ModTags.Items.GLOWSQUITO_TEMPTATION_ITEMS)
                .add(ModBlocks.SHROOMLIGHT_TEAR.get().asItem());

        for (ShroomlightCompatRegistry.Variant variant : ShroomlightCompatRegistry.VARIANTS) {
            temptationBuilder.add(variant.tearBlockHolder.get().asItem());
        }

        trackTag(ModTags.Items.VOLINE_FOOD)
                .add(Items.MAGMA_CREAM);

        copyTag(BlockTags.BUTTONS);
        copyTag(BlockTags.CAMPFIRES);
        copyTag(BlockTags.DOORS);
        copyTag(BlockTags.FENCE_GATES);
        copyTag(BlockTags.FENCES);
        copyTag(BlockTags.IRON_ORES);
        copyTag(BlockTags.LOGS);
        copyTag(BlockTags.PLANKS);
        copyTag(BlockTags.PRESSURE_PLATES);
        copyTag(BlockTags.SLABS);
        copyTag(BlockTags.STAIRS);
        copyTag(BlockTags.TRAPDOORS);
        copyTag(BlockTags.WALLS);
        copyTag(BlockTags.WART_BLOCKS);
        copyTag(BlockTags.SMELTS_TO_GLASS);
        copyTag(BlockTags.PIGLIN_REPELLENTS);

        copyTag(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "ores/iron")));
    }

    @Override
    public @NotNull CompletableFuture<?> run(CachedOutput writer) {
        return super.run(writer).thenCompose(ignored -> CompletableFuture.runAsync(() -> {
            for (TagKey<Item> tagKey : generatedTags) {
                Path path = this.pathProvider.json(tagKey.location());
                try {
                    if (Files.exists(path)) {
                        String content = Files.readString(path);
                        if (!content.contains("\"replace\":")) {
                            String newContent = content.replaceFirst("\\{\\s*", "{\n  \"replace\": false,\n");
                            Files.writeString(path, newContent);
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Failed to post-process tag: " + tagKey.location(), e);
                }
            }
        }));
    }
}
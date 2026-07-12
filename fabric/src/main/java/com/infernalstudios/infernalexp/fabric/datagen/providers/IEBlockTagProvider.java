package com.infernalstudios.infernalexp.fabric.datagen.providers;

import com.infernalstudios.infernalexp.module.ModBlocks;
import com.infernalstudios.infernalexp.module.ModTags;
import com.infernalstudios.infernalexp.registration.holders.BlockDataHolder;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class IEBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    private final List<TagKey<Block>> generatedTags = new ArrayList<>();

    public IEBlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    private FabricTagProvider<Block>.FabricTagBuilder trackTag(TagKey<Block> tagKey) {
        generatedTags.add(tagKey);
        return getOrCreateTagBuilder(tagKey);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        for (Map.Entry<TagKey<Block>, List<BlockDataHolder<?>>> entry : BlockDataHolder.getBlockTags().entrySet()) {
            FabricTagProvider<Block>.FabricTagBuilder tagBuilder = trackTag(entry.getKey());

            entry.getValue().stream()
                    .map(BlockDataHolder::get)
                    .sorted(Comparator.comparing(BuiltInRegistries.BLOCK::getKey))
                    .forEach(tagBuilder::add);
        }

        trackTag(ModTags.Blocks.SHROOMLIGHT_TEARS_GROWABLE)
                .add(Blocks.SHROOMLIGHT);

        trackTag(ModTags.Blocks.SHROOMNIGHT_TEARS_GROWABLE)
                .addOptional(ResourceLocation.fromNamespaceAndPath("netherexp", "shroomnight"));

        trackTag(ModTags.Blocks.SHROOMBLIGHT_TEARS_GROWABLE)
                .addOptional(ResourceLocation.fromNamespaceAndPath("netherexp", "shroomblight"));

        trackTag(ModTags.Blocks.SHROOMBRIGHT_TEARS_GROWABLE)
                .addOptional(ResourceLocation.fromNamespaceAndPath("netherexp", "shroombright"));

        trackTag(ModTags.Blocks.GLOW_FIRE_BASE_BLOCKS)
                .add(Blocks.GLOWSTONE);

        trackTag(ModTags.Blocks.GLOWSQUITO_SUCKABLES)
                .add(Blocks.GLOWSTONE)
                .add(ModBlocks.DIMSTONE.get())
                .add(Blocks.SHROOMLIGHT)
                .addOptional(ResourceLocation.fromNamespaceAndPath("netherexp", "shroomnight"))
                .addOptional(ResourceLocation.fromNamespaceAndPath("netherexp", "shroomblight"))
                .addOptional(ResourceLocation.fromNamespaceAndPath("netherexp", "shroombright"));

        trackTag(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "ores/iron")))
                .add(ModBlocks.BASALT_IRON_ORE.get());
    }

    @Override
    public @NotNull CompletableFuture<?> run(CachedOutput writer) {
        return super.run(writer).thenCompose(ignored -> CompletableFuture.runAsync(() -> {
            for (TagKey<Block> tagKey : generatedTags) {
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
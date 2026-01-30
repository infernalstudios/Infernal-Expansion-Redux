package com.infernalstudios.infernalexp.fabric.datagen.providers;

import com.infernalstudios.infernalexp.module.ModBlocks;
import com.infernalstudios.infernalexp.module.ModTags;
import com.infernalstudios.infernalexp.registration.holders.BlockDataHolder;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class IEBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public IEBlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        for (Map.Entry<TagKey<Block>, List<BlockDataHolder<?>>> entry : BlockDataHolder.getBlockTags().entrySet()) {
            FabricTagProvider<Block>.FabricTagBuilder tagBuilder = getOrCreateTagBuilder(entry.getKey());

            entry.getValue().stream()
                    .map(BlockDataHolder::get)
                    .sorted(Comparator.comparing(BuiltInRegistries.BLOCK::getKey))
                    .forEach(tagBuilder::add);
        }

        getOrCreateTagBuilder(ModTags.Blocks.SHROOMLIGHT_TEARS_GROWABLE)
                .add(Blocks.SHROOMLIGHT);

        getOrCreateTagBuilder(ModTags.Blocks.SHROOMNIGHT_TEARS_GROWABLE)
                .addOptional(new ResourceLocation("netherexp", "shroomnight"));

        getOrCreateTagBuilder(ModTags.Blocks.SHROOMBLIGHT_TEARS_GROWABLE)
                .addOptional(new ResourceLocation("netherexp", "shroomblight"));

        getOrCreateTagBuilder(ModTags.Blocks.SHROOMBRIGHT_TEARS_GROWABLE)
                .addOptional(new ResourceLocation("netherexp", "shroombright"));

        getOrCreateTagBuilder(ModTags.Blocks.GLOW_FIRE_BASE_BLOCKS)
                .add(Blocks.GLOWSTONE);

        getOrCreateTagBuilder(ModTags.Blocks.GLOWSQUITO_SUCKABLES)
                .add(Blocks.GLOWSTONE)
                .add(ModBlocks.DIMSTONE.get())
                .add(Blocks.SHROOMLIGHT)
                .addOptional(new ResourceLocation("netherexp", "shroomnight"))
                .addOptional(new ResourceLocation("netherexp", "shroomblight"))
                .addOptional(new ResourceLocation("netherexp", "shroombright"));

        getOrCreateTagBuilder(TagKey.create(Registries.BLOCK, new ResourceLocation("c", "ores")))
                .add(ModBlocks.BASALT_IRON_ORE.get());

        getOrCreateTagBuilder(TagKey.create(Registries.BLOCK, new ResourceLocation("c", "iron_ores")))
                .add(ModBlocks.BASALT_IRON_ORE.get());
    }
}
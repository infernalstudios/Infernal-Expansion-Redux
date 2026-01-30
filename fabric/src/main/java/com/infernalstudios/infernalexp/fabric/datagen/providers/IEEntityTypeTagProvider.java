package com.infernalstudios.infernalexp.fabric.datagen.providers;

import com.infernalstudios.infernalexp.module.ModEntityTypes;
import com.infernalstudios.infernalexp.module.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.entity.EntityType;

import java.util.concurrent.CompletableFuture;

public class IEEntityTypeTagProvider extends FabricTagProvider.EntityTypeTagProvider {
    public IEEntityTypeTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        getOrCreateTagBuilder(ModTags.EntityTypes.VOLINE_FEAR)
                .add(EntityType.PIGLIN)
                .add(EntityType.PIGLIN_BRUTE);

        getOrCreateTagBuilder(ModTags.EntityTypes.VOLINE_HOSTILE)
                .add(EntityType.MAGMA_CUBE);

        getOrCreateTagBuilder(ModTags.EntityTypes.GLIMMER_GRAVEL_BLACKLIST)
                .add(ModEntityTypes.GLOWSILK_MOTH.get())
                .add(ModEntityTypes.GLOWSQUITO.get());
    }
}
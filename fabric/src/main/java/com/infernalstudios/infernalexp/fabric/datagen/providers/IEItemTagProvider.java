package com.infernalstudios.infernalexp.fabric.datagen.providers;

import com.infernalstudios.infernalexp.compat.CinderscapesCompat;
import com.infernalstudios.infernalexp.compat.GardensOfTheDeadCompat;
import com.infernalstudios.infernalexp.compat.NetherExpCompat;
import com.infernalstudios.infernalexp.module.ModBlocks;
import com.infernalstudios.infernalexp.module.ModTags;
import com.infernalstudios.infernalexp.registration.holders.ItemDataHolder;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class IEItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public IEItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        for (Map.Entry<TagKey<Item>, List<ItemDataHolder<?>>> entry : ItemDataHolder.getItemTags().entrySet()) {
            FabricTagProvider<Item>.FabricTagBuilder tagBuilder = getOrCreateTagBuilder(entry.getKey());

            entry.getValue().forEach(b -> tagBuilder.add(b.get()));
        }

        getOrCreateTagBuilder(ModTags.Items.GLOWSQUITO_TEMPTATION_ITEMS)
                .add(ModBlocks.SHROOMLIGHT_TEAR.get().asItem())
                .add(NetherExpCompat.SHROOMNIGHT_TEAR.get().asItem())
                .add(CinderscapesCompat.SHROOMBRIGHT_TEAR.get().asItem())
                .add(GardensOfTheDeadCompat.SHROOMBLIGHT_TEAR.get().asItem());

        getOrCreateTagBuilder(ModTags.Items.VOLINE_FOOD)
                .add(Items.MAGMA_CREAM);
    }
}
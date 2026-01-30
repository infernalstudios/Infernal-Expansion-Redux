package com.infernalstudios.infernalexp.fabric.datagen;

import com.infernalstudios.infernalexp.fabric.datagen.providers.*;
import com.infernalstudios.infernalexp.module.ModBiomes;
import com.infernalstudios.infernalexp.world.carver.ModConfiguredCarvers;
import com.infernalstudios.infernalexp.world.feature.ModConfiguredFeatures;
import com.infernalstudios.infernalexp.world.feature.ModPlacedFeatures;
import com.infernalstudios.infernalexp.world.structure.ModProcessorLists;
import com.infernalstudios.infernalexp.world.structure.ModStructurePools;
import com.infernalstudios.infernalexp.world.structure.ModStructures;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;

public class IEDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        pack.addProvider(IEWorldGenProvider::new);
        pack.addProvider(IEBlockTagProvider::new);
        pack.addProvider(IEItemTagProvider::new);
        pack.addProvider(IEEntityTypeTagProvider::new);
        pack.addProvider(IEBlockLootTableProvider::new);
        pack.addProvider(IEModelProvider::new);
        pack.addProvider(IELangProvider::new);
        pack.addProvider(IERecipeProvider::new);
    }

    @Override
    public void buildRegistry(RegistrySetBuilder builder) {
        builder.add(Registries.BIOME, ModBiomes::bootstrap);

        builder.add(Registries.CONFIGURED_FEATURE, ModConfiguredFeatures::bootstrap);
        builder.add(Registries.PLACED_FEATURE, ModPlacedFeatures::bootstrap);
        builder.add(Registries.CONFIGURED_CARVER, ModConfiguredCarvers::bootstrap);

        builder.add(Registries.PROCESSOR_LIST, ModProcessorLists::bootstrap);
        builder.add(Registries.TEMPLATE_POOL, ModStructurePools::bootstrap);
        builder.add(Registries.STRUCTURE, ModStructures::bootstrapStructures);
        builder.add(Registries.STRUCTURE_SET, ModStructures::bootstrapSets);
    }
}
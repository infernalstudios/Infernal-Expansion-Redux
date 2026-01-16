package com.infernalstudios.infernalexp.fabric;

import com.infernalstudios.infernalexp.IECommon;
import com.infernalstudios.infernalexp.fabric.module.*;
import net.fabricmc.api.ModInitializer;

public class InfernalExpansionFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        IECommon.init();

        BlockModuleFabric.registerBlocks();
        ItemModuleFabric.registerItems();
        EntityTypeModuleFabric.registerEntities();
        EffectModuleFabric.registerEffects();
        FeatureModuleFabric.registerFeatures();
        CarverModuleFabric.registerCarvers();
        SpawnPlacementModuleFabric.registerSpawnPlacements();

        IECommon.registerCompostables();
    }
}
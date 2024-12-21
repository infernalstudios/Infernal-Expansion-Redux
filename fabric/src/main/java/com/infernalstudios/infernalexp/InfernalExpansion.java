package com.infernalstudios.infernalexp;

import com.infernalstudios.infernalexp.module.*;
import net.fabricmc.api.ModInitializer;

public class InfernalExpansion implements ModInitializer {
    
    @Override
    public void onInitialize() {
        IECommon.init();

        BlockModuleFabric.registerBlocks();
        ItemModuleFabric.registerItems();
        EntityTypeModuleFabric.registerEntities();
        EffectModuleFabric.registerEffects();
        FeatureModuleFabric.registerFeatures();
        CarverModuleFabric.registerCarvers();
    }
}

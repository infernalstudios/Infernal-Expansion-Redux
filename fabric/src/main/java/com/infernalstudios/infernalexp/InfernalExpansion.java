package com.infernalstudios.infernalexp;

import com.infernalstudios.infernalexp.module.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.NetherBiomes;

public class InfernalExpansion implements ModInitializer {
    
    @Override
    public void onInitialize() {
        IECommon.init();

        BlockModuleFabric.registerBlocks();
        ItemModuleFabric.registerItems();
        EntityTypeModuleFabric.registerEntities();
        EffectModuleFabric.registerEffects();
    }
}

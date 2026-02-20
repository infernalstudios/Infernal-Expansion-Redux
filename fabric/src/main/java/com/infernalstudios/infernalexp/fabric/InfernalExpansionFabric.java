package com.infernalstudios.infernalexp.fabric;

import com.infernalstudios.infernalexp.IECommon;
import com.infernalstudios.infernalexp.command.NtpCommand;
import com.infernalstudios.infernalexp.fabric.module.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

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
        EnchantmentModuleFabric.registerEnchantments();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            NtpCommand.register(dispatcher);
        });

        IECommon.commonSetup();
    }
}
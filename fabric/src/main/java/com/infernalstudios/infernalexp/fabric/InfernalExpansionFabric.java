package com.infernalstudios.infernalexp.fabric;

import com.infernalstudios.infernalexp.IECommon;
import com.infernalstudios.infernalexp.command.NtpCommand;
import com.infernalstudios.infernalexp.fabric.module.*;
import com.infernalstudios.infernalexp.items.BlindsightTongueWhipItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.item.v1.EnchantmentEvents;
import net.fabricmc.fabric.api.util.TriState;

public class InfernalExpansionFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        IECommon.init();

        EffectModuleFabric.registerEffects();
        BlockModuleFabric.registerBlocks();
        ItemModuleFabric.registerItems();
        EntityTypeModuleFabric.registerEntities();
        FeatureModuleFabric.registerFeatures();
        CarverModuleFabric.registerCarvers();
        SpawnPlacementModuleFabric.registerSpawnPlacements();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            NtpCommand.register(dispatcher);
        });

        EnchantmentEvents.ALLOW_ENCHANTING.register((enchantment, target, context) ->
                target.getItem() instanceof BlindsightTongueWhipItem && BlindsightTongueWhipItem.isExtraEnchantment(enchantment) ? TriState.TRUE : TriState.DEFAULT
        );

        IECommon.commonSetup();
    }
}
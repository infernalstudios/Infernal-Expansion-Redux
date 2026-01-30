package com.infernalstudios.infernalexp.forge.module;

import com.infernalstudios.infernalexp.module.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegisterEvent;

@Mod.EventBusSubscriber(modid = "infernalexp", bus = Mod.EventBusSubscriber.Bus.MOD)
public class BlockModuleForge {
    @SubscribeEvent
    public static void registerBlocks(RegisterEvent event) {
        ModBlocks.registerBlocks(
                (id, block) -> event.register(Registries.BLOCK, helper -> helper.register(id, block)),
                (id, item) -> event.register(Registries.ITEM, helper -> helper.register(id, item))
        );
    }
}
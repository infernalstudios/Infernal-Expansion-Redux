package com.infernalstudios.infernalexp.neoforge.module;

import com.infernalstudios.infernalexp.IEConstants;
import com.infernalstudios.infernalexp.module.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;

@EventBusSubscriber(modid = IEConstants.MOD_ID)
public class BlockModuleNeoForge {
    @SubscribeEvent
    public static void registerBlocks(RegisterEvent event) {
        ModBlocks.registerBlocks(
                (id, block) -> event.register(Registries.BLOCK, helper -> helper.register(id, block)),
                (id, item) -> event.register(Registries.ITEM, helper -> helper.register(id, item))
        );
    }
}
package com.infernalstudios.infernalexp.forge.compat.autumnity;

import com.infernalstudios.infernalexp.module.ModBlocks;
import com.infernalstudios.infernalexp.module.ModCreativeTabs;
import com.infernalstudios.infernalexp.registration.holders.BlockDataHolder;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;

public class AutumnityCompat {
    public static BlockDataHolder<?> GLOWLIGHT_JACK_O_LANTERN;
    public static BlockDataHolder<?> LARGE_GLOWLIGHT_JACK_O_LANTERN_SLICE;

    public static void register(IEventBus eventBus) {
        GLOWLIGHT_JACK_O_LANTERN = ModBlocks.register("glowlight_jack_o_lantern", BlockDataHolder.of(() ->
                        new GlowlightJackOLanternBlock(BlockBehaviour.Properties.copy(Blocks.PUMPKIN)
                                .lightLevel((state) -> 15)))
                .withItem()
                .withTranslation("Glowlight Jack o'Lantern"));

        LARGE_GLOWLIGHT_JACK_O_LANTERN_SLICE = ModBlocks.register("large_glowlight_jack_o_lantern_slice", BlockDataHolder.of(() ->
                        new LargeGlowlightJackOLanternSliceBlock(BlockBehaviour.Properties.copy(Blocks.PUMPKIN)
                                .lightLevel((state) -> 15)))
                .withItem()
                .withTranslation("Large Glowlight Jack o'Lantern Slice"));

        eventBus.addListener(AutumnityCompat::addCreative);
    }

    private static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == ModCreativeTabs.INFERNAL_EXPANSION_TAB.get()) {
            event.accept(GLOWLIGHT_JACK_O_LANTERN.get());
            event.accept(LARGE_GLOWLIGHT_JACK_O_LANTERN_SLICE.get());
        }
    }
}
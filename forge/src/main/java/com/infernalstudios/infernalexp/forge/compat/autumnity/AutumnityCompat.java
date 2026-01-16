package com.infernalstudios.infernalexp.forge.compat.autumnity;

import com.infernalstudios.infernalexp.IEConstants;
import com.infernalstudios.infernalexp.module.ModCreativeTabs;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AutumnityCompat {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, IEConstants.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, IEConstants.MOD_ID);

    public static final RegistryObject<Block> GLOW_JACK_O_LANTERN = BLOCKS.register("glow_jack_o_lantern", () ->
            new GlowlightJackOLanternBlock(BlockBehaviour.Properties.copy(Blocks.PUMPKIN)
                    .lightLevel((state) -> 15)));

    public static final RegistryObject<Item> GLOW_JACK_O_LANTERN_ITEM = ITEMS.register("glow_jack_o_lantern", () ->
            new BlockItem(GLOW_JACK_O_LANTERN.get(), new Item.Properties()));

    public static final RegistryObject<Block> LARGE_GLOW_JACK_O_LANTERN_SLICE = BLOCKS.register("large_glow_jack_o_lantern_slice", () ->
            new LargeGlowlightJackOLanternSliceBlock(BlockBehaviour.Properties.copy(Blocks.PUMPKIN)
                    .lightLevel((state) -> 15)));

    public static final RegistryObject<Item> LARGE_GLOW_JACK_O_LANTERN_SLICE_ITEM = ITEMS.register("large_glow_jack_o_lantern_slice", () ->
            new BlockItem(LARGE_GLOW_JACK_O_LANTERN_SLICE.get(), new Item.Properties()));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
        eventBus.addListener(AutumnityCompat::addCreative);
    }

    private static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == ModCreativeTabs.INFERNAL_EXPANSION_TAB.get()) {
            event.accept(GLOW_JACK_O_LANTERN_ITEM);
            event.accept(LARGE_GLOW_JACK_O_LANTERN_SLICE_ITEM);
        }
    }
}
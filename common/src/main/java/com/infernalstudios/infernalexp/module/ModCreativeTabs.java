package com.infernalstudios.infernalexp.module;

import com.infernalstudios.infernalexp.IEConstants;
import com.infernalstudios.infernalexp.registration.util.RegistrationProvider;
import com.infernalstudios.infernalexp.registration.util.RegistryObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModCreativeTabs {
    /**
     * The provider for creative tabs
     */
    public static final RegistrationProvider<CreativeModeTab> TABS = RegistrationProvider.get(Registries.CREATIVE_MODE_TAB, IEConstants.MOD_ID);

    public static final RegistryObject<CreativeModeTab> INFERNAL_EXPANSION_TAB = TABS.register(IEConstants.MOD_ID, () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0).title(Component.translatable("itemgroup.infernalexp")).icon(() -> new ItemStack(ModItems.TAB_ICON.get())).displayItems((itemDisplayParameters, entries) -> {

        entries.accept(ModBlocks.SHIMMER_SAND.get());
        entries.accept(ModBlocks.SHIMMER_SHEET.get());
        entries.accept(ModBlocks.GLIMMER_GRAVEL.get());
        entries.accept(ModBlocks.GLOWLIGHT_GLASS.get());
        entries.accept(ModBlocks.GLOWLIGHT_GLASS.getPaneBlock().get());

        entries.accept(ModBlocks.SHIMMER_STONE.get());
        entries.accept(ModBlocks.SHIMMER_STONE_BRICKS.get());
        entries.accept(ModBlocks.SHIMMER_STONE_BRICKS.getStairs().get());
        entries.accept(ModBlocks.SHIMMER_STONE_BRICKS.getSlab().get());

        entries.accept(ModBlocks.POLISHED_GLOWSTONE.get());
        entries.accept(ModBlocks.DIMSTONE.get());
        entries.accept(ModBlocks.POLISHED_DIMSTONE.get());
        entries.accept(ModBlocks.DULLSTONE.get());
        entries.accept(ModBlocks.POLISHED_DULLSTONE.get());
        entries.accept(ModBlocks.DULLSTONE_PRESSURE_PLATE.get());
        entries.accept(ModBlocks.DULLSTONE_BUTTON.get());
        entries.accept(ModItems.DULLROCKS.get());

        entries.accept(ModBlocks.GLOWSILK_COCOON.get());
        entries.accept(ModItems.GLOWSILK_STRING.get());
        entries.accept(ModItems.GLOWSILK_BOW.get());

        entries.accept(ModBlocks.LUMINOUS_FUNGUS.get());
        entries.accept(ModBlocks.LUMINOUS_WART_BLOCK.get());

        entries.accept(ModBlocks.LUMINOUS_FUNGUS_CAP.get());
        entries.accept(ModBlocks.CRIMSON_FUNGUS_CAP.get());
        entries.accept(ModBlocks.WARPED_FUNGUS_CAP.get());

        entries.accept(ModBlocks.LUMINOUS_STEM.get());
        entries.accept(ModBlocks.STRIPPED_LUMINOUS_STEM.get());
        entries.accept(ModBlocks.LUMINOUS_HYPHAE.get());
        entries.accept(ModBlocks.STRIPPED_LUMINOUS_HYPHAE.get());

        entries.accept(ModBlocks.LUMINOUS_PLANKS.get());
        entries.accept(ModBlocks.LUMINOUS_PLANKS.getStairs().get());
        entries.accept(ModBlocks.LUMINOUS_PLANKS.getSlab().get());
        entries.accept(ModBlocks.LUMINOUS_PLANKS.getFence().get());
        entries.accept(ModBlocks.LUMINOUS_PLANKS.getFenceGate().get());
        entries.accept(ModBlocks.LUMINOUS_PLANKS.getButton().get());
        entries.accept(ModBlocks.LUMINOUS_PLANKS.getPressurePlate().get());

        entries.accept(ModBlocks.LUMINOUS_DOOR.get());
        entries.accept(ModBlocks.LUMINOUS_TRAPDOOR.get());

        entries.accept(ModBlocks.SHROOMLIGHT_TEAR.get());

        entries.accept(ModBlocks.DULLTHORNS.get());
        entries.accept(ModBlocks.DULLTHORNS_BLOCK.get());

        entries.accept(ModItems.GLOWLIGHT_TORCH.get());
        entries.accept(ModBlocks.GLOWLIGHT_CAMPFIRE.get());
        entries.accept(ModBlocks.GLOWLIGHT_LANTERN.get());

        entries.accept(ModBlocks.BASALT_STAIRS.get());
        entries.accept(ModBlocks.BASALT_SLAB.get());
        entries.accept(ModBlocks.BASALT_WALL.get());
        entries.accept(ModBlocks.BASALT_IRON_ORE.get());
        entries.accept(ModBlocks.BASALT_SAND.get());
        entries.accept(ModBlocks.BASALT_SAND_SHEET.get());

        entries.accept(ModBlocks.COBBLED_BASALT.get());
        entries.accept(ModBlocks.COBBLED_BASALT.getStairs().get());
        entries.accept(ModBlocks.COBBLED_BASALT.getSlab().get());

        entries.accept(ModBlocks.BASALT_BRICKS.get());
        entries.accept(ModBlocks.BASALT_BRICKS.getStairs().get());
        entries.accept(ModBlocks.BASALT_BRICKS.getSlab().get());
        entries.accept(ModBlocks.BASALT_BRICKS.getWall().get());

        entries.accept(ModBlocks.QUARTZ_GLASS.get());
        entries.accept(ModBlocks.QUARTZ_GLASS.getPaneBlock().get());

        entries.accept(ModBlocks.VOLATILE_GEYSER.get());

        entries.accept(ModItems.VOLINE_BUCKET.get());
        entries.accept(ModItems.VOLINE_SPAWN_EGG.get());

        entries.accept(ModItems.GLOWSQUITO_SPAWN_EGG.get());

    }).build());

    // Called in the mod initializer / constructor in order to make sure that items are registered
    public static void load() {
    }
}
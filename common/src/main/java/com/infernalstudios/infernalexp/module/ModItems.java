package com.infernalstudios.infernalexp.module;

import com.infernalstudios.infernalexp.IECommon;
import com.infernalstudios.infernalexp.registration.holders.ItemDataHolder;
import net.minecraft.core.Direction;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.StandingAndWallBlockItem;

import java.util.HashMap;
import java.util.Map;

public class ModItems {
    /** Map of all Item Resource Locations to their ItemDataHolders. */
    private static final Map<ResourceLocation, ItemDataHolder<?>> ITEM_REGISTRY = new HashMap<>();

    public static ItemDataHolder<?> register(String name, ItemDataHolder<?> itemDataHolder) {
        return register(IECommon.id(name), itemDataHolder);
    }

    public static ItemDataHolder<?> register(ResourceLocation id, ItemDataHolder<?> itemDataHolder) {
        ITEM_REGISTRY.put(id, itemDataHolder);
        return itemDataHolder;
    }

    public static Map<ResourceLocation, ItemDataHolder<?>> getItemRegistry() {
        return ITEM_REGISTRY;
    }

    // Called in the mod initializer / constructor in order to make sure that items are registered
    public static void load() {}


    public static final ItemDataHolder<?> TAB_ICON = register("tab_icon", ItemDataHolder.of(() ->
                    new Item(new Item.Properties()))
            .withModel(ModelTemplates.FLAT_ITEM)
            .withTranslation("Infernal Expansion")
    );


    public static final ItemDataHolder<?> DULLROCKS = register("dullrocks", ItemDataHolder.of(() ->
                    new Item(new Item.Properties()))
            .withModel(ModelTemplates.FLAT_ITEM)
            .withTranslation("Dullrocks")
    );

    public static final ItemDataHolder<?> GLOWLIGHT_TORCH = register("glowlight_torch", ItemDataHolder.of(() ->
                    new StandingAndWallBlockItem(ModBlocks.GLOWLIGHT_TORCH.get(), ModBlocks.GLOWLIGHT_WALL_TORCH.get(),
                            new Item.Properties(), Direction.DOWN))
            .withModel(ModelTemplates.FLAT_ITEM)
    );

    public static final ItemDataHolder<?> GLOWSILK_STRING = register("glowsilk_string", ItemDataHolder.of(() ->
                    new Item(new Item.Properties()))
            .withModel(ModelTemplates.FLAT_ITEM)
            .withTranslation("Glowsilk String")
    );
}

package com.infernalstudios.infernalexp.module;

import com.infernalstudios.infernalexp.IECommon;
import com.infernalstudios.infernalexp.items.EntityBucketItem;
import com.infernalstudios.infernalexp.registration.holders.ItemDataHolder;
import net.minecraft.core.Direction;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.material.Fluids;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ModItems {
    /**
     * Map of all Item Resource Locations to their ItemDataHolders.
     */
    private static final Map<ResourceLocation, ItemDataHolder<?>> ITEM_REGISTRY = new HashMap<>();
    public static final ModelTemplate SPAWN_EGG = new ModelTemplate(Optional.of(new ResourceLocation("minecraft", "item/template_spawn_egg")), Optional.empty());

    public static ItemDataHolder<?> register(String name, ItemDataHolder<?> itemDataHolder) {
        return register(IECommon.makeID(name), itemDataHolder);
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

    public static final ItemDataHolder<?> VOLINE_BUCKET = register("voline_bucket", ItemDataHolder.of(() ->
                    new EntityBucketItem(
                            ModEntityTypes.VOLINE::get,
                            Fluids.LAVA,
                            () -> SoundEvents.BUCKET_EMPTY_LAVA,
                            new Item.Properties().stacksTo(1)
                    ))
            .withModel(ModelTemplates.FLAT_ITEM)
            .withTranslation("Voline Bucket")
    );

    public static final ItemDataHolder<?> VOLINE_SPAWN_EGG = register("voline_spawn_egg", ItemDataHolder.of(() ->
                    new SpawnEggItem(
                            ModEntityTypes.VOLINE.get(),
                            0x2E2631,
                            0x652833,
                            new Item.Properties()
                    ))
            .withModel(SPAWN_EGG)
            .withTranslation("Voline Spawn Egg")
    );

    public static final ItemDataHolder<?> GLOWSQUITO_SPAWN_EGG = register("glowsquito_spawn_egg", ItemDataHolder.of(() ->
                    new SpawnEggItem(
                            ModEntityTypes.GLOWSQUITO.get(),
                            0x383948,
                            0xe5c092,
                            new Item.Properties()
                    ))
            .withModel(SPAWN_EGG)
            .withTranslation("Glowsquito Spawn Egg")
    );


    public static final ItemDataHolder<?> GLOWSILK_STRING = register("glowsilk_string", ItemDataHolder.of(() ->
                    new Item(new Item.Properties()))
            .withModel(ModelTemplates.FLAT_ITEM)
            .withTranslation("Glowsilk String")
    );
}

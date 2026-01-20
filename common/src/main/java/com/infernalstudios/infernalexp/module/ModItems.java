package com.infernalstudios.infernalexp.module;

import com.infernalstudios.infernalexp.IECommon;
import com.infernalstudios.infernalexp.items.EntityBucketItem;
import com.infernalstudios.infernalexp.items.GlowsilkBowItem;
import com.infernalstudios.infernalexp.items.MusicDiscItem;
import com.infernalstudios.infernalexp.registration.holders.ItemDataHolder;
import net.minecraft.core.Direction;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.material.Fluids;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ModItems {
    public static final ModelTemplate SPAWN_EGG = new ModelTemplate(Optional.of(new ResourceLocation("minecraft", "item/template_spawn_egg")), Optional.empty());
    /**
     * Map of all Item Resource Locations to their ItemDataHolders.
     */
    private static final Map<ResourceLocation, ItemDataHolder<?>> ITEM_REGISTRY = new HashMap<>();
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

    public static final ItemDataHolder<?> MAGMA_CUBE_BUCKET = register("magma_cube_bucket", ItemDataHolder.of(() ->
                    new EntityBucketItem(
                            () -> EntityType.MAGMA_CUBE,
                            Fluids.LAVA,
                            () -> SoundEvents.MAGMA_CUBE_SQUISH,
                            new Item.Properties().stacksTo(1)
                    ))
            .withModel(ModelTemplates.FLAT_ITEM)
            .withTranslation("Magma Cube Bucket")
    );

    public static final ItemDataHolder<?> STRIDER_BUCKET = register("strider_bucket", ItemDataHolder.of(() ->
                    new EntityBucketItem(
                            () -> EntityType.STRIDER,
                            Fluids.LAVA,
                            () -> SoundEvents.BUCKET_EMPTY_LAVA,
                            new Item.Properties().stacksTo(1)
                    ))
            .withModel(ModelTemplates.FLAT_ITEM)
            .withTranslation("Strider Bucket")
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

    public static final ItemDataHolder<?> GLOWSILK_MOTH_SPAWN_EGG = register("glowsilk_moth_spawn_egg", ItemDataHolder.of(() ->
                    new SpawnEggItem(
                            ModEntityTypes.GLOWSILK_MOTH.get(),
                            0x724423,
                            0xe3b064,
                            new Item.Properties()
                    ))
            .withModel(SPAWN_EGG)
            .withTranslation("Glowsilk Moth Spawn Egg")
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

    public static final ItemDataHolder<?> BLINDSIGHT_SPAWN_EGG = register("blindsight_spawn_egg", ItemDataHolder.of(() ->
                    new SpawnEggItem(
                            ModEntityTypes.BLINDSIGHT.get(),
                            0x312c36,
                            0xfbda74,
                            new Item.Properties()
                    ))
            .withModel(SPAWN_EGG)
            .withTranslation("Blindsight Spawn Egg")
    );

    public static final ItemDataHolder<?> GLOWSILK_STRING = register("glowsilk_string", ItemDataHolder.of(() ->
                    new Item(new Item.Properties()))
            .withModel(ModelTemplates.FLAT_ITEM)
            .withTranslation("Glowsilk String")
    );

    public static final ItemDataHolder<?> GLOWSILK_BOW = register("glowsilk_bow", ItemDataHolder.of(() ->
                    new GlowsilkBowItem(new Item.Properties().durability(384)))
            .withTranslation("Glowsilk Bow")
    );

    public static final ItemDataHolder<?> MUSIC_DISC_FLUSH = register("music_disc_flush", ItemDataHolder.of(() ->
                    new MusicDiscItem(14, ModSounds.RECORD_FLUSH.get(), new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 2440))
            .withModel(ModelTemplates.FLAT_ITEM)
            .withTranslation("Music Disc")
            .withTags(ItemTags.MUSIC_DISCS)
    );

    public static final ItemDataHolder<?> GLOWCOKE = register("glowcoke", ItemDataHolder.of(() ->
                    new Item(new Item.Properties()))
            .withModel(ModelTemplates.FLAT_ITEM)
            .withTranslation("Glowcoke")
            .withFuel(1600)
    );

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

    public static void load() {
    }
}
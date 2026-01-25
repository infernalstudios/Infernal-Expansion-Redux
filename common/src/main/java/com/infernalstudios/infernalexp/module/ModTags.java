package com.infernalstudios.infernalexp.module;

import com.infernalstudios.infernalexp.IECommon;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static class Blocks {
        public static TagKey<Block> create(String name) {
            return TagKey.create(Registries.BLOCK, IECommon.makeID(name));
        }

        public static final TagKey<Block> GLOW_FIRE_BASE_BLOCKS = create("glowlight_fire_base_blocks");
        public static final TagKey<Block> SHROOMLIGHT_TEARS_GROWABLE = create("shroomlight_tears_growable_on");
        public static final TagKey<Block> SHROOMNIGHT_TEARS_GROWABLE = create("shroomnight_tears_growable_on");
        public static final TagKey<Block> SHROOMBLIGHT_TEARS_GROWABLE = create("shroomblight_tears_growable_on");
        public static final TagKey<Block> GLOWSQUITO_SUCKABLES = create("glowsquito_suckables");
        public static final TagKey<Block> GLOWSTONE_CANYON_CARVER_REPLACEABLES = create("glowstone_canyon_carver_replaceable");
    }

    public static class Biomes {
        public static TagKey<Biome> create(String name) {
            return TagKey.create(Registries.BIOME, IECommon.makeID(name));
        }

        public static final TagKey<Biome> IS_GLOWSTONE_CANYON = create("is_glowstone_canyon");
    }

    public static class Items {
        public static TagKey<Item> create(String name) {
            return TagKey.create(Registries.ITEM, IECommon.makeID(name));
        }

        public static final TagKey<Item> VOLINE_FOOD = create("voline_food");
        public static final TagKey<Item> GLOWSQUITO_TEMPTATION_ITEMS = create("glowsquito_temptation_items");
    }

    public static class EntityTypes {
        public static TagKey<EntityType<?>> create(String name) {
            return TagKey.create(Registries.ENTITY_TYPE, IECommon.makeID(name));
        }

        public static final TagKey<EntityType<?>> VOLINE_FEAR = create("voline_fear");
        public static final TagKey<EntityType<?>> VOLINE_HOSTILE = create("voline_hostile");
        public static final TagKey<EntityType<?>> GLIMMER_GRAVEL_BLACKLIST = create("glimmer_gravel_blacklist");
    }
}
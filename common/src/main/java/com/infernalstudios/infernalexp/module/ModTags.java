package com.infernalstudios.infernalexp.module;

import com.infernalstudios.infernalexp.IECommon;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static class Blocks {
        public static TagKey<Block> create(String name) {
            return TagKey.create(Registries.BLOCK, IECommon.id(name));
        }

        public static final TagKey<Block> GLOW_FIRE_BASE_BLOCKS = create("glowlight_fire_base_blocks");
        public static final TagKey<Block> SHROOMLIGHT_TEARS_GROWABLE = create("shroomlight_tears_growable_on");
        public static final TagKey<Block> GLOWSTONE_CANYON_CARVER_REPLACEABLES = create("glowstone_canyon_carver_replaceable");
    }

    public static class Biomes {
        public static TagKey<Biome> create(String name) {
            return TagKey.create(Registries.BIOME, IECommon.id(name));
        }

        public static final TagKey<Biome> IS_GLOWSTONE_CANYON = create("is_glowstone_canyon");
    }
}

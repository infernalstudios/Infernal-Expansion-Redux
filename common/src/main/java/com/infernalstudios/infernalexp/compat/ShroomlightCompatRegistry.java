package com.infernalstudios.infernalexp.compat;

import com.infernalstudios.infernalexp.block.HollowlightBlock;
import com.infernalstudios.infernalexp.block.ShroomlightTearBlock;
import com.infernalstudios.infernalexp.module.ModBlocks;
import com.infernalstudios.infernalexp.module.ModTags;
import com.infernalstudios.infernalexp.platform.Services;
import com.infernalstudios.infernalexp.registration.GlowsquitoInteractionRegistry;
import com.infernalstudios.infernalexp.registration.holders.BlockDataHolder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ShroomlightCompatRegistry {
    public static final List<Variant> VARIANTS = new ArrayList<>();
    public static final Variant NETHEREXP = new Variant(
            "netherexp",
            "shroomnight",
            "shroomnight_tear",
            "hollownight",
            "Shroomnight Tear",
            "Hollownight",
            ModTags.Blocks.SHROOMNIGHT_TEARS_GROWABLE);

    public static final Variant GARDENS_OF_THE_DEAD = new Variant(
            "gardens_of_the_dead",
            "shroomblight",
            "shroomblight_tear",
            "hollowblight",
            "Shroomblight Tear",
            "Hollowblight",
            ModTags.Blocks.SHROOMBLIGHT_TEARS_GROWABLE);

    public static final Variant CINDERSCAPES = new Variant(
            "cinderscapes",
            "shroombright",
            "shroombright_tear",
            "hollowbright",
            "Shroombright Tear",
            "Hollowbright",
            ModTags.Blocks.SHROOMBRIGHT_TEARS_GROWABLE);

    public static final Variant SPELUNKERY = new Variant(
            "spelunkery",
            "phosphor_shroomlight",
            "phosphor_shroomlight_tear",
            "phosphor_hollowlight",
            "Phosphor Shroomlight Tear",
            "Phosphor Hollowlight",
            ModTags.Blocks.PHOSPHOR_SHROOMLIGHT_TEARS_GROWABLE);

    private static final Map<Block, Supplier<Block>> TEAR_MAPPINGS = new HashMap<>();

    public static void loadAll() {
        VARIANTS.forEach(Variant::loadBlocks);
    }

    public static void registerAllCompat() {
        TEAR_MAPPINGS.put(Blocks.SHROOMLIGHT, ModBlocks.SHROOMLIGHT_TEAR::get);
        VARIANTS.forEach(Variant::registerInteractions);
    }

    @Nullable
    public static Block getTearVariant(Block targetBlock) {
        Supplier<Block> tearSupplier = TEAR_MAPPINGS.get(targetBlock);
        return tearSupplier != null ? tearSupplier.get() : null;
    }

    public static class Variant {
        public final String modId;
        public final String baseBlockPath;
        public final String tearId;
        public final String hollowId;
        public final String tearTranslation;
        public final String hollowTranslation;
        public final TagKey<Block> growableTag;

        public BlockDataHolder<?> tearBlockHolder;
        public BlockDataHolder<?> hollowBlockHolder;

        public Variant(String modId, String baseBlockPath, String tearId, String hollowId, String tearTranslation, String hollowTranslation, TagKey<Block> growableTag) {
            this.modId = modId;
            this.baseBlockPath = baseBlockPath;
            this.tearId = tearId;
            this.hollowId = hollowId;
            this.tearTranslation = tearTranslation;
            this.hollowTranslation = hollowTranslation;
            this.growableTag = growableTag;
            VARIANTS.add(this);
        }

        public void loadBlocks() {
            tearBlockHolder = ModBlocks.register(tearId, BlockDataHolder.of(() ->
                            new ShroomlightTearBlock(BlockBehaviour.Properties.copy(Blocks.SHROOMLIGHT).instabreak().noCollission(), growableTag))
                    .withItem().cutout().dropsSelf()
                    .withTranslation(tearTranslation)
            );

            hollowBlockHolder = ModBlocks.register(hollowId, BlockDataHolder.of(() ->
                            new HollowlightBlock(BlockBehaviour.Properties.copy(Blocks.SHROOMLIGHT),
                                    () -> BuiltInRegistries.BLOCK.get(new ResourceLocation(modId, baseBlockPath))))
                    .withItem().withModel(BlockDataHolder.Model.CUBE).dropsSelf()
                    .withTags(BlockTags.MINEABLE_WITH_HOE)
                    .withTranslation(hollowTranslation)
            );
        }

        public void registerInteractions() {
            if (!Services.PLATFORM.isModLoaded(modId)) return;

            ResourceLocation targetId = new ResourceLocation(modId, baseBlockPath);
            Block targetBlock = BuiltInRegistries.BLOCK.get(targetId);

            if (targetBlock != Blocks.AIR) {
                GlowsquitoInteractionRegistry.register(
                        targetBlock,
                        () -> hollowBlockHolder.get().defaultBlockState(),
                        baseBlockPath,
                        SoundEvents.SHROOMLIGHT_BREAK
                );
                TEAR_MAPPINGS.put(targetBlock, () -> tearBlockHolder.get());
            }
        }
    }
}
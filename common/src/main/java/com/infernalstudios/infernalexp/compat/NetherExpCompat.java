package com.infernalstudios.infernalexp.compat;

import com.infernalstudios.infernalexp.block.HollowlightBlock;
import com.infernalstudios.infernalexp.block.ShroomlightTearBlock;
import com.infernalstudios.infernalexp.module.ModBlocks;
import com.infernalstudios.infernalexp.module.ModTags;
import com.infernalstudios.infernalexp.platform.Services;
import com.infernalstudios.infernalexp.registration.GlowsquitoInteractionRegistry;
import com.infernalstudios.infernalexp.registration.holders.BlockDataHolder;
import net.jadenxgamer.netherexp.registry.worldgen.feature.custom.WarpedFungusFeature;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class NetherExpCompat {
    public static BlockDataHolder<?> SHROOMNIGHT_TEAR;
    public static BlockDataHolder<?> HOLLOWNIGHT;

    public static void load() {
        SHROOMNIGHT_TEAR = ModBlocks.register("shroomnight_tear", BlockDataHolder.of(() ->
                        new ShroomlightTearBlock(BlockBehaviour.Properties.copy(Blocks.SHROOMLIGHT).instabreak().noCollission(),
                                ModTags.Blocks.SHROOMNIGHT_TEARS_GROWABLE))
                .withItem().cutout().dropsSelf()
                .withTranslation("Shroomnight Tear")
        );

        HOLLOWNIGHT = ModBlocks.register("hollownight", BlockDataHolder.of(() ->
                        new HollowlightBlock(BlockBehaviour.Properties.copy(Blocks.SHROOMLIGHT),
                                () -> BuiltInRegistries.BLOCK.get(new ResourceLocation("netherexp", "shroomnight"))))
                .withItem().withModel(BlockDataHolder.Model.CUBE)
                .dropsSelf()
                .withTags(BlockTags.MINEABLE_WITH_HOE)
                .withTranslation("Hollownight")
        );

    }

    public static boolean isShroomnight(Block block) {
        if (!Services.PLATFORM.isModLoaded("netherexp")) {
            return false;
        }
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
        return id.getNamespace().equals("netherexp") && id.getPath().equals("shroomnight");
    }

    public static boolean isNetherExpFungus(Object feature) {
        if (!Services.PLATFORM.isModLoaded("netherexp")) {
            return false;
        }

        return InnerCompat.isWarpedFungus(feature);
    }

    public static void registerCompat() {
        Block shroomnight = BuiltInRegistries.BLOCK.get(new ResourceLocation("netherexp", "shroomnight"));

        if (shroomnight != Blocks.AIR) {
            GlowsquitoInteractionRegistry.register(
                    shroomnight,
                    () -> NetherExpCompat.HOLLOWNIGHT.get().defaultBlockState(),
                    "shroomnight",
                    SoundEvents.SHROOMLIGHT_BREAK
            );
        }
    }

    private static class InnerCompat {
        static boolean isWarpedFungus(Object feature) {
            return feature instanceof WarpedFungusFeature;
        }
    }
}
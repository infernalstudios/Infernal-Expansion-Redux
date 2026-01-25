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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class GardensOfTheDeadCompat {
    public static BlockDataHolder<?> SHROOMBLIGHT_TEAR;
    public static BlockDataHolder<?> HOLLOWBLIGHT;

    public static void load() {
        SHROOMBLIGHT_TEAR = ModBlocks.register("shroomblight_tear", BlockDataHolder.of(() ->
                        new ShroomlightTearBlock(BlockBehaviour.Properties.copy(Blocks.SHROOMLIGHT).instabreak().noCollission(),
                                ModTags.Blocks.SHROOMBLIGHT_TEARS_GROWABLE))
                .withItem().cutout().dropsSelf()
                .withTranslation("Shroomblight Tear")
        );

        HOLLOWBLIGHT = ModBlocks.register("hollowblight", BlockDataHolder.of(() ->
                        new HollowlightBlock(BlockBehaviour.Properties.copy(Blocks.SHROOMLIGHT),
                                () -> BuiltInRegistries.BLOCK.get(new ResourceLocation("netherexp", "shroomblight"))))
                .withItem().withModel(BlockDataHolder.Model.CUBE)
                .dropsSelf()
                .withTags(BlockTags.MINEABLE_WITH_HOE)
                .withTranslation("Hollowblight")
        );

    }

    public static boolean isShroomblight(Block block) {
        if (!Services.PLATFORM.isModLoaded("netherexp") || !Services.PLATFORM.isModLoaded("gardens_of_the_dead"))
            return false;
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
        return id.getNamespace().equals("netherexp") && id.getPath().equals("shroomblight");
    }

    public static void registerCompat() {
        Block shroomblight = BuiltInRegistries.BLOCK.get(new ResourceLocation("netherexp", "shroomblight"));
        if (shroomblight != Blocks.AIR) {
            GlowsquitoInteractionRegistry.register(
                    shroomblight,
                    () -> GardensOfTheDeadCompat.HOLLOWBLIGHT.get().defaultBlockState(),
                    "shroomblight",
                    SoundEvents.SHROOMLIGHT_BREAK
            );
        }
    }
}
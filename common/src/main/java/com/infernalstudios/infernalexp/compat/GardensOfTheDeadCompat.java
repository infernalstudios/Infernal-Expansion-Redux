package com.infernalstudios.infernalexp.compat;

import com.infernalstudios.infernalexp.block.HollowlightBlock;
import com.infernalstudios.infernalexp.block.ShroomlightTearBlock;
import com.infernalstudios.infernalexp.module.ModBlocks;
import com.infernalstudios.infernalexp.module.ModTags;
import com.infernalstudios.infernalexp.platform.Services;
import com.infernalstudios.infernalexp.registration.holders.BlockDataHolder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class GardensOfTheDeadCompat {
    public static BlockDataHolder<?> SHROOMBLIGHT_TEAR;
    public static BlockDataHolder<?> HOLLOWBLIGHT;
    public static BlockDataHolder<?> SHROOMBRIGHT_TEAR;
    public static BlockDataHolder<?> HOLLOWBRIGHT;

    public static void load() {
        SHROOMBLIGHT_TEAR = ModBlocks.register("shroomblight_tear", BlockDataHolder.of(() ->
                        new ShroomlightTearBlock(BlockBehaviour.Properties.copy(Blocks.SHROOMLIGHT).instabreak().noCollission(),
                                ModTags.Blocks.SHROOMBLIGHT_TEARS_GROWABLE))
                .withItem().cutout().dropsSelf()
                .withTranslation("Shroomblight Tear")
        );

        HOLLOWBLIGHT = ModBlocks.register("hollowblight", BlockDataHolder.of(() ->
                        new HollowlightBlock(BlockBehaviour.Properties.copy(Blocks.SHROOMLIGHT),
                                () -> BuiltInRegistries.BLOCK.get(new ResourceLocation("gardens_of_the_dead", "shroomblight"))))
                .withItem().withModel(BlockDataHolder.Model.CUBE)
                .dropsSelf()
                .withTags(BlockTags.MINEABLE_WITH_HOE)
                .withTranslation("Hollowblight")
        );

        SHROOMBRIGHT_TEAR = ModBlocks.register("shroombright_tear", BlockDataHolder.of(() ->
                        new ShroomlightTearBlock(BlockBehaviour.Properties.copy(Blocks.SHROOMLIGHT).instabreak().noCollission(),
                                ModTags.Blocks.SHROOMBLIGHT_TEARS_GROWABLE))
                .withItem().cutout().dropsSelf()
                .withTranslation("Shroombright Tear")
        );

        HOLLOWBRIGHT = ModBlocks.register("hollowbright", BlockDataHolder.of(() ->
                        new HollowlightBlock(BlockBehaviour.Properties.copy(Blocks.SHROOMLIGHT),
                                () -> BuiltInRegistries.BLOCK.get(new ResourceLocation("gardens_of_the_dead", "shroombright"))))
                .withItem().withModel(BlockDataHolder.Model.CUBE)
                .dropsSelf()
                .withTags(BlockTags.MINEABLE_WITH_HOE)
                .withTranslation("Hollowbright")
        );
    }

    public static boolean isShroomblight(Block block) {
        if (!Services.PLATFORM.isModLoaded("gardens_of_the_dead")) {
            return false;
        }
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
        return id.getNamespace().equals("gardens_of_the_dead") && id.getPath().equals("shroomblight");
    }

    public static boolean isShroombright(Block block) {
        if (!Services.PLATFORM.isModLoaded("gardens_of_the_dead")) {
            return false;
        }
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
        return id.getNamespace().equals("gardens_of_the_dead") && id.getPath().equals("shroombright");
    }
}
package com.infernalstudios.infernalexp.registration;

import com.infernalstudios.infernalexp.module.ModBlocks;
import com.infernalstudios.infernalexp.module.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class GlowsquitoInteractionRegistry {
    private static final Map<Block, Interaction> REGISTRY = new HashMap<>();

    public static void register(Block input, Supplier<BlockState> result, @Nullable String variant, SoundEvent sound) {
        REGISTRY.put(input, new Interaction(result, variant, sound));
    }

    @Nullable
    public static Interaction getInteraction(Block block) {
        return REGISTRY.get(block);
    }

    public static void registerDefaults() {
        register(Blocks.GLOWSTONE, () -> ModBlocks.DIMSTONE.get().defaultBlockState(),
                null, SoundEvents.GLASS_BREAK);

        register(ModBlocks.DIMSTONE.get(), () -> ModBlocks.DULLSTONE.get().defaultBlockState(),
                null, ModSounds.BLOCK_DULLSTONE_BREAK.get());

        register(Blocks.SHROOMLIGHT, () -> ModBlocks.HOLLOWLIGHT.get().defaultBlockState(),
                "shroomlight", SoundEvents.SHROOMLIGHT_BREAK);
    }

    public record Interaction(Supplier<BlockState> result, @Nullable String variant, SoundEvent sound) {
    }
}
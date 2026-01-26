package com.infernalstudios.infernalexp.forge.compat.autumnity;

import com.infernalstudios.infernalexp.module.ModBlocks;
import com.infernalstudios.infernalexp.module.ModCreativeTabs;
import com.infernalstudios.infernalexp.registration.holders.BlockDataHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.ForgeRegistries;

public class AutumnityCompat {
    public static BlockDataHolder<?> GLOWLIGHT_JACK_O_LANTERN;
    public static BlockDataHolder<?> LARGE_GLOWLIGHT_JACK_O_LANTERN_SLICE;

    public static void register(IEventBus eventBus) {
        GLOWLIGHT_JACK_O_LANTERN = ModBlocks.register("glowlight_jack_o_lantern", BlockDataHolder.of(() ->
                        new GlowlightJackOLanternBlock(BlockBehaviour.Properties.copy(Blocks.PUMPKIN)
                                .lightLevel((state) -> 15)))
                .withItem()
                .withTranslation("Glowlight Jack o'Lantern"));

        LARGE_GLOWLIGHT_JACK_O_LANTERN_SLICE = ModBlocks.register("large_glowlight_jack_o_lantern_slice", BlockDataHolder.of(() ->
                        new LargeGlowlightJackOLanternSliceBlock(BlockBehaviour.Properties.copy(Blocks.PUMPKIN)
                                .lightLevel((state) -> 15)))
                .withItem()
                .withTranslation("Large Glowlight Jack o'Lantern Slice"));

        eventBus.addListener(AutumnityCompat::addCreative);
        MinecraftForge.EVENT_BUS.addListener(AutumnityCompat::onRightClickBlock);
    }

    private static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == ModCreativeTabs.INFERNAL_EXPANSION_TAB.get()) {
            event.accept(GLOWLIGHT_JACK_O_LANTERN.get());
            event.accept(LARGE_GLOWLIGHT_JACK_O_LANTERN_SLICE.get());
        }
    }

    private static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        ItemStack itemStack = event.getItemStack();
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);

        if (itemStack.is(ModBlocks.GLOWLIGHT_TORCH.get().asItem())) {
            ResourceLocation id = ForgeRegistries.BLOCKS.getKey(state.getBlock());

            if (state.is(Blocks.CARVED_PUMPKIN)) {
                level.setBlock(pos, GLOWLIGHT_JACK_O_LANTERN.get().defaultBlockState()
                        .setValue(GlowlightJackOLanternBlock.FACING, state.getValue(GlowlightJackOLanternBlock.FACING)), 3);

            } else if (id != null && "autumnity".equals(id.getNamespace()) && "carved_large_pumpkin_slice".equals(id.getPath())) {
                BlockState newState = LARGE_GLOWLIGHT_JACK_O_LANTERN_SLICE.get().defaultBlockState();
                newState = newState.setValue(LargeGlowlightJackOLanternSliceBlock.FACING, state.getValue(LargeGlowlightJackOLanternSliceBlock.FACING));
                newState = newState.setValue(LargeGlowlightJackOLanternSliceBlock.HALF, state.getValue(LargeGlowlightJackOLanternSliceBlock.HALF));
                newState = newState.setValue(LargeGlowlightJackOLanternSliceBlock.CARVED_SIDE, state.getValue(LargeGlowlightJackOLanternSliceBlock.CARVED_SIDE));
                level.setBlock(pos, newState, 3);
            } else {
                return;
            }

            Block block = level.getBlockState(pos).getBlock();
            level.playSound(null, pos, block.getSoundType(level.getBlockState(pos), level, pos, null).getPlaceSound(), SoundSource.BLOCKS, 1.0F, 1.0F);

            if (!event.getEntity().isCreative()) {
                itemStack.shrink(1);
            }

            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
        }
    }
}
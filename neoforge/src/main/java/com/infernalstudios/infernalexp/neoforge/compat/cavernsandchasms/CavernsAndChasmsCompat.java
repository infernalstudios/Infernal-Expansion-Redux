package com.infernalstudios.infernalexp.neoforge.compat.cavernsandchasms;

import com.infernalstudios.infernalexp.module.ModBlocks;
import com.infernalstudios.infernalexp.module.ModCreativeTabs;
import com.infernalstudios.infernalexp.registration.holders.BlockDataHolder;
import com.teamabnormals.caverns_and_chasms.common.block.BrazierBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class CavernsAndChasmsCompat {
    public static BlockDataHolder<?> GLOWLIGHT_BRAZIER;

    public static void register(IEventBus eventBus) {
        GLOWLIGHT_BRAZIER = ModBlocks.register("glowlight_brazier", BlockDataHolder.of(() ->
                        new GlowlightBrazierBlock(2.0F, BlockBehaviour.Properties.of()
                                .requiresCorrectToolForDrops()
                                .strength(3.5F)
                                .sound(SoundType.LANTERN)
                                .noOcclusion()
                                .lightLevel((state) -> state.getValue(BrazierBlock.LIT) ? 15 : 0)))
                .cutout()
                .withItem()
                .withTranslation("Glowlight Brazier"));

        eventBus.addListener(CavernsAndChasmsCompat::addCreative);
        NeoForge.EVENT_BUS.addListener(CavernsAndChasmsCompat::onRightClickBlock);
    }

    private static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == ModCreativeTabs.INFERNAL_EXPANSION_TAB.get()) {
            event.accept(GLOWLIGHT_BRAZIER.get());
        }
    }

    private static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        ItemStack itemStack = event.getItemStack();
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);

        if (itemStack.is(ModBlocks.GLOWLIGHT_TORCH.get().asItem())) {
            ResourceLocation id = BuiltInRegistries.BLOCK.getKey(state.getBlock());

            if ("caverns_and_chasms".equals(id.getNamespace()) && "brazier".equals(id.getPath())) {
                BlockState newState = GLOWLIGHT_BRAZIER.get().defaultBlockState()
                        .setValue(BrazierBlock.LIT, state.getValue(BrazierBlock.LIT))
                        .setValue(BrazierBlock.HANGING, state.getValue(BrazierBlock.HANGING))
                        .setValue(BrazierBlock.WATERLOGGED, state.getValue(BrazierBlock.WATERLOGGED));

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
package com.infernalstudios.infernalexp.mixin;

import com.infernalstudios.infernalexp.block.ShroomlightTearBlock;
import com.infernalstudios.infernalexp.module.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BoneMealItem.class)
public class BoneMealItemMixin {

    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    public void useOn(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();

        if (world.getBlockState(pos).is(Blocks.SHROOMLIGHT)) {
            BlockPos targetPos = world.getBiome(pos).is(Biomes.WARPED_FOREST) ? pos.above() : pos.below();

            if (world.getBlockState(targetPos).isAir()) {
                if (!world.isClientSide) {
                    context.getItemInHand().shrink(1);
                    world.levelEvent(1505, pos, 0);

                    BlockState tear = ModBlocks.SHROOMLIGHT_TEAR.get().defaultBlockState();
                    if (world.getBiome(pos).is(Biomes.WARPED_FOREST))
                        world.setBlock(targetPos, tear.setValue(ShroomlightTearBlock.UP, true), Block.UPDATE_ALL);
                    else
                        world.setBlock(targetPos, tear, Block.UPDATE_ALL);
                }
                cir.setReturnValue(InteractionResult.sidedSuccess(world.isClientSide));
            }
        }
    }
}
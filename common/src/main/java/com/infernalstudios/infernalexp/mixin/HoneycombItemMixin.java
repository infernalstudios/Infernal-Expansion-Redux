package com.infernalstudios.infernalexp.mixin;

import com.infernalstudios.infernalexp.module.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HoneycombItem.class)
public class HoneycombItemMixin {

    @Inject(method = "useOn", at = @At("RETURN"), cancellable = true)
    public void infernalexp$waxGlowstone(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        if (cir.getReturnValue() != InteractionResult.PASS) return;

        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        if (state.is(Blocks.GLOWSTONE)) {
            if (player != null) {
                player.swing(context.getHand());
            }

            if (!level.isClientSide) {
                level.setBlock(pos, ModBlocks.WAXED_GLOWSTONE.get().defaultBlockState(), Block.UPDATE_ALL);
            }

            level.levelEvent(player, 3003, pos, 0);

            if (player != null && !player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            cir.setReturnValue(InteractionResult.sidedSuccess(level.isClientSide));
        }
    }
}
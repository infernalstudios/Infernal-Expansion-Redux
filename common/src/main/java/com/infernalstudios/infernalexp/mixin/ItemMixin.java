package com.infernalstudios.infernalexp.mixin;

import com.infernalstudios.infernalexp.module.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {
    @Inject(method = "useOn", at = @At("TAIL"), cancellable = true)
    public void place(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        if (cir.getReturnValue() != InteractionResult.PASS) return;

        BlockPos pos = context.getClickedPos().relative(context.getClickedFace(), 1);

        if (((Item) (Object) this) == Items.QUARTZ) {
            BlockState state = ModBlocks.PLANTED_QUARTZ.get().getStateForPlacement(new BlockPlaceContext(context));
            if (state.canSurvive(context.getLevel(), pos)) {
                context.getLevel().setBlock(pos, state, Block.UPDATE_ALL);
                cir.setReturnValue(InteractionResult.sidedSuccess(context.getLevel().isClientSide));
            }
        }

        if (((Item) (Object) this) == Items.BONE) {
            BlockState state = ModBlocks.BURIED_BONE.get().getStateForPlacement(new BlockPlaceContext(context));
            if (state.canSurvive(context.getLevel(), pos)) {
                context.getLevel().setBlock(pos, state, Block.UPDATE_ALL);
                cir.setReturnValue(InteractionResult.sidedSuccess(context.getLevel().isClientSide));
            }
        }
    }
}

package com.infernalstudios.infernalexp.mixin;

import com.infernalstudios.infernalexp.module.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
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
        Level level = context.getLevel();
        Player player = context.getPlayer();

        if (((Item) (Object) this) == Items.QUARTZ) {
            BlockState state = ModBlocks.PLANTED_QUARTZ.get().getStateForPlacement(new BlockPlaceContext(context));
            if (state.canSurvive(level, pos)) {
                level.setBlock(pos, state, Block.UPDATE_ALL);
                SoundType soundtype = state.getSoundType();
                level.playSound(player, pos, soundtype.getPlaceSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                cir.setReturnValue(InteractionResult.sidedSuccess(level.isClientSide));
            }
        }

        if (((Item) (Object) this) == Items.BONE) {
            BlockState state = ModBlocks.BURIED_BONE.get().getStateForPlacement(new BlockPlaceContext(context));
            if (state.canSurvive(level, pos)) {
                level.setBlock(pos, state, Block.UPDATE_ALL);
                SoundType soundtype = state.getSoundType();
                level.playSound(player, pos, soundtype.getPlaceSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                cir.setReturnValue(InteractionResult.sidedSuccess(level.isClientSide));
            }
        }
    }
}
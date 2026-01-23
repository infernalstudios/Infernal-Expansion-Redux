package com.infernalstudios.infernalexp.mixin;

import com.infernalstudios.infernalexp.entities.ThrowableFireChargeEntity;
import com.infernalstudios.infernalexp.entities.ThrowableMagmaCreamEntity;
import com.infernalstudios.infernalexp.module.ModBlocks;
import com.infernalstudios.infernalexp.module.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    public void use(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        ItemStack heldItemStack = player.getItemInHand(hand);

        if (heldItemStack.is(Items.MAGMA_CREAM)) {
            player.swing(hand);
            if (!level.isClientSide) {
                ThrowableMagmaCreamEntity throwableMagmaCreamEntity = new ThrowableMagmaCreamEntity(level, player);
                throwableMagmaCreamEntity.setItem(heldItemStack);
                throwableMagmaCreamEntity.shootFromRotation(player, player.getXRot(), player.getYRot(), -20, 0.5f, 1);
                level.addFreshEntity(throwableMagmaCreamEntity);
                level.playSound(null, player.blockPosition(), SoundEvents.SNOWBALL_THROW, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            player.awardStat(Stats.ITEM_USED.get(heldItemStack.getItem()));
            if (!player.getAbilities().instabuild) {
                heldItemStack.shrink(1);
            }
            cir.setReturnValue(InteractionResultHolder.sidedSuccess(heldItemStack, level.isClientSide()));
        } else if (heldItemStack.is(Items.FIRE_CHARGE)) {
            player.swing(hand);
            if (!level.isClientSide) {
                ThrowableFireChargeEntity throwableFireChargeEntity = new ThrowableFireChargeEntity(level, player, player.getLookAngle().x(), player.getLookAngle().y(), player.getLookAngle().z());
                level.addFreshEntity(throwableFireChargeEntity);
                level.playSound(null, player.blockPosition(), SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            player.awardStat(Stats.ITEM_USED.get(heldItemStack.getItem()));
            if (!player.getAbilities().instabuild) {
                heldItemStack.shrink(1);
            }
            cir.setReturnValue(InteractionResultHolder.sidedSuccess(heldItemStack, level.isClientSide()));
        }
    }

    @Inject(method = "useOn", at = @At("TAIL"), cancellable = true)
    public void place(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        if (cir.getReturnValue() != InteractionResult.PASS) return;

        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        if (stack.is(Items.GLOWSTONE_DUST)) {
            BlockState state = level.getBlockState(pos);
            BlockState newState = null;

            if (state.is(ModBlocks.DULLSTONE.get())) {
                newState = ModBlocks.DIMSTONE.get().defaultBlockState();
            } else if (state.is(ModBlocks.DIMSTONE.get())) {
                newState = Blocks.GLOWSTONE.defaultBlockState();
            }

            if (newState != null) {
                level.setBlock(pos, newState, Block.UPDATE_ALL);
                level.playSound(player, pos, ModSounds.BLOCK_DULLSTONE_BREAK.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                if (player != null && !player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                cir.setReturnValue(InteractionResult.sidedSuccess(level.isClientSide));
                return;
            }
        }

        BlockPos relativePos = pos.relative(context.getClickedFace(), 1);

        if ((Object) this == Items.QUARTZ) {
            BlockState state = ModBlocks.PLANTED_QUARTZ.get().getStateForPlacement(new BlockPlaceContext(context));
            if (state.canSurvive(level, relativePos)) {
                level.setBlock(relativePos, state, Block.UPDATE_ALL);
                SoundType soundtype = state.getSoundType();
                level.playSound(player, relativePos, soundtype.getPlaceSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                cir.setReturnValue(InteractionResult.sidedSuccess(level.isClientSide));
            }
        }

        if ((Object) this == Items.BONE) {
            BlockState state = ModBlocks.BURIED_BONE.get().getStateForPlacement(new BlockPlaceContext(context));
            if (state.canSurvive(level, relativePos)) {
                level.setBlock(relativePos, state, Block.UPDATE_ALL);
                SoundType soundtype = state.getSoundType();
                level.playSound(player, relativePos, soundtype.getPlaceSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                cir.setReturnValue(InteractionResult.sidedSuccess(level.isClientSide));
            }
        }
    }
}
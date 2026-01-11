package com.infernalstudios.infernalexp.entities;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.Optional;

public interface IBucketable {
    boolean infernalexp$isFromBucket();

    void infernalexp$setFromBucket(boolean isFromBucket);

    void infernalexp$copyToStack(ItemStack stack);

    void infernalexp$copyFromAdditional(CompoundTag compound);

    ItemStack infernalexp$getBucketItem();

    SoundEvent infernalexp$getBucketedSound();

    static void copyDataToStack(Mob entity, ItemStack stack) {
        CompoundTag compound = stack.getOrCreateTag();
        if (entity.hasCustomName()) {
            stack.setHoverName(entity.getCustomName());
        }

        if (entity.isNoAi()) {
            compound.putBoolean("NoAI", entity.isNoAi());
        }

        if (entity.isSilent()) {
            compound.putBoolean("Silent", entity.isSilent());
        }

        if (entity.isNoGravity()) {
            compound.putBoolean("NoGravity", entity.isNoGravity());
        }

        if (entity.isCurrentlyGlowing()) {
            compound.putBoolean("Glowing", entity.isCurrentlyGlowing());
        }

        if (entity.isInvulnerable()) {
            compound.putBoolean("Invulnerable", entity.isInvulnerable());
        }

        compound.putFloat("Health", entity.getHealth());
    }

    static void copyDataFromAdditional(Mob entity, CompoundTag compound) {
        if (compound.contains("NoAI")) {
            entity.setNoAi(compound.getBoolean("NoAI"));
        }

        if (compound.contains("Silent")) {
            entity.setSilent(compound.getBoolean("Silent"));
        }

        if (compound.contains("NoGravity")) {
            entity.setNoGravity(compound.getBoolean("NoGravity"));
        }

        if (compound.contains("Glowing")) {
            entity.setGlowingTag(compound.getBoolean("Glowing"));
        }

        if (compound.contains("Invulnerable")) {
            entity.setInvulnerable(compound.getBoolean("Invulnerable"));
        }

        if (compound.contains("Health", 99)) {
            entity.setHealth(compound.getFloat("Health"));
        }
    }

    static <T extends LivingEntity & IBucketable> Optional<InteractionResult> tryBucketEntity(Player player, InteractionHand hand, T entity) {
        ItemStack heldItem = player.getItemInHand(hand);
        if (heldItem.getItem() == Items.LAVA_BUCKET && entity.isAlive()) {
            entity.playSound(entity.infernalexp$getBucketedSound(), 1.0F, 1.0F);

            ItemStack bucketItem = entity.infernalexp$getBucketItem();
            entity.infernalexp$copyToStack(bucketItem);

            ItemStack resultStack = ItemUtils.createFilledResult(heldItem, player, bucketItem, false);
            player.setItemInHand(hand, resultStack);

            Level world = entity.level();
            if (!world.isClientSide) {
                CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer) player, bucketItem);
            }

            entity.discard();

            return Optional.of(InteractionResult.sidedSuccess(world.isClientSide));
        } else {
            return Optional.empty();
        }
    }

    class ItemUtils {
        public static ItemStack createFilledResult(ItemStack emptyStack, Player player, ItemStack filledStack, boolean preventCreativeDupes) {
            if (player.getAbilities().instabuild && preventCreativeDupes) {
                return emptyStack;
            }
            emptyStack.shrink(1);
            if (emptyStack.isEmpty()) {
                return filledStack;
            } else {
                if (!player.getInventory().add(filledStack)) {
                    player.drop(filledStack, false);
                }
                return emptyStack;
            }
        }
    }
}
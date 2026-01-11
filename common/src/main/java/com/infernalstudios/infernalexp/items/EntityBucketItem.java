package com.infernalstudios.infernalexp.items;

import com.infernalstudios.infernalexp.entities.IBucketable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class EntityBucketItem extends BucketItem {
    private final Supplier<EntityType<?>> entityTypeSupplier;
    private final Supplier<? extends SoundEvent> emptyingSoundSupplier;

    public EntityBucketItem(Supplier<EntityType<?>> entityType, Fluid fluid, Supplier<? extends SoundEvent> emptyingSound, Properties properties) {
        super(fluid, properties);
        this.entityTypeSupplier = entityType;
        this.emptyingSoundSupplier = emptyingSound;
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        if (world instanceof ServerLevel) {
            this.checkExtraContent(player, world, stack, pos);
        } else {
            world.playSound(player, pos, this.emptyingSoundSupplier.get(), SoundSource.NEUTRAL, 1.0F, 1.0F);
        }

        if (player != null && !player.getAbilities().instabuild) {
            player.setItemInHand(context.getHand(), new ItemStack(Items.LAVA_BUCKET));
        }

        return InteractionResult.sidedSuccess(world.isClientSide);
    }

    @Override
    public void checkExtraContent(@Nullable Player player, @NotNull Level world, @NotNull ItemStack stack, @NotNull BlockPos pos) {
        if (world instanceof ServerLevel serverLevel) {
            this.spawn(serverLevel, stack, pos);
            world.gameEvent(player, GameEvent.ENTITY_PLACE, pos);
            this.playEmptySound(player, world, pos);
        }
    }

    @Override
    protected void playEmptySound(@Nullable Player player, LevelAccessor world, @NotNull BlockPos pos) {
        world.playSound(player, pos, this.emptyingSoundSupplier.get(), SoundSource.NEUTRAL, 1.0F, 1.0F);
    }

    private void spawn(ServerLevel world, ItemStack stack, BlockPos pos) {
        var entity = this.entityTypeSupplier.get().spawn(world, stack, null, pos, MobSpawnType.BUCKET, true, false);
        if (entity instanceof IBucketable bucketable) {
            bucketable.infernalexp$setFromBucket(true);
            bucketable.infernalexp$copyFromAdditional(stack.getOrCreateTag());
        }
    }
}
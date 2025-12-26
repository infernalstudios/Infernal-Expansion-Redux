package com.infernalstudios.infernalexp.block.entity;

import com.infernalstudios.infernalexp.block.VolatileGeyserBlock;
import com.infernalstudios.infernalexp.module.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class VolatileGeyserBlockEntity extends BlockEntity implements GeoBlockEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int cooldown = 0;

    public VolatileGeyserBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.VOLATILE_GEYSER.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, VolatileGeyserBlockEntity entity) {
        boolean isPowered = state.getValue(VolatileGeyserBlock.POWERED);

        if (isPowered) {
            entity.performBurst(level, pos, state, 0.2f);

            if (level.isClientSide && level.getGameTime() % 10 == 0) {
                level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.1F, 0.5F + level.random.nextFloat() * 0.2F, false);
            }
        } else {
            if (!level.isClientSide) {
                if (entity.cooldown > 0) {
                    entity.cooldown--;
                } else {
                    entity.cooldown = 40 + level.random.nextInt(60);
                    level.blockEvent(pos, state.getBlock(), 1, 0);
                }
            }
        }
    }

    private void performBurst(Level level, BlockPos pos, BlockState state, float strengthMultiplier) {
        Direction facing = state.getValue(VolatileGeyserBlock.FACING);

        if (level.isClientSide) {
            boolean isBigBurst = strengthMultiplier >= 1.0f;

            if (isBigBurst || level.getGameTime() % 2 == 0) {
                int particleCount = isBigBurst ? 20 : 3;

                for(int i = 0; i < particleCount; i++) {
                    double speed = 0.05 + (strengthMultiplier * 0.25);

                    double x = pos.getX() + 0.5 + (facing.getStepX() * 0.4);
                    double y = pos.getY() + 0.5 + (facing.getStepY() * 0.4);
                    double z = pos.getZ() + 0.5 + (facing.getStepZ() * 0.4);

                    double velX = facing.getStepX() * speed;
                    double velY = facing.getStepY() * speed;
                    double velZ = facing.getStepZ() * speed;

                    double spread = isBigBurst ? 0.3 : 0.1;
                    double randX = (level.random.nextDouble() - 0.5) * spread;
                    double randY = (level.random.nextDouble() - 0.5) * spread;
                    double randZ = (level.random.nextDouble() - 0.5) * spread;

                    double velSpread = speed * 0.2;
                    double randVelX = (level.random.nextDouble() - 0.5) * velSpread;
                    double randVelY = (level.random.nextDouble() - 0.5) * velSpread;
                    double randVelZ = (level.random.nextDouble() - 0.5) * velSpread;

                    level.addParticle(ParticleTypes.CLOUD, x + randX, y + randY, z + randZ, velX + randVelX, velY + randVelY, velZ + randVelZ);
                }

                if (isBigBurst) {
                    level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 1.0F, false);
                }
            }
        }

        if (!level.isClientSide) {
            AABB effectBox = new AABB(pos).expandTowards(
                    facing.getStepX() * 2.0,
                    facing.getStepY() * 2.0,
                    facing.getStepZ() * 2.0
            );

            List<Entity> entities = level.getEntitiesOfClass(Entity.class, effectBox);

            for (Entity target : entities) {
                double velocityScale;

                if (target instanceof ItemEntity) {
                    velocityScale = 1.3;
                } else if (target instanceof LivingEntity) {
                    if (target instanceof Player player && player.isFallFlying()) {
                        velocityScale = 1.5;
                        player.fallDistance = 0;
                    } else {
                        velocityScale = 0.9;
                    }
                } else {
                    velocityScale = 0.5;
                }

                Vec3 motion = new Vec3(facing.getStepX(), facing.getStepY(), facing.getStepZ())
                        .normalize().scale(velocityScale * strengthMultiplier);

                target.setDeltaMovement(target.getDeltaMovement().add(motion));
                target.hurtMarked = true;
            }
        }
    }

    @Override
    public boolean triggerEvent(int id, int type) {
        if (id == 1) {
            if (this.level != null) {
                this.performBurst(this.level, this.worldPosition, this.getBlockState(), 1.0f);
            }
            return true;
        }
        return super.triggerEvent(id, type);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // controllers.add(new AnimationController<>(this, "controller", 0, state -> state.setAndContinue(RawAnimation.begin().thenLoop("animation.volatile_geyser.idle"))));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
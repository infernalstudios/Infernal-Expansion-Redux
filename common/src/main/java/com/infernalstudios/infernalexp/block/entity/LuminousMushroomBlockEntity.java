package com.infernalstudios.infernalexp.block.entity;

import com.infernalstudios.infernalexp.IECommon;
import com.infernalstudios.infernalexp.block.LuminousMushroomBlock;
import com.infernalstudios.infernalexp.module.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class LuminousMushroomBlockEntity extends BlockEntity {
    private int lightTime = 0;

    public LuminousMushroomBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.LUMINOUS_MUSHROOM.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, LuminousMushroomBlockEntity entity) {
        if (level.getGameTime() % 5 != 0) {
            if (entity.lightTime > 0) entity.lightTime--;
            return;
        }

        double distance = IECommon.getConfig().common.miscellaneous.LuminousMushroomActivateDistance;
        AABB area = new AABB(pos).inflate(distance);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area);

        if (!entities.isEmpty()) {
            entity.lightTime = 60;
            if (!state.getValue(LuminousMushroomBlock.LIT)) {
                level.setBlock(pos, state.setValue(LuminousMushroomBlock.LIT, true), 3);
            }
        } else {
            if (entity.lightTime > 0) {
                entity.lightTime -= 5;
            } else if (state.getValue(LuminousMushroomBlock.LIT)) {
                level.setBlock(pos, state.setValue(LuminousMushroomBlock.LIT, false), 3);
            }
        }
    }
}
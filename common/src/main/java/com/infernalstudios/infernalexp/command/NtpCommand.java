package com.infernalstudios.infernalexp.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public class NtpCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("ntp")
                .requires(source -> source.hasPermission(2))
                .executes(NtpCommand::execute)
        );
    }

    private static int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        ServerLevel nether = context.getSource().getServer().getLevel(Level.NETHER);

        if (nether == null) {
            context.getSource().sendFailure(Component.translatable("commands.infernalexp.ntp.not_found"));
            return 0;
        }

        if (player.level() == nether) {
            context.getSource().sendFailure(Component.translatable("commands.infernalexp.ntp.already_in_nether"));
            return 0;
        }

        double scale = player.level().dimension() == Level.OVERWORLD ? 0.125D : 1.0D;
        int targetX = (int) Math.floor(player.getX() * scale);
        int targetZ = (int) Math.floor(player.getZ() * scale);

        int safeY = findSafeY(nether, targetX, targetZ);

        if (safeY == -1) {
            context.getSource().sendSuccess(() -> Component.translatable("commands.infernalexp.ntp.failed"), false);
        } else {
            context.getSource().sendSuccess(() -> Component.translatable("commands.infernalexp.ntp.success"), false);
        }

        player.teleportTo(nether, targetX + 0.5, safeY, targetZ + 0.5, player.getYRot(), player.getXRot());

        return 1;
    }

    private static int findSafeY(ServerLevel level, int x, int z) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(x, 110, z);
        for (int y = 110; y > 32; y--) {
            pos.setY(y);
            if (level.getBlockState(pos).isAir() && level.getBlockState(pos.above()).isAir()) {
                if (level.getBlockState(pos.below()).canOcclude()) {
                    return y;
                }
            }
        }
        return -1;
    }

}
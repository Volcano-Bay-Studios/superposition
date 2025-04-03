package org.modogthedev.superposition.core;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.modogthedev.superposition.system.cable.Cable;
import org.modogthedev.superposition.system.cable.CableManager;
import org.modogthedev.superposition.system.cable.rope_system.RopeNode;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SuperpositionCommands {
    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(registerSuperpositionCommand());
    }

    public static LiteralArgumentBuilder<CommandSourceStack> registerSuperpositionCommand() {

        return Commands.literal("superposition").requires(stack -> stack.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.literal("removeCables").then(Commands.argument("radius", FloatArgumentType.floatArg(0, 1000000))
                        .executes(commandContext ->
                                removeCables(commandContext.getSource(), commandContext.getSource().getLevel(), commandContext.getSource().getPosition(), FloatArgumentType.getFloat(commandContext,"radius"))))
        );
    }

    private static int removeCables(CommandSourceStack stack, Level level, Vec3 position, float radius) {
        int i = 0;
        if (!level.isClientSide) {
            List<UUID> forRemoval = new ArrayList<>();
            for (Cable cable : CableManager.getLevelCables(level)) {
                if (cable.getPoints().getFirst().getPosition().lerp(cable.getPoints().getLast().getPosition(), 0.5f).distanceTo(position) <= (radius + (cable.getPoints().size() * SuperpositionConstants.cableRadius / 2f))) {
                    for (RopeNode node : cable.getPoints()) {
                        if (node.getPosition().distanceTo(position) < radius) {
                            forRemoval.add(cable.getId());
                            break;
                        }
                    }
                }
            }
            for (UUID id : forRemoval) {
                CableManager.removeCable(level, id);
                i++;
            }
            if (forRemoval.isEmpty()) {
                stack.sendSuccess(() -> Component.translatable("commands.superposition.removed_fail"), true);
            } else {
                stack.sendSuccess(() -> Component.translatable("commands.superposition.removed_success", forRemoval.size()), true);
            }
        }
        return i;
    }
}

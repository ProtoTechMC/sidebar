package xyz.prototech.protosidebar.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.argument.ObjectiveArgumentType;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SidebarCommand {
    private static final SimpleCommandExceptionType OBJECTIVES_DISPLAY_ALREADY_EMPTY_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.scoreboard.objectives.display.alreadyEmpty"));
    private static final SimpleCommandExceptionType OBJECTIVES_DISPLAY_ALREADY_SET_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.scoreboard.objectives.display.alreadySet"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher){
        LiteralArgumentBuilder<ServerCommandSource> sidebar = literal("sidebar").
                requires(player -> true).
                executes(SidebarCommand::clearSidebar).
                then(argument("objective", ObjectiveArgumentType.objective())
                        .executes(SidebarCommand::setSidebar));

        dispatcher.register(sidebar);
    }

    public static int clearSidebar(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        Scoreboard scoreboard = source.getMinecraftServer().getScoreboard();
        int slot = Scoreboard.getDisplaySlotId("sidebar");
        if (scoreboard.getObjectiveForSlot(slot) == null) {
            throw OBJECTIVES_DISPLAY_ALREADY_EMPTY_EXCEPTION.create();
        } else {
            scoreboard.setObjectiveSlot(slot, null);
            source.sendFeedback(new TranslatableText(
                    "commands.scoreboard.objectives.display.cleared",
                    Scoreboard.getDisplaySlotNames()[slot]
            ), true);
            return 0;
        }
    }

    public static int setSidebar(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {ServerCommandSource source = context.getSource();
        Scoreboard scoreboard = source.getMinecraftServer().getScoreboard();
        ScoreboardObjective objective = ObjectiveArgumentType.getObjective(context, "objective");
        int slot = Scoreboard.getDisplaySlotId("sidebar");

        if (scoreboard.getObjectiveForSlot(slot) == objective) {
            throw OBJECTIVES_DISPLAY_ALREADY_SET_EXCEPTION.create();
        } else {
            scoreboard.setObjectiveSlot(slot, objective);
            source.sendFeedback(new TranslatableText(
                    "commands.scoreboard.objectives.display.set",
                    Scoreboard.getDisplaySlotNames()[slot],
                    objective.getDisplayName()
            ), true);
            return 0;
        }
    }
}

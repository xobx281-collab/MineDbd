package dev.projectentity;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import dev.projectentity.game.MatchManager;
import dev.projectentity.game.MatchPhase;
import dev.projectentity.game.MatchState;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ProjectEntity implements ModInitializer {
    public static final String MOD_ID = "project_entity";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> MatchManager.reset());
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> MatchManager.reset());
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
            dispatcher.register(Commands.literal("entity")
                .then(Commands.literal("status").executes(context -> {
                    MatchState state = MatchManager.current();
                    context.getSource().sendSuccess(() -> Component.literal(
                        "Project Entity | Phase: " + state.phase()
                            + " | Generators: " + state.completedGeneratorCount()
                            + "/" + state.requiredGenerators()
                    ), false);
                    return 1;
                }))
                .then(Commands.literal("debug")
                    .requires(source -> source.hasPermission(2))
                    .then(Commands.literal("start").executes(context ->
                        reportTransition(context.getSource(), MatchManager.current(),
                            MatchManager.current().beginPreparation(), "Preparation started")))
                    .then(Commands.literal("countdown").executes(context ->
                        reportTransition(context.getSource(), MatchManager.current(),
                            MatchManager.current().beginCountdown(), "Countdown started")))
                    .then(Commands.literal("begin").executes(context ->
                        reportTransition(context.getSource(), MatchManager.current(),
                            MatchManager.current().beginMatch(), "Active match started")))
                    .then(Commands.literal("complete_generator")
                        .then(Commands.argument("id", IntegerArgumentType.integer())
                            .executes(context -> {
                                MatchState state = MatchManager.current();
                                int id = IntegerArgumentType.getInteger(context, "id");
                                boolean accepted = state.completeGenerator(id);
                                String message = accepted
                                    ? "Generator " + id + " completed. Phase: " + state.phase()
                                    : "Generator completion rejected. Check match phase and generator ID (0-6).";
                                context.getSource().sendSuccess(() -> Component.literal(message), false);
                                return accepted ? 1 : 0;
                            })))
                    .then(Commands.literal("killer_win").executes(context ->
                        reportTransition(context.getSource(), MatchManager.current(),
                            MatchManager.current().finish(true), "Killer victory recorded")))
                    .then(Commands.literal("survivor_win").executes(context ->
                        reportTransition(context.getSource(), MatchManager.current(),
                            MatchManager.current().finish(false), "Survivor victory recorded")))
                    .then(Commands.literal("lobby").executes(context ->
                        reportTransition(context.getSource(), MatchManager.current(),
                            MatchManager.current().returnToLobby(), "Returned to lobby")))
                    .then(Commands.literal("reset").executes(context -> {
                        MatchManager.current().reset();
                        context.getSource().sendSuccess(() -> Component.literal("Match state reset."), false);
                        return 1;
                    }))
                )
            )
        );

        LOGGER.info("Project Entity initialized. Core match-state scaffold is available for operator testing.");
    }

    private static int reportTransition(
        net.minecraft.commands.CommandSourceStack source,
        MatchState state,
        boolean accepted,
        String successMessage
    ) {
        String message = accepted
            ? successMessage + ". Current phase: " + state.phase()
            : "Action rejected in phase " + state.phase() + ".";
        source.sendSuccess(() -> Component.literal(message), false);
        return accepted ? 1 : 0;
    }
}

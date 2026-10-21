package dev.projectentity;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import dev.projectentity.game.MatchManager;
import dev.projectentity.game.MatchPhase;
import dev.projectentity.game.MatchState;
import dev.projectentity.game.ObjectiveBlocks;
import dev.projectentity.game.ObjectiveProgress;
import dev.projectentity.game.PlayerRole;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ProjectEntity implements ModInitializer {
    public static final String MOD_ID = "project_entity";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        // Force registration of the custom objective blocks and their items.
        ObjectiveBlocks.GENERATOR.getDescriptionId();
        ServerLifecycleEvents.SERVER_STARTED.register(server -> MatchManager.reset());
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> MatchManager.reset());

        UseBlockCallback.EVENT.register((player, world, hand, hit) -> {
            if (world.isClientSide()) return InteractionResult.PASS;
            var clicked = world.getBlockState(hit.getBlockPos());
            var role = MatchManager.role(player.getUUID());
            MatchState match = MatchManager.current();

            if (clicked.is(ObjectiveBlocks.GENERATOR)) {
                if (role != PlayerRole.SURVIVOR) {
                    player.displayClientMessage(Component.literal("Only survivors can repair generators. Use /entity role survivor."), true);
                    return InteractionResult.SUCCESS;
                }
                int progress = ObjectiveProgress.repair(hit.getBlockPos(), match);
                if (progress == -1) {
                    player.displayClientMessage(Component.literal("Generators can only be repaired during an active match."), true);
                } else if (progress == -2) {
                    player.displayClientMessage(Component.literal("All seven generator slots are already assigned."), true);
                } else if (progress == -3) {
                    player.displayClientMessage(Component.literal("This generator is already complete."), true);
                } else if (progress >= ObjectiveProgress.CLICKS_TO_REPAIR) {
                    player.displayClientMessage(Component.literal("Generator repaired! " + match.completedGeneratorCount()
                        + "/" + match.requiredGenerators() + " required. Phase: " + match.phase()), false);
                    if (match.phase() == MatchPhase.ENDGAME) {
                        player.displayClientMessage(Component.literal("The exit gates are powered. Find an Exit Gate block and activate it."), false);
                    }
                } else {
                    player.displayClientMessage(Component.literal("Repairing generator: " + progress
                        + "/" + ObjectiveProgress.CLICKS_TO_REPAIR + " | Completed: "
                        + match.completedGeneratorCount() + "/" + match.requiredGenerators()), true);
                }
                return InteractionResult.SUCCESS;
            }

            if (clicked.is(ObjectiveBlocks.EXIT_GATE)) {
                if (role != PlayerRole.SURVIVOR) {
                    player.displayClientMessage(Component.literal("Only survivors can escape through the exit gate."), true);
                } else if (match.phase() != MatchPhase.ENDGAME) {
                    player.displayClientMessage(Component.literal("The exit gate has no power. Complete five generators first."), true);
                } else if (match.finish(false)) {
                    player.displayClientMessage(Component.literal("ESCAPED — Survivor victory! Use /entity reset before another test."), false);
                } else {
                    player.displayClientMessage(Component.literal("The match has already ended."), true);
                }
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
            dispatcher.register(Commands.literal("entity")
                .then(Commands.literal("status").executes(context -> {
                    MatchState state = MatchManager.current();
                    var player = context.getSource().getEntity();
                    PlayerRole role = player == null ? null : MatchManager.role(player.getUUID());
                    context.getSource().sendSuccess(() -> Component.literal(
                        "Project Entity | Phase: " + state.phase()
                            + " | Role: " + (role == null ? "unselected" : role)
                            + " | Generators: " + state.completedGeneratorCount()
                            + "/" + state.requiredGenerators()
                    ), false);
                    return 1;
                }))
                .then(Commands.literal("role")
                    .then(Commands.argument("role", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            builder.suggest("survivor");
                            builder.suggest("killer");
                            return builder.buildFuture();
                        })
                        .executes(context -> {
                            String requested = StringArgumentType.getString(context, "role");
                            PlayerRole role;
                            if (requested.equalsIgnoreCase("survivor")) role = PlayerRole.SURVIVOR;
                            else if (requested.equalsIgnoreCase("killer")) role = PlayerRole.KILLER;
                            else {
                                context.getSource().sendFailure(Component.literal("Choose survivor or killer."));
                                return 0;
                            }
                            var player = context.getSource().getPlayerOrException();
                            MatchManager.setRole(player.getUUID(), role);
                            context.getSource().sendSuccess(() -> Component.literal("Role selected: " + role), false);
                            return 1;
                        })))
                .then(Commands.literal("demo")
                    .executes(context -> {
                        var player = context.getSource().getPlayerOrException();
                        MatchState state = MatchManager.current();
                        if (state.phase() != MatchPhase.LOBBY) {
                            context.getSource().sendFailure(Component.literal("A match is already in progress. Use /entity reset as an operator to restart."));
                            return 0;
                        }
                        MatchManager.setRole(player.getUUID(), PlayerRole.SURVIVOR);
                        state.beginPreparation();
                        state.beginCountdown();
                        state.beginMatch();
                        player.getInventory().add(new ItemStack(ObjectiveBlocks.GENERATOR, 7));
                        player.getInventory().add(new ItemStack(ObjectiveBlocks.EXIT_GATE, 2));
                        ServerLevel level = player.serverLevel();
                        Husk killer = EntityType.HUSK.create(level);
                        if (killer != null) {
                            killer.setPos(player.getX() + 8.0, player.getY(), player.getZ() + 8.0);
                            killer.setCustomName(Component.literal("The Killer"));
                            killer.setCustomNameVisible(true);
                            killer.addTag("project_entity_killer");
                            killer.setPersistenceRequired();
                            killer.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_AXE));
                            killer.setTarget(player);
                            level.addFreshEntity(killer);
                        }
                        context.getSource().sendSuccess(() -> Component.literal(
                            "Solo demo started. Role: SURVIVOR. A hostile killer is hunting you. Place seven Generators and two Exit Gates, then right-click each generator eight times. Five completed generators power the gates."), false);
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
                    .then(Commands.literal("lobby").executes(context -> {
                        boolean accepted = MatchManager.current().returnToLobby();
                        if (accepted) ObjectiveProgress.reset();
                        return reportTransition(context.getSource(), MatchManager.current(), accepted, "Returned to lobby");
                    }))
                    .then(Commands.literal("reset").executes(context -> {
                        for (ServerLevel level : context.getSource().getServer().getAllLevels()) {
                            for (Entity entity : level.getAllEntities()) {
                                if (entity.getTags().contains("project_entity_killer")) {
                                    entity.discard();
                                }
                            }
                        }
                        MatchManager.reset();
                        context.getSource().sendSuccess(() -> Component.literal("Match state, roles, objectives and demo killer reset."), false);
                        return 1;
                    }))
                )
            )
        );

        LOGGER.info("Project Entity initialized. In-world generator and exit-gate objectives registered.");
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
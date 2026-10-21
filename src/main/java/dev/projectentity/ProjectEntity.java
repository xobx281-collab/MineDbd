package dev.projectentity;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import dev.projectentity.game.MatchManager;
import dev.projectentity.game.MatchPhase;
import dev.projectentity.game.MatchState;
import dev.projectentity.game.ObjectiveBlocks;
import dev.projectentity.game.ObjectiveProgress;
import dev.projectentity.game.PlayerRole;
import dev.projectentity.game.SurvivorCondition;
import dev.projectentity.game.SurvivorState;
import dev.projectentity.game.SurvivorBotAI;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public final class ProjectEntity implements ModInitializer {
    public static final String MOD_ID = "project_entity";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        // Force registration of the custom objective blocks and their items.
        ObjectiveBlocks.GENERATOR.getDescriptionId();
        ServerLifecycleEvents.SERVER_STARTED.register(server -> MatchManager.reset());
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> MatchManager.reset());
        SurvivorBotAI.register();

        // A hit from the tagged demo killer becomes a server-owned two-hit condition
        // rather than normal Minecraft damage. Full downed/carry/rescue presentation
        // is a later milestone; this keeps the early test loop from instantly killing players.
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if (!(entity instanceof LivingEntity living)) return true;
            var attacker = source.getEntity();
            if (attacker == null || !attacker.getTags().contains("project_entity_killer")) return true;
            MatchState match = MatchManager.current();
            if (match.phase() != MatchPhase.ACTIVE && match.phase() != MatchPhase.ENDGAME) return true;

            if (living instanceof ServerPlayer player) {
                if (MatchManager.role(player.getUUID()) != PlayerRole.SURVIVOR) return true;
            } else if (!living.getTags().contains(SurvivorBotAI.BOT_TAG)) {
                return true;
            }

            SurvivorState survivor = MatchManager.survivorState(living.getUUID());
            boolean changed = survivor.takeHit();
            SurvivorCondition condition = survivor.condition();
            if (condition == SurvivorCondition.INJURED) {
                living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 0));
                if (living instanceof ServerPlayer player) {
                    player.displayClientMessage(Component.literal("INJURED — the killer has hit you once."), false);
                }
            } else if (condition == SurvivorCondition.DOWNED) {
                living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 120, 3));
                if (living instanceof ServerPlayer player) {
                    player.displayClientMessage(Component.literal("DOWNED — you need help to recover. Carry and rescue interactions are still in development."), false);
                }
                if (MatchManager.allSurvivorsDowned()) {
                    finishMatch((ServerLevel) living.level(), true);
                }
            } else if (!changed) {
                living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 3));
                if (living instanceof ServerPlayer player) {
                    player.displayClientMessage(Component.literal("You are downed. Escape and rescue mechanics are not yet implemented."), true);
                }
            }
            return false;
        });

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
                } else if (finishMatch(player.serverLevel(), false)) {
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
                            + " | Survivor: " + (player == null ? "n/a" : MatchManager.survivorState(player.getUUID()).condition())
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
                        ServerLevel level = player.serverLevel();
                        Set<BlockPos> usedPositions = new HashSet<>();
                        int placedGenerators = placeObjectives(level, player.blockPosition(), ObjectiveBlocks.GENERATOR,
                            new int[][]{{12, 0}, {8, 8}, {0, 12}, {-8, 8}, {-12, 0}, {-8, -8}, {0, -12}}, usedPositions);
                        int placedGates = placeObjectives(level, player.blockPosition(), ObjectiveBlocks.EXIT_GATE,
                            new int[][]{{18, 4}, {-18, -4}}, usedPositions);
                        if (placedGenerators < 7) {
                            player.getInventory().add(new ItemStack(ObjectiveBlocks.GENERATOR, 7 - placedGenerators));
                        }
                        if (placedGates < 2) {
                            player.getInventory().add(new ItemStack(ObjectiveBlocks.EXIT_GATE, 2 - placedGates));
                        }
                        Husk killer = EntityType.HUSK.create(level);
                        if (killer != null) {
                            BlockPos killerPos = findNearbySurface(level, player.blockPosition(), 8, 6, usedPositions);
                            if (killerPos == null) killerPos = player.blockPosition().offset(8, 0, 6);
                            killer.setPos(killerPos.getX() + 0.5, killerPos.getY(), killerPos.getZ() + 0.5);
                            killer.setCustomName(Component.literal("The Killer"));
                            killer.setCustomNameVisible(true);
                            killer.addTag("project_entity_killer");
                            killer.setPersistenceRequired();
                            killer.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_AXE));
                            killer.setTarget(player);
                            level.addFreshEntity(killer);
                        }
                        int bots = SurvivorBotAI.spawnFor(player, 3);
                        int finalGenerators = placedGenerators;
                        int finalGates = placedGates;
                        context.getSource().sendSuccess(() -> Component.literal(
                            "Solo demo started. Role: SURVIVOR. Spawned a killer and " + bots + " survivor bots. Auto-placed " + finalGenerators
                                + "/7 generators and " + finalGates + "/2 gates near you; remaining objectives are in your inventory."), false);
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
                        var objectivePositions = ObjectiveProgress.allObjectivePositions();
                        for (ServerLevel level : context.getSource().getServer().getAllLevels()) {
                            for (var pos : objectivePositions) {
                                var block = level.getBlockState(pos);
                                if (block.is(ObjectiveBlocks.GENERATOR) || block.is(ObjectiveBlocks.EXIT_GATE)) {
                                    level.removeBlock(pos, false);
                                }
                            }
                        }
                        removeDemoEntities(context.getSource().getServer());
                        MatchManager.reset();
                        context.getSource().sendSuccess(() -> Component.literal("Match state, roles, objectives, demo killer and survivor bots reset."), false);
                        return 1;
                    }))
                )
            )
        );

        LOGGER.info("Project Entity initialized. In-world generator and exit-gate objectives registered.");
    }

    private static boolean finishMatch(ServerLevel level, boolean killerVictory) {
        MatchState match = MatchManager.current();
        if (!match.finish(killerVictory)) return false;
        String result = killerVictory
            ? "KILLER VICTORY — all survivors are downed."
            : "SURVIVOR VICTORY — the exit has been reached.";
        level.getServer().getPlayerList().broadcastSystemMessage(Component.literal(result), false);
        removeDemoEntities(level.getServer());
        return true;
    }

    private static void removeDemoEntities(net.minecraft.server.MinecraftServer server) {
        for (ServerLevel level : server.getAllLevels()) {
            for (Entity entity : level.getAllEntities()) {
                if (entity.getTags().contains("project_entity_killer")
                    || entity.getTags().contains(SurvivorBotAI.BOT_TAG)) {
                    entity.discard();
                }
            }
        }
    }

    private static int placeObjectives(
        ServerLevel level,
        BlockPos origin,
        net.minecraft.world.level.block.Block block,
        int[][] offsets,
        Set<BlockPos> usedPositions
    ) {
        int placed = 0;
        for (int[] offset : offsets) {
            BlockPos pos = findNearbySurface(level, origin, offset[0], offset[1], usedPositions);
            if (pos == null) continue;
            if (level.setBlock(pos, block.defaultBlockState(), 3)) {
                usedPositions.add(pos.immutable());
                placed++;
            }
        }
        return placed;
    }

    private static BlockPos findNearbySurface(
        ServerLevel level,
        BlockPos origin,
        int offsetX,
        int offsetZ,
        Set<BlockPos> usedPositions
    ) {
        for (int radius = 0; radius <= 3; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (radius > 0 && Math.abs(dx) != radius && Math.abs(dz) != radius) continue;
                    int x = origin.getX() + offsetX + dx;
                    int z = origin.getZ() + offsetZ + dz;
                    for (int y = origin.getY() + 2; y >= origin.getY() - 4; y--) {
                        BlockPos pos = new BlockPos(x, y, z);
                        if (usedPositions.contains(pos) || !level.getBlockState(pos).isAir()
                            || !level.getBlockState(pos.above()).isAir()) continue;
                        var floor = level.getBlockState(pos.below());
                        if (floor.isFaceSturdy(level, pos.below(), Direction.UP)) {
                            return pos.immutable();
                        }
                    }
                }
            }
        }
        return null;
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
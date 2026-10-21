package dev.projectentity.game;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;

/**
 * Lightweight solo-mode survivor bots. They navigate to placed generators,
 * contribute server-side repair progress, and head toward the gates in endgame.
 * This intentionally starts with simple objective-seeking behavior, not a claim
 * of full DBD-grade tactical AI.
 */
public final class SurvivorBotAI {
    public static final String BOT_TAG = "project_entity_survivor_bot";
    private static int ticks;

    private SurvivorBotAI() {}

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(SurvivorBotAI::tick);
    }

    public static int spawnFor(ServerPlayer player, int count) {
        ServerLevel level = player.serverLevel();
        int spawned = 0;
        for (int i = 0; i < count; i++) {
            Villager bot = EntityType.VILLAGER.create(level);
            if (bot == null) continue;
            double xOffset = (i % 2 == 0 ? 4.0 : -4.0);
            double zOffset = (i < 2 ? 4.0 : -4.0);
            bot.setPos(player.getX() + xOffset, player.getY(), player.getZ() + zOffset);
            bot.setCustomName(net.minecraft.network.chat.Component.literal("Survivor Bot " + (i + 1)));
            bot.setCustomNameVisible(true);
            bot.addTag(BOT_TAG);
            bot.setPersistenceRequired();
            level.addFreshEntity(bot);
            MatchManager.setRole(bot.getUUID(), PlayerRole.SURVIVOR);
            MatchManager.survivorState(bot.getUUID());
            spawned++;
        }
        return spawned;
    }

    private static void tick(MinecraftServer server) {
        if (++ticks % 20 != 0) return;
        MatchState match = MatchManager.current();
        if (match.phase() != MatchPhase.ACTIVE && match.phase() != MatchPhase.ENDGAME) return;

        for (ServerLevel level : server.getAllLevels()) {
            for (Entity entity : level.getAllEntities()) {
                if (!(entity instanceof Villager bot) || !bot.getTags().contains(BOT_TAG)) continue;
                SurvivorState survivor = MatchManager.survivorState(bot.getUUID());
                SurvivorCondition condition = survivor.condition();
                if (condition != SurvivorCondition.HEALTHY && condition != SurvivorCondition.INJURED) {
                    bot.getNavigation().stop();
                    continue;
                }

                List<BlockPos> targets = match.phase() == MatchPhase.ENDGAME
                    ? ObjectiveProgress.exitGates()
                    : ObjectiveProgress.availableGenerators(match);
                BlockPos target = targets.stream()
                    .filter(pos -> {
                        if (match.phase() == MatchPhase.ENDGAME) {
                            return level.getBlockState(pos).is(ObjectiveBlocks.EXIT_GATE);
                        }
                        return level.getBlockState(pos).is(ObjectiveBlocks.GENERATOR);
                    })
                    .min(Comparator.comparingDouble(pos -> bot.blockPosition().distSqr(pos)))
                    .orElse(null);

                if (target == null) {
                    bot.getNavigation().stop();
                    continue;
                }

                double distance = bot.distanceToSqr(Vec3.atCenterOf(target));
                if (distance <= 6.25) {
                    bot.getNavigation().stop();
                    if (match.phase() == MatchPhase.ACTIVE) {
                        ObjectiveProgress.repair(target, match);
                    } else if (distance <= 2.25) {
                        if (survivor.escape() && match.finish(false)) {
                            server.getPlayerList().broadcastSystemMessage(
                                net.minecraft.network.chat.Component.literal("SURVIVOR VICTORY — a survivor bot reached the exit."), false);
                            for (ServerLevel cleanupLevel : server.getAllLevels()) {
                                for (Entity cleanupEntity : cleanupLevel.getAllEntities()) {
                                    if (cleanupEntity.getTags().contains("project_entity_killer")
                                        || cleanupEntity.getTags().contains(BOT_TAG)) {
                                        cleanupEntity.discard();
                                    }
                                }
                            }
                            return;
                        }
                        bot.getNavigation().stop();
                    }
                } else {
                    bot.getNavigation().moveTo(target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5, 0.85);
                }
            }
        }
    }
}

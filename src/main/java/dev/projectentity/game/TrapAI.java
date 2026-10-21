package dev.projectentity.game;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.AABB;

import java.util.List;

/** One-shot traps placed by a human killer; trapped survivors are slowed briefly. */
public final class TrapAI {
    private static int ticks;

    private TrapAI() {}

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(TrapAI::tick);
    }

    public static void reset() {
        ticks = 0;
    }

    private static void tick(MinecraftServer server) {
        if (++ticks % 5 != 0) return;
        MatchState match = MatchManager.current();
        if (match.phase() != MatchPhase.ACTIVE && match.phase() != MatchPhase.ENDGAME) return;

        List<BlockPos> traps = ObjectiveProgress.trapPositions();
        if (traps.isEmpty()) return;

        for (ServerLevel level : server.getAllLevels()) {
            for (BlockPos trapPos : traps) {
                if (!level.getBlockState(trapPos).is(ObjectiveBlocks.TRAP)) continue;
                AABB triggerArea = new AABB(trapPos).inflate(0.3, 1.2, 0.3);
                LivingEntity survivor = level.getEntitiesOfClass(ServerPlayer.class, triggerArea,
                    player -> MatchManager.role(player.getUUID()) == PlayerRole.SURVIVOR).stream()
                    .findFirst().map(player -> (LivingEntity) player).orElse(null);
                if (survivor == null) {
                    survivor = level.getEntitiesOfClass(Villager.class, triggerArea,
                        bot -> bot.getTags().contains(SurvivorBotAI.BOT_TAG)).stream()
                        .findFirst().map(bot -> (LivingEntity) bot).orElse(null);
                }
                if (survivor == null) continue;

                survivor.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 4));
                if (survivor instanceof ServerPlayer player) {
                    player.displayClientMessage(net.minecraft.network.chat.Component.literal("TRAPPED — the killer's snare caught you!"), false);
                }
                level.removeBlock(trapPos, false);
                ObjectiveProgress.unregisterTrap(trapPos);
            }
        }
    }
}

package dev.projectentity.game;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Husk;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Stun timer and simple pallet-breaking behavior for the temporary killer mob. */
public final class KillerBotAI {
    public static final String KILLER_TAG = "project_entity_killer";
    private static final Map<UUID, Integer> stunnedTicks = new HashMap<>();
    private static final Map<BlockPos, Integer> palletBreakProgress = new HashMap<>();
    private static int ticks;

    private KillerBotAI() {}

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(KillerBotAI::tick);
    }

    public static synchronized void stun(Husk killer, int durationTicks) {
        killer.setNoAi(true);
        killer.addTag("project_entity_stunned");
        stunnedTicks.put(killer.getUUID(), Math.max(1, durationTicks));
    }

    public static synchronized void reset() {
        stunnedTicks.clear();
        palletBreakProgress.clear();
    }

    private static synchronized void tick(MinecraftServer server) {
        if (++ticks % 5 != 0) return;
        for (ServerLevel level : server.getAllLevels()) {
            for (Entity entity : level.getAllEntities()) {
                if (!(entity instanceof Husk killer) || !killer.getTags().contains(KILLER_TAG)) continue;
                Integer remainingTicks = stunnedTicks.get(killer.getUUID());
                if (remainingTicks != null) {
                    int remaining = remainingTicks - 5;
                    if (remaining <= 0) {
                        killer.setNoAi(false);
                        killer.removeTag("project_entity_stunned");
                        stunnedTicks.remove(killer.getUUID());
                    } else {
                        stunnedTicks.put(killer.getUUID(), remaining);
                    }
                    continue;
                }

                BlockPos pallet = findNearbyDroppedPallet(level, killer.blockPosition());
                if (pallet != null) {
                    int progress = palletBreakProgress.getOrDefault(pallet, 0) + 5;
                    if (progress >= 40) {
                        level.removeBlock(pallet, false);
                        palletBreakProgress.remove(pallet);
                    } else {
                        palletBreakProgress.put(pallet.immutable(), progress);
                    }
                }
            }
        }
    }

    private static BlockPos findNearbyDroppedPallet(ServerLevel level, BlockPos origin) {
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -1; dy <= 2; dy++) {
                for (int dz = -2; dz <= 2; dz++) {
                    BlockPos candidate = origin.offset(dx, dy, dz);
                    if (level.getBlockState(candidate).is(ObjectiveBlocks.DROPPED_PALLET)) {
                        return candidate.immutable();
                    }
                }
            }
        }
        return null;
    }
}

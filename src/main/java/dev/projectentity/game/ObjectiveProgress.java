package dev.projectentity.game;

import net.minecraft.core.BlockPos;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** Server-owned progress for the physical objective blocks in the current world. */
public final class ObjectiveProgress {
    public static final int CLICKS_TO_REPAIR = 8;
    private static final Map<BlockPos, Integer> progress = new HashMap<>();
    private static final Map<BlockPos, Integer> generatorIds = new HashMap<>();
    private static final Set<Integer> assignedIds = new HashSet<>();

    private ObjectiveProgress() {}

    public static synchronized int repair(BlockPos position, MatchState match) {
        if (match.phase() != MatchPhase.ACTIVE) return -1;
        BlockPos key = position.immutable();
        Integer id = generatorIds.get(key);
        if (id == null) {
            for (int candidate = 0; candidate < match.generatorCount(); candidate++) {
                if (!assignedIds.contains(candidate)) {
                    id = candidate;
                    assignedIds.add(candidate);
                    generatorIds.put(key, id);
                    break;
                }
            }
        }
        if (id == null) return -2;
        if (match.isGeneratorComplete(id)) return 0;
        int next = Math.min(CLICKS_TO_REPAIR, progress.getOrDefault(key, 0) + 1);
        progress.put(key, next);
        if (next >= CLICKS_TO_REPAIR) {
            match.completeGenerator(id);
            return CLICKS_TO_REPAIR;
        }
        return next;
    }

    public static synchronized void reset() {
        progress.clear();
        generatorIds.clear();
        assignedIds.clear();
    }
}

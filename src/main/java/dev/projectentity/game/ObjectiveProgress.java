package dev.projectentity.game;

import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Server-owned progress for the physical objective blocks in the current world. */
public final class ObjectiveProgress {
    public static final int CLICKS_TO_REPAIR = 8;
    public static final int CLICKS_TO_OPEN_GATE = 10;
    private static final Map<BlockPos, Integer> progress = new HashMap<>();
    private static final Map<BlockPos, Integer> generatorIds = new HashMap<>();
    private static final Set<Integer> assignedIds = new HashSet<>();
    private static final Set<BlockPos> exitGates = new HashSet<>();
    private static final Map<BlockPos, Integer> gateProgress = new HashMap<>();
    private static final Set<BlockPos> openGates = new HashSet<>();
    private static final Set<BlockPos> pallets = new HashSet<>();

    private ObjectiveProgress() {}

    public static synchronized void registerGenerator(BlockPos position, MatchState match) {
        BlockPos key = position.immutable();
        if (generatorIds.containsKey(key)) return;
        for (int candidate = 0; candidate < match.generatorCount(); candidate++) {
            if (!assignedIds.contains(candidate)) {
                assignedIds.add(candidate);
                generatorIds.put(key, candidate);
                return;
            }
        }
    }

    public static synchronized void registerExitGate(BlockPos position) {
        exitGates.add(position.immutable());
    }

    public static synchronized void registerPallet(BlockPos position) {
        pallets.add(position.immutable());
    }

    public static synchronized List<BlockPos> availableGenerators(MatchState match) {
        List<BlockPos> result = new ArrayList<>();
        for (Map.Entry<BlockPos, Integer> entry : generatorIds.entrySet()) {
            if (!match.isGeneratorComplete(entry.getValue())) result.add(entry.getKey());
        }
        return result;
    }

    public static synchronized List<BlockPos> exitGates() {
        return new ArrayList<>(exitGates);
    }

    /** Advances a powered gate's opening sequence. Returns -1 when not in endgame. */
    public static synchronized int openGate(BlockPos position, MatchState match) {
        if (match.phase() != MatchPhase.ENDGAME) return -1;
        BlockPos key = position.immutable();
        if (openGates.contains(key)) return CLICKS_TO_OPEN_GATE;
        int next = Math.min(CLICKS_TO_OPEN_GATE, gateProgress.getOrDefault(key, 0) + 1);
        gateProgress.put(key, next);
        if (next >= CLICKS_TO_OPEN_GATE) openGates.add(key);
        return next;
    }

    public static synchronized boolean isGateOpen(BlockPos position) {
        return openGates.contains(position.immutable());
    }

    public static synchronized List<BlockPos> allObjectivePositions() {
        List<BlockPos> result = new ArrayList<>(generatorIds.keySet());
        result.addAll(exitGates);
        result.addAll(pallets);
        return result;
    }

    public static synchronized int repair(BlockPos position, MatchState match) {
        if (match.phase() != MatchPhase.ACTIVE) return -1;
        BlockPos key = position.immutable();
        if (!generatorIds.containsKey(key)) registerGenerator(key, match);
        Integer id = generatorIds.get(key);
        if (id == null) return -2;
        if (match.isGeneratorComplete(id)) return -3;
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
        exitGates.clear();
        gateProgress.clear();
        openGates.clear();
        pallets.clear();
    }
}

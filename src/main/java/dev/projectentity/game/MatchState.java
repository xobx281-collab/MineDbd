package dev.projectentity.game;

import java.util.HashSet;
import java.util.Set;

/** Server-owned match rules, independent of client presentation. */
public final class MatchState {
    public static final int DEFAULT_GENERATOR_COUNT = 7;
    public static final int DEFAULT_REQUIRED_GENERATORS = 5;
    private MatchPhase phase = MatchPhase.LOBBY;
    private final int generatorCount;
    private final int requiredGenerators;
    private final Set<Integer> completedGenerators = new HashSet<>();
    private boolean killerVictory;

    public MatchState() { this(DEFAULT_GENERATOR_COUNT, DEFAULT_REQUIRED_GENERATORS); }

    public MatchState(int generatorCount, int requiredGenerators) {
        if (generatorCount < 1 || requiredGenerators < 1 || requiredGenerators > generatorCount)
            throw new IllegalArgumentException("Generator requirements must be between 1 and the total generator count.");
        this.generatorCount = generatorCount;
        this.requiredGenerators = requiredGenerators;
    }

    public synchronized MatchPhase phase() { return phase; }
    public synchronized int completedGeneratorCount() { return completedGenerators.size(); }
    public int generatorCount() { return generatorCount; }
    public int requiredGenerators() { return requiredGenerators; }
    public synchronized boolean killerVictory() { return killerVictory; }
    public synchronized boolean isGeneratorComplete(int generatorId) { return completedGenerators.contains(generatorId); }
    public synchronized boolean beginPreparation() { return transition(MatchPhase.PREPARATION); }
    public synchronized boolean beginCountdown() { return transition(MatchPhase.COUNTDOWN); }
    public synchronized boolean beginMatch() { return transition(MatchPhase.ACTIVE); }

    /** Marks one generator complete; duplicate or out-of-range IDs are rejected. */
    public synchronized boolean completeGenerator(int generatorId) {
        if (phase != MatchPhase.ACTIVE || generatorId < 0 || generatorId >= generatorCount) return false;
        if (!completedGenerators.add(generatorId)) return false;
        if (completedGenerators.size() >= requiredGenerators) phase = MatchPhase.ENDGAME;
        return true;
    }

    public synchronized boolean finish(boolean killerVictory) {
        if (phase != MatchPhase.ACTIVE && phase != MatchPhase.ENDGAME) return false;
        this.killerVictory = killerVictory;
        phase = MatchPhase.RESULTS;
        return true;
    }

    public synchronized boolean returnToLobby() { return transition(MatchPhase.LOBBY); }

    public synchronized void reset() {
        phase = MatchPhase.LOBBY;
        completedGenerators.clear();
        killerVictory = false;
    }

    private boolean transition(MatchPhase target) {
        boolean allowed = switch (phase) {
            case LOBBY -> target == MatchPhase.PREPARATION;
            case PREPARATION -> target == MatchPhase.COUNTDOWN || target == MatchPhase.LOBBY;
            case COUNTDOWN -> target == MatchPhase.ACTIVE || target == MatchPhase.LOBBY;
            case ACTIVE -> target == MatchPhase.ENDGAME || target == MatchPhase.RESULTS;
            case ENDGAME -> target == MatchPhase.RESULTS;
            case RESULTS -> target == MatchPhase.LOBBY;
        };
        if (!allowed) return false;
        phase = target;
        if (target == MatchPhase.LOBBY) {
            completedGenerators.clear();
            killerVictory = false;
        }
        return true;
    }
}
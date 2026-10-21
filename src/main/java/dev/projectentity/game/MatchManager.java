package dev.projectentity.game;

/** Holds the current server match state; later milestones will bind it to the server lifecycle. */
public final class MatchManager {
    private static MatchState current = new MatchState();

    private MatchManager() {}

    public static synchronized MatchState current() {
        return current;
    }

    public static synchronized void reset() {
        current = new MatchState();
    }
}

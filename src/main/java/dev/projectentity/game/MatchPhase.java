package dev.projectentity.game;

/** Authoritative high-level state of one Project Entity match. */
public enum MatchPhase {
    LOBBY,
    PREPARATION,
    COUNTDOWN,
    ACTIVE,
    ENDGAME,
    RESULTS
}

package dev.projectentity.game;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MatchManagerTest {
    @AfterEach
    void resetManager() {
        MatchManager.reset();
    }

    @Test
    void killerWinsOnlyWhenEverySurvivorIsDownedOrEliminated() {
        UUID first = UUID.randomUUID();
        UUID second = UUID.randomUUID();
        MatchManager.setRole(first, PlayerRole.SURVIVOR);
        MatchManager.setRole(second, PlayerRole.SURVIVOR);

        assertFalse(MatchManager.allSurvivorsDowned());
        MatchManager.survivorState(first).takeHit();
        MatchManager.survivorState(first).takeHit();
        assertFalse(MatchManager.allSurvivorsDowned());

        MatchManager.survivorState(second).takeHit();
        MatchManager.survivorState(second).takeHit();
        assertTrue(MatchManager.allSurvivorsDowned());
    }

    @Test
    void ignoresPlayersWhoAreNotSurvivors() {
        UUID killer = UUID.randomUUID();
        MatchManager.setRole(killer, PlayerRole.KILLER);
        assertFalse(MatchManager.allSurvivorsDowned());
    }
}

package dev.projectentity.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MatchStateTest {
    @Test
    void rejectsInvalidPhaseTransitions() {
        MatchState state = new MatchState();
        assertFalse(state.beginMatch());
        assertEquals(MatchPhase.LOBBY, state.phase());
        assertTrue(state.beginPreparation());
        assertFalse(state.beginPreparation());
        assertTrue(state.beginCountdown());
        assertTrue(state.beginMatch());
    }

    @Test
    void requiresFiveDistinctGeneratorsToEnterEndgame() {
        MatchState state = new MatchState();
        state.beginPreparation();
        state.beginCountdown();
        state.beginMatch();

        for (int id = 0; id < 4; id++) {
            assertTrue(state.completeGenerator(id));
        }
        assertEquals(4, state.completedGeneratorCount());
        assertEquals(MatchPhase.ACTIVE, state.phase());
        assertFalse(state.completeGenerator(3), "duplicate generator must be rejected");
        assertFalse(state.completeGenerator(7), "out-of-range generator must be rejected");
        assertTrue(state.completeGenerator(4));
        assertEquals(5, state.completedGeneratorCount());
        assertEquals(MatchPhase.ENDGAME, state.phase());
        assertFalse(state.completeGenerator(5), "objective progress is locked after endgame begins");
    }

    @Test
    void resultsCanReturnToFreshLobby() {
        MatchState state = new MatchState();
        state.beginPreparation();
        state.beginCountdown();
        state.beginMatch();
        assertTrue(state.finish(true));
        assertEquals(MatchPhase.RESULTS, state.phase());
        assertTrue(state.killerVictory());
        assertTrue(state.returnToLobby());
        assertEquals(MatchPhase.LOBBY, state.phase());
        assertEquals(0, state.completedGeneratorCount());
        assertFalse(state.killerVictory());
    }

    @Test
    void rejectsImpossibleGeneratorConfiguration() {
        assertThrows(IllegalArgumentException.class, () -> new MatchState(3, 4));
        assertThrows(IllegalArgumentException.class, () -> new MatchState(0, 0));
    }
}

package dev.projectentity.game;

import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ObjectiveProgressTest {
    @AfterEach
    void resetObjectives() {
        ObjectiveProgress.reset();
    }

    @Test
    void trapRegistrationCanBeRemovedAfterTrigger() {
        BlockPos trap = new BlockPos(2, 64, 3);
        ObjectiveProgress.registerTrap(trap);
        assertTrue(ObjectiveProgress.trapPositions().contains(trap));
        ObjectiveProgress.unregisterTrap(trap);
        assertFalse(ObjectiveProgress.trapPositions().contains(trap));
    }

    @Test
    void gatesCannotOpenBeforeEndgameAndNeedTenInteractions() {
        MatchState match = new MatchState();
        BlockPos gate = new BlockPos(10, 64, 10);
        ObjectiveProgress.registerExitGate(gate);
        assertEquals(-1, ObjectiveProgress.openGate(gate, match));
        assertFalse(ObjectiveProgress.isGateOpen(gate));

        match.beginPreparation();
        match.beginCountdown();
        match.beginMatch();
        for (int id = 0; id < 5; id++) {
            match.completeGenerator(id);
        }
        for (int progress = 1; progress < ObjectiveProgress.CLICKS_TO_OPEN_GATE; progress++) {
            assertEquals(progress, ObjectiveProgress.openGate(gate, match));
            assertFalse(ObjectiveProgress.isGateOpen(gate));
        }
        assertEquals(ObjectiveProgress.CLICKS_TO_OPEN_GATE, ObjectiveProgress.openGate(gate, match));
        assertTrue(ObjectiveProgress.isGateOpen(gate));
        assertEquals(ObjectiveProgress.CLICKS_TO_OPEN_GATE, ObjectiveProgress.openGate(gate, match));
    }
}

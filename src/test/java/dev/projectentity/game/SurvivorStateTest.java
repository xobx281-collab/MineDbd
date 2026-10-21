package dev.projectentity.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SurvivorStateTest {
    @Test
    void takesTwoHitsToDownAndAllowsHealOrRevive() {
        SurvivorState survivor = new SurvivorState();
        assertTrue(survivor.takeHit());
        assertEquals(SurvivorCondition.INJURED, survivor.condition());
        assertTrue(survivor.heal());
        assertEquals(SurvivorCondition.HEALTHY, survivor.condition());
        assertTrue(survivor.takeHit());
        assertTrue(survivor.takeHit());
        assertEquals(SurvivorCondition.DOWNED, survivor.condition());
        assertFalse(survivor.takeHit());
        assertTrue(survivor.revive());
        assertEquals(SurvivorCondition.INJURED, survivor.condition());
    }

    @Test
    void carryCaptureRescueAndThirdStageElimination() {
        SurvivorState survivor = new SurvivorState();
        assertFalse(survivor.pickUp());
        assertTrue(survivor.takeHit());
        assertTrue(survivor.takeHit());
        assertTrue(survivor.pickUp());
        assertTrue(survivor.capture());
        assertEquals(SurvivorCondition.CAPTURED, survivor.condition());
        assertEquals(1, survivor.captureStages());
        assertTrue(survivor.rescue());
        assertEquals(SurvivorCondition.INJURED, survivor.condition());

        assertTrue(survivor.takeHit());
        assertTrue(survivor.pickUp());
        assertTrue(survivor.capture());
        assertEquals(2, survivor.captureStages());
        assertTrue(survivor.rescue());
        assertTrue(survivor.takeHit());
        assertTrue(survivor.pickUp());
        assertTrue(survivor.capture());
        assertEquals(SurvivorCondition.ELIMINATED, survivor.condition());
        assertEquals(3, survivor.captureStages());
        assertFalse(survivor.rescue());
    }

    @Test
    void escapeIsOnlyAllowedForLivingFreeSurvivors() {
        SurvivorState survivor = new SurvivorState();
        assertTrue(survivor.escape());
        assertEquals(SurvivorCondition.ESCAPED, survivor.condition());
        assertFalse(survivor.takeHit());

        survivor.reset();
        assertTrue(survivor.takeHit());
        assertTrue(survivor.escape());
        assertEquals(SurvivorCondition.ESCAPED, survivor.condition());
    }

    @Test
    void resetReturnsToHealthyWithNoCaptureProgress() {
        SurvivorState survivor = new SurvivorState();
        survivor.takeHit();
        survivor.takeHit();
        survivor.pickUp();
        survivor.capture();
        survivor.reset();
        assertEquals(SurvivorCondition.HEALTHY, survivor.condition());
        assertEquals(0, survivor.captureStages());
    }
}

package dev.projectentity.game;

/**
 * Pure game-rule model for one survivor. Minecraft entities and UI must call
 * these transitions from server-side events; this class itself is deterministic
 * and can be tested without starting a game client.
 */
public final class SurvivorState {
    public static final int MAX_CAPTURE_STAGES = 3;
    private SurvivorCondition condition = SurvivorCondition.HEALTHY;
    private int captureStages;

    public synchronized SurvivorCondition condition() { return condition; }
    public synchronized int captureStages() { return captureStages; }

    /** First hit injures; a second hit downs the survivor. */
    public synchronized boolean takeHit() {
        if (condition == SurvivorCondition.HEALTHY) {
            condition = SurvivorCondition.INJURED;
            return true;
        }
        if (condition == SurvivorCondition.INJURED) {
            condition = SurvivorCondition.DOWNED;
            return true;
        }
        return false;
    }

    public synchronized boolean heal() {
        if (condition != SurvivorCondition.INJURED) return false;
        condition = SurvivorCondition.HEALTHY;
        return true;
    }

    public synchronized boolean revive() {
        if (condition != SurvivorCondition.DOWNED) return false;
        condition = SurvivorCondition.INJURED;
        return true;
    }

    public synchronized boolean pickUp() {
        if (condition != SurvivorCondition.DOWNED) return false;
        condition = SurvivorCondition.CARRIED;
        return true;
    }

    /** Each completed capture increments the stage; the third eliminates. */
    public synchronized boolean capture() {
        if (condition != SurvivorCondition.CARRIED) return false;
        captureStages = Math.min(MAX_CAPTURE_STAGES, captureStages + 1);
        condition = captureStages >= MAX_CAPTURE_STAGES
            ? SurvivorCondition.ELIMINATED
            : SurvivorCondition.CAPTURED;
        return true;
    }

    public synchronized boolean rescue() {
        if (condition != SurvivorCondition.CAPTURED) return false;
        condition = SurvivorCondition.INJURED;
        return true;
    }

    public synchronized boolean escape() {
        if (condition != SurvivorCondition.HEALTHY && condition != SurvivorCondition.INJURED) return false;
        condition = SurvivorCondition.ESCAPED;
        return true;
    }

    public synchronized void reset() {
        condition = SurvivorCondition.HEALTHY;
        captureStages = 0;
    }
}

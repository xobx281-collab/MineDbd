package dev.projectentity.game;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Holds the authoritative match state and per-player role selections. */
public final class MatchManager {
    private static MatchState current = new MatchState();
    private static final Map<UUID, PlayerRole> roles = new ConcurrentHashMap<>();

    private MatchManager() {}

    public static synchronized MatchState current() { return current; }
    public static void setRole(UUID playerId, PlayerRole role) { roles.put(playerId, role); }
    public static PlayerRole role(UUID playerId) { return roles.get(playerId); }
    public static void clearRoles() { roles.clear(); }

    public static synchronized void reset() {
        current = new MatchState();
        ObjectiveProgress.reset();
        roles.clear();
    }
}

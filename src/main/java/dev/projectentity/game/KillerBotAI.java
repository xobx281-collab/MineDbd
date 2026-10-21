package dev.projectentity.game;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Husk;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Small stun timer for the temporary hostile-mob killer. */
public final class KillerBotAI {
    public static final String KILLER_TAG = "project_entity_killer";
    private static final Map<UUID, Integer> stunnedTicks = new HashMap<>();
    private static int ticks;

    private KillerBotAI() {}

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(KillerBotAI::tick);
    }

    public static synchronized void stun(Husk killer, int durationTicks) {
        killer.setNoAi(true);
        killer.addTag("project_entity_stunned");
        stunnedTicks.put(killer.getUUID(), Math.max(1, durationTicks));
    }

    public static synchronized void reset() {
        stunnedTicks.clear();
    }

    private static synchronized void tick(MinecraftServer server) {
        if (++ticks % 5 != 0 || stunnedTicks.isEmpty()) return;
        for (ServerLevel level : server.getAllLevels()) {
            for (Entity entity : level.getAllEntities()) {
                if (!(entity instanceof Husk killer) || !stunnedTicks.containsKey(killer.getUUID())) continue;
                int remaining = stunnedTicks.get(killer.getUUID()) - 5;
                if (remaining <= 0) {
                    killer.setNoAi(false);
                    killer.removeTag("project_entity_stunned");
                    stunnedTicks.remove(killer.getUUID());
                } else {
                    stunnedTicks.put(killer.getUUID(), remaining);
                }
            }
        }
    }
}

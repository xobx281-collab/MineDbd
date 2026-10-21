package dev.projectentity;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ProjectEntity implements ModInitializer {
    public static final String MOD_ID = "project_entity";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Project Entity is loading. Gameplay systems are under active development.");
    }
}

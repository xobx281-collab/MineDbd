package dev.projectentity.client;

import net.fabricmc.api.ClientModInitializer;
import dev.projectentity.ProjectEntity;

public final class ProjectEntityClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ProjectEntity.LOGGER.info("Project Entity client presentation initialized.");
    }
}

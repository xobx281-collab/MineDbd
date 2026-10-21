# Architecture and Technical Decisions

## Initial toolchain
- Minecraft Java Edition: 1.21.1
- Java: 21
- Mod loader: Fabric
- Fabric API: 0.116.17+1.21.1
- Fabric Loader: 0.19.5
- Fabric Loom: 1.18-SNAPSHOT

The Minecraft 1.21.1/Java 21 combination and project structure are based on the official Fabric 1.21.1 example project and version-specific Fabric documentation. These versions are the initial baseline, not a claim that the project has already built successfully. Dependency resolution and clean-build verification remain pending.

Sources:
- https://github.com/FabricMC/fabric-example-mod/tree/1.21.1
- https://github.com/FabricMC/fabric-docs/blob/main/versions/1.21.1/develop/getting-started/setting-up-a-development-environment.md

## Constraints
- Dedicated-server compatible.
- Client-only rendering/UI separated from gameplay rules.
- Server-authoritative state and validation.
- Custom entities/blocks/items/interactions and custom HUD.
- Support original resources, animation, audio, multiplayer, solo bots and distributable client/server builds.
- Avoid unnecessary dependencies.

## Planned boundaries
- **Match lifecycle:** state machine, lobby, countdown, endgame, results, rematch.
- **Player roles and states:** role assignment, survivor condition transitions, action permissions.
- **Objectives:** generators, skill checks, gates and objective configuration.
- **Combat and chase:** server-validated attacks, recovery, pallets, stuns, traps.
- **AI:** navigation/decision logic for killer and survivor bots.
- **Networking:** client action requests, server validation, state replication.
- **Client presentation:** HUD, menus, sound, animation and model rendering.
- **Content/config:** map layout, balance values, original assets and license records.
- **Testing:** pure game-rule tests plus available integration/manual scenarios.

## Dependency policy
Add animation/model libraries only when their compatibility with the selected Minecraft version and loader is confirmed and the first feature actually needs them. Client graphics or shader libraries must never be server requirements.

## Current limitations
The repository contains the initial mod entrypoints and build configuration. A clean build, client launch, dedicated-server launch, and gameplay have not yet been verified.

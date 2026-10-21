# Architecture and Technical Decisions

## Current toolchain
- Minecraft Java Edition: 1.21.1
- Java: 21
- Mod loader: Fabric
- Fabric API: 0.116.17+1.21.1
- Fabric Loader: 0.19.5
- Fabric Loom: 1.15.5 (pinned stable release; the previous moving snapshot required a newer Java runtime)

CI has confirmed that main and client Java source compilation, resource processing, remapping and test compilation can run on Java 21. The first full CI attempt then failed at test execution because the JUnit Platform launcher was missing. A launcher dependency has been added; the next CI run must confirm the complete build and tests.

## Supplied map
The user supplied a Minecraft Java world named **Dead By Daylight**, packaged as `§a§lDead By Daylight-Freddy Fazbear's.zip`. Its level metadata reports Minecraft **1.21.1** (DataVersion 3955), making it the intended arena world for Project Entity. The map is treated as the chosen location, not as a source of gameplay logic. Keep the original world content intact; add objectives and game-specific behavior through the mod and a separate map configuration rather than destructively rewriting the source world.

The uploaded world archive contains region, entity, POI, player-data, and map data. It must be installed as a world save for both local testing and a dedicated server. The exact gameplay arena bounds, safe spawn pads, navigation paths and objective positions still need to be inspected and recorded in a live Minecraft session before spawning objectives automatically.

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
- **Content/config:** supplied Dead By Daylight world, arena layout, balance values, original assets and license records.
- **Testing:** pure game-rule tests plus available integration/manual scenarios.

## Dependency policy
Add animation/model libraries only when their compatibility with the selected Minecraft version and loader is confirmed and the first feature actually needs them. Client graphics or shader libraries must never be server requirements.

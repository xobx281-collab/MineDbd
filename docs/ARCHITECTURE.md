# Architecture and Technical Decisions

## Status
Initial architectural principles only. The exact Minecraft version, loader, Java toolchain and library versions remain **unselected until compatibility is verified**.

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

## Decisions policy
Choose the simplest stable stack that supports the full design. Check official documentation and library compatibility before pinning versions. Document the chosen versions and reasons here. Client graphics or shader libraries must never be server requirements.

## Current limitations
No game code or toolchain has been verified yet. The repository initialization is not a playable build.

# Project Entity

An original Minecraft Java Edition asymmetrical survival-horror game mode inspired by the gameplay structure of Dead by Daylight and the cohesive custom-game ambition of creator-made Minecraft projects.

## Current status

**Stage:** Early gameplay prototype; CI build and automated tests pass, but live Minecraft behavior is not yet verified.
**Canonical arena:** The user's supplied Dead By Daylight / Freddy Fazbear's world (Minecraft 1.21.1). See [map integration notes](docs/MAP_INTEGRATION.md).
**Canonical specification:** [docs/GAME_DESIGN.md](docs/GAME_DESIGN.md)
**Current progress:** [docs/PROGRESS.md](docs/PROGRESS.md)

## Current prototype

The mod has an early in-world objective loop:
1. Install the built mod JAR in a Minecraft 1.21.1 Fabric client with Java 21 and Fabric API.
2. Open a backup copy of the supplied world with commands available.
3. Run `/entity demo` to start a test match, assign the player as survivor, receive seven generator blocks and two exit-gate consoles, and spawn a hostile vanilla Husk as a temporary killer.
4. Place the objective blocks in safe, open areas. Right-click each generator eight times to repair it.
5. Repair five distinct generators to enter endgame, then right-click an Exit Gate Console to finish with a survivor victory.
6. Run `/entity status` to inspect the phase, role and objective count. An operator can run `/entity reset` to clear match state and the tagged demo killer.

The Husk is only a temporary basic hostile-mob stand-in, not a custom killer AI. Survivor bots use simple objective-seeking behavior and are not yet tactically equivalent to DBD survivors. Objective blocks currently use temporary vanilla-based visuals. The map's actual spawn safety, arena geometry, objective positions and live client behavior have not yet been verified.

## Core vision

- One killer versus four survivors.
- Complete matches with objectives, chases, injuries, captures, rescues, gates, escapes and results.
- Solo testing with AI-controlled players.
- Multiplayer through a dedicated server and LAN/internet connections.
- Original, properly licensed assets and a cohesive horror presentation.
- A simple client installation and server setup process.

## Verification policy

GitHub Actions clean build and automated tests have passed. This does not prove that the mod loads or behaves correctly in a live Minecraft client. Do not describe the current prototype as a finished game until client and dedicated-server smoke tests have passed.

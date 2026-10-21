# Project Entity

An original Minecraft Java Edition asymmetrical survival-horror game mode inspired by the gameplay structure of Dead by Daylight and the cohesive custom-game ambition of creator-made Minecraft projects.

## Current status

**Stage:** Early playable-systems development; not yet a verified playable release.  
**Release status:** No downloadable release until CI and in-game behavior are verified.  
**Canonical specification:** [docs/GAME_DESIGN.md](docs/GAME_DESIGN.md)  
**Current progress and next steps:** [docs/PROGRESS.md](docs/PROGRESS.md)

## First in-world test scaffold

The mod now includes the first visible objective blocks and a simple solo test path:

1. Install the built mod JAR in a Minecraft 1.21.1 Fabric client with Java 21 and Fabric API.
2. Enter a world with cheats enabled.
3. Run `/entity demo` to start a test match and receive seven generator blocks and two exit-gate consoles.
4. Place the blocks. Right-click each generator eight times to repair it.
5. Repair five distinct generators to power the exit gates, then right-click an exit-gate console to finish with a survivor victory.
6. Run `/entity status` to inspect the phase, role, and objective count.

This is an early mechanics scaffold, not the intended final experience: there is no killer AI, survivor AI, chase/combat system, custom map, animation set, or polished lobby yet. It has not been verified in a launched Minecraft client.

## Core vision

- One killer versus four survivors.
- Complete matches with objectives, chases, injuries, captures, rescues, gates, escapes, and results.
- Solo testing with AI-controlled players.
- Multiplayer through a dedicated server and LAN/internet connections.
- Original, properly licensed assets and a cohesive horror presentation.
- A simple client installation and server setup process.

## Verification policy

A successful compilation is not proof that the game is playable. Test reports must clearly distinguish verified behavior from untested implementation. The CI workflow builds, runs tests, and publishes the JAR as a temporary workflow artifact when successful.

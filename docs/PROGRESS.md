# Project Entity Progress

## Current milestone
**Milestone 2 — First in-world gameplay slice (in progress).**

## Implemented in repository
- [x] Project instructions, game design, architecture notes and acceptance test plan.
- [x] Fabric mod project targeting Minecraft 1.21.1 / Java 21.
- [x] Gradle wrapper and GitHub Actions build workflow.
- [x] Explicit server-owned match phases and victory/result state.
- [x] Seven generator slots with five required to power the exit phase.
- [x] Placeable Generator and Exit Gate Console blocks, with block/item models and English/Russian names.
- [x] `/entity demo` starts a solo test match, selects the survivor role and gives objective blocks.
- [x] `/entity role survivor|killer` and `/entity status` commands.
- [x] Server-side generator interaction progress, distinct-generator tracking and survivor escape condition.
- [x] Match-state unit tests for phase transitions, objective threshold, reset and invalid configurations.
- [x] CI configured to run a clean Gradle build/tests and upload the JAR as a temporary artifact on success.

## Verification status
- [ ] Clean Gradle build has been observed and confirmed.
- [ ] Automated tests have passed in CI.
- [ ] Development client launch and in-game interaction verified.
- [ ] Dedicated server launch.
- [ ] AI killer and AI survivors.
- [ ] Combat, injury/downed/carry/capture/rescue, pallets and traps.
- [ ] Custom map, lobby/HUD, original models/animations/audio.
- [ ] Five-client multiplayer and client/server distribution.

**Important:** the objective mechanics are implemented in source but have not yet been confirmed by a successful build or live Minecraft session. The current slice is not a finished game. The objective blocks use temporary vanilla-based visuals, and the match has no AI or killer combat yet.

## Next actions
1. Obtain a real CI result and fix compile/test failures.
2. Verify the mod in a Minecraft client; fix any mapping/API or interaction issues.
3. Add a simple arena and meaningful solo AI.
4. Implement the killer/survivor interaction loop, then improve presentation and multiplayer.

## Owner-facing status
The repo has advanced beyond the state-only scaffold: it now contains a first in-world objective loop and an exit condition. A download should not be offered until CI succeeds and the behavior is tested honestly.

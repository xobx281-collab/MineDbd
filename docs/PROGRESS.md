# Project Entity Progress

## Current milestone
**Milestone 2 — First gameplay loop (in progress).**

## Implemented in source
- [x] Minecraft Java 1.21.1 / Java 21 / Fabric mod project.
- [x] Stable Fabric Loom 1.15.5 and GitHub Actions build/test workflow.
- [x] Earlier clean CI runs compiled and tested the objective prototype; the latest win-condition changes require another clean CI run.
- [x] Server-owned match phases, seven generator slots and five-generator endgame threshold.
- [x] Placeable Generator and Exit Gate Console blocks with temporary vanilla-based models and English/Russian names.
- [x] /entity demo starts a solo test match, assigns the player as survivor, attempts to place objectives on nearby safe surfaces, and gives any objectives it cannot place as items.
- [x] A tagged hostile Husk serves as the temporary killer; its hits feed server-side two-hit survivor condition logic.
- [x] Three villager-based survivor bots seek placed generators, contribute repair progress and move toward gates during endgame.
- [x] Survivor state model includes healthy, injured, downed, carried, captured, escaped and eliminated; healing, revival, three capture stages, rescue and escape transitions have unit tests.
- [x] Survivor victory can be triggered by reaching a powered exit; killer victory is triggered when every survivor is downed.
- [x] /entity status, /entity role survivor|killer and operator /entity reset commands.
- [x] User-supplied Dead By Daylight / Freddy Fazbear's world is the canonical arena; archive metadata identifies Minecraft 1.21.1.

## Verification status
- [x] Earlier CI run passed clean build and automated tests for auto-placement and bot navigation (GitHub Actions run 70).
- [ ] Run a fresh CI build after the latest match-end conditions and tests.
- [ ] Launch Minecraft 1.21.1 with the mod and supplied arena.
- [ ] Verify that automatic objective placement selects safe, reachable surfaces on this specific map.
- [ ] Verify bot navigation, generator progress, injury/downed feedback and both win conditions in-game.
- [ ] Verify dedicated server launch.
- [ ] Implement carry/capture/rescue as actual world interactions, not only rule-model transitions.
- [ ] Add pallets, stuns, traps and killer powers.
- [ ] Replace temporary vanilla mobs/blocks with original custom entities, models, animations, sounds and HUD.
- [ ] Complete five-client multiplayer and package a tested client/server release.

## Honest boundary
CI success confirms compilation and automated tests only. The current prototype has not been launched in a Minecraft client, and the map's actual geometry has not been inspected in-game. The current artifact is not a finished game.

## Next actions
1. Verify the latest source with a clean CI build.
2. Use the first build to validate automatic objective placement in the supplied world.
3. Fix any map-specific placement/pathfinding issues.
4. Build proper survivor downed/carry/capture/rescue interactions and the pallet/trap chase loop.
5. Continue to multiplayer, presentation and final packaging only after the solo loop is stable.

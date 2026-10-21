# Project Entity Progress

## Current milestone
**Milestone 2 — First complete solo-loop prototype (in progress).**

## Implemented in source
- [x] Minecraft Java 1.21.1 / Java 21 / Fabric mod project.
- [x] Stable Fabric Loom 1.15.5 and GitHub Actions build/test workflow.
- [x] Latest clean CI build and automated tests passed (GitHub Actions run 82). A remapped mod JAR artifact was produced.
- [x] Server-owned match phases, seven generator slots and five-generator endgame threshold.
- [x] Placeable Generator and Exit Gate Console blocks with temporary vanilla-based models and English/Russian names.
- [x] /entity demo starts a solo test match, assigns the player as survivor, attempts to place objectives on nearby safe surfaces, and gives any objectives it cannot place as items.
- [x] A tagged hostile Husk serves as the temporary killer; its hits feed server-side two-hit survivor condition logic.
- [x] Three villager-based survivor bots seek placed generators, contribute repair progress and move toward gates during endgame.
- [x] Survivor state model includes healthy, injured, downed, carried, captured, escaped and eliminated; healing, revival, three capture stages, rescue and escape transitions have unit tests.
- [x] Powered exit gates require ten interactions to open before survivors can escape; killer victory is triggered when every survivor is downed.
- [x] /entity status, /entity role survivor|killer and operator /entity reset commands.
- [x] User-supplied Dead By Daylight / Freddy Fazbear's world is the canonical arena; archive metadata identifies Minecraft 1.21.1.

## Verification status

A clean build is required after adding standing/dropped pallet blocks, survivor drop-and-stun behavior, AI pallet breaking, and the new exit gate opening flow.

A fresh CI build is required after adding the ten-interaction exit gate sequence and its unit test.
- [x] Latest clean CI build succeeded after match-end fixes.
- [x] Automated JUnit tests passed, including match phases, generator threshold, survivor state transitions and killer win condition.
- [ ] Launch Minecraft 1.21.1 with the mod and supplied arena.
- [ ] Verify automatic objective placement selects safe, reachable surfaces on this specific map.
- [ ] Verify bot navigation, generator progress, injury/downed feedback and both win conditions in-game.
- [ ] Verify dedicated server launch.
- [ ] Implement carry/capture/rescue as actual world interactions, not only rule-model transitions.
- [x] Basic pallet loop: survivors drop a pallet, a nearby killer is stunned, and the killer can break a dropped pallet after reaching it. The block visuals/collision remain placeholders.
- [ ] Add traps and killer powers.
- [ ] Replace temporary vanilla mobs/blocks with original custom entities, models, animations, sounds and HUD.
- [ ] Complete five-client multiplayer and package a tested client/server release.

## Honest boundary
CI success confirms compilation and automated tests only. The current prototype has not been launched in a Minecraft client, and the map's actual geometry has not been inspected in-game. The current artifact is an early prototype, not a finished game.

## Next actions
1. Validate objective placement and mob spawning on the supplied world.
2. Add actual downed/carry/capture/rescue interactions and pallet stuns.
3. Add killer abilities and smarter survivor behavior.
4. Implement skill checks, gate-opening delay, match timer and player-facing HUD.
5. Add dedicated-server validation and multiplayer synchronization.
6. Replace temporary visuals and audio, then package a release only after live smoke tests.

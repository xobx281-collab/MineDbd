# Project Entity Progress

## Current milestone
**Milestone 2 — First gameplay slice (in progress).**

## Implemented
- [x] Project instructions, game design, architecture notes, acceptance test plan and map integration notes.
- [x] Minecraft 1.21.1 / Java 21 / Fabric project and GitHub Actions build.
- [x] Stable Fabric Loom 1.15.5; Gradle configuration-cache setting disabled while compatibility is established.
- [x] CI clean build and automated tests pass on the verification PR (GitHub Actions run 46). The build produced the remapped mod JAR and uploaded it as a temporary artifact.
- [x] Server-owned match phases and victory/result state, with unit tests.
- [x] Seven generator slots; five completed generators trigger endgame.
- [x] Placeable Generator and Exit Gate Console blocks, temporary vanilla-based models and English/Russian names.
- [x] /entity demo starts a solo test match, assigns the player as survivor and provides objective blocks.
- [x] The solo demo now spawns a tagged hostile Husk as a basic killer prototype; /entity reset removes tagged demo killers.
- [x] Three villager-based survivor bots spawn in solo demo, navigate toward placed generators, contribute repair progress, and head toward exit gates during endgame.
- [x] Placed generator and exit-gate positions are registered server-side; reset removes registered objective blocks and demo entities.
- [x] Pure survivor condition model: healthy, injured, downed, carried, captured, escaped and eliminated; two-hit injury/downed transitions, healing, revival, three capture stages, rescue and escape rules.
- [x] Basic tagged-killer hits now feed server-side survivor state and show injury/downed feedback; full carry/rescue presentation remains incomplete.
- [x] /entity role survivor|killer and /entity status commands.
- [x] Server-side generator interaction progress, distinct-generator tracking and survivor escape condition.
- [x] The user-supplied Dead By Daylight / Freddy Fazbear's world is the canonical arena. Its metadata matches Minecraft 1.21.1.

## Verified vs unverified
- [x] CI clean build succeeded.
- [x] Automated Gradle/JUnit test task completed successfully.
- [ ] Minecraft client launch and in-game behavior verified.
- [ ] Dedicated server launch verified.
- [ ] Supplied arena opened in-game and its safe spawns / routes recorded.
- [ ] Objective blocks automatically placed at configured map points.
- [ ] Survivor health/downed/carry/capture/rescue system.
- [ ] Pallets, stuns, traps and killer powers.
- [ ] Smarter survivor AI (avoid killer, rescue/heal, use pallets, react to traps); current bots only seek objectives and gates.
- [ ] Custom killer AI and a proper carry/rescue interaction loop.
- [ ] Lobby/HUD, custom models, animations, sounds and map dressing.
- [ ] Five-client multiplayer and packaged client/server distribution.

CI success proves the code compiles and automated tests pass; it does not prove the mod loads or behaves correctly in a live Minecraft client. The current JAR is a development prototype, not a finished game.

## Next actions
1. Validate the mod in Minecraft 1.21.1 on the supplied world.
2. Add persistent arena-point configuration and objective placement based on the actual map.
3. Implement survivor hit/downed states and meaningful chase interactions.
4. Add survivor AI, then pallets, traps, gates, match-end flow and presentation.
5. Build the repeatable match loop and dedicated-server test.
6. Package a release only after the client and server have been launched and smoke-tested.

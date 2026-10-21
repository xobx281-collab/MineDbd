# Project Entity Progress

## Current milestone
**Milestone 1 — Foundation (in progress).**

## Implemented in repository
- [x] Project instructions, game design, architecture notes and acceptance test plan.
- [x] Initial Fabric mod project for Minecraft 1.21.1 / Java 21.
- [x] Gradle wrapper scripts and wrapper JAR.
- [x] Automated GitHub Actions build workflow.
- [x] Common and client mod entrypoints.
- [x] Explicit match phases: lobby, preparation, countdown, active, endgame and results.
- [x] Server-side match-state rules with seven generator slots and a five-generator endgame threshold.
- [x] Initial unit tests for phase transitions, generator validation and results/reset behavior.
- [x] Operator-only `/entity debug` commands to exercise the state scaffold.

## Verification status
- [ ] Clean Gradle build has been observed and confirmed.
- [ ] Automated tests have passed in CI.
- [ ] Development client launch.
- [ ] Dedicated server launch.
- [ ] In-game match and objectives.
- [ ] Bots, chases, traps, custom map, animations/audio and polished UI.
- [ ] Five-client multiplayer.
- [ ] Client/server distribution.

The debug commands only test the current state scaffold. They do **not** represent a playable match, and no bot or generator world object has been implemented yet.

## Next actions
1. Confirm the first CI build and fix any build errors.
2. Add an actual lobby/start flow rather than command-only testing.
3. Implement player roles and the first visible gameplay objective.
4. Build toward a complete playable solo vertical slice, including useful bots and real win/loss outcomes.

## Owner-facing status
The repository now has a real mod project foundation and the first tested-in-design match rules, but gameplay has not yet been verified in Minecraft. Do not install it as a playable game yet.

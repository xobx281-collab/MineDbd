# First-slice smoke test

This checklist is for validating the early objective prototype after CI produces a JAR.

## Build checks
- [ ] Java 21 build completes from a clean checkout.
- [ ] JUnit tests pass.
- [ ] The mod JAR is present as a workflow artifact.

## Minecraft client checks
- [ ] Launch Minecraft 1.21.1 with Fabric Loader and Fabric API.
- [ ] Confirm the title screen reaches the world without a mod-loading error.
- [ ] Create a cheats-enabled creative test world.
- [ ] Run `/entity status` and confirm lobby state.
- [ ] Run `/entity demo` and confirm role selection and objective items.
- [ ] Place a generator and confirm the block has a visible model/name.
- [ ] Right-click it eight times and confirm progress completes once.
- [ ] Repeat on five different generators and confirm the phase changes to ENDGAME.
- [ ] Use an Exit Gate Console and confirm the phase changes to RESULTS.
- [ ] Run `/entity reset` as an operator and confirm the state returns to a fresh lobby.

## Server checks
- [ ] Start a dedicated server with the mod.
- [ ] Confirm server startup succeeds without client-only class errors.
- [ ] Repeat the objective loop with a connected client.

Do not mark an item complete unless it was actually observed. The source implementation alone is not evidence that an item passes.

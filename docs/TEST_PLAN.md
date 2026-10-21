# Test Plan and Acceptance Checklist

Mark an item verified only after performing the test and recording evidence.

## Build and launch
- [ ] Clean checkout builds.
- [ ] Development client launches and loads the mod.
- [ ] Dedicated server launches and accepts a compatible client.
- [ ] Packaged client installation works using documented steps.
- [ ] Server package can create/load the intended world.
- [ ] Required resources/configuration are included.

## Complete solo match
- [ ] Enter lobby and start a solo match.
- [ ] Select human killer vs four AI survivors.
- [ ] Select human survivor with three AI teammates vs AI killer.
- [ ] Correct role/team/spawn is assigned.
- [ ] Find and repair generators.
- [ ] Verify skill-check success and failure, including progress/audio feedback.
- [ ] Verify interruption and repair regression.
- [ ] Verify chase and valid attack range/direction/timing.
- [ ] Verify healthy → injured → downed transitions.
- [ ] Carry and capture a downed survivor.
- [ ] Rescue/revive/heal and verify valid/invalid state transitions.
- [ ] Drop a pallet and verify a valid stun; verify no distant/through-wall stun.
- [ ] Break a pallet and confirm its state persists for the match.
- [ ] Place, trigger, resolve and clean up a trap.
- [ ] Complete five of seven generators.
- [ ] Activate and open an escape gate.
- [ ] Escape and verify individual results.
- [ ] Verify killer victory and endgame timeout.
- [ ] Show results, return to lobby and start a second match without restarting Minecraft.

## AI behavior
- [ ] Survivor bots navigate to generators and repair.
- [ ] Survivor bots react to danger and can evade.
- [ ] Bots handle skill checks.
- [ ] Bots can use pallets and respond to traps.
- [ ] Bots attempt appropriate rescue/heal/revive actions.
- [ ] Bots travel to gates and escape when possible.
- [ ] Killer bot detects valid clues/targets without wallhack omniscience.
- [ ] Killer bot pursues, attacks, recovers, carries/captures, damages generators and breaks pallets.
- [ ] Easy and Normal difficulties work.

## Multiplayer
- [ ] Five clients connect to a dedicated server.
- [ ] Roles and player states are consistent across clients.
- [ ] Objective progress and skill-check outcomes synchronize.
- [ ] Combat, pallet stun and trap state are consistent.
- [ ] Capture/rescue cannot resolve twice in a network race.
- [ ] Gate/endgame/results synchronize.
- [ ] Invalid client requests are rejected.
- [ ] Disconnects do not permanently lock the match or an interaction.
- [ ] Bot replacement behavior follows configuration.

## Automated rule tests
- [ ] Generator progress/completion and invalid values.
- [ ] Skill-check outcomes.
- [ ] Injury/downing/carry/capture/rescue/elimination.
- [ ] Pallet stun validation.
- [ ] Trap activation and repeated-trigger protection.
- [ ] Gate activation, escape and killer victory.
- [ ] Invalid state transitions.
- [ ] Match reset and repeat startup.

## Reporting
For each test, record status as verified, failed, not run, or blocked. State whether verification was automated, in-game manual, or inferred from compilation only. Do not claim five-client verification if five clients were not actually run.

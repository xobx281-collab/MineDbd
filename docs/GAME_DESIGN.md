# Project Entity — Canonical Game Design

This document is the canonical implementation brief. It preserves the core requirements of the owner's master specification. More detailed acceptance scenarios are tracked in `TEST_PLAN.md`.

## Product
An original Minecraft Java Edition asymmetrical survival-horror game mode. Primary gameplay reference: Dead by Daylight. Quality/presentation ambition reference: Nazzy's custom Minecraft game modes. These are references, not sources for proprietary assets or code.

## Non-negotiable principles
1. Implement working software directly in this repository; do not stop at a proposal or project skeleton.
2. Make technical decisions independently after compatibility checks.
3. Keep each milestone buildable.
4. A feature is complete only when integrated and tested to the extent possible.
5. Use original or properly licensed models, textures, sounds, and other assets.
6. Report honestly what is implemented, verified, incomplete, or blocked.

## Game format and match flow
- Standard match: one killer versus four survivors.
- Support five human players on LAN/internet through a dedicated server.
- Solo mode must support a human killer against four AI survivors, or a human survivor with three AI teammates against an AI killer.
- Mixed human/bot configurations and bot replacement for disconnected players should be supported where practical.
- Match states: entry/menu, lobby, preparation, countdown, active match, endgame, results, return to lobby.
- Reject invalid state transitions; disconnects must not permanently lock the match.
- Lobby: player slots/readiness, human/bot status, killer and survivor selection, map/settings, start/countdown, and return-to-menu controls.

## Survivor gameplay
Required actions: movement, sprinting, crouching/hiding where supported, generator repair, interactive skill checks, cooperative repair, pallet drop/vault, healing, reviving, rescue, gate interaction, escape, abilities, and contextual HUD prompts.

Explicit states: healthy, injured, downed, being carried, captured, rescued/post-rescue, escaped, eliminated. Each state governs permitted movement/actions, HUD, animation, and server-side validation.

Initial injury rules: first valid hit injures a healthy survivor; a subsequent hit downs an injured survivor. Downed survivors can be carried/captured; teammates can rescue/revive under valid conditions. Healing returns injured survivors to healthy. Capture progression supports up to three stages, with a final stage causing elimination; timing and transitions must be internally consistent and explained in the tutorial.

## Generators and skill checks
- Initial map: seven generators; repair five to activate escape phase.
- Target solo uninterrupted repair duration: approximately 70 seconds.
- Skill checks are timed player interactions at randomized intervals, not automatic successes in normal mode.
- Success grants a small bonus or avoids a penalty; failure makes a loud notification and removes progress.
- Cooperative repair uses diminishing returns.
- Killer interruption/damage regresses repair.
- Synchronize progress and state across clients. Handle interruption and disconnect during skill checks.
- Put all balance values in centralized configuration.

## Gates, endgame, and results
After the required generators are complete, enter endgame and activate two escape gates. Gate opening takes time and can be interrupted. Configure the endgame timer. Escaped survivors retain their individual result even if the killer later wins against remaining players. Show individual outcomes and overall match outcome. Killer victory is defined by eliminating all survivors or preventing the required escape outcome before timeout according to configured rules.

## Killer
Implement a complete killer with distinct visuals, movement/combat parameters, primary attack, cooldown/recovery, validated hit detection, injury/downing, carrying/capture, generator damage, pallet breaking, ability cooldowns, audio and chase feedback. Attacks must validate position, range, direction, timing, and state on the server; no arbitrary map-wide damage.

Initial special ability: a deployable trap. Complete one killer and one survivor identity before expanding the roster.

## Pallets and traps
Pallets: ready/standing, survivor drop, collision/traversal, validated stun zone/timing, timed killer stun, killer break action, destroyed state persistent for the match. No stun through arbitrary distance or walls.

Traps: valid placement, discoverable physical presence, activation/trigger state, server-validated detection, disarm/escape interaction, consequence such as temporary immobilization and audible alert, synchronized state, no unintended repeated triggers, match-end cleanup.

## AI and solo testing
AI is mandatory for the first playable release; bots must not be stationary targets.
- AI survivors find/navigate to generators, repair and handle skill checks, react to danger, evade/pursue intelligently enough for ordinary play, use pallets, rescue/heal/revive when appropriate, respond to traps, and travel to/escape through gates.
- AI killer finds or investigates survivors using valid clues, pursues, attacks, recovers after misses, carries/captures, damages generators, breaks pallets, uses traps, and responds to match/endgame state. No wallhack omniscience unless an ability explicitly grants it.
- Difficulty settings: Easy and Normal, Easy default.
- Authorized solo/operator debug controls: start solo match, choose role, spawn/remove bots, reset, skip countdown, set objective progress, teleport to test location, trigger skill checks, test hit/pallet/trap/rescue/escape, inspect concise match state. Debug controls must not give ordinary multiplayer clients unrestricted control.

## Map
One original map: abandoned industrial research facility overtaken by a mysterious hostile presence. Include seven generators, two gates, multiple capture points, pallets/chase loops, alternative routes, cover/hiding, indoor/outdoor spaces where practical, spawn zones, landmarks, playable boundaries, readable lighting and balanced sightlines. Objectives must be placed with travel distances, visibility and chase balance in mind. Support map restoration after destructive tests. Playable without shaders on a reasonable desktop system.

## Presentation
Create a cohesive original dark horror identity, not a random collection of unrelated mods. Clearly distinguish roles, survivor health states, generator states, pallet states, trap states, gates, captured and eliminated players.

Prioritize custom/properly licensed models, animations, and sounds. Animation categories include survivor movement/injury, generator repair, pallet drop/vault, killer movement/attack, hit reaction, downed movement, carry/capture, rescue/healing, trap placement/trigger, and escape. If temporary simpler animation is necessary, preserve correct gameplay and track polish as incomplete.

Audio cues: ambience, match start, generators, skill-check results, attacks/misses, injury/downing, chase escalation, pallets, traps, capture/rescue, gates, escape/elimination/results. Audio must support gameplay without unfair information leaks.

Custom HUD and menus, not chat-only ordinary gameplay:
- Survivor: generator progress/objectives, teammate status, health, interaction prompts, objective, abilities/cooldowns, endgame/timer.
- Killer: relevant survivor states, ability/cooldown, prompts, endgame, action feedback.
- Include lobby, skill-check UI, pause/settings, tutorial and results. Support common resolutions and GUI scales.

## Multiplayer and security
Server is authoritative for match state, roles, player states, objectives, skill checks, combat, pallets, traps, capture/rescue, gates, escapes/elimination, and victory. Clients request actions; server validates and synchronizes. Handle latency, repeated/invalid input, disconnects and reconnects where feasible. No client-trusted combat/objective outcomes. Dedicated server must not require client-only graphics or shader libraries; shaders are optional.

## Configuration and accessibility
Centralize validated settings for movement, sprint/crouch, attacks, injury, healing/revival, generator repair/skill checks/co-op/regression, objective counts, gate/endgame timing, capture/rescue, pallets, traps, bot difficulty and match time limits. Normal balance changes must not require source edits.

Tutorial explains roles, objectives, repair/skill checks, health/capture, pallets/traps, gates, victory conditions and controls. Important states cannot be communicated by color alone. Include basic sound and HUD settings, contextual prompts and discoverable controls.

## Milestones
1. Foundation: inspect repo, choose compatible toolchain, project/build/resources, instructions, automated checks, client/server launch verification.
2. Playable match core: match state, lobby/roles/player states/objectives/win conditions/map/HUD/synchronization.
3. Objective loop: repair/skill checks, injury/downing/carry/capture/rescue/heal/revive, gates/escape/endgame/results/reset.
4. Chases: attacks/recovery, pallets/stuns/breaking, traps/ability, contextual interactions.
5. AI and solo mode: useful survivor and killer bots, role choice, difficulty, debug panel, complete solo verification.
6. Presentation: original identity, models, animations, audio, UI, map atmosphere.
7. Multiplayer, balance, stabilization and disconnect tests.
8. Distribution: client package, dedicated server package, map/config/resources, optional shader instructions, launch scripts where appropriate, installation/hosting/controls guides, changelog, known issues and test report.

## Distribution and definition of done
The first acceptable release lets one person start a solo match with bots, play the complete objective/chase loop, reach a real win/loss result and restart without restarting Minecraft. The complete project additionally supports five-player multiplayer, dedicated server, original assets, repeat matches and straightforward distribution.

Never confuse compile success with gameplay verification. Clearly distinguish verified, implemented-but-unverified, planned, and blocked work.

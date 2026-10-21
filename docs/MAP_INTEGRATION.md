# Supplied Arena World

## Decision
Use the user's uploaded **Dead By Daylight — Freddy Fazbear's** Minecraft world as the location for Project Entity. Do not generate a replacement map or substitute a generic arena.

## Confirmed from the archive
- World folder name in the archive: §a§lDead By Daylight-Freddy Fazbear's
- Level name in metadata: Dead By Daylight
- Minecraft Java world version: 1.21.1 (DataVersion 3955)
- The archive includes normal world save data, region files, entities, POI data and player data.
- The metadata spawn coordinates are X=-1, Y=-30, Z=-50. The unusually low spawn Y must be checked in-game before using it as a player spawn; do not assume it is a safe ground-level location.

## How the project will use it
1. Preserve the world as the canonical match location.
2. Determine safe survivor spawn points, killer spawn point, arena bounds, generator pads, exit-gate positions, chase loops, pallets, obstacles and hook/capture points from the actual in-game map.
3. Store those coordinates in a separate Project Entity arena configuration so the original map is not destructively modified.
4. Place or activate game objectives from the mod at match start and clear/reset their match state after each round.
5. Use the same world on the dedicated server; clients connect to that server and do not need separate copies of a server's save.

## Install for local inspection
1. Close Minecraft.
2. Extract the uploaded ZIP so that the world folder (the one containing level.dat and region/) is placed inside the Java Edition saves directory.
3. Start Minecraft 1.21.1 and open Dead By Daylight.
4. Make a backup before testing mods or commands on the world.
5. Verify that the world opens and locate the actual playable arena before placing automated objectives.

## Current limitation
The world has been identified from metadata and archive structure, but its playable spawn and route geometry have not yet been verified by launching Minecraft. No objective coordinates are guessed or hard-coded until they can be grounded in the map.

# Installation Notes

This is a development project, not yet a polished public release. Do not install a JAR unless it comes from a successful CI build or a tagged release.

## Requirements
- Minecraft Java Edition 1.21.1
- Java 21 for the development/dedicated-server runtime
- Fabric Loader for Minecraft 1.21.1
- Fabric API matching Minecraft 1.21.1
- The supplied Dead By Daylight world ZIP (canonical arena; see MAP_INTEGRATION.md)

## Local world
1. Back up the original world ZIP.
2. Extract the ZIP into the Minecraft Java Edition saves folder so that level.dat is directly inside the world folder.
3. Launch Minecraft 1.21.1 and verify that the world opens.
4. Do not use the unusual metadata spawn Y=-30 as a confirmed spawn point until it has been checked in-game.

## Current prototype
The source includes an early objective test path, but a successful full build and live Minecraft validation are still required before the mod can be called playable. Follow PROGRESS.md and SMOKE_TEST.md for verification status.

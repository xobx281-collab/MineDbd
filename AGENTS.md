# Project Entity — Repository Instructions

## Mission
Implement the canonical game design in `docs/GAME_DESIGN.md`. This is a playable software project, not a design-only repository. Do not stop at planning or scaffolding.

## Owner communication
The owner is a player and creative director, not a programmer. Keep progress reports concise and nontechnical, preferably in Russian when responding directly to the owner. Never make the owner choose implementation libraries or troubleshoot obscure logs unless unavoidable.

## Engineering rules
- Inspect the existing repository before making changes; preserve unrelated work.
- Verify Minecraft, Java, loader, and library compatibility before locking versions. Record decisions and reasons.
- Keep the repository buildable at each milestone.
- Server-authoritative validation is mandatory for gameplay state, objectives, combat, traps, rescues, gates, and outcomes.
- Separate server gameplay from client-only rendering and UI.
- Do not claim features are complete based only on source code or a successful compilation.
- Run available build/tests after meaningful changes and report what was actually verified.
- Never invent test results, dependency versions, or API capabilities.
- Do not include proprietary Dead by Daylight or Nazzy assets. Use original or properly licensed assets and record provenance.
- Avoid committing credentials, tokens, personal data, machine-specific paths, or generated build output.
- Add automated tests for important rules and invalid state transitions.
- Keep `docs/PROGRESS.md`, installation instructions, and changelog current.
- Prefer a complete playable vertical slice over disconnected features, but do not silently discard requirements for later milestones.
- When blocked, investigate and choose a viable alternative, document it, and continue.

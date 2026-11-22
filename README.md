# qits-agents-roster-javalib

`eu.wohlben.qits:qits-agents-roster` — the qits project's own declaration of its long-running agents
("pets"), as code (epic qits-2517).

## Why

A project's standing agents — what they are told, how they recover after a release, which model
they run — are part of the project, so they are reviewed and released like the rest of it rather
than typed into a form. This jar implements `eu.wohlben.qits.agents.contract.AgentRoster` for
`project() == "qits"`. qits-agents-service (epic qits-2518) puts it on its class path via
`java.util.ServiceLoader`, loads the roster and keeps one running agent per declaration, restarting
an agent exactly when its canonical document's hash changes — see
`eu.wohlben.qits:qits-agents-contract`'s README for the hash and the canonical document it hashes.

Every qits project that wants standing agents keeps one of these, named by convention
`<projectslug>-agents-roster-javalib` — this one is `qits-agents-roster-javalib` because the project
is `qits`.

## What it declares

One agent, `developer` (the contract's default name — `QitsAgentRoster` never calls `.name(...)` on
its builder, deliberately, so it stays whatever the contract defaults to):

- **description**: `Develops qits work items end to end, delegating each to subagents.`
- **brief**: `src/main/resources/eu/wohlben/qits/agents/roster/developer-brief.md` — its standing
  instructions, appended to the harness system prompt. Today a placeholder; epic qits-2520 rewrites
  it with the real operating brief.
- **recoveryPrompt**: `src/main/resources/eu/wohlben/qits/agents/roster/developer-recovery.md` — the
  first turn it is given when a new version of its declaration restarts it.

## Adding an agent

1. Add a declaration to `QitsAgentRoster.agents()`, built with `Agent.builder()`. Give it an explicit
   `.name(...)` (lowercase, starts with a letter, at most 32 characters) — only the roster's first,
   default-named agent skips it.
2. Write its brief and recovery prompt as their own classpath resources under
   `src/main/resources/eu/wohlben/qits/agents/roster/`, and load them in `QitsAgentRoster` the same
   way the `developer` agent's are loaded.
3. Regenerate `src/test/resources/qits-roster.json` from the real
   `AgentRosterDocument.render(new QitsAgentRoster())` output (see `QitsRosterDocumentTest`) and
   review the diff — it is the change that will actually reach qits-agents-service.
4. Run `AgentRosters.validate` (via `QitsAgentRosterTest`) before pushing; it is the same validation
   qits-agents-service applies, so a roster that passes here will not be refused there.

## Changing an agent restarts it

`AgentRosterDocument.hash(agent)` is a SHA-256 over that agent's canonical JSON object alone. Editing
one agent's `brief`, `description`, `recoveryPrompt`, `model` or `effort` changes only that agent's
hash; reordering the roster or editing a different agent changes nothing for it. qits-agents-service
restarts an agent exactly when its hash changes and gives it its (possibly new) `recoveryPrompt` as
the first turn — so a prompt change here is also an operational action, not just a doc edit.

## The golden file

`src/test/resources/qits-roster.json` is this roster's canonical document, generated once from
`AgentRosterDocument.render` and committed. `QitsRosterDocumentTest` pins the live render against it
byte for byte, so any change to an agent's declaration shows up as a reviewed diff of this file —
and that diff is exactly the hash change that will restart the agent in qits-agents-service.

## Building

    ./mvnw -B -ntp -s .qits-maven-settings.xml verify

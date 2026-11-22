# qits-agents-roster-javalib — working notes

Read `README.md` first: it explains what this jar declares and why. These are the rules that bite
when you change it.

- **This is the qits project's own roster**, not a template for every project's roster — it
  implements `eu.wohlben.qits.agents.contract.AgentRoster` for `project() == "qits"` only.
- **The golden file is a wire format.** `src/test/resources/qits-roster.json` pins the canonical
  document `AgentRosterDocument.render` produces for this roster. Changing an agent's `brief`,
  `description`, `recoveryPrompt`, `model` or `effort` changes that agent's hash and so restarts it
  in qits-agents-service; changing the roster at all means regenerating this file and reviewing the
  diff like any other change. Never hand-edit it to make the test pass — regenerate it from the real
  `render` output.
- **The contract pin is a maintenance pin.** `qits.agents-contract.version` in `pom.xml` was written
  by hand once (first adoption, qits-2517), because qits-maintenance bumps only a pin that already
  exists. Every later contract release reaches it as a `maintenance/*` bump; never move it by hand.
- **Brief and recovery prompt are classpath resources**, not Java string literals, so they read like
  prose and diff cleanly: `src/main/resources/eu/wohlben/qits/agents/roster/developer-brief.md` and
  `developer-recovery.md`. `QitsAgentRoster` loads them eagerly and fails loudly
  (`IllegalStateException`) if either is missing from the jar.
- **The `developer` agent is built without calling `.name(...)`.** `AgentDeclaration.DEFAULT_NAME` is
  `"developer"`, and the contract's builder already defaults to it — naming it explicitly would just
  be noise that could drift from the default. Keep it that way.

## Building

    JAVA_TOOL_OPTIONS=-Xmx1g ./mvnw -B -ntp -s .qits-maven-settings.xml verify

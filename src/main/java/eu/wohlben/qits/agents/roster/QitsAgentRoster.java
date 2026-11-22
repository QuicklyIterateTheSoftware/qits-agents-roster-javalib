package eu.wohlben.qits.agents.roster;

import eu.wohlben.qits.agents.contract.Agent;
import eu.wohlben.qits.agents.contract.AgentDeclaration;
import eu.wohlben.qits.agents.contract.AgentRoster;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * The qits project's own roster, listed in
 * {@code META-INF/services/eu.wohlben.qits.agents.contract.AgentRoster}. qits-agents-service loads
 * this through {@link java.util.ServiceLoader} and keeps one running agent per declaration.
 */
public final class QitsAgentRoster implements AgentRoster {

    private static final AgentDeclaration DEVELOPER = Agent.builder()
            // Deliberately no .name(...): AgentDeclaration.DEFAULT_NAME is already "developer", and
            // the builder already starts there, so naming it here would just be noise that could
            // drift from the default.
            .description("Develops qits work items end to end, delegating each to subagents.")
            .brief(resource("developer-brief.md"))
            .recoveryPrompt(resource("developer-recovery.md"))
            .build();

    @Override
    public String project() {
        return "qits";
    }

    @Override
    public List<AgentDeclaration> agents() {
        return List.of(DEVELOPER);
    }

    private static String resource(String name) {
        String path = "eu/wohlben/qits/agents/roster/" + name;
        try (InputStream in = QitsAgentRoster.class.getClassLoader().getResourceAsStream(path)) {
            if (in == null) {
                throw new IllegalStateException("missing classpath resource " + path);
            }
            String text = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            return text.endsWith("\n") ? text.substring(0, text.length() - 1) : text;
        } catch (IOException e) {
            throw new UncheckedIOException("failed to read classpath resource " + path, e);
        }
    }
}

package eu.wohlben.qits.agents.roster;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import eu.wohlben.qits.agents.contract.AgentRosterDocument;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

/**
 * Pins the live render of {@link QitsAgentRoster} against the committed golden file. A failure here
 * means the roster's canonical document changed: review the diff of {@code qits-roster.json}
 * (regenerate it from the real {@link AgentRosterDocument#render} output, never hand-edit it to make
 * this pass) — that diff is exactly the hash change that restarts the affected agent in
 * qits-agents-service.
 */
class QitsRosterDocumentTest {

    @Test
    void rendersTheCommittedGoldenDocument() {
        String rendered = AgentRosterDocument.render(new QitsAgentRoster());
        assertEquals(golden(), rendered);
    }

    private static String golden() {
        String path = "qits-roster.json";
        try (InputStream in = QitsRosterDocumentTest.class.getClassLoader().getResourceAsStream(path)) {
            if (in == null) {
                fail("missing test resource " + path);
            }
            String text = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            return text.endsWith("\n") ? text.substring(0, text.length() - 1) : text;
        } catch (IOException e) {
            throw new UncheckedIOException("failed to read test resource " + path, e);
        }
    }
}

package eu.wohlben.qits.agents.roster;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import eu.wohlben.qits.agents.contract.AgentDeclaration;
import eu.wohlben.qits.agents.contract.AgentRoster;
import eu.wohlben.qits.agents.contract.AgentRosters;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class QitsAgentRosterTest {

    @Test
    void isFoundForProjectQits() {
        Optional<AgentRoster> found = AgentRosters.forProject(QitsAgentRosterTest.class.getClassLoader(), "qits");
        AgentRoster roster = found.orElseThrow();
        assertInstanceOf(QitsAgentRoster.class, roster);
    }

    @Test
    void validates() {
        assertDoesNotThrow(() -> AgentRosters.validate(new QitsAgentRoster()));
    }

    @Test
    void isTheOnlyRosterOnTheClassPath() {
        List<AgentRoster> rosters = AgentRosters.load(QitsAgentRosterTest.class.getClassLoader());
        assertEquals(1, rosters.size());
    }

    @Test
    void declaresExactlyDeveloper() {
        AgentRoster roster = new QitsAgentRoster();
        List<String> names = roster.agents().stream().map(AgentDeclaration::name).toList();
        assertEquals(List.of("developer"), names);
        assertEquals(AgentDeclaration.DEFAULT_NAME, names.get(0));
    }
}

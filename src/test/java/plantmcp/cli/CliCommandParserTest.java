package plantmcp.cli;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CliCommandParserTest {

	@Test
	@SuppressWarnings("DataFlowIssue")
	void nullArgs_throwsWithNoCommandMessage() {
		var ex = assertThrows(IllegalArgumentException.class, () -> CliCommandParser.parse(null));
		assertTrue(ex.getMessage().contains("No command provided"));
	}

	@Test
	void emptyArgs_throwsWithNoCommandMessage() {
		var ex = assertThrows(IllegalArgumentException.class, () -> CliCommandParser.parse(new String[] {}));
		assertTrue(ex.getMessage().contains("No command provided"));
	}

	@Test
	void unknownCommand_throwsWithUnknownCommandMessage() {
		var ex = assertThrows(IllegalArgumentException.class, () -> CliCommandParser.parse(new String[] { "foo" }));
		assertTrue(ex.getMessage().contains("Unknown command 'foo'"));
	}

	@Test
	void emptyStringCommand_throwsWithUnknownCommandMessage() {
		var ex = assertThrows(IllegalArgumentException.class, () -> CliCommandParser.parse(new String[] { "" }));
		assertTrue(ex.getMessage().contains("Unknown command ''"));
	}

}

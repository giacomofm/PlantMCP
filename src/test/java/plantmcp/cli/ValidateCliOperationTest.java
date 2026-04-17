package plantmcp.cli;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ValidateCliOperationTest {

	@TempDir
	Path tempDir;

	@ParameterizedTest
	@ValueSource(strings = { "validate", "VALIDATE", "Validate", "vAlIdAtE" })
	void validateCommand_caseInsensitive_returnsValidateOperation(String command) {
		var operation = CliCommandParser.parse(new String[] { command, "diagram.puml" });
		assertInstanceOf(ValidateCliOperation.class, operation);
	}

	@Test
	void validateCommand_withPath_returnsValidateOperation() {
		var operation = CliCommandParser.parse(new String[] { "validate", "diagram.puml" });
		assertInstanceOf(ValidateCliOperation.class, operation);
	}

	@Test
	void validateCommand_withMultipleArgs_returnsValidateOperation() {
		var operation = CliCommandParser.parse(new String[] { "validate", "path/to/diagram.puml", "extra" });
		assertInstanceOf(ValidateCliOperation.class, operation);
	}

	@Test
	void validateCommand_missingPath_throwsIllegalArgumentException() {
		var ex = assertThrows(IllegalArgumentException.class,
				() -> CliCommandParser.parse(new String[] { "validate" }));
		assertTrue(ex.getMessage().contains("Missing path"));
	}

	@Test
	void execute_fileNotFound_throwsIllegalArgumentException() {
		var op = CliCommandParser.parse(new String[] { "validate", "/nonexistent/path.puml" });
		assertThrows(IllegalArgumentException.class, op::execute);
	}

	@Test
	void execute_validDiagram_returnsZero() throws Exception {
		var puml = tempDir.resolve("valid.puml");
		Files.writeString(puml, """
				@startuml
				Alice -> Bob: Hello
				@enduml
				""");
		var op = CliCommandParser.parse(new String[] { "validate", puml.toString() });
		assertEquals(0, op.execute());
	}

	@Test
	void execute_invalidDiagram_returnsThree() throws Exception {
		var puml = tempDir.resolve("invalid.puml");
		Files.writeString(puml, """
				@startuml
				this_is_not_valid_plantuml_syntax
				@enduml
				""");
		var op = CliCommandParser.parse(new String[] { "validate", puml.toString() });
		assertEquals(3, op.execute());
	}

}

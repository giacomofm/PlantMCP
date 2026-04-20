package plantmcp.cli;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static plantmcp.TestUtils.VALID_DIAGRAM;

class EncodeCliOperationTest {

	@TempDir
	Path tempDir;

	@Test
	void encodeCommand_withPath_returnsEncodeOperation() {
		var operation = CliCommandParser.parse(new String[] { "encode", "diagram.puml" });
		assertInstanceOf(EncodeCliOperation.class, operation);
	}

	@Test
	void encodeCommand_missingPath_throwsIllegalArgumentException() {
		var ex = assertThrows(IllegalArgumentException.class, () -> CliCommandParser.parse(new String[] { "encode" }));
		assertTrue(ex.getMessage().contains("Missing path"));
	}

	@Test
	void execute_fileNotFound_throwsIllegalArgumentException() {
		var op = CliCommandParser.parse(new String[] { "encode", "/nonexistent/path.puml" });
		assertThrows(IllegalArgumentException.class, op::execute);
	}

	@Test
	void execute_validDiagram_returnsZero() throws Exception {
		var puml = tempDir.resolve("valid.puml");
		Files.writeString(puml, VALID_DIAGRAM);
		var op = CliCommandParser.parse(new String[] { "encode", puml.toString() });
		assertEquals(0, op.execute());
	}

}

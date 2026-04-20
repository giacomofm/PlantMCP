package plantmcp.cli;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static plantmcp.TestUtils.VALID_DIAGRAM;
import static plantmcp.TestUtils.VALID_DIAGRAM_ENCODED;

class RenderCliOperationTest {

	@TempDir
	Path tempDir;

	@Test
	void renderCommand_withPaths_returnsRenderOperation() {
		var operation = CliCommandParser.parse(new String[] { "render", "diagram.puml", "diagram.svg" });
		assertInstanceOf(RenderCliOperation.class, operation);
	}

	@Test
	void renderCommand_missingArgs_throwsIllegalArgumentException() {
		var ex = assertThrows(IllegalArgumentException.class,
				() -> CliCommandParser.parse(new String[] { "render", "diagram.puml" }));
		assertTrue(ex.getMessage().contains("Missing arguments"));
	}

	@Test
	void renderCommand_noArgs_throwsIllegalArgumentException() {
		assertThrows(IllegalArgumentException.class, () -> CliCommandParser.parse(new String[] { "render" }));
	}

	@Test
	void execute_fileNotFound_throwsIllegalArgumentException() {
		var op = CliCommandParser.parse(new String[] { "render", "/nonexistent/path.puml", "out.svg" });
		assertThrows(IllegalArgumentException.class, op::execute);
	}

	@Test
	void execute_validDiagram_returnsZeroAndCreatesSvgFile() throws Exception {
		var puml = tempDir.resolve("valid.puml");
		var svg = tempDir.resolve("valid.svg");
		Files.writeString(puml, VALID_DIAGRAM);

		var op = CliCommandParser.parse(new String[] { "render", puml.toString(), svg.toString() });
		assertEquals(0, op.execute());

		assertTrue(Files.exists(svg), "SVG output file should exist");
		String content = Files.readString(svg);
		assertTrue(content.contains("<svg"), "Output file should contain SVG markup");
		assertTrue(content.contains(VALID_DIAGRAM_ENCODED), "Output SVG should contain encoded diagram data");
	}

}

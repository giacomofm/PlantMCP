package plantmcp.mcp;

import io.modelcontextprotocol.spec.McpSchema;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import plantmcp.TestUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ValidateFileMcpToolTest {

	private static final ValidateFileMcpTool tool = new ValidateFileMcpTool();

	@TempDir
	Path tempDir;

	@Test
	void validFile_returnsOk() throws Exception {
		var file = tempDir.resolve("valid.puml");
		Files.writeString(file, TestUtils.VALID_DIAGRAM);

		var args = Map.of("path", (Object) file.toString());
		var req = McpSchema.CallToolRequest.builder().name("test-validate-file").arguments(args).build();
		var res = tool.handler().apply(null, req);

		assertFalse(res.isError(), "Expected no error for valid diagram file");
		assertFalse(res.content().isEmpty());

		var textContent = (McpSchema.TextContent) res.content().getFirst();
		assertEquals("Schema is valid", textContent.text());
	}

	@Test
	void invalidFile_returnsErrors() throws Exception {
		var file = tempDir.resolve("invalid.puml");
		Files.writeString(file, """
				@startuml
				this_is_not_valid_plantuml_syntax
				@enduml
				""");

		var args = Map.of("path", (Object) file.toString());
		var req = McpSchema.CallToolRequest.builder().name("test-validate-file").arguments(args).build();
		var res = tool.handler().apply(null, req);

		assertTrue(res.isError(), "Expected error for invalid diagram file");
		assertFalse(res.content().isEmpty());
	}

	@Test
	void nonExistentFile_returnsFileNotFoundError() {
		var args = Map.of("path", (Object) "/nonexistent/path/diagram.puml");
		var req = McpSchema.CallToolRequest.builder().name("test-validate-file").arguments(args).build();
		var res = tool.handler().apply(null, req);

		assertTrue(res.isError(), "Expected error for missing file");
		assertFalse(res.content().isEmpty());

		var textContent = (McpSchema.TextContent) res.content().getFirst();
		assertTrue(textContent.text().contains("File not found"), "Expected file-not-found message");
	}

	@Test
	void invalidArgs_returnError() {
		var args = Map.of("path", (Object) 0);
		var req = McpSchema.CallToolRequest.builder().name("test-validate-file").arguments(args).build();
		var res = tool.handler().apply(null, req);

		assertTrue(res.isError(), "Expected error for non-string input");

		var textContent = (McpSchema.TextContent) res.content().getFirst();
		assertEquals("Invalid input data. Expected a string.", textContent.text());
	}

	@Test
	void nullArgs_returnError() {
		var args = new HashMap<String, Object>();
		args.put("path", null);
		var req = McpSchema.CallToolRequest.builder().name("test-validate-file").arguments(args).build();
		var res = tool.handler().apply(null, req);

		assertTrue(res.isError(), "Expected error for null input");

		var textContent = (McpSchema.TextContent) res.content().getFirst();
		assertEquals("Invalid input data. Expected a string.", textContent.text());
	}
}

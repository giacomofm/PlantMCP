package plantmcp.mcp;

import io.modelcontextprotocol.spec.McpSchema;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import plantmcp.TestUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidateMcpToolTest {

	private static final ValidateMcpTool tool = new ValidateMcpTool();

	@TempDir
	Path tempDir;

	// --- data (source) ---

	@Test
	void validSource_returnsOk() {
		var args = Map.of("data", (Object) TestUtils.VALID_DIAGRAM);
		var req = McpSchema.CallToolRequest.builder().name("test-validate").arguments(args).build();
		var res = tool.handler().apply(null, req);

		assertFalse(res.isError(), "Expected no error for valid source");
		var text = (McpSchema.TextContent) res.content().getFirst();
		assertEquals("Schema is valid", text.text());
	}

	@Test
	void invalidSource_returnsErrors() {
		var args = Map.of("data", (Object) "@startuml\nthis_is_not_valid\n@enduml");
		var req = McpSchema.CallToolRequest.builder().name("test-validate").arguments(args).build();
		var res = tool.handler().apply(null, req);

		assertTrue(res.isError(), "Expected error for invalid source");
		assertFalse(res.content().isEmpty());
	}

	@Test
	void emptySource_returnsError() {
		var args = Map.of("data", (Object) "");
		var req = McpSchema.CallToolRequest.builder().name("test-validate").arguments(args).build();
		var res = tool.handler().apply(null, req);

		assertTrue(res.isError(), "Expected error for empty source");
		assertFalse(res.content().isEmpty());
	}

	// --- path (file) ---

	@Test
	void validFile_returnsOk() throws Exception {
		var file = tempDir.resolve("valid.puml");
		Files.writeString(file, TestUtils.VALID_DIAGRAM);

		var args = Map.of("path", (Object) file.toString());
		var req = McpSchema.CallToolRequest.builder().name("test-validate").arguments(args).build();
		var res = tool.handler().apply(null, req);

		assertFalse(res.isError(), "Expected no error for valid file");
		var text = (McpSchema.TextContent) res.content().getFirst();
		assertEquals("Schema is valid", text.text());
	}

	@Test
	void invalidFile_returnsErrors() throws Exception {
		var file = tempDir.resolve("invalid.puml");
		Files.writeString(file, "@startuml\nthis_is_not_valid\n@enduml");

		var args = Map.of("path", (Object) file.toString());
		var req = McpSchema.CallToolRequest.builder().name("test-validate").arguments(args).build();
		var res = tool.handler().apply(null, req);

		assertTrue(res.isError(), "Expected error for invalid file");
		assertFalse(res.content().isEmpty());
	}

	@Test
	void nonExistentFile_returnsFileNotFoundError() {
		var args = Map.of("path", (Object) "/nonexistent/path/diagram.puml");
		var req = McpSchema.CallToolRequest.builder().name("test-validate").arguments(args).build();
		var res = tool.handler().apply(null, req);

		assertTrue(res.isError(), "Expected error for missing file");
		var text = (McpSchema.TextContent) res.content().getFirst();
		assertTrue(text.text().contains("error occurred while reading file"));
	}

	// --- mutual exclusion ---

	@Test
	void bothDataAndPath_returnsError() throws Exception {
		var file = tempDir.resolve("valid.puml");
		Files.writeString(file, TestUtils.VALID_DIAGRAM);

		var args = Map.of("data", TestUtils.VALID_DIAGRAM, "path", (Object) file.toString());
		var req = McpSchema.CallToolRequest.builder().name("test-validate").arguments(args).build();
		var res = tool.handler().apply(null, req);

		assertTrue(res.isError(), "Expected error when both data and path are provided");
		var text = (McpSchema.TextContent) res.content().getFirst();
		assertTrue(text.text().contains("not both"));
	}

	// --- invalid/missing args ---

	@Test
	void noArgs_returnsError() {
		var args = new HashMap<String, Object>();
		var req = McpSchema.CallToolRequest.builder().name("test-validate").arguments(args).build();
		var res = tool.handler().apply(null, req);

		assertTrue(res.isError(), "Expected error when no args provided");
		var text = (McpSchema.TextContent) res.content().getFirst();
		assertTrue(text.text().contains("'data'") && text.text().contains("'path'"));
	}

	@Test
	void nonStringData_returnsError() {
		var args = Map.of("data", (Object) 42);
		var req = McpSchema.CallToolRequest.builder().name("test-validate").arguments(args).build();
		var res = tool.handler().apply(null, req);

		assertTrue(res.isError(), "Expected error for non-string data");
		var text = (McpSchema.TextContent) res.content().getFirst();
		assertTrue(text.text().contains("Invalid input data"));
	}

	@Test
	void nonStringPath_returnsError() {
		var args = Map.of("path", (Object) 42);
		var req = McpSchema.CallToolRequest.builder().name("test-validate").arguments(args).build();
		var res = tool.handler().apply(null, req);

		assertTrue(res.isError(), "Expected error for non-string path");
		var text = (McpSchema.TextContent) res.content().getFirst();
		assertTrue(text.text().contains("Invalid input data"));
	}

	@Test
	void nullData_returnsError() {
		var args = new HashMap<String, Object>();
		args.put("data", null);
		var req = McpSchema.CallToolRequest.builder().name("test-validate").arguments(args).build();
		var res = tool.handler().apply(null, req);

		assertTrue(res.isError(), "Expected error for null data");
		var text = (McpSchema.TextContent) res.content().getFirst();
		assertTrue(text.text().contains("Invalid input data"));
	}

	@Test
	void nullPath_returnsError() {
		var args = new HashMap<String, Object>();
		args.put("path", null);
		var req = McpSchema.CallToolRequest.builder().name("test-validate").arguments(args).build();
		var res = tool.handler().apply(null, req);

		assertTrue(res.isError(), "Expected error for null path");
		var text = (McpSchema.TextContent) res.content().getFirst();
		assertTrue(text.text().contains("Invalid input data"));
	}
}

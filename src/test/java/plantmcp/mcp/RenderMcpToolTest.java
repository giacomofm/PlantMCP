package plantmcp.mcp;

import io.modelcontextprotocol.spec.McpSchema;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static plantmcp.TestUtils.VALID_DIAGRAM;

class RenderMcpToolTest {

	private static final RenderMcpTool tool = new RenderMcpTool();

	@TempDir
	Path tempDir;

	@Test
	void validDiagram_writesSvgFile() throws Exception {
		var outputPath = tempDir.resolve("diagram.svg");
		var args = Map.of("data", VALID_DIAGRAM, "path", (Object) outputPath.toString());
		var req = McpSchema.CallToolRequest.builder().name("test-render").arguments(args).build();
		var res = tool.handler().apply(null, req);

		assertFalse(res.isError(), "Expected no error for valid diagram");
		assertFalse(res.content().isEmpty(), "Expected confirmation message");

		var text = ((McpSchema.TextContent) res.content().getFirst()).text();
		assertTrue(text.startsWith("SVG saved to"), "Expected confirmation message");

		assertTrue(Files.exists(outputPath), "SVG file should exist");
		String svgContent = Files.readString(outputPath);
		assertTrue(svgContent.contains("<svg"), "File should contain SVG markup");
	}

	@Test
	void nonStringData_returnsError() {
		var args = Map.of("data", 42, "path", (Object) "out.svg");
		var req = McpSchema.CallToolRequest.builder().name("test-render").arguments(args).build();
		var res = tool.handler().apply(null, req);

		assertTrue(res.isError(), "Expected error for non-string data");
		var text = ((McpSchema.TextContent) res.content().getFirst()).text();
		assertEquals("Invalid input data. Expected a string.", text);
	}

	@Test
	void nullData_returnsError() {
		var args = new HashMap<String, Object>();
		args.put("data", null);
		args.put("path", "out.svg");
		var req = McpSchema.CallToolRequest.builder().name("test-render").arguments(args).build();
		var res = tool.handler().apply(null, req);

		assertTrue(res.isError(), "Expected error for null data");
		var text = ((McpSchema.TextContent) res.content().getFirst()).text();
		assertEquals("Invalid input data. Expected a string.", text);
	}

	@Test
	void nonStringPath_returnsError() {
		var args = Map.of("data", VALID_DIAGRAM, "path", (Object) 99);
		var req = McpSchema.CallToolRequest.builder().name("test-render").arguments(args).build();
		var res = tool.handler().apply(null, req);

		assertTrue(res.isError(), "Expected error for non-string path");
		var text = ((McpSchema.TextContent) res.content().getFirst()).text();
		assertEquals("Invalid input data. Expected a string.", text);
	}

}

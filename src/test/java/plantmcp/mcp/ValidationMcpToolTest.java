package plantmcp.mcp;

import io.modelcontextprotocol.spec.McpSchema;
import org.junit.jupiter.api.Test;
import plantmcp.TestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ValidationMcpToolTest {

	private static final ValidationMcpTool tool = new ValidationMcpTool();

	@Test
	void validDiagram_returnsOk() {
		var args = Map.of("data", (Object) TestUtils.VALID_DIAGRAM);
		var req = McpSchema.CallToolRequest.builder().name("test-validation").arguments(args).build();
		var res = tool.handler().apply(null, req);

		assertFalse(res.isError(), "Expected no error for valid diagram");
		assertFalse(res.content().isEmpty(), "Expected content in response");

		var content = res.content().getFirst();
		assertInstanceOf(McpSchema.TextContent.class, content, "Expected text content in response");

		var textContent = (McpSchema.TextContent) content;
		assertEquals("Schema is valid", textContent.text(), "Expected validation success message");
	}

	@Test
	void invalidArgs_returnError() {
		var args = Map.of("data", (Object) 0);
		var req = McpSchema.CallToolRequest.builder().name("test-validation").arguments(args).build();
		var res = tool.handler().apply(null, req);

		assertTrue(res.isError(), "Expected error for non-string input");
		assertFalse(res.content().isEmpty(), "Expected content in response");

		var content = res.content().getFirst();
		assertInstanceOf(McpSchema.TextContent.class, content);

		var textContent = (McpSchema.TextContent) content;
		assertEquals("Invalid input data. Expected a string.", textContent.text());
	}

	@Test
	void nullArgs_returnError() {
		var args = new HashMap<String, Object>();
		args.put("data", null);
		var req = McpSchema.CallToolRequest.builder().name("test-validation").arguments(args).build();
		var res = tool.handler().apply(null, req);

		assertTrue(res.isError(), "Expected error for null input");
		assertFalse(res.content().isEmpty(), "Expected content in response");

		var content = res.content().getFirst();
		assertInstanceOf(McpSchema.TextContent.class, content);

		var textContent = (McpSchema.TextContent) content;
		assertEquals("Invalid input data. Expected a string.", textContent.text());
	}

	@Test
	void emptyStringArgs_returnError() {
		var args = Map.of("data", (Object) "");
		var req = McpSchema.CallToolRequest.builder().name("test-validation").arguments(args).build();
		var res = tool.handler().apply(null, req);

		assertTrue(res.isError(), "Expected error for empty string input");
		assertFalse(res.content().isEmpty(), "Expected content in response");
		assertInstanceOf(McpSchema.TextContent.class, res.content().getFirst());
	}
}
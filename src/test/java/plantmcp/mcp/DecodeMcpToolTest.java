package plantmcp.mcp;

import io.modelcontextprotocol.spec.McpSchema;
import org.junit.jupiter.api.Test;
import plantmcp.TestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DecodeMcpToolTest {

	private static final DecodeMcpTool tool = new DecodeMcpTool();

	@Test
	void validEncodedString_returnsDecodedSource() {
		var args = Map.of("data", (Object) TestUtils.VALID_DIAGRAM_ENCODED);
		var req = McpSchema.CallToolRequest.builder().name("test-decode").arguments(args).build();
		var res = tool.handler().apply(null, req);

		assertFalse(res.isError(), "Expected no error for valid encoded string");
		assertFalse(res.content().isEmpty(), "Expected content in response");

		var content = res.content().getFirst();
		assertInstanceOf(McpSchema.TextContent.class, content);

		var textContent = (McpSchema.TextContent) content;
		assertEquals(TestUtils.VALID_DIAGRAM, textContent.text());
	}

	@Test
	void invalidArgs_returnError() {
		var args = Map.of("data", (Object) 0);
		var req = McpSchema.CallToolRequest.builder().name("test-decode").arguments(args).build();
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
		var req = McpSchema.CallToolRequest.builder().name("test-decode").arguments(args).build();
		var res = tool.handler().apply(null, req);

		assertTrue(res.isError(), "Expected error for null input");
		assertFalse(res.content().isEmpty(), "Expected content in response");

		var content = res.content().getFirst();
		assertInstanceOf(McpSchema.TextContent.class, content);

		var textContent = (McpSchema.TextContent) content;
		assertEquals("Invalid input data. Expected a string.", textContent.text());
	}

	@Test
	void invalidEncodedString_returnError() {
		var args = Map.of("data", (Object) "!!!invalid-encoded-data!!!");
		var req = McpSchema.CallToolRequest.builder().name("test-decode").arguments(args).build();
		var res = tool.handler().apply(null, req);

		assertTrue(res.isError(), "Expected error for invalid encoded string");
		assertFalse(res.content().isEmpty(), "Expected content in response");

		var content = res.content().getFirst();
		assertInstanceOf(McpSchema.TextContent.class, content);

		var textContent = (McpSchema.TextContent) content;
		assertTrue(textContent.text().contains("An error occurred during decoding"),
				"Expected decoding error message, got: " + textContent.text());
	}

}

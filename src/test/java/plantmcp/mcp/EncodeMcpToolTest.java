package plantmcp.mcp;

import io.modelcontextprotocol.spec.McpSchema;
import org.junit.jupiter.api.Test;
import plantmcp.TestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EncodeMcpToolTest {

	private static final EncodeMcpTool tool = new EncodeMcpTool();

	@Test
	void validDiagram_returnsEncodedString() {
		var args = Map.of("data", (Object) TestUtils.VALID_DIAGRAM);
		var req = McpSchema.CallToolRequest.builder().name("test-encode").arguments(args).build();
		var res = tool.handler().apply(null, req);

		assertFalse(res.isError(), "Expected no error for valid diagram");
		assertFalse(res.content().isEmpty(), "Expected content in response");

		var content = res.content().getFirst();
		assertInstanceOf(McpSchema.TextContent.class, content);

		var textContent = (McpSchema.TextContent) content;
		assertEquals(TestUtils.VALID_DIAGRAM_ENCODED, textContent.text());
	}

	@Test
	void invalidArgs_returnError() {
		var args = Map.of("data", (Object) 0);
		var req = McpSchema.CallToolRequest.builder().name("test-encode").arguments(args).build();
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
		var req = McpSchema.CallToolRequest.builder().name("test-encode").arguments(args).build();
		var res = tool.handler().apply(null, req);

		assertTrue(res.isError(), "Expected error for null input");
		assertFalse(res.content().isEmpty(), "Expected content in response");

		var content = res.content().getFirst();
		assertInstanceOf(McpSchema.TextContent.class, content);

		var textContent = (McpSchema.TextContent) content;
		assertEquals("Invalid input data. Expected a string.", textContent.text());
	}

	@Test
	void emptyString_returnsEncodedResult() {
		var args = Map.of("data", (Object) "");
		var req = McpSchema.CallToolRequest.builder().name("test-encode").arguments(args).build();
		var res = tool.handler().apply(null, req);

		assertFalse(res.isError(), "Empty string is valid input for encoding");
		assertFalse(res.content().isEmpty(), "Expected content in response");
		assertInstanceOf(McpSchema.TextContent.class, res.content().getFirst());
	}

	@Test
	void roundTrip_encodeThenDecode() {
		var encodeArgs = Map.of("data", (Object) TestUtils.VALID_DIAGRAM);
		var encodeReq = McpSchema.CallToolRequest.builder().name("test-encode").arguments(encodeArgs).build();
		var encodeRes = tool.handler().apply(null, encodeReq);
		assertFalse(encodeRes.isError());

		var encoded = ((McpSchema.TextContent) encodeRes.content().getFirst()).text();

		var decodeTool = new DecodeMcpTool();

		var decodeArgs = Map.of("data", (Object) encoded);
		var decodeReq = McpSchema.CallToolRequest.builder().name("test-decode").arguments(decodeArgs).build();
		var decodeRes = decodeTool.handler().apply(null, decodeReq);

		assertFalse(decodeRes.isError());
		var decoded = ((McpSchema.TextContent) decodeRes.content().getFirst()).text();
		assertEquals(TestUtils.VALID_DIAGRAM, decoded);
	}
}

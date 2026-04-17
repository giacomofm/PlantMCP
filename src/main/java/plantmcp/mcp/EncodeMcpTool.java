package plantmcp.mcp;

import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.function.BiFunction;

class EncodeMcpTool extends CustomMcpTool {

	@Override
	protected String name() {
		return "encode";
	}

	@Override
	protected String title() {
		return "Encode a PlantUML diagram to a shareable string";
	}

	@Override
	protected BiFunction<McpSyncServerExchange, McpSchema.CallToolRequest, McpSchema.CallToolResult> handler() {
		return (_, req) -> {
			var data = req.arguments().get("data");

			log.debug("Received encode request with data: {}", data);

			if (data instanceof String strData) {
				return encodePlantUml(strData);
			}

			return McpSchema.CallToolResult.builder()
					.isError(true)
					.addTextContent("Invalid input data. Expected a string.")
					.build();
		};
	}

	private McpSchema.CallToolResult encodePlantUml(String data) {
		try {
			var encoded = engine.encode(data);
			return McpSchema.CallToolResult.builder().addTextContent(encoded).build();
		} catch (Exception e) {
			log.warn("Error during encoding", e);

			return McpSchema.CallToolResult.builder()
					.isError(true)
					.addTextContent("An error occurred during encoding: " + e.getMessage())
					.build();
		}
	}
}

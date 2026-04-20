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
	protected String description() {
		return """
				Encode PlantUML source text to a compressed, URL-safe string.

				Input:
				- data: PlantUML source (string). Should be valid — run `validation` first.

				Output:
				- isError=false: encoded string (Deflate + custom base64).
				  Use it to build a shareable URL: https://www.plantuml.com/plantuml/uml/<encoded>
				- isError=true: error message if encoding fails.

				Notes:
				- Always validate the source before encoding.
				- Encoding invalid source may produce a string, but the diagram will not render correctly.
				""".trim();
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

package plantmcp.mcp;

import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.function.BiFunction;

class DecodeMcpTool extends CustomMcpTool {

	@Override
	protected String name() {
		return "decode";
	}

	@Override
	protected String title() {
		return "Decode a PlantUML encoded string back to source";
	}

	@Override
	protected String description() {
		return """
				Decode a PlantUML encoded string back to the original source text.

				Input:
				- data: a PlantUML encoded string, as returned by the `encode` tool or extracted
				  from a PlantUML URL (https://www.plantuml.com/plantuml/uml/<encoded>).

				Output:
				- isError=false: the original PlantUML source text (@startuml ... @enduml).
				- isError=true: error message if the encoded string is invalid or cannot be decoded.

				Notes:
				- Use this tool when the user provides a PlantUML URL or encoded string and wants
				  to view or edit the underlying diagram source.
				""".trim();
	}

	@Override
	protected BiFunction<McpSyncServerExchange, McpSchema.CallToolRequest, McpSchema.CallToolResult> handler() {
		return (_, req) -> {
			var data = req.arguments().get("data");

			log.debug("Received decode request with data: {}", data);

			if (data instanceof String strData) {
				return decodePlantUml(strData);
			}

			return McpSchema.CallToolResult.builder()
					.isError(true)
					.addTextContent("Invalid input data. Expected a string.")
					.build();
		};
	}

	private McpSchema.CallToolResult decodePlantUml(String data) {
		try {
			var decoded = engine.decode(data);
			return McpSchema.CallToolResult.builder().addTextContent(decoded).build();
		} catch (Exception e) {
			log.warn("Error during decoding", e);

			return McpSchema.CallToolResult.builder()
					.isError(true)
					.addTextContent("An error occurred during decoding: " + e.getMessage())
					.build();
		}
	}
}

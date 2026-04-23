package plantmcp.mcp;

import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.function.BiFunction;

class ValidateSourceMcpTool extends CustomMcpTool {

	@Override
	protected String name() {
		return "validate_source";
	}

	@Override
	protected String title() {
		return "Validate PlantUML source text";
	}

	@Override
	protected String description() {
		return """
				Validate PlantUML source text and return syntax diagnostics.
				
				Input:
				- data: PlantUML source (string). Typically includes @startuml ... @enduml.
				
				Output:
				- isError=false: text "Schema is valid" when no errors occurs.
				- isError=true: w/ possible list of parser errors or a general error message.
				
				Notes:
				- Validation only (no rendering).
				""".trim();
	}

	@Override
	protected BiFunction<McpSyncServerExchange, McpSchema.CallToolRequest, McpSchema.CallToolResult> handler() {
		return (_, req) -> {
			var data = req.arguments().get("data");

			log.debug("Received validate_source request with data: {}", data);

			if (data instanceof String strData) {
				return validatePlantUmlSource(strData);
			}

			return McpSchema.CallToolResult.builder()
					.isError(true)
					.addTextContent("Invalid input data. Expected a string.")
					.build();
		};
	}

	private McpSchema.CallToolResult validatePlantUmlSource(String data) {
		try {
			var errors = engine.getErrors(data);

			if (errors.isEmpty()) {
				return McpSchema.CallToolResult.builder().addTextContent("Schema is valid").build();
			}

			return McpSchema.CallToolResult.builder().isError(true).textContent(errors).build();
		} catch (Exception e) {
			log.warn("Error during validation", e);

			return McpSchema.CallToolResult.builder()
					.isError(true)
					.addTextContent("An error occurred during validation: " + e.getMessage())
					.build();
		}
	}
}

package plantmcp.mcp;

import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

class ValidateMcpTool extends CustomMcpTool {

	@Override
	protected String name() {
		return "validate";
	}

	@Override
	protected String title() {
		return "Validate PlantUML source text or file";
	}

	@Override
	protected String description() {
		return """
				Validate PlantUML source text and return syntax diagnostics.
				
				Input (provide exactly one):
				- data: PlantUML source (string). Typically includes @startuml ... @enduml.
				- path: absolute or relative path to a .puml file (string).
				
				Output:
				- isError=false: text "Schema is valid" when no errors occurs.
				- isError=true: w/ possible list of parser errors or a general error message.
				
				Notes:
				- Validation only (no rendering).
				- Use this tool when the user refers to a file on disk rather than pasting source text.
				- When running inside a Docker container, use just the filename (e.g. `diagram.puml`),
				  not an absolute path. Mount your working directory to /data when starting the container.
				""".trim();
	}

	@Override
	protected McpSchema.JsonSchema schema() {
		return new McpSchema.JsonSchema(
				//@formatter:off
				"object",
				Map.of(
						"data", Map.of("type", "string"),
						"path", Map.of("type", "string")
				),
				List.of(),
				false,
				null,
				null
				//@formatter:on
		);
	}

	@Override
	protected BiFunction<McpSyncServerExchange, McpSchema.CallToolRequest, McpSchema.CallToolResult> handler() {
		return (_, req) -> {
			var data = req.arguments().get("data");
			var path = req.arguments().get("path");

			log.debug("Received validate request with path: {}, data: {}", path, data);

			boolean hasData = data instanceof String;
			boolean hasPath = path instanceof String;
			if (hasData && hasPath) {
				return McpSchema.CallToolResult.builder()
						.isError(true)
						.addTextContent("Provide either 'data' or 'path', not both.")
						.build();
			}
			if (hasData) {
				return validate((String) data);
			}
			if (hasPath) {
				return validateFile((String) path);
			}
			return McpSchema.CallToolResult.builder()
					.isError(true)
					.addTextContent("Invalid input data. Expected 'data' (source string) or 'path' (file path).")
					.build();
		};
	}

	private McpSchema.CallToolResult validateFile(String path) {
		try {
			String source = Files.readString(Path.of(EnvironmentUtils.resolvePath(path)));
			return validate(source);
		} catch (IOException e) {
			log.warn("Error reading file for validation", e);

			return McpSchema.CallToolResult.builder()
					.isError(true)
					.addTextContent("An error occurred while reading file: " + path)
					.build();
		}
	}

	private McpSchema.CallToolResult validate(String data) {
		try {
			var errors = engine.getErrors(data);
			if (errors.isEmpty()) {
				return McpSchema.CallToolResult.builder().addTextContent("Schema is valid").build();
			}
			return McpSchema.CallToolResult.builder().isError(true).textContent(errors).build();
		} catch (Exception e) {
			log.warn("Error during source validation", e);

			return McpSchema.CallToolResult.builder()
					.isError(true)
					.addTextContent("An error occurred during validation: " + e.getMessage())
					.build();
		}
	}

}

package plantmcp.mcp;

import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema;

import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

class ValidateFileMcpTool extends CustomMcpTool {

	@Override
	protected String name() {
		return "validate_file";
	}

	@Override
	protected String title() {
		return "Validate a PlantUML file by path";
	}

	@Override
	protected String description() {
		return """
				Validate a PlantUML file from disk and return syntax diagnostics.
				
				Input:
				- path: absolute or relative path to a .puml file (string).
				
				Output:
				- isError=false: text "Schema is valid" when no errors occur.
				- isError=true: list of parser errors, or a general error message.
				
				Notes:
				- Validation only (no rendering).
				- Use this tool when the user refers to a file on disk rather than pasting source text.
				""".trim();
	}

	@Override
	protected McpSchema.JsonSchema schema() {
		return new McpSchema.JsonSchema(
				//@formatter:off
				"object",
				Map.of("path", Map.of("type", "string")),
				List.of("path"),
				false,
				null,
				null
				//@formatter:on
		);
	}

	@Override
	protected BiFunction<McpSyncServerExchange, McpSchema.CallToolRequest, McpSchema.CallToolResult> handler() {
		return (_, req) -> {
			var path = req.arguments().get("path");

			log.debug("Received validate_file request with path: {}", path);

			if (!(path instanceof String strPath)) {
				return McpSchema.CallToolResult.builder()
						.isError(true)
						.addTextContent("Invalid input data. Expected a string.")
						.build();
			}

			return validateFile(strPath);
		};
	}

	private McpSchema.CallToolResult validateFile(String path) {
		try {
			String source = Files.readString(Path.of(path));
			var errors = engine.getErrors(source);

			if (errors.isEmpty()) {
				return McpSchema.CallToolResult.builder().addTextContent("Schema is valid").build();
			}

			return McpSchema.CallToolResult.builder().isError(true).textContent(errors).build();
		} catch (NoSuchFileException e) {
			return McpSchema.CallToolResult.builder()
					.isError(true)
					.addTextContent("File not found: " + path)
					.build();
		} catch (Exception e) {
			log.warn("Error during file validation", e);
			return McpSchema.CallToolResult.builder()
					.isError(true)
					.addTextContent("An error occurred during validation: " + e.getMessage())
					.build();
		}
	}
}

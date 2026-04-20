package plantmcp.mcp;

import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

class RenderMcpTool extends CustomMcpTool {

	@Override
	protected String name() {
		return "render";
	}

	@Override
	protected String title() {
		return "Render a PlantUML diagram to SVG";
	}

	@Override
	protected String description() {
		return """
				Render PlantUML source text to an SVG file saved on disk.
				
				Input:
				- data: PlantUML source (string). Should be valid — run `validation` first.
				- path: output file path (string) where the SVG will be written.
				
				Output:
				- isError=false: confirmation message with the path of the saved file.
				- isError=true: error message if rendering or writing fails.
				
				Notes:
				- The SVG file is written to disk at the specified path.
				- Always validate the source before rendering.
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
				List.of("data", "path"),
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

			log.debug("Received render request with path: {}", path);

			if (!(data instanceof String strData) || !(path instanceof String strPath)) {
				return McpSchema.CallToolResult.builder()
						.isError(true)
						.addTextContent("Invalid input data. Expected a string.")
						.build();
			}

			return renderToSvg(strData, strPath);
		};
	}

	private McpSchema.CallToolResult renderToSvg(String data, String path) {
		try {
			String svg = engine.renderSvg(data);
			var outputPath = Path.of(path);
			Files.writeString(outputPath, svg);
			return McpSchema.CallToolResult.builder()
					.addTextContent("SVG saved to " + outputPath.toAbsolutePath())
					.build();
		} catch (Exception e) {
			log.warn("Error during rendering", e);
			return McpSchema.CallToolResult.builder()
					.isError(true)
					.addTextContent("An error occurred during rendering: " + e.getMessage())
					.build();
		}
	}

}

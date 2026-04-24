package plantmcp.mcp;

import io.modelcontextprotocol.json.jackson3.JacksonMcpJsonMapper;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

public final class ServerMcpApp {

	private static final Logger log = LoggerFactory.getLogger(ServerMcpApp.class);

	public static void start() {
		JacksonMcpJsonMapper jsonMapper = new JacksonMcpJsonMapper(new JsonMapper());
		// Stdio Server Transport (Support for SSE also available)
		var transportProvider = new StdioServerTransportProvider(jsonMapper);

		// Sync tool specification
		var syncToolSpecification = List.of(
				//@formatter:off
				new ValidateMcpTool().build(),
				new EncodeMcpTool().build(),
				new DecodeMcpTool().build(),
				new RenderMcpTool().build()
				//@formatter:on
		);

		// Create a server with custom configuration
		McpSyncServer syncServer = McpServer.sync(transportProvider)
				.serverInfo("plantuml-mcp-server", "0.1")
				.instructions(instructions)
				.capabilities(McpSchema.ServerCapabilities.builder().tools(true).logging().build())
				.tools(syncToolSpecification) // Register tools, resources, and prompts
				.build();

		log.info("Started {} (ver. {}) ...", syncServer.getServerInfo().name(), syncServer.getServerInfo().version());
	}

	private static final String instructions = """
			# PlantUML MCP Server — Usage Guide
			
			This server provides tools to create, validate, encode, decode, and render PlantUML diagrams.
			Use these tools whenever the user asks to generate, check, or share any PlantUML diagram.
			
			## CRITICAL RULE — Creating vs Rendering
			
			**Creating / generating a diagram always means producing PlantUML source text ONLY.**
			- DO NOT call `render` unless the user explicitly asks to save or export an SVG file.
			- Default output of any "create", "generate", "make", "show me", or "write" request is the PlantUML source text — nothing more.
			- `render` is an opt-in operation triggered only by explicit user intent (e.g. "save as SVG", "render to disk").
			
			## Available Tools
			
			| Tool       | Purpose                                               |
			|------------|-------------------------------------------------------|
			| `validate` | Validate PlantUML source text or file on disk         |
			| `encode`   | Encode PlantUML source to a shareable URL-safe string |
			| `decode`   | Decode an encoded string back to PlantUML source      |
			| `render`   | Render PlantUML source to an SVG file on disk         |
			
			## When to Use These Tools
			
			- User asks to **create or generate** any diagram → produce PlantUML source text (NO rendering) → call `validate` with `data`
			- User asks to **validate** PlantUML source text → call `validate` with `data`
			- User asks to **validate** a PlantUML file on disk → call `validate` with `path`
			- User wants a **shareable link** or encoded string → call `encode`
			- User provides an **encoded PlantUML string** and wants to view or edit the source → call `decode`
			- User asks to **fix** or **correct** a PlantUML diagram → produce corrected PlantUML source text (NO rendering)
			- User **explicitly** asks to **save or export** a diagram as an SVG image file → call `render`
			
			## Recommended Workflow: Create and Validate
			
			Always follow this loop when generating PlantUML diagrams:
			
			1. **Draft** the PlantUML source (`@startuml ... @enduml`)
			2. **Call `validate`** with `data` set to the draft source
			3. If `isError=true`: read the error list, fix the source, go back to step 2
			4. Repeat until `validate` returns `isError=false` ("Schema is valid")
			5. **Present the validated PlantUML source to the user** — this is the final output
			6. Optionally **call `encode`** only if the user asks for a shareable string or URL
			7. Optionally **call `render`** only if the user explicitly asks to save an SVG file
			
			## Tool Usage Details
			
			### `validate`
			- Input `data`: full PlantUML source, typically starting with `@startuml` and ending with `@enduml`
			- Input `path`: absolute or relative path to a `.puml` file on disk
			- Provide **exactly one** of `data` or `path` — passing both is an error
			- On success: returns `"Schema is valid"` (isError=false)
			- On failure: returns a list of parser error messages (isError=true); use these to fix the source
			- Use this tool proactively — call it on every draft before showing or encoding
			
			### `encode`
			- Input `data`: valid PlantUML source (validate first!)
			- Output: an encoded string (Deflate + custom base64), e.g. `SyfFKj2rKt3CoKnELR1Io4ZDoSa7...`
			- Use this encoded string to build a shareable URL: `https://www.plantuml.com/plantuml/uml/<encoded>`
			- Call this after validation confirms the source is valid
			
			### `decode`
			- Input `data`: a PlantUML encoded string (as returned by `encode`, or found in a PlantUML URL)
			- Output: the original PlantUML source text
			- Use when the user provides a URL like `https://www.plantuml.com/plantuml/uml/SyfFKj...` and wants to view or edit the diagram source
			
			### `render`
			- Input `data`: valid PlantUML source (validate first!)
			- Input `path`: output file path where the SVG will be written (e.g. `diagram.svg`)
			- Output: confirmation message with the absolute path of the saved SVG file
			- The SVG content is NOT returned to the model — it is saved directly to disk
			- **Only call this when the user explicitly requests SVG file output**
			
			## Example Scenarios
			
			**User: "Create a sequence diagram showing login flow"**
			1. Draft PlantUML source for the sequence diagram
			2. Call `validate` with `data` → fix any errors → repeat until valid
			3. Present the final PlantUML source to the user
			→ DO NOT call `render`. Source text is the complete response.
			
			**User: "Create a sequence diagram and save it as login.svg"**
			1. Draft PlantUML source for the sequence diagram
			2. Call `validate` with `data` → fix any errors → repeat until valid
			3. Call `render` with the source and path `login.svg`
			4. Confirm the file has been saved
			
			**User: "Is this PlantUML valid? @startuml ... @enduml"**
			1. Call `validate` with `data` set to the provided source
			2. If valid: confirm to user; if not: report the specific errors
			
			**User: "Validate the file at /home/user/diagram.puml"**
			1. Call `validate` with `path` set to `/home/user/diagram.puml`
			2. If valid: confirm to user; if not: report the specific errors
			
			**User: "What does this encoded diagram look like? SyfFKj2rKt3..."**
			1. Call `decode` with the encoded string
			2. Present the PlantUML source to the user
			
			**User: "Give me a shareable link for this diagram"**
			1. Call `validate_source` first to ensure source is valid
			2. Call `encode` to get the encoded string
			3. Return: `https://www.plantuml.com/plantuml/uml/<encoded>`
			
			**User: "Save this diagram as an SVG file"**
			1. Call `validate_source` first to ensure source is valid
			2. Call `render` with the source and the desired output path
			3. Confirm the file has been saved
			
			## Notes
			
			- Always validate before encoding or rendering — invalid source may produce incorrect output
			- When fixing errors, use the exact error messages returned by `validate` to guide corrections
			- PlantUML diagrams must start with `@startuml` and end with `@enduml`
			- Supported diagram types: sequence, class, activity, state, component, object, ER, Gantt, mindmap, WBS, salt (UI mockups), and more
			""";

}


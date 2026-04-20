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
				new ValidationMcpTool().build(),
				new EncodeMcpTool().build(),
				new DecodeMcpTool().build()
				//@formatter:on
		);

		// Create a server with custom configuration
		McpSyncServer syncServer = McpServer.sync(transportProvider)
				.serverInfo("plantuml-mcp-server", "0.1")
				.instructions(instructions)
				.capabilities(McpSchema.ServerCapabilities.builder().tools(true).logging().build())
				// Register tools, resources, and prompts
				.tools(syncToolSpecification)
				.build();

		log.info("Started {} (ver. {}) ...", syncServer.getServerInfo().name(), syncServer.getServerInfo().version());
	}

	private static final String instructions = """
			# PlantUML MCP Server — Usage Guide

			This server provides tools to create, validate, encode, and decode PlantUML diagrams.
			Use these tools whenever the user asks to generate, check, or share any PlantUML diagram.

			## Available Tools

			| Tool         | Purpose                                              |
			|--------------|------------------------------------------------------|
			| `validation` | Validate PlantUML source and get syntax diagnostics  |
			| `encode`     | Encode PlantUML source to a shareable URL-safe string |
			| `decode`     | Decode an encoded string back to PlantUML source     |

			## When to Use These Tools

			- User asks to **create or generate** any diagram (sequence, class, activity, state, component, ER, Gantt, mindmap, etc.)
			- User asks to **validate** PlantUML source text
			- User wants a **shareable link** or encoded string for a PlantUML diagram
			- User provides an **encoded PlantUML string** and wants to view or edit the source
			- User asks to **fix** or **correct** a PlantUML diagram that has errors

			## Recommended Workflow: Create and Validate

			Always follow this loop when generating PlantUML diagrams:

			1. **Draft** the PlantUML source (`@startuml ... @enduml`)
			2. **Call `validation`** with the draft source
			3. If `isError=true`: read the error list, fix the source, go back to step 2
			4. Repeat until `validation` returns `isError=false` ("Schema is valid")
			5. **Never present invalid PlantUML to the user** — always validate first
			6. Optionally **call `encode`** to produce a shareable string or URL

			## Tool Usage Details

			### `validation`
			- Input `data`: full PlantUML source, typically starting with `@startuml` and ending with `@enduml`
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

			## Example Scenarios

			**User: "Create a sequence diagram showing login flow"**
			1. Draft PlantUML source for the sequence diagram
			2. Call `validation` → fix any errors → repeat until valid
			3. Present the final PlantUML source to the user
			4. Optionally call `encode` and provide the URL

			**User: "Is this PlantUML valid? @startuml ... @enduml"**
			1. Call `validation` with the provided source
			2. If valid: confirm to user; if not: report the specific errors

			**User: "What does this encoded diagram look like? SyfFKj2rKt3..."**
			1. Call `decode` with the encoded string
			2. Present the PlantUML source to the user

			**User: "Give me a shareable link for this diagram"**
			1. Call `validation` first to ensure source is valid
			2. Call `encode` to get the encoded string
			3. Return: `https://www.plantuml.com/plantuml/uml/<encoded>`

			## Notes

			- Always validate before encoding — encoding invalid source may still produce a string, but the diagram will not render correctly
			- When fixing errors, use the exact error messages returned by `validation` to guide corrections
			- PlantUML diagrams must start with `@startuml` and end with `@enduml`
			- Supported diagram types: sequence, class, activity, state, component, object, ER, Gantt, mindmap, WBS, salt (UI mockups), and more
			""";

}

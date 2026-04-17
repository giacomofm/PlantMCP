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
				.capabilities(McpSchema.ServerCapabilities.builder().tools(true).logging().build())
				// Register tools, resources, and prompts
				.tools(syncToolSpecification)
				.build();

		log.info("Started {} (ver. {}) ...", syncServer.getServerInfo().name(), syncServer.getServerInfo().version());
	}

}

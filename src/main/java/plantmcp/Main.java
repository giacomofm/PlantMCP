package plantmcp;

import plantmcp.cli.CliApp;
import plantmcp.mcp.ServerMcpApp;

public final class Main {

	static void main(String[] args) {
		if (args.length > 0) {
			CliApp.run(args);
		}
		ServerMcpApp.start();
	}

}
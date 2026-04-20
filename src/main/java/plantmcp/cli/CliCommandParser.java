package plantmcp.cli;

import plantmcp.cli.CliOperation.CliCommand;
import plantmcp.plant.PlantEngine;

public final class CliCommandParser {

	public static CliOperation parse(String[] args) {
		String allowedCommands = "allowed command are <%s>".formatted(
				String.join("|", CliCommand.stream().map(Enum::name).toList()));

		if (args == null || args.length == 0) {
			throw new IllegalArgumentException("No command provided, " + allowedCommands);
		}

		var cc = CliCommand.stream()
				.filter(c -> c.name().equalsIgnoreCase(args[0]))
				.findFirst()
				.orElseThrow(
						() -> new IllegalArgumentException("Unknown command '" + args[0] + "', " + allowedCommands));

		var engine = new PlantEngine();
		return switch (cc) {
			case validate -> new ValidateCliOperation(engine, args);
			case encode -> new EncodeCliOperation(engine, args);
			case decode -> new DecodeCliOperation(engine, args);
			case render -> new RenderCliOperation(engine, args);
		};
	}

}

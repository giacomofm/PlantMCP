package plantmcp.cli;

import plantmcp.plant.PlantEngine;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

final class ValidateCliOperation implements CliOperation {

	private final Path path;
	private final PlantEngine engine;

	ValidateCliOperation(PlantEngine engine, String[] args) {
		Objects.requireNonNull(engine, "PlantEngine cannot be null");
		if (args.length < 2) {
			throw new IllegalArgumentException("Missing path argument. Usage: validate <path>");
		}
		this.path = Path.of(args[1]);
		this.engine = engine;
	}

	@Override
	public int execute() throws Exception {
		if (!Files.exists(path)) {
			throw new IllegalArgumentException("File not found: " + path);
		}

		String content = Files.readString(path);
		var errors = engine.getErrors(content);
		if (!errors.isEmpty()) {
			errors.forEach(IO::println);
			return CLI_OPS_ERROR;
		}
		IO.println("OK");
		return 0;
	}
}

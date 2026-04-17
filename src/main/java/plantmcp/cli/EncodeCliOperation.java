package plantmcp.cli;

import plantmcp.plant.PlantEngine;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

final class EncodeCliOperation implements CliOperation {

	private final Path path;
	private final PlantEngine engine;

	EncodeCliOperation(PlantEngine engine, String[] args) {
		Objects.requireNonNull(engine, "PlantEngine cannot be null");
		if (args.length < 2) {
			throw new IllegalArgumentException("Missing path argument. Usage: encode <path>");
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
		String encoded = engine.encode(content);
		IO.println(encoded);
		return 0;
	}

}

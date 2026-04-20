package plantmcp.cli;

import plantmcp.plant.PlantEngine;

import java.io.IOException;
import java.util.Objects;

final class DecodeCliOperation implements CliOperation {

	private final String encoded;
	private final PlantEngine engine;

	DecodeCliOperation(PlantEngine engine, String[] args) {
		Objects.requireNonNull(engine, "PlantEngine cannot be null");
		if (args.length < 2) {
			throw new IllegalArgumentException("Missing encoded string argument. Usage: decode <encoded-string>");
		}
		this.encoded = args[1];
		this.engine = engine;
	}

	@Override
	public int execute() throws IOException {
		String decoded = engine.decode(encoded);
		IO.println(decoded);
		return 0;
	}
}

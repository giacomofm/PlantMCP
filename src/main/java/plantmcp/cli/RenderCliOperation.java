package plantmcp.cli;

import plantmcp.plant.PlantEngine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

final class RenderCliOperation implements CliOperation {

	private final Path inputPath;
	private final Path outputPath;
	private final PlantEngine engine;

	RenderCliOperation(PlantEngine engine, String[] args) {
		Objects.requireNonNull(engine, "PlantEngine cannot be null");
		if (args.length < 3) {
			throw new IllegalArgumentException("Missing arguments. Usage: render <input-path> <output-path>");
		}
		this.inputPath = Path.of(args[1]);
		this.outputPath = Path.of(args[2]);
		this.engine = engine;
	}

	@Override
	public int execute() throws IOException {
		if (!Files.exists(inputPath)) {
			throw new IllegalArgumentException("File not found: " + inputPath);
		}

		String source = Files.readString(inputPath);
		String svg = engine.renderSvg(source);
		Files.writeString(outputPath, svg);
		IO.println("SVG saved to " + outputPath.toAbsolutePath());
		return 0;
	}

}

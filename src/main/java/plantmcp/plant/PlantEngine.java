package plantmcp.plant;

import net.sourceforge.plantuml.BlockUml;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.code.NoPlantumlCompressionException;
import net.sourceforge.plantuml.code.TranscoderUtil;
import net.sourceforge.plantuml.error.PSystemError;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlantEngine {

	public List<String> getErrors(String source) {
		Objects.requireNonNull(source, "Source cannot be null");

		var reader = new SourceStringReader(source);
		var blocks = reader.getBlocks();

		if (blocks.isEmpty()) return List.of("No diagram found in source");

		var errors = new ArrayList<String>();
		for (BlockUml block : blocks) {
			if (block.getDiagram() instanceof PSystemError error) {
				error.getErrorsUml().forEach(e -> errors.add(e.getError()));
			}
		}

		return errors;
	}

	public String encode(String source) {
		Objects.requireNonNull(source, "Source cannot be null");
		try {
			return TranscoderUtil.getDefaultTranscoder().encode(source);
		} catch (IOException e) {
			throw new RuntimeException("Failed to encode the provided source", e);
		}
	}

	public String decode(String encoded) {
		Objects.requireNonNull(encoded, "Encoded string cannot be null");
		try {
			return TranscoderUtil.getDefaultTranscoder().decode(encoded);
		} catch (NoPlantumlCompressionException e) {
			throw new RuntimeException("Failed to decode the provided string", e);
		}
	}

	public String renderSvg(String source) {
		Objects.requireNonNull(source, "Source cannot be null");
		try (var baos = new ByteArrayOutputStream()) {
			var reader = new SourceStringReader(source);
			reader.outputImage(baos, new FileFormatOption(FileFormat.SVG));
			return baos.toString(StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new RuntimeException("Failed to render SVG from the provided source", e);
		}
	}

}

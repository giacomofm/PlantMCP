package plantmcp.cli;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;

public sealed interface CliOperation permits ValidateCliOperation, EncodeCliOperation, DecodeCliOperation, RenderCliOperation {

	int CLI_OPS_ERROR = 3;

	int execute() throws IOException;

	enum CliCommand {
		validate, encode, decode, render;

		static Stream<CliCommand> stream() {
			return Arrays.stream(values());
		}
	}

}

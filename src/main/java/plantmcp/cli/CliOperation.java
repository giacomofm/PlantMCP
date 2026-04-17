package plantmcp.cli;

import java.util.Arrays;
import java.util.stream.Stream;

public sealed interface CliOperation permits ValidateCliOperation, EncodeCliOperation, DecodeCliOperation {

	int CLI_OPS_ERROR = 3;

	int execute() throws Exception;

	enum CliCommand {
		validate, encode, decode;

		static Stream<CliCommand> stream() {
			return Arrays.stream(values());
		}
	}

}

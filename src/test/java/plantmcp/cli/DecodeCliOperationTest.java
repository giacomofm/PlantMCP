package plantmcp.cli;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static plantmcp.TestUtils.VALID_DIAGRAM_ENCODED;

class DecodeCliOperationTest {

	@Test
	void decodeCommand_withArg_returnsDecodeOperation() {
		var operation = CliCommandParser.parse(new String[] { "decode", VALID_DIAGRAM_ENCODED });
		assertInstanceOf(DecodeCliOperation.class, operation);
	}

	@Test
	void decodeCommand_missingArg_throwsIllegalArgumentException() {
		var ex = assertThrows(IllegalArgumentException.class, () -> CliCommandParser.parse(new String[] { "decode" }));
		assertTrue(ex.getMessage().contains("Missing encoded string"));
	}

	@Test
	void execute_validEncoded_returnsZero() throws Exception {
		var op = CliCommandParser.parse(new String[] { "decode", VALID_DIAGRAM_ENCODED });
		assertEquals(0, op.execute());
	}

}

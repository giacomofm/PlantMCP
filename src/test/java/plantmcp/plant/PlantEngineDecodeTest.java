package plantmcp.plant;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static plantmcp.TestUtils.VALID_DIAGRAM;
import static plantmcp.TestUtils.VALID_DIAGRAM_ENCODED;

public class PlantEngineDecodeTest extends PlantEngineTest {

	@Test
	void decode_validEncoded_returnsOriginalSource() {
		String decoded = engine.decode(VALID_DIAGRAM_ENCODED);
		assertEquals(VALID_DIAGRAM, decoded);
	}

	@Test
	void decode_nullInput_throwsNPE() {
		//noinspection DataFlowIssue
		assertThrows(NullPointerException.class, () -> engine.decode(null));
	}

	@Test
	void decode_emptyInput_throwsException() {
		assertThrows(RuntimeException.class, () -> engine.decode(""));
	}

}

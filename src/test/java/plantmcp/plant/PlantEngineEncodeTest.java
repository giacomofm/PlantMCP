package plantmcp.plant;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static plantmcp.TestUtils.VALID_DIAGRAM;
import static plantmcp.TestUtils.VALID_DIAGRAM_ENCODED;

public class PlantEngineEncodeTest extends PlantEngineTest {

	@Test
	void encode_validDiagram_returnsNonEmptyString() {
		String encoded = engine.encode(VALID_DIAGRAM);
		assertNotNull(encoded);
		assertFalse(encoded.isEmpty(), "Encoded string should not be empty");
		assertEquals(VALID_DIAGRAM_ENCODED, encoded, "Encoded string does not match expected value");
	}

	@Test
	void encode_nullSource_throwsNPE() {
		//noinspection DataFlowIssue
		assertThrows(NullPointerException.class, () -> engine.encode(null));
	}

	@Test
	void encode_emptySource_returnsEmptyString() {
		String encoded = engine.encode("");
		assertNotNull(encoded);
		assertTrue(encoded.isEmpty());
	}

}

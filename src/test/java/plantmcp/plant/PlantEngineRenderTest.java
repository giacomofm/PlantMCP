package plantmcp.plant;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static plantmcp.TestUtils.VALID_DIAGRAM;
import static plantmcp.TestUtils.VALID_DIAGRAM_ENCODED;

public class PlantEngineRenderTest extends PlantEngineTest {

	@Test
	void renderSvg_validDiagram_returnsSvgString() {
		String svg = engine.renderSvg(VALID_DIAGRAM);
		assertNotNull(svg);
		assertTrue(svg.contains("<svg"), "Output should contain SVG markup");
		assertTrue(svg.contains(VALID_DIAGRAM_ENCODED), "SVG output should contain encoded diagram data");
	}

	@Test
	void renderSvg_nullSource_throwsNPE() {
		//noinspection DataFlowIssue
		assertThrows(NullPointerException.class, () -> engine.renderSvg(null));
	}

	@Test
	void renderSvg_emptySource_returnsSvgString() {
		String svg = engine.renderSvg("");
		assertNotNull(svg);
		assertFalse(svg.isBlank(), "SVG output should not be blank even for empty source");
		assertTrue(svg.contains("<svg"), "Output should contain SVG markup even for empty source");
	}

}

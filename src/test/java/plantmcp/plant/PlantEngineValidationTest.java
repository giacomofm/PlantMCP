package plantmcp.plant;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static plantmcp.TestUtils.VALID_DIAGRAM;

class PlantEngineValidationTest extends PlantEngineTest {

	@Test
	void validDiagram_hasNoErrors() {
		var errors = engine.getErrors(VALID_DIAGRAM);
		assertTrue(errors.isEmpty(), "Expected no errors, but got: " + errors);
	}

	@Test
	void noDiagram_hasError() {
		var errors = engine.getErrors("");
		assertFalse(errors.isEmpty(), "Expected errors, but got none");
		assertTrue(errors.getFirst().contains("No diagram found in source"),
				"Expected no diagram error, but got: " + errors.getFirst());

		//noinspection DataFlowIssue
		assertThrows(NullPointerException.class, () -> engine.getErrors(null),
				"Expected NullPointerException for null source");
	}

	@Test
	void invalidDiagram_hasError() {
		String source = """
				@startuml
				Bob - > Alice : hello
				@enduml
				""";
		var errors = engine.getErrors(source);
		assertFalse(errors.isEmpty(), "Expected errors, but got none");
		assertTrue(errors.getFirst().contains("Syntax Error"), "Expected syntax error, but got: " + errors.getFirst());
	}

}

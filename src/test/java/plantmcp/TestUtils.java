package plantmcp;

public interface TestUtils {

	String VALID_DIAGRAM = """
			@startuml
			Alice -> Bob: Authentication Request
			Bob --> Alice: Authentication Response
			@enduml""";
	String VALID_DIAGRAM_ENCODED = "Syp9J4vLqBLJSCfFib9mB2t9ICqhoKnEBCdCprC8IYqiJIqkuGBAAUW2rJY256DHLLoGdrUS2W00";

}

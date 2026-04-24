package plantmcp.mcp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EnvironmentUtilsTest {

	// --- resolvePath outside a container ---

	@Test
	void outsideDocker_simpleFilename_unchanged() {
		System.clearProperty(EnvironmentUtils.ENV_VAR);
		assertEquals("diagram.svg", EnvironmentUtils.resolvePath("diagram.svg"));
	}

	@Test
	void outsideDocker_absolutePath_unchanged() {
		System.clearProperty(EnvironmentUtils.ENV_VAR);
		assertEquals("/home/user/diagrams/diagram.svg",
				EnvironmentUtils.resolvePath("/home/user/diagrams/diagram.svg"));
	}

	@Test
	void outsideDocker_relativePath_unchanged() {
		System.clearProperty(EnvironmentUtils.ENV_VAR);
		assertEquals("output/diagram.svg", EnvironmentUtils.resolvePath("output/diagram.svg"));
	}

	// --- resolvePath inside a container ---

	@Test
	void insideDocker_simpleFilename_prependsDataDir() {
		System.setProperty(EnvironmentUtils.ENV_VAR, "true");
		assertEquals("/data/diagram.svg", EnvironmentUtils.resolvePath("diagram.svg"));
	}

	@Test
	void insideDocker_absolutePath_stripsDirectoryAndPrependsDataDir() {
		System.setProperty(EnvironmentUtils.ENV_VAR, "true");
		assertEquals("/data/diagram.svg", EnvironmentUtils.resolvePath("/home/user/diagrams/diagram.svg"));
	}

	@Test
	void insideDocker_relativePath_stripsDirectoryAndPrependsDataDir() {
		System.setProperty(EnvironmentUtils.ENV_VAR, "true");
		assertEquals("/data/diagram.svg", EnvironmentUtils.resolvePath("output/diagram.svg"));
	}

	@Test
	void insideDocker_pumlInputFile_stripsDirectoryAndPrependsDataDir() {
		System.setProperty(EnvironmentUtils.ENV_VAR, "true");
		assertEquals("/data/my-diagram.puml", EnvironmentUtils.resolvePath("/workspace/src/my-diagram.puml"));
	}
}

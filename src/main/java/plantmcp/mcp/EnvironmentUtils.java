package plantmcp.mcp;

import java.nio.file.Path;

/**
 * Detects whether the server is running inside a Docker container and resolves file
 * paths accordingly.
 *
 * <p>When the JVM system property {@value #ENV_VAR} is set (regardless of its value),
 * the server is considered to be running in a container. In that mode:
 * <ul>
 *   <li>All file I/O is rooted at {@value #DATA_DIR}.</li>
 *   <li>Any path provided by the caller is stripped to its filename component; the
 *       directory portion is discarded and replaced with {@value #DATA_DIR}.</li>
 * </ul>
 *
 * <p>The property is set automatically via the Dockerfile ENTRYPOINT
 * ({@code -DPLANTMCP_DOCKER=true}). Mount your host directory to {@value #DATA_DIR}
 * when starting the container so that input/output files are exchanged through that volume:
 * <pre>
 *   docker run --rm -v /your/host/dir:/data plantmcp
 * </pre>
 */
final class EnvironmentUtils {

	/** Directory used for all file I/O when running inside a container. */
	static final String DATA_DIR = "/data";

	/** JVM system property whose presence signals a containerised deployment. */
	static final String ENV_VAR = "PLANTMCP_DOCKER";

	private EnvironmentUtils() {}

	/**
	 * Resolves a caller-supplied file path.
	 *
	 * <ul>
	 *   <li>Outside a container: returns {@code path} unchanged.</li>
	 *   <li>Inside a container: extracts the filename from {@code path} and returns {@value #DATA_DIR}/{filename}, ignoring any directory components supplied by the caller.</li>
	 * </ul>
	 */
	static String resolvePath(String path) {
		if (System.getProperty(ENV_VAR) == null) {
			return path;
		}
		String filename = Path.of(path).getFileName().toString();
		return DATA_DIR + "/" + filename;
	}
}

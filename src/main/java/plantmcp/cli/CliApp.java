package plantmcp.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CliApp {

	private static final Logger log = LoggerFactory.getLogger(CliApp.class);

	public static final int INTERNAL_ERROR = 1;
	public static final int USER_ERROR = 2;

	public static void run(String[] args) {
		try {
			var operation = CliCommandParser.parse(args);
			System.exit(operation.execute());
		} catch (IllegalArgumentException e) {
			log.error("User error: {}", e.getMessage());
			System.exit(USER_ERROR);
		} catch (Exception e) {
			log.error("Internal error: {}", e.getMessage(), e);
			System.exit(INTERNAL_ERROR);
		}
	}

}

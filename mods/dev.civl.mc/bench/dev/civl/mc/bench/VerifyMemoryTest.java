package dev.civl.mc.bench;

import dev.civl.abc.front.IF.ParseException;
import dev.civl.abc.front.IF.PreprocessorException;
import dev.civl.abc.token.IF.SyntaxException;
import dev.civl.mc.run.IF.UserInterface;

/**
 * java -classpath ${WORKING_DIR}/civl.jar:${WORKING_DIR}/bin
 * dev.civl.mc.bench.VerifyMemoryTest $arg0 $arg1
 * 
 * where $arg0 is the full path to the test file and $arg1 is the number of
 * iteration, ${WORKING_DIR} is your CIVL directory.
 * 
 * @author zmanchun
 *
 */
public class VerifyMemoryTest {
	private static UserInterface ui = new UserInterface();

	public static void main(String[] args) throws SyntaxException, PreprocessorException, ParseException {
		String testFile = args[0];
		int n = Integer.parseInt(args[1]);

		for (int i = 0; i < n; i++) {
			System.out.println("i is " + i);
			ui.run("verify", testFile);
		}
	}
}

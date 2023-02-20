package dev.civl.abc.preproc;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.PrintStream;

import org.antlr.runtime.CharStream;
import org.junit.Test;

import dev.civl.abc.front.c.preproc.CommonCharacterStream;
import dev.civl.abc.front.c.preproc.FilteredStream;

/**
 * Tests the removal of backslash-newline sequences from character streams using
 * the two classes {@link CommonCharacterStream} and {@link FilteredStream}.
 * 
 * @author siegel
 *
 */
public class FilteredCharStreamTest {

	private static boolean debug = false;

	private static PrintStream out = System.out;

	private String filter(CharStream output) throws IOException {
		String result = "";
		int index0 = 0;

		while (index0 == output.index()) {
			int number = output.LA(1);
			char c = (char) number;

			if (number == CharStream.EOF)
				break;
			result += c;
			output.consume();
			index0++;
		}
		if (debug) {
			out.print("Output--->" + result + "<--");
			out.println(" (size=" + output.size() + ")");
			out.println();
		}
		return result;
	}

	String streamFilter(String original, int chunkSize) throws IOException {
		CharStream stringStream = new CommonCharacterStream("test", original,
				chunkSize);
		CharStream filteredStream = new FilteredStream(stringStream);

		return filter(filteredStream);
	}

	private void test(String expected, String original) throws IOException {
		if (debug) {
			out.print("Input---->" + original + "<--");
			out.println(" (size=" + original.length() + ")");
			out.println();
		}
		if (!expected.endsWith("\n"))
			expected = expected + "\n";
		// why? because the filtered stream adds \n at end if not
		// already there.
		// assertEquals(expected, fileFilter(original));
		assertEquals(expected, streamFilter(original, 1));
		assertEquals(expected, streamFilter(original, 2));
		assertEquals(expected, streamFilter(original, 8));
	}

	@Test
	public void testNone() throws IOException {
		test("abc", "abc");
	}

	@Test
	public void testMiddle() throws IOException {
		test("ab", "a\\\nb");
	}

	@Test
	public void testBegin() throws IOException {
		test("ab", "\\\nab");
	}

	@Test
	public void testEnd() throws IOException {
		test("ab", "ab\\\n");
	}

	@Test
	public void testEmpty() throws IOException {
		test("", "\\\n");
	}

	@Test
	public void testDouble() throws IOException {
		test("ab", "a\\\n\\\nb");
	}

	@Test
	public void testBackslashOnly() throws IOException {
		test("a\\b", "a\\b");
	}

	@Test
	public void testNewlineOnly() throws IOException {
		test("a\nb", "a\nb");
	}

	@Test
	public void testDoubleBegin() throws IOException {
		test("x", "\\\n\\\nx");
	}
}

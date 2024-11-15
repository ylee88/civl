package dev.civl.abc.token.IF;

import java.util.ArrayList;

/**
 * Methods for printing source information in various related tools.
 */
public class SourceFormatter {

	/**
	 * Plain text output, or we can use special ASCI sequences for color and
	 * bold? The special sequences only work in certain terminals (including
	 * most Unix-like terminals). They don't work in Eclipse debugger. This
	 * could be made a command line option.
	 */
	public final static boolean PLAIN = true;

	/**
	 * After quoted source exceeds this number of characters, the quote will be
	 * elided using an ellipsis ("...")
	 */
	public final static int MAX_SOURCE_CHARS = 80;

	public final static String ANSI_RESET = "\u001B[0m";
	public final static String ANSI_BOLD = "\u001B[1m";
	public final static String ANSI_BLUE = "\u001B[34m";
	public final static String ANSI_RED = "\u001B[31m";

	/**
	 * Locators are strings of the form "filname:nnn:mmm-mmm" which indicate a
	 * location in a source file. These two constants are inserted before and
	 * after a locator to highlight it properly.
	 */
	private final static String OPEN_LOCATOR = PLAIN ? "" : ANSI_BOLD;
	private final static String CLOSE_LOCATOR = PLAIN ? "" : ANSI_RESET;

	/**
	 * These two constants are placed before/after source code excerpts to
	 * highlight the code properly.
	 */
	private final static String OPEN_SOURCE = PLAIN ? "\"" : ANSI_BLUE;
	private final static String CLOSE_SOURCE = PLAIN ? "\"" : ANSI_RESET;

	/**
	 * These are for the color of error messages.
	 */
	public final static String OPEN_ERR = PLAIN ? "" : ANSI_RED + ANSI_BOLD;
	public final static String CLOSE_ERR = PLAIN ? "" : ANSI_RESET;

	/**
	 * List of source segments. Each segment is formed my joining adjacent
	 * tokens with a similar formation history. A null entry in this list
	 * indicates an elision which will be represented by an ellipsis "..."
	 */
	ArrayList<SourceSegment> segments = new ArrayList<>();

	private static String escape(String txt) {
		txt = txt.replaceAll("\n", "\\\\n");
		txt = txt.replaceAll("\r", "\\\\r");
		txt = txt.replaceAll("\t", "\\\\t");
		return txt;
	}

	private static String clearWhiteSpace(String txt) {
		txt = txt.replaceAll("\n", " ");
		txt = txt.replaceAll("\r", "");
		txt = txt.replaceAll("\t", " ");
		txt = txt.strip();
		return txt;
	}

	public static String quoteSource(String text, boolean clear) {
		if (clear)
			return OPEN_SOURCE + clearWhiteSpace(text) + CLOSE_SOURCE;
		else
			return OPEN_SOURCE + escape(text) + CLOSE_SOURCE;
	}

	public static void addLocator(StringBuffer buf, String filename, int lineno,
			int startCol, int stopCol) {
		buf.append(OPEN_LOCATOR);
		buf.append(filename);
		buf.append(":" + lineno + ":" + startCol);
		if (stopCol != startCol)
			buf.append("-" + stopCol);
		buf.append(CLOSE_LOCATOR);
	}

	public static void addLocator(StringBuffer buf, String filename, int lineno,
			int startCol) {
		buf.append(OPEN_LOCATOR);
		buf.append(filename);
		buf.append(":" + lineno + ":" + startCol);
		buf.append(CLOSE_LOCATOR);
	}

	public static void addLocator(StringBuffer buf, String filename,
			int lineno) {
		buf.append(OPEN_LOCATOR);
		buf.append(filename);
		buf.append(":" + lineno);
		buf.append(CLOSE_LOCATOR);
	}

	public static String locator(String filename, int lineno, int startCol,
			int stopCol) {
		StringBuffer buf = new StringBuffer();
		addLocator(buf, filename, lineno, startCol, stopCol);
		return buf.toString();
	}

	public static String locator(String filename, int lineno, int startCol) {
		StringBuffer buf = new StringBuffer();
		addLocator(buf, filename, lineno, startCol);
		return buf.toString();
	}

	public static String locator(String filename, int lineno) {
		StringBuffer buf = new StringBuffer();
		addLocator(buf, filename, lineno);
		return buf.toString();
	}

	/**
	 * Put string in the right color/format for an error message.
	 * 
	 * @param str
	 *                a non-null String
	 * @return a new String with same content as original except for
	 *         color/formatting
	 */
	public static String errorify(String str) {
		return OPEN_ERR + str + CLOSE_ERR;
	}

	/**
	 * Length of string excluding hidden characters.
	 * 
	 * @param str
	 *                a non-null String
	 * @return apparent length
	 */
	private static int apparentLength(String str) {
		int n = str.length();
		int result = 0;
		for (int i = 0; i < n; i++) {
			char c = str.charAt(i);
			if (c == '\u001B') {
				i += 3;
				continue;
			}
			result++;
		}
		return result;
	}

	public SourceFormatter(CivlcToken first, CivlcToken last) {
		int firstIndex = first.getIndex();
		int lastIndex = last.getIndex();
		if (firstIndex < 0)
			return;
		if (lastIndex < 0)
			return;
		if (firstIndex > lastIndex)
			return;
		CivlcToken token = first;
		SourceSegment seg = null;
		int mass = 0;
		for (int i = firstIndex; i < lastIndex; i++) {
			if (token == null) { // stream ended early
				segments.add(null); // eliding i .. lastIndex-1
				seg = null;
				break;
			}
			if (seg == null || !seg.add(token)) {
				if (mass >= MAX_SOURCE_CHARS) {
					segments.add(null);
					seg = null;
					break;
				}
				seg = new SourceSegment();
				seg.add(token);
				segments.add(seg);
			}
			mass += token.getText().length();
			token = token.getNext();
		}
		if (seg == null || !seg.add(last)) {
			seg = new SourceSegment();
			seg.add(last);
			segments.add(seg);
		}
	}

	public void addContent(StringBuffer buf) {
		buf.append(OPEN_SOURCE);
		boolean first = true;
		for (SourceSegment seg : segments) {
			if (seg == null) {
				if (first)
					first = false;
				else
					buf.append(" ");
				buf.append("...");
				continue;
			}
			if (seg.theText.isBlank())
				continue;
			if (first)
				first = false;
			else
				buf.append(" ");
			buf.append(clearWhiteSpace(seg.theText));
		}
		buf.append(CLOSE_SOURCE);
	}

	public String getContent() {
		StringBuffer buf = new StringBuffer();
		addContent(buf);
		return buf.toString();
	}

	public String getLocator(boolean abbrev) {
		if (segments.isEmpty())
			return "";
		SourceSegment seg0 = segments.get(0);
		String filename = abbrev
				? seg0.theFile.getIndexName()
				: seg0.theFilename;
		return SourceFormatter.locator(filename, seg0.theLineno,
				seg0.theStartCol);
	}

	public void getDetailedReportOld(StringBuffer buf) {
		if (segments.isEmpty())
			return;
		addContent(buf);
		buf.append("\n");
		for (SourceSegment seg : segments) {
			if (seg == null) {
				buf.append(".\n.\n.\n");
			} else {
				String text = seg.theText;
				text = text.stripTrailing();
				buf.append(quoteSource(text, false));
				buf.append("\n  from ");
				addLocator(buf, seg.theFilename, seg.theLineno, seg.theStartCol,
						seg.theStopCol);
				if (seg.theFormation != null)
					buf.append(seg.theFormation.suffix());
				buf.append("\n");
			}
		}
	}

	public void getDetailedReport(StringBuffer buf) {
		if (segments.isEmpty())
			return;
		addContent(buf);
		buf.append("\n");
		// pass 1: get maximum column width of locators...
		int nseg = segments.size();
		int[] widths = new int[nseg];
		int locWidth = 0;
		for (int i = 0; i < nseg; i++) {
			SourceSegment seg = segments.get(i);
			if (seg != null) {
				String locator = locator(seg.theFilename, seg.theLineno,
						seg.theStartCol, seg.theStopCol);
				int width = apparentLength(locator);
				if (width > locWidth)
					locWidth = width;
				widths[i] = width;
			}
		}
		// pass 2: print...
		for (int i = 0; i < nseg; i++) {
			SourceSegment seg = segments.get(i);
			if (seg == null) {
				for (int j = 0; j < 3; j++)
					buf.append("     .\n");
			} else {
				addLocator(buf, seg.theFilename, seg.theLineno, seg.theStartCol,
						seg.theStopCol);
				for (int j = widths[i]; j < locWidth; j++)
					buf.append(" ");
				buf.append(" | ");
				String text = seg.theText;
				text = text.stripTrailing();
				buf.append(quoteSource(text, false));
				buf.append("\n");
				if (seg.theFormation != null) {
					String suffix = seg.theFormation.suffix();
					if (!suffix.isBlank()) {
						buf.append(" ");
						buf.append(suffix);
						buf.append("\n");
					}
				}
			}
		}

	}

	/**
	 * Produces a short locator string based on first token, followed by the
	 * complete text of the segments, all on one line.
	 * 
	 * @return single-line string representation of source
	 */
	public String getShortString(boolean abbrev) {
		if (segments.isEmpty())
			return "";
		SourceSegment seg0 = segments.get(0);
		StringBuffer buf = new StringBuffer();
		String filename = abbrev
				? seg0.theFile.getIndexName()
				: seg0.theFilename;
		SourceFormatter.addLocator(buf, filename, seg0.theLineno,
				seg0.theStartCol);
		buf.append(" ");
		addContent(buf);
		return buf.toString();
	}
}

/**
 * A segment is an accumulation of consecutive tokens that share the same
 * formation and come from one common line of a file. Their data can be merged
 * together to give a more concise output.
 */
class SourceSegment {
	boolean initialized = false;
	Formation theFormation = null;
	SourceFile theFile = null;
	String theFilename = null;
	String theText = "";
	int theLineno = -1;
	int theStartCol = -1;
	int theStopCol = -1;

	/**
	 * Add the given token's data to the current segment if that data is
	 * compatible with the segment. If it is compatible, the add succeeds and
	 * this method returns true, otherwise this method returns false without
	 * changing the segment.
	 * 
	 * @param token
	 *                  token to try to add to this segment
	 * @return true if add succeeds, else false
	 */
	boolean add(CivlcToken token) {
		Formation formation = token.getFormation();
		int lineno = token.getLine();
		SourceFile file = token.getSourceFile();
		int startCol = token.getCharPositionInLine();
		String text = token.getText();
		int stopCol = startCol + text.length() - 1;

		if (!initialized) {
			theFormation = formation;
			theFile = file;
			theFilename = file.getName();
			theLineno = lineno;
			theStartCol = startCol;
			theText = text;
			theStopCol = stopCol;
			initialized = true;
			return true;
		}
		if (!(formation == null
				? theFormation == null
				: formation.equals(theFormation)))
			return false;
		if (lineno != theLineno)
			return false;
		if (!(file == null ? theFile == null : file.equals(theFile)))
			return false;
		if (startCol < theStartCol)
			theStartCol = startCol;
		if (stopCol > theStopCol)
			theStopCol = stopCol;
		theText += text;
		return true;
	}
}

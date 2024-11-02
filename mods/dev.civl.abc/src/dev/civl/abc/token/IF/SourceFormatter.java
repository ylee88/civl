package dev.civl.abc.token.IF;

import java.util.ArrayList;

/**
 * Methods for printing source information in various related tools.
 */
public class SourceFormatter {

	/**
	 * Plain text output, or we can use special ASCI sequences for color and
	 * bold? The special sequences only work in certain terminals (including
	 * most Unix-like terminals). They don't work in Eclipse debugger.
	 */
	public final static boolean PLAIN = false;

	/**
	 * After quoted source exceeds this number of characters, the quote will be
	 * elided using an ellipsis ("...")
	 */
	public final static int MAX_SOURCE_CHARS = 80;

	public final static String ANSI_RESET = "\u001B[0m";
	public final static String ANSI_BOLD = "\u001B[1m";
	public final static String ANSI_BLUE = "\u001B[34m";

	private final static String OPEN_FILENAME = PLAIN ? "" : ANSI_BOLD;
	private final static String CLOSE_FILENAME = PLAIN ? "" : ANSI_RESET;
	private final static String OPEN_SOURCE = PLAIN ? "\"" : ANSI_BLUE;
	private final static String CLOSE_SOURCE = PLAIN ? "\"" : ANSI_RESET;

	/**
	 * List of source segments. Each segment is formed my joining adjacent
	 * tokens with a similar formation history. A null entry in this list
	 * indicates an elision which will be represented by an ellipsis "..."
	 */
	ArrayList<SourceSegment> segments = new ArrayList<>();

	private static String escape(String text) {
		String txt = text.replaceAll("\n", "\\\\n");
		txt = txt.replaceAll("\r", "\\\\r");
		txt = txt.replaceAll("\t", "\\\\t");
		return txt;
	}

	public static String quoteSource(String text) {
		return OPEN_SOURCE + escape(text) + CLOSE_SOURCE;
	}

	public static void addLocator(StringBuffer buf, String filename, int lineno,
			int startCol, int stopCol) {
		buf.append(OPEN_FILENAME);
		buf.append(filename);
		buf.append(":" + lineno + ":" + startCol);
		if (stopCol != startCol)
			buf.append("-" + stopCol);
		buf.append(CLOSE_FILENAME);
	}

	public static void addLocator(StringBuffer buf, String filename, int lineno,
			int startCol) {
		buf.append(OPEN_FILENAME);
		buf.append(filename);
		buf.append(":" + lineno + ":" + startCol);
		buf.append(CLOSE_FILENAME);
	}

	public static void addLocator(StringBuffer buf, String filename,
			int lineno) {
		buf.append(OPEN_FILENAME);
		buf.append(filename);
		buf.append(":" + lineno);
		buf.append(CLOSE_FILENAME);
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
		for (int i = firstIndex; i <= lastIndex; i++) {
			if (token == null || (i < lastIndex && mass >= MAX_SOURCE_CHARS)) {
				segments.add(null); // eliding i .. lastIndex-1
				if (last != null) {
					seg = new SourceSegment();
					seg.add(last);
					segments.add(seg);
				}
				break;
			}
			if (seg == null || !seg.add(token)) {
				seg = new SourceSegment();
				seg.add(token);
				segments.add(seg);
			}
			mass += token.getText().length();
			token = token.getNext();
		}
	}

	public void addContent(StringBuffer buf) {
		buf.append(OPEN_SOURCE);
		for (SourceSegment seg : segments) {
			if (seg == null)
				buf.append(" ... ");
			else
				buf.append(escape(seg.theText));
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

	public void getDetailedReport(StringBuffer buf) {
		if (segments.isEmpty())
			return;
		addContent(buf);
		buf.append("\n");
		for (SourceSegment seg : segments) {
			if (seg == null) {
				buf.append("\n...\n");
			} else {
				buf.append(quoteSource(seg.theText));
				buf.append("\n  from ");
				addLocator(buf, seg.theFilename, seg.theLineno, seg.theStartCol,
						seg.theStopCol);
				if (seg.theFormation != null)
					buf.append(seg.theFormation.suffix());
				buf.append("\n");
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

package dev.civl.mc.model.common;

import java.io.PrintStream;

import dev.civl.abc.token.IF.CivlcToken;
import dev.civl.abc.token.IF.Formation;
import dev.civl.abc.token.IF.Source;
import dev.civl.abc.token.IF.SourceFile;
import dev.civl.mc.model.IF.CIVLSource;

/**
 * Implementation of CIVLSource formed by wrapping an ABC Source object.
 * 
 * @author siegel
 * 
 */
public class ABC_CIVLSource implements CIVLSource {

	/* ************************** Instance Fields ************************** */

	private Source abcSource;

	/* **************************** Constructors *************************** */

	public ABC_CIVLSource(Source abcSource) {
		this.abcSource = abcSource;
	}

	/* *********************** Methods from CIVLSource ********************* */

	@Override
	public String getLocation() {
		return abcSource.getLocation(false);
	}

	@Override
	public String getSummary(boolean isException) {
		return abcSource.getSummary(false, isException);
	}

	@Override
	public String getContent() {
		return abcSource.getContent(false);
	}

	@Override
	public void print(PrintStream out) {
		abcSource.print(out);
	}

	/* ************************* Methods from Object *********************** */

	public String toString() {
		return abcSource.toString();
	}

	/* *************************** Public Methods ************************** */

	public Source getABCSource() {
		return abcSource;
	}

	@Override
	public boolean isSystemSource() {
		return false;
	}

	@Override
	public String getFileName() {
		if (abcSource != null) {
			CivlcToken t = abcSource.getFirstToken();

			if (t != null) {
				Formation f = t.getFormation();

				if (f != null) {
					SourceFile sf = f.getLastFile();

					if (sf != null)
						return sf.getName();
				}
			}
		}
		return "";
	}

	public String getAbsoluteFilePath() {
		return abcSource.getFirstToken().getSourceFile().getFile()
				.getAbsolutePath();
	}

}

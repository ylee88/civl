package dev.civl.abc.token.common;

import dev.civl.abc.token.IF.CivlcToken;
import dev.civl.abc.token.IF.Inclusion;
import dev.civl.abc.token.IF.SourceFile;
import dev.civl.abc.token.IF.SourceFormatter;

public class CommonInclusion implements Inclusion {

	/**
	 * The file included. Always non-null.
	 */
	private SourceFile file;

	/**
	 * The token containing the file name in the include directive that named
	 * the file. Will be null for the original file (which wasn't included from
	 * anything).
	 */
	private CivlcToken includeToken;

	public CommonInclusion(SourceFile file) {
		assert file != null;
		this.file = file;
		this.includeToken = null;
	}

	public CommonInclusion(SourceFile file, CivlcToken includeToken) {
		assert file != null;
		this.file = file;
		this.includeToken = includeToken;
	}

	@Override
	public String suffix() {
		if (includeToken != null)
			return " included from " + SourceFormatter.locator(
					includeToken.getSourceFile().getName(),
					includeToken.getLine());
		else
			return "";
	}

	@Override
	public SourceFile getLastFile() {
		return file;
	}

	@Override
	public SourceFile getFile() {
		return file;
	}

	@Override
	public CivlcToken getIncludeToken() {
		return includeToken;
	}

	@Override
	public String toString() {
		return "Inclusion[" + file + ", " + includeToken + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof CommonInclusion))
			return false;
		CommonInclusion that = (CommonInclusion) obj;
		if (!file.equals(that.file))
			return false;
		if (includeToken == null) {
			if (that.includeToken != null)
				return false;
		} else if (!includeToken.equals(that.includeToken)) {
			return false;
		}
		return true;
	}
}

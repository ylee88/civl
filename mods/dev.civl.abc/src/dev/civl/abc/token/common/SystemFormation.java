package dev.civl.abc.token.common;

import java.io.File;

import dev.civl.abc.token.IF.Formation;
import dev.civl.abc.token.IF.SourceFile;

/**
 * This formation is used to represent the formation of a token through some
 * "system" means, instead of token derived from a source file.
 * 
 * @author siegel
 */
public class SystemFormation implements Formation {

	private String identifier;

	private SourceFile file;

	public SystemFormation(String identifier, int index) {
		this.identifier = identifier;
		this.file = new SourceFile(new File(identifier), index);
	}

	@Override
	public String suffix() {
		return identifier;
	}

	@Override
	public SourceFile getLastFile() {
		return file;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof SystemFormation))
			return false;
		SystemFormation that = (SystemFormation) obj;
		if (identifier == null) {
			if (that.identifier != null)
				return false;
		} else {
			if (!identifier.equals(that.identifier))
				return false;
		}
		if (file == null) {
			if (that.file != null)
				return false;
		} else {
			if (!file.equals(that.file))
				return false;
		}
		return true;
	}

}

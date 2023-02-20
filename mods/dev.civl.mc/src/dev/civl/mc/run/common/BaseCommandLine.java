package dev.civl.mc.run.common;

import java.io.Serializable;

import dev.civl.mc.run.IF.CommandLine;
import dev.civl.gmc.GMCConfiguration;

public abstract class BaseCommandLine implements CommandLine, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5277715072720777165L;
	protected String commandString;
	protected CommandLineKind commandlineKind;
	protected GMCConfiguration gmcConfig;

	@Override
	public GMCConfiguration gmcConfig() {
		return this.gmcConfig;
	}

	@Override
	public void setGMCConfig(GMCConfiguration config) {
		this.gmcConfig = config;
	}

	@Override
	public void setCommandString(String string) {
		this.commandString = string;
	}

	@Override
	public String getCommandString() {
		return this.commandString;
	}

}

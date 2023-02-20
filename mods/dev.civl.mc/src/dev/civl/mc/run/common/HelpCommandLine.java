package dev.civl.mc.run.common;

import dev.civl.mc.run.IF.CommandLine;

public class HelpCommandLine extends NormalCommandLine implements CommandLine {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6455237632409616768L;
	/**
	 * The argument of the help command, which could be one of the following:
	 */
	private String arg;

	public HelpCommandLine() {
		this.commandKind = NormalCommandKind.HELP;
		arg = null;
	}

	public String arg() {
		return arg;
	}

	public void setArg(String arg) {
		this.arg = arg;
	}
}

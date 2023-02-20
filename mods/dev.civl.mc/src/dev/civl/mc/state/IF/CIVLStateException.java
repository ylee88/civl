package dev.civl.mc.state.IF;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import dev.civl.mc.model.IF.CIVLSource;
import dev.civl.mc.model.IF.CIVLException.Certainty;
import dev.civl.mc.model.IF.CIVLProperty;

/**
 * Extends an execution exception with a state at which error occurred.
 * 
 * @author siegel
 * 
 */
public class CIVLStateException extends Exception {

	/**
	 * Eclipse generated.
	 */
	private static final long serialVersionUID = -6159425221287192305L;

	protected State state;

	protected CIVLProperty property;

	protected Certainty certainty;

	protected String message;

	protected CIVLSource source;

	public CIVLStateException(CIVLProperty property, Certainty certainty,
			String message, State state, CIVLSource source) {
		this.property = property;
		this.certainty = certainty;
		this.message = message;
		assert state != null;
		this.state = state;
		this.source = source;
	}

	public CIVLSource source() {
		return this.source;
	}

	public State state() {
		return this.state;
	}

	public CIVLProperty civlProperty() {
		return this.property;
	}

	public Certainty certainty() {
		return this.certainty;
	}

	public String message() {
		return this.message;
	}

	public String toString() {
		String result = super.toString() + "\n";
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);

		ps.print(state.toString());
		result += baos.toString();
		return result;
	}
}

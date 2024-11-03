package dev.civl.mc.log.IF;

import dev.civl.abc.token.IF.SourceFormatter;
import dev.civl.mc.config.IF.CIVLConstants;
import dev.civl.mc.model.IF.CIVLException;
import dev.civl.mc.model.IF.CIVLProperty;
import dev.civl.mc.model.IF.CIVLSource;
import dev.civl.mc.model.IF.location.Location;
import dev.civl.mc.model.common.SystemCIVLSource;
import dev.civl.mc.state.IF.StackEntry;
import dev.civl.mc.state.IF.State;

/**
 * This represents an error during the execution of a program.
 * 
 * @author Manchun Zheng
 *
 */
public class CIVLExecutionException extends CIVLException {

	/**
	 * Added by Eclipse.
	 */
	private static final long serialVersionUID = 1L;

	private int pid = -1;

	private State state = null;

	private StringBuffer stateString;

	private Certainty certainty;

	private CIVLProperty property;

	private String process;

	private boolean reported = false;

	/**
	 * Constructs a new CIVLExecutionException with an associated process.
	 * 
	 * @param property
	 *                      the CIVLProperty that has been violated
	 * @param certainty
	 *                      the certainty with which this is known to be an
	 *                      error in the program being verified
	 * @param process
	 *                      process name, i.e., "p"+process identifier
	 * @param message
	 *                      a message explaining the error
	 * @param state
	 *                      the state the exception appears in
	 * @param pid
	 *                      the process id of the process which triggered the
	 *                      exception
	 * @param source
	 *                      the source code element associated to the error; may
	 *                      be null
	 */
	public CIVLExecutionException(CIVLProperty property, Certainty certainty,
			String process, String message, State state, int pid,
			CIVLSource source) {
		this(true, property, certainty, process, message, state, pid, source,
				null);
	}

	/**
	 * Constructs a new CIVLExecutionException with an associated process and
	 * stateString.
	 * 
	 * @param property
	 *                        the CIVLProperty that has been violated
	 * @param certainty
	 *                        the certainty with which this is known to be an
	 *                        error in the program being verified
	 * @param process
	 *                        process name, i.e., "p"+process identifier
	 * @param message
	 *                        a message explaining the error
	 * @param state
	 *                        the state the exception appears in
	 * @param pid
	 *                        the process id of the process which triggered the
	 *                        exception
	 * @param source
	 *                        the source code element associated to the error;
	 *                        may be null
	 * @param stateString
	 *                        the string representation of the state where the
	 *                        error occurs; may be null
	 */
	public CIVLExecutionException(CIVLProperty property, Certainty certainty,
			String process, String message, State state, int pid,
			CIVLSource source, StringBuffer stateString) {
		this(true, property, certainty, process, message, state, pid, source,
				stateString);
	}

	/**
	 * Constructs new CIVLExecutionException with no associated process.
	 * 
	 * @param property
	 *                      the CIVLProperty that has been violated
	 * @param certainty
	 *                      the certainty with which this is known to be an
	 *                      error in the program being verified
	 * @param message
	 *                      a message explaining the error
	 * @param state
	 *                      the state the exception appears in
	 */
	public CIVLExecutionException(CIVLProperty property, Certainty certainty,
			String message, State state) {

		this(false, property, certainty, null, message, state, -1, null, null);
	}

	/**
	 * Constructs new CIVLExecutionException with no associated process.
	 * 
	 * @param property
	 *                      the CIVLProperty that has been violated
	 * @param certainty
	 *                      the certainty with which this is known to be an
	 *                      error in the program being verified
	 * @param message
	 *                      a message explaining the error
	 * @param state
	 *                      the state the exception appears in
	 * @param source
	 *                      the source code element associated to the error; may
	 *                      be null
	 */
	public CIVLExecutionException(CIVLProperty property, Certainty certainty,
			String message, State state, CIVLSource source) {
		this(false, property, certainty, null, message, state, -1, source,
				null);
	}

	private CIVLExecutionException(boolean assocToProc, CIVLProperty property,
			Certainty certainty, String process, String message, State state,
			int pid, CIVLSource source, StringBuffer stateString) {
		super(message, source);
		assert property != null;
		assert certainty != null;
		assert state != null;
		if (assocToProc)
			assert pid >= 0;
		this.process = process;
		this.state = state;
		this.property = property;
		this.certainty = certainty;
		this.pid = pid;
		this.stateString = stateString;
	}

	/**
	 * @return the certainty of this error.
	 */
	public Certainty certainty() {
		return certainty;
	}

	/**
	 * @return the kind of this error.
	 */
	public CIVLProperty civlProperty() {
		return property;
	}

	/**
	 * @return the state in which this error occurred.
	 */
	public State state() {
		return state;
	}

	/**
	 * Is this error reported?
	 * 
	 * @return true iff the error has already been reported
	 */
	public boolean isReported() {
		return this.reported;
	}

	/**
	 * Set this error to be reported.
	 */
	public void setReported() {
		this.reported = true;
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		String sourceAbsPathName = source.getAbsoluteFilePath();
		String sysResourcePrefix = "/include";
		String transformersuffix = "Transformer";

		result.append(SourceFormatter.OPEN_ERR);
		result.append("CIVL execution violation ");
		if (process != null) {
			result.append("in ");
			result.append(process);
			result.append(" ");
		}
		if (pid >= 0) {
			String libraryString = "";
			for (StackEntry se : state.getProcessState(pid).getStackEntries()) {
				String fileName = se.location().function().getSource()
						.getFileName();
				if (CIVLConstants.getAllLibFilenames().contains(fileName)) {
					libraryString = "Library: " + fileName + ", Function: "
							+ se.location().function().name();
				}
			}
			if (libraryString != "")
				result.append("[" + libraryString + "] ");
		}
		result.append("(property: ");
		result.append(property);
		result.append(", certainty: ");
		result.append(certainty);
		result.append(")");
		result.append(SourceFormatter.CLOSE_ERR);
		// The violation source
		if (source != null && !(source instanceof SystemCIVLSource)
				&& !sourceAbsPathName.startsWith(sysResourcePrefix)) {
			result.append(" at\n");
			if (sourceAbsPathName.endsWith(transformersuffix)) {
				result.append(this.source.getSummary(false));
			} else {
				result.append(this.source.getSummary(true));
			}
		}
		// The call stack info
		else if (pid >= 0) {
			for (StackEntry e : state.getProcessState(pid).getStackEntries()) {
				Location location = e.location();

				if (location != null) {
					CIVLSource tempSource = location.getSource();
					String tempSourceAbsPath = tempSource.getAbsoluteFilePath();

					if (tempSource != null
							&& !(tempSource instanceof SystemCIVLSource)
							&& !tempSourceAbsPath
									.startsWith(sysResourcePrefix)) {
						result.append("\nat ");
						if (tempSourceAbsPath.endsWith(transformersuffix)) {
							result.append(tempSource.getSummary(false));
						} else {
							result.append(tempSource.getSummary(true));
						}
						break;
					}
				}
			}
		}
		result.append("\n");
		result.append(this.getMessage());
		if (this.stateString != null) {
			result.append("\n");
			result.append(stateString);
		} else if (this.state != null) {
			result.append("\n");
			result.append(this.state.callStackToString());
		}
		return result.toString();
	}
}

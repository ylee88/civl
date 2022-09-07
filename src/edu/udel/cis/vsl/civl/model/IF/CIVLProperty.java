package edu.udel.cis.vsl.civl.model.IF;

import edu.udel.cis.vsl.gmc.Option;
import edu.udel.cis.vsl.gmc.Option.OptionType;

/**
 * Enum class for representing different properties that CIVL checks for. Each
 * CIVLProperty specifies its title which is displayed in the list of standard
 * properties checked after an error free run of civl verify. Most
 * CIVLProperties are configurable by a command line option. This is also
 * specified here.
 * 
 * @author awilton
 *
 */
public enum CIVLProperty {
	/* Unconfigurable properties */
	DEREFERENCE ("dereference errors"),
	FUNCTIONAL_EQUIVALENCE ("functional equivalence"),
	INTERNAL ("internal errors"),
	LIBRARY ("library loading errors"),
	OTHER ("other errors"),
	/* Configurable properties */
	ASSERTION_VIOLATION ("assertion violations", "checkAssertion", OptionType.BOOLEAN, true),
	COMMUNICATION ("communication errors", "checkCommErr", OptionType.BOOLEAN, true),
	CONSTANT_WRITE ("writes to constant variables", "checkConstWrite", OptionType.BOOLEAN, true),
	DEADLOCK ("deadlocks", "checkDeadlock", OptionType.STRING, "absolute"),
	DIVISION_BY_ZERO ("division by zero", "checkDivisionByZero", OptionType.BOOLEAN, true),
	INPUT_WRITE ("writes to $input variables", "checkInputWrite", OptionType.BOOLEAN, true),
	INVALID_CAST ("invalid casts", "checkInvalidCast", OptionType.BOOLEAN, true),
	MALLOC ("malloc errors", "checkMallocErr", OptionType.BOOLEAN, true),
	MEMORY_LEAK ("memory leaks", "checkMemoryLeak", OptionType.BOOLEAN, true),
	MEMORY_MANAGE ("memory management errors", "checkMemManageErr", OptionType.BOOLEAN, true),
	MPI_ERROR ("mpi usage errors", "checkMpiErr", OptionType.BOOLEAN, true),
	OUT_OF_BOUNDS ("out of bounds", "checkOutOfBounds", OptionType.BOOLEAN, true),
	OUTPUT_READ ("reads from $output variables", "checkOutputRead", OptionType.BOOLEAN, true),
	POINTER ("pointer errors", "checkPointerErr", OptionType.BOOLEAN, true),
	PROCESS_LEAK ("process leaks", "checkProcLeak", OptionType.BOOLEAN, true),
	SEQUENCE ("sequence errors", "checkSeqErr", OptionType.BOOLEAN, true),
	TERMINATION ("termination", "checkTermination", OptionType.BOOLEAN, true),
	UNDEFINED_VALUE ("use of undefined values", "checkUndefVal", OptionType.BOOLEAN, true),
	UNION ("union errors", "checkUnionErr", OptionType.BOOLEAN, true);

	public final static CIVLProperty[] getAllUnconfigurableProperties() {
		return new CIVLProperty[]{DEREFERENCE, FUNCTIONAL_EQUIVALENCE, INTERNAL,
				LIBRARY, OTHER};
	}

	public final static CIVLProperty[] getAllConfigurableProperties() {
		return new CIVLProperty[]{ASSERTION_VIOLATION, COMMUNICATION,
				CONSTANT_WRITE, DEADLOCK, DIVISION_BY_ZERO, INPUT_WRITE,
				INVALID_CAST, MALLOC, MEMORY_LEAK, MEMORY_MANAGE, MPI_ERROR,
				OUT_OF_BOUNDS, OUTPUT_READ, POINTER, PROCESS_LEAK, SEQUENCE,
				TERMINATION, UNDEFINED_VALUE, UNION};
	}
	
	/**
	 * A few word description of the property being checked.
	 */
	private String propTitle;
	/**
	 * Command line option which controls if and/or how we check this property.
	 */
	private Option controllingOption;

	private CIVLProperty(String propTitle) {
		this.propTitle = propTitle;
		this.controllingOption = null;
	}

	private CIVLProperty(String propTitle, String optionStr,
			OptionType optionType, Object defaultValue) {
		this.propTitle = propTitle;
		this.controllingOption = Option.newScalarOption(optionStr, optionType,
				"check " + propTitle, defaultValue);
	}

	public Option getOption() {
		return controllingOption;
	}

	public String getPropertyTitle() {
		return propTitle;
	}
}

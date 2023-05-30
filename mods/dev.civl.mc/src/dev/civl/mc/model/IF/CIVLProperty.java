package dev.civl.mc.model.IF;

import java.util.ArrayList;
import java.util.List;

import dev.civl.gmc.Option;
import dev.civl.gmc.Option.OptionType;

/**
 * Enum class for representing different properties that CIVL checks for. Each
 * CIVLProperty specifies its title which is displayed in the list of standard
 * properties checked after an error free run of civl verify.
 * 
 * Most CIVLProperties are configurable by a command line option which is part
 * of their specification. CIVLProperties which do not have an associated
 * command line option are called unconfigurable.
 * 
 * @author awilton
 *
 */
public enum CIVLProperty {
	/* Unconfigurable properties */
	DEREFERENCE ("dereference errors"),
	FUNCTIONAL_EQUIVALENCE ("functional equivalence violations"),
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
	MPI_ERROR ("MPI usage errors", "checkMpiErr", OptionType.BOOLEAN, true),
	OUT_OF_BOUNDS ("out of bounds errors", "checkOutOfBounds", OptionType.BOOLEAN, true),
	OUTPUT_READ ("reads from $output variables", "checkOutputRead", OptionType.BOOLEAN, true),
	POINTER ("pointer errors", "checkPointerErr", OptionType.BOOLEAN, true),
	PROCESS_LEAK ("process leaks", "checkProcLeak", OptionType.BOOLEAN, true),
	SEQUENCE ("sequence errors", "checkSeqErr", OptionType.BOOLEAN, true),
	TERMINATION ("non-termination", "checkTermination", OptionType.BOOLEAN, true),
	UNDEFINED_VALUE ("use of undefined values", "checkUndefVal", OptionType.BOOLEAN, true),
	UNION ("union errors", "checkUnionErr", OptionType.BOOLEAN, true);

	/**
	 * Creates an unconfigurable property.
	 * 
	 * @param propTitle
	 *            The title of the property
	 */
	private CIVLProperty(String propTitle) {
		this.propTitle = propTitle;
		this.controllingOption = null;
	}

	/**
	 * Creates a configurable property.
	 * 
	 * @param propTitle
	 *            The title of the property
	 * @param optionStr
	 *            The name of the command line option which controls this
	 *            property
	 * @param optionType
	 *            The type of the command line option
	 * @param defaultValue
	 *            The default value of the command line option
	 */
	private CIVLProperty(String propTitle, String optionStr,
			OptionType optionType, Object defaultValue) {
		this.propTitle = propTitle;
		this.controllingOption = Option.newScalarOption(optionStr, optionType,
				"check " + propTitle, defaultValue);
	}
	
	private static ArrayList<CIVLProperty> unconfigurableProperties = new ArrayList<>(CIVLProperty.values().length);
	private static ArrayList<CIVLProperty> configurableProperties = new ArrayList<>(CIVLProperty.values().length);
	
	static {
		for (CIVLProperty prop : CIVLProperty.values()) {
			if (prop.controllingOption == null)
				unconfigurableProperties.add(prop);
			else
				configurableProperties.add(prop);
		}
	}
	
	/**
	 * @return returns all of the CIVLProperty's which are unconfigurable
	 */
	public final static List<CIVLProperty> getAllUnconfigurableProperties() {
		return unconfigurableProperties;
	}
	
	/**
	 * @return returns all of the CIVLProperty's which are configurable
	 */
	public final static List<CIVLProperty> getAllConfigurableProperties() {
		return configurableProperties;
	}
	
	/**
	 * A few word description of the property being checked.
	 */
	private String propTitle;
	/**
	 * Command line option which controls if and/or how we check this property.
	 */
	private Option controllingOption;

	/**
	 * @return returns the option which controls this property if it is
	 *         configurable. Returns null if it is unconfigurable,
	 */
	public Option getOption() {
		return controllingOption;
	}

	/**
	 * @return returns the property's title.
	 */
	public String getPropertyTitle() {
		return propTitle;
	}
}

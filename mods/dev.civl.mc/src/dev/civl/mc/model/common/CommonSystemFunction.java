/**
 * 
 */
package dev.civl.mc.model.common;

import java.util.List;

import dev.civl.mc.model.IF.CIVLSource;
import dev.civl.mc.model.IF.Identifier;
import dev.civl.mc.model.IF.Scope;
import dev.civl.mc.model.IF.SystemFunction;
import dev.civl.mc.model.IF.location.Location;
import dev.civl.mc.model.IF.type.CIVLType;
import dev.civl.mc.model.IF.variable.Variable;

/**
 * @author zirkel
 * 
 */
public class CommonSystemFunction extends CommonFunction implements
		SystemFunction {

	private String library = null;
	private boolean needsEnabler = false;

	/**
	 * @param name
	 * @param parameters
	 * @param returnType
	 * @param containingScope
	 * @param startLocation
	 * @param factory
	 */
	public CommonSystemFunction(CIVLSource source, Identifier name,
			Scope parameterScope, List<Variable> parameters,
			CIVLType returnType, Scope containingScope, int fid,
			Location startLocation, String libraryName, boolean needsEnabler) {
		super(source, true, name, parameterScope, parameters, returnType,
				containingScope, fid, startLocation);
		this.isRoot = true;
		this.library = libraryName;
		this.needsEnabler = needsEnabler;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dev.civl.mc.model.IF.SystemFunction#getLibrary()
	 */
	@Override
	public String getLibrary() {
		return library;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * dev.civl.mc.model.IF.SystemFunction#setLibrary(java.lang.String
	 * )
	 */
	@Override
	public void setLibrary(String libraryName) {
		this.library = libraryName;
	}

	@Override
	public String toString() {
		return this.name().name() + " : system function";
	}

	@Override
	public boolean isSystemFunction() {
		return true;
	}

	@Override
	public boolean isNormalFunction() {
		return false;
	}

	@Override
	public boolean needsEnabler() {
		return this.needsEnabler;
	}
}

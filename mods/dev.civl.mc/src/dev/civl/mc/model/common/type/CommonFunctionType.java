package dev.civl.mc.model.common.type;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import dev.civl.mc.model.IF.type.CIVLFunctionType;
import dev.civl.mc.model.IF.type.CIVLPrimitiveType;
import dev.civl.mc.model.IF.type.CIVLType;
import dev.civl.mc.model.IF.variable.Variable;
import dev.civl.sarl.IF.SymbolicUniverse;
import dev.civl.sarl.IF.type.SymbolicType;

/**
 * A function type has the declaration of the following format: int (int,int).
 * 
 * @author Manchun Zheng
 * 
 */
public class CommonFunctionType extends CommonType implements CIVLFunctionType {

	/* ************************** Instance Fields ************************** */

	/**
	 * The return type of this function type.
	 */
	private CIVLType returnType;

	/**
	 * The types of the parameter list of this function type.
	 */
	private CIVLType[] parameterTypes;

	/* **************************** Constructors *************************** */

	/**
	 * Creates a new instance of function type.
	 * 
	 * @param returnType
	 *            The return type of the function type.
	 * @param parasTypes
	 *            The types of the parameter list.
	 */
	public CommonFunctionType(CIVLType returnType, CIVLType[] parasTypes) {
		this.returnType = returnType;
		this.parameterTypes = parasTypes;
	}

	/* ******************* Methods from CIVLFunctionType ******************* */

	@Override
	public boolean hasState() {
		if (this.returnType.hasState())
			return true;
		for (CIVLType parameterType : this.parameterTypes) {
			if (parameterType.hasState())
				return true;
		}
		return false;
	}

	@Override
	public SymbolicType getDynamicType(SymbolicUniverse universe) {
		if (dynamicType == null) {
			List<SymbolicType> parameters = new ArrayList<>(parameterTypes.length);

			for (CIVLType paramType : parameterTypes)
				parameters.add(paramType.getDynamicType(universe));
			dynamicType = universe.functionType(parameters, returnType.getDynamicType(universe));
		}
		return dynamicType;
	}

	@Override
	public CIVLType returnType() {
		return this.returnType;
	}

	@Override
	public CIVLType[] parameterTypes() {
		return this.parameterTypes;
	}

	@Override
	public String toString() {
		String result = returnType.toString() + " (";

		if (this.parameterTypes != null) {
			for (CIVLType type : parameterTypes) {
				result += type.toString() + ", ";
			}
		}
		result = result.substring(0, result.length() - 2);
		result += ")";
		result = "(" + result + ")";
		return result;
	}

	@Override
	public void setReturnType(CIVLType type) {
		this.returnType = type;
		this.dynamicType = null;
	}

	@Override
	public void setParameterTypes(CIVLType[] types) {
		this.parameterTypes = types;
		this.dynamicType = null;
	}

	@Override
	public TypeKind typeKind() {
		return TypeKind.FUNCTION;
	}

	@Override
	public CIVLType copyAs(CIVLPrimitiveType type, SymbolicUniverse universe) {
		return type;
	}

	@Override
	public boolean isFunction() {
		return true;
	}

	@Override
	protected void addFreeVariables(Set<Variable> result,
			Set<CIVLType> seenTypes) {
		if (seenTypes.add(this)) {
			if (returnType != null)
				((CommonType) returnType).addFreeVariables(result, seenTypes);
			for (CIVLType atype : parameterTypes)
				((CommonType) atype).addFreeVariables(result, seenTypes);
		}
	}

	@Override
	public boolean hasReferences() {
		return false;
	}

	@Override
	public boolean analyze() {
		return true;
	}
}

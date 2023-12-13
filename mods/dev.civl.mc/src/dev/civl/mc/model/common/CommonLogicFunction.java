package dev.civl.mc.model.common;

import java.util.List;

import dev.civl.mc.model.IF.CIVLSource;
import dev.civl.mc.model.IF.Identifier;
import dev.civl.mc.model.IF.LogicFunction;
import dev.civl.mc.model.IF.Scope;
import dev.civl.mc.model.IF.expression.Expression;
import dev.civl.mc.model.IF.location.Location;
import dev.civl.mc.model.IF.type.CIVLType;
import dev.civl.mc.model.IF.variable.Variable;
import dev.civl.sarl.prove.IF.ProverFunctionInterpretation;

public class CommonLogicFunction extends CommonFunction
		implements
			LogicFunction {

	/**
	 * {@linkplain ACSLPredicate#definition()}
	 */
	private final Expression definition;

	private final int[] pointerToArrayMap;

	/**
	 * The constant evaluation of this logic function definition, which is an
	 * instance of {@link ProverFunctionInterpretation}.
	 */
	private ProverFunctionInterpretation constantValue = null;

	public CommonLogicFunction(CIVLSource source, Identifier name,
			Scope parameterScope, List<Variable> parameters,
			CIVLType outputType, int[] pointerToArrayMap, Scope containingScope,
			int fid, Expression definition, Location startLocation) {
		super(source, true, name, parameterScope, parameters, outputType,
				containingScope, fid, startLocation);
		this.definition = definition;
		this.pointerToArrayMap = pointerToArrayMap;
	}

	@Override
	public Expression definition() {
		return definition;
	}

	@Override
	public int hashCode() {
		// no over-loading, so parameters are not part of the hash:
		if (definition != null)
			return definition.hashCode() ^ this.name().hashCode() ^ 897653;
		else
			return this.name().hashCode() ^ 897653;
	}

	@Override
	public void setConstantValue(ProverFunctionInterpretation constantValue) {
		assert this.definition != null;
		this.constantValue = constantValue;
	}

	@Override
	public ProverFunctionInterpretation getConstantValue() {
		return this.constantValue;
	}

	@Override
	public int[] pointerToHeapVidMap() {
		return this.pointerToArrayMap;
	}

	@Override
	public boolean isReservedFunction() {
		return LogicFunction.ReservedLogicFunctionNames.contains(name().name());
	}
}

package dev.civl.mc.model.common.contract;

import java.io.PrintStream;
import java.util.HashMap;

import dev.civl.mc.model.IF.CIVLSource;
import dev.civl.mc.model.IF.Scope;
import dev.civl.mc.model.IF.contract.FunctionBehavior;
import dev.civl.mc.model.IF.contract.FunctionContract;
import dev.civl.mc.model.IF.contract.NamedFunctionBehavior;
import dev.civl.mc.model.IF.expression.Expression;
import dev.civl.mc.model.common.CommonSourceable;

public class CommonFunctionContract extends CommonSourceable implements FunctionContract {

	private boolean pure = false;

	private Expression guard = null;

	private FunctionBehavior defaultBehavior;

	private HashMap<String, NamedFunctionBehavior> namedBehaviors = new HashMap<>();

	/**
	 * The static cope in which the contract occurs, usually the parameter scope of
	 * the function definition or prototype in which the contract occurred. Not
	 * necessarily the same as the final value of the function's parameter scope
	 * because if the contract occurred first on a prototype and then later the
	 * function definition occurred, the function object will be updated to use the
	 * definition's parameter scope. Note the variable names used in the definition
	 * do not have to be the same as those used in the prototype (and contract).
	 */
	private Scope scope;

	public CommonFunctionContract(CIVLSource source, Scope scope) {
		super(source);
		this.scope = scope;
		defaultBehavior = new CommonFunctionBehavior(source);
	}

	@Override
	public Scope scope() {
		return scope;
	}

	@Override
	public FunctionBehavior defaultBehavior() {
		return this.defaultBehavior;
	}

	@Override
	public Iterable<NamedFunctionBehavior> namedBehaviors() {
		return this.namedBehaviors.values();
	}

	@Override
	public Expression guard() {
		return this.guard;
	}

	@Override
	public void setGuard(Expression expression) {
		this.guard = expression;
	}

	@Override
	public void setDefaultBehavior(FunctionBehavior behavior) {
		this.defaultBehavior = behavior;
	}

	@Override
	public void addNamedBehavior(NamedFunctionBehavior behavior) {
		this.namedBehaviors.put(behavior.name(), behavior);
	}

	@Override
	public NamedFunctionBehavior getBehavior(String name) {
		return this.namedBehaviors.get(name);
	}

	@Override
	public void print(String prefix, PrintStream out, boolean isDebug) {
		String subPrefix = prefix + "| ";

		out.println(prefix + "contract");
		if (this.pure)
			out.println(subPrefix + "pure");
		if (guard != null)
			out.println(subPrefix + "guard: " + guard.toString());
		defaultBehavior.print(subPrefix, out, isDebug);
		for (NamedFunctionBehavior behavior : namedBehaviors.values()) {
			behavior.print(subPrefix, out, isDebug);
		}
	}

	@Override
	public boolean isPure() {
		return this.pure;
	}

	@Override
	public void setPure(boolean value) {
		this.pure = value;
	}

	@Override
	public boolean hasReadsClause() {
		return this.defaultBehavior.readsNothing() || defaultBehavior.numReadsMemoryUnits() > 0;
	}

	@Override
	public boolean hasAssignsClause() {
		return defaultBehavior.assignsNothing() || defaultBehavior.numAssignsMemoryUnits() > 0;
	}

	@Override
	public boolean hasDependsClause() {
		return defaultBehavior.dependsAnyact() || defaultBehavior.dependsNoact()
				|| defaultBehavior.numDependsEvents() > 0;
	}

	@Override
	public boolean hasRequirementsOrEnsurances() {
		return (defaultBehavior.numEnsurances() + defaultBehavior.numRequirements()) > 0;
	}

}

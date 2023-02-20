package dev.civl.mc.model.common.contract;

import java.io.PrintStream;

import dev.civl.mc.model.IF.CIVLSource;
import dev.civl.mc.model.IF.contract.NamedFunctionBehavior;
import dev.civl.mc.model.IF.expression.Expression;

public class CommonNamedFunctionBehavior extends CommonFunctionBehavior
		implements NamedFunctionBehavior {
	private String name;
	private Expression assumptions;

	public CommonNamedFunctionBehavior(CIVLSource source, String name) {
		super(source);
		this.name = name;
	}

	@Override
	public String name() {
		return this.name;
	}

	@Override
	public void print(String prefix, PrintStream out, boolean isDebug) {
		String subPrefix = prefix + "| ";

		out.println(prefix + "behavior " + this.name + ":");
		out.print(subPrefix + "assumes ");
		out.print(assumptions.toString());
		out.println();
		super.print(subPrefix, out, isDebug);
	}

	@Override
	public Expression assumptions() {
		return this.assumptions;
	}

	@Override
	public void setAssumption(Expression assumption) {
		this.assumptions = assumption;
	}
}

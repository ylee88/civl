package dev.civl.mc.model.common;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;

import dev.civl.mc.model.IF.AbstractFunction;
import dev.civl.mc.model.IF.CIVLSource;
import dev.civl.mc.model.IF.Identifier;
import dev.civl.mc.model.IF.ModelFactory;
import dev.civl.mc.model.IF.Scope;
import dev.civl.mc.model.IF.type.CIVLType;
import dev.civl.mc.model.IF.variable.Variable;

public class CommonAbstractFunction extends CommonFunction implements
		AbstractFunction {

	private int continuity;

	/**
	 * An abstract function.
	 * 
	 * @param source
	 *            The CIVL source of the function
	 * @param name
	 *            The name of this function.
	 * @param parameters
	 *            The list of parameters.
	 * @param returnType
	 *            The return type of this function.
	 * @param containingScope
	 *            The scope containing this function.
	 * @param continuity
	 *            The total number of partial derivatives of this function that
	 *            may be taken.
	 * @param factory
	 *            The model factory
	 */
	public CommonAbstractFunction(CIVLSource source, Identifier name,
			Scope parameterScope, List<Variable> parameters,
			CIVLType returnType, Scope containingScope, int fid,
			int continuity, ModelFactory factory) {
		super(source, true, name, parameterScope, parameters, returnType,
				containingScope, fid, null, factory);
		this.continuity = continuity;
	}

	@Override
	public int continuity() {
		return continuity;
	}

	/* ********************** Methods from CIVLFunction ******************** */

	@Override
	public void print(String prefix, PrintStream out, boolean isDebug) {
		Iterator<Variable> iter;

		out.println(prefix + "abstract function " + this.name());
		out.println(prefix + "| continuous " + continuity);
		out.println(prefix + "| formal parameters");
		iter = this.parameters().iterator();
		while (iter.hasNext()) {
			out.println(prefix + "| | " + iter.next().name());
		}
		this.outerScope().print(prefix + "| ", out, isDebug);
		out.flush();
	}

	@Override
	public boolean isAbstractFunction() {
		return true;
	}

	@Override
	public boolean isNormalFunction() {
		return false;
	}

}

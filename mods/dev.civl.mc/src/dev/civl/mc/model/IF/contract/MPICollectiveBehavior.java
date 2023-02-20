package dev.civl.mc.model.IF.contract;

import java.util.List;

import dev.civl.mc.model.IF.expression.Expression;
import dev.civl.mc.model.IF.variable.Variable;

public interface MPICollectiveBehavior extends FunctionBehavior {
	public static enum MPICommunicationPattern {
		P2P, COL
	};

	Expression communicator();

	Variable[] agreedVariables();

	void setAgreedVariables(Variable[] jointVariables);

	MPICommunicationPattern mpiCommunicationPattern();

	void addNamedBehaviors(NamedFunctionBehavior namedBehavior);

	List<NamedFunctionBehavior> namedBehaviors();

	NamedFunctionBehavior namedBahavior(String name);
}

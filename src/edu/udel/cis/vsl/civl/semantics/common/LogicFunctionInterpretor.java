package edu.udel.cis.vsl.civl.semantics.common;

import java.util.Arrays;
import java.util.List;

import edu.udel.cis.vsl.civl.model.IF.CIVLInternalException;
import edu.udel.cis.vsl.civl.model.IF.LogicFunction;
import edu.udel.cis.vsl.civl.model.IF.Scope;
import edu.udel.cis.vsl.civl.model.IF.variable.Variable;
import edu.udel.cis.vsl.civl.semantics.IF.Evaluation;
import edu.udel.cis.vsl.civl.semantics.IF.Evaluator;
import edu.udel.cis.vsl.civl.state.IF.CIVLHeapException;
import edu.udel.cis.vsl.civl.state.IF.State;
import edu.udel.cis.vsl.civl.state.IF.StateFactory;
import edu.udel.cis.vsl.civl.state.IF.UnsatisfiablePathConditionException;
import edu.udel.cis.vsl.sarl.IF.SymbolicUniverse;
import edu.udel.cis.vsl.sarl.IF.expr.SymbolicConstant;
import edu.udel.cis.vsl.sarl.prove.IF.ProverFunctionInterpretation;

/**
 * Evaluates {@link LogicFunction}s to their interpretations which can be fed to
 * SARL. Since logic functions are stateless, the evaluation of each logic
 * function is only need to be done once.
 * 
 * @author ziqing
 */
public class LogicFunctionInterpretor {

	/**
	 * Translates (evaluates) a set of {@link ACSLPredicate}s to P
	 * {@link ProverFunctionInterpretation}s. This evaluation is
	 * state-independent.
	 * 
	 */
	static public ProverFunctionInterpretation[] evaluateLogicFunctions(
			List<LogicFunction> logicFunctions, Evaluator evaluator,
			StateFactory stateFactory) {
		ProverFunctionInterpretation logicFunctionInterprets[] = new ProverFunctionInterpretation[logicFunctions
				.size()];
		int i = 0;
		State state;

		try {
			// dummy state and pid:
			state = stateFactory.initialState(evaluator.modelFactory().model());
		} catch (CIVLHeapException he) {
			throw new CIVLInternalException(
					"Unexpected heap exception when creating an initial state.",
					evaluator.modelFactory().model().getSource());
		}

		try {
			for (LogicFunction pred : logicFunctions) {
				if (pred.definition() != null)
					logicFunctionInterprets[i++] = evaluateLogicFunction(pred,
							state, evaluator);
			}
		} catch (UnsatisfiablePathConditionException e) {
			throw new CIVLInternalException(
					"Unexpected unsatisfiable path condition exception when computing logic function values.",
					evaluator.modelFactory().model().getSource());
		}
		return Arrays.copyOf(logicFunctionInterprets, i);
	}

	/**
	 * 
	 * @throws UnsatisfiablePathConditionException
	 *             if the definition of the logic function is unsatisfiable.
	 */
	static private ProverFunctionInterpretation evaluateLogicFunction(
			LogicFunction logicFunc, State state, Evaluator evaluator)
			throws UnsatisfiablePathConditionException {
		ProverFunctionInterpretation result = logicFunc.getConstantValue();
		SymbolicUniverse su = evaluator.universe();
		StateFactory sf = evaluator.stateFactory();

		if (result != null)
			return result;

		// evaluate arguments:
		SymbolicConstant[] actualArg = new SymbolicConstant[logicFunc
				.parameters().size()];
		int i = 0;

		// TODO: check pointer restriction:
		for (Variable var : logicFunc.parameters()) {
			if (var.type().isPointerType())
				actualArg[i++] = null; // will be set later
			else
				actualArg[i++] = su.symbolicConstant(
						su.stringObject(var.name().name()),
						var.type().getDynamicType(su));
		}
		state = sf.pushCallStack(state, 0, logicFunc, actualArg);

		// the parameter dynamic scope
		int dyscopeId = state.getProcessState(0).getDyscopeId();
		// the parameter lexical scope, note that if there is any pointer type
		// argument, this scope will contain dummy "heap" variable for it.
		// This is the way of achieving the state-independent. The pointer type
		// argument will be set to point to its unique dummy heap.
		Scope lexScope = state.getDyscope(dyscopeId).lexicalScope();

		i = 0;
		// set pointer to dummy heap; set heap to arbitrary arrayof T:
		for (Variable var : logicFunc.parameters()) {
			if (var.type().isPointerType()) {
				int heapVid = logicFunc.pointerToHeapVidMap()[i];
				Variable heapVar = lexScope.variable(heapVid);
				SymbolicConstant heapVal = su.symbolicConstant(
						su.stringObject(heapVar.name().name()),
						heapVar.type().getDynamicType(su));

				state = sf.setVariable(state, var.vid(), dyscopeId,
						evaluator.symbolicUtility().makePointer(dyscopeId,
								heapVid, su.arrayElementReference(
										su.identityReference(), su.zeroInt())));
				state = sf.setVariable(state, heapVid, dyscopeId, heapVal);
				actualArg[i] = heapVal;
			}
			i++;
		}

		Evaluation eval = evaluator.evaluate(state, 0, logicFunc.definition());

		result = ProverFunctionInterpretation.newProverPredicate(
				evaluator.universe(), logicFunc.name().name(), actualArg,
				eval.value);
		logicFunc.setConstantValue(result);
		return result;
	}
}

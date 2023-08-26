package dev.civl.mc.semantics.common;

import java.util.Set;

import dev.civl.mc.model.IF.expression.Expression;
import dev.civl.mc.model.IF.expression.LHSExpression;
import dev.civl.mc.semantics.IF.Evaluation;
import dev.civl.mc.semantics.IF.Evaluator;
import dev.civl.mc.state.IF.State;
import dev.civl.mc.state.IF.UnsatisfiablePathConditionException;
import dev.civl.sarl.IF.expr.SymbolicExpression;

/**
 * An evaluator that extends {@link CommonEvaluator} for collecting the read set
 * from the expression that is going to be evaluated.
 * 
 * @author ziqing
 */
public class ReadSetCollectEvaluator extends CommonEvaluator
		implements
			Evaluator {
	/**
	 * a reference to {@link ReadSetAnalyzer}, which carries out the read-set
	 * analysis work
	 */
	private ReadSetAnalyzer readSetAnalyzer;

	/**
	 * a reference to a {@link CommonEvaluator}, which carries out the
	 * Evaluation work
	 */
	private Evaluator superEvaluator;

	public ReadSetCollectEvaluator(CommonEvaluator superEvaluator) {
		super(superEvaluator.modelFactory, superEvaluator.stateFactory,
				superEvaluator.libLoader, superEvaluator.libExeLoader,
				superEvaluator.symbolicUtil, superEvaluator.symbolicAnalyzer,
				superEvaluator.memUnitFactory, superEvaluator.errorLogger,
				superEvaluator.civlConfig);
		this.superEvaluator = superEvaluator;
		((CommonSymbolicAnalyzer) this.symbolicAnalyzer)
				.setEvaluator(superEvaluator);
		readSetAnalyzer = new ReadSetAnalyzer(superEvaluator);
	}

	@Override
	public Evaluation evaluate(State state, int pid, Expression expression,
			boolean checkUndefinedValue)
			throws UnsatisfiablePathConditionException {
		Set<SymbolicExpression> readSets = readSetAnalyzer.analyze(expression,
				state, pid);
		Evaluation eval = superEvaluator.evaluate(state, pid, expression,
				checkUndefinedValue);

		for (SymbolicExpression memVal : readSets)
			eval.state = stateFactory.addReadWriteRecords(eval.state, pid,
					memVal, true);
		return eval;
	}

	@Override
	public Evaluation reference(State state, int pid, LHSExpression operand)
			throws UnsatisfiablePathConditionException {
		Set<SymbolicExpression> readSets = readSetAnalyzer.analyze(operand,
				state, pid);
		Evaluation eval = superEvaluator.reference(state, pid, operand);

		for (SymbolicExpression memVal : readSets)
			eval.state = stateFactory.addReadWriteRecords(eval.state, pid,
					memVal, true);
		return eval;
	}

	/**
	 * Collect read set for the LHS expression. The thing that is special is
	 * that the memory location represented by the LHS expression itself is not
	 * included in the collected set. Returns the state where read set has been
	 * collected.
	 */
	public State collectForLHS(State state, int pid,
			LHSExpression lhsExpression)
			throws UnsatisfiablePathConditionException {
		for (SymbolicExpression memVal : readSetAnalyzer
				.analyzeAsAddressof(state, pid, lhsExpression))
			state = stateFactory.addReadWriteRecords(state, pid, memVal, true);
		return state;
	}
}

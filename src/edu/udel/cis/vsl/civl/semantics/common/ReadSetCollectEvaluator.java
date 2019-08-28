package edu.udel.cis.vsl.civl.semantics.common;

import java.util.Set;

import edu.udel.cis.vsl.civl.model.IF.expression.Expression;
import edu.udel.cis.vsl.civl.model.IF.expression.LHSExpression;
import edu.udel.cis.vsl.civl.semantics.IF.Evaluation;
import edu.udel.cis.vsl.civl.semantics.IF.Evaluator;
import edu.udel.cis.vsl.civl.state.IF.State;
import edu.udel.cis.vsl.civl.state.IF.UnsatisfiablePathConditionException;
import edu.udel.cis.vsl.sarl.IF.expr.SymbolicExpression;

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
}

package dev.civl.mc.library.domain;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import dev.civl.mc.config.IF.CIVLConfiguration;
import dev.civl.mc.dynamic.IF.SymbolicUtility;
import dev.civl.mc.library.common.BaseLibraryExecutor;
import dev.civl.mc.model.IF.CIVLProperty;
import dev.civl.mc.model.IF.CIVLSource;
import dev.civl.mc.model.IF.ModelConfiguration;
import dev.civl.mc.model.IF.ModelFactory;
import dev.civl.mc.model.IF.expression.Expression;
import dev.civl.mc.model.IF.type.CIVLDomainType;
import dev.civl.mc.model.IF.type.CIVLType;
import dev.civl.mc.semantics.IF.Evaluation;
import dev.civl.mc.semantics.IF.Executor;
import dev.civl.mc.semantics.IF.LibraryEvaluatorLoader;
import dev.civl.mc.semantics.IF.LibraryExecutor;
import dev.civl.mc.semantics.IF.LibraryExecutorLoader;
import dev.civl.mc.semantics.IF.SymbolicAnalyzer;
import dev.civl.mc.state.IF.State;
import dev.civl.mc.state.IF.UnsatisfiablePathConditionException;
import dev.civl.sarl.IF.Reasoner;
import dev.civl.sarl.IF.expr.NumericExpression;
import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.IF.number.IntegerNumber;
import dev.civl.sarl.IF.type.SymbolicTupleType;
import dev.civl.sarl.IF.type.SymbolicType;
import dev.civl.sarl.IF.type.SymbolicUnionType;

public class LibdomainExecutor extends BaseLibraryExecutor
		implements
			LibraryExecutor {

	public LibdomainExecutor(String name, Executor primaryExecutor,
			ModelFactory modelFactory, SymbolicUtility symbolicUtil,
			SymbolicAnalyzer symbolicAnalyzer, CIVLConfiguration civlConfig,
			LibraryExecutorLoader libExecutorLoader,
			LibraryEvaluatorLoader libEvaluatorLoader) {
		super(name, primaryExecutor, modelFactory, symbolicUtil,
				symbolicAnalyzer, civlConfig, libExecutorLoader,
				libEvaluatorLoader);
	}

	@Override
	protected Evaluation executeValue(State state, int pid, String process,
			CIVLSource source, String functionName, Expression[] arguments,
			SymbolicExpression[] argumentValues)
			throws UnsatisfiablePathConditionException {
		Evaluation callEval = null;

		switch (functionName) {
			case "$dimension_of" :
				callEval = execute_dimension_of(state, pid, process, arguments,
						argumentValues, source);
				break;
			case "$domain_partition" :
				callEval = execute_domain_partition(state, pid, process,
						arguments, argumentValues, source);
				break;
			case "$high_of_regular_range" :
				callEval = execute_high_of_regular_range(state, pid, process,
						arguments, argumentValues, source);
				break;
			case "$is_rectangular_domain" :
				callEval = execute_is_rectangular_domain(state, pid, process,
						arguments, argumentValues, source);
				break;
			case "$is_regular_range" :
				callEval = execute_is_regular_range(state, pid, process,
						arguments, argumentValues, source);
				break;
			case "$low_of_regular_range" :
				callEval = execute_low_of_regular_range(state, pid, process,
						arguments, argumentValues, source);
				break;
			case "$range_of_rectangular_domain" :
				callEval = execute_range_of_rectangular_domain(state, pid,
						process, arguments, argumentValues, source);
				break;
			case "$step_of_regular_range" :
				callEval = execute_step_of_regular_range(state, pid, process,
						arguments, argumentValues, source);
				break;
		}
		return callEval;
	}

	private Evaluation execute_dimension_of(State state, int pid,
			String process, Expression[] arguments,
			SymbolicExpression[] argumentValues, CIVLSource source)
			throws UnsatisfiablePathConditionException {
		return new Evaluation(state,
				this.symbolicUtil.getDimensionOf(argumentValues[0]));

	}

	private Evaluation execute_range_of_rectangular_domain(State state, int pid,
			String process, Expression[] arguments,
			SymbolicExpression[] argumentValues, CIVLSource source)
			throws UnsatisfiablePathConditionException {
		int index = this.symbolicUtil.extractInt(source,
				(NumericExpression) argumentValues[1]);

		return new Evaluation(state, this.symbolicUtil
				.getRangeOfRectangularDomain(argumentValues[0], index));
	}

	private Evaluation execute_is_rectangular_domain(State state, int pid,
			String process, Expression[] arguments,
			SymbolicExpression[] argumentValues, CIVLSource source)
			throws UnsatisfiablePathConditionException {
		return new Evaluation(state, universe.bool(
				this.symbolicUtil.isRectangularDomain(argumentValues[0])));
	}

	private Evaluation execute_is_regular_range(State state, int pid,
			String process, Expression[] arguments,
			SymbolicExpression[] argumentValues, CIVLSource source)
			throws UnsatisfiablePathConditionException {
		return new Evaluation(state, universe
				.bool(this.symbolicUtil.isRegularRange(argumentValues[0])));
	}

	private Evaluation execute_step_of_regular_range(State state, int pid,
			String process, Expression[] arguments,
			SymbolicExpression[] argumentValues, CIVLSource source)
			throws UnsatisfiablePathConditionException {
		return new Evaluation(state,
				this.symbolicUtil.getStepOfRegularRange(argumentValues[0]));
	}

	private Evaluation execute_low_of_regular_range(State state, int pid,
			String process, Expression[] arguments,
			SymbolicExpression[] argumentValues, CIVLSource source)
			throws UnsatisfiablePathConditionException {
		return new Evaluation(state,
				this.symbolicUtil.getLowOfRegularRange(argumentValues[0]));
	}

	private Evaluation execute_high_of_regular_range(State state, int pid,
			String process, Expression[] arguments,
			SymbolicExpression[] argumentValues, CIVLSource source)
			throws UnsatisfiablePathConditionException {
		return new Evaluation(state,
				this.symbolicUtil.getHighOfRegularRange(argumentValues[0]));
	}

	/**
	 * Executes the domain_partition statement. Returns a object with type of
	 * struct "$domian_decomposition"
	 * 
	 * @param state
	 *            The current state
	 * @param pid
	 *            The PID of the process
	 * @param process
	 *            The information of the process
	 * @param lhs
	 *            The left-hand side expression
	 * @param arguments
	 *            The expressions of arguments
	 * @param argumentValues
	 *            The symbolic expressions of arguments
	 * @param source
	 *            The CIVL source of the statement
	 * @return
	 * @throws UnsatisfiablePathConditionException
	 */
	private Evaluation execute_domain_partition(State state, int pid,
			String process, Expression[] arguments,
			SymbolicExpression[] argumentValues, CIVLSource source)
			throws UnsatisfiablePathConditionException {
		SymbolicExpression domain = argumentValues[0];
		NumericExpression strategy = (NumericExpression) argumentValues[1];
		NumericExpression numParts = (NumericExpression) argumentValues[2];
		Reasoner reasoner = universe.reasoner(state.getPathCondition(universe));
		IntegerNumber strategy_num = (IntegerNumber) reasoner
				.extractNumber(strategy),
				numParts_num = (IntegerNumber) reasoner.extractNumber(numParts);
		int strategy_int, numParts_int;
		List<SymbolicExpression> subDomains = null;
		SymbolicTupleType resultType;
		SymbolicExpression result;

		if (strategy_num == null) {
			this.errorLogger.logSimpleError(source, state, pid, process,
					symbolicAnalyzer.stateInformation(state), CIVLProperty.OTHER,
					"$domain_partition requires a concrete strategy argument");
			throw new UnsatisfiablePathConditionException();
		}
		if (numParts_num == null) {
			this.errorLogger.logSimpleError(source, state, pid, process,
					symbolicAnalyzer.stateInformation(state), CIVLProperty.OTHER,
					"$domain_partition requires a concrete number of partitions argument");
			throw new UnsatisfiablePathConditionException();
		}
		strategy_int = strategy_num.intValue();
		numParts_int = numParts_num.intValue();
		switch (strategy_int) {
			default :
			case ModelConfiguration.DECOMP_ROUND_ROBIN :
				subDomains = this.domainPartition_round_robin(domain,
						numParts_int);
				break;
		}
		resultType = universe.tupleType(
				universe.stringObject("$domain_decomposition"),
				Arrays.asList(universe.integerType(),
						universe.arrayType(domain.type(), numParts)));
		result = universe.tuple(resultType, Arrays.asList(numParts,
				universe.array(domain.type(), subDomains)));
		return new Evaluation(state, result);
	}

	/**
	 * Do a domain partition based on the robin strategy.
	 * 
	 * @param domain
	 *            The symbolic expression of the domain.
	 * @param number
	 *            The number of the partitions.
	 * @return
	 */
	private List<SymbolicExpression> domainPartition_round_robin(
			SymbolicExpression domain, int number) {

		if (number == 1)
			return Arrays.asList(domain);
		else {
			Map<Integer, List<SymbolicExpression>> partitions = new HashMap<>(
					number);
			List<SymbolicExpression> current = symbolicUtil
					.getDomainInit(domain);
			int id = 0;
			List<SymbolicExpression> result = new LinkedList<>();
			Iterator<List<SymbolicExpression>> domIter = symbolicUtil
					.getDomainIterator(domain);
			CIVLType rangeType = this.typeFactory.rangeType();
			CIVLDomainType civlDomType = this.typeFactory.domainType(rangeType);
			SymbolicTupleType domType = (SymbolicTupleType) civlDomType
					.getDynamicType(universe);
			SymbolicUnionType domUnionType = civlDomType
					.getDynamicSubTypesUnion(universe);
			SymbolicExpression myDomain, myLiterals;
			SymbolicType domainElementType = symbolicUtil
					.getDomainElementType(domain);
			NumericExpression dim = (NumericExpression) universe
					.tupleRead(domain, zeroObject);

			while (domIter.hasNext()) {
				SymbolicExpression element;
				List<SymbolicExpression> partitionedElements;

				current = domIter.next();
				element = universe.array(universe.integerType(), current);
				if (partitions.containsKey(id)) {
					partitionedElements = partitions.get(id);

					partitionedElements.add(element);
				} else {
					partitionedElements = new LinkedList<SymbolicExpression>();
					partitionedElements.add(element);
					partitions.put(id, partitionedElements);
				}
				id = (id + 1) % number;
			}
			// Making all integer-elements entries be a literal domain
			for (int i = 0; i < number; i++) {
				List<SymbolicExpression> myPartition = partitions.get(i);
				SymbolicExpression elementsArray;

				if (myPartition != null)
					elementsArray = universe.array(domainElementType,
							myPartition);
				else {
					elementsArray = universe.emptyArray(domainElementType);
				}
				myLiterals = universe.unionInject(domUnionType, oneObject,
						elementsArray);
				myDomain = universe.tuple(domType,
						Arrays.asList(dim, one, myLiterals));
				result.add(myDomain);
			}
			return result;
		}
	}
}

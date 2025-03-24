package dev.civl.mc.library.domain;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import dev.civl.mc.config.IF.CIVLConfiguration;
import dev.civl.mc.dynamic.IF.SymbolicUtility;
import dev.civl.mc.kripke.IF.Enabler;
import dev.civl.mc.kripke.IF.LibraryEnabler;
import dev.civl.mc.kripke.IF.LibraryEnablerLoader;
import dev.civl.mc.library.common.BaseLibraryEnabler;
import dev.civl.mc.model.IF.CIVLInternalException;
import dev.civl.mc.model.IF.CIVLSource;
import dev.civl.mc.model.IF.CIVLUnimplementedFeatureException;
import dev.civl.mc.model.IF.ModelConfiguration;
import dev.civl.mc.model.IF.ModelFactory;
import dev.civl.mc.model.IF.Scope;
import dev.civl.mc.model.IF.expression.CompoundLiteralExpression;
import dev.civl.mc.model.IF.expression.Expression;
import dev.civl.mc.model.IF.statement.AssignStatement;
import dev.civl.mc.model.IF.statement.CallOrSpawnStatement;
import dev.civl.mc.model.IF.statement.Statement;
import dev.civl.mc.model.IF.type.CIVLStructOrUnionType;
import dev.civl.mc.semantics.IF.Evaluation;
import dev.civl.mc.semantics.IF.Evaluator;
import dev.civl.mc.semantics.IF.LibraryEvaluatorLoader;
import dev.civl.mc.semantics.IF.LibraryLoaderException;
import dev.civl.mc.semantics.IF.Semantics;
import dev.civl.mc.semantics.IF.SymbolicAnalyzer;
import dev.civl.mc.semantics.IF.Transition;
import dev.civl.mc.state.IF.State;
import dev.civl.mc.state.IF.UnsatisfiablePathConditionException;
import dev.civl.mc.util.IF.Pair;
import dev.civl.sarl.IF.Reasoner;
import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.IF.expr.NumericExpression;
import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.IF.number.IntegerNumber;
import dev.civl.sarl.IF.number.Number;
import dev.civl.sarl.IF.type.SymbolicTupleType;
import dev.civl.sarl.IF.type.SymbolicType;
import dev.civl.sarl.IF.type.SymbolicUnionType;

public class LibdomainEnabler extends BaseLibraryEnabler
		implements
			LibraryEnabler {

	public LibdomainEnabler(String name, Enabler primaryEnabler,
			Evaluator evaluator, ModelFactory modelFactory,
			SymbolicUtility symbolicUtil, SymbolicAnalyzer symbolicAnalyzer,
			CIVLConfiguration civlConfig, LibraryEnablerLoader libEnablerLoader,
			LibraryEvaluatorLoader libEvaluatorLoader) {
		super(name, primaryEnabler, evaluator, modelFactory, symbolicUtil,
				symbolicAnalyzer, civlConfig, libEnablerLoader,
				libEvaluatorLoader);
	}

	@Override
	public List<Transition> enabledTransitions(State state,
			CallOrSpawnStatement call, BooleanExpression clause, int pid)
			throws UnsatisfiablePathConditionException {
		String functionName = call.function().name().name();
		try {
			switch (functionName) {
				case "$domain_partition" :
					return this.enabledDomainPartition(state, call, clause,
							pid);
				default :
					return super.enabledTransitions(state, call, clause, pid);
			}
		} catch (LibraryLoaderException e) {
			throw new CIVLInternalException("Domain library loader fails",
					call.getSource());
		}
	}

	/* *************************** Private Methods ************************* */

	private List<Transition> enabledDomainPartition(State state,
			CallOrSpawnStatement call, BooleanExpression clause, int pid)
			throws UnsatisfiablePathConditionException, LibraryLoaderException {
		List<Statement> statements = new LinkedList<>();
		List<Transition> transitions = new LinkedList<>();
		Expression[] arguments = new Expression[3];
		SymbolicExpression strategy;
		SymbolicExpression nthreads;
		SymbolicExpression domain;
		Evaluation eval;
		Number strategyNum;
		int strategyInt;
		Reasoner reasoner = universe.reasoner(
				universe.and(state.getPathCondition(universe), clause));
		String process = "p" + pid;

		call.arguments().toArray(arguments);
		// arguments: domain, strategy, number of threads
		eval = evaluator.evaluate(state, pid, arguments[0]);
		state = eval.state;
		domain = eval.value;
		eval = evaluator.evaluate(state, pid, arguments[1]);
		state = eval.state;
		strategy = eval.value;
		eval = evaluator.evaluate(state, pid, arguments[2]);
		state = eval.state;
		nthreads = eval.value;
		// TODO: strategy should always be a concrete value ?
		assert strategy instanceof NumericExpression : call.getSource()
				+ ": stratey must be a numeric type";
		strategyNum = reasoner.extractNumber((NumericExpression) strategy);
		assert strategyNum instanceof IntegerNumber : arguments[1].getSource()
				+ ": strategy must be a DECOMP_STRATEGY type";
		strategyInt = ((IntegerNumber) strategyNum).intValue();
		switch (strategyInt) {
			case ModelConfiguration.DECOMP_ALL :
				List<SymbolicExpression> subDecomp;
				SymbolicExpression[] argValues = new SymbolicExpression[3];
				CIVLStructOrUnionType domDecompType = (CIVLStructOrUnionType) call
						.lhs().getExpressionType();

				Arrays.asList(domain, strategy, nthreads).toArray(argValues);
				subDecomp = evaluateDomDecompAllPartition(state, pid, process,
						arguments, argValues, call.getSource());
				statements.addAll(allDecompStatements(call,
						arguments[0].expressionScope(),
						(CIVLStructOrUnionType) domDecompType, subDecomp,
						arguments[0].getSource()));
				break;
			case ModelConfiguration.DECOMP_ROUND_ROBIN :
				return super.enabledTransitions(state, call, clause, pid);
			case ModelConfiguration.DECOMP_RANDOM :
			default :
				throw new CIVLUnimplementedFeatureException("domain strategy");
		}
		for (int i = 0; i < statements.size(); i++) {
			transitions.add(
					Semantics.newTransition(pid, clause, statements.get(i)));
		}
		return transitions;
	}

	private List<AssignStatement> allDecompStatements(CallOrSpawnStatement call,
			Scope exprScope, CIVLStructOrUnionType exprType,
			List<SymbolicExpression> subDecomp, CIVLSource sourceOfLocation) {
		CompoundLiteralExpression decompsConstantExpr;
		List<AssignStatement> assignStatements = new LinkedList<>();

		for (int i = 0; i < subDecomp.size(); i++) {
			SymbolicExpression decomp = subDecomp.get(i);
			AssignStatement assignStatement;

			decompsConstantExpr = modelFactory.compoundLiteralExpression(
					sourceOfLocation, exprScope, exprType, false);
			decompsConstantExpr.setLiteralConstantValue(decomp);
			assignStatement = modelFactory.assignStatement(call.getSource(),
					call.source(), call.lhs(), decompsConstantExpr, false);
			assignStatement.setTargetTemp(call.target());
			assignStatements.add(assignStatement);
			assignStatement.source().removeOutgoing(assignStatement);
		}
		return assignStatements;
	}

	/**
	 * Evaluates the decomposition struct of all partition strategy for the
	 * $domain_partition(domain, strategy, number) function.
	 * 
	 * @return All possible domain decomposition objects
	 */
	private List<SymbolicExpression> evaluateDomDecompAllPartition(State state,
			int pid, String process, Expression[] arguments,
			SymbolicExpression[] argumentValues, CIVLSource source) {
		List<SymbolicExpression> allDecomp = new LinkedList<>();
		List<List<Pair<Integer, Integer>>> partitions;
		SymbolicExpression domain = argumentValues[0];
		@SuppressWarnings("unused")
		NumericExpression strategy = (NumericExpression) argumentValues[1];
		NumericExpression numParts = (NumericExpression) argumentValues[2];
		NumericExpression dim;
		Reasoner reasoner = universe.reasoner(state.getPathCondition(universe));
		int numElements_int; // domain size
		int numParts_int;
		Number numPartsNumber, numElementsNumber; // Number type objects
													// extracted by reasoner
		SymbolicType domainElementType = symbolicUtil
				.getDomainElementType(domain);
		SymbolicTupleType decompType;
		SymbolicExpression decomp;

		dim = ((NumericExpression) universe.tupleRead(domain, zeroObject));
		// the following cast should be guaranteed, dimension should always a
		// concrete number
		// assert strategy == DECOMP_ALL;
		numPartsNumber = reasoner.extractNumber(numParts);
		numElementsNumber = reasoner
				.extractNumber(symbolicUtil.getDomainSize(domain));
		decompType = universe.tupleType(
				universe.stringObject("$domain_decomposition"),
				Arrays.asList(universe.integerType(),
						universe.arrayType(domain.type(), numParts)));
		if (numPartsNumber == null)
			throw new CIVLInternalException("Non-concrete partition number",
					arguments[2].getSource());
		if (numElementsNumber == null)
			throw new CIVLInternalException("Non-concrete domain size",
					arguments[0].getSource());
		try {
			numElements_int = ((IntegerNumber) numElementsNumber).intValue();
			numParts_int = ((IntegerNumber) numPartsNumber).intValue();
		} catch (ClassCastException e) {
			throw new CIVLInternalException(
					"Number cannot cast to IntegerNumber", source);
		}
		partitions = this.getAllPartitions(numElements_int, numParts_int);
		// For every partition, make a decomposition struct
		// create sub-domains at first
		for (int i = 0; i < partitions.size(); i++) {
			List<Pair<Integer, Integer>> singlePartition;
			// key: thread id
			// value a list of domain elements which at this point are an array
			// of
			// integers(list of integers).
			Map<Integer, List<List<SymbolicExpression>>> decompedDomainsElements = new HashMap<>();

			singlePartition = partitions.get(i);
			try {
				Iterator<List<SymbolicExpression>> domIter = symbolicUtil
						.getDomainIterator(domain);
				SymbolicUnionType unionType = (SymbolicUnionType) universe
						.tupleRead(domain, twoObject).type();
				List<SymbolicExpression> subDomains = new LinkedList<>();

				for (int j = 0; j < singlePartition.size(); j++) {
					// Get a pair of the element index and thread index
					Pair<Integer, Integer> element_thread = singlePartition
							.get(j);
					List<SymbolicExpression> element;
					List<List<SymbolicExpression>> elements;

					assert element_thread.left == j;
					// Here we don't check if it has next, it should be
					// guaranteed and if a call of next() throws an exception,
					// thats a bug, this "try" will catch it.
					element = domIter.next();
					if (!decompedDomainsElements
							.containsKey(element_thread.right)) {
						elements = new LinkedList<>();
					} else {
						elements = decompedDomainsElements
								.get(element_thread.right);
					}
					elements.add(element);
					decompedDomainsElements.put(element_thread.right, elements);
				}
				if (decompedDomainsElements.keySet().size() < numParts_int)
					continue;
				// creating sub-domains and decomp struct
				for (int j = 0; j < decompedDomainsElements.keySet()
						.size(); j++) {
					List<List<SymbolicExpression>> elements;
					SymbolicExpression myDomain;
					SymbolicExpression literalDomainElement, literalDomain,
							domainUnion;
					List<SymbolicExpression> litDomEleArrayComp = new LinkedList<>();

					elements = decompedDomainsElements.get(j);
					for (int k = 0; k < elements.size(); k++) {
						literalDomainElement = universe
								.array(universe.integerType(), elements.get(k));
						litDomEleArrayComp.add(literalDomainElement);
					}
					literalDomain = universe.array(domainElementType,
							litDomEleArrayComp);
					domainUnion = universe.unionInject(unionType, oneObject,
							literalDomain);
					myDomain = universe.tuple((SymbolicTupleType) domain.type(),
							Arrays.asList(dim, one, domainUnion));
					subDomains.add(myDomain);
				}
				decomp = universe.tuple(decompType, Arrays.asList(numParts,
						universe.array(domain.type(), subDomains)));
				allDecomp.add(decomp);

			} catch (NullPointerException e) {
				throw new CIVLInternalException(
						"All partition doesn't give each thread at least one task",
						source);
			} catch (CIVLInternalException e) {
				throw new CIVLInternalException(
						"Unexpected problem happened when iterating a domain for all composition strategy",
						source);
			}
		}
		return allDecomp;
	}

	/**
	 * The returned collection should have such structure: par1:{0:n1, 1:n2,
	 * 2:n2.........numEle:nx}; par2{...}; For every element, it should know
	 * which process owns itself.
	 * 
	 * @param numEle
	 * @param numPart
	 * @return
	 */
	private List<List<Pair<Integer, Integer>>> getAllPartitions(int numEle,
			int numPart) {
		List<List<Pair<Integer, Integer>>> result;
		List<Pair<Integer, Integer>> singlePartiton = new LinkedList<>();

		result = this.getAllPartitionsWorker(singlePartiton, numEle, numPart);
		return result;
	}

	private List<List<Pair<Integer, Integer>>> getAllPartitionsWorker(
			List<Pair<Integer, Integer>> singlePartition, int numEle,
			int numParts) {
		List<List<Pair<Integer, Integer>>> result = new LinkedList<>();
		int startElement = singlePartition.size();

		for (int i = startElement; i < numEle; i++) {
			for (int j = 1; j < numParts; j++) {
				List<Pair<Integer, Integer>> singlePartitionBranch = new LinkedList<>(
						singlePartition);

				singlePartitionBranch.add(new Pair<>(i, j));
				result.addAll(this.getAllPartitionsWorker(singlePartitionBranch,
						numEle, numParts));
			}
			singlePartition.add(new Pair<>(i, 0));
		}
		result.add(singlePartition);
		return result;
	}
}

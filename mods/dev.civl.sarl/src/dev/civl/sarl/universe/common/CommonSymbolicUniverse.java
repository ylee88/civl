/*******************************************************************************
 * Copyright (c) 2013 Stephen F. Siegel, University of Delaware.
 * 
 * This file is part of SARL.
 * 
 * SARL is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * SARL is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with SARL. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package dev.civl.sarl.universe.common;

import java.util.ArrayList;
import java.util.List;

import dev.civl.sarl.IF.Reasoner;
import dev.civl.sarl.IF.SARLException;
import dev.civl.sarl.IF.SymbolicUniverse;
import dev.civl.sarl.IF.ValidityResult.ResultType;
import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.IF.expr.NumericExpression;
import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.IF.number.Number;
import dev.civl.sarl.IF.type.SymbolicType;
import dev.civl.sarl.IF.type.SymbolicType.SymbolicTypeKind;
import dev.civl.sarl.expr.IF.NumericExpressionFactory;
import dev.civl.sarl.preuniverse.IF.FactorySystem;
import dev.civl.sarl.preuniverse.common.CommonPreUniverse;
import dev.civl.sarl.prove.IF.ProverFunctionInterpretation;
import dev.civl.sarl.reason.IF.ReasonerFactory;
import dev.civl.sarl.util.autotg.TestTranslator;

/**
 * A standard implementation of {@link SymbolicUniverse}, relying heavily on a
 * given {@link NumericExpressionFactory} for dealing with numeric issues and a
 * BooleanExpressionFactory for dealing with boolean expressions.
 * 
 * @author siegel
 */
public class CommonSymbolicUniverse extends CommonPreUniverse
		implements SymbolicUniverse {

	/**
	 * The factory for producing new Reasoner instances.
	 */
	private ReasonerFactory reasonerFactory;

	/**
	 * The factory for producing new {@link Why3Reasoner} instances.
	 */
	private ReasonerFactory why3ReasonerFactory = null;

	/**
	 * A reference to a {@link TestTranslator}, which is instantiated when
	 * {@link #enableSARLTestGeneration(boolean)} is called with argument equals
	 * to true.
	 */
	private TestTranslator sarlTestGenerator = null;

	/**
	 * A list of logic function definitions that will be sent to a reasoner
	 * whenever a reasoner is created because a prover query may invloves logic
	 * functions.
	 */
	private ProverFunctionInterpretation logicFunctions[] = new ProverFunctionInterpretation[0];

	// Constructor...

	/**
	 * Constructs a new CommonSymbolicUniverse from the given system of
	 * factories.
	 * 
	 * @param system
	 *            a factory system
	 */
	public CommonSymbolicUniverse(FactorySystem system) {
		super(system);
	}

	// Helper methods...

	@Override
	public Reasoner reasoner(BooleanExpression context) {
		List<BooleanExpression> contextStack = new ArrayList<>(1);
		contextStack.add(context);
		return reasoner(contextStack);
	}

	@Override
	public Reasoner reasoner(List<BooleanExpression> contextStack) {
		return reasonerFactory.getReasoner(contextStack,
				getUseBackwardSubstitution(), logicFunctions);
	}

	public void setReasonerFactory(ReasonerFactory reasonerFactory) {
		this.reasonerFactory = reasonerFactory;
	}

	@Override
	public Number extractNumber(BooleanExpression assumption,
			NumericExpression expression) {
		Number result = extractNumber(expression);

		if (result != null)
			return result;
		return reasoner(assumption).extractNumber(expression);
	}

	@Override
	public SymbolicExpression valueSetWidening(BooleanExpression context,
			SymbolicExpression valueSetTemplate) {
		if (!expressionFactory.isValueSetTemplateType(valueSetTemplate.type()))
			throw new SARLException("the operand: " + valueSetTemplate
					+ " of the widening operator does not have value set template type");
		SymbolicType valueType = getValueTypeOfValueSetTemplate(
				valueSetTemplate);
		SymbolicExpression refArr = tupleRead(valueSetTemplate, intObject(1));

		return expressionFactory.valueSetWidening(reasoner(context), valueType,
				refArr);
	}

	@Override
	public SymbolicExpression valueSetProtectiveWidening(
			BooleanExpression context, SymbolicExpression vstM,
			SymbolicExpression vstP) {
		assert vstM != null && vstP != null;
		if (!expressionFactory.isValueSetTemplateType(vstM.type()))
			throw new SARLException("the m operand: " + vstM
					+ " of the protective widening operator does not have value set template type");
		if (!expressionFactory.isValueSetTemplateType(vstP.type()))
			throw new SARLException("the p operand: " + vstP
					+ " of the protective widening operator does not have value set template type");
		SymbolicType valueTypeM = getValueTypeOfValueSetTemplate(vstM);
		SymbolicType valueTypeP = getValueTypeOfValueSetTemplate(vstP);
		if (!valueTypeM.equals(valueTypeP)) {
			throw new SARLException(
					"Value types of m and p in protective widening are not the same."
							+ " m value type: " + valueTypeM
							+ ". p value type: " + valueTypeP + ".");
		}
		SymbolicExpression refArrM = tupleRead(vstM, intObject(1));
		SymbolicExpression refArrP = tupleRead(vstP, intObject(1));

		return expressionFactory.valueSetProtectiveWidening(reasoner(context),
				valueTypeM, refArrM, refArrP);
	}

	@Override
	public SymbolicExpression valueSetElimWidening(BooleanExpression context,
			SymbolicExpression vst, SymbolicExpression elimExpr,
			SymbolicExpression lower, SymbolicExpression upper) {
		if (!expressionFactory.isValueSetTemplateType(vst.type())) {
			throw new SARLException("the operand: " + vst
					+ " of the elim widening"
					+ " operator does not have value set template type");
		}
		if (elimExpr.type().typeKind() != SymbolicTypeKind.INTEGER) {
			throw new SARLException(
					"the operand: " + elimExpr + " of the elim widening"
							+ " operator does not have integer type");
		}
		if (lower.type().typeKind() != SymbolicTypeKind.INTEGER) {
			throw new SARLException(
					"the operand: " + lower + " of the elim widening"
							+ " operator does not have integer type");
		}
		if (upper.type().typeKind() != SymbolicTypeKind.INTEGER) {
			throw new SARLException(
					"the operand: " + upper + " of the elim widening"
							+ " operator does not have integer type");
		}
		SymbolicType valueType = getValueTypeOfValueSetTemplate(vst);
		SymbolicExpression refArr = tupleRead(vst, intObject(1));

		return expressionFactory.valueSetElimWidening(reasoner(context),
				valueType, refArr, elimExpr, (NumericExpression) lower,
				(NumericExpression) upper);
	}

	@Override
	public Reasoner why3Reasoner(BooleanExpression context) {
		if (why3ReasonerFactory == null)
			return reasoner(context);
		else
			return why3ReasonerFactory.getReasoner(context,
					getUseBackwardSubstitution(), logicFunctions);
	}

	public void setWhy3ReasonerFactory(ReasonerFactory reasonerFactory) {
		this.why3ReasonerFactory = reasonerFactory;
	}

	@Override
	public void enableSARLTestGeneration(boolean enable) {
		if (enable) {
			if (sarlTestGenerator == null)
				sarlTestGenerator = new TestTranslator();
		} else
			sarlTestGenerator = null;
	}

	@Override
	public void saveValidCallAsSARLTest(BooleanExpression context,
			BooleanExpression predicate, ResultType expectedResult,
			boolean useWhy3, String testName, String... comments) {
		if (sarlTestGenerator != null)
			sarlTestGenerator.generateValidCheckMethod(context, predicate,
					expectedResult, useWhy3, testName, comments);
	}

	@Override
	public void generateTestClass(String name) {
		if (sarlTestGenerator != null)
			sarlTestGenerator.generateTestClass(name);
	}

	@Override
	public void setLogicFunctions(
			ProverFunctionInterpretation[] logicFunctions) {
		if (logicFunctions != null)
			this.logicFunctions = logicFunctions;
		else
			this.logicFunctions = new ProverFunctionInterpretation[0];
	}
}

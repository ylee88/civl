package dev.civl.sarl.prove.smt;

import dev.civl.sarl.IF.config.ProverInfo.ProverKind;
import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.IF.object.SymbolicObject;
import dev.civl.sarl.IF.type.SymbolicFunctionType;
import dev.civl.sarl.IF.type.SymbolicType;
import dev.civl.sarl.IF.type.SymbolicFunctionType.SpecialRelationKind;
import dev.civl.sarl.preuniverse.IF.PreUniverse;
import dev.civl.sarl.prove.IF.ProverFunctionInterpretation;
import dev.civl.sarl.util.FastList;
import dev.civl.sarl.util.Pair;

public class Z3Translator extends SMTTranslator {

	public Z3Translator(PreUniverse universe, SymbolicExpression theExpression,
			ProverFunctionInterpretation logicFunctions[]) {
		super(universe, ProverKind.Z3, theExpression, logicFunctions);
	}

	public Z3Translator(PreUniverse universe, SymbolicExpression theExpression) {
		this(universe, theExpression, new ProverFunctionInterpretation[0]);
	}

	public Z3Translator(Z3Translator startingContext, SymbolicExpression theExpression) {
		super(startingContext, theExpression);
	}

	protected FastList<String> functionDeclaration(String name, SymbolicFunctionType functionType) {
		// Use special Z3 features for special relations...
		SpecialRelationKind relKind = functionType.specialRelationKind();
		boolean useZ3Special = relKind != SpecialRelationKind.NONE;
		String funDeclPrefix = useZ3Special ? "(define-fun " : "(declare-fun ";
		FastList<String> result = new FastList<>(funDeclPrefix, name, " (");
		boolean first = true;
		int i = 0;

		for (SymbolicType inputType : functionType.inputTypes()) {
			if (first)
				first = false;
			else
				result.add(" ");
			if (useZ3Special) {
				result.add("(x" + i + " ");
				result.append(translateType(inputType));
				result.add(")");
			} else
				result.append(translateType(inputType));
			i++;
		}
		result.add(") ");
		result.append(translateType(functionType.outputType()));
		/*
		 * These attributes are supported by Z3 only:
		 * https://microsoft.github.io/z3guide/docs/theories/Special%20Relations/ See
		 * CVCTranslator to see how to translate these attributes when they are not
		 * supported directly.
		 */
		switch (relKind) {
		case PARTIAL_ORDER:
			result.add("((_ partial-order 0) x0 x1)");
			break;
		case LINEAR_ORDER:
			result.add("((_ linear-order 0) x0 x1)");
			break;
		case PIECEWISE_LINEAR_ORDER:
			result.add("((_ piecewise-linear-order 0) x0 x1)");
			break;
		case TREE_ORDER:
			result.add("((_ tree-order 0) x0 x1)");
			break;
		case NONE:
		default:
			break;
		}
		result.add(")\n");
		Pair<String, String> key = new Pair<>(name, result.toString());
		if (functionSet.contains(key))
			return new FastList<>();
		functionSet.add(key);
		return result;
	}

	protected FastList<String> translatePower(SymbolicExpression expression) {
		SymbolicExpression base = (SymbolicExpression) expression.argument(0);
		SymbolicObject exponent = expression.argument(1);
		return translatePowerAsCarrot(translate(base), translateExponent(exponent));
	}

}

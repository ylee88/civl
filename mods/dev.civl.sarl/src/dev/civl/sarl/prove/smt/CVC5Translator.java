package dev.civl.sarl.prove.smt;

import java.math.BigInteger;

import dev.civl.sarl.IF.config.ProverInfo.ProverKind;
import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.IF.object.SymbolicObject;
import dev.civl.sarl.preuniverse.IF.PreUniverse;
import dev.civl.sarl.prove.IF.ProverFunctionInterpretation;
import dev.civl.sarl.util.FastList;

public class CVC5Translator extends SMTTranslator {

	public CVC5Translator(PreUniverse universe, SymbolicExpression theExpression,
			ProverFunctionInterpretation logicFunctions[]) {
		super(universe, ProverKind.Z3, theExpression, logicFunctions);
	}

	public CVC5Translator(CVC5Translator startingContext, SymbolicExpression theExpression) {
		super(startingContext, theExpression);
	}

	protected FastList<String> translatePower(SymbolicExpression expression) {
		SymbolicExpression base = (SymbolicExpression) expression.argument(0);
		SymbolicObject exponent = expression.argument(1);
		BigInteger expBig = extractConcreteInt(extractConcreteNumber(exponent));
		if (expBig != null) {
			FastList<String> result = translatePowerSmall(base, expBig);
			if (result != null)
				return result;
		}
		// CVC5 can use ^ if exponent is concrete and at most 67108864:
		if (expBig != null && expBig.signum() == 1 && expBig.compareTo(BigInteger.valueOf(67108864)) <= 0) {
			String expStr = expBig.toString();
			if (base.type().isReal())
				expStr += ".0"; // CVC5 insists base and exponent have same type
			return translatePowerAsCarrot(translate(base), new FastList<String>(expStr));
		}
		return translatePowerGeneric(base, exponent);
	}

}

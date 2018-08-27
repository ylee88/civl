package edu.udel.cis.vsl.civl.model.common.type;

import edu.udel.cis.vsl.civl.model.IF.type.CIVLMemType;
import edu.udel.cis.vsl.sarl.IF.expr.BooleanExpression;
import edu.udel.cis.vsl.sarl.IF.expr.NumericExpression;
import edu.udel.cis.vsl.sarl.IF.type.SymbolicType;

public class CommonMemType extends CommonPrimitiveType implements CIVLMemType {

	public CommonMemType(SymbolicType symbolicType,
			NumericExpression sizeofExpression, BooleanExpression facts) {
		super(PrimitiveTypeKind.MEM, symbolicType, sizeofExpression, facts);
	}
}

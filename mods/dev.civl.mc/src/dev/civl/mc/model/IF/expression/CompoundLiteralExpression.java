package dev.civl.mc.model.IF.expression;

import java.util.List;

import dev.civl.abc.ast.node.IF.compound.CompoundLiteralObject;
import dev.civl.abc.ast.node.IF.compound.LiteralObject;
import dev.civl.abc.ast.node.IF.compound.ScalarLiteralObject;
import dev.civl.mc.model.IF.type.CIVLType;
/**
 * 
 * This class represents compound literal expressions, including string
 * literals.
 * 
 * A CompoundLiteralExpression should be set with either a
 * {@link CIVLCompoundLiteralObject} or a constant value, but not both of them.
 * 
 * @author zmanchun
 */
public interface CompoundLiteralExpression extends LiteralExpression {

	/**
	 * 
	 * @return a C aggregate type
	 */
	CIVLType type();

	/**
	 * 
	 * @return true iff this compound literal expression represents a string
	 *         literal
	 */
	boolean isStringLiteral();

	/**
	 * Set {@link CIVLCompoundLiteralObject} of this expression. A
	 * CompoundLiteralExpression must either have a CompoundLiteralObj or a
	 * constant value.
	 */
	void setLiteralObject(CIVLCompoundLiteralObject obj);

	/**
	 * @return the {@link CIVLCompoundLiteralObject} of this expression; or null
	 *         if this expression has a constant value
	 */
	CIVLCompoundLiteralObject getLiteralObject();

	/* Methods for creating LiteralObjs */

	CIVLLiteralObject createScalarLiteralObject(CIVLType type, Expression expr);

	CIVLCompoundLiteralObject createCompoundLiteralObject(CIVLType type,
			List<CIVLLiteralObject> elements);

	/**
	 * The CIVL model counterpart of {@link LiteralObject} in ABC.
	 */
	static interface CIVLLiteralObject {
		CIVLType type();

		/**
		 * @return the set of expressions involved in the literal expression
		 *         represented by this {@link CIVLLiteralObject}. Expressions in
		 *         the returning list can be in any order.
		 */
		List<Expression> subExpressions();
	}

	/**
	 * The CIVL model counterpart of {@link ScalarLiteralObject} in ABC.
	 */
	static interface CIVLScalarLiteralObject extends CIVLLiteralObject {
		Expression getExpression();
	}

	/**
	 * The CIVL model counterpart of {@link CompoundLiteralObject} in ABC.
	 */
	static interface CIVLCompoundLiteralObject
			extends
				CIVLLiteralObject,
				Iterable<CIVLLiteralObject> {
		int size();
	}
}

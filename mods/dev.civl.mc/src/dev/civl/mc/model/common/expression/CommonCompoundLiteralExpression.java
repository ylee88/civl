package dev.civl.mc.model.common.expression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import dev.civl.mc.model.IF.CIVLSource;
import dev.civl.mc.model.IF.Scope;
import dev.civl.mc.model.IF.expression.CompoundLiteralExpression;
import dev.civl.mc.model.IF.expression.Expression;
import dev.civl.mc.model.IF.type.CIVLType;
import dev.civl.mc.model.IF.variable.Variable;
import dev.civl.sarl.IF.expr.SymbolicExpression;

public class CommonCompoundLiteralExpression extends CommonExpression
		implements
			CompoundLiteralExpression {

	private CIVLCompoundLiteralObject obj = null;
	
	final private boolean isStringLiteral;

	public CommonCompoundLiteralExpression(CIVLSource source, Scope hscope,
			Scope lscope, CIVLType type, boolean isStringLiteral) {
		super(source, hscope, lscope, type);
		this.isStringLiteral = isStringLiteral;
	}
	
	@Override
	public boolean isStringLiteral() {
		return isStringLiteral;
	}

	@Override
	public ExpressionKind expressionKind() {
		return ExpressionKind.COMPOUND_LITERAL;
	}

	@Override
	public CIVLType type() {
		return expressionType;
	}

	@Override
	public String toString() {
		assert (obj != null || this.constantValue != null);
		if (obj == null)
			return "(" + this.type() + ")" + this.constantValue();
		return this.obj.toString();
	}

	@Override
	public Set<Variable> variableAddressedOf(Scope scope) {
		Set<Variable> result = new HashSet<>();

		if (obj == null)
			return result;
		for (Expression elt : obj.subExpressions()) {
			Set<Variable> vars = elt.variableAddressedOf(scope);

			if (vars != null)
				result.addAll(vars);
		}
		return result;
	}

	@Override
	public Set<Variable> variableAddressedOf() {
		Set<Variable> result = new HashSet<>();

		if (obj == null)
			return result;
		for (Expression elt : obj.subExpressions()) {
			Set<Variable> vars = elt.variableAddressedOf();

			if (vars != null)
				result.addAll(vars);
		}
		return result;
	}

	@Override
	public LiteralKind literalKind() {
		return LiteralKind.COMPOUND;
	}

	@Override
	protected boolean expressionEquals(Expression expression) {
		assert (obj != null || this.constantValue != null);

		if (expression instanceof CommonCompoundLiteralExpression) {
			CommonCompoundLiteralExpression that = (CommonCompoundLiteralExpression) expression;

			if (obj == null)
				return that.constantValue != null
						&& this.constantValue == that.constantValue;
			if (that.obj == null)
				return false;
			if (obj.size() != that.obj.size() || !type().equals(that.type()))
				return false;

			Iterator<CIVLLiteralObject> thisI = this.getLiteralObject()
					.iterator();
			Iterator<CIVLLiteralObject> thatI = that.getLiteralObject()
					.iterator();

			while (thisI.hasNext() && thatI.hasNext()) {
				CIVLLiteralObject thisE = thisI.next();
				CIVLLiteralObject thatE = thatI.next();

				if (!thisE.equals(thatE))
					return false;
			}
			return true;
		}
		return false;
	}

	@Override
	public void setLiteralConstantValue(SymbolicExpression value) {
		assert obj == null;
		this.constantValue = value;
	}

	@Override
	protected void addFreeVariables(Set<Variable> result) {
		if (obj == null)
			return;
		for (Expression elt : obj.subExpressions())
			result.addAll(elt.freeVariables());
	}

	@Override
	public void setLiteralObject(CIVLCompoundLiteralObject obj) {
		assert constantValue == null;
		this.obj = obj;
	}

	@Override
	public CIVLCompoundLiteralObject getLiteralObject() {
		return this.obj;
	}

	@Override
	public CIVLLiteralObject createScalarLiteralObject(CIVLType type,
			Expression expr) {
		return new CommonCIVLScalarLiteralObject(type, expr);
	}

	@Override
	public CIVLCompoundLiteralObject createCompoundLiteralObject(CIVLType type,
			List<CIVLLiteralObject> elements) {
		return new CommonCIVLCompoundLiteralObject(type, elements);
	}

	class CommonCIVLScalarLiteralObject implements CIVLScalarLiteralObject {

		final private Expression expr;
		final private CIVLType type;

		CommonCIVLScalarLiteralObject(CIVLType type, Expression expr) {
			assert expr == null || expr.getExpressionType() == type;
			this.expr = expr;
			this.type = type;
		}

		@Override
		public CIVLType type() {
			return type;
		}

		@Override
		public Expression getExpression() {
			return expr;
		}

		@Override
		public String toString() {
			if (expr == null)
				return "(" + type + ") null";
			return expr.toString();
		}

		@Override
		public List<Expression> subExpressions() {
			if (expr != null)
				return Arrays.asList(expr);
			return Arrays.asList();
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof CIVLScalarLiteralObject) {
				if (expr == null)
					return ((CIVLScalarLiteralObject) o).getExpression() == null;

				Expression otherExpr = ((CIVLScalarLiteralObject) o).getExpression();

				return otherExpr != null && otherExpr.equals(expr);
			}
			return false;
		}
	}

	class CommonCIVLCompoundLiteralObject implements CIVLCompoundLiteralObject {
		private final CIVLType type;
		private final List<CIVLLiteralObject> elements;

		CommonCIVLCompoundLiteralObject(CIVLType type, List<CIVLLiteralObject> elements) {
			this.type = type;
			this.elements = new ArrayList<>(elements);
		}

		@Override
		public CIVLType type() {
			return type;
		}

		@Override
		public Iterator<CIVLLiteralObject> iterator() {
			return elements.iterator();
		}

		@Override
		public String toString() {
			StringBuffer SB = new StringBuffer();

			SB.append("(" + this.type() + "){");
			if (elements.size() <= 0) {
				SB.append("}");
				return SB.toString();
			}
			SB.append(elements.get(0));

			final int size = elements.size();

			for (CIVLLiteralObject elt : elements.subList(1, size)) {
				SB.append(", " + elt);
			}
			SB.append("}");
			return SB.toString();
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof CommonCIVLCompoundLiteralObject) {
				CommonCIVLCompoundLiteralObject that = (CommonCIVLCompoundLiteralObject) o;

				if (that.size() != size())
					return false;
				if (!that.type.equals(this.type))
					return false;

				Iterator<CIVLLiteralObject> thisI = iterator();
				Iterator<CIVLLiteralObject> thatI = that.iterator();

				while (thisI.hasNext() && thatI.hasNext()) {
					CIVLLiteralObject thisE = thisI.next();
					CIVLLiteralObject thatE = thatI.next();

					if (!thisE.equals(thatE))
						return false;
				}
				return true;
			}
			return false;
		}

		@Override
		public List<Expression> subExpressions() {
			List<Expression> ll = new LinkedList<>();

			for (CIVLLiteralObject elt : elements)
				if (elt != null)
					ll.addAll(elt.subExpressions());
			return ll;
		}

		@Override
		public int size() {
			return elements.size();
		}
	}
}

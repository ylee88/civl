package edu.udel.cis.vsl.civl.transform.analysis.common;

import java.util.HashMap;
import java.util.Map;

import edu.udel.cis.vsl.abc.ast.entity.IF.Entity;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.ExpressionNode;
import edu.udel.cis.vsl.civl.transform.analysisIF.AssignmentFactory;
import edu.udel.cis.vsl.civl.transform.analysisIF.AssignmentIF;
import edu.udel.cis.vsl.civl.transform.analysisIF.AssignmentIF.AssignExprIF;

public class CommonAssignmentFactory implements AssignmentFactory {

	/**
	 * a table maps variables to their unique abstraction
	 */
	private Map<Entity, AssignExprIF> seenAssignExprs;

	/**
	 * A abstraction of right hand side representing the worst case
	 */
	private final AssignExprIF FULL;

	private int AssignExprCounter = 0;

	class CommonAssignExpr implements AssignExprIF {

		private int id;

		private Entity source = null;

		private ExpressionNode nonEntitySource = null;

		CommonAssignExpr(Entity source) {
			this.id = AssignExprCounter++;
			this.source = source;
		}

		CommonAssignExpr(ExpressionNode source) {
			this.id = AssignExprCounter++;
			this.nonEntitySource = source;
		}

		CommonAssignExpr() {
			this.id = AssignExprCounter++;
		}

		@Override
		public Entity source() {
			return source;
		}

		@Override
		public ExpressionNode nonEntitySource() {
			return nonEntitySource;
		}

		@Override
		public int hashCode() {
			return id;
		}

		@Override
		public boolean isFull() {
			return this == FULL;
		}

		@Override
		public String toString() {
			if (this == FULL)
				return "FULL";
			else if (source != null)
				return "V" + id + "(" + source.getName() + ")";
			else
				return "V" + id + "(" + nonEntitySource.prettyRepresentation()
						+ ")";
		}

		@Override
		public int id() {
			return id;
		}
	}

	/**
	 * a simple implementation of {@link AssignmentIF}
	 */
	class CommonAssignment implements AssignmentIF {

		private AssignExprIF lhs, rhs;

		private AssignmentKind kind;

		CommonAssignment(AssignExprIF lhs, boolean lhsDeref, AssignExprIF rhs,
				boolean rhsDeref, boolean rhsAddrof) {
			assert !(rhsDeref && rhsAddrof);
			assert !lhsDeref || (!rhsDeref && !rhsAddrof);
			if (lhsDeref)
				kind = AssignmentKind.COMPLEX_LD;
			else if (rhsDeref)
				kind = AssignmentKind.COMPLEX_RD;
			else if (rhsAddrof)
				kind = AssignmentKind.BASE;
			else
				kind = AssignmentKind.SIMPLE;
			assert lhs != null;
			assert rhs != null;
			this.lhs = lhs;
			this.rhs = rhs;
		}

		@Override
		public AssignExprIF lhs() {
			return lhs;
		}

		@Override
		public AssignExprIF rhs() {
			return rhs;
		}

		@Override
		public AssignmentKind kind() {
			return kind;
		}

		@Override
		public String toString() {
			switch (kind) {
				case BASE :
					return this.lhs.toString() + " = &" + this.rhs.toString();
				case COMPLEX_LD :
					return "*" + this.lhs.toString() + " = "
							+ this.rhs.toString();
				case COMPLEX_RD :
					return this.lhs.toString() + " = *" + this.rhs.toString();
				case SIMPLE :
					return this.lhs.toString() + " = " + this.rhs.toString();
				default :
					return null;
			}
		}
	}

	CommonAssignmentFactory() {
		this.FULL = new CommonAssignExpr();
		this.seenAssignExprs = new HashMap<>();
	}

	@Override
	public AssignmentIF assignment(AssignExprIF lhs, boolean lhsDeref,
			AssignExprIF rhs, boolean rhsDeref, boolean rhsAddrof) {
		return new CommonAssignment(lhs, lhsDeref, rhs, rhsDeref, rhsAddrof);
	}

	@Override
	public AssignExprIF assignmentExpression(Entity source) {
		AssignExprIF result = seenAssignExprs.get(source);

		if (result == null) {
			result = new CommonAssignExpr(source);
			seenAssignExprs.put(source, result);
		}
		return result;
	}

	@Override
	public AssignExprIF assignmentExpression(ExpressionNode source) {
		return new CommonAssignExpr(source);
	}

	@Override
	public AssignExprIF full() {
		return FULL;
	}
}

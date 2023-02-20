package dev.civl.mc.model.common.statement;

import dev.civl.mc.model.IF.CIVLSource;
import dev.civl.mc.model.IF.contract.LoopContract;
import dev.civl.mc.model.IF.expression.Expression;
import dev.civl.mc.model.IF.location.Location;
import dev.civl.mc.model.IF.statement.LoopBranchStatement;

public class CommonLoopBranchStatement extends CommonNoopStatement
		implements
			LoopBranchStatement {

	/* *************************** Instance Fields ************************* */

	/**
	 * Mark this statement to be the if branch or else branch.
	 */
	private boolean isTrueBranch;

	/**
	 * {@link LoopContract} attached with this loop (optional)
	 */
	private LoopContract loopContract = null;

	/* ************************** Instance Fields ************************** */

	/**
	 * 
	 * @param civlSource
	 *            The CIVL source of this statement
	 * @param source
	 *            The source location of this statement
	 * @param isTrue
	 *            true iff this is the if branching, else the else branching.
	 */
	public CommonLoopBranchStatement(CIVLSource civlSource, Location source,
			Expression guard, boolean isTrue, LoopContract loopContract) {
		super(civlSource, source, guard, null);
		this.noopKind = NoopKind.LOOP;
		source.setBinaryBranching(true);
		this.isTrueBranch = isTrue;
		this.statementScope = guard.expressionScope();
		this.loopContract = loopContract;
	}

	/* ************************* Methods from Object *********************** */

	@Override
	public String toString() {
		if (isTrueBranch) {
			return "LOOP_BODY_ENTER";
		} else
			return "LOOP_BODY_EXIT";
	}

	@Override
	public boolean isEnter() {
		return this.isTrueBranch;
	}

	@Override
	public boolean isContracted() {
		return loopContract != null;
	}

	@Override
	public LoopContract getLoopContract() {
		return loopContract;
	}

	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj)) {
			CommonLoopBranchStatement other = (CommonLoopBranchStatement) obj;

			return other.isTrueBranch == isTrueBranch;
		}
		return false;
	}
}

package dev.civl.mc.semantics.common;

import dev.civl.mc.model.IF.statement.Statement;
import dev.civl.mc.semantics.IF.Transition;
import dev.civl.sarl.IF.expr.BooleanExpression;

/**
 * A noop transition which represents a kind of transitions that either have no
 * statement needs to be executed or the associated statements are unnecessary
 * to be executed (e.g. $elaborate statements).
 * 
 * @author ziqingluo
 *
 */
public class NoopTransition extends CommonTransition implements Transition {

	public NoopTransition(int pid, BooleanExpression assumption,
			Statement statement, boolean symplifyState) {
		super(assumption, pid, statement, symplifyState);
	}

	@Override
	public TransitionKind transitionKind() {
		return TransitionKind.NOOP;
	}
}

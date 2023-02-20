package dev.civl.sarl.simplify.eval;

/**
 * The parent of all {@link EvalNodeRat} nodes
 * 
 * @author ziqing
 *
 */
public abstract class EvalNodeRat extends EvalNode<Rat> {
	@Override
	abstract Rat evaluate();
}

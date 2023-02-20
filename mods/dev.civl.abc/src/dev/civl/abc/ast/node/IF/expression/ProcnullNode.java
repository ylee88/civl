package dev.civl.abc.ast.node.IF.expression;

/**
 * Represents the CIVL-C null process constant <code>$proc_null</code>, which is
 * a constant of type <code>$proc</code>.
 * 
 * @author siegel
 * 
 */
public interface ProcnullNode extends ConstantNode {
	@Override
	ProcnullNode copy();
}

package dev.civl.abc.ast.node.IF.statement;

/**
 * A null statement: ";". AKA "no-op".
 * 
 * @author siegel
 * 
 */
public interface NullStatementNode extends StatementNode {
	@Override
	NullStatementNode copy();
}

package dev.civl.abc.ast.node.common.statement;

import java.io.PrintStream;

import dev.civl.abc.ast.IF.ASTException;
import dev.civl.abc.ast.IF.DifferenceObject;
import dev.civl.abc.ast.node.IF.ASTNode;
import dev.civl.abc.ast.node.IF.statement.AtomicNode;
import dev.civl.abc.ast.node.IF.statement.StatementNode;
import dev.civl.abc.token.IF.Source;

/**
 * An "atomic" statement has the form <code>$atomic body</code>, where body is a
 * list of statements.
 * 
 * @author zheng
 * 
 */

public class CommonAtomicNode extends CommonStatementNode
		implements
			AtomicNode {

	/**
	 * Constructor
	 * 
	 * @param source
	 * @param body
	 */
	public CommonAtomicNode(Source source, StatementNode body) {
		super(source, body);
	}

	@Override
	public StatementKind statementKind() {
		return StatementKind.ATOMIC;
	}

	@Override
	public StatementNode getBody() {
		return (StatementNode) child(0);
	}

	@Override
	public AtomicNode copy() {
		return new CommonAtomicNode(getSource(), duplicate(getBody()));
	}

	@Override
	protected void printBody(PrintStream out) {
		out.print("atomic");
	}

	@Override
	protected DifferenceObject diffWork(ASTNode that) {
		if (that instanceof AtomicNode)
			return null;
		return new DifferenceObject(this, that);
	}

	@Override
	public void setBody(StatementNode body) {
		setChild(0, body);
	}

	@Override
	public ASTNode setChild(int index, ASTNode child) {
		if (index != 0)
			throw new ASTException(
					"CommonAtomicNode has only one child, but saw index "
							+ index);
		if (!(child == null || child instanceof StatementNode))
			throw new ASTException(
					"Child of CommonAtomicNode must be a StatementNode, but saw "
							+ child + " with type " + child.nodeKind());
		return super.setChild(index, child);
	}
}

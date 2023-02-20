package dev.civl.abc.ast.node.common.statement;

import java.io.PrintStream;

import dev.civl.abc.ast.IF.DifferenceObject;
import dev.civl.abc.ast.node.IF.ASTNode;
import dev.civl.abc.ast.node.IF.statement.JumpNode;
import dev.civl.abc.token.IF.Source;

public class CommonJumpNode extends CommonStatementNode implements JumpNode {

	private JumpKind jumpKind;

	public CommonJumpNode(Source source, JumpKind jumpKind) {
		super(source);
		this.jumpKind = jumpKind;
	}

	@Override
	public JumpKind getKind() {
		return jumpKind;
	}

	@Override
	protected void printBody(PrintStream out) {
		switch (jumpKind) {
			case GOTO :
				out.print("GotoStatement");
				break;
			case CONTINUE :
				out.print("ContinueStatement");
				break;
			case BREAK :
				out.print("BreakStatement");
				break;
			case RETURN :
				out.print("ReturnStatement");
				break;
			default :
				throw new RuntimeException("impossible");
		}
	}

	@Override
	public JumpNode copy() {
		return new CommonJumpNode(getSource(), getKind());
	}

	@Override
	public StatementKind statementKind() {
		return StatementKind.JUMP;
	}

	@Override
	protected DifferenceObject diffWork(ASTNode that) {
		if (that instanceof JumpNode)
			if (this.jumpKind == ((JumpNode) that).getKind())
				return null;
		return new DifferenceObject(this, that);
	}
}

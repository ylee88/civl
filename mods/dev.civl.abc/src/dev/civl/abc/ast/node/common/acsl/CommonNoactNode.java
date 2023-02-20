package dev.civl.abc.ast.node.common.acsl;

import java.io.PrintStream;

import dev.civl.abc.ast.node.IF.acsl.NoactNode;
import dev.civl.abc.token.IF.Source;

public class CommonNoactNode extends CommonDependsEventNode
		implements
			NoactNode {

	public CommonNoactNode(Source source) {
		super(source);
	}

	@Override
	public DependsEventNodeKind getEventKind() {
		return DependsEventNodeKind.NOACT;
	}

	@Override
	public NoactNode copy() {
		return new CommonNoactNode(getSource());
	}

	@Override
	protected void printBody(PrintStream out) {
		out.print("\\noact");
	}
}

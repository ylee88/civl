package dev.civl.abc.ast.node.common.type;

import java.io.PrintStream;

import dev.civl.abc.ast.node.IF.type.TypeNode;
import dev.civl.abc.token.IF.Source;

/**
 * This class represents a {@link TypeNode} of a <code>$mem</code> type.
 * 
 * @author ziqingluo
 *
 */
public class CommonMemTypeNode extends CommonTypeNode {

	public CommonMemTypeNode(Source source) {
		super(source, TypeNodeKind.MEM);
	}

	@Override
	public TypeNode copy() {
		return new CommonMemTypeNode(this.getSource());
	}

	@Override
	protected void printBody(PrintStream out) {
		out.print("$mem");
	}
}

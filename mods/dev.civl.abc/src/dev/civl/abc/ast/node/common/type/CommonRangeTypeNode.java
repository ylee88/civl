package dev.civl.abc.ast.node.common.type;

import java.io.PrintStream;

import dev.civl.abc.ast.IF.ASTException;
import dev.civl.abc.ast.node.IF.ASTNode;
import dev.civl.abc.ast.node.IF.type.TypeNode;
import dev.civl.abc.token.IF.Source;

/**
 * Type node representing the type "$range".
 * 
 * @author siegel
 * 
 */
public class CommonRangeTypeNode extends CommonTypeNode {

	public CommonRangeTypeNode(Source source) {
		super(source, TypeNodeKind.RANGE);
	}

	@Override
	public TypeNode copy() {
		CommonRangeTypeNode result = new CommonRangeTypeNode(getSource());

		copyData(result);
		return result;
	}

	@Override
	protected void printBody(PrintStream out) {
		out.print("$range");
	}

	@Override
	public ASTNode setChild(int index, ASTNode child) {
		throw new ASTException(
				"CommonRangeTypeNode has no child, but saw index " + index);
	}
}

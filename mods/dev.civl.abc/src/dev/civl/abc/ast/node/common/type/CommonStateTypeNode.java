package dev.civl.abc.ast.node.common.type;

import java.io.PrintStream;

import dev.civl.abc.ast.IF.ASTException;
import dev.civl.abc.ast.node.IF.ASTNode;
import dev.civl.abc.ast.node.IF.type.TypeNode;
import dev.civl.abc.token.IF.Source;

/**
 * Type node representing the type "$state". This is used to give a state a
 * name. It is very much like a variable declaration and is treated as such.
 * 
 * "$state s;" is translated as a variable declaration of a variable named "s",
 * with type node an instances of this class.
 * 
 * @author ziqingluo
 * 
 */
public class CommonStateTypeNode extends CommonTypeNode {

	public CommonStateTypeNode(Source source) {
		super(source, TypeNodeKind.STATE);
	}

	@Override
	public TypeNode copy() {
		CommonStateTypeNode result = new CommonStateTypeNode(getSource());

		copyData(result);
		return result;
	}

	@Override
	protected void printBody(PrintStream out) {
		out.print("$state");
	}

	@Override
	public ASTNode setChild(int index, ASTNode child) {
		throw new ASTException(
				"CommonStateTypeNode has no child, but saw index " + index);
	}
}

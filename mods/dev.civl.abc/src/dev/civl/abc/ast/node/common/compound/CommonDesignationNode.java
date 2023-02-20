package dev.civl.abc.ast.node.common.compound;

import java.util.List;

import dev.civl.abc.ast.IF.ASTException;
import dev.civl.abc.ast.node.IF.ASTNode;
import dev.civl.abc.ast.node.IF.compound.DesignationNode;
import dev.civl.abc.ast.node.IF.compound.DesignatorNode;
import dev.civl.abc.ast.node.common.CommonSequenceNode;
import dev.civl.abc.token.IF.Source;

public class CommonDesignationNode extends CommonSequenceNode<DesignatorNode>
		implements
			DesignationNode {

	public CommonDesignationNode(Source source,
			List<DesignatorNode> childList) {
		super(source, "DesignatorList", childList);
	}

	@Override
	public DesignationNode copy() {
		return new CommonDesignationNode(getSource(), childListCopy());
	}

	@Override
	public ASTNode setChild(int index, ASTNode child) {
		if (!(child == null || child instanceof DesignatorNode))
			throw new ASTException(
					"Child of CommonDesignationNode must be a DesignatorNode, but saw "
							+ child + " with type " + child.nodeKind());
		return super.setChild(index, child);
	}
}

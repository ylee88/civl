package dev.civl.abc.ast.node.common.acsl;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import dev.civl.abc.ast.node.IF.acsl.ContractNode;
import dev.civl.abc.ast.node.IF.acsl.InsertTransformNode;
import dev.civl.abc.ast.node.IF.statement.BlockItemNode;
import dev.civl.abc.token.IF.Source;
import dev.civl.abc.token.IF.SyntaxException;

public class CommonInsertTransformNode extends CommonTransformNode
		implements
			InsertTransformNode {
	
	private List<BlockItemNode> nodesToInsert;
	private boolean insertAfter;

	public CommonInsertTransformNode(Source source,
			List<BlockItemNode> nodesToInsert, boolean insertAfter) {
		super(source);
		this.nodesToInsert = nodesToInsert;
		this.insertAfter = insertAfter;
	}

	@Override
	public List<BlockItemNode> transform(List<BlockItemNode> items)
			throws SyntaxException {
		List<BlockItemNode> result = new ArrayList<BlockItemNode>();
		if (!insertAfter) {
			result.addAll(nodesToInsert);
		}
		result.addAll(items);
		if (insertAfter) {
			result.addAll(nodesToInsert);
		}
		return result;
	}

	@Override
	public ContractNode copy() {
		List<BlockItemNode> nodesToInsertCopy = new ArrayList<BlockItemNode>();
		for (BlockItemNode node : nodesToInsert) {
			nodesToInsertCopy.add(node.copy());
		}
		return new CommonInsertTransformNode(this.getSource(), nodesToInsertCopy, insertAfter);
	}

	@Override
	protected void printBody(PrintStream out) {
		out.print("transform insert");
	}

}

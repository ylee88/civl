package dev.civl.abc.analysis.pointsTo.common;

import dev.civl.abc.analysis.pointsTo.IF.AssignExprIF;
import dev.civl.abc.analysis.pointsTo.IF.InvocationGraphNode;
import dev.civl.abc.analysis.pointsTo.IF.InvocationGraphNode.IGNodeKind;
import dev.civl.abc.analysis.pointsTo.IF.InvocationGraphNodeFactory;
import dev.civl.abc.ast.entity.IF.Function;

public class CommonInvocationGraphFactory implements InvocationGraphNodeFactory {

	@Override
	public InvocationGraphNode newNode(Function function,
			InvocationGraphNode parent, AssignExprIF returnTo,
			AssignExprIF... actualArgs) {
		InvocationGraphNode ancestor = parent;
		IGNodeKind kind = IGNodeKind.ORDINARY;

		// decide kind:
		while (ancestor != null) {
			if (ancestor.function() == function) {
				kind = IGNodeKind.APPROXIMATE;
				ancestor.markRecursive();
				break;
			}
			ancestor = ancestor.parent();
		}

		InvocationGraphNode newNode;

		if (kind != IGNodeKind.APPROXIMATE)
			newNode = new CommonInvocationGraphNode(parent, function, kind,
					returnTo, actualArgs);
		else
			newNode = new CommonInvocationGraphNode(parent, ancestor, function,
					kind, returnTo, actualArgs);
		if (parent != null)
			parent.addChild(newNode);
		return newNode;
	}
}

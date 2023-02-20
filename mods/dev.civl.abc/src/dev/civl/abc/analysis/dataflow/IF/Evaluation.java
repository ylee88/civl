package dev.civl.abc.analysis.dataflow.IF;

import java.util.Map;

import dev.civl.abc.ast.entity.IF.Entity;
import dev.civl.abc.ast.node.IF.ASTNode;

public interface Evaluation<E> {
	E evaluate(ASTNode e, Map<Entity, E> map, E top);

}

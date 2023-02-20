package dev.civl.abc.analysis.pointsTo.IF;

import dev.civl.abc.ast.type.IF.Field;

public interface AssignFieldExprIF extends AssignExprIF {

	AssignExprIF struct();

	Field field();
}

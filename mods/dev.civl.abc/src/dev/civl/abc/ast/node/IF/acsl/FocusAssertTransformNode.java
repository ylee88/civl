package dev.civl.abc.ast.node.IF.acsl;

import java.util.List;

import dev.civl.abc.analysis.entity.FocusAnalysisData;

public interface FocusAssertTransformNode extends FocusTransformNode {
	List<String> getFocusTags();
}

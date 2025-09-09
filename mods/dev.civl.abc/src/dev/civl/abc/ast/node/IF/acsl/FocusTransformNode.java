package dev.civl.abc.ast.node.IF.acsl;

import dev.civl.abc.analysis.entity.FocusAnalysisData;

public interface FocusTransformNode extends TransformNode {
	public enum FocusKind {
		LOOP,
		ASSERT
	};
	
	public void setFocusAnalysisData(FocusAnalysisData analysisData);
	
	public FocusKind getFocusKind();
}

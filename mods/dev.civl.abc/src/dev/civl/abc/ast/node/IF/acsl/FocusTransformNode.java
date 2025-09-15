package dev.civl.abc.ast.node.IF.acsl;

import dev.civl.abc.analysis.IF.FocusAnalysisData;

public interface FocusTransformNode extends TransformNode {
	public enum FocusKind {
		LOOP,
		ASSERT,
		ORDERED
	};
	
	public void setFocusAnalysisData(FocusAnalysisData analysisData);
	
	public FocusKind getFocusKind();
}

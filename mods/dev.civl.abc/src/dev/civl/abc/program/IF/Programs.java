package dev.civl.abc.program.IF;

import dev.civl.abc.analysis.IF.Analyzer;
import dev.civl.abc.ast.IF.ASTFactory;
import dev.civl.abc.program.common.CommonProgramFactory;

public class Programs {

	public static ProgramFactory newProgramFactory(ASTFactory factory,
			Analyzer standardAnalyzer) {
		return new CommonProgramFactory(factory, standardAnalyzer);
	}

}

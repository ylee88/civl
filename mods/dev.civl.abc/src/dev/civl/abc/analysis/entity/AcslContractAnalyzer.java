package dev.civl.abc.analysis.entity;

import dev.civl.abc.ast.conversion.IF.ConversionFactory;
import dev.civl.abc.ast.entity.IF.Function;
import dev.civl.abc.ast.node.IF.SequenceNode;
import dev.civl.abc.ast.node.IF.acsl.ContractNode;
import dev.civl.abc.token.IF.SyntaxException;

public class AcslContractAnalyzer {
	/**
	 * The entity analyzer controlling this declaration analyzer.
	 */
	private EntityAnalyzer entityAnalyzer;
	private ConversionFactory conversionFactory;

	AcslContractAnalyzer(EntityAnalyzer entityAnalyzer,
			ConversionFactory conversionFactory) {
		this.entityAnalyzer = entityAnalyzer;
		this.conversionFactory = conversionFactory;
	}

	void processContractNodes(SequenceNode<ContractNode> contract,
			Function result) throws SyntaxException {
		AcslContractAnalyzerWorker worker = new AcslContractAnalyzerWorker(
				this.entityAnalyzer, conversionFactory);

		worker.processContractNodes(contract, result);
	}

	void processLoopContractNodes(SequenceNode<ContractNode> loopContracts)
			throws SyntaxException {
		AcslContractAnalyzerWorker worker = new AcslContractAnalyzerWorker(
				this.entityAnalyzer, conversionFactory);

		worker.processLoopContractNodes(loopContracts);
	}
}

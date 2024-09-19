package dev.civl.abc.program.common;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

import dev.civl.abc.analysis.IF.Analyzer;
import dev.civl.abc.ast.IF.AST;
import dev.civl.abc.ast.IF.ASTFactory;
import dev.civl.abc.ast.node.IF.ASTNode;
import dev.civl.abc.ast.node.IF.ASTNode.NodeKind;
import dev.civl.abc.program.IF.Program;
import dev.civl.abc.token.IF.SyntaxException;
import dev.civl.abc.token.IF.TokenFactory;
import dev.civl.abc.transform.IF.Transform;
import dev.civl.abc.transform.IF.Transformer;

public class CommonProgram implements Program {

	private Analyzer standardAnalyzer;

	private AST ast;

	public CommonProgram(Analyzer standardAnalyzer, AST ast)
			throws SyntaxException {
		this.standardAnalyzer = standardAnalyzer;
		this.ast = ast;
		standardAnalyzer.clear(ast);
		standardAnalyzer.analyze(ast);
	}

	@Override
	public AST getAST() {
		return ast;
	}

	@Override
	public void print(PrintStream out) {
		ast.print(out);
	}

	@Override
	public void prettyPrint(PrintStream out) {
		ast.prettyPrint(out, false);
	}

	@Override
	public void printSymbolTable(PrintStream out) {
		ast.getRootNode().getScope().print(out);
	}

	@Override
	public TokenFactory getTokenFactory() {
		return ast.getASTFactory().getTokenFactory();
	}

	@Override
	public void apply(Transformer transformer) throws SyntaxException {
		ast = transformer.transform(ast);
		standardAnalyzer.clear(ast);
		//ast.prettyPrint(System.out, false);
		standardAnalyzer.analyze(ast);
	}

	@Override
	public void applyTransformer(String code) throws SyntaxException {
		Transformer transformer = Transform.newTransformer(code,
				ast.getASTFactory());

		apply(transformer);
	}

	@Override
	public void apply(Iterable<Transformer> transformers)
			throws SyntaxException {
		for (Transformer transformer : transformers) {
			ast = transformer.transform(ast);
			standardAnalyzer.clear(ast);
			standardAnalyzer.analyze(ast);
		}
	}

	@Override
	public void applyTransformers(Iterable<String> codes)
			throws SyntaxException {
		List<Transformer> transformers = new LinkedList<>();
		ASTFactory astFactory = ast.getASTFactory();

		for (String code : codes)
			transformers.add(Transform.newTransformer(code, astFactory));
		apply(transformers);
	}

	@Override
	public boolean hasOmpPragma() {
		return this.hasOmpPragmaInASTNode(ast.getRootNode());
	}

	private boolean hasOmpPragmaInASTNode(ASTNode node) {
		if (node.nodeKind() == NodeKind.OMP_NODE) {
			// PragmaNode pragmaNode = (PragmaNode) node;
			// if (pragmaNode.getPragmaIdentifier().name().equals("omp"))
			return true;
		} else {
			for (ASTNode child : node.children()) {
				if (child != null)
					if (this.hasOmpPragmaInASTNode(child))
						return true;
			}
		}
		return false;
	}
}

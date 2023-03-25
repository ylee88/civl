package dev.civl.abc.front.c.astgen;

import dev.civl.abc.ast.IF.AST;
import dev.civl.abc.ast.IF.ASTFactory;
import dev.civl.abc.ast.node.IF.SequenceNode;
import dev.civl.abc.ast.node.IF.statement.BlockItemNode;
import dev.civl.abc.config.IF.Configuration;
import dev.civl.abc.front.IF.ASTBuilder;
import dev.civl.abc.front.IF.ParseTree;
import dev.civl.abc.front.c.ptree.CParseTree;
import dev.civl.abc.front.common.astgen.ASTBuilderWorker;
import dev.civl.abc.front.common.astgen.PragmaFactory;
import dev.civl.abc.token.IF.SyntaxException;

public class CASTBuilder implements ASTBuilder {

	private ASTFactory astFactory;

	private PragmaFactory pragmaFactory;

	private Configuration config;

	public CASTBuilder(Configuration config, ASTFactory astFactory) {
		this.astFactory = astFactory;
		this.config = config;
		pragmaFactory = new PragmaFactory(this);
	}

	@Override
	public AST getTranslationUnit(ParseTree tree) throws SyntaxException {
		ASTBuilderWorker worker = getWorker(tree);
		SequenceNode<BlockItemNode> rootNode = worker.translateRoot();
		AST ast = astFactory.newAST(rootNode,
				((CParseTree) tree).getSourceFiles(), false);

		return ast;
	}

	public CASTBuilderWorker getWorker(ParseTree tree) {
		return new CASTBuilderWorker(config, (CParseTree) tree, astFactory,
				pragmaFactory);
	}

	@Override
	public ASTFactory getASTFactory() {
		return astFactory;
	}
}

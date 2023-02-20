package dev.civl.abc.front.c.astgen;

import java.util.List;

import dev.civl.abc.ast.node.IF.ASTNode;
import dev.civl.abc.ast.node.IF.NodeFactory;
import dev.civl.abc.ast.node.IF.PragmaNode;
import dev.civl.abc.ast.node.IF.statement.BlockItemNode;
import dev.civl.abc.front.IF.Front;
import dev.civl.abc.front.IF.ParseException;
import dev.civl.abc.front.IF.ParseTree;
import dev.civl.abc.front.c.parse.CParser;
import dev.civl.abc.front.c.parse.CParser.RuleKind;
import dev.civl.abc.front.common.astgen.PragmaHandler;
import dev.civl.abc.front.common.astgen.SimpleScope;
import dev.civl.abc.token.IF.CivlcTokenSource;
import dev.civl.abc.token.IF.Source;
import dev.civl.abc.token.IF.SyntaxException;

public class CIVLPragmaHandler extends PragmaHandler {

	private NodeFactory nodeFactory;

	private CParser parser;

	private ParseTree parseTree;

	CASTBuilderWorker worker;

	public CIVLPragmaHandler(CASTBuilder builder, ParseTree parseTree) {
		this.nodeFactory = builder.getASTFactory().getNodeFactory();
		this.parseTree = parseTree;
		this.worker = builder.getWorker(parseTree);
		this.parser = (CParser) Front.newParser(parseTree.getLanguage());
	}

	@Override
	public EntityKind getEntityKind() {
		return EntityKind.PRAGMA_HANDLER;
	}

	@Override
	public String getName() {
		return "CIVL";
	}

	@Override
	public ASTNode processPragmaNode(PragmaNode pragmaNode, SimpleScope scope)
			throws SyntaxException, ParseException {
		CivlcTokenSource tokens = pragmaNode.newTokenSource();
		Source source = pragmaNode.getSource();
		ParseTree pragmaTree = parser.parse(RuleKind.BLOCK_ITEM, tokens,
				scope.getScopeSymbolStack());
		List<BlockItemNode> blockList = worker.translateBlockItem(
				pragmaTree.getRoot(), scope);

		return blockList.size() == 1 ? blockList.get(0) : nodeFactory
				.newCompoundStatementNode(source, blockList);
	}

	@Override
	public ParseTree getParseTree() {
		return parseTree;
	}

}

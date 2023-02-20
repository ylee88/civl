package dev.civl.abc.front.common.astgen;

import dev.civl.abc.ast.node.IF.ASTNode;
import dev.civl.abc.ast.node.IF.PragmaNode;
import dev.civl.abc.front.IF.ParseTree;

public class TrivialPragmaHandler extends PragmaHandler {

	private String name;

	private ParseTree parseTree;

	public TrivialPragmaHandler(String name, ParseTree parseTree) {
		this.name = name;
		this.parseTree = parseTree;
	}

	@Override
	public EntityKind getEntityKind() {
		return EntityKind.PRAGMA_HANDLER;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public ASTNode processPragmaNode(PragmaNode pragmaNode, SimpleScope scope) {
		return pragmaNode;
	}

	@Override
	public ParseTree getParseTree() {
		return parseTree;
	}

}

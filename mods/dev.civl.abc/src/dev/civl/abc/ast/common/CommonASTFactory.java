package dev.civl.abc.ast.common;

import java.io.File;
import java.util.Collection;

import dev.civl.abc.ast.IF.AST;
import dev.civl.abc.ast.IF.ASTFactory;
import dev.civl.abc.ast.node.IF.NodeFactory;
import dev.civl.abc.ast.node.IF.SequenceNode;
import dev.civl.abc.ast.node.IF.expression.StringLiteralNode;
import dev.civl.abc.ast.node.IF.statement.BlockItemNode;
import dev.civl.abc.ast.type.IF.TypeFactory;
import dev.civl.abc.config.IF.Configurations.Language;
import dev.civl.abc.err.IF.ABCException;
import dev.civl.abc.front.IF.CivlcTokenConstant;
import dev.civl.abc.main.ABCExecutor;
import dev.civl.abc.main.TranslationTask;
import dev.civl.abc.main.TranslationTask.TranslationStage;
import dev.civl.abc.main.UnitTask;
import dev.civl.abc.token.IF.CivlcToken;
import dev.civl.abc.token.IF.Formation;
import dev.civl.abc.token.IF.Source;
import dev.civl.abc.token.IF.SourceFile;
import dev.civl.abc.token.IF.StringToken;
import dev.civl.abc.token.IF.SyntaxException;
import dev.civl.abc.token.IF.TokenFactory;
import dev.civl.abc.token.IF.CivlcToken.TokenVocabulary;

//TODO improve the printing of variable declarations when array type is involved.

public class CommonASTFactory implements ASTFactory {

	private NodeFactory nodeFactory;

	private TokenFactory tokenFactory;

	private TypeFactory typeFactory;

	public CommonASTFactory(NodeFactory nodeFactory, TokenFactory tokenFactory,
			TypeFactory typeFactory) {
		this.nodeFactory = nodeFactory;
		this.tokenFactory = tokenFactory;
		this.typeFactory = typeFactory;
	}

	@Override
	public NodeFactory getNodeFactory() {
		return nodeFactory;
	}

	@Override
	public TokenFactory getTokenFactory() {
		return tokenFactory;
	}

	@Override
	public TypeFactory getTypeFactory() {
		return typeFactory;
	}

	@Override
	public AST newAST(SequenceNode<BlockItemNode> root,
			Collection<SourceFile> sourceFiles, boolean isWholeprogram)
			throws SyntaxException {
		AST unit = new CommonAST(this, root, false, sourceFiles,
				isWholeprogram);

		// do some preparation?
		return unit;
	}

	@Override
	public AST getASTofLibrary(File file, Language language)
			throws ABCException {
		UnitTask task = new UnitTask(new File[]{file});

		task.setLanguage(language);

		TranslationTask translation = new TranslationTask(new UnitTask[]{task});

		translation.setStage(TranslationStage.GENERATE_ASTS);

		ABCExecutor executor = new ABCExecutor(translation);

		executor.execute();
		return executor.getAST(0);
	}
	
	@Override
	public StringLiteralNode newStringLiteralNode(Formation formation,
			String representation) throws SyntaxException {
		CivlcToken token = tokenFactory.newCivlcToken(
				CivlcTokenConstant.STRING_LITERAL, representation, formation,
				TokenVocabulary.DUMMY);
		StringToken stringToken = tokenFactory.newStringToken(token);
		Source source = tokenFactory.newSource(stringToken);
		StringLiteralNode result = nodeFactory.newStringLiteralNode(source,
				representation, stringToken.getStringLiteral());
		return result;
	}
}

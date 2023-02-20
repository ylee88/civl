package dev.civl.abc.transform.IF;

import dev.civl.abc.ast.IF.ASTFactory;
import dev.civl.abc.ast.node.IF.NodeFactory;
import dev.civl.abc.ast.node.IF.expression.StringLiteralNode;
import dev.civl.abc.config.IF.Configuration;
import dev.civl.abc.front.IF.CivlcTokenConstant;
import dev.civl.abc.token.IF.CivlcToken;
import dev.civl.abc.token.IF.CivlcToken.TokenVocabulary;
import dev.civl.abc.token.IF.Formation;
import dev.civl.abc.token.IF.Source;
import dev.civl.abc.token.IF.StringToken;
import dev.civl.abc.token.IF.SyntaxException;
import dev.civl.abc.token.IF.TokenFactory;

/**
 * A very basic partial implementation of {@link Transformer}. Implements the
 * book-keeping methods {@link #getCode()}, {@link #getShortDescription()},
 * {@link #toString()}, and provides similar fields. Most implementations of
 * {@link Transformer} can extend this class.
 * 
 * @author siegel
 * 
 */
public abstract class BaseTransformer implements Transformer {

	protected String code;

	protected String longName;

	protected String shortDescription;

	protected ASTFactory astFactory;

	protected NodeFactory nodeFactory;

	protected BaseTransformer(String code, String longName,
			String shortDescription, ASTFactory astFactory) {
		this.code = code;
		this.longName = longName;
		this.shortDescription = shortDescription;
		this.astFactory = astFactory;
		this.nodeFactory = astFactory.getNodeFactory();
	}

	/**
	 * gets the configuration associated with this translation task
	 * 
	 * @return the configuration associated with this translation task
	 */
	protected Configuration getConfiguration() {
		return this.nodeFactory.configuration();
	}

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public String getShortDescription() {
		return shortDescription;
	}

	@Override
	public String toString() {
		return longName;
	}

	@Override
	public StringLiteralNode newStringLiteralNode(String method,
			String representation) throws SyntaxException {
		TokenFactory tokenFactory = astFactory.getTokenFactory();
		Formation formation = tokenFactory.newTransformFormation(longName,
				method);
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

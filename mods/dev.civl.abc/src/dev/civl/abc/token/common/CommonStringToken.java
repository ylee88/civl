package dev.civl.abc.token.common;

import dev.civl.abc.token.IF.CivlcToken;
import dev.civl.abc.token.IF.Formation;
import dev.civl.abc.token.IF.StringLiteral;
import dev.civl.abc.token.IF.StringToken;

public class CommonStringToken extends CommonCivlcToken implements StringToken {

	/**
	 * Eclipse made me do it.
	 */
	private static final long serialVersionUID = 6839260551000953066L;

	private StringLiteral literal;

	public CommonStringToken(int type, Formation formation,
			StringLiteral literal, TokenVocabulary tokenVocab) {
		super(type, literal.toString(), formation, tokenVocab);
		this.literal = literal;
	}

	public CommonStringToken(CivlcToken token, Formation formation,
			StringLiteral data) {
		super(token, formation, token.getTokenVocab());
		this.literal = data;
	}

	@Override
	public StringLiteral getStringLiteral() {
		return literal;
	}

}

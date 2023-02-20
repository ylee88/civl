package dev.civl.abc.front.c.ptree;

import org.antlr.runtime.tree.CommonTree;

import dev.civl.abc.config.IF.Configurations.Language;
import dev.civl.abc.front.c.parse.CParser.RuleKind;
import dev.civl.abc.front.common.ptree.CommonParseTree;
import dev.civl.abc.token.IF.CivlcTokenSource;

public class CParseTree extends CommonParseTree {

	private RuleKind kind;

	public CParseTree(Language language, RuleKind kind,
			CivlcTokenSource tokenSource, CommonTree root) {
		super(language, tokenSource, root);
		this.kind = kind;
	}

	/**
	 * What kind of parse tree is this?
	 */
	public RuleKind getKind() {
		return kind;
	}
}

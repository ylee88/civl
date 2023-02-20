package dev.civl.abc.front.fortran.astgen;

import dev.civl.abc.ast.IF.AST;
import dev.civl.abc.ast.IF.ASTFactory;
import dev.civl.abc.config.IF.Configuration;
import dev.civl.abc.front.IF.ASTBuilder;
import dev.civl.abc.front.IF.ParseTree;
import dev.civl.abc.front.common.astgen.PragmaFactory;
import dev.civl.abc.front.fortran.ptree.MFTree;
import dev.civl.abc.token.IF.SyntaxException;

public class MFASTBuilder implements ASTBuilder {

	private ASTFactory astFactory;

	private PragmaFactory pragmaFactory;

	private Configuration config;

	private String filePath;


	public MFASTBuilder(Configuration configuration,
			ASTFactory astFactory) {
		this.config = configuration;
		this.astFactory = astFactory;
		pragmaFactory = new PragmaFactory(this);
		this.filePath = "";
	}
	
	public MFASTBuilder(Configuration configuration,
			ASTFactory astFactory, String filePath) {
		this.config = configuration;
		this.astFactory = astFactory;
		pragmaFactory = new PragmaFactory(this);
		this.filePath = filePath;
	}

	public MFASTBuilderWorker getWorker(MFTree tree) {
		return new MFASTBuilderWorker(config, tree, astFactory, filePath,
				pragmaFactory);
	}

	@Override
	public AST getTranslationUnit(ParseTree tree) throws SyntaxException {
		MFTree fTree = (MFTree) tree;
		MFASTBuilderWorker worker = new MFASTBuilderWorker(config,
				fTree, astFactory, filePath, pragmaFactory);

		return worker.generateAST();
	}

	@Override
	public ASTFactory getASTFactory() {
		return this.astFactory;
	}

	@Override
	public PragmaFactory getPragmaFactory() {
		return null; // No progma for Fortran
	}
}

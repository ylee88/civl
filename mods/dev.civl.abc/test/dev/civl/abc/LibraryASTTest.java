package dev.civl.abc;

import org.junit.Test;

import dev.civl.abc.ast.IF.AST;
import dev.civl.abc.ast.IF.ASTFactory;
import dev.civl.abc.ast.IF.ASTs;
import dev.civl.abc.ast.node.IF.NodeFactory;
import dev.civl.abc.ast.node.IF.Nodes;
import dev.civl.abc.ast.type.IF.TypeFactory;
import dev.civl.abc.ast.type.IF.Types;
import dev.civl.abc.ast.value.IF.ValueFactory;
import dev.civl.abc.ast.value.IF.Values;
import dev.civl.abc.config.IF.Configuration;
import dev.civl.abc.config.IF.Configurations;
import dev.civl.abc.config.IF.Configurations.Language;
import dev.civl.abc.front.IF.ASTBuilder;
import dev.civl.abc.front.IF.Front;
import dev.civl.abc.front.IF.ParseException;
import dev.civl.abc.front.IF.Parser;
import dev.civl.abc.front.IF.Preprocessor;
import dev.civl.abc.front.IF.PreprocessorException;
import dev.civl.abc.front.common.astgen.LibraryASTFactory;
import dev.civl.abc.token.IF.FileIndexer;
import dev.civl.abc.token.IF.SyntaxException;
import dev.civl.abc.token.IF.TokenFactory;
import dev.civl.abc.token.IF.Tokens;

public class LibraryASTTest {

	private boolean debug = false;

	@Test
	public void stdio()
			throws PreprocessorException, ParseException, SyntaxException {
		Configuration configuration = Configurations.newMinimalConfiguration();
		TokenFactory tokenFactory = Tokens.newTokenFactory();
		FileIndexer indexer = tokenFactory.newFileIndexer();
		Language language = Language.C;
		Preprocessor preprocessor = Front.newPreprocessor(language,
				configuration, indexer, tokenFactory);
		Parser parser = Front.newParser(language);
		TypeFactory typeFactory = Types.newTypeFactory();
		ValueFactory valueFactory = Values.newValueFactory(configuration,
				typeFactory);
		NodeFactory nodeFactory = Nodes.newNodeFactory(configuration,
				typeFactory, valueFactory);
		ASTFactory astFactory = ASTs.newASTFactory(nodeFactory, tokenFactory,
				typeFactory);
		ASTBuilder astBuilder = Front.newASTBuilder(language, configuration,
				astFactory);
		LibraryASTFactory libFac = new LibraryASTFactory(preprocessor, parser,
				astBuilder);

		AST ast = libFac.getASTofLibrary(LibraryASTFactory.STDLIB);

		if (debug)
			ast.prettyPrint(System.out, false);
	}
}

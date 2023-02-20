package dev.civl.abc.analysis.common;

import dev.civl.abc.analysis.IF.Analyzer;
import dev.civl.abc.analysis.entity.EntityAnalyzer;
import dev.civl.abc.ast.IF.AST;
import dev.civl.abc.ast.IF.ASTFactory;
import dev.civl.abc.ast.conversion.IF.ConversionFactory;
import dev.civl.abc.ast.entity.IF.EntityFactory;
import dev.civl.abc.config.IF.Configuration;
import dev.civl.abc.config.IF.Configurations.Language;
import dev.civl.abc.token.IF.SyntaxException;

/**
 * Performs all standard analyses of a Translation Unit: determines and sets
 * Scopes, Types, and Entities.
 * 
 * @author siegel
 * 
 */
public class StandardAnalyzer implements Analyzer {

	private ScopeAnalyzer scopeAnalyzer;

	private EntityAnalyzer entityAnalyzer;

	private CallAnalyzer callAnalyzer;

	public StandardAnalyzer(Language language, Configuration configuration,
			ASTFactory astFactory, EntityFactory entityFactory,
			ConversionFactory conversionFactory) {
		scopeAnalyzer = new ScopeAnalyzer(entityFactory);
		entityAnalyzer = new EntityAnalyzer(language, configuration,
				astFactory, entityFactory, conversionFactory, scopeAnalyzer);
		callAnalyzer = new CallAnalyzer();
	}

	@Override
	public void analyze(AST unit) throws SyntaxException {
		scopeAnalyzer.analyze(unit);
		entityAnalyzer.analyze(unit);
		callAnalyzer.analyze(unit);
	}

	@Override
	public void clear(AST unit) {
		scopeAnalyzer.clear(unit);
		entityAnalyzer.clear(unit);
		callAnalyzer.clear(unit);
	}

}

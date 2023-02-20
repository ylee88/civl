package dev.civl.abc;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import dev.civl.abc.analysis.common.CallAnalyzer;
import dev.civl.abc.analysis.dataflow.ControlFlowAnalysis;
import dev.civl.abc.analysis.dataflow.DominatorAnalysis;
import dev.civl.abc.analysis.gsa.GatedSingleAssignment;
import dev.civl.abc.analysis.gsa.GatingExpression;
import dev.civl.abc.analysis.gsa.GatingExpressionFactory;
import dev.civl.abc.ast.IF.AST;
import dev.civl.abc.ast.entity.IF.Function;
import dev.civl.abc.ast.node.IF.ASTNode;
import dev.civl.abc.config.IF.Configuration;
import dev.civl.abc.config.IF.Configurations;
import dev.civl.abc.config.IF.Configurations.Language;
import dev.civl.abc.front.IF.ParseException;
import dev.civl.abc.front.IF.PreprocessorException;
import dev.civl.abc.main.FrontEnd;
import dev.civl.abc.token.IF.SyntaxException;

/**
 * Checks a number of simple C programs to make sure they pass on the control
 * flow graph construction analyzer.
 * 
 * @author dwyer
 * 
 */
@Ignore
public class GatingAssignmentTest {

	/**
	 * Turn on a lot of output for debugging? Set this to true only in your
	 * local copy. Be sure to set it back to false before committing!
	 */
	private static boolean debug = true;

	private static File root = new File(new File("examples"), "c");

	private static Configuration config = Configurations
			.newMinimalConfiguration();

	private static FrontEnd fe = new FrontEnd(config);

	private static ControlFlowAnalysis cfa;
	private static DominatorAnalysis dom;
	private static GatedSingleAssignment gsa;

	private AST ast;

	@Before
	public void setUp() throws Exception {
		cfa = ControlFlowAnalysis.getInstance();
		dom = DominatorAnalysis.getInstance();
		gsa = GatedSingleAssignment.getInstance();

	}

	@After
	public void tearDown() throws Exception {
		cfa.clear();
		dom.clear();
		gsa.clear();
	}

	private AST getAST(File file)
			throws ParseException, SyntaxException, PreprocessorException {
		AST ast = fe.compile(new File[] { file }, Language.C, new File[0],
				new File[0], new HashMap<String, String>());
		return ast;
	}

	@Test
	public void kicktires() throws ParseException, SyntaxException, PreprocessorException {
		File file = new File(root, "ifthen.c");
		ast = getAST(file);

		cfa.analyze(ast);

		if (debug) {
			cfa.printControlFlowGraph(ast);
		}

		Map<ASTNode, GatingExpression> geMap = new HashMap<ASTNode, GatingExpression>();

		for (Function f : CallAnalyzer.functions(ast)) {
			dom.analyze(f);
			gsa.analyze(f);
			
			/*
			System.out.println("Dominator Tree for "+f);
			dom.printDominatorTree(f);
			
			System.out.println("Subtree roots in the dominator tree:");
			for (ASTNode n1 : cfa.allNodes(f)) {
				for (ASTNode n2 : cfa.allNodes(f)) {
					System.out.println("Subtree root for "+n1+" below "+n2);
					System.out.println("   -> "+gsa.subroot(n1,n2));
				}
			}
			*/
			
			GatingExpressionFactory gef = new GatingExpressionFactory(cfa);
			
			for (ASTNode n : cfa.allNodes(f)) {
				if (cfa.successors(n) != null) {
					for (ASTNode s : cfa.successors(n)) {
						geMap.put(s, gef.makeGatingExpression(n, s));
					}
				}
			}
			
			for (ASTNode n : cfa.allNodes(f)) {
				if (cfa.predecessors(n) != null && cfa.predecessors(n).size() > 1) {
					// merge
					GatingExpression current = new GatingExpression(false);
					for (ASTNode p : cfa.predecessors(n)) {
						current = current.or(current, geMap.get(p));
					}
					geMap.put(n, current);
				} else {
					if (cfa.predecessors(n) != null) {
						for (ASTNode p : cfa.predecessors(n)) {
							if (geMap.get(p) != null) {
								GatingExpression sGExpr = geMap.get(p);
								geMap.put(n, sGExpr.concat(sGExpr, geMap.get(n)));
							}
						}
					}
				}
			}
			
		}
		
		System.out.println("Gating Expressions for AST:");
		for (ASTNode n : geMap.keySet()) {
			System.out.println("-----------------");
			System.out.println("  node:"+n);
			System.out.println("  gexpr:"+geMap.get(n));
		}
		System.out.println("-----------------");


	}

}

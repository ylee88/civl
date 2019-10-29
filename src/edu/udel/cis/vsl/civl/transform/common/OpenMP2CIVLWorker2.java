package edu.udel.cis.vsl.civl.transform.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import edu.udel.cis.vsl.abc.ast.IF.AST;
import edu.udel.cis.vsl.abc.ast.IF.ASTFactory;
import edu.udel.cis.vsl.abc.ast.entity.IF.Variable;
import edu.udel.cis.vsl.abc.ast.node.IF.ASTNode;
import edu.udel.cis.vsl.abc.ast.node.IF.ASTNode.NodeKind;
import edu.udel.cis.vsl.abc.ast.node.IF.IdentifierNode;
import edu.udel.cis.vsl.abc.ast.node.IF.NodePredicate;
import edu.udel.cis.vsl.abc.ast.node.IF.PairNode;
import edu.udel.cis.vsl.abc.ast.node.IF.SequenceNode;
import edu.udel.cis.vsl.abc.ast.node.IF.compound.CompoundInitializerNode;
import edu.udel.cis.vsl.abc.ast.node.IF.compound.DesignationNode;
import edu.udel.cis.vsl.abc.ast.node.IF.declaration.InitializerNode;
import edu.udel.cis.vsl.abc.ast.node.IF.declaration.VariableDeclarationNode;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.CastNode;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.ExpressionNode;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.FunctionCallNode;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.IdentifierExpressionNode;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.IntegerConstantNode;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.OperatorNode;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.OperatorNode.Operator;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.RegularRangeNode;
import edu.udel.cis.vsl.abc.ast.node.IF.omp.OmpAtomicNode;
import edu.udel.cis.vsl.abc.ast.node.IF.omp.OmpDeclarativeNode;
import edu.udel.cis.vsl.abc.ast.node.IF.omp.OmpExecutableNode;
import edu.udel.cis.vsl.abc.ast.node.IF.omp.OmpForNode;
import edu.udel.cis.vsl.abc.ast.node.IF.omp.OmpNode;
import edu.udel.cis.vsl.abc.ast.node.IF.omp.OmpParallelNode;
import edu.udel.cis.vsl.abc.ast.node.IF.omp.OmpReductionNode;
import edu.udel.cis.vsl.abc.ast.node.IF.omp.OmpReductionNode.OmpReductionOperator;
import edu.udel.cis.vsl.abc.ast.node.IF.omp.OmpSymbolReductionNode;
import edu.udel.cis.vsl.abc.ast.node.IF.omp.OmpSyncNode;
import edu.udel.cis.vsl.abc.ast.node.IF.omp.OmpWorksharingNode;
import edu.udel.cis.vsl.abc.ast.node.IF.statement.BlockItemNode;
import edu.udel.cis.vsl.abc.ast.node.IF.statement.CivlForNode;
import edu.udel.cis.vsl.abc.ast.node.IF.statement.CompoundStatementNode;
import edu.udel.cis.vsl.abc.ast.node.IF.statement.DeclarationListNode;
import edu.udel.cis.vsl.abc.ast.node.IF.statement.ExpressionStatementNode;
import edu.udel.cis.vsl.abc.ast.node.IF.statement.ForLoopInitializerNode;
import edu.udel.cis.vsl.abc.ast.node.IF.statement.ForLoopNode;
import edu.udel.cis.vsl.abc.ast.node.IF.statement.StatementNode;
import edu.udel.cis.vsl.abc.ast.node.IF.type.TypeNode;
import edu.udel.cis.vsl.abc.ast.type.IF.StandardBasicType.BasicTypeKind;
import edu.udel.cis.vsl.abc.front.IF.CivlcTokenConstant;
import edu.udel.cis.vsl.abc.token.IF.Source;
import edu.udel.cis.vsl.abc.token.IF.SyntaxException;
import edu.udel.cis.vsl.civl.config.IF.CIVLConfiguration;
import edu.udel.cis.vsl.civl.config.IF.CIVLConstants;
import edu.udel.cis.vsl.civl.model.IF.CIVLSyntaxException;
import edu.udel.cis.vsl.civl.model.IF.CIVLUnimplementedFeatureException;
import edu.udel.cis.vsl.civl.transform.IF.OpenMP2CIVLTransformer;
import edu.udel.cis.vsl.civl.transform.common.OmpRegion.OmpRgnKind;
import edu.udel.cis.vsl.civl.util.IF.Triple;

/**
 * See
 * 
 * https://vsl.cis.udel.edu/trac/civl/wiki/OpenMPTransformation
 * 
 * for documentation on this transformation.
 * 
 *
 * @author wuwenhao (wuwenhao@udel.edu)
 */
public class OpenMP2CIVLWorker2 extends BaseWorker {
	// Fields
	/**
	 * The source information used by all nodes created by this OpenMP-to-CIVL
	 * transformer.
	 */
	static private final String SRC_INFO = "OpenMP2CIVLWorker2";

	/* Specific numbers used in this transformer */
	static private final int INDEX_PVT_DECLS = 0;
	static private final int INDEX_TMP_DECLS = 1;
	static private final int INDEX_RDC_INITS = 0;
	static private final int INDEX_RDC_COMBS = 1;
	/* OpenMP variable identifiers used */
	// variable identifier prefix
	static private final String _OMP_ = "_omp_";
	static private final String FIRSTPRIVATE_ = _OMP_ + "fstpvt_";
	static private final String REDUCTION_ = _OMP_ + "reduction_";
	// commonly used variable identifiers
	static private final String DOM = _OMP_ + "dom";
	static private final String GTEAM = _OMP_ + "gteam";
	static private final String RANGE = _OMP_ + "range";
	static private final String NTHREADS = _OMP_ + "nthreads";
	static private final String NUM_THREADS = _OMP_ + "num_threads";
	/** CIVL input variable for the maximum number of OpenMP threads */
	static private final String TEAM = _OMP_ + "team";
	static private final String THREAD_MAX = _OMP_ + "thread_max";
	static private final String THREAD_RANGE = _OMP_ + "thread_range";
	static private final String TID = _OMP_ + "tid";
	// Construct loops
	static private final String DOM_LOOP = _OMP_ + "loop_domain";
	static private final String LOOP_DIST = _OMP_ + "loop_dist";
	// Construct sections
	static private final String SECTIONS_DIST = _OMP_ + "sections_dist";
	/** The variable name representing the OpenMP section block id */
	static private final String SID = _OMP_ + "sid";
	// Construct single
	static private final String SINGLE_DIST = _OMP_ + "single_dist";
	// clauses

	/* OpenMP function identifier */
	static private final String OMP_GET_MAX_THREADS = "omp_get_max_threads";
	static private final String OMP_GET_NUM_PROCS = "omp_get_num_procs";
	static private final String OMP_GET_NUM_THREADS = "omp_get_num_threads";
	static private final String OMP_GET_THREAD_NUM = "omp_get_thread_num";
	static private final String OMP_SET_NUM_THREADS = "omp_set_num_threads";
	/* CIVL OpenMP verification helper function identifiers */
	static private final String LOCAL_START = "$local_start";
	static private final String LOCAL_END = "$local_end";
	static private final String OMP_ARRIVE_SECTIONS = "$omp_arrive_sections";
	static private final String OMP_ARRIVE_SINGLE = "$omp_arrive_single";
	static private final String OMP_BARRIER_AND_FLUSH = "$omp_barrier_and_flush";
	static private final String OMP_GTEAM_CREATE = "$omp_gteam_create";
	static private final String OMP_GTEAM_DESTROY = "$omp_gteam_destroy";
	static private final String OMP_REDUCTION_COMBINE = "$omp_reduction_combine";
	static private final String OMP_TEAM_CREATE = "$omp_team_create";
	static private final String OMP_TEAM_DESTROY = "$omp_team_destroy";
	static private final String READ_SET_POP = "$read_set_pop";
	static private final String READ_SET_PUSH = "$read_set_push";
	static private final String WRITE_SET_POP = "$write_set_pop";
	static private final String WRITE_SET_PUSH = "$write_set_push";

	Map<String, VariableDeclarationNode> reductionId2TempDecls;

	static private final NodePredicate PREDICATE_BARRIER_AND_FLUSH = new NodePredicate() {
		@Override
		public boolean holds(ASTNode node) {
			if (node instanceof ExpressionStatementNode) {
				ExpressionNode expr = ((ExpressionStatementNode) node)
						.getExpression();

				if (expr instanceof FunctionCallNode) {
					ExpressionNode func = ((FunctionCallNode) expr)
							.getFunction();

					if (func instanceof IdentifierExpressionNode)
						return ((IdentifierExpressionNode) func).getIdentifier()
								.name().equals(OMP_BARRIER_AND_FLUSH);
				}
			}
			return false;
		}
	};

	/**
	 * The kind of a OpenMp private variable specified by a
	 * private/firstprivate/lastprivate clause or threadprivate directive.
	 */
	private enum PrivateKind {
		/** OpenMP private clause */
		DEFAULT,
		/** OpenMP firstprivate clause */
		FIRST,
		/** OpenMP lastprivate clause */
		LAST, // Unsupported currently
		/** threadprivate directive */
		THREAD, // Unsupported currently
	}

	/**
	 * The command line configuration information for querying transformation
	 * conditions.
	 */
	private CIVLConfiguration config;

	/** A counter for $omp_arrive_loop functions */
	private int ctrOmpArriveLoop = 0;
	/** A counter for $omp_arrive_sections functions */
	private int ctrOmpArriveSections = 0;
	/** A counter for $omp_arrive_single functions */
	private int ctrOmpArriveSingle = 0;
	/** A counter for OpenMP reduction items */
	private int ctrOmpReductionItem = 0;

	private int levelParallel = 0;

	/**
	 * The stack storing current omp region information.
	 */
	private Stack<OmpRegion> ompRgn = new Stack<>();

	/**
	 * The root node of the input AST.
	 */
	private SequenceNode<BlockItemNode> root;

	/**
	 * A list of critical variable name for critical sections encountered
	 */
	private List<String> criticalVarNames = new ArrayList<>();

	// Constructors
	/**
	 * Constructs a new instance of {@link OpenMP2CIVLWorker2}
	 * 
	 * @param astFactory
	 *            the {@link ASTFactory} instance used for performing
	 *            transformation
	 * @param config
	 *            the {@link CIVLConfiguration} instance used for querying
	 *            transformation conditions
	 */
	public OpenMP2CIVLWorker2(ASTFactory astFactory, CIVLConfiguration config) {
		super(OpenMP2CIVLTransformer.LONG_NAME, astFactory);
		this.identifierPrefix = "$omp_";
		this.config = config;
	}

	// Helper Functions or methods
	/**
	 * Return a list of {@link BlockItemNode} representing:<br>
	 * <code>$read_set_pop;</code><br>
	 * <code>$write_set_pop;</code>
	 * 
	 * @param srcMethod
	 *            Dummy {@link Source} information based on caller name
	 * @return see abvoe
	 */
	private List<BlockItemNode> callRWSetPop(String srcMethod) {
		return Arrays.asList(
				nodeFactory.newExpressionStatementNode(nodeExprCall(srcMethod,
						READ_SET_POP, new LinkedList<>())),
				nodeFactory.newExpressionStatementNode(nodeExprCall(srcMethod,
						WRITE_SET_POP, new LinkedList<>())));
	}

	/**
	 * Return a list of {@link BlockItemNode} representing:<br>
	 * <code>$read_set_push;</code><br>
	 * <code>$write_set_push;</code>
	 * 
	 * @param srcMethod
	 *            Dummy {@link Source} information based on caller name
	 * @return see abvoe
	 */
	private List<BlockItemNode> callRWSetPush(String srcMethod) {
		return Arrays.asList(
				nodeFactory.newExpressionStatementNode(nodeExprCall(srcMethod,
						READ_SET_PUSH, new LinkedList<>())),
				nodeFactory.newExpressionStatementNode(nodeExprCall(srcMethod,
						WRITE_SET_PUSH, new LinkedList<>())));
	}

	/**
	 * Return {@link VariableDeclarationNode} representing:<br>
	 * <code>_Bool /criticalVariableName/ = false;</code>
	 * 
	 * @param srcMethod
	 *            Dummy {@link Source} information based on caller name
	 * @param criticalVariableName
	 *            The name of a critical variable for a critical section
	 *            encountered.
	 * @return See above
	 */
	private VariableDeclarationNode declOmpCriticalVar(String srcMethod,
			String criticalVariableName) {
		// type: $omp_gteam
		TypeNode typeBool = nodeFactory.newBasicTypeNode(
				newSource(srcMethod, CivlcTokenConstant.BOOL),
				BasicTypeKind.BOOL);
		// id: criticalVariableName
		IdentifierNode cvId = nodeIdent(srcMethod, criticalVariableName);
		// init: false
		InitializerNode init = nodeFactory.newBooleanConstantNode(
				newSource(srcMethod, CivlcTokenConstant.INTEGER_CONSTANT),
				false);

		return nodeFactory.newVariableDeclarationNode(
				newSource(srcMethod, CivlcTokenConstant.DECLARATION), //
				cvId, typeBool, init);
	}

	/**
	 * Return {@link VariableDeclarationNode} representing:<br>
	 * <code>$domain(collapse) _omp_loop_dist = ($domain(collapse))
	 * $omp_arrive_loop(team, loop_id++, _omp_loop_domain, STRATEGY);</code>
	 * 
	 * @param srcMethod
	 *            Dummy {@link Source} information based on caller name
	 * @param collapse
	 *            the collapse value specified with the current OpenMP loop
	 *            construct
	 * @return see above
	 */
	private VariableDeclarationNode declOmpDistLoop(String srcMethod,
			int collapse) {
		// type: $domain(collapse)
		TypeNode typeDom = nodeTypeDom(srcMethod, collapse);
		// id: _omp_loop_dist
		IdentifierNode _omp_loop_dist = nodeIdent(srcMethod, LOOP_DIST);
		// init: ($domain(collapse))$omp_arrive_loop(
		// team, FOR_LOC++, _omp_loop_domain, STRATEGY);
		ExpressionNode init = nodeExprCast(srcMethod, typeDom.copy(),
				nodeExprCall(srcMethod, "$omp_arrive_loop", Arrays.asList(
						nodeExprId(srcMethod, TEAM),
						nodeExprInt(srcMethod, ctrOmpArriveLoop++),
						nodeExprCast(srcMethod, nodeTypeDom(srcMethod, 0),
								nodeExprId(srcMethod, DOM_LOOP)),
						nodeExprInt(srcMethod, config.ompLoopDecomp()))));

		return nodeFactory.newVariableDeclarationNode(
				newSource(srcMethod, CivlcTokenConstant.DECLARATION),
				_omp_loop_dist, typeDom, init);
	}

	/**
	 * Return {@link VariableDeclarationNode} representing:<br>
	 * <code>$domain(1) _omp_sections_dist = ($domain(1)) 
	 * $omp_arrive_sections(_omp_team, section_id++, numSection);</code>
	 * 
	 * @param srcMethod
	 *            Dummy {@link Source} information based on caller name
	 * @param numSection
	 *            the number of OpenMP section block in related sections
	 *            construct
	 * @return see above
	 */
	private BlockItemNode declOmpDistSections(String srcMethod,
			int numSection) {
		// type: $domain(1)
		TypeNode typeDom = nodeTypeDom(srcMethod, 1);
		// id: _omp_sections_dist
		IdentifierNode _omp_sections_dist = nodeIdent(srcMethod, SECTIONS_DIST);
		// init: ($domain(collapse))$omp_arrive_loop(
		// team, FOR_LOC++, _omp_loop_domain, STRATEGY);
		ExpressionNode init = nodeExprCast(srcMethod, typeDom.copy(),
				nodeExprCall(srcMethod, OMP_ARRIVE_SECTIONS, Arrays.asList(//
						nodeExprId(srcMethod, TEAM),
						nodeExprInt(srcMethod, ctrOmpArriveSections++),
						nodeExprInt(srcMethod, numSection))));

		return nodeFactory.newVariableDeclarationNode(
				newSource(srcMethod, CivlcTokenConstant.DECLARATION),
				_omp_sections_dist, typeDom, init);
	}

	/**
	 * Return {@link VariableDeclarationNode} representing:<br>
	 * <code>int _omp_single_dist = $omp_arrive_single(team, single_id++);</code>
	 * 
	 * @param srcMethod
	 *            Dummy {@link Source} information based on caller name
	 * @return see above
	 */
	private BlockItemNode declOmpDistSingle(String srcMethod) {
		// type: int
		TypeNode type = nodeTypeInt(srcMethod);
		// id: _omp_single_dist
		IdentifierNode _omp_single_dist = nodeIdent(srcMethod, SINGLE_DIST);
		// init: $omp_arrive_single(team, single_id++);
		ExpressionNode init = nodeExprCall(srcMethod, OMP_ARRIVE_SINGLE,
				Arrays.asList(//
						nodeExprId(srcMethod, TEAM),
						nodeExprInt(srcMethod, ctrOmpArriveSingle++)));

		return nodeFactory.newVariableDeclarationNode(
				newSource(srcMethod, CivlcTokenConstant.DECLARATION),
				_omp_single_dist, type, init);
	}

	/**
	 * Return {@link VariableDeclarationNode} representing:<br>
	 * <code>$domain(1) _omp_dom = ($domain){_omp_thread_range};</code> (if
	 * <code>numRanges</code> is 0) <strong>OR</strong><br>
	 * <code>$domain(1) _omp_loop_dom = ($domain){_omp_range1, ...};</code> (if
	 * <code>numRanges</code> is positive) <br>
	 * <strong>PRE-CONDITION</strong>: <code>numRanges</code> shall be
	 * non-negative.
	 * 
	 * @param srcMethod
	 *            Dummy {@link Source} information based on caller name
	 * @param numRanges
	 *            <code>0</code> for OpenMP parallel region; or a positive
	 *            number representing the number of associated for loops.
	 * @return See above
	 */
	private VariableDeclarationNode declOmpDomain(String srcMethod,
			int numRanges) {
		boolean isParallel = numRanges == 0;
		// type: $domain
		TypeNode typeDom = nodeTypeDom(srcMethod, 1);
		IdentifierNode idDom = null;
		List<PairNode<DesignationNode, InitializerNode>> initials = new ArrayList<>();

		if (isParallel) {
			// id: _omp_dom
			idDom = nodeIdent(srcMethod, DOM);
			// initials: _omp_thread_range
			initials.add(nodeFactory.newPairNode(
					/* src */ newSource(srcMethod, CivlcTokenConstant.STRUCT),
					/* dsgn */(DesignationNode) null, //
					/* init */ nodeExprId(srcMethod, THREAD_RANGE)));
		} else {
			// id: _omp_loop_domain
			idDom = nodeIdent(srcMethod, DOM_LOOP);
			// initials: _omp_range1, .. ,_omp_rangeX
			for (int i = 1; i <= numRanges; i++)
				initials.add(nodeFactory.newPairNode(
						/* src */ newSource(srcMethod,
								CivlcTokenConstant.STRUCT),
						/* dsgn */ (DesignationNode) null, //
						/* init */ nodeExprId(srcMethod,
								RANGE + Integer.toString(i))));
		}

		// initialList: {_omp_thread_range} OR {_omp_range1, .., _omp_rangeX}
		CompoundInitializerNode initialList = nodeFactory
				.newCompoundInitializerNode(newSource(srcMethod,
						CivlcTokenConstant.INITIALIZER_LIST), initials);
		// init: ($domain){..} or
		InitializerNode init = nodeFactory.newCompoundLiteralNode(
				/* src */ newSource(srcMethod,
						CivlcTokenConstant.COMPOUND_LITERAL),
				/* type */ nodeTypeDom(srcMethod, 0),
				/* initials */ initialList);

		return nodeFactory.newVariableDeclarationNode(
				newSource(srcMethod, CivlcTokenConstant.DECLARATION), //
				idDom, typeDom, init);
	}

	/**
	 * Return {@link VariableDeclarationNode} representing:<br>
	 * <code>$omp_gteam gteam = $omp_gteam_create($here, nthreads);</code>
	 * 
	 * @param srcMethod
	 *            Dummy {@link Source} information based on caller name
	 * @return See above
	 */
	private VariableDeclarationNode declOmpGteam(String srcMethod) {
		// type: $omp_gteam
		TypeNode typeGteam = nodeTypeNamed(srcMethod, "$omp_gteam");
		// id: _omp_gteam
		IdentifierNode _omp_gteam = nodeIdent(srcMethod, GTEAM);
		// init: $omp_gteam_create($here, nthreads)
		InitializerNode init = nodeExprCall(srcMethod, OMP_GTEAM_CREATE,
				Arrays.asList(
						nodeFactory.newHereNode(
								newSource(srcMethod, CivlcTokenConstant.HERE)),
						nodeExprId(srcMethod, NTHREADS)));

		return nodeFactory.newVariableDeclarationNode(
				newSource(srcMethod, CivlcTokenConstant.DECLARATION), //
				_omp_gteam, typeGteam, init);
	}

	/**
	 * Return {@link VariableDeclarationNode} representing:<br>
	 * <code>int _omp_nthreads = 1+$choose_int(_omp_num_threads);</code>
	 * 
	 * @param srcMethod
	 *            Dummy {@link Source} information based on caller name
	 * @param numThds
	 *            The {@link ExpressionNode} for <code>_omp_num_threads</code>
	 * @return see above
	 */
	private VariableDeclarationNode declOmpNthreads(String srcMethod,
			ExpressionNode numThds) {
		// type: int
		TypeNode typeInt = nodeTypeInt(srcMethod);
		// id: _omp_nthreads
		IdentifierNode _omp_nthreads = nodeIdent(srcMethod, NTHREADS);
		// init: 1+$choose_int(_omp_num_threads)
		InitializerNode init = nodeFactory.newOperatorNode(
				/* src */ newSource(srcMethod, CivlcTokenConstant.PLUS),
				/* op */ Operator.PLUS, /* arg0 */ nodeExprInt(srcMethod, 1),
				/* arg1 */ nodeExprCall(srcMethod, "$choose_int",
						Arrays.asList(numThds)));

		return nodeFactory.newVariableDeclarationNode(
				newSource(srcMethod, CivlcTokenConstant.DECLARATION), //
				_omp_nthreads, typeInt, init);
	}

	/**
	 * Return {@link VariableDeclarationNode} representing:<br>
	 * <code>int _omp_num_threads = _omp_thread_max;</code>
	 * 
	 * @param srcMethod
	 *            Dummy {@link Source} information based on caller name
	 * @return see above
	 */
	private VariableDeclarationNode declOmpNumThreads(String srcMethod) {
		// type: int
		TypeNode typeInt = nodeTypeInt(srcMethod);
		// id: _omp_nthreads
		IdentifierNode _omp_num_threads = nodeIdent(srcMethod, NUM_THREADS);
		// init: _omp_thread_max
		InitializerNode init = nodeExprId(srcMethod, THREAD_MAX);

		return nodeFactory.newVariableDeclarationNode(
				newSource(srcMethod, CivlcTokenConstant.DECLARATION), //
				_omp_num_threads, typeInt, init);
	}

	/**
	 * Return {@link VariableDeclarationNode} representing:<br>
	 * <code>$range _omp_rangeX = {lo .. hi#step};</code>
	 * 
	 * @param srcMethod
	 *            Dummy {@link Source} information based on caller name
	 * @param num
	 *            The identifier number X of the declared $range type variable
	 * @return See above
	 */
	private VariableDeclarationNode declOmpRange(String srcMethod, int num,
			Triple<ExpressionNode, ExpressionNode, ExpressionNode> range) {
		// type: $range
		TypeNode typeInt = nodeTypeRange(srcMethod);
		// id: _omp_rangeX
		IdentifierNode _omp_rangeX = nodeIdent(srcMethod,
				RANGE + Integer.toString(num));
		// init: {lo .. hi#step}
		InitializerNode init = nodeExprRange(srcMethod, range.first,
				range.second, range.third);

		return nodeFactory.newVariableDeclarationNode(
				newSource(srcMethod, CivlcTokenConstant.DECLARATION), //
				_omp_rangeX, typeInt, init);
	}

	/**
	 * Return {@link VariableDeclarationNode} representing:<br>
	 * <code>$omp_team _omp_team = $omp_team_create($here, _omp_gteam, _omp_tid);</code>
	 * 
	 * @param srcMethod
	 *            Dummy {@link Source} information based on caller name
	 * @return See above
	 */
	private VariableDeclarationNode declOmpTeam(String srcMethod) {
		// type: "$omp_team"
		TypeNode typeTeam = nodeTypeNamed(srcMethod, "$omp_team");
		// id: _omp_team
		IdentifierNode _omp_team = nodeIdent(srcMethod, TEAM);
		// init: $omp_team_create($here, gteam, _tid);
		InitializerNode init = nodeExprCall(srcMethod, OMP_TEAM_CREATE,
				Arrays.asList(
						nodeFactory.newHereNode(
								newSource(srcMethod, CivlcTokenConstant.HERE)),
						nodeExprId(srcMethod, GTEAM),
						nodeExprId(srcMethod, TID)));

		return nodeFactory.newVariableDeclarationNode(
				newSource(srcMethod, CivlcTokenConstant.DECLARATION), //
				_omp_team, typeTeam, init);
	}

	/**
	 * Return {@link VariableDeclarationNode} representing:<br>
	 * <code>$input int _omp_thread_max;</code>
	 * 
	 * @param srcMethod
	 *            Dummy {@link Source} information based on caller name
	 * @return
	 */
	private VariableDeclarationNode declOmpThreadMax(String srcMethod) {
		// type: int
		TypeNode type = nodeTypeInt(srcMethod);
		// id: _omp_thread_max
		IdentifierNode _omp_thread_max = nodeIdent(srcMethod, THREAD_MAX);

		// set $input
		type.setInputQualified(true);
		return nodeFactory.newVariableDeclarationNode(
				newSource(srcMethod, CivlcTokenConstant.DECLARATION), //
				_omp_thread_max, type);
	}

	/**
	 * Return {@link VariableDeclarationNode} representing:<br>
	 * <code>$range _omp_thread_range = {0 .. _omp_nthreads-1};</code>
	 * 
	 * @param srcMethod
	 *            Dummy {@link Source} information based on caller name
	 * @return See above
	 */
	private VariableDeclarationNode declOmpThreadRange(String srcMethod) {
		// type: $range
		TypeNode typeInt = nodeTypeRange(srcMethod);
		// id: _omp_thread_range
		IdentifierNode _omp_thread_range = nodeIdent(srcMethod, THREAD_RANGE);
		// init: {0 .. _omp_nthreads-1}
		InitializerNode init = nodeExprRange(srcMethod,
				/* lb */ nodeExprInt(srcMethod, 0),
				/* ub */ nodeFactory.newOperatorNode(
						newSource(srcMethod, CivlcTokenConstant.SUB),
						Operator.MINUS, nodeExprId(srcMethod, NTHREADS),
						nodeExprInt(srcMethod, 1)),
				/* step */ null);

		return nodeFactory.newVariableDeclarationNode(
				newSource(srcMethod, CivlcTokenConstant.DECLARATION), //
				_omp_thread_range, typeInt, init);
	}

	/**
	 * Process a list of {@link OmpLoopInfo}s to retrieve all involved loop
	 * variables, so that CIVL <code>$for</code> can declare them correctly in
	 * its loop initial expression.
	 * 
	 * @param srcMethod
	 *            Dummy {@link Source} information based on caller name
	 * @param loopInfos
	 *            A list of {@link OmpLoopInfo}s, each of which contains a
	 *            single loop variable.
	 * @return A list of {@link VariableDeclarationNode} representing
	 *         declarations of all involved loop variables.
	 */
	private List<VariableDeclarationNode> declVarsLoopInit(String srcMethod,
			List<OmpLoopInfo> loopInfos) {
		List<VariableDeclarationNode> loopVarDecls = new LinkedList<>();

		for (OmpLoopInfo loopInfo : loopInfos)
			loopVarDecls.add(nodeDeclVarInt(srcMethod, loopInfo.loopVarName));
		return loopVarDecls;
	}

	/**
	 * Process dummy declarations for private variables in thread-local scope.
	 * 
	 * @param srcMethod
	 *            Dummy {@link Source} information based on caller name
	 * @param varIds
	 *            a sequence of {@link IdentifierExpressionNode} representing a
	 *            list of variables specified by a single privatization clause
	 *            or directive.
	 * @param kind
	 *            the kind of the related privatization clause or directive.
	 * @return a non-empty list containing at least one non-<code>null</code>
	 *         list of {@link VariableDeclarationNode}s for dummy declarations
	 *         of private variables. A second optional list for temporary
	 *         declarations that shall be inserted before the OpenMP region.
	 */
	private List<List<VariableDeclarationNode>> declVarsPrivate(
			String srcMethod, SequenceNode<IdentifierExpressionNode> varIds,
			PrivateKind kind) {
		Source declSrc = newSource(srcMethod, CivlcTokenConstant.DECLARATION);
		List<List<VariableDeclarationNode>> privateVarDecls = new LinkedList<List<VariableDeclarationNode>>();
		VariableDeclarationNode actualVarDecl = null;
		IdentifierNode actualVarId = null;
		VariableDeclarationNode pvtVarDecl = null;
		IdentifierNode pvtVarId = null;
		TypeNode pvtVarType = null;

		// The first list is for private variable declarations,
		// which is required for all situations
		privateVarDecls.add(new LinkedList<VariableDeclarationNode>());
		// The second list for temporary declarations that
		// shall be inserted before the parallel region
		privateVarDecls.add(new LinkedList<VariableDeclarationNode>());
		// If there is no private/firstprivate/lastprivate/threadprivate,
		// or no actual variable are specified with them.
		if (varIds == null || varIds.numChildren() == 0)
			// The first list for private variables is empty.
			return privateVarDecls;
		// else at least one private variable is specified
		switch (kind) {
			case DEFAULT :
				for (ASTNode varId : varIds.children()) {
					// get actual declaration for private variables
					actualVarId = ((IdentifierExpressionNode) varId)
							.getIdentifier();
					actualVarDecl = (VariableDeclarationNode) ((Variable) actualVarId
							.getEntity()).getFirstDeclaration();
					// create dummy declaration for private variables
					pvtVarId = nodeIdent(srcMethod, actualVarId.name());
					pvtVarType = actualVarDecl.getTypeNode().copy();
					pvtVarDecl = nodeFactory.newVariableDeclarationNode(//
							declSrc, pvtVarId, pvtVarType);
					// ADD: private variable declarations with same names
					privateVarDecls.get(0).add(pvtVarDecl);
				}
				break;
			case FIRST :
				// ADD: local variable declarations for each firstprivate ones
				// with a temporary variable transferring the value from its
				// original variable to the newly declared local one.
				// E.g.,
				// ==== ==== ==== ==== ==== ==== ==== ==== ==== ==== ==== ====
				// int n = n_val;
				// int *p = p_val;
				// #pragma omp parallel firstprivate(n, p)
				// { ..
				// }
				// ==== ==== ==== ==== ==== ==== ==== ==== ==== ==== ==== ====
				// int n = n_val;
				// int* p = p_val;
				// int _omp_fstpvt_n = n; // get the value of the outer 'n'
				// int* _omp_fstpvt_p = p; // get the value of the outer 'p'
				// $parfor ( .. )
				// { ..
				// int n = _omp_fstpvt_n; // assign the outer 'n' to inner 'n'
				// int* p = _omp_fstpvt_p; // assign the outer 'p' to inner 'p'
				// ..
				// }
				// ==== ==== ==== ==== ==== ==== ==== ==== ==== ==== ==== ====
				VariableDeclarationNode tmpVarDecl = null;
				IdentifierNode tmpVarId = null;
				TypeNode tmpVarType = null;
				String pvtVarName, tmpVarName;

				for (ASTNode varId : varIds.children()) {
					// get actual declaration for private variables
					actualVarId = ((IdentifierExpressionNode) varId)
							.getIdentifier();
					actualVarDecl = (VariableDeclarationNode) ((Variable) actualVarId
							.getEntity()).getFirstDeclaration();
					// create dummy declaration for private variables
					pvtVarName = actualVarId.name();
					tmpVarName = FIRSTPRIVATE_ + pvtVarName;
					pvtVarId = nodeIdent(srcMethod, pvtVarName);
					pvtVarType = actualVarDecl.getTypeNode().copy();
					pvtVarDecl = nodeFactory.newVariableDeclarationNode(//
							declSrc, pvtVarId, pvtVarType,
							nodeExprId(srcMethod, tmpVarName));
					// create dummy declaration for temporary variables
					tmpVarId = nodeIdent(srcMethod, tmpVarName);
					tmpVarType = pvtVarType.copy();
					tmpVarDecl = nodeFactory.newVariableDeclarationNode(//
							declSrc, tmpVarId, tmpVarType,
							nodeExprId(srcMethod, pvtVarName));
					// ADD: private variable declarations with same names
					privateVarDecls.get(INDEX_PVT_DECLS).add(pvtVarDecl);
					privateVarDecls.get(INDEX_TMP_DECLS).add(tmpVarDecl);
				}
				break;
			case LAST :
			case THREAD :
				assert false;
		}
		return privateVarDecls;
	}

	/**
	 * Extract lb, b, and incr values for OpenMP loop from the associated
	 * canonical {@link ForLoopNode} <br>
	 * <strong>PRE-CONDITION</strong>: the given node <code>forLoop</code> shall
	 * be a strict canonical <code>for</code> loop. (See OpenMP 5.0 Sec.
	 * 2.9.1)<br>
	 * <strong>POST-CONDITION</strong>: the lower bound (the first ) of the
	 * range triple is always less than the upper bound (the second). So, for
	 * decreasing loop variable, the range is from b to lb.
	 * 
	 * @param forLoop
	 *            A CIVL-AST node representing a canonical for loop.
	 * @return A triple of {@link ASTNode} representing a range's lower bound,
	 *         upper bound and step
	 */
	private OmpLoopInfo extractLoopInfo(ForLoopNode forLoop)
			throws SyntaxException {
		String srcMethod = SRC_INFO + ".extractLoopInfo";
		Source exprSrc = newSource(srcMethod, CivlcTokenConstant.EXPR);
		ExpressionNode one = nodeFactory.newIntegerConstantNode(exprSrc, "1");
		ForLoopInitializerNode initExpr = forLoop.getInitializer();
		ExpressionNode testExpr = forLoop.getCondition();
		ExpressionNode incrExpr = forLoop.getIncrementer();
		IdentifierNode var = null;
		ExpressionNode lb = null, b = null, incr = null;
		OperatorNode expr = null;
		ExpressionNode lhsExpr = null, rhsExpr = null;
		boolean isOpenBound = false;

		// GET: var (loop variable) and lb (the initial value of var)
		if (initExpr instanceof OperatorNode) {
			expr = (OperatorNode) initExpr;
			if (expr.getOperator() == Operator.ASSIGN) {
				// var = lb
				lhsExpr = expr.getArgument(0);
				rhsExpr = expr.getArgument(1);
				var = ((IdentifierExpressionNode) lhsExpr).getIdentifier();
				lb = rhsExpr;
			} // else non-canonical init-expr
		} else if (initExpr instanceof DeclarationListNode) {
			// type var = lb
			VariableDeclarationNode decl = (VariableDeclarationNode) initExpr
					.child(0);

			var = decl.getIdentifier();
			lb = (ExpressionNode) decl.getInitializer();
		}
		if (var == null || lb == null)
			throw new CIVLSyntaxException(
					"Non-canonical OpenMP loop init-expr.");

		// GET: b (the bound value of var)
		if (testExpr instanceof OperatorNode) {
			expr = (OperatorNode) testExpr;
			switch (expr.getOperator()) {
				case GT :
				case LT :
					isOpenBound = true;
				case LTE :
				case GTE :
				case NEQ :
					lhsExpr = expr.getArgument(0);
					rhsExpr = expr.getArgument(1);
					if (isSameVarEntity(lhsExpr, var))
						// var rel-op b
						b = rhsExpr;
					else if (isSameVarEntity(rhsExpr, var))
						// b rel-op var
						b = lhsExpr;
				default :
			}
		}
		if (b == null)
			throw new CIVLSyntaxException(
					"Non-canonical OpenMP loop test-expr.");

		// GET: incr (the step value of var)
		if (incrExpr instanceof OperatorNode) {
			expr = (OperatorNode) incrExpr;
			if (expr.getOperator() == Operator.ASSIGN) {
				// var = var + incr
				// var = incr + var
				// var = var - incr
				incrExpr = expr.getArgument(1);
				if (incrExpr instanceof OperatorNode)
					expr = (OperatorNode) incrExpr;
				else
					throw new CIVLSyntaxException(
							"Non-canonical OpenMP loop incr-expr.");
			}
			if (expr.getNumberOfArguments() == 2) {
				lhsExpr = expr.getArgument(0);
				rhsExpr = expr.getArgument(1);
				if (isSameVarEntity(lhsExpr, var))
					// var + incr | var - incr
					// var += incr | var -= incr
					incr = rhsExpr;
				else if (isSameVarEntity(rhsExpr, var))
					// incr + var
					incr = lhsExpr;
			} else if (expr.getNumberOfArguments() == 1)
				// ++va | var++ | --var | var--
				incr = one;
		}
		if (incr == null)
			throw new CIVLSyntaxException(
					"Non-canonical OpenMP loop incr-expr.");
		lb = lb.copy();
		b = b.copy();
		incr = incr.copy();
		// CREATE: triple<rangeLower, rangeUpper, rangeStep>
		switch (expr.getOperator()) {
			case PLUS : // var + incr | incr + var
			case PLUSEQ : // var += incr
			case PREINCREMENT : // ++var
			case POSTINCREMENT : // var++
				// open test bound b is decreased by 1
				if (isOpenBound)
					b = nodeFactory.newOperatorNode(b.getSource(),
							Operator.MINUS, b, one.copy());
				return new OmpLoopInfo(var.name(), new Triple<>(lb, b, incr));
			case MINUS : // var - incr
			case MINUSEQ : // var -= incr
			case PREDECREMENT : // --var
			case POSTDECREMENT : // var--
				// negate incr
				incr = nodeFactory.newOperatorNode(incr.getSource(),
						Operator.UNARYMINUS, incr);
				// open test bound b is increased by 1
				if (isOpenBound)
					b = nodeFactory.newOperatorNode(b.getSource(),
							Operator.PLUS, b, one.copy());
				// b < lb, so range should be [b, lb]
				return new OmpLoopInfo(var.name(), new Triple<>(b, lb, incr));
			default :
				throw new CIVLSyntaxException(
						"Non-canonical OpenMP loop incr-expr.");
		}
	}

	/**
	 * 
	 * @param srcMethod
	 * @param isParallel
	 * @param domainVarName
	 * @param loopVarDecls
	 * @param loopBodyItems
	 * @return
	 */
	private CivlForNode genCivlFor(String srcMethod, boolean isParallel,
			String domainVarName, List<VariableDeclarationNode> loopVarDecls,
			List<BlockItemNode> loopBodyItems) {
		// create CIVL loop var. decl.: int loopVar1, .. , loopVarX
		ForLoopInitializerNode loopInit = nodeFactory.newForLoopInitializerNode(
				newSource(srcMethod, CivlcTokenConstant.INITIALIZER_LIST),
				loopVarDecls);
		// create CIVL loop body: { .. }
		StatementNode loopBody = nodeFactory.newCompoundStatementNode(
				newSource(srcMethod, CivlcTokenConstant.COMPOUND_STATEMENT),
				loopBodyItems);

		// if (isParallel): $parfor(int _omp_tid : _omp_domain) { .. }
		// else: $for(int loopVar1, .. loopVarX : _omp_loop_dist) { .. }
		return nodeFactory.newCivlForNode(
				newSource(srcMethod, CivlcTokenConstant.CIVLFOR), isParallel,
				(DeclarationListNode) loopInit,
				nodeExprId(srcMethod, domainVarName), loopBody, null);
	}

	/**
	 * Return <code>true</code> iff <code>sourceFile</code> indicating that the
	 * corresponding node is imported from library source files including: <br>
	 * *.cvh, *.h (except for stdio.h), civlc.cvl, concurrency.cvl, omp.cvl,
	 * pthread.cvl, stdio.cvl, string.cvl
	 * 
	 * @param sourceFileName
	 *            the name of a source file.
	 * @return see above.
	 */
	private boolean isImported(String sourceFileName) {
		return sourceFileName.endsWith(".cvh")
				|| sourceFileName.equals("civlc.cvl")
				|| sourceFileName.equals("concurrency.cvl")
				|| sourceFileName.equals("omp.cvl")
				|| sourceFileName.equals("pthread.cvl")
				|| sourceFileName.equals("stdio.cvl")
				|| sourceFileName.equals("string.cvl")
				|| (sourceFileName.endsWith(".h"));
	}

	/**
	 * Return <code>true</code> iff the {@link IdentifierNode} wrapped by
	 * <code>varExpr</code> refers to a same variable entity identified by
	 * <code>varId</code>.
	 * 
	 * @param varExpr
	 *            A variable expression holding a single variable
	 * @param varId
	 *            An identifier of a variable
	 * @return See above.
	 */
	private boolean isSameVarEntity(ExpressionNode varExpr,
			IdentifierNode varId) {
		return varExpr instanceof IdentifierExpressionNode
				&& ((IdentifierExpressionNode) varExpr).getIdentifier()
						.getEntity().equals(varId.getEntity());
	}

	/** construct {@link CompoundStatementNode} */
	private CompoundStatementNode nodeBlock(String srcMethod,
			List<BlockItemNode> blockItems) {
		return nodeFactory.newCompoundStatementNode(
				newSource(srcMethod, CivlcTokenConstant.COMPOUND_STATEMENT),
				blockItems);
	}

	/** construct {@link VariableDeclarationNode} with int type and no init */
	private VariableDeclarationNode nodeDeclVarInt(String srcMethod,
			String varName) {
		return nodeFactory.newVariableDeclarationNode(
				newSource(srcMethod, CivlcTokenConstant.DECLARATION),
				nodeIdent(srcMethod, varName), nodeTypeInt(srcMethod));
	}

	/** construct {@link ExpressionNode} for <code>&expr</code> */
	private ExpressionNode nodeExprAddrOf(String srcMethod,
			ExpressionNode expr) {
		return nodeFactory.newOperatorNode(
				newSource(srcMethod, CivlcTokenConstant.EXPR),
				Operator.ADDRESSOF, Arrays.asList(expr));
	}

	/** construct {@link FunctionCallNode} */
	private ExpressionNode nodeExprCall(String srcMethod, String funcName,
			List<ExpressionNode> funcArgs) {
		return nodeFactory.newFunctionCallNode(
				newSource(srcMethod, CivlcTokenConstant.CALL),
				nodeExprId(srcMethod, funcName), funcArgs, null);
	}

	/** construct {@link CastNode} */
	private ExpressionNode nodeExprCast(String srcMethod, TypeNode type,
			ExpressionNode expr) {
		return nodeFactory.newCastNode(
				newSource(srcMethod, CivlcTokenConstant.CAST), type, expr);
	}

	/** construct {@link IdentifierExpressionNode} */
	private ExpressionNode nodeExprId(String srcMethod, String idName) {
		IdentifierNode ident = nodeIdent(srcMethod, idName);

		return nodeFactory.newIdentifierExpressionNode(ident.getSource(),
				ident);
	}

	/** construct {@link IntegerConstantNode} */
	private ExpressionNode nodeExprInt(String srcMethod, int val) {
		return nodeFactory.newIntConstantNode(
				newSource(srcMethod, CivlcTokenConstant.INTEGER_CONSTANT), val);
	}

	/** construct {@link RegularRangeNode} */
	private ExpressionNode nodeExprRange(String srcMethod, ExpressionNode lo,
			ExpressionNode hi, ExpressionNode step) {
		if (step == null)
			return nodeFactory.newRegularRangeNode(
					newSource(srcMethod, CivlcTokenConstant.EXPR), lo, hi);
		else
			return nodeFactory.newRegularRangeNode(
					newSource(srcMethod, CivlcTokenConstant.EXPR), lo, hi,
					step);
	}

	/** construct {@link ExpressionNode} for omp reduction init val */
	private ExpressionNode nodeExprReductionInit(String srcMethod,
			OmpReductionOperator reductionOp, TypeNode type) {
		// TODO: an initial value shall comply with its bonding types
		switch (type.kind()) {
			case BASIC :
				switch (reductionOp) {
					case MAX : // TYPE.MIN_VALUE
						return nodeExprInt(srcMethod, Integer.MIN_VALUE);
					case MIN : // TYPE.MAX_VALUE
						return nodeExprInt(srcMethod, Integer.MAX_VALUE);
					case BAND : // ~0
						return nodeExprInt(srcMethod, ~0);
					case LAND : // true (or !0)
					case EQV : // true (or !0)
					case PROD : // 1
						return nodeExprInt(srcMethod, 1);
					case LOR : // false (or 0)
					case NEQ : // false (or 0)
					case SUM : // 0
					case MINUS :// 0
					case BOR : // 0
					case BXOR : // 0
						return nodeExprInt(srcMethod, 0);
					default :
						throw new CIVLUnimplementedFeatureException(
								"Unsupported OpenMP reduction operator: "
										+ reductionOp);
				}
			case ARRAY :
			case POINTER :
			default :
				throw new CIVLUnimplementedFeatureException("Unsupported type: "
						+ type + " for OpenMP reduction operator: "
						+ reductionOp);

		}
	}

	/** construct {@link IdentifierNode} */
	private IdentifierNode nodeIdent(String srcMethod, String idName) {
		return nodeFactory.newIdentifierNode(
				newSource(srcMethod, CivlcTokenConstant.IDENTIFIER), idName);
	}

	/** construct CIVL <code>$domain(dim)</code> type node: */
	private TypeNode nodeTypeDom(String srcMethod, int dim) {
		if (dim > 0) // $domain(dim)
			return nodeFactory.newDomainTypeNode(
					newSource(srcMethod, CivlcTokenConstant.DOMAIN),
					nodeExprInt(srcMethod, dim));
		else // $domain
			return nodeFactory.newDomainTypeNode(
					newSource(srcMethod, CivlcTokenConstant.DOMAIN));
	}

	/** construct <code>int</code> {@link TypeNode} */
	private TypeNode nodeTypeInt(String srcMethod) {
		return nodeFactory.newBasicTypeNode(
				newSource(srcMethod, CivlcTokenConstant.INT),
				BasicTypeKind.INT);
	}

	/** construct named_type_def {@link TypeNode} w/ given <code>name</code> */
	private TypeNode nodeTypeNamed(String srcMethod, String name) {
		return nodeFactory.newTypedefNameNode(nodeIdent(srcMethod, name), null);
	}

	/** construct CIVL <code>$range</code> {@link TypeNode} */
	private TypeNode nodeTypeRange(String srcMethod) {
		return nodeFactory.newRangeTypeNode(
				newSource(srcMethod, CivlcTokenConstant.RANGE));
	}

	/**
	 * Perform transformation on OpenMP Parallel Region (see:
	 * https://vsl.cis.udel.edu/trac/civl/wiki/OpenMPTransformation#Translatingfor
	 * )
	 * 
	 * @param ompForNode
	 * @throws SyntaxException
	 */
	private void procOmpForNode(OmpForNode ompForNode) throws SyntaxException {
		// TODO: reduction,lastprivate clause
		String srcMethod = SRC_INFO + ".procOmpForNode";
		List<BlockItemNode> ompBlockItems = new LinkedList<>();
		// PROC: collapse
		int collapse = ompForNode.collapse();
		int numLoopRanges = 0;
		ArrayList<OmpLoopInfo> loopInfos = new ArrayList<>();
		ForLoopNode curLoop = (ForLoopNode) ompForNode.statementNode();
		OmpLoopInfo curLoopInfo = extractLoopInfo(curLoop);

		// PRE: Record the current OpenMP region info
		ompRgn.push(new OmpRegion(OmpRgnKind.LOOP));
		loopInfos.add(curLoopInfo);
		for (int i = 1; i < collapse; i++) {
			curLoop = (ForLoopNode) curLoop.getBody();
			curLoopInfo = extractLoopInfo(curLoop);
			loopInfos.add(curLoopInfo);
		}
		// ADD: $read_set_pop();
		// ADD: $write_set_pop();
		ompBlockItems.addAll(callRWSetPop(srcMethod));
		// ADD: $range _omp_rangeX = {lo .. hi, step};
		// * note that X is [1 .. numLoopRanges]
		for (OmpLoopInfo info : loopInfos)
			ompBlockItems
					.add(declOmpRange(srcMethod, ++numLoopRanges, info.range));
		assert numLoopRanges == loopInfos.size();
		// ADD: $domain(1) _omp_loop_domain = ($domain){_omp_range1, ...};
		ompBlockItems.add(declOmpDomain(srcMethod, numLoopRanges));
		// ADD: $domain(collapse) _omp_loop_dist = ($domain(collapse))
		// $omp_arrive_loop(_omp_team, loop_id++, _omp_loop_domain, STRATEGY);
		ompBlockItems.add(declOmpDistLoop(srcMethod, collapse));

		// PROC: shared, private and firstprivate variabe list.
		// NOTE: an item can appear in both firstprivate and last private.
		List<List<VariableDeclarationNode>> pvtDeclsList = declVarsPrivate(
				srcMethod, ompForNode.privateList(), PrivateKind.DEFAULT);
		List<List<VariableDeclarationNode>> fstpvtDeclsList = declVarsPrivate(
				srcMethod, ompForNode.firstprivateList(), PrivateKind.FIRST);
		List<List<BlockItemNode>> rdcItemsList = transOmpReduction(
				ompForNode.reductionList());

		// ADD: dummy decl. for pvt. var.
		ompBlockItems.addAll(pvtDeclsList.get(INDEX_PVT_DECLS));
		// ADD: temp. decl. for holding val. of pvt.1st var.
		ompBlockItems.addAll(fstpvtDeclsList.get(INDEX_TMP_DECLS));
		// ADD: dummy decl. for pvt.1st var.
		ompBlockItems.addAll(fstpvtDeclsList.get(INDEX_PVT_DECLS));
		// ADD: $read_set_push();
		// ADD: $write_set_push();
		ompBlockItems.addAll(callRWSetPush(srcMethod));
		// dummy decl. and init. for reduction items
		ompBlockItems.addAll(rdcItemsList.get(INDEX_RDC_INITS));

		// TRANS: OMP loop Region -> CIVL $for construct
		List<BlockItemNode> cvlForBodyItems = new LinkedList<>();
		StatementNode loopBody = null;

		// process and transfer all other children
		searchOmpInstructions(curLoop);
		// get processed body
		loopBody = curLoop.getBody();
		// transfer into civl $for loop body
		if (loopBody instanceof CompoundStatementNode)
			for (ASTNode child : loopBody.children()) {
				child.remove();
				cvlForBodyItems.add((BlockItemNode) child);
			}
		else {
			loopBody.remove();
			cvlForBodyItems.add(loopBody);
		}
		ompBlockItems.add(genCivlFor(//
				srcMethod, false, /* domName */ LOOP_DIST,
				/* loopVarDecls */ declVarsLoopInit(srcMethod, loopInfos),
				/* loopBodyItems */ cvlForBodyItems));
		// dummy decl. and init. for reduction items
		ompBlockItems.addAll(rdcItemsList.get(INDEX_RDC_COMBS));
		if (!ompForNode.nowait())
			// ADD: $omp_barrier_and_flush(
			// team, $read_set_pop(), $write_set_pop());
			ompBlockItems.add(nodeFactory.newExpressionStatementNode( //
					nodeExprCall(srcMethod, OMP_BARRIER_AND_FLUSH,
							Arrays.asList(nodeExprId(srcMethod, TEAM)))));

		// TRANS: replace parallel region with transformed block
		replaceOmpNode(srcMethod, ompForNode, ompBlockItems);
		ompRgn.pop();
	}

	/**
	 * Perform transformation on OpenMP Parallel Region (see:
	 * https://vsl.cis.udel.edu/trac/civl/wiki/Next-GenOpenMPTransformation#Translatingparallel
	 * )
	 * 
	 * @param ompParallelNode
	 * @throws SyntaxException
	 */
	private void procOmpParallelNode(OmpParallelNode ompParallelNode)
			throws SyntaxException {
		// TODO: reduction clause, num_threads clause
		String srcMethod = SRC_INFO + ".procOmpParallelNode";
		Source declSrc = newSource(srcMethod, CivlcTokenConstant.DECLARATION);
		List<BlockItemNode> ompBlockItems = new LinkedList<>();

		// PRE: Record the current OpenMP region info
		ompRgn.push(new OmpRegion(OmpRgnKind.PARALLEL));
		levelParallel += 1;

		// PROC: _omp_num_threads
		ExpressionNode _omp_num_threads = ompParallelNode.numThreads();
		// PROC: shared, private and firstprivate variabe list.
		// NOTE: an item can appear in both firstprivate and last private.
		List<List<VariableDeclarationNode>> pvtDeclsList = declVarsPrivate(
				srcMethod, ompParallelNode.privateList(), PrivateKind.DEFAULT);
		List<List<VariableDeclarationNode>> fstpvtDeclsList = declVarsPrivate(
				srcMethod, ompParallelNode.firstprivateList(),
				PrivateKind.FIRST);
		List<List<BlockItemNode>> rdcItemsList = transOmpReduction(
				ompParallelNode.reductionList());

		if (_omp_num_threads == null) // If absent
			_omp_num_threads = nodeExprId(srcMethod, _OMP_ + "num_threads");
		else
			_omp_num_threads.remove();
		ompBlockItems.add(elaborateExpression(_omp_num_threads).copy());
		// ADD: int _omp_nthreads = 1+$choose_int(_omp_num_threads);
		ompBlockItems.add(declOmpNthreads(srcMethod, _omp_num_threads));
		// ADD: temporary variable declarations for firstprivate variables
		ompBlockItems.addAll(fstpvtDeclsList.get(INDEX_TMP_DECLS));
		// ADD: $range _omp_thread_range = {0 .. _omp_nthreads-1};
		ompBlockItems.add(declOmpThreadRange(srcMethod));
		// ADD: $domain(1) dom = ($domain){thread_range};
		ompBlockItems.add(declOmpDomain(srcMethod, 0)); // 0 for parallel region
		// ADD: $omp_gteam gteam = $omp_gteam_create($here, nthreads);
		ompBlockItems.add(declOmpGteam(srcMethod));

		// TRANS: OMP Parallel Region -> CIVL $parfor construct
		List<BlockItemNode> parForBodyItems = new LinkedList<>();
		StatementNode bodyStatement = null;

		// ADD to $parfor:
		// $local_start();
		parForBodyItems.add(nodeFactory.newExpressionStatementNode(
				nodeExprCall(srcMethod, LOCAL_START, Arrays.asList())));
		// $omp_team _omp_team = $omp_team_create($here, _omp_gteam, _omp_tid);
		parForBodyItems.add(declOmpTeam(srcMethod));
		// $read_set_push();
		// $write_set_push();
		parForBodyItems.addAll(callRWSetPush(srcMethod));
		// dummy decl. and init. for reduction items
		parForBodyItems.addAll(rdcItemsList.get(INDEX_RDC_INITS));
		// dummy decl. for pvt. var. shall be inserted in parfor body
		parForBodyItems.addAll(pvtDeclsList.get(INDEX_PVT_DECLS));
		parForBodyItems.addAll(fstpvtDeclsList.get(INDEX_PVT_DECLS));
		// DEPRECATED: transformation for shared variables, due to R/W set
		// process and transfer all other children
		searchOmpInstructions(ompParallelNode);
		bodyStatement = ompParallelNode.statementNode();
		bodyStatement.remove();
		parForBodyItems.add(bodyStatement);
		// TODO: reduction impl. in civl-omp.cvl should push-red-pop
		// dummy decl. and init. for reduction items
		parForBodyItems.addAll(rdcItemsList.get(INDEX_RDC_COMBS));
		// $omp_barrier_and_flush(team);
		parForBodyItems.add(nodeFactory.newExpressionStatementNode( //
				nodeExprCall(srcMethod, OMP_BARRIER_AND_FLUSH,
						Arrays.asList(nodeExprId(srcMethod, TEAM)))));
		// $read_set_pop();
		// $write_set_pop();
		parForBodyItems.addAll(callRWSetPop(srcMethod));
		// $omp_team_destroy(team);
		parForBodyItems.add(nodeFactory.newExpressionStatementNode(
				nodeExprCall(srcMethod, OMP_TEAM_DESTROY,
						Arrays.asList(nodeExprId(srcMethod, TEAM)))));
		// $local_end();
		parForBodyItems.add(nodeFactory.newExpressionStatementNode(
				nodeExprCall(srcMethod, LOCAL_END, Arrays.asList())));

		// ADD: $parfor (int _omp_tid : _omp_dom) { .. }
		ompBlockItems.add(genCivlFor(srcMethod, true, /* domName */ DOM,
				/* loopVarDecls */ Arrays.asList(//
						nodeFactory.newVariableDeclarationNode(declSrc,
								nodeIdent(srcMethod, TID),
								nodeTypeInt(srcMethod))),
				parForBodyItems));
		// ADD: $omp_gteam_destroy(gteam);
		ompBlockItems.add(nodeFactory.newExpressionStatementNode(
				nodeExprCall(srcMethod, OMP_GTEAM_DESTROY,
						Arrays.asList(nodeExprId(srcMethod, GTEAM)))));
		// TRANS: replace parallel region with transformed block
		replaceOmpNode(srcMethod, ompParallelNode, ompBlockItems);
		ompRgn.pop();
	}

	private void procOmpSectionsNode(OmpWorksharingNode ompSectionsNode)
			throws SyntaxException {
		String srcMethod = SRC_INFO + ".procOmpSectionsNode";
		List<BlockItemNode> ompBlockItems = new LinkedList<>();

		// PRE: Record the current OpenMP region info
		ompRgn.push(new OmpRegion(OmpRgnKind.SECTIONS));

		// get each section block in the current sections construct
		List<StatementNode> sectionBlocks = new LinkedList<>();
		// PROC: shared, private and firstprivate variabe list.
		// NOTE: an item can appear in both firstprivate and last private.
		List<List<VariableDeclarationNode>> pvtDeclsList = declVarsPrivate(
				srcMethod, ompSectionsNode.privateList(), PrivateKind.DEFAULT);
		List<List<VariableDeclarationNode>> fstpvtDeclsList = declVarsPrivate(
				srcMethod, ompSectionsNode.firstprivateList(),
				PrivateKind.FIRST);
		// PROC: analysis the number of section constructs
		StatementNode sectionsItems = ompSectionsNode.statementNode();
		int numItems = sectionsItems.numChildren();
		int idx = 0;
		ASTNode item = null;

		// Check the first item
		item = sectionsItems.child(idx++);
		if (item instanceof StatementNode)
			// implicit section block
			sectionBlocks.add((StatementNode) item);
		else
			// explicit section block
			sectionBlocks.add(((OmpWorksharingNode) item).statementNode());
		while (idx < numItems) {
			item = sectionsItems.child(idx++);
			if (item instanceof OmpWorksharingNode)
				sectionBlocks.add(((OmpWorksharingNode) item).statementNode());
			else
				throw new CIVLSyntaxException(
						"Non-section item in OpenMP sections construct.");
		}
		// ADD: $read_set_pop();
		// ADD: $write_set_pop();
		ompBlockItems.addAll(callRWSetPop(srcMethod));
		// ADD: $domain(1) _omp_sections_dist = ($domain(1))
		// $omp_arrive_sections(_omp_team, section_id++, numSection);
		ompBlockItems.add(declOmpDistSections(srcMethod, sectionBlocks.size()));
		// ADD: dummy decl. for pvt. var.
		ompBlockItems.addAll(pvtDeclsList.get(INDEX_PVT_DECLS));
		// ADD: temp. decl. for holding val. of pvt.1st var.
		ompBlockItems.addAll(fstpvtDeclsList.get(INDEX_TMP_DECLS));
		// ADD: dummy decl. for pvt.1st var.
		ompBlockItems.addAll(fstpvtDeclsList.get(INDEX_PVT_DECLS));
		// ADD: $read_set_push();
		// ADD: $write_set_push();
		ompBlockItems.addAll(callRWSetPush(srcMethod));

		// TRANS: OMP sections Region -> CIVL $for construct
		// ADD:
		// $for(int _omp_sid : _omp_sections_dist) {
		// if (_omp_sid == 1) { BLOCK1 }
		// ..
		// }
		// all section blocks are recursively processed in 'transOmpSection'
		ompBlockItems.add(genCivlFor(//
				srcMethod, false, /* domName */ SECTIONS_DIST,
				/* loopVarDecls */ Arrays.asList(//
						nodeDeclVarInt(srcMethod, SID)),
				/* loopBodyItems */ transOmpSection(sectionBlocks)));
		if (!ompSectionsNode.nowait())
			// ADD: $omp_barrier_and_flush(team);
			ompBlockItems.add(nodeFactory.newExpressionStatementNode(//
					nodeExprCall(srcMethod, OMP_BARRIER_AND_FLUSH,
							Arrays.asList(nodeExprId(srcMethod, TEAM)))));
		// TRANS: replace sections region with transformed block
		replaceOmpNode(srcMethod, ompSectionsNode, ompBlockItems);
		ompRgn.pop();
	}

	private void procOmpSingleNode(OmpWorksharingNode ompSingleNode)
			throws SyntaxException {
		String srcMethod = SRC_INFO + ".procOmpSingleNode";
		List<BlockItemNode> ompBlockItems = new LinkedList<>();

		// PRE: Record the current OpenMP region info
		ompRgn.push(new OmpRegion(OmpRgnKind.SINGLE));

		List<List<VariableDeclarationNode>> pvtDeclsList = declVarsPrivate(
				srcMethod, ompSingleNode.privateList(), PrivateKind.DEFAULT);
		List<List<VariableDeclarationNode>> fstpvtDeclsList = declVarsPrivate(
				srcMethod, ompSingleNode.firstprivateList(), PrivateKind.FIRST);

		assert ompSingleNode.copyprivateList() == null; // TODO: copypvt clause
		assert ompSingleNode.copyinList() == null; // TODO: copyin clause

		// ADD: $read_set_pop();
		// ADD: $write_set_pop();
		ompBlockItems.addAll(callRWSetPop(srcMethod));
		// ADD: int _omp_single_dist = $omp_arrive_single(team, single_id++);
		ompBlockItems.add(declOmpDistSingle(srcMethod));
		// ADD: dummy decl. for pvt. var.
		ompBlockItems.addAll(pvtDeclsList.get(INDEX_PVT_DECLS));
		// ADD: temp. decl. for holding val. of pvt.1st var.
		ompBlockItems.addAll(fstpvtDeclsList.get(INDEX_TMP_DECLS));
		// ADD: dummy decl. for pvt.1st var.
		ompBlockItems.addAll(fstpvtDeclsList.get(INDEX_PVT_DECLS));
		// ADD: $read_set_push();
		// ADD: $write_set_push();
		ompBlockItems.addAll(callRWSetPush(srcMethod));
		// TRANS: recursively transforms child nodes.
		searchOmpInstructions(ompSingleNode);

		// TRANS: adds the condition ' _omp_tid == _omp_single_dist ' to
		// the associated block so that exactly one thread executes it.
		StatementNode singleBody = ompSingleNode.statementNode();
		// cond_expr: _omp_tid == _omp_single_dist
		ExpressionNode condExpr = nodeFactory.newOperatorNode(
				newSource(srcMethod, CivlcTokenConstant.EXPR), Operator.EQUALS,
				nodeExprId(srcMethod, TID), nodeExprId(srcMethod, SINGLE_DIST));

		singleBody.remove();
		ompBlockItems.add(nodeFactory.newIfNode(
				newSource(srcMethod, CivlcTokenConstant.STATEMENT), condExpr,
				singleBody));
		if (!ompSingleNode.nowait())
			// ADD: $omp_barrier_and_flush(team);
			ompBlockItems.add(nodeFactory.newExpressionStatementNode(//
					nodeExprCall(srcMethod, OMP_BARRIER_AND_FLUSH,
							Arrays.asList(nodeExprId(srcMethod, TEAM)))));
		// TRANS: replace single construct with transformed block
		replaceOmpNode(srcMethod, ompSingleNode, ompBlockItems);
		ompRgn.pop();
	}

	private void recognizeOmpFunctionCalls(
			FunctionCallNode ompFunctionCallNode) {
		String srcMethod = SRC_INFO + ".recognizeOmpFunctionCalls: ";
		ExpressionNode functionExpr = ompFunctionCallNode.getFunction();
		ExpressionNode transformedFunctionCall = null;

		if (functionExpr instanceof IdentifierExpressionNode) {
			switch (((IdentifierExpressionNode) functionExpr).getIdentifier()
					.name()) {
				case OMP_GET_MAX_THREADS :
					transformedFunctionCall = nodeExprId(
							srcMethod + OMP_GET_MAX_THREADS, THREAD_MAX);
					break;
				case OMP_GET_NUM_PROCS :
					transformedFunctionCall = nodeExprInt(
							srcMethod + OMP_GET_NUM_PROCS, 1);
					break;
				case OMP_GET_NUM_THREADS :
					transformedFunctionCall = nodeExprId(
							srcMethod + OMP_GET_MAX_THREADS, NTHREADS);
					break;
				case OMP_GET_THREAD_NUM :
					transformedFunctionCall = nodeExprId(
							srcMethod + OMP_GET_MAX_THREADS, TID);
					break;
				case OMP_SET_NUM_THREADS :
					transformedFunctionCall = nodeFactory.newOperatorNode(
							newSource(srcMethod, CivlcTokenConstant.EXPR),
							Operator.ASSIGN, nodeExprId(srcMethod, NUM_THREADS),
							ompFunctionCallNode.getArgument(0).copy());
					break;
				default :
					// do nothing
			}
			if (transformedFunctionCall != null)
				ompFunctionCallNode.parent().setChild(
						ompFunctionCallNode.childIndex(),
						transformedFunctionCall);
		}
	}

	private void recognizeOmpInstructions(OmpNode ompNode)
			throws SyntaxException {
		switch (ompNode.ompNodeKind()) {
			case DECLARATIVE :
				OmpDeclarativeNode ompDeclNode = (OmpDeclarativeNode) ompNode;

				switch (ompDeclNode.ompDeclarativeNodeKind()) {
					case REDUCTION :
					case SIMD :
					case TARGET :
					case THREADPRIVATE :
				}
			case EXECUTABLE :
				OmpExecutableNode ompExecNode = (OmpExecutableNode) ompNode;

				switch (ompExecNode.ompExecutableKind()) {
					case PARALLEL :
						procOmpParallelNode((OmpParallelNode) ompExecNode);
						return;
					case SIMD :
					case SYNCHRONIZATION :
						OmpSyncNode ompSyncNode = (OmpSyncNode) ompExecNode;

						switch (ompSyncNode.ompSyncNodeKind()) {
							case MASTER :
							case CRITICAL :
							case BARRIER :
							case FLUSH :
								// Omitted
								ompSyncNode.remove();
								return;
							case ORDERED :
							case OMPATOMIC :
								OmpAtomicNode ompAtomicNode = (OmpAtomicNode) ompSyncNode;

								switch (ompAtomicNode.atomicClause()) {
									case READ :
									case WRITE :
									case UPDATE :
									case CAPTURE :
								}
						}
					case WORKSHARING :
						OmpWorksharingNode ompWorkSNode = (OmpWorksharingNode) ompExecNode;

						switch (ompWorkSNode.ompWorkshareNodeKind()) {
							case SECTIONS :
								procOmpSectionsNode(ompWorkSNode);
								return;
							case SECTION :
								throw new CIVLSyntaxException(
										"OpenMP section construct shall appear in sections constuct directly.");
							case SINGLE :
								procOmpSingleNode(ompWorkSNode);
								return;
							case FOR :
								procOmpForNode((OmpForNode) ompWorkSNode);
								return;
						}
				}
		}
		// DFS: recursively search for OmpNode among successors of this OmpNode
		searchOmpInstructions(ompNode);
	}

	/**
	 * Replace the processed {@link OmpNode} <code>ompNode</code> with a list of
	 * {@link BlockItemNode} transformed.
	 * 
	 * @param srcMethod
	 *            Dummy {@link Source} information based on caller name
	 * @param ompNode
	 *            The processed {@link OmpNode}
	 * @param blockItems
	 *            A list of {@link BlockItemNode} representing behaviors
	 *            peformed by the processed OpenMP pragma and its associated
	 *            block constructs.
	 */
	private void replaceOmpNode(String srcMethod, OmpNode ompNode,
			List<BlockItemNode> blockItems) {
		ASTNode parent = ompNode.parent();
		int indexOld = ompNode.childIndex();

		parent.setChild(indexOld, nodeBlock(srcMethod, blockItems));
	}

	/**
	 * Perform recursive DFS for searching {@link OmpNode}s among successors of
	 * the given {@link ASTNode} <code>root</code>. If an {@link OmpNode} is
	 * found, then it is processed and this function is applied for continuing
	 * exploring its successors
	 * 
	 * @param root
	 *            a {@link ASTNode}, if it is an {@link OmpNode} then it must be
	 *            processed by <code>this</code> transformer.
	 * @throws SyntaxException
	 */
	private void searchOmpInstructions(ASTNode root) throws SyntaxException {
		// DFS: recursively search for OmpNode
		for (ASTNode child : root.children()) {
			if (child instanceof OmpNode) // recognize and process OmpNode
				recognizeOmpInstructions((OmpNode) child);
			else if (child instanceof FunctionCallNode)
				recognizeOmpFunctionCalls((FunctionCallNode) child);
			else if (child != null) // Explore non-OmpNode
				searchOmpInstructions(child);
		}
	}

	private List<List<BlockItemNode>> transOmpReduction(
			SequenceNode<OmpReductionNode> reductionClauses) {
		String srcMethod = SRC_INFO + ".transOmpReduction";
		Source ptrSrc = newSource(srcMethod, CivlcTokenConstant.POINTER);
		Source declSrc = newSource(srcMethod, CivlcTokenConstant.DECLARATION);
		Source exprSrc = newSource(srcMethod, CivlcTokenConstant.EXPR);
		List<BlockItemNode> varPtrDecls = new LinkedList<>();
		List<BlockItemNode> varTmpDecls = new LinkedList<>();
		List<BlockItemNode> varRdcInits = new LinkedList<>();
		List<BlockItemNode> initialItems = new LinkedList<>();
		List<BlockItemNode> combineItems = new LinkedList<>();
		Set<String> rcVarDeclName = new HashSet<>();
		OmpSymbolReductionNode symbRc = null;
		OmpReductionOperator rcOp = null;

		if (reductionClauses == null)
			return Arrays.asList(initialItems, combineItems);
		for (OmpReductionNode rc : reductionClauses) {
			// Trans each reduction clause
			symbRc = (OmpSymbolReductionNode) rc;
			rcOp = symbRc.operator();

			for (IdentifierExpressionNode rcVar : symbRc.variables()) {
				String vName = rcVar.getIdentifier().name();
				String vpName = REDUCTION_ + ctrOmpReductionItem + "_" + vName;
				TypeNode vType = ((VariableDeclarationNode) ( //
				(Variable) rcVar.getIdentifier().getEntity())
						.getFirstDeclaration()).getTypeNode().copy();
				TypeNode vpType = nodeFactory.newPointerTypeNode( //
						ptrSrc, vType.copy());
				ExpressionNode vpInit = nodeFactory.newOperatorNode(exprSrc,
						Operator.ADDRESSOF, nodeExprId(srcMethod, vName));
				// type *_omp_reduction_x_var = &var;
				VariableDeclarationNode vpDecl = nodeFactory
						.newVariableDeclarationNode(declSrc, //
								nodeIdent(srcMethod, vpName), vpType, vpInit);
				// type var;
				VariableDeclarationNode vDecl = nodeFactory
						.newVariableDeclarationNode(declSrc, //
								nodeIdent(srcMethod, vName), vType);
				// var = [omp_priv];
				ExpressionNode varInitExpr = nodeFactory.newOperatorNode(
						exprSrc, Operator.ASSIGN, nodeExprId(srcMethod, vName),
						nodeExprReductionInit(srcMethod, rcOp, vType));
				ExpressionStatementNode varInitStmt = nodeFactory
						.newExpressionStatementNode(varInitExpr);
				// $omp_rdc(OPERATOR, VAR_PTR, VAR_TMP);
				ExpressionNode combFuncCall = nodeExprCall(srcMethod,
						OMP_REDUCTION_COMBINE,
						Arrays.asList(nodeExprInt(srcMethod, rcOp.civlOp()),
								nodeExprId(srcMethod, vpName),
								nodeExprAddrOf(srcMethod,
										nodeExprId(srcMethod, vName))));
				ExpressionStatementNode combineItem = nodeFactory
						.newExpressionStatementNode(combFuncCall);

				varPtrDecls.add(vpDecl);
				if (!rcVarDeclName.contains(vName)) {
					varTmpDecls.add(vDecl);
					rcVarDeclName.add(vName);
				} else
					throw new CIVLSyntaxException("Reduction item identifier "
							+ vName + "shall appear only once in data clause.");
				varRdcInits.add(varInitStmt);
				combineItems.add(combineItem);
			}
			ctrOmpReductionItem++;
		}
		initialItems.addAll(varPtrDecls);
		initialItems.addAll(varTmpDecls);
		initialItems.addAll(varRdcInits);
		return Arrays.asList(initialItems, combineItems);
	}

	/**
	 * Return a list of {@link BlockItemNode} transformed from each of
	 * structured-blocks<br>
	 * (see:
	 * https://vsl.cis.udel.edu/trac/civl/wiki/Next-GenOpenMPTransformation )
	 * 
	 * @param sectionBlocks
	 *            a list of {@link StatementNode} representing each associated
	 *            section block
	 * @return see above
	 * @throws SyntaxException
	 */
	private List<BlockItemNode> transOmpSection(
			List<StatementNode> sectionBlocks) throws SyntaxException {
		// sections {
		// [section] { BLOCK0 }
		// [section { BLCOK1 }] ..
		// }
		/* **** **** is transformed to **** **** */
		// $for(int _omp_sid : _omp_sec_dist) {
		// if (_omp_sid==0) { BLOCK0 }
		// if (_omp_sid==1) { BLCOK1 } ..
		// }
		String srcMethod = SRC_INFO + ".transOmpSection";
		Source exprSrc = newSource(srcMethod, CivlcTokenConstant.EXPR);
		Source ifSrc = newSource(srcMethod, CivlcTokenConstant.STATEMENT);
		List<BlockItemNode> sections = new LinkedList<>();
		ExpressionNode condExpr = null;
		int sectionId = 0;

		for (StatementNode block : sectionBlocks) {
			// _omp_sid == N
			condExpr = nodeFactory.newOperatorNode(exprSrc, Operator.EQUALS,
					nodeExprId(srcMethod, SID),
					nodeExprInt(srcMethod, sectionId++));
			searchOmpInstructions(block);
			block.remove();
			// if (_omp_sid == N) { BLOCK_N }
			sections.add(nodeFactory.newIfNode(ifSrc, condExpr, block));
		}
		return sections;
	}

	// Public functions or interfaces

	/**
	 * Transform an AST of a OpenMP program in C into an equivalent AST of
	 * CIVL-C program.<br>
	 * 
	 * @param oldAst
	 *            The AST of the original OpenMP program in C.
	 * @return An AST of CIVL-C program equivalent to the original OpenMP
	 *         program.
	 * @throws SyntaxException
	 */
	@Override
	public AST transform(AST oldAst) throws SyntaxException {
		assert super.astFactory == oldAst.getASTFactory();
		assert super.nodeFactory == astFactory.getNodeFactory();
		// Check the inclusion of CIVL's OpenMP Implementation file.
		if (!super.hasHeader(oldAst, CIVLConstants.CIVL_OMP_IMP))
			return oldAst;
		root = oldAst.getRootNode();
		oldAst.release();

		String srcMethod = SRC_INFO;
		// A list holding all processed block items for building new AST.
		List<BlockItemNode> newItems = new LinkedList<>();
		List<BlockItemNode> oldItems = new LinkedList<>();
		List<BlockItemNode> importedItems = new LinkedList<>();
		List<BlockItemNode> declaredItems = new LinkedList<>();
		List<String> ioVarNames = new LinkedList<>();
		String srcFile = null;

		// Search and Transform all OpenMP Nodes
		searchOmpInstructions((ASTNode) root);
		reduceDuplicateNode(root, PREDICATE_BARRIER_AND_FLUSH);
		for (BlockItemNode item : root) {
			if (item == null)
				continue;
			item.remove();
			srcFile = item.getSource().getFirstToken().getSourceFile()
					.getName();
			if (srcFile.equals("stdio.h")) {
				if (item.nodeKind() == NodeKind.VARIABLE_DECLARATION)
					oldItems.add(item);
				else
					importedItems.add(item);
			} else if (isImported(srcFile))
				// Extract items imported from library files.
				importedItems.add(item);
			else if (isRelatedAssumptionNode(item, ioVarNames))
				// Extract assumptions related with input/output var. decl.
				declaredItems.add(item);
			else if (item.nodeKind() == NodeKind.VARIABLE_DECLARATION) {
				VariableDeclarationNode varDecl = (VariableDeclarationNode) item;
				TypeNode varType = varDecl.getTypeNode();

				if (varType.isInputQualified() || varType.isOutputQualified()) {
					// Extract input/output var. decl.
					ioVarNames.add(varDecl.getName());
					declaredItems.add(item);
				} else
					oldItems.add(item);
			} else
				oldItems.add(item);
		}

		/* **** **** **** **** Build the new AST **** **** **** **** */
		// ADD: Nodes from header files
		newItems.addAll(importedItems);
		// ADD: Nodes of input/output var. dec. and their assumptions
		newItems.addAll(declaredItems);
		// ADD: $input int _omp_thread_max
		newItems.add(declOmpThreadMax(srcMethod));
		// ADD: decls for OpenMP critical variables
		for (String cVName : criticalVarNames)
			newItems.add(declOmpCriticalVar(srcMethod, cVName));
		// ADD: int omp_num_threads = _omp_thread_max;
		newItems.add(declOmpNumThreads(srcMethod));
		// ADD: transformed Civl AST nodes from the old AST.
		newItems.addAll(oldItems);

		// CREATE: a new AST from the old one by transforming all OpenMP nodes
		SequenceNode<BlockItemNode> newRoot = nodeFactory
				.newSequenceNode(root.getSource(), "Omp2CivlProgram", newItems);
		AST newAst = astFactory.newAST(newRoot, oldAst.getSourceFiles(),
				oldAst.isWholeProgram());

		completeSources(newRoot);
		return newAst;
	}
}

class OmpRegion {
	protected enum OmpRgnKind {
		PARALLEL, // parallel
		SECTIONS, SECTION, SINGLE, WORKSHARE, // workshare
		FOR, SIMD, SIMD_DECL, LOOP, // loop
		BARRIER, CRITICAL, ATOMIC, MASTER, ORDERED, // sync
	}

	OmpRgnKind ompRgns[];

	OmpRegion(OmpRgnKind... OmpRegions) {
		assert OmpRegions.length > 0;
		this.ompRgns = OmpRegions;
	}

	OmpRgnKind[] getOmpRegions() {
		return this.ompRgns;
	}
}

class OmpLoopInfo {
	String loopVarName;
	Triple<ExpressionNode, ExpressionNode, ExpressionNode> range;

	OmpLoopInfo(String varName,
			Triple<ExpressionNode, ExpressionNode, ExpressionNode> range) {
		this.loopVarName = varName;
		this.range = range;
	}
}

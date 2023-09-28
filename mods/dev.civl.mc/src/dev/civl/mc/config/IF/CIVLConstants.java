package dev.civl.mc.config.IF;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import dev.civl.mc.model.IF.CIVLProperty;
import dev.civl.gmc.Option;
import dev.civl.gmc.Option.OptionType;

/**
 * This class manages all constant configurations of the system.
 * 
 * NOTE: when you add a new option, add it here, give it name ending in "O",
 * like the others, AND add it to the list in method {@link #getAllOptions()}.
 * And keep them in alphabetical order.
 * 
 * @author Manchun Zheng
 * 
 */
public class CIVLConstants {

	/**
	 * Kinds of deadlock: absolute, potential or none.
	 * 
	 * @author Manchun Zheng
	 *
	 */
	public enum DeadlockKind {
		ABSOLUTE, POTENTIAL, NONE
	}

	/**
	 * Error state equivalence semantics for suppressing logging of redundant
	 * errors.
	 * 
	 * @author Matt Dwyer
	 */
	public enum ErrorStateEquivalence {
		LOC, // Require the current location to match
		CALLSTACK, // Require the call stacks to match
		FULL // Require the full trace to match
	}

	/**
	 * The different MPI implementation model that CIVL provides.
	 * 
	 * @author ziqingluo
	 */
	public enum MPIModelKind {
		/* The default one, simple but only supports blocking operations */
		BLOCKING,
		/* supports both blocking and non-blocking operations but verbose */
		NON_BLOCKING,
		/* used for the contract-based verification */
		CONTRACT;

		/**
		 * maps string literal option value to {@link MPIModelKind}.
		 */
		static public MPIModelKind select(String name) {
			switch (name) {
				case "nonblocking" :
					return NON_BLOCKING;
				case "contract" :
					return CONTRACT;
				case "blocking" :
				default :
					return BLOCKING; // default value
			}
		}
	}

	/**
	 * The common root of the paths of all resources.
	 */
	public final static String ROOT_RESOURCE_PATH_STR = "dev/civl/";

	/**
	 * Resource path to the CIVL library implementations files (.cvl).
	 */
	public final static File CIVL_LIB_SRC_PATH = new File(
			ROOT_RESOURCE_PATH_STR + "mc/src/");

	/**
	 * Where the CIVL header files (suffix .h and .cvh) are located. This path
	 * is relative to the class path.
	 */
	public final static File CIVL_LIB_INCLUDE_PATH = new File(
			ROOT_RESOURCE_PATH_STR + "abc/include/");

	/** The version of this release of CIVL. */
	public final static String version = "1.22";

	/**
	 * The date of this release of CIVL. Format: YYYY-MM-DD in accordance with
	 * ISO 8601.
	 */
	public final static String date = "2023-02-21";

	/**
	 * The prefix of the full name of the class of a library enabler/executor.
	 */
	public final static String LIBRARY_PREFIX = "dev.civl.mc.library.";

	/**
	 * A string printed before and after titles of sections of output to make
	 * them stand out among the clutter.
	 */
	public final static String bar = "===================";

	public final static String statsBar = "===";

	/**
	 * The int value of the char '\0', which represents the end of string.
	 */
	public final static int EOS = 0;

	/**
	 * The name of the directory into which CIVL will store the artifacts it
	 * generates.
	 */
	public final static String CIVLREP = "CIVLREP";

	/**
	 * Number of seconds between printing of update messages.
	 */
	public final static int consoleUpdatePeriod = 15;

	/**
	 * Number of seconds between saving update messages to disk when in web app
	 * mode.
	 */
	public final static int webUpdatePeriod = 1;

	// Option names
	public static String DEBUG = "debug";

	public static String TIMEOUT = "timeout";

	public static String ENABLE_PRINTF = "enablePrintf";

	public static String ERROR_BOUND = "errorBound";

	public static String MAX_PROCS = "maxProcs";

	public static String ERROR_STATE_EQUIV = "errorStateEquiv";

	public static String GUIDED = "guided";

	public static String ID = "id";

	public static String INPUT = "input";

	public static String MAX_DEPTH = "maxdepth";

	public static String MIN = "min";

	public static String MPI_CONTRACT = "mpiContract"; // TODO: change to
														// general "-contract"

	public static String MPI_MODEL = "mpi";

	public static String LOOP_INV = "loop";
	public static String PROC_BOUND = "procBound";
	public static String RANDOM = "random";
	public static String SAVE_STATES = "saveStates";
	public static String SEED = "seed";
	public static String ANALYZE_ABS = "analyze_abs";
	public static String AST = "ast";
	public static String UNPREPROC = "unpreproc";
	public static String SHOW_AMPLE_SET = "showAmpleSet";
	public static String SHOW_AMPLE_SET_STATES = "showAmpleSetWtStates";
	public static String SHOW_MEM_UNITS = "showMemoryUnits";
	public static String SHOW_MODEL = "showModel";
	public static String SHOW_PROVER_QUERIES = "showProverQueries";
	public static String SHOW_QUERIES = "showQueries";
	public static String SHOW_SAVED_STATES = "showSavedStates";
	public static String SHOW_STATES = "showStates";
	public static String SHOW_TIME = "showTime";
	public static String SHOW_TRANSITIONS = "showTransitions";
	public static String SHOW_UNREACHED = "showUnreached";
	public static String SIMPLIFY = "simplify";
	public static String SOLVE = "solve";
	public static String STATELESS_PRINTF = "statelessPrintf";
	public static String STRICT = "strict";
	public static String SYS_INCLUDE_PATH = "sysIncludePath";
	public static String TRACE = "trace";
	public static String USER_INCLUDE_PATH = "userIncludePath";
	public static String VERBOSE = "verbose";
	public static String GUI = "gui";
	public static String SVCOMP16 = "svcomp16";
	public static String SVCOMP17 = "svcomp17";
	public static String SHOW_INPUTS = "showInputs";
	public static String PREPROC = "preproc";
	public static String PROB = "prob";
	public static String SHOW_PROGRAM = "showProgram";
	public static String SHOW_PATH_CONDITION = "showPathCondition";
	public static String OMP_NO_SIMPLIFY = "ompNoSimplify";
	public static String OMP_ONLY_SIMPLIFIER = "ompOnlySimplifier";
	public static String COLLECT_OUTPUT = "collectOutput";
	public static String COLLECT_PROCESSES = "collectProcesses";
	public static String COLLECT_SCOPES = "collectScopes";
	public static String COLLECT_SYMBOLIC_CONSTANTS = "collectSymbolicConstants";
	public static String COLLECT_HEAPS = "collectHeaps";
	public static String LINK = "link";
	public static String MACRO = "D";
	public static String WEB = "web";
	public static String OMP_LOOP_DECOMP = "ompLoopDecomp";
	public static String CIVL_MACRO = "_CIVL";
	public static String QUIET = "quiet";
	public static String INT_OPERATION_TRANSFORMER = "intOperationTransformer";
	public static String SLICE_ANALYSIS = "sliceAnalysis";
	public static String WITNESS = "witness";
	public static String DIRECT = "direct";
	public static String INTBIT = "int_bit";
	public static String TEST_GEN = "testGen";
	public static String CYCLES_VIOLATE = "cyclesViolate";
	public static String RUNTIME_UPDATE = "runtimeUpdate";
	public static String PREEMPTION_BOUND = "preemptionBound";
	public static String DISABLE_LOCAL_BLOCK = "disableLocalBlock";

	// Option objects
	/**
	 * Default option value for -mpiContract option {@link #mpiContractO}
	 */
	public final static String CONTRACT_CHECK_ALL = "_CIVL_CONTRACT_ALL";

	/**
	 * Default option value for -mpiContract option {@link #mpiContractO}
	 */
	public final static String CONTRACT_CHECK_NONE = "_CIVL_CONTRACT_NONE";

	/**
	 * Debug option, false by default.
	 */
	public final static Option debugO = Option.newScalarOption(DEBUG,
			OptionType.BOOLEAN, "debug mode: print very detailed information",
			false);

	public final static Option timeoutO = Option.newScalarOption(TIMEOUT,
			OptionType.INTEGER,
			"time out in seconds, default is never time out", -1);

	/**
	 * Enables printf? true by default. When false, nothing is printed for
	 * printf function.
	 */
	public final static Option enablePrintfO = Option.newScalarOption(
			ENABLE_PRINTF, OptionType.BOOLEAN, "enable printf function", true);

	/**
	 * The maximal number of errors allowed before terminating CIVL. 1 by
	 * default.
	 */
	public final static Option errorBoundO = Option.newScalarOption(ERROR_BOUND,
			OptionType.INTEGER, "stop after finding this many errors", 1);

	public final static Option maxProcsO = Option.newScalarOption(MAX_PROCS,
			OptionType.INTEGER, "the maximum number of processes", 1000);

	/**
	 * The semantics for used to determine when error states are equivalent;
	 * CIVL suppresses logging of equivalent states. All semantics use the kind
	 * of error, but they may vary in the portion of the state that is checked.
	 * Current options include using the current location (LOC), the call stacks
	 * (CALLSTACK), and the full trace (FULL), but others are possible. LOC by
	 * default.
	 */
	public final static Option errorStateEquivO = Option.newScalarOption(
			ERROR_STATE_EQUIV, OptionType.STRING,
			"semantics for equivalent error states: (LOC|CALLSTACK|FULL)",
			"LOC");

	/**
	 * User guided simulation?
	 */
	public final static Option guidedO = Option.newScalarOption(GUIDED,
			OptionType.BOOLEAN,
			"user guided simulation; applies only to run command",
			null);

	/**
	 * The id of the trace for replay, 0 by default.
	 */
	public final static Option idO = Option.newScalarOption(ID,
			OptionType.INTEGER,
			"ID number of trace to replay; applies only to replay command", 0);

	/**
	 * Specify values of input variables.
	 */
	public final static Option inputO = Option.newMapOption(INPUT,
			"initialize input variable KEY to VALUE; applies only to run and verify");

	/**
	 * The maximal depth for search. Infinite by default.
	 */
	public final static Option maxdepthO = Option.newScalarOption(MAX_DEPTH,
			OptionType.INTEGER, "bound on search depth", Integer.MAX_VALUE);

	/**
	 * Search for the minimum counterexample? false by default.
	 */
	public final static Option minO = Option.newScalarOption(MIN,
			OptionType.BOOLEAN, "search for minimal counterexample", false);

	/**
	 * MPI contract mode? Disable by default.
	 */
	public final static Option mpiContractO = Option.newScalarOption(
			MPI_CONTRACT, OptionType.STRING,
			"Name of annotated MPI function.",
			CONTRACT_CHECK_NONE);

	/**
	 * Chooses MPI implementation models (see {@link MPIModelKind}).
	 * {@link MPIModelKind#BLOCKING} is the default setting.
	 */
	public final static Option mpiModelO = Option.newScalarOption(MPI_MODEL,
			OptionType.STRING,
			"MPI implementation model. Available values: blocking, nonblocking, contract",
			"blocking");

	/**
	 * Enable all settings that are required for verifying with loop invariants.
	 * Disable by default.
	 */
	public final static Option loopO = Option.newScalarOption(LOOP_INV,
			OptionType.BOOLEAN,
			"Enable all settings that are required for verifying with loop invariants",
			false);

	/**
	 * The bound on number of live processes (no bound if negative). No bound by
	 * default.
	 */
	public final static Option procBoundO = Option.newScalarOption(PROC_BOUND,
			OptionType.INTEGER,
			"bound on number of live processes (no bound if negative)", -1);

	/**
	 * Use probabilistic techniques for verifying numeric identifies. False by
	 * default.
	 */
	public final static Option probO = Option.newScalarOption(PROB,
			OptionType.BOOLEAN,
			"use probabilistic techniques for verifying numeric identifies",
			false);

	public final static Option randomO = Option.newScalarOption(RANDOM,
			OptionType.BOOLEAN,
			"select enabled transitions randomly",
			null);

	/**
	 * set <code>false</code> to disable CIVL {@link UpdaterRunnable} thread.
	 * The default value is <code>true</code>
	 */
	public final static Option runtimeUpdateO = Option.newScalarOption(
			RUNTIME_UPDATE, OptionType.BOOLEAN,
			"print update-info periodically?",
			true);

	/**
	 * Save states during depth-first search? true by default.
	 */
	public final static Option saveStatesO = Option.newScalarOption(SAVE_STATES,
			OptionType.BOOLEAN, "save states during depth-first search", true);

	/**
	 * Set the random seed for run mode.
	 */
	public final static Option seedO = Option.newScalarOption(SEED,
			OptionType.INTEGER, "set the random seed; applies only to run",
			null);

	/**
	 * Set the upper bound of integers.
	 */
	public final static Option intBit = Option.newScalarOption(INTBIT,
			OptionType.INTEGER, "set the number of bits of integer", 32);

	/**
	 * Analyze abs calls? false by default.
	 */
	public final static Option analyzeAbsO = Option.newScalarOption(ANALYZE_ABS,
			OptionType.BOOLEAN, "analyze abs calls? false by default", false);

	/**
	 * Show the AST of the program? false by default.
	 */
	public final static Option astO = Option.newScalarOption(AST,
			OptionType.BOOLEAN, "print the AST of the program", false);

	/**
	 * Print the ample set when it contains more than one processes? false by
	 * default.
	 */
	public final static Option showAmpleSetO = Option.newScalarOption(
			SHOW_AMPLE_SET, OptionType.BOOLEAN,
			"print the ample set when it contains more than one processes",
			false);

	/**
	 * Print ample set and state when ample set contains more than one
	 * processes? false by default.
	 */
	public final static Option showAmpleSetWtStatesO = Option.newScalarOption(
			SHOW_AMPLE_SET_STATES, OptionType.BOOLEAN,
			"print ample set and state when ample set contains >1 processes",
			false);

	/**
	 * Print the impact/reachable memory units when the state contains more than
	 * one processes? false by default.
	 */
	public final static Option showMemoryUnitsO = Option.newScalarOption(
			SHOW_MEM_UNITS, OptionType.BOOLEAN,
			"print the impact/reachable memory units when the state contains more than one processes",
			false);

	/**
	 * Show the CIVL model of the program? false by default.
	 */
	public final static Option showModelO = Option.newScalarOption(SHOW_MODEL,
			OptionType.BOOLEAN, "print the model", false);

	/**
	 * Show theorem prover queries? false by default.
	 */
	public final static Option showProverQueriesO = Option.newScalarOption(
			SHOW_PROVER_QUERIES, OptionType.BOOLEAN,
			"print theorem prover queries only", false);

	/**
	 * Show all SARL queries? false by default.
	 */
	public final static Option showQueriesO = Option.newScalarOption(
			SHOW_QUERIES, OptionType.BOOLEAN, "print all queries", false);

	/**
	 * Show all states that are saved? false by default.
	 */
	public final static Option showSavedStatesO = Option.newScalarOption(
			SHOW_SAVED_STATES, OptionType.BOOLEAN, "print saved states only",
			false);

	/**
	 * Show all states? false by default.
	 */
	public final static Option showStatesO = Option.newScalarOption(SHOW_STATES,
			OptionType.BOOLEAN, "print all states", false);

	/**
	 * Show the time used by each translation phase? false by default.
	 */
	public final static Option showTimeO = Option.newScalarOption(SHOW_TIME,
			OptionType.BOOLEAN, "print timings", false);

	/**
	 * Show all transitions? false by default;
	 */
	public final static Option showTransitionsO = Option.newScalarOption(
			SHOW_TRANSITIONS, OptionType.BOOLEAN, "print transitions", false);

	/**
	 * Show unreachable code? false by default;
	 */
	public final static Option showUnreachedCodeO = Option.newScalarOption(
			SHOW_UNREACHED, OptionType.BOOLEAN, "print the unreachable code",
			false);

	/**
	 * Simplify states using path conditions? true by default.
	 */
	public final static Option simplifyO = Option.newScalarOption(SIMPLIFY,
			OptionType.BOOLEAN, "simplify states?", true);

	/**
	 * Try to solve for concrete counterexample? false by default.
	 */
	public final static Option solveO = Option.newScalarOption(SOLVE,
			OptionType.BOOLEAN, "try to solve for concrete counterexample",
			false);

	/**
	 * Don't modify file system when running printf? true by default.
	 */
	public final static Option statelessPrintfO = Option.newScalarOption(
			STATELESS_PRINTF, OptionType.BOOLEAN,
			"prevent printf function modifying the file system", true);

	/**
	 * Print the impact/reachable memory units when the state contains more than
	 * one processes? false by default.
	 */
	public final static Option strictCompareO = Option.newScalarOption(STRICT,
			OptionType.BOOLEAN, "check strict functional equivalence?", true);

	/**
	 * Set the system include path.
	 */
	public final static Option sysIncludePathO = Option.newScalarOption(
			SYS_INCLUDE_PATH, OptionType.STRING,
			"set the system include path, using : to separate multiple paths",
			null);

	/**
	 * Unpreprocess the source? false by default.
	 */
	public final static Option unpreprocO = Option.newScalarOption(UNPREPROC,
			OptionType.BOOLEAN, "unpreprocess the source?", false);

	/**
	 * File name of trace to replay
	 */
	public final static Option traceO = Option.newScalarOption(TRACE,
			OptionType.STRING, "filename of trace to replay", null);

	/**
	 * Sets user include path.
	 */
	public final static Option userIncludePathO = Option.newScalarOption(
			USER_INCLUDE_PATH, OptionType.STRING,
			"set the user include path, using : to separate multiple paths",
			null);

	/**
	 * Verbose mode? false by default
	 */
	public final static Option verboseO = Option.newScalarOption(VERBOSE,
			OptionType.BOOLEAN, "verbose mode", false);

	/**
	 * Perform svcomp16 transformation? false by default.
	 */
	public final static Option svcomp16O = Option.newScalarOption(SVCOMP16,
			OptionType.BOOLEAN, "translate program for sv-comp 2016?", false);

	/**
	 * Perform svcomp transformation? false by default.
	 */
	public final static Option svcomp17O = Option.newScalarOption(SVCOMP17,
			OptionType.BOOLEAN, "translate program for sv-comp 2017?", false);

	/**
	 * Show the input variables of this model? false by default.
	 */
	public final static Option showInputVarsO = Option.newScalarOption(
			SHOW_INPUTS, OptionType.BOOLEAN,
			"show input variables of my program?", false);

	/**
	 * Show the preprocessing result? false by default.
	 */
	public final static Option preprocO = Option.newScalarOption(PREPROC,
			OptionType.BOOLEAN, "show the preprocessing result?", false);

	/**
	 * Show the program after all applicable transformations? false by default.
	 */
	public final static Option showProgramO = Option.newScalarOption(
			SHOW_PROGRAM, OptionType.BOOLEAN,
			"show my program after transformations?", false);

	/**
	 * Show the path condition of each state? false by default.
	 */
	public final static Option showPathConditionO = Option.newScalarOption(
			SHOW_PATH_CONDITION, OptionType.STRING,
			"show path condition of each state as one line (LINE) or on multiple lines (BLOCK)?",
			"LINE");

	/**
	 * Don't simplify OpenMP pragmas? false by default.
	 */
	public final static Option ompNoSimplifyO = Option.newScalarOption(
			OMP_NO_SIMPLIFY, OptionType.BOOLEAN, "don't simplify omp pragmas",
			true);

	/**
	 * Only relies on the OpenMP simplifier ? i.e., either simplify an omp
	 * program or report possible data-race
	 */
	public final static Option ompOnlySimplifierO = Option.newScalarOption(
			OMP_ONLY_SIMPLIFIER, OptionType.BOOLEAN,
			"rely on the OpenMP simplifier only, i.e. no data-race checking.",
			false);

	/**
	 * Collect output? false by default.
	 */
	public final static Option collectOutputO = Option.newScalarOption(
			COLLECT_OUTPUT, OptionType.BOOLEAN, "collect output?", false);

	/**
	 * Collect processes? true by default.
	 */
	public final static Option collectProcessesO = Option.newScalarOption(
			COLLECT_PROCESSES, OptionType.BOOLEAN, "collect processes?", true);

	/**
	 * Collect scopes? true by default.
	 */
	public final static Option collectScopesO = Option.newScalarOption(
			COLLECT_SCOPES, OptionType.BOOLEAN, "collect dyscopes?", true);

	/**
	 * Collect symbolic constants ? false by default.
	 */
	public final static Option collectSymbolicConstantsO = Option
			.newScalarOption(COLLECT_SYMBOLIC_CONSTANTS, OptionType.BOOLEAN,
					"collect symbolic constant?", false);

	/**
	 * Collect heaps? true by default.
	 */
	public final static Option collectHeapsO = Option.newScalarOption(
			COLLECT_HEAPS, OptionType.BOOLEAN, "collect heaps?", true);

	/**
	 * Link a source file with the target program.
	 */
	public final static Option linkO = Option.newScalarOption(LINK,
			OptionType.STRING, "link a source file with the target program",
			null);

	/**
	 * Define macros.
	 */
	public final static Option macroO = Option.newMapOption(MACRO,
			"macro definitions: <macro> or <macro>=<object>");

	/**
	 * Write output for web app? false by default.
	 */
	public final static Option webO = Option.newScalarOption(WEB,
			OptionType.BOOLEAN, "write output for web app?", false);

	/**
	 * Set the loop decomposition strategy for OpenMP transformer. Round robin
	 * by default.
	 */
	public final static Option ompLoopDecompO = Option.newScalarOption(
			OMP_LOOP_DECOMP, OptionType.STRING,
			"loop decomposition strategy? (ALL|ROUND_ROBIN|RANDOM)",
			"ROUND_ROBIN");

	/**
	 * Collect heaps? true by default.
	 */
	public final static Option CIVLMacroO = Option.newScalarOption(CIVL_MACRO,
			OptionType.BOOLEAN, "Define _CIVL macro?", true);

	/**
	 * Ignore the output? false by default.
	 */
	public final static Option quietO = Option.newScalarOption(QUIET,
			OptionType.BOOLEAN, "ignore output?", false);

	/**
	 * apply int operation transformer? true by default.
	 */
	public final static Option intOperationTransformer = Option.newScalarOption(
			INT_OPERATION_TRANSFORMER, OptionType.BOOLEAN,
			"apply int operation transformer?", false);

	/**
	 * Perform slice analysis on trace? false by default.
	 */
	public final static Option sliceAnalysisO = Option.newScalarOption(
			SLICE_ANALYSIS, OptionType.BOOLEAN,
			"Perform slice analysis on trace?", false);

	/**
	 * Generate witness from trace? false by default.
	 */
	public final static Option witnessO = Option.newScalarOption(WITNESS,
			OptionType.BOOLEAN, "Generate witness from trace?", false);

	/**
	 * Inject instrumentation to direct the branches at the line numbers in
	 * given file so as to explore a sub-space of execution. Note: currently
	 * assumes you are given one C file (no linking)
	 */
	public final static Option direct0 = Option.newScalarOption(DIRECT,
			OptionType.STRING,
			"Direct branching at line numbers in the given file", null);

	/**
	 * An option to enable test generation for SARL, i.e. generate SARL tests
	 * for some validity checks that CIVL encounters during a run.
	 */
	public final static Option SARLTestGenO = Option.newScalarOption(TEST_GEN,
			OptionType.BOOLEAN,
			"Generating SARL Junit tests for some validity tests that CIVL encountered",
			false);

	public final static Option preemptionBoundO = Option.newScalarOption(
			PREEMPTION_BOUND, OptionType.INTEGER, "preemption bound", -1);

	/**
	 * Disable the local block, which optimizes the POR impl. false by default
	 */
	public final static Option disableLocalBlockO = Option.newScalarOption(
			DISABLE_LOCAL_BLOCK, OptionType.BOOLEAN, "disable local block",
			false);

	/**
	 * The name of the CIVL system function, which is the starting point of a
	 * CIVL model.
	 */
	public final static String civlSystemFunction = "main";

	/**
	 * Returns all options defined for CIVL in alphabetic order.
	 * 
	 * @return all options defined for CIVL in alphabetic order.
	 */
	public final static Option[] getAllOptions() {
		return Stream.concat(Stream.of(astO, collectHeapsO, collectProcessesO,
				collectScopesO, collectSymbolicConstantsO, debugO,
				enablePrintfO, errorBoundO, errorStateEquivO, guidedO, idO,
				inputO, linkO, loopO, macroO, maxdepthO, minO, mpiContractO,
				mpiModelO, ompLoopDecompO, ompNoSimplifyO, ompOnlySimplifierO,
				probO, preprocO, procBoundO, randomO, runtimeUpdateO,
				saveStatesO, seedO, showAmpleSetO, showAmpleSetWtStatesO,
				showInputVarsO, showMemoryUnitsO, showModelO,
				showPathConditionO, showProgramO, showProverQueriesO,
				showQueriesO, showSavedStatesO, showStatesO, showTimeO,
				showTransitionsO, showUnreachedCodeO, simplifyO, solveO,
				statelessPrintfO, svcomp16O, svcomp17O, quietO, sysIncludePathO,
				traceO, userIncludePathO, verboseO, webO, CIVLMacroO,
				analyzeAbsO, strictCompareO, collectOutputO, timeoutO,
				unpreprocO, sliceAnalysisO, witnessO, direct0, intBit,
				intOperationTransformer, maxProcsO, SARLTestGenO,
				preemptionBoundO, disableLocalBlockO),
				CIVLProperty.getAllConfigurableProperties().stream()
						.map(e -> e.getOption()))
				.toArray(Option[]::new);
	}

	/*** Library headers ***/
	// Standard library...
	public final static String SYS_MMAN = "sys/mman.h";
	public final static String SYS_RESOURCE = "sys/resource.h";
	public final static String SYS_TIME = "sys/time.h";
	public final static String SYS_TIMES = "sys/times.h";
	public final static String SYS_TYPES = "sys/types.h";

	public final static String ASSERT = "assert.h";
	public final static String COMPLEX = "complex.h";
	public final static String CTYPE = "ctype.h";
	public final static String CUDA_RUNTIME_API = "cuda_runtime_api.h";
	public final static String CUDA = "cuda.h";
	public final static String ERRNO = "errno.h";
	public final static String FENV = "fenv.h";
	public final static String FLOAT = "float.h";
	public final static String GD_IO = "gd_io.h";
	public final static String GD = "gd.h";
	public final static String GDFX = "gdfx.h";
	public final static String GNUC = "gnuc.h";
	public final static String INTTYPES = "inttypes.h";
	public final static String ISO646 = "iso646.h";
	public final static String LIMITS = "limits.h";
	public final static String LOCALE = "locale.h";
	public final static String MATH = "math.h";
	public final static String MPI = "mpi.h";
	public final static String OMP = "omp.h";
	public final static String OP = "op.h";
	public final static String PTHREAD = "pthread.h";
	public final static String SCHED = "sched.h";
	public final static String SETJMP = "setjmp.h";
	public final static String SIGNAL = "signal.h";
	public final static String STDALIGN = "stdalign.h";
	public final static String STDARG = "stdarg.h";
	public final static String STDATOMIC = "stdatomic.h";
	public final static String STDBOOL = "stdbool.h";
	public final static String STDDEF = "stddef.h";
	public final static String STDINT = "stdint.h";
	public final static String STDIO = "stdio.h";
	public final static String STDLIB = "stdlib.h";
	public final static String STDNORETURN = "stdnoreturn.h";
	public final static String STRING = "string.h";
	public final static String STRINGS = "strings.h";
	public final static String TGMATH = "tgmath.h";
	public final static String THREADS = "threads.h";
	public final static String TIME = "time.h";
	public final static String UCHAR = "uchar.h";
	public final static String UNISTD = "unistd.h";
	public final static String WCHAR = "wchar.h";
	public final static String WCTYPE = "wctype.h";

	// CIVL library
	public final static String BUNDLE = "bundle.cvh";
	public final static String CIVLC = "civlc.cvh";
	public final static String CIVL_CUDA = "civl-cuda.cvh";
	public final static String CIVL_MPI = "civl-mpi.cvh";
	public final static String CIVL_MPI_BLOCKING = "civl-mpi-blocking.cvh";
	public final static String CIVL_MPI_NONBLOCKING = "civl-mpi-nonblocking.cvh";
	public final static String CIVL_OMP = "civl-omp.cvh";
	public final static String CIVL_PTHREAD = "civl-pthread.cvh";
	public final static String CIVL_STDIO = "civl-stdio.cvh";
	public final static String COLLATE = "collate.cvh";
	public final static String COMM = "comm.cvh";
	public final static String COMM2 = "comm2.cvh";
	public final static String CONCURRENCY_CONTRACT = "concurrency_contract.cvh";
	public final static String CONCURRENCY = "concurrency.cvh";
	public final static String DOMAIN = "domain.cvh";
	public final static String FORTRAN_ARRAY = "fortran_array.cvh";
	public final static String FORTRAN_SIGP = "fortran_sigp.cvh";
	public final static String LOOP_ASSIGNS_GEN = "loop_assigns_gen.cvh";
	public final static String MEM = "mem.cvh";
	public final static String MEMORY = "memory.cvh";
	public final static String POINTER = "pointer.cvh";
	public final static String SCOPE = "scope.cvh";
	public final static String SEQ = "seq.cvh";

	public final static String SVCOMP = "svcomp.h";

	/*** Library source files ***/
	// Standard library...
	public final static String ASSERT_SRC = "assert.cvl";
	public final static String CUDA_SRC = "cuda.cvl";
	public final static String MATH_SRC = "math.cvl";
	public final static String MPI_SRC = "mpi.cvl";
	public final static String OMP_SRC = "omp.cvl";
	public final static String PTHREAD_SRC = "pthread.cvl";
	public final static String SCHED_SRC = "sched.cvl";
	public final static String STDING_SRC = "stding.cvl";
	public final static String STDIO_SRC = "stdio.cvl";
	public final static String STDLIB_SRC = "stdlib.cvl";
	public final static String STRING_SRC = "string.cvl";
	public final static String SYS_TIME_SRC = "sys-time.cvl";
	public final static String TIME_SRC = "time.cvl";
	public final static String TIMES_SRC = "times.cvl";
	public final static String UNISTD_SRC = "unistd.cvl";

	// CIVL library
	public final static String BUNDLE_SRC = "bundle.cvl";
	public final static String CIVLC_SRC = "civlc.cvl";
	public final static String CIVL_CUDA_SRC = "civl-cuda.cvl";
	public final static String CIVL_MPI_BLOCKING_SRC = "civl-mpi-blocking.cvl";
	public final static String CIVL_MPI_NONBLOCKING_SRC = "civl-mpi-nonblocking.cvl";
	public final static String CIVL_OMP_SRC = "civl-omp.cvl";
	public final static String CIVL_OMP2_SRC = "civl-omp2.cvl";
	public final static String CIVL_PTHREAD_SRC = "civl-pthread.cvl";
	public final static String COLLATE_SRC = "collate.cvl";
	public final static String COMM_SRC = "comm.cvl";
	public final static String CONCURRENCY_SRC = "concurrency.cvl";
	public final static String FORTRAN_ARRAY_SRC = "fortran_array.cvl";
	public final static String FORTRAN_SIGP_SRC = "fortran_sigp.cvl";
	public final static String INT_DIV_NO_CHECKING_SRC = "int_div_no_checking.cvl";
	public final static String INT_DIV_SRC = "int_div.cvl";
	public final static String LOOP_ASSIGNS_GEN_SRC = "loop_assigns_gen.cvl";
	public final static String SEQ_SRC = "seq.cvl";
	public final static String UNSIGNED_ARITH_SRC = "unsigned_arith.cvl";

	public final static String SVCOMP_SRC = "svcomp.cvl";

	/**
	 * @return all standard c library headers.
	 */
	public final static Set<String> getCStdLibHeaders() {
		return new HashSet<String>(Arrays.asList(SYS_MMAN, SYS_RESOURCE,
				SYS_TIME, SYS_TIMES, SYS_TYPES, ASSERT, COMPLEX, CTYPE,
				CUDA_RUNTIME_API, CUDA, ERRNO, FENV, FLOAT, GD_IO, GD, GDFX,
				GNUC, INTTYPES, ISO646, LIMITS, LOCALE, MATH, MPI, OMP, OP,
				PTHREAD, SCHED, SETJMP, SIGNAL, STDALIGN, STDARG, STDATOMIC,
				STDBOOL, STDDEF, STDINT, STDIO, STDLIB, STDNORETURN, STRING,
				STRINGS, SVCOMP, TGMATH, THREADS, TIME, UCHAR, UNISTD, WCHAR,
				WCTYPE));
	}

	/**
	 * @return all CIVL-C library headers.
	 */
	public final static Set<String> getCivlLibHeaders() {
		return new HashSet<String>(Arrays.asList(BUNDLE, CIVLC, CIVL_CUDA,
				CIVL_MPI, CIVL_MPI_BLOCKING, CIVL_MPI_NONBLOCKING, CIVL_OMP,
				CIVL_PTHREAD, CIVL_STDIO, COLLATE, COMM, COMM2,
				CONCURRENCY_CONTRACT, CONCURRENCY, DOMAIN, FORTRAN_ARRAY,
				FORTRAN_SIGP, LOOP_ASSIGNS_GEN, MEM, MEMORY, POINTER, SCOPE,
				SEQ));
	}

	/**
	 * @return all library headers, both from CIVL-C and the standard library.
	 */
	public final static Set<String> getAllLibHeaders() {
		Set<String> libs = getCStdLibHeaders();
		libs.addAll(getCivlLibHeaders());
		return libs;
	}

	public final static Set<String> getCStdLibSrcs() {
		return new HashSet<String>(Arrays.asList(ASSERT_SRC, CUDA_SRC, MATH_SRC,
				MPI_SRC, OMP_SRC, PTHREAD_SRC, SCHED_SRC, STDING_SRC, STDIO_SRC,
				STDLIB_SRC, STRING_SRC, SYS_TIME_SRC, SVCOMP_SRC, TIME_SRC,
				TIMES_SRC, UNISTD_SRC));
	}

	public final static Set<String> getCivlLibSrcs() {
		return new HashSet<String>(Arrays.asList(BUNDLE_SRC, CIVLC_SRC,
				CIVL_CUDA_SRC, CIVL_MPI_BLOCKING_SRC, CIVL_MPI_NONBLOCKING_SRC,
				CIVL_OMP_SRC, CIVL_OMP2_SRC, CIVL_PTHREAD_SRC, COLLATE_SRC,
				COMM_SRC, CONCURRENCY_SRC, FORTRAN_ARRAY_SRC, FORTRAN_SIGP_SRC,
				INT_DIV_NO_CHECKING_SRC, INT_DIV_SRC, LOOP_ASSIGNS_GEN_SRC,
				SEQ_SRC, UNSIGNED_ARITH_SRC));
	}

	/**
	 * @return all library headers, both from CIVL-C and the standard library.
	 */
	public final static Set<String> getAllLibSrcs() {
		Set<String> libs = getCStdLibSrcs();
		libs.addAll(getCivlLibSrcs());
		return libs;
	}

	public final static Set<String> getAllLibFilenames() {
		Set<String> libs = getAllLibHeaders();
		libs.addAll(getAllLibSrcs());
		return libs;
	}
}

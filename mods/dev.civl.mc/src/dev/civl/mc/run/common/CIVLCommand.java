package dev.civl.mc.run.common;

import static dev.civl.mc.config.IF.CIVLConstants.CIVLMacroO;
import static dev.civl.mc.config.IF.CIVLConstants.analyzeAbsO;
import static dev.civl.mc.config.IF.CIVLConstants.astO;
import static dev.civl.mc.config.IF.CIVLConstants.collectHeapsO;
import static dev.civl.mc.config.IF.CIVLConstants.collectOutputO;
import static dev.civl.mc.config.IF.CIVLConstants.collectProcessesO;
import static dev.civl.mc.config.IF.CIVLConstants.collectScopesO;
import static dev.civl.mc.config.IF.CIVLConstants.collectSymbolicConstantsO;
import static dev.civl.mc.config.IF.CIVLConstants.debugO;
import static dev.civl.mc.config.IF.CIVLConstants.direct0;
import static dev.civl.mc.config.IF.CIVLConstants.dporO;
import static dev.civl.mc.config.IF.CIVLConstants.enablePrintfO;
import static dev.civl.mc.config.IF.CIVLConstants.errorBoundO;
import static dev.civl.mc.config.IF.CIVLConstants.fairO;
import static dev.civl.mc.config.IF.CIVLConstants.guidedO;
import static dev.civl.mc.config.IF.CIVLConstants.idO;
import static dev.civl.mc.config.IF.CIVLConstants.inputO;
import static dev.civl.mc.config.IF.CIVLConstants.intBit;
import static dev.civl.mc.config.IF.CIVLConstants.intOperationTransformer;
import static dev.civl.mc.config.IF.CIVLConstants.loopO;
import static dev.civl.mc.config.IF.CIVLConstants.macroO;
import static dev.civl.mc.config.IF.CIVLConstants.maxProcsO;
import static dev.civl.mc.config.IF.CIVLConstants.maxdepthO;
import static dev.civl.mc.config.IF.CIVLConstants.memEqO;
import static dev.civl.mc.config.IF.CIVLConstants.minO;
import static dev.civl.mc.config.IF.CIVLConstants.mpiContractO;
import static dev.civl.mc.config.IF.CIVLConstants.ompLoopDecompO;
import static dev.civl.mc.config.IF.CIVLConstants.ompNoSimplifyO;
import static dev.civl.mc.config.IF.CIVLConstants.ompOnlySimplifierO;
import static dev.civl.mc.config.IF.CIVLConstants.preprocO;
import static dev.civl.mc.config.IF.CIVLConstants.procBoundO;
import static dev.civl.mc.config.IF.CIVLConstants.quietO;
import static dev.civl.mc.config.IF.CIVLConstants.randomO;
import static dev.civl.mc.config.IF.CIVLConstants.saveStatesO;
import static dev.civl.mc.config.IF.CIVLConstants.seedO;
import static dev.civl.mc.config.IF.CIVLConstants.showAmpleSetO;
import static dev.civl.mc.config.IF.CIVLConstants.showAmpleSetWtStatesO;
import static dev.civl.mc.config.IF.CIVLConstants.showInputVarsO;
import static dev.civl.mc.config.IF.CIVLConstants.showMemoryUnitsO;
import static dev.civl.mc.config.IF.CIVLConstants.showModelO;
import static dev.civl.mc.config.IF.CIVLConstants.showPathConditionO;
import static dev.civl.mc.config.IF.CIVLConstants.showProgramO;
import static dev.civl.mc.config.IF.CIVLConstants.showProverQueriesO;
import static dev.civl.mc.config.IF.CIVLConstants.showQueriesO;
import static dev.civl.mc.config.IF.CIVLConstants.showSavedStatesO;
import static dev.civl.mc.config.IF.CIVLConstants.showStatesO;
import static dev.civl.mc.config.IF.CIVLConstants.showTimeO;
import static dev.civl.mc.config.IF.CIVLConstants.showTransitionsO;
import static dev.civl.mc.config.IF.CIVLConstants.showUnreachedCodeO;
import static dev.civl.mc.config.IF.CIVLConstants.simplifyO;
import static dev.civl.mc.config.IF.CIVLConstants.solveO;
import static dev.civl.mc.config.IF.CIVLConstants.statelessPrintfO;
import static dev.civl.mc.config.IF.CIVLConstants.strictCompareO;
import static dev.civl.mc.config.IF.CIVLConstants.sysIncludePathO;
import static dev.civl.mc.config.IF.CIVLConstants.timeoutO;
import static dev.civl.mc.config.IF.CIVLConstants.traceO;
import static dev.civl.mc.config.IF.CIVLConstants.userIncludePathO;
import static dev.civl.mc.config.IF.CIVLConstants.verboseO;

import java.io.PrintStream;
import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

import dev.civl.gmc.Option;
import dev.civl.mc.model.IF.CIVLInternalException;
import dev.civl.mc.model.IF.CIVLProperty;
import dev.civl.mc.model.IF.CIVLSource;
import dev.civl.mc.run.common.CommandLine.CommandLineKind;
import dev.civl.mc.run.common.NormalCommandLine.NormalCommandKind;

public class CIVLCommand {

	private static SortedMap<String, Option> showOptions = new TreeMap<>();
	private static SortedMap<String, Option> verifyOptions = new TreeMap<>();
	private static SortedMap<String, Option> compareOptions = new TreeMap<>();
	private static SortedMap<String, Option> replayOptions = new TreeMap<>();
	private static SortedMap<String, Option> runOptions = new TreeMap<>();

	static {
		CIVLCommand.addShowOption(showModelO, verboseO, debugO, userIncludePathO, sysIncludePathO, showInputVarsO,
				showProgramO, ompNoSimplifyO, ompOnlySimplifierO, ompLoopDecompO, macroO, preprocO, astO, showTimeO,
				CIVLMacroO, quietO, direct0, intBit, intOperationTransformer, maxProcsO);
		CIVLCommand.addVerifyOption(errorBoundO, verboseO, debugO, userIncludePathO, sysIncludePathO, showTransitionsO,
				showStatesO, showSavedStatesO, showQueriesO, showProverQueriesO, inputO, minO, loopO, memEqO,
				mpiContractO, maxdepthO, procBoundO, saveStatesO, simplifyO, solveO, enablePrintfO, showAmpleSetO,
				showAmpleSetWtStatesO, statelessPrintfO, showProgramO, showPathConditionO, ompNoSimplifyO,
				ompOnlySimplifierO, ompLoopDecompO, collectProcessesO, collectScopesO, collectSymbolicConstantsO,
				collectHeapsO, macroO, preprocO, astO, showTimeO, showMemoryUnitsO, CIVLMacroO, showUnreachedCodeO,
				analyzeAbsO, collectOutputO, timeoutO, quietO, direct0, intBit, intOperationTransformer, maxProcsO,
				fairO, dporO);
		CIVLCommand.addVerifyOption(
				CIVLProperty.getAllConfigurableProperties().stream().map(e -> e.getOption()).toArray(Option[]::new));
		CIVLCommand.addCompareOption(errorBoundO, verboseO, debugO, userIncludePathO, sysIncludePathO, showTransitionsO,
				showStatesO, showSavedStatesO, showQueriesO, showProverQueriesO, inputO, minO, maxdepthO, procBoundO,
				saveStatesO, simplifyO, solveO, enablePrintfO, showAmpleSetO, showAmpleSetWtStatesO, statelessPrintfO,
				showProgramO, showPathConditionO, ompNoSimplifyO, ompOnlySimplifierO, ompLoopDecompO, collectProcessesO,
				collectScopesO, collectHeapsO, macroO, preprocO, astO, showTimeO, showMemoryUnitsO, CIVLMacroO,
				showUnreachedCodeO, analyzeAbsO, strictCompareO, timeoutO, quietO, intBit, intOperationTransformer,
				maxProcsO, fairO);
		CIVLCommand.addCompareOption(
				CIVLProperty.getAllConfigurableProperties().stream().map(e -> e.getOption()).toArray(Option[]::new));
		CIVLCommand.addReplayOption(showModelO, verboseO, debugO, showTransitionsO, showStatesO, showSavedStatesO,
				showQueriesO, showProverQueriesO, idO, traceO, enablePrintfO, showAmpleSetO, showAmpleSetWtStatesO,
				statelessPrintfO, showProgramO, showPathConditionO, preprocO, astO, showMemoryUnitsO, collectOutputO,
				CIVLProperty.DIVISION_BY_ZERO.getOption(), CIVLProperty.MEMORY_LEAK.getOption(), quietO, intBit,
				intOperationTransformer, maxProcsO);
		CIVLCommand.addRunOption(errorBoundO, verboseO, randomO, guidedO, seedO, debugO, userIncludePathO,
				sysIncludePathO, showTransitionsO, showStatesO, showSavedStatesO, showQueriesO, showProverQueriesO,
				inputO, maxdepthO, procBoundO, simplifyO, enablePrintfO, showAmpleSetO, showAmpleSetWtStatesO,
				statelessPrintfO, showProgramO, showPathConditionO, ompNoSimplifyO, ompOnlySimplifierO, ompLoopDecompO,
				collectProcessesO, collectScopesO, collectHeapsO, macroO, preprocO, astO, showMemoryUnitsO, CIVLMacroO,
				collectOutputO, timeoutO, quietO, intBit, intOperationTransformer, maxProcsO, fairO);
		CIVLCommand.addRunOption(
				CIVLProperty.getAllConfigurableProperties().stream().map(e -> e.getOption()).toArray(Option[]::new));
	}

	private static void addShowOption(Option... options) {
		for (Option option : options) {
			if (showOptions.containsKey(option.name()))
				throw new CIVLInternalException(
						"Option " + option.name() + " has already been added to show option map.", (CIVLSource) null);
			showOptions.put(option.name(), option);
		}
	}

	private static void addVerifyOption(Option... options) {
		for (Option option : options) {
			if (verifyOptions.containsKey(option.name()))
				throw new CIVLInternalException(
						"Option " + option.name() + " has already been added to verify option map.", (CIVLSource) null);
			verifyOptions.put(option.name(), option);
		}
	}

	private static void addCompareOption(Option... options) {
		for (Option option : options) {
			if (compareOptions.containsKey(option.name()))
				throw new CIVLInternalException(
						"Option " + option.name() + " has already been added to compare option map.",
						(CIVLSource) null);
			compareOptions.put(option.name(), option);
		}
	}

	private static void addReplayOption(Option... options) {
		for (Option option : options) {
			if (replayOptions.containsKey(option.name()))
				throw new CIVLInternalException(
						"Option " + option.name() + " has already been added to replay option map.", (CIVLSource) null);
			replayOptions.put(option.name(), option);
		}
	}

	private static void addRunOption(Option... options) {
		for (Option option : options) {
			if (runOptions.containsKey(option.name()))
				throw new CIVLInternalException(
						"Option " + option.name() + " has already been added to run option map.", (CIVLSource) null);
			runOptions.put(option.name(), option);
		}
	}

	public static void printOptionsOfCommand(String command, PrintStream out) {
		switch (command) {
		case CommandLine.COMPARE:
			printOptions(verifyOptions.values(), out);
			break;
		case CommandLine.VERIFY:
			printOptions(verifyOptions.values(), out);
			break;
		case CommandLine.REPLAY:
			printOptions(replayOptions.values(), out);
			break;
		case CommandLine.RUN:
			printOptions(runOptions.values(), out);
			break;
		case CommandLine.SHOW:
			printOptions(showOptions.values(), out);
			break;
		case CommandLine.GUI:
		case CommandLine.CONFIG: // no options for "civl config"
		default:
		}
	}

	private static void printOptions(Collection<Option> options, PrintStream out) {
		for (Option option : options)
			out.println(option);
	}

	public static boolean isValid(CommandLine commandLine, Option option) {
		CommandLineKind kind = commandLine.commandLineKind();

		if (kind == CommandLineKind.NORMAL) {
			NormalCommandLine cmd = (NormalCommandLine) commandLine;
			NormalCommandKind cmdKind = cmd.normalCommandKind();

			switch (cmdKind) {
			case SHOW:
				return showOptions.containsKey(option.name());
			case VERIFY:
				return verifyOptions.containsKey(option.name());
			case REPLAY:
			case RUN:
				return replayOptions.containsKey(option.name());
			case CONFIG:
				return false; // no options for "civl config"
			default:
				return false;
			}
		} else {
			return verifyOptions.containsKey(option.name());
		}
	}

	public static SortedMap<String, Option> getReplayOptions() {
		return replayOptions;
	}

	public static SortedMap<String, Option> getRunOptions() {
		return runOptions;
	}

	public static SortedMap<String, Option> getShowOptions() {
		return showOptions;
	}

	public static SortedMap<String, Option> getVerifyOrCompareOptions() {
		return verifyOptions;
	}
}

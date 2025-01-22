package dev.civl.gmc.dpor;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.stream.Stream;

import dev.civl.gmc.GMCConfiguration;
import dev.civl.gmc.StatePredicateIF;
import dev.civl.gmc.StateSpaceCycleException;
import dev.civl.gmc.seq.StateManager;

public class DporDfsSearcher<STATE, TRANSITION> {

	/**
	 * The analyzer used to determine when a dependency exists in our search
	 */
	DependencyAnalyzer<STATE, TRANSITION> analyzer;

	/**
	 * The state manager, used to determine the next state, given a state and
	 * transition. Also used for other state management issues.
	 */
	private StateManager<STATE, TRANSITION> manager;

	/**
	 * The predicate on states. This searching is searching for state that
	 * satisfies this predicate. Typically, this predicate describes something
	 * "bad", like deadlock.
	 */
	private StatePredicateIF<STATE> predicate;

	/**
	 * The depth-first search stack. An element in this stack in a transition
	 * sequence, which encapsulates a state together with the transitions
	 * enabled at that state which have not yet been completely explored.
	 */
	private DporSearchStack<STATE, TRANSITION> stack = null;
	//private Stack<DporStackEntry<STATE, TRANSITION>> stack;

	//private Map<Integer, Integer> procStackMap = new HashMap<>();
	
	/**
	 * This factory is used to get or construct some objects used in the search.
	 * For example, it is used to get the associated {@link DporNode} of a
	 * {@code state} and construct new instances of {@link DporStackEntry}.
	 */
	private DporNodeFactory<STATE, TRANSITION> dporNodeFactory;

	/**
	 * The number of states encountered which are recognized as having already
	 * been seen earlier in the search.
	 */
	private int numStatesMatched = 0;

	private int numRaces = 0;
	
	/**
	 * The number of states seen in this search.
	 */
	//private int numStatesSeen = 1;

	/**
	 * Where to print debugging output, if debugging is turned on.
	 */
	private PrintStream debugOut;

	/**
	 * Should we print debugging output?
	 */
	private boolean debugging = false;

	/**
	 * A name to give this searcher, used only for printing out messages about
	 * the search, such as in debugging.
	 */
	private String name = null;

	/**
	 * When the stack is being summarized in debugging output, this is the upper
	 * bound on the number of stack entries (starting from the top and moving
	 * down) that will be printed.
	 */
	private int summaryCutOff = 5;

	/**
	 * Should this print transitions as it searches?
	 */
	boolean printTransitions = false;

	/**
	 * Constructs a new depth first search searcher.
	 * 
	 * @param enabler
	 *                      the enabler used to determine the set of enabled
	 *                      transitions at each state in the course of this
	 *                      search
	 * @param manager
	 *                      the object used to manage states, compute the next
	 *                      state from a current state and transition, and so,
	 *                      during this search
	 * @param predicate
	 *                      the state predicate -- this will be checked at each
	 *                      state encountered in the search, and if it is found
	 *                      to hold, the search method will return; hence it is
	 *                      usually a predicate about something "bad" happening,
	 *                      like a deadlock
	 * @param gmcConfig
	 *                      GMC configuration object
	 * @param debugOut
	 *                      if null, debugging output is not printed, otherwise
	 *                      debugging output will be printing to this stream
	 */
	public DporDfsSearcher(DependencyAnalyzer<STATE, TRANSITION> analyzer,
			StateManager<STATE, TRANSITION> manager,
			StatePredicateIF<STATE> predicate, GMCConfiguration gmcConfig,
			PrintStream debugOut) {
		if (analyzer == null)
			throw new NullPointerException("null analyzer");
		if (manager == null)
			throw new NullPointerException("null manager");
		this.analyzer = analyzer;
		this.manager = manager;
		this.predicate = predicate;
		this.debugOut = debugOut;
		this.dporNodeFactory = new DporNodeFactory<>(manager,
				gmcConfig.getSaveStates());
		this.manager.setGetIdFunction(dporNodeFactory);
		if (debugOut != null)
			this.debugging = true;
		//stack = new Stack<>();
		this.printTransitions = gmcConfig.printTransitions();
	}

	public StatePredicateIF<STATE> predicate() {
		return predicate;
	}

	public DporDfsSearcher(DependencyAnalyzer<STATE, TRANSITION> analyzer,
			StateManager<STATE, TRANSITION> manager,
			StatePredicateIF<STATE> predicate, GMCConfiguration gmcConfig) {
		this(analyzer, manager, predicate, gmcConfig, null);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String name() {
		return name;
	}

	/**
	 * Returns the state at the top of the stack, without modifying the stack.
	 */
	public STATE currentState() {
		return stack == null ? null : stack.currentState();
	}

	/** Returns the stack used to perform the depth first search */
	public DporSearchStack<STATE, TRANSITION> stack() {
		return stack;
	}

	public boolean explore(STATE initialState) throws StateSpaceCycleException {
		stack = new DporSearchStack<>(manager, dporNodeFactory, initialState);
		
		if (predicate.holdsAt(stack.currentState()))
			return true;
		
		/**
		 * Invariant: If stack isn't empty, then either
		 *   1. stack.currentTransition() is a transition we haven't explored yet
		 *   2. stack.currentState() has no enabled processes
		 */
		while (!stack.isEmpty()) {
			DporSearchStack<STATE, TRANSITION>.Entry topStackEntry = stack.top();
			
			for (Integer outerPid : topStackEntry.enabledProcs()) {
				DporSearchStack<STATE, TRANSITION>.StackTraversal stackTraversal = stack
						.makeStackTraversal(outerPid);
				boolean foundRace = false;
				for (int i = stackTraversal.next(); i != -1; i = stackTraversal.next()) {
					DporSearchStack<STATE, TRANSITION>.Entry currEntry = stack.get(i);
					if (analyzer.checkDependent(stack, i, outerPid)) {
						stack.addRace(outerPid, i);
						numRaces++;
						debug("New Race added: ");
						
						// Only need to add to backtrack if this is the first race we found
						if (!foundRace) {
							foundRace = true;
							boolean addToBacktrack = true;
							// proc id of a process in "E" set that is enabled at currEntry
							// will remain -1 if no such process exists
							int enabledProc = -1;
							
							DporSearchStack<STATE, TRANSITION>.HbRelation topHbRel = topStackEntry
									.getHbRel();
							final int oPidCopy = outerPid;
							final int pos = i;
							// This stream represents the "E" set in algorithm from
							// DPOR paper
							Iterator<Integer> candidateIter = Stream
									.concat(Stream.of(outerPid),
											topHbRel.hbProcSet().stream()
													.filter(p -> p != oPidCopy && topHbRel
															.lastHbEntry(p) > pos))
									.iterator();
							while (candidateIter.hasNext()) {
								int proc = candidateIter.next();
	
								if (currEntry.inBacktrack(proc)) {
									// A process in the E set is already being
									// backtracked here
									addToBacktrack = false;
									break;
								}
	
								if (enabledProc == -1
										&& currEntry.enabledProcs().contains(proc))
									enabledProc = proc;
							}
							
							if (addToBacktrack) {
								if (enabledProc != -1) {
									currEntry.addToBacktrack(enabledProc);
								} else {
								// No process in E was enabled so we must fully expand
									currEntry.fullyEnable();
								}
							}
						}
					}
				}
			}

			if (stack.currentTransition() == null) {
				while (!stack.isEmpty() && !stack.nextTransition()) {
					stack.popTransition();
				}
				if (stack.isEmpty())
					break;
			}
			
			if (!stack.pushTransition()) {
				if (predicate.holdsAt(stack.currentState()))
					return true;
			}
		}
		
		return false;
	}

	/**
	 * Set the debugging flag to the given value. If true, debugging output will
	 * be printed to the debug stream. Otherwise debugging output will not be
	 * printed.
	 * 
	 * @param value
	 *                  if true, start showing the debugging output, otherwise
	 *                  don't show it
	 */
	public void setDebugging(boolean value) {
		debugging = value;
	}

	/**
	 * Returns the current value of the debugging flag. If true, debugging
	 * output will be printed to the debug stream. Otherwise debugging output
	 * will not be printed.
	 * 
	 * @return the current value of the debugging flag
	 */
	boolean debugging() {
		return debugging;
	}

	/**
	 * Sets the debugging output stream to the given stream. This is the stream
	 * used to print debugging information if the debugging flag is on.
	 * 
	 * @param out
	 *                the output stream to which debugging information should be
	 *                sent
	 */
	public void setDebugOut(PrintStream out) {
		if (out == null) {
			throw new NullPointerException("null out");
		}
		debugOut = out;
	}

	/**
	 * Returns the stream used to print debugging output when the debugging flag
	 * is on.
	 * 
	 * @return the debugging output stream
	 */
	public PrintStream getDebugOut() {
		return debugOut;
	}

	/**
	 * If the debugging flag is on, prints the message s to the debugging output
	 * stream, otherwise does nothing.
	 * 
	 * @param s
	 *              a debugging message
	 */
	protected void debug(String s) {
		if (debugging) {
			debugOut.println(s);
			debugOut.flush();
		}
	}

	/**
	 * Prints the current stack in a human-readable format.
	 * 
	 * @param out
	 *                       the stream to which to print the stack
	 * @param longFormat
	 *                       if true, provide detailed information about each
	 *                       state
	 * @param summarize
	 *                       if true, don't print out more than some fixed bound
	 *                       number of entries from the top of the stack;
	 *                       otherwise print the whole stack
	 */
	public void printStack(PrintStream out, boolean longFormat,
			boolean summarize) {
		int size = stack.size();

		if (size == 0) {
			out.println("  <EMPTY>");
		}
		for (int i = 0; i < size; i++) {
			DporSearchStack<STATE, TRANSITION>.Entry stackEntry = stack.get(i);
			STATE state = stackEntry.getState();

			if (!summarize || i <= 1 || size - i < summaryCutOff - 1) {
				if (i > 0) {
					out.print(" -> ");
					manager.printStateShort(out, state);
					out.println();
				}
				if (longFormat) {
					out.println();
					manager.printStateLong(out, state);
					out.println();
				}
			}
			if (summarize && size - i == summaryCutOff - 1) {
				for (int j = 0; j < 3; j++)
					out.println("     .");
			}
			if (!summarize || i <= 0 || size - i < summaryCutOff) {
				out.print("Step " + (i + 1) + ": ");
				manager.printStateShort(out, state);
				TRANSITION currTran = stackEntry.currentTransition();
				if (currTran != null) {
					out.print(" --");
					manager.printTransitionShort(out, currTran);
				}
				out.flush();
			}
		}
		out.println();
		out.flush();
	}

	/**
	 * Prints the whole stack in a human readable form to the given stream.
	 * Prints first a summary, then the stack in full detail (with detailed
	 * state information).
	 * 
	 * @param out
	 *                output stream to which this information should be sent
	 */
	public void printStack(PrintStream out) {
		if (name != null)
			out.print(name + " ");
		out.println("Trace summary:\n");
		printStack(out, false, false);
		out.println();
		if (name != null)
			out.print(name + " ");
		out.println("Trace details:");
		printStack(out, true, false);
	}

	/**
	 * Prints the stack, summarizing, i.e., only printing out the first few
	 * entries from the top.
	 * 
	 * @param s
	 *                       a message to print at the beginning
	 * @param longFormat
	 *                       if true, print complete state information,
	 *                       otherwise use short names for the states
	 */
	void debugPrintStack(String s, boolean longFormat) {
		if (debugging) {
			debugOut.println(s + "  New stack for " + name + ":\n");
			printStack(debugOut, longFormat, true);
			debugOut.println();
		}
	}

	/**
	 * If the debugging flag is on, prints out all the states held by the state
	 * manager in their full gory detail. Otherwise, a no-op.
	 * 
	 * @param s
	 *              a message to print first
	 */
	void debugStates(String s) {
		if (debugging) {
			debugOut.println(s + "All states for " + name + ":\n");
			manager.printAllStatesLong(debugOut);
			debugOut.println();
			printSummary(debugOut);
		} else {
		}
	}

	/**
	 * The number of states seen in this search.
	 * 
	 * @return the number of states seen so far
	 */
	public int numStatesSeen() {
		return stack.numStatesSeen();
	}

	/**
	 * The number of transitions executed in the course of this search so far.
	 * 
	 * @return the number of transitions executed.
	 */
	public int numTraceSteps() {
		return stack.numTraceSteps();
	}

	/**
	 * The number of states matched so far. A state is "matched" when the search
	 * determines the state has been seen before, earlier in the search. If the
	 * state has been seen before, it is not explored.
	 * 
	 * @return the number of states matched
	 */
	public int numStatesMatched() {
		return numStatesMatched;
	}

	/**
	 * @return the number of search nodes saved which is also the number of
	 *         non-equal states.
	 */
	public int numOfSearchNodeSaved() {
		return dporNodeFactory.numOfSearchNodeSaved();
	}

	/**
	 * Summarizes the current state of the search in a human-readable form
	 * printed to the given stream.
	 * 
	 * @param out
	 *                the stream to which to print the information
	 */
	public void printSummary(PrintStream out) {
		out.println("Number of states seen:    " + numStatesSeen());
		out.println("Number of trace steps:   " + numTraceSteps());
		out.println("Number of states matched: " + numStatesMatched + "\n");
		out.flush();
	}

	/**
	 * Write the state of the current stack in a condensed form that can be used
	 * to replay the trace later.
	 * 
	 * @param stream
	 *                   stream to which to write the current state of the DFS
	 *                   stack
	 */
	public void writeStack(PrintStream stream) {
		int size = stack.size();
		int prevTid = 0;
		int count = 0;

		stream.println("LENGTH = " + size);
		for (int i = 0; i < size; i++) {
			int curTid = stack.get(i).getTid();

			if (count == 0) {
				count++;
				prevTid = curTid;
			} else {
				if (curTid == prevTid) {
					count++;
				} else {
					stream.println(count + ":" + prevTid);
					count = 1;
					prevTid = curTid;
				}
			}
			if (i == size - 1) {
				stream.println(count + ":" + prevTid);
			}
		}
		stream.flush();
	}
}

package dev.civl.mc.run.common;

public class VerificationStatus {
	public int maxProcessCount;
	public int numStates;
	public int numSavedStates;
	public int numMatchedStates;
	public long numTransitions;
	public int numCrossTransitions;
	public int numTraceSteps;
	public int numTraceStepsMatched;
	public int numCrossTraceSteps;

	public VerificationStatus(int maxProcCount, int states, int savedStates,
			int matchedStates, long trans, int traceSteps,
			int traceStepsMatched, int crossTransitions, int crossTraceSteps) {
		this.maxProcessCount = maxProcCount;
		this.numStates = states;
		this.numSavedStates = savedStates;
		this.numMatchedStates = matchedStates;
		this.numTransitions = trans;
		this.numTraceSteps = traceSteps;
		this.numTraceStepsMatched = traceStepsMatched;
		this.numCrossTransitions = crossTransitions;
		this.numCrossTraceSteps = crossTraceSteps;
	}
}
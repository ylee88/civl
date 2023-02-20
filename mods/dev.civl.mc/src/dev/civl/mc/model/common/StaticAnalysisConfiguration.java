package dev.civl.mc.model.common;

import java.util.BitSet;

import dev.civl.mc.model.IF.CIVLFunction;

public class StaticAnalysisConfiguration {
	public static final String SEQ_INIT = "$seq_init";
	public static final String SEQ_INSERT = "$seq_insert";

	public static BitSet getIgnoredArgumenets(CIVLFunction function) {
		String name = function.name().name();
		BitSet result = new BitSet();

		switch (name) {
		case SEQ_INIT:
			result.set(2);
			break;
		case SEQ_INSERT:
			result.set(2);
			break;
		default:
		}
		return result;
	}
}

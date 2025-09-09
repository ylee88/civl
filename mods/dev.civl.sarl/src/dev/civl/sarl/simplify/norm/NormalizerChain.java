package dev.civl.sarl.simplify.norm;

import java.io.PrintStream;
import java.util.Set;

import dev.civl.sarl.IF.expr.SymbolicConstant;
import dev.civl.sarl.simplify.simplifier.InconsistentContextException;
import dev.civl.sarl.simplify.simplifier.MutableContext;
import dev.civl.sarl.simplify.simplifier.SimplifierUtility;

public class NormalizerChain implements Normalizer {

	// private static int globalID = 0; // for debugging
	//
	// private static int globalDepth = 0; // for debugging

	public static boolean debug = false;

	public final static PrintStream out = System.out;

	private MutableContext context;
	private Normalizer[] members;

	public NormalizerChain(MutableContext context, Normalizer... members) {
		this.context = context;
		this.members = members;
	}

	@Override
	public void normalize(Set<SymbolicConstant> dirtyIn,
			Set<SymbolicConstant> dirtyOut)
			throws InconsistentContextException {
		// int id = globalID++; // make me atomic
		//
		// if (debug) {
		// globalDepth++;
		// out.println("Starting normalization " + id + " at depth "
		// + globalDepth);
		// }
		int n = members.length;
		@SuppressWarnings("unchecked")
		Set<SymbolicConstant>[] dirts = new Set[n];
		boolean hasDirt = true;
		Set<SymbolicConstant> tmp = SimplifierUtility.newDirtySet();
		// int cycleCount = 0; // for debugging

		for (int i = 0; i < n; i++)
			dirts[i] = SimplifierUtility.cloneDirtySet(dirtyIn);
		while (hasDirt) {
			// if (debug) {
			// out.println("Normalization " + id + " cycle " + cycleCount);
			// cycleCount++;
			// }
			hasDirt = false;
			for (int i = 0; i < n; i++) {
				if (!dirts[i].isEmpty()) {
					if (debug) {
						context.checkSubMapInvariant();
						//System.out.println("Invariant holds");
					}
					members[i].normalize(dirts[i], tmp);
					dirts[i].clear();
					if (!tmp.isEmpty()) {
						hasDirt = true;
						for (int j = 0; j < n; j++)
							if (j != i)
								dirts[j].addAll(tmp);
						dirtyOut.addAll(tmp);
						tmp.clear();
					}
				}
			}
		}
		// if (debug) {
		// out.println("Finishing normalization " + id);
		// globalDepth--;
		// }
	}

}

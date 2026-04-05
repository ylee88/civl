package dev.civl.mc.slice.common;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dev.civl.gmc.Trace;
import dev.civl.mc.model.IF.Model;
import dev.civl.mc.semantics.IF.Transition;
import dev.civl.mc.slice.IF.Slice;
import dev.civl.mc.state.IF.State;
import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.IF.expr.SymbolicConstant;
import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.preuniverse.IF.FactorySystem;
import dev.civl.sarl.preuniverse.IF.PreUniverse;
import dev.civl.sarl.preuniverse.IF.PreUniverses;
import dev.civl.sarl.prove.smt.Z3Translator;
import dev.civl.sarl.util.FastList;

public class CommonSlice implements Slice {

	private PrintStream out = System.out;
	private Set<ErrorCfaLoc> slice;
	private Map<SymbolicExpression, String> map;
	private int numberSlicedAway;

	public CommonSlice(Trace<Transition, State> trace, Model model) {
		SliceAnalysis sa = new SliceAnalysis(model, trace);
		this.slice = sa.getSlice();
		this.map = sa.getMapping();
		this.numberSlicedAway = sa.getNumberSliced();
	}

	@Override
	public void print() {
		out.println("=== Sliced PC ===");
		printConstraints(slice);
		out.println("=== END ===");
		out.println("=== ACF Mapping ===");
		printAcfMapping(map);
		out.println("=== END ===");
		out.println("=== Number sliced ===");
		out.println(numberSlicedAway);
		out.println("=== END ===");
	}

	private void printAcfMapping(Map<SymbolicExpression, String> map) {
		for (SymbolicExpression s : map.keySet()) {
			String isolatedSymVar = sanitizeSymbolicExpression(s);
			out.println(isolatedSymVar + " " + map.get(s));
		}
	}

	private String sanitizeSymbolicExpression(SymbolicExpression s) {
		Pattern p = Pattern.compile("Y\\d+");
		Matcher m = p.matcher(s.toString());
		if (m.find()) {
			return m.group(0);
		} else {
			return s.toString();
		}
	}

	private void printConstraints(Set<ErrorCfaLoc> locs) {
		FactorySystem factorySystem = PreUniverses.newIdealFactorySystem();
		PreUniverse universe = PreUniverses.newPreUniverse(factorySystem);
		BooleanExpression context = universe.trueExpression();

		Z3Translator startingContext = new Z3Translator(universe, context);

		List<String> mappings = new ArrayList<>();

		for (ErrorCfaLoc l : locs) {
			BooleanExpression expression = l.branchConstraint;
			Z3Translator translator = new Z3Translator(startingContext, expression);
			FastList<String> predicateText = translator.getTranslation();
			String mapping = "";
			if (!predicateText.toString().equals("true")) {
				mapping = expression.toString() + " #Z3# " + predicateText.toString() + " #TYPES# "
						+ varTypePairs(expression.getFreeVars()) + "\n";
				mappings.add(mapping);
			}
		}
		java.util.Collections.sort(mappings);
		for (String s : mappings)
			out.print(s);
	}

	private String varTypePairs(Set<SymbolicConstant> symVars) {
		String str = "";
		for (SymbolicConstant s : symVars) {
			str += varTypeStr(s);
		}
		return str;
	}

	private String varTypeStr(SymbolicConstant s) {
		String var = s.toString();
		String type = s.type().toString();
		return (var + " " + type + " ");
	}
}

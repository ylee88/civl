/*******************************************************************************
 * Copyright (c) 2013 Stephen F. Siegel, University of Delaware.
 * 
 * This file is part of SARL.
 * 
 * SARL is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * SARL is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with SARL. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package dev.civl.sarl.prove.smt;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.LinkedList;

import dev.civl.sarl.IF.SARLException;
import dev.civl.sarl.IF.TheoremProverException;
import dev.civl.sarl.IF.ValidityResult;
import dev.civl.sarl.IF.config.ProverInfo;
import dev.civl.sarl.IF.config.ProverInfo.ProverKind;
import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.preuniverse.IF.PreUniverse;
import dev.civl.sarl.prove.IF.Prove;
import dev.civl.sarl.prove.IF.ProverFunctionInterpretation;
import dev.civl.sarl.prove.IF.TheoremProver;
import dev.civl.sarl.util.FastList;
import dev.civl.sarl.util.ProcessControl;

/**
 * An implementation of {@link TheoremProver} for an SMT-based prover that
 * follows the SMT-LIB standard. Transforms a theorem proving query into
 * SMT-LIB, invokes the tool through its command line interface in a new
 * process, and interprets the output.
 * 
 * Commands:
 * 
 * <pre>
 * (assert expr)
 * (check-sat)
 * </pre>
 * 
 * To check if predicate is valid under context:
 * 
 * <pre>
 * (assert context)
 * (assert (not predicate))
 * (check-sat)
 * </pre>
 * 
 * If result is "sat": answer is NO (not valid). If result is "unsat": answer is
 * YES (valid). Otherwise, MAYBE.
 * </p>
 * 
 * @author Stephen F. Siegel
 */
public class SMTProver implements TheoremProver {

	// ************************** Static Fields *************************** //

	/**
	 * Nick-name for <code>stderr</code>, where warnings and error messages will be
	 * sent.
	 */
	public static final PrintStream err = System.err;

	// ****************************** Fields ****************************** //

	/**
	 * Info object on underlying SMT theorem prover.
	 */
	private ProverInfo info;

	/**
	 * Java object for producing new OS-level processes executing a specified
	 * command.
	 */
	private ProcessBuilder processBuilder;

	/**
	 * The symbolic universe used for managing symbolic expressions. Initialized by
	 * constructor and never changes.
	 */
	private PreUniverse universe;

	/**
	 * The translation of the given context to an SMT expression. Created once
	 * during instantiation and never modified.
	 */
	private SMTTranslator assumptionTranslator;

	// *************************** Factory Methods ************************ //

	private static SMTTranslator newSMTTranslator(ProverKind kind, SMTTranslator startingContext,
			SymbolicExpression theExpression) {
		switch (kind) {
		case Z3:
			return new Z3Translator((Z3Translator) startingContext, theExpression);
		case CVC5:
			return new CVC5Translator((CVC5Translator) startingContext, theExpression);
		default:
			return new SMTTranslator(startingContext, theExpression);
		}
	}

	private static SMTTranslator newSMTTranslator(ProverKind kind, PreUniverse universe,
			SymbolicExpression theExpression, ProverFunctionInterpretation logicFunctions[]) {
		switch (kind) {
		case Z3:
			return new Z3Translator(universe, theExpression, logicFunctions);
		case CVC5:
			return new CVC5Translator(universe, theExpression, logicFunctions);
		default:
			return new SMTTranslator(universe, kind, theExpression, logicFunctions);
		}
	}

	// *************************** Constructors *************************** //

	/**
	 * Constructs new SMT theorem prover for the given context.
	 * 
	 * @param universe       the controlling symbolic universe
	 * @param context        the assumption(s) the prover will use for queries
	 * @param ProverInfo     information object on the underlying theorem prover,
	 *                       which must be an SMT prover
	 * @param logicFunctions a list of {@link ProverFunctionInterpretation}s which
	 *                       are the logic function definitions
	 */
	SMTProver(PreUniverse universe, BooleanExpression context, ProverInfo info,
			ProverFunctionInterpretation logicFunctions[]) throws TheoremProverException {
		LinkedList<String> command = new LinkedList<>();

		assert universe != null;
		assert context != null;
		assert info != null;
		this.universe = universe;
		this.info = info;
		context = (BooleanExpression) universe.cleanBoundVariables(context);
		this.assumptionTranslator = newSMTTranslator(info.getKind(), universe, context, logicFunctions);
		command.add(info.getPath().getAbsolutePath()); // the name of the tool
		command.addAll(info.getOptions()); // tool-specific options
		this.processBuilder = new ProcessBuilder(command);
	}

	private ValidityResult readSmtOutput(BufferedReader smtOut, BufferedReader smtErr) {
		try {
			while (true) {
				// save a copy of the original line for error reporting...
				String line = smtOut.readLine(), originalLine = line;
				if (line == null) {
					if (info.getShowErrors() || info.getShowInconclusives()) {
						try {
							if (smtErr.ready()) {
								PrintStream exp = new PrintStream(new File(universe.getErrFile()));

								printProverUnexpectedException(smtErr, exp);
								exp.close();
							}
						} catch (IOException e) {
							printProverUnexpectedException(smtErr, err);
						}
					}
					return Prove.RESULT_MAYBE;
				}
				int commentStart = line.indexOf(';');
				if (commentStart >= 0)
					line = line.substring(0, commentStart);
				line = line.trim();
				if (line.isEmpty())
					continue;
				if ("unsat".equals(line))
					return Prove.RESULT_YES;
				if ("sat".equals(line))
					return Prove.RESULT_NO;
				if (info.getShowInconclusives()) {
					err.println(info.getFirstAlias() + " inconclusive with message: " + originalLine);
					for (line = smtOut.readLine(); line != null; line = smtOut.readLine()) {
						err.println(line);
					}
				}
				if ("unknown".equals(line))
					return Prove.RESULT_MAYBE;
				throw new SARLException(info.getFirstAlias() + " unexpected message: " + originalLine);
			}
		} catch (IOException e) {
			if (info.getShowErrors())
				err.println("I/O error reading " + info.getFirstAlias() + " output: " + e.getMessage());
			return Prove.RESULT_MAYBE;
		}
	}

	/**
	 * <p>
	 * Run the SMT prover to reason about the given predicate <code>p</code> under
	 * the context <code>c</code>.
	 * </p>
	 * 
	 * <p>
	 * if the purpose is to test if <code>p</code> is unsatisfiable (the argument
	 * testUNSAT set to true), then the tool checks if <code>c && p</code> is UNSAT.
	 * </p>
	 * 
	 * <p>
	 * if the purpose is to test if <code>p</code> is valid under the context (the
	 * argument testUNSAT set to false), then the tool checks if
	 * <code>c && !p</code> is UNSAT.
	 * </p>
	 * 
	 * @param predicate  the boolean expression representing the predicate
	 * @param checkUNSAT true means check unsatisfiability of the given predicate;
	 *                   false means test if the context entails the predicate.
	 * @param id         the ID number of this prover call
	 * @param show       a flag indicating whether printing the prover script
	 * @param out        the output stream
	 * @return a {@link ValidityResult}
	 * @throws TheoremProverException
	 */
	private ValidityResult runProver(BooleanExpression predicate, boolean checkUNSAT, int id, boolean show,
			PrintStream out) throws TheoremProverException {
		Process process = null;
		ValidityResult result = null;

		try {
			process = processBuilder.start();
		} catch (Throwable e) {
			if (info.getShowErrors())
				err.println("I/O exception reading " + info.getFirstAlias() + " output: " + e.getMessage());
			result = Prove.RESULT_MAYBE;
		}
		try {
			if (result == null) {
				PrintStream stdin = new PrintStream(process.getOutputStream());
				BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
				BufferedReader stderr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
				FastList<String> assumptionDecls = assumptionTranslator.getDeclarations();
				FastList<String> assumptionText = assumptionTranslator.getTranslation();

				// this harms Z3. takes away ability to use special relations...
				if (info.getKind() != ProverKind.Z3)
					stdin.println("(set-logic ALL)");
				assumptionDecls.print(stdin);
				stdin.print("(assert ");
				assumptionText.print(stdin);
				stdin.println(")");
				predicate = (BooleanExpression) universe.cleanBoundVariables(predicate);

				SMTTranslator translator = newSMTTranslator(info.getKind(), assumptionTranslator, predicate);
				FastList<String> predicateDecls = translator.getDeclarations();
				FastList<String> predicateText = translator.getTranslation();

				predicateDecls.print(stdin);
				if (checkUNSAT) {
					// the conjunction of a predicate `p` and a context `c` is
					// UNSAT, iff p && c is UNSAT
					stdin.print("(assert  ");
					predicateText.print(stdin);
					stdin.println(")");
				} else {
					// a predicate p is valid under a context c,
					// iff `c` && `!p` is UNSAT
					stdin.print("(assert (not ");
					predicateText.print(stdin);
					stdin.println("))");
				}
				stdin.println("(check-sat)");
				stdin.flush();
				stdin.close();
				if (show) {
					String queryKind = checkUNSAT ? "check-unsat" : "";

					out.print("\n" + info.getFirstAlias() + queryKind + " predicate   " + id + ":\n");
					predicateDecls.print(out);
					predicateText.print(out);
					out.println();
					out.println();
					out.flush();
				}
				if (info.getTimeout() > 0 && !ProcessControl.waitForProcess(process, info.getTimeout())) {
					if (info.getShowErrors() || info.getShowInconclusives())
						err.println(info.getFirstAlias() + " query       " + id + ": time out");
					result = Prove.RESULT_MAYBE;
				} else {
					result = readSmtOutput(stdout, stderr);
				}
			}
		} catch (Exception e) {
			if (process != null)
				process.destroyForcibly();
			process = null;
			throw e;
		}
		if (process != null)
			process.destroy();
		return result;
	}

	@Override
	public ValidityResult valid(BooleanExpression predicate) {
		PrintStream out = universe.getOutputStream();
		int id = universe.numProverValidCalls();
		FastList<String> assumptionDecls = assumptionTranslator.getDeclarations();
		FastList<String> assumptionText = assumptionTranslator.getTranslation();
		boolean show = universe.getShowProverQueries() || info.getShowQueries();

		universe.incrementProverValidCount();
		if (show) {
			out.println();
			out.print(info.getFirstAlias() + " assumptions " + id + ":\n");
			assumptionDecls.print(out);
			assumptionText.print(out);
			out.println();
			out.flush();
		}

		ValidityResult result;

		try {
			result = runProver(predicate, false, id, show, out);
		} catch (TheoremProverException e) {
			if (show)
				err.println("Warning: " + e.getMessage());
			result = Prove.RESULT_MAYBE;
		}
		if (show) {
			out.println(info.getFirstAlias() + " result      " + id + ": " + result);
			out.flush();
		}
		return result;
	}

	@Override
	public ValidityResult validOrModel(BooleanExpression predicate) {
		return Prove.RESULT_MAYBE;
	}

	@Override
	public String toString() {
		return "SMTProver[" + info.getFirstAlias() + "]";
	}

	void printProverUnexpectedException(BufferedReader proverErr, PrintStream exp) throws IOException {
		String errline;

		do {
			errline = proverErr.readLine();
			if (errline == null)
				break;
			exp.append(errline + "\n");
		} while (errline != null);
	}

	@Override
	public ValidityResult unsat(BooleanExpression predicate) throws TheoremProverException {
		PrintStream out = universe.getOutputStream();
		int id = universe.numProverValidCalls();
		FastList<String> assumptionDecls = assumptionTranslator.getDeclarations();
		FastList<String> assumptionText = assumptionTranslator.getTranslation();
		boolean show = universe.getShowProverQueries() || info.getShowQueries();

		universe.incrementProverValidCount();
		if (show) {
			out.println();
			out.print(info.getFirstAlias() + " assumptions " + id + ":\n");
			assumptionDecls.print(out);
			assumptionText.print(out);
			out.println();
			out.flush();
		}

		ValidityResult result;

		try {
			result = runProver(predicate, true, id, show, out);
		} catch (TheoremProverException e) {
			if (show)
				err.println("Warning: " + e.getMessage());
			result = Prove.RESULT_MAYBE;
		}
		if (show) {
			out.println(info.getFirstAlias() + " result      " + id + ": " + result);
			out.flush();
		}
		return result;
	}
}

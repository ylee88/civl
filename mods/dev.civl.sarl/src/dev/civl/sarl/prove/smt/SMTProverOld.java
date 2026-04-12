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
 * To check whether predicate is unsatisfiable under context:
 * 
 * <pre>
 * (assert context)
 * (assert predicate)
 * (check-sat)
 * </pre>
 * 
 * To check whether predicate is entailed by context:
 * 
 * <pre>
 * (assert context)
 * (assert (not predicate))
 * (check-sat)
 * </pre>
 * 
 * In both cases, if result is "sat", answer is NO. If result is "unsat", answer
 * is YES. Otherwise, MAYBE.
 * 
 * Note: need a temporary directory to store the prover input files.
 * Should this be another argument to the constructor?
 * </p>
 * 
 * @author Stephen F. Siegel
 */
public class SMTProverOld implements TheoremProver {

	// ************************** Static Fields *************************** //

	/**
	 * Nick-name for <code>stderr</code>, where warnings and error messages will be
	 * sent.
	 */
	public static final PrintStream err = System.err;

	// ************************** Static Methods ************************** //

	// TODO: probably better to make this a base class and introduce new subclasses
	// Z3Prover, CVC5Prover, etc.

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

	private static void print(String str, PrintStream stream1, PrintStream stream2) {
		stream1.print(str);
		if (stream2 != null)
			stream2.print(str);
	}

	private static void println(String str, PrintStream stream1, PrintStream stream2) {
		stream1.println(str);
		if (stream2 != null)
			stream2.println(str);
	}

	private static void print(FastList<String> fl, PrintStream stream1, PrintStream stream2) {
		fl.print(stream1);
		if (stream2 != null)
			fl.print(stream2);
	}

	private static void printProverUnexpectedException(BufferedReader proverErr, PrintStream exp) throws IOException {
		String errline;

		do {
			errline = proverErr.readLine();
			if (errline == null)
				break;
			exp.append(errline + "\n");
		} while (errline != null);
	}

	// **************************** Instance Fields **************************** //

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
	SMTProverOld(PreUniverse universe, BooleanExpression context, ProverInfo info,
			ProverFunctionInterpretation logicFunctions[]) {
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

	// ************************* Instance Methods ************************* //

	/**
	 * Parse SMT tool's output. Whether asking about unsatisfiability or validity, a
	 * result of "unsat" means the answer is "yes", "sat" means the answer is "no",
	 * and anything else means the answer is "maybe".
	 * 
	 * @param smtOut stdout output from prover
	 * @param smtErr stderr output from prover
	 * @return the answer to the unsatisfiability or validity question
	 */
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
	 * @param checkUNSAT true means check unsatisfiability of the given predicate in
	 *                   the context; false means check if the context entails the
	 *                   predicate.
	 * @param id         the ID number of this prover call
	 * @param show       a flag indicating whether printing the prover script
	 * @param out        the output stream
	 * @return a {@link ValidityResult}
	 */
	private ValidityResult runProver(BooleanExpression predicate, boolean checkUNSAT) {
		boolean show = universe.getShowProverQueries() || info.getShowQueries();
		// out2 is used only if showing prover queries...
		PrintStream out2 = show ? universe.getOutputStream() : null;
		int id = universe.numProverValidCalls();
		Process process = null;
		ValidityResult result = null;

		universe.incrementProverValidCount();
		if (show) {
			String queryKind = checkUNSAT ? "unsat?" : "valid?";
			out2.println();
			out2.println("; " + info.getFirstAlias() + " query " + id + " (" + queryKind + "):");
			out2.println();
		}
		try {
			process = processBuilder.start();
		} catch (Exception e) {
			if (info.getShowErrors()) {
				err.println("Exception occurred executing command: " + processBuilder.command() + ":");
				err.println(e.getMessage());
			}
			result = Prove.RESULT_MAYBE;
		}
		try {
			if (result == null) {
				PrintStream stdin = new PrintStream(process.getOutputStream());
				BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
				BufferedReader stderr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
				FastList<String> assumptionDecls = assumptionTranslator.getDeclarations();
				FastList<String> assumptionText = assumptionTranslator.getTranslation();
				predicate = (BooleanExpression) universe.cleanBoundVariables(predicate);
				SMTTranslator translator = newSMTTranslator(info.getKind(), assumptionTranslator, predicate);
				FastList<String> predicateDecls = translator.getDeclarations();
				FastList<String> predicateText = translator.getTranslation();

				// set-logic harms Z3. takes away ability to use special relations...
				if (info.getKind() != ProverKind.Z3)
					println("(set-logic ALL)", stdin, out2);
				print(assumptionDecls, stdin, out2);
				print("(assert ", stdin, out2);
				print(assumptionText, stdin, out2);
				println(")", stdin, out2);
				print(predicateDecls, stdin, out2);
				if (checkUNSAT) {
					// p is unsat in context c iff p && c is UNSAT:
					println("(assert  ", stdin, out2);
					print(predicateText, stdin, out2);
					println(")", stdin, out2);
				} else {
					// p is entailed by context c iff c && !p is UNSAT:
					println("(assert (not ", stdin, out2);
					print(predicateText, stdin, out2);
					println("))", stdin, out2);
				}
				println("(check-sat)", stdin, out2);
				stdin.flush();
				if (show)
					out2.flush();
				stdin.close();
				if (info.getTimeout() > 0 && !ProcessControl.waitForProcess(process, info.getTimeout())) {
					if (info.getShowErrors() || info.getShowInconclusives())
						err.println("; " + info.getFirstAlias() + " query " + id + ": time out");
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
		if (show) {
			out2.println();
			out2.println("; " + info.getFirstAlias() + " query " + id + " result :" + result);
		}
		return result;
	}

	@Override
	public String toString() {
		return "SMTProver[" + info.getFirstAlias() + "]";
	}

	@Override
	public ValidityResult valid(BooleanExpression predicate) {
		return runProver(predicate, false);
	}

	@Override
	public ValidityResult unsat(BooleanExpression predicate) throws TheoremProverException {
		return runProver(predicate, true);
	}

	@Override
	public ValidityResult validOrModel(BooleanExpression predicate) {
		// TODO: not yet implemented.
		return Prove.RESULT_MAYBE;
	}
}

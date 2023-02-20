package dev.civl.abc;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import dev.civl.abc.token.IF.SyntaxException;
import org.junit.Test;

import dev.civl.abc.config.IF.Configuration;
import dev.civl.abc.config.IF.Configurations;
import dev.civl.abc.err.IF.ABCException;
import dev.civl.abc.main.ABCExecutor;
import dev.civl.abc.main.FrontEnd;
import dev.civl.abc.main.TranslationTask;

import static junit.framework.TestCase.assertTrue;

public class ContractTest {

	private static boolean debug = false;

	private static List<String> codes = Arrays.asList("prune", "sef");

	private File root = new File(new File("examples"), "contract");

	private static Configuration config = Configurations
			.newMinimalConfiguration();

	private static FrontEnd f = new FrontEnd(config);

	private void checkCIVL(String filenameRoot)
			throws ABCException, IOException {
		File file = new File(root, filenameRoot + ".cvl");
		TranslationTask task = new TranslationTask(file);

		task.addAllTransformCodes(codes);
		task.setVerbose(debug);
		task.setSilent(!debug);
		if (debug)
			task.setPrettyPrint(true);
		ABCExecutor.execute(f, task);
	}

	private void checkC(String filenameRoot) throws ABCException, IOException {
		File file = new File(root, filenameRoot + ".c");
		TranslationTask task = new TranslationTask(file);

		task.addAllTransformCodes(codes);
		task.setVerbose(debug);
		task.setSilent(!debug);
		if (debug)
			task.setPrettyPrint(true);
		ABCExecutor.execute(f, task);
	}

	@Test
	public void por() throws ABCException, IOException {
		checkCIVL("por");
	}

	@Test
	public void por2() throws ABCException, IOException {
		checkCIVL("por2");
	}

	@Test
	public void acslLogicFunctions() throws ABCException, IOException {
		checkCIVL("acslLogicFunctions");
	}

	@Test
	public void acslIgnore() throws ABCException, IOException {
		checkC("acsl_ignore");
	}

	@Test
	public void mpiContractRequirementGuarantee() throws ABCException, IOException {
		checkC("mpi_contract_requirement_guarantee");
	}

	@Test
	public void mpiContractRequirementGuaranteeBad() throws ABCException, IOException {
		boolean hasError = false;

		try {
			checkC("mpi_contract_requirement_guarantee-bad");
		} catch (SyntaxException e) {
			hasError = true;
		}
		assertTrue(hasError);
	}

	@Test
	public void mpiContractRequirementGuaranteeBad2() throws ABCException, IOException {
		boolean hasError = false;

		try {
			checkC("mpi_contract_requirement_guarantee-bad2");
		} catch (SyntaxException e) {
			hasError = true;
		}
		assertTrue(hasError);
	}
}

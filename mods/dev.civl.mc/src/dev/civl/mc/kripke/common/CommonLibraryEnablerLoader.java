package dev.civl.mc.kripke.common;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;

import dev.civl.mc.config.IF.CIVLConfiguration;
import dev.civl.mc.config.IF.CIVLConstants;
import dev.civl.mc.dynamic.IF.SymbolicUtility;
import dev.civl.mc.kripke.IF.Enabler;
import dev.civl.mc.kripke.IF.LibraryEnabler;
import dev.civl.mc.kripke.IF.LibraryEnablerLoader;
import dev.civl.mc.model.IF.ModelFactory;
import dev.civl.mc.semantics.IF.Evaluator;
import dev.civl.mc.semantics.IF.LibraryEvaluatorLoader;
import dev.civl.mc.semantics.IF.LibraryLoaderException;
import dev.civl.mc.semantics.IF.SymbolicAnalyzer;

public class CommonLibraryEnablerLoader implements LibraryEnablerLoader {

	/* *************************** Instance Fields ************************* */

	/**
	 * The cache of known library enablers.
	 */
	private Map<String, LibraryEnabler> libraryEnablerCache = new LinkedHashMap<>();

	private LibraryEvaluatorLoader libEvaluatorLoader;

	private CIVLConfiguration civlConfig;

	public CommonLibraryEnablerLoader(
			LibraryEvaluatorLoader libEvaluatorLoader,
			CIVLConfiguration civlConfig) {
		this.libEvaluatorLoader = libEvaluatorLoader;
		this.civlConfig = civlConfig;
	}

	/* ********************* Methods from LibraryLoader ******************** */

	@Override
	public LibraryEnabler getLibraryEnabler(String name,
			Enabler primaryEnabler, Evaluator evaluator,
			ModelFactory modelFacotry, SymbolicUtility symbolicUtil,
			SymbolicAnalyzer symbolicAnalyzer) throws LibraryLoaderException {
		LibraryEnabler result;

		result = libraryEnablerCache.get(name);
		if (result == null) {
			String aClassName = this.className(name, "Enabler");

			try {
				@SuppressWarnings("unchecked")
				Class<? extends LibraryEnabler> aClass = (Class<? extends LibraryEnabler>) Class
						.forName(aClassName);
				Constructor<? extends LibraryEnabler> constructor = aClass
						.getConstructor(String.class, Enabler.class,
								Evaluator.class, ModelFactory.class,
								SymbolicUtility.class, SymbolicAnalyzer.class,
								CIVLConfiguration.class,
								LibraryEnablerLoader.class,
								LibraryEvaluatorLoader.class);

				// System.out.println("library " + name);
				result = constructor.newInstance(name, primaryEnabler,
						evaluator, modelFacotry, symbolicUtil,
						symbolicAnalyzer, this.civlConfig, this,
						this.libEvaluatorLoader);
			} catch (ClassNotFoundException | NoSuchMethodException
					| SecurityException | InstantiationException
					| IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				throw new LibraryLoaderException(e.getMessage());
			}
			libraryEnablerCache.put(name, result);
		}
		return result;
	}

	/* *************************** Private Methods ************************* */

	/**
	 * Computes the full name of the class of an enabler/executor of a library.
	 * 
	 * @param library
	 *            The name of the library.
	 * @param suffix
	 *            "Enabler" or "Executor", depending on whether the enabler or
	 *            executor is to be used.
	 * @return The full name of the class of the enabler or executor of the
	 *         given library.
	 */
	private String className(String library, String suffix) {
		String result = CIVLConstants.LIBRARY_PREFIX + library + ".Lib"
				+ library + suffix;

		return result;
	}
}

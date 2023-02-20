package dev.civl.abc.token.IF;

import dev.civl.abc.token.common.CommonFileIndexer;
import dev.civl.abc.token.common.CommonTokenFactory;

/**
 * This class is the entry point for the "token" module. It provides a static
 * method to create a new {@link TokenFactory}. That factory can then be used to
 * produce new tokens and all objects related to tokens.
 * 
 * @author siegel
 * 
 */
public class Tokens {

	/**
	 * Creates a new token factory.
	 * 
	 * @return the new token factory
	 */
	public static TokenFactory newTokenFactory() {
		return new CommonTokenFactory();
	}

	/**
	 * Creates a new, empty {@link FileIndexer}.
	 * 
	 * @return a new {@link FileIndexer}
	 */
	public static FileIndexer newFileIndexer() {
		return new CommonFileIndexer();
	}

}

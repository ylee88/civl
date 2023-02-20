package dev.civl.mc.transform.IF;

import dev.civl.abc.ast.IF.ASTFactory;

public class Transforms {

	public static TransformerFactory newTransformerFactory(ASTFactory astFactory) {
		return new TransformerFactory(astFactory);
	}

}

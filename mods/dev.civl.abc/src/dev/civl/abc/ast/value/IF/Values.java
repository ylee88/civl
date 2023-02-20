package dev.civl.abc.ast.value.IF;

import dev.civl.abc.ast.type.IF.TypeFactory;
import dev.civl.abc.ast.value.common.CommonValueFactory;
import dev.civl.abc.config.IF.Configuration;

public class Values {

	public static ValueFactory newValueFactory(Configuration configuration,
			TypeFactory typeFactory) {
		return new CommonValueFactory(configuration, typeFactory);
	}

}

package dev.civl.sarl.preuniverse.IF;

import dev.civl.sarl.IF.number.NumberFactory;
import dev.civl.sarl.expr.IF.ExpressionFactory;
import dev.civl.sarl.expr.IF.Expressions;
import dev.civl.sarl.number.IF.Numbers;
import dev.civl.sarl.object.IF.ObjectFactory;
import dev.civl.sarl.object.IF.Objects;
import dev.civl.sarl.preuniverse.common.CommonFactorySystem;
import dev.civl.sarl.preuniverse.common.CommonPreUniverse;
import dev.civl.sarl.type.IF.SymbolicTypeFactory;
import dev.civl.sarl.type.IF.Types;

public class PreUniverses {
	public static FactorySystem newFactorySystem(ObjectFactory objectFactory,
			SymbolicTypeFactory typeFactory,
			ExpressionFactory expressionFactory) {
		return new CommonFactorySystem(objectFactory, typeFactory,
				expressionFactory);
	}

	public static FactorySystem newIdealFactorySystem() {
		NumberFactory numberFactory = Numbers.REAL_FACTORY;
		ObjectFactory objectFactory = Objects.newObjectFactory(numberFactory);
		SymbolicTypeFactory typeFactory = Types.newTypeFactory(objectFactory);
		ExpressionFactory expressionFactory = Expressions
				.newIdealExpressionFactory(numberFactory, objectFactory,
						typeFactory);

		return newFactorySystem(objectFactory, typeFactory, expressionFactory);
	}

	public static FactorySystem newHerbrandFactorySystem() {
		NumberFactory numberFactory = Numbers.REAL_FACTORY;
		ObjectFactory objectFactory = Objects.newObjectFactory(numberFactory);
		SymbolicTypeFactory typeFactory = Types.newTypeFactory(objectFactory);
		ExpressionFactory expressionFactory = Expressions
				.newHerbrandExpressionFactory(numberFactory, objectFactory,
						typeFactory);

		return newFactorySystem(objectFactory, typeFactory, expressionFactory);
	}

	public static PreUniverse newPreUniverse(FactorySystem system) {
		return new CommonPreUniverse(system);
	}

}

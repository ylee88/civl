package dev.civl.sarl.preuniverse.common;

import java.util.Map;

import dev.civl.sarl.IF.expr.SymbolicConstant;
import dev.civl.sarl.IF.expr.SymbolicExpression;
import dev.civl.sarl.IF.object.StringObject;
import dev.civl.sarl.IF.type.SymbolicType;
import dev.civl.sarl.object.IF.ObjectFactory;
import dev.civl.sarl.preuniverse.IF.PreUniverse;
import dev.civl.sarl.type.IF.SymbolicTypeFactory;

/**
 * A substituter used to change the names of symbolic constants. It is specified
 * by a map on {@link StringObject}s (essentially the same as Strings). This
 * maps the old names to the new. The substitution is applied to all symbolic
 * constants, including bound ones.
 * 
 * @author Stephen F. Siegel
 */
public class NameSubstituter extends ExpressionSubstituter {

	/**
	 * Map from old names to new. A name which does not occur as a key in this
	 * map will not be changed.
	 */
	private Map<StringObject, StringObject> map;

	public NameSubstituter(PreUniverse universe,
 ObjectFactory objectFactory, SymbolicTypeFactory typeFactory,
			Map<StringObject, StringObject> map) {
		super(universe, objectFactory, typeFactory);
		this.map = map;
	}

	@Override
	protected SubstituterState newState() {
		return null;
	}

	@Override
	protected SymbolicExpression substituteQuantifiedExpression(
			SymbolicExpression expr, SubstituterState state) {
		return super.substituteNonquantifiedExpression(expr, state);
	}

	@Override
	protected SymbolicExpression substituteNonquantifiedExpression(
			SymbolicExpression expr, SubstituterState state) {
		if (expr instanceof SymbolicConstant) {
			StringObject oldName = ((SymbolicConstant) expr).name();
			StringObject newName = map.get(oldName);

			if (newName != null) {
				SymbolicType newType = substituteType(expr.type(), state);
				SymbolicConstant result = universe.symbolicConstant(newName,
						newType);

				return result;
			}
		}
		return super.substituteNonquantifiedExpression(expr, state);
	}

}

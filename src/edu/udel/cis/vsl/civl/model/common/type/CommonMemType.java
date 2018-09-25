package edu.udel.cis.vsl.civl.model.common.type;

import java.util.Arrays;

import edu.udel.cis.vsl.civl.model.IF.type.CIVLMemType;
import edu.udel.cis.vsl.civl.model.IF.type.CIVLPointerType;
import edu.udel.cis.vsl.civl.model.IF.type.CIVLPrimitiveType;
import edu.udel.cis.vsl.civl.model.IF.type.CIVLType;
import edu.udel.cis.vsl.sarl.IF.SymbolicUniverse;
import edu.udel.cis.vsl.sarl.IF.type.SymbolicType;

/**
 * <p>
 * Implementation of {@link CIVLMemType}. A struct is used to implement a
 * CIVLMemType: <br>
 * <code>
 * struct CIVLMemType {
 *   int extent;
 *   void * pointers[];
 * }
 * </code>
 * 
 * </p>
 * 
 * @author ziqing
 *
 */
public class CommonMemType extends CommonType implements CIVLMemType {

	/**
	 * A reference to its sub-type:
	 */
	private CIVLPointerType pointerType;

	public CommonMemType(CIVLPointerType pointerType) {
		super();
		this.pointerType = pointerType;
	}

	@Override
	public TypeKind typeKind() {
		return TypeKind.MEM;
	}

	@Override
	public boolean hasState() {
		return false;
	}

	@Override
	public SymbolicType getDynamicType(SymbolicUniverse universe) {
		if (dynamicType == null) {
			SymbolicType subType = pointerType.getDynamicType(universe);

			// there must be such a typedef declaration in civlc.cvh file:
			// typedef struct $mem $mem, so that the user will not allow to
			// declare a type named $mem.
			dynamicType = universe.tupleType(universe.stringObject("$mem"),
					Arrays.asList(universe.integerType(),
							universe.arrayType(subType)));
		}
		return dynamicType;
	}

	@Override
	public CIVLType copyAs(CIVLPrimitiveType type, SymbolicUniverse universe) {
		return type;
	}
}

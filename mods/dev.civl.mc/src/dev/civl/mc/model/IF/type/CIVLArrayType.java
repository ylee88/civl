package dev.civl.mc.model.IF.type;

/**
 * The type for an array of T.
 * 
 * @author Timothy K. Zirkel (zirkel)
 * 
 */
public interface CIVLArrayType extends CIVLType {

	/**
	 * @return The type of elements in this array.
	 */
	CIVLType elementType();

	/**
	 * 
	 * @return Is this a complete array type? (i.e. is the length specified?)
	 */
	boolean isComplete();
	
	/**
	 * @return true iff this array type is complete and has compile-time
	 *         constant array length
	 */
	boolean hasConstantLength();

	/**
	 * computes the dimension of this array type.
	 * 
	 * @return the dimension of this array type.
	 */
	int dimension();

}

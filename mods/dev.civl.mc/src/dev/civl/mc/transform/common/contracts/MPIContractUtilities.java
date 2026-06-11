package dev.civl.mc.transform.common.contracts;

import java.util.LinkedList;
import java.util.List;

import dev.civl.abc.ast.node.IF.NodeFactory;
import dev.civl.abc.ast.node.IF.expression.ExpressionNode;
import dev.civl.abc.ast.node.IF.statement.BlockItemNode;
import dev.civl.abc.ast.node.IF.statement.StatementNode;
import dev.civl.abc.token.IF.Source;

public class MPIContractUtilities {

	static private MPIContractUtilities singleInstance = null;

	/* **************** Intermediate variable names: *****************/

	static final String CIVL_CONTRACT_PREFIX = "_cc";

	static final String ASSIGN_VAR_PREFIX = CIVL_CONTRACT_PREFIX + "_assign_";

	static final String HEAP_VAR_PREFIX = CIVL_CONTRACT_PREFIX + "_heap_";

	static final String EXTENT_VAR_PREFIX = CIVL_CONTRACT_PREFIX + "_extent_";

	static final String BOUND_VAR_PREFIX = CIVL_CONTRACT_PREFIX + "_bv_";

	static final String POINTER_VAR_PREFIX = CIVL_CONTRACT_PREFIX + "_ptr_";

	/* **************** Artificial identifier names: *****************/

	static final String ACSL_RESULT_VAR = "$result";

	static final String HAVOC = "$havoc";

	static final String MEM_HAVOC = "$mem_havoc";

	static ExpressionNode getStateNullExpression(Source source, NodeFactory nodeFactory) {
		return nodeFactory.newStatenullNode(source);
	}

	static ExpressionNode getProcNullExpression(Source source, NodeFactory nodeFactory) {
		return nodeFactory.newProcnullNode(source);
	}

	static String nextAssignName(int counter) {
		return ASSIGN_VAR_PREFIX + counter;
	}

	static String nextAllocationName(int count) {
		return HEAP_VAR_PREFIX + count;
	}

	static String nextPointerName(int count) {
		return POINTER_VAR_PREFIX + count;
	}

	/**
	 * Return a new instance of {@link MemoryAllocation}.
	 * 
	 * @param freshNewValues         see
	 *                               {@linkplain UnconstrainedMemoryAssignment#memoryDefinitions}
	 * @param refreshMemoryLocations see
	 *                               {@linkplain UnconstrainedMemoryAssignment#assignMemoryReferences}
	 * @return
	 */
	static MemoryAllocation newMemoryAllocation(List<BlockItemNode> memoryDefinitions,
			List<BlockItemNode> assignMemoryReferences) {
		if (singleInstance == null)
			singleInstance = new MPIContractUtilities();
		return singleInstance.new MemoryAllocation(memoryDefinitions, assignMemoryReferences);
	}

	/**
	 * Return a new instance of {@link MemoryAllocation}.
	 * 
	 * @param freshNewValues         see
	 *                               {@linkplain UnconstrainedMemoryAssignment#memoryDefinitions}
	 * @param refreshMemoryLocations see
	 *                               {@linkplain UnconstrainedMemoryAssignment#assignMemoryReferences}
	 * @return
	 */
	static MemoryAllocation newMemoryAllocation(List<BlockItemNode> memoryDefinitions,
			StatementNode assignMemoryReferences) {
		if (singleInstance == null)
			singleInstance = new MPIContractUtilities();

		List<BlockItemNode> newList = new LinkedList<>();

		newList.add(assignMemoryReferences);
		return singleInstance.new MemoryAllocation(memoryDefinitions, newList);
	}

	/* *************** package classes *********************/
	// Following classes are mainly used for a better readability of the
	// contract transformers.

	/**
	 * A grouped artificial {@link BlockItemNode}s which shall be used for the
	 * purpose of allocating memory spaces for valid pointers. Usually, memory
	 * spaces are simulated by variables, see {@link memoryDefinitions}, whose life
	 * time must be long enough.
	 * 
	 * @author ziqingluo
	 *
	 */
	class MemoryAllocation {
		/**
		 * <p>
		 * A list of {@link BlockItemNode}s which is an ordered group of object
		 * definitions. These objects have fresh new unconstrained values.
		 * </p>
		 * <p>
		 * Since these object definitions play roles of memory objects, their life time
		 * must be long enough. Thus, this nodes should be inserted in some scopes where
		 * those objects can always be referred until the verification is done.
		 * </p>
		 */
		List<BlockItemNode> memoryDefinitions;

		/**
		 * <p>
		 * A list of {@link BlockItemNode}s which is an ordered group of statements.
		 * These statements will assign memory references to valid pointers.
		 * </p>
		 * 
		 * <p>
		 * In contrast to {@link #memoryDefinitions}, this nodes must stick with their
		 * behaviors (if they are specified under some named behaviors).
		 * </p>
		 */
		List<BlockItemNode> assignMemoryReferences;

		MemoryAllocation(List<BlockItemNode> memoryDefinitions, List<BlockItemNode> assignMemoryReferences) {
			if (memoryDefinitions == null)
				this.memoryDefinitions = new LinkedList<>();
			else
				this.memoryDefinitions = memoryDefinitions;
			if (assignMemoryReferences == null)
				this.assignMemoryReferences = new LinkedList<>();
			else
				this.assignMemoryReferences = assignMemoryReferences;
		}
	}
}

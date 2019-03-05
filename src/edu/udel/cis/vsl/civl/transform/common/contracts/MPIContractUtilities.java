package edu.udel.cis.vsl.civl.transform.common.contracts;

import java.util.LinkedList;
import java.util.List;

import edu.udel.cis.vsl.abc.ast.node.IF.NodeFactory;
import edu.udel.cis.vsl.abc.ast.node.IF.expression.ExpressionNode;
import edu.udel.cis.vsl.abc.ast.node.IF.statement.BlockItemNode;
import edu.udel.cis.vsl.abc.ast.node.IF.statement.StatementNode;
import edu.udel.cis.vsl.abc.token.IF.Source;

public class MPIContractUtilities {

	static private MPIContractUtilities singleInstance = null;

	/* **************** Intermediate variable names: *****************/

	static final String CIVL_CONTRACT_PREFIX = "_cc";

	static final String COLLATE_STATE_PREFIX = CIVL_CONTRACT_PREFIX + "_cs";

	static final String PRE_COLLATE_STATE = COLLATE_STATE_PREFIX + "_pre";

	static final String POST_COLLATE_STATE = COLLATE_STATE_PREFIX + "_post";

	static final String ASSIGN_VAR_PREFIX = CIVL_CONTRACT_PREFIX + "_assign_";

	static final String HEAP_VAR_PREFIX = CIVL_CONTRACT_PREFIX + "_heap_";

	static final String EXTENT_VAR_PREFIX = CIVL_CONTRACT_PREFIX + "_extent_";

	static final String BOUND_VAR_PREFIX = CIVL_CONTRACT_PREFIX + "_bv_";

	static final String POINTER_VAR_PREFIX = CIVL_CONTRACT_PREFIX + "_ptr_";

	/* **************** Artificial identifier names: *****************/

	static final String MPI_COMM_RANK_CONST = "$mpi_comm_rank";

	static final String MPI_COMM_SIZE_CONST = "$mpi_comm_size";

	static final String MPI_COMM_RANK_CALL = "MPI_Comm_rank";

	static final String MPI_COMM_SIZE_CALL = "MPI_Comm_size";

	static final String MPI_BARRIER_CALL = "MPI_Barrier";

	static final String MPI_SNAPSHOT = "$mpi_snapshot";

	static final String MPI_UNSNAPSHOT = "$mpi_unsnapshot";

	static final String MPI_ASSIGNS = "$mpi_assigns";

	static final String MPI_SIZEOF_DATATYPE = "sizeofDatatype";

	static final String MPI_CHECK_EMPTY_COMM = "$mpi_comm_empty";

	final static String MPI_COMM_WORLD = "MPI_COMM_WORLD";

	static final String MPI_COMM_P2P_MODE = "P2P";

	static final String MPI_COMM_COL_MODE = "COL";

	static final String MEMCPY_CALL = "memcpy";

	static final String COLLATE_COMPLETE = "$collate_complete";

	static final String COLLATE_ARRIVED = "$collate_arrived";

	static final String COLLATE_PRE_STATE = "_cs_pre";

	static final String PRE_STATE = "_s_pre";

	static final String COLLATE_POST_STATE = "_cs_post";

	static final String STATE_NULL = "$state_null";

	static final String PROC_NULL = "$proc_null";

	static final String COLLATE_STATE_TYPE = "$collate_state";

	static final String COLLATE_GET_STATE_CALL = "$collate_get_state";

	static final String REGULAR_GET_STATE_CALL = "$get_state";

	static final String COLLATE_SNAPSHOT = "$collate_snapshot";

	static final String ACSL_RESULT_VAR = "$result";

	static final String HAVOC = "$havoc";

	static final String MEM_HAVOC = "$mem_havoc";

	static ExpressionNode getStateNullExpression(Source source,
			NodeFactory nodeFactory) {
		return nodeFactory.newStatenullNode(source);
	}

	static ExpressionNode getProcNullExpression(Source source,
			NodeFactory nodeFactory) {
		return nodeFactory.newProcnullNode(source);
	}

	static String nextAssignName(int counter) {
		return ASSIGN_VAR_PREFIX + counter;
	}

	static String nextAllocationName(int count) {
		return HEAP_VAR_PREFIX + count;
	}

	static String nextExtentName(int count) {
		return EXTENT_VAR_PREFIX + count;
	}

	static String nextPointerName(int count) {
		return POINTER_VAR_PREFIX + count;
	}

	/**
	 * Return a new instance of {@link MemoryAllocation}.
	 * 
	 * @param freshNewValues
	 *            see
	 *            {@linkplain UnconstrainedMemoryAssignment#memoryDefinitions}
	 * @param refreshMemoryLocations
	 *            see
	 *            {@linkplain UnconstrainedMemoryAssignment#assignMemoryReferences}
	 * @return
	 */
	static MemoryAllocation newMemoryAllocation(
			List<BlockItemNode> memoryDefinitions,
			List<BlockItemNode> assignMemoryReferences) {
		if (singleInstance == null)
			singleInstance = new MPIContractUtilities();
		return singleInstance.new MemoryAllocation(memoryDefinitions,
				assignMemoryReferences);
	}

	/**
	 * Return a new instance of {@link MemoryAllocation}.
	 * 
	 * @param freshNewValues
	 *            see
	 *            {@linkplain UnconstrainedMemoryAssignment#memoryDefinitions}
	 * @param refreshMemoryLocations
	 *            see
	 *            {@linkplain UnconstrainedMemoryAssignment#assignMemoryReferences}
	 * @return
	 */
	static MemoryAllocation newMemoryAllocation(
			List<BlockItemNode> memoryDefinitions,
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
	 * spaces are simulated by variables, see {@link memoryDefinitions}, whose
	 * life time must be long enough.
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
		 * Since these object definitions play roles of memory objects, their
		 * life time must be long enough. Thus, this nodes should be inserted in
		 * some scopes where those objects can always be referred until the
		 * verification is done.
		 * </p>
		 */
		List<BlockItemNode> memoryDefinitions;

		/**
		 * <p>
		 * A list of {@link BlockItemNode}s which is an ordered group of
		 * statements. These statements will assign memory references to valid
		 * pointers.
		 * </p>
		 * 
		 * <p>
		 * In contrast to {@link #memoryDefinitions}, this nodes must stick with
		 * their behaviors (if they are specified under some named behaviors).
		 * </p>
		 */
		List<BlockItemNode> assignMemoryReferences;

		MemoryAllocation(List<BlockItemNode> memoryDefinitions,
				List<BlockItemNode> assignMemoryReferences) {
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

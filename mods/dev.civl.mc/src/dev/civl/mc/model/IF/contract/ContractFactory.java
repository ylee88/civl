package dev.civl.mc.model.IF.contract;

import java.util.List;
import java.util.Set;

import dev.civl.mc.model.IF.CIVLFunction;
import dev.civl.mc.model.IF.CIVLSource;
import dev.civl.mc.model.IF.Scope;
import dev.civl.mc.model.IF.contract.CompositeEvent.CompositeEventOperator;
import dev.civl.mc.model.IF.contract.DependsEvent.DependsEventKind;
import dev.civl.mc.model.IF.contract.MPICollectiveBehavior.MPICommunicationPattern;
import dev.civl.mc.model.IF.expression.Expression;

/**
 * This factory is to create new instances of function contract components.
 * 
 * @author Manchun Zheng
 *
 */
public interface ContractFactory {
	/**
	 * Creates a new function behavior.
	 * 
	 * @param source
	 *            the source of the function behavior
	 * @return
	 */
	FunctionBehavior newFunctionBehavior(CIVLSource source);

	/**
	 * Creates a new named function behavior
	 * 
	 * @param source
	 * @param name
	 * @return
	 */
	NamedFunctionBehavior newNamedFunctionBehavior(CIVLSource source,
			String name);

	/**
	 * Creates a new function contract.
	 * 
	 * @param source
	 * @return
	 */
	FunctionContract newFunctionContract(CIVLSource source, Scope scope);

	/**
	 * Creates a new instance of call event.
	 * 
	 * @param function
	 * @param arguments
	 * @return
	 */
	CallEvent newCallEvent(CIVLSource source, CIVLFunction function,
			List<Expression> arguments);

	/**
	 * Creates a new instance of composite event.
	 * 
	 * @param source
	 * @param op
	 * @param@Override left
	 * @param right
	 * @return
	 */
	CompositeEvent newCompositeEvent(CIVLSource source,
			CompositeEventOperator op, DependsEvent left, DependsEvent right);

	/**
	 * Creates a new instance of memory event of the given kind.
	 * 
	 * @param source
	 * @param kind
	 *            the kind of this memory event, which could be either READ,
	 *            WRITE or REACH.
	 * @param memoryUnits
	 * @return
	 */
	MemoryEvent newMemoryEvent(CIVLSource source, DependsEventKind kind,
			Set<Expression> memoryUnits);

	// /**
	// * Creates a new instance of <code>\read</code> event.
	// *
	// * @param source
	// * @param memoryUnits
	// * @return
	// */
	// MemoryEvent newWriteEvent(CIVLSource source, Set<Expression>
	// memoryUnits);

	/**
	 * Creates a new instance of <code>\anyact</code> event
	 * 
	 * @param source
	 * @return
	 */
	DependsEvent newAnyactEvent(CIVLSource source);

	/**
	 * Creates a new instance of <code>\noact</code> event
	 * 
	 * @param source
	 * @return
	 */
	DependsEvent newNoactEvent(CIVLSource source);

	/**
	 * Creates a new instance of <code>\mpi_collective(comm, pattern)</code>
	 * 
	 * @param source
	 * @param communicator
	 * @param pattern
	 * @return
	 */
	MPICollectiveBehavior newMPICollectiveBehavior(CIVLSource source,
			Expression communicator, MPICommunicationPattern pattern);
}

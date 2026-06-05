package dev.civl.mc.model.common.contract;

import java.util.List;
import java.util.Set;

import dev.civl.mc.model.IF.CIVLFunction;
import dev.civl.mc.model.IF.CIVLSource;
import dev.civl.mc.model.IF.Scope;
import dev.civl.mc.model.IF.contract.CallEvent;
import dev.civl.mc.model.IF.contract.CompositeEvent;
import dev.civl.mc.model.IF.contract.CompositeEvent.CompositeEventOperator;
import dev.civl.mc.model.IF.contract.ContractFactory;
import dev.civl.mc.model.IF.contract.DependsEvent;
import dev.civl.mc.model.IF.contract.DependsEvent.DependsEventKind;
import dev.civl.mc.model.IF.contract.FunctionBehavior;
import dev.civl.mc.model.IF.contract.FunctionContract;
import dev.civl.mc.model.IF.contract.MemoryEvent;
import dev.civl.mc.model.IF.contract.NamedFunctionBehavior;
import dev.civl.mc.model.IF.expression.Expression;

public class CommonContractFactory implements ContractFactory {

	@Override
	public FunctionBehavior newFunctionBehavior(CIVLSource source) {
		return new CommonFunctionBehavior(source);
	}

	@Override
	public NamedFunctionBehavior newNamedFunctionBehavior(CIVLSource source, String name) {
		return new CommonNamedFunctionBehavior(source, name);
	}

	@Override
	public FunctionContract newFunctionContract(CIVLSource source, Scope scope) {
		return new CommonFunctionContract(source, scope);
	}

	@Override
	public CallEvent newCallEvent(CIVLSource source, CIVLFunction function, List<Expression> arguments) {
		return new CommonCallEvent(source, function, arguments);
	}

	@Override
	public CompositeEvent newCompositeEvent(CIVLSource source, CompositeEventOperator op, DependsEvent left,
			DependsEvent right) {
		return new CommonCompositeEvent(source, op, left, right);
	}

	@Override
	public MemoryEvent newMemoryEvent(CIVLSource source, DependsEventKind kind, Set<Expression> memoryUnits) {
		return new CommonMemoryEvent(source, kind, memoryUnits);
	}

	@Override
	public DependsEvent newAnyactEvent(CIVLSource source) {
		return new CommonDependsEvent(source, DependsEventKind.ANYACT);
	}

	@Override
	public DependsEvent newNoactEvent(CIVLSource source) {
		return new CommonDependsEvent(source, DependsEventKind.NOACT);
	}

}

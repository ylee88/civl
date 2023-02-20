package dev.civl.abc.ast.entity.common;

import dev.civl.abc.ast.entity.IF.BehaviorEntity;
import dev.civl.abc.ast.node.IF.acsl.BehaviorNode;

public class CommonBehavior implements BehaviorEntity {

	private String name;
	private BehaviorNode behavior;

	public CommonBehavior(String name, BehaviorNode behavior) {
		this.name = name;
		this.behavior = behavior;
	}

	@Override
	public BehaviorNode getBehavior() {
		return this.behavior;
	}

	@Override
	public EntityKind getEntityKind() {
		return EntityKind.BEHAVIOR;
	}

	@Override
	public String getName() {
		return this.name;
	}

}

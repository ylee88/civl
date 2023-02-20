package dev.civl.abc.ast.entity.IF;

import dev.civl.abc.ast.node.IF.acsl.BehaviorNode;

/**
 * A behavior is an entity which has a name and an associating behavior node.
 * 
 * @author Manchun Zheng
 *
 */
public interface BehaviorEntity extends Entity {
	BehaviorNode getBehavior();
}

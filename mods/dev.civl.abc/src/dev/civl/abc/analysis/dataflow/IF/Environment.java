package dev.civl.abc.analysis.dataflow.IF;

import java.util.Map;

import dev.civl.abc.ast.entity.IF.Entity;

/*
 * Mapping from entities to abstract values
 * 
 * What to include?
 * 
 * How to use?
 */

public interface Environment {
	Map<Entity, AbstractValue> map = null;
}

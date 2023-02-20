package dev.civl.abc.ast.entity.common;

import dev.civl.abc.ast.entity.IF.BehaviorEntity;
import dev.civl.abc.ast.entity.IF.EntityFactory;
import dev.civl.abc.ast.entity.IF.Function;
import dev.civl.abc.ast.entity.IF.Label;
import dev.civl.abc.ast.entity.IF.ProgramEntity;
import dev.civl.abc.ast.entity.IF.Scope;
import dev.civl.abc.ast.entity.IF.Scope.ScopeKind;
import dev.civl.abc.ast.entity.IF.Typedef;
import dev.civl.abc.ast.entity.IF.Variable;
import dev.civl.abc.ast.node.IF.ASTNode;
import dev.civl.abc.ast.node.IF.acsl.BehaviorNode;
import dev.civl.abc.ast.node.IF.label.OrdinaryLabelNode;
import dev.civl.abc.ast.type.IF.Type;

public class CommonEntityFactory implements EntityFactory {

	@Override
	public Scope newScope(ScopeKind kind, Scope parent, ASTNode root) {
		return new CommonScope(kind, (CommonScope) parent, root);
	}

	@Override
	public Variable newVariable(String name, ProgramEntity.LinkageKind linkage,
			Type type) {
		return new CommonVariable(name, linkage, type);
	}

	@Override
	public Function newFunction(String name, ProgramEntity.LinkageKind linkage,
			Type type) {
		return new CommonFunction(name, linkage, type);
	}

	@Override
	public Typedef newTypedef(String name, Type type) {
		return new CommonTypedef(name, type);
	}

	@Override
	public Label newLabel(OrdinaryLabelNode declaration) {
		return new CommonLabel(declaration);
	}

	@Override
	public Scope join(Scope scope1, Scope scope2) {
		for (Scope scope1a = scope1; scope1a != null; scope1a = scope1a
				.getParentScope())
			for (Scope scope2a = scope2; scope2a != null; scope2a = scope2a
					.getParentScope())
				if (scope1a.equals(scope2a))
					return scope2a;
		return null;
	}

	@Override
	public BehaviorEntity newBehavior(String name, BehaviorNode behavior) {
		return new CommonBehavior(name, behavior);
	}
}

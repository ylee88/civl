package dev.civl.abc.transform.IF;

import dev.civl.abc.ast.IF.ASTFactory;

public abstract class TransformRecord {

	public String code;

	public String name;

	public String shortDescription;

	public TransformRecord(String code, String name, String shortDescription) {
		this.code = code;
		this.name = name;
		this.shortDescription = shortDescription;
	}

	public abstract Transformer create(ASTFactory astFactory);
}

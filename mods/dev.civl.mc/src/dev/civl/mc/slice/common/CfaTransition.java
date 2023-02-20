package dev.civl.mc.slice.common;

import dev.civl.mc.model.IF.statement.Statement;

public class CfaTransition {
	
	public Statement statement;
	
	public CfaTransition (Statement s) {
		this.statement = s;
	}
	
	public String toString() {
		if (statement != null) {
			return statement.toString();
		} else {
			return "No source statement.";
		}
	}

}

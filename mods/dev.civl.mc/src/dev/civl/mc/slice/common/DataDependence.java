package dev.civl.mc.slice.common;

import java.io.PrintStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dev.civl.mc.model.IF.expression.AbstractFunctionCallExpression;
import dev.civl.mc.model.IF.expression.AddressOfExpression;
import dev.civl.mc.model.IF.expression.BinaryExpression;
import dev.civl.mc.model.IF.expression.BooleanLiteralExpression;
import dev.civl.mc.model.IF.expression.BoundVariableExpression;
import dev.civl.mc.model.IF.expression.CastExpression;
import dev.civl.mc.model.IF.expression.CharLiteralExpression;
import dev.civl.mc.model.IF.expression.CompoundLiteralExpression;
import dev.civl.mc.model.IF.expression.ConditionalExpression;
import dev.civl.mc.model.IF.expression.DereferenceExpression;
import dev.civl.mc.model.IF.expression.DerivativeCallExpression;
import dev.civl.mc.model.IF.expression.DomainGuardExpression;
import dev.civl.mc.model.IF.expression.DotExpression;
import dev.civl.mc.model.IF.expression.DynamicTypeOfExpression;
import dev.civl.mc.model.IF.expression.Expression;
import dev.civl.mc.model.IF.expression.FunctionGuardExpression;
import dev.civl.mc.model.IF.expression.FunctionIdentifierExpression;
import dev.civl.mc.model.IF.expression.HereOrRootExpression;
import dev.civl.mc.model.IF.expression.InitialValueExpression;
import dev.civl.mc.model.IF.expression.IntegerLiteralExpression;
import dev.civl.mc.model.IF.expression.LHSExpression;
import dev.civl.mc.model.IF.expression.LiteralExpression;
import dev.civl.mc.model.IF.expression.MemoryUnitExpression;
import dev.civl.mc.model.IF.expression.ProcnullExpression;
import dev.civl.mc.model.IF.expression.QuantifiedExpression;
import dev.civl.mc.model.IF.expression.RealLiteralExpression;
import dev.civl.mc.model.IF.expression.RecDomainLiteralExpression;
import dev.civl.mc.model.IF.expression.RegularRangeExpression;
import dev.civl.mc.model.IF.expression.ScopeofExpression;
import dev.civl.mc.model.IF.expression.SelfExpression;
import dev.civl.mc.model.IF.expression.SizeofExpression;
import dev.civl.mc.model.IF.expression.SizeofTypeExpression;
import dev.civl.mc.model.IF.expression.SubscriptExpression;
import dev.civl.mc.model.IF.expression.SystemGuardExpression;
import dev.civl.mc.model.IF.expression.UnaryExpression;
import dev.civl.mc.model.IF.expression.UndefinedProcessExpression;
import dev.civl.mc.model.IF.expression.VariableExpression;
import dev.civl.mc.model.IF.location.Location;
import dev.civl.mc.model.IF.statement.AssignStatement;
import dev.civl.mc.model.IF.statement.CallOrSpawnStatement;
import dev.civl.mc.model.IF.statement.Statement;
import dev.civl.mc.model.IF.statement.Statement.StatementKind;
import dev.civl.mc.model.IF.variable.Variable;
import dev.civl.mc.util.IF.Pair;

public class DataDependence {
	
	private List<ErrorCfaLoc> backwardsTrace;
	private boolean debug = false;
	
	private PrintStream out = System.out;
	
	public DataDependence (List<ErrorCfaLoc> trace) {
		
		/* Reverse solution taken from:
		 * stackoverflow.com/questions/3962766/how-to-reverse-a-list-in-java */
		List<ErrorCfaLoc> shallowCopyTrace = trace.subList(0, trace.size());
		Collections.reverse(shallowCopyTrace);
		
		this.backwardsTrace = shallowCopyTrace;
		discoverDependence(backwardsTrace);
		
	}
	
	/* As Matt did, just print out results until you know where
	 * to stuff the data. */
	public void discoverDependence (List<ErrorCfaLoc> trace) {
		
		Set<Variable> variables = new HashSet<>();
		if (debug) out.println("\n*** Begin Data Dependence DEBUG ***\n");
		
		for (ErrorCfaLoc l : trace) {
			
			if (l.getCIVLLocation() == null) {
				continue;
			} else if (isBranching(l)) {
				
				variables.addAll(variablesInBranch(l));
				
			} else if (isAssignment(l)) {
				
				Variable lhs = getLHS(l);
				if (variables.contains(lhs)) {
					if (debug) out.println("   Variables set contains "+lhs);
					variables.remove(lhs);
					variables.addAll(variablesInAssignRHS(l));
				}
				
			} else if (isFunctionCall(l)) {
				
				Variable lhs = getLHS(l);
				if (variables.contains(lhs)) {
					if (!l.toString().contains("VERIFIER_nondet")) {
						assert false : "The LHS variable is assigned"+
								"a value from a function call. Not yet implemented.";
					}
				}
				
			}
		}
		
		if (debug) out.println("\n*** End Data Dependence DEBUG ***\n");
		
	}

	private Set<Variable> variablesInAssignRHS(ErrorCfaLoc l) {
		
		AssignStatement as = (AssignStatement) l.nextTransition().statement;
		Expression rhs = as.rhs();
		Set<Variable> variables = collectVariables(rhs);
		if (debug) out.println("Variables found in RHS:"); for (Variable v : variables) out.println("  "+v);
		return variables;
		
	}

	private Set<Variable> variablesInBranch(ErrorCfaLoc l) {
		
		Expression guard = l.nextTransition().statement.guard();
		Set<Variable> variables = collectVariables(guard);
		if (debug) out.println("Variables found in branch:"); for (Variable v : variables) out.println("  "+v);
		return variables;
		
	}

	private Variable getLHS(ErrorCfaLoc l) {
		
		Statement s = l.nextTransition().statement;
		StatementKind kind = s.statementKind();
		Variable lhs = null;
		
		switch (kind) {
			case ASSIGN :
				AssignStatement as = (AssignStatement) s;
				assert as.getLhs() != null : "Assign statement has no LHS.";
				lhs = as.getLhs().variableWritten();
				if (debug) out.println("Assign statement with lhs: "+lhs);
				break;
			case CALL_OR_SPAWN :
				CallOrSpawnStatement cs = (CallOrSpawnStatement) s;
				if (cs.lhs() == null) {
					break;
				} else {
					lhs = cs.lhs().variableWritten();
				}
				if (debug) out.println("Call statement with lhs: "+lhs);
				break;
			default : assert false : "Neither assign nor call statement.";
		}
		
		return lhs;
		
	}

	private boolean isFunctionCall(ErrorCfaLoc l) {

		Statement s = l.nextTransition().statement;
		if (s.statementKind().equals(Statement.StatementKind.CALL_OR_SPAWN)) {
			return true;
		} else {
			return false;
		}
		
	}

	private boolean isAssignment(ErrorCfaLoc l) {
		
		Statement s = l.nextTransition().statement;
		if (s.statementKind().equals(Statement.StatementKind.ASSIGN)) {
			return true;
		} else {
			return false;
		}
		
	}

	private boolean isBranching(ErrorCfaLoc l) {	
		
		Location loc = l.getCIVLLocation();
		if (loc.getNumOutgoing() > 1) {
			Expression guard = l.nextTransition().statement.guard();
			if (guard.toString().contains("$sef$")) {
				if (debug) out.println("  (skipping: "+guard+"; it's instrumentation)");
				return false;
			} else {
				if (debug) out.println("Branch: "+guard);
				return true;
			}
		} else {
			return false;
		}
		
	}
	
	/* Collect Variables from a given Expression */
	private Set<Variable> collectVariables(Expression expr) {
		Set<Variable> vars = new HashSet<Variable>();
		collectVariablesWorker(expr,vars);
		return vars;
	}
	
	private void collectVariablesWorker(Expression expr, Set<Variable> vars) {
		if (expr instanceof AbstractFunctionCallExpression) {
			List<Expression> args = ((AbstractFunctionCallExpression) expr).arguments();
			for (Expression e : args) 
				collectVariablesWorker(e,vars);
		} else if (expr instanceof AddressOfExpression) {
			collectVariablesWorker(((AddressOfExpression)expr).operand(),vars);
		} else if (expr instanceof BinaryExpression) {
			Expression left = ((BinaryExpression) expr).left();
			Expression right = ((BinaryExpression) expr).right();
			collectVariablesWorker(left,vars);
			collectVariablesWorker(right,vars);
		} else if (expr instanceof BooleanLiteralExpression) {
			// do nothing - is this correct?
		} else if (expr instanceof BoundVariableExpression) {
			// ask 
		} else if (expr instanceof CastExpression) {
			Expression e = ((CastExpression) expr).getExpression();
			collectVariablesWorker(e,vars);
		} else if (expr instanceof CharLiteralExpression) {
			// do nothing
		} else if (expr instanceof ConditionalExpression) {
			// ask 
			ConditionalExpression condExpr = (ConditionalExpression) expr;
			Expression cond = condExpr.getCondition();
			Expression trueBranch = condExpr.getTrueBranch();
			Expression falseBranch = condExpr.getFalseBranch();
			collectVariablesWorker(cond,vars);
			collectVariablesWorker(trueBranch,vars);
			collectVariablesWorker(falseBranch,vars);
		} else if (expr instanceof DereferenceExpression) {
			Expression p = ((DereferenceExpression) expr).pointer();
			collectVariablesWorker(p,vars);
		} else if (expr instanceof DerivativeCallExpression) {
			// what are these expressions?
			List<Pair<Variable, IntegerLiteralExpression>> partials = ((DerivativeCallExpression) expr).partials();
			for (Pair<Variable, IntegerLiteralExpression> p : partials) {
				vars.add(p.left);
			}
		} else if (expr instanceof DomainGuardExpression) {
			// ask what this is doing
		} else if (expr instanceof DotExpression) {
			// ask  
			Expression e = ((DotExpression) expr).structOrUnion();
			collectVariablesWorker(e,vars);
		} else if (expr instanceof DynamicTypeOfExpression) {
			// do nothing
		} else if (expr instanceof FunctionGuardExpression) {
			// ask 
			FunctionGuardExpression fgExpr = (FunctionGuardExpression) expr;
			Expression funcExpr = fgExpr.functionExpression();
			collectVariablesWorker(funcExpr,vars);
			List<Expression> args = fgExpr.arguments();
			for (Expression e : args) {
				collectVariablesWorker(e,vars);
			}
		} else if (expr instanceof FunctionIdentifierExpression) {
			// do nothing
		} else if (expr instanceof HereOrRootExpression) {
			// do nothing
		} else if (expr instanceof InitialValueExpression) {
			Variable v = ((InitialValueExpression) expr).variable();
			vars.add(v);
		} else if (expr instanceof IntegerLiteralExpression) {
			// do nothing
		} else if (expr instanceof LHSExpression) {
			Variable v = ((LHSExpression) expr).variableWritten();
			vars.add(v);
		} else if (expr instanceof LiteralExpression) {
			// do nothing
		} else if (expr instanceof MemoryUnitExpression) {
			Variable v = ((MemoryUnitExpression) expr).variable();
			vars.add(v);
		} else if (expr instanceof ProcnullExpression) {
			// do nothing (this is just an empty interface extending Expression)
		} else if (expr instanceof QuantifiedExpression) {
			// is this correct?
			Expression e = ((QuantifiedExpression) expr).expression();
			collectVariablesWorker(e,vars);
		} else if (expr instanceof RealLiteralExpression) {
			// do nothing
		} else if (expr instanceof RecDomainLiteralExpression) {
			// ask 
		} else if (expr instanceof RegularRangeExpression) {
			// this is a CIVL-C expression kind - can we ignore?
		} else if (expr instanceof ScopeofExpression) {
			LHSExpression lhsExpr = ((ScopeofExpression) expr).argument();
			Expression e = (Expression) lhsExpr;
			collectVariablesWorker(e,vars);
		} else if (expr instanceof SelfExpression) {
			// do nothing
		} else if (expr instanceof SizeofExpression) {
			Expression e = ((SizeofExpression) expr).getArgument();
			collectVariablesWorker(e,vars);
		} else if (expr instanceof SizeofTypeExpression) {
			// do nothing
		} else if (expr instanceof CompoundLiteralExpression) {
			CompoundLiteralExpression compound = (CompoundLiteralExpression) expr;

			if (!compound.hasConstantValue())
				for (Expression subExpr : compound.getLiteralObject()
						.subExpressions())
					collectVariablesWorker(subExpr, vars);
		} else if (expr instanceof SubscriptExpression) {
			// ask 
		}  else if (expr instanceof SystemGuardExpression) {
			List<Expression> args = ((SystemGuardExpression) expr).arguments();
			for (Expression e : args) {
				collectVariablesWorker(e,vars);
			}
		} else if (expr instanceof UnaryExpression) {
			Expression e = ((UnaryExpression) expr).operand();
			collectVariablesWorker(e,vars);
		} else if (expr instanceof UndefinedProcessExpression) {
			// do nothing	
		} else if (expr instanceof VariableExpression) {
			vars.add(((VariableExpression)expr).variable());
		} else {
			assert false : expr;
		}
	}

}

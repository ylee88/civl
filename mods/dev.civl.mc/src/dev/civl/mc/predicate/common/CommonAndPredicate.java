package dev.civl.mc.predicate.common;

import java.util.LinkedHashSet;
import java.util.Set;

import dev.civl.mc.log.IF.CIVLExecutionException;
import dev.civl.mc.predicate.IF.AndPredicate;
import dev.civl.mc.predicate.IF.CIVLStatePredicate;
import dev.civl.mc.state.IF.State;

public class CommonAndPredicate extends CommonCIVLStatePredicate implements
		AndPredicate {

	private Set<CIVLStatePredicate> clauses = new LinkedHashSet<>();

	public CommonAndPredicate(CIVLStatePredicate clause) {
		this.clauses.add(clause);
	}

	@Override
	public boolean holdsAt(State state) {
		for (CIVLStatePredicate predicate : this.clauses) {
			if (predicate.holdsAt(state))
				return true;
		}
		return false;
	}

	@Override
	public String explanation() {
		return this.getUnreportedViolatedPredicate().explanation();
	}

	@Override
	public void addClause(CIVLStatePredicate predicate) {
		this.clauses.add(predicate);
	}

	@Override
	public Iterable<CIVLStatePredicate> clauses() {
		return this.clauses;
	}

	@Override
	public boolean isAndPredicate() {
		return true;
	}

	@Override
	public CIVLExecutionException getViolation() {
		return this.getUnreportedViolation();
	}

	@Override
	public CIVLExecutionException getUnreportedViolation() {
		CIVLStatePredicate predicate = this.getUnreportedViolatedPredicate();

		if (predicate != null)
			return predicate.getViolation();
		return null;
	}

	@Override
	public CIVLStatePredicate getUnreportedViolatedPredicate() {
		for (CIVLStatePredicate predicate : clauses) {
			CIVLExecutionException violation = predicate
					.getUnreportedViolation();

			if (violation != null)
				return predicate;
		}
		return null;
	}
}

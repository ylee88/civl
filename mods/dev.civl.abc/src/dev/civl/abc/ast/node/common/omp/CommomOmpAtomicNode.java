package dev.civl.abc.ast.node.common.omp;

import dev.civl.abc.ast.node.IF.omp.OmpAtomicNode;
import dev.civl.abc.ast.node.IF.statement.StatementNode;
import dev.civl.abc.token.IF.Source;

public class CommomOmpAtomicNode extends CommonOmpSyncNode
		implements
			OmpAtomicNode {

	private OmpAtomicClause clause;

	private boolean seqConsistent = false;

	public CommomOmpAtomicNode(Source source, StatementNode statement,
			OmpAtomicClause atomicClause, boolean seqConsistent) {
		super(source, OmpSyncNodeKind.OMPATOMIC, statement);
		clause = atomicClause != null ? atomicClause : OmpAtomicClause.UPDATE;
		this.seqConsistent = seqConsistent;
	}

	@Override
	public OmpAtomicClause atomicClause() {
		return clause;
	}

	@Override
	public boolean seqConsistent() {
		return seqConsistent;
	}
}

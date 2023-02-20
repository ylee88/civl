package dev.civl.abc.analysis.pointsTo.IF;

/**
 * <p>
 * The flow-insensitive abstraction of a function body for points-to analysis.
 * It consists of a set of {@link AssignmentIF}s, each of which represents an
 * assignment that may have an impact on what a pointer may points-to.
 * </p>
 * 
 * 
 * @author ziqing
 *
 */
public interface InsensitiveFlow extends Iterable<AssignmentIF> {

	/**
	 * 
	 * @return the reference to a {@link InsensitiveFlowFactory} that creates
	 *         this {@link InsensitiveFlow}
	 */
	InsensitiveFlowFactory insensitiveFlowfactory();
}

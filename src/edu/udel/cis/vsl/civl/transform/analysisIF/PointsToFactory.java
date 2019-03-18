package edu.udel.cis.vsl.civl.transform.analysisIF;

/**
 * A factory that provides the interface for generating {@link PointsToNode}s
 * and {@link PointsToSubsetRelation}s in fly-weight pattern.
 * 
 * @author ziqing
 *
 */
public interface PointsToFactory {

	/**
	 * Creates a new {@link PointsToNode}
	 * 
	 * @return
	 */
	PointsToNode newNode();

	/**
	 * Creates a new {@link PointsToSubsetRelation}
	 * 
	 * @param subSet
	 * @param superSet
	 * @return
	 */
	PointsToSubsetRelation isSubSetOf(PointsToNode subSet,
			PointsToNode superSet);

	/**
	 * @param r0
	 *            a subset-of relation <code>b X a</code>
	 * @param r1
	 *            a subset-of relation <code>c X a</code>
	 * 
	 * @return a subset-of relation <code>{b,c} X a</code>
	 */
	PointsToSubsetRelation union(PointsToSubsetRelation r0, PointsToNode r1);

	/**
	 * This intersection rule makes sense under the fact that
	 * {@link PointsToNode}s are disjoint.
	 * 
	 * @param r0
	 *            a subset-of relation <code>a X b</code>
	 * @param r1
	 *            a subset-of relation <code>a X c</code>
	 * 
	 * @return a subset-of relation <code>a X intersection(b, c)</code>
	 */
	PointsToSubsetRelation intersect(PointsToSubsetRelation r0,
			PointsToNode r1);
}

package edu.udel.cis.vsl.civl.transform.analysisIF;

/**
 * <p>
 * A binary <b>transitive</b> relation <code>PointsToNode X PointsToNode</code>.
 * A relation <code>n X n'</code> means that <code>n</code> is a subset of
 * <code>n'</code>.
 * </p>
 * 
 * <p>
 * An instance of {@link PointsToSubsetRelation} is an element of the relation
 * <code>n X n'</code> that {@link #subSet()} gives <code>n</code> and
 * {@link #superSet()} gives <code>n'</code>.
 * </p>
 * 
 * 
 * @author ziqing
 *
 */
public interface PointsToSubsetRelation {

	PointsToNode subSet();

	PointsToNode superSet();
}

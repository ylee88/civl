package edu.udel.cis.vsl.civl.util.IF;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;

/**
 * A {@link SeqSet} represents a set of sequences of nonnegative integers. By
 * sequence, we mean finite sequence of integers. Only certain sets are
 * representable. If p and q are sequences, write p<=q if p is a prefix of q.
 * (This includes the case p=q.) If p is a sequence, let [p] = {q|p<=q}. A
 * {@link SeqSet} represents a finite union of sets of the form [p]. If p<=q
 * then [p] contains [q], so any SeqSet S can be represented as a finite union
 * of sets of the form [p] where p is a minimal element of S.
 * 
 * <p>
 * If () is the empty sequence then [()] consists of all sequences. It is
 * represented by the SeqSet {()}.
 * </p>
 * 
 * <p>
 * {(1,2),(1,3,4),(2)} is a {@link SeqSet}.
 * </p>
 * 
 * @author siegel
 *
 */
public class SeqSet {

	// Types...

	/**
	 * A node in the tree representation. Each node represents a single integer.
	 * A minimal element of the set corresponds to a path in the tree from the
	 * root to a leaf node.
	 * 
	 * @author siegel
	 */
	class Node {
		/**
		 * The parent node in the tree, or null if this is the root.
		 */
		Node parent;

		/**
		 * The index of this node in the parent's list of children. If this is
		 * the root node (the only node with no parent), -1.
		 */
		int index;

		/**
		 * The set consisting of the indexes of the children of this node.
		 */
		SortedSet<Integer> childrenIndexes = new TreeSet<Integer>();
		// would a BitSet be faster? HashSet?

		/**
		 * The children nodes. If i is in childrenIndexes, then children[i] will
		 * be a non-null Node with index i and is considered "active".
		 * Otherwise, children[i] may or may not be null; if not null, the child
		 * is considered "inactive". An inactive child can be reactivated at
		 * some future point when this set changes. This is an optimization---it
		 * should be functionally equivalent but faster than setting children[i]
		 * to null and creating a new Node.
		 */
		Node[] children = new Node[2];

		/**
		 * Creates new node with given index, empty set of childrenIndexes.
		 * 
		 * @param parent
		 *            the parent node or null if the new node will be a root
		 * @param index
		 *            the index for the new node: should be -1 for a root, and
		 *            nonnegative for any other node---the index of this new
		 *            node in its parent
		 */
		Node(Node parent, int index) {
			this.parent = parent;
			this.index = index;
		}

		/**
		 * Does this node have a child with the given index?
		 * 
		 * @param index
		 *            a nonnegative integer
		 * @return {@code true} iff this node has a child with that index
		 */
		boolean hasChild(int index) {
			return childrenIndexes.contains(index);
		}

		/**
		 * Is this node a leaf node? A node is a leaf iff it has 0 children.
		 * 
		 * @return {@code true} iff this is a leaf node
		 */
		boolean isLeaf() {
			return childrenIndexes.isEmpty();
		}

		/**
		 * If this node already contains the child at index, does nothing and
		 * returns {@code false}. Otherwise, the node is created or reactivated
		 * and cleared, and the child index is added to this node's
		 * {@link #childrenIndexes}.
		 * 
		 * @param index
		 *            index of child
		 * @return {@code true} iff the child was not already there
		 */
		boolean setChild(int index) {
			if (!childrenIndexes.add(index))
				return false;

			int length = children.length;

			if (index >= length) {
				do {
					length *= 2;
				} while (index >= length);
				children = Arrays.copyOf(children, length);
			}

			Node child = children[index];

			if (child == null) {
				child = new Node(this, index);
				children[index] = child;
			} else {
				child.clear();
			}
			return true;
		}

		/**
		 * Empties {@link #childrenIndexes}.
		 */
		void clear() {
			childrenIndexes.clear();
		}

		/**
		 * Returns a new iterator over {@link #childrenIndexes}.
		 * 
		 * @return
		 */
		Iterator<Integer> iterator() {
			return childrenIndexes.iterator();
		}

		@Override
		public String toString() {
			return Integer.toString(index);
		}
	}

	// Fields...

	/**
	 * Is this the empty set? If true, then the root is inactive.
	 */
	private boolean isEmpty = true;

	/**
	 * The root node of the tree. Not null, but may be inactive.
	 */
	private Node root = new Node(null, -1);

	// Constructors...

	/**
	 * Creates new empty set. The root node will be created but will be
	 * inactive.
	 */
	public SeqSet() {
	}

	// Methods...

	public boolean isEmpty() {
		return isEmpty;
	}

	/**
	 * Adds the set represented by the given sequence to this set. No part of
	 * the given sequence object will be shared with this set.
	 * 
	 * @param seq
	 *            a non-null (but possibly empty) sequence of nonnegative
	 *            integers
	 * @return <code>true</code> if this operation resulted in a change to this
	 *         set
	 */
	public boolean add(int... seq) {
		int n = seq.length;
		boolean newPath = false;

		if (isEmpty) {
			isEmpty = false;
			newPath = true;
		}

		Node curr = root;

		for (int i = 0; i < n; i++) {
			if ((!newPath) && curr.isLeaf())
				return false; // new set is subsumed by old path

			int idx = seq[i];

			assert idx >= 0;
			if (curr.setChild(idx))
				newPath = true;
			curr = curr.children[idx];
		}
		if (!newPath) { // old path subsumed by val
			boolean change = !curr.isLeaf();

			curr.childrenIndexes.clear();
			return change;
		}
		return newPath;
	}

	private void printLeaves(StringBuffer sb, Node node, Stack<Integer> trail) {
		if (node.childrenIndexes.isEmpty()) {
			if (sb.length() > 2)
				sb.append(", ");
			sb.append(trail.toString());
		} else {
			for (int child : node.childrenIndexes) {
				trail.push(child);
				printLeaves(sb, node.children[child], trail);
				trail.pop();
			}
		}
	}

	private void getLeaves(Collection<int[]> result, Node node,
			Stack<Integer> trail) {
		if (node.childrenIndexes.isEmpty()) {
			int len = trail.size();
			int[] leaf = new int[len];

			for (int i = 0; i < len; i++)
				leaf[i] = trail.get(i);
			result.add(leaf);
		} else {
			for (int child : node.childrenIndexes) {
				trail.push(child);
				getLeaves(result, node.children[child], trail);
				trail.pop();
			}
		}
	}

	public LinkedList<int[]> getLeaves() {
		LinkedList<int[]> result = new LinkedList<>();
		Stack<Integer> trail = new Stack<>();

		if (!isEmpty)
			getLeaves(result, root, trail);
		return result;
	}

	/*
	 * class LeafIterator implements Iterator<Node> {
	 * 
	 * private Stack<Iterator<Integer>> stack = new Stack<>();
	 * 
	 * private Node currentNode;
	 * 
	 * private Iterator<Integer> currentIter;
	 * 
	 * LeafIterator(Node root) { currentNode = root; if (currentNode == null)
	 * return; currentIter = currentNode.iterator(); stack.push(currentIter);
	 * while (currentIter.hasNext()) { currentNode =
	 * currentNode.children[currentIter.next()]; currentIter =
	 * currentNode.iterator(); stack.push(currentIter); } }
	 * 
	 * private void proceedToNext() { // backtrack from current leaf... do {
	 * stack.pop(); currentNode = currentNode.parent; if (currentNode == null) {
	 * assert stack.isEmpty(); currentIter = null; return; } currentIter =
	 * stack.peek(); } while (!currentIter.hasNext()); // now go down to next
	 * leaf... do { currentNode = currentNode.children[currentIter.next()];
	 * currentIter = currentNode.iterator(); stack.push(currentIter); } while
	 * (currentIter.hasNext()); }
	 * 
	 * @Override public boolean hasNext() { return currentNode != null; }
	 * 
	 * @Override public Node next() { Node result = currentNode;
	 * 
	 * if (result == null) throw new NoSuchElementException(); proceedToNext();
	 * return result; }
	 * 
	 * }
	 * 
	 * private Iterator<Node> leafIterator() { return new LeafIterator(root); }
	 * 
	 * private Iterable<Node> leaves() { return new Iterable<Node>() {
	 * 
	 * @Override public Iterator<Node> iterator() { return new
	 * LeafIterator(root); } }; }
	 */

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();

		sb.append('{');
		if (!isEmpty)
			printLeaves(sb, root, new Stack<Integer>());
		sb.append('}');
		return sb.toString();
	}

	/**
	 * Creates a copy of a tree. The source tree is rooted at
	 * <code>source</code>. The copy will be inserted as child <code>idx</code>
	 * of <code>destParent</code>. If <code>destParent</code> is
	 * <code>null</code>, the root of the copy will become the root node of this
	 * SeqSet.
	 * 
	 * @param destinationParent
	 *            the node that will be the parent to the root of the copy of
	 *            the source tree; may be null
	 * @param index
	 *            index under {@code destinationParent}; if
	 *            {@code destinationParent} is {@code null} this must be -1
	 * @param source
	 *            the root of the source tree to be copied; not {@code null}
	 */
	private void copy(Node destinationParent, int index, Node source) {
		Stack<Iterator<Integer>> stack = new Stack<>();
		Node thisNode, thatNode = source;

		stack.push(source.iterator());
		if (destinationParent == null) {
			thisNode = root;
			assert index == -1;
			isEmpty = false;
		} else {
			destinationParent.setChild(index);
			thisNode = destinationParent.children[index];
		}
		while (!stack.isEmpty()) {
			Iterator<Integer> iter = stack.peek();

			if (iter.hasNext()) {
				int i = iter.next();

				thisNode.setChild(i);
				thisNode = thisNode.children[i];
				thatNode = thatNode.children[i];
				stack.push(thatNode.iterator());
			} else {
				stack.pop();
				thisNode = thisNode.parent;
				thatNode = thatNode.parent;
			}
		}
	}

	/**
	 * Inserts a copy of the tree rooted at source into the target SeqSet. If
	 * the source node has prefix alpha, then the path alpha is added to the
	 * target if it is not already there, and the tree rooted at source is
	 * copied.
	 * 
	 * @param target
	 * @param source
	 */
	private static void insertCopy(SeqSet target, Stack<Node> prefix,
			Node source) {
		Node targetNode = target.root;

		if (!prefix.isEmpty()) {
			Iterator<Node> iter = prefix.iterator();
			Node sourceNode = iter.next(); // must be root

			target.isEmpty = false;
			while (iter.hasNext()) {
				sourceNode = iter.next();

				int idx = sourceNode.index;

				targetNode.setChild(idx);
				targetNode = targetNode.children[idx];
			}
		}
		target.copy(targetNode, source.index, source);
	}

	public SeqSet intersectionWith(SeqSet that) {
		SeqSet result = new SeqSet();

		if (isEmpty || that.isEmpty)
			return result;

		Stack<Iterator<Integer>> dfsStack = new Stack<>(); // DFS stack for that
		Stack<Node> prefix = new Stack<Node>();
		Node thisNode = root, thatNode = that.root; // current nodes

		top : while (true) {
			if (thisNode.isLeaf()) {
				insertCopy(result, prefix, thatNode);
				thatNode = thatNode.parent;
				if (thatNode == null)
					break; // search is over
				thisNode = thisNode.parent;
			} else if (thatNode.isLeaf()) {
				insertCopy(result, prefix, thisNode);
				thatNode = thatNode.parent;
				if (thatNode == null)
					break; // search is over
				thisNode = thisNode.parent;
			} else { // neither is leaf: push
				prefix.push(thatNode);
				dfsStack.push(thatNode.iterator());
			}
			/*
			 * At this point, thisNode and thatNode are non-null corresponding
			 * nodes, and neither is a leaf. The iterator at top of stack
			 * corresponds to thatNode. The following will push the search
			 * forward to the next new node pair...
			 */
			do {
				Iterator<Integer> thatIter = dfsStack.peek();

				while (thatIter.hasNext()) {
					int idx = thatIter.next();

					if (thisNode.hasChild(idx)) { // new pair!
						thisNode = thisNode.children[idx];
						thatNode = thatNode.children[idx];
						continue top;
					}
				}
				dfsStack.pop();
				prefix.pop();
				thisNode = thisNode.parent;
				thatNode = thatNode.parent;
			} while (!dfsStack.isEmpty());
			break; // search is complete
		}
		return result;
	}

	/**
	 * Adds everything in the given set to this set. In the post-state, this
	 * SeqSet will represent the union of the set represented by this SeqSet in
	 * the pre-state and the set represented by {@code that}. SeqSet
	 * {@code that} is not modified.
	 * 
	 * <p>
	 * Perform DFS of that while walking through this in tandem with the search.
	 * Specifically:
	 * </p>
	 * 
	 * <p>
	 * Let u be the current node for this and v for that. If u is a leaf then
	 * backtrack. If v is leaf than prune the children of u and backtrack.
	 * 
	 * Otherwise, iterate over the edges of v. For an edge on int a, see if u
	 * has an edge on a. If u does have an edge on a, proceed to the target node
	 * in both trees. If u does not have an edge on a, copy the branch starting
	 * with a from v to u.
	 * </p>
	 * 
	 * @param that
	 *            a non-null {@code SeqSet}
	 * @return {@code true} iff this operation results in a change to this
	 *         {@code SeqSet}
	 */
	public boolean addAll(SeqSet that) {
		if (that.isEmpty)
			return false;
		if (isEmpty) {
			copy(null, -1, that.root);
			return true;
		}

		boolean change = false; // has there been a change to this?
		Stack<Iterator<Integer>> stack = new Stack<>(); // DFS stack for that
		Node thisNode = root, thatNode = that.root; // current nodes

		// invariant: stack specifies a path in that starting from root.
		// the members of stack are child iterators for the nodes in the
		// path from the root of that (inclusive) to thatNode (exclusive).

		// invariant: thisNode and thatNode correspond, i.e., the both
		// represent the same integer sequence.

		// invariant: thatNode results
		// from following top of stack iterator's last edge (or stack is empty
		// and thatNode is root).

		top : while (true) {
			if (thisNode.isLeaf()) {
				// backtrack and proceed to to next new node pair:
				thatNode = thatNode.parent;
				if (thatNode == null)
					break;
				thisNode = thisNode.parent;
			} else if (thatNode.isLeaf()) {
				// prune this then backtrack and proceed to next new node pair:
				thisNode.clear();
				change = true;
				thatNode = thatNode.parent;
				if (thatNode == null)
					break;
				thisNode = thisNode.parent;
			} else { // neither is leaf: push
				stack.push(thatNode.iterator());
			}
			/*
			 * At this point, thisNode and thatNode are non-null corresponding
			 * nodes, and neither is a leaf. The iterator at top of stack
			 * corresponds to thatNode. The following will push the search
			 * forward to the next new node pair...
			 */
			do {
				Iterator<Integer> thatIter = stack.peek();

				while (thatIter.hasNext()) {
					int idx = thatIter.next();

					if (thisNode.hasChild(idx)) { // new pair!
						thisNode = thisNode.children[idx];
						thatNode = thatNode.children[idx];
						continue top;
					} else {
						copy(thisNode, idx, thatNode.children[idx]);
					}
				}
				stack.pop();
				thisNode = thisNode.parent;
				thatNode = thatNode.parent;
			} while (!stack.isEmpty());
			break; // no new node pair: search is complete
		}
		return change;
	}

	@Override
	public SeqSet clone() {
		SeqSet result = new SeqSet();

		result.addAll(this);
		return result;
	}

	/**
	 * Does this set contain the set [s] of sequences which extend a given
	 * sequence s?
	 * 
	 * @param seq
	 *            a non-null (but possibly empty) sequence of nonnegative
	 *            integers; note that if s is the empty sequence the [s] is the
	 *            universal set consisting of all sequences
	 * @return {@code} true iff this set contains all sequences which extend
	 *         {@code seq} (including {@code seq} itself)
	 */
	public boolean contains(int... seq) {
		int n = seq.length;

		if (isEmpty)
			return false;
		Node node = root;
		for (int i = 0; i < n; i++) {
			int idx = seq[i];

			if (!node.hasChild(idx))
				break;
			node = node.children[idx];
		}
		return node.isLeaf();
	}

	/**
	 * Does this set contain the given set?
	 * 
	 * @param that
	 *            a non-null (but possibly empty) SeqSet
	 * @return {@code true} iff the set of sequences represented by this SeqSet
	 *         contains the set of sequences represented by {@code that}
	 */
	public boolean containsAll(SeqSet that) {
		if (that.isEmpty)
			return true;
		if (this.isEmpty)
			return false;

		Node thisNode = root, thatNode = that.root;

		if (thatNode.isLeaf() && !thisNode.isLeaf())
			return false;

		Stack<Iterator<Integer>> stack = new Stack<>();

		stack.push(thatNode.iterator());
		top : do {
			Iterator<Integer> iter = stack.peek();

			while (iter.hasNext()) {
				int idx = iter.next();

				if (thisNode.hasChild(idx)) {
					thisNode = thisNode.children[idx];
					thatNode = thatNode.children[idx];
					if (thatNode.isLeaf() && !thisNode.isLeaf())
						return false;
					stack.push(thatNode.iterator());
					continue top;
				} else if (!thisNode.isLeaf())
					return false;
			}
			// backtrack
			thisNode = thisNode.parent;
			thatNode = thatNode.parent;
			stack.pop();
		} while (!stack.isEmpty());
		return true;
	}

	/**
	 * Are this set and the given set disjoint?
	 * 
	 * @param that
	 *            a non-null SeqSet
	 * @return {@code true} iff the two sets are disjoint
	 */
	public boolean disjoint(SeqSet that) {
		if (isEmpty || that.isEmpty)
			return true;

		Node thisNode = root, thatNode = that.root;

		if (thatNode.isLeaf() || thisNode.isLeaf())
			return false;

		Stack<Iterator<Integer>> stack = new Stack<>();

		stack.push(thatNode.iterator());
		top : do {
			Iterator<Integer> iter = stack.peek();

			while (iter.hasNext()) {
				int idx = iter.next();

				if (thisNode.hasChild(idx)) {
					thisNode = thisNode.children[idx];
					thatNode = thatNode.children[idx];
					if (thatNode.isLeaf() || thisNode.isLeaf())
						return false;
					stack.push(thatNode.iterator());
					continue top;
				}
			}
			// backtrack
			thisNode = thisNode.parent;
			thatNode = thatNode.parent;
			stack.pop();
		} while (!stack.isEmpty());
		return true;
	}

	/**
	 * Do this set and the given set represent the same set of sequences?
	 * 
	 * @param obj
	 *            any object to be compared with this one
	 * @return {@code true} iff {@code obj} is a SeqSet representing the same
	 *         set as this SeqSet
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof SeqSet))
			return false;
		SeqSet that = (SeqSet) obj;

		if (isEmpty)
			return that.isEmpty;
		if (that.isEmpty)
			return false;

		Node thisNode = root, thatNode = that.root;

		if (!thisNode.childrenIndexes.equals(thatNode.childrenIndexes))
			return false;

		Stack<Iterator<Integer>> stack = new Stack<>();

		stack.push(thisNode.iterator());
		while (!stack.isEmpty()) {
			Iterator<Integer> iter = stack.peek();

			if (iter.hasNext()) {
				int index = iter.next();

				thisNode = thisNode.children[index];
				thatNode = thatNode.children[index];
				if (!thisNode.childrenIndexes.equals(thatNode.childrenIndexes))
					return false;
				stack.push(thisNode.iterator());
			} else {
				stack.pop();
				thisNode = thisNode.parent;
				thatNode = thatNode.parent;
			}
		}
		return true;
	}

	/**
	 * Makes this set empty.
	 */
	public void clear() {
		isEmpty = true;
		root.clear();
	}

	/**
	 * Makes this the full set (consisting of all tuples).
	 */
	public void makeFull() {
		clear();
		add();
	}

}

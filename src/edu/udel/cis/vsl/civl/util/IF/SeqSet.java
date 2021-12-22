package edu.udel.cis.vsl.civl.util.IF;

import java.util.Arrays;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;

/**
 * A SeqSet represents a set of sequences of integers. By sequence, we mean
 * finite sequence of integers. Only certain sets are representable. If p and q
 * are sequences, write p<=q if p is a prefix of q. (This includes the case
 * p=q.) If p is a sequence, let [p] = {q|p<=q}. A SeqSet represents a finite
 * union of sets of the form [p]. If p<=q then [p] contains [q], so any SeqSet
 * equals a SeqSet in which every element is minimal in the set. (Only keep
 * minimal elements.)
 * 
 * If () is the empty sequence then [()] consists of all sequences. It is
 * represented by the SeqSet {()}.
 * 
 * {(1,2),(1,3,4),(2)} is a SeqSet.
 * 
 * 
 * @author siegel
 *
 */
public class SeqSet {

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
		SortedSet<Integer> childrenIndexes; // would a BitSet be faster?

		/**
		 * The children nodes. If i is in childrenIndexes, then children[i] will
		 * be a non-null Node with index i and is considered "active".
		 * Otherwise, children[i] may or may not be null; if not null, the child
		 * is considered "inactive". An inactive child can be reactivated at
		 * some future point when this set changes. This is an optimization---it
		 * should be functionally equivalent but faster than setting children[i]
		 * to null and creating a new Node.
		 */
		Node[] children;

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
			childrenIndexes = new TreeSet<Integer>();
			children = new Node[2];
		}

		boolean isLeaf() {
			return childrenIndexes.isEmpty();
		}

		/**
		 * If this node already contains the child at index, does nothing and
		 * returns {@code false}. Otherwise, the node is created or reactivated
		 * and cleared, and the child index is added to this node's @{code
		 * childrenIndexes} set.
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

		void clear() {
			childrenIndexes.clear();
		}

		Iterator<Integer> iterator() {
			return childrenIndexes.iterator();
		}
	}

	/**
	 * Is this the empty set? If true, then the root is inactive.
	 */
	private boolean isEmpty = true;

	/**
	 * The root node of the tree. Not null, but may be inactive.
	 */
	private Node root = new Node(null, -1);

	/**
	 * Creates new empty set. The root node will be created but will be
	 * inactive.
	 */
	public SeqSet() {
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

	/*
	class LeafIterator implements Iterator<Node> {

		private Stack<Iterator<Integer>> stack = new Stack<>();

		private Node currentNode;

		private Iterator<Integer> currentIter;

		LeafIterator(Node root) {
			currentNode = root;
			if (currentNode == null)
				return;
			currentIter = currentNode.iterator();
			stack.push(currentIter);
			while (currentIter.hasNext()) {
				currentNode = currentNode.children[currentIter.next()];
				currentIter = currentNode.iterator();
				stack.push(currentIter);
			}
		}

		private void proceedToNext() {
			// backtrack from current leaf...
			do {
				stack.pop();
				currentNode = currentNode.parent;
				if (currentNode == null) {
					assert stack.isEmpty();
					currentIter = null;
					return;
				}
				currentIter = stack.peek();
			} while (!currentIter.hasNext());
			// now go down to next leaf...
			do {
				currentNode = currentNode.children[currentIter.next()];
				currentIter = currentNode.iterator();
				stack.push(currentIter);
			} while (currentIter.hasNext());
		}

		@Override
		public boolean hasNext() {
			return currentNode != null;
		}

		@Override
		public Node next() {
			Node result = currentNode;

			if (result == null)
				throw new NoSuchElementException();
			proceedToNext();
			return result;
		}

	}

	private Iterator<Node> leafIterator() {
		return new LeafIterator(root);
	}

	private Iterable<Node> leaves() {
		return new Iterable<Node>() {
			@Override
			public Iterator<Node> iterator() {
				return new LeafIterator(root);
			}
		};
	}
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
	 * 
	 * Perform DFS of that and walk along this in tandem with the search. Let u
	 * be the current node for this and v for that. If u is a leaf then
	 * backtrack. If v is leaf than prune the children of u and backtrack.
	 * 
	 * Otherwise, iterate over the edges of v. For an edge on int a, see if u
	 * has an edge on a. If u does have an edge on a, proceed to the target node
	 * in both graphs. If u does not have an edge on a, copy the branch starting
	 * with a from v to u.
	 * 
	 * @param that
	 * @return
	 */
	public boolean addAll(SeqSet that) {
		if (isEmpty) {
			if (that.isEmpty)
				return false;
			copy(null, -1, that.root);
			return true;
		}

		boolean change = false; // has there been a change to this?
		Stack<Iterator<Integer>> stack = new Stack<>(); // DFS stack for that
		Node thisNode = root, thatNode = that.root; // current nodes

		// invariant: stack specifies a path in that starting from root
		// for all nodes v in that path: neither v nor the corresponding node
		// u in this is a leaf.
		// invariant: thisNode and thatNode correspond and thatNode results
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
			// thisNode and thatNode correspond and are not null
			// neither is a leaf
			// the iterator at top of stack corresponds to thatNode
			// proceed to next new node pair...
			do {
				Iterator<Integer> thatIter = stack.peek();

				while (thatIter.hasNext()) {
					int idx = thatIter.next();

					if (thatNode.childrenIndexes.contains(idx)) { // new nodes!
						thisNode = thisNode.children[idx];
						thatNode = thatNode.children[idx];
						continue top;
					} else {
						copy(thisNode, idx, thatNode.children[idx]);
					}
				}
				stack.pop();
			} while (!stack.isEmpty());
			break; // no new node pair: search is complete
		}
		return change;
	}

	public boolean contains(int[] seq) {
		// TODO
		return false;
	}

	public boolean containsAll(SeqSet that) {
		// TODO
		return false;
	}

	public boolean disjoint(SeqSet that) {
		// TODO
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		return false;

	}

	public void clear() {
		isEmpty = true;
		root.clear();
	}

}

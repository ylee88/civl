package edu.udel.cis.vsl.civl.kripke.common;

import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import edu.udel.cis.vsl.civl.state.IF.UnsatisfiablePathConditionException;
import edu.udel.cis.vsl.civl.util.IF.SeqSet;

/**
 * <p>
 * An implementation of Tarjan's algorithm for computing the set of strongly
 * connected components of a directed graph, in the case where the directed
 * graph encodes the dependence relation on processes, used to find an ample set
 * at the current state, in partial order reduction. This class is owned and
 * used by a {@link SimpleEnablerWorker}, which has a reference to the current
 * state ({@code theState}).
 * </p>
 * 
 * <p>
 * There is a directed graph in which the nodes are processes and there is an
 * edge p->q if q can reach an object which is in p's depend set. The depend set
 * is a set S of objects in the current state with the property that any
 * statement in another process that does not access an object in S is
 * independent of all statements emanating from the current location of p.
 * </p>
 * 
 * <p>
 * We define what it means for a process q to reach an object o in the current
 * state using a different binary relation on the set of objects. There is an
 * edge from one object to another if the first contains a pointer into the
 * second. We start with all objects in all dynamic scopes on q's call stack and
 * all dynamic scopes that are ancestors (reachable under the parent relation)
 * of such dynamic scopes. The set of objects reachable from those initial
 * objects is the set of objects that q can reach.
 * </p>
 *
 * <p>
 * Returning to the dependence graph: since an ample set requires "nothing
 * dependent on an action in the ample set can occur until something in the
 * ample set occurs", if p is included in an ample set and there is an edge
 * p->q, then q must also be included. More specifically, an ample set is a set
 * of processes P such that:
 * 
 * <ol>
 * <li>there is at least one enabled transition in P</li>
 * <li>all enabled transitions in P are invisible to the property being
 * checked</li>
 * <li>P is closed under ->: if p is in P and p->q then q is in P</li>
 * <li>P does not contain a back-edge: [this is handled by GMC]</li>
 * </ol>
 * </p>
 * 
 * @author siegel
 */
public class StrongConnect {
	
	// Types...

	/**
	 * A node in the dependence graph. The nodes are the processes, and there is
	 * an edge p->q if p can reach an object on which q depends.
	 * 
	 * @author siegel
	 */
	class Node {

		/**
		 * The ID number of this process.
		 */
		int pid;

		/**
		 * Can this process not be used in a proper ample set because it or a
		 * process it can reach contains an enabled visible transition?
		 */
		boolean spoiled = false;

		/**
		 * Tarjan's "lowlink" field.
		 */
		int lowlink = -1;

		/**
		 * Tarjan's "index" field.
		 */
		int index = -1;

		/**
		 * The depend set: set of objects which have a dependency with this
		 * process in the current state.
		 */
		SeqSet depend = null;

		/**
		 * The reach set: the set of objects which this process can reach
		 * through its stack and pointer operations.
		 */
		SeqSet reach = null;

		/**
		 * Is this node currently on the Tarjan stack?
		 */
		boolean onstack;

		/**
		 * Constructs new node, initializing the {@link #pid} field as
		 * specified. Other fields will be initialized to {@code null} or
		 * {@code -1} indicating this node has not yet been initialized.
		 * 
		 * @param pid
		 *            ID of process represented by this node
		 */
		Node(int pid) {
			this.pid = pid;
		}

		/**
		 * Gets the depends set for this node. If the depend set was previously
		 * computed, the previous result is returned immediately, otherwise it
		 * is computed and cached.
		 * 
		 * @return the depends set for the process represented by this node
		 * @throws UnsatisfiablePathConditionException
		 *             if it is determined that the path condition in the
		 *             current state is unsatisfiable
		 */
		SeqSet getDependSet() throws UnsatisfiablePathConditionException {
			if (depend == null) {
				depend = worker.computeDepends(pid);
			}
			return depend;
		}

		SeqSet getReachSet() {
			if (reach == null) {
				reach = worker.computeReach(pid);
			}
			return reach;
		}

		@Override
		public String toString() {
			return "Node[" + pid + "]";
		}
	}

	class ChildIterator implements Iterator<Node> {

		private Node theNode;

		private boolean current = false;

		private int childPid = -1;

		public ChildIterator(Node theNode) {
			this.theNode = theNode;
		}

		@Override
		public boolean hasNext() {
			if (current)
				return childPid < nprocs;
			childPid++;
			while (childPid < nprocs) {
				try {
					SeqSet depends = theNode.getDependSet();
					Node child = getNode(childPid);
					SeqSet reach = child.getReachSet();

					if (!depends.disjoint(reach)) {
						current = true;
						return true;
					}
				} catch (UnsatisfiablePathConditionException e) {
					// don't do anything, there is no edge here
				}
				childPid++;
			}
			current = true;
			return false;
		}

		@Override
		public Node next() {
			if (!hasNext())
				throw new NoSuchElementException();
			current = false;
			return getNode(childPid);
		}
	}
	
	// Fields ...

	private Node[] nodes; // length nprocs

	private static int verbose = 0;

	private PrintStream out = System.out;

	/**
	 * The worker responsible for computing an ample set for a state, providing
	 * methods to compute the depends and reach sets for a process.
	 */
	private SimpleEnablerWorker worker;

	private int nprocs;

	/**
	 * Tarjan's stack.
	 */
	private Deque<Node> tstack = new ArrayDeque<>();

	/**
	 * Used by Tarjan's algorithm to assign a new ID number to each node.
	 */
	private int max_num = 0;

	public StrongConnect(SimpleEnablerWorker worker) {
		this.worker = worker;
		this.nprocs = worker.theState.numProcs(); // some are null
		this.nodes = new Node[nprocs];
	}

	private Node getNode(int pid) {
		if (nodes[pid] == null) {
			nodes[pid] = new Node(pid);
		}
		return nodes[pid];
	}

	/**
	 * Sets the spoiled bit on all elements of the stack.
	 * 
	 * <p>
	 * Precondition: if an element of the stack is spoiled, then all earlier
	 * (lower) elements on the stack are spoiled. Therefore this algorithm can
	 * start at the top of the stack and proceed until encountering the first
	 * spoiled entry, then stop.
	 * </p>
	 */
	private void spoil_tstack() {
		for (Node v : tstack) { // iterates from top of stack
			if (v.spoiled)
				return;
			v.spoiled = true;
		}
	}

	private LinkedList<Integer> strong_connect(Node v)
			throws UnsatisfiablePathConditionException {
		v.lowlink = v.index = max_num++;
		if (!worker.allInvisible(v.pid)) {
			v.spoiled = true;
			spoil_tstack();
		}
		tstack.push(v);
		v.onstack = true;
		if (verbose >= 3)
			out.println(v);

		Iterator<Node> iter = new ChildIterator(v);

		while (iter.hasNext()) {
			Node w = iter.next();

			if (w.index == -1) {
				LinkedList<Integer> result = strong_connect(w);

				if (result != null)
					return result;
				if (w.lowlink < v.lowlink)
					v.lowlink = w.lowlink;
			} else if (w.onstack) {
				if (w.index < v.lowlink)
					v.lowlink = w.index;
			} else {
				if (w.spoiled)
					spoil_tstack();
			}
		}
		if (v.lowlink == v.index) { // an SCC has been found
			Node w;

			if (v.spoiled) { // this SCC is spoiled (contains visible)
				if (verbose >= 3)
					out.print("SCC (spoiled): ");
				do {
					w = tstack.pop();
					w.onstack = false;
					if (verbose >= 3)
						out.print(w.pid + " ");
				} while (w != v);
				if (verbose >= 3)
					out.println("}");
			} else { // not spoiled
				int numEnabled = 0;
				LinkedList<Integer> result = new LinkedList<>();

				// a possible ample candidate
				// this may not be a bottom SCC, but all SCCs this
				// one can reach have 0 enabled transitions
				if (verbose >= 3)
					out.print("SCC (candidate): {");
				do {
					w = tstack.pop();
					numEnabled += worker
							.enabledTransitionsInProcess(w.pid).length;
					w.onstack = false;
					result.add(w.pid);
					if (verbose >= 3)
						out.print(w.pid + " ");
				} while (w != v);
				if (numEnabled > 0)
					return result;
			}
		}
		return null;
	}

	public LinkedList<Integer> findAmple()
			throws UnsatisfiablePathConditionException {
		for (int i = 0; i < nprocs; i++) {
			Node node = getNode(i);

			if (node.index == -1) {
				LinkedList<Integer> result = strong_connect(node);

				if (result != null)
					return result;
			}
		}
		return null;
	}
}

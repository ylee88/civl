package edu.udel.cis.vsl.civl.kripke.common;

import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import edu.udel.cis.vsl.civl.state.IF.ProcessState;
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
 * Fix the state s and process p. There are two sets of objects residing in s
 * that are associated to p: p.depend and p.dependWrite, where p.dependWrite is
 * a subset of p.depend. These satisfy the following. If E is any execution from
 * s in which every transition t satisfies all of the following:
 * <ol>
 * <li>t does not belong to p</li>
 * <li>t does not write or delete an object in p.depend</li>
 * <li>t does not read an object in p.dependWrite</li>
 * <li>t does not disable an enabled transition in p</li>
 * <li>t does not enable a disabled transition in p</li>
 * </ol>
 * then every transition in E commutes with every enabled transition in p. One
 * way to define these sets is to let p.reach consist of all objects that are
 * accessed (read, modify, delete) by the guards (including the disabled ones)
 * and enabled statements of p, and p.reachWrite consist of the accesses in the
 * enabled statements which are modify or delete. For system functions this
 * information can be taken from the contract. In general, it is always "safe"
 * to over-approximate these sets. It is also possible to let p.depend =
 * p.dependWrite.
 * </p>
 * 
 * <p>
 * There are two additional sets of objects residing in s associated to p:
 * p.reach and p.reachWrite, where p.reachWrite is a subset of p.reach. These
 * satisfy the following. If E is any execution from s then no object outside of
 * p.reach will be modified or deleted by a transition in E, and no object
 * outside of p.reachWrite will be read, modified, or deleted. A way to compute
 * p.reach is to start with all variables on p's call stack, and consider the
 * relation on objects o1->o2 if o1 contains a pointer into o2. p.reach consists
 * of all objects reachable from p's call stack under this relation. Any
 * variable which has its address taken or which occurs on the left side of an
 * assignment at some program location reachable from a location on the call
 * stack are included in reachWrite.
 * </p>
 * 
 * <p>
 * Now consider the directed graph in which the nodes are processes and there is
 * an edge p->q if p!=q, and q.reachWrite intersects p.depend or q.reach
 * intersects p.dependWrite. An ample set is either the full set of enabled
 * transitions at s, or the set of enabled transitions in a subset P of the
 * processes satisfying:
 * <ol>
 * <li>there is at least one enabled transition in P</li>
 * <li>all enabled transitions in P are invisible to the property being checked
 * (some form of deadlock)</li>
 * <li>P is closed under ->: if p is in P and p->q then q is in P</li>
 * <li>P does not contain a back-edge: [this is handled by GMC]</li>
 * </ol>
 * </p>
 * 
 * <p>
 * This class attempts to find a minimal ample set P. It uses a variant of
 * Tarjan's SCC algorithm. That algorithm finds the SCCs from the "bottom up",
 * i.e., the first SCC it produces will be a "bottom SCC", one which has no
 * outgoing edges to another SCC. When it produces an SCC C, it will have
 * already produced all SCCs C' reachable from C. We declare a node to be
 * spoiled if it can reach a node with a visible enabled transition. Such a node
 * can never be included in a (proper) ample set. This algorithm rejects spoiled
 * SCCs and SCCs with no enabled transition. It returns the first SCC C it finds
 * which is not spoiled and which contains at least one enabled transition.
 * Since C is not spoiled, none of the SCCs C' which C can reach are spoiled;
 * therefore all such C' must have 0 enabled transitions. Hence the enabled
 * transitions of C form an ample set, since they are also the enabled
 * transitions of all nodes reachable from C.
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
		 * Has this process terminated?
		 */
		boolean terminated;

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
		 * A subset of {@link #depend} which over-approximates the set of
		 * variables that can be modified by some action from the current state.
		 * Any variable not in this set is only read.
		 */
		SeqSet dependWrite = null;

		/**
		 * The reach set: the set of objects which this process can reach
		 * through its stack using pointer operations.
		 */
		SeqSet reach = null;

		/**
		 * A subset of {@link #reach} which over-approximates the set of
		 * variables in {@link #reach} that can be modified at some point in the
		 * future. Any variable not in this set will only be read on any path
		 * starting from the current state.
		 */
		SeqSet reachWrite = null;

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
			ProcessState ps = worker.theState.getProcessState(pid);

			this.pid = pid;
			this.terminated = ps == null || ps.getLocation() == null;
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
				depend = new SeqSet();
				dependWrite = new SeqSet();
				worker.computeDepends(pid, depend, dependWrite);
			}
			return depend;
		}

		/**
		 * Gets the dependWrite set for this node, using either the cached
		 * result or computing it.
		 * 
		 * @return the dependWrite set
		 * @throws UnsatisfiablePathConditionException
		 */
		SeqSet getDependWriteSet() throws UnsatisfiablePathConditionException {
			if (dependWrite == null) {
				depend = new SeqSet();
				dependWrite = new SeqSet();
				worker.computeDepends(pid, depend, dependWrite);
			}
			return dependWrite;
		}

		/**
		 * Gets the reach set for this node, either from the cache or by
		 * computing it.
		 * 
		 * @return the reach set
		 */
		SeqSet getReachSet() {
			if (reach == null) {
				reach = new SeqSet();
				reachWrite = new SeqSet();
				worker.computeReach(pid, reach, reachWrite);
			}
			return reach;
		}

		/**
		 * Gets the reachWrite set for this node, either from cache of by
		 * computing it.
		 * 
		 * @return the reachWrite set
		 */
		SeqSet getReachWriteSet() {
			if (reachWrite == null) {
				reach = new SeqSet();
				reachWrite = new SeqSet();
				worker.computeReach(pid, reach, reachWrite);
			}
			return reachWrite;
		}

		@Override
		public String toString() {
			return "Node[" + pid + "]";
		}
	}

	/**
	 * An iterator over all nodes q such that this->q, where p->q iff
	 * p.dependWrite intersects q.reach or p.depend intersects q.reachWrite.
	 * 
	 * @author siegel
	 */
	class ChildIterator implements Iterator<Node> {

		/**
		 * The node p whose children we are iterating over.
		 */
		private Node theNode;

		/**
		 * Is the current value of {@link #childPid} the pid of the next node to
		 * be returned by this iterator?
		 */
		private boolean current = false;

		/**
		 * The process ID of the next child node to be returned.
		 */
		private int childPid = -1;

		/**
		 * Creates a new iterator for which the parent node p is given.
		 * 
		 * @param theNode
		 *            the parent node p
		 */
		public ChildIterator(Node theNode) {
			this.theNode = theNode;
		}

		@Override
		public boolean hasNext() {
			if (current)
				return childPid < nprocs;
			for (childPid++; childPid < nprocs; childPid++) {
				// this binary relation is irreflexive:
				if (childPid == theNode.pid)
					continue;
				Node child = getNode(childPid);

				if (!child.terminated) {
					try {
						if (!theNode.getDependSet()
								.disjoint(child.getReachWriteSet())
								|| !theNode.getDependWriteSet()
										.disjoint(child.getReachSet())) {
							current = true;
							return true;
						}
					} catch (UnsatisfiablePathConditionException e) {
						// don't do anything, there is no edge here
					}
				}
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

	/**
	 * The nodes, corresponding to processes. This will be an array of length
	 * {@link #nprocs}, where n is the number of processes in the state,
	 * {@link SimpleEnablerWorker#theState}.
	 */
	private Node[] nodes;

	/**
	 * How much debugging information should we print? 0=nothing, higher=more.
	 */
	private static int verbose = 0;

	/**
	 * Where the debugging information goes.
	 */
	private PrintStream out = System.out;

	/**
	 * The worker responsible for computing an ample set for a state, providing
	 * methods to compute the depends and reach sets for a process.
	 */
	private SimpleEnablerWorker worker;

	/**
	 * The number of processes in the state.
	 */
	private int nprocs;

	/**
	 * Tarjan's stack. Note from Java SDK: "Deques can also be used as LIFO
	 * (Last-In-First-Out) stacks. This interface should be used in preference
	 * to the legacy Stack class. When a deque is used as a stack, elements are
	 * pushed and popped from the beginning of the deque."
	 */
	private Deque<Node> tstack = new ArrayDeque<>();

	/**
	 * Used by Tarjan's algorithm to assign a new ID number to each node.
	 */
	private int max_num = 0;

	/**
	 * Construct a new instance ready to perform the Tarjan SCC algorithm.
	 * 
	 * @param worker
	 *            the worker that is creating this {@link StrongConnect} and
	 *            will be used to compute the depends and reach sets
	 */
	public StrongConnect(SimpleEnablerWorker worker) {
		this.worker = worker;
		this.nprocs = worker.theState.numProcs(); // some are null
		this.nodes = new Node[nprocs];
	}

	/**
	 * Get node for process with ID {@code pid}. If the node doesn't yet exist,
	 * create it.
	 * 
	 * @param pid
	 *            ID of the process for the requested node
	 * @return the {@link Node} for the requested process
	 */
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
		int vid = v.pid;

		v.lowlink = v.index = max_num++;
		if (worker.enabledTransitionsInProcess(vid).length > 0
				&& (!worker.allInvisible(vid) || worker.unsafeAtomic(vid))) {
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

	/**
	 * Attempts to find an ample set by performing Tarjan's algorithm from every
	 * node until a qualifying set is found. If none is found, returns
	 * {@code null}
	 * 
	 * @return the list of PIDs of the processes comprising the ample set, or
	 *         {@code null}; strictly speaking, this list may omit processes
	 *         that have 0 enabled transitions
	 * @throws UnsatisfiablePathConditionException
	 *             if it is discovered that the path condition of
	 *             {@link SimpleEnablerWorker#theState} is unsatisfiable
	 */
	public LinkedList<Integer> findAmple()
			throws UnsatisfiablePathConditionException {
		for (int i = 0; i < nprocs; i++) {
			Node node = getNode(i);

			if (!node.terminated && node.index == -1) {
				LinkedList<Integer> result = strong_connect(node);

				if (result != null)
					return result;
			}
		}
		return null;
	}

	/**
	 * Prints data associated with this computation. This method should be
	 * called after the ample set has been found (or failed to be found).
	 * 
	 * @param out
	 *            where the output should go
	 */
	public void printData(PrintStream out) {
		for (int i = 0; i < nprocs; i++) {
			Node node = nodes[i];

			if (node != null && !node.terminated && node.index != -1) {
				out.print("  p" + i + ": ");
				if (worker.enabledTransitions[i] != null
						&& worker.enabledTransitions[i].length == 0) {
					out.print("(not enabled)  ");
				}
				out.println();
				if (node.depend != null) {
					out.print("    depend = { ");
					worker.printObjSet(out, node.depend);
					out.println(" }");
				}
				if (node.dependWrite != null) {
					out.print("    dependWrite = { ");
					worker.printObjSet(out, node.dependWrite);
					out.println(" }");
				}
				if (node.reach != null) {
					out.print("    reach = { ");
					worker.printObjSet(out, node.reach);
					out.println(" }");
				}
				if (node.reachWrite != null) {
					out.print("    reachWrite = { ");
					worker.printObjSet(out, node.reachWrite);
					out.println(" }");
				}
				out.print("    successors: ");
				Iterator<Node> childIter = new ChildIterator(node);
				if (childIter.hasNext()) {
					boolean first = true;

					while (childIter.hasNext()) {
						if (first)
							first = false;
						else
							out.print(", ");
						out.print("p" + childIter.next().pid);
					}
				} else {
					out.print("none");
				}
				out.println();
			}
		}
	}
}

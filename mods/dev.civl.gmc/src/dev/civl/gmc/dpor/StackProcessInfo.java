package dev.civl.gmc.dpor;

/**
 * Holds process-specific DPOR information which is associated to some "local
 * state" of the process. Here "local state" means the part of the state which
 * can only be affected by transitions the process itself takes like its set of
 * "next" transitions.
 * 
 * The general framework is that the {@link DporSearchStack} maintains a map
 * from each available process in the program to an instance of this class and
 * each {@link DporStackEntry} has one instance of this class.
 * 
 * When a transition gets pushed on the stack, the stack info of that
 * transition's proc gets moved from the stack's map to the
 * {@link DporStackEntry} with that transition and a fresh
 * {@link StackProcessInfo} is created for that proc on the stack.
 * 
 * When a transition gets popped from the stack, the inverse happens. The
 * {@link StackProcessInfo} of that {@link DporStackEntry} (which will now
 * be the head after the pop) is moved to the stack's map, replacing the one
 * previously held by the process of the transition just popped.
 * 
 * The reason for this is that pushing and popping transitions changes the
 * "local state" of a process and thus requires either a fresh object (in the
 * case of a push) or to recall the last one for the process (in the case of a
 * pop).
 * 
 * @author awilton
 *
 */
public class StackProcessInfo {
	StackProcessInfo() {}
	
	public int lastEntry = -1;
	
	public DporHbSet hbSet = new DporHbSet();
}

package dev.civl.gmc.dpor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Maintains a set of stack entries which are downward closed wrt to the
 * happens-before relation. Meaning: if e is in our set and e' happens
 * before e then e' is also in our set.
 * 
 * This class is the foundation for building the full happens-before
 * relation. Essentially, each entry on the stack has an HbSet which represents the set
 * of entries that happen-before it.
 *
 */
public class DporHbSet {
	// Maps a process id to the largest entry on the stack that is in this set
	private Map<Integer, Integer> entryMap = new HashMap<>();
	
	public DporHbSet() {}
	
	/*
	 * Add a new entry to the HbSet and all entries that happen before it.
	 */
	public void addEntry(DporStackEntry<?,?> entry) {
		int entryPos = entry.getStackPosition();
		for (Map.Entry<Integer, Integer> hbEdge : entry.getHbSet().entryMap.entrySet()) {
			int edgePos = hbEdge.getValue();
			if (edgePos >= entryMap.getOrDefault(hbEdge.getKey(), edgePos)) {
				entryMap.put(hbEdge.getKey(), edgePos);
			}
		}
		entryMap.put(entry.getPid(), entryPos);
	}
	
	/**
	 * @param index of stack entry
	 * @return whether the entry is in this set
	 */
	public boolean contains(DporStackEntry<?,?> entry) {
		return entry.getStackPosition() <= entryMap.getOrDefault(entry.getPid(), -1);
	}
	
	/**
	 * @param process id
	 * @return the last stack entry of pid that is in this set if it exists; -1 if it does not
	 */
	public int lastEntryPos(int pid) {
		return entryMap.getOrDefault(pid, -1);
	}
	
	/**
	 * @return the set of process ids that have stack entries in this set
	 */
	public Set<Integer> procSet() {
		return entryMap.keySet();
	}
}
package dev.civl.abc.analysis.entity;

import java.util.HashMap;
import java.util.Set;

import dev.civl.abc.token.IF.SyntaxException;

public class FocusAnalysisData {
	public class TagData {
		public TagData(String focusTag) {
			focusVarName = "$focus_"+focusTag;
			altFocusVarName = "$_focus_"+focusTag;
		}
		String focusVarName;
		String altFocusVarName;
	}
	private HashMap<String, TagData> tagMap;

	public FocusAnalysisData() {
		tagMap = new HashMap<String, TagData>();
	}

	/**
	 * Adds a new tag entry into the tagMap if not previously there. If one did
	 * not already exist then an insert transformer is added at the top of the
	 * AST to insert the tag's corresponding focus variables.
	 * 
	 * @param tag
	 *            The tag that this new data pertains to.
	 */
	public void addFocusTag(String tag)
			throws SyntaxException {
		if (!tagMap.containsKey(tag)) {
			TagData newData = new TagData(tag);
			tagMap.put(tag, newData);
		}
	}


	private TagData tagData(String tag) {
		return tagMap.get(tag);
	}

	public String getVarNameFromTag(String tag) {
		return tagData(tag).focusVarName;
	}
	
	public String getAltVarNameFromTag(String tag) {
		return tagData(tag).altFocusVarName;
	}

	public Set<String> getFocusTags() {
		return tagMap.keySet();
	}
}

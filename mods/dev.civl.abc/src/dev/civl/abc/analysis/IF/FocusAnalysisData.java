package dev.civl.abc.analysis.IF;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import dev.civl.abc.ast.node.IF.expression.ExpressionNode;
import dev.civl.abc.token.IF.SyntaxException;

public class FocusAnalysisData {
	public class TagData {
		public TagData(String focusTag) {
			focusVarName = "$focus_"+focusTag;
			altFocusVarName = "$_focus_"+focusTag;
		}
		String focusVarName;
		String altFocusVarName;
		SortedSet<Integer> offsets = new TreeSet<>();
		List<ExpressionNode> protectedMemExprs = new ArrayList<>();
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

	public void addFocusOffset(String tag, int offset) {
		TagData data = tagData(tag);
		data.offsets.add(offset);
	}
	
	public SortedSet<Integer> getFocusOffsets(String tag) {
		return tagData(tag).offsets;
	}

	public void addProtectedMemExpr(String tag, ExpressionNode expr) {
		tagData(tag).protectedMemExprs.add(expr);
	}

	public List<ExpressionNode> getProtectedMemExprs(String tag) {
		return tagData(tag).protectedMemExprs;
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

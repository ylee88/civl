package dev.civl.mc.slice.common;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dev.civl.mc.model.IF.location.Location;
import dev.civl.sarl.IF.expr.BooleanExpression;

public class ControlDependencyElement {
	
	List<ErrorCfaLoc> branchPoints;
	CfaLoc mergePoint;
	int context;
	
	public ControlDependencyElement (ErrorCfaLoc bp, 
			CfaLoc mp, int callingContext) {
		
		this.branchPoints = new ArrayList<>();
		this.branchPoints.add(bp);
		this.mergePoint = mp;
		this.context = callingContext;
		
	}
	
	public String toString() {
		
		List<BooleanExpression> guardExpressions = new ArrayList<>();
		List<String> lineNumbers = new ArrayList<>();
		
		for (ErrorCfaLoc b : branchPoints) {
			Location l = b.getCIVLLocation();
			guardExpressions.add(b.branchConstraint);
			lineNumbers.add(getSourceLine(l.getSource().toString()));
		}

		return "  Branch(es): "+guardExpressions+"; Merge: "+mergePoint+
				"; Context: "+context+"; Lines: "+lineNumbers;
	}
	
	
	private String getSourceLine(String locationString) {
		
		Pattern p = Pattern.compile(":(\\d+)\\.");
		Matcher m = p.matcher(locationString);
		String line = "";
		if (m.find()) line = m.group(1);
		assert !line.isEmpty();
		
		return line;
	}

}

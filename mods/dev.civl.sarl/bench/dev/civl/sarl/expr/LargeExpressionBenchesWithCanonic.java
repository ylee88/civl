package dev.civl.sarl.expr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import dev.civl.sarl.IF.SymbolicUniverse;
import dev.civl.sarl.IF.expr.BooleanExpression;
import dev.civl.sarl.IF.type.SymbolicType;
import dev.civl.sarl.object.IF.ObjectFactory;
import dev.civl.sarl.preuniverse.IF.FactorySystem;
import dev.civl.sarl.preuniverse.IF.PreUniverses;
import dev.civl.sarl.universe.IF.Universes;

/**
 * A Bench for large or Expressions using Canonic and expressions
 * @author schivi
 *
 */
public class LargeExpressionBenchesWithCanonic {

	public static void main(String[] args) {
		SymbolicUniverse sUniverse;
		SymbolicType booleanType;
		long start;
		long end;
		long mark;
		int numexpr;
		ObjectFactory of;
		
		FactorySystem system = PreUniverses.newIdealFactorySystem();
		of = system.objectFactory();
		Collection<BooleanExpression> col1;
		Collection<BooleanExpression> col2;
		numexpr = 1000;
		sUniverse = Universes.newIdealUniverse();
		booleanType = sUniverse.booleanType();
		BooleanExpression[] ExpressionList1 = {};
		col1= new ArrayList<BooleanExpression>(Arrays.asList(ExpressionList1));
		BooleanExpression[] ExpressionList2 = {};
		col2= new ArrayList<BooleanExpression>(Arrays.asList(ExpressionList2));
		for(int i = 0; i < numexpr; i++){
			col1.add(((BooleanExpression) sUniverse.symbolicConstant(sUniverse.stringObject(Integer.toString(i)), booleanType)));
		}
		BooleanExpression s1 = of.canonic(sUniverse.and(col1));
		BooleanExpression s2 = of.canonic(sUniverse.and(col2));
		start = System.currentTimeMillis();
			@SuppressWarnings("unused")
			BooleanExpression s3 = sUniverse.or(s1,s2);
		end = System.currentTimeMillis();
		mark = end - start;
		System.out.println(mark);
			}
}

package edu.buffalo.cse562.physicalPlan;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

import edu.buffalo.cse562.logger.logManager;
import edu.buffalo.cse562.sql.expression.evaluator.CalcTools;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.schema.Column;

public class SelectionOperator implements Operator {
	
	Operator input;
	Expression condition;
	String oneLineFromDat;
	Boolean isTupleMapPresent;
	logManager lg;
	
	public SelectionOperator(Operator input, Expression condition) {
		this.input = input;
		this.condition = condition;
		this.isTupleMapPresent = true;
		lg = new logManager();
	}
	
	@Override
	public void resetStream() {
		input.resetStream();
	}

	@Override
	public Datum[] readOneTuple() {

		//System.out.println("Select operator readOneTuple method called");

		Datum[] t = null;
		do {
			//System.out.println("Doing now");
			t = input.readOneTuple();
			if(t == null) {
				return null;
			}
			if(!evaluate(t,condition)) {
				//System.out.println("Condition not Satisfied");
				t = null;
			} //else {
				//System.out.println("Condition satisfied");
			//}
		} while(t==null);
		//System.out.println("Came here 2");

		//System.out.println("///////////return tuple length"+t.length);

		return t;
		
	}
	
	private boolean evaluate(Datum[] t, Expression condition2) {

		if(isTupleMapPresent) {
			TupleStruct.setTupleTableMap(t);
			isTupleMapPresent = false;
		}
		CalcTools calc = new CalcTools(t);
//		lg.logger.log(Level.INFO, "OYOYOYOYOYOYOYOYO");
		condition2.accept(calc);
		return calc.getAccumulatorBoolean();
	}

}

package edu.buffalo.cse562.physicalPlan;

import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.OrderByElement;
import edu.buffalo.cse562.sql.expression.evaluator.CalcTools;

public class Mysorter implements Comparator<Datum[]> {

	List<OrderByElement> ordEle;
	Boolean isTupleMapPresent;

	public Mysorter(List<OrderByElement> elements) {
		this.ordEle = elements;
		isTupleMapPresent = true;
	}

	@Override
	public int compare(Datum[] t1, Datum[] t2) {

		Iterator iter = ordEle.iterator();
		CalcTools calc1 = null, calc2 = null;
		int comparison = -2;
		while (iter.hasNext()) {

			OrderByElement ele = (OrderByElement) iter.next();
			Expression exe = ele.getExpression();
			if(isTupleMapPresent) {
				TupleStruct.setTupleTableMap(t1);
				isTupleMapPresent = false;
			}
			calc1 = new CalcTools(t1);
			exe.accept(calc1);
			calc2 = new CalcTools(t2);
			exe.accept(calc2);
			
			comparison = getCompareValue(calc1.getResult(), calc2.getResult(),
					ele.isAsc());
			
			if (comparison != 0) {
				//System.out.println("In :: " + calc1.getResult() + " : " + calc2.getResult() + " : " + comparison);
				return comparison;
			}
		}
		//System.out.println("Out :: " + calc1.getResult() + " : " + calc2.getResult() + " : " + comparison);
		return comparison;
	}

	public int getCompareValue(Object t1, Object t2, boolean asc) {
		if (t1 instanceof Long) {
			Long value1 = (Long)t1;
			Long value2 = (Long)t2;
			int comp = value1.compareTo(value2);
			//System.out.println(comp);
			if(comp == 0) {
				return comp;
			} else if(asc) {
				return comp;
			} else {
				if(comp == -1)
					return 1;
				else 
					return -1;
			}

		} else if (t1 instanceof String) {
			String value1 = (String)t1;
			String value2 = (String)t2;
			int comp = value1.compareTo(value2);
			if(comp == 0) {
				return comp;
			} else if(asc) {
				return comp;
			} else {
				if(comp == -1)
					return 1;
				else 
					return -1;
			}

		}  else if (t1 instanceof Date) {
			Date value1 = (Date)t1;
			Date value2 = (Date)t2;
			int comp = value1.compareTo(value2);
			if(comp == 0) {
				return comp;
			} else if(asc) {
				return comp;
			} else {
				if(comp == -1)
					return 1;
				else 
					return -1;
			}
		} else {
			return -2;
		}
		
		
	}
}


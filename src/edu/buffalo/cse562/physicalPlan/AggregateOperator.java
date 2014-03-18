package edu.buffalo.cse562.physicalPlan;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.buffalo.cse562.physicalPlan.Datum.dDate;
import edu.buffalo.cse562.physicalPlan.Datum.dDecimal;
import edu.buffalo.cse562.physicalPlan.Datum.dLong;
import edu.buffalo.cse562.physicalPlan.Datum.dString;
import edu.buffalo.cse562.sql.expression.evaluator.CalcTools;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;

public class AggregateOperator {
	Operator oper;
	ArrayList<SelectExpressionItem> selectExpressionList;

	Map<String, Datum[]> groupByMap = new HashMap<String, Datum[]>();
	boolean isTupleMapPresent;
	boolean isMasterFirst;
	Datum[] masterDatum;
	Integer masterCount;
	ArrayList<Integer> avgIndex; 
	boolean isAvgFirst;

	public AggregateOperator(Operator oper,
			ArrayList<SelectExpressionItem> selectExpressionList) {
		this.oper = oper;
		this.isMasterFirst = true;
		this.isTupleMapPresent = true;
		this.selectExpressionList = selectExpressionList;
		this.masterCount = 0;
		this.avgIndex = new ArrayList<Integer>();
		this.isAvgFirst = true;
	}

	public void resetStream() {
		// TODO Auto-generated method stub

	}

	/*
	 * read one tuple from the operator, iterate over the column
	 */
	public ArrayList<Datum[]> readOneTuple() {

		ArrayList<Datum[]> finalGroupByDatumArrayList = new ArrayList<Datum[]>();
		Datum[] readOneTupleFromOper = oper.readOneTuple();
		// printTuple(readOneTupleFromOper);
		Datum singleDatum;

		if(readOneTupleFromOper == null) {
			return null;
		}
		
//		if (isTupleMapPresent) {
//			TupleStruct.setTupleTableMap(readOneTupleFromOper);
//			isTupleMapPresent = false;
//		}

		int count = 0;
		ArrayList<String> datumColumnName = (ArrayList<String>) TupleStruct
				.getTupleTableMap();
		this.masterDatum = new Datum[selectExpressionList.size()];
		while (readOneTupleFromOper != null) {
			// System.out.println("NEW TUPLE READ");
			if (isTupleMapPresent) {
				TupleStruct.setTupleTableMap(readOneTupleFromOper);
				if(!TupleStruct.isNestedCondition())
					isTupleMapPresent = false;
			}

			// Building the datum[] from select item expressions
			Datum[] newSelectItemsArray = new Datum[selectExpressionList.size()];
			Map<Integer, String> fnMap = new HashMap<Integer, String>();
			for (int itr = 0; itr < selectExpressionList.size(); itr++) {
				// System.out.println("EXPRESSION"+countExpression);
				SelectExpressionItem newItem = selectExpressionList.get(itr);
				Expression e = newItem.getExpression();

				CalcTools calc = new CalcTools(readOneTupleFromOper);

				if (e instanceof Function) {
					// System.out.println("PRINT THERE IS A FUNCTION IN THE SELECT BODY");
					Function aggregateFunction = (Function) e;
					// aggregareFunctionList.add(aggregateFunction);
					String funcName = aggregateFunction.getName();
					fnMap.put(itr, funcName);
				} else {
					fnMap.put(itr, "col");
				}
				e.accept(calc);
				Datum tempDatum = getDatum(calc, newItem);
				newSelectItemsArray[itr] = tempDatum;
			}

			
			if (isMasterFirst) {
				masterDatum = newSelectItemsArray;
				++masterCount;
				isMasterFirst = false;
			} else {

				for (int i = 0; i < masterDatum.length; i++) {
					String funcName = fnMap.get(i);
					if(funcName.toLowerCase().equalsIgnoreCase("avg") && isAvgFirst)
						avgIndex.add(i);
					masterDatum[i] = getDatumFun(funcName,
							newSelectItemsArray[i], masterDatum[i]);
//					System.out.println(newSelectItemsArray[i] + " :: "
//							+ masterDatum[i] + " :: " + funcName);
				}
				isAvgFirst = false;
			}
			readOneTupleFromOper = this.oper.readOneTuple();
			
		}
		
		//System.out.println(avgIndex);
		for(Integer i : avgIndex) {
//			System.out.println(i);
			masterDatum[i] = avg(masterDatum[i], new Datum.dDecimal(masterCount,null)); 
		}
		
		finalGroupByDatumArrayList.add(masterDatum);
		return finalGroupByDatumArrayList;
	}

	private Datum getDatumFun(String funcName, Datum t1, Datum t2) {
		switch (funcName.toLowerCase()) {
		case "col":
			return t1;
		case "sum":
			// System.out.println("AGGREGATE FUNC - SUM");
			return sum(t1, t2);
		case "count":
			// System.out.println("AGGREGATE FUNC - COUNT method");
			return count();
		case "min":
			// System.out.println("MIN method");
			return min(t1, t2);
		case "max":
			// System.out.println("MAX method");
			return max(t1, t2);
		case "avg":
			return sum(t1, t2);
		case "stdev":
			System.out.println("STDEV method... Not handled");
			return null;
		default:
			System.out.println("AGGREGATE FUNCTION NOT MATCHED" + funcName);
			return null;
		}
	}

	private Datum count() {
		return new Datum.dLong(String.valueOf(++masterCount), null);
	}

	public void printTestMap(Map groupMap) {
//		System.out.println("SIZE OF THE MAP" + groupByMap.size());
		for (Entry<String, Datum[]> entry : groupByMap.entrySet()) {
			System.out.println("Key = " + entry.getKey() + ", Value = "
					+ entry.getValue() + "value size: "
					+ entry.getValue().length);
		}
	}

	private Datum getDatum(CalcTools calc, SelectExpressionItem newItem) {
		Column newCol = null;
		Object calcOut = calc.getResult();
		if (newItem.getAlias() != null) {
			// System.out.println("Alias");
			newCol = new Column(null, newItem.getAlias());
		} else {
			// System.out.println("Exp");
			newCol = calc.getColumn();
			// System.out.println(newCol.getColumnName());
		}
		Datum tempDatum = null;
		if (calcOut instanceof Long) {
			String value = calcOut.toString();
			// String valueWithFuncName = funcName.concat("("+value+")");
			tempDatum = new Datum.dLong(calcOut.toString(), newCol);
			// System.out.println("CALC OUTPUT FOR dLONG: "+calcOut.toString());
			// System.out.println("CALC OUTPUT FOR dLONG: "+newCol);
			// System.out.println("NEW FUNC VALUE: "+valueWithFuncName);
			// System.out.println("tempDatum: "+tempDatum.getStringValue());
		} else if (calcOut instanceof String) {
			tempDatum = new Datum.dString((String) calcOut, newCol);
			// System.out.println("CALC OUTPUT FOR DSTRING: "+(String) calcOut);
			// System.out.println("tempDatum"+tempDatum.getStringValue());
		} else if (calcOut instanceof Date) {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			tempDatum = new Datum.dDate(df.format(calcOut), newCol);
			// System.out.println("tempDatum"+tempDatum.getStringValue());

		} else if (calcOut instanceof Double) {
			Double value = (Double) calcOut;
			tempDatum = new Datum.dDecimal(calcOut.toString(), newCol);
		}
		return tempDatum;
	}

	private void printTuple(Datum[] row) {
		if (row != null && row.length != 0) {
			for (Datum col : row) {
				System.out.print(col + "|");
			}
			System.out.println("");
		}
//		System.out.println("------------------------------------------------");
	}

	public Datum sum(Datum t1, Datum t2) {
		if (t1 instanceof dLong) {
			long value1 = ((dLong) t1).getValue();
			long value2 = ((dLong) t2).getValue();
			return new Datum.dLong(String.valueOf(value1 + value2),
					t1.getColumn());
		} else if (t1 instanceof dString) {
			String value1 = ((dString) t1).getValue();
			String value2 = ((dString) t2).getValue();
			return new Datum.dString(String.valueOf(value1 + value2),
					t1.getColumn());
		} else if (t1 instanceof dDate) {
			System.out.println("Date not handled !!! in sum");
			return null;
		} else if (t1 instanceof dDecimal) {
			Double value1 = ((dDecimal) t1).getValue();
			Double value2 = ((dDecimal) t2).getValue();
			return new Datum.dDecimal(String.valueOf(value1 + value2),
					t1.getColumn());
		} else {
			System.out.println("Unknown datatype not handled !!! in sum");
			return null;
		}

	}

	private Datum avg(Datum t1, Datum t2) {
		if (t1 instanceof dLong) {
			Double value1 = Double.parseDouble(((dLong) t1).getStringValue());
			Double value2 = ((dDecimal) t2).getValue();
			return new Datum.dDecimal(value1 / value2,
					t1.getColumn());
		} else if (t1 instanceof dString) {
			System.out.println("String not handled !!! in avg");
			return null;
		} else if (t1 instanceof dDate) {
			System.out.println("Date not handled !!! in avg");
			return null;
		} else if (t1 instanceof dDecimal) {
			Double value1 = ((dDecimal) t1).getValue();
			Double value2 = ((dDecimal) t2).getValue();
			return new Datum.dDecimal(value1 / value2,
					t1.getColumn());
		} else {
			System.out.println("Unknown datatype not handled !!! in avg");
			return null;
		}
	}

	public Datum min(Datum t1, Datum t2) {
		if (t1 instanceof dLong) {
			Long value1 = ((dLong) t1).getValue();
			Long value2 = ((dLong) t2).getValue();
			int compare = value1.compareTo(value2);
			if (compare <= 0)
				return t1;
			else
				return t2;

		} else if (t1 instanceof dString) {
			String value1 = ((dString) t1).getValue();
			String value2 = ((dString) t2).getValue();
			int compare = value1.compareTo(value2);
			if (compare <= 0)
				return t1;
			else
				return t2;
		} else if (t1 instanceof dDate) {
			Date value1 = ((dDate) t1).getValue();
			Date value2 = ((dDate) t2).getValue();
			int compare = value1.compareTo(value2);
			if (compare <= 0)
				return t1;
			else
				return t2;
		} else if (t1 instanceof dDecimal) {
			Double value1 = ((dDecimal) t1).getValue();
			Double value2 = ((dDecimal) t2).getValue();
			int compare = value1.compareTo(value2);
			if (compare <= 0)
				return t1;
			else
				return t2;
		} else {
			System.out.println("Unknown datatype not handled !!! in min");
			return null;
		}
	}

	public Datum max(Datum t1, Datum t2) {
		if (t1 instanceof dLong) {
			Long value1 = ((dLong) t1).getValue();
			Long value2 = ((dLong) t2).getValue();
			int compare = value1.compareTo(value2);
			if (compare >= 0)
				return t1;
			else
				return t2;

		} else if (t1 instanceof dString) {
			String value1 = ((dString) t1).getValue();
			String value2 = ((dString) t2).getValue();
			int compare = value1.compareTo(value2);
			if (compare >= 0)
				return t1;
			else
				return t2;
		} else if (t1 instanceof dDate) {
			Date value1 = ((dDate) t1).getValue();
			Date value2 = ((dDate) t2).getValue();
			int compare = value1.compareTo(value2);
			if (compare >= 0)
				return t1;
			else
				return t2;
		} else if (t1 instanceof dDecimal) {
			Double value1 = ((dDecimal) t1).getValue();
			Double value2 = ((dDecimal) t2).getValue();
			int compare = value1.compareTo(value2);
			if (compare >= 0)
				return t1;
			else
				return t2;
		} else {
			System.out.println("Unknown datatype not handled !!! in min");
			return null;
		}
	}

}

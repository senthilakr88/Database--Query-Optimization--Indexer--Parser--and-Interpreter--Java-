package edu.buffalo.cse562.physicalPlan;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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

public class GroupbyOperator {
	Operator oper;
	ArrayList<SelectExpressionItem> selectExpressionList;
	List<Column> groupbyList;
	Map<String, Datum[]> groupByMap = new HashMap<String, Datum[]>();
	boolean isTupleMapPresent;
	static long tempCount = 1;
	HashMap<String,Long> mapGroupCountMap = new HashMap<String,Long>();
	Boolean avgFlag = false;
	String avgKeyString = "";
	Column avgColumnName = null;
	int avgIndex = 0;
	Boolean firstTimeFlag = false;
	Set<Integer> avgIndexList = new HashSet<Integer> ();
	Set<String> keyThatIsSumSet = new HashSet<String>();

	public GroupbyOperator(Operator oper,ArrayList<SelectExpressionItem> selectExpressionList, List<Column> groupbyList) {
		this.oper = oper;
		this.groupbyList = groupbyList;
		this.isTupleMapPresent = true;
		this.selectExpressionList = selectExpressionList;
		
	}

	/*
	 * read one tuple from the operator, iterate over the column
	 */
	public ArrayList<Datum[]> readOneTuple() {

		ArrayList<Datum[]> finalGroupByDatumArrayList = new ArrayList<Datum[]>();
		Datum[] readOneTupleFromOper = oper.readOneTuple();
		Datum singleDatum;
		if(readOneTupleFromOper == null){
			return null;
		}

		

		int count = 0;
		ArrayList<String> datumColumnName=null;
		while (readOneTupleFromOper != null) {
//			System.out.println("NEW TUPLE READ");
			if (isTupleMapPresent) {
				TupleStruct.setTupleTableMap(readOneTupleFromOper);
//				System.out.println(TupleStruct.getTupleTableMap());
				datumColumnName = (ArrayList<String>) TupleStruct
						.getTupleTableMap();
				if(!TupleStruct.isNestedCondition())
					isTupleMapPresent = false;
			}
			StringBuilder mapKey = new StringBuilder();
			for (Column groupbyColumnName : groupbyList) {
				String grpColName = groupbyColumnName.getWholeColumnName()
						.toLowerCase();
//				System.out.println(grpColName);
				if (datumColumnName.contains(grpColName)) {
					// System.out.println("TESTTTTTT");
					int index = datumColumnName.indexOf(grpColName);
//					 printTuple(readOneTupleFromOper);
					// System.out.println(index);
					singleDatum = readOneTupleFromOper[index];
					// System.out.println("GROUP BY COLUMN NAME/VALUE: "+singleDatum.getColumn().getColumnName().toLowerCase()+"/"+singleDatum.getStringValue());
					mapKey.append(singleDatum.getStringValue());
					// System.out.println(mapKey);
				} else {
					System.out.println("Not able to find a match"+ datumColumnName + " : " + grpColName);
					return null;
				}
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
					//System.out.println("FUNC NAME IN CHECK:"+funcName);
					fnMap.put(itr,funcName);
				} else {
					fnMap.put(itr, "col");
				}
				e.accept(calc);
				Datum tempDatum = getDatum(calc, newItem);
				newSelectItemsArray[itr] = tempDatum;
				//printTuple(newSelectItemsArray);
			}

			
			
			if (!groupByMap.containsKey(mapKey.toString())) {
				firstTimeFlag = true;
				//System.out.println("IF CONDITION");
				tempCount = 1;
				mapGroupCountMap.put(mapKey.toString(), tempCount);
				groupByMap.put(mapKey.toString(), newSelectItemsArray);
				//System.out.println("FIRST TIME KEY: "+mapKey.toString());
				
			} else {
				firstTimeFlag=false;
				//System.out.println("KEY/VALUE PAIR ALREADY EXISTING IN MAP");
				tempCount = mapGroupCountMap.get(mapKey.toString());
				//System.out.println("DATUM THAT S SUMMED------------: "+ mapKey.toString());
				//keyThatIsSum.add(mapKey.toString());
				keyThatIsSumSet.add(mapKey.toString());
				tempCount++;
				Datum[] datumArray = groupByMap.get(mapKey.toString());
				Datum[] tempDatum = new Datum[datumArray.length];
				int i;
				for (i=0;i<datumArray.length;i++) {
					String funcName = fnMap.get(i);
//					avgIndex = i;
					if (funcName.equalsIgnoreCase("avg")){
						avgIndexList.add(i);
					}
//					System.out.println("INDEX for average is: "+avgIndexList.toString());
					tempDatum[i] = getDatumFun(funcName, newSelectItemsArray[i], datumArray[i]);
					//System.out.println(newSelectItemsArray[i] + " :: " + datumArray[i]+ " :: " + funcName + "::"+tempDatum[i]);
				}

				groupByMap.put(mapKey.toString(), tempDatum);
				mapGroupCountMap.put(mapKey.toString(), tempCount);
//				System.out.println("PRINT COUNT GROUP MAP");
//				printCountMap(mapGroupCountMap);
				
			}
			readOneTupleFromOper = this.oper.readOneTuple();
		}
//		System.out.println("PRINT MAP");
//		printMap(groupByMap);
//		System.out.println(keyThatIsSumSet);
		//avg code
		if(avgFlag == true){
//			System.out.println("avg flag true");

			for (Entry<String, Datum[]> entry : groupByMap.entrySet()) {
//				System.out.println("entered loop");

				Datum[] tempDatum = entry.getValue();
//				System.out.println(tempDatum.toString());
//				for(String iter: keyThatIsSumSet){
					//System.out.println(iter);
					
				for(int avgIndex:avgIndexList){		
					Datum avgDatum = tempDatum[avgIndex];
					tempDatum[avgIndex]=avg(avgDatum,entry.getKey());
				}
//					}
//				}
				groupByMap.put(entry.getKey(), tempDatum);
			}
		}
		
//		System.out.println("PRINT MAP AFTER AVG IF AVG IS THERE!!!");
//		printMap(groupByMap);
		//printCountMap(mapGroupCountMap);
		finalGroupByDatumArrayList.addAll(groupByMap.values());
		return finalGroupByDatumArrayList;
	}

	private Datum avg(Datum avgDatum, String groupName) {
		Long groupCount = mapGroupCountMap.get(groupName);
//		System.out.println("GroupCount"+groupCount);
		if(firstTimeFlag!=true){
			if (avgDatum instanceof dLong) {
				long value1 = ((dLong) avgDatum).getValue();
				
				double val1 = (double)value1;
				double tempCountDouble = (double)groupCount;
//				System.out.println("GroupVal"+val1);
				Double avg = val1/tempCountDouble;
				avgColumnName = avgDatum.getColumn();
				return new Datum.dDecimal(String.valueOf(avg),avgDatum.getColumn());
			} else if (avgDatum instanceof dString) {
				//System.out.println("Date not handled !!! in sum");
				return null;
			} else if (avgDatum instanceof dDate) {
				//System.out.println("Date not handled !!! in sum");
				return null;
			} else if (avgDatum instanceof dDecimal) {
//				System.out.println("Decimal type");
				Double value1 = ((dDecimal) avgDatum).getValue();
//				System.out.println(value1);
				value1 = value1*100/100;
//				System.out.println(value1);
				Double result = value1/groupCount;
				result=result*100/100;
				return new Datum.dDecimal(String.valueOf(result),
						avgDatum.getColumn());
			} else {
				//System.out.println("Unknown datatype not handled !!! in sum");
				return null;
			}
		}
		else{
			return avgDatum;
		}
		
	}

	private void printCountMap(HashMap<String, Long> mapGroupCountMap2) {
		//System.out.println("SIZE OF THE MAP" + mapGroupCountMap2.size());
		for (Entry<String, Long> entry : mapGroupCountMap2.entrySet()) {
			System.out.println("Key = " + entry.getKey() + ", Value = "
					+ entry.getValue());
		}
		
	}

	private Datum getDatumFun(String funcName, Datum t1,
			Datum t2) {
		switch(funcName.toLowerCase()){
		case "col":
			return t1;
		case "sum":
//			System.out.println("AGGREGATE FUNC - SUM");
			return sum(t1,t2);
		case "count":
//			System.out.println("AGGREGATE FUNC - COUNT method");
			return count();
		case "min":
//			System.out.println("MIN method");
			return min(t1,t2);
		case "max":
//			System.out.println("MAX method");
			return max(t1,t2);
		case "avg":
			//System.out.println("AVG method");
			avgFlag = true;
			return sum(t1,t2);
		case "stdev":
			//System.out.println("STDEV method... Not handled");
			return null;
		default:
			//System.out.println("AGGREGATE FUNCTION NOT MATCHED" + funcName);
			return null;
		}
	}
	
	private Datum count() {
//		System.out.println("COUNT of Count method"+ tempCount);
		String value = Long.toString(tempCount);
		Column newCol = new Column();
		return new dLong(value,newCol);
	}

	public void printTestMap(Map groupMap) {
		System.out.println("SIZE OF THE MAP" + groupByMap.size());
		for (Entry<String, Datum[]> entry : groupByMap.entrySet()) {
			System.out.println("Key = " + entry.getKey() + ", Value = "+ entry.getValue() + "value size: "+ entry.getValue().length);
		}
	}

	 public void printMap(Map<String, Datum[]> groupByMap) {
		 for (Entry<String, Datum[]> entry : groupByMap.entrySet()) {
			 Datum[] MapValue = entry.getValue();
			 System.out.println("KEY: "+ entry.getKey());
			 printTuple(MapValue);
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
			tempDatum = new Datum.dDecimal((Double)(calcOut), newCol);
			
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
		System.out.println("------------------------------------------------");
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
			//System.out.println("Date not handled !!! in sum");
			return null;
		} else if (t1 instanceof dDecimal) {
			Double value1 = ((dDecimal) t1).getValue();
			Double value2 = ((dDecimal) t2).getValue();
			return new Datum.dDecimal(String.valueOf(value1 + value2),
					t1.getColumn());
		} else {
			//System.out.println("Unknown datatype not handled !!! in sum");
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
			//System.out.println("Unknown datatype not handled !!! in min");
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
			//System.out.println("Unknown datatype not handled !!! in min");
			return null;
		}
	}

}

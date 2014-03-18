package edu.buffalo.cse562.physicalPlan;

/*
 * to do
 * 1. pass the column name to the sum method
 * 2. loop readOneTuple method read all tuple as a datum[] from where (call readOneTuple method on select operator object)
 * 3. find the duplicate in the column (group by - column name) passed and compute the expression on the 
 * 4. Keep a map: key as the column name and value as the computed value (This has to be done as a buffer while reading the tuples. Ex: sum can be adding the values)
 * 
 */


import java.util.ArrayList;
import java.util.List;

import edu.buffalo.cse562.physicalPlan.Datum.dDate;
import edu.buffalo.cse562.physicalPlan.Datum.dLong;
import edu.buffalo.cse562.physicalPlan.Datum.dString;
import net.sf.jsqlparser.expression.AllComparisonExpression;
import net.sf.jsqlparser.expression.AnyComparisonExpression;
import net.sf.jsqlparser.expression.CaseExpression;
import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.InverseExpression;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.TimeValue;
import net.sf.jsqlparser.expression.TimestampValue;
import net.sf.jsqlparser.expression.WhenClause;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseAnd;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseOr;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseXor;
import net.sf.jsqlparser.expression.operators.arithmetic.Concat;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.Between;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExistsExpression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.IsNullExpression;
import net.sf.jsqlparser.expression.operators.relational.LikeExpression;
import net.sf.jsqlparser.expression.operators.relational.Matches;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SelectItemVisitor;
import net.sf.jsqlparser.statement.select.SubSelect;

public class Test{
	// Select body and the groupby column name as the fields in the object that is passed to the visitor object
	Operator oper;
	SelectBody selectBody;
	List<Column> groupByColumnReferences;
	List<Column> tableColumnList;

	Datum aggregateSumDatum = null;
	
	public Test(Operator oper,SelectBody selectBody, List<Column> tableColumnList){
		this.selectBody = selectBody;
		groupByColumnReferences = ((PlainSelect) selectBody).getGroupByColumnReferences();
		this.tableColumnList = tableColumnList;
		this.oper = oper;

	}
	
	public void resetAggregateDatumBuffer(){
		this.aggregateSumDatum = null;
	}
	
	

	public void resetStream() {
		oper.resetStream();
		
	}

	public Datum[] aggregateFunction(Datum[] readOneTupleFromGroupBy, ArrayList<Function> aggregateFuncList) {
		Datum[] readOneTupleFromOper = null;
		
		System.out.println("AGGREGATE FUNC");
		Datum[] test = readOneTupleFromGroupBy;
		//printTuple(test);
		if(test != null){
			System.out.println("LIST SIZE:"+ aggregateFuncList.size());
			for(Function func:aggregateFuncList){
				ExpressionList funcParamExpressionList = func.getParameters();
				List<Column> funcParamList = funcParamExpressionList.getExpressions();
				String funcName = func.getName().toLowerCase().trim();
				
				Boolean isGroupByColumnSingleFlag = false;
				//ArrayList<Column> sumParamArrayList = (ArrayList<Column>) funcParamList;
				String singleParamColumnName = "";
				if(groupByColumnReferences.size()==1){
					isGroupByColumnSingleFlag = true;
				}	
				System.out.println("AGGREGATE FUNC HAS SINGLE PARAMETER");
				singleParamColumnName = funcParamList.get(0).getColumnName();
				//System.out.println("************"+singleParamColumnName);

				switch(funcName){
				case "sum":
					System.out.println("AGGREGATE FUNC - SUM");
					readOneTupleFromOper = sum(test, singleParamColumnName);
					break;
				case "count":
					System.out.println("AGGREGATE FUNC - COUNT method");
					//readOneTupleFromOper = count(test, singleParamColumnName);
					break;
				case "min":
					System.out.println("MIN method");
					break;
				case "max":
					System.out.println("MAX method");
					break;
				case "avg":
					System.out.println("AVG method");
					break;
				case "stdev":
					System.out.println("STDEV method");
					break;
				default:
					System.out.println("AGGREGATE FUNCTION NOT MATCHED");
					break;
				}
			
			}
		}
		
		return readOneTupleFromOper;
	}
	
	private Datum[] count(Datum[] test, String singleParamColumnName,
			Boolean isGroupByColumnSingleFlag) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * to do - compute sum of the values stored (singleParamColumnName) in a column within a table (singleParamTableName)
	 * input - table and column name
	 * compute - read the tuple (Datum[]) after where i.e. after checking the where condition
	 */
	public Datum[] sum(Datum[] readOneTupleFromOper,String singleParamColumnName){
		//System.out.println("DATUM[] PASSED TO SUM FUNCTION");
		//printTuple(readOneTupleFromOper);
		Datum matchDatum=null;
		for(int i=0;i<readOneTupleFromOper.length;i++){
			//System.out.println("LENGTH OF READONETUPLE: "+readOneTupleFromOper.length);
			//System.out.println("Function Parameter column name: "+singleParamColumnName);
			Datum singleTupleElement =null;
			if(readOneTupleFromOper.length>0){
				singleTupleElement = readOneTupleFromOper[i];
			}
			
			dLong datumInLong;
			dString datumInString;
			dDate datumInDate;
			if(singleTupleElement instanceof dLong){
				datumInLong = (dLong) singleTupleElement;
				//System.out.println(datumInLong.toString().toLowerCase()+"&&&&&&&&&&&&&&&&&&");
				if(datumInLong.getColumn().getColumnName().toLowerCase().equals(singleParamColumnName.toLowerCase())){
					matchDatum = datumInLong;
					System.out.println("MATCH DATUM: "+matchDatum);
					dLong aggregatedLongSumDatum = null;
					if(aggregateSumDatum != null){
						aggregatedLongSumDatum = (dLong) aggregateSumDatum;
						aggregateSumDatum = datumInLong.sumDatum(aggregatedLongSumDatum);
					}
					else{
						aggregateSumDatum = datumInLong;
					}
					dLong temp = (dLong) aggregateSumDatum;
					System.out.println("REPLACE AGE: "+temp);
					break;
				}
				continue;
			}
			else if(singleTupleElement instanceof dString){
				datumInString = (dString) singleTupleElement;
				if(datumInString.getColumn().equals(singleParamColumnName)){						
				//aggregateSumDatum = datumInString.sumDatum(aggregateSumDatum);
					matchDatum = datumInString;
					break;
				}
				continue;
			}
			else if(singleTupleElement instanceof dString){
				datumInDate = (dDate) singleTupleElement;
				if(datumInDate.getColumn().equals(singleParamColumnName)){
					//aggregateSumDatum = datumInDate.sumDatum(aggregateSumDatum);
					matchDatum = datumInDate;
					break;
				}
				continue;
			}	
		}			
		for (int i=0; i<readOneTupleFromOper.length;i++) {
			   if (readOneTupleFromOper[i].equals(matchDatum)) { 
			   	readOneTupleFromOper[i] = aggregateSumDatum;
		        break;
		    }
		}	
		printTuple(readOneTupleFromOper);
		return readOneTupleFromOper;
	}
	
	private void printTuple(Datum[] row) {
		if(row!=null && row.length !=0) {
		for(Datum col : row) {
			System.out.print(col + "|");
		}
		System.out.println("");
		}
		System.out.println("------------------------------------------------");
	}

}

	
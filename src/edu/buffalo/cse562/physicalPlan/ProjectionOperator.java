package edu.buffalo.cse562.physicalPlan;

//import net.sf.jsqlparser.expression.Expression;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;
//import net.sf.jsqlparser.statement.select.SelectItem;
import edu.buffalo.cse562.logger.logManager;
import edu.buffalo.cse562.physicalPlan.Operator;
import edu.buffalo.cse562.physicalPlan.Tuple;
import edu.buffalo.cse562.sql.expression.evaluator.CalcTools;
import edu.buffalo.cse562.sql.expression.evaluator.SelectItemTools;

public class ProjectionOperator implements Operator {

	Operator input;

	String temp = null;
	List<SelectExpressionItem> selectcolumns;
	boolean isTupleMapPresent;


	public ProjectionOperator(Operator input,
			List<SelectExpressionItem> selectcolumns) {
		this.selectcolumns = selectcolumns;
		this.input = input;
		this.isTupleMapPresent = true;

	}


	public void resetStream() {
		input.resetStream();

	}

	public Datum[] readOneTuple() {
		logManager lg = new logManager();
		Datum[] t = null;
		Datum[] listDatum = new Datum [] {};
		ArrayList<Datum> arrayListDatum = new ArrayList<Datum> ();
		
		t = input.readOneTuple();
		if (t != null) {
			int i=0;
			if(isTupleMapPresent) {
				TupleStruct.setTupleTableMap(t);
				if(!TupleStruct.isNestedCondition())
					isTupleMapPresent = false;
//				System.out.println("_____"+TupleStruct.getTupleTableMap());
			}
			Iterator<SelectExpressionItem> iter=selectcolumns.iterator();
			while(iter.hasNext()){
				SelectItem newItem = iter.next();
				SelectItemTools st = new SelectItemTools();
				newItem.accept(st);
				String stType = st.getItemType();
//				System.out.println(newItem.getClass().getName());
//				System.out.println(i);
				if(stType.equalsIgnoreCase("AllColumns")){
					//					listDatum = t;
				} else if (stType.equalsIgnoreCase("AllTableColumns")){
					String requestedTableName = ((AllTableColumns) newItem).getTable().toString();
//					System.out.println("Received request for---"+((AllTableColumns) newItem).getTable());
					for (int iterable=0;iterable<t.length;iterable++){
//						System.out.println("In the tuple----"+t[iterable].getColumn().getTable().toString());
						if(t[iterable].getColumn().getTable().getAlias().toString().equalsIgnoreCase(requestedTableName)){
							
//								System.out.println("Matched alias name");
								arrayListDatum.add(t[iterable]);
							
						} else {
							if(t[iterable].getColumn().getTable().toString().equalsIgnoreCase(requestedTableName)){
//								System.out.println("Matched table name");
								arrayListDatum.add(t[iterable]);
							}
						}
					}
					//					listDatum[i]=new Datum.dString("SsssS", new Column(null,"sdfdfs"));
				} else if (stType.equalsIgnoreCase("SelectExpressionItem")){
//					System.out.println(((SelectExpressionItem) newItem).getExpression().toString());
					Expression e = ((SelectExpressionItem) newItem).getExpression();

					CalcTools calc = new CalcTools(t); 
					e.accept(calc);
					
					
					lg.logger.log(Level.INFO, calc.getResult().toString());
					//                System.out.println("PRinting column name--->"+calc.getColumn().getColumnName());
					Column newCol = null;
					//                Table result = new Table("", "ResultTable");
					if (((SelectExpressionItem) newItem).getAlias()!=null){
						newCol = new Column(null, ((SelectExpressionItem) newItem).getAlias());
					}
					else {
						newCol = calc.getColumn();
						//                    newCol = new Column(result, newItem.toString());
					}
					//                System.out.println(newCol.getWholeColumnName());

					Object ob = calc.getResult();
					Datum tempDatum = null;
					if (ob instanceof Long) {
						lg.logger.log(Level.INFO, "========Long");
						tempDatum = new Datum.dLong(ob.toString(), newCol);
					} else if (ob instanceof Double) {
						lg.logger.log(Level.INFO, "========Double");
						tempDatum = new Datum.dDecimal(ob.toString(), newCol);
					} else if (ob instanceof String) {
						lg.logger.log(Level.INFO, "========String");
						tempDatum = new Datum.dString((String) ob, newCol);
					} else if (ob instanceof java.util.Date) {
						lg.logger.log(Level.INFO, "=========Date");
						DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
						tempDatum = new Datum.dDate(df.format(ob), newCol);
					} else {
						lg.logger.log(Level.INFO, "Wrong Type");
						try {
							throw new Exception("Not aware of this data type ");
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
					//                lg.logger.log(Level.INFO, "(((()))))"+tempDatum.toComString());
					arrayListDatum.add(tempDatum);
					i++;

				}
			}
			//			for(int index=0;index<listDatum.length;index++){
			//				System.out.println(listDatum[index].toString());
			//			}
			//			System.out.println(listDatum.toString());


		}

		else {
			return null;
		}
		listDatum = arrayListDatum.toArray(listDatum);
//		System.out.println(arrayListDatum.toString());
		return listDatum;

	}
}


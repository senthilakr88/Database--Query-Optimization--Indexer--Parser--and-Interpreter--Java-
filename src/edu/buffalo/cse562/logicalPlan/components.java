package edu.buffalo.cse562.logicalPlan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SubJoin;
import net.sf.jsqlparser.statement.select.SubSelect;
import edu.buffalo.cse562.logger.logManager;
import edu.buffalo.cse562.physicalPlan.AggregateOperator;
import edu.buffalo.cse562.physicalPlan.Datum;
import edu.buffalo.cse562.physicalPlan.FileScanOperator;
import edu.buffalo.cse562.physicalPlan.FromItemParser;
import edu.buffalo.cse562.physicalPlan.GroupbyOperator;
import edu.buffalo.cse562.physicalPlan.JoinOperator;
import edu.buffalo.cse562.physicalPlan.Operator;
import edu.buffalo.cse562.physicalPlan.OrderByOperator;
import edu.buffalo.cse562.physicalPlan.ProjectionOperator;
import edu.buffalo.cse562.physicalPlan.SelectionOperator;
import edu.buffalo.cse562.physicalPlan.Test;
import edu.buffalo.cse562.physicalPlan.Tuple;
import edu.buffalo.cse562.physicalPlan.TupleStruct;

public class components{

	logManager lg;
	List<Column> tableMap;
	Map<String, ArrayList<String>> tableColTypeMap;
	ArrayList<SelectExpressionItem> projectStmt;
	ArrayList tableJoins;
	Expression whereClause;
	String tableDir;
	FromItem fromItem;
	SelectBody selectBody;
	private List orderbyElements;
	Thread t[];

	public components() {

		tableMap = new ArrayList<Column>();
		tableColTypeMap = new HashMap<String, ArrayList<String>>();
		lg = new logManager();
	}

	public void initializeNewStatement() {
		projectStmt = new ArrayList<SelectExpressionItem>();

	}

	public void addProjectStmts(List<SelectExpressionItem> list) {
		projectStmt.addAll(list);
	}

	public void addWhereConditions(Expression where) {
		whereClause = where;
	}

	public void setSelectBody(SelectBody selectBody) {
		this.selectBody = selectBody;
	}

	public String toString() {
		StringBuffer toPrint = new StringBuffer();
		toPrint.append("PROJECT [(" + projectStmt + ")]\n");
		toPrint.append("SELECT [" + whereClause + "\n");
		// for (Map.Entry<String, ArrayList<String>> entry :
		// tableMap.entrySet()) {
		// toPrint.append("SCAN [" + entry.getKey() + "(" + entry.getValue()
		// + ")]");
		// }
		return toPrint.toString();
	}

	public Operator executePhysicalPlan() {
		Operator oper = null;
		long startTime = System.currentTimeMillis();
		FromItemParser fip = new FromItemParser(tableDir, tableMap,
				tableColTypeMap);
		fromItem.accept(fip);
		oper = fip.getOperator();
		if (tableJoins != null) {
			TupleStruct.setJoinCondition(true);
			Iterator joinIte = tableJoins.iterator();
			int totaljoins=tableJoins.size();
			
			if(totaljoins>=4)
			{
				
			}
			while (joinIte.hasNext()) {

				Join joinTable = (Join) joinIte.next();
				fip = new FromItemParser(tableDir, tableMap, tableColTypeMap);
				joinTable.getRightItem().accept(fip);
				Operator rightOper = fip.getOperator();
				oper = new JoinOperator(oper, rightOper,
						joinTable.getOnExpression());

			}
		}

		if (whereClause != null) {
			oper = new SelectionOperator(oper, whereClause);

		}
		
		boolean isFunction=false;
		for(SelectExpressionItem sei : projectStmt) {
			Expression e = sei.getExpression();
			if(e instanceof Function) 
				isFunction = true;
		}
		
		if (((PlainSelect) selectBody).getGroupByColumnReferences() != null) {
			// Groupby computation
			PlainSelect select = (PlainSelect) selectBody;
			List<Column> groupbyList = select.getGroupByColumnReferences();
			GroupbyOperator groupOper = new GroupbyOperator(oper, projectStmt,
					groupbyList);
			ArrayList<Datum[]> finalGroupbyArrayList = groupOper.readOneTuple();
			OrderBy(finalGroupbyArrayList);
			return null;
		} else if (isFunction) {
			PlainSelect select = (PlainSelect) selectBody;
			AggregateOperator aggrOper = new AggregateOperator(oper, projectStmt);
			ArrayList<Datum[]> finalGroupbyArrayList = aggrOper.readOneTuple();
			OrderBy(finalGroupbyArrayList);
			return null;
		} else {
		
			
		
			
			oper = new ProjectionOperator(oper, projectStmt);
			long endTime   = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			System.out.println(totalTime);
			
		}

		return oper;

	}

	
	public void OrderBy(ArrayList<Datum[]> list) {
		if(list == null)
			return;
		OrderByOperator obp = new OrderByOperator(orderbyElements);
		obp.setListDatum(list);
		if (orderbyElements != null) {
			obp.sort();
		}
		obp.print();
	}
	
	public void processTuples(Operator oper) {
		OrderByOperator obp = new OrderByOperator(orderbyElements);
		Datum[] t = oper.readOneTuple();
		while (t != null) {
			if (orderbyElements != null) {
				obp.addTuple(t);
			} else {
				printTuple(t);
			}
			t = oper.readOneTuple();
		}
		if (orderbyElements != null) {
			obp.sort();
			obp.print();
		}
	}

	private void printGroupTuples(ArrayList<Datum[]> finalGroupbyArrayList) {
		System.out
				.println("------------PRINTING TUPLE FROM GROUPBY OPERATOR--------");
		for (Datum[] singleDatum : finalGroupbyArrayList) {
			printTuple(singleDatum);
		}
	}

	private void printTuple(Datum[] row) {
		Boolean first = true;
		if (row != null && row.length != 0) {
			for (Datum col : row) {
				if (!first)
					System.out.print("|" + col);
				else {
					System.out.print(col);
					first = false;
				}
			}
			System.out.println();
		}
	}

	public void setTableDirectory(String tableDir) {

		this.tableDir = tableDir;

	}

	public void setFromItems(FromItem fromItem) {
		this.fromItem = fromItem;

	}

	public void addColsTypeToTable(String table,
			ArrayList<String> columnTypeList) {
		if (tableColTypeMap.containsKey(table)) {
			tableColTypeMap.remove(table);
			tableColTypeMap.put(table, columnTypeList);
		} else {
			tableColTypeMap.put(table, columnTypeList);
		}

	}

	public void addColsTypeToTable(
			Map<String, ArrayList<String>> tableColTypeMap) {
		this.tableColTypeMap = tableColTypeMap;

	}

	public void addColsToTable(ArrayList<Column> columnNameList) {
		tableMap.addAll(columnNameList);

	}

	public void addJoins(List joins) {
		this.tableJoins = (ArrayList) joins;

	}

	public void addOrderBy(List orderByElements) {
		this.orderbyElements = orderByElements;

	}

}

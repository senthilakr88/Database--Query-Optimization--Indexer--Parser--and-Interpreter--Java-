package edu.buffalo.cse562.physicalPlan;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import edu.buffalo.cse562.logicalPlan.components;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.FromItemVisitor;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SubJoin;
import net.sf.jsqlparser.statement.select.SubSelect;

public class FromItemParser implements FromItemVisitor {

	Operator oper = null;
	String basePath;
	List<Column> tableMap;
	Map<String, ArrayList<String>> tableColTypeMap;

	public FromItemParser(String basePath, List<Column> tableMap,
			Map<String, ArrayList<String>> tableColTypeMap) {
		this.basePath = basePath;
		this.tableMap = tableMap;
		this.tableColTypeMap = tableColTypeMap;
	}

	@Override
	public void visit(Table table) {
		
		oper = new FileScanOperator(table, basePath, tableMap, tableColTypeMap);

	}

	@Override
	public void visit(SubSelect subSelect) {
		SelectBody selectStmt = subSelect.getSelectBody();
		if (selectStmt instanceof PlainSelect) {
			PlainSelect plainSelect = (PlainSelect) subSelect.getSelectBody();
			//System.out.println(plainSelect.toString());
			components comp = new components();
			comp.initializeNewStatement();
			comp.addColsToTable((ArrayList<Column>) tableMap);
			comp.addColsTypeToTable(tableColTypeMap);
			comp.setTableDirectory(basePath);
			comp.addProjectStmts(plainSelect.getSelectItems());
			comp.setSelectBody(selectStmt);
			comp.setFromItems(plainSelect.getFromItem());
			comp.addWhereConditions(plainSelect.getWhere());
			comp.addOrderBy(plainSelect.getOrderByElements());
			comp.addJoins(plainSelect.getJoins());
			TupleStruct.setNestedCondition(true);
			oper = comp.executePhysicalPlan();
		} else {
			System.out.println("This subselect statement is yet to be handled :: "+selectStmt);
		}
	}

	@Override
	public void visit(SubJoin subJoin) {

	}

	public Operator getOperator() {
		return oper;
	}

}

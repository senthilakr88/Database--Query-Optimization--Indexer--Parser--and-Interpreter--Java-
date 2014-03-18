package edu.buffalo.cse562.sql.expression.evaluator;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.*;
import net.sf.jsqlparser.expression.operators.conditional.*;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import edu.buffalo.cse562.logger.logManager;
import edu.buffalo.cse562.physicalPlan.Datum;
import edu.buffalo.cse562.physicalPlan.TupleStruct;

public class SelectItemTools extends AbstractSelectItemVisitor {
	private String type;
	public SelectItemTools() {
		type = null;
	}
	@Override
	public void visit(AllColumns arg0) {
		type = "AllColumns";
//		System.out.println("All Columns");
	}

	@Override
	public void visit(AllTableColumns arg0) {
		type = "AllTableColumns";
//		System.out.println("All Table Columns");
		
	}

	@Override
	public void visit(SelectExpressionItem arg0) {
		type = "SelectExpressionItem";
//		System.out.println("SelectExpressionItem");
		
	}
    public String getItemType(){
    	return type;
    }
}
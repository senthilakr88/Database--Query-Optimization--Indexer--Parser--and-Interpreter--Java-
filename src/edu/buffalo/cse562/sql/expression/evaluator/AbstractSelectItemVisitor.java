package edu.buffalo.cse562.sql.expression.evaluator;

import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.*;
import net.sf.jsqlparser.expression.operators.conditional.*;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.OrderByVisitor;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItemVisitor;
import net.sf.jsqlparser.statement.select.SubJoin;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.select.WithItem;

public class AbstractSelectItemVisitor implements SelectItemVisitor
{

	@Override
	public void visit(AllColumns arg0) {
        throw new UnsupportedOperationException("Not supported yet."); 
		
	}

	@Override
	public void visit(AllTableColumns arg0) {
        throw new UnsupportedOperationException("Not supported yet."); 
		
	}

	@Override
	public void visit(SelectExpressionItem arg0) {
        throw new UnsupportedOperationException("Not supported yet."); 
		
	}
    
}

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
import net.sf.jsqlparser.statement.select.SubJoin;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.select.WithItem;

/**
 *
 * Default behavior for ExpressionVisitors: raise UnsupportedOperationException
 * 
 * @author Niccolo' Meneghetti
 */
public class AbstractExpressionVisitor implements ExpressionVisitor, OrderByVisitor
{

    @Override
    public void visit(NullValue nv) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void visit(Function fnctn) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void visit(InverseExpression ie) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void visit(JdbcParameter jp) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void visit(DoubleValue dv) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void visit(LongValue lv) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void visit(DateValue dv) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void visit(TimeValue tv) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void visit(TimestampValue tv) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void visit(Parenthesis prnths) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void visit(StringValue sv) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void visit(Addition adtn) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void visit(Division dvsn) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void visit(Multiplication m) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void visit(Subtraction s) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void visit(AndExpression ae) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void visit(OrExpression oe) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void visit(Between btwn) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void visit(EqualsTo et) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void visit(GreaterThan gt) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void visit(GreaterThanEquals gte) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void visit(InExpression ie) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void visit(IsNullExpression ine) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void visit(LikeExpression le) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void visit(MinorThan mt) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void visit(MinorThanEquals mte) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void visit(NotEqualsTo net) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void visit(Column column) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void visit(SubSelect ss) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void visit(CaseExpression ce) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void visit(WhenClause wc) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void visit(ExistsExpression ee) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void visit(AllComparisonExpression ace) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void visit(AnyComparisonExpression ace) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void visit(Concat concat) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void visit(Matches mtchs) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void visit(BitwiseAnd ba) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void visit(BitwiseOr bo) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void visit(BitwiseXor bx) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

	public void visit(WithItem wi) {
		// TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not supported yet."); 

	}

	public void visit(SelectExpressionItem sei) {
		// TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not supported yet."); 

	}

	public void visit(AllTableColumns atc) {
		// TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not supported yet."); 

	}

	public void visit(AllColumns ac) {
		// TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not supported yet."); 

	}

	public void visit(SubJoin subjoin) {
		// TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not supported yet."); 

	}

	public void visit(Object longValue) {
		// TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not supported yet."); 

	}

	@Override
	public void visit(OrderByElement arg0) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not supported yet.");
	}
    
}

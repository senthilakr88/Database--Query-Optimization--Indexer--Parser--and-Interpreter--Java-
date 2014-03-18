package edu.buffalo.cse562.sql.expression.example;

import java.util.logging.Level;

import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction;
import edu.buffalo.cse562.logger.logManager;

/**
 *
 * @author Niccolo' Meneghetti
 */
public class SimpleCalculator extends AbstractExpressionVisitor
{
    private long accumulator;
    logManager lg = new logManager();
    
    public long getResult()
    
    { 	
        lg.logger.log(Level.INFO, Long.toString(accumulator));
    	return accumulator; }

    @Override
    public void visit(LongValue lv)
    { 	
    	
    	lg.logger.log(Level.INFO, Long.toString(accumulator));
    	accumulator=lv.getValue(); }
    

    @Override
    public void visit(Addition adtn) 
    {
        adtn.getLeftExpression().accept(this);
        long leftValue = accumulator;
        adtn.getRightExpression().accept(this);
        long rightValue = accumulator;
        accumulator=(leftValue+rightValue);
    }

    @Override
    public void visit(Subtraction s) 
    {
        s.getLeftExpression().accept(this);
        long leftValue = accumulator;
        s.getRightExpression().accept(this);
        long rightValue = accumulator;
        accumulator=(leftValue-rightValue);
    }
    
    @Override
    public void visit(Multiplication s) 
    {
        s.getLeftExpression().accept(this);
        long leftValue = accumulator;
        s.getRightExpression().accept(this);
        long rightValue = accumulator;
        accumulator=(leftValue*rightValue);
    }
    
    public void visit(Division s) 
    {
        s.getLeftExpression().accept(this);
        long leftValue = accumulator;
        s.getRightExpression().accept(this);
        long rightValue = accumulator;
        accumulator=(leftValue/rightValue);
    }

    @Override
    public void visit(Parenthesis prnths) 
    { prnths.getExpression().accept(this); }
    
    
}
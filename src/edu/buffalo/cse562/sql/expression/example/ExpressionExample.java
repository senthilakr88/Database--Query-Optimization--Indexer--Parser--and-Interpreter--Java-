package edu.buffalo.cse562.sql.expression.example;

import java.io.StringReader;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.parser.ParseException;

public class ExpressionExample 
{
    public static void main(String[] args) throws ParseException
    {
        String str = "1-(9/2)";
        Expression arithmeticExpr = parseArithmeticExpression(str);
        //lg.logger.setUseParentHandlers(false);
        
        SimpleCalculator calculator = new SimpleCalculator();
        arithmeticExpr.accept(calculator);
       
        
    }
    
    /**
     * 
     * Parse something like "(3+2)-A"
     * 
     * @param exprStr
     * @return
     * @throws ParseException 
     */
    public static Expression parseArithmeticExpression(String exprStr) throws ParseException
    {
        CCJSqlParser parser = new CCJSqlParser(new StringReader(exprStr));
        Expression parExp = parser.SimpleExpression();
        System.out.println("Expression :: " + parExp);
        return parExp;
    }
    
    
    /**
     * 
     * Parse something like "A<B*10"
     * 
     * @param exprStr
     * @return
     * @throws ParseException 
     */
    public static Expression parseGeneralExpression(String exprStr) throws ParseException
    {
        CCJSqlParser parser = new CCJSqlParser(new StringReader(exprStr));
        return parser.Expression();
    }
}
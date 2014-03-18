package edu.buffalo.cse562.controller;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import edu.buffalo.cse562.logger.logManager;
import edu.buffalo.cse562.logicalPlan.components;
import edu.buffalo.cse562.physicalPlan.Operator;
import edu.buffalo.cse562.utilities.fileReader;

public class queryParser {

	List<String> sqlFiles;
	List<String> sqlQueryList;
	logManager lg;
	components comp;
	String tableDir;

	public queryParser(String tableDir, List<String> sqlFiles) {
		this.sqlFiles = sqlFiles;
		this.tableDir = tableDir;
		lg = new logManager();

	}

	public void interpretFile() {

		Iterator<String> fileIte = sqlFiles.iterator();
		while (fileIte.hasNext()) {
			fileReader fr = new fileReader(fileIte.next());
			sqlQueryList = fr.readContents();
			lg.logger.log(Level.INFO, sqlQueryList.toString());
			comp = new components();
			interpretQuery();

		}
	}

	public void interpretQuery() {
		Iterator<String> queryIte = sqlQueryList.iterator();
		while (queryIte.hasNext()) {
			CCJSqlParserManager parser = new CCJSqlParserManager();

			try {
				Statement statement = parser.parse(new StringReader(queryIte
						.next()));
				lg.logger.log(Level.INFO, statement.toString());

				/*
				 * @author - vino logic - create a hashMap with table name as
				 * the key and array list of column names as value
				 */
				if (statement instanceof CreateTable) {
					lg.logger.log(Level.INFO, "QUERY TYPE: create");
					CreateTable createTableStatement = (CreateTable) statement;
					ArrayList<Column> columnNameList = new ArrayList<Column>();
					ArrayList<String> columnTypeList = new ArrayList<String>();
					ArrayList<ColumnDefinition> columnDefinitionList = (ArrayList) createTableStatement
							.getColumnDefinitions();
					Table table = createTableStatement.getTable();
					for (ColumnDefinition s : columnDefinitionList) {
						columnNameList
								.add(new Column(table, s.getColumnName()));
						columnTypeList.add(s.getColDataType().toString());
					}
					// Adding table name and column names to the map
					comp.addColsToTable(columnNameList);
					comp.addColsTypeToTable(createTableStatement.getTable()
							.getName().toLowerCase(), columnTypeList);
					comp.setTableDirectory(tableDir);
					// Printing the contents of the HashMap
					lg.logger.log(Level.INFO, comp.toString());

				} else if (statement instanceof Select) {
					comp.initializeNewStatement();
					SelectBody selectStmt = ((Select) statement)
							.getSelectBody();
					if (selectStmt instanceof PlainSelect) {
						
						PlainSelect plainSelect = (PlainSelect) selectStmt;
						lg.logger.log(Level.INFO, "plainSelect :: "
								+ plainSelect.toString());
						comp.addProjectStmts(plainSelect.getSelectItems());
						comp.setSelectBody(selectStmt);
						// lg.logger.log(Level.INFO,"from :: " +
						// plainSelect.getFromItem().toString());
						comp.setFromItems(plainSelect.getFromItem());
						// lg.logger.log(Level.INFO,plainSelect.getOrderByElements());
						comp.addWhereConditions(plainSelect.getWhere());
						// lg.logger.log(Level.INFO,"where :: "+plainSelect.getWhere().toString());
						// lg.logger.log(Level.INFO,plainSelect.getGroupByColumnReferences().toString());
						// lg.logger.log(Level.INFO,plainSelect.getInto().toString());
						// lg.logger.log(Level.INFO,plainSelect.getHaving().toString());
						// lg.logger.log(Level.INFO,plainSelect.getLimit().toString());
						// lg.logger.log(Level.INFO,plainSelect.getJoins().toString());
						comp.addOrderBy(plainSelect.getOrderByElements());
						comp.addJoins(plainSelect.getJoins());
						// lg.logger.log(Level.INFO,plainSelect.getTop().toString());
						lg.logger.log(Level.INFO, comp.toString());
						Operator oper = comp.executePhysicalPlan();
						if(oper!=null)
						comp.processTuples(oper);
					} else {
						System.out
								.println("Select type of statement !!! still not handled");
					}

				} else {
					System.out
							.println("Not a create or select statement !!! Skipped from validation");
				}
			} catch (JSQLParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}

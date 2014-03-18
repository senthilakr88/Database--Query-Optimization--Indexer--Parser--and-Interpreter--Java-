package edu.buffalo.cse562.physicalPlan;

import java.util.ArrayList;
import java.util.List;

import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;

public class TupleStruct {

	static List<String> tupleTableMap;
	static boolean joinCondition;
	static boolean nestedCondition = false;
	
	public static boolean isNestedCondition() {
		return nestedCondition;
	}

	public static void setNestedCondition(boolean nestedCondition) {
		TupleStruct.nestedCondition = nestedCondition;
	}

	public static Boolean getJoinCondition() {
		return joinCondition;
	}

	public static void setJoinCondition(Boolean joinCondition) {
		TupleStruct.joinCondition = joinCondition;
	}

	public static void setTupleTableMap(Datum[] t) {
		int index;
		tupleTableMap = new ArrayList<String>(t.length);
		for(index = 0;index < t.length;index++) {
			Datum row = (Datum) t[index];
			Table tableName = row.getColumn().getTable();
			
			String datumColumn = row.getColumn().getColumnName().toLowerCase();
			if(tableName != null) {
				String alias = tableName.getAlias();
				if(alias !=null) {
					tupleTableMap.add(alias.toLowerCase()+"."+datumColumn);
				} else if(joinCondition) {
					tupleTableMap.add(tableName.getName()+"."+datumColumn);
				} else {
					tupleTableMap.add(datumColumn);
				}
			} else {
				tupleTableMap.add(datumColumn);
			}
			
				
		}
	}
	
	public static List<String> getTupleTableMap () {
		return tupleTableMap;
	}
	
}

package edu.buffalo.cse562.physicalPlan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

//import com.sun.org.apache.xpath.internal.operations.Lt;


public class Tuple {
	
	Map tableMap;
	
	
	public Tuple(Map tableMap){
		this.tableMap = tableMap;
	}
	
	public boolean contains(String key) {
		return tableMap.containsKey(key);
	}
	
	public Object valueOf(String key) {
		return tableMap.get(key);
	}
	
	public String toString(){
		StringBuffer output=new StringBuffer();
		Iterator iterator = tableMap.entrySet().iterator();
		
		while(iterator.hasNext()) {
			Map.Entry mapEntry = (Map.Entry) iterator.next();
			output.append(mapEntry.getValue() +"|");
		}
		return output.toString();
	}

	public Tuple combine(Tuple rt) {
		
		Map m = new HashMap();
		m.putAll(tableMap);
		m.putAll(rt.tableMap);
		return new Tuple(m);
	}
}

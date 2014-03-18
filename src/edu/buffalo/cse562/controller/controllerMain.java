package edu.buffalo.cse562.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import edu.buffalo.cse562.logger.logManager;

public class controllerMain {

	String[] input;
	List<String> sqlFiles;
	String tableDir;
	logManager lg;
	
	public controllerMain(String[] args) {
		this.input = args;
		sqlFiles = new ArrayList<String>();
		lg = new logManager();
	}
	
	public void parseInput() {
		for(int i =0; i< input.length;i++){
			if(input[i].equalsIgnoreCase("--data")) {
				tableDir = input[i+1];
				i = i+1;
			} else {
				sqlFiles.add(input[i]);
				lg.logger.log(Level.INFO, sqlFiles.toString());
			}
		}
		if(sqlFiles != null) {
			queryParser qp = new queryParser(tableDir,sqlFiles);
			qp.interpretFile();
		}
		else {
			try {
				throw new Exception("");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public String toString() {
		return "Controller has information "+tableDir+" "+sqlFiles;
	}

}

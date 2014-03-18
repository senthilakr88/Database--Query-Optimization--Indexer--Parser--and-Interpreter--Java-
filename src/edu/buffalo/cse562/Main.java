package edu.buffalo.cse562;

import java.util.logging.Level;

import edu.buffalo.cse562.controller.controllerMain;
import edu.buffalo.cse562.logger.logManager;
//import edu.buffalo.cse562.logger.logManager;

public class Main {

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		logManager lg = new logManager();
		lg.logger.log(Level.INFO, "Started Execution");
		
		controllerMain  main = new controllerMain(args);
		if(args.length > 2) {
			main.parseInput();
			lg.logger.log(Level.INFO, main.toString());
		} else {
			try {
				throw new Exception("Not enough arguments");
			} catch (Exception e) {
				
				e.printStackTrace();
				lg.logger.log(Level.SEVERE, e.getMessage());
			}
		}
		
		
	}

}

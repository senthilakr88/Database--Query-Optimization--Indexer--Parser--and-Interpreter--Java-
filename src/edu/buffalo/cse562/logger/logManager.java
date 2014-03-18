package edu.buffalo.cse562.logger;

import java.util.logging.Logger;

public class logManager {

	public Logger logger;
	
	public logManager() {
		logger = Logger.getLogger("myLogger");
		logger.setUseParentHandlers(false);
	}
	
}

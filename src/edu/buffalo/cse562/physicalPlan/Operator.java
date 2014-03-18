package edu.buffalo.cse562.physicalPlan;

public interface Operator {

	public void resetStream();
	public Datum[] readOneTuple();
	
}

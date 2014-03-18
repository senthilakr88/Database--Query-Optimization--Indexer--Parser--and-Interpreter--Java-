package edu.buffalo.cse562.physicalPlan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.sf.jsqlparser.statement.select.OrderByElement;

public class OrderByOperator {
	Operator oper;
	Datum[] t1, t2;
	List<Datum[]> listDatum;
	List<OrderByElement> elements;

	// Boolean firstEntry;

	public OrderByOperator(List<OrderByElement> elements) {
		this.elements = elements;
		this.listDatum = new ArrayList<Datum[]>();
	}

	public List<Datum[]> getListDatum() {
		return listDatum;
	}

	public void setListDatum(List<Datum[]> listDatum) {
		this.listDatum = listDatum;
	}
	
	public void addTuple(Datum[] Tuple) {
		listDatum.add(Tuple);
	}
	
	public void sort() {
		Collections.sort(listDatum, new Mysorter(elements));
	}

	public void print() {
		Iterator<Datum[]> ite = listDatum.iterator();
		
		while(ite.hasNext()) {
		Boolean first = true;
		Datum[] row = ite.next();
		if (row != null && row.length != 0) {
			for (Datum col : row) {
				if (!first)
					System.out.print("|" + col);
				else {
					System.out.print(col);
					first = false;
				}
			}
			System.out.println();
		}
		}
	}

}

package edu.buffalo.cse562.physicalPlan;

import java.util.ArrayList;
import java.util.List;

import edu.buffalo.cse562.sql.expression.evaluator.CalcTools;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Table;

public class JoinOperator implements Operator {

	Operator left;
	Operator right;
	Datum[] leftTuple;
	Boolean firstEntry;
	Expression expr;
	boolean isTupleMapPresent;
	List<Datum[]> leftBuffer;
	List<Datum[]> rightBuffer;
	Integer BufferMaxSize;
	Integer leftBufCursor, rightBufCursor;

	public JoinOperator(Operator left, Operator right, Expression expression) {
		this.left = left;
		this.right = right;
		this.expr = expression;
		this.isTupleMapPresent = true;
		this.BufferMaxSize = 1000;
		this.leftBuffer = fillLeftBuffer();
		this.rightBuffer = fillRightBuffer();
	}

	@Override
	public void resetStream() {
		right.resetStream();
	}

	public Datum[] readOneTuple() {

		Datum[] lt = null, rt = null;
		Datum[] t = null;
//		System.out.println("inside join");
		long startTime = System.nanoTime();
		do {
			//System.out.println("entering loop" + firstEntry);
			if (firstEntry) {
				lt = getLeftDatum();
				setTuple(lt);
				firstEntry = false;
			}
			lt = getTuple();
			if (lt != null) {
				//System.out.println("left is not empty");
				rt = getRightDatum();
				if (rt == null) {
					//System.out.println("right is empty");
					lt = getLeftDatum();
					if(lt==null) {
						//System.out.println("both block end");
						////System.out.println("entering left null");
						resetLeftBuf();
						//System.out.println("reset left");
						lt = getLeftDatum();
						
						rightBuffer = fillRightBuffer();
						//System.out.println("fill right");
					} else {
						resetRightBuf();
						//System.out.println("reset right");
					}
					setTuple(lt);
					rt = getRightDatum();
//					//System.out.println(lt.toString());
//					//System.out.println(rt.toString());
					if(rt == null) {
						//System.out.println("right is again empty");
						//System.out.println("fill left");
						leftBuffer = fillLeftBuffer();
						if(isBufEmpty(leftBuffer)){
//							System.out.println("both stream end");
							setTuple(null);
							return null;
						}
						right.resetStream();
						rightBuffer = fillRightBuffer();
//						//System.out.println("Fill right");
						continue;
					}
				} else {
					//System.out.println("right is not empty");
				}
				////System.out.println("combining");
				t = combine(lt, rt);

				if (t == null) {
					
					return null;
				}
				if (!evaluate(t, expr)) {
					t = null;
				}
			}  else {
				return null;
			}
//			System.out.println("in join");
		} while (t == null);
//		System.out.println("Outside join :: " + String.valueOf(System.nanoTime() - startTime));
		return t;
	}

	public Datum[] getLeftDatum() {
		Datum[] lt;
		
		if(leftBuffer == null)
			return null;
		++leftBufCursor;
		if (leftBufCursor < leftBuffer.size()) {
			lt = leftBuffer.get(leftBufCursor);
		} else {
			lt = null;
		}
		return lt;
	}

	public Datum[] getRightDatum() {
		Datum[] rt;
		
		if(rightBuffer == null) {
			//System.out.println("Datum return :: rightbuffer empty");
			return null;
		}
		++rightBufCursor;
		if (rightBufCursor < rightBuffer.size()) {
			//System.out.println("getting rightbuffer value");
			rt = rightBuffer.get(rightBufCursor);
		} else {
			rt = null;
		}
		return rt;
	}
	
	public void resetRightBuf() {
		rightBufCursor = -1;
	}
	
	public void resetLeftBuf() {
		leftBufCursor = -1;
	}

	public ArrayList<Datum[]> fillLeftBuffer() {
		//System.out.println("entering left buffer for filling");
		ArrayList<Datum[]> tempBuf = new ArrayList<Datum[]>();
		Datum[] tempDatum;
		this.firstEntry = true;
		this.leftBufCursor = -1;
		tempDatum = left.readOneTuple();
		if(tempDatum == null)
			return null;
		while (tempDatum != null) {
			tempBuf.add(tempDatum);
			//System.out.println(tempDatum);
			if(tempBuf.size() == BufferMaxSize)
				return tempBuf;
			tempDatum = left.readOneTuple();
		}
		return tempBuf;
	}

	public ArrayList<Datum[]> fillRightBuffer() {
		//System.out.println("entering right buffer for filling");
		ArrayList<Datum[]> tempBuf = new ArrayList<Datum[]>();
		Datum[] tempDatum;
		this.rightBufCursor = -1;
		tempDatum = right.readOneTuple();
		if(tempDatum == null)
			return null;
		while (tempDatum != null) {
			tempBuf.add(tempDatum);
			//System.out.println(tempDatum);
			if(tempBuf.size() == BufferMaxSize)
				return tempBuf;
			tempDatum = right.readOneTuple();
		}
		return tempBuf;
	}
	
	public boolean isBufEmpty(List<Datum[]> buffer) {
		return (buffer == null || buffer.size() == 0) ? true : false;
	}

	private void printTuple(Datum[] row) {
		Boolean first = true;
		if (row != null && row.length != 0) {
			for (Datum col : row) {
				if (!first)
					System.out.print("|" + col);
				else {
					System.out.print(col);
					first = false;
				}
			}
			//System.out.println();
		}
	}

	private boolean evaluate(Datum[] t, Expression expr2) {
		if (expr2 != null) {

			if (isTupleMapPresent) {
				TupleStruct.setTupleTableMap(t);
				isTupleMapPresent = false;
			}
			CalcTools calc = new CalcTools(t);
			expr.accept(calc);
			// //System.out.println(calc.getAccumulatorBoolean());
			return calc.getAccumulatorBoolean();
		} else {
			return true;
		}
	}

	private Datum[] combine(Datum[] lt, Datum[] rt) {
		int i = 0, j = 0;
		int len = 0;
		len += lt.length;
		len += rt.length;
		Datum[] temp = new Datum[len];
		
		for (i = 0; i < lt.length; i++) {
			temp[i] = lt[i];
			// //System.out.println(lt[i].toComString());
		}
		for (j = 0; j < rt.length; j++, i++) {
			temp[i] = rt[j];
			// //System.out.println(rt[j].toComString());
		}
		//System.out.println("Combining :: " + lt[0] + " :: " +rt[0]);
		return temp;
	}

	public Datum[] getTuple() {
		return leftTuple;
	}

	public void setTuple(Datum[] lt) {
		this.leftTuple = lt;
	}

}

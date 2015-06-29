/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xmlProcessor.DBServer.operators.twigs;

import java.io.IOException;
import java.util.ArrayList;

import xmlProcessor.DBServer.DBException;
import xmlProcessor.DBServer.operators.QueryOperator;
import xmlProcessor.DBServer.utils.QueryTuple;


public class QueryTwigStk02MJInput 
{
	
	private QueryOperator op;
	private ArrayList<QueryTuple> subSet;
	private QueryTuple nextTuple;
	private boolean consumed = false;
	private QueryTuple key;
	private int[][] spec;
	private boolean inputConsumed = false;
	
	public QueryTwigStk02MJInput(QueryOperator op, int[][] spec) {
		this.op = op;
		this.subSet = new ArrayList<QueryTuple>();
		this.spec = spec;
	}
	
	public void open() throws DBException, IOException {
		op.open();
		inputNext();
	}
	
	private void inputNext() throws DBException, IOException {
		this.nextTuple = op.next();
		if (nextTuple == null) {
			if (inputConsumed) {
				consumed = true; 
			} else {
				this.inputConsumed = true;
			}
		}
	}
	
	public boolean consumed() {
		return this.consumed ;
	}
	

	public void close() throws DBException {
		this.op.close();
	}
	
//	public QueryTuple nextTuple() throws DBException {
//		QueryTuple res = nextTuple;
//		if (!inputConsumed) {
//			inputNext();
//		} else {
//			consumed = true;
//		}
//		return res;
//	}
	
	public void advance() throws DBException, IOException {
//		System.out.println("advance");
		
		if (inputConsumed) { 
			consumed = true;
			return;
		}
		
		key = nextTuple;
		subSet.clear();
		
//		System.out.println("key = nextTuple = null ? " + (nextTuple == null? "null" : "not null"));
		
		while(!inputConsumed && compare(key, nextTuple, true ) == 0) {//internal comparation, merge join of duplicates supported
			subSet.add(nextTuple);
			inputNext();
		}
//		System.out.println("subSet size " + subSet.size());
//		for (QueryTuple t : subSet) {
//			t.print();
//		}
	}
	
	public QueryTuple key() {
		return this.key;
	}
	
//	public int compare(QueryTuple t1, QueryTuple t2, boolean internal) throws DBException {
//		for (int i = 0; i < spec.length; i++) {
//			if( spec[i] >= 0 ){
//				int compare = internal? t1.getIDForPos(i).compareTo(t2.getIDForPos(i)) : t1.getIDForPos(i).compareTo(t2.getIDForPos(spec[i]));
//				if (compare != 0) return compare;
//			}
//		}
//		return 0;
//	}

	// spec[0].length == spec[1].length == number of join positions
	// the spec[0][i]-th attribute of a tuple is to be joined with the spec[1][i]-th attribute of the other 
	public int compare(QueryTuple t1, QueryTuple t2, boolean internal) throws DBException {
		for (int i = 0; i < spec[0].length; i++) {
			if( spec[0][i] >= 0 ){
				int comp = internal? t1.getIDForPos(spec[0][i]).compareTo(t2.getIDForPos(spec[0][i])) : t1.getIDForPos(i).compareTo(t2.getIDForPos(spec[1][i]));
				if (comp != 0) return comp;
			}
		}
		return 0;
	}
	
	public ArrayList<QueryTuple> xProduct(QueryTwigStk02MJInput right) throws DBException {
		ArrayList<QueryTuple> res = new ArrayList<QueryTuple>(); 
//		System.out.println("xProduct");
		for (QueryTuple rightTuple : right.subSet) {
			rightTuple.project(spec[1]);
		}
		for (QueryTuple leftTuple : this.subSet) {
//			System.out.println("leftTuple "); leftTuple.print();
//			leftTuple.project(spec[0]); //FIXME:rightTuple.project!!!!!!!
//			System.out.println("leftTuple after project "); leftTuple.print();
			for (QueryTuple rightTuple : right.subSet) {
//				System.out.println("rightTuple "); rightTuple.print();
//				res.add(QueryTuple.mergeSorted(leftTuple, rightTuple)); //FIXME:mergeUnsorted!!!!!!!!!!
				res.add(QueryTuple.merge(leftTuple, rightTuple));
			}
		}
		
//		System.out.println("xproduct results:");
//		for (QueryTuple tuple : res) {
//			tuple.print();
//		}
		
		/*
		// we may need meta-data in the tuples to avoid this sort phase
		QueryTuple[] resArr = new QueryTuple[res.size()]; 
		resArr = res.toArray(resArr);
		Arrays.sort(resArr, new QueryTupleComparator());
		res.clear();
		for (int i = 0; i < resArr.length; i++) {
			res.add(i, resArr[i]);
		}
		*/
	
//		System.out.println("xproduct results after sorting:");
//		for (QueryTuple tuple : res) {
//			tuple.print();
//		}
		
//		System.out.println("added " + res.size()+ " tuples" );
		return res;
	}

	
//	public boolean inputConsumed() {
//		return this.inputConsumed;
//	}
	
//	public QueryTuple peek() 
//	{
//		return this.nextTuple;
//	}
		
}

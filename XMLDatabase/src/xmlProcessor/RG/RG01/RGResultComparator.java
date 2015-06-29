/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xmlProcessor.RG.RG01;

import java.util.ArrayList;
import java.util.Comparator;

import xmlProcessor.DeweyID;
import xmlProcessor.DBServer.utils.QueryTuple;

public class RGResultComparator implements Comparator<QueryTuple> {

	public int compare(QueryTuple t1, QueryTuple t2) {
		ArrayList<DeweyID> v1 = t1.getValues();
		ArrayList<DeweyID> v2 = t2.getValues();
		return v1.get(0).compareTo(v2.get(0));
	}

}

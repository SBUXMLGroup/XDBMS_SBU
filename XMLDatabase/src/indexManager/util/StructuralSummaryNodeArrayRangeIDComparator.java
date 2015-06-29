package indexManager.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.Comparator;

import indexManager.StructuralSummaryNode;
import indexManager.structuralSummaryMgr.twigStack.TwigResult;
import xmlProcessor.QTP.QTPNode;

/**
 * @author Kamyar
 *
 */
public class StructuralSummaryNodeArrayRangeIDComparator implements Comparator<TwigResult> 
{
	ArrayList<QTPNode> qNodes;
	public StructuralSummaryNodeArrayRangeIDComparator(ArrayList<QTPNode> qNodes)
	{
		this.qNodes = qNodes;
	}

	public int compare(TwigResult pathRes1, TwigResult pathRes2)
	{
//		if (pathRes1.getPathLeaf() != pathRes2.getPathLeaf())
//		{
//			System.err.println("Error in TwigStack PathResult comparison");
//		}
		ArrayList<StructuralSummaryNode> psNodeList1 = pathRes1.getRes(qNodes);
		ArrayList<StructuralSummaryNode> psNodeList2 = pathRes2.getRes(qNodes);
//		ArrayList<DeweyID> psNodeList1 = t1.getValues();
//		ArrayList<DeweyID> psNodeList2 = t2.getValues();
//	by lr on 28.03.07 - try/catch statement deleted (DeweyID.compareTo no longer throws an exception).
 		for (int i = 0; i < psNodeList1.size() && i < psNodeList2.size(); i++) 
 		{
 			int comp = psNodeList1.get(i).getRangeID().compareTo(psNodeList2.get(i).getRangeID());
			if (comp != 0 ) return comp;	
 		}
 		if (psNodeList1.size() == psNodeList2.size())	{
 			return 0;
 		} else if (psNodeList1.size() < psNodeList2.size()) {
 			return -1;
 		} else {// v1.size() > v2.size()
 			return 1;
 		}
		
	//	return psNode1.getRangeID().compareTo(psNode2.getRangeID());		
	}

}
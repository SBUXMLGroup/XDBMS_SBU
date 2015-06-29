
package xmlProcessor.RG.RG01;

import java.util.ArrayList;
import java.util.Comparator;

import indexManager.StructuralSummaryNode;
import xmlProcessor.DBServer.utils.TwinObject;
import xmlProcessor.QTP.QTPNode;
import xmlProcessor.RG.executionPlan.RGExecutionPlanTuple;

/**
 * @author Kamyar
 *
 */
public class RGExecutionPlanTupleComparator implements Comparator<RGExecutionPlanTuple> 
{
	private QTPNode[] qNodes;
	
	public RGExecutionPlanTupleComparator(QTPNode[] qNodes)
	{
		this.qNodes = qNodes;
	}
	public int compare(RGExecutionPlanTuple t1, RGExecutionPlanTuple t2) 
	{
		for (int i = 0; i < qNodes.length; i++) 
		{
			int CID1 = t1.getStructuralSummaryNode(qNodes[i]).getCID(); 
			int CID2 = t2.getStructuralSummaryNode(qNodes[i]).getCID();

			if (CID1 < CID2)
			{
				return -1; 
			}
			else if (CID1 > CID2)
			{
				return 1;
			}
		} //for (int i = 0;
		
		return 0;
	} //compare()

}

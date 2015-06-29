/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xmlProcessor.RG.RG05;

/**
 * 
 */


import java.util.Comparator;


import xmlProcessor.QTP.QTPNode;
import xmlProcessor.RG.executionPlan.RGExecutionPlanTuple;

/**
 * @author Kamyar
 *
 */
public class RGCorrelatedPlansTupleComparator implements Comparator<CorrelatedPlans> 
{
	private QTPNode[] qNodes;
	
	public RGCorrelatedPlansTupleComparator(QTPNode[] qNodes)
	{
		this.qNodes = qNodes;
	}
	public int compare(CorrelatedPlans t1, CorrelatedPlans t2) 
	{
		for (int i = 0; i < qNodes.length; i++) 
		{
			//TODO: what would happen if a correlated plan does not have all leaf nodes
			int PCR1 = t1.getPCR(qNodes[i]); 
			int PCR2 = t2.getPCR(qNodes[i]);

			if (PCR1 < PCR2)
			{
				return -1; 
			}
			else if (PCR1 > PCR2)
			{
				return 1;
			}
		} //for (int i = 0;
		
		return 0;
	} //compare()

}



package xmlProcessor.RG.executionPlan;

/**
 * 
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import xmlProcessor.QTP.QTPNode;

/**
 * @author Kamyar
 *
 */
//XTCxquery-RG-Execution-Plan: Execution Plan used for RG method !!!
public class RGExecutionPlan {
	
	private ArrayList<RGExecutionPlanTuple> tuples;
	//targetPCRs calculates the number of distinct PCRs for all
	//nodes while some of them are not really target!!!
	private HashMap<QTPNode, HashSet<Integer>> targetCIDs = null;
	
	public RGExecutionPlan()
	{
		this.tuples = new ArrayList<RGExecutionPlanTuple>();
	}
	
	public int size()
	{
		return tuples.size();
	}
	
	public void add(RGExecutionPlanTuple tuple)
	{
		this.tuples.add(tuple);
	}
	public RGExecutionPlanTuple getTuple(int i)
	{		
		return tuples.get(i);
	}

	public void setTargetCIDs(HashMap<QTPNode, HashSet<Integer>> targetCIDs)
	{
		//targetPCRs calculates the number of distinct PCRs for all
		//nodes while some of them are not really target!!!
		this.targetCIDs = targetCIDs;
	}
	
	public HashMap<QTPNode, HashSet<Integer>> getTargetCIDs()
	{
		return this.targetCIDs;
	}
	
	/**
	 * CAUTION: any changes in returned value directly apples to the RGExecutionPlan object itself 
	 * @return
	 */
	public RGExecutionPlanTuple[] getTuples()
	{
		RGExecutionPlanTuple[] res = new RGExecutionPlanTuple[tuples.size()];
		res = tuples.toArray(res);
		return res;
	}
}

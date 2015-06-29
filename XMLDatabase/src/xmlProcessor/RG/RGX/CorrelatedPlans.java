/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xmlProcessor.RG.RGX;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import xmlProcessor.DBServer.DBException;
import indexManager.StructuralSummaryNode;
import xmlProcessor.DBServer.utils.TwinObject;
import xmlProcessor.QTP.QTPNode;
import xmlProcessor.RG.executionPlan.RGExecutionPlanTuple;

public class CorrelatedPlans
{
	private ArrayList<RGExecutionPlanTuple> correlatedPlans;
	private HashMap<QTPNode, StructuralSummaryNode> targetCIDs = null;

	public CorrelatedPlans(RGExecutionPlanTuple plan) throws DBException
	{		
		this.targetCIDs = new HashMap<QTPNode, StructuralSummaryNode>();
		this.correlatedPlans = new ArrayList<RGExecutionPlanTuple>();
		this.Add(plan);
	}

	public void Add(RGExecutionPlanTuple plan) throws DBException
	{
		correlatedPlans.add(plan);
		
		//calculates PCRs used regards to QTP nodes 
		HashMap<QTPNode, StructuralSummaryNode> pcrs;
		pcrs = plan.getStructuralSummaryNode();//executionPlans.get(i).getO2().getTargetCIDs();
		Set<Entry<QTPNode, StructuralSummaryNode>> mapEntry = pcrs.entrySet();
		for (Entry<QTPNode, StructuralSummaryNode> entry : mapEntry)
		{
			QTPNode node;
			node = entry.getKey();
			if (!targetCIDs.containsKey(node))
			{
				targetCIDs.put(node, entry.getValue());
			}
			else
			{
				if (targetCIDs.get(node).getCID() != entry.getValue().getCID())
				{
					throw new DBException(this.getClass().getName() + ": plan is not correlated!!!!");
				}
			}
		}
	}
	
	public boolean IsCorrelatedWith(RGExecutionPlanTuple plan)
	{
		for (RGExecutionPlanTuple sPlan : this.correlatedPlans)
		{
			if (sPlan.isSubsetOf(plan))
			{
				return true;
			}
			if (plan.isSubsetOf(sPlan))
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean AddIfIsCorrelatedWith(RGExecutionPlanTuple plan) throws DBException
	{
		for (RGExecutionPlanTuple sPlan : this.correlatedPlans)
		{
			if (sPlan.isSubsetOf(plan)) 
			{
				this.Add(plan);				
				return true;
			}
			if (plan.isSubsetOf(sPlan))
			{
				this.Add(plan);				
				return true;				
			}
		}
		return false;
	}

//	public ArrayList<StructuralSummaryNode> getMinJoinPoints()
//	{
//		return this.minJoinPoints;
//	}
	
	public HashMap<QTPNode, StructuralSummaryNode> getTargetCIDs()
	{
		return targetCIDs;
	}
	
	public int getCID(QTPNode node)
	{
		return targetCIDs.get(node).getCID();
	}
	
	public int getLevel(QTPNode node)
	{
		return targetCIDs.get(node).getLevel();
	}

	public StructuralSummaryNode getStructuralSummaryNode(QTPNode node)
	{
		return targetCIDs.get(node);
	}
	
	public ArrayList<RGExecutionPlanTuple> getPlans()
	{
		return this.correlatedPlans;
	}
	
	public int plansNo()
	{
		return this.correlatedPlans.size();
	}
	
	public String toString()
	{
		return this.correlatedPlans.toString();
	}
}
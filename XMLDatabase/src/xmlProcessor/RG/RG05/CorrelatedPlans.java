/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xmlProcessor.RG.RG05;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import xmlProcessor.DBServer.DBException;
import xmlProcessor.QTP.QTPNode;
import xmlProcessor.RG.executionPlan.RGExecutionPlanTuple;
import indexManager.StructuralSummaryNode;

public class CorrelatedPlans
{
	private ArrayList<RGExecutionPlanTuple> correlatedPlans;
	private HashMap<QTPNode, StructuralSummaryNode> targetPCRs = null;

	public CorrelatedPlans(RGExecutionPlanTuple plan) throws DBException
	{		
		this.targetPCRs = new HashMap<QTPNode, StructuralSummaryNode>();
		this.correlatedPlans = new ArrayList<RGExecutionPlanTuple>();
		this.Add(plan);
	}

	public void Add(RGExecutionPlanTuple plan) throws DBException
	{
		correlatedPlans.add(plan);
		
		//calculates PCRs used regards to QTP nodes 
		HashMap<QTPNode, StructuralSummaryNode> cids;
		cids = plan.getStructuralSummaryNode();//executionPlans.get(i).getO2().getTargetPCRs();
		Set<Entry<QTPNode, StructuralSummaryNode>> mapEntry = cids.entrySet();
		for (Entry<QTPNode, StructuralSummaryNode> entry : mapEntry)
		{
			QTPNode node;
			node = entry.getKey();
			if (!targetPCRs.containsKey(node))
			{
				targetPCRs.put(node, entry.getValue());
			}
			else
			{
				if (targetPCRs.get(node).getCID() != entry.getValue().getCID())
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
	
	public HashMap<QTPNode, StructuralSummaryNode> getTargetPCRs()
	{
		return targetPCRs;
	}
	
	public int getPCR(QTPNode node)
	{
		return targetPCRs.get(node).getCID();
	}
	
	public int getLevel(QTPNode node)
	{
		return targetPCRs.get(node).getLevel();
	}

	public StructuralSummaryNode getStructuralSummaryNode(QTPNode node)
	{
		return targetPCRs.get(node);
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
		//return this.correlatedPlans.toString();
		StringBuilder str = new StringBuilder();
		for (QTPNode qNode : this.targetPCRs.keySet())
		{
			str.append(this.targetPCRs.get(qNode).getCID() + ":");
		}
		return str.toString();
		//return this.targetPCRs.toString();
	}
}
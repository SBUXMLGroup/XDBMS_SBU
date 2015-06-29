/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xmlProcessor.RG.RG07;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import indexManager.StructuralSummaryNode;
import xmlProcessor.DeweyID;
import xmlProcessor.QTP.QTPNode;
import xmlProcessor.QTP.QueryTreePattern;
import xmlProcessor.RG.executionPlan.RGExecutionPlanTuple;

/**
 * @author Kamyar
 *
 */
public class RGGroupedExecutionPlanTuple 
{

	private ArrayList<RGExecutionPlanTuple> executionPlanTuples;
	private HashMap<QTPNode, HashSet<Integer>> targetCIDs = null;
	private QueryTreePattern QTP;
	private ArrayList<QTPNode> leafNodes;
	private ArrayList<StructuralSummaryNode> minJoinPoints = null;
	
	public RGGroupedExecutionPlanTuple(QueryTreePattern QTP, ArrayList<RGExecutionPlanTuple> executionPlanTuples)
	{
		this.executionPlanTuples = executionPlanTuples;
		this.QTP = QTP;
		this.leafNodes = QTP.getLeaves();
		this.init();
	}
	
	private void init()
	{
		
		
		targetCIDs = new HashMap<QTPNode, HashSet<Integer>>(leafNodes.size());
		for (int i = 0; i < leafNodes.size(); i++)
		{
			targetCIDs.put(leafNodes.get(i), new HashSet<Integer>());
		}
				
		for (int i = 0; i < executionPlanTuples.size(); ++i)
		{	
			RGExecutionPlanTuple execTuple = executionPlanTuples.get(i);
			
			if (i == 0)
			{
				this.minJoinPoints = (ArrayList<StructuralSummaryNode>)execTuple.getJoinPoints().clone();
			}
			else
			{
				ArrayList<StructuralSummaryNode> joinPoints = execTuple.getJoinPoints();
				for (int j = 0; j < this.minJoinPoints.size(); j++) 
				{
					if (this.minJoinPoints.get(j).getLevel() > joinPoints.get(j).getLevel())
					{
						this.minJoinPoints.set(j, joinPoints.get(j));
					}
				}
			}
//			ArrayList<TwinObject<QTPNode,TwinObject<StructuralSummaryNode, ArrayList<StructuralSummaryNode>>>> leafNodesAnc;
//			leafNodesAnc = execTuple.getLeafNodesAnc();
			for (int j = 0; j < leafNodes.size(); ++j)
			{
				QTPNode qNode = leafNodes.get(j);
				HashSet<Integer> pcrSet = targetCIDs.get(qNode);
				pcrSet.add(execTuple.getStructuralSummaryNode(qNode).getCID());
			}
		}
	} //setTargetCIDs
	
	public HashMap<QTPNode, HashSet<Integer>> getTargetCIDs()
	{
		return this.targetCIDs;
	}
	
	public HashSet<Integer> getTargetCIDs(QTPNode qNode)
	{
		return this.targetCIDs.get(qNode);
	}

	public ArrayList<StructuralSummaryNode> getMinJoinPoints()
	{
		return this.minJoinPoints;
	}
	
	public RGExecutionPlanTuple getPlan(int i)
	{
		return this.executionPlanTuples.get(i);
	}
	
	public ArrayList<RGExecutionPlanTuple> findMatchedPlans(TwigResult res)
	{
		ArrayList<RGExecutionPlanTuple> matchedPlans = new ArrayList<RGExecutionPlanTuple>();
		
		for (int i = 0; i < this.executionPlanTuples.size(); i++) 
		{
			if (matchPlan(this.executionPlanTuples.get(i), res))
			{
				matchedPlans.add(this.executionPlanTuples.get(i));
			}
		}
		
		return matchedPlans;
	}
	
	private boolean matchPlan(RGExecutionPlanTuple plan, TwigResult res)
	{
		boolean match = true;
		QTPNode leaf = leafNodes.get(0);
		DeweyID id1 = res.getRes(leaf);
		if (id1.getCID() != plan.getStructuralSummaryNode(leaf).getCID())
		{
			return false;
		}
		DeweyID id2 = null;
		for (int i = 1; i < leafNodes.size(); ++i)
		{
			leaf = leafNodes.get(i);
			id2 = res.getRes(leaf);
			if (id2.getCID() != plan.getStructuralSummaryNode(leaf).getCID())
			{
				return false;
			}
			else
			{
				int level = plan.getJoinPoints().get(i-1).getLevel();
				if (id1.compareTo(level, id2) != 0)
				{
					return false;
				}
			}
			id1 = id2;
		}
		
		return match;
	}
	
	public int groupedPlansNo()
	{
		return executionPlanTuples.size();
	}
}

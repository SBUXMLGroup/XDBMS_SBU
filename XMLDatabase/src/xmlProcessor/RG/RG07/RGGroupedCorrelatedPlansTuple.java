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
import xmlProcessor.DBServer.utils.TwinObject;
import xmlProcessor.QTP.QTPNode;
import xmlProcessor.QTP.QueryTreePattern;
import xmlProcessor.RG.executionPlan.RGExecutionPlanTuple;

/**
 * @author Kamyar
 *
 */
public class RGGroupedCorrelatedPlansTuple 
{

	private ArrayList<CorrelatedPlans> executionPlanTuples;
	private HashMap<QTPNode, HashSet<StructuralSummaryNode>> targetPCRs = null;
	private QueryTreePattern QTP;
	private ArrayList<QTPNode> nodes;
//	private ArrayList<StructuralSummaryNode> minJoinPoints = null;
	
	public RGGroupedCorrelatedPlansTuple(QueryTreePattern QTP, ArrayList<CorrelatedPlans> executionPlanTuples)
	{
		this.executionPlanTuples = executionPlanTuples;
		this.QTP = QTP;
		this.nodes = QTP.getNodes();
		this.init();
	}
	
	public RGGroupedCorrelatedPlansTuple(ArrayList<RGGroupedCorrelatedPlansTuple> groupedexecutionPlanTuples, QueryTreePattern QTP)
	{
		this.executionPlanTuples = new ArrayList<CorrelatedPlans>();
		for (int i = 0; i < groupedexecutionPlanTuples.size(); i++)
		{
			for (int j = 0; j < groupedexecutionPlanTuples.get(i).groupedPlansNo(); j++)
			{
				this.executionPlanTuples.add(groupedexecutionPlanTuples.get(i).getPlan(j));	
			}			
		}
		
		this.QTP = QTP;
		this.nodes = QTP.getNodes();
		this.init();
	}
	private void init()
	{
		
		
		targetPCRs = new HashMap<QTPNode, HashSet<StructuralSummaryNode>>(nodes.size());
		for (int i = 0; i < nodes.size(); i++)
		{
			targetPCRs.put(nodes.get(i), new HashSet<StructuralSummaryNode>());
		}
				
		for (int i = 0; i < executionPlanTuples.size(); ++i)
		{	
			CorrelatedPlans execTuple = executionPlanTuples.get(i);
			
//			if (i == 0)
//			{
//				this.minJoinPoints = (ArrayList<StructuralSummaryNode>)execTuple.getJoinPoints().clone();
//			}
//			else
//			{
//				ArrayList<StructuralSummaryNode> joinPoints = execTuple.getJoinPoints();
//				for (int j = 0; j < this.minJoinPoints.size(); j++) 
//				{
//					if (this.minJoinPoints.get(j).getLevel() > joinPoints.get(j).getLevel())
//					{
//						this.minJoinPoints.set(j, joinPoints.get(j));
//					}
//				}
//			}
//			ArrayList<TwinObject<QTPNode,TwinObject<StructuralSummaryNode, ArrayList<StructuralSummaryNode>>>> leafNodesAnc;
//			leafNodesAnc = execTuple.getLeafNodesAnc();
			for (int j = 0; j < nodes.size(); ++j)
			{
				QTPNode qNode = nodes.get(j);
				HashSet<StructuralSummaryNode> pcrSet = targetPCRs.get(qNode);
				StructuralSummaryNode psNode = execTuple.getStructuralSummaryNode(qNode);
				if (psNode != null)
				{
					pcrSet.add(psNode);
				}
			}
		}
	} //setTargetPCRs
	
	public HashMap<QTPNode, HashSet<StructuralSummaryNode>> getTargetCIDs()
	{
		return this.targetPCRs;
	}
	
	public HashSet<StructuralSummaryNode> getTargetCIDs(QTPNode qNode)
	{
		return this.targetPCRs.get(qNode);
	}

//	public ArrayList<StructuralSummaryNode> getMinJoinPoints()
//	{
//		return this.minJoinPoints;
//	}
	
	public CorrelatedPlans getPlan(int i)
	{
		return this.executionPlanTuples.get(i);
	}
	
//	public ArrayList<CorrelatedPlans> findMatchedPlans(TwigResult res)
//	{
//		ArrayList<CorrelatedPlans> matchedPlans = new ArrayList<CorrelatedPlans>();
//		
//		for (int i = 0; i < this.executionPlanTuples.size(); i++) 
//		{
//			if (matchPlan(this.executionPlanTuples.get(i), res))
//			{
//				matchedPlans.add(this.executionPlanTuples.get(i));
//			}
//		}
//		
//		return matchedPlans;
//	}
	
//	private boolean matchPlan(CorrelatedPlans plan, TwigResult res)
//	{
//		boolean match = true;
//		QTPNode leaf = nodes.get(0);
//		DeweyID id1 = res.getRes(leaf);
//		if (id1.getPCR() != plan.getPCR(leaf))
//		{
//			return false;
//		}
//		DeweyID id2 = null;
//		for (int i = 1; i < nodes.size(); ++i)
//		{
//			leaf = nodes.get(i);
//			id2 = res.getRes(leaf);
//			if (id2.getPCR() != plan.getPCR(leaf))
//			{
//				return false;
//			}
//			else
//			{
//				int level = plan.getMinJoinPoints().get(i-1).getLevel();
//				if (id1.compareTo(level, id2) != 0)
//				{
//					return false;
//				}
//			}
//			id1 = id2;
//		}
//		
//		return match;
//	}
	
	public int groupedPlansNo()
	{
		return executionPlanTuples.size();
	}
	
	public String toString()
	{
		return this.executionPlanTuples.toString();
	}
}

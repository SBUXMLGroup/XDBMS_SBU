/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xmlProcessor.RG.RG05;


///**
// * 
// */
//package xtc.server.xmlSvc.xqueryOperators.Twigs.RG.RG02;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.HashSet;
//
//import indexManager.StructuralSummaryNode;
//import xmlProcessor.DeweyID;
//import xmlProcessor.DBServer.utils.TwinObject;
//import xmlProcessor.QTP.QTPNode;
//import xtc.server.xmlSvc.xqueryOperators.Twigs.QTP.QueryTreePattern;
//import xtc.server.xmlSvc.xqueryOperators.Twigs.RG.RG02.TwigResult;
//import xtc.server.xmlSvc.xqueryOperators.Twigs.RG.executionPlan.RGExecutionPlanTuple;
//
///**
// * @author Kamyar
// *
// */
//public class RGGroupedCorrelatedPlansTuple 
//{
//
//	private ArrayList<CorrelatedPlans> executionPlanTuples;
//	private HashMap<QTPNode, HashSet<Integer>> targetPCRs = null;
//	private QueryTreePattern QTP;
//	private ArrayList<QTPNode> leafNodes;
//	private ArrayList<PathSynopsisNode> minJoinPoints = null;
//	
//	public RGGroupedCorrelatedPlansTuple(QueryTreePattern QTP, ArrayList<CorrelatedPlans> executionPlanTuples)
//	{
//		this.executionPlanTuples = executionPlanTuples;
//		this.QTP = QTP;
//		this.leafNodes = QTP.getLeaves();
//		this.init();
//	}
//	
//	private void init()
//	{
//		
//		
//		targetPCRs = new HashMap<QTPNode, HashSet<Integer>>(leafNodes.size());
//		for (int i = 0; i < leafNodes.size(); i++)
//		{
//			targetPCRs.put(leafNodes.get(i), new HashSet<Integer>());
//		}
//				
//		for (int i = 0; i < executionPlanTuples.size(); ++i)
//		{	
//			CorrelatedPlans execTuple = executionPlanTuples.get(i);
//			
//			if (i == 0)
//			{
//				this.minJoinPoints = (ArrayList<PathSynopsisNode>)execTuple.getMinJoinPoints().clone();
//			}
//			else
//			{
//				ArrayList<PathSynopsisNode> joinPoints = execTuple.getMinJoinPoints();
//				for (int j = 0; j < this.minJoinPoints.size(); j++) 
//				{
//					if (this.minJoinPoints.get(j).getLevel() > joinPoints.get(j).getLevel())
//					{
//						this.minJoinPoints.set(j, joinPoints.get(j));
//					}
//				}
//			}
////			ArrayList<TwinObject<QTPNode,TwinObject<PathSynopsisNode, ArrayList<PathSynopsisNode>>>> leafNodesAnc;
////			leafNodesAnc = execTuple.getLeafNodesAnc();
//			for (int j = 0; j < leafNodes.size(); ++j)
//			{
//				QTPNode qNode = leafNodes.get(j);
//				HashSet<Integer> pcrSet = targetPCRs.get(qNode);
//				pcrSet.add(execTuple.getPCR(qNode));
//			}
//		}
//	} //setTargetPCRs
//	
//	public HashMap<QTPNode, HashSet<Integer>> getTargetPCRs()
//	{
//		return this.targetPCRs;
//	}
//	
//	public HashSet<Integer> getTargetPCRs(QTPNode qNode)
//	{
//		return this.targetPCRs.get(qNode);
//	}
//
//	public ArrayList<PathSynopsisNode> getMinJoinPoints()
//	{
//		return this.minJoinPoints;
//	}
//	
//	public CorrelatedPlans getPlan(int i)
//	{
//		return this.executionPlanTuples.get(i);
//	}
//	
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
//	
//	private boolean matchPlan(CorrelatedPlans plan, TwigResult res)
//	{
//		boolean match = true;
//		QTPNode leaf = leafNodes.get(0);
//		DeweyID id1 = res.getRes(leaf);
//		if (id1.getPCR() != plan.getPCR(leaf))
//		{
//			return false;
//		}
//		DeweyID id2 = null;
//		for (int i = 1; i < leafNodes.size(); ++i)
//		{
//			leaf = leafNodes.get(i);
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
//	
//	public int groupedPlansNo()
//	{
//		return executionPlanTuples.size();
//	}
//}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xmlProcessor.RG.RG05;

///**
// * 
// */
//package xtc.server.xmlSvc.xqueryOperators.Twigs.RG.RG04;
//
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.LinkedList;
//import java.util.Queue;
//import java.util.Set;
//
//import xmlProcessor.DBServer.DBException;
//import indexManager.StructuralSummaryNode;
//import xtc.server.xmlSvc.XTCxqueryContext;
//import xmlProcessor.DBServer.operators.twigs.QueryStatistics;
//import xmlProcessor.QTP.QTPNode;
//import xtc.server.xmlSvc.xqueryOperators.Twigs.QTP.QueryTreePattern;
//import xtc.server.xmlSvc.xqueryOperators.Twigs.RG.executionPlan.RGExecutionPlanTuple;
////import xtc.server.xmlSvc.xqueryOperators.Twigs.RG.executionPlan.RGGroupedExecutionPlanTuple;
//
///**
// * @author Kamyar
// *
// */
//public class TwigJoinMultiplexerOp implements QueryStatistics
//{
//	private QueryTreePattern QTP;
//	private CorrelatedPlans correlatedPlans;
//	private ArrayList<QTPNode> leafNodes;
//	private ArrayList<TwigJoinOp> twigJoinOps;
//	private TwigResult head;
//	private boolean finished;
//	private Queue<TwigResult> resultQueue;
//	
//	public TwigJoinMultiplexerOp(QueryTreePattern QTP, CorrelatedPlans correlatedPlans, XTCxqueryContext context, int idxNo) throws DBException
//	{
//		this.correlatedPlans = correlatedPlans;
//		this.QTP = QTP;
//		this.leafNodes = QTP.getLeaves();
//		
//		this.twigJoinOps = new ArrayList<TwigJoinOp>();
//		for (RGExecutionPlanTuple plan : this.correlatedPlans.getPlans())
//		{
//			this.twigJoinOps.add(getTwigJoinOP(plan, context, idxNo));
//		}
//			
//		
//		this.head = null;
//		this.resultQueue = new LinkedList<TwigResult>();
//		this.finished = false;
//	}
//	public void open() throws DBException 
//	{
//		for (int i = 0; i < this.correlatedPlans.getPlans().size(); ++i) 
//		{			
//			this.twigJoinOps.get(i).open();
//		}			
//		this.MoveNext();
//	}
//
//	public void close() throws DBException 
//	{
//		//TODO: Statistics should be calculated here
//		for (int i = 0; i < this.correlatedPlans.getPlans().size(); ++i) 
//		{			
//			this.twigJoinOps.get(i).close();
//		}			
//	}
//	
//	public boolean isFinished()
//	{
//		return finished;
//	}
//	
//	public TwigResult getHead()
//	{
//		return head;
//	}
//	
//	/**
//	 * Calculates the next result 
//	 **/
//	public void MoveNext() throws DBException
//	{
//		if (!resultQueue.isEmpty())
//		{
//			this.head = resultQueue.poll();
//			return;
//		}
//		
//		TwigResult res = this.produceNextRes();
//		
//		if (res == null)
//		{
//			this.finished = true;
//			return;
//		}
//		
//		this.head = res;
//		return;
//		
//		
//		
////		if (this.correlatedPlans.plansNo() == 1)
////		{
////			//this.head = this.produceResults(this.tuple.getPlan(0), res);
////			this.head = res.completeRes(this.correlatedPlans.getPlan(0), leafNodes, false);
////			return ;
////		}
////		else
////		{
////			ArrayList<RGExecutionPlanTuple> matchedPlans = this.findMatchedPlans(res);
////			if (matchedPlans.size() == 0)
////			{
////				MoveNext();
////				return;
////			}
////			
////			TwigResult[] results = this.produceResults(matchedPlans, res);
////			
////			this.head = results[0];
////			if (results.length == 1)
////			{			
////				return;
////			}
////			else
////			{
////				for (int i = 1; i < results.length; i++) 
////				{
////					resultQueue.offer(results[i]);	
////				}			
////			}
////		}
//	}
//
//	/**
//	 * Calculates next res based on logical operators of original QTP for correlated plans 
//	 * @return
//	 */
//	private TwigResult produceNextRes()
//	{
//		
//	}
//	
////	private ArrayList<RGExecutionPlanTuple> findMatchedPlans(TwigResult res)
////	{
////		return this.plan.findMatchedPlans(res);
////	}
//	
//	private TwigResult[] produceResults(ArrayList<RGExecutionPlanTuple> matchedPlans, TwigResult res)
//	{
//		TwigResult[] results = new TwigResult[matchedPlans.size()];
//		for (int i = 0; i < results.length; i++) 
//		{			
//			results[i] = res.completeRes(matchedPlans.get(i), leafNodes, true);
//		}
//		
//		return results;
//	}
//	
//	private TwigJoinOp getTwigJoinOP(RGExecutionPlanTuple tuple, XTCxqueryContext context, int idxNo) throws DBException
//	{
////		//ArrayList<PathSynopsisNode> leafNodes= tuple.getleafNodes(); //nodes of Pathsynopsis related to the leaves of the QTP
////		ArrayList<PathSynopsisNode> joinPoints = tuple.getJoinPoints();			
////
////		TwigInput in1 = new InputWrapper(leafNodes.get(0), tuple.getPathSynopsisNode(leafNodes.get(0)), tuple.getAnc(leafNodes.get(0)));
////		
////		if (leafNodes.size() == 1)
////		{
////			TwigJoinOp twigJoinOp = new TwigJoinOp(in1);
////			twigJoinOp.open();
////			return twigJoinOp;
////		}
////		
////		TwigInput in2 = new InputWrapper(leafNodes.get(1), tuple.getPathSynopsisNode(leafNodes.get(1)), tuple.getAnc(leafNodes.get(1)));
////		
////		TwigJoinOp twigJoinOp = new TwigJoinOp(in1, in2, joinPoints.get(0).getLevel()); 
////		for(int i=2; i < leafNodes.size(); ++i)
////		{
////			TwigInput in = new InputWrapper(leafNodes.get(i), tuple.getPathSynopsisNode(leafNodes.get(i)), tuple.getAnc(leafNodes.get(i)));			
////			twigJoinOp = new TwigJoinOp(twigJoinOp, in, joinPoints.get(i-1).getLevel());
////		}
////		twigJoinOp.open(); 
////		return twigJoinOp;
//		
//		//ArrayList<PathSynopsisNode> leafNodes= tuple.getleafNodes(); //nodes of Pathsynopsis related to the leaves of the QTP
//		ArrayList<PathSynopsisNode> joinPoints = tuple.getJoinPoints();			
//
//		TwigInput in1 = new InputWrapper(QTP, leafNodes.get(0), tuple.getPathSynopsisNode(leafNodes.get(0)), context, idxNo);
//
//		if (leafNodes.size() == 1)
//		{
//			TwigJoinOp twigJoinOp = new TwigJoinOp(in1);
//			return twigJoinOp;
//		}
//
//		TwigInput in2 = new InputWrapper(QTP, leafNodes.get(1), tuple.getPathSynopsisNode(leafNodes.get(1)), context, idxNo);
//		
//		TwigJoinOp twigJoinOp = new TwigJoinOp(in1, in2, joinPoints.get(0).getLevel()); 
//		for(int i=2; i < leafNodes.size(); ++i)
//		{
//			TwigInput in = new InputWrapper(QTP, leafNodes.get(i), tuple.getPathSynopsisNode(leafNodes.get(i)), context, idxNo);			
//			twigJoinOp = new TwigJoinOp(twigJoinOp, in, joinPoints.get(i-1).getLevel());
//		}
//		
//		return twigJoinOp;		
//	} //getTwigJoinOP()
//
//	public long getNumberOfReadElements()
//	{
//		//TODO:
//	//	return this.twigJoinOp.getNumberOfReadElements();
//		return 0;
//	}
//	
//	public long getIOTime()
//	{
//		//TODO:
//	//	return this.twigJoinOp.getIOTime();
//		return 0;
//	}
//
//}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xmlProcessor.RG.RG01;

/**
 * 
 */
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import xmlProcessor.DBServer.DBException;
import indexManager.StructuralSummaryNode;
import java.io.IOException;
//import xtc.server.xmlSvc.XTCxqueryContext;
import xmlProcessor.DBServer.operators.twigs.QueryStatistics;;
import xmlProcessor.QTP.QTPNode;
import xmlProcessor.QTP.QueryTreePattern;
import xmlProcessor.RG.executionPlan.RGExecutionPlanTuple;
//shouldn be in executionPlanTuple:?!
import xmlProcessor.RG.RG01.RGGroupedExecutionPlanTuple;

/**
 * @author Kamyar
 *
 */
public class TwigJoinMultiplexerOp implements QueryStatistics
{
	private QueryTreePattern QTP;
	private RGGroupedExecutionPlanTuple tuple;
	private ArrayList<QTPNode> leafNodes;
	private TwigJoinOp twigJoinOp;
	private TwigResult head;
	private boolean finished;
	private Queue<TwigResult> resultQueue;
	
	public TwigJoinMultiplexerOp(QueryTreePattern QTP, RGGroupedExecutionPlanTuple tuple, String indexName) throws DBException
	{
		this.tuple = tuple;
		this.QTP = QTP;
		this.leafNodes = QTP.getLeaves();
		this.twigJoinOp = getTwigJoinOP(tuple,indexName);
		this.head = null;
		this.resultQueue = new LinkedList<TwigResult>();
		this.finished = false;
	}
	public void open() throws DBException, IOException 
	{
		twigJoinOp.open();
		this.MoveNext();
	}

	public void close() throws DBException 
	{
		twigJoinOp.close();
	}
	
	public boolean isFinished()
	{
		return finished;
	}
	
	public TwigResult getHead()
	{
		return head;
	}
	
	public void MoveNext() throws DBException, IOException
	{
		if (!resultQueue.isEmpty())
		{
			this.head = resultQueue.poll();
			return;
		}
		
		TwigResult res = twigJoinOp.next();
                //Here one result is ready
		if (res == null)
		{
			this.finished = true;
			return;
		}
		
		if (this.tuple.groupedPlansNo() == 1)
		{
			//this.head = this.produceResults(this.tuple.getPlan(0), res);
			this.head = res.completeRes(this.tuple.getPlan(0), leafNodes, false);
			return ;
		}
		else
		{
			ArrayList<RGExecutionPlanTuple> matchedPlans = this.findMatchedPlans(res);
			if (matchedPlans.size() == 0)
			{
				MoveNext();
				return;
			}
			
			TwigResult[] results = this.produceResults(matchedPlans, res);
			//FIXME! what would happen if all results are false positive
			this.head = results[0];
			if (results.length == 1)
			{			
				return;
			}
			else
			{
				for (int i = 1; i < results.length; i++) 
				{
					resultQueue.offer(results[i]);	
				}			
			}
		}
	}

	private ArrayList<RGExecutionPlanTuple> findMatchedPlans(TwigResult res)
	{
		return this.tuple.findMatchedPlans(res);
	}
	
	private TwigResult[] produceResults(ArrayList<RGExecutionPlanTuple> matchedPlans, TwigResult res)
	{
		TwigResult[] results = new TwigResult[matchedPlans.size()];
		for (int i = 0; i < results.length; i++) 
		{			
			results[i] = res.completeRes(matchedPlans.get(i), leafNodes, true);
		}
		
		return results;
	}
	
	private TwigJoinOp getTwigJoinOP(RGGroupedExecutionPlanTuple tuple,String indexName) throws DBException
	{
		//ArrayList<StructuralSummaryNode> leafNodes= tuple.getleafNodes(); //nodes of Pathsynopsis related to the leaves of the QTP
		ArrayList<StructuralSummaryNode> joinPoints = tuple.getMinJoinPoints();			

		TwigInput in1 = new InputWrapper(QTP, leafNodes.get(0), tuple.getTargetCIDs(leafNodes.get(0)),indexName);

		if (leafNodes.size() == 1)
		{
			TwigJoinOp twigJoinOp = new TwigJoinOp(in1);
			return twigJoinOp;
		}

		TwigInput in2 = new InputWrapper(QTP, leafNodes.get(1), tuple.getTargetCIDs(leafNodes.get(1)),indexName);
		
		TwigJoinOp twigJoinOp = new TwigJoinOp(in1, in2, joinPoints.get(0).getLevel()); 
		for(int i=2; i < leafNodes.size(); ++i)
		{
			TwigInput in = new InputWrapper(QTP, leafNodes.get(i), tuple.getTargetCIDs(leafNodes.get(i)),indexName);			
			twigJoinOp = new TwigJoinOp(twigJoinOp, in, joinPoints.get(i-1).getLevel());
		}
		
		return twigJoinOp;		
	} //getTwigJoinOP()

	public long getNumberOfReadElements()
	{
		return this.twigJoinOp.getNumberOfReadElements();
	}
	
	public long getIOTime()
	{
		return this.twigJoinOp.getIOTime();
	}

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xmlProcessor.RG.RG07;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

import xmlProcessor.DBServer.DBException;
import xmlProcessor.DBServer.operators.twigs.QueryStatistics;
import xmlProcessor.QTP.QTPNode;
import xmlProcessor.QTP.QueryTreePattern;
import xmlProcessor.DBServer.operators.twigs.QueryRGExtendedStatistics;
import xmlProcessor.DBServer.utils.QueryTuple;

/**
 *  
 * @author Kamyar
 *
 */
public class RGResultSet implements QueryStatistics, QueryRGExtendedStatistics
{
	private RG aRG;
	private int pos = 0;
	private TwigResult head;
	//private QTPNode extractionPoint;
	private ArrayList<QTPNode> outputNodes;
	private boolean isANDQTP;
	private QueryTreePattern QTP;
	//private Queue<QueryTuple> resultQueue; 
	/**
	 * This constructor will be used when docOrdered is false
	 * @param aRG
	 */
	public RGResultSet(RG aRG) throws DBException, IOException, InterruptedException
	{
		this.aRG = aRG;
		//this.extractionPoint = aRG.getExtractionPoint();
		this.head = aRG.next();
		this.QTP = aRG.getQTP();
		this.isANDQTP = this.QTP.isANDQTP();
		if (!this.isANDQTP)
		{
			this.outputNodes = this.QTP.getOutputNodes();
		}
		//this.resultQueue = new LinkedList<QueryTuple>();
	}
	

	/**
	 * returns only IDs which are related to the QTPNodes.
	 * returned IDs are ordered regard to the order of QTPNode(s) in QTPNodes  
	 * @param QTPNodes - ArrayList of QTPNode(s) which their related ID should be returned
	 * @return
	 */
	public QueryTuple next(ArrayList<QTPNode> QTPNodes) throws DBException, IOException, InterruptedException
	{
		if (this.head == null) return null;
		
		TwigResult nextRes = aRG.next();
//		while (nextRes != null && nextRes.getRes(extractionPoint).compareTo(this.head.getRes(extractionPoint)) == 0)
//		{
//			this.head.concat(nextRes);
//			nextRes = aRG.next();
//		}
		if (!isANDQTP)
		{
			while (nextRes != null && hasEqualOutputNodes(nextRes, this.head))
			{
				nextRes = aRG.next();
				
			}
		}
		
		TwigResult retRes = this.head;
		this.head = nextRes; 
		//System.out.println(res.getRes(QTPNodes).toString());

		return  retRes.getRes(QTPNodes);

	}
	
	private boolean hasEqualOutputNodes(TwigResult res1, TwigResult res2)
	{
		for (QTPNode node : this.outputNodes)
		{
			if (res1.getRes(node) == null)
			{
				if (res2.getRes(node) != null)
				{
					return false;
				}
			}
			else
			{
				if (res2.getRes(node) == null)
				{
					return false;
				}
				else
				{
					if (res1.getRes(node).compareTo(res2.getRes(node)) != 0)
					{
						return false;	
					}
				}
			}
		}
		return true;
	}
	
	public void close() throws DBException
	{
		aRG.close();
	}
	
	public long getIOTime()
	{
		return aRG.getIOTime();
	}
	
	public long getNumberOfReadElements()
	{
		return aRG.getNumberOfReadElements();
	}

	public long getNumberOfExecutionPlans()
	{
		return aRG.getNumberOfExecutionPlans();
	}
	
	public long getNumberOfGroupedExecutionPlans()
	{
		return aRG.getNumberOfGroupedExecutionPlans();
	}

	public String getExecutionMode()
	{
		return aRG.getExecutionMode();
	}

	
//	void setRes(TwigResult res)
//	{
//		resultQueue.add(tuple);
//	}
	
}

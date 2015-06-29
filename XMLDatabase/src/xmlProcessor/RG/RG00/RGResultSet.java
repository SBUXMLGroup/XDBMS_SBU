package xmlProcessor.RG.RG00;
/**
 * 
 */
//package xtc.server.xmlSvc.xqueryOperators.Twigs.RG.RG00;

import java.io.IOException;
import java.util.ArrayList;

import xmlProcessor.DBServer.DBException;
//import xmlProcessor.DBServer.operators.twigs.QueryStatistics;;
import xmlProcessor.QTP.QTPNode;
//import xmlProcessor.DBServer.operators.twigs.QueryRGExtendedStatistics;
import xmlProcessor.DBServer.utils.QueryTuple;

/**
 *  
 * @author Kamyar
 *
 */
public class RGResultSet //implements QueryStatistics, XTCxqueryRGExtendedStatistics
{
	private RG aRG;
	private int pos = 0;
	private boolean online; //means if results will be fetched when next is called or stored in object creation 
	//private Queue<QueryTuple> resultQueue; 
	private ArrayList<TwigResult> result;
	/**
	 * This constructor will be used when docOrdered is false
	 * @param aRG
	 */
	public RGResultSet(RG aRG)
	{
		this.aRG = aRG;
		this.online = true;
		//this.resultQueue = new LinkedList<QueryTuple>();
	}
	
	public RGResultSet(ArrayList<TwigResult> result)
	{
		this.result = result;
		this.online = false;
		this.pos = 0;
	}

	/**
	 * returns only IDs which are related to the QTPNodes.
	 * returned IDs are ordered regard to the order of QTPNode(s) in QTPNodes  
	 * @param QTPNodes - ArrayList of QTPNode(s) which their related ID should be returned
	 * @return
	 */
	public QueryTuple next(ArrayList<QTPNode> QTPNodes) throws DBException, IOException
	{
//		if(!resultQueue.isEmpty())
//		{
//			return resultQueue.poll();		
//		}
		if (online)
		{
			TwigResult res = aRG.next(); 
			//System.out.println(res.getRes(QTPNodes).toString());
			if (res == null) return null; 

			return res.getRes(QTPNodes);
		}
		else
		{
			if (pos < result.size())
			{
				return result.get(pos++).getRes(QTPNodes);
			}			
		}
		
		return null;
//		return resultQueue.poll();
	}
	
	public long getIOTime()
	{
		if (online)
		{
			return aRG.getIOTime();
		}
		
		return 0;
	}
	
	public long getNumberOfReadElements()
	{
		if (online)
		{
			return aRG.getNumberOfReadElements();
		}
		
		return 0;
	}

	public long getNumberOfExecutionPlans()
	{
		if (online)
		{
			return aRG.getNumberOfExecutionPlans();
		}
		
		return 0;
	}
	
	public long getNumberOfGroupedExecutionPlans()
	{
		if (online)
		{
			return aRG.getNumberOfGroupedExecutionPlans();
		}
		
		return 0;
	}

	public String getExecutionMode()
	{
		if (online)
		{
			return aRG.getExecutionMode();
		}
		
		return "N/A";
	}

//	void setRes(TwigResult res)
//	{
//		resultQueue.add(tuple);
//	}
	
}

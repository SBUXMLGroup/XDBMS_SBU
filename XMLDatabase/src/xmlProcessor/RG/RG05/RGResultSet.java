/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xmlProcessor.RG.RG05;


import java.io.IOException;
import java.util.ArrayList;

import xmlProcessor.DBServer.DBException;
import xmlProcessor.DBServer.operators.twigs.QueryStatistics;
import xmlProcessor.QTP.QTPNode;
import xmlProcessor.QTP.QueryTreePattern;
import xmlProcessor.QTP.QueryTreePattern;
import xmlProcessor.DBServer.operators.twigs.QueryRGExtendedStatistics;
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
	private QTPNode extractionPoint;
	private QueryTreePattern QTP;
	private ArrayList<QTPNode> outputNodes;
	private boolean isANDQTP;
	//private Queue<QueryTuple> resultQueue; 
	/**
	 * This constructor will be used when docOrdered is false
	 * @param aRG
	 */
	public RGResultSet(RG aRG) throws DBException, IOException
	{
		this.aRG = aRG;
		this.extractionPoint = aRG.getExtractionPoint();
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
	public QueryTuple next(ArrayList<QTPNode> QTPNodes) throws DBException, IOException
	{
		
		if (this.head == null) return null;
		
		TwigResult excludableRes = this.head;
		TwigResult retRes = this.head;
		boolean allNOTDummy = !this.head.isDummy();
		TwigResult nextRes = aRG.next();

		if (this.isANDQTP)
		{
			this.head = nextRes;
			return  retRes.getRes(QTPNodes);
		}

		
		
		if (retRes.isDummy() && nextRes == null)
		{
			return null;
		}
		if (!retRes.isDummy() && nextRes == null)
		{
			this.head = nextRes;
			return retRes.getRes(QTPNodes);
		}
		//one match but may be dummy
		while (!hasEqualOutputNodes(nextRes, excludableRes))
		{
			if (!retRes.isDummy())
			{
				this.head = nextRes;
				return  retRes.getRes(QTPNodes);
			}
			else
			{
				this.head = nextRes;
				excludableRes = this.head;
				retRes = this.head;
				allNOTDummy = !this.head.isDummy();
				nextRes = aRG.next();
				if (retRes.isDummy() && nextRes == null)
				{
					return null;
				}
				if (!retRes.isDummy() && nextRes == null)
				{
					this.head = nextRes;
					return retRes.getRes(QTPNodes);
				}
			}
		}
		while (nextRes != null && hasEqualOutputNodes(nextRes, excludableRes))
		{
			excludableRes = nextRes;
			allNOTDummy = allNOTDummy && !nextRes.isDummy();
			nextRes = aRG.next();
			if (nextRes == null)
			{
				if (allNOTDummy)
				{
					//if more than two results are equal in outputnodes and no one are dummy distinct of them is result
					//therfore excludableRes have not to be excluded
					this.head = null; 
					return excludableRes.getRes(QTPNodes); 
				}
				else
				{
					return null;
				}
			}
			while (hasEqualOutputNodes(nextRes, excludableRes))
			{
				allNOTDummy = allNOTDummy & !nextRes.isDummy();
				nextRes = aRG.next();	
				if (nextRes == null)
				{
					if (allNOTDummy)
					{
						//if more than two results are equal in outputnodes and no one are dummy distinct of them is result
						//therfore excludableRes have not to be excluded
						this.head = null; 
						return excludableRes.getRes(QTPNodes); 
					}
					else
					{
						return null;
					}
				}
			}	
			this.head = nextRes; //outer node is because it is possible to have two different removable result exactly after the other
			if (allNOTDummy)
			{
				//if more than two results are equal in outputnodes and no one are dummy distinct of them is result
				//therfore excludableRes have not to be excluded
				return excludableRes.getRes(QTPNodes); 
			}
			excludableRes = this.head;
			allNOTDummy = !this.head.isDummy();
			retRes = this.head;
			
			nextRes = aRG.next();
			if (retRes.isDummy() && nextRes == null)
			{
				return null;
			}
			//one match but may be dummy
			while (!hasEqualOutputNodes(nextRes, excludableRes))
			{
				if (!retRes.isDummy())
				{
					this.head = nextRes;
					return  retRes.getRes(QTPNodes);
				}
				else
				{
					this.head = nextRes;
					excludableRes = this.head;
					retRes = this.head;
					allNOTDummy = !this.head.isDummy();
					nextRes = aRG.next();
					if (retRes.isDummy() && nextRes == null)
					{
						return null;
					}
					if (!retRes.isDummy() && nextRes == null)
					{
						this.head = nextRes;
						return retRes.getRes(QTPNodes);
					}
				}
			}

		}
		
		//this point may be unreacable
		if (retRes.isDummy() && nextRes == null)
		{
			return null;
		}
		else if (retRes.isDummy())
		{
			this.head = nextRes; //= null
			
			return next(QTPNodes);
		}
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

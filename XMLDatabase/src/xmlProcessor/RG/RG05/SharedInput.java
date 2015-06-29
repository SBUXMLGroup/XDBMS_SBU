/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xmlProcessor.RG.RG05;

import java.util.ArrayList;
import java.util.LinkedList;

import xmlProcessor.DBServer.DBException;
import xmlProcessor.DeweyID;
//import xtc.server.xmlSvc.XTCxqueryContext;
//import xtc.server.xmlSvc.xqueryOperators.PathIdxBased.XTCxqueryPCRScanOp;
import indexManager.IndexManager;
import indexManager.ElementIndex;
import indexManager.Iterators.ElementIterator;
import java.io.IOException;
import xmlProcessor.DBServer.operators.twigs.QueryStatistics;
import xmlProcessor.QTP.QTPNode;
import xmlProcessor.QTP.QTPNodeConstraint;
import xmlProcessor.DBServer.utils.QueryTuple;

public class SharedInput implements QueryStatistics
{
	private QTPNode qNode;
	private int CID;
	//private XTCxqueryContext context;
        private String indexName;
        private ElementIndex input;
        private IndexManager indxMgr;
	private int idxNo;
	private ElementIterator inputStream;
	//private int minLevel;//Shows the level that Id of nodes having same prefix up to that level
	private ArrayList<LinkedList<QueryTuple>> inputWrappers;
	private boolean isOpen = false;

	public SharedInput(QTPNode qNode, int PCR,String indexName)
	{
		this.qNode = qNode;
		this.CID = PCR;
		//this.context = context;
		//this.idxNo = idxNo;
                this.indexName=indexName;
		//this.minLevel = minLevel;
		this.inputStream = null;
                indxMgr=IndexManager.Instance;
                this.inputWrappers = new ArrayList<LinkedList<QueryTuple>>();
	}
	
	public void open() throws DBException, IOException 
	{
		QTPNodeConstraint constraint;
		if (isOpen)
		{
			return;
		}
		input=indxMgr.openElementIndex(indexName);
		inputStream =input.getStream(CID);
                        //new XTCxqueryPCRScanOp(context, idxNo, qNode, CID, qNode.getName());
		inputStream.open();
		this.isOpen = true;
		
	}

	public int register(InputWrapper inputWrapper)
	{
		LinkedList<QueryTuple> queue = new LinkedList<QueryTuple>();
		
		this.inputWrappers.add(queue);
		
		return this.inputWrappers.size() - 1;
	}
	
	public DeweyID next(int index) throws DBException, IOException
	{
		LinkedList<QueryTuple> queue = this.inputWrappers.get(index);
//		if (queue == null)
//		{
//			throw new DBException(this.getClass().getName() + "::next FATAL ERROR");
//		}
		if (!queue.isEmpty())
		{
			return queue.poll().getIDForPos(0);
		}
		else
		{
			QueryTuple tuple =null;
                        DeweyID nexDeweyID=inputStream.next();
                        if(nexDeweyID!=null)
                          tuple=new QueryTuple(nexDeweyID);
			if (tuple == null)
			{
				return null;
			}
			for (int i = 0; i < this.inputWrappers.size(); i++)
			{
				this.inputWrappers.get(i).add(tuple);
			}
			return queue.poll().getIDForPos(0);
		}
	}
	
	public long getNumberOfReadElements()
	{
		return inputStream.getNumberOfReadElements();
	}
	
	public long getIOTime()
	{
		return inputStream.getIOTime();
	}

}

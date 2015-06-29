
package xmlProcessor.RG.RG06;


import java.util.ArrayList;
import java.util.LinkedList;


import xmlProcessor.DBServer.DBException;
import xmlProcessor.DeweyID;
//import xtc.server.xmlSvc.XTCxqueryContext;
//import xtc.server.xmlSvc.xqueryOperators.PathIdxBased.ElementIterator;
import indexManager.ElementIndex;
import indexManager.Iterators.ElementIterator;
import indexManager.IndexManager;
import java.io.IOException;
import xmlProcessor.DBServer.operators.twigs.QueryStatistics;
import xmlProcessor.QTP.QTPNode;
import xmlProcessor.QTP.QTPNodeConstraint;
import xmlProcessor.DBServer.utils.QueryTuple;
import statistics.Monitor;
public class SharedInput implements QueryStatistics
{
	private QTPNode qNode;
	private int CID;
        private String indexName;
	//private XTCxqueryContext context;
	//private int idxNo;
        private ElementIndex input;
	private ElementIterator inputStream;
        private IndexManager indxMgr;
	//private int minLevel;//Shows the level that Id of nodes having same prefix up to that level
	private ArrayList<LinkedList<QueryTuple>> inputWrappers;
	private boolean isOpen = false;
        private Monitor monitor=Monitor.getInstance();
	public SharedInput(QTPNode qNode, int CID,String indexName)
	{
		this.qNode = qNode;
		this.CID = CID;
//		this.context = context;
//		this.idxNo = idxNo;
		//this.minLevel = minLevel;
                this.indexName=indexName;
		this.inputStream = null;
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
		// = new ElementIterator( qNode, CID, qNode.getName());
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
                        DeweyID nextDID=inputStream.next();
                        monitor.addElementsRead();
                        if(nextDID!=null)
                            tuple=new QueryTuple(nextDID);
                                    
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

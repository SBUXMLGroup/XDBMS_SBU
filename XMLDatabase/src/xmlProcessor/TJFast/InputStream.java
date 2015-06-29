

package xmlProcessor.TJFast;

import java.util.ArrayList;
import java.util.Set;

import xmlProcessor.DBServer.DBException;

//import xtc.server.accessSvc.XTClocator;

import indexManager.StructuralSummaryManager; 
import indexManager.StructuralSummaryManager01;
//import xtc.server.metaData.vocabulary.DictionaryMgr;
import xmlProcessor.DeweyID;
//import xtc.server.taSvc.XTCtransaction;
//import xtc.server.xmlSvc.XTCxqueryContext;
//import xtc.server.xmlSvc.xqueryOperators.PathIdxBased.ElementIterator;
import indexManager.Iterators.ElementIterator;
import indexManager.IndexManager;
import indexManager.ElementIndex;
import java.io.IOException;
import xmlProcessor.DBServer.operators.twigs.QueryStatistics;
import xmlProcessor.QTP.QTPNode;
import xmlProcessor.DBServer.utils.QueryTuple;

public class InputStream implements QueryStatistics
{
		
	private QTPNode qNode;
	private ElementIterator  input;
        private String indexName;
        private IndexManager indxMgr;
        private ElementIndex inputIndx;
        private DeweyID streamHead;
	private boolean streamConsumed;
	private Set<Integer> CIDs;
	private long IOTime = 0;
	private long numberOfReadElements = 0;
	
	/**
	 * Create a new open stream
	 *  @param name
	 */
	public InputStream(QTPNode qNode,String indexName) throws DBException, IOException
	{
		StructuralSummaryManager ssMgr =new StructuralSummaryManager01();
                       // context.getMetaDataMgr().getStructuralSummaryManager();
		//XTCtransaction transaction = context.getTransaction();
		//DictionaryMgr dictionaryMgr = context.getMetaDataMgr().getDictionaryMgr();
		//XTClocator doc = context.getLocator();				
		this.indexName=indexName;
//		SimplePathExpr pExpr = new SimplePathExpr(qNode.getPath());
//		String path = pExpr.getPathAsString(transaction, doc.getID(), dictionaryMgr);
		
		this.CIDs = ssMgr.getPCRsForPath(indexName, qNode);
		this.qNode = qNode;		
				
		//System.err.println("InputStream constructor in package [xtc.server.xmlSvc.xqueryOperators.Twigs.TJFast]: SPLID SHOULD BE USED");
		this.indxMgr=IndexManager.Instance;
                this.inputIndx=indxMgr.openElementIndex(indexName);
                this.input = inputIndx.getStream(qNode, qNode.getName());
                        //new ElementIterator(qNode, qNode.getName(),);
		this.input.open();
		advanceStream();				
	}
	
	public DeweyID getStreamHead()
	{
		return streamHead;
	}
	
	public void advanceStream() throws DBException, IOException
	{
		//QueryTuple tuple;
                DeweyID tuple;
		tuple = input.next();
		if (tuple == null)
		{
			this.streamHead = null;
			this.streamConsumed = true;			
		}
		else
		{			
			++numberOfReadElements;
		    this.streamHead = tuple;
		    //System.out.println(this.streamHead);
		}
	}
	
	public boolean streamConsumed()
	{
		return streamConsumed;
	}
	
	public void locateMatchedLabel() throws DBException, IOException
	{
		if (this.streamConsumed()) return;
		
		int PCR = this.streamHead.getCID();
		
		while (!CIDs.contains(PCR))
		{
			advanceStream();
			if (streamConsumed()) break;
			PCR = this.streamHead.getCID();			
		}		
	}
	
	public long getIOTime()
	{
		return this.input.getIOTime();
	}
	
	public long getNumberOfReadElements()
	{
		return numberOfReadElements;
	}
	

}

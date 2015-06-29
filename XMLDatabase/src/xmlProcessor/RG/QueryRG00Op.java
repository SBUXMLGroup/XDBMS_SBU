package xmlProcessor.RG;
//package xtc.server.xmlSvc.xqueryOperators.Twigs.RG;

import java.io.IOException;
import java.util.ArrayList;

import xmlProcessor.DBServer.DBException;
//import xtc.server.accessSvc.XTClocator;
//import indexManager.StructuralSummaryManager; import indexManager.StructuralSummaryManager01;
//import xtc.server.taSvc.XTCtransaction;
//import xtc.server.xmlSvc.XTCxqueryContext;
import xmlProcessor.DBServer.operators.QueryOperator;
//import xmlProcessor.DBServer.operators.twigs.QueryStatistics;;
import xmlProcessor.RG.executionPlan.RGExecutionPlan;
import xmlProcessor.QTP.QTPNode;
import xmlProcessor.QTP.QueryTreePattern;
//import xtc.server.xmlSvc.xqueryUtils.XTCxqueryLevelCounters;
import xmlProcessor.DBServer.utils.QueryTuple;
import xmlProcessor.RG.RG00.RG;
import xmlProcessor.RG.RG00.RGResultSet;


/**
 * 
 *  @author Kamyar
 */
public class QueryRG00Op implements QueryOperator
{
	
	//private XTCxqueryContext context;
	//private XTClocator doc;
	private QueryTreePattern QTP;
	private RGExecutionPlan executionPlan;
	//private XTCtransaction transaction;
	//private PathSynopsisMgr pathSynopsisMgr;
	private RGResultSet result;
	private int pos = -1;	
	boolean docOrdered;
	private String indexName;
	ArrayList<QTPNode> QTPNodes;
	
	/**
	 * NOTICE: Locator should be set in the context before making new object
	 * 
	 * */
	public QueryRG00Op(QueryTreePattern QTP,String indexName,boolean docOrdered) throws DBException
	{		
		//this.context = context;
		//this.doc = context.getLocator();
		//if (doc == null) throw new DBException("Null locator in the context.");
		//this.transaction = context.getTransaction();
		//this.pathSynopsisMgr = context.getMetaDataMgr().getPathSynopsisMgr();
		this.QTP = QTP;
		this.QTPNodes = QTP.getNodes();
		this.executionPlan = null;
//		this.result = new ArrayList<QueryTuple>(XTCsvrCfg.initialIteratorSize);
		this.docOrdered = docOrdered;
		this.indexName=indexName;
	}

	/**
	 * NOTICE: Locator should be set in the context before making new object
	 * NOTICE: Result will be in document order
	 * */
	public QueryRG00Op(QueryTreePattern QTP,String indexName) throws DBException
	{
		this(QTP,indexName,false);		
	}
	
        @Override
	public void open() throws DBException, IOException
	{
		RG aRG = new RG(QTP,indexName,docOrdered);
		this.result = aRG.execute();
	} //open()

        @Override
	public void close() throws DBException
	{
		// TODO Auto-generated method stub

	} //close()

        @Override
	public QueryTuple next() throws DBException, IOException
	{
		return result.next(QTPNodes);
//		if(pos < result.size()-1)
//		{
//			pos++;
//			return result.get(pos);
//		}
//		return null;
	}

	public long getIOTime()
	{
		return this.result.getIOTime();
	}
	
	public long getNumberOfReadElements()
	{
		return this.result.getNumberOfReadElements();
	}

	public long getNumberOfExecutionPlans()
	{
		return this.result.getNumberOfExecutionPlans();
	}
	
	public long getNumberOfGroupedExecutionPlans()
	{
		return this.result.getNumberOfGroupedExecutionPlans();
	}

	public String getExecutionMode()
	{
		return this.result.getExecutionMode();
	}

/////////////////////////////////////////////////
////////////////////////////////////////////////
//////////////////////////////////////////////	
	public void print() {
		// TODO Auto-generated method stub

	}

	public void sysprint(String indent) {
		System.err.println("Not supported method");

	}

	public int getJoinPosition(int qtNodeID) throws DBException {
		throw new DBException("Not supported method");
		//return 0;
	}

	/*public XTCxqueryLevelCounters getMetaData(int qtNodeID) throws DBException {
		throw new DBException("Not supported method");
		//return null;
	}*/

	public int getResultTupleSize() throws DBException {
		// TODO Auto-generated method stub
		return 0;
	}

}

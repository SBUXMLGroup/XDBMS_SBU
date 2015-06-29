/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package xmlProcessor.RG;


import java.util.ArrayList;


import xmlProcessor.DBServer.DBException;
import indexManager.StructuralSummaryManager;
import indexManager.StructuralSummaryManager01;
import indexManager.StructuralSummaryNode;
import java.io.IOException;
import xmlProcessor.DeweyID;
import xmlProcessor.DBServer.utils.SvrCfg;
import xmlProcessor.DBServer.operators.QueryOperator;
import xmlProcessor.DBServer.operators.twigs.QueryStatistics;
import xmlProcessor.RG.RG05.RG;
import xmlProcessor.RG.RG05.RGResultSet;
import xmlProcessor.RG.executionPlan.RGExecutionPlan;
import xmlProcessor.RG.executionPlan.RGExecutionPlanTuple;
import xmlProcessor.QTP.QTPNode;
import xmlProcessor.QTP.QueryTreePattern;
import xmlProcessor.DBServer.utils.QueryTuple;


/**
 * 
 *  @author Kamyar
 */
public class QueryRG05Op implements QueryOperator, QueryStatistics, QueryRGExtendedStatistics 
{
	
	//private XTCxqueryContext context;
	//private XTClocator doc;
	private QueryTreePattern QTP;
	private RGExecutionPlan executionPlan;
	//private XTCtransaction transaction;
	private StructuralSummaryManager ssMgr;
        private String indexName;
	private RGResultSet result;
	//private int idxNo;
	private int pos = -1;	
	//boolean docOrdered;
	ArrayList<QTPNode> QTPNodes;
	
	/**
	 * NOTICE: Locator should be set in the context before making new object
	 * 
	 * */
	public QueryRG05Op(QueryTreePattern QTP, String indexName/*, boolean docOrdered*/) throws DBException
	{		
		//this.context = context;
		//this.doc = context.getLocator();
		//if (doc == null) throw new DBException("Null locator in the context.");
		//this.transaction = context.getTransaction();
		this.ssMgr =new StructuralSummaryManager01();
                        //context.getMetaDataMgr().getPathSynopsisMgr();
                this.indexName=indexName;
		this.QTP = QTP;
		//this.idxNo = idxNo;
		this.QTPNodes = QTP.getOutputNodes();//QTP.getNodes();
		this.executionPlan = null;
//		this.result = new ArrayList<XTCxqueryTuple>(XTCsvrCfg.initialIteratorSize);
		//this.docOrdered = docOrdered;
	}

	public void open() throws DBException, IOException 
	{
		RG aRG = new RG( QTP,indexName/*, docOrdered*/);
		this.result = aRG.execute();
	} //open()

	public void close() throws DBException 
	{
		this.result.close();
	} //close()

	public QueryTuple next() throws DBException, IOException 
	{
		return result.next(QTPNodes);
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

//	public XTCxqueryLevelCounters getMetaData(int qtNodeID) throws DBException {
//		throw new DBException("Not supported method");
//		//return null;
//	}

	public int getResultTupleSize() throws DBException {
		// TODO Auto-generated method stub
		return 0;
	}

}

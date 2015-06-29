///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package xmlProcessor.Backups;
//
//import xmlProcessor.RG00.RG;
//import xmlProcessor.RG00.RGResultSet;
//
// import java.util.ArrayList;
//
//import xmlProcessor.xtcServer.DBException;
////import xtc.server.accessSvc.XTClocator;
////import indexManager.StructuralSummaryManager; import indexManager.StructuralSummaryManager01;
////import xtc.server.taSvc.XTCtransaction;
////import xtc.server.xmlSvc.XTCxqueryContext;
//import xmlProcessor.xtcServer.operators.XTCxqueryOperator;
////import xmlProcessor.DBServer.operators.twigs.QueryStatistics;;
//import xmlProcessor.executionPlan.RGExecutionPlan;
//import xmlProcessor.QTP.QTPNode;
//import xmlProcessor.QTP.QueryTreePattern;
////import xtc.server.xmlSvc.xqueryUtils.XTCxqueryLevelCounters;
//import xmlProcessor.xtcServer.utils.QueryTuple;
//
//
///**
// * 
// *  @author Kamyar
// */
//public class XTCxqueryRG00Op0 implements XTCxqueryOperator, QueryStatistics, XTCxqueryRGExtendedStatistics
//{
//	
//	private XTCxqueryContext context;
//	private XTClocator doc;
//	private QueryTreePattern QTP;
//	private RGExecutionPlan executionPlan;
//	private XTCtransaction transaction;
//	private PathSynopsisMgr pathSynopsisMgr;
//	private RGResultSet result;
//	private int pos = -1;	
//	boolean docOrdered;
//	private int idxNo;
//	ArrayList<QTPNode> QTPNodes;
//	
//	/**
//	 * NOTICE: Locator should be set in the context before making new object
//	 * 
//	 * */
//	public XTCxqueryRG00Op(XTCxqueryContext context, QueryTreePattern QTP, int idxNo, boolean docOrdered) throws DBException
//	{		
//		this.context = context;
//		this.doc = context.getLocator();
//		if (doc == null) throw new DBException("Null locator in the context.");
//		this.transaction = context.getTransaction();
//		this.pathSynopsisMgr = context.getMetaDataMgr().getPathSynopsisMgr();
//		this.QTP = QTP;
//		this.QTPNodes = QTP.getNodes();
//		this.executionPlan = null;
////		this.result = new ArrayList<QueryTuple>(XTCsvrCfg.initialIteratorSize);
//		this.docOrdered = docOrdered;
//		this.idxNo = idxNo;
//	}
//
//	/**
//	 * NOTICE: Locator should be set in the context before making new object
//	 * NOTICE: Result will be in document order
//	 * */
//	public XTCxqueryRG00Op(XTCxqueryContext context, QueryTreePattern QTP, int idxNo) throws DBException
//	{
//		this(context, QTP, idxNo, false);		
//	}
//	
//	public void open() throws DBException 
//	{
//		RG aRG = new RG(context, QTP, idxNo, docOrdered);
//		this.result = aRG.execute();
//	} //open()
//
//	public void close() throws DBException 
//	{
//		// TODO Auto-generated method stub
//
//	} //close()
//
//	public QueryTuple next() throws DBException 
//	{
//		return result.next(QTPNodes);
////		if(pos < result.size()-1)
////		{
////			pos++;
////			return result.get(pos);
////		}
////		return null;
//	}
//
//	public long getIOTime()
//	{
//		return this.result.getIOTime();
//	}
//	
//	public long getNumberOfReadElements()
//	{
//		return this.result.getNumberOfReadElements();
//	}
//
//	public long getNumberOfExecutionPlans()
//	{
//		return this.result.getNumberOfExecutionPlans();
//	}
//	
//	public long getNumberOfGroupedExecutionPlans()
//	{
//		return this.result.getNumberOfGroupedExecutionPlans();
//	}
//
//	public String getExecutionMode()
//	{
//		return this.result.getExecutionMode();
//	}
//
///////////////////////////////////////////////////
//////////////////////////////////////////////////
////////////////////////////////////////////////	
//	public void print() {
//		// TODO Auto-generated method stub
//
//	}
//
//	public void sysprint(String indent) {
//		System.err.println("Not supported method");
//
//	}
//
//	public int getJoinPosition(int qtNodeID) throws DBException {
//		throw new DBException("Not supported method");
//		//return 0;
//	}
//
//	public XTCxqueryLevelCounters getMetaData(int qtNodeID) throws DBException {
//		throw new DBException("Not supported method");
//		//return null;
//	}
//
//	public int getResultTupleSize() throws DBException {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//}

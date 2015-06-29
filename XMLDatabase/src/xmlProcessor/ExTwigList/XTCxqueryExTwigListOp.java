//package xmlProcessor.ExTwigList;
//
//import java.util.ArrayList;
//
//import xmlProcessor.xtcServer.DBException;
//import xtc.server.accessSvc.XTClocator;
//import xtc.server.taSvc.XTCtransaction;
//import xmlProcessor.xtcServer.utils.XTCsvrCfg;
//import xtc.server.xmlSvc.XTCxqueryContext;
//import xmlProcessor.xtcServer.operators.XTCxqueryOperator;
//import xmlProcessor.DBServer.operators.twigs.QueryStatistics;;
//import xmlProcessor.QTP.QTPNode;
//import xmlProcessor.QTP.QueryTreePattern;
//import xtc.server.xmlSvc.xqueryUtils.XTCxqueryLevelCounters;
//import xmlProcessor.DBServer.utils.QueryTuple;
//
//public class XTCxqueryExTwigListOp implements XTCxqueryOperator, QueryStatistics
//{
//
//	private XTCxqueryContext context;
//	private XTClocator doc;
//	private QueryTreePattern QTP;
//	private ArrayList<QTPNode> QTPNodes;
//	private XTCtransaction transaction;
//	//private TwigJoinOp result = null;
//	private TwigJoinOp result = null;
//	private int pos = -1;	
//	private ExTwigList aTwigList = null;
//	private int idxNo;
//
//	/**
//	 * NOTICE: Locator should be set in the context before making new object
//	 * 
//	 * */
//	public XTCxqueryExTwigListOp(XTCxqueryContext context, QueryTreePattern QTP, int idxNo) throws DBException
//	{
//		this.context = context;
//		this.idxNo = idxNo;
//		this.doc = context.getLocator();
//		if (doc == null) throw new DBException("Null locator in the context.");
//		this.transaction = context.getTransaction();
//		this.QTP = QTP;
//		this.QTPNodes = QTP.getNodes();
//		//this.result = new ArrayList<QueryTuple>(XTCsvrCfg.initialIteratorSize);		
//	}
//
//	public void open() throws DBException 
//	{
//		aTwigList = new ExTwigList(context, QTP, idxNo);
//		//TwigList01 aTwigList = new TwigList01(context, QTP);
//		try
//		{
//			this.result = aTwigList.execute();
//		}
//		catch (DBException e) 
//		{
//			this.aTwigList.close();
//			throw e;
//		}
//		catch (Exception e) 
//		{
//			this.aTwigList.close();
//			e.printStackTrace();
//			throw new DBException("Error in ExTWigList");
//		}
//
//	}
//
//	public QueryTuple next() throws DBException 
//	{
//		TwigResult res = null;
//		try
//		{
//			res = result.next();
//		}
//		catch (DBException e) 
//		{
//			this.result.close();
//			throw e;
//		}
//		catch (Exception e) 
//		{
//			this.result.close();
//			e.printStackTrace();
//			throw new DBException("Error in ExTWigList");
//		}
//
//		
//		
//		if (res == null) return null;
//		return res.getRes(QTPNodes);
//	}
//	
//	public void close() throws DBException {
//		// TODO Auto-generated method stub
//		this.result.close();
//	}
//
//	public long getIOTime()
//	{
//		return this.aTwigList.getIOTime();
//	}
//	
//	public long getNumberOfReadElements()
//	{
//		return this.aTwigList.getNumberOfReadElements();
//	}
//	
//	
//	
//	public void print() {
//		// TODO Auto-generated method stub
//
//	}
//
//	public void sysprint(String indent) {
//		// TODO Auto-generated method stub
//
//	}
//	public int getJoinPosition(int qtNodeID) throws DBException {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	public XTCxqueryLevelCounters getMetaData(int qtNodeID) throws DBException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	public int getResultTupleSize() throws DBException {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//}

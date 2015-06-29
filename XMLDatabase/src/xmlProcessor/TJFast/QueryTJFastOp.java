/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package xmlProcessor.TJFast;

import java.io.IOException;
import java.util.ArrayList;

import xmlProcessor.DBServer.DBException;
//import xtc.server.accessSvc.XTClocator;
//import xtc.server.taSvc.XTCtransaction;
import xmlProcessor.DBServer.utils.SvrCfg;
//import xtc.server.xmlSvc.XTCxqueryContext;
import xmlProcessor.DBServer.operators.QueryOperator;
import xmlProcessor.DBServer.operators.twigs.QueryStatistics;
import xmlProcessor.QTP.QTPNode;
import xmlProcessor.QTP.QueryTreePattern;
//import xtc.server.xmlSvc.xqueryUtils.XTCxqueryLevelCounters;
import xmlProcessor.DBServer.utils.QueryTuple;

/**
 * 
 *  
 * @author Kamyar
 *
 */
public class QueryTJFastOp implements QueryOperator, QueryStatistics 
{

	//private XTCxqueryContext context;
	//private XTClocator doc;
	private QueryTreePattern QTP;
	private ArrayList<QTPNode> QTPNodes;
	//private XTCtransaction transaction;
	private TwigJoinOp result = null;
	private int pos = -1;
	private TJFast tJFast = null;
	//private int idxNo;
        private String indexName;

	/**
	 * NOTICE: Locator should be set in the context before making new object
	 * 
	 * */
	public QueryTJFastOp(QueryTreePattern QTP,String indexName) throws DBException
	{
		//this.context = context;
		//this.idxNo = idxNo;
		//this.doc = context.getLocator();
		//if (doc == null) throw new DBException("Null locator in the context.");
		//this.transaction = context.getTransaction();
		this.QTP = QTP;
		this.QTPNodes = QTP.getNodes();
		//this.result = new ArrayList<XTCxqueryTuple>(XTCsvrCfg.initialIteratorSize);
	}

	public void open() throws DBException, IOException {
		tJFast = new TJFast(QTP,indexName);
		this.result = tJFast.execute();
	}

	public QueryTuple next() throws DBException, IOException {
		TwigResult res = result.next();
		if (res == null) return null;
		return res.getRes(QTPNodes);
//		ArrayList<QTPNode> arr = QTP.getLeaves();
//		ArrayList<QTPNode> arr2 = new ArrayList<QTPNode>();
//		arr2.add(arr.get(3));
//		arr2.add(arr.get(2));
//		arr2.add(arr.get(1));
//		arr2.add(arr.get(0));
//		return res.getRes(arr2);
//		if(pos < result.size()-1)
//		{
//			pos++;
//			return result.get(pos).getRes(QTPNodes);
//		}
//		return null;
	}
	
	public void close() throws DBException 
	{
		// TODO Auto-generated method stub
		this.result.close();
	}

	public long getIOTime()
	{
		return this.tJFast.getIOTime();
	}
	
	public long getNumberOfReadElements()
	{
		return this.tJFast.getNumberOfReadElements();
	}
	
	public void print() {
		// TODO Auto-generated method stub

	}

	public void sysprint(String indent) {
		// TODO Auto-generated method stub

	}
	public int getJoinPosition(int qtNodeID) throws DBException {
		// TODO Auto-generated method stub
		return 0;
	}
////////////commented by Ouldouz//////////////
//	public QueryLevelCounters getMetaData(int qtNodeID) throws DBException {
//		// TODO Auto-generated method stub
//		return null;
//	}

	public int getResultTupleSize() throws DBException {
		// TODO Auto-generated method stub
		return 0;
	}

}

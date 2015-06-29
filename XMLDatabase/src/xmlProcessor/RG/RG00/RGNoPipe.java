package xmlProcessor.RG.RG00;

//
//package xmlProcessor.RG00;
////package xtc.server.xmlSvc.xqueryOperators.Twigs.RG.RG00;
//
///**
// * Main class for processing a Twig with RG(ResultGraph) method
// * 
// *  @author Kamyar
// */
//
//import java.lang.reflect.Array;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.LinkedList;
//import java.util.Queue;
//import java.util.Set;
//
//import xmlProcessor.xtcServer.DBException;
//import xtc.server.accessSvc.XTClocator;
//import indexManager.StructuralSummaryManager; import indexManager.StructuralSummaryManager01;
//import indexManager.StructuralSummaryNode;
//import xmlProcessor.DeweyID;
//import xtc.server.taSvc.XTCtransaction;
//import xtc.server.util.XTCsvrCfg;
//import xtc.server.xmlSvc.XTCxqueryContext;
//import xtc.server.xmlSvc.xqueryOperators.XTCxqueryOperator;
//import xtc.server.xmlSvc.xqueryOperators.PathIdxBased.ElementIteratorNoStream;
//import xtc.server.xmlSvc.xqueryOperators.Twigs.RG.executionPlan.RGExecutionPlan;
//import xtc.server.xmlSvc.xqueryOperators.Twigs.RG.executionPlan.RGExecutionPlanTuple;
//import xtc.server.xmlSvc.xqueryOperators.Twigs.QTP.QueryTreePattern;
//import xtc.server.xmlSvc.xqueryUtils.XTCxqueryLevelCounters;
//import xmlProcessor.DBServer.utils.QueryTuple;
//import xtc.server.xmlSvc.xqueryUtils.QueryTupleComparator;
//
///**
// * 
// *  @author Kamyar
// */
//public class RGNoPipe 
//{ 
//	
//	private XTCxqueryContext context;
//	private XTClocator doc;
//	private QueryTreePattern QTP;
//	private RGExecutionPlan executionPlan;
//	private XTCtransaction transaction;
//	private PathSynopsisMgr pathSynopsisMgr;
//	private ArrayList<QueryTuple> result;
//	boolean docOrder;
//	boolean splid;
//	
//	/**
//	 * NOTICE: Locator should be set in the context before making new object
//	 * 
//	 * */
//	public RGNoPipe(XTCxqueryContext context, QueryTreePattern QTP, boolean docOrder) throws DBException
//	{
//		this.context = context;
//		this.doc = context.getLocator();
//		if (doc == null) throw new DBException("Null locator in the context.");
//		this.splid = false; //PCR Clustering
//		this.transaction = context.getTransaction();
//		this.pathSynopsisMgr = context.getMetaDataMgr().getPathSynopsisMgr();
//		this.QTP = QTP;
//		this.executionPlan = null;
//		this.result = new ArrayList<QueryTuple>(XTCsvrCfg.initialIteratorSize);
//		this.docOrder = docOrder;
//	}
//
//	/**
//	 * NOTICE: Locator should be set in the context before making new object
//	 * NOTICE: Result will be in document order
//	 * */
//	public RGNoPipe(XTCxqueryContext context, QueryTreePattern QTP) throws DBException
//	{
//		this(context, QTP, false);		
//	}
//	
////	public void open() throws DBException {
////		
////		this.execute();
////
////		//Sort results in document position order
////		
////		//long s= System.currentTimeMillis();
////		if (docOrder)
////		{
////			RGResultComparator comparator = new RGResultComparator();
////			Collections.sort(result, comparator);
////		}
////		//long e = System.currentTimeMillis();
////		//System.out.println("Sort time: " + (e-s));
////		
////	}
//	/////////////////////////////////////////////////////////////////////////////////
//	///////////////////////////////////////////////////////////////////////////////
//	//////////////////////////////////////////////////////////////////////////////
//	/**
//	 *  RGResult is used to manage the process of merging input streams of the leaves of the QTP  
//	 */
//	private class RGResultComposer
//	{
//		ArrayList<QueryTuple> res;
//		int lPos; //position off last data in XTCqueryTuple data
//		public RGResultComposer(ElementIteratorNoStream firstInput) throws DBException
//		{
//			res = new ArrayList<QueryTuple>(XTCsvrCfg.initialIteratorSize);
//			QueryTuple data = firstInput.next();
//			while (data != null)
//			{
//				res.add(data);
//				data = firstInput.next();				
//				
////				if(data!=null) System.out.println(data + "with PCR: " + data.getIDForPos(0).getPCR());
//			}
//			this.lPos = 0;
//		}
//		
//		public void mergeWithLast(ElementIteratorNoStream input, int compareLevel) throws DBException
//		{
//			int r = 0;
//			ArrayList<QueryTuple> rSub = new ArrayList<QueryTuple>();
//			ArrayList<QueryTuple> iSub = new ArrayList<QueryTuple>();
//			ArrayList<QueryTuple> newRes = new ArrayList<QueryTuple>(XTCsvrCfg.initialIteratorSize);
//						
//			QueryTuple inp = input.next();
//			while ((r < res.size()) && (inp != null))
//			{
//				QueryTuple rBase = res.get(r);
//				int compare = rBase.getIDForPos(lPos).compareTo(compareLevel, inp.getIDForPos(0));
//				if( compare == 0) //they are the same up to compareLevel
//				{
//					rSub.clear();
//					iSub.clear();
//					//while following items are the same as rBase up to compareLevel
//					//index lpos used to retrieve last ID that has been added
//					while ((r < res.size()) && (res.get(r).getIDForPos(lPos).compareTo(compareLevel, rBase.getIDForPos(0)) == 0))
//					{
//						rSub.add(res.get(r));
//						++r;
//					}
//
//					//while following items are the same as (inp) up to compareLevel
//					QueryTuple tInp; //Temp inp
//					do 
//					{
//						iSub.add(inp);
//						tInp = inp; //Temp inp
//						inp = input.next();
//					} while ((inp != null) && (tInp.getIDForPos(0).compareTo(compareLevel, inp.getIDForPos(0)) == 0));//do
//					
//					//X-product of rSub and iSub
//					for (QueryTuple rTuple : rSub)
//					{
//						for (QueryTuple iTuple : iSub)
//						{
//							//merge and add to the newRes
//							newRes.add(QueryTuple.merge(rTuple, iTuple));													
//						} //for					
//					} //for
//				} //if
//				else if (compare < 0)
//				{
////					if (lPos == 0)
////					{
////						System.out.println("rBase:" + rBase.getIDForPos(lPos) + " with PCR: " + rBase.getIDForPos(lPos).getPCR());
////						System.out.println("inp: " + inp.getIDForPos(0) + " with PCR: " + inp.getIDForPos(0).getPCR() + " compareLevel: " + compareLevel +"\n _______");
////						
////					}
//					++r;
//				}
//				else //compare > 0
//				{
////					if (lPos == 0)
////					{
////						System.out.println("rBase:" + rBase.getIDForPos(lPos) + " with PCR: " + rBase.getIDForPos(lPos).getPCR());
////						System.out.println("inp: " + inp.getIDForPos(0) + " with PCR: " + inp.getIDForPos(0).getPCR() + " compareLevel: " + compareLevel +"\n _______");
////						
////					}
//					inp = input.next();
//				}//if compare					
//			} //while ((r < res.size()) && (inp != null))
//			
//			//change to the new result
//			lPos++;
//			res = newRes;
//		}
//		
//		public ArrayList<QueryTuple> getRes()
//		{
//			return res;			 
//		}
//	} //class RGResult
//	/////////////////////////////////////////////////////////////////////////////////
//	///////////////////////////////////////////////////////////////////////////////
//	//////////////////////////////////////////////////////////////////////////////
////	public void execute() throws DBException
////	{
////		ArrayList<ArrayList<QueryTuple>> partialRes = new ArrayList<ArrayList<QueryTuple>>(executionPlan.size()); 
////		for (int i = 0; i < executionPlan.size(); i++)
////		{
////			RGExecutionPlanTuple tuple = executionPlan.getTuple(i);
////			ArrayList<PathSynopsisNode> nodes = tuple.getNodes(); //nodes of Pathsynopsis related to the leaves of the QTP
////			ArrayList<PathSynopsisNode> joinPoints = tuple.getJoinPoints();
////			String name = context.getMetaDataMgr().resolveVocID(transaction, doc.getID(), nodes.get(0).getVocID());
////			ElementIterator first = new ElementIterator(context, name, nodes.get(0).getPCR(), false);
////			first.open();
////			nodes.remove(0);
////			RGResult res = new RGResult(first);
////			
////			for (int j = 0; j < nodes.size(); ++j)
////			{
////				name = context.getMetaDataMgr().resolveVocID(transaction, doc.getID(), nodes.get(j).getVocID());
////				ElementIterator second = new ElementIterator(context, name, nodes.get(j).getPCR(), false);
////				second.open();
////				PathSynopsisNode join = joinPoints.get(j);
////				res.mergeWithLast(second, join.getLevel());				
////			}
////			ArrayList<QueryTuple> arr = res.getRes(); 
//////			System.out.println("Number of Tuples: " + i + " Number of results:" + arr.size());
////			if(! arr.isEmpty()) partialRes.add(arr);
////			
////		}
////		//Join partial results in document position order
////		long s= System.currentTimeMillis();
////		QueryTupleComparator comparator = new QueryTupleComparator(); 
////		while (! partialRes.isEmpty())
////		{
////			int minIndex = 0;
////
////			QueryTuple min = partialRes.get(0).get(0);
////			for(int i = 0; i < partialRes.size(); i++)
////			{
////				// new smaller element found
////				if(comparator.compare(min, partialRes.get(i).get(0)) > 0)
////				{
////					minIndex = i;
////					min = partialRes.get(i).get(0);
////				}
////			}
////			
////			// remove element from corr. queue
////			// remove whole queue if empty
////			partialRes.get(minIndex).remove(0);
////			if(partialRes.get(minIndex).isEmpty())
////				partialRes.remove(minIndex);
////			
////			result.add(min);
////		}		
////		long e = System.currentTimeMillis();
////		System.out.println("Sort time: " + (e-s));
////	}
//	public ArrayList<QueryTuple> execute() throws DBException
//	{
//		//TODO: This implementation of merging is not memory efficient 
//		//it should be changed to the situation that after construction of 
//		//each final result it be returned to the caller instead of keeping all results in the memory  
//		this.executionPlan = getExecutionPlan();
////		//for each tuple in execution plan a stream of results will be produced 
//		for (int i = 0; i < executionPlan.size(); i++)
//		{
//			RGExecutionPlanTuple tuple = executionPlan.getTuple(i);
//			ArrayList<PathSynopsisNode> leafNodes= tuple.getleafNodes(); //nodes of Pathsynopsis related to the leaves of the QTP
//			ArrayList<PathSynopsisNode> joinPoints = tuple.getJoinPoints();
//			String name = context.getMetaDataMgr().resolveVocID(transaction, doc.getID(), leafNodes.get(0).getVocID());
//			ElementIteratorNoStream first = new ElementIteratorNoStream(context, name, leafNodes.get(0).getPCR(), this.splid);
//			first.open();
//			RGResultComposer res = new RGResultComposer(first);
//			//merge other leaves
//			for (int j = 1; j < leafNodes.size(); ++j)
//			{
//				name = context.getMetaDataMgr().resolveVocID(transaction, doc.getID(), leafNodes.get(j).getVocID());
//				ElementIteratorNoStream second = new ElementIteratorNoStream(context, name, leafNodes.get(j).getPCR(), this.splid);
//				second.open();
//				PathSynopsisNode join = joinPoints.get(j - 1);
//				//long s = System.currentTimeMillis();
//				res.mergeWithLast(second, join.getLevel());
//				//long e = System.currentTimeMillis();
//				//System.out.println("Merge " + j + ": " + (e-s));
//			}
////			System.out.println("Number of Tuples: " + i + " Number of results:" + arr.size());			
//			//result of one of selected tuple in the execution plan is ready!!!
//			ArrayList<QueryTuple> arr = res.getRes();
//			
//			arr = produceOutput(arr, leafNodes, joinPoints, tuple.getInnerNodes());
//			//results of the selected tuple of the execution plan are added to the final result
//			result.addAll(arr);
//			
//		}
//
//		if (docOrder)
//		{
//			RGResultComparator comparator = new RGResultComparator();
//			Collections.sort(result, comparator);
//		}
//		
//		return result;
//	} //execute()
//
//	/**
//	 * Complete the results in resArr and returns resArr
//	 * @param resArr
//	 * @param leafNodes
//	 * @param joinPoints
//	 * @param innerNodes
//	 * @return
//	 */
//	private ArrayList<QueryTuple> produceOutput(ArrayList<QueryTuple> resArr, ArrayList<PathSynopsisNode> leafNodes, ArrayList<PathSynopsisNode> joinPoints, ArrayList<PathSynopsisNode> innerNodes)
//	{
//		/////////////////////////////////////////////////////////////////////////////////////////
//		/////////////////////////////////////////////////////////////////////////////////////////
//		////////////AFTER EXECUTION OF THIS METHOD ID OF ROOT OF QTP SHOULD BE PLACED //////////
//		////////////AT THE FIRST POSITION SO SORTING WILL WORK BECAUSE IT IS BASE ON //////////
//		////////////THE ROOT OF QTP//////////////////////////////////////////////////////////////
//		////////////////////////////////////////////////////////////////////////////////////////
//		///////////////////////////////////////////////////////////////////////////////////////
//		
//		//plan[i] means that for innerNodes.get(i) plan[i][0] is used for resArr.getIdForPos(?)  
//		//plan[i][1] represent the level of prefix of resArr.getIdForPos(plan[i][0]) which will be ID of one of query nodes in QTP  
//		int[][] plan = new int[innerNodes.size()][2];
//
//		boolean[] created = new boolean[innerNodes.size()];
//		for(int i=0; i < innerNodes.size(); ++i)
//		{
//			created[i] = false;
//		}
//		
//		//for each leaf node we start to go to the root and create required IDs
//		//for each node it is saved that which leaf could or should create its ID 
//		for (int i=0; i < leafNodes.size(); ++i)
//		{
//			PathSynopsisNode node = leafNodes.get(i);
//			node = node.getParent();
//			while (node != null)
//			{
//				int index = innerNodes.indexOf(node); 
//				if (index != -1) //
//				{
//					if (!created[index])
//					{
//						PathSynopsisNode pNode =  innerNodes.get(index);
////						int l = pNode.getLevel();
//						plan[index][0] = i; //position of leaf ID in res which will be used later
//						plan[index][1] = pNode.getLevel();					
//						created[index] = true;
//					}
//					else //required plan is created
//					{
//						break; //there is no need to go to upper levels. The needed plan for upper nodes are created by previous leaf nodes
//					}
//				}				
//				//node is not involved in the QTP or its plan is created
//				node = node.getParent();
//			}// while
//		}
//		//ArrayList<PathSynopsisNode> innerNodes = QTP.getInnerNodes();
//		//if (res.size() != leafNodes.size()) System.err.println(" XTCxqueryRGOp.java [produceOutput]: number of leaves are not the same as available IDs");
//		ArrayList<DeweyID> addition = new ArrayList<DeweyID>();
//		//long s = System.currentTimeMillis();
//		for (int i = 0; i < resArr.size(); i++) 
//		{
//			QueryTuple res = resArr.get(i);
//			//QueryTuple addition = new QueryTuple();
//			addition.clear();
//			for (int j = 0; j < innerNodes.size(); j++) 
//			{
//				addition.add(res.getIDForPos(plan[j][0]).getAncestorDeweyID(plan[j][1])); 
//			}
//			res.addDeweyID(0, addition);	
//		}
//		//long e = System.currentTimeMillis();
//		//System.out.println("produceOutput: " + (e-s));
//
//		return resArr;
//	}
//
//	public void close() throws DBException {
//		// TODO Auto-generated method stub
//
//	}
//
////	public QueryTuple next() throws DBException {
////		if(pos < result.size()-1)
////		{
////			pos++;
////			return result.get(pos);
////		}
////		return null;
////	}
//
//	private RGExecutionPlan getExecutionPlan() throws DBException
//	{
//		 RGExecutionPlan execPlan = pathSynopsisMgr.getRGExecutionPlan(transaction, doc, QTP);
//		 
////			Set<QTPNode> qNodes = res.keySet();
////			for (Iterator<QTPNode> iterator2 = qNodes.iterator(); iterator2.hasNext();) {
////				QTPNode node = iterator2.next();
////				String str = "[" + node.getName() + ": " + res.get(node).getPCR() + "] ";
////				System.out.print(str);				
////			}
////			System.out.println();
////		}
////		System.out.println("Number of results: " + twRes.size());
//
//		//System.out.println(pathSynopsisMgr.getXMLPathSynopsis(transaction, doc));
//		return execPlan; 
//	}
//
//}

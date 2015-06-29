///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package xmlProcessor.Backups;
//
//import xmlProcessor.RG00.RG;
//import xmlProcessor.RG00.RGResultSet;
//import xmlProcessor.RG00.TwigInput;
//import xmlProcessor.RG00.TwigJoinOp;
//import xmlProcessor.RG00.TwigResult;
//
////package xtc.server.xmlSvc.xqueryOperators.Twigs.RG.RG00;
//
///**
// * Main class for processing a Twig with RG(ResultGraph) method
// * S3
// * 
// *  @author Kamyar
// */
//
//import java.util.ArrayList;
//import java.util.Comparator;
//import java.util.HashMap;
//import java.util.HashSet;
//import xmlProcessor.xtcServer.DBException;
//import xtc.server.accessSvc.XTClocator;
//import indexManager.StructuralSummaryManager; import indexManager.StructuralSummaryManager01;
//import indexManager.StructuralSummaryNode;
//import xmlProcessor.DeweyID;
//import xtc.server.taSvc.XTCtransaction;
//import xmlProcessor.DBServer.utils.TwinObject;
//import xtc.server.util.XTCcalc;
//import xtc.server.util.XTCsvrCfg;
//import xtc.server.xmlSvc.XTCxqueryContext;
//import xmlProcessor.xtcServer.operators.XTCxqueryOperator;
//import xtc.server.xmlSvc.xqueryOperators.PathIdxBased.XTCxqueryElementScanOp;
//import xtc.server.xmlSvc.xqueryOperators.PathIdxBased.XTCxqueryPCRScanOp;
//import xmlProcessor.DBServer.operators.twigs.QueryStatistics;;
//import xmlProcessor.DBServer.operators.twigs.QueryRGExtendedStatistics;
//import xmlProcessor.executionPlan.RGExecutionPlan;
//import xmlProcessor.executionPlan.RGExecutionPlanTuple;
//import xmlProcessor.QTP.QTPNode;
//import xmlProcessor.QTP.QTPNodeConstraint;
//import xmlProcessor.QTP.QueryTreePattern;
//import xmlProcessor.xtcServer.utils.QueryTuple;
//
///**
// * 
// *  @author Kamyar
// */
//public class RG0Backup implements QueryStatistics, XTCxqueryRGExtendedStatistics
//{
//	private enum IO_ModeType{
//		DIRECT, INDIRECT
//	}
//	
//	private XTCxqueryContext context;
//	private XTClocator doc;
//	private QueryTreePattern QTP;
//	private RGExecutionPlan executionPlan;
//	private XTCtransaction transaction;
//	private PathSynopsisMgr pathSynopsisMgr;
//	private ArrayList<TwigResult> result;
//	private ArrayList<TwinObject<TwigJoinOp, TwigResult>> twigJoinOps = null; //used when docOrdered is true:: each time head of the TwigJoinOp is saved in TwigResult of TwinObject
//	private RG.TwigResultComparator twigResultComparator = null; //used when docOrdered is true
//	private int currentPlan = 0; //used when docOrdered is false
//	private TwigJoinOp currentTwigJoinOp = null; //used when docOrdered is false	
//	private boolean docOrdered;
//	//boolean splid;
//	private int idxNo;
//	private RG.IO_ModeType IO_Mode; 
//	private ArrayList<QTPNode> leafNodes;
//	private HashMap<QTPNode, HashMap<Integer, ArrayList<QueryTuple>>> IndirectInputs; //used when INDIRECT IO is chosen
//	private long numberOfReadElements = 0; //used for experimental results
//	private long IOTime = 0; //used for experimental results
//	private long estimatedPlansNo;
//	private long exactPlansNo;
//	private HashMap<QTPNode, HashSet<Integer>> targetPCRS = null;
//	
//	
//	/**
//	 * NOTICE: Locator should be set in the context before making new object
//	 * 
//	 * */
//	public RG(XTCxqueryContext context, QueryTreePattern QTP, int idxNo, boolean docOrdered) throws DBException
//	{
//		this.context = context;
//		this.doc = context.getLocator();		
//		if (doc == null) throw new DBException("Null locator in the context.");
//		//this.splid = false; //TODO: It works with PCR clustering! no meta-data available now!!!
//		this.transaction = context.getTransaction();
//		this.pathSynopsisMgr = context.getMetaDataMgr().getPathSynopsisMgr();
//		this.QTP = QTP;
//		this.idxNo = idxNo;
//		this.executionPlan = null;
//		this.result = new ArrayList<TwigResult>(XTCsvrCfg.initialIteratorSize);
//		this.docOrdered = docOrdered;
//		if (docOrdered) this.twigResultComparator = new RG.TwigResultComparator(QTP.getNodes());
//		this.leafNodes = QTP.getLeaves();		
//		this.IndirectInputs = new HashMap<QTPNode, HashMap<Integer,ArrayList<QueryTuple>>>(leafNodes.size());		
//	}
//
//	/**
//	 * NOTICE: Locator should be set in the context before making new object
//	 * NOTICE: Result will be in document order
//	 * */
//	public RG(XTCxqueryContext context, int idxNo, QueryTreePattern QTP) throws DBException
//	{
//		this(context, QTP, idxNo, true);		
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
//		public RGResultComposer(XTCxqueryPCRScanOp firstInput) throws DBException
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
//		public void mergeWithLast(XTCxqueryPCRScanOp input, int compareLevel) throws DBException
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
//					//index (lPos) used to retrieve last ID that has been added
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
////			XTCxqueryPCRScanOp first = new XTCxqueryPCRScanOp(context, name, nodes.get(0).getPCR(), false);
////			first.open();
////			nodes.remove(0);
////			RGResult res = new RGResult(first);
////			
////			for (int j = 0; j < nodes.size(); ++j)
////			{
////				name = context.getMetaDataMgr().resolveVocID(transaction, doc.getID(), nodes.get(j).getVocID());
////				XTCxqueryPCRScanOp second = new XTCxqueryPCRScanOp(context, name, nodes.get(j).getPCR(), false);
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
////	public ArrayList<QueryTuple> execute() throws DBException
////	{
////		//TODO: This implementation of merging is not memory efficient 
////		//it should be changed to the situation that after construction of 
////		//each final result it be returned to the caller instead of keeping all results in the memory
////		this.executionPlan = getExecutionPlan();		
//////		//for each tuple in execution plan a stream of results will be produced 
////		for (int i = 0; i < executionPlan.size(); i++)
////		{
////			RGExecutionPlanTuple tuple = executionPlan.getTuple(i);
////			ArrayList<PathSynopsisNode> leafNodes= tuple.getleafNodes(); //nodes of Pathsynopsis related to the leaves of the QTP
////			ArrayList<PathSynopsisNode> joinPoints = tuple.getJoinPoints();
////			String name = context.getMetaDataMgr().resolveVocID(transaction, doc.getID(), leafNodes.get(0).getVocID());
////			XTCxqueryPCRScanOp first = new XTCxqueryPCRScanOp(context, name, leafNodes.get(0).getPCR(), this.splid);
////			first.open();
////			RGResultComposer res = new RGResultComposer(first);
////			//merge other leaves
////			for (int j = 1; j < leafNodes.size(); ++j)
////			{
////				name = context.getMetaDataMgr().resolveVocID(transaction, doc.getID(), leafNodes.get(j).getVocID());
////				XTCxqueryPCRScanOp second = new XTCxqueryPCRScanOp(context, name, leafNodes.get(j).getPCR(), this.splid);
////				second.open();
////				PathSynopsisNode join = joinPoints.get(j - 1);
////				//long s = System.currentTimeMillis();
////				res.mergeWithLast(second, join.getLevel());
////				//long e = System.currentTimeMillis();
////				//System.out.println("Merge " + j + ": " + (e-s));
////			}
//////			System.out.println("Number of Tuples: " + i + " Number of results:" + arr.size());
////			
////			//result of one of selected tuple in the execution plan is ready!!!
////			ArrayList<QueryTuple> arr = res.getRes();
////			
////			arr = produceOutput(arr, leafNodes, joinPoints, tuple.getInnerNodes());
////			//results of the selected tuple of the execution plan are added to the final result
////			result.addAll(arr);
////			
////		}
////
////		if (docOrder)
////		{
////			RGResultComparator comparator = new RGResultComparator();
////			Collections.sort(result, comparator);
////		}
////		
////		return result;
////	} //execute()
//
//	private RG.IO_ModeType chooseIO_Mode()
//	{
//		//TODO: This method should calculate IO_Mode based on the number of execution plans,
//		//number of PCRS for each leaf and number of indexed elements for each PCR		
//		
//		if (exactPlansNo * exactPlansNo * exactPlansNo <= estimatedPlansNo)
//		{
//			this.IO_Mode = RG.IO_ModeType.DIRECT;
//		}
//		else
//		{
//			this.IO_Mode = RG.IO_ModeType.INDIRECT;
//		}
//		
//		//this.IO_Mode = IO_ModeType.INDIRECT;
//		this.IO_Mode = RG.IO_ModeType.DIRECT;
//		
//		if (this.IO_Mode == RG.IO_ModeType.INDIRECT)
//		{
//			System.out.println("RG is executing with INDIRECT I/O mode");
//		}
//		else if (this.IO_Mode == RG.IO_ModeType.DIRECT)
//		{
//			System.out.println("RG is executing with DIRECT I/O mode");
//		}
//		else
//		{
//			System.err.println("Not supported I/O mode for RG. DIRECT I/O mode is selected instead");
//			this.IO_Mode = RG.IO_ModeType.DIRECT;
//		}
//		return this.IO_Mode;
//	}
//	
//	private void prepareIndirectInputs() throws DBException
//	{
//		for (QTPNode node : leafNodes) 
//		{
//			HashMap<Integer,ArrayList<QueryTuple>> leafNodesIndirectInputs = new HashMap<Integer, ArrayList<QueryTuple>>();
//			IndirectInputs.put(node, leafNodesIndirectInputs);
//			
//			HashSet<Integer> pcrSet = targetPCRS.get(node);
//			for (Integer pcr: pcrSet)
//			{
//				XTCxqueryOperator inputStream;
//				//String name = context.getMetaDataMgr().resolveVocID(RG.this.transaction, RG.this.doc.getID(), psNode.getVocID());
//				String name = node.getName();
//				//inputStream = new XTCxqueryPCRScanOp(RG.this.context, name, psNode.getPCR(), RG.this.splid);
//				inputStream = new XTCxqueryPCRScanOp(RG.this.context, RG.this.idxNo, node, pcr, node.getName());
//				inputStream.open();
//				QueryTuple tuple = inputStream.next();
//				ArrayList<QueryTuple> indirectInput = leafNodesIndirectInputs.get(pcr);
//				if (indirectInput == null)
//				{
//					//TODO: It is better to estimate a better size for indirectInput
//					indirectInput = new ArrayList<QueryTuple>(10);
//					leafNodesIndirectInputs.put(pcr, indirectInput);
//				}
//				while (tuple != null)
//				{
//					indirectInput.add(tuple);
//					tuple = inputStream.next();
//				}
//				inputStream.close();
//			}
//		} //for		
//	}
//	
//	private void prepareIndirectInputs0() throws DBException
//	{
//		for (QTPNode node : leafNodes) 
//		{
//			HashMap<Integer,ArrayList<QueryTuple>> leafNodesIndirectInputs = new HashMap<Integer, ArrayList<QueryTuple>>();
//			IndirectInputs.put(node, leafNodesIndirectInputs);
//			XTCxqueryElementScanOp input = new XTCxqueryElementScanOp(doc, idxNo, node, node.getName(), context);
//			input.open();
//			
//			QueryTuple tuple = input.next();
//			while (tuple != null)
//			{			
//				int PCR = tuple.getIDForPos(0).getPCR();
//				if (targetPCRS.get(node).contains(PCR))
//				{
//					ArrayList<QueryTuple> indirectInput = leafNodesIndirectInputs.get(PCR);
//					if (indirectInput == null)
//					{
//						//TODO: It is better to estimate a better size for indirectInput
//						indirectInput = new ArrayList<QueryTuple>(10);
//						leafNodesIndirectInputs.put(PCR, indirectInput);
//					}
//					indirectInput.add(tuple);
//				}
//				tuple = input.next();
//			} //while
//			
//			input.close();
//		} //for		
//	}
//
//    public RGResultSet execute() throws DBException
//	{	
//		///commented
////            estimatedPlansNo = this.estimateNumberOfPlans();
////		System.out.println(estimatedPlansNo + " plans are estimated!");
////		if ( estimatedPlansNo> 100000000)
////		{
////			//throw new DBException("Please choose another method for executing your Twig!!! " + estimatedPlansNo + " plans are estimated!!!");
////		}
/////end
//            
////		long s = System.currentTimeMillis();
//		this.executionPlan = getExecutionPlan();
////		long e = System.currentTimeMillis();
////		System.out.println("Fetching execution plan: " + (e-s));
//		
//		exactPlansNo = executionPlan.size();
//		System.out.println("Number of fetched plans: " + this.exactPlansNo);
//		this.chooseIO_Mode();
//		
////		ArrayList<QTPNode> nodes = QTP.getNodes();
////		targetPCRS = new HashMap<QTPNode, HashSet<Integer>>(nodes.size());
////		for (int i = 0; i < nodes.size(); i++)
////		{
////			targetPCRS.put(nodes.get(i), new HashSet<Integer>());
////		}
//		
//		targetPCRS = this.executionPlan.getTargetPCRs();
////		for (int i = 0; i < this.executionPlan.size(); i++)
////		{
////			RGExecutionPlanTuple plan = this.executionPlan.getTuple(i);
////			ArrayList<TwinObject<QTPNode,TwinObject<PathSynopsisNode, ArrayList<PathSynopsisNode>>>> leafNodesAnc = plan.getLeafNodesAnc();
////			
////			for (int j = 0; j < leafNodesAnc.size(); j++) 
////			{
////				PathSynopsisNode pathSynopsisNode = leafNodesAnc.get(j).getO2().getO1();
////				QTPNode node = leafNodesAnc.get(j).getO1();
////				HashSet<Integer> pcrSet = targetPCRS.get(node);
////				pcrSet.add(pathSynopsisNode.getPCR());
////			}	
////		}
//		
//		if (this.IO_Mode == RG.IO_ModeType.INDIRECT)
//		{
//			prepareIndirectInputs();
//		}
//		
//		////////////
//                /////Commented by ooldooz 
//                ////////////
//		//Open twigJoins if ordered output is required 
//		//if (docOrdered)
//		//{
//	//		this.twigJoinOps = new ArrayList<TwinObject<TwigJoinOp,TwigResult>>(this.executionPlan.size());
//	//		for (int i = 0; i < this.executionPlan.size(); i++) 
//	//		{
//	//			TwigJoinOp twigJoinOp = getTwigJoinOP(this.executionPlan.getTuple(i));
//	//			TwigResult twigResult = twigJoinOp.next();
//	//			if (twigResult != null) twigJoinOps.add(new TwinObject<TwigJoinOp, TwigResult>(twigJoinOp, twigResult));
//	//		}			
//	//	}
//		////////////////////////
//                ////////////////////////
//                ///////////////////////
//                
//		return new RGResultSet(this);
//		
//	} //execute()
//
//
//	public long estimateNumberOfPlans() throws DBException
//	{
//		ArrayList<QTPNode> leaves = QTP.getLeaves();
//		long estimate = 1;
//		for (QTPNode leaf: leaves)
//		{
//			int vocID = context.getMetaDataMgr().getDictionaryMgr().getVocabularyID(transaction, doc.getID(), XTCcalc.getBytes(leaf.getName()));
//			estimate = estimate * pathSynopsisMgr.getNodeCount(transaction, doc, vocID);
//		}
//		return estimate;
//
//	}
//	
//	protected TwigResult next() throws DBException
//	{
//		if (!docOrdered)
//		{
//			if(currentPlan < this.executionPlan.size())
//			{
//				if (currentTwigJoinOp == null) //First call
//				{
//					currentPlan = 0;
//					RGExecutionPlanTuple tuple = executionPlan.getTuple(this.currentPlan);
//					currentTwigJoinOp = getTwigJoinOP(tuple);
//				}
//				TwigResult res = currentTwigJoinOp.next();
//
//				if (res == null)
//				{
//					currentPlan++;
//					while (currentPlan < this.executionPlan.size()) 
//					{					
//						RGExecutionPlanTuple tuple = executionPlan.getTuple(this.currentPlan);
//						currentTwigJoinOp = getTwigJoinOP(tuple);
//						res = currentTwigJoinOp.next();
//						if (res != null) return res;
//						currentPlan++;
//					}
//				}
//				else
//				{
//					return res;
//				}
//			}
//		} //(docOrdered)
//		else
//		{
//			return nextOrdered();
//		}
//		
//		return null;
//	} //next()
//	
//	private TwigJoinOp getTwigJoinOP(RGExecutionPlanTuple tuple) throws DBException
//	{
//		//ArrayList<PathSynopsisNode> leafNodes= tuple.getleafNodes(); //nodes of Pathsynopsis related to the leaves of the QTP
//		ArrayList<PathSynopsisNode> joinPoints = tuple.getJoinPoints();			
//
//		TwigInput in1 = new RG.InputWrapper(leafNodes.get(0), tuple.getPathSynopsisNode(leafNodes.get(0)), tuple.getAnc(leafNodes.get(0)));
//		
//		if (leafNodes.size() == 1)
//		{
//			TwigJoinOp twigJoinOp = new TwigJoinOp(in1);
//			twigJoinOp.open();
//			return twigJoinOp;
//		}
//		
//		TwigInput in2 = new RG.InputWrapper(leafNodes.get(1), tuple.getPathSynopsisNode(leafNodes.get(1)), tuple.getAnc(leafNodes.get(1)));
//		
//		TwigJoinOp twigJoinOp = new TwigJoinOp(in1, in2, joinPoints.get(0).getLevel()); 
//		for(int i=2; i < leafNodes.size(); ++i)
//		{
//			TwigInput in = new RG.InputWrapper(leafNodes.get(i), tuple.getPathSynopsisNode(leafNodes.get(i)), tuple.getAnc(leafNodes.get(i)));			
//			twigJoinOp = new TwigJoinOp(twigJoinOp, in, joinPoints.get(i-1).getLevel());
//		}
//		twigJoinOp.open(); 
//		return twigJoinOp;		
//	} //getTwigJoinOP()
//
////	private TwigJoinOp getTwigJoinOP(RGExecutionPlanTuple tuple) throws DBException
////	{
////		//ArrayList<PathSynopsisNode> leafNodes= tuple.getleafNodes(); //nodes of Pathsynopsis related to the leaves of the QTP
////		ArrayList<PathSynopsisNode> joinPoints = tuple.getJoinPoints();			
////
////		ArrayList<TwinObject<QTPNode,TwinObject<PathSynopsisNode, ArrayList<PathSynopsisNode>>>> leafNodesAnc = tuple.getLeafNodesAnc();
////		
////		TwigInput in1 = new InputWrapper(leafNodesAnc.get(0).getO1(), leafNodesAnc.get(0).getO2().getO1(), leafNodesAnc.get(0).getO2().getO2());
////		TwigInput in2 = new InputWrapper(leafNodesAnc.get(1).getO1(), leafNodesAnc.get(1).getO2().getO1(), leafNodesAnc.get(1).getO2().getO2());
////		
////		TwigJoinOp twigJoinOp = new TwigJoinOp(in1, in2, joinPoints.get(0).getLevel()); 
////		for(int i=2; i < leafNodesAnc.size(); ++i)
////		{
////			TwigInput in = new InputWrapper(leafNodesAnc.get(i).getO1(), leafNodesAnc.get(i).getO2().getO1(), leafNodesAnc.get(i).getO2().getO2());			
////			twigJoinOp = new TwigJoinOp(twigJoinOp, in, joinPoints.get(i-1).getLevel());
////		}
////		twigJoinOp.open(); 
////		return twigJoinOp;		
////	} //getTwigJoinOP()
//
//	
//	private TwigResult nextOrdered() throws DBException
//	{
//		if (this.twigJoinOps.size() == 0) return null;
////		TwigResult minRes = this.twigJoinOps.get(0).getO2();
//		int min = 0;
//		TwigResult minRes = null;
//		for (int i = 0; i < this.twigJoinOps.size(); i++) 
//		{
//			TwigResult res = this.twigJoinOps.get(i).getO2();
//			if (minRes == null || this.twigResultComparator.compare(res, minRes) < 0)
//			{
//				min = i;
//				minRes = res;
//			}
//		} //for
//		
//		//set the head of twigJoinOp which its head is the minimum and should be returned
//		TwinObject<TwigJoinOp, TwigResult> minTwigJoinOp = twigJoinOps.get(min);
//		TwigResult headRes = minTwigJoinOp.getO1().next();
//		if (headRes != null)
//		{
//			this.twigJoinOps.get(min).setO2(headRes);			
//		}
//		else
//		{
//			minTwigJoinOp.getO1().close();
//			this.twigJoinOps.remove(min); //twigJoinOp[min] is finished and should be removed
//		}
//		return minRes;
//	}
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
//		////////////AT THE FIRST POSITION SO SORTING WILL WORK BECAUSE IT IS BASED ON //////////
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
//	public long getIOTime()
//	{
//		return this.IOTime;
//	}
//	
//	public long getNumberOfReadElements()
//	{
//		return this.numberOfReadElements;
//	}
//
//	public long getNumberOfExecutionPlans()
//	{
//		return exactPlansNo;
//	}
//	
//	public long getNumberOfGroupedExecutionPlans()
//	{
//		return exactPlansNo;
//	}
//
//	public String getExecutionMode()
//	{
//		if (this.IO_Mode == RG.IO_ModeType.INDIRECT)
//		{
//			return "INDIRECT";
//		}
//		else if (this.IO_Mode == RG.IO_ModeType.DIRECT)
//		{
//			return "DIRECT";
//		}
//		else
//		{
//			System.err.println("Not supported I/O mode for RG. DIRECT I/O mode is selected instead");
//			return "DIRECT";
//		}
//	}
//
//	////////////////////////////////////////////////////////////////
//	///////////////INPUTWRAPPER CLASS///////////////////////////////
//	///////////////////////////////////////////////////////////////
//	private class InputWrapper implements TwigInput 
//	{
//		//private Queue<TwigResult> leafStream;
//		//private int pos = -1;
//		//private QueryTreePattern QTP;
//		//private TJFast aTJFast;
//		private XTCxqueryPCRScanOp inputStream = null;
//		ArrayList<QueryTuple> indrectInput = null;
//		private int indirectPos = 0;
//		private QTPNode qNode;
//		private PathSynopsisNode psNode;
//		private ArrayList<Integer> ancLevels;
//		
//		public InputWrapper(QTPNode qNode, PathSynopsisNode psNode, ArrayList<PathSynopsisNode> anc)
//		{
//			//this.leafStream = leafStream;
//			//pos = 0;
////			this.QTP = QTP;
////			this.aTJFast = aTJFast;
//			this.qNode = qNode;
//			this.psNode = psNode;
//			ancLevels = new ArrayList<Integer>(anc.size());
//			for (int i = 0; i < anc.size(); i++) 
//			{
//				PathSynopsisNode p = anc.get(i); 
//				ancLevels.add(p.getLevel());
//			}
//		}
//		
//		public QTPNode getRightLeaf()
//		{
//			return qNode;
//		}
//		
//		public void close() throws DBException 
//		{
//			IOTime += inputStream.getIOTime();
//		}
//
//		private ArrayList<DeweyID> addition = new ArrayList<DeweyID>();
//		
//		public TwigResult next() throws DBException 
//		{
////			long s = System.currentTimeMillis();
//			
//			QueryTuple tuple = null;
//			
//			if (RG.this.IO_Mode == RG.IO_ModeType.DIRECT)
//			{
//				tuple =  inputStream.next();
//			}
//			else //if (RG.this.IO_Mode == IO_ModeType.INDIRECT)
//			{
//				if (indirectPos < indrectInput.size())
//				{
//					tuple = indrectInput.get(indirectPos++);
//					//Because the same indirectInput is possible to be used twice or more
//					//new tuple should be created because tuple will be changed and its change remain
//					//if the new tuple is not created
//					tuple = new QueryTuple(tuple.getIDForPos(0));
//				}
//			}
//
//			if (tuple == null) 
//			{
//				//System.out.println("Number of elemets in " + qNode.getName() + " element Index are: " + pos);
////				IOTime += System.currentTimeMillis() - s;
//				return null;
//			}
////			if (tuple.size() > 1)
////			{
////				int j = 1;
////			}			
////			IOTime += System.currentTimeMillis() - s;
//			++numberOfReadElements;
//			DeweyID leafID = tuple.getIDForPos(0);
//			addition.clear();
//			for (int i = 0; i < ancLevels.size(); i++)
//			{
//				addition.add(leafID.getAncestorDeweyID(ancLevels.get(i)));
//			}
//			tuple.addDeweyID(0, addition);
//			
//			return new TwigResult(RG.this.QTP, qNode, tuple);
//			
////			ArrayList<DeweyID> addition = new ArrayList<DeweyID>();
////			//long s = System.currentTimeMillis();
////			for (int i = 0; i < resArr.size(); i++) 
////			{
////				QueryTuple res = resArr.get(i);
////				//QueryTuple addition = new QueryTuple();
////				addition.clear();
////				for (int j = 0; j < innerNodes.size(); j++) 
////				{
////					addition.add(res.getIDForPos(plan[j][0]).getAncestorDeweyID(plan[j][1])); 
////				}
////				res.addDeweyID(0, addition);	
////			}
//
////			TwigResult res = aTJFast.next(this.qNode);
////			//System.out.println("Node " + qNode.getName() + " is requested. Result is:" + res);
////			return res;
////			if (pos == pathResults.size()) return null;
////			//TwigResult res = new TwigResult(QTP, pathResults.get(pos));
////			TwigResult res = pathResults.get(pos);
////			++pos;
////			return res;
//		}
//
//		public void open() throws DBException 
//		{
//			if (IO_Mode == RG.IO_ModeType.DIRECT)
//			{
//				String name = context.getMetaDataMgr().resolveVocID(RG.this.transaction, RG.this.doc.getID(), psNode.getVocID());
//				//SimplePathExpr pathExpr = new SimplePathExpr("//" + name);
//				//inputStream = new XTCxqueryPathScanOverElementIndexOp (context.getLocator(), name, pathExpr, false, context);
//				inputStream = new XTCxqueryPCRScanOp(RG.this.context, RG.this.idxNo, (QTPNodeConstraint)qNode, psNode.getPCR(), name);
//				inputStream.open();
//			}
//			else //if (IO_Mode == IO_ModeType.INDIRECT)
//			{
//				HashMap<Integer,ArrayList<QueryTuple>> leafNodesIndirectInputs = IndirectInputs.get(qNode);
//				indrectInput = leafNodesIndirectInputs.get(psNode.getPCR());
//				indirectPos = 0;
//			}
//		}
//
//	} //private class InputWrapper
//	////////////////////////////////////////////////////////////////
//	///////////////////////////////////////////////////////////////
//	///////////////////////////////////////////////////////////////
//
//	////////////////////////////////////////////////////////////////
//	////////////////////TWIGRESULTCOMPARATOR///////////////////////
//	///////////////////////////////////////////////////////////////
//	private class TwigResultComparator implements Comparator<TwigResult>
//	{
//		private ArrayList<QTPNode> nodes;
//		public TwigResultComparator(ArrayList<QTPNode> nodes)
//		{
//			this.nodes = nodes;
//		}
//		
//		public int compare(TwigResult t1, TwigResult t2)
//		{
//			for (int i = 0; i < nodes.size(); i++) 
//			{
//				QTPNode node = nodes.get(i);
//				DeweyID d1 = t1.getRes(node);
//				DeweyID d2 = t2.getRes(node);
//				int c = d1.compareTo(d2);
//				if (c != 0) return c;
//			}
//			return 0;
//		}
//	}
//	
//	////////////////////////////////////////////////////////////////
//	///////////////////////////////////////////////////////////////
//	///////////////////////////////////////////////////////////////
//
//}

///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//
//package xmlProcessor.RG.RG01;
//import indexManager.Iterators.ElementIterator;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Comparator;
//import java.util.HashMap;
//import java.util.HashSet;
//import xmlProcessor.DBServer.DBException;
////import xtc.server.accessSvc.XTClocator;
//import indexManager.StructuralSummaryManager;
//import indexManager.StructuralSummaryManager01;
//import indexManager.StructuralSummaryNode;
//import java.io.IOException;
//import logManager.LogManager;
//import xmlProcessor.DeweyID;
////import xtc.server.taSvc.XTCtransaction;
//
////import xtc.server.util.XTCcalc;
//import xmlProcessor.DBServer.utils.SvrCfg;
////import xtc.server.xmlSvc.XTCxqueryContext;
////import xtc.server.xmlSvc.xqueryOperators.PathIdxBased.XTCxqueryPCRScanOp;
////do we need them?!
//import xmlProcessor.DBServer.operators.twigs.QueryStatistics;;
//import xmlProcessor.DBServer.operators.twigs.QueryRGExtendedStatistics;
////common with other RG s?:
//import xmlProcessor.RG.executionPlan.RGExecutionPlan;
//import xmlProcessor.RG.executionPlan.RGExecutionPlanTuple;
//
//
////import xtc.server.xmlSvc.xqueryOperators.Twigs.RG.executionPlan.RGGroupedExecutionPlanTuple;
//import xmlProcessor.QTP.QTPNode;
//import xmlProcessor.QTP.QueryTreePattern;
//import xmlProcessor.DBServer.utils.QueryTuple;
//
//public class RG0001 
//{
//    
///**
// * Main class for processing a Twig with RG(ResultGraph) method OS3
// *
// * @author Kamyar
// */
//
//
///**
// *
// * @author Kamyar
// */
//public class RG implements QueryStatistics, QueryRGExtendedStatistics {
//
//    private QueryTreePattern QTP;
//    private RGExecutionPlan executionPlan;
//    private StructuralSummaryManager ssMgr;
//    private ArrayList<TwigResult> result;
//    private ArrayList<TwigJoinMultiplexerOp> twigJoinOps = null;
//    private TwigResultComparator twigResultComparator = null;
////	boolean splid;
//    private String indexName;
//    private ArrayList<QTPNode> leafNodes;
//    private long estimatedPlansNo;
//    private long exactPlansNo;
//    private long groupedPlanNo;
//    private HashMap<QTPNode, HashSet<Integer>> targetCIDs = null;
//    private long numberOfReadElements;
//    private long IOTime;
//
//    /**
//     * NOTICE: Locator should be set in the context before making new object
//     *
//     *
//     */
//    public RG(QueryTreePattern QTP, String indxName) throws DBException {
//        this.numberOfReadElements = 0;
//        this.IOTime = 0;
//
//        this.indexName = indxName;
//        this.ssMgr = new StructuralSummaryManager01();
//        this.QTP = QTP;
//        this.executionPlan = null;
//        this.result = new ArrayList<TwigResult>(SvrCfg.initialIteratorSize);
//        //this.docOrdered = true; 
//        //if (docOrdered) this.twigResultComparator = new TwigResultComparator(QTP.getNodes());
//        this.twigResultComparator = new TwigResultComparator(QTP.getNodes());
//        this.leafNodes = QTP.getLeaves();
//
//        ////////////////////////////////
//        /////////////////////////////////
//
////    	ArrayList<QueryTreePattern> parsedQTPs = QTPParser.Parse(QTP, QTP.root());
////    	for (QueryTreePattern parsedQTP : parsedQTPs)
////		{
////			System.out.println(parsedQTP.toString());
////		}
//        ////////////////////////////////////
//
//    }
//
//    /////////////////////////////////////////////////////////////////////////////////
//    ///////////////////////////////////////////////////////////////////////////////
//    //////////////////////////////////////////////////////////////////////////////
//    /**
//     * RGResult is used to manage the process of merging input streams of the
//     * leaves of the QTP
//     */
//    private class RGResultComposer {
//
//        ArrayList<QueryTuple> res;
//        int lPos; //position off last data in XTCqueryTuple data
//
//        public RGResultComposer(ElementIterator firstInput) throws DBException, IOException {
//            res = new ArrayList<QueryTuple>(SvrCfg.initialIteratorSize);
//            DeweyID nextDeweyID=firstInput.next();
//            QueryTuple data=null;
//            if(nextDeweyID!=null)
//                data= new QueryTuple(nextDeweyID);
//            while (data != null) {
//                res.add(data);
//                nextDeweyID=firstInput.next();
//                if(nextDeweyID!=null)
//                    data = new QueryTuple(firstInput.next());				
//
//    }
//            this.lPos = 0;
//        }
//
//        public void mergeWithLast(ElementIterator input, int compareLevel) throws DBException, IOException {
//            int r = 0;
//            ArrayList<QueryTuple> rSub = new ArrayList<QueryTuple>();
//            ArrayList<QueryTuple> iSub = new ArrayList<QueryTuple>();
//            ArrayList<QueryTuple> newRes = new ArrayList<QueryTuple>(SvrCfg.initialIteratorSize);
//            DeweyID nextDeweyID=input.next();
//            QueryTuple inp=null;
//            if(nextDeweyID!=null)
//                inp= new QueryTuple(nextDeweyID);
//           
//            while ((r < res.size()) && (inp != null)) {
//                QueryTuple rBase = res.get(r);
//                int compare = rBase.getIDForPos(lPos).compareTo(compareLevel, inp.getIDForPos(0));
//                if (compare == 0) //they are the same up to compareLevel
//                {
//                    rSub.clear();
//                    iSub.clear();
//                    //while following items are the same as rBase up to compareLevel
//                    //index (lPos) used to retrieve last ID that has been added
//                    while ((r < res.size()) && (res.get(r).getIDForPos(lPos).compareTo(compareLevel, rBase.getIDForPos(0)) == 0)) {
//                        rSub.add(res.get(r));
//                        ++r;
//                    }
//
//                    //while following items are the same as (inp) up to compareLevel
//                    QueryTuple tInp; //Temp inp
//                    DeweyID tInpCore;
//                    do {
//                        iSub.add(inp);
//                        tInp = inp; //Temp inp
//                        tInpCore=input.next();
//                        if(tInpCore!=null)
//                            inp =new QueryTuple(tInpCore);
//                    } while ((inp != null) && (tInp.getIDForPos(0).compareTo(compareLevel, inp.getIDForPos(0)) == 0));//do
//
//                    //X-product of rSub and iSub
//                    for (QueryTuple rTuple : rSub) {
//                        for (QueryTuple iTuple : iSub) {
//                            //merge and add to the newRes
//                            newRes.add(QueryTuple.merge(rTuple, iTuple));
//                        } //for					
//                    } //for
//                } //if
//                else if (compare < 0) {
//                    ++r;
//                } else //compare > 0
//                {
//                    
//                   nextDeweyID= input.next();
//                   if(nextDeweyID==null)
//                       inp=null;
//                   else
//                       inp=new QueryTuple(nextDeweyID);
//                }//if compare					
//            } //while ((r < res.size()) && (inp != null))
//
//            //change to the new result
//            lPos++;
//            res = newRes;
//        }
//
//        public ArrayList<QueryTuple> getRes() {
//            return res;
//        }
//    } //class RGResult
//    /////////////////////////////////////////////////////////////////////////////////
//    ///////////////////////////////////////////////////////////////////////////////
//    //////////////////////////////////////////////////////////////////////////////
//
//    public RGResultSet execute() throws DBException, IOException 
//    {
//        ////commented by ouldouz
////            estimatedPlansNo = this.estimateNumberOfPlans();
////		System.out.println(estimatedPlansNo + " plans are estimated!");
////		if ( estimatedPlansNo> 100000000)
////		{
////			//throw new DBException("Please choose another method for executing your Twig!!! " + estimatedPlansNo + " plans are estimated!!!");
////		}
/////// ended
////		long s = System.currentTimeMillis();
//        this.executionPlan = getExecutionPlan();
////		long e = System.currentTimeMillis();
//        //System.out.println("Fetching execution plan: " + (e-s));
//
//        exactPlansNo = executionPlan.size();
//        System.out.println("Number of fetched plans: " + this.exactPlansNo);
//        if (exactPlansNo == 0) {
//            this.twigJoinOps = new ArrayList<TwigJoinMultiplexerOp>();
//            return new RGResultSet(this);
//
//        }
//        //Set targetCIDs
//        //targetCIDs calculates the number of distinct PCRs for all
//        //nodes while some of them are not really target!!!
//        targetCIDs = this.executionPlan.getTargetCIDs();
//
//        //Grouping plans to reduce disk access
//        ArrayList<RGGroupedExecutionPlanTuple> groupedExecutionTuples = this.groupExecutionPlanTuples();
//
//        this.groupedPlanNo = groupedExecutionTuples.size();
//        System.out.println("Number of groupd plans: " + this.groupedPlanNo);
//
//        this.twigJoinOps = new ArrayList<TwigJoinMultiplexerOp>(groupedExecutionTuples.size());
//        for (int i = 0; i < groupedExecutionTuples.size(); i++) {
//            TwigJoinMultiplexerOp twigJoinMultiplexerOp = new TwigJoinMultiplexerOp(QTP, groupedExecutionTuples.get(i),indexName);
//            twigJoinMultiplexerOp.open();
//            if (!twigJoinMultiplexerOp.isFinished()) {
//                twigJoinOps.add(twigJoinMultiplexerOp);
//            } else {
//                this.IOTime += twigJoinMultiplexerOp.getIOTime();
//                this.numberOfReadElements += twigJoinMultiplexerOp.getNumberOfReadElements();
//            }
//        }
//
//        return new RGResultSet(this);
//
//    } //execute()
//
//    private ArrayList<RGGroupedExecutionPlanTuple> groupExecutionPlanTuples() {
//        //Comparator class to sort plans based on the cardinality of pathsynopsis nodes related to the
//        //leaves of QTP
//        final class TargetCIDsComparator implements Comparator<QTPNode> {
//
//            private HashMap<QTPNode, HashSet<Integer>> targetCIDs;
//
//            public TargetCIDsComparator(HashMap<QTPNode, HashSet<Integer>> targetCIDs) {
//                this.targetCIDs = targetCIDs;
//            }
//
//            public int compare(QTPNode q1, QTPNode q2) {
//                int c1 = targetCIDs.get(q1).size();
//                int c2 = targetCIDs.get(q2).size();
//                return (c1 < c2 ? -1 : (c1 == c2 ? 0 : 1));
//            }
//        } //final class TargetCIDsComparator
//        ////////////////////////////////////One Group:
//        if(1==1)
//        {
//           ArrayList<RGGroupedExecutionPlanTuple> groupedExecutionTuples = new ArrayList<RGGroupedExecutionPlanTuple>();
//          for (int i = 0; i < executionPlan.size(); i++) 
//          {
//            
//            ArrayList<RGExecutionPlanTuple> execPlanTuplesGroup = null;
//
//            execPlanTuplesGroup = new ArrayList<RGExecutionPlanTuple>();
//            HashMap<QTPNode,StructuralSummaryNode> X=this.executionPlan.getTuple(i).getStructuralSummaryNode();
////            if(i==0)
//                LogManager.log(7,X.toString());
//            ArrayList<Integer> CIDs=new ArrayList<>();
//            for( QTPNode qtpNode: X.keySet())
//            {
//                CIDs.add(X.get(qtpNode).getCID());
//            }
//            execPlanTuplesGroup.add(this.executionPlan.getTuple(i));
//
//            groupedExecutionTuples.add(new RGGroupedExecutionPlanTuple(QTP, execPlanTuplesGroup));
//          }
//          return groupedExecutionTuples;
//       }
///////////////////////////////////////////////
//        if (this.executionPlan.size() == 1) {
//            ArrayList<RGGroupedExecutionPlanTuple> groupedExecutionTuples = new ArrayList<RGGroupedExecutionPlanTuple>();
//            ArrayList<RGExecutionPlanTuple> execPlanTuplesGroup = null;
//
//            execPlanTuplesGroup = new ArrayList<RGExecutionPlanTuple>();
//            execPlanTuplesGroup.add(this.executionPlan.getTuple(0));
//
//            groupedExecutionTuples.add(new RGGroupedExecutionPlanTuple(QTP, execPlanTuplesGroup));
//
//            return groupedExecutionTuples;
//
//        }
//
//        QTPNode[] sortedLeafNodes = new QTPNode[this.leafNodes.size()];
//        sortedLeafNodes = leafNodes.toArray(sortedLeafNodes);
//
//        Arrays.sort(sortedLeafNodes, new TargetCIDsComparator(this.targetCIDs));
//
//        //Sort Plans
//        RGExecutionPlanTuple[] execPlanTuples = this.executionPlan.getTuples();
//        Arrays.sort(execPlanTuples, new RGExecutionPlanTupleComparator(sortedLeafNodes));
//
//        //Grouping
//        ArrayList<RGGroupedExecutionPlanTuple> groupedExecutionTuples = new ArrayList<RGGroupedExecutionPlanTuple>();
//        ArrayList<RGExecutionPlanTuple> execPlanTuplesGroup = null;
//
//        execPlanTuplesGroup = new ArrayList<RGExecutionPlanTuple>();
//        execPlanTuplesGroup.add(execPlanTuples[0]);
//
//        for (int i = 1; i < execPlanTuples.length; i++) {
//            if (execPlanTuples[i].getStructuralSummaryNode(sortedLeafNodes[0]).getCID() == execPlanTuples[i - 1].getStructuralSummaryNode(sortedLeafNodes[0]).getCID()) {
//                execPlanTuplesGroup.add(execPlanTuples[i]);
//            } else {
//                groupedExecutionTuples.add(new RGGroupedExecutionPlanTuple(QTP, execPlanTuplesGroup));
//
//                execPlanTuplesGroup = new ArrayList<RGExecutionPlanTuple>();
//                execPlanTuplesGroup.add(execPlanTuples[i]);
//
//            }
//        }
//        //The last group is added 
//        groupedExecutionTuples.add(new RGGroupedExecutionPlanTuple(QTP, execPlanTuplesGroup));
//
//        return groupedExecutionTuples;
//    }
////commented by ooldouz
////	private long estimateNumberOfPlans() throws DBException
////	{
////		ArrayList<QTPNode> leaves = QTP.getLeaves();
////		long estimate = 1;
////		for (QTPNode leaf: leaves)
////		{
////			int vocID = context.getMetaDataMgr().getDictionaryMgr().getVocabularyID(indexName, XTCcalc.getBytes(leaf.getName()));
////			estimate = estimate * structuralSumMgr.getNodeCount(transaction, doc, vocID);
////		}
////		return estimate;
////
////	}
////	///ended!
//
//    protected TwigResult next() throws DBException, IOException {
//        TwigResult res = nextOrdered();
//        return res;
//
//    } //next()
////	long cmptime = 0;
//    private int resCounter=0;
//    private int planNo=0;
//    private TwigResult nextOrdered() throws DBException, IOException {
//        
////		long s = System.currentTimeMillis();
//        if (this.twigJoinOps.size() == 0) {
////			System.out.println("COMPARING TIME: " + cmptime);
//            return null;
//        }
//        resCounter++;
//        int min = 0;
//        TwigResult minRes = null;
//        for (int i = 0; i < this.twigJoinOps.size(); i++) {
//            TwigResult res = this.twigJoinOps.get(i).getHead();           
//            if (minRes == null || this.twigResultComparator.compare(res, minRes) < 0) {
//                min = i;
//                minRes = res;
//            }
//        } //for
////		cmptime += System.currentTimeMillis() - s;
///////////////////////////////TO REMOVE ORDER:
////        TwigJoinMultiplexerOp minOp = twigJoinOps.get(min);
/////////////////////////////////////////////////////changed for tet
//        TwigJoinMultiplexerOp minOp = twigJoinOps.get(0);
//        ///////////added for test:
//        minRes=minOp.getHead();
//        ///////////
//        minOp.MoveNext();
//        
//        if (minOp.isFinished()) {
//            if(resCounter!=0)
//                LogManager.log(7,"Plan"+planNo +" : Results:"+resCounter);
//            minOp.close();
//            
//            planNo++;
//            resCounter=0;
//            
//            this.IOTime += minOp.getIOTime();
//            this.numberOfReadElements += minOp.getNumberOfReadElements();
//
//            this.twigJoinOps.remove(0); //twigJoinOp[min] is finished and should be removed
//        }
//        return minRes;
//    }
//    
//    /*private TwigResult nextOrdered() throws DBException, IOException {
////		long s = System.currentTimeMillis();
//        if (this.twigJoinOps.size() == 0) {
////			System.out.println("COMPARING TIME: " + cmptime);
//            return null;
//        }
//
//        int min = 0;
//        TwigResult minRes = null;
//        for (int i = 0; i < this.twigJoinOps.size(); i++) {
//            TwigResult res = this.twigJoinOps.get(i).getHead();
//            if (minRes == null || this.twigResultComparator.compare(res, minRes) < 0) {
//                min = i;
//                minRes = res;
//            }
//        } //for
////		cmptime += System.currentTimeMillis() - s;
//
//        TwigJoinMultiplexerOp minOp = twigJoinOps.get(min);
//
//        minOp.MoveNext();
//        if (minOp.isFinished()) {
//            minOp.close();
//
//            this.IOTime += minOp.getIOTime();
//            this.numberOfReadElements += minOp.getNumberOfReadElements();
//
//            this.twigJoinOps.remove(min); //twigJoinOp[min] is finished and should be removed
//        }
//        return minRes;
//    } */
//    /**
//     * Complete the results in resArr and returns resArr
//     *
//     * @param resArr
//     * @param leafNodes
//     * @param joinPoints
//     * @param innerNodes
//     * @return
//     */
//    private ArrayList<QueryTuple> produceOutput(ArrayList<QueryTuple> resArr, ArrayList<StructuralSummaryNode> leafNodes, ArrayList<StructuralSummaryNode> joinPoints, ArrayList<StructuralSummaryNode> innerNodes) {
//        return null;
//    }
//
//    public void close() throws DBException {
//        for (int i = 0; i < this.twigJoinOps.size(); i++) {
//            this.twigJoinOps.get(i).close();
//        }
//    }
//
////	public QueryTuple next() throws DBException {
////		if(pos < result.size()-1)
////		{
////			pos++;
////			return result.get(pos);
////		}
////		return null;
////	}
//    private RGExecutionPlan getExecutionPlan() throws DBException, IOException 
//    {
//        RGExecutionPlan execPlan = ssMgr.getRGExecutionPlan(indexName, QTP);
//
////			Set<QTPNode> qNodes = res.keySet();
////			for (Iterator<QTPNode> iterator2 = qNodes.iterator(); iterator2.hasNext();) {
////				QTPNode node = iterator2.next();
////				String str = "[" + node.getName() + ": " + res.get(node).getCID() + "] ";
////				System.out.print(str);				
////			}
////			System.out.println();
////		}
////		System.out.println("Number of results: " + twRes.size());
//
//        //System.out.println(structuralSumMgr.getXMLStructuralSummary(transaction, doc));
//        return execPlan;
//    }
//
//    public long getIOTime() {
//        return this.IOTime;
//    }
//
//    public long getNumberOfReadElements() {
//        return this.numberOfReadElements;
//    }
//
//    public long getNumberOfExecutionPlans() {
//        return exactPlansNo;
//    }
//
//    public long getNumberOfGroupedExecutionPlans() {
//        return groupedPlanNo;
//    }
//
//    public String getExecutionMode() {
//        return "DIRECT";
//    }
//    ////////////////////////////////////////////////////////////////
//    ////////////////////TWIGRESULTCOMPARATOR///////////////////////
//    ///////////////////////////////////////////////////////////////
//
//    private class TwigResultComparator implements Comparator<TwigResult> {
//
//        private ArrayList<QTPNode> nodes;
//
//        public TwigResultComparator(ArrayList<QTPNode> nodes) {
//            this.nodes = nodes;
//        }
//
//        public int compare(TwigResult t1, TwigResult t2) {
//            for (int i = 0; i < nodes.size(); i++) {
//                QTPNode node = nodes.get(i);
//                DeweyID d1 = t1.getRes(node);
//                DeweyID d2 = t2.getRes(node);
//                int c = d1.compareTo(d2);
//                if (c != 0) {
//                    return c;
//                }
//            }
//            return 0;
//        }
//    }
//    ////////////////////////////////////////////////////////////////
//    ///////////////////////////////////////////////////////////////
//    ///////////////////////////////////////////////////////////////
//}
//
//    
//}

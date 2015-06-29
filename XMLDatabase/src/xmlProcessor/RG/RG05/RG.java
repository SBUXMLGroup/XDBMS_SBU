
package xmlProcessor.RG.RG05;


/**z
 * Designed for extending S3 for logical operator OR 
 * It has but do not use shared input but uses correlated plans
 * 
 *  
 * 
 *  @author Kamyar
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

import xmlProcessor.DBServer.DBException;
//import xtc.server.accessSvc.XTClocator;
import indexManager.StructuralSummaryManager;
import indexManager.StructuralSummaryManager01;
import indexManager.StructuralSummaryNode;
import java.io.IOException;
import xmlProcessor.DeweyID;
//import xtc.server.taSvc.XTCtransaction;
import xmlProcessor.DBServer.utils.TwinObject;
import xmlProcessor.DBServer.utils.SvrCfg;
//import xtc.server.xmlSvc.XTCxqueryContext;
import xmlProcessor.DBServer.operators.QueryOperator;
//import xtc.server.xmlSvc.xqueryOperators.PathIdxBased.XTCxqueryPCRScanOp;
import indexManager.Iterators.ElementIterator;
import xmlProcessor.DBServer.operators.twigs.QueryStatistics;
import xmlProcessor.DBServer.operators.twigs.QueryRGExtendedStatistics;
import xmlProcessor.RG.RG05.EvaluationTree.EvaluationTree;
import xmlProcessor.RG.RG05.EvaluationTree.EvaluationTreeNode;
import xmlProcessor.RG.RG05.EvaluationTree.EvaluationTreeNodeInput;
import xmlProcessor.RG.RG05.EvaluationTree.SelfInput;
import xmlProcessor.RG.executionPlan.RGExecutionPlan;
import xmlProcessor.RG.executionPlan.RGExecutionPlanTuple;
//import xtc.server.xmlSvc.xqueryOperators.Twigs.RG.executionPlan.RGGroupedExecutionPlanTuple;
import xmlProcessor.QTP.QTPNode;
import xmlProcessor.QTP.QueryTreePattern;
import xmlProcessor.QTP.QTPNode.OpType;
import xmlProcessor.DBServer.utils.QueryTuple;

/**
 * 
 *  @author Kamyar
 */
public class RG implements QueryStatistics, QueryRGExtendedStatistics
{
	//private XTCxqueryContext context;
	//private XTClocator doc;
        private String indexName;
	private QueryTreePattern QTP;
	//private RGExecutionPlan executionPlan;
	//private XTCtransaction transaction;
	private StructuralSummaryManager ssMgr;
	private ArrayList<TwigResult> result;
	private ArrayList<EvaluationTree> evaluationTrees = null;  
	private TwigResultComparator twigResultComparator = null; 
//	boolean splid;
	private int idxNo;
	//private ArrayList<QTPNode> leafNodes;
	private long estimatedPlansNo;
	private long exactPlansNo;
	//private long groupedPlanNo;
	private int parsedQTPNo;
	private int correlatedPlansNo;
	//private HashMap<QTPNode, HashSet<Integer>> targetPCRs = null;
	private long numberOfReadElements;
	private long IOTime;
	private HashMap<Integer, SharedInput> openedStreams = new HashMap<Integer, SharedInput>();
	private QTPNode extractionPoint;
	private boolean useSharedInput = false;
	private ArrayList<QTPNode> outputNodes;
	
	/**
	 * NOTICE: Locator should be set in the context before making new object
	 * 
	 * */
	
	public RG(QueryTreePattern QTP,String indexName/*, boolean docOrdered*/) throws DBException
	{
		this.numberOfReadElements = 0;
		this.IOTime = 0;
//		this.context = context;
//		this.doc = context.getLocator();	
                this.indexName=indexName;
		//if (doc == null) throw new DBException("Null locator in the context.");
//		this.splid = false; //TODO: It works with PCR clustering! no meta-data available now!!!
		//this.idxNo = idxNo;
		//this.transaction = context.getTransaction();
		this.ssMgr =StructuralSummaryManager01.getInctance();
		this.QTP = QTP;
		this.extractionPoint = QTP.getExtractionPoint();
		//this.executionPlan = null;
		this.result = new ArrayList<TwigResult>(SvrCfg.initialIteratorSize);
		//this.docOrdered = true; 
		//if (docOrdered) this.twigResultComparator = new TwigResultComparator(QTP.getNodes());
		this.outputNodes = QTP.getOutputNodes();
		this.twigResultComparator = new TwigResultComparator(this.outputNodes);
		//this.leafNodes = QTP.getLeaves();
		//this.targetPCRs = new HashMap<QTPNode, HashSet<Integer>>();
		

	}

	/////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	/**
	 *  RGResult is used to manage the process of merging input streams of the leaves of the QTP  
	 */
	private class RGResultComposer
	{
		ArrayList<QueryTuple> res;
		int lPos; //position off last data in XTCqueryTuple data
		public RGResultComposer(ElementIterator firstInput) throws DBException, IOException
		{
			res = new ArrayList<QueryTuple>(SvrCfg.initialIteratorSize);
                        DeweyID nextDID;
                        QueryTuple data=null;
                        nextDID=firstInput.next();
                        if(nextDID!=null)
                        {
                            data=new QueryTuple(nextDID);
                        }
			while (data != null)
			{
				res.add(data);
                                
				nextDID = firstInput.next();
                                if(nextDID!=null)
                               {
                                  data=new QueryTuple(nextDID);
                               }
                                
			}
			this.lPos = 0;
		}
		
		public void mergeWithLast(ElementIterator input, int compareLevel) throws DBException, IOException
		{
			int r = 0;
			ArrayList<QueryTuple> rSub = new ArrayList<QueryTuple>();
			ArrayList<QueryTuple> iSub = new ArrayList<QueryTuple>();
			ArrayList<QueryTuple> newRes = new ArrayList<QueryTuple>(SvrCfg.initialIteratorSize);
						
			QueryTuple inp=null;
                        DeweyID nextDID;
                        nextDID= input.next();
                        if(nextDID!=null)
                            inp=new QueryTuple(nextDID);
			while ((r < res.size()) && (inp != null))
			{
				QueryTuple rBase = res.get(r);
				int compare = rBase.getIDForPos(lPos).compareTo(compareLevel, inp.getIDForPos(0));
				if( compare == 0) //they are the same up to compareLevel
				{
					rSub.clear();
					iSub.clear();
					//while following items are the same as rBase up to compareLevel
					//index (lPos) used to retrieve last ID that has been added
					while ((r < res.size()) && (res.get(r).getIDForPos(lPos).compareTo(compareLevel, rBase.getIDForPos(0)) == 0))
					{
						rSub.add(res.get(r));
						++r;
					}

					//while following items are the same as (inp) up to compareLevel
					QueryTuple tInp; //Temp inp
					do 
					{
						iSub.add(inp);
						tInp = inp; //Temp inp
						nextDID= input.next();
                                                if(nextDID!=null)
                                                    inp=new QueryTuple(nextDID);
					} while ((inp != null) && (tInp.getIDForPos(0).compareTo(compareLevel, inp.getIDForPos(0)) == 0));//do
					
					//X-product of rSub and iSub
					for (QueryTuple rTuple : rSub)
					{
						for (QueryTuple iTuple : iSub)
						{
							//merge and add to the newRes
							newRes.add(QueryTuple.merge(rTuple, iTuple));													
						} //for					
					} //for
				} //if
				else if (compare < 0)
				{
					++r;
				}
				else //compare > 0
				{
					nextDID= input.next();
                                        if(nextDID!=null)
                                            inp=new QueryTuple(nextDID);
				}//if compare					
			} //while ((r < res.size()) && (inp != null))
			
			//change to the new result
			lPos++;
			res = newRes;
		}
		
		public ArrayList<QueryTuple> getRes()
		{
			return res;			 
		}
	} //class RGResult
	/////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////

    public RGResultSet execute() throws DBException, IOException
	{	
            //commented by ouldouz,should be then uncommented:
		//estimatedPlansNo = this.estimateNumberOfPlans();
            
//		System.out.println(estimatedPlansNo + " plans are estimated!");
//		if ( estimatedPlansNo> 100000000)
//		{
//			//throw new DBException("Please choose another method for executing your Twig!!! " + estimatedPlansNo + " plans are estimated!!!");
//		}
            //end of comented by ouldouz

		//Parse QTP to produce parsed ones which only have and operator
		ArrayList<QueryTreePattern> parsedQTPs = QTPParser.Parse(QTP, QTP.root());
		//get execution plan of each parsed tree
		ArrayList<TwinObject<QueryTreePattern, RGExecutionPlan>> executionPlans = new ArrayList<TwinObject<QueryTreePattern,RGExecutionPlan>>();
		RGExecutionPlan executionPlan;
                ///////////////////////////////////////////
		for (QueryTreePattern parsedQTP: parsedQTPs)
		{
			executionPlan = this.getExecutionPlan(parsedQTP);
			if (executionPlan.size() > 0)
			{
				this.exactPlansNo += executionPlan.size(); 
				executionPlans.add(new TwinObject<QueryTreePattern, RGExecutionPlan>(parsedQTP, executionPlan));
			}
		}
                //////////////////////////////////////////////////////
//		executionPlan = this.getExecutionPlan(parsedQTPs.get(0));
//                if (executionPlan.size() > 0)
//                {
//                        this.exactPlansNo += executionPlan.size(); 
//                        executionPlans.add(new TwinObject<QueryTreePattern, RGExecutionPlan>(parsedQTPs.get(idxNo), executionPlan));
//                }
                
		//if nothing happen!
		if (executionPlans.size() == 0)
		{
			this.evaluationTrees = new ArrayList<EvaluationTree>();
			return new RGResultSet(this);			
		}
		
		//Create Correlated Plans
		ArrayList<CorrelatedPlans> correlatedPlans = new ArrayList<CorrelatedPlans>();
		RGExecutionPlanTuple NCPlan;
		
		TwinObject<QueryTreePattern, RGExecutionPlan>[] sortedExecutionPlans = new TwinObject[executionPlans.size()];
		sortedExecutionPlans = executionPlans.toArray(sortedExecutionPlans);
		Arrays.sort(sortedExecutionPlans, new ExecutionPlanSizeComparator());
		for (int i = sortedExecutionPlans.length - 1; i >= 0 ; i--)
		{
			executionPlans.set(sortedExecutionPlans.length - i - 1, sortedExecutionPlans[i]);
		}
		for (int j = 0; j < executionPlans.size(); ++j)
		{
			for (int i = 0; i < executionPlans.get(j).getO2().size(); ++i)
			{
				boolean found = false;
				for (int k = 0; k < correlatedPlans.size(); k++)
				{
					
					NCPlan = executionPlans.get(j).getO2().getTuple(i);
					if (correlatedPlans.get(k).AddIfIsCorrelatedWith(NCPlan))
					{
						//correlatedPlans.get(k).Add(plan);
						found = true;
						break;
					}					
				}
				if (!found)
				{

                                    NCPlan = executionPlans.get(j).getO2().getTuple(i);
					correlatedPlans.add(new CorrelatedPlans(NCPlan));
				}
			}
		}

		this.correlatedPlansNo = correlatedPlans.size();
		
		
//		
//		
//		//Grouping correlated plans based on targetPCRs to reduce disk access
//		ArrayList<RGGroupedCorrelatedPlansTuple> groupedCorrelatedPlansTuples = this.groupCorrelatedExecutionPlans(correlatedPlans, targetPCRs);
		
		
		this.parsedQTPNo = parsedQTPs.size();
		System.out.println("Number of parsed QTPs: " + this.parsedQTPNo);		
		
		System.out.println("Number of fetched plans: " + this.exactPlansNo);
		System.out.println("Number of correlated plans: " + this.correlatedPlansNo);		
		

		this.evaluationTrees = new ArrayList<EvaluationTree>();
		
		for (int i = 0; i < correlatedPlansNo; i++)
		{
			EvaluationTreeNodeInput root = getEvaluationTree(QTP, QTP.root(), correlatedPlans.get(i),indexName);
			EvaluationTree evaluationTree = new EvaluationTree(root);
			evaluationTrees.add(evaluationTree);
                        /////added:
                        if(i==0)
                            logManager.LogManager.log(7,correlatedPlans.get(i).getTargetPCRs().toString());
                        ///////////
		}
		
		for (int j = 0; j < evaluationTrees.size(); )
		{
			EvaluationTree evaluationTree = this.evaluationTrees.get(j);			
                        //EvaluationTree evaluationTree = this.evaluationTrees.get(11);	
			evaluationTree.open();
                       
			
			if (evaluationTree.isFinished())
			{
                                //isFinished means: it has not produced any results.its head is null so it has to be removed from the result set.
				this.IOTime += evaluationTree.getIOTime();
				this.numberOfReadElements += evaluationTree.getNumberOfReadElements();
				this.evaluationTrees.remove(j);
			}
			else
			{
				++j;
			}
		}
			
		return new RGResultSet(this);
		
	} //execute()

    private EvaluationTreeNodeInput getEvaluationTree(QueryTreePattern QTP, QTPNode root, CorrelatedPlans correlatedPlan,String indexName) throws DBException, IOException
    {    	
    	EvaluationTreeNode evaluationTreeRootNode;
    	ArrayList<TwinObject<QTPNode, Integer>> qNodes = new ArrayList<TwinObject<QTPNode,Integer>>();
    	if (root.getParent() != null)
    	{
    		StructuralSummaryNode psNode = correlatedPlan.getStructuralSummaryNode(root.getParent());
			int level=0;
			if (psNode != null)
			{
				level = psNode.getLevel();
				qNodes.add(new TwinObject<QTPNode, Integer>(root.getParent(), level));
			}			    		
    	}
    	if (!root.hasChildren())
    	{
    		InputWrapper inputWrapper;
    		inputWrapper = new InputWrapper(QTP, root, correlatedPlan.getStructuralSummaryNode(root), indexName, openedStreams, useSharedInput);    		
    		evaluationTreeRootNode = new EvaluationTreeNode(qNodes, root);
    		evaluationTreeRootNode.addChild(new SelfInput(inputWrapper), false);
    		return evaluationTreeRootNode;
    	}
    	ArrayList<QTPNode> children;
    	children = root.getChildren();
    	if (children.size() == 1)
    	{
    		QTPNode node = root;
    		StructuralSummaryNode psNode = correlatedPlan.getStructuralSummaryNode(node);
			int level=0;
			if (psNode != null)
			{
				level = psNode.getLevel();
				qNodes.add(new TwinObject<QTPNode, Integer>(node, level));
			}			    		

    		//qNodes.add(new TwinObject<QTPNode, Integer>(node, correlatedPlan.getStructuralSummaryNode(node).getLevel()));
    		if (children.get(0).hasNotAxis())
    		{
    			evaluationTreeRootNode = new EvaluationTreeNode(qNodes, node);
				InputWrapper inputWrapper;
				inputWrapper = new InputWrapper(QTP, node, correlatedPlan.getStructuralSummaryNode(node), indexName, openedStreams, useSharedInput);
				evaluationTreeRootNode.addChild(new SelfInput(inputWrapper), false);
				if (this.outputNodes.indexOf(node) >= 0)
				{
					evaluationTreeRootNode.setAsuppestNOTPoint();
				}
				
				EvaluationTreeNode child = (EvaluationTreeNode)getEvaluationTree(QTP, children.get(0), correlatedPlan,indexName);
				evaluationTreeRootNode.addChild(child, true);
    		}
    		else
    		{
    			node = children.get(0);
    			children = node.getChildren();
    			while (children.size() == 1 && !children.get(0).hasNotAxis())
    			{
    				psNode = correlatedPlan.getStructuralSummaryNode(node);
    				level=0;
    				if (psNode != null)
    				{
    					level = psNode.getLevel();
    					qNodes.add(new TwinObject<QTPNode, Integer>(node, level));
    				}
    				//qNodes.add(new TwinObject<QTPNode, Integer>(node, correlatedPlan.getStructuralSummaryNode(node).getLevel()));
    				node = children.get(0);
    				children = node.getChildren();
    			}    		

    			if (children.size() == 1 && children.get(0).hasNotAxis())
    			{
    				evaluationTreeRootNode = new EvaluationTreeNode(qNodes, node);
    				InputWrapper inputWrapper;
    				inputWrapper = new InputWrapper(QTP, node, correlatedPlan.getStructuralSummaryNode(node),indexName, openedStreams, useSharedInput);
    				evaluationTreeRootNode.addChild(new SelfInput(inputWrapper), false);
    				if (this.outputNodes.indexOf(node) >= 0)
    				{
    					evaluationTreeRootNode.setAsuppestNOTPoint();
    				}

    				EvaluationTreeNode child = (EvaluationTreeNode)getEvaluationTree(QTP, children.get(0), correlatedPlan, indexName);
    				evaluationTreeRootNode.addChild(child, true);

    			}
    			else if (children.size() > 0)
    			{
    				evaluationTreeRootNode = new EvaluationTreeNode(qNodes, node);
    				psNode = correlatedPlan.getStructuralSummaryNode(node);
    				int joinLevel=0;
    				if (psNode != null)
    				{
    					joinLevel = psNode.getLevel();
    				}
    				//evaluationTreeRootNode.setJoinLevel(joinLevel);
    				boolean hasOrdinaryChild = false;
    				boolean hasNOTChild = false;
    				boolean allOrdinaryFinished = true;
    				for (int i = 0; i < node.getChildren().size(); i++)
    				{
    					EvaluationTreeNode child = (EvaluationTreeNode)getEvaluationTree(QTP, children.get(i), correlatedPlan,indexName);
    					boolean notAxis = children.get(i).hasNotAxis();
    					hasOrdinaryChild = hasOrdinaryChild || !notAxis;
    					if (!notAxis)
    					{
    						allOrdinaryFinished = allOrdinaryFinished & (correlatedPlan.getStructuralSummaryNode(children.get(i)) == null);
    					}
    					else
    					{
    						hasNOTChild = true;
    						if (this.outputNodes.indexOf(node) >= 0)
    						{
    							evaluationTreeRootNode.setAsuppestNOTPoint();
    						}

    					}
    					evaluationTreeRootNode.addChild(child, notAxis);
    				}    
    				//if (!hasOrdinaryChild || (hasOrdinaryChild && allOrdinaryFinished && node.getOpType() == OpType.OR)) //all children are not connectors so selfInput is required
    				if (!hasOrdinaryChild || (hasNOTChild && node.getOpType() == OpType.OR)) //all children are not connectors so selfInput is required
    				{
    					InputWrapper inputWrapper;
    					inputWrapper = new InputWrapper(QTP, node, correlatedPlan.getStructuralSummaryNode(node),indexName, openedStreams, useSharedInput);
    					evaluationTreeRootNode.addChild(new SelfInput(inputWrapper), false);  			
    				}
    			}
    			else //if (children.size() == 0)
    			{
    				if (node.hasNotAxis() && node.getParent().getChildren().size() == 1)
    				{
    					evaluationTreeRootNode = new EvaluationTreeNode(qNodes, node.getParent());
    					InputWrapper inputWrapper;
    					inputWrapper = new InputWrapper(QTP, node.getParent(), correlatedPlan.getStructuralSummaryNode(node.getParent()),indexName, openedStreams, useSharedInput);
    					evaluationTreeRootNode.addChild(new SelfInput(inputWrapper), false);
    					if (this.outputNodes.indexOf(node.getParent()) >= 0)
    					{
    						evaluationTreeRootNode.setAsuppestNOTPoint();
    					}

    					EvaluationTreeNodeInput child = getEvaluationTree(QTP, node, correlatedPlan,indexName);
    					evaluationTreeRootNode.addChild(child, true);
    				}
    				else
    				{
    					evaluationTreeRootNode = new EvaluationTreeNode(qNodes, node);
    					InputWrapper inputWrapper;
    					inputWrapper = new InputWrapper(QTP, node, correlatedPlan.getStructuralSummaryNode(node),indexName, openedStreams, useSharedInput);
    					evaluationTreeRootNode.addChild(new SelfInput(inputWrapper), false);
    				}
    			}
    		}
    	}
    	else
    	{
    		QTPNode node = root;
    		evaluationTreeRootNode = new EvaluationTreeNode(qNodes, node);
    		StructuralSummaryNode psNode = correlatedPlan.getStructuralSummaryNode(node);
    		int joinLevel=0;
    		if (psNode != null)
    		{
    			joinLevel = psNode.getLevel();
    		}
			//evaluationTreeRootNode.setJoinLevel(joinLevel);
    		boolean hasOrdinaryChild = false;
    		boolean hasNOTChild = false;
    		boolean allOrdinaryFinished = true; 
    		for (int i = 0; i < node.getChildren().size(); i++)
			{
        		EvaluationTreeNodeInput child = getEvaluationTree(QTP, children.get(i), correlatedPlan,indexName);
        		boolean notAxis = children.get(i).hasNotAxis();        		
        		hasOrdinaryChild = hasOrdinaryChild || !notAxis;
				if (!notAxis)
				{
					allOrdinaryFinished = allOrdinaryFinished & (correlatedPlan.getStructuralSummaryNode(children.get(i)) == null);
				}
				else
				{
					hasNOTChild = true;
					if (this.outputNodes.indexOf(node) >= 0)
					{
						evaluationTreeRootNode.setAsuppestNOTPoint();
					}

				}
        		evaluationTreeRootNode.addChild(child, notAxis);				
			}    		
    		
    		//if (!hasOrdinaryChild || (hasOrdinaryChild && allOrdinaryFinished && node.getOpType() == OpType.OR)) //all children are not connectors so selfInput is required
    		if (!hasOrdinaryChild || (hasNOTChild && node.getOpType() == OpType.OR)) //all children are not connectors so selfInput is required
    		{
				InputWrapper inputWrapper;
				inputWrapper = new InputWrapper(QTP, node, correlatedPlan.getStructuralSummaryNode(node), indexName, openedStreams, useSharedInput);
				evaluationTreeRootNode.addChild(new SelfInput(inputWrapper), false); 			
    		}
    	}
    	
		return evaluationTreeRootNode;
    }

    
    

    
    
//    private ArrayList<RGGroupedExecutionPlanTuple> groupExecutionPlanTuples()
//    {
//    	//Comparator class to sort plans based on the cardinality of pathsynopsis nodes related to the
//    	//leaves of QTP
//    	final class TargetPCRsComparator implements Comparator<QTPNode>
//    	{
//    		private HashMap<QTPNode, HashSet<Integer>> targetPCRs;
//    		
//    		public TargetPCRsComparator(HashMap<QTPNode, HashSet<Integer>> targetPCRs)
//    		{
//    			this.targetPCRs = targetPCRs;
//    		}
//    		public int compare(QTPNode q1, QTPNode q2)
//    		{
//    			int c1 = targetPCRs.get(q1).size();
//    			int c2 = targetPCRs.get(q2).size();
//    			return (c1 < c2 ? -1: (c1 == c2 ? 0: 1));
//    		}
//    	} //final class TargetPCRsComparator
//    	
//    	if (this.executionPlan.size() == 1)
//    	{
//        	ArrayList<RGGroupedExecutionPlanTuple> groupedExecutionTuples = new ArrayList<RGGroupedExecutionPlanTuple>();
//        	ArrayList<RGExecutionPlanTuple> execPlanTuplesGroup = null;
//
//    		execPlanTuplesGroup = new ArrayList<RGExecutionPlanTuple>();
//    		execPlanTuplesGroup.add(this.executionPlan.getTuple(0));
//    		
//        	groupedExecutionTuples.add(new RGGroupedExecutionPlanTuple(QTP, execPlanTuplesGroup));
//        	
//        	return groupedExecutionTuples;
//
//    	}
//    	
//    	QTPNode[] sortedLeafNodes = new QTPNode[this.leafNodes.size()];
//    	sortedLeafNodes = leafNodes.toArray(sortedLeafNodes);
//    	
//    	Arrays.sort(sortedLeafNodes, new TargetPCRsComparator(this.targetPCRs));
//    	
//    	//Sort Plans
//    	RGExecutionPlanTuple[] execPlanTuples = this.executionPlan.getTuples();
//    	Arrays.sort(execPlanTuples, new RGCorrelatedPlansTupleComparator(sortedLeafNodes));
//    	
//    	//Grouping
//    	ArrayList<RGGroupedExecutionPlanTuple> groupedExecutionTuples = new ArrayList<RGGroupedExecutionPlanTuple>();
//    	ArrayList<RGExecutionPlanTuple> execPlanTuplesGroup = null;
//
//		execPlanTuplesGroup = new ArrayList<RGExecutionPlanTuple>();
//		execPlanTuplesGroup.add(execPlanTuples[0]);
//
//    	for (int i = 1; i < execPlanTuples.length; i++) 
//    	{
//    		if (execPlanTuples[i].getStructuralSummaryNode(sortedLeafNodes[0]).getPCR() == execPlanTuples[i -1].getStructuralSummaryNode(sortedLeafNodes[0]).getPCR())
//    		{
//    			execPlanTuplesGroup.add(execPlanTuples[i]);
//    		}
//    		else
//    		{
//    			groupedExecutionTuples.add(new RGGroupedExecutionPlanTuple(QTP, execPlanTuplesGroup));
//
//    			execPlanTuplesGroup = new ArrayList<RGExecutionPlanTuple>();
//    			execPlanTuplesGroup.add(execPlanTuples[i]);
//
//    		}
//    	}
//    	//The last group is added 
//    	groupedExecutionTuples.add(new RGGroupedExecutionPlanTuple(QTP, execPlanTuplesGroup));
//    	
//    	return groupedExecutionTuples;
//    }
//
//    private ArrayList<RGGroupedCorrelatedPlansTuple> groupCorrelatedExecutionPlans(ArrayList<CorrelatedPlans> correlatedPlans, HashMap<QTPNode, HashSet<Integer>> targetPCRs)
//    {
//    	//Comparator class to sort plans based on the cardinality of pathsynopsis nodes related to the
//    	//leaves of QTP
//    	final class TargetPCRsComparator implements Comparator<QTPNode>
//    	{
//    		private HashMap<QTPNode, HashSet<Integer>> targetPCRs;
//    		
//    		public TargetPCRsComparator(HashMap<QTPNode, HashSet<Integer>> targetPCRs)
//    		{
//    			this.targetPCRs = targetPCRs;
//    		}
//    		public int compare(QTPNode q1, QTPNode q2)
//    		{
//    			int c1 = targetPCRs.get(q1).size();
//    			int c2 = targetPCRs.get(q2).size();
//    			return (c1 < c2 ? -1: (c1 == c2 ? 0: 1));
//    		}
//    	} //final class TargetPCRsComparator
//    	
//    	if (correlatedPlans.size() == 1)
//    	{
//        	ArrayList<RGGroupedCorrelatedPlansTuple> groupedExecutionTuples = new ArrayList<RGGroupedCorrelatedPlansTuple>();
//        	ArrayList<CorrelatedPlans> correlatedPlansTuplesGroup = null;
//
//    		correlatedPlansTuplesGroup = new ArrayList<CorrelatedPlans>();
//    		correlatedPlansTuplesGroup.add(correlatedPlans.get(0));
//    		
//        	groupedExecutionTuples.add(new RGGroupedCorrelatedPlansTuple(QTP, correlatedPlansTuplesGroup));
//        	
//        	return groupedExecutionTuples;
//
//    	}
//    	
//    	QTPNode[] sortedLeafNodes = new QTPNode[this.leafNodes.size()];
//    	sortedLeafNodes = leafNodes.toArray(sortedLeafNodes);
//    	
//    	Arrays.sort(sortedLeafNodes, new TargetPCRsComparator(this.targetPCRs));
//    	
//    	//Sort Plans
//    	CorrelatedPlans[] correlatedPlanTuples = new CorrelatedPlans[correlatedPlans.size()];
//    	correlatedPlanTuples = correlatedPlans.toArray(correlatedPlanTuples);
//    	Arrays.sort(correlatedPlanTuples, new RGCorrelatedPlansTupleComparator(sortedLeafNodes));
//    	
//    	//Grouping
//    	ArrayList<RGGroupedCorrelatedPlansTuple> groupedExecutionTuples = new ArrayList<RGGroupedCorrelatedPlansTuple>();
//    	ArrayList<CorrelatedPlans> execPlanTuplesGroup = null;
//
//		execPlanTuplesGroup = new ArrayList<CorrelatedPlans>();
//		execPlanTuplesGroup.add(correlatedPlanTuples[0]);
//
//    	for (int i = 1; i < correlatedPlanTuples.length; i++) 
//    	{
//    		//TODO: What would happen if a tuple doesn't have all leaf nodes or even first one (sortedLeafNodes[0])
//    		// It seems that when tuples do not have the first leaf we continue by second leaf and so on
//    		if (correlatedPlanTuples[i].getPCR(sortedLeafNodes[0]) == correlatedPlanTuples[i -1].getPCR(sortedLeafNodes[0]))
//    		{
//    			execPlanTuplesGroup.add(correlatedPlanTuples[i]);
//    		}
//    		else
//    		{
//    			groupedExecutionTuples.add(new RGGroupedCorrelatedPlansTuple(QTP, execPlanTuplesGroup));
//
//    			execPlanTuplesGroup = new ArrayList<CorrelatedPlans>();
//    			execPlanTuplesGroup.add(correlatedPlanTuples[i]);
//
//    		}
//    	}
//    	//The last group is added 
//    	groupedExecutionTuples.add(new RGGroupedCorrelatedPlansTuple(QTP, execPlanTuplesGroup));
//    	
//    	return groupedExecutionTuples;
//    }

    
    //commented by ouldouz:
//	private long estimateNumberOfPlans() throws DBException
//	{
//		ArrayList<QTPNode> leaves = QTP.getLeaves();
//		long estimate = 1;
//		for (QTPNode leaf: leaves)
//		{
//			int vocID = context.getMetaDataMgr().getDictionaryMgr().getVocabularyID(transaction, doc.getID(), XTCcalc.getBytes(leaf.getName()));
//			estimate = estimate * ssMgr.getNodeCount(transaction, doc, vocID);
//		}
//		return estimate;
//
//	}
        //private int tester=0;
	protected TwigResult next() throws DBException, IOException
	{   
             /////////commented to remove Order:
		TwigResult res = nextOrdered();
            ///////////////////////end of comment
//                TwigResult res = nextUnOrdered();
		return res;
		
	} //next()
//	long cmptime = 0;
	private TwigResult nextOrdered() throws DBException, IOException
	{
//		long s = System.currentTimeMillis();
		if (this.evaluationTrees.size() == 0)
		{
//			System.out.println("COMPARING TIME: " + cmptime);
			return null;
		}

		int min = 0;
		TwigResult minRes = null;
		for (int i = 0; i < this.evaluationTrees.size(); i++) 
		{
			TwigResult res = this.evaluationTrees.get(i).getHead();
			if (minRes == null || this.twigResultComparator.compare(res, minRes) < 0)
			{
				min = i;
				minRes = res;
			}
		} //for

		EvaluationTree minEvaluationTree = evaluationTrees.get(min);
		minEvaluationTree.next();

		if (minEvaluationTree.isFinished())
		{
			minEvaluationTree.close();
			this.IOTime += minEvaluationTree.getIOTime();
			this.numberOfReadElements += minEvaluationTree.getNumberOfReadElements();
			this.evaluationTrees.remove(min); 
		}
		return minEvaluationTree.completeRes(minRes);

	}
        int testCounter=0;
	private TwigResult nextUnOrdered() throws DBException, IOException
	{
            /////MODIFY TO REMOVE ORDER:
//		long s = System.currentTimeMillis();
            
		if (this.evaluationTrees.size() == 0)
		{
//			System.out.println("COMPARING TIME: " + cmptime);
			return null;
		}
  ///////////////added to make only one eve tree produce result:              
//                if(evaluationTrees.get(0).isFinished())
//                {  
//                    return null;
//                
//                }
     ///////////////////////////           
                int treeIndx;
//		int min = 0;
		TwigResult minRes = null;
                testCounter++;
//		for (int i = 0; i < this.evaluationTrees.size(); i++) 
//		{
			TwigResult res = this.evaluationTrees.get(0).getHead();
                        minRes=res;
//			if (minRes == null || this.twigResultComparator.compare(res, minRes) < 0)
//			{
//				min = i;
//				minRes = res;
//			}
//		} //for

		EvaluationTree minEvaluationTree = evaluationTrees.get(0);
		minEvaluationTree.next();

		if (minEvaluationTree.isFinished())
		{
			minEvaluationTree.close();
			this.IOTime += minEvaluationTree.getIOTime();
			this.numberOfReadElements += minEvaluationTree.getNumberOfReadElements();
			this.evaluationTrees.remove(0);                         
                        
		}
		return minEvaluationTree.completeRes(minRes);
	}

	public void close() throws DBException 
	{
		for (int i = 0; i < this.evaluationTrees.size(); i++) 
		{
			this.evaluationTrees.get(i).close();
		}
		//this.IOTime = 0;
		if (this.useSharedInput)
		{
			for (Integer PCR : openedStreams.keySet())
			{
				this.IOTime = this.IOTime + this.openedStreams.get(PCR).getIOTime();
				this.numberOfReadElements = this.numberOfReadElements + this.openedStreams.get(PCR).getNumberOfReadElements();
			}
		}
		for (int i = 0; i < this.evaluationTrees.size(); i++) 
		{
			this.IOTime += this.evaluationTrees.get(i).getIOTime();
			this.numberOfReadElements += this.evaluationTrees.get(i).getNumberOfReadElements();
		}
	}

//	public QueryTuple next() throws DBException {
//		if(pos < result.size()-1)
//		{
//			pos++;
//			return result.get(pos);
//		}
//		return null;
//	}

	private RGExecutionPlan getExecutionPlan(QueryTreePattern QTP) throws DBException, IOException
	{
		 RGExecutionPlan execPlan = ssMgr.getRGExecutionPlan(indexName,QTP);
		 
//			Set<QTPNode> qNodes = res.keySet();
//			for (Iterator<QTPNode> iterator2 = qNodes.iterator(); iterator2.hasNext();) {
//				QTPNode node = iterator2.next();
//				String str = "[" + node.getName() + ": " + res.get(node).getPCR() + "] ";
//				System.out.print(str);				
//			}
//			System.out.println();
//		}
//		System.out.println("Number of results: " + twRes.size());

		//System.out.println(ssMgr.getXMLPathSynopsis(transaction, doc));
		return execPlan; 
	}

	public QTPNode getExtractionPoint()
	{
		return this.extractionPoint;
	}
	
	public long getIOTime()
	{
		return this.IOTime;
	}
	
	public long getNumberOfReadElements()
	{
		return this.numberOfReadElements;
	}

	public long getNumberOfExecutionPlans()
	{
		return exactPlansNo;
	}
	
	public long getNumberOfGroupedExecutionPlans()
	{
		return parsedQTPNo;
	}
	
	public QueryTreePattern getQTP()
	{
		return this.QTP;
	}
	
	public String getExecutionMode()
	{
		return "DIRECT";
	}
	////////////////////////////////////////////////////////////////
	////////////////////TWIGRESULTCOMPARATOR///////////////////////
	///////////////////////////////////////////////////////////////
	private class TwigResultComparator implements Comparator<TwigResult>
	{
		private ArrayList<QTPNode> nodes;
		public TwigResultComparator(ArrayList<QTPNode> nodes)
		{
			this.nodes = nodes;
		}
		
		public int compare(TwigResult t1, TwigResult t2)
		{
			for (int i = 0; i < nodes.size(); i++) 
			{
				QTPNode node = nodes.get(i);
				DeweyID d1 = t1.getRes(node);
				DeweyID d2 = t2.getRes(node);
				if (d1 == null && d2 == null) return 0;
				if (d1 == null) return -1;
				if (d2 == null) return 1;
				int c = d1.compareTo(d2);
				if (c != 0) return c;
			}
			return 0;
		}
	}
	
	////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////
	/////----------------------------------------------------
	private class ExecutionPlanSizeComparator implements Comparator<TwinObject<QueryTreePattern, RGExecutionPlan>>
	{
		
		public ExecutionPlanSizeComparator()
		{
			
		}
		
		public int compare(TwinObject<QueryTreePattern, RGExecutionPlan> input1, TwinObject<QueryTreePattern, RGExecutionPlan> input2)
		{
			int c = input1.getO1().getNodes().size() - input2.getO1().getNodes().size();
			return c;
		}
	}

}

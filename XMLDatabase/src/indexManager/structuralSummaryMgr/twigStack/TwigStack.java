package indexManager.structuralSummaryMgr.twigStack;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import xmlProcessor.DBServer.DBException;
//import xtc.server.accessSvc.XTClocator;
//import xtc.server.accessSvc.idxMgr.idxBuilders.SimplePathExpr;
//import xtc.server.metaData.manager.MetaDataMgr;
import indexManager.StructuralSummaryIndex;
import indexManager.StructuralSummaryNode;
import indexManager.StructuralSummaryTree;
//import xtc.server.metaData.pathSynopsis.util.StructuralSummaryNodeRangeIDComparator;
//import xtc.server.taSvc.XTCtransaction;
//import xtc.server.util.XTCcalc;
import xmlProcessor.QTP.QTPSettings;
import xmlProcessor.QTP.QTPNode;
import xmlProcessor.QTP.QueryTreePattern;

/**
 * This class is used to run TwigStack on Path Synopsis for RG method
 * 
 * @author Kamyar
 *
 */
public class TwigStack {
	private TwigStackNode root = null;
        //private XTCtransaction transaction;
	//private XTClocator doc;
        private String indexName;
	private QueryTreePattern QTP;
	private StructuralSummaryTree ps;
	private Queue<ArrayList<StructuralSummaryNode>> bufferQueue = null;
	//private MetaDataMgr metaDataMgr;
	private ArrayList<QTPNode> leafNodes;
	private HashMap<QTPNode, Queue<TwigResult>> leavesPathRes;

	//XTCtransaction transaction, int docID, StructuralSummaryNode psNode
	public TwigStack (String indexName,QueryTreePattern QTP,StructuralSummaryTree ps) throws DBException
	{//1time
		this.QTP = QTP;
		this.ps = ps;
		this.indexName=indexName;
		bufferQueue = new LinkedList<ArrayList<StructuralSummaryNode>>();
		this.root = prepareTwigNodes();
		this.leafNodes = QTP.getLeaves();
		leavesPathRes = new HashMap<QTPNode, Queue<TwigResult>>(leafNodes.size());
		for (QTPNode node : leafNodes) 
		{			
			this.leavesPathRes.put(node, new LinkedList<TwigResult>());
		}
	}
	
	
	private ArrayList<StructuralSummaryNode> prepareInputStreams(QTPNode qNode) throws DBException
	{
		//int vocID = metaDataMgr.getDictionaryMgr().getVocabularyID(transaction, doc.getID(), XTCcalc.getBytes(qNode.getName()));
		/////bayd load index bshe
		return ps.getNodeIndex(qNode.getName());//bayd b jaye vocID ,az in bad string bgire
		
//		//getPath is better than getName because it could filter more nodes!!!		
//		//T O D O: Save results of this method in the pathsynopsis object for future use
//		SimplePathExpr pathExpr = new SimplePathExpr(qNode.getPath());
//		String parsedPath = pathExpr.getPathAsString(transaction, doc.getID(), metaDataMgr.getDictionaryMgr());
//		Set<StructuralSummaryNode> pSet = ps.getNodesForPath(parsedPath);
//		ArrayList<StructuralSummaryNode> qNodeStream = new ArrayList<StructuralSummaryNode>(pSet.size());
//		
//		
//		StructuralSummaryNode[] resArr = new StructuralSummaryNode[pSet.size()]; 
//		resArr = pSet.toArray(resArr);
//	
//		Arrays.sort(resArr, new StructuralSummaryNodeRangeIDComparator());
//		
//		for (int i = 0; i < resArr.length; i++) {
//			qNodeStream.add(resArr[i]);			
//		}
//		return qNodeStream;
	}
	
	private TwigStackNode prepareTwigNodes() throws DBException
	{
		long estimate = 1;
		ArrayList<StructuralSummaryNode> qNodeStream = null;
		TwigStackNode root = null;
		TwigStackNode twNode = null;
		QTPNode qNode = null;

		Stack<QTPNode> qStack = new Stack<QTPNode>();
		Stack<TwigStackNode> twStack = new Stack<TwigStackNode>();
		
		qNode = QTP.root();
		qNodeStream = prepareInputStreams(qNode);		
		twNode = new TwigStackNode(qNode, qNodeStream, null);
		root = twNode;
		qStack.push(qNode);
		twStack.push(twNode);
		while (!qStack.isEmpty())
		{
			qNode = qStack.pop();
			twNode = twStack.pop();
			ArrayList<QTPNode> children = qNode.getChildren();
			for (QTPNode cQNode : children) {
				qStack.push(cQNode);
				qNodeStream = prepareInputStreams(cQNode);				
				TwigStackNode cTWNode = new TwigStackNode(cQNode, qNodeStream, twNode);
				twStack.push(cTWNode);
			}
//			if (children.size() == 0)
//			{
//				estimate *= qNodeStream.size();
//			}
		}
//		System.out.println("StructuralSummaryIndex.TwigStack.prepareTwigNodes estimate: " + estimate);
//		if ( estimate> 100000000)
//		{
//			throw new DBException("Please choose another method for executing your Twig!!! " + estimate + " plans are estimated!!!");
//		}

		return root;
	}
	
	
	//returns leaves of TwigStack tree
	private ArrayList<TwigStackNode> getLeaves()
	{
		LinkedList<TwigStackNode> queue = new LinkedList<TwigStackNode>();
		ArrayList<TwigStackNode> result = new ArrayList<TwigStackNode>();
		queue.add(root);
		while(!queue.isEmpty())
		{
			TwigStackNode node = queue.poll();
			queue.addAll(node.getChildren());
			if (!node.hasChildren()) //leaf node
			{
				result.add(node);
			}
		}
		
		return result;
	}


	public TwigJoinOp Execute() throws DBException
	{

		if(root.getAxis() == QTPSettings.AXIS_CHILD)
		{
		    //yani rootName ro bde:	
                    String rName =ps.getRoot().getQName();
			if (!rName.equals(root.getName()))
			{
				//return empty list
				return new TwigJoinOp();
			}
		}
		
		TwigResult pathRes = null;
		
		ArrayList<TwigStackNode> leaves = getLeaves();
		for (int i = 0; i < leafNodes.size(); i++ )
		{
			pathRes = nextPath(leaves.get(i));
			//ArrayList<PathResult> leafPathRes = new ArrayList<PathResult>();			
			Queue<TwigResult> leafPathRes = leavesPathRes.get(leaves.get(i).getQTPNode());
			while(pathRes != null)
			{
			   leafPathRes.add(pathRes);
			    pathRes = nextPath(leaves.get(i));				
			} 
			leavesPathRes.put(leaves.get(i).getQTPNode(), leafPathRes);
		}
		
		return mergePathResults();
	}
	
	private TwigResult next(QTPNode qNode) throws DBException
	{
		return leavesPathRes.get(qNode).poll();
	}
	
	private TwigResult nextPath(TwigStackNode node) throws DBException {
		while(node.getPathBufferQueue().isEmpty() && !root.end()) {
			twigStack();
		}
		
	
		if (node.getPathBufferQueue().isEmpty() && root.end() && node.getUnSortedPathBufferQueue().size() > 0) {
			node.sortBufferedPaths();
			return node.getPathBufferQueue().poll();
		}
		
		return node.getPathBufferQueue().poll();
	}
	
	private boolean checkPath(TwigStackNode twStackNode, ArrayList<StructuralSummaryNode> pathRes) throws DBException
	{
		//checks each node with its parent to match the axis
		//axis of root should be checked in the beginning of TwigStack execution in the Execute method
	
		for (int i = pathRes.size() - 1; i > 0; i--) 
		{
			//the following two lines used to check if algorithm works well or not after debug it could be removed
			//String psNodeName = metaDataMgr.resolveVocID(transaction, doc.getID(), pathRes.get(i).getVocID());
			//if (!twStackNode.getName().equals(psNodeName)) throw new DBException("Error in TwigStack Partial Results");
			////////////////////////////////////////////////////////////
			if(twStackNode.getAxis() == QTPSettings.AXIS_CHILD)
			{								
				if (pathRes.get(i).getRangeID().getLevel() != (pathRes.get(i - 1).getRangeID().getLevel() + 1))//distance is more than two level
				{
					return false;
				}
			}
			twStackNode = twStackNode.getParent();
		}
		return true;
	}
	private void twigStack() throws DBException {
		while(!root.end())
		{
			TwigStackNode currentNode = getNext(root);
			if(! currentNode.isRootNode())
			{
				currentNode.getParent().cleanStack(currentNode.getStreamHead());
			}
			
			if(currentNode.isRootNode() || ! currentNode.getParent().isEmptyStack())
			{
				currentNode.cleanStack(currentNode.getStreamHead());					
				currentNode.moveStreamToStack();
				
				if(currentNode.isLeafNode())
				{
					// show solutions with blocking
					ArrayList<ArrayList<StructuralSummaryNode>> result = currentNode.getResult(currentNode.getStackSize()-1);						
					if(result != null && result.size() != 0)
					{
						for (ArrayList<StructuralSummaryNode> pathArr : result)
						{
							if (checkPath(currentNode, pathArr))
							{
								currentNode.getUnSortedPathBufferQueue().add(new TwigResult(QTP, currentNode.getQTPNode(), pathArr));
							}
						}
//						currentNode.getUnSortedPathBufferQueue().addAll(result);
						
						return;	//for ONC protocol uncomment
					}
						
					else
					
					currentNode.popStack();
				}
				
			} else if(! currentNode.streamConsumed()) { // updated by oy on 29.05.06
				currentNode.advanceStream();
			}
		}
		
	}

	private TwigStackNode getNext(TwigStackNode node) throws DBException
	{	

		if(node.isLeafNode())
			return node;
		
		ArrayList<TwigStackNode> children = node.getChildren();
		
		TwigStackNode minChild = null;
		TwigStackNode maxChild = null;

		for(TwigStackNode child : children)
		{
			TwigStackNode currentNode = this.getNext(child);
			if(currentNode != child)
				return currentNode;

			// updated by oy on 16.06.06 --
			
			// also, calculate min and max tuple
			// updated by oy on 29.05.06
			if(minChild == null )
			{
				minChild = child;
			}
			if (maxChild == null)
			{
				maxChild = child;
			}

			if(child.isLeftLessThan(minChild))
			{
				minChild = child;
			}
			if(maxChild.isLeftLessThan(child)) {
				maxChild = child;
			}
			// updated by oy on 29.05.06
			// updated by oy on 16.06.06
			
		}		
		
		while(node.isRightLessThan(maxChild) ) { //&& !node.inputConsumed()
			node.advanceStream();
		}
		
		if(node.isLeftLessThan(minChild))
		{
			return node;
		}

		return minChild;
		
	}
	
	private TwigJoinOp mergePathResults() throws DBException
	{	
		QTPNode[] leaves = new QTPNode[leavesPathRes.size()];
		//leaves = leavesPathRes.keySet().toArray(leaves);
		leaves = this.QTP.getLeaves().toArray(leaves);

		if (leaves.length == 1)
		{
			TwigJoinOp twigJoinOp = new TwigJoinOp(leavesPathRes.get(leaves[0]));
			twigJoinOp.open();
			return twigJoinOp;
		}
		
		TwigInput in1 = new InputWrapper(this, /*this.QTP, */leaves[0]/*, leavesPathRes.get(leaves[0])*/);
		TwigInput in2 = new InputWrapper(this, /*this.QTP, */leaves[1]/*, leavesPathRes.get(leaves[1])*/);
		TwigJoinOp twigJoinOp = new TwigJoinOp(in1, in2, QTP.findNCA(leaves[0], leaves[1]).getLevel()); 
		for(int i=2; i < leafNodes.size(); ++i)
		{
			TwigInput in = new InputWrapper(this, /*this.QTP, */leaves[i]/*, leavesPathRes.get(leaves[i])*/);
			twigJoinOp = new TwigJoinOp(twigJoinOp, in, QTP.findNCA(leaves[i-1], leaves[i]).getLevel());
		}
		twigJoinOp.open(); 
		return twigJoinOp;

	}
	
	////////////////////////////////////////////////////////////////
	///////////////INPUTWRAPPER CLASS///////////////////////////////
	///////////////////////////////////////////////////////////////
	private class InputWrapper implements TwigInput 
	{
		//private Queue<TwigResult> pathResults;
		private int pos = -1;
		//private QueryTreePattern QTP;
		private TwigStack aTwigStack;
		private QTPNode qNode;
		public InputWrapper(TwigStack aTwigStack, /*QueryTreePattern QTP, */QTPNode qNode /*,Queue<TwigResult> pathResults*/)
		{
			//this.pathResults = pathResults;
			pos = 0;
			//this.QTP = QTP;
			this.aTwigStack = aTwigStack;
			this.qNode = qNode;
		}

		public void close() throws DBException {
			// TODO Auto-generated method stub

		}

		public TwigResult next() throws DBException 
		{			
			TwigResult res = aTwigStack.next(this.qNode);
			//System.out.println("Node " + qNode.getName() + " is requested. Result is:" + res);
			return res;
//			if (pos == pathResults.size()) return null;
//			//TwigResult res = new TwigResult(QTP, pathResults.get(pos));
//			TwigResult res = pathResults.get(pos);
//			++pos;
//			return res;
		}

		public void open() throws DBException {
			// TODO Auto-generated method stub

		}

	} //private class InputWrapper
	////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////

}
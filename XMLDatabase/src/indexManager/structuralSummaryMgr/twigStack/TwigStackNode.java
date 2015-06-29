package indexManager.structuralSummaryMgr.twigStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import xmlProcessor.DBServer.DBException;
import indexManager.StructuralSummaryNode;
import indexManager.util.StructuralSummaryNodeArrayRangeIDComparator;
import xmlProcessor.DBServer.utils.SvrCfg;
import xmlProcessor.QTP.QTPNode;
//import xtc.server.xmlSvc.xqueryOperators.XTCxqueryOperator;
//import xmlProcessor.DBServer.utils.QueryTuple;
//import xtc.server.xmlSvc.xqueryUtils.QueryTupleComparator;


/**
 * TwigStackNode encapsulates stack and stream needed for TwigStack on PathSynopsis
 * 
 * @author Kamyar
 *
 */
public class TwigStackNode {
	private int	axis;
	private String name;
	private QTPNode qNode;
	
	// node handlilng
	//protected int							nodeID;
	protected TwigStackNode	 			parent = null;
	protected ArrayList<TwigStackNode> 	children;
	
	// twig join property
	private Stack<StructuralSummaryNode> stack;
	private Stack<Integer> ptrStack;
	private List<StructuralSummaryNode> stream;
		
//	private QueryTuple nextTuple = null;
	private StructuralSummaryNode streamHead = null;
	private boolean streamConsumed = false;
	
	private Queue<TwigResult> pathBufferQueue = null;
	private ArrayList<TwigResult> unSortedPathBufferQueue;
	private ArrayList<TwigStackNode> pushListners;
	private StructuralSummaryNode oldestRootTuple = null;
	private ArrayList<QTPNode> pathNodes;
	private int pos = 0;
	
	public TwigStackNode (QTPNode qNode, ArrayList<StructuralSummaryNode> stream, TwigStackNode parent) throws DBException
	{
		this.qNode = qNode;
		this.axis = qNode.getAxis();		
		this.stream = stream;
//		this.nodeID = id;		
		this.parent = parent;
		this.children = new ArrayList<TwigStackNode>();
		
		if (parent != null) parent.addChildNode(this);
		
		this.stack = new Stack<StructuralSummaryNode>();
		this.ptrStack = new Stack<Integer>();
		pos = 0;
		if (stream == null)
		{
			this.streamConsumed = true;
		}
		else if (stream.size() > 0)
		{
			this.streamHead = stream.get(pos++);
			this.streamConsumed = false;
		}
		else
		{
			this.streamConsumed = true;	
		}
		
		
		this.name = qNode.getName();
		
		if (isRootNode()) {
			pushListners = new ArrayList<TwigStackNode>();
		}
		
		if (isLeafNode()) {
			getTwigRoot().addPushListener(this);
		}
		
		
		pathBufferQueue = new LinkedList<TwigResult>();
		unSortedPathBufferQueue = new ArrayList<TwigResult>();
		
		this.pathNodes = new ArrayList<QTPNode>(qNode.getLevel()+1);
		QTPNode q = qNode;
		while (q != null) 
		{
			this.pathNodes.add(0, q);
			q = q.getParent();
		}
	}
	
	public String getName()
	{
		return name;
	}
	
	// Input handling stuff ---------------------------------------------------------
//	public void open() throws DBException 
//	{		
//		if (isRootNode()) {
//			pushListners = new ArrayList<TwigStackNode>();
//		}
//		
//		if (isLeafNode()) {
//			getRoot().addPushListener(this);
//		}
//		
//		input.open();
//		this.nextTuple = input.next();
//		
//		// updated by oy on 16.06.06 --
//		if (this.nextTuple == null) { 
//			this.inputConsumed = true;
//		}
//		// updated by oy on 16.06.06 
//		
//		//System.out.println("next tuple: " + this.name + " " + nextTuple.getIDForPos(0));
//		for(TwigStackNode child : children)
//			child.open();
//	}
	
//	public void close() throws DBException
//	{
//		input.close();
//		for(TwigStackNode child : children)
//			child.close();
//	}
	
	public Queue<TwigResult> getPathBufferQueue() {
		return this.pathBufferQueue;
	}
	
	public void advanceStream() throws DBException
	{
		if (pos >= stream.size())
		{
			this.streamConsumed = true;
			this.streamHead = null;
		}
		else
		{
			this.streamHead = stream.get(pos++);
		}
		
//		stream.remove(0); // removes head of the list
//		if (stream.size() == 0)
//		{
//			this.streamConsumed = true;
//			this.streamHead = null;
//		}
//		else
//		{
//			this.streamHead = stream.get(0);
//		}
		
//		QueryTuple tmp = input.next();
		
		// updated by oy on 16.06.06 --
		
		// nextTuple will be set in any case, that means getNextTuple will return a null value
		// if the input stream is consumed
//		this.nextTuple = tmp;
//		
//		if(tmp == null) {
//			this.inputConsumed = true;
//			System.out.println(this.name + " input consumed!");
//		}
//		else {
////			System.out.println(this.name + " nextTuple is " + nextTuple.getIDForPos(0));
//		}
		
		// updated by oy on 16.06.06
		
		//if(! inputConsumed)
			//System.out.println("advanced to tuple " + this.name + " " + nextTuple.getIDForPos(0));
		//else
			//System.out.println("input for " + this.name + " CONSUMED.");
	}
	
	// updated by oy on 16.06.06
	// after this update, getNextTuple will return a null value if the input stream is consumed 
	public StructuralSummaryNode getStreamHead() 
	{
		//this.streamHead = this.stream.get(0);
		return streamHead;
		//return this.nextTuple;
	}
	
	public boolean streamConsumed()
	{
		return streamConsumed;
	}

	// added by oy on 16.06.06 --
	public boolean isLeftLessThan(TwigStackNode node) throws DBException {
		
		if(this.streamConsumed && node.streamConsumed) return true;
		
		if (this.streamConsumed) return false;
		
		if (node.streamConsumed) return true;
		
		return this.streamHead.getRangeID().compareTo(node.streamHead.getRangeID()) < 0 ;
	}
	
	public boolean isRightLessThan(TwigStackNode node) throws DBException {
		
		if(this.streamConsumed && node.streamConsumed) return false;
		
		if(this.streamConsumed) return false;
		
		if(node.streamConsumed) return true;
		
		return this.streamHead.getRangeID().isPrecedingOf(node.streamHead.getRangeID());
		
//		return !this.nextTuple.getIDForPos(0).isAncestorOf(node.nextTuple.getIDForPos(0)) && (this.nextTuple.getIDForPos(0).compareTo(node.nextTuple.getIDForPos(0)) < 0);
		
	}
	
	// added by oy on 16.06.06
	
//	public boolean end()
//	{		
//		// if we are the root ...
//		if(this.isRootNode())
//		{
//			if(this.inputConsumed)
//				return true;
//			ArrayList<XTCxqueryTwigNodeNew> desc = this.getDescendantNodes();
//			for(XTCxqueryTwigNodeNew node : desc) 
//			{						
//				if(node.end())
//					return true;							
//			}	
//			return false;
//		}
//		else 		
//			return inputConsumed;
//				
//	}
	
	// updated by oy on 29.05.06
	public boolean end()
	{		
		ArrayList<TwigStackNode> subTreeNodes = subTreeNodes();
		for (TwigStackNode node : subTreeNodes) {
			if (node.isLeafNode() && !node.streamConsumed()) {
				return false;
			}
		}
		return true;
	}
	// updated by oy on 29.05.06
	
	// Input handling stuff end -----------------------------------------------------
	
	// Stack stuff --------------------------------------------------------------------
	public void moveStreamToStack() throws DBException
	{
//		System.out.print("pushing onto stack " + name + ", " ); this.nextTuple.print(); 
		
		this.stack.push(this.streamHead);

		
		if(isRootNode())
		{	
			// The below if is false for ever !!! because oldestRootTuple never gets any value before this if !!! have a look at source code!!!
			if ( oldestRootTuple != null && !oldestRootTuple.getRangeID().isAncestorOf(this.streamHead.getRangeID()))
			{
				System.out.println("You are wrong this if is entered during TwigStack for pathsynopsis");
				for (TwigStackNode leaf : pushListners)
				{
					leaf.sortBufferedPaths();
				}
				oldestRootTuple = this.streamHead;
			}
		}
		
		this.advanceStream();
		
		// updated by oy on 29.05.06
		if(! this.isRootNode())
			this.ptrStack.push(this.getParent().getStackSize()-1);			
	}	
	
	public boolean isEmptyStack() 
	{
		return stack == null || stack.size() == 0;
	}
	
	public int getStackSize()
	{
		return stack.size();
	}
	
	public void cleanStack(StructuralSummaryNode node) throws DBException
	{
		while(!this.stack.isEmpty() && stack.peek().getRangeID().isPrecedingOf(node.getRangeID()))
		{
//			System.out.println("popping from stack " + name + " cleanStack");
//			stack.pop();
			popStack(); // updated by oy on 28.06.06
		}
	}
	
	public void popStack()
	{
//		System.out.println("popping from stack " + name);
		StructuralSummaryNode top = stack.pop();
		if (!this.isRootNode()) ptrStack.pop(); // updated by oy on 28.06.06
//		if (isRootNode()) {
////			System.out.println("root popped ##########");
////			top.print();
//			for (TwigStackNode leaf : pushListners) {
//				leaf.rootPushed();
//			}
//		}
	}
	
	public StructuralSummaryNode peekStack()
	{
		try {
			return stack.peek();
		}
		catch(Exception ex)
		{
			return null;
		}
	}
	// Stack stuff end ----------------------------------------------------------------
	
	// Output stuff -------------------------------------------------------------------
//	public ArrayList<QueryTuple> getResult() 
//	{	
//		System.out.println("getResult()");
//		// first try
//		ArrayList<XTCxqueryTwigNodeNew> desc = this.getDescendantNodes();
//		ArrayList<DeweyID> resultIDs = new ArrayList<DeweyID>();
//		if(stack.size() == 0)
//		{
//			//System.out.println("found an empty stack for name " + name);
//			return null;
//		}		
//		resultIDs.add(this.stack.peek().getIDForPos(0));
//		for(XTCxqueryTwigNodeNew node : desc) 
//		{
//			QueryTuple tmp = node.peekStack();			
//			if(tmp == null)
//			{
//				//System.out.println("found an empty stack for name " + node.getName());
//				return null;
//			}			
//			resultIDs.add(tmp.getIDForPos(0));
//		}
//		QueryTuple resultTuple = new QueryTuple();
//		for(DeweyID id : resultIDs)
//		{
//			resultTuple.addDeweyID(id);
//		}
//		
//		ArrayList<QueryTuple> result = new ArrayList<QueryTuple>();
//		result.add(resultTuple);
//		return result;
//	}
	
	// added by oy on 29.05.06
	public ArrayList<ArrayList<StructuralSummaryNode>> getResult(int pos) 
	{	
//		System.out.println("getResult(int pos) " + pos);
		
		ArrayList<ArrayList<StructuralSummaryNode>> result = new ArrayList<ArrayList<StructuralSummaryNode>>();
		
		if (this.isRootNode()) {

			//StructuralSummaryNodeh t = new QueryTuple(stack.get(pos).getIDForPos(0));
			ArrayList<StructuralSummaryNode> t = new ArrayList<StructuralSummaryNode>();
			t.add(stack.get(pos));
			result.add(t);
			return result;
		}
		
		
		int parentStackPos = ptrStack.get(pos);
//		System.out.println("parentStackPos " + parentStackPos);
		
		for (int i = 0; i <= parentStackPos; i++) {
			result.addAll(parent.getResult(i));
//			System.out.println("       parent result added!");
		}

		for (ArrayList<StructuralSummaryNode> psNodeList : result) {
//			System.out.println("tuple exploded: ");
//			tuple.print();
//			System.out.println("deweyId added: " + stack.get(pos).getIDForPos(0));
//			tuple.addDeweyID(stack.get(pos).getIDForPos(0));
			psNodeList.add(stack.get(pos));
		}
		
		return result;
	}
	// added by oy on 29.05.06

	// added by oy on 29.05.06
//	public ArrayList<QueryTuple> getResultL(int pos) 
//	{	
////		System.out.println("getResult(int pos) " + pos);
//		
//		TwigStackNode currentNode = this;
//		int position = pos;
//		
//		ArrayList<ArrayList<QueryTuple>> tmp = new ArrayList<ArrayList<QueryTuple>>();
//		
//		QueryTuple tuple = new QueryTuple(currentNode.stack.get(position).getIDForPos(0));
//		
//		tmp.add(position, new ArrayList());
//		tmp.get(position).add(tuple);
//		
//		while (!currentNode.isRootNode()) {
//			
//			int parentStackPos = currentNode.ptrStack.get(position);
//	
//			ArrayList<ArrayList<QueryTuple>> zw = new ArrayList<ArrayList<QueryTuple>>();
//			
//			for (int i = 0; i <= parentStackPos; i++) {
//				
//				ArrayList<QueryTuple> partRes = new ArrayList<QueryTuple>(); 
//				
//				for (QueryTuple tuple2 : tmp.get(position)) {
//					QueryTuple tuple3 = new QueryTuple (tuple2, new QueryTuple(currentNode.parent.stack.get(i).getIDForPos(0)));
//					partRes.add(tuple3);
//				}
//				zw.add(i, partRes);
//			}
//			tmp = new ArrayList<ArrayList<QueryTuple>>();
//			tmp.addAll(zw); //zw
//				
//			if (position > 0) {
//				position--;
//			} else {
//				position = parent.getStackSize()-1;
//				currentNode = currentNode.parent; 
//			}
//		}
//		
//		ArrayList<QueryTuple> result = new ArrayList<QueryTuple>();
//		
//		for (ArrayList<QueryTuple> partList : tmp) {
//			result.addAll(partList);
//		}
//		
//		return result;
//	}
//	// added by oy on 29.05.06
	
//	public ArrayList<QueryTuple> getResult2(ArrayList<QueryTuple> result) 
//	{		
//		return null;
//	}
	
	// Output stuff end ---------------------------------------------------------------
	
	// node handling stuff ------------------------------------------------------------
	public void setAxis (int axis)
	{
		this.axis = axis;
	}
	
	public int getAxis ()
	{
		return this.axis;
	}
	
//	public int getNodeID() {
//		return nodeID;
//	}
		
	public void setParent(TwigStackNode parent) {
		this.parent = parent;
	}
	
	public TwigStackNode getParent() {
		return parent;
	}

	public QTPNode getQTPNode()
	{
		return qNode;
	}
	public void addChildNode(TwigStackNode child) throws DBException
	{
		if (child == null) throw new DBException("Null child is not acceptable");
		if (children.indexOf(child)!= -1) throw new DBException("Child has been added before");	
		children.add(child);
	}
				
	public boolean hasChildNode() {
		return children.size() != 0;
	}
	
	public boolean isRootNode() {
		return parent == null;
	}
	
	public boolean isLeafNode()
	{
		if(children == null || children.size() == 0)
			return true;
		return false;
	}
	
	public ArrayList<TwigStackNode> getDescendantNodes() 
	{
		ArrayList<TwigStackNode> desc = new ArrayList<TwigStackNode>();
		
		for(TwigStackNode child: children) {
			desc.add(child);
			desc.addAll(child.getDescendantNodes());
		}
		return desc;
	}
	
	// updated by oy on 29.05.06
	public ArrayList<TwigStackNode> subTreeNodes() {
		ArrayList<TwigStackNode> desc = getDescendantNodes();
		desc.add(this);
		return desc;
	}
	// updated by oy on 29.05.06
	
	public ArrayList<TwigStackNode> getChildren()
	{
		return children;
	}

	public boolean hasChildren()
	{
		return children.size() > 0;
	}
	// misc methods
//	public void print() 
//	{
//		System.out.println("+-- XTCxqueryQTStepNode --+");
//		System.out.println("nodeID:\t\t" + nodeID);
//		System.out.print("axis:\t\t");
//		if(axis == SvrCfg.AXIS_CHILD)
//			System.out.println("/");
//		else if(axis == SvrCfg.AXIS_DESCENDANT)
//			System.out.println("//");
//		else {
//			System.out.println("none");
//		}		
//		System.out.println("+-------------------------+\n");
//		
//		for(TwigStackNode child: children)
//			child.print();
//	}
	
//	public void rootPopped(){
//		
//		QueryTuple[] resArr = new QueryTuple[unSortedPathBufferQueue.size()]; 
//		resArr = unSortedPathBufferQueue.toArray(resArr);
//		unSortedPathBufferQueue.clear();
//		Arrays.sort(resArr, new QueryTupleComparator());
//		for (int i = 0; i < resArr.length; i++) {
//			pathBufferQueue.add(resArr[i]);
//		}
////		System.out.println("rootPopped Called, sorted elements: " + resArr.length);
//	}
	
	public void sortBufferedPaths(){
		
		TwigResult[] resArr = new TwigResult[unSortedPathBufferQueue.size()]; 
		resArr = unSortedPathBufferQueue.toArray(resArr);
		unSortedPathBufferQueue.clear();		
		Arrays.sort(resArr, new StructuralSummaryNodeArrayRangeIDComparator(this.pathNodes));
		for (int i = 0; i < resArr.length; i++) {
			pathBufferQueue.add(resArr[i]);
		}
//		System.out.println("rootPopped Called, sorted elements: " + resArr.length);
	}
	
	public void addPushListener(TwigStackNode node) {
		this.pushListners.add(node);
	}
	
	private TwigStackNode getTwigRoot() {
		if (isRootNode()) {
			return this;
		} else {
			return parent.getTwigRoot();
		}
	}

	public ArrayList<TwigResult> getUnSortedPathBufferQueue() {
		return this.unSortedPathBufferQueue;
	}

	public int findNCALevel(TwigStackNode node)
	{
		TwigStackNode node1 = this;
		ArrayList<TwigStackNode> node1Anc = new ArrayList<TwigStackNode>();
		while (node1 != null)
		{
			node1Anc.add(node1);
			node1 = node1.getParent();
		}
		
		ArrayList<TwigStackNode> node2Anc = new ArrayList<TwigStackNode>();
		while (node != null)
		{
			node2Anc.add(node);
			node = node.getParent();
		}
		//if everything is OK then the rot should be the same so there is no need to compare it 
		int i1 = node1Anc.size() - 2;
		int i2 = node2Anc.size() - 2;
		for (; (i1 >= 0) && (i2 >= 0); --i1, --i2)
		{
			if (node1Anc.get(i1) != node2Anc.get(i2)) return node1Anc.size() - i1 -2; //equal to: (node1Anc.size()-1)-(i1 +1)
		}
		//this point will be reached if something is wrong
		return -1;
	
	}


}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package xmlProcessor.TJFast;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Level;

import xmlProcessor.DBServer.DBException;
//import xtc.server.accessSvc.XTClocator;
import indexManager.StructuralSummaryManager;
import indexManager.StructuralSummaryManager01;
import indexManager.StructuralSummaryNode;
import java.io.IOException;
//import xtc.server.metaData.vocabulary.DictionaryMgr;
import xmlProcessor.DeweyID;
//import xtc.server.taSvc.XTCtransaction;
import xmlProcessor.DBServer.utils.TwinObject;
//import xtc.server.util.XTCcalc;
import xmlProcessor.DBServer.utils.SvrCfg;
import xmlProcessor.DBServer.utils.TwinArrayList;
//import xtc.server.xmlSvc.XTCxqueryContext;
import xmlProcessor.DBServer.operators.twigs.QueryStatistics;
import xmlProcessor.QTP.QTPNode;
import xmlProcessor.QTP.QueryTreePattern;
import xmlProcessor.DBServer.utils.QueryTuple;

/**
 * This class implements TJFast method for processing Twigs
 * 
 * @author Kamyar
 *
 */
public class TJFast implements QueryStatistics
{

	private QueryTreePattern QTP;
	//private XTCxqueryContext context;
	//private XTClocator doc;
	//private int idxNo;
	private StructuralSummaryManager ssMgr;
        private String indexName;
	//private DictionaryMgr dictionaryMgr;
	//private XTCtransaction transaction;
	private ArrayList<QTPNode> leafNodes;
	private ArrayList<InputStream> inputs;
	private boolean isEnded = false;
	//Branching node sets are simulated by an ArrayList of TwinArrayList.
	//So each set is simulated by a TwinArrayList which is composed of two objects
	//first object is the TwinObject for XTCDeweyID and its corresponding DeweyID related to the QTP leaf
	// and second object is its Self and Inherit list which are wrapped in a TwinObject
	private ArrayList<TwinArrayList<TwinObject<DeweyID, DeweyID>, TwinObject<ArrayList<TwinObject<QueryTuple, QTPNode>>, ArrayList<TwinObject<QueryTuple, QTPNode>>>>> branchingNodesSets;
	private ArrayList<QTPNode> branchingNodes;
	private HashMap<QTPNode, Queue<TwigResult>> leavesPathRes;
	private HashMap<TwinObject<Integer, QTPNode>, TwinArrayList<Integer, Integer>> nodeMatches;
	private TwinObject<Integer, QTPNode> PCRQNode; //this property is used here to allocate memory for it only once
	private HashMap<TwinObject<Integer, QTPNode>, ArrayList<ArrayList<TwinObject<Integer, Integer>>>> pathMatches;
	private long IOTime = 0;
	private long numberOfReadElements = 0;
//	long T = 0;
//	long T2 = 0;

	public TJFast(QueryTreePattern QTP,String indexName)
	{
		this.QTP = QTP;
		//this.context = context;
		//this.idxNo = idxNo;
		//this.doc = context.getLocator();
		this.ssMgr =new StructuralSummaryManager01();
                        //context.getMetaDataMgr().getStructuralSummaryManager();
		//this.transaction = context.getTransaction();
		//this.dictionaryMgr = context.getMetaDataMgr().getDictionaryMgr();
		this.leafNodes = QTP.getLeaves();
		this.inputs = new ArrayList<InputStream>(leafNodes.size());
		this.branchingNodes = QTP.branchingNodes();
		this.branchingNodesSets = new ArrayList<TwinArrayList<TwinObject<DeweyID,DeweyID>,TwinObject<ArrayList<TwinObject<QueryTuple,QTPNode>>,ArrayList<TwinObject<QueryTuple,QTPNode>>>>>(branchingNodes.size());
		for (int i = 0; i < branchingNodes.size(); i++) 
		{
			TwinArrayList<TwinObject<DeweyID, DeweyID>,TwinObject<ArrayList<TwinObject<QueryTuple, QTPNode>>,ArrayList<TwinObject<QueryTuple, QTPNode>>>> branchingNodeSet;
			branchingNodeSet = new TwinArrayList<TwinObject<DeweyID,DeweyID>, TwinObject<ArrayList<TwinObject<QueryTuple,QTPNode>>,ArrayList<TwinObject<QueryTuple,QTPNode>>>>();
			branchingNodesSets.add(branchingNodeSet);			 
		}
		
		this.leavesPathRes = new HashMap<QTPNode, Queue<TwigResult>>();
		for (QTPNode node : leafNodes) 
		{
			this.leavesPathRes.put(node, new LinkedList<TwigResult>());
		}
		
		this.nodeMatches = new HashMap<TwinObject<Integer, QTPNode>, TwinArrayList<Integer,Integer>>();
		this.PCRQNode = new TwinObject<Integer, QTPNode>();
		
		this.pathMatches = new HashMap<TwinObject<Integer,QTPNode>, ArrayList<ArrayList<TwinObject<Integer, Integer>>>>();
		
	}
	
	public TwigJoinOp execute() throws DBException, IOException
	{
		//FIXME: Check: if query has only one node or one branch (simple path query)
		//then simply return streamHead after locateMatchedLabel in a loop
		//otherwise you may get some exceptions i the rest of code

		//FIXME: check if the QTP root match the document root if the axis of QTP root is PARENT
//		QTPNode root = QTP.root();
//		if(root.getAxis() == XTCsvrCfg.AXIS_PARENT)
//		{
//			String rName = context.getMetaDataMgr().resolveVocID(transaction, doc.getID(), ps.getRoot().getVocID());
//			if (!rName.equals(root.getName()))
//			{
//				//return empty list
//				return new TwigJoinOp();
//			}
//		}

		for (int i=0; i < leafNodes.size(); ++i)
		{
			InputStream input = new InputStream(leafNodes.get(i),indexName);
			input.locateMatchedLabel();
			inputs.add(input);
		}
		
		this.execTJfast();
		
		return mergeAllPathSolutions();
	} //execute
	
	private void execTJfast() throws DBException, IOException
	{
		if (leafNodes.size() == 1)
		{
			//FIXME: the output of single path queries is not sorted
			//something like blocking methods are needed
			Queue<TwigResult> pathResArr = leavesPathRes.get(leafNodes.get(0));
			InputStream stream;
			stream = inputs.get(0);
			if (!stream.streamConsumed())
			{
				ArrayList<QueryTuple> res = completePath(leafNodes.get(0), stream.getStreamHead()); 

				for (QueryTuple tuple : res) 
				{
					pathResArr.add(new TwigResult(QTP, leafNodes.get(0), tuple));
				}
				stream.advanceStream();
				stream.locateMatchedLabel();
			}
			return;			
		}
		
		while(!end())
		{
			InputStream stream;
			QTPNode currentNode = getNext(QTP.topBranchingNode());
			if (currentNode == null)
			{
				this.isEnded = true;
				//System.err.println("<JFast::execTJfast>: getNext() returns null");
				return;
			}
			stream = inputs.get(leafNodes.indexOf(currentNode));
			if (stream.streamConsumed())
			{
				throw new DBException("TJFast getNext() returns a node while its stream is finished.!!!!");
			}
			else
			{
				outputToSelfList(currentNode);
				stream.advanceStream();
				stream.locateMatchedLabel();
				return;
			}
		}
	}
	
	private TwigResult next(QTPNode qNode) throws DBException, IOException
	{
		while(leavesPathRes.get(qNode).isEmpty() && !end())
		{
			execTJfast();
		}
		
		if (end() && leavesPathRes.get(qNode).isEmpty())
		{
			if (leafNodes.size() > 1) this.emptyOutSets(QTP.topBranchingNode());
//			System.out.println("findNodeMatches: " + T);
//			System.out.println("findPathMatches: " + T2);
		}
		return leavesPathRes.get(qNode).poll();
	}
	
	private boolean end()
	{
		if (isEnded)
			return true;
		
		for (InputStream input : inputs) 
		{
			if(!input.streamConsumed()) return false;
		}
		
		return true;
	}//end
	
	private QTPNode getNext(QTPNode node) throws DBException
	{
		if(!node.hasChildren()) return node;
		
		ArrayList<QTPNode> dbl = QTP.directBranchingOrLeafNodes(node);
		HashMap<QTPNode, QTPNode> f = new HashMap<QTPNode, QTPNode>(); 
		TwinObject<DeweyID, DeweyID> minE = null;
		QTPNode minQNode = null;
		TwinObject<DeweyID, DeweyID> maxE = null;
		QTPNode maxQNode = null;
		boolean first = true;
		
		for (QTPNode dblNode : dbl) 
		{
			QTPNode cNode = getNext(dblNode);			
			if (cNode==null) continue;
			f.put(dblNode, cNode);
			int index = branchingNodes.indexOf(dblNode);
			
			if (dblNode.isBranching())
			{		
				if(branchingNodesSets.get(index).isEmpty())
				{
					return cNode;
				}
			}
			
			TwinObject<DeweyID, DeweyID> e = max(MB(dblNode, node));
			if (e == null) continue; 
			if (first)
			{
				minE = e;
				maxE = e;
				minQNode = cNode;
				maxQNode = cNode;
				first = false;
			}
			
			if ((minE == null ) || (e != null && e.getO1().compareTo(minE.getO1()) < 0)
					|| (e != null && (e.getO1().compareTo(minE.getO1()) == 0) && (e.getO1().compareTo(minE.getO2())) < 0))
			{
				minE = e;
				minQNode = cNode;
			}
			
			//when stream is finished you could consider that it has the biggest ID!!!
			if ((maxE != null) && ((e == null || e.getO1().compareTo(maxE.getO1()) > 0)))
			{
				maxE = e;
				maxQNode = cNode;
			}
		} //for (QTPNode dblNode : dbl)
		
		if (minE == null) 
			return null;
		
		for (QTPNode dblNode : dbl) 
		{
			ArrayList<TwinObject<DeweyID, DeweyID>> mb = MB(dblNode, node);
			if (mb.size() == 0) continue;
			
			boolean retFlag = true;
			for (TwinObject<DeweyID, DeweyID> deweyID : mb) 
			{
				if (maxE != null && deweyID.getO1().isAncestorOrSelfOf(maxE.getO1()))
				{
					retFlag = false;
					break;
				}
			}
			
			if (retFlag)
			{
				QTPNode retNode = f.get(dblNode);
				return retNode;
			}
			
		} // (second for)for (QTPNode dblNode : dbl)
		
		ArrayList<TwinObject<DeweyID, DeweyID>> mb = MB(minQNode, node);
		for (TwinObject<DeweyID, DeweyID> deweyID : mb) 
		{
			if (deweyID.getO1().isAncestorOrSelfOf(maxE.getO1())) 
			{
				updateSet(node, deweyID);
			}
		}//for (DeweyID deweyID : mb)
		
		return minQNode;
	}//getNext()
	
	private void updateSet(QTPNode branchingNode, TwinObject<DeweyID, DeweyID> deweyID) throws DBException
	{
		int setIndex = branchingNodes.indexOf(branchingNode);
		TwinArrayList<TwinObject<DeweyID, DeweyID>,TwinObject<ArrayList<TwinObject<QueryTuple, QTPNode>>,ArrayList<TwinObject<QueryTuple, QTPNode>>>> set = this.branchingNodesSets.get(setIndex);
		//Clear Set
		set.sortBasedOnL1(new BranchingSetComparator());
		for (int i = set.size() - 1; i >= 0 ; i--) 
		{
			DeweyID cdeweyID = set.get(i).getO1().getO1();
			if (!cdeweyID.isAncestorOrSelfOf(deweyID.getO1()) && !deweyID.getO1().isAncestorOrSelfOf(cdeweyID))
			{
				execBlockingOutput(branchingNode, i);
					
				set.remove(i);
			} //if
		}
		
		//Add deweyID to set
		int index = -1; 
		int next = -1;
		for (int j = 0; j < set.size(); j++) 
		{
			int c = set.getL1(j).getO1().compareTo(deweyID.getO1());
			if (c > 0 && next == -1)
			{
				next = j;
			}
			if (c == 0)
			{
				index = j;
				break;
			}
		}
		if (index == -1)
		{
			TwinObject<ArrayList<TwinObject<QueryTuple, QTPNode>>, ArrayList<TwinObject<QueryTuple, QTPNode>>> lists;
			lists = new TwinObject<ArrayList<TwinObject<QueryTuple, QTPNode>>, ArrayList<TwinObject<QueryTuple, QTPNode>>>();
			lists.set(new ArrayList<TwinObject<QueryTuple, QTPNode>>(), new ArrayList<TwinObject<QueryTuple, QTPNode>>());
			
			if (next == -1)
			{
				next = 0;
			}
			set.add(next, deweyID, lists);		
		}
	}//updateSet
	
	private TwinObject<DeweyID, DeweyID> max(ArrayList<TwinObject<DeweyID, DeweyID>> ids) throws DBException
	{
		TwinObject<DeweyID, DeweyID> deweyID = null;
		if (ids == null) return null;
		for (TwinObject<DeweyID, DeweyID> cdeweyID : ids)
		{
			if(deweyID == null)
			{
				deweyID = cdeweyID;
			}
			else
			{
				try
				{
				if (cdeweyID.getO1().compareTo(deweyID.getO1()) > 0)
				{
					deweyID = cdeweyID;
				}
				}
				catch (Exception e) {
					int k = 0;
					e.printStackTrace();
					throw new DBException("TJFAST:MAX");
					
				}
			}
		}
		return deweyID;
	}

	//another implementation of max method! it was not possible to overload it!!! 
	private DeweyID max2(ArrayList<DeweyID> ids)
	{
		DeweyID deweyID = null;
		if (ids == null) return null;
		for (DeweyID cdeweyID : ids)
		{
			if(deweyID == null)
			{
				deweyID = cdeweyID;
			}
			else
			{
				if (cdeweyID.compareTo(deweyID) > 0)
				{
					deweyID = cdeweyID;
				}
			}
		}
		return deweyID;
	}

	private ArrayList<TwinObject<DeweyID, DeweyID>> MB(QTPNode n, QTPNode b) throws DBException
	{
		TwinObject<DeweyID, DeweyID> deweyID = null;
		
		if (n.isBranching())
		{
			int index = branchingNodes.indexOf(n);
			ArrayList<TwinObject<DeweyID, DeweyID>> set = branchingNodesSets.get(index).getL1();
			//select maximal deweyID in the set
			deweyID = max(set);
		}
		else
		{
			int index = leafNodes.indexOf(n);
			DeweyID id = inputs.get(index).getStreamHead();
			if (id != null)
			{
				deweyID = new TwinObject<DeweyID, DeweyID>(id, id); 
			}
			else
			{
				deweyID = null;
			}
		}
		
		if (deweyID == null) return new ArrayList<TwinObject<DeweyID, DeweyID>>(0);
		
		int PCR = deweyID.getO1().getCID();
		Set<TwinObject<Integer, Integer>> levels = null; //<Level, PCR>	
		levels = this.findNodeMatches(PCR, n, b);
		
		
		ArrayList<TwinObject<DeweyID, DeweyID>> result = new ArrayList<TwinObject<DeweyID, DeweyID>>(levels.size());
		DeweyID leafNode = deweyID.getO2();

		for (TwinObject<Integer, Integer> level: levels)
		{
			try
			{
			int l = level.getO1();
			DeweyID anc = deweyID.getO1().getAncestorDeweyID(l);
			anc.setCID(level.getO2());
			if (anc == null)
			{
				int k = 0;
			}
			result.add(new TwinObject<DeweyID, DeweyID>(anc, leafNode));
			}
			catch (Exception e)
			{
				e.printStackTrace();
				throw new DBException("TJFast::MB: catastrophic problem");			
			}
			//node = node.getParent();
		}

		return result;
	}
	
	public Set<TwinObject<Integer, Integer>> findNodeMatches(int PCR, QTPNode n, QTPNode b) throws DBException
	{
		ArrayList<ArrayList<TwinObject<Integer, Integer>>> paths = findPathMatches(PCR, n);
		
		HashSet<TwinObject<Integer, Integer>> set = new HashSet<TwinObject<Integer,Integer>>();
		
		for (ArrayList<TwinObject<Integer, Integer>> path : paths) 
		{
			TwinObject<Integer, Integer> res = new TwinObject<Integer, Integer>(path.get(b.getLevel()).getO1(), path.get(b.getLevel()).getO2());
			set.add(res);
		}
		
		return set; 
	}
	
	//First matches of the path from root to the Head of QTPNode stream regards to path from root of
	//QTP up to the parent of QTPNode are found and then head of QTPNode stream is added to this matches
	//to find matches of QTPnode which are really ended to the head of QTPnode stream
	public ArrayList<ArrayList<TwinObject<Integer, Integer>>> findPathMatches(int PCR, QTPNode qNode) throws DBException
	{
		ArrayList<ArrayList<TwinObject<Integer, Integer>>> resArr = null;
		QTPNode node = qNode.getParent(); // to first find matches regards to the parent of the node
		this.PCRQNode.set(PCR, qNode);
		resArr = this.pathMatches.get(this.PCRQNode);
		if (resArr == null)
		{
			ArrayList<Integer> path = new ArrayList<Integer>();
			while (node != null)
			{
				String name = node.getName();
				int vocID = dictionaryMgr.getVocabularyID(transaction, doc.getID(), XTCcalc.getBytes(name));
				path.add(0, vocID);
				node = node.getParent();
			}
//			long s = System.currentTimeMillis();
			resArr = ssMgr.findPathMatches(transaction, doc, PCR, path);
//			long e = System.currentTimeMillis();
//			T2 = T2 + (e-s);
			TwinObject<Integer, QTPNode> PCRQNode = new TwinObject<Integer, QTPNode>(PCR, qNode);
			node = qNode.getParent();
			for (int i = 0; i < resArr.size(); )
			{
				ArrayList<TwinObject<Integer, Integer>> res = resArr.get(i);
				
				if (!checkPath(node, res))
				{
					resArr.remove(res);
				}
				else
				{
					++i;
				}
			}

			this.pathMatches.put(PCRQNode, resArr);
		} //if (resArr == null)
		
		return resArr;
	}
	
	private ArrayList<QueryTuple> completePath(QTPNode qNode, DeweyID deweyID) throws DBException
	{
		ArrayList<QueryTuple> result = new ArrayList<QueryTuple>();
		QueryTuple tuple = null;		
		ArrayList<ArrayList<TwinObject<Integer, Integer>>> resArr = null;
		resArr = this.findPathMatches(deweyID.getCID(), qNode);
		for (ArrayList<TwinObject<Integer, Integer>> res : resArr) 
		{
			if (qNode.getAxis() == SvrCfg.AXIS_PARENT)
			{
				if (deweyID.getLogicalLevel() - res.get(res.size() - 1).getO1() != 1)
				{
					continue;
				}
			}

			tuple = new QueryTuple();
			for (TwinObject<Integer, Integer> level : res) 
			{
				DeweyID anc = deweyID.getAncestorDeweyID(level.getO1());
				tuple.addDeweyID(anc);

			}
			tuple.addDeweyID(deweyID);
			result.add(tuple);
		}
		
		return result;
	}
	//In this method some matches of head of stream of QTPNode regards to the path of QTPNode are made
	private void outputToSelfList(QTPNode qNode) throws DBException
	{
				
		int index = leafNodes.indexOf(qNode);
		DeweyID deweyID = inputs.get(index).getStreamHead();

//		QTPNode node = qNode.getParent(); // to first find matches regards to the parent of the node 
		int PCR = deweyID.getCID();
		ArrayList<ArrayList<TwinObject<Integer, Integer>>> resArr = null;
		
		resArr = this.findPathMatches(PCR, qNode);

		
		//create deewey ids and add the result to final result
		QueryTuple tuple = null;		
		
		for (ArrayList<TwinObject<Integer, Integer>> res : resArr) 
		{
			if (qNode.getAxis() == SvrCfg.AXIS_PARENT)
			{
				if (deweyID.getLogicalLevel() - res.get(res.size() - 1).getO1() != 1)
				{
					continue;
				}
			}

			tuple = new QueryTuple();
			for (TwinObject<Integer, Integer> level : res) 
			{
				DeweyID anc = deweyID.getAncestorDeweyID(level.getO1());
				tuple.addDeweyID(anc);

			}
			tuple.addDeweyID(deweyID);
			
//			if (!checkPath(qNode, tuple))
//			{
//				continue;
//			}

			//Check tuple to be OK with branching Sets
			boolean out = true;			
			//number of IDs in results should be the same as qNode.level() + 1!!!
			//path.size() contains one less than actual path and level should be 
			//equal to the length of actual path minus one because indexes are stating from 0
			//thus int level = path.size(); is correct !!!!
			QTPNode node = qNode;
			int upNearestBranchingAnc = -1;
			TwinObject<DeweyID, QTPNode> branchNode = new TwinObject<DeweyID, QTPNode>();
			int level = qNode.getLevel(); 
			TwinObject<DeweyID, QTPNode> id = new TwinObject<DeweyID, QTPNode>();
			while (node != null)
			{
				int i = branchingNodes.indexOf(node);
				if (i != -1)//branching node
				{
					id.set(tuple.getIDForPos(level), qNode);

					if (upNearestBranchingAnc == -1)
					{
						upNearestBranchingAnc = i;
						branchNode.set(id.getO1(), id.getO2());
					}

					boolean contains = false;
					ArrayList<TwinObject<DeweyID, DeweyID>> s = branchingNodesSets.get(i).getL1();
					for (int j = 0; j < s.size(); j++) 
					{
						int c = s.get(j).getO1().compareTo(id.getO1());
						if (c == 0)
						{
							contains = true;
							break;
						}
					}

					if (!contains)
					{
						out = false;
						break;
					}

				}
				node = node.getParent();
				--level;
			}
			
			if (tuple != null && out)
			{
				//ADD TO SELF LIST
				int idx = -1;
				ArrayList<TwinObject<DeweyID, DeweyID>> s = branchingNodesSets.get(upNearestBranchingAnc).getL1();
				for (int j = 0; j < s.size(); j++) 
				{
					int c = s.get(j).getO1().compareTo(branchNode.getO1());
					if (c == 0)
					{
						idx = j;
						break;
					}
				}

				//int idx = branchingNodesSets.get(upNearestBranchingAnc).indexOfL1(branchNode);
				branchingNodesSets.get(upNearestBranchingAnc).getL2(idx).getO1().add(new TwinObject<QueryTuple, QTPNode>(tuple, qNode));
			}	
			else
			{
				int K =0;
			}
			
			
			
			
		} //for (ArrayList<Integer> res : resArr) 
		
	}
	
	private void outputSolutions(TwinObject<QueryTuple, QTPNode> outputNode) throws DBException
	{		
		QTPNode qNode = outputNode.getO2();
		QueryTuple tuple = outputNode.getO1();
		Queue<TwigResult> pathResArr = leavesPathRes.get(qNode);
		
		if (checkPath(qNode, tuple))
		{
			pathResArr.add(new TwigResult(QTP, qNode, tuple));
			//System.out.println(tuple + " is added");
		}
		else
		{
			int k=0;
		}
	} //outputSolutions()

	
	private boolean checkPath(QTPNode qNode, QueryTuple pathRes) throws DBException
	{
		//checks each node with its parent to match the axis
		//axis of root should be checked in the beginning of TwigStack execution in the Execute method
	
		for (int i = pathRes.size() - 1; i > 0; i--) 
		{
			if(qNode.getAxis() == SvrCfg.AXIS_PARENT)
			{								
				if (!pathRes.getIDForPos(i-1).isParentOf((pathRes.getIDForPos((i)))))//distance is more than two level
				{
					return false;
				}
			}
			qNode = qNode.getParent();
		}
		return true;
	}

	/**
	 * 
	 * @param qNode
	 * @param pathRes contains the level of pathsynopsis nodes which constructing the path
	 * @return
	 * @throws DBException
	 */
	private boolean checkPath(QTPNode qNode, ArrayList<TwinObject<Integer, Integer>> pathRes) throws DBException
	{
		//checks each node with its parent to match the axis
		//axis of root should be checked in the beginning of TwigStack execution in the Execute method
	
		for (int i = pathRes.size() - 1; i > 0; i--) 
		{
			if(qNode.getAxis() == SvrCfg.AXIS_PARENT)
			{								
				if (pathRes.get(i).getO1() - pathRes.get(i-1).getO1() != 1)//distance is more than two level
				{
					return false;
				}
			}
			qNode = qNode.getParent();
		}
		return true;
	}

	private void emptyOutSets(QTPNode branchingNode) throws DBException
	{
		ArrayList<QTPNode> dbl = QTP.directBranchingOrLeafNodes(branchingNode);
		for (QTPNode node : dbl) 
		{
			if (node.isBranching())
			{
				emptyOutSets(node);
			}
		}

		int setIndex = branchingNodes.indexOf(branchingNode);
		TwinArrayList<TwinObject<DeweyID, DeweyID>,TwinObject<ArrayList<TwinObject<QueryTuple, QTPNode>>,ArrayList<TwinObject<QueryTuple, QTPNode>>>> set = this.branchingNodesSets.get(setIndex);
		set.sortBasedOnL1(new BranchingSetComparator());
		//each time remove last element
		for (int i = set.size() - 1; i >= 0; i = set.size() - 1) 
		{
			execBlockingOutput(branchingNode, i);
			set.remove(i);
		}
//		for (int i = 0; i < set.size(); ) 
//		{
//			execBlockingOutput(branchingNode, i);
//			set.remove(i);
//		}

	}
	
	private void execBlockingOutput(QTPNode branchingNode, int itemToBeRemovedIndex) throws DBException
	{
		//FIXME: this blocking technique produce results sorted based on the top branching node
		//which is somehow different from sorting of TwigStack
		int setIndex = branchingNodes.indexOf(branchingNode);
		TwinArrayList<TwinObject<DeweyID, DeweyID>,TwinObject<ArrayList<TwinObject<QueryTuple, QTPNode>>,ArrayList<TwinObject<QueryTuple, QTPNode>>>> set;
		set = this.branchingNodesSets.get(setIndex);
		
		DeweyID cdeweyID = set.get(itemToBeRemovedIndex).getO1().getO1();
		if (set.size() > 1) //part a of blocking technique
		{
			int minDis = Integer.MAX_VALUE;
			int nearestAnc = -1;
			for (int j = set.size() - 1; j >= 0 ; j--) 
			{
				if (itemToBeRemovedIndex != j) // do not compare a node with itself
				{
					DeweyID ancdeweyID = set.get(j).getO1().getO1();
					if (ancdeweyID.isAncestorOf(cdeweyID))
					{
						int dis = cdeweyID.getLogicalLevel() - ancdeweyID.getLogicalLevel();
						if (minDis > dis)
						{
							minDis = dis;
							nearestAnc = j;
						}
					}
				} //if
			} //for

			ArrayList<TwinObject<QueryTuple, QTPNode>> ancInheritList;
			ancInheritList = set.getL2(nearestAnc).getO2();
			ancInheritList.addAll(set.getL2(itemToBeRemovedIndex).getO1()); //Append Self list of node that has to be removed
			ancInheritList.addAll(set.getL2(itemToBeRemovedIndex).getO2()); //Append Inherit list of node that has to be removed
		}
		else if (set.size() == 1)
		{
			int upNearestBranchingAnc = -1;
			QTPNode currentNode = this.branchingNodes.get(setIndex);
			
			currentNode = currentNode.getParent();					
			while (currentNode != null)
			{
				upNearestBranchingAnc = this.branchingNodes.indexOf(currentNode); 
				if ( upNearestBranchingAnc != -1) break;
				currentNode = currentNode.getParent();
			}
			
			if (currentNode != null) //part b of blocking technique
			{
				TwinArrayList<TwinObject<DeweyID, DeweyID>,TwinObject<ArrayList<TwinObject<QueryTuple, QTPNode>>,ArrayList<TwinObject<QueryTuple, QTPNode>>>> ancSet;
				ancSet = branchingNodesSets.get(upNearestBranchingAnc);
				ArrayList<TwinObject<ArrayList<TwinObject<QueryTuple, QTPNode>>, ArrayList<TwinObject<QueryTuple, QTPNode>>>> lists;
				lists = ancSet.getL2();
				ArrayList<TwinObject<DeweyID, DeweyID>> ancNodes = ancSet.getL1();

				//TODO: In TJFast paper they said that the following loop should be executed completely
				//but it has some bugs because it is possible to out put one result two or more times
				//now the loop is only executed for the nearest ancestor but it not checked if this change has any side effect or not
				for (int j = 0; j < ancNodes.size(); j++) 
				{
					if (ancNodes.get(j).getO1().isAncestorOrSelfOf(set.getL1(0).getO1()))
					{
						ArrayList<TwinObject<QueryTuple, QTPNode>> ancSelfList = lists.get(j).getO1();
						ancSelfList.addAll(set.getL2(0).getO1()); //Append Self list of node that has to be removed
						ancSelfList.addAll(set.getL2(0).getO2()); //Append Inherit list of node that has to be removed
						break;
					}
				}
			}
			else //part c of blocking technique
			{
				//Now contents of Self and inherit list should be output
				
				set.getL2(0).getO1().addAll(set.getL2(0).getO2());
				
				TwinObject<QueryTuple, QTPNode>[] arr = new TwinObject[set.getL2(0).getO1().size()];
				
				arr = set.getL2(0).getO1().toArray(arr);
				Arrays.sort(arr, new BranchingSetSelfListComparator());
				
				for (int j = 0; j < arr.length; j++) 
				{
					outputSolutions(arr[j]);
					//outputSolutions(set.getL2(0).getO1().get(j));
				}
			}
		} //close if (set.size() > 1)
	
	}
	private TwigJoinOp mergeAllPathSolutions() throws DBException
	{
//		QTPNode[] leaves = new QTPNode[leavesPathRes.size()];
//		leaves = leavesPathRes.keySet().toArray(leaves);

		TwigInput in1 = new InputWrapper(this, /*this.QTP, */leafNodes.get(0)/*, leavesPathRes.get(leaves[0])*/);

		if (leafNodes.size() == 1)
		{
			//TwigJoinOp twigJoinOp = new TwigJoinOp(leavesPathRes.get(leafNodes.get(0)));
			TwigJoinOp twigJoinOp = new TwigJoinOp(in1);
			twigJoinOp.open();
			return twigJoinOp;
		}

		TwigInput in2 = new InputWrapper(this, /*this.QTP, */leafNodes.get(1)/*, leavesPathRes.get(leaves[1])*/);
		TwigJoinOp twigJoinOp = new TwigJoinOp(in1, in2, QTP.findNCA(leafNodes.get(0), leafNodes.get(1)).getLevel()); 
		for(int i=2; i < leafNodes.size(); ++i)
		{
			TwigInput in = new InputWrapper(this, /*this.QTP, */leafNodes.get(i)/*, leavesPathRes.get(leaves[i])*/);
			twigJoinOp = new TwigJoinOp(twigJoinOp, in, QTP.findNCA(leafNodes.get(i-1), leafNodes.get(i)).getLevel());
		}
		twigJoinOp.open(); 
		return twigJoinOp;
//		TwigInput in1 = new InputWrapper(this, /*this.QTP, */leaves[0]/*, leavesPathRes.get(leaves[0])*/);
//		TwigInput in2 = new InputWrapper(this, /*this.QTP, */leaves[1]/*, leavesPathRes.get(leaves[1])*/);
//		TwigJoinOp twigJoinOp = new TwigJoinOp(in1, in2, QTP.findNCA(leaves[0], leaves[1]).getLevel()); 
//		for(int i=2; i < leafNodes.size(); ++i)
//		{
//			TwigInput in = new InputWrapper(this, /*this.QTP, */leaves[i]/*, leavesPathRes.get(leaves[i])*/);
//			twigJoinOp = new TwigJoinOp(twigJoinOp, in, QTP.findNCA(leaves[i-1], leaves[i]).getLevel());
//		}

	}
	
	public long getIOTime()
	{			
		for (int i = 0; i < inputs.size(); i++) 
		{
			this.IOTime += inputs.get(i).getIOTime();
		}
		
		return this.IOTime;
	}
	
	public long getNumberOfReadElements()
	{
		for (int i = 0; i < inputs.size(); i++) 
		{
			this.numberOfReadElements += inputs.get(i).getNumberOfReadElements();
		}
		
		return this.numberOfReadElements;
		
	}

	////////////////////////////////////////////////////////////////
	///////////////INPUTWRAPPER CLASS///////////////////////////////
	///////////////////////////////////////////////////////////////
	private class InputWrapper implements TwigInput 
	{
		private TJFast aTJFast;
		private QTPNode qNode;
		//private ArrayList<QTPNode> nodes;

		
		public InputWrapper(TJFast aTJFast, /*QueryTreePattern QTP, */QTPNode qNode /*,Queue<TwigResult> pathResults*/)
		{
			this.aTJFast = aTJFast;
			this.qNode = qNode;
			

			QTPNode node = qNode; 
//			nodes = new ArrayList<QTPNode>();
//			while(node != null)
//			{
//				nodes.add(0, node);
//				node = node.getParent();
//			}

		}

		public void close() throws DBException {
			// TODO Auto-generated method stub
		}

		public TwigResult next() throws DBException,IOException 
		{		
			TwigResult res = aTJFast.next(this.qNode);			
			return res;
		}

		public void open() throws DBException {
			// TODO Auto-generated method stub

		}
		
		public QTPNode getRightLeaf()
		{
			return qNode;
		}

	} //private class InputWrapper
	////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////

	public class BranchingSetComparator implements Comparator<TwinObject<DeweyID, DeweyID>> 
	{
		public int compare(TwinObject<DeweyID, DeweyID> s1, TwinObject<DeweyID, DeweyID> s2)
		{
			return s1.getO1().compareTo(s2.getO1());
		}
	}

	public class BranchingSetSelfListComparator implements Comparator<TwinObject<QueryTuple, QTPNode>> 
	{
		public int compare(TwinObject<QueryTuple, QTPNode> s1, TwinObject<QueryTuple, QTPNode> s2)
		{
			if (s1.getO2() == s2.getO2())
			{
				QueryTuple psNodeList1 = s1.getO1();
				QueryTuple psNodeList2 = s2.getO1();
//				ArrayList<DeweyID> psNodeList1 = t1.getValues();
//				ArrayList<DeweyID> psNodeList2 = t2.getValues();
//				by lr on 28.03.07 - try/catch statement deleted (xtcDeweyID.compareTo no longer throws an exception).
				for (int i = 0; i < psNodeList1.size() && i < psNodeList2.size(); i++) 
				{
					int comp = psNodeList1.getIDForPos(i).compareTo(psNodeList2.getIDForPos(i));
					if (comp != 0 ) return comp;	
				}
				if (psNodeList1.size() == psNodeList2.size())	{
					return 0;
				} else if (psNodeList1.size() < psNodeList2.size()) {
					return -1;
				} else {// v1.size() > v2.size()
					return 1;
				}
			}
			else
			{
				int h1 = s1.getO2().hashCode();
				int h2 = s2.getO2().hashCode();

				if (h1 < h2)
				{
					return -1;
				}
				else if (h1 > h2)
				{
					return 1;
				}
				return 0;
			}
		}
	}

	
}

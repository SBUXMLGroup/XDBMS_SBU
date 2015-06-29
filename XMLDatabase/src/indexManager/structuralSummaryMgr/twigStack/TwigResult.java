package indexManager.structuralSummaryMgr.twigStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import xmlProcessor.DBServer.DBException;
import indexManager.StructuralSummaryNode;
import xmlProcessor.QTP.QTPNode;
import xmlProcessor.QTP.QueryTreePattern;

/**
 * This class is used for storing final matches of Twig on pathsynopsis
 * implementation is not memory  optimized due the smallness of pathsynopsis and results on this structure
 *   
 * @author Kamyar
 *
 */
public class TwigResult 
{
	
//	private TwigStackNode twigStackRoot;
//	private TwigStackNode twigStackLastMergedLeaf;
//	private HashMap<TwigStackNode, StructuralSummaryNode> data;
//
//	public TwigResult(TwigStackNode twigStackRoot)
//	{
//		this.twigStackRoot = twigStackRoot;
//		this.data = new  HashMap<TwigStackNode, StructuralSummaryNode>();
//	}
//	
//	public void setPathResult(PathResult pathResult)
//	{
//		this.twigStackLastMergedLeaf = pathResult.getPathLeaf();
//		ArrayList<StructuralSummaryNode> psNodes = pathResult.getPath();
//		TwigStackNode node = pathResult.getPathLeaf();
//		for (int i = psNodes.size() - 1; i >= 0; i--)
//		{
//			data.put(node, psNodes.get(i));
//			node = node.getParent();
//		}		
//	}

	
	private QueryTreePattern QTP;
	private QTPNode twigRoot;
	private QTPNode twigLastMergedLeaf;
	private HashMap<QTPNode, StructuralSummaryNode> data;	

	private TwigResult(QueryTreePattern QTP)
	{
		this.QTP = QTP;
		this.twigRoot = QTP.root();
		this.data = new  HashMap<QTPNode, StructuralSummaryNode>();
		this.twigLastMergedLeaf = null;
				
	}
	
	public TwigResult(QueryTreePattern QTP, QTPNode node, ArrayList<StructuralSummaryNode> tuple)
	{
		this.QTP = QTP;
		this.twigRoot = QTP.root();
		this.data = new  HashMap<QTPNode, StructuralSummaryNode>();
		this.twigLastMergedLeaf = node;
		for (int i = tuple.size() - 1; i >= 0; i--)
		{
			data.put(node, tuple.get(i));
			node = node.getParent();
		}		
		
	}

	
//	public void mergeWithLast0(PathResult pathResult)
//	{		
//		ArrayList<StructuralSummaryNode> path = pathResult.getPath();
//		TwigStackNode nca = findNCA(pathResult.getPathLeaf(), this.twigStackLastMergedLeaf);		
//		
//		TwigStackNode twNode = pathResult.getPathLeaf();
//		int i = path.size() - 1;		
//		while (twNode != nca)
//		{
//			data.put(twNode, path.get(i));
//			twNode = twNode.getParent();
//			--i;
//		}
//		this.twigStackLastMergedLeaf = pathResult.getPathLeaf();		
//	}

	//create a new copy of this object merge it with pathResult and return new created object
//	public TwigResult mergeWithLast(PathResult pathResult)
//	{		
//		TwigResult result = new TwigResult(this.twigStackRoot);
//		
//		Set<TwigStackNode> keys = this.data.keySet();
//		for (Iterator<TwigStackNode> iterator = keys.iterator(); iterator.hasNext();)
//		{
//			TwigStackNode twigStackNode = iterator.next();
//			result.data.put(twigStackNode, this.data.get(twigStackNode));
//		}
//		
//		ArrayList<StructuralSummaryNode> path = pathResult.getPath();
//		TwigStackNode NCA = findNCA(pathResult.getPathLeaf(), this.twigStackLastMergedLeaf);		
//		
//		TwigStackNode twNode = pathResult.getPathLeaf();
//		int i = path.size() - 1;		
//		while (twNode != NCA)
//		{
//			result.data.put(twNode, path.get(i));
//			twNode = twNode.getParent();
//			--i;
//		}
//		result.twigStackLastMergedLeaf = pathResult.getPathLeaf();
//		return result;
//	}
	
	//create a new copy of this object merge it with pathResult and return new created object
	public TwigResult mergeWithLast(TwigResult twigResult)
	{		
		TwigResult result = new TwigResult(this.QTP);
		
		Set<QTPNode> keys = this.data.keySet();
		for (Iterator<QTPNode> iterator = keys.iterator(); iterator.hasNext();)
		{
			QTPNode twigNode = iterator.next();
			result.data.put(twigNode, this.data.get(twigNode));
		}
		
		//QueryTuple tuple = twigResult.getPath();
		QTPNode NCA = QTP.findNCA(twigResult.twigLastMergedLeaf, this.twigLastMergedLeaf);		
		
		QTPNode qNode = twigResult.twigLastMergedLeaf;
		//int i = tuple.size() - 1;		
		while (qNode != NCA)
		{
			result.data.put(qNode, twigResult.data.get(qNode));// tuple.getIDForPos(i));
			qNode = qNode.getParent();
			//--i;
		}
		result.twigLastMergedLeaf = twigResult.twigLastMergedLeaf;
		return result;
	}
	

//	//list the ancestor of two nodes then walk from the root to the point that they become different
//	private TwigStackNode findNCA(TwigStackNode node1, TwigStackNode node2)
//	{
//		ArrayList<TwigStackNode> node1Anc = new ArrayList<TwigStackNode>();
//		while (node1 != null)
//		{
//			node1Anc.add(node1);
//			node1 = node1.getParent();
//		}
//		
//		ArrayList<TwigStackNode> node2Anc = new ArrayList<TwigStackNode>();
//		while (node2 != null)
//		{
//			node2Anc.add(node2);
//			node2 = node2.getParent();
//		}
//		//if everything is OK then the root should be the same so there is no need to compare it 
//		int i1 = node1Anc.size() - 2;
//		int i2 = node2Anc.size() - 2;
//		for (; (i1 >= 0) && (i2 >= 0); --i1, --i2)
//		{
//			if (node1Anc.get(i1) != node2Anc.get(i2)) return node1Anc.get(i1 + 1);
//		}
//		//this point will be reached if something is wrong
//		return null;
//	
//	}
//	
//	//returns level of NCA if possibleToMerge otherwise -1
//	public int possibleToMerge(PathResult path)
//	{
////		TwigStackNode NCA = findNCA(twigStackLastMergedLeaf, path.getPathLeaf());
//
//		TwigStackNode node1 = twigStackLastMergedLeaf;
//		ArrayList<TwigStackNode> node1Anc = new ArrayList<TwigStackNode>();
//		while (node1 != null)
//		{
//			node1Anc.add(node1);
//			node1 = node1.getParent();
//		}
//		
//		TwigStackNode node2 = path.getPathLeaf();
//		ArrayList<TwigStackNode> node2Anc = new ArrayList<TwigStackNode>();
//		while (node2 != null)
//		{
//			node2Anc.add(node2);
//			node2 = node2.getParent();
//		}
//		//if everything is OK then the root should be the same so there is no need to compare it 
//		int i1 = node1Anc.size() - 2;
//		int i2 = node2Anc.size() - 2;
//		
//		boolean possible = true;
//		for (; ((i1 >= 0) && (i2 >= 0) && possible); --i1, --i2)
//		{			
//			if (node1Anc.get(i1) != node2Anc.get(i2))
//			{
//				break; //NCA is reached so they are the same from root to NCA
//			}
//
//			StructuralSummaryNode psNode1 = data.get(node1Anc.get(i1));
//			StructuralSummaryNode psNode2 = path.getPath().get((node2Anc.size() -1) -i2);
//			if (psNode1 != psNode2) possible = false;
//		}
//		
//		if (possible)
//		{
//			return node2Anc.size() -i2 -2;//equal to: (node2Anc.size()-1)-(i2 +1)
//		}
//
//		return -1;
//
//	}

	public int equalUpTo(int level, TwigResult res) throws DBException
	{
		if (res.twigLastMergedLeaf != this.twigLastMergedLeaf)
		{
			//throw new DBException("Error in TwigStack Merge");
		}
		QTPNode node = twigLastMergedLeaf;
		ArrayList<QTPNode> nodeAnc = new ArrayList<QTPNode>();
		while (node != null)
		{
			nodeAnc.add(node);
			node = node.getParent();
		}
		
		boolean equal = true;
		
		//ArrayList<StructuralSummaryNode> pathRes2 = path.getPath();
		for (int i= nodeAnc.size() - 1; level >= 0; --level, --i)
		{
			int c = this.data.get(nodeAnc.get(i)).getRangeID().compareTo(res.data.get(nodeAnc.get(i)).getRangeID());			
			if ( c != 0) return c;
		}
		return 0;
	}

	/**
	 * returns only IDs which are related to the QTPNodes.
	 * returned IDs are ordered regard to the order of QTPNode(s) in QTPNodes  
	 * @param QTPNodes - ArrayList of QTPNode(s) which their related ID should be returned
	 * @return
	 */

	public ArrayList<StructuralSummaryNode> getRes(ArrayList<QTPNode> QTPNodes)
	{
		ArrayList<StructuralSummaryNode> tuple = new ArrayList<StructuralSummaryNode>();
		for (QTPNode node : QTPNodes) 
		{
			tuple.add(data.get(node));
		}

		return tuple;
	}
	
	public HashMap<QTPNode, StructuralSummaryNode> getRes()
	{
		return this.data;
	}
	
	public String toString()
	{
		Set<QTPNode> keys = this.data.keySet();
		StringBuilder str = new StringBuilder();
		for (Iterator<QTPNode> iterator = keys.iterator(); iterator.hasNext();)
		{
			QTPNode twigNode = iterator.next();
			str.append(twigNode.getName() + ":" + this.data.get(twigNode).getCID() + "  ");			
		}
		
		return str.toString();
	}
}

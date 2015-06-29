/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xmlProcessor.RG.RG01;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import xmlProcessor.DBServer.DBException;
import indexManager.StructuralSummaryNode;
import xmlProcessor.DeweyID;
import xmlProcessor.QTP.QTPNode;
import xmlProcessor.QTP.QueryTreePattern;
import xmlProcessor.RG.executionPlan.RGExecutionPlanTuple;
import xmlProcessor.DBServer.utils.QueryTuple;

/**
 * This class is used for storing final matches of Twig 
 *    
 * @author Kamyar
 *
 */
public class TwigResult 
{
	
	private QueryTreePattern QTP;
	private QTPNode twigLastMergedLeaf;
	private HashMap<QTPNode, DeweyID> data;	

	private TwigResult(QueryTreePattern QTP)
	{
		this.QTP = QTP;
		this.data = new  HashMap<QTPNode, DeweyID>();
		this.twigLastMergedLeaf = null;
				
	}
	
	public TwigResult(QueryTreePattern QTP, QTPNode node, QueryTuple tuple)
	{
		this.QTP = QTP;
		this.data = new  HashMap<QTPNode, DeweyID>();
		this.twigLastMergedLeaf = node;
		for (int i = tuple.size() - 1; i >= 0; i--)
		{
			data.put(node, tuple.getIDForPos(i));
			node = node.getParent();
		}		
		
	}
	
//	public TwigResult(QueryTreePattern QTP, PathResult pathResult)
//	{
//		this(QTP);
//		setPathResult(pathResult);
//	} 
//
//	public void setPathResult(PathResult pathResult)
//	{
//		this.twigLastMergedLeaf = pathResult.getPathLeaf();
//		QueryTuple tuple = pathResult.getPath();
//		QTPNode node = pathResult.getPathLeaf();
//		for (int i = tuple.size() - 1; i >= 0; i--)
//		{
//			data.put(node, tuple.getIDForPos(i));
//			node = node.getParent();
//		}		
//	}
	
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
//		TwigResult result = new TwigResult(this.QTP);
//		
//		Set<QTPNode> keys = this.data.keySet();
//		for (Iterator<QTPNode> iterator = keys.iterator(); iterator.hasNext();)
//		{
//			QTPNode twigNode = iterator.next();
//			result.data.put(twigNode, this.data.get(twigNode));
//		}
//		
//		QueryTuple tuple = pathResult.getPath();
//		QTPNode NCA = QTP.findNCA(pathResult.getPathLeaf(), this.twigLastMergedLeaf);		
//		
//		QTPNode qNode = pathResult.getPathLeaf();
//		int i = tuple.size() - 1;		
//		while (qNode != NCA)
//		{
//			result.data.put(qNode, tuple.getIDForPos(i));
//			qNode = qNode.getParent();
//			--i;
//		}
//		result.twigLastMergedLeaf = pathResult.getPathLeaf();
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
		
		result.data.put(twigResult.twigLastMergedLeaf, twigResult.data.get(twigResult.twigLastMergedLeaf));

//		//QueryTuple tuple = twigResult.getPath();
//		QTPNode NCA = QTP.findNCA(twigResult.twigLastMergedLeaf, this.twigLastMergedLeaf);		
//		
//		QTPNode qNode = twigResult.twigLastMergedLeaf;
//		//int i = tuple.size() - 1;		
//		while (qNode != NCA)
//		{
//			result.data.put(qNode, twigResult.data.get(qNode));
//			qNode = qNode.getParent();
//			//--i;
//		}

		result.twigLastMergedLeaf = twigResult.twigLastMergedLeaf;
		return result;
	}

	/**
	 * return a new copy of this object which will have DeweyIDs for each node of the QTP 
	 * @param plan
	 * @return
	 */
	public TwigResult completeRes(RGExecutionPlanTuple plan, ArrayList<QTPNode> leafNodes, boolean clone)
	{
		TwigResult result = this;
		
		if (clone)
		{
			result = new TwigResult(this.QTP);

			Set<QTPNode> keys = this.data.keySet();
			for (Iterator<QTPNode> iterator = keys.iterator(); iterator.hasNext();)
			{
				QTPNode twigNode = iterator.next();
				result.data.put(twigNode, this.data.get(twigNode));
			}		 
		}
		
		QTPNode leafNode = leafNodes.get(0); 
		
		ArrayList<Integer> ancLevels = plan.getAncLevels(leafNode);
		DeweyID leafID = result.data.get(leafNode);
		QTPNode node = leafNode.getParent(); 

		for (int j = ancLevels.size() - 1; j >= 0 ; j--)
		{
			result.data.put(node, leafID.getAncestorDeweyID(ancLevels.get(j)));
			node = node.getParent();
		}			
		
		for (int i = 1; i < leafNodes.size(); ++i)
		{
			leafNode = leafNodes.get(i); 
			QTPNode NCA = QTP.findNCA(leafNodes.get(i - 1), leafNodes.get(i));
			
			ancLevels = plan.getAncLevels(leafNode);
			leafID = result.data.get(leafNode);
			node = leafNode.getParent(); 

			for (int j = ancLevels.size() - 1; j > NCA.getLevel() ; j--)
			{
				result.data.put(node, leafID.getAncestorDeweyID(ancLevels.get(j)));
				node = node.getParent();
			}			
			
		}
		
//		//QueryTuple tuple = twigResult.getPath();
//		QTPNode NCA = QTP.findNCA(twigResult.twigLastMergedLeaf, this.twigLastMergedLeaf);		
//		
//		QTPNode qNode = twigResult.twigLastMergedLeaf;
//		//int i = tuple.size() - 1;		
//		while (qNode != NCA)
//		{
//			result.data.put(qNode, twigResult.data.get(qNode));
//			qNode = qNode.getParent();
//			//--i;
//		}

//		DeweyID leafID = tuple.getIDForPos(0);
//		addition.clear();
//		for (int i = 0; i < ancLevels.size(); i++)
//		{
//			addition.add(leafID.getAncestorDeweyID(ancLevels.get(i)));
//		}
//		tuple.addDeweyID(0, addition);
//		
//		return new TwigResult(RG.this.QTP, qNode, tuple);

		result.twigLastMergedLeaf = this.twigLastMergedLeaf;
		return result;
		
	}

	
	
//	//returns NCA level of QTP leaf nodes related to (PathResult path) and 
//	//twigLastMergedLeaf if possibleToMerge otherwise -1
//	public int possibleToMerge(PathResult path)
//	{
////		TwigStackNode NCA = findNCA(twigStackLastMergedLeaf, path.getPathLeaf());
//
//		QTPNode node1 = twigLastMergedLeaf;
//		ArrayList<QTPNode> node1Anc = new ArrayList<QTPNode>();
//		while (node1 != null)
//		{
//			node1Anc.add(node1);
//			node1 = node1.getParent();
//		}
//		
//		QTPNode node2 = path.getPathLeaf();
//		ArrayList<QTPNode> node2Anc = new ArrayList<QTPNode>();
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
//			DeweyID tuple1 = data.get(node1Anc.get(i1));
//			DeweyID tuple2 = path.getPath().getIDForPos((node2Anc.size() -1) -i2);
//			if (tuple1.compareTo(tuple2) != 0) possible = false;
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

	/**
	 * Comparison is based on the last leaf that is inserted into the TwigResult
	 */
	public int compareUpTo(int level, TwigResult res) throws DBException
	{
		DeweyID lID = this.data.get(this.twigLastMergedLeaf);
		DeweyID rID = res.data.get(res.twigLastMergedLeaf);
		return lID.compareTo(level, rID);
	}

	/**
	 * returns only IDs which are related to the QTPNodes.
	 * returned IDs are ordered regard to the order of QTPNode(s) in QTPNodes  
	 * @param QTPNodes - ArrayList of QTPNode(s) which their related ID should be returned
	 * @return
	 */
	public QueryTuple getRes(ArrayList<QTPNode> QTPNodes)
	{
		QueryTuple tuple = new QueryTuple();
		for (QTPNode node : QTPNodes) 
		{
			tuple.addDeweyID(data.get(node));
		}
		return tuple;
	}

	public DeweyID getRes(QTPNode qNode)
	{

		return data.get(qNode);
	}

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package xmlProcessor.TJFast;

//package xtc.server.xmlSvc.xqueryOperators.Twigs.TJFast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import xmlProcessor.DBServer.DBException;
//import xtc.server.metaData.pathSynopsis.util.PathSynopsisNode;
import indexManager.StructuralSummaryNode;
import xmlProcessor.DeweyID;
import xmlProcessor.QTP.QTPNode;
import xmlProcessor.QTP.QueryTreePattern;
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
	private QTPNode twigRoot;
	private QTPNode twigLastMergedLeaf;
	private HashMap<QTPNode, DeweyID> data;	

	private TwigResult(QueryTreePattern QTP)
	{
		this.QTP = QTP;
		this.twigRoot = QTP.root();
		this.data = new  HashMap<QTPNode, DeweyID>();
		this.twigLastMergedLeaf = null;
				
	}
	
	public TwigResult(QueryTreePattern QTP, QTPNode node, QueryTuple tuple)
	{
		this.QTP = QTP;
		this.twigRoot = QTP.root();
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
//		XTCxqueryTuple tuple = pathResult.getPath();
//		QTPNode node = pathResult.getPathLeaf();
//		for (int i = tuple.size() - 1; i >= 0; i--)
//		{
//			data.put(node, tuple.getIDForPos(i));
//			node = node.getParent();
//		}		
//	}
	
//	public void mergeWithLast0(PathResult pathResult)
//	{		
//		ArrayList<PathSynopsisNode> path = pathResult.getPath();
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
//		XTCxqueryTuple tuple = pathResult.getPath();
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
//			try{
//				if (this.data.get(twigNode).compareTo(new XTCdeweyID("33554951:1.3.3.3.5.9")) == 0
//						|| data.get(twigNode).compareTo(new XTCdeweyID("33554951:1.3.3.3.5.9.3")) == 0)
//					{int K = 0;}
//				}catch (XTCexception e) {
//					// TODO: handle exception
//				}
//
//
//			System.out.println(data.get(twigNode));
		}
		
		//XTCxqueryTuple tuple = twigResult.getPath();
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
//			XTCdeweyID tuple1 = data.get(node1Anc.get(i1));
//			XTCdeweyID tuple2 = path.getPath().getIDForPos((node2Anc.size() -1) -i2);
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

	public int equalUpTo(int level, TwigResult res) throws DBException
	{
		if (res.twigLastMergedLeaf != this.twigLastMergedLeaf)
		{
			//throw new XTCexception("Error in TwigStack Merge");
		}

		QTPNode node = twigLastMergedLeaf;
		ArrayList<QTPNode> nodeAnc = new ArrayList<QTPNode>();
		while (node != null)
		{
			nodeAnc.add(node);
			node = node.getParent();
		}
		
		boolean equal = true;
		
		//ArrayList<PathSynopsisNode> pathRes2 = path.getPath();
		for (int i= nodeAnc.size() - 1; level >= 0; --level, --i)
		{
			int c = this.data.get(nodeAnc.get(i)).compareTo(res.data.get(nodeAnc.get(i)));
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

	public String toString()
	{
//		XTCxqueryTuple tuple = new XTCxqueryTuple();
//		for (QTPNode node : QTP.getNodes()) 
//		{
//			XTCdeweyID ID = data.get(node);
//			if (ID != null) tuple.addDeweyID(ID);
//		}
//
//		return tuple.toString();
		QueryTuple tuple = new QueryTuple();
		String res = "<";
		boolean first = true;
		for (QTPNode node : QTP.getNodes()) 
		{			
			DeweyID ID = data.get(node);
			if (ID != null)
			{
				if (!first) res += " ";
				res += node.getName() + ":" + ID.toString();				
			}
			first = false;
		}
		res += ">";
		
		return res;
	}
	
	public QTPNode getTwigLastMergedLeaf()
	{
		return twigLastMergedLeaf;
	}
}

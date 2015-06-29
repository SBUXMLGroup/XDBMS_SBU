
package xmlProcessor.RG.executionPlan;
import java.util.ArrayList;
import java.util.HashMap;
import xmlProcessor.DBServer.DBException;
//import indexManager.StructuralSummaryNode;
import indexManager.StructuralSummaryNode;
import xmlProcessor.DBServer.utils.TwinObject;
import xmlProcessor.QTP.QTPNode;
import xmlProcessor.QTP.QueryTreePattern;

/**
 * @author Kamyar
 *
 */
//XTCxquery-RG-Execution-Plan-Tuple: Tuples used in execution plan !!!
public class RGExecutionPlanTuple 
{
	
	private ArrayList<StructuralSummaryNode> leafNodes;
	private ArrayList<TwinObject<QTPNode, TwinObject<StructuralSummaryNode, ArrayList<StructuralSummaryNode>>>> leafNodesAnc; //leaf nodes and its ancestors
	private ArrayList<StructuralSummaryNode> joinPoints;	
	private ArrayList<StructuralSummaryNode> innerNodes;
	private HashMap<QTPNode, StructuralSummaryNode> tuple;
	private HashMap<QTPNode, ArrayList<StructuralSummaryNode>> leavesAnc;
	private HashMap<QTPNode, ArrayList<Integer>> leavesAncLevels;
	
	/**
	 *  
	 * @param QTP
	 * @param tuple
	 * @throws DBException
	 */
	public RGExecutionPlanTuple(QueryTreePattern QTP,HashMap<QTPNode, StructuralSummaryNode> tuple) throws DBException
	{
		this.tuple = tuple;
		ArrayList<QTPNode> QTPleaves = QTP.getLeaves();

		leafNodes = new ArrayList<StructuralSummaryNode>(QTPleaves.size());
		joinPoints = new ArrayList<StructuralSummaryNode>(QTPleaves.size() - 1);//max possible?	
		leafNodesAnc = new ArrayList<TwinObject<QTPNode,TwinObject<StructuralSummaryNode,ArrayList<StructuralSummaryNode>>>>(QTPleaves.size() - 1);
		leavesAnc = new HashMap<QTPNode, ArrayList<StructuralSummaryNode>>(QTPleaves.size());
		leavesAncLevels = new HashMap<QTPNode, ArrayList<Integer>>();
		
		QTPNode first = QTPleaves.get(0);
		leafNodes.add(tuple.get(first));
		leafNodesAnc.add(new TwinObject<QTPNode, TwinObject<StructuralSummaryNode, ArrayList<StructuralSummaryNode>>>(first, new TwinObject<StructuralSummaryNode,ArrayList<StructuralSummaryNode>>(tuple.get(first),getAnc(QTPleaves.get(0)))));
		
		for (QTPNode second : QTPleaves)
		{
			if (first != second)
			{
				QTPNode join = QTP.findNCA(first, second);
				leafNodes.add(tuple.get(second));
				joinPoints.add(tuple.get(join));
				leafNodesAnc.add(new TwinObject<QTPNode, TwinObject<StructuralSummaryNode, ArrayList<StructuralSummaryNode>>>(second,  new TwinObject<StructuralSummaryNode, ArrayList<StructuralSummaryNode>>(tuple.get(second), getAnc(QTPleaves.get(QTPleaves.indexOf(second))))));
				first = second;
			}
		}		
		
		ArrayList<QTPNode> qNodes = QTP.getInnerNodes();
		innerNodes = new ArrayList<StructuralSummaryNode>(qNodes.size());
		for (int i = 0; i < qNodes.size(); i++)
		{
			innerNodes.add(tuple.get(qNodes.get(i)));
		}

	}
	
	 /**
	 * returns ancestor of a pathsynopsis node that match a leaf node in QTP 
	 * 
	 * CAUTION: any changes in the returned value reflects directly to the other calls, this function only return an existent reference in itself 
	 * @return
	 */	 
	public ArrayList<StructuralSummaryNode> getAnc(QTPNode leafNode)
	{
		ArrayList<StructuralSummaryNode> res;
		
		res = leavesAnc.get(leafNode);
		if (res != null)
		{
			return res;
		}
			
		res = new ArrayList<StructuralSummaryNode>();
		ArrayList<Integer> resLevel = new ArrayList<Integer>();
		QTPNode node = leafNode;
		node = node.getParent();
		while (node != null)
		{		
			StructuralSummaryNode psNode = tuple.get(node); 
			res.add(0, psNode);
			resLevel.add(0, psNode.getLevel());
			node = node.getParent();
		}
		leavesAnc.put(leafNode, res);
		leavesAncLevels.put(leafNode, resLevel);
		return res;
	}

	 /**
	 * returns levels of ancestor of a pathsynopsis node that match a leaf node in QTP 
	 * 
	 * CAUTION: any changes in the returned value reflects directly to the other calls, this function only return an existent reference in itself 
	 * @return
	 */	 
	public ArrayList<Integer> getAncLevels(QTPNode leafNode)
	{
		ArrayList<Integer> res;
		
		res = leavesAncLevels.get(leafNode);
		if (res != null)
		{
			return res;
		}
			
		res = new ArrayList<Integer>();
		ArrayList<StructuralSummaryNode> resNodes = new ArrayList<StructuralSummaryNode>();
		QTPNode node = leafNode;
		node = node.getParent();
		while (node != null)
		{		
			StructuralSummaryNode psNode = tuple.get(node); 
			res.add(0, psNode.getLevel());
			resNodes.add(0, psNode);
			node = node.getParent();
		}
		leavesAnc.put(leafNode, resNodes);
		leavesAncLevels.put(leafNode, res);
		return res;
	}
		
	public ArrayList<StructuralSummaryNode> getleafNodes()
	{
		return leafNodes;
	}
	
	/**
	 * returns nearest common ancestor of each two leaf nodes
	 * (the order of leaf nodes are the same a s the output of getLeafNodesAnc) 
	 * 
	 * CAUTION: any changes in the returned value reflects directly to the other calls, this function only return an existent reference in itself 
	 * @return
	 */	 
	public ArrayList<StructuralSummaryNode> getJoinPoints()
	{
		return joinPoints;//size=QTP nodes-1
	}
	
	public ArrayList<StructuralSummaryNode> getInnerNodes()
	{
		return innerNodes;
	}
	
	/**
	 * returns an ArrayList which each of its elements contains a QTP node and its corresponding
	 * structural summary node beside the ancestors of this structural summary node based on the path of QTP node in the QTP
	 * 
	 *  CAUTION: any changes in the returned value reflects directly to the other calls, this function only return an existent reference in itself
	 * @return
	 */
	public ArrayList<TwinObject<QTPNode, TwinObject<StructuralSummaryNode, ArrayList<StructuralSummaryNode>>>> getLeafNodesAnc()
	{
		return leafNodesAnc;
	}
	
	public StructuralSummaryNode getStructuralSummaryNode(QTPNode qNode)
	{
		return tuple.get(qNode);
	}

	public HashMap<QTPNode, StructuralSummaryNode> getStructuralSummaryNode()
	{
		return tuple;
	}
	
	public boolean isSubsetOf(RGExecutionPlanTuple plan)
	{
		boolean isSubset = true;
		for (QTPNode node : this.tuple.keySet())
		{
			StructuralSummaryNode psNode = plan.tuple.get(node);
			if (psNode != null)
			{
				if (psNode.getCID() != this.tuple.get(node).getCID())
				{
					isSubset = false;
					break;
				}
			}
			else
			{
				isSubset = false;
				break;				
			}
		}
		
//		for (TwinObject<QTPNode, TwinObject<StructuralSummaryNode, ArrayList<StructuralSummaryNode>>> lNode : leafNodesAnc)
//		{
//			int index = plan.leafNodes.indexOf(lNode.getO2().getO1());
//			//if contains the same pathsynopsis node
//			if (index != -1)
//			{				
//				//if nodes don't have same path synopsis nodes as their ancestors
//				if (!lNode.getO2().getO2().equals(plan.leafNodesAnc.get(index).getO2().getO2()))
//				{
//					isSubset = false;
//					break;
//				}
//			}
//			else
//			{
//				isSubset = false;
//				break;
//			}
//		}
		return isSubset;
	}
	
	public String toString()
	{
		String res = "";
		for (QTPNode node : tuple.keySet())
		{
			res = res + "<" + node.toString() + ":" + tuple.get(node).getCID() + ">";
		}
		return res;
	}
}

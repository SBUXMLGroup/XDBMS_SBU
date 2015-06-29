
package xmlProcessor.QTP;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;
import java.util.UUID;

//import xtc.server.DBException;
import xmlProcessor.DBServer.DBException;
import static xmlProcessor.QTP.QTPNode.OpType;
public class QueryTreePattern {

	private QTPNode root;
	private QTPNode extractionPoint = null;
	
	public QueryTreePattern(QTPNode root) throws DBException
	{
		//if (root == null) throw new DBException("Null root is not acceptable.");
		
		//TODO: root as null is accepted to show empty QTP for evaluation graph (not operator)
		// every other methods in this class should be updated to check this case
		
		this.root = root;		
	}
	
	public QTPNode root()
	{
		return root;
	}
	
	//returns leaves of subQTP rooted at root parameter
	public ArrayList<QTPNode> getLeaves(QTPNode root)
	{
		ArrayList<QTPNode> result = new ArrayList<QTPNode>();
		Stack<QTPNode> stack = new Stack<QTPNode>();
		
		stack.push(root);
		while (!stack.empty())
		{
			QTPNode node = stack.pop();
			ArrayList<QTPNode> children = node.getChildren(); 
			if (children.size() == 0)
			{
				result.add(node);
			}
			else
			{
				for (int i = children.size() -1; i >= 0; --i) 
				{
					stack.push(children.get(i));
				}
			}
			
		}
		
		return result;
	}
	
	
	//returns leaves of the QueryTreePattern 
	public ArrayList<QTPNode> getLeaves()
	{
		return getLeaves(this.root);
	}
	
	//returns inner nodes of subQTP rooted at root parameter
	public ArrayList<QTPNode> getInnerNodes(QTPNode root)
	{
		ArrayList<QTPNode> result = new ArrayList<QTPNode>();
		Stack<QTPNode> stack = new Stack<QTPNode>();
		
		stack.push(root);
		while (!stack.empty())
		{
			QTPNode node = stack.pop();
			ArrayList<QTPNode> children = node.getChildren(); 
			if (children.size() > 0)
			{
				result.add(node);
				for (int i = children.size() -1; i >= 0; --i) 
				{
					stack.push(children.get(i));
				}
			}
		}
		
		return result;
	}
	
	//returns nodes of the QueryTreePattern 
	public ArrayList<QTPNode> getNodes()
	{
		return getNodes(this.root);
	}

	//returns nodes of subQTP rooted at root parameter
	//CAUTION: root should be the first node in the result
	public ArrayList<QTPNode> getNodes(QTPNode root)
	{
		ArrayList<QTPNode> result = new ArrayList<QTPNode>();
		Stack<QTPNode> stack = new Stack<QTPNode>();
		
		stack.push(root);
		while (!stack.empty())
		{
			QTPNode node = stack.pop();
			ArrayList<QTPNode> children = node.getChildren();
			
			for (int i = children.size() -1; i >= 0; --i) 
			{
				stack.push(children.get(i));
			}
			
			result.add(node);			
		}
		
		return result;
	}
	
	//returns inner nodes of the QueryTreePattern 
	public ArrayList<QTPNode> getInnerNodes()
	{
		return getInnerNodes(this.root);
	}

	
	public QTPNode findNCA(QTPNode node1, QTPNode node2)
	{
		ArrayList<QTPNode> node1Anc = new ArrayList<QTPNode>();
		while (node1 != null)
		{
			node1Anc.add(node1);
			node1 = node1.getParent();
		}
		
		ArrayList<QTPNode> node2Anc = new ArrayList<QTPNode>();
		while (node2 != null)
		{
			node2Anc.add(node2);
			node2 = node2.getParent();
		}
		//if everything is OK then the rot should be the same so there is no need to compare it 
		int i1 = node1Anc.size() - 2;
		int i2 = node2Anc.size() - 2;
		for (; (i1 >= 0) && (i2 >= 0); --i1, --i2)
		{
			if (!node1Anc.get(i1).equals(node2Anc.get(i2)))
			{
				return node1Anc.get(i1 + 1);
			}
		}
		//this point will be reached if something is wrong
		return null;
		
	}
	
	//especially used by TJFast 
	public ArrayList<QTPNode> directBranchingOrLeafNodes(QTPNode qNode)
	{
		LinkedList<QTPNode> queue = new LinkedList<QTPNode>();
		ArrayList<QTPNode> result = new ArrayList<QTPNode>();
		queue.add(qNode);
		while(!queue.isEmpty())
		{
			QTPNode node = queue.poll();
			if (qNode == node)
			{
				queue.addAll(node.getChildren());
			}
			else
			{
				if (!node.hasChildren()) // leaf node
				{				
					result.add(node);
				}
				else if (node.getChildren().size() > 1) //branching node
				{
					result.add(node);
				}
				else
				{
					queue.addAll(node.getChildren());
				}
			}
		}		
		return result;
	}
	
	//especially used by TJFast
	public ArrayList<QTPNode> branchingNodes()
	{
		return branchingNodes(this.root);
	}
	
	public ArrayList<QTPNode> branchingNodes(QTPNode root)
	{
		LinkedList<QTPNode> queue = new LinkedList<QTPNode>();
		ArrayList<QTPNode> result = new ArrayList<QTPNode>();
		queue.add(root);
		while(!queue.isEmpty())
		{
			QTPNode node = queue.poll();
			queue.addAll(node.getChildren());
			if (node.isBranching()) 
			{
				result.add(node);
			}
		}
		
		return result;
	}
	
	//especially used by TJFast
	public QTPNode topBranchingNode()
	{
		return topBranchingNode(this.root);
	}
	
	public QTPNode topBranchingNode(QTPNode root)
	{
		QTPNode node = root;
		while(true)
		{
			if(!node.hasChildren())
			{
				return null;
			}
			else if (node.isBranching())
			{
				return node;
			}
			else //node has only one child
			{
				node = node.getChildren().get(0);
			}
		}
	}

	public ArrayList<QTPNode> getAncestors(QTPNode node)
	{
		ArrayList<QTPNode> res = new ArrayList<QTPNode>(node.getLevel());
		node = node.getParent();
		while (node != null)
		{
			res.add(0, node);
			node = node.getParent();
		}
		
		return res;
	}
	
	public QTPNode getNodeByUUID(UUID uuid)
	{
		//ArrayList<QTPNode> result = new ArrayList<QTPNode>();
		Stack<QTPNode> stack = new Stack<QTPNode>();
		
		stack.push(root);
		while (!stack.empty())
		{
			QTPNode node = stack.pop();
			
			if (node.getUUID().equals(uuid))
			{
				return node;
			}
			
			ArrayList<QTPNode> children = node.getChildren();
			
			for (int i = children.size() -1; i >= 0; --i) 
			{
				stack.push(children.get(i));
			}
			
			//result.add(node);			
		}
		
		return null;		
	}
	
	public QueryTreePattern clone()
	{
		try
		{
			return new QueryTreePattern(this.root.clone());
		}
		catch (DBException e)
		{
			e.printStackTrace();	
                        System.err.println("Exceotion occured while Clone() in QueryTreePattern");
			return null;
		}
	}

	
	
	public String toString()
	{
		String result = "";
		Stack<QTPNode> stack = new Stack<QTPNode>();
		
		stack.push(root);
		while (!stack.empty())
		{
			QTPNode node = stack.pop();
			ArrayList<QTPNode> children = node.getChildren();
			
			for (int i = children.size() -1; i >= 0; --i) 
			{
				stack.push(children.get(i));
			}
			
			result = result + node.toString()+ ' ';			
		}
		
		return result;
	}

	public void setExtractionPoint(QTPNode extractionPoint)
	{
		this.extractionPoint = extractionPoint;
	}
	
	public QTPNode getExtractionPoint()
	{
		return this.extractionPoint;
	}
	
	public ArrayList<QTPNode> getOutputNodes()
	{
		ArrayList<QTPNode> result = new ArrayList<QTPNode>();
		LinkedList<QTPNode> queue = new LinkedList<QTPNode>();
		
		if (!root.hasNotAxis())
		{
			queue.offer(root);	
		}
		
		while (!queue.isEmpty())
		{
			QTPNode node = queue.poll();
			ArrayList<QTPNode> children = node.getChildren();
			if (node.getOpType() != OpType.OR)
			{
				for (int i = children.size() -1; i >= 0; --i) 
				{
					if (!children.get(i).hasNotAxis())
					{
						queue.offer(children.get(i));	
					}				
				}	
			}
						
			result.add(node);			
		}
		
		return result;		
	}
	
	public boolean hasNOTChild()
	{		
		Stack<QTPNode> stack = new Stack<QTPNode>();
		
		if (!root.hasNotAxis())
		{
			stack.push(root);	
		}
		else
		{
			return true;
		}
		
		while (!stack.empty())
		{
			QTPNode node = stack.pop();
			ArrayList<QTPNode> children = node.getChildren();
			
			for (int i = children.size() -1; i >= 0; --i) 
			{
				if (!children.get(i).hasNotAxis())
				{
					stack.push(children.get(i));	
				}		
				else
				{
					return true;
				}
			}			
		}
		
		return false;		
	}
	
	public boolean isANDQTP()
	{		
		Stack<QTPNode> stack = new Stack<QTPNode>();
		
		if (!root.hasNotAxis())
		{
			stack.push(root);	
		}
		else
		{
			return false;
		}
		
		while (!stack.empty())
		{
			QTPNode node = stack.pop();
			
			if (node.getOpType() == OpType.AND)
			{
				ArrayList<QTPNode> children = node.getChildren();
				for (int i = children.size() -1; i >= 0; --i) 
				{
					if (!children.get(i).hasNotAxis())
					{
						stack.push(children.get(i));	
					}		
					else
					{
						return false;
					}
				}							
			}
			else
			{
				return false;
			}
		}
		
		return true;		
	}
}

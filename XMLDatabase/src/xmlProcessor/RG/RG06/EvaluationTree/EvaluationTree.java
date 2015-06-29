/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xmlProcessor.RG.RG06.EvaluationTree;

import java.util.ArrayList;
import java.util.Set;
import java.util.Stack;

import xmlProcessor.DBServer.DBException;
import indexManager.StructuralSummaryNode;
import java.io.IOException;
import xmlProcessor.DeweyID;
import xmlProcessor.DBServer.utils.TwinArrayList;
import xmlProcessor.DBServer.utils.TwinObject;
import xmlProcessor.DBServer.operators.twigs.QueryStatistics;
import xmlProcessor.QTP.QTPNode;
import xmlProcessor.RG.RG06.TwigResult;

/**
 * @author Kamyar
 */
public class EvaluationTree implements QueryStatistics
{
	private EvaluationTreeNodeInput root;
	private ArrayList<EvaluationTreeNodeInput> nodes;
	private TwinArrayList<QTPNode, ArrayList<TwinObject<QTPNode, Set<StructuralSummaryNode>>>> comps;
	private TwigResult head;
	private boolean isFinished;
	
	public EvaluationTree(EvaluationTreeNodeInput root)
	{
		this.root = root;
		this.nodes = null;
		this.comps = new TwinArrayList<QTPNode, ArrayList<TwinObject<QTPNode,Set<StructuralSummaryNode>>>>();		
		this.isFinished = false;
		this.head = null;
	}

	public void open() throws DBException, IOException
	{
		this.root.open();
		
		if (this.root.isFinished())
		{
			this.head = null;
			this.isFinished = true;
		}
		else
		{
			this.head = this.root.getHead();
		}
		this.nodes = this.calcNodes();
	}
	
	public void next() throws DBException, IOException
	{
		this.root.next();
		this.head = this.root.getHead();
		if (this.head == null)
		{
			this.isFinished = true;
		}
	}
	
	public TwigResult getHead()
	{
		return this.head;
	}
	
	public boolean isFinished()
	{
		return isFinished;
	}
	
	public void close() throws DBException
	{
		//System.out.println(this.getClass().getName() + "::close is not implemented");
		this.root.close();
	}
	
	private ArrayList<EvaluationTreeNodeInput> calcNodes()
	{
		ArrayList<EvaluationTreeNodeInput> result = new ArrayList<EvaluationTreeNodeInput>();
		Stack<EvaluationTreeNodeInput> stack = new Stack<EvaluationTreeNodeInput>();
		
		stack.push(root);
		while (!stack.empty())
		{
			EvaluationTreeNodeInput node = stack.pop();
			if (node instanceof SelfInput)
			{
				continue;
			}
			ArrayList<EvaluationTreeNodeInput> children = node.getChildren();
			
			for (int i = children.size() -1; i >= 0; --i) 
			{
				stack.push(children.get(i));
			}
			
			ArrayList<EvaluationTreeNodeInput> notChildren = node.getNOTChildren();
			
			for (int i = notChildren.size() -1; i >= 0; --i) 
			{
				stack.push(notChildren.get(i));
			}
			
			result.add(node);	
			this.comps.add(node.getMainNode(), node.getComps());
		}
		
		return result;
	}
	
	public long getIOTime()
	{
		return this.root.getIOTime();
	}
	
	public long getNumberOfReadElements()
	{
		return this.root.getNumberOfReadElements();
	}
//	/***
//	 * complete res and return it
//	 * @param res
//	 * @return return completed result
//	 */
//	public TwigResult completeRes(TwigResult res)
//	{
//		for (int i = 0; i < this.comps.size(); i++)
//		{
//			QTPNode node = this.comps.getL1(i);
//			DeweyID id = res.getRes(node);
//			if (id != null)
//			{
//				ArrayList<TwinObject<QTPNode, Integer>> comp = this.comps.getL2(i);
//				for (int k = 1; k < comp.size(); k++)
//				{
//					TwinObject<QTPNode, Integer> c =  comp.get(k);
//					res.add(c.getO1(), id.getAncestorDeweyID(c.getO2()));				
//				}				
//			}
//		}
//		return res;
//	}

}

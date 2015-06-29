/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xmlProcessor.RG.RG05;


import java.util.ArrayList;
import java.util.Stack;

import xmlProcessor.DBServer.DBException;
import xmlProcessor.DBServer.utils.TwinObject;
import xmlProcessor.QTP.QTPNode;
import xmlProcessor.QTP.QueryTreePattern;
import xmlProcessor.QTP.QTPNode.OpType;

public class QTPParser
{
	private QueryTreePattern OriginalQTP;
	private QueryTreePattern NQTP = null; //Normalized QTP
	
//	public  QTPParser(QueryTreePattern QTP)
//	{
//		this.OriginalQTP = QTP;
//	}
	
//	public ArrayList<QueryTreePattern> Parse() throws DBException
//	{
//		
//		return Parse(OriginalQTP.root());
//	}

	static public ArrayList<QueryTreePattern> Parse(QueryTreePattern QTP, QTPNode root) throws DBException
	{	
		ArrayList<QueryTreePattern> result = new ArrayList<QueryTreePattern>();
		
		ArrayList<TwinObject<QTPNode, ArrayList<QueryTreePattern>>> childrenParsedQTPs = new ArrayList<TwinObject<QTPNode,ArrayList<QueryTreePattern>>>();			
		
		ArrayList<QTPNode> children = root.getChildren();
		
		if (!root.hasChildren())
		{
//			QTPNode clNode = root.cloneOnlyNode();
//			result.add(new QueryTreePattern(clNode));
//			return result;
			result.add(QTP);
			return result;
		}
		/*
		 * 1: trimmed <- Remove children of Or root
		 * 2: parse each children
		 * 3: for each combination of trees in steps 2 and 3 join them in 3 states regards to Or operator  
		 */
		
		//QueryTreePattern trimmed = this.OriginalQTP.clone();
		QueryTreePattern trimmedQTP = QTP.clone();
		QTPNode opNode = trimmedQTP.getNodeByUUID(root.getUUID());
		
		//1: trimmed <- Remove children of Or root
		opNode.removeChildren();

		if (children.size() == 1 /*&& root.getChildren().get(0).getChildren().size() == 1*/) //No Operation
		{
			if (children.get(0).hasNotAxis())
			{
				result.add(trimmedQTP.clone());
			}
			QueryTreePattern cQTP = new QueryTreePattern(QTP.root().getChildren().get(0));
			ArrayList<QueryTreePattern> parsedBranches = Parse(cQTP, root.getChildren().get(0));
			for (QueryTreePattern parsedBranch : parsedBranches)
			{
				QueryTreePattern parsedQTP = trimmedQTP.clone();
				opNode = parsedQTP.getNodeByUUID(root.getUUID());
				opNode.addChild(parsedBranch.root());
				result.add(parsedQTP);
			}
			return result;

		} 
		
		//2: parse each children		
		for (QTPNode cNode : root.getChildren())
		{
			QueryTreePattern cQTP = new QueryTreePattern(cNode);
			ArrayList<QueryTreePattern> parsedBranches = Parse(cQTP, cNode);
			childrenParsedQTPs.add(new TwinObject<QTPNode, ArrayList<QueryTreePattern>>(cNode, parsedBranches));
		}

//		//3: parse trimmed
//		QTPParser trimmedParser = new QTPParser(trimmed);
//		ArrayList<QueryTreePattern> trimmedParsedQTPs = trimmedParser.Parse();


//		if (children.size() == 1) //NO Operation
//		{
//			for (QueryTreePattern trimmedQTP : trimmedParsedQTPs)
//			{
//				//add parsed trees of the single child 
//				for (QueryTreePattern cQTP : childrenParsedTrees.get(0).getO2())
//				{
//					QueryTreePattern parsedQTP = trimmedQTP.clone();
//					opNode = parsedQTP.getNodeByUUID(root.getUUID());
//					opNode.addChild(cQTP.root());
//					result.add(parsedQTP);
//				}
//			}			
//		}
//		else if (root.getOpType() == OpType.AND)
		if (root.getOpType() == OpType.AND)
		{			
//			//Create parsed trees for each child 
//			for (QTPNode cNode : children)
//			{
//				QueryTreePattern cQTP = new QueryTreePattern(cNode);
//				ArrayList<QueryTreePattern> parsedBranches = Parse(cQTP, cNode);
//				childrenParsedTrees.add(new TwinObject<QTPNode, ArrayList<QueryTreePattern>>(cNode, parsedBranches));
//			}
			//Produce any possible combination of trees using parsed branches
			int[] combinationFlag = new int[children.size()];
			for (int i = 0; i < combinationFlag.length; i++)
			{
				combinationFlag[i] = 0;
			}
			int branch = 0;
			boolean finished = false;
			//while (branch < children.size() && combinationFlag[children.size() - 1] < childrenParsedQTPs.get(children.size() - 1).getO2().size())
			while (!finished)
			{
				QueryTreePattern parsedQTP = trimmedQTP.clone();
				opNode = parsedQTP.getNodeByUUID(root.getUUID());
				for (int i = 0; i < combinationFlag.length; i++)
				{	
					if (combinationFlag[i] < childrenParsedQTPs.get(i).getO2().size())
					{
						opNode.addChild(childrenParsedQTPs.get(i).getO2().get(combinationFlag[i]).root());
					}
				}
				result.add(parsedQTP);
				branch = 0;
				combinationFlag[branch]++;
				boolean cnt = true;
				while (cnt)
				{
					if (
							(combinationFlag[branch] == childrenParsedQTPs.get(branch).getO2().size() && !childrenParsedQTPs.get(branch).getO1().hasNotAxis())
							||
							(combinationFlag[branch] > childrenParsedQTPs.get(branch).getO2().size()) //the last number used for not participating branch for NOT operator
						)
					{
						combinationFlag[branch] = 0;
						branch++;
						if (branch < combinationFlag.length)
						{							
							combinationFlag[branch]++;
							if (
									(combinationFlag[branch] == childrenParsedQTPs.get(branch).getO2().size() && !childrenParsedQTPs.get(branch).getO1().hasNotAxis())
									||
									(combinationFlag[branch] > childrenParsedQTPs.get(branch).getO2().size()) //the last number used for not participating branch for NOT operator
								)
							{
								cnt = true;
							}
							else
							{
								cnt = false;
							}
						}
						else
						{
							finished = true;
							cnt = false;
						}
					}
					else
					{
						cnt = false;
					}
				}
			}
		}
		else if (root.getOpType() == OpType.OR)
		{
			
			int[] combinationFlag = new int[children.size()];
			boolean allOrdinary = true;
			for (int i = 0; i < combinationFlag.length; i++)
			{
				combinationFlag[i] = 0;
				allOrdinary = allOrdinary && !childrenParsedQTPs.get(i).getO1().hasNotAxis();
			}
			int branch = 0;
			boolean finished = false;
			//while (branch < children.size() && combinationFlag[children.size() - 1] < childrenParsedQTPs.get(children.size() - 1).getO2().size())
			while (!finished)
			{
				QueryTreePattern parsedQTP = trimmedQTP.clone();
				opNode = parsedQTP.getNodeByUUID(root.getUUID());
				for (int i = 0; i < combinationFlag.length; i++)
				{	
					if (combinationFlag[i] < childrenParsedQTPs.get(i).getO2().size())
					{
						opNode.addChild(childrenParsedQTPs.get(i).getO2().get(combinationFlag[i]).root());
					}
				}
				result.add(parsedQTP);
				branch = 0;
				combinationFlag[branch]++;
				boolean cnt = true;
				while (cnt)
				{
					if (
							(combinationFlag[branch] > childrenParsedQTPs.get(branch).getO2().size()) //the last number used for not participating branch for NOT operator
						)
					{
						combinationFlag[branch] = 0;
						branch++;
						if (branch < combinationFlag.length)
						{							
							combinationFlag[branch]++;
							if (
									(combinationFlag[branch] > childrenParsedQTPs.get(branch).getO2().size()) //the last number used for not participating branch for NOT operator
								)
							{
								cnt = true;
							}
							else
							{
								cnt = false;
							}
						}
						else
						{
							finished = true;
							if (allOrdinary)
							{
								result.remove(result.size() - 1); //the last result has no child it is for OR nodes which all of its nodes are ordinary nodes
							}
							cnt = false;
						}
					}
					else
					{
						cnt = false;
					}
				}
			}
			
//			//Or nodes with two children is supported now!
//			//* 4: for each combination of trees in steps 2 and 3 join them in 3 states regards to Or operator
//			if (root.getChildren().size() > 2)
//			{
//				throw new DBException("Could not parse OR node with more than two childs");
//			}
//
//			//add only parsed trees of left branch 
//			for (QueryTreePattern lQTP : childrenParsedQTPs.get(0).getO2())
//			{
//				QueryTreePattern parsedQTP = trimmedQTP.clone();
//				opNode = parsedQTP.getNodeByUUID(root.getUUID());
//				opNode.addChild(lQTP.root());
//				result.add(parsedQTP);
//			}
//			//add only parsed trees of right branch
//			for (QueryTreePattern rQTP : childrenParsedQTPs.get(1).getO2())
//			{
//				QueryTreePattern parsedQTP = trimmedQTP.clone();
//				opNode = parsedQTP.getNodeByUUID(root.getUUID());
//				opNode.addChild(rQTP.root());
//				result.add(parsedQTP);
//			}
//			//add any combination of both branches to trimmed
//			for (QueryTreePattern lQTP : childrenParsedQTPs.get(0).getO2())
//			{
//				for (QueryTreePattern rQTP : childrenParsedQTPs.get(1).getO2())
//				{
//					QueryTreePattern parsedQTP = trimmedQTP.clone();
//					opNode = parsedQTP.getNodeByUUID(root.getUUID());
//					opNode.addChild(lQTP.root());
//					opNode.addChild(rQTP.root());
//					result.add(parsedQTP);					
//				}
//			}
			
			
		}
 
		return result;
	}

//	public void Normalize() throws DBException
//	{
//		QTPNode nRoot, nNode, cNode;
//		
////		ArrayList<QTPNode> result = new ArrayList<QTPNode>();
//		Stack<TwinObject<QTPNode, QTPNode>> stack = new Stack<TwinObject<QTPNode,QTPNode>>();
//		
//		nRoot = OriginalQTP.root().cloneOnlyNode();
//		stack.push(new TwinObject<QTPNode, QTPNode>(OriginalQTP.root(), nRoot));
//		
//		nNode = nRoot;
//		while (!stack.empty())
//		{
//			TwinObject<QTPNode, QTPNode> node = stack.pop();
//			
//			ArrayList<QTPNode> children = node.getO1().getChildren();
//			
//			for (int i = children.size() -1; i >= 0; --i) 
//			{
//				if 
//				cNode = children.get(i).cloneOnlyNode();
//				node.getO2().addChild(cNode);
//				
//				stack.push(new TwinObject<QTPNode, QTPNode>(children.get(i), cNode));
//			}
//			
////			result.add(node);			
//		}
//		
//		NQTP = new QueryTreePattern(nRoot);
//
//	}
	
	
}

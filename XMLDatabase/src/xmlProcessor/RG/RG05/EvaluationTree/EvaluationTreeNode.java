/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xmlProcessor.RG.RG05.EvaluationTree;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;

import xmlProcessor.DBServer.DBException;
import xmlProcessor.DeweyID;
import xmlProcessor.DBServer.utils.TwinArrayList;
import xmlProcessor.DBServer.utils.TwinObject;
import xmlProcessor.QTP.QTPNode;
import xmlProcessor.QTP.QTPNode.OpType;
import xmlProcessor.RG.RG05.InputWrapper;
import xmlProcessor.RG.RG05.TwigResult;

/**
 * 
 * @author Izadi
 *
 */
public class EvaluationTreeNode implements EvaluationTreeNodeInput
{

	private ArrayList<TwinObject<QTPNode, Integer>> qNodes; //nodes and their level
	private QTPNode mainNode; //main node which qNodes produce their corresponding results based on it 
//	private EvaluationTreeNode parent;	
	private TwinArrayList<EvaluationTreeNodeInput, ArrayList<TwigResult>> children; //children with  ordinary connection with their subSet 
	private TwinArrayList<EvaluationTreeNodeInput, ArrayList<TwigResult>> NOTChildren; //children with  NOT operator connection with their subSet
	private TwinArrayList<EvaluationTreeNodeInput, ArrayList<TwigResult>> AllChildren; //children with  NOT operator connection with their subSet
	private boolean hasChild;
	private InputWrapper selfInput;
	private QTPNode.OpType opType;
	//private int joinLevel;
	private TwigResult head;
	private boolean finished;
	private int NOTChildrenNo;
	//private ArrayList<TwigResult> subSet;
	//private HashMap<EvaluationTreeNodeInput, ArrayList<TwigResult>> subSets;
	private Queue<TwigResult> outputQueue; 
	private boolean checkForNot; // it will be false when one of not branches which has data gets empty so every result is OK
	private boolean isLeaf;
	private long IOTime = 0;
	private long numberOfReadElements = 0;
	private boolean uppestNOTPoint;
	
	
	
	public EvaluationTreeNode(ArrayList<TwinObject<QTPNode, Integer>> qNodes, QTPNode mainNode)
	{
		this.qNodes = qNodes;
		this.mainNode = mainNode;
//		this.parent = null;
		this.children = new TwinArrayList<EvaluationTreeNodeInput, ArrayList<TwigResult>>();
		this.NOTChildren = new TwinArrayList<EvaluationTreeNodeInput, ArrayList<TwigResult>>();
		this.AllChildren = new TwinArrayList<EvaluationTreeNodeInput, ArrayList<TwigResult>>();
		this.hasChild = false;
		this.selfInput = null;
		this.opType = mainNode.getOpType();
		//this.joinLevel = -1;
		this.head = null;
		this.finished = false;
		//this.subSet = new ArrayList<TwigResult>();
		//this.subSets = new HashMap<EvaluationTreeNodeInput, ArrayList<TwigResult>>();
		this.outputQueue = new LinkedList<TwigResult>();
		this.checkForNot = true;
		this.isLeaf = false;
		this.uppestNOTPoint = false;
	}
	
	public void setAsuppestNOTPoint()
	{
		this.uppestNOTPoint = true;
	}
	public void addChild(EvaluationTreeNodeInput child, boolean NOTConnection)
	{
		ArrayList<TwigResult> subSet = new ArrayList<TwigResult>();
		if (NOTConnection)
		{
			this.NOTChildren.add(child, subSet);			
		}
		else
		{
			this.children.add(child, subSet);
		}
		this.AllChildren.add(child, subSet);
		//this.subSets.put(child, new ArrayList<TwigResult>());
		
		//child.setParent(this);
		this.hasChild = true;
	}
	
//	public void setParent(EvaluationTreeNode parent)
//	{
//		this.parent = parent;
//	}
	
//	public void setOpType(QTPNode.OpType opType)
//	{
//		this.opType = opType;
//	}
	
//	public void setSelfInput(InputWrapper selfInput)
//	{
//		if (selfInput.isFinished())
//		{
//			this.selfInput = null;
//			return;
//		}
//		this.selfInput = new SelfInput(selfInput);
//		this.addChild(this.selfInput, false);
//	}
	
//	public void setJoinLevel(int joinLevel)
//	{
//		this.joinLevel = joinLevel;
//	}
	
	public void open() throws DBException, IOException
	{
		if (children.size() == 1 && NOTChildren.size() == 0 && children.getL1(0) instanceof SelfInput)
		{
			this.isLeaf = true;
			this.selfInput = ((SelfInput)children.getL1(0)).getInputWrapper();
			this.selfInput.open();
			if (this.selfInput.isFinished())
			{
				this.head = null;
				this.finished = true;
			}
			else
			{
				TwigResult res;
				res = this.selfInput.getHead();

				this.head = this.completeToUpperJoinPoint(res);
                                logManager.LogManager.log(6,"Completed Head: "+head.toString());

			}
			return;
		}
		
		for (EvaluationTreeNodeInput child : children.getL1())
		{
			child.open();
		}
		for (EvaluationTreeNodeInput NOTChild : NOTChildren.getL1())
		{
			NOTChild.open();
		}
		this.NOTChildrenNo = this.NOTChildren.size();
//		if (this.selfInput != null)
//		{
//			this.selfInput.open();
//			//this.selfInput.next();
//		}
		this.next();
		if (this.head == null)
		{
			this.finished = true;
		}
		
	}
	
	public TwigResult getHead()
	{
		return this.head;
	}
	
	public void next() throws DBException, IOException
	{
		if(isLeaf)
		{
			this.selfInput.next();
			if (this.selfInput.isFinished())
			{
				this.head = null;
				this.finished = true;
			}
			else
			{
				TwigResult res;
				res = this.selfInput.getHead();

				this.head = this.completeToUpperJoinPoint(res);
                                logManager.LogManager.log(6,"Completed Head: "+head.toString());
			}
			return;
		}
		if (!this.outputQueue.isEmpty())
		{
			this.head = this.outputQueue.poll();
                       // logManager.LogManager.log(6,"Returning1: Head is: "+this.head.toString());
//			System.out.println("Returning1: Head is: "+this.head.toString());
                        return;
		}
			
		if (! hasChild)
		{	
			if (this.selfInput == null)
			{
				this.head = null;
				this.finished = true;
				return;	
			}
			TwigResult res;
			res = this.selfInput.getHead();
			this.selfInput.next();
			if (res == null)
			{
				this.head = null;
				this.finished = true;
				return;
			}
			else
			{
				this.head = this.completeToUpperJoinPoint(res);
                                logManager.LogManager.log(6,"Completed Head: "+head.toString());

				return;
			}
		}
		
		if (this.opType == QTPNode.OpType.AND)
		{
			do
			{
				this.processAND();	
			}
			while (this.outputQueue.isEmpty() && !isFinished());
		} // if (this.opType == QTPNode.OpType.AND)
		else if (this.opType == QTPNode.OpType.OR)
		{
			do
			{
				this.processOR();	
			}
			while (this.outputQueue.isEmpty() && !isFinished());
		} // if (this.opType == QTPNode.OpType.OR)
		
		if (!this.outputQueue.isEmpty())
		{
			this.head = this.outputQueue.poll();
                       // logManager.LogManager.log(6,"Returning2: Head is: "+this.head.toString());
                       //System.out.println("Returning2: Head is: "+this.head.toString());
			return;
		}
		else
		{			
			if (!isFinished())
			{
				next();
			}
		}
	}
	
	private void processAND() throws DBException, IOException
	{		
		//All ordinary children should have something to do further processing
		for (int i = 0; i < this.children.size(); ++i)
		{
			if (this.children.getL1(i).isFinished())
			{
				this.head = null;
				this.finished = true;
				return;
			}
//			else
//			{
//				this.advance(this.children.getL1(i), this.children.getL2(i));
//			}										 
		}
		// Not children is not necessary to have data
		for (int i =0; i < NOTChildren.size(); )
		{
			if (!NOTChildren.getL1(i).isFinished())
			{
//				this.advance(NOTChildren.getL1(i), NOTChildren.getL2(i));
				++i;
			}
			else
			{
				this.IOTime += NOTChildren.getL1(i).getIOTime();
				this.numberOfReadElements += NOTChildren.getL1(i).getNumberOfReadElements(); 
				this.AllChildren.remove(this.AllChildren.indexOfL1(NOTChildren.getL1(i)));
				this.NOTChildren.remove(i);
			}
		}

		//balance all child input regards to JoinLevel 
		boolean bl = andBalance(this.children);
		if (!bl)
		{
			this.head = null;
			this.finished = true;
			return;
		}
		for (int i = 0; i < this.children.size(); i++)
		{
			this.advance(this.children.getL1(i), this.children.getL2(i));
		}
		boolean setAsDummy = false;
		for (int i = 0; i < this.children.size() && !setAsDummy; i++)
		{
			for (int j = 0; j < this.children.getL2(i).size() && !setAsDummy; j++)
			{
				if (this.children.getL2(i).get(j).isDummy())
				{
					setAsDummy = true;
				}
			}
		}
		
		ArrayList<TwigResult> andJoinResult;
		andJoinResult = xProdcut(this.children);

		boolean excluded = false;
		if (NOTChildren.size() > 0)
		{
			excluded = this.andExclude(andJoinResult.get(0));
		}
		
		if (!excluded)
		{
			if (!setAsDummy)
			{
				for (int i = 0; i < andJoinResult.size(); i++)
				{
					this.outputQueue.offer(this.completeToUpperJoinPoint(andJoinResult.get(i)));
				}
			}
			else
			{
				for (int i = 0; i < andJoinResult.size(); i++)
				{
					TwigResult dummyRes;
					dummyRes = this.completeToUpperJoinPoint(andJoinResult.get(i));
					dummyRes.setAsDummy();
					this.outputQueue.offer(dummyRes);
				}
			}
		}
               
		else //if(uppestNOTPoint)
		{
			for (int i = 0; i < andJoinResult.size(); i++)
			{
				TwigResult dummyRes;
				dummyRes = this.completeToUpperJoinPoint(andJoinResult.get(i));
				dummyRes.setAsDummy();
				this.outputQueue.offer(dummyRes);
			}			
		}
              
//		if (children.size() > 0)
//		{
//			//balance all child input regards to JoinLevel 
//			boolean bl = andBalance(this.children);
//			if (!bl)
//			{
//				this.head = null;
//				this.finished = true;
//				return;
//			}
//					
//			
//			ArrayList<TwigResult> andJoinResult;
//			andJoinResult = xProdcut(this.children);
//
//			
//			boolean excluded = this.andExclude(andJoinResult.get(0));
//			
//			if (excluded)
//			{
//				processAND();
//			}
//
//			for (int i = 0; i < andJoinResult.size(); i++)
//			{
//				this.outputQueue.offer(this.completeToUpperJoinPoint(andJoinResult.get(i)));
//			}
//
//		}
//		else //if (children.size() == 0)  //hasChild is true so it means that  we have only NOTChildrens and so selfInput
//		{
//			ArrayList<TwigResult> selfInputSubSet;
//
//			this.selfInput.advance();
//			selfInputSubSet = this.selfInput.getSubSet();
//			if (selfInputSubSet.isEmpty())
//			{
//				this.head = null;
//				this.finished = true;
//				return;
//			}
//
//			boolean excluded = this.andExclude(selfInputSubSet.get(0));
//			
//			if (excluded)
//			{
//				processAND();
//			}
//			
//			for (int i = 0; i < selfInputSubSet.size(); i++)
//			{
//				this.outputQueue.offer(this.completeToUpperJoinPoint(selfInputSubSet.get(i)));
//			}
//		}// if (children.size() == 0)

				
		return;
		
	} //processAND
	
	private boolean andBalance(TwinArrayList<EvaluationTreeNodeInput, ArrayList<TwigResult>> inputs) throws DBException, IOException
	{
		for (int i = 0; i < inputs.size() - 1; )
		{
			int c;
			c = inputs.getL1(i).getHead().getRes(mainNode).compareTo(inputs.getL1(i+1).getHead().getRes(mainNode));
			//c = inputs.getL2(i).get(0).getRes(mainNode).compareTo(joinLevel, inputs.getL2(i+1).get(0).getRes(mainNode));
			if (c > 0)
			{
				this.advance(inputs.getL1(i+1), inputs.getL2(i+1)); //advance to skip unusable data
				if (inputs.getL1(i+1).isFinished())
				{
					return false;
				}
			}
			else if (c < 0)
			{
				for (int j = 0; j <= i; j++)
				{
					this.advance(inputs.getL1(j), inputs.getL2(j)); //advance to skip unusable data
					if (inputs.getL1(j).isFinished())
					{
						return false;
					}
				}				
				//All left children should be advanced so the process should be start from beginning
				i = 0;
			}
			else //c ==0
			{
				//all till now are the same next input should be checked
				i++;
			}
		}
//		for (int i = 0; i < inputs.size(); i++)
//		{
//			this.advance(inputs.getL1(i), inputs.getL2(i));
//		}
		return true; //all inputs have balanced data 
	}
	
	private void processOR() throws DBException, IOException
	{
		//Not all ordinary children should have something to do further processing
		for (int i =0; i < children.size(); )
		{
			if (children.getL1(i).isFinished())
			{
				this.IOTime += children.getL1(i).getIOTime();
				this.numberOfReadElements += children.getL1(i).getNumberOfReadElements(); 

				this.AllChildren.remove(this.AllChildren.indexOfL1(children.getL1(i)));
				this.children.remove(i);
				
			}
			else
			{
				++i;
			}										 
		}
		if (children.size() == 0 && this.selfInput == null)
		{
			this.head = null;
			this.finished = true;
			return;
		}

		// Not children is not necessary to have data
		for (int i =0; i < NOTChildren.size(); )
		{
			if (!NOTChildren.getL1(i).isFinished())
			{
				++i;
			}
			else
			{				
				this.checkForNot = false;
				this.IOTime += NOTChildren.getL1(i).getIOTime();
				this.numberOfReadElements += NOTChildren.getL1(i).getNumberOfReadElements(); 

				this.AllChildren.remove(this.AllChildren.indexOfL1(NOTChildren.getL1(i)));
				this.NOTChildren.remove(i);
			}
		}

		
		TwinArrayList<EvaluationTreeNodeInput, ArrayList<TwigResult>> selected = new TwinArrayList<EvaluationTreeNodeInput, ArrayList<TwigResult>>();
		selected = orBalance(this.AllChildren, selected);
		
		for (int i = 0; i < selected.size(); i++)
		{
			this.advance(selected.getL1(i), selected.getL2(i));
		}
		
		ArrayList<TwigResult> orJoinResult;
		orJoinResult = xProdcut(selected);
		
		//FIXME: If only all branches are dummy then the result is dummy too
		if (checkForNot)
		{
			for (int i = 0; i < orJoinResult.size(); )
			{
				boolean AllNegative = true;
				for (int j = 0; j < this.children.size(); ++j)
				{
					QTPNode node;
					node = this.children.getL1(j).getMainNode();
					if (node != mainNode && orJoinResult.get(i).getRes(node) != null) //SelfInput have not to be checked
					{
						AllNegative = false;
						break;
					}
				}

				if (AllNegative)
				{
					for (int j = 0; j < this.NOTChildren.size(); ++j)
					{
						QTPNode node;
						node = this.NOTChildren.getL1(j).getMainNode();
						if (orJoinResult.get(i).getRes(node) == null)
						{
							AllNegative = false;
							break;
						}
					}
				}
				if (AllNegative)
				{
					if (uppestNOTPoint)
					{
						orJoinResult.get(i).setAsDummy();
						i++;
					}
					else
					{
						orJoinResult.remove(i);
					}
				}
				else
				{					
					i++;
				}
			}
		}
		for (int i = 0; i < orJoinResult.size(); i++)
		{
			this.outputQueue.offer(this.completeToUpperJoinPoint(orJoinResult.get(i)));
		}
		
//		if (this.children.size()>1)
//		{
//			//Process OR operator with equal or more than two children				
//			selected = orBalance(this.children, selected);
//			ArrayList<TwigResult> orJoinResult;
//			orJoinResult = xProdcut(selected);
//			
//			selected.clear();
//			boolean excluded = this.orExclude(orJoinResult.get(0), selected);
//			
//			if (excluded)
//			{
//				processOR();
//			}
//			else
//			{
//				orJoinResult = xProdcut(orJoinResult, selected);
//			}
//
//
//			for (int i = 0; i < orJoinResult.size(); i++)
//			{
//				this.outputQueue.offer(this.completeToUpperJoinPoint(orJoinResult.get(i)));
//			}
//
//		}
//		else if (this.children.size()==1)
//		{
//			ArrayList<TwigResult> res = new ArrayList<TwigResult>();
//			this.advance(this.children.getL1(0), this.children.getL2(0));
//			
//			selected.clear();
//			boolean excluded = this.orExclude(this.children.getL2(0).get(0), selected);
//			
//			if (excluded)
//			{
//				processOR();
//			}
//			else
//			{
//				res = xProdcut(this.children.getL2(0), selected);
//			}
//
//
//			for (int i = 0; i < res.size(); i++)
//			{
//				this.outputQueue.offer(this.completeToUpperJoinPoint(res.get(i)));
//			}
//
//		}
//		else //if (children.size() == 0)  //hasChild is true so it means that  we have only NOTChildrens 
//		{
//			ArrayList<TwigResult> selfInputSubSet;
//
//			this.selfInput.advance();
//			selfInputSubSet = this.selfInput.getSubSet();
//			if (selfInputSubSet.isEmpty())
//			{
//				this.head = null;
//				this.finished = true;
//				return;
//			}
//
//			selected.clear();
//			boolean excluded = this.orExclude(selfInputSubSet.get(0), selected);
//			
//			if (excluded)
//			{
//				processOR();
//			}
//			else
//			{
//				selfInputSubSet = xProdcut(selfInputSubSet, selected);
//			}
//
//
//			for (int i = 0; i < selfInputSubSet.size(); i++)
//			{
//				this.outputQueue.offer(this.completeToUpperJoinPoint(selfInputSubSet.get(i)));
//			}
//		}
		
		return;

	} //processOR
	
	private TwinArrayList<EvaluationTreeNodeInput, ArrayList<TwigResult>> orBalance(TwinArrayList<EvaluationTreeNodeInput, ArrayList<TwigResult>> inputs, TwinArrayList<EvaluationTreeNodeInput, ArrayList<TwigResult>> selected) throws DBException
	{
		//Sort children for OR operator			
		inputs.sortBasedOnL1(new ChildrenInputComparator(this.mainNode));
		
		int c = -1;
		for (int i = 0; i < inputs.size() - 1; )
		{
			c = inputs.getL1(i).getHead().getRes(mainNode).compareTo(inputs.getL1(i+1).getHead().getRes(mainNode));
			if (c < 0)
			{					
				//this.advance(inputs.getL1(i), inputs.getL2(i));
				selected.add(inputs.getL1(i), inputs.getL2(i));
				break; //break to produce result for these selected inputs 
			}
			else if (c > 0)
			{
				throw new DBException(this.getClass().getName() + ":orBalance children are not sorted!");
			}
			else //c ==0
			{
				//all till now are the same next input should be checked					
				//this.advance(inputs.getL1(i), inputs.getL2(i));
				selected.add(inputs.getL1(i), inputs.getL2(i));					
				i++;
			}
		}

		if (c ==0 || inputs.size() == 1)
		{
			int last =inputs.size() -1;
			//this.advance(inputs.getL1(last), inputs.getL2(last));
			selected.add(inputs.getL1(last), inputs.getL2(last));
		}
		return selected;
	}
	
	public boolean isFinished()
	{
		return this.finished;
	}

//	public void advance(int level) 
//	{
////		if (inputConsumed) { 
////			consumed = true;
////			return;
////		}
//
//		TwigResult key = this.head;
//		subSet.clear();
//
//		while(!inputConsumed && (key.compareUpTo(level, nextTuple) == 0))
//		{
//			subSet.add(nextTuple);
//			inputNext();
//		}
//	}
	
	private ArrayList<TwigResult> xProdcut(TwinArrayList<EvaluationTreeNodeInput, ArrayList<TwigResult>> inputs)
	{
		ArrayList<TwigResult> result = new ArrayList<TwigResult>();
		
		if (inputs == null || inputs.size() == 0)
		{
			return result;
		}
		
		for (int i = 0; i < inputs.getL2(0).size(); i++)
		{
			result.add(inputs.getL2(0).get(i));
		}
		TwigResult res = null;
		for (int i = 1; i < inputs.size(); i++)
		{
			ArrayList<TwigResult> newRes = new ArrayList<TwigResult>();
			for (int j = 0; j < result.size(); j++)
			{
				for (int j2 = 0; j2 < inputs.getL2(i).size(); j2++)
				{
					res = inputs.getL2(i).get(j2);
					newRes.add(result.get(j).concat(inputs.getL2(i).get(j2), true));
				}
			}
			
			result = newRes;
		}
		
		return result;
	}

	private ArrayList<TwigResult> xProdcut(ArrayList<TwigResult> ordinaryRes, TwinArrayList<EvaluationTreeNodeInput, ArrayList<TwigResult>> inputs)
	{		
		ArrayList<TwigResult> notRes = xProdcut(inputs);
		ArrayList<TwigResult> newRes = new ArrayList<TwigResult>();
		
		if (inputs.size() == 0)
		{
			for (int i = 0; i < ordinaryRes.size(); i++)
			{
				newRes.add(ordinaryRes.get(i));
			}
			return newRes;
		}
		for (int i = 0; i < ordinaryRes.size(); i++)
		{
			for (int j = 0; j < notRes.size(); j++)
			{
				newRes.add(ordinaryRes.get(i).concat(notRes.get(j), true));	
			}
		}
		return newRes;
	}
	private boolean andExclude(TwigResult res) throws DBException, IOException
	{
		if (NOTChildren.isEmpty())
		{
			return false;
		}
		
		TwinArrayList<EvaluationTreeNodeInput, ArrayList<TwigResult>> selected = new TwinArrayList<EvaluationTreeNodeInput, ArrayList<TwigResult>>();
		selected = orBalance(NOTChildren, selected);
		
		int c;
		
		c = res.getRes(mainNode).compareTo(selected.getL1(0).getHead().getRes(mainNode));
		
		while(c > 0)
		{
			for(int i = 0; i < selected.size(); ++i)
			{
				this.advance(selected.getL1(i), selected.getL2(i)); //used to skip unsed data
				if(selected.getL1(i).isFinished())
				{
					this.IOTime += selected.getL1(i).getIOTime();
					this.numberOfReadElements += selected.getL1(i).getNumberOfReadElements(); 

					this.AllChildren.remove(this.AllChildren.indexOfL1(selected.getL1(i)));
					this.NOTChildren.remove(this.NOTChildren.indexOfL1(selected.getL1(i)));
				}
			}
			if (NOTChildren.isEmpty())
			{
				return false;
			}
			selected.clear();
			selected = orBalance(NOTChildren, selected);
			
			c = res.getRes(mainNode).compareTo(selected.getL1(0).getHead().getRes(mainNode));						
		}
		
		if(c == 0)
		{
			return true;
		}
		
		return false;
		
//		boolean bl = NOTChildren.isEmpty()? false : andBalance(NOTChildren);
//		
//		if (bl)
//		{
//			//ArrayList<TwigResult> subSet;
//			c = res.getRes(mainNode).compareTo(joinLevel, NOTChildren.getL1(0).getHead().getRes(mainNode));
//			while (bl && c >0)
//			{
//				//subSet = this.subSets.get(NOTChildren.get(0));
//				this.advance(NOTChildren.getL1(0), NOTChildren.getL2(0));
//				if (!NOTChildren.getL1(0).isFinished())
//				{
//					//bl = andBalance(NOTChildren);
//					TwinArrayList<EvaluationTreeNodeInput, ArrayList<TwigResult>> selected = new TwinArrayList<EvaluationTreeNodeInput, ArrayList<TwigResult>>();
//					selected = orBalance(NOTChildren, selected);
//					if (bl)
//					{
//						c = res.getRes(mainNode).compareTo(joinLevel, NOTChildren.getL1(0).getHead().getRes(mainNode));
//					}
//					else
//					{
//						this.NOTChildren.clear(); //no not empty NOTChildren is usable for AND operator
//					}
//				}
//				else
//				{
//					this.NOTChildren.clear(); //no not empty NOTChildren is usable for AND operator
//					return false;
//				}
//			}
//			if (bl && c ==0)
//			{
//				return true; //Not children could delete all outputs from selfInput so the res should be exclude from final result
//			}
//		}
//		else
//		{
//			this.NOTChildren.clear(); //no not empty NOTChildren is usable for AND operator
//		}
//		return false;
	}
	/***
	 * 
	 * @param res
	 * @param balanced return Inputs which are balanced
	 * @return true if output should be excluded
	 * @throws DBException
	 */
	private boolean orExclude(TwigResult res, TwinArrayList<EvaluationTreeNodeInput, ArrayList<TwigResult>> balanced) throws DBException, IOException
	{	
		if (NOTChildren.isEmpty())
		{
			return false;
		}
		balanced = orBalance(NOTChildren, balanced);
		boolean bl = !balanced.isEmpty();
		int c;
		if (bl)
		{
//			ArrayList<TwigResult> subSet;
			c = res.getRes(mainNode).compareTo(balanced.getL2(0).get(0).getRes(mainNode));
			while (bl && c >0)
			{
				//subSet = this.subSets.get(NOTChildren.get(0));
				for (int i = 0; i < balanced.size(); i++)
				{
					this.advance(balanced.getL1(i), balanced.getL2(i)); //advance to skip unusable data
					if(balanced.getL1(i).isFinished())
					{
						this.IOTime += balanced.getL1(i).getIOTime();
						this.numberOfReadElements += balanced.getL1(i).getNumberOfReadElements(); 
						
						this.AllChildren.remove(this.AllChildren.indexOfL1(balanced.getL1(i)));
						this.NOTChildren.remove(this.NOTChildren.indexOfL1(balanced.getL1(i)));
					}
				}

				balanced = orBalance(NOTChildren, balanced);
				bl = !balanced.isEmpty();
				if (bl)
				{
					c = res.getRes(mainNode).compareTo(balanced.getL1(0).getHead().getRes(mainNode));
				}
//				else
//				{						
//					for (int i = 0; i < balanced.size(); i++)
//					{
//						subSet = this.subSets.get(balanced.get(i));
//						this.advance(balanced.get(0), subSet);
//					}
//				}

			}
			if (bl && c ==0 && balanced.size() == NOTChildrenNo) //all original NOT children have data so OR for NOT children is false and exclude shoudl be true
			{
				return true; //Not children could delete all outputs from selfInput so the res should be exclude from final result
			}
		}
//		else
//		{
//			balanced.clear(); //no not empty NOTChildren is usable for AND operator
//		}
		return false;
	}

	/***
	 * 
	 * @param input
	 * @param subSet
	 * @return the subSet object itself when filled
	 * @throws DBException
	 */
	private ArrayList<TwigResult> advance(EvaluationTreeNodeInput input, ArrayList<TwigResult> subSet) throws DBException, IOException
	{
		
		TwigResult key = input.getHead();
		subSet.clear();
//		try
//		{
		while(!input.isFinished() && (key.getRes(mainNode).compareTo(input.getHead().getRes(mainNode)) == 0))
		{
			subSet.add(input.getHead());
			input.next();
		}
//		}
//		catch (Exception e) {
//			// TODO: handle exception
//			int i =1;
//		}
		return subSet;	
	}
	
	/***
	 * 
	 * @param res
	 * @return complete the result into the res and return res itself 
	 */
	private TwigResult completeToUpperJoinPoint(TwigResult res)
	{
		//System.out.println(this.getClass().getName() + ": res function not implemented!");
		DeweyID deweyId = res.getRes(mainNode);
		QTPNode node;
		int level;
		TwinObject<QTPNode, Integer> twnObj;
//		for (int i =0; i < qNodes.size(); ++i)
//		{
//			twnObj = qNodes.get(i);
//			res.add(twnObj.getO1(), deweyId.getAncestorDeweyID(twnObj.getO2()));
//		}
		if (qNodes.size() > 0)
		{
			twnObj = qNodes.get(0);
			res.add(twnObj.getO1(), deweyId.getAncestorDeweyID(twnObj.getO2()));
		}
		//res.addComp(mainNode, qNodes);
		return res;
	}
	
	public ArrayList<TwinObject<QTPNode, Integer>> getComps()
	{
		return this.qNodes;
	}
	
	public ArrayList<EvaluationTreeNodeInput> getChildren()
	{
		return this.children.getL1();
	}
	
	public ArrayList<EvaluationTreeNodeInput> getNOTChildren()
	{
		return this.NOTChildren.getL1();
	}
	
	public QTPNode getMainNode()
	{
		return this.mainNode;
	}
	
	public String toString()
	{
		return this.mainNode.toString();
	}
	
	public long getIOTime()
	{
		if (this.isLeaf)
		{
			return this.selfInput.getIOTime();
		}
		for (EvaluationTreeNodeInput input : this.AllChildren.getL1())
		{
			this.IOTime += input.getIOTime();
		}
		
		return this.IOTime;
	}
	
	public long getNumberOfReadElements()
	{
		if (this.isLeaf)
		{
			return this.selfInput.getNumberOfReadElements();
		}
		
		for (EvaluationTreeNodeInput input : this.AllChildren.getL1())
		{
			this.numberOfReadElements += input.getNumberOfReadElements();
		}
		
		return this.numberOfReadElements;		
	}

	//////////////////////////////////////////////////////////////////////////////
	/////----------------------------------------------------
	private class ChildrenInputComparator implements Comparator<EvaluationTreeNodeInput>
	{
		private QTPNode node;
		
		public ChildrenInputComparator(QTPNode node)
		{
			this.node = node;
		}
		
		public int compare(EvaluationTreeNodeInput input1, EvaluationTreeNodeInput input2)
		{
			TwigResult res1;
			TwigResult res2;
			res1 = input1.getHead();
			res2 = input2.getHead();
			int c = res1.getRes(node).compareTo(res2.getRes(node));
			if (c != 0)
			{
				return c;
			}
			return 0;
		}
	}

}

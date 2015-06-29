/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xmlProcessor.RG.RGX.EvaluationTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import xmlProcessor.DBServer.utils.BlockingQueue;
import xmlProcessor.DBServer.DBException;
import indexManager.StructuralSummaryNode;
import java.io.IOException;

import xmlProcessor.DeweyID;
import xmlProcessor.DBServer.utils.TwinArrayList;
import xmlProcessor.DBServer.utils.TwinObject;
import xmlProcessor.QTP.QTPNode;
import xmlProcessor.QTP.QTPNode.OpType;
import xmlProcessor.RG.RGX.RGGroupedCorrelatedPlansTuple;
import xmlProcessor.RG.RGX.CorrelatedPlans;
import xmlProcessor.RG.RGX.InputWrapper;
import xmlProcessor.RG.RGX.TwigResult;

/**
 * 
 * @author Izadi
 *
 */
public class EvaluationTreeNode implements EvaluationTreeNodeInput
{

	private ArrayList<TwinObject<QTPNode, Set<StructuralSummaryNode>>> qNodes; //nodes and their level
	private QTPNode mainNode; //main node which qNodes produce their corresponding results based on it 
//	private EvaluationTreeNode parent;	
	private TwinArrayList<EvaluationTreeNodeInput, ArrayList<TwigResult>> children; //children with  ordinary connection with their subSet 
	private TwinArrayList<EvaluationTreeNodeInput, ArrayList<TwigResult>> NOTChildren; //children with  NOT operator connection with their subSet
	private TwinArrayList<EvaluationTreeNodeInput, ArrayList<TwigResult>> AllChildren; //children with  NOT operator connection with their subSet
	private boolean hasChild;
	private InputWrapper selfInputWrapper;
	private QTPNode.OpType opType;
	//private int joinLevel;
	private TwigResult head;
	private boolean finished;
	private int NOTChildrenNo;
	//private ArrayList<TwigResult> subSet;
	//private HashMap<EvaluationTreeNodeInput, ArrayList<TwigResult>> subSets;
	private Queue<TwigResult> outputQueue;
        //**************FOR PARALLELIZING:
        private BlockingQueue<TwigResult> paralQueue;
        //**************
	private ArrayList<Plan> plans;
//	private	TwinArrayList<Integer, TwinArrayList<QTPNode,Integer>> plans; // join point level for selceted plan in ArrayList<Integer>
//	private TwinArrayList<Integer, TwinArrayList<QTPNode,Integer>> notPlans;
//	private ArrayList<TwinArrayList<QTPNode, TwinObject<Integer, Integer>>> upperNodes; //upper nodes and their level and PCR which regards to it their dewey ids should be produced
	private int minJoinLevel;
	private int maxJoinLevel;
	private RGGroupedCorrelatedPlansTuple groupedCorrelatedPlansTuple = null;
	private ArrayList<TwigResult> completedRes;
	private ArrayList<TwigResult> outputRes;
        private boolean checkForNot; // it will be false when one of not branches which has data gets empty so every result is OK
	private boolean isLeaf;
	private long IOTime = 0;
	private long numberOfReadElements = 0;
        private Thread threadRef;
        
        
	public EvaluationTreeNode(ArrayList<TwinObject<QTPNode, Set<StructuralSummaryNode>>> qNodes, QTPNode mainNode, RGGroupedCorrelatedPlansTuple groupedCorrelatedPlansTuple)
	{
            
		this.qNodes = qNodes;
		this.mainNode = mainNode;
		//this.parent = null;
		this.children = new TwinArrayList<EvaluationTreeNodeInput, ArrayList<TwigResult>>();
		this.NOTChildren = new TwinArrayList<EvaluationTreeNodeInput, ArrayList<TwigResult>>();
		this.AllChildren = new TwinArrayList<EvaluationTreeNodeInput, ArrayList<TwigResult>>();
		this.hasChild = false;
		this.selfInputWrapper = null;
		this.opType = mainNode.getOpType();
		//this.joinLevel = -1;
		this.head = null;
		this.finished = false;
		//this.subSet = new ArrayList<TwigResult>();
		//this.subSets = new HashMap<EvaluationTreeNodeInput, ArrayList<TwigResult>>();
		this.outputQueue = new LinkedList<TwigResult>();
                //*****threadref is not valid yet:
               // this.paralQueue=new BlockingQueue<TwigResult>(this.threadRef);
                //****
		this.groupedCorrelatedPlansTuple = groupedCorrelatedPlansTuple;
		this.plans = new ArrayList<Plan>();
//		this.plans = new TwinArrayList<Integer, TwinArrayList<QTPNode,Integer>>();
//		this.notPlans = new TwinArrayList<Integer, TwinArrayList<QTPNode,Integer>>();
//		this.upperNodes = new ArrayList<TwinArrayList<QTPNode, TwinObject<Integer,Integer>>>();
		this.completedRes = new ArrayList<TwigResult>();
		this.outputRes = new ArrayList<TwigResult>();
		this.checkForNot = true;
		this.isLeaf = false;
                //new Thread(this).start();
                
	}
//        @Override
//	public void run()
//        {
//            commonSettings.Settings.tCount++;
//            logManager.LogManager.log("Thread"+commonSettings.Settings.tCount );
//            try {
//                try {
//                    this.open();
//                } catch (InterruptedException ex) {
//                    System.err.println("InterruptedException occured in thread EvaluationTreeNode while runing function open()");
//                }
//            } catch (DBException ex) {
//                    System.err.println("DBException occured in thread EvaluationTreeNode while runing function open()");
//                } catch (IOException ex) {
//                    System.err.println("IOException occured in thread EvaluationTreeNode while runing function open()");
//                }
//            while(true)
//            {
//                try {
//                     paralQueue.enqueue(this.getInternalHead());
//                    this.next();
//                    if(this.getInternalHead()==null)
//                        break;
//                   
//                } catch (DBException ex) {
//                    System.err.println("DBException occured in thread EvaluationTreeNode while runing function next()");
//                } catch (IOException ex) {
//                    System.err.println("IOException occured in thread EvaluationTreeNode while runing function next()");
//                } catch (InterruptedException ex) {
//                    System.err.println("InterruptedException occured in thread EvaluationTreeNode while runing function next()");
//                }
//            }
//            
//        }
//	public EvaluationTreeNode(ArrayList<TwinObject<QTPNode, Integer>> qNodes, QTPNode mainNode)
//	{
//		this.qNodes = qNodes;
//		this.mainNode = mainNode;
////		this.parent = null;
//		this.children = new TwinArrayList<EvaluationTreeNodeInput, ArrayList<TwigResult>>();
//		this.NOTChildren = new TwinArrayList<EvaluationTreeNodeInput, ArrayList<TwigResult>>();
//		this.AllChildren = new TwinArrayList<EvaluationTreeNodeInput, ArrayList<TwigResult>>();
//		this.hasChild = false;
//		this.selfInputWrapper = null;
//		this.opType = mainNode.getOpType();
//		this.joinLevel = -1;
//		this.head = null;
//		this.finished = false;
//		//this.subSet = new ArrayList<TwigResult>();
//		//this.subSets = new HashMap<EvaluationTreeNodeInput, ArrayList<TwigResult>>();
//		this.outputQueue = new LinkedList<TwigResult>();
//		this.checkForNot = true;
//		this.isLeaf = false;
//	}
//	
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
	
//	public void setSelfInput(InputWrapper selfInputWrapper)
//	{
//		if (selfInputWrapper.isFinished())
//		{
//			this.selfInputWrapper = null;
//			return;
//		}
//		this.selfInputWrapper = new SelfInput(selfInputWrapper);
//		this.addChild(this.selfInputWrapper, false);
//	}
	
//	public void setJoinLevel(int joinLevel)
//	{
//		this.joinLevel = joinLevel;
//	}
	
	public void open() throws DBException, IOException,InterruptedException
	{
		
		Set<Integer> joinPointsLevels = new HashSet<Integer>();
		Plan plan;
		TwinArrayList<QTPNode, TwinObject<Integer,Integer>> upperNodes;
		int level;
		TwinArrayList<QTPNode, Integer> PCRs;
		TwinArrayList<QTPNode, Integer> notPCRs;
		CorrelatedPlans cPlan;
		cPlan = groupedCorrelatedPlansTuple.getPlan(0);
		StructuralSummaryNode psNode = cPlan.getStructuralSummaryNode(mainNode);
		if (psNode == null)
		{
			this.head = null;
			this.finished = true;
			return;
		}
//		level = cPlan.getLevel(mainNode);
		level = psNode.getLevel();
		PCRs = new TwinArrayList<QTPNode, Integer>();
		notPCRs = new TwinArrayList<QTPNode, Integer>();
		for (int i = 0; i < children.getL1().size(); i++)
		{
			try
			{
				PCRs.add(children.getL1(i).getMainNode(), cPlan.getCID(children.getL1(i).getMainNode()));
			}
			catch (Exception e)
			{
				PCRs.add(children.getL1(i).getMainNode(), null);
			}			
		}
		joinPointsLevels.add(level);
		TwinObject<QTPNode, Set<StructuralSummaryNode>> twnObj;
		int upperLevel;
		int upperPCR;
		upperNodes = new TwinArrayList<QTPNode, TwinObject<Integer,Integer>>();
		for (int i = 0; i < qNodes.size(); i++)
		{
			twnObj = qNodes.get(i);					
			upperLevel = groupedCorrelatedPlansTuple.getPlan(0).getLevel(twnObj.getO1());
			upperPCR = groupedCorrelatedPlansTuple.getPlan(0).getCID(twnObj.getO1());
			upperNodes.add(twnObj.getO1(), new TwinObject<Integer, Integer>(upperLevel, upperPCR));
		}
		//this.upperNodes.add(upperNodes);

		//plans.add(level, new TwinObject<TwinArrayList<QTPNode,Integer>, TwinArrayList<QTPNode, TwinObject<Integer, Integer>>>(PCRs, upperNodes));

		notPCRs = new TwinArrayList<QTPNode, Integer>();
		for (int i = 0; i < NOTChildren.getL1().size(); i++)
		{
			try
			{
				notPCRs.add(NOTChildren.getL1(i).getMainNode(), cPlan.getCID(NOTChildren.getL1(i).getMainNode()));
			}
			catch (Exception e)
			{
				notPCRs.add(NOTChildren.getL1(i).getMainNode(), null);
			}			
		}
		//notPlans.add(level, new TwinObject<TwinArrayList<QTPNode,Integer>, TwinArrayList<QTPNode, TwinObject<Integer, Integer>>>(notPCRs, upperNodes));

		plan = new Plan(level, PCRs, notPCRs, upperNodes);
		this.plans.add(plan);
		this.minJoinLevel = level;
		this.maxJoinLevel = level;
		for (int i = 1; i < groupedCorrelatedPlansTuple.groupedPlansNo(); i++)
		{
			cPlan = groupedCorrelatedPlansTuple.getPlan(i);
			level = cPlan.getLevel(mainNode);
			PCRs = new TwinArrayList<QTPNode, Integer>();
			for (int j = 0; j < children.getL1().size(); j++)
			{
				try
				{
					PCRs.add(children.getL1(j).getMainNode(), cPlan.getCID(children.getL1(j).getMainNode()));
				}
				catch (Exception e)
				{
					PCRs.add(children.getL1(j).getMainNode(), null);
				}	
			}
			

			notPCRs = new TwinArrayList<QTPNode, Integer>();
			for (int j = 0; j < NOTChildren.getL1().size(); j++)
			{
				try
				{
					notPCRs.add(NOTChildren.getL1(j).getMainNode(), cPlan.getCID(NOTChildren.getL1(j).getMainNode()));
				}
				catch (Exception e)
				{
					notPCRs.add(NOTChildren.getL1(j).getMainNode(), null);
				}	
			}
			
			
			upperNodes = new TwinArrayList<QTPNode, TwinObject<Integer,Integer>>();
			for (int j = 0; j < qNodes.size(); j++)
			{
				twnObj = qNodes.get(j);					
				upperLevel = groupedCorrelatedPlansTuple.getPlan(i).getLevel(twnObj.getO1());
				upperPCR = groupedCorrelatedPlansTuple.getPlan(i).getCID(twnObj.getO1());
				upperNodes.add(twnObj.getO1(), new TwinObject<Integer, Integer>(upperLevel, upperPCR));
			}
			boolean match = false;
			for (int j = 0; j < plans.size(); j++)
			{
				//if(notPCRs.equals(notPlans.getL2(j)) && PCRs.equals(plans.getL2(j)) && upperNodes.equals(this.upperNodes.get(j)))
				if(PCRs.equals(plans.get(j).plan) && upperNodes.equals(this.plans.get(j).upperNodes))
				{
					match = true;
					break; //it is not needed to be added
				}				
			}
			if (!match)
			{
				plan = new Plan(level, PCRs, notPCRs, upperNodes);
				this.plans.add(plan);
				//notPlans.add(level, new TwinObject<TwinArrayList<QTPNode,Integer>, TwinArrayList<QTPNode, TwinObject<Integer, Integer>>>(notPCRs, upperNodes));
				//plans.add(level, new TwinObject<TwinArrayList<QTPNode,Integer>, TwinArrayList<QTPNode, TwinObject<Integer, Integer>>>(PCRs, upperNodes));
				//this.upperNodes.add(upperNodes);

				if (level>maxJoinLevel)
				{
					maxJoinLevel = level;
				}
				if (level < minJoinLevel)
				{
					minJoinLevel = level;
				}
				joinPointsLevels.add(level);

			}
		}
		
		Plan[] p = new Plan[plans.size()];
		p = plans.toArray(p);
		Arrays.sort(p, new PlanComparator());
		for (int j = 0; j < p.length; j++)
		{
			plans.set(j, p[j]);
		}
		//plans.sortBasedOnL1(new PlanComparator());
		//notPlans.sortBasedOnL1(new PlanComparator());
		
		
		if (children.size() == 1 && NOTChildren.size() == 0 && children.getL1(0) instanceof SelfInput)
		{
			this.isLeaf = true;
			this.selfInputWrapper = ((SelfInput)children.getL1(0)).getInputWrapper();
			this.selfInputWrapper.open();
			if (this.selfInputWrapper.isFinished())
			{
				this.head = null;
				this.finished = true;
			}
			else
			{
				TwigResult res;
				res = this.selfInputWrapper.getHead();				
				this.completedRes.clear();
				this.completeToUpperJoinPoints(res, completedRes, false);
				for (int i = 0; i < completedRes.size(); i++)
				{
					this.outputQueue.offer(completedRes.get(i));
				}
				this.head = this.outputQueue.poll();
                                //here problem starts:!!!!!!!!!!
				this.selfInputWrapper.next();
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
//		if (this.selfInputWrapper != null)
//		{
//			this.selfInputWrapper.open();
//			//this.selfInputWrapper.next();
//		}
		this.next();
		if (this.head == null)
		{
			this.finished = true;
		}
		
	}

	public void close() throws DBException
	{
		for (EvaluationTreeNodeInput child : children.getL1())
		{
			child.close();
		}
		for (EvaluationTreeNodeInput NOTChild : NOTChildren.getL1())
		{
			NOTChild.close();
		}
	}
	
        @Override
        public TwigResult getHead() throws InterruptedException
        {
//            return this.paralQueue.getHead();
//                    //(TwigResult)paralQueue.getHead();
            return this.head;                  
        }
//	private TwigResult getInternalHead()
//	{
//		return this.head;
//	}
	
	public void next() throws DBException, IOException, InterruptedException
	{
		if (!this.outputQueue.isEmpty())
		{
			this.head = this.outputQueue.poll();
                        //????????dequeue
			return;
		}

		if(isLeaf)
		{
			//this.selfInputWrapper.next();
			if (this.selfInputWrapper.isFinished())
			{
				this.head = null;
				this.finished = true;
			}
			else
			{
				TwigResult res;
				res = this.selfInputWrapper.getHead();
				
//				this.completedRes.clear();
				this.completeToUpperJoinPoints(res, outputQueue, false);
//				for (int i = 0; i < completedRes.size(); i++)
//				{
//					this.outputQueue.offer(completedRes.get(i));
//				}
				this.head = this.outputQueue.poll();
				this.selfInputWrapper.next();
			}
			return;
		}
			
//		if (! hasChild)
//		{			
//			TwigResult res;
//			res = this.selfInputWrapper.getHead();
//			this.selfInputWrapper.next();
//			if (res == null)
//			{
//				this.head = null;
//				this.finished = true;
//				return;
//			}
//			else
//			{
//				this.completedRes.clear();
//				this.completeToUpperJoinPoints(res, completedRes, false);
//				for (int i = 0; i < completedRes.size(); i++)
//				{
//					this.outputQueue.offer(completedRes.get(i));
//				}
//			}
//		}
//		else if (this.opType == QTPNode.OpType.AND)
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
		
		if (!isFinished())
		{
			if (this.outputQueue.isEmpty())
			{
				next();//internal ehtemalan
			}
			else
			{
				this.head = this.outputQueue.poll();
			}
		}
		else
		{
			this.head = null;
			this.finished = true;
			return;
		}
	}
	
	private void processAND() throws DBException, IOException, InterruptedException
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
		
		
		ArrayList<TwigResult> andJoinResult;
		andJoinResult = xProdcut(this.children);

		boolean excluded = false;
		if (NOTChildren.size() > 0)
		{
			excluded = this.andExclude(andJoinResult.get(0));
		}
		
		if (!excluded)
		{
			for (int i = 0; i < andJoinResult.size(); i++)
			{
				this.completeToUpperJoinPoints(andJoinResult.get(i), this.outputQueue, false);
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
//		else //if (children.size() == 0)  //hasChild is true so it means that  we have only NOTChildrens and so selfInputWrapper
//		{
//			ArrayList<TwigResult> selfInputSubSet;
//
//			this.selfInputWrapper.advance();
//			selfInputSubSet = this.selfInputWrapper.getSubSet();
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
	
	private boolean andBalance(TwinArrayList<EvaluationTreeNodeInput, ArrayList<TwigResult>> inputs) throws DBException, IOException, InterruptedException
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
	
	private void processOR() throws DBException, IOException, InterruptedException
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
		if (children.size() == 0 && this.selfInputWrapper == null)
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
				//TODO: if the empty not branch had data at start up then checkForNot should be set false
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
					orJoinResult.remove(i);
				}
				else
				{
					i++;
				}
			}
		}
		for (int i = 0; i < orJoinResult.size(); i++)
		{
			this.completeToUpperJoinPoints(orJoinResult.get(i), this.outputQueue, false);
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
//			this.selfInputWrapper.advance();
//			selfInputSubSet = this.selfInputWrapper.getSubSet();
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
	
	private TwinArrayList<EvaluationTreeNodeInput, ArrayList<TwigResult>> orBalance(TwinArrayList<EvaluationTreeNodeInput, ArrayList<TwigResult>> inputs, TwinArrayList<EvaluationTreeNodeInput, ArrayList<TwigResult>> selected) throws DBException, InterruptedException
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
			result.add( inputs.getL2(0).get(i));
		}
		
		for (int i = 1; i < inputs.size(); i++)
		{
			ArrayList<TwigResult> newRes = new ArrayList<TwigResult>();
			for (int j = 0; j < result.size(); j++)
			{
				for (int j2 = 0; j2 < inputs.getL2(i).size(); j2++)
				{
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
	private boolean andExclude(TwigResult res) throws DBException, IOException, InterruptedException
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
//				return true; //Not children could delete all outputs from selfInputWrapper so the res should be exclude from final result
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
	private boolean orExclude(TwigResult res, TwinArrayList<EvaluationTreeNodeInput, ArrayList<TwigResult>> balanced) throws DBException, IOException, InterruptedException
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
				return true; //Not children could delete all outputs from selfInputWrapper so the res should be exclude from final result
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
	private ArrayList<TwigResult> advance(EvaluationTreeNodeInput input, ArrayList<TwigResult> subSet) throws DBException, IOException, InterruptedException
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
	private void completeToUpperJoinPoints(TwigResult res, Queue<TwigResult> completedRes, boolean notPlan)
	{
		//System.out.println(this.getClass().getName() + ": res function not implemented!");
		
//		TwinArrayList<Integer, TwinObject<TwinArrayList<QTPNode,Integer>,TwinArrayList<QTPNode, TwinObject<Integer, Integer>>>> plans;
		TwinArrayList<EvaluationTreeNodeInput, ArrayList<TwigResult>> inputs;
		DeweyID deweyId = res.getRes(mainNode);
//		if (notPlan)
//		{
//			plans = this.notPlans;
//			inputs = this.NOTChildren;
//		}
//		else
//		{
//			plans = this.plans;
//			inputs = this.children;
//		}

		if (plans.size() > 1)
		{
			for (int i = 0; i < plans.size(); i++)
			{
				boolean match;
				if (notPlan)
				{
					inputs = this.NOTChildren;
					match = matchPlan(res, plans.get(i).notPlan, plans.get(i).level, inputs, this.opType);	
				}
				else
				{
					inputs = this.children;
					match = matchPlan(res, plans.get(i).plan, plans.get(i).level, inputs, this.opType);
				}
				
				if (match)
				{
					TwigResult newRes = res.clone();
					TwinArrayList<QTPNode, TwinObject<Integer, Integer>> upperNodes;
					//upperNodes = this.upperNodes.get(i);
					upperNodes = this.plans.get(i).upperNodes;
					for (int j = 0; j < upperNodes.size(); j++)
					{
						DeweyID topDeweyID = deweyId.getAncestorDeweyID(upperNodes.getL2(j).getO1());
						topDeweyID.setCID(upperNodes.getL2(j).getO2());
						newRes.add(upperNodes.getL1(j), topDeweyID); 					
					}
					completedRes.offer(newRes);

				}
			}
		}
		else
		{			
			TwinArrayList<QTPNode, TwinObject<Integer, Integer>> upperNodes;
			//upperNodes = this.upperNodes.get(0);
			upperNodes = this.plans.get(0).upperNodes;
			for (int j = 0; j < upperNodes.size(); j++)
			{
				DeweyID topDeweyID = deweyId.getAncestorDeweyID(upperNodes.getL2(j).getO1());
				topDeweyID.setCID(upperNodes.getL2(j).getO2());
				res.add(upperNodes.getL1(j), topDeweyID); 					
			}
			completedRes.offer(res);
		}
		
		
	}
	
	/***
	 * 
	 * @param 
	 * @return  
	 */
	private void completeToUpperJoinPoints(TwigResult res, ArrayList<TwigResult> completedRes, boolean notPlan)
	{
		//System.out.println(this.getClass().getName() + ": res function not implemented!");
		//TwinArrayList<Integer, TwinObject<TwinArrayList<QTPNode,Integer>,TwinArrayList<QTPNode, TwinObject<Integer, Integer>>>> plans;
		TwinArrayList<EvaluationTreeNodeInput, ArrayList<TwigResult>> inputs;
		DeweyID deweyId = res.getRes(mainNode);
//		if (notPlan)
//		{
//			plans = this.notPlans;
//			inputs = this.NOTChildren;
//		}
//		else
//		{
//			plans = this.plans;
//			inputs = this.children;
//		}

		if (plans.size() > 1)
		{
			boolean hasMatch = false;
			for (int i = 0; i < plans.size(); i++)
			{
				boolean match;
				if (notPlan)
				{
					inputs = this.NOTChildren;
					match = matchPlan(res, plans.get(i).notPlan, plans.get(i).level, inputs, this.opType);	
				}
				else
				{
					inputs = this.children;
					match = matchPlan(res, plans.get(i).plan, plans.get(i).level, inputs, this.opType);
				}

				if (match)
				{
					hasMatch = true;
					TwigResult newRes = res.clone();
					TwinArrayList<QTPNode, TwinObject<Integer, Integer>> upperNodes;
					//upperNodes = this.upperNodes.get(i);
					upperNodes = this.plans.get(i).upperNodes;
					for (int j = 0; j < upperNodes.size(); j++)
					{
						DeweyID topDeweyID = deweyId.getAncestorDeweyID(upperNodes.getL2(j).getO1());
						topDeweyID.setCID(upperNodes.getL2(j).getO2());
						newRes.add(upperNodes.getL1(j), topDeweyID); 					
					}
					completedRes.add(newRes);

				}
			}
			if(!hasMatch)
				System.out.println("NOT MATCHED!");

		}
		else
		{
			TwinArrayList<QTPNode, TwinObject<Integer, Integer>> upperNodes;
			//upperNodes = this.upperNodes.get(0);
			upperNodes = this.plans.get(0).upperNodes;
			for (int j = 0; j < upperNodes.size(); j++)
			{
				DeweyID topDeweyID = deweyId.getAncestorDeweyID(upperNodes.getL2(j).getO1());
				topDeweyID.setCID(upperNodes.getL2(j).getO2());
				res.add(upperNodes.getL1(j), topDeweyID); 					
			}
			completedRes.add(res);
		}
	}

	
	private boolean matchPlan(TwigResult res, TwinArrayList<QTPNode,Integer> matchPlan, int joinPointLevel, TwinArrayList<EvaluationTreeNodeInput, ArrayList<TwigResult>> inputs, OpType opType)
	{
		//System.out.println(this.getClass().getName() + ": res function not implemented!");
		boolean match = true;

		if (opType == OpType.OR)
		{
			HashSet<DeweyID> joinPointIDs = new HashSet<DeweyID>();
			DeweyID joinPointID = null;
			boolean multipleRes = false;
			for (int j = 0; j < inputs.size(); j++)
			{
				if (matchPlan.get(j) != null)
				{
					QTPNode node = inputs.get(j).getO1().getMainNode();
					int index = matchPlan.indexOfL1(node);
					if (res.getRes(node) != null)
					{
						if(res.getRes(node).getCID() != matchPlan.getL2(index))
						{
							match = false;
							break;
						}
						if (joinPointID == null)
						{
							joinPointID = res.getRes(node).getAncestorDeweyID(joinPointLevel);
							joinPointIDs.add(joinPointID);
						}
						else if(joinPointIDs.add(res.getRes(node).getAncestorDeweyID(joinPointLevel))) //TRUE IF DIFFERENT JOIN POINT
						{
							multipleRes = true;										
						}
					}
				}
			}
			if (match & multipleRes)
			{
				System.out.println("MULTIPLE RES SHOUDL BE CREATED.");
			}
		}
		else if (opType == OpType.AND)
		{
			DeweyID joinPointID = null;
			//here matchPlan and inputs have to have the same size
			for (int j = 0; j < matchPlan.size(); j++)
			{
				if (matchPlan.get(j) != null)
				{
					QTPNode node = inputs.get(j).getO1().getMainNode();
					
					if (res.getRes(node)!= null)
					{
						if(res.getRes(node).getCID() != matchPlan.getL2(j))
						{
							match = false;
							break;
						}
						if (joinPointID == null)
						{
							joinPointID = res.getRes(node).getAncestorDeweyID(joinPointLevel);
						}
						if(!joinPointID.isSelfOf(res.getRes(node).getAncestorDeweyID(joinPointLevel)))
						{
							match = false;
							break;						
						}
					}
					else
					{
						match = false;
						break;
					}
				}

			}
		}
//		for (int j = 0; j < matchPlan.size(); j++)
//		{
//				if (matchPlan.get(j) != null)
//				{
//					QTPNode node = inputPlans.get(j).getO1().getMainNode();
//					int index = matchPlan.indexOfL1(node);
//					if(res.getRes(node) != null && res.getRes(node).getCID() != matchPlan.getL2(index))
//					{
//						match = false;
//						break;
//					}
//				}
//			{
//			}
//		}
		return match;
	}
		
//	/***
//	 * 
//	 * @param res
//	 * @return complete the result into the res and return res itself 
//	 */
//	private TwigResult completeToUpperJoinPoint(TwigResult res)
//	{
//		//System.out.println(this.getClass().getName() + ": res function not implemented!");
//		DeweyID deweyId = res.getRes(mainNode);
//		QTPNode node;
//		int level;
//		TwinObject<QTPNode, Integer> twnObj;
////		for (int i =0; i < qNodes.size(); ++i)
////		{
////			twnObj = qNodes.get(i);
////			res.add(twnObj.getO1(), deweyId.getAncestorDeweyID(twnObj.getO2()));
////		}
//		if (qNodes.size() > 0)
//		{
//			twnObj = qNodes.get(0);
//			res.add(twnObj.getO1(), deweyId.getAncestorDeweyID(twnObj.getO2()));
//		}
//		//res.addComp(mainNode, qNodes);
//		return res;
//	}
	
	public ArrayList<TwinObject<QTPNode, Set<StructuralSummaryNode>>> getComps()
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

//    @Override
//    public void next() throws DBException, IOException, InterruptedException 
//    {
//        //return getHead();
//         this.paralQueue.dequeue();
//    }
//       @Override
//    public void start(Thread t)
//    {
//         this.threadRef=t;
//         this.paralQueue=new BlockingQueue<TwigResult>(this.threadRef);
//         t.start();
//    }
//	//////////////////////////////////////////////////////////////////////////////
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
			TwigResult res1=null;
			TwigResult res2=null;
                    try {
                        res1 = input1.getHead();
                    } catch (InterruptedException ex) {
                        System.err.println("InterruptedException");
                    }
                    try {
                        res2 = input2.getHead();
                    } catch (InterruptedException ex) {
                        System.err.println("InterruptedException");
                    }
			int c = res1.getRes(node).compareTo(res2.getRes(node));
			if (c != 0)
			{
				return c;
			}
			return 0;
		}
	}
	
	public long getIOTime()
	{
		if (this.isLeaf)
		{
			return this.selfInputWrapper.getIOTime();
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
			return this.selfInputWrapper.getNumberOfReadElements();
		}
		
		for (EvaluationTreeNodeInput input : this.AllChildren.getL1())
		{
			this.numberOfReadElements += input.getNumberOfReadElements();
		}
		
		return this.numberOfReadElements;		
	}

//////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////
	private class PlanComparator implements Comparator<Plan>
	{
		public int compare(Plan input1, Plan input2)
		{
			return ((Integer)(input1.level)).compareTo((Integer)input2.level);
		}
	}
	
	////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////
	private class Plan
	{		
		public int level;
		public TwinArrayList<QTPNode,Integer> plan;
		public TwinArrayList<QTPNode,Integer> notPlan;
		public TwinArrayList<QTPNode, TwinObject<Integer, Integer>> upperNodes;
		
		public Plan(int level, TwinArrayList<QTPNode,Integer> plan, TwinArrayList<QTPNode,Integer> notPlan, TwinArrayList<QTPNode, TwinObject<Integer, Integer>> upperNodes)
		{
			this.level = level;
			this.plan = plan;
			this.notPlan = notPlan;
			this.upperNodes = upperNodes;
		}
                
	}
}

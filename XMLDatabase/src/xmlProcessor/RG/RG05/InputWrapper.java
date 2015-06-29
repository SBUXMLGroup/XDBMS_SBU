/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xmlProcessor.RG.RG05;

import indexManager.IndexManager;
import indexManager.ElementIndex;
import java.util.ArrayList;
import java.util.HashMap;

import xmlProcessor.DBServer.DBException;
import indexManager.StructuralSummaryNode;
import xmlProcessor.DeweyID;
import xmlProcessor.DBServer.utils.TwinObject;
//import xtc.server.xmlSvc.XTCxqueryContext;
import xmlProcessor.DBServer.operators.QueryOperator;
//import xtc.server.xmlSvc.xqueryOperators.PathIdxBased.XTCxqueryPCRScanOp;
import indexManager.Iterators.ElementIterator;
import java.io.IOException;
import java.util.HashSet;
import xmlProcessor.DBServer.operators.twigs.QueryStatistics;
import xmlProcessor.QTP.QTPNode;
import xmlProcessor.QTP.QueryTreePattern;
import xmlProcessor.QTP.QTPNodeConstraint;
import xmlProcessor.RG.RG05.EvaluationTree.EvaluationTreeNode;
import xmlProcessor.RG.RG05.EvaluationTree.EvaluationTreeNodeInput;
import xmlProcessor.DBServer.utils.QueryTuple;
import statistics.Monitor;

/**
 * @author Kamyar
 *
 */
////////////////////////////////////////////////////////////////
///////////////INPUTWRAPPER CLASS///////////////////////////////
///////////////////////////////////////////////////////////////
public class InputWrapper implements QueryStatistics 
{
	//private Sh inputStreams ;
	//private ArrayList<QueryTuple> streamHeads;
	private ElementIterator inputStream = null;
	private ArrayList<QueryTuple> indrectInput = null;
	private int indirectPos = 0;
	private QTPNode qNode;
	private int CID;
        private long numberOfReadElements = 0; //used for experimental results
	private long IOTime = 0; //used for experimental results
	//private XTCxqueryContext context;
	//private int idxNo;
        private String indexName;
//	private TwigResult head;
//	private boolean isFinished;
//	private boolean splid;
	private QueryTreePattern QTP;
	//private ArrayList<Integer> ancLevels;
	private ArrayList<DeweyID> addition = new ArrayList<DeweyID>();
	//List of open streams that can be shared for new instances of InputWrapper
	private HashMap<Integer, SharedInput> openedStreams = new HashMap<Integer, SharedInput>();
	private SharedInput sharedInput;
	private TwigResult head;
	private boolean finished;
	private ArrayList<TwigResult> subSet;
	private int level;
	private int sharedInputIndex;
	private boolean useSharedInput;
	private ElementIndex input;
        private IndexManager indxMgr;
        private Monitor monitor=Monitor.getInstance();
	
	public InputWrapper(QueryTreePattern QTP, QTPNode qNode, StructuralSummaryNode psNode,String indexName, HashMap<Integer, SharedInput> openedStreams, boolean useSharedInput) throws IOException, DBException
	{
		this.qNode = qNode;
		//this.inputStreams = new ArrayList<ElementIterator>(PCRs.size());
//		this.streamHeads = new ArrayList<QueryTuple>(PCRs.size());
		//this.context = context;
		//this.idxNo = idxNo;
                this.indexName=indexName;
                this.indxMgr=IndexManager.Instance;               
//		this.isFinished = false;
//		this.head = null;
//		this.splid = splid;
		this.QTP = QTP;
		if (psNode == null)
		{
			this.head = null;
			this.finished = true;
			return;
		}
		this.CID = psNode.getCID();

		this.openedStreams = openedStreams;
		this.useSharedInput = useSharedInput;
		if (this.useSharedInput)
		{
			this.sharedInput = this.openedStreams.get(CID);
			if (sharedInput == null)
			{
				sharedInput = new SharedInput(qNode, CID,indexName);			

				this.openedStreams.put(CID, sharedInput);			
			}		
			sharedInputIndex = sharedInput.register(this);
		}
		else
		{
                        input=indxMgr.openElementIndex(indexName);
                        logManager.LogManager.log(5,"getStream("+psNode.getCID()+") "+psNode.getQName());
                        
			this.inputStream =input.getStream(psNode.getCID());
                       // if(psNode.getCID()=311)
                           
                                //new ElementIterator(indexName, (QTPNodeConstraint)qNode, psNode.getCID(), qNode.getName());
                        
		}
//		ancLevels = new ArrayList<Integer>(anc.size());
//		for (int i = 0; i < anc.size(); i++) 
//		{
//			StructuralSummaryNode p = anc.get(i); 
//			ancLevels.add(p.getLevel());
//		}

		this.head = null;
		this.finished = false;
		this.subSet = new ArrayList<TwigResult>();
		this.level = psNode.getLevel();
	}

	public void close() throws DBException
	{
		// TODO Auto-generated method stub

	}
	
//	public QTPNode getRightLeaf()
//	{
//		return qNode;
//	}
//
	//private ArrayList<DeweyID> addition = new ArrayList<DeweyID>();
	int testCounter;
        public static int counter2=0;
	public void next() throws DBException, IOException
	{
//		long s = System.currentTimeMillis();
		if (isFinished())
		{
			this.head = null;
			this.finished = true;
			return;			
		}
		DeweyID id = null;
		
		//tuple =  getMinID();
		if (this.useSharedInput)
		{
			id = this.sharedInput.next(this.sharedInputIndex);
		}
		else
		{
                    DeweyID nextDID= this.inputStream.next();
                    monitor.addElementsRead();
                    QueryTuple tuple=null;
                    if(nextDID!=null)
                    {   tuple =new QueryTuple(nextDID);                        
                        //logManager.LogManager.log(6,"DID: "+nextDID.toString());
                    }
                        if (tuple!= null)
			{
				id = tuple.getIDForPos(0);
			}
		}

		if (id == null) 
		{
//			this.IOTime += System.currentTimeMillis() - s;
			this.head = null;
			this.finished = true;
			return;
		}
		
		//++numberOfReadElements;
//		this.IOTime += System.currentTimeMillis() - s;
                //logManager.LogManager.log(6,"count: "+ ++testCounter);
                logManager.LogManager.log(6,"count: "+ ++counter2);
		this.head = new TwigResult(QTP, qNode, id);                
                logManager.LogManager.log(6,"InpWrapper Head: "+" "+ head.toString());
//		DeweyID leafID = id.getIDForPos(0);
//		addition.clear();
//		for (int i = 0; i < ancLevels.size(); i++)
//		{
//			addition.add(leafID.getAncestorDeweyID(ancLevels.get(i)));
//		}
//		id.addDeweyID(0, addition);

		return;
		//return new TwigResult(QTP, qNode, id);
		
	}
	
	public void advance() throws DBException, IOException
	{
//		if (inputConsumed) { 
//			consumed = true;
//			return;
//		}

		TwigResult key = this.head;
		subSet.clear();

		while(!finished && (key.getRes(qNode).compareTo(level, this.head.getRes(qNode)) == 0))
		{
			subSet.add(this.head);
			next();
		}

	}
	
	public TwigResult getHead()
	{
		return this.head;
	}
	
	public boolean isFinished()
	{
		return this.finished;
	}
	
	public ArrayList<TwigResult> getSubSet()
	{
		return this.subSet;
	}
//	public TwigResult getHead()
//	{
//		return this.head;
//	}
//
//	public boolean isFinished()
//	{
//		return this.isFinished;
//	}
//	private QueryTuple getMinID() throws DBException
//	{
//		if (this.inputStreams.size() == 0) return null;
//
//		int min = 0;
//		QueryTuple minRes = null;
//		for (int i = 0; i < this.inputStreams.size(); i++) 
//		{
//			QueryTuple res = this.streamHeads.get(i);
//			try
//			{
//			if (minRes == null || res.getIDForPos(0).compareTo(minRes.getIDForPos(0)) < 0)
//			{
//				min = i;
//				minRes = res;
//			}
//			}
//			catch (Exception e) {
//				e.printStackTrace();
//				throw new DBException("RG.InputWrapper::getMinID SSSSSSSSS");
//			}
//		} //for
//		
//		//set the head of twigJoinOp which its head is the minimum and should be returned
//		XTCxqueryPCRScanOp minStream = inputStreams.get(min);
//		QueryTuple headRes = minStream.next();
//		if (headRes != null)
//		{
//			this.streamHeads.set(min, headRes);			
//		}
//		else
//		{
//			this.IOTime += minStream.getIOTime();
//			this.inputStreams.remove(min); //inputStreams[min] is finished and should be removed
//			this.streamHeads.remove(min);
//		}
//		return minRes;
//		
//	}
	
	public void open() throws DBException, IOException 
	{
		if (!isFinished())
		{
			if (this.useSharedInput)
			{
				this.sharedInput.open();
			}
			else
			{
				this.inputStream.open();
                                logManager.LogManager.log(6,"InpWrapper opens inpStream with CID: "+this.CID);
			}
			this.next();
		}
		
//		SharedInput sharedInput = this.openedStreams.get(CID);
//		if (sharedInput == null)
//		{
//			sharedInput = new SharedInput(qNode, CID, context, idxNo);
//			sharedInput.open();
//			sharedInput.register(this);
//			this.openedStreams.put(CID, sharedInput);
//		}
	}

	public QTPNode getQTPNode()
	{
		return this.qNode;
	}
	
	public long getNumberOfReadElements()
	{
		if (this.useSharedInput)
		{
		return this.sharedInput.getNumberOfReadElements();
		}
		else
		{
			if (this.inputStream == null)
			{
				return 0;
			}
			else
			{
				return this.inputStream.getNumberOfReadElements();
			}			
		}
	}
	
	public long getIOTime()
	{
		if (this.useSharedInput)
		{
			return this.sharedInput.getIOTime();
		}
		else
		{
			if (this.inputStream == null)
			{
				return 0;
			}
			else
			{
				return this.inputStream.getIOTime();
			}			
		}
	}

	public int hashCode()
	{
		int h;
		h = this.CID*37 + qNode.hashCode();
		//h = name.hashCode();
		//h = level + name.hashCode() + 11;
		//h = 10;
		//h=level;
		return  h;
	}

	public boolean equals(Object anObject)
	{
		if (this == anObject) {
		    return true;
		}
//		if (anObject instanceof InputWrapper)
//		{
//			InputWrapper anInputWrapper = (InputWrapper)anObject;
//			if (this.QTP.equals(anInputWrapper.QTP) && this.qNode.equals(anInputWrapper.qNode) && (this.CID == anInputWrapper.CID))
//			{
//				return true;
//			}
//		}
		return false;
	}
}
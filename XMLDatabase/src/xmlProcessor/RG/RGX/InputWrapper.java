
package xmlProcessor.RG.RGX;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;


import indexManager.StructuralSummaryNode;
import xmlProcessor.DeweyID;
import xmlProcessor.DBServer.utils.TwinArrayList;
//import xtc.server.xmlSvc.XTCxqueryContext;
//import xtc.server.xmlSvc.xqueryOperators.PathIdxBased.ElementIterator;
import indexManager.Iterators.ElementIterator;
import indexManager.ElementIndex;
import indexManager.IndexManager;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import xmlProcessor.DBServer.DBException;
import xmlProcessor.DBServer.operators.twigs.QueryStatistics;
import xmlProcessor.DBServer.utils.BlockingQueue;
import xmlProcessor.QTP.QTPNode;
import xmlProcessor.QTP.QueryTreePattern;
import xmlProcessor.DBServer.utils.QueryTuple;

/**
 * @author Kamyar
 *
 */
////////////////////////////////////////////////////////////////
///////////////INPUTWRAPPER CLASS///////////////////////////////
///////////////////////////////////////////////////////////////
public class InputWrapper implements QueryStatistics, Runnable
{
	//private Sh inputStreams ;
	private ArrayList<DeweyID> streamHeads;
	private ArrayList<ElementIterator> inputStreams = null;
	private ArrayList<QueryTuple> indrectInput = null;
	private int indirectPos = 0;
	private QTPNode qNode;
//	private Set<Integer> PCRs;
	private long numberOfReadElements = 0; //used for experimental results
	private long IOTime = 0; //used for experimental results
	//private XTCxqueryContext context;
	//private int idxNo;
        private String indexName;
        private IndexManager indxMgr;
        private ElementIndex input;
        private ElementIterator tmpInpStream;
        private BlockingQueue paralQueue;
        //private Thread threadRef;
//	private TwigResult head;
//	private boolean isFinished;
//	private boolean splid;
	private QueryTreePattern QTP;
	//private ArrayList<Integer> ancLevels;
	private ArrayList<DeweyID> addition = new ArrayList<DeweyID>();
	//List of open streams that can be shared for new instances of InputWrapper
	private HashMap<Integer, SharedInput> openedStreams = new HashMap<Integer, SharedInput>();
	private TwinArrayList<SharedInput, Integer> sharedInputs;
	private TwigResult head;
	private boolean finished;
	private ArrayList<TwigResult> subSet;
//	private int level;
//	private int sharedInputIndex;
	private boolean useSharedInput;
	private Set<StructuralSummaryNode> psNodes;
	private SharedInput singleSharedInput = null;
	private int singleCID;
	private boolean hasSingleCID = false;
	private ElementIterator singleInputStreams = null;
	
	
	public InputWrapper(QueryTreePattern QTP, QTPNode qNode, Set<StructuralSummaryNode> psNodes,String indexName, HashMap<Integer, SharedInput> openedStreams, boolean useSharedInput) throws IOException, DBException
	{
//		this.CID = psNodes.getPCR();
		this.psNodes = psNodes;
		this.qNode = qNode;
                this.inputStreams = new ArrayList<ElementIterator>(psNodes.size());
		this.streamHeads = new ArrayList<DeweyID>(psNodes.size());
//		this.context = context;
//		this.idxNo = idxNo;
                this.indexName=indexName;
                this.indxMgr=IndexManager.Instance;
                this.input=indxMgr.openElementIndex(indexName);
                paralQueue=new BlockingQueue();
//		this.isFinished = false;
//		this.head = null;
//		this.splid = splid;
		this.QTP = QTP;
		if (psNodes == null || psNodes.isEmpty())
		{
			this.head = null;
			this.finished = true;
			return;
		}

		this.openedStreams = openedStreams;
		this.useSharedInput = useSharedInput;
		int CID = 0;
		if (this.useSharedInput)
		{
			//FIXME: shareinput mode needs more testing
			System.out.println("shareinput mode needs more testing");
			for (StructuralSummaryNode psNode: psNodes)
			{				
				SharedInput sharedInput;
				CID = psNode.getCID();
				sharedInput = this.openedStreams.get(CID);
				if (sharedInput == null)
				{
					sharedInput = new SharedInput(qNode, CID,indexName);			

					this.openedStreams.put(CID, sharedInput);			
				}
				
				int sharedInputIndex = sharedInput.register(this);
				this.sharedInputs.add(sharedInput, sharedInputIndex);
				
			}
		}
		else
		{
			for (StructuralSummaryNode psNode: psNodes)
			{
				CID = psNode.getCID();
                                tmpInpStream=input.getStream(CID);
                                tmpInpStream.open();//this is NECESSARY!
				this.inputStreams.add(tmpInpStream);
                                //new ElementIterator(context, idxNo, (QTPNodeConstraint)qNode, CID, qNode.getName())
			}
		}
		
		if (psNodes.size()==1)
		{
			this.hasSingleCID = true;
			this.singleCID = CID;
			if (useSharedInput)
			{
				this.singleSharedInput = this.sharedInputs.getL1(0);
			}
			else
			{
				this.singleInputStreams = this.inputStreams.get(0);
			}
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
//		this.level = psNode.getLevel();
	}

	public void close() throws DBException
	{
		// TODO Auto-generated method stub
		for (int i = 0; i < this.inputStreams.size(); )
		{
			this.IOTime += this.inputStreams.get(i).getIOTime();
			this.inputStreams.remove(0);
		}			
	}
	
//	public QTPNode getRightLeaf()
//	{
//		return qNode;
//	}
//
	//private ArrayList<DeweyID> addition = new ArrayList<DeweyID>();
	
	private void internalNext() throws DBException, IOException
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
//		if (this.useSharedInput)
//		{
//			id = this.sharedInput.next(this.sharedInputIndex);
//		}
//		else
//		{
//			QueryTuple tuple = this.inputStream.next();
//			if (tuple!= null)
//			{
//				id = tuple.getIDForPos(0);
//			}
//		}

		if (useSharedInput && hasSingleCID)
		{			
			id = singleSharedInput.next(singleCID);
			if(id == null)
			{
				//this.IOTime += singleSharedInput.getIOTime();
			}
		}
		else if (hasSingleCID)
		{
			QueryTuple tuple =null; 
                        DeweyID nextDID=singleInputStreams.next();
                        if(nextDID!=null)
                            tuple=new QueryTuple(nextDID);
			if (tuple == null)
			{
				//this.IOTime += singleInputStreams.getIOTime();
			}
			else
			{
				id = tuple.getIDForPos(0);
			}
		}
		else
		{
			id = getMinID();
		}
		if (id == null) 
		{
//			this.IOTime += System.currentTimeMillis() - s;
			this.head = null;
			this.finished = true;
			return;
		}
		
		++numberOfReadElements;
//		this.IOTime += System.currentTimeMillis() - s;
		this.head = new TwigResult(QTP, qNode, id);

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
	public void next() throws InterruptedException
        {
            logManager.LogManager.log(5,"InpWrapper: "+this.qNode.toString()+": deques");
            paralQueue.dequeue();
        }
	private DeweyID getMinID() throws DBException, IOException
	{
		if (this.useSharedInput)
		{
			if (this.sharedInputs.size() == 0) return null;

			int min = 0;
			DeweyID minRes = null;
			for (int i = 0; i < this.sharedInputs.size(); i++) 
			{
				DeweyID res = this.streamHeads.get(i);
				try
				{
					if (minRes == null || res.compareTo(minRes) < 0)
					{
						min = i;
						minRes = res;
					}
				}
				catch (Exception e) {
					e.printStackTrace();
					throw new DBException("RG.InputWrapper::getMinID SSSSSSSSS");
				}
			} //for

			//set the head of twigJoinOp which its head is the minimum and should be returned
			SharedInput minStream = sharedInputs.get(min).getO1();
			DeweyID headRes = minStream.next(sharedInputs.get(min).getO2());
			if (headRes != null)
			{
				this.streamHeads.set(min, headRes);			
			}
			else
			{
				this.IOTime += minStream.getIOTime();
				this.inputStreams.remove(min); //inputStreams[min] is finished and should be removed
				this.streamHeads.remove(min);
				//FIXME:  removing and closing shared inputs needs more attention
				System.out.println("removing and closing shared inputs needs more attention");
				if (this.sharedInputs.size() ==1)
				{
					
				}
			}
			return minRes;
			
		}
		else
		{
			if (this.inputStreams.size() == 0) return null;

			int min = 0;
			DeweyID minRes = null;
			for (int i = 0; i < this.inputStreams.size(); i++) 
			{
				DeweyID res = this.streamHeads.get(i);
				try
				{
					if (minRes == null || res.compareTo(minRes) < 0)
					{
						min = i;
						minRes = res;
					}
				}
				catch (Exception e) 
                                {
				      logManager.LogManager.log(minRes.toString());
                                      throw  e;
					//throw new DBException("RG.InputWrapper::getMinID SSSSSSSSS");
				}
			} //for

			//set the head of twigJoinOp which its head is the minimum and should be returned
			ElementIterator minStream = inputStreams.get(min);
                        QueryTuple headRes=null; 
                        DeweyID nextDID=minStream.next();
                         if(nextDID!=null)
                             headRes=new QueryTuple(nextDID);
			if (headRes != null)
			{
				this.streamHeads.set(min, headRes.getIDForPos(0));			
			}
			else
			{
				this.IOTime += minStream.getIOTime();
				this.inputStreams.remove(min); //inputStreams[min] is finished and should be removed
				this.streamHeads.remove(min);
//				if(this.inputStreams.size() == 1)
//				{
//					this.singleInputStreams = this.inputStreams.get(0);
//				}
			}
			return minRes;
		}
	}

	public void advance(int level) throws DBException, IOException, InterruptedException
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
	
	private TwigResult getInternalHead()
	{
		return this.head;
	}
	public TwigResult getHead() throws InterruptedException
        {
            return (TwigResult)paralQueue.peek();                        
        }	
	
	public boolean isFinished()
	{
		//return this.finished;            
            return !paralQueue.hasMoreData();
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
//		ElementIterator minStream = inputStreams.get(min);
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
	
	public void open() throws DBException, IOException, InterruptedException 
	{
                DeweyID nextDID;
                QueryTuple tuple=null;
		if (!isFinished())
		{
			if (this.useSharedInput)
			{
				for (int i = 0; i < this.sharedInputs.size(); ++i)
				{
					SharedInput sharedInput = this.sharedInputs.getL1(i);
					int shareInputIndex = this.sharedInputs.getL2(i);
					sharedInput.open();
					this.streamHeads.add(sharedInput.next(shareInputIndex));
				}
			}
			else
			{
				//QTPNodeConstraint constraint;
		///		for (ElementIterator inputStream: this.inputStreams)
				for(int i=0;i<this.inputStreams.size();i++)
                                {
                                    ElementIterator inputStream;
                                    inputStream=this.inputStreams.get(i);
					inputStream.open();
					if (!hasSingleCID)
					{
                                                nextDID=inputStream.next();
                                                if(nextDID!=null)
                                                    tuple=new QueryTuple(nextDID);
                                                if(tuple!=null)
                                                    this.streamHeads.add(tuple.getIDForPos(0));	
                                                //???
                                                else 
                                                    this.streamHeads.add(null);
					}
				}
			}
			this.internalNext();
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
		return this.numberOfReadElements;
	}
	
	public long getIOTime()
	{
		for (int i = 0; i < this.inputStreams.size(); i++)
		{
			this.IOTime += this.inputStreams.get(i).getIOTime();
		}	
		return this.IOTime;
	}
	public int hashCode()
	{
		int h = 0;
		for (StructuralSummaryNode psNode : this.psNodes)
		{
			h = h+ psNode.getCID() * 37;
		}
		h = h + qNode.hashCode();
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
        
        @Override
        public void run()
        {
            commonSettings.Settings.tCount++;
            logManager.LogManager.log("Thread"+commonSettings.Settings.tCount );
          try {
                
                this.open();
                paralQueue.enqueue(this.getInternalHead());
                
            } catch (DBException ex) {
                    System.err.println("DBException occured in thread EvaluationTreeNode while runing function next()");
                } catch (IOException ex) {
                    System.err.println("IOException occured in thread EvaluationTreeNode while runing function next()");
                } catch (InterruptedException ex) {
                Logger.getLogger(InputWrapper.class.getName()).log(Level.SEVERE, null, ex);
            }
            while(true)
            {
                try {
                    if(this.getInternalHead()==null)
                        break;
                    this.internalNext();
                    paralQueue.enqueue(this.getInternalHead());
                    
                    
                    //logManager.LogManager.log(this.getInternalHead().toString(), 1);
                } catch (DBException ex) {
                    System.err.println("DBException occured in thread SelfInput while runing function next()");
                } catch (IOException ex) {
                    System.err.println("IOException occured in thread SelfInput while runing function next()");
                } 
                    catch (InterruptedException ex) {
                  System.err.println("InterruptedException occured in thread SelfInput while runing function next()");
              }
            }
        }
        
}
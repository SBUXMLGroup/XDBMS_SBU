/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xmlProcessor.RG.RG01;

/**
 * 
 */

import java.util.ArrayList;
import java.util.HashSet;

import xmlProcessor.DBServer.DBException;
import xmlProcessor.DBServer.operators.QueryOperator;
import indexManager.Iterators.ElementIterator;
import indexManager.IndexManager;
import indexManager.ElementIndex;
import java.io.IOException;
import xmlProcessor.DBServer.operators.twigs.QueryStatistics;;
import xmlProcessor.QTP.QTPNode;
import xmlProcessor.QTP.QTPNodeConstraint;
import xmlProcessor.QTP.QueryTreePattern;
import xmlProcessor.DBServer.utils.QueryTuple;
import xmlProcessor.DeweyID;
import statistics.Monitor;

/**
 * @author Kamyar
 *
 */
////////////////////////////////////////////////////////////////
///////////////INPUTWRAPPER CLASS///////////////////////////////
///////////////////////////////////////////////////////////////
public class InputWrapper implements TwigInput,QueryStatistics 
{
	private ArrayList<ElementIterator> inputStreams ;
	private ArrayList<QueryTuple> streamHeads;
//	private ArrayList<QueryTuple> indrectInput = null;
//	private int indirectPos = 0;
	private QTPNode qNode;
	private HashSet<Integer> CIDs;
	private long numberOfReadElements = 0; //used for experimental results
	private long IOTime = 0; //used for experimental results
        private String indexName;
        private IndexManager indxMgr;
//	private boolean splid;
	private QueryTreePattern QTP;
        private Monitor monitor=Monitor.getInstance();
	
	public InputWrapper(QueryTreePattern QTP, QTPNode qNode, HashSet<Integer> CIDs,String indexName)
	{
		this.CIDs = CIDs;
		this.qNode = qNode;
		this.inputStreams = new ArrayList<ElementIterator>(CIDs.size());
		this.streamHeads = new ArrayList<QueryTuple>(CIDs.size());
		this.indexName=indexName;
                this.indxMgr=IndexManager.Instance;
//		this.splid = splid;
		this.QTP = QTP;
	}

	public void close() throws DBException
	{
		// TODO Auto-generated method stub
		for (int i = 0; i < this.inputStreams.size(); i++)
		{
			this.IOTime += this.inputStreams.get(i).getIOTime();
		}

	}
	
	public QTPNode getRightLeaf()
	{
		return qNode;
	}

	//private ArrayList<DeweyID> addition = new ArrayList<DeweyID>();
	
        
	public TwigResult next() throws DBException, IOException 
	{
//		long s = System.currentTimeMillis();
		QueryTuple tuple = null;
		
		tuple =  getMinID();

		if (tuple == null) 
		{
//			this.IOTime += System.currentTimeMillis() - s;
			return null;
		}
		++numberOfReadElements;
//		this.IOTime += System.currentTimeMillis() - s;
		return new TwigResult(QTP, qNode, tuple);
		
	}

	private QueryTuple getMinID() throws DBException, IOException
	{
		if (this.inputStreams.size() == 0) return null;

		int min = 0;
		QueryTuple minRes = null;
		for (int i = 0; i < this.inputStreams.size(); i++) 
		{
			QueryTuple res = this.streamHeads.get(i);
			try
			{
			if (minRes == null || res.getIDForPos(0).compareTo(minRes.getIDForPos(0)) < 0)
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
		ElementIterator minStream = inputStreams.get(min);
                DeweyID nextDeweyID=minStream.next();
                monitor.addElementsRead();
                QueryTuple headRes=null;
                if(nextDeweyID!=null)
                {
                    headRes =new QueryTuple(nextDeweyID);  
                }
		
		if (headRes!= null)
		{
			this.streamHeads.set(min, headRes);			
		}
		else
		{
			this.IOTime += minStream.getIOTime();
			this.inputStreams.remove(min); //inputStreams[min] is finished and should be removed
			this.streamHeads.remove(min);
		}
		return minRes;
		
	}
	
	public void open() throws DBException, IOException 
	{
		QTPNodeConstraint constraint;
                ElementIndex input=indxMgr.openElementIndex(indexName);
                //getStream opens a ssNode containing CID,then NEW s an ElemItrator to 
                //shuld be revised to see if it works correctly.
		for (Integer cid: CIDs)
		{
			ElementIterator inputStream = input.getStream(cid);
                                //new ElementIterator(indexName, qNode, cid, qNode.getName());
			inputStream.open();
                        DeweyID streamRes=inputStream.next();
                        monitor.addElementsRead();
                        QueryTuple streamResCover=null;
                        if(streamRes!=null)
                        {
                            streamResCover=new QueryTuple(streamRes);                            
                        }
			this.streamHeads.add(streamResCover);
			this.inputStreams.add(inputStream);
		}
	}
	
	public long getNumberOfReadElements()
	{
		return numberOfReadElements;
	}
	
	public long getIOTime()
	{
		return IOTime;
	}

}

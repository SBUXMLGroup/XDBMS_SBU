/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xmlProcessor.RG.RG07.EvaluationTree;

import java.util.ArrayList;
import java.util.Set;

import xmlProcessor.DBServer.DBException;
import indexManager.StructuralSummaryNode;
import java.io.IOException;
import xmlProcessor.DBServer.utils.TwinObject;
import xmlProcessor.DBServer.utils.BlockingQueue;
import xmlProcessor.QTP.QTPNode;
import xmlProcessor.RG.RG07.InputWrapper;
import xmlProcessor.RG.RG07.TwigResult;

public class SelfInput implements EvaluationTreeNodeInput
{

	private InputWrapper selfInputWrapper;
//	private BlockingQueue paralQueue;
//        private Thread threadRef;
	public SelfInput(InputWrapper selfInput)
	{
		this.selfInputWrapper = selfInput;
                //paralQueue=new BlockingQueue(this.threadRef);
                //new Thread(this).start();Dangerous!
        }
	@Override
	public ArrayList<EvaluationTreeNodeInput> getChildren()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<TwinObject<QTPNode, Set<StructuralSummaryNode>>> getComps()
	{
		// TODO Auto-generated method stub
		return null;
	}
        @Override
        public TwigResult getHead() throws InterruptedException
        {
            //return (TwigResult)paralQueue.getHead();
            return this.selfInputWrapper.getHead();
        }	
//	private TwigResult getInternalHead() throws InterruptedException
//	{
//		// TODO Auto-generated method stub
//		return this.selfInputWrapper.getHead();
//	}

	@Override
	public QTPNode getMainNode()
	{
		// TODO Auto-generated method stub
		return this.selfInputWrapper.getQTPNode();
	}

	@Override
	public ArrayList<EvaluationTreeNodeInput> getNOTChildren()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isFinished()
	{
		// TODO Auto-generated method stub
		return this.selfInputWrapper.isFinished();
	}

//	private void internalNext() throws DBException, IOException
//	{
//            logManager.LogManager.log("self: internalnext()");
//		// TODO Auto-generated method stub
//		this.selfInputWrapper.internalNext();
//	}
        @Override
        public void next() throws DBException, IOException, InterruptedException
        {
//            //just throws out the older head of queue(cause it has been concumed before),
//            //then head points to the internalNext member of queue
//            //the queue is like this:h1,h2,h3,h4,...
//            //heads prepared in inputwrapper have been pushed into paralQueue.
//            paralQueue.dequeue();
//            //agar void bashe unvaght che balai sare in element i k khundim miad?
        //new IMPLEMENTATION:
            this.selfInputWrapper.next();
        }

	@Override
	public void open() throws DBException, IOException, InterruptedException
	{
		// TODO Auto-generated method stub
		this.selfInputWrapper.open();
	}
	
	@Override
	public void close() throws DBException
	{
		this.selfInputWrapper.close();
	}
	
	public InputWrapper getInputWrapper()
	{
            
		return this.selfInputWrapper;
	}

	public long getIOTime()
	{
		return this.selfInputWrapper.getIOTime();
	}
	
	public long getNumberOfReadElements()
	{
		return this.selfInputWrapper.getNumberOfReadElements();
	}
        
//        @Override
//        public void run()
//        {
//            commonSettings.Settings.tCount++;
//            logManager.LogManager.log("Thread"+commonSettings.Settings.tCount );
//          try {
//                
//                this.open();
//                
//            } catch (DBException ex) {
//                    System.err.println("DBException occured in thread EvaluationTreeNode while runing function internalNext()");
//                } catch (IOException ex) {
//                    System.err.println("IOException occured in thread EvaluationTreeNode while runing function internalNext()");
//                }
//            while(true)
//            {
//                try {
//                    paralQueue.enqueue(this.getInternalHead());
//                    this.internalNext();
//                    if(this.getInternalHead()==null)
//                        break;
//                    
//                    //logManager.LogManager.log(this.getInternalHead().toString(), 1);
//                } catch (DBException ex) {
//                    System.err.println("DBException occured in thread SelfInput while runing function internalNext()");
//                } catch (IOException ex) {
//                    System.err.println("IOException occured in thread SelfInput while runing function internalNext()");
//                } 
//                    catch (InterruptedException ex) {
//                  System.err.println("InterruptedException occured in thread SelfInput while runing function internalNext()");
//              }
//            }
//        }
//        
//        @Override
//        public void start(Thread t)
//        {
//           this.threadRef=t;
//           paralQueue=new BlockingQueue(this.threadRef);
//           t.start();
//        }

}

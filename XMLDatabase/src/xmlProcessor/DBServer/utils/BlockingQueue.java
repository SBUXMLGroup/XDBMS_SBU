
package xmlProcessor.DBServer.utils;

import java.util.LinkedList;
import java.util.Queue;
import xmlProcessor.RG.RG07.EvaluationTree.EvaluationTreeNode;



public class BlockingQueue <T> 
{
   
  private Queue<T> queue=new LinkedList<T>();
  //hatman az add o remove estefade kon na poll o ...
  private int  limit = 10;
  private Object head=null;
  //private  Thread producer;
  private EvaluationTreeNode producer;
   //if we want to have some BlockingQueues:
  //private BlockingQueue[] Instances=new BlockingQueue[5];
  public BlockingQueue()
  {
         
  }
  public BlockingQueue(EvaluationTreeNode producer)
  {
      this.producer=producer;   
  }
//  public BlockingQueue(Thread producer)
//  {
//      this.producer=producer;   
//  }
  
  public BlockingQueue(int limit){
    this.limit = limit;
  }
  public void setProducer(EvaluationTreeNode producer)
  {
      this.producer=producer;
  }
  private boolean isWriterAlive = true;
  
  private synchronized  boolean isWriterAlive()
  {
       return isWriterAlive;
  }
  
  public synchronized boolean hasMoreData()
  {
      return isWriterAlive || this.queue.size()!=0;
  }
  public synchronized void enqueue(T item) throws InterruptedException 
  {
    boolean sw=false;
    while(this.queue.size() == this.limit) {
        sw=true;
        logManager.LogManager.log(8,"enqueue wait: counter:"+testCounter);
        wait();
    }
    if(sw)
        logManager.LogManager.log(8,"enquque wait ended");
    if(this.queue.size() == 0) {
      notifyAll();
    }
//    if(item!=null)
      this.queue.add(item);
//    else
//        isWriterAlive = false;
    if(item==null)
    {
        isWriterAlive = false;
    }
    logManager.LogManager.log(9, "ENQUEUe:" + item+" size: "+this.queue.size());
    //queue2
//    if(this.queue.size()==1)
//        head=queue.get(0);//the head of queue is the first element added to it.
  }


  public synchronized T dequeue()
  throws InterruptedException{
    //while(this.producer.isAlive()&& this.queue.size() == 0){
   while(this.queue.size() == 0)
   {
      logManager.LogManager.log(8,"point11");
      wait();
    }
   // if(!this.producer.isAlive() && this.queue.size() == 0)
    if(this.queue.size() == 0)
          return null;
    if(this.queue.size() == this.limit){
      notifyAll();
    }
    T item = queue.peek();
    logManager.LogManager.log(9, "DEQUE:" + item + "\n");
//    //dequue must chnge head:should throw out or remove older head(first one),and sets next(second) item into head
//    head=this.queue.get(1); //ehshkali nadare ghabla az remove meghdar bdim?
    return this.queue.remove();
  }
  int testCounter=0;
public synchronized T peek() throws InterruptedException
{
    //while(this.producer.isAlive() && this.queue.size() == 0)
    testCounter++;
    boolean sw=false;      
    while(this.queue.size() == 0)
    {
        logManager.LogManager.log(9,"point12");
        sw=true;        
        wait();
    }
    if(sw)
    { 
        //logManager.LogManager.log(8,"wait ended by:"+ this.queue.peek().toString()); 
        sw=false;
    }
    //logManager.LogManager.log(9,this.producer.getMainNode().toString()+" is peeking:");
    if(this.queue.peek()!=null)
      logManager.LogManager.log(9,this.queue.peek().toString());
    else 
        logManager.LogManager.log(9,"peeks null");
    //logManager.LogManager.log(8,"GET HEAD!");
    //if(!this.producer.isAlive() && this.queue.size() == 0)
    if(this.queue.size() == 0)
          return null;
    return this.queue.peek();
}
public synchronized T poll() throws InterruptedException
{
    //while(this.producer.isAlive() && this.queue.size() == 0)
    while(this.queue.size() == 0)
    {
        logManager.LogManager.log(9,"point13");
      wait();
    }
    //if(!this.producer.isAlive() && this.queue.size() == 0)
    if(this.queue.size() == 0)
          return null;
    if(this.queue.size() == this.limit){
      notifyAll();
    }
    T item = queue.peek();
    logManager.LogManager.log(8, "DEQUE:" + item + "\n");
    return this.queue.poll();
}

public synchronized int size()
{
    return this.queue.size();
}
    
}

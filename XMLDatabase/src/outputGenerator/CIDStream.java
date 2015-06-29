/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package outputGenerator;

//import bufferManager.XBufferChannel;
import indexManager.ElementIndex;
//import indexManager.util.OutputWrapper;
import indexManager.ElementBTreeNode;
import indexManager.IndexManager;
import indexManager.Iterators.ElementIterator;
//import indexManager.StructuralSummaryNode;
import java.io.IOException;
import xmlProcessor.DBServer.DBException;
import xmlProcessor.DeweyID;


public class CIDStream 
{
    private IndexManager indxMgr;
    private ElementIterator inputStream;
    private static int testCounter=0;
    public CIDStream()
    {}
    public void open(String indexName) throws IOException, DBException
    {
        indxMgr= IndexManager.Instance;
        ElementIndex input=indxMgr.openElementIndex(indexName);
        //getStream opens a ssNode containing CID,then NEWs an ElemItrator to 
        //we got 22 separate CIDs:
        //XBufferChannel xbuf=bufferManager.BufferManager.getInstance().getPage(4);
//        ElementBTreeNode elem=new ElementBTreeNode(4, indexName);
//        do{
//        OutputWrapper o=elem.next(1);
//        }while(OutputWrapper!=null);
//        for(int i=1;i<=32;i++)
//        {
//            inputStream =input.getStream(i);
//            inputStream.open();
//            DeweyID next=inputStream.next();
//            while(next!=null)
//            {
//                System.out.println(next);
//                System.out.println(next.getCID());
//                next=inputStream.next();
//            }
//        }
 ///********Complete Iteration:
        inputStream=input.getStream();
        inputStream.open();
        DeweyID next =next=inputStream.next();
        DeweyID prev = null;
        boolean flag=false;
        do{
            if(flag)
                prev=next;
             
            if(flag)
                if(prev.getCID()==next.getCID() && !(prev.notGreaterThan(next)))
                    System.err.println("ERROR: Previous: "+prev.getCID()+":"+prev.toString()
                            + " Next: "+next.getCID()+":"+next.toString());
            flag=true;
            
            //logManager.LogManager.log(6,":"+testCounter++);
            //System.out.println(next);
            System.out.println(next.getCID());
            next=inputStream.next();
        }while(next!=null);
                
 ////////****************************************
//        inputStream=input.getStream(537);
//        inputStream.open();
//        DeweyID next;
//        do{
//            next=inputStream.next();
//            testCounter++;
//            //logManager.LogManager.log(6,"Rec:"+testCounter);
//            System.out.println(next);
//            System.out.println(next.getCID());
//        }while(next!=null);
//                System.out.println(next.getCID());
    }
    public void close(String indexName) throws IOException
    {
        //indxMgr.closeIndexes(indexName);
    }
}

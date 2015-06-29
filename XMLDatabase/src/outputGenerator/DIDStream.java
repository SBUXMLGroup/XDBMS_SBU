/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package outputGenerator;

//import bufferManager.XBufferChannel;
import indexManager.BTreeIndex;
import indexManager.ElementIndex;
//import indexManager.util.OutputWrapper;
import indexManager.BTreeNode;
import indexManager.IndexManager;
import indexManager.Iterators.BTreeIterator;
//import indexManager.StructuralSummaryNode;
import java.io.IOException;
import xmlProcessor.DBServer.DBException;
import xmlProcessor.DeweyID;
import indexManager.util.Record;

public class DIDStream 
{

    private IndexManager indxMgr;
    private BTreeIterator inputStream;
    private static int testCounter=0;
    public DIDStream()
    {}
    public void open(String indexName) throws IOException, DBException
    {
        indxMgr= IndexManager.Instance;
        BTreeIndex input=indxMgr.openBTreeIndex(indexName);
 
 ///********Complete Iteration:
        inputStream=input.getStream();
        inputStream.open();
        DeweyID next = null;
        Record nextRec=inputStream.next();
        DeweyID prev = null;
        boolean flag=false;
        do{
            if(flag)
                prev=next;            
            next=nextRec.getDeweyID();
            if(flag)
                if(!(prev.notGreaterThan(next)))
                    System.err.println("ERROR: Previous: "+prev.toString()
                            + " Next: "+next.toString());
            flag=true;
            
            //logManager.LogManager.log(6,":"+testCounter++);
            //System.out.println(next);
            System.out.println(next.toString());
            nextRec=inputStream.next(); 
        }while(nextRec!=null);
                
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

    


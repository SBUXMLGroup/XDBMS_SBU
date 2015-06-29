package documentManager;

import bufferManager.InternalBuffer;
import bufferManager.BufferManager;
import commonSettings.Settings;
import xmlProcessor.DeweyID;
import indexManager.IndexManager;
import indexManager.BTreeIndex;
import indexManager.ElementIndex;
import indexManager.StructuralSummaryIndex;
import indexManager.StructuralSummaryNode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import xmlProcessor.DBServer.DBException;

public class Document {

    private String docName;//haman db name
    private BTreeIndex bTreeIndex;
    private ElementIndex elemIndex;
    private StructuralSummaryIndex structSumIndex;
    private DocumentManager docMgr;
    private BufferManager bufMgr=BufferManager.getInstance();
    //IndexManager indexMgr;
    private List<DeweyID> Deweylist;
    /**
    public Document(String fileName, String indexName ,BTreeIndex bTreeIndex) throws IOException 
    {
        this.fileName = fileName;
        this.indexName = indexName;
        this.bTreeIndex=bTreeIndex;
        Deweylist = new ArrayList<DeweyID>();

    }  */  
    public Document(String docName ,BTreeIndex bTreeIndex, StructuralSummaryIndex ssIndex,ElementIndex elementIndex) throws IOException 
    {
        
        //this.indexName = indexName;
        this.docName = docName;
        docMgr=DocumentManager.Instance;
        this.bTreeIndex=bTreeIndex;
        this.elemIndex=elementIndex;
        this.structSumIndex=ssIndex;
        Deweylist = new ArrayList<DeweyID>();

    }
//    public Document(String docName ,BTreeIndex bTreeIndex) throws IOException 
//    {
//        this.docName = docName;
//        this.bTreeIndex=bTreeIndex;
//        Deweylist = new ArrayList<DeweyID>();
//
//    }
    
     public List getDeweyList()
    {
        return Deweylist;
    }

   /* public void addElement(DeweyID deweyId) throws IOException, Exception 
    {
        
          bTreeIndex.insert(deweyId);   
          
    }*/
     public HashMap checkBufPool()
     {
         logManager.LogManager.log(0,"Checking bufPool status");
        return bufMgr.getPoolStatus();
     }
    public void addElement(DeweyID deweyID,String qName,int parentCID,boolean qNameFound) throws IOException, Exception 
    {
        InternalBuffer buffer;         
         // structSumIndex.insert(CID,qName,Parent) //mishhe in karo akhar kard
        int CID=deweyID.getCID();
 
        logManager.LogManager.log(6,qName);
        logManager.LogManager.log(6,deweyID.toString());
        logManager.LogManager.log(6,String.valueOf(CID));

        elemIndex.insert(CID,deweyID);          
        bTreeIndex.insert(deweyID,CID,qName);
        //logManager.LogManager.log(5,deweyID.toString());
        //logManager.LogManager.log(5,"CID: "+CID);
        if(!qNameFound)
            structSumIndex.add(CID,qName,parentCID); 
    //checkBufPool();
    }
    
     public void addAttribute(DeweyID deweyID,String qName,int parentCID,boolean qNameFound) throws IOException, Exception
     {
         //parentCID is curNode's CID
        logManager.LogManager.log(6,qName);
        logManager.LogManager.log(6,"Attrib: "+deweyID.toString());
        logManager.LogManager.log(6,String.valueOf(deweyID.getCID()));
//        }
         elemIndex.insert(deweyID.getCID(),deweyID);   
         bTreeIndex.insert(deweyID,deweyID.getCID(),qName);
         if(!qNameFound)
            structSumIndex.add(deweyID.getCID(),qName,parentCID);
         //checkBufPool();
     }
     public void addValue(DeweyID deweyID,String value,int parentCID) throws IOException, Exception
     {
        //FOR ATTrib Values (Actually they are behaved the same as text contents)
         //text contents are not added to element index,thier CID shouldn be Zero 
         //elemIndex.insert(0,deweyID);
         bTreeIndex.insert(deweyID,-1,value);
        logManager.LogManager.log(6,value);
        logManager.LogManager.log(6,"Value: "+deweyID.toString());
       

         // structSumIndex.add(-1,value,parentCID);THIS IS NOT NEEDED!
         //checkBufPool();
     }
     public void addContent(DeweyID deweyID,StringBuilder contentBuilder,int parentCID) throws IOException, Exception
     {
         String content=new String(contentBuilder);
         //text contents are not added to element index,thier CID shouldn be Zero 
         //elemIndex.insert(0,deweyID);
         bTreeIndex.insert(deweyID,-2,content);
         System.out.println("CID: -2");
         //structSumIndex.add(-2,content,parentCID);THIS IS NOT NEEDED
         //checkBufPool();
     }
     public void endOfDocSignal() throws IOException, DBException
     {
         structSumIndex.endDoc();
         docMgr.closeDoc(this.docName);
         
     }
}

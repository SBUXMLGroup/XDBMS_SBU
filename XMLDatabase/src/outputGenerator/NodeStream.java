package outputGenerator;

import indexManager.BTreeIndex;
import indexManager.IndexManager;
import bufferManager.BufferManager;
import bufferManager.InternalBuffer;
import bufferManager.XBufferChannel;
import commonSettings.Settings;
import indexManager.BTreeNode;
import java.io.IOException;
import java.util.ArrayList;
import xmlProcessor.DBServer.DBException;
import xmlProcessor.DeweyID;


public class NodeStream
{
    private IndexManager indxMgr;
    private BufferManager bufMgr;
    private BTreeIndex input;
    public NodeStream()
    {
        bufMgr=BufferManager.getInstance();
    }
    public void open(String indexName) throws IOException, DBException
    {
        indxMgr= IndexManager.Instance;
        input=indxMgr.openBTreeIndex(indexName);
//        traverse();
        read(input.getRootP());
    }
//    public int traverse()
//    {
//        
//        int nextPagePointer;
//        boolean keyFound = false;
//        BTreeNode bTreeNode;        
//        BTreeNode rootNode=input.getRoot();
//        int rootPointer=input.getRootP();
//        //inja bayad parents empty bashe.
//        if (rootNode.IsLeaf())       
//        {  
//               return read(rootPointer);//this is erroneos           
//        }
//        else 
//             nextPagePointer = ;           
//       
//        while(true)
//        {
//          //buf=bufMngr.getPage(nextPagePointer);
//          bTreeNode=new BTreeNode(nextPagePointer,indexName);
//          if(!(bTreeNode.IsLeaf()))
//              nextPagePointer=bTreeNode.internalNodeSearch(deweyID,CID,content);
//          else{
//              keyFound=bTreeNode.leafNodeSearch(deweyID,CID,content);
//              break;
//          }
//        }
//          if(keyFound)
//              return nextPagePointer; //adrese barg havie in key
//          else
//              return -1;
//                               
//    }
    public ArrayList read(int pageNo) throws IOException
    {
        XBufferChannel xBuf;
        DeweyID curDID;
        int curCID;
        int recLength;
        int strLength;
        byte[]dSeq;
        byte[]qSeq;
        String qStr;
        xBuf=input.getRoot().getBuf();
        xBuf.position(Settings.nextPagePos);
        int nextPage=xBuf.getInt();
        int freeOffset=xBuf.getInt();
        byte isRoot=xBuf.get();
        byte isLeaf=xBuf.get();
        int numOfKeys=xBuf.getInt();
        int pagePointer;
        ArrayList<Integer> pagePointerList=new ArrayList();        
        System.out.println("next page is: "+nextPage);
        System.out.println("free offset is: "+ freeOffset);
        System.out.println(numOfKeys+" keys are stored in this page.");
        
        
        if(isRoot==1 && isLeaf==0)
        {
            System.out.println("this is ROOT page ");
          for(int i=0;i<numOfKeys;i++)
          {
              pagePointer=xBuf.getInt();
              pagePointerList.add(pagePointer);
              recLength=xBuf.getInt();
              strLength=xBuf.getInt();
              curCID=xBuf.getInt();
              dSeq=new byte[recLength-Settings.sizeOfInt];
              xBuf.get(dSeq,0,dSeq.length);
              curDID=new DeweyID();
              curDID.setDeweyId(dSeq);
              qSeq=new byte[strLength];
              xBuf.get(qSeq, 0, strLength);
              qStr=new String(qSeq);
              System.out.println("The node with qname: "+qStr+" is labeled: "+ curDID.toString()+ " belongs to class: "+ curCID+ " is the maximum of page: "+pagePointer+" .");
              
          }
        }
        return pagePointerList;
    }
    public void close(String indexName) throws IOException
    {
        //indxMgr.closeIndexes(indexName);
    }
}

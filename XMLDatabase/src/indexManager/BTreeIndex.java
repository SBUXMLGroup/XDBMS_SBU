
package indexManager;
import bufferManager.BufferManager;
import indexManager.Iterators.BTreeIterator;
import java.io.IOException;
import java.util.Stack;
import java.util.ArrayList;
import xmlProcessor.DBServer.DBException;
import xmlProcessor.DeweyID;

public class BTreeIndex 
{
    private BufferManager bufMngr;
    private int rootPointer;
    private BTreeNode rootNode;
    private String indexName;
    public Stack parents;
    public BTreeIndex(String indexName) throws IOException, DBException
    {
        //for creating BtreeIndex
        rootNode=new BTreeNode(indexName,(byte)1,(byte)1,0);
        //since createBNode adds a page to index,the first initial page of the index is empty.
        rootPointer=rootNode.createBNode();
        //rootNode.loadBTreeNode(rootPointer, indexName);
        bufMngr=BufferManager.getInstance();
        this.indexName=indexName;
        this.parents=new Stack();
    }
    
    public BTreeIndex (String indexName,int rootPointer) throws IOException, DBException
    {
        //this constructor is used to OPEN a btree index with the present root page in disk
        this.rootPointer=rootPointer;
        rootNode=new BTreeNode(rootPointer,indexName);
        bufMngr=BufferManager.getInstance();
        this.indexName=indexName;
        this.parents=new Stack();
               
    }
    public BTreeNode getRoot()
    {
        return rootNode;
    }
    public int getRootP()
    {
        return rootPointer;        
    }
      
    public int closeBTreeIndex() throws IOException
    {
       // bufMngr.unpinAll(null);
        System.out.println("BTree ROOT P: "+rootPointer);
        return rootPointer;        
    }
    public String getIndexName()
    {
        return this.indexName;
    }
    
    public void insert(DeweyID deweyID,int CID,String content) throws IOException, Exception
    {
        //age bkhaym tu y bauf chand ta ky insert konim chi?
        //srch mikonim ta beresim b barg dar unja insert mikonim
        //InternalBuffer rootBuf;  
        //InternalBuffer buf;
        int nextPagePointer;
        boolean keyFound = false;

        parents.clear();
        // BTreeNode rootNode;
        BTreeNode bTreeNode;
        rootNode.loadBTreeNode(rootPointer, indexName);
        
        //inja bayad parents empty bashe.
        if (rootNode.IsLeaf()) 
        {
            if (!(rootNode.leafNodeSearch(deweyID,CID,content))) 
            {
                rootPointer = rootNode.insertIntoLeaf(deweyID,CID,content,rootPointer,parents);
                rootNode.closeBTreeNode();
            }
            //else print:deweyID already exists in the tree
        } 
        else 
        {
            nextPagePointer = rootNode.internalNodeSearch(deweyID,CID,content);
            rootNode.closeBTreeNode();
            parents.push(rootPointer);
            while (true) {
                //buf = bufMngr.getPage(nextPagePointer);
                bTreeNode = new BTreeNode(nextPagePointer, indexName);
                if (!(bTreeNode.IsLeaf())) 
                {
                    parents.push(nextPagePointer);
                    nextPagePointer = bTreeNode.internalNodeSearch(deweyID,CID,content);                    
                    bTreeNode.closeBTreeNode();
                } 
                else 
                {
                    if (!(bTreeNode.leafNodeSearch(deweyID,CID,content))) 
                    {
                        rootPointer = bTreeNode.insertIntoLeaf(deweyID,CID,content,rootPointer,parents);
                        bTreeNode.closeBTreeNode();
                    } 
                    else
                    {
//                        bTreeNode.closeBTreeNode();
                        throw new Exception("dddd");//yek odam bp khce clas exyad extend konam
                    }
                    break;
                }
            }
        }
        
    }
    
    { /*public void insert(DeweyID deweyID,int CID,StringBuilder qName) throws IOException
    {
        //age bkhaym tu y bauf chand ta ky insert konim chi?
        //srch mikonim ta beresim b barg dar unja insert mikonim
        //InternalBuffer rootBuf;  
        //InternalBuffer buf;
        int nextPagePointer;
        boolean keyFound = false;

        parents.clear();
        // BTreeNode rootNode;
        BTreeNode bTreeNode;
        rootNode.loadBTreeNode(rootPointer, indexName);
        //inja bayad parents empty bashe.
        if (rootNode.IsLeaf()) 
        {
            if (!(rootNode.leafNodeSearch(deweyID,CID,qName))) 
            {
                rootPointer = rootNode.insertIntoLeaf(deweyID,qName,rootPointer,parents);
            }
            //else print:deweyID already exists in the tree
        } 
        else 
        {
            nextPagePointer = rootNode.internalNodeSearch(deweyID,CID,qName);
            parents.push(rootPointer);
            while (true) {
                //buf = bufMngr.getPage(nextPagePointer);
                bTreeNode = new BTreeNode(nextPagePointer, indexName);
                if (!(bTreeNode.IsLeaf())) 
                {
                    parents.push(nextPagePointer);
                    nextPagePointer = bTreeNode.internalNodeSearch(deweyID,CID,qName);
                    
                } else {
                    if (!(bTreeNode.leafNodeSearch(deweyID,CID,qName))) 
                    {
                        rootPointer = bTreeNode.insertIntoLeaf(deweyID,qName, rootPointer, parents);
                    } else {
                        throw new Exception("dddd");//yek odam bp khce clas exyad extend konam
                    }
                    break;
                }
            }
        }
    }*/
   
    /* public void insert(DeweyID deweyID,int CID,String qName) throws IOException, Exception
    {
        //age bkhaym tu y bauf chand ta ky insert konim chi?
        //srch mikonim ta beresim b barg dar unja insert mikonim
        //InternalBuffer rootBuf;  
        //InternalBuffer buf;
        int nextPagePointer;
        boolean keyFound = false;

        parents.clear();
        // BTreeNode rootNode;
        BTreeNode bTreeNode;
        rootNode.loadBTreeNode(rootPointer, indexName);
        //inja bayad parents empty bashe.
        if (rootNode.IsLeaf()) 
        {
            if (!(rootNode.leafNodeSearch(deweyID,CID,qName))) 
            {
                rootPointer = rootNode.insertIntoLeaf(deweyID, rootPointer, parents);
            }
            //else print:deweyID already exists in the tree
        } 
        else 
        {
            nextPagePointer = rootNode.internalNodeSearch(deweyID,CID,qName);
            parents.push(rootPointer);
            while (true) {
                //buf = bufMngr.getPage(nextPagePointer);
                bTreeNode = new BTreeNode(nextPagePointer, indexName);
                if (!(bTreeNode.IsLeaf())) 
                {
                    parents.push(nextPagePointer);
                    nextPagePointer = bTreeNode.internalNodeSearch(deweyID,CID,qName);
                    
                } else {
                    if (!(bTreeNode.leafNodeSearch(deweyID,CID,qName))) {
                        rootPointer = bTreeNode.insertIntoLeaf(deweyID, rootPointer, parents);
                    } else {
                        throw new Exception("dddd");//yek odam bp khce clas exyad extend konam
                    }
                    break;
                }
            }
        }
    } */}
     public int search(DeweyID deweyID,int CID,String content) throws IOException, DBException 
    {
        //InternalBuffer rootBuf;  
        //InternalBuffer buf;
        int nextPagePointer;
        boolean keyFound = false;
        BTreeNode bTreeNode;
        //rootBuf=bufMngr.getPage(rootPointer);
        rootNode.loadBTreeNode(rootPointer, indexName);
        //inja bayad parents empty bashe.
        if (rootNode.IsLeaf())       
        {  
            if(rootNode.leafNodeSearch(deweyID,CID,content))
            {   
                rootNode.closeBTreeNode();
                return rootPointer; 
            
            } 
           else return -1;
        }
        else 
        {
             nextPagePointer = rootNode.internalNodeSearch(deweyID,CID,content); 
             rootNode.closeBTreeNode();
        }
       //age dare key vagharna esharegar        
        //page: pagestate,nextpage,freeoffset
        //ps,isroot,isleaf,number of keys in this page,freeoff,nextp(leftmost),deweyId,nextp,deweyId,nextp(rightmost)
        while(true)
        {
          //buf=bufMngr.getPage(nextPagePointer);
          bTreeNode=new BTreeNode(nextPagePointer,indexName);
          if(!(bTreeNode.IsLeaf()))
          {
              nextPagePointer=bTreeNode.internalNodeSearch(deweyID,CID,content);
              bTreeNode.closeBTreeNode();
          }
          else
          {
              keyFound=bTreeNode.leafNodeSearch(deweyID,CID,content);
              bTreeNode.closeBTreeNode();
              break;
          }
        }
          if(keyFound)
              return nextPagePointer; //adrese barg havie in key
          else
              return -1;
                               
        }
     
      public int search(DeweyID deweyID) throws IOException, DBException 
    {
        int nextPagePointer;
        boolean keyFound = false;
        BTreeNode bTreeNode;
        //rootBuf=bufMngr.getPage(rootPointer);
        rootNode.loadBTreeNode(rootPointer, indexName);
        //inja bayad parents empty bashe.
        if (rootNode.IsLeaf())       
        {  
            if(rootNode.leafNodeSearch(deweyID))
            {   
                rootNode.closeBTreeNode();
                return rootPointer; 
            
            } 
           else return -1;
        }
        else 
        {
             nextPagePointer = rootNode.internalNodeSearch(deweyID); 
             rootNode.closeBTreeNode();
        }
       //age dare key vagharna esharegar        
        //page: pagestate,nextpage,freeoffset
        //ps,isroot,isleaf,number of keys in this page,freeoff,nextp(leftmost),deweyId,nextp,deweyId,nextp(rightmost)
        while(true)
        {
          //buf=bufMngr.getPage(nextPagePointer);
          bTreeNode=new BTreeNode(nextPagePointer,indexName);
          if(!(bTreeNode.IsLeaf()))
          {
              nextPagePointer=bTreeNode.internalNodeSearch(deweyID);
              bTreeNode.closeBTreeNode();
          }
          else
          {
              keyFound=bTreeNode.leafNodeSearch(deweyID);
              bTreeNode.closeBTreeNode();
              break;
          }
        }
          if(keyFound)
              return nextPagePointer; //leaf node page containing key is returned
          else
              return -1;
                               
        }
     public void scanBTIndex()
     {
         
     }
     
     public BTreeNode open() throws IOException, DBException
     {
       /**returns first leave of the tree: So it has to find deweyId=1;*/
         ArrayList<Integer> firstDIDSeq=new ArrayList<>();
         firstDIDSeq.add(1);
         DeweyID firstDID=new DeweyID(firstDIDSeq);
         int firstLeave=search(firstDID);
         BTreeNode container=new BTreeNode(firstLeave, indexName);
         return container; 
         
     }
     public BTreeIterator getStream() throws IOException, DBException
     {
         BTreeNode container=this.open();
         BTreeIterator bTStream=new BTreeIterator(container);
         return bTStream;
     }
     
    
     
}


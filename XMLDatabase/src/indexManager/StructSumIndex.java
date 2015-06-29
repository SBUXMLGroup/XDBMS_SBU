//
//package indexManager;
//
//import bufferManager.BufferManager;
//import java.io.IOException;
//import java.util.Stack;
//import xmlProcessor.DeweyID;
//
//public class StructSumIndex
//{
//    private BufferManager bufMngr;
//    private int rootPointer;
//    private BTreeNode rootNode;
//    private String indexName;
//    public Stack parents;
//    public StructSumIndex(String indexName) throws IOException
//    {
//        rootNode=new BTreeNode(indexName,(byte)1,(byte)1,0);
//        rootPointer=rootNode.createBNode();
//        rootNode.loadBTreeNode(rootPointer, indexName);
//        bufMngr=BufferManager.getInstance();
//        this.indexName=indexName;
//        this.parents=new Stack();
//    }
//    
//    public StructSumIndex(int rootPageNum,String indexName) throws IOException
//    {
//        this.indexName=indexName;
//        rootNode=new BTreeNode(indexName,(byte)1,(byte)1,0);
//        rootPointer=rootPageNum;
//        rootNode.updateBNode(rootPageNum);
//        //rootNode.loadBTreeNode(rootPointer, indexName);
//        bufMngr=BufferManager.getInstance();
//        this.parents=new Stack();
//    }
//   //here KEY is CID
//    public void insert(DeweyID key,String qName) throws IOException, Exception
//    {
//        //age bkhaym tu y bauf chand ta ky insert konim chi?
//        //srch mikonim ta beresim b barg dar unja insert mikonim
//        //InternalBuffer rootBuf;  
//        //InternalBuffer buf;
//        int nextPagePointer;
//        boolean keyFound = false;
//        parents.clear();
//        // BTreeNode rootNode;
//        BTreeNode bTreeNode;
//        rootNode.loadBTreeNode(rootPointer, indexName);
//        //inja bayad parents empty bashe.
//        if (rootNode.IsLeaf()) 
//        {
//            if (!(rootNode.leafNodeSearch(key))) 
//            {
//                rootPointer = rootNode.insertIntoLeaf(key,qName,rootPointer, parents);
//            }
//            //else print:key already exists in the tree
//        } 
//        else 
//        {
//            nextPagePointer = rootNode.internalNodeSearch(key);
//            parents.push(rootPointer);
//            while (true) {
//                //buf = bufMngr.getPage(nextPagePointer);
//                bTreeNode = new BTreeNode(nextPagePointer, indexName);
//                if (!(bTreeNode.IsLeaf())) 
//                {
//                    parents.push(nextPagePointer);
//                    nextPagePointer = bTreeNode.internalNodeSearch(key);
//                    
//                } else {
//                    if (!(bTreeNode.leafNodeSearch(key))) {
//                        rootPointer = bTreeNode.insertIntoLeaf(key,qName, rootPointer, parents);
//                    } else {
//                        throw new Exception("dddd");//yek odam bp khce clas exyad extend konam
//                    }
//                    break;
//                }
//            }
//        }
//    }
//    
//     public int search(DeweyID key,String qName) throws IOException 
//    {
//        //InternalBuffer rootBuf;  
//        //InternalBuffer buf;
//        int nextPagePointer;
//        String keyFound = false;
//        BTreeNode rootNode;
//        BTreeNode bTreeNode;
//        //rootBuf=bufMngr.getPage(rootPointer);
//        rootNode=new BTreeNode(rootPointer,indexName);
//        nextPagePointer=rootNode.internalNodeSearch(key);
//       //age dare key vagharna esharegar
//        
//        //page: pagestate,nextpage,freeoffset
//        //ps,isroot,isleaf,number of keys in this page,freeoff,nextp(leftmost),deweyId,nextp,deweyId,nextp(rightmost)
//        while(true)
//        {
//          //buf=bufMngr.getPage(nextPagePointer);
//          bTreeNode=new BTreeNode(nextPagePointer,indexName);
//          if(!(bTreeNode.IsLeaf()))
//              nextPagePointer=bTreeNode.internalNodeSearch(key);
//          else{
//              keyFound=bTreeNode.qNameSearch(key);
//              break;
//          }
//        }
//          if(!(keyFound))
//              return keyFound; //adrese barg havie in key
//          else
//              return null;
//                               
//        }
//}

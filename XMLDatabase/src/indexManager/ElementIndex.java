package indexManager;

import bufferManager.BufferManager;
import bufferManager.XBufferChannel;
import commonSettings.Settings;
import java.io.IOException;
import java.util.Stack;
import java.util.Queue;
import java.util.LinkedList;
import xmlProcessor.DeweyID;
import indexManager.Iterators.ElementIterator;
import java.util.ArrayList;
import xmlProcessor.DBServer.DBException;

public class ElementIndex {

    private BufferManager bufMngr;
    private int rootPointer; //khase khodash
    private ElementBTreeNode rootNode;
    private String indexName;
    public Stack parents;
    private ArrayList<Integer> pinnedPages;
    int lastRootP = -1;
    int testCounter=0;
    public ElementIndex(String indexName) throws IOException, DBException {
        //creates ElementIndex
        rootNode = new ElementBTreeNode(indexName, (byte) 1, (byte) 1, 0);
        rootPointer = rootNode.createElementBNode();
        //rootNode.loadElementBTreeNode(rootPointer, indexName);
        bufMngr = BufferManager.getInstance();
        this.indexName = indexName;
        this.parents = new Stack();
        this.pinnedPages = new ArrayList<>();
        pinnedPages.add(rootPointer);

    }

    public ElementIndex(String indexName, int rootPointer) throws IOException, DBException {
        //this constructor is used to OPEN an Element index with the present root page in disk
        this.rootPointer = rootPointer;
        rootNode = new ElementBTreeNode(rootPointer, indexName);
        bufMngr = BufferManager.getInstance();
        this.indexName = indexName;
        this.parents = new Stack();
        this.pinnedPages = new ArrayList();
        pinnedPages.add(rootPointer);
    }

    public XBufferChannel export() {
        return rootNode.getBuf();

    }

    public int closeElementIndex() throws IOException {
        //bayd hameye page haye pin shode unpin shan.
        //this.indexName=indexName;
        //rootNode=new ElementBTreeNode(indexName,(byte)1,(byte)1,0);
        //rootPointer=rootPageNum;
        // rootNode.updateElementBNode(rootPageNum);
        //rootNode.loadBTreeNode(rootPointer, indexName); 
        //PINNED PAGES SHOULD ONLY CONTAIN ONE SINGLE ELEMENT:
        System.out.println("Element ROOT P: " + rootPointer);
        bufMngr.unpinAll(pinnedPages);
        return rootPointer;
    }

    public String getIndexName() {
        return this.indexName;
    }

    public int search(int CID, DeweyID deweyID) throws IOException, DBException {
        //search should be done based on CID
        int nextPagePointer;
        boolean keyFound = false;
        ElementBTreeNode elemBTreeNode;
        if (rootNode.IsLeaf()) {
            if (rootNode.leafElementSearch(CID, deweyID)) {
                rootNode.closeElementNode();
                return rootPointer;
            } else {
                return -1;
            }
        } else {
            nextPagePointer = rootNode.internalElementSearch(CID, deweyID);
            rootNode.closeElementNode();
        //page: pagestate,nextpage,freeoffset
            //ps,isroot,isleaf,number of keys in this page,freeoff,nextp(leftmost),deweyId,nextp,deweyId,nextp(rightmost)
            while (true) {
                //buf=bufMngr.getPage(nextPagePointer);
                elemBTreeNode = new ElementBTreeNode(nextPagePointer, indexName);
                //pinnedPages.add(nextPagePointer);
                if (!(elemBTreeNode.IsLeaf())) {
                    nextPagePointer = elemBTreeNode.internalElementSearch(CID, deweyID);
                    elemBTreeNode.closeElementNode();

                } else {
                    if (elemBTreeNode.leafElementSearch(CID, deweyID)) {
                        elemBTreeNode.closeElementNode();
                        return nextPagePointer;
                    } else {
                        elemBTreeNode.closeElementNode();
                        return -1;
                    }
                }
            }
        }
    }

    public void insert(int CID, DeweyID deweyID) throws IOException, Exception {
        int nextPagePointer;
        boolean keyFound = false;
//        if(CID==32 || CID==40)
//        {   
//            testCounter++;
//            logManager.LogManager.log(6,CID+"is Being Inserted.");
//            
//        }
        parents.clear();
        // BTreeNode rootNode;
        ElementBTreeNode elementBTreeNode;
        if (lastRootP != rootPointer) {
            System.err.println("ELEMENT ROOT : " + rootPointer);
        }
        lastRootP = rootPointer;
        rootNode.loadElementBTreeNode(rootPointer, indexName);
        //inja bayad parents empty bashe.
        if (rootNode.IsLeaf()) {
            if (!(rootNode.leafElementSearch(CID, deweyID))) {
                rootPointer = rootNode.insertIntoLeafElement(CID, deweyID, rootPointer, parents);
                rootNode.closeElementNode();
//                bufMngr.unpinBuffer(rootNode.getBuf(),true);
                // pinnedPages.add(nextPagePointer);
            }
            //else print:key already exists in the tree
        } else {
            nextPagePointer = rootNode.internalElementSearch(CID, deweyID);
            rootNode.closeElementNode();
            //pinnedPages.add(nextPagePointer);
            parents.push(rootPointer);
            while (true) {
                //buf = bufMngr.getPage(nextPagePointer);
                elementBTreeNode = new ElementBTreeNode(nextPagePointer, indexName);
                if (!(elementBTreeNode.IsLeaf())) {
                    parents.push(nextPagePointer);
                    nextPagePointer = elementBTreeNode.internalElementSearch(CID, deweyID);
                    elementBTreeNode.closeElementNode();
                    //pinnedPages.add(nextPagePointer);

                } else {
                    if (!(elementBTreeNode.leafElementSearch(CID, deweyID))) {
                        rootPointer = elementBTreeNode.insertIntoLeafElement(CID, deweyID, rootPointer, parents);
                        elementBTreeNode.closeElementNode();
                    } else {
                        elementBTreeNode.closeElementNode();
                        throw new Exception("dddd");//yek odam bp khce clas exyad extend konam
                    }
                    break;
                }
            }
        }
    }
     ///--------------------------------------------------------------

    /* public ElementIterator getDeweyID(int CID)
     {
     return new ElementIterator(this);
     }
     // private void getNextDeweyID(int CID){}
     public ElementIndex getElementIndex(String indexName)
     {
     return this;//y jurriiiiiii
     //singleton?
     }*/
    public ElementBTreeNodeWrapper scan(int CID) throws IOException, DBException {
        /**
         * search should be done based on CID looks for a CID in
         * ElementIndex,finds its containing ElementBTreeNode and then finds its
         * position then wraps the Node and the position into an object
         */
        int nextPagePointer;
        boolean keyFound = false;
        ElementBTreeNode elemBTreeNode;
        ElementBTreeNodeWrapper wrapper;
        int recPosition;
        rootNode.loadElementBTreeNode(rootPointer, indexName);        
        //inja bayad parents empty bashe.
        if (rootNode.IsLeaf()) {
            //pedarashun miaim pain bayd unpin she.
            //offset ro bargardune.
            // 3)y obj ezafi ham cid ham offs,ham hata buffer
            recPosition = rootNode.leafElementSearch(CID);            
            if (recPosition != -1) {
                wrapper = new ElementBTreeNodeWrapper(rootNode, recPosition);
                return wrapper;
            } else {
                rootNode.closeElementNode();
                return null;
            }
        } else {
            nextPagePointer = rootNode.internalElementSearch(CID);
            rootNode.closeElementNode();
        //page: pagestate,nextpage,freeoffset
            //ps,isroot,isleaf,number of keys in this page,freeoff,nextp(leftmost),deweyId,nextp,deweyId,nextp(rightmost)
            while (true) {
                //buf=bufMngr.getPage(nextPagePointer);
                elemBTreeNode = new ElementBTreeNode(nextPagePointer, indexName);
                if (!(elemBTreeNode.IsLeaf())) {
                    nextPagePointer = elemBTreeNode.internalElementSearch(CID);
                    elemBTreeNode.closeElementNode();
                } else {
                    recPosition = elemBTreeNode.leafElementSearch(CID);                    
                    if (recPosition != -1) {
                        //must be open still(pinned):
                        wrapper = new ElementBTreeNodeWrapper(elemBTreeNode, recPosition);
                        return wrapper;
                    } else {
                        elemBTreeNode.closeElementNode();
                        return null;
                    }
                }
            }
        }
        //unpin bayad she
    }
    public ElementBTreeNode findLeftMostLeave() throws IOException, DBException
    {
        int nextPagePointer;
        boolean keyFound = false;
        ElementBTreeNode elemBTreeNode;
        ElementBTreeNodeWrapper wrapper;
        int leftMostPointer;
        rootNode.loadElementBTreeNode(rootPointer, indexName);
        //inja bayad parents empty bashe.
        if (rootNode.IsLeaf())
        {
            return rootNode;
        
        } 
        else 
        {
            leftMostPointer = rootNode.leftMostPointer();
            while (true) 
            {
                elemBTreeNode = new ElementBTreeNode(leftMostPointer, indexName);
                if ((elemBTreeNode.IsLeaf())) 
                    return elemBTreeNode;
                    
                else 
                
                    leftMostPointer = elemBTreeNode.leftMostPointer();
                
            }
        }
    }
    /**
     * public DeweyID next(int CID,ElementBTreeNode container) throws
     * IOException { DeweyID targetDID; targetDID=container.next(); return
     * targetDID;      *
     * }
     */
    public ElementBTreeNodeWrapper open(int CID) throws IOException, DBException {
        //return new ElementIterator(ElementBTreeNode)
        ElementBTreeNodeWrapper containerWrapper;
        containerWrapper = scan(CID);
        return containerWrapper;
    }

    //being used in RG:
    public ElementIterator getStream(int CID) throws IOException, DBException {
        ElementBTreeNodeWrapper containerWrapper = this.open(CID);
        return new ElementIterator(containerWrapper, CID);
    }
    public ElementBTreeNodeWrapper open() throws IOException, DBException
     {
       /**returns first leave of the tree: So it can either search for CID=1 or goes to left most leave. ;*/
         ElementBTreeNode leftMostLeave;
         ElementBTreeNodeWrapper leftMostLeaveWrapper;
         leftMostLeave=findLeftMostLeave();
         leftMostLeaveWrapper=new ElementBTreeNodeWrapper(leftMostLeave, Settings.dataStartPos);
         return leftMostLeaveWrapper; 
         
     }
     public ElementIterator getStream() throws IOException, DBException
     {
         ElementBTreeNodeWrapper container=this.open();
         ElementIterator bTStream=new ElementIterator(container);
         return bTStream;
     }

    public void close() 
    {

    }
}

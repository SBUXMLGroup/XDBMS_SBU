
package indexManager;
import bufferManager.BufferManager;
import bufferManager.XBufferChannel;
import commonSettings.Settings;
import java.io.IOException;
import java.util.Stack;
import java.util.Queue;
import java.util.LinkedList;
import xmlProcessor.DeweyID;
import indexManager.Iterators.ContentElementIterator;
import java.util.ArrayList;
import xmlProcessor.DBServer.DBException;

public class ContentElementIndex
{    
    private BufferManager bufMngr;
    private int rootPointer; //khase khodash
    private ContentElementNode rootNode;
    private String indexName;
    public Stack parents;
    private ArrayList<Integer> pinnedPages;
    int lastRootP = -1;
    int testCounter=0;
    public ContentElementIndex(String indexName) throws IOException, DBException {
        //creates ElementIndex
        rootNode = new ContentElementNode(indexName, (byte) 1, (byte) 1, 0);
        rootPointer = rootNode.createContentElemNode();
        //rootNode.loadElementBTreeNode(rootPointer, indexName);
        bufMngr = BufferManager.getInstance();
        this.indexName = indexName;
        this.parents = new Stack();
        this.pinnedPages = new ArrayList<>();
        pinnedPages.add(rootPointer);

    }

    public ContentElementIndex(String indexName, int rootPointer) throws IOException, DBException {
        //this constructor is used to OPEN an Element index with the present root page in disk
        this.rootPointer = rootPointer;
        rootNode = new ContentElementNode(rootPointer, indexName);
        bufMngr = BufferManager.getInstance();
        this.indexName = indexName;
        this.parents = new Stack();
        this.pinnedPages = new ArrayList();
        pinnedPages.add(rootPointer);
    }

    public XBufferChannel export() {
        return rootNode.getBuf();

    }

    public int closeContentElemIndex() throws IOException {
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

    public int search(int CID,String cont, DeweyID deweyID) throws IOException, DBException {
        //search should be done based on CID
        int nextPagePointer;
        boolean keyFound = false;
        ContentElementNode elemBTreeNode;
        if (rootNode.IsLeaf()) {
            if (rootNode.leafContentElemSearch(CID,cont,deweyID)) {
                rootNode.closeContentElemNode();
                return rootPointer;
            } else {
                return -1;
            }
        } else {
            nextPagePointer = rootNode.internalContentElemSearch(CID,cont,deweyID);
            rootNode.closeContentElemNode();
        //page: pagestate,nextpage,freeoffset
            //ps,isroot,isleaf,number of keys in this page,freeoff,nextp(leftmost),deweyId,nextp,deweyId,nextp(rightmost)
            while (true) {
                //buf=bufMngr.getPage(nextPagePointer);
                elemBTreeNode = new ContentElementNode(nextPagePointer, indexName);
                //pinnedPages.add(nextPagePointer);
                if (!(elemBTreeNode.IsLeaf())) {
                    nextPagePointer = elemBTreeNode.internalContentElemSearch(CID,cont, deweyID);
                    elemBTreeNode.closeContentElemNode();

                } else {
                    if (elemBTreeNode.leafContentElemSearch(CID,cont, deweyID)) {
                        elemBTreeNode.closeContentElemNode();
                        return nextPagePointer;
                    } else {
                        elemBTreeNode.closeContentElemNode();
                        return -1;
                    }
                }
            }
        }
    }

    public void insert(int CID,String cont,DeweyID deweyID) throws IOException, Exception {
        int nextPagePointer;
        boolean keyFound = false;
        parents.clear();
        // BTreeNode rootNode;
        ContentElementNode elementBTreeNode;
        if (lastRootP != rootPointer) {
            System.err.println("ELEMENT ROOT : " + rootPointer);
        }
        lastRootP = rootPointer;
        rootNode.loadContentElementNode(rootPointer, indexName);
        //inja bayad parents empty bashe.
        if (rootNode.IsLeaf()) {
            if (!(rootNode.leafContentElemSearch(CID,cont, deweyID))) {
                rootPointer = rootNode.insertIntoLeafContentElem(CID,cont, deweyID, rootPointer, parents);
                rootNode.closeContentElemNode();
//                bufMngr.unpinBuffer(rootNode.getBuf(),true);
                // pinnedPages.add(nextPagePointer);
            }
            //else print:key already exists in the tree
        } else {
            nextPagePointer = rootNode.internalContentElemSearch(CID,cont, deweyID);
            rootNode.closeContentElemNode();
            //pinnedPages.add(nextPagePointer);
            parents.push(rootPointer);
            while (true) {
                //buf = bufMngr.getPage(nextPagePointer);
                elementBTreeNode = new ContentElementNode(nextPagePointer, indexName);
                if (!(elementBTreeNode.IsLeaf())) {
                    parents.push(nextPagePointer);
                    nextPagePointer = elementBTreeNode.internalContentElemSearch(CID,cont, deweyID);
                    elementBTreeNode.closeContentElemNode();
                    //pinnedPages.add(nextPagePointer);

                } else {
                    if (!(elementBTreeNode.leafContentElemSearch(CID,cont, deweyID))) {
                        rootPointer = elementBTreeNode.insertIntoLeafContentElem(CID,cont, deweyID, rootPointer, parents);
                        elementBTreeNode.closeContentElemNode();
                    } else {
                        elementBTreeNode.closeContentElemNode();
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
     return this;
    
     }*/
    public ContentElementNodeWrapper scan(int CID) throws IOException, DBException {
        /**
         * search should be done based on CID looks for a CID in
         * ElementIndex,finds its containing ElementBTreeNode and then finds its
         * position then wraps the Node and the position into an object
         */
        int nextPagePointer;
        boolean keyFound = false;
        ContentElementNode contElemNode;
        ContentElementNodeWrapper wrapper;
        int recPosition;
        rootNode.loadContentElementNode(rootPointer, indexName);        
        //inja bayad parents empty bashe.
        if (rootNode.IsLeaf()) {
            //pedarashun miaim pain bayd unpin she.
            //offset ro bargardune.
            // 3)y obj ezafi ham cid ham offs,ham hata buffer
            recPosition = rootNode.leafContentElemSearch(CID);            
            if (recPosition != -1) {
                wrapper = new ContentElementNodeWrapper(rootNode, recPosition);
                return wrapper;
            } else {
                rootNode.closeContentElemNode();
                return null;
            }
        } else {
            nextPagePointer = rootNode.internalContentElemSearch(CID);
            rootNode.closeContentElemNode();
        //page: pagestate,nextpage,freeoffset
            //ps,isroot,isleaf,number of keys in this page,freeoff,nextp(leftmost),deweyId,nextp,deweyId,nextp(rightmost)
            while (true) {
                //buf=bufMngr.getPage(nextPagePointer);
                contElemNode = new ContentElementNode(nextPagePointer, indexName);
                if (!(contElemNode.IsLeaf())) {
                    nextPagePointer = contElemNode.internalContentElemSearch(CID);
                    contElemNode.closeContentElemNode();
                } else {
                    recPosition = contElemNode.leafContentElemSearch(CID);                    
                    if (recPosition != -1) {
                        //must be open still(pinned):
                        wrapper = new ContentElementNodeWrapper(contElemNode, recPosition);
                        return wrapper;
                    } else {
                        contElemNode.closeContentElemNode();
                        return null;
                    }
                }
            }
        }
        //unpin bayad she
    }
    public ContentElementNode findLeftMostLeave() throws IOException, DBException
    {
        int nextPagePointer;
        boolean keyFound = false;
        ContentElementNode elemBTreeNode;
        ElementBTreeNodeWrapper wrapper;
        int leftMostPointer;
        rootNode.loadContentElementNode(rootPointer, indexName);
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
                elemBTreeNode = new ContentElementNode(leftMostPointer, indexName);
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
    public ContentElementNodeWrapper open(int CID) throws IOException, DBException {
        //return new ElementIterator(ElementBTreeNode)
        ContentElementNodeWrapper containerWrapper;
        containerWrapper = scan(CID);
        return containerWrapper;
    }

    //being used in RG:
    public ContentElementIterator getStream(int CID) throws IOException, DBException {
        ContentElementNodeWrapper containerWrapper = this.open(CID);
        return new ContentElementIterator(containerWrapper, CID);
    }
    public ContentElementNodeWrapper open() throws IOException, DBException
     {
       /**returns first leave of the tree: So it can either search for CID=1 or goes to left most leave. ;*/
         ContentElementNode leftMostLeave;
         ContentElementNodeWrapper leftMostLeaveWrapper;
         leftMostLeave=findLeftMostLeave();
         leftMostLeaveWrapper=new ContentElementNodeWrapper(leftMostLeave, Settings.dataStartPos);
         return leftMostLeaveWrapper; 
         
     }
     public ContentElementIterator getStream() throws IOException, DBException
     {
         ContentElementNodeWrapper container=this.open();
         ContentElementIterator bTStream=new ContentElementIterator(container);
         return bTStream;
     }

    public void close() 
    {

    }
}

    



package indexManager;

import bufferManager.BufferManager;
import bufferManager.IBufferManager;
import bufferManager.InternalBuffer;
import diskManager.DiskManager;
import LogicalFileManager.LogicalFileManager;
import commonSettings.Settings;
import xmlProcessor.DeweyID;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import xmlProcessor.DBServer.DBException;

public class IndexManager implements IIndexManager
{
    private IBufferManager iBufMngr;//che farghi dare inja ba dakhele sazande?
    private InternalBuffer buf;
    //private int indexPageNum;
    private DiskManager diskMngr;
    private LogicalFileManager logicalFileMgr;
    public static IndexManager Instance=new IndexManager();
    private BTreeIndex openBTreeIndx;
    private ElementIndex openElemIndx;
    private StructuralSummaryIndex openSSIndx;
    private IndexManager()
    {
        this.iBufMngr = BufferManager.getInstance();
        //=DiskManager.Instance;  
        logicalFileMgr=LogicalFileManager.Instance;
        //iBufMngr=;
    }
    //vase inja createInstance mikhad:
//    private IndexManager(String fileName) throws IOException
//     {
//        this.iBufMngr = BufferManager.getInstance();
//       // iBufMngr=new BufferManager();
//        
//        //=sth.getIndexPageNumber();
//        buf=iBufMngr.getPage(indexPageNum);      
//        
//        
//    }
   
    /**
     *
     * @param indexName
     * @param type
     * @return BTreeIndex
     */
   /* @Override
    public BTreeIndex createIndex(String indexName,int type)
    {
        BTreeIndex bTreeIndex;
        int indexPageNum;
        indexPageNum=logicalFileMgr.createIndex(indexName,type);
        bTreeIndex=new BTreeIndex(indexPageNum,indexName);        
        return bTreeIndex;
        //return diskMngr.createFile(fileName);
    }*/
 @Override
    public BTreeIndex createBTreeIndex(String indexName) throws IOException, DBException
    {
        //BTreeIndex bTreeIndex = null;
//        int FAKEindexPageNum;
          logicalFileMgr.createIndex(indexName,Settings.BTreeType);
            ///int indexPageNum;
            ///indexPageNum=logicalFileMgr.createIndex(indexName,type);
            ///gives an empty page number
            openBTreeIndx=new BTreeIndex(indexName);//creates rootNode        
        return openBTreeIndx;
        //return diskMngr.createFile(fileName);
    }
    @Override
     public ElementIndex createElementIndex(String indexName) throws IOException, DBException
    {
        //ElementIndex elemIndex = null;
        //int FAKEindexPageNum;
        logicalFileMgr.createIndex(indexName,Settings.ElementType);
        
            ///int indexPageNum;
            ///indexPageNum=logicalFileMgr.createIndex(indexName,type);
            ///gives an empty page number
            openElemIndx=new ElementIndex(indexName);
         
        return openElemIndx;
        //return diskMngr.createFile(fileName);
    }
    @Override
   public StructuralSummaryIndex createSSIndex(String indexName) throws IOException, DBException
    {
        //StructuralSummaryIndex structuralSumIndex;
        //int FAKEindexPageNum;
        logicalFileMgr.createIndex(indexName,Settings.SSType);
        ///int indexPageNum;
        ///indexPageNum=logicalFileMgr.createIndex(indexName,type);
        ///gives an empty page number
        openSSIndx=new StructuralSummaryIndex(indexName);        
        return openSSIndx;
        //return diskMngr.createFile(fileName);
    }
    
    @Override
    public BTreeIndex openBTreeIndex(String indexName) throws IOException, DBException 
    {
        //BTreeIndex bTreeIndex;
        //+1 faghat bara vaghtui k close nashode hanuz!
        int rootPointer=logicalFileMgr.getFirstPage(indexName, Settings.BTreeType);
        openBTreeIndx=new BTreeIndex(indexName,rootPointer);//opens index
        return openBTreeIndx;
    }
    @Override
    public ElementIndex openElementIndex(String indexName) throws IOException, DBException 
    {
       // ElementIndex elemIndex;
        int rootPointer=logicalFileMgr.getFirstPage(indexName,Settings.ElementType);
        openElemIndx=new ElementIndex(indexName,rootPointer);//opens index
        return openElemIndx;
    }
    @Override
    public StructuralSummaryIndex openSSIndex(String indexName) throws IOException, DBException 
    {
        //returns an SSIndex(loads its first page into mem) so that we can work with its functions
       // StructuralSummaryIndex ssIndex;
        //add is writen such that firstpage is empty,put aside for MD.
        int indexPage=logicalFileMgr.getFirstPage(indexName,Settings.SSType);
        openSSIndx=new StructuralSummaryIndex(indexPage,indexName);//opens index
        return openSSIndx;
    }
    
    @Override
    public void closeIndexes(String indexName) throws IOException, DBException //we may need to name indexes later
    {
        if(openBTreeIndx!=null && openBTreeIndx.getIndexName()==indexName)
           logicalFileMgr.closeIndex(indexName,Settings.BTreeType,openBTreeIndx.closeBTreeIndex());
        if(openElemIndx!=null && openElemIndx.getIndexName()==indexName)
            logicalFileMgr.closeIndex(indexName,Settings.ElementType,openElemIndx.closeElementIndex());
        if(openSSIndx!=null && openSSIndx.getIndexName()==indexName)
            logicalFileMgr.closeIndex(indexName,Settings.SSType,openSSIndx.closeSSIndex());;
        
    }

    @Override
    public void insert(int key, int recAdrs) 
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(int key, int recAdrs)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    @Override
    public int search(DeweyID key) 
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

    }

    @Override
    public void update(int key, int recAdrs) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}

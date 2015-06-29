
package LogicalFileManager;
import bufferManager.InternalBuffer;
import bufferManager.BufferManager;
import bufferManager.XBufferChannel;
import diskManager.DiskManager;
import diskManager.MetaDataManager;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import commonSettings.Settings;
import xmlProcessor.DBServer.DBException;

public class LogicalFileManager implements ILogicalFileManager
{
    private MetaDataManager mDMgr;
    private DiskManager diskMgr;
    private BufferManager bufMgr;
    //private XBufferChannel xBuf;
    public static LogicalFileManager Instance=new LogicalFileManager();
    private LogicalFileManager()
    {
        mDMgr=MetaDataManager.Instance;
        diskMgr=DiskManager.Instance;
        bufMgr=BufferManager.getInstance();
        
    }
    //****************
    public void createPhysicalFile(String fileName) throws IOException, DBException
    {
      final int GMDPageNum=0;
      XBufferChannel xBuf=bufMgr.getInitialPage();
      xBuf=mDMgr.createGlobalMetadata(xBuf);//getmd
      ///changed!REMOVED TRY CATCH BLOCK
//    diskMgr.writePage(GMDPageNum, xBuf);//az koja bfahmim kodum page?in male inite system ast.
      //instead:
      //bufMgr.unpinBuffer(xBuf, true);
//      return GMDPageNum; //behtare k true false bargardunim ya p.N.r
    }
    {// not being used
    /* public int createFile(String fileName) //bayad dbname o type o ...bgire
    {
        int waterMark;
        InternalBuffer xBuf=new InternalBuffer();
        //int firstPageNum;
        try {
            // String fName="A";

             xBuf=diskMgr.readPage(0, xBuf);
        } catch (IOException ex) {
            Logger.getLogger(LogicalFileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        xBuf=mDMgr.modifyGMD(xBuf,fileName);
        xBuf.position(13);//getting watermark
        waterMark=xBuf.getInt()-1; //chon dar modify watermark ra bordim jolo.
        xBuf.position(Settings.pageSize);// prepare for the next action:flip
        xBuf.flip();
        try { //in kar doroste ya throw???
            diskMgr.writePage(0, xBuf); //rewriting metadata
        } catch (IOException ex) {
            Logger.getLogger(LogicalFileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
                        
           this.setPageMD(fileName,waterMark);
           return waterMark;
           
     }
    
    public int createFile(int pageNum)
    {
        return 0;//not implemented yet
    }
    
    * function being used in btreeLeaf(not active already) 
    * public int addPage(String fileName) 
    {
        int waterMark;
        int pageNumber;
        int nextPage;
        InternalBuffer xBuf=new InternalBuffer();
        try {
            //get global meta data
            diskMgr.readPage(0, xBuf);
        } catch (IOException ex) {
            Logger.getLogger(LogicalFileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        xBuf.setPageNumber(0);
       
       pageNumber=mDMgr.getFirstPage(xBuf,fileName);
       if(pageNumber!=-1 & pageNumber>0)
           //mohem:-1 dar methode getFirstPage()... b manie an ast k chenin 
           //filename i ra nayaftim.
       {
            xBuf=mDMgr.modifyGMD(xBuf);
            xBuf.position(13);//getting watermark
            waterMark=xBuf.getInt()-1; //chon dar modify watermark ra bordim jolo.
            xBuf.position(Settings.pageSize);
            xBuf.flip();
            try {
                //nabayd flip o ina konam???
                diskMgr.writePage(0, xBuf);
            } catch (IOException ex) {
                Logger.getLogger(LogicalFileManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            xBuf.clear();
            xBuf=getLastPage(pageNumber); //returns the last page of file in order to change its nextP field
            
            nextPage=waterMark;
            xBuf.position(1);
            xBuf.putInt(nextPage); 
            xBuf.position(Settings.pageSize);
            xBuf.flip();
            try {
                
                diskMgr.writePage(xBuf.getPageNumber(),xBuf);
            } catch (IOException ex) {
                Logger.getLogger(LogicalFileManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            //adding page:
                setPageMD(fileName, nextPage); //ghblesh pN bud
              
                return nextPage;
       }
       return -1;//such a filename has not been created yet
        
       // convertPageContent(pageNumber);
    }*/
//    public int addPage(String fileName,byte isRoot,byte isLeaf,int numOfKeys) throws IOException 
//    {
//        byte pageState=1; //for live pages:1, deleted page:-1
//        int nextPage;
//        int freeOffset;
//        nextPage=-1; //pageNum+1;
//        freeOffset=Settings.dataStartPos;//only for init. phase
//        
//        InternalBuffer xBufHlpr=bufMgr.getFreePage();
//        xBufHlpr.clear();
//        //nextp of the last page must change somewhere only for leaves            xBuf.clear();
//        //adding page:
//        //setPageMD(xBufHlpr.getPageNumber(),isRoot,isLeaf,numOfKeys); //ghblesh pN bud
//        xBufHlpr.put(pageState); //1 byte
//        xBufHlpr.putInt(nextPage);//4byte
//        xBufHlpr.putInt(freeOffset);//4byte
//        //---------------
//        xBufHlpr.put(isRoot);
//        xBufHlpr.put(isLeaf);
//        xBufHlpr.putInt(numOfKeys);
//        
//        xBufHlpr.position(Settings.pageSize-1);
//        xBufHlpr.flip();//lazeme.
//        bufMgr.unpinBuffer(xBufHlpr, true);//???age hamintori unpin konam k unpin nemishe b mogh unvaght chi kar konam?        
//        return xBufHlpr.getPageNumber();
//    }
    }
     public int creatRootPage(String fileName,byte isRoot,byte isLeaf,int numOfKeys,XBufferChannel xBufHlpr) throws IOException 
    {
        byte pageState=1; //for live pages:1, deleted page:-1
        int nextPage;
        int freeOffset;
        nextPage=-1; //pageNum+1;
        freeOffset=Settings.dataStartPos;//only for init. phase        
        xBufHlpr.clear();
        //nextp of the last page must change somewhere only for leaves            xBuf.clear();
        //adding page:
        //setPageMD(xBufHlpr.getPageNumber(),isRoot,isLeaf,numOfKeys); //ghblesh pN bud
        xBufHlpr.put(pageState); //1 byte
        xBufHlpr.putInt(nextPage);//4byte
        xBufHlpr.putInt(freeOffset);//4byte
        //---------------
        xBufHlpr.put(isRoot);
        xBufHlpr.put(isLeaf);
        xBufHlpr.putInt(numOfKeys);
        
//        xBufHlpr.position(Settings.pageSize-1);
//        xBufHlpr.flip();//lazeme baraye flush kardan(write).
        //in kar faghat baraye flush shodan ast:
        //???aya lazeme k flush konim hatman dar in noghte?:
        //bufMgr.unpinBuffer(xBufHlpr, true);//???age hamintori unpin konam k unpin nemishe b mogh unvaght chi kar konam?        
        
        return xBufHlpr.getPageNumber();
    }
      public int creatSibling(XBufferChannel xBufHlpr) throws IOException 
    {
        byte pageState=1; //for live pages:1, deleted page:-1
        int nextPage;
        int freeOffset;
        nextPage=-1; //pageNum+1;
        freeOffset=Settings.dataStartPos;//only for init. phase        
        xBufHlpr.clear();
        xBufHlpr.put(pageState); //1 byte
        xBufHlpr.putInt(nextPage);//4byte
        xBufHlpr.putInt(freeOffset);//4byte
        //---------------
        xBufHlpr.put((byte)Settings.invalidValue);
        xBufHlpr.put((byte)Settings.invalidValue);
        xBufHlpr.putInt(0);//numOfKeys      
        return xBufHlpr.getPageNumber();
    }
//    public int addPage(String fileName,int freeOffset,byte isRoot,byte isLeaf,int numOfKeys,InternalBuffer xBuf) throws IOException 
//    {
//        byte pageState=1; //for live pages:1, deleted page:-1
//        int nextPage;
//        //int freeOffset;
//        int fakeInput=0;
//        nextPage=-1; //pageNum+1;
//        //freeOffset=Settings.dataStartPos;//only for init. phase
//        xBuf.setPageNumber(bufMgr.getFreePage(fakeInput));
//        //nextp of the last page must change somewhere only for leaves
//        //adding page:
//        //??? not completed yet:
//        //setPageMD(xBufHlpr.getPageNumber(),isRoot,isLeaf,numOfKeys,xBuf); //ghblesh pN bud
//        xBuf.position(0);
//        xBuf.put(pageState); //1 byte
//        xBuf.putInt(nextPage);//4byte
//        xBuf.putInt(freeOffset);//4byte //in writePage we have defined freeOffset.
//        //---------------
//       // xBuf.position(Settings.isRootPos);
//        xBuf.put(isRoot);
//        xBuf.put(isLeaf);
//        xBuf.putInt(numOfKeys);
//        
//        xBuf.position(Settings.pageSize);
//        xBuf.flip();//lazeme.
//        bufMgr.unpinBuffer(xBuf,true);        
//        return xBuf.getPageNumber();
//    }
    public int createTwinPage(String fileName,int freeOffset,byte isRoot,byte isLeaf,int numOfKeys,XBufferChannel xBuf) throws IOException 
    {
        byte pageState=1; //for live pages:1, deleted page:-1
        int nextPage;
        xBuf.position(0);
        xBuf.put(pageState); //1 byte
       // xBuf.putInt(nextPage);//4byte next page is set inside function
        //xBuf.putInt(freeOffset);//4byte //in writePage we have defined freeOffset.
        xBuf.position(Settings.isRootPos);
        xBuf.put(isRoot);
        xBuf.put(isLeaf);
        //xBuf.putInt(numOfKeys);        
        xBuf.position(Settings.pageSize);
        xBuf.flip();
        bufMgr.unpinBuffer(xBuf,true);        
        return xBuf.getPageNumber();
    }
     //public void updatePage(int pageNumber,int parentPointer,byte isRoot,byte isLeaf,int numOfKeys,InternalBuffer xBuf)  {  }   
    public void updatePage(XBufferChannel xBuf) throws IOException  //usage of this func must be replaced by unpin
    {  
//        try {
//            diskMgr.writePage(pageNumber, xBuf);
//        } catch (IOException ex) {
//            Logger.getLogger(LogicalFileManager.class.getName()).log(Level.SEVERE, null, ex);
//        }
           bufMgr.unpinBuffer(xBuf, true);
    }  
    //used in creatIndex:
    private XBufferChannel setPageMD(String fileName,XBufferChannel xBuf) throws IOException
    {
        byte pagestate=1; //for live pages:1, deleted page:-1
        int nextPage;
        int freeOffset;
        
        nextPage=-1; //pageNum+1;
        freeOffset=Settings.isRootPos;
        //byte[] lFNameArray=fileName.getBytes();
       

        xBuf.clear();
        xBuf.put(pagestate); //1 byte
        xBuf.putInt(nextPage);//4byte
        xBuf.putInt(freeOffset);//4byte
        //---------------
        
//        xBuf.position(Settings.pageSize-1);
//        xBuf.flip();//?lazeme?
        return xBuf;
        
    }
    {/*private void setPageMD(String fileName,int pageNum) throws IOException
    {
        byte pagestate=1; //for live pages:1, deleted page:-1
        int nextPage;
        int freeOffset;
        
        nextPage=-1; //pageNum+1;
        InternalBuffer xBuf=new InternalBuffer();
        freeOffset=9;
        //byte[] lFNameArray=fileName.getBytes();
       

        xBuf.clear();
        xBuf.put(pagestate); //1 byte
        xBuf.putInt(nextPage);//4byte
        xBuf.putInt(freeOffset);//4byte
        //---------------
        
        xBuf.position(Settings.pageSize);
        xBuf.flip();//?lazeme?
//        try 
//        {
//            diskMgr.writePage(pageNum, xBuf);
//        } catch (IOException ex) 
//        {
//            Logger.getLogger(LogicalFileManager.class.getName()).log(Level.SEVERE, null, ex);
//        }
        xBuf.setPageNumber(pageNum);
        bufMgr.unpinBuffer(xBuf, true);
        //true false doroste?

    }
     private void setPageMD(int pageNum,byte isRoot,byte isLeaf,int numOfKeys)
    {
        byte pageState=1; //for live pages:1, deleted page:-1
        int nextPage;
        int freeOffset;
        nextPage=-1; //pageNum+1;
        InternalBuffer xBuf=new InternalBuffer();
        freeOffset=Settings.dataStartPos;//only for init. phase
        xBuf.setPageNumber(pageNum);
        xBuf.clear();
        xBuf.put(pageState); //1 byte
        xBuf.putInt(nextPage);//4byte
        xBuf.putInt(freeOffset);//4byte
        //---------------
        xBuf.put(isRoot);
        xBuf.put(isLeaf);
        xBuf.putInt(numOfKeys);
        
        xBuf.position(Settings.pageSize);
        xBuf.flip();//lazeme.
        try 
        {
            diskMgr.writePage(pageNum, xBuf);
        } catch (IOException ex) 
        {
            Logger.getLogger(LogicalFileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        //true false doroste?

    }
    
    private void setPageMD(int pageNum,byte isRoot,byte isLeaf,int numOfKeys,InternalBuffer xBuf)
    {
        byte pageState=1; //for live pages:1, deleted page:-1
        int nextPage;
        int freeOffset;
        nextPage=-1; //pageNum+1;
        //freeOffset=Settings.dataStartPos;//inam irad dare!:(bayd tu hamun insert set she
        xBuf.setPageNumber(pageNum);
        xBuf.position(0);//must not clear xbuf cause it contains data
        xBuf.put(pageState); //1 byte
        xBuf.putInt(nextPage);//4byte
       // xBuf.putInt(freeOffset);//4byte
        //---------------
        xBuf.position(Settings.isRootPos);
        xBuf.put(isRoot);
        xBuf.put(isLeaf);
        xBuf.putInt(numOfKeys);
        //DATA...
        xBuf.position(Settings.pageSize);
        xBuf.flip();//lazeme.
        try 
        {
            diskMgr.writePage(pageNum, xBuf);//???INJA bayd ehtemalan unpin shavad
        } catch (IOException ex) 
        {
            Logger.getLogger(LogicalFileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        //true false doroste?

    }*/}    
    
    public int getFirstPage(String indexName,int type)
    {
        InternalBuffer xBuf=new InternalBuffer();
        xBuf.setPageNumber(0);
        try 
        {
             xBuf=diskMgr.readPage(0, xBuf);
        } catch (IOException ex) {
            Logger.getLogger(LogicalFileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        int firstPage;
        firstPage= mDMgr.getFirstPage(xBuf,indexName,type);
        return firstPage;
    }
    public InternalBuffer getLastPage(int firstPage)
     {
         byte pageState;
         int nextPage;
         int pageNum;
         InternalBuffer xBuf=new InternalBuffer();
         //pageNum=firstPage;
         nextPage=firstPage;
         do
         {
            pageNum=nextPage;
            xBuf.setPageNumber(pageNum);
            xBuf.clear();
             try {
                  diskMgr.readPage(pageNum, xBuf);
             } catch (IOException ex) {
                 Logger.getLogger(LogicalFileManager.class.getName()).log(Level.SEVERE, null, ex);
             }
            
            xBuf.position(1);
           //byte pS=xBuf;
            nextPage=xBuf.getInt();
            } while(nextPage!=-1); //ya: p.N>0
         
         return xBuf;
     }
      
    public void createIndex(String indexName,int type) throws IOException, DBException
    {
        int waterMark;
        XBufferChannel xBuf;
                //=new XBufferChannel();
        xBuf=bufMgr.getFreePage(indexName,type);
        xBuf=this.setPageMD(indexName,xBuf);
        bufMgr.unpinBuffer(xBuf, true);
                    
    }
    
    public void closeIndex(String indexName,int type,int rootPointer) throws IOException, DBException
    {
        int waterMark;
        XBufferChannel xBuf;
        //chera CLOCK estefade nakardim?
        //int firstPageNum;
        xBuf=bufMgr.getPage(0);              
        xBuf=mDMgr.closeOperation(xBuf,indexName,type,rootPointer);
        xBuf.position(Settings.pageSize);// prepare for the next action:flip
        xBuf.flip();        
        bufMgr.unpinBuffer(xBuf, true);
        bufMgr.unpinAll();
                        
     }
}

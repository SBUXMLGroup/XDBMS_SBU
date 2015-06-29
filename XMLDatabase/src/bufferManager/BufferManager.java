
package bufferManager;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import logManager.LogManager;
import diskManager.DiskManager;
import java.util.ArrayList;
import commonSettings.Settings;
import diskManager.MetaDataManager;
import java.util.HashMap;
import xmlProcessor.DBServer.DBException;

public class BufferManager implements IBufferManager 
{
    int sign=0;
    private BufferPool bufPool;
    private Clock clk;
    private DiskManager diskMgr;
    private MetaDataManager mDMgr;
    private static BufferManager Instance=new BufferManager();
    private int pageCounter;
    private BufferManager()
    {        
        bufPool=new BufferPool();
        clk=new Clock(bufPool);
        diskMgr=DiskManager.Instance;
        mDMgr=MetaDataManager.Instance;
    }
    private void flushBuffer(InternalBuffer inBuf,int pageNum) throws IOException
    {
        
        if(pageNum!=inBuf.getPageNumber())
            LogManager.log(6,"Caught: "+"pageNum: "+pageNum+"real num: "+inBuf.getPageNumber());
         if(inBuf.isDirty())//must not check being pinned.
         {                
            inBuf.position(Settings.pageSize);             
            inBuf.flip();           
            diskMgr.writePage(pageNum,inBuf);
            LogManager.log("Unpinned & Written:"+String.valueOf(inBuf.getPageNumber())+" Pin count: "+String.valueOf(inBuf.getPinCount()));
            if(pageNum==0)
                LogManager.log("GMD Written",5);
               //diskMgr.convertPageContent(pageNum,inBuf);
             inBuf.Undirty();  
           
         }         
          
         
    }
    public static BufferManager getInstance()
    {
        return Instance;
    }     
            
    @Override
    public void flushAllBuffers()
    {
            int pageNum;
            for (int i=0;i<10;i++)
            {
                //tmpBuf=bufPool.getBuffer(i);
                if(bufPool.getBuffer(i).isDirty())
                {
                    pageNum=bufPool.getBuffer(i).getPageNumber();
                    try {        
                        diskMgr.writePage(pageNum, bufPool.getBuffer(i));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    bufPool.getBuffer(i).Undirty(); 
                    bufPool.removeMapEntry(pageNum);
                    bufPool.arrayMap.remove(pageNum);//value is the index of array which is released.
                }         
                
            }
        
     }
    /**
     *
     * @param newPageNumber
     * @return
     * @throws IOException
     */
    @Override
    public XBufferChannel getPage(int newPageNumber) throws IOException,DBException
    {
//        BufWrapper hlper;
//        hlper=clk.selectBuffer();
//          LogManager.log(0,"getPage("+newPageNumber+")");       
            int expiredPage;// the previous page mapped to selected buffer
            InternalBuffer buffer;
            BufWrapper wrapper;
            //InternalBuffer gMDBuf;//for GMD PAge(0)
            XBufferChannel xChannel;
            /////FOR RESERVED GMD:!!!(It's not reserved now!)
            if(newPageNumber==0)
            {   
                if(bufPool.containsPage(0))
                    buffer=bufPool.getMappedBuffer(0);
                else
                {
                     buffer=bufPool.getBuffer(10);
                     buffer.Pin();
//                pageNum=gMDBuf.getPageNumber();
//                bufPool.removeMapEntry(pageNum);//
//                if(gMDBuf.isDirty())
//                {                    
//                    flushBuffer(gMDBuf,pageNum);//in lazem nis chon ghablan tu select flush mishe.
//                }                          
                diskMgr.readPage(0,buffer);
                bufPool.add(0,buffer);
                }
                 if(buffer.isPin())
                    logManager.LogManager.log("Pinned page being Asked: "+String.valueOf(newPageNumber)+" Pin count: "+String.valueOf(buffer.getPinCount()),5);
                 LogManager.log("GMD Read",5);
            }
            else {
            if(bufPool.containsPage(newPageNumber))
            {   
                buffer=bufPool.getMappedBuffer(newPageNumber);
                if(buffer.isPin())
                  logManager.LogManager.log("Pinned page being Asked: "+String.valueOf(newPageNumber)+" Pin count: "+String.valueOf(buffer.getPinCount()),5);
                //for every time that page is being asked,page should be pinned again:pin count should be increased.
                
            }            
            else
            {
                
                //buffer=clk.selectBuffer();
                wrapper=clk.selectBuffer();
                buffer=wrapper.getInBuf();
                expiredPage=buffer.getPageNumber();                 
                if(buffer.isDirty())
                {   
                    flushBuffer(buffer,expiredPage);
                }
                bufPool.removeMapEntry(expiredPage);
                bufPool.arrayMap.remove(expiredPage);
                buffer.clear();
                buffer= diskMgr.readPage(newPageNumber,buffer); 
                if(newPageNumber==0)
                    LogManager.log("GMD Read", 5);
                //buffer.setPageNumber(newPageNumber);//tuye disk
                bufPool.add(newPageNumber,buffer);
                bufPool.arrayMap.put(newPageNumber,wrapper.getIndx());
            }
           }
            buffer.Pin();
            //LogManager.log("Sign: "+String.valueOf(++sign));
            //LogManager.log("Pinned: "+String.valueOf(newPageNumber)+" Pin count: "+String.valueOf(buffer.getPinCount()),5);
            xChannel=new XBufferChannel(buffer);
            return xChannel;            
    }
    //only for very first page:GMD!
    public XBufferChannel getInitialPage() throws IOException,DBException
    {
             InternalBuffer gMDBuf;//for GMD Page(0)
             XBufferChannel gmdChannel;
            //HERE SHOULD ALWAYS pick Buffer[0]
             BufWrapper gmdWrapper=clk.selectBuffer();
             gMDBuf=gmdWrapper.getInBuf();
             //preserve buf[0] for GMD:
//            gMDBuf=bufPool.bufArray[Settings.resBuf];                                                             
            diskMgr.readPage(0,gMDBuf); //Since this is the first read no need to check dirtiness and flush ,...
            gMDBuf.Pin();
            LogManager.log("GMD Read", 1);
            bufPool.add(0,gMDBuf);
            bufPool.arrayMap.put(0,gmdWrapper.getIndx());
           // gMDBuf.setPageNumber(0);THIS IS DONE IN readPage()
            gmdChannel=new XBufferChannel(gMDBuf);
            return gmdChannel;
    }
   public XBufferChannel getFreePage() throws IOException,DBException
   {
            pageCounter++;
            LogManager.log("getFreePage()", 1);
            int pageNum;// the previous page mapped to selected buffer
            InternalBuffer buffer;
            BufWrapper wrapper;
            InternalBuffer gMDBuf;//for GMD PAge(0)
            XBufferChannel xChannel;
            int freePageNum;
//            if(bufPool.containsPage(0))
//                gMDBuf=bufPool.getMappedBuffer(0);            
//            else
//            {
//                gMDBuf=clk.selectBuffer();
//                gMDBuf.Pin();
//                pageNum=gMDBuf.getPageNumber();
//                bufPool.removeMapEntry(pageNum);//
//                if(gMDBuf.isDirty())
//                {                    
//                    flushBuffer(gMDBuf,pageNum);//in lazem nis chon ghablan tu slect flush mishe.
//                }                          
//                diskMgr.readPage(0,gMDBuf);
//                bufPool.add(0,gMDBuf);
//            }
            if(bufPool.containsPage(0))
                gMDBuf=bufPool.getMappedBuffer(0);
            
            else
            {
                gMDBuf=bufPool.getBuffer(10);
                gMDBuf.Pin();
//                pageNum=gMDBuf.getPageNumber();
//                bufPool.removeMapEntry(pageNum);//
//                if(gMDBuf.isDirty())
//                {                    
//                    flushBuffer(gMDBuf,pageNum);//in lazem nis chon ghablan tu select flush mishe.
//                }                          
                diskMgr.readPage(0,gMDBuf);
                bufPool.add(0,gMDBuf);
            }
            freePageNum=mDMgr.modifyGMD(gMDBuf);                     
//            gMDBuf.position(Settings.waterMarkPos);//getting watermark
//            freePageNum=gMDBuf.getInt()-1; //chon dar modify watermark ra bordim jolo.
//            gMDBuf.position(Settings.pageSize-1);
//            gMDBuf.flip();
            //unpinBuffer(gMDBuf, true);  //DISABLE Unpin GMD  
            wrapper=clk.selectBuffer();
            buffer=wrapper.getInBuf();                      
            pageNum=buffer.getPageNumber();
            //if(bufPool) contanis such mapping:(there is no need to check)
            bufPool.removeMapEntry(pageNum);//
            bufPool.arrayMap.remove(pageNum);
            if(buffer.isDirty())
            {                
                flushBuffer(buffer,pageNum);
            }
            
            buffer.clear();
           ///COmment for test::: buffer= diskMgr.readPage(freePageNum,buffer); 
           buffer.setPageNumber(freePageNum);
            bufPool.add(freePageNum,buffer);
            buffer.Pin(); //aval bayd isDirty chek shavad  bad pin shavad.Strongly recommended.
            xChannel=new XBufferChannel(buffer);
            return xChannel;          
      }
    public XBufferChannel getFreePage(String indexName,int type) throws IOException,DBException
    {
            //this method allocates the first page of Index to it.
            LogManager.log("Index of type "+type +" is being created", 1);
            int pageNum;// the previous page mapped to selected buffer
            InternalBuffer buffer;
            BufWrapper wrapper;
            InternalBuffer gMDBuf;//for GMD PAge(0)
            XBufferChannel xChannel;
            int freePageNum;
//            if(bufPool.containsPage(0))
//                gMDBuf=bufPool.getMappedBuffer(0);            
//            else
//            {
//                gMDBuf=clk.selectBuffer();
//                gMDBuf.Pin();
//                pageNum=gMDBuf.getPageNumber();
//                bufPool.removeMapEntry(pageNum);//
//                if(gMDBuf.isDirty())
//                {                    
//                    flushBuffer(gMDBuf,pageNum);//in lazem nis chon ghablan tu slect flush mishe.
//                }                          
//                diskMgr.readPage(0,gMDBuf);
//                bufPool.add(0,gMDBuf);
//            }
            if(bufPool.containsPage(0))
                gMDBuf=bufPool.getMappedBuffer(0);
            
            else
            {
                //with the assumption to reserve buf[10] for GMD
                gMDBuf=bufPool.getBuffer(10);
                gMDBuf.Pin();
//                pageNum=gMDBuf.getPageNumber();
//                bufPool.removeMapEntry(pageNum);//
//                if(gMDBuf.isDirty())
//                {                    
//                    flushBuffer(gMDBuf,pageNum);//in lazem nis chon ghablan tu select flush mishe.
//                }                          
                diskMgr.readPage(0,gMDBuf);
                bufPool.add(0,gMDBuf);
            }
            freePageNum=mDMgr.modifyGMD(gMDBuf, indexName, type)-1;
//            gMDBuf.position(Settings.waterMarkPos);//getting watermark
//            freePageNum=gMDBuf.getInt()-1; //chon dar modify watermark ra bordim jolo.
//            gMDBuf.position(Settings.pageSize-1);
//            gMDBuf.flip();
//            unpinBuffer(gMDBuf, true); disable unpin GMD 
//             buffer=clk.selectBuffer();
            wrapper=clk.selectBuffer();
            buffer=wrapper.getInBuf();            
            //buffer.Pin();            
            pageNum=buffer.getPageNumber();
            //if(bufPool) contanis such mapping:(there is no need to check)
            bufPool.removeMapEntry(pageNum);//
            bufPool.arrayMap.remove(pageNum);
            if(buffer.isDirty())
            {                
                flushBuffer(buffer,pageNum);
            }
            
            buffer.clear();
            //COmment for test ::::buffer= diskMgr.readPage(freePageNum,buffer); //IO exception?
            //??? in kar lazeme?khundane page khali?
            buffer.setPageNumber(freePageNum);//tuye disk
            bufPool.add(freePageNum,buffer);
            bufPool.arrayMap.put(freePageNum,wrapper.getIndx());
            buffer.Pin(); //aval isDirty bayad chek shavd v bad pin shavad. Strongly recommended.
            xChannel=new XBufferChannel(buffer);
            return xChannel;          
      }
    { 

}
  public void unpinBuffer(InternalBuffer buf)
  {
      if(buf.getPageNumber()==0)
            LogManager.log("GMD MUST NOT GET UNPINNED",5);
      if(buf.isPin())
      {
         
         buf.Unpin();
         buf.setRef(true);
         LogManager.log("Unpinned:"+String.valueOf(buf.getPageNumber())+" Pin count: "+String.valueOf(buf.getPinCount()),5);
      }
      
  }
   public void unpinBuffer(XBufferChannel xChannel)
  {
      unpinBuffer(xChannel.getInternalBuffer());
  }
  public void unpinAll(ArrayList<Integer> pinnedList)
  {
     InternalBuffer inBuf;
     for(int i=0;i<pinnedList.size();i++)
     {
         inBuf=bufPool.getBuffer(pinnedList.get(i));
      if(inBuf.isPin())      
          inBuf.Unpin(); 
     }
  }
  public void unpinAll() throws IOException
  {
     InternalBuffer inBuf;
     for(int i=0;i<bufPool.size();i++)
     {
         inBuf=bufPool.getBuffer(i);
         
          if(inBuf.isDirty()) //no matter if its pinned or unpinned,in any case it should be flushed
              unpinBuffer(inBuf, true);
         if(inBuf.isPin())   
             unpinBuffer(inBuf);         
     }
  }
    /**
     *
     * @param buf
     * @param flush
     * @throws IOException
     */
    @Override
    public void unpinBuffer(InternalBuffer buf,boolean flush) throws IOException
    {
        if(buf.getPageNumber()==0)
            LogManager.log("GMD MUST NOT GET UNPINNED",5);        
        buf.Unpin();        
        buf.setRef(true);
        flushBuffer(buf,buf.getPageNumber());
    }
    public void unpinBuffer(XBufferChannel xChannel,boolean flush) throws IOException
    {
        unpinBuffer(xChannel.getInternalBuffer(),flush);
        
    }
  
    public BufferPool getBufPool()
    {
        return bufPool;
    }
    public HashMap getPoolStatus()
    {
        return bufPool.getStatus();
        
    }
}

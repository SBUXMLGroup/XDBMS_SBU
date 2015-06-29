
package diskManager;

import java.nio.ByteBuffer;
import bufferManager.InternalBuffer;
import bufferManager.XBufferChannel;
import commonSettings.MDSettings;
import commonSettings.Settings;

public class MetaDataManager {
    
    private int FilesCount;
    private int pagesCount;
    private byte pagestate;
    private int nextPage;
    private int nextFreePage;//for future use for deleted pages
    private int waterMark; //the first free page after written pages
    private int freeOffset; //the begining of empty part of the page
    private int lastDeletedPage; //this field is saved only in global metadata
    //type: file/indextype:b+tree,btree,...
    
    public static MetaDataManager Instance=new MetaDataManager();
    private MetaDataManager()
    { }
    
    public XBufferChannel createGlobalMetadata(XBufferChannel xBuf) 
    {
      /*page state,nextFreePage link(for deleted pages) OR next page(for live 
       pages),free Offset,pagesCount,watermark,logical files count,
       * Table:FileName(length-byte array)- start page*/
       pagesCount=20;
        pagestate=1;
        nextPage=-1;
        waterMark=1;// pages:0,1,2,...
        lastDeletedPage=-1;      
        //fchanel.position=0 ast dar in noghte
        xBuf.clear();
        //for any page:
        xBuf.put(pagestate); //1 byte
        xBuf.putInt(nextPage);//4byte
        xBuf.putInt(freeOffset);//not valid value //4byte
        //for GMD only:
        xBuf.putInt(pagesCount);        //4byte
        xBuf.putInt(waterMark);    //4byte
        xBuf.putInt(FilesCount);   //4byte
           
        String text = "GMD";  
        byte[] textArray = text.getBytes();  
        xBuf.putInt(textArray.length);//4byte
        xBuf.put(textArray,0,textArray.length); //textArray.length
        freeOffset=25+ textArray.length;//=28
        xBuf.position(MDSettings.freeOffsetPos);
        xBuf.putInt(freeOffset);//valid value
        xBuf.position(freeOffset+1);//needed for setting limit     or: positon(1023)   
       // xBuf.flip();
        return xBuf;
        /*try {
            fChannel.write(xBuf);
        } catch (IOException e2) {
            e2.printStackTrace();
        }*/

    }
    { //This is a method for changing globalmetadata for creating a new file
//    public InternalBuffer modifyGMD(InternalBuffer xBuf,String fileName,int type) 
//    {
//      
//        xBuf.position(MDSettings.freeOffsetPos);   
//        int lastFreeOffset;
//        lastFreeOffset=xBuf.getInt(); // OR:xBuf.position(9);
//        pagesCount=xBuf.getInt();        //4byte
//        int lastWaterMark;
//        lastWaterMark=xBuf.getInt();    //4byte
//        FilesCount=xBuf.getInt();  
//        
//        waterMark=lastWaterMark+1; //page number=0,1,2,...
//        FilesCount++;
//        xBuf.position(MDSettings.waterMarkPos);
//        xBuf.putInt(waterMark);
//        xBuf.putInt(FilesCount);
//        //4bytes:length+3 bytes for "GMD"        
//        xBuf.position(lastFreeOffset);        
//        byte[] textArray = fileName.getBytes();
//        xBuf.putInt(textArray.length);
//        xBuf.put(textArray,0,textArray.length);//pos=32
//        xBuf.putInt(lastWaterMark);// file: A:pageX (first page of file A)
//        xBuf.putInt(type);
//        freeOffset=freeOffset+Settings.sizeOfInt+textArray.length+2*Settings.sizeOfInt;
//        xBuf.position(MDSettings.freeOffsetPos);
//        xBuf.putInt(freeOffset);
//        
//        xBuf.position(Settings.pageSize); //1023,63,...
//        xBuf.flip();//sets limit=position, position=0
//        return xBuf;
//       
//    }
}
 public int modifyGMD(InternalBuffer xBuf,String fileName,int type) 
    {
      
        xBuf.position(MDSettings.freeOffsetPos);   
        int lastFreeOffset;
        lastFreeOffset=xBuf.getInt(); // OR:xBuf.position(9);
        pagesCount=xBuf.getInt();        //4byte
        int lastWaterMark;
        lastWaterMark=xBuf.getInt();    //4byte
        FilesCount=xBuf.getInt();        
        waterMark=lastWaterMark+1; //page number=0,1,2,...
        if(type==1)
            FilesCount++;
        xBuf.position(MDSettings.waterMarkPos);
        logManager.LogManager.log(0,"WaterMark: "+waterMark);
        xBuf.putInt(waterMark);
        xBuf.putInt(FilesCount);
        //4bytes:length+3 bytes for "GMD"        
        xBuf.position(lastFreeOffset);        
        byte[] textArray = fileName.getBytes();
        xBuf.putInt(textArray.length);
        xBuf.put(textArray,0,textArray.length);//pos=32
        xBuf.putInt(lastWaterMark);// file: A:pageX (first page of file A)
        xBuf.putInt(type);
        freeOffset=freeOffset+Settings.sizeOfInt+textArray.length+2*Settings.sizeOfInt;
        xBuf.position(MDSettings.freeOffsetPos);
        xBuf.putInt(freeOffset);
        
//        xBuf.position(Settings.pageSize-1); //1023,63,...
//        xBuf.flip();//sets limit=position, position=0
        return waterMark;
       
    }
    //This is a method for changing globalmetadata for creating a new page
    //used in getfreepage: buffermgr:
    public int modifyGMD(InternalBuffer xBuf) 
    {
        int lastWaterMark;
        xBuf.position(MDSettings.waterMarkPos);   
        waterMark=xBuf.getInt();    //4byte   
        lastWaterMark=waterMark;
        waterMark++; //page number=0,1,2,...
        xBuf.position(MDSettings.waterMarkPos);
        logManager.LogManager.log(0,"WaterMark: "+waterMark);
        xBuf.putInt(waterMark);
        //in unpin->flush it gets fliped (before being written)
        //xBuf.position(Settings.pageSize-1);
        //xBuf.flip();//sets limit=position, position=0
        return lastWaterMark;       
    }
    //used in logicalFileMgr:
    public XBufferChannel closeOperation(XBufferChannel xBuf,String indexName,int type,int rootPointer) 
    {
        //this function writes rootPointer in front of indexName in GMD.
        //xbuf contains page 0: GMD page
        xBuf.position(MDSettings.freeOffsetPos);   
        int lastFreeOffset;
        int length;
        int pageNo;
        int pageNoPos;
        int typeOfIndex;
        String fileName;
        xBuf.position(MDSettings.freeOffsetPos);
        lastFreeOffset=xBuf.getInt(); // OR:xBuf.position(9);   
        xBuf.position(MDSettings.filesCountPos);
        FilesCount=xBuf.getInt();
        xBuf.position(MDSettings.MDStartPos); //start filename ha
        for(int i=0;i<FilesCount;i++)
        {
            //we got 3 indexes in each file:
            for(int j=0;j<3;j++)
            {
                length=xBuf.getInt();
                byte[]fileNameBytes=new byte[length];
                xBuf.get(fileNameBytes,0,length);   
                fileName=new String(fileNameBytes);
                pageNoPos=xBuf.position();
                pageNo=xBuf.getInt();
                typeOfIndex=xBuf.getInt();
                if(type==typeOfIndex && fileName.compareTo(indexName)==0)
                {    xBuf.position(pageNoPos);
                     xBuf.putInt(rootPointer);
                     break;
                }
            }
        } 
//        xBuf.position(Settings.pageSize-1); //1023,63,...
//        xBuf.flip();//sets limit=position, position=0
        return xBuf;
       
    }
     //used in bufMgr:(getPage:)
     public int getFirstPage(InternalBuffer xBuf,String logFileName,int type)
     {
        String fileName;
        int length;
        int pageNumber=-1;
        int FilesCount;
        int typeOfIndex;
        xBuf.position(MDSettings.filesCountPos);
        FilesCount=xBuf.getInt();
        //int iterations=FilesCount;
        xBuf.position(MDSettings.MDStartPos); //start filename ha
        for(int i=0;i<FilesCount;i++)
        {
           for(int j=0;j<3;j++)
           {
            length=xBuf.getInt();
            byte[]fileNameBytes=new byte[length];
            xBuf.get(fileNameBytes,0,length);   
            fileName=new String(fileNameBytes);
            pageNumber=xBuf.getInt();
            typeOfIndex=xBuf.getInt();
            if(type==typeOfIndex && fileName.compareTo(logFileName)==0)
                return pageNumber;
           }
        }  
        return pageNumber;
     }
     
   
    {/* public int createLogFileMetaData() {
       byte pagestate;
       int nextPage;
       int freeOffset;
       int localPagesCount;
       long channelLocation;
       
       pagestate=1;
       //chon in tabe bad az modifGMD farakhani mishavad ma pegeCount ro darim.
       nextPage=pagesCount-10+1;//in GMD 10 pages have been allocated for this file
       localPagesCount=1;// this is for initialization
       freeOffset=13;
       
       fBuffer.clear();
       fBuffer.put(pagestate);
       fBuffer.putInt(nextPage);
       fBuffer.putInt(freeOffset);
       fBuffer.putInt(localPagesCount);//the only metadata here!
              
       channelLocation=(pagesCount-10)*1024;       
       fBuffer.flip();
        try {
            fChannel.position(channelLocation);
            fChannel.write(fBuffer);
        } catch (IOException e2) {
            e2.printStackTrace();
        }
       
       return nextPage;
    }*/}
    { /*public void modifyLogFileMetaData(){}*/}
    { /*public int getWaterMark(){
     int waterMark; 
     try {
            fChannel.position(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    fBuffer.clear();
    try {
            fChannel.read(fBuffer); //1024bytes
                       
        } catch (IOException e) {
            e.printStackTrace();
        }
    fBuffer.position(13);
    waterMark=fBuffer.getInt();
    return waterMark;
    }*/
    }
    
}

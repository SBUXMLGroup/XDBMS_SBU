package diskManager;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import commonSettings.Settings;
import statistics.Monitor;
import bufferManager.InternalBuffer;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import xmlProcessor.DeweyID;

public class DiskManager
{
    static int counter;
    static int lastFreeOffset;
    private FileChannel fChannel;
    private ByteBuffer byteBuf;
    private File file;
    
    RandomAccessFile ranAcsFile;
//    String filename = "d:/X11.bin";
//    String filename = "d:/ds2.bin";
//    String filename = "d:/ds3.bin";
//    String filename = "d:/m1.bin";
//    String filename = "d:/Nasa.bin";
//    String filename = "d:/SwissProt.bin";
//    String filename = "d:/XMarkPrime1.bin";
//    String filename = "d:/XMark5.bin";
//    String filename = "d:/D4.bin";
//    String filename = "d:/X5.bin";
//    String filename = "d:/XMark5Mirror.bin";
//    String filename = "d:/Disk.bin";
  String filename = "d:/DBLP.bin";
    private MetaDataManager mDMgr;
    //behtar nis inharo b onvane motgheyere komaki dashte bashim????
    //private int FilesCount;
    //private int pagesFilesCountCount;
    private byte pagestate;
    //private int nextPage;     
    //private int nextFreePage;//for future use for deleted pages
    private int freeOffset; //the begining of empty part of the page
    private Monitor monitor;
    public static DiskManager Instance=new DiskManager();
    private DiskManager()
    {
       try {
           counter=0;
            mDMgr=MetaDataManager.Instance; //instance bayad bashe dar dbmangr
            File file = new File(filename);
            // if file doesnt exists, then create it
//            if(file.exists())
//                file.delete();
              
            file.createNewFile();
          
           ranAcsFile = new RandomAccessFile(filename, "rw");
           long diskLength=1500000*Settings.pageSize; //5000000pages(From 0 to 1999999)
           ranAcsFile.setLength(diskLength);
           fChannel = ranAcsFile.getChannel();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        this.byteBuf = ByteBuffer.allocate(Settings.pageSize);
        this.monitor=Monitor.getInstance();

    }
    public void dispose()
    {
        if (file.exists()) 
           {
               file.delete();
           } 
    }
    
    public void export(int pageNum,InternalBuffer xBuf) throws FileNotFoundException, IOException
    {
        String outFilename="d:/DBOutput.txt";
        File aFile = new File(outFilename); // File object for the file path
        aFile.createNewFile();
        FileOutputStream outputFile;
        outputFile = new FileOutputStream(aFile);
        FileChannel outChannel = outputFile.getChannel();
        ByteBuffer byteBuf;
        byteBuf=xBuf.getByteBuffer();
        byteBuf.flip();
        outChannel.write(byteBuf); //Write the buffer to the file channel
        outputFile.close();
    }
    public void export(InternalBuffer xBuf) throws FileNotFoundException, IOException
    {
        String outFilename="d:/DBOutput.txt";
        File aFile = new File(outFilename); // File object for the file path
        aFile.createNewFile();
        FileOutputStream outputFile;
        outputFile = new FileOutputStream(aFile);
        FileChannel outChannel = outputFile.getChannel();
        ByteBuffer byteBuf;
        byteBuf=xBuf.getByteBuffer();
        byteBuf.flip();
        outChannel.write(byteBuf); // Write the buffer to the file channel
        outputFile.close();
    }
//    public void convertPageContent(int pageNum,InternalBuffer xBuf)
//    {
//        counter++;
//        int divNum;
//        ByteBuffer byteBuf;
//        byteBuf=xBuf.getByteBuffer();
//        ByteBuffer tmpBuf;
//        tmpBuf=ByteBuffer.allocate(Settings.pageSize);
//        byteBuf.position(0);
//        tmpBuf.position(0);
//        String tmpStr;
//        
//        byte[] bytes; 
//        // System.out.println("Page State: "+Byte.toString(byteBuf.get()));
//        tmpStr =Byte.toString(byteBuf.get());
//        bytes=tmpStr.getBytes();
//        tmpBuf.put(bytes);      
//       //System.out.println("Next Page: "+String.valueOf(byteBuf.getInt()));
//        tmpStr=String.valueOf(byteBuf.getInt());
//        bytes=tmpStr.getBytes();
//        tmpBuf.put(bytes);
//        
//       //int freeOffset=byteBuf.getInt();
//       //System.out.println("Free Offset: "+String.valueOf(freeOffset));
//       tmpStr=String.valueOf(byteBuf.getInt());
//       bytes=tmpStr.getBytes();
//       tmpBuf.put(bytes);        
//      // System.out.println("isLeaf: "+Byte.toString(byteBuf.get()));
//       tmpStr=String.valueOf(byteBuf.get());
//       bytes=tmpStr.getBytes();
//       tmpBuf.put(bytes);
//       //System.out.println("isRoot: "+Byte.toString(byteBuf.get()));
//       tmpStr=String.valueOf(byteBuf.get());
//        bytes=tmpStr.getBytes();
//        tmpBuf.put(bytes);        
//       //int NumOfKeys= byteBuf.getInt();
//       //System.out.println("NumOfKeys: "+String.valueOf(NumOfKeys));
//        tmpStr=String.valueOf(byteBuf.getInt());
//        bytes=tmpStr.getBytes();
//        tmpBuf.put(bytes);
//        byteBuf.position(Settings.dataStartPos);
//       //System.out.println("leftPagePointer: "+String.valueOf(byteBuf.getInt()));
//        tmpStr=String.valueOf(byteBuf.getInt());
//        bytes=tmpStr.getBytes();
//        tmpBuf.put(bytes); 
//        
//        divNum=byteBuf.getInt();
//       //System.out.println("Div num: "+String.valueOf(divNum));
//       tmpStr=String.valueOf(byteBuf.getInt());
//       bytes=tmpStr.getBytes();
//       tmpBuf.put(bytes);               
//       for(int i=0;i<divNum;i++)
//       {
//            System.out.println("DeweyId: "+Byte.toString(byteBuf.get()));
//       }
//       int recLength;
//       int qLength;
//       int curCid;
//       
//       recLength=byteBuf.getInt();//assumption:recLength=DeweyIdsize+4(Cid)
//                qLength=byteBuf.getInt();
//                curCid=byteBuf.getInt();
//                curId = new byte[recLength-Settings.sizeOfInt];
//                curDId=new DeweyID();
//                xBufChannel.get(curId,0,recLength-Settings.sizeOfInt);
//                curDId.setDeweyId(curId);
//                curQnameSeq=new byte[strLength];
//                curQnameStr=new String(curQnameSeq);
//                xBufChannel.get(curQnameSeq,0,strLength);
//                
//       
//       byteBuf.clear(); 
//    }
   /* public void convertPageContent(int pageNum,InternalBuffer xBuf)
    {
        counter++;
        int divNum;
        ByteBuffer byteBuf;
        byteBuf=xBuf.getByteBuffer();
        try
        {
            fChannel=fChannel.position(pageNum*1024);
            fChannel.read(this.byteBuf);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
       byteBuf.position(0);
       System.out.println("Page State: "+Byte.toString(byteBuf.get()));
       System.out.println("Next Page: "+String.valueOf(byteBuf.getInt()));
       int freeOffset=byteBuf.getInt();
       System.out.println("Free Offset: "+String.valueOf(freeOffset));
       System.out.println("isLeaf: "+Byte.toString(byteBuf.get()));
       System.out.println("isRoot: "+Byte.toString(byteBuf.get()));
       int NumOfKeys= byteBuf.getInt();
       System.out.println("NumOfKeys: "+String.valueOf(NumOfKeys));
       byteBuf.position(lastFreeOffset);
       System.out.println("leftPagePointer: "+String.valueOf(byteBuf.getInt()));
       divNum=byteBuf.getInt();
       System.out.println("Div num: "+String.valueOf(divNum));
       for(int i=0;i<divNum;i++)
       {
            System.out.println("DeweyId: "+Byte.toString(byteBuf.get()));
       }
       System.out.println("RightPointer: "+String.valueOf(byteBuf.getInt()));
       lastFreeOffset=freeOffset; 
       byteBuf.clear(); 
    }*/

{/* public void convertGMDContent()
    {
       try
        {
            fChannel.position(0);
            fChannel.read(byteBuf);
        } catch (IOException e) {
            e.printStackTrace();
        }
       byteBuf.position(0);
       System.out.println("Page State: "+Byte.toString(byteBuf.get()));
       System.out.println("Next Page: "+String.valueOf(byteBuf.getInt()));
       System.out.println("Free Offset: "+String.valueOf(byteBuf.getInt()));
       System.out.println("Pages Count: "+String.valueOf(byteBuf.getInt()));
       System.out.println("Watermark Offset: "+String.valueOf(byteBuf.getInt()));
       int filesCount=byteBuf.getInt();
       System.out.println("Files Count: "+String.valueOf(filesCount));
       int mdlength=byteBuf.getInt();
       byte[]md=new byte[mdlength];
       byteBuf.get(md, 0, mdlength);   
       String s=new String(md);
       System.out.println("First Page: "+s);
       int length;
       byte[]fileName;
       for(int i=0;i<filesCount;i++)
       {
           length=byteBuf.getInt();
           fileName=new byte[length];
           byteBuf.get(fileName, 0, length);   
           s=new String(fileName);           
           System.out.println("LogicalFileName: "+s);
           System.out.println("First Page: "+String.valueOf(byteBuf.getInt()));                  
       
       
       }       
       byteBuf.clear();      
       
    }*/}
    
    public void writePage (int pageNum,InternalBuffer xBuf) throws IOException
    {       
        //int endOfFile=Settings.pageSize-1; 
       ByteBuffer byteBuf;
       int bytesWritten;
       long t1;
       long t2;
       xBuf.setRef(true);
       byteBuf=xBuf.getByteBuffer();
       fChannel.position(pageNum*Settings.pageSize);
       t1=System.currentTimeMillis();
       bytesWritten=fChannel.write(byteBuf);
       t2=System.currentTimeMillis();
       if(bytesWritten==0)
          throw new IOException("Attempt to write to page "+pageNum+" failed.");
       monitor.increaseIOTime(t2-t1);
    }
    public InternalBuffer readPage (int pageNum,InternalBuffer xBuf) throws IOException
    {
       ByteBuffer byteBuf;
       byteBuf=xBuf.getByteBuffer();
       int bytesRead;
       long t1;
       long t2;
       monitor.addReadPagesAttempt();
       fChannel= fChannel.position(pageNum*Settings.pageSize);
       t1=System.currentTimeMillis();
       try{
       bytesRead=fChannel.read(byteBuf);
       }catch(Exception e)
       {
           throw new IOException("test");
       }
       t2=System.currentTimeMillis();
       if(bytesRead==0)
           throw new IOException("Failed Attempt to read from page: "+pageNum);       
       monitor.increaseIOTime(t2-t1);
       monitor.addReadPages();
//           logManager.LogManager.log(0, "page: "+pageNum+" bytesRead: "+bytesRead);
        
        xBuf.setByteBuffer(byteBuf);//ehtemalan ino nemikhad aslan.
        xBuf.setPageNumber(pageNum);
        return xBuf;
    }
    
}

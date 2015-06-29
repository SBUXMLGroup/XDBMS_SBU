
package indexManager;
import indexManager.util.OutputWrapper;
import LogicalFileManager.LogicalFileManager;
import bufferManager.BufferManager;
import bufferManager.InternalBuffer;
import bufferManager.XBufferChannel;
import java.nio.ByteBuffer;
import java.io.IOException;
import xmlProcessor.DeweyID;
import java.util.ArrayList;
import java.util.Stack;
import commonSettings.Settings;
import xmlProcessor.DBServer.DBException;
public class ContentElementNode 
{

    // private byte pageState; //for future use
    private byte isRoot;
    private byte isLeaf;
    //private int parentPointer;
    private ContentMaxKey MaxKey;
    private int numOfKeys;
    private int freeOffset;
    private int clue=-1;
    //minkeys=20
    //private ByteBuffer byteBuf;
    private int indexPage;
    private XBufferChannel xBufChannel;
    private LogicalFileManager logicFileMgr;
    private String indexName;
    private BufferManager bufMngr;
    
    private int xbufMarker;
    private int bound;//auxillary var for open and next funcs
    

    public ContentElementNode(String indexName,byte isRoot,byte isLeaf,int numOfKeys) throws IOException, DBException
    {
        //creat:
        this.indexName=indexName;
        this.isRoot=isRoot;
        this.isLeaf=isLeaf;
        this.numOfKeys=numOfKeys;
        logicFileMgr = LogicalFileManager.Instance;
        bufMngr = BufferManager.getInstance();
        //InternalBuffer=new xBufChannel();//vasl bshe b buf mgr:getfreepage()
        xBufChannel=bufMngr.getFreePage();
        this.indexPage=xBufChannel.getPageNumber();
        xBufChannel.clear();
        MaxKey=new ContentMaxKey();
        //init values:
        xbufMarker=Settings.dataStartPos;
        //bound=numOfKeys;
                
    }

    public ContentElementNode(int indexPage,String indexName) throws IOException, DBException 
    {
        //LOAD btreeNode:
        logicFileMgr = LogicalFileManager.Instance;
        this.indexName=indexName;
        this.indexPage=indexPage;
        bufMngr=BufferManager.getInstance();
        xBufChannel=bufMngr.getPage(indexPage); //inja wMra
        xBufChannel.position(Settings.freeOffsetPos);//5
        freeOffset = xBufChannel.getInt();
        //byteBuf=xBuf.getByteBuffer();
        isRoot=xBufChannel.get();
        isLeaf = xBufChannel.get();
        //InternalBuffer.position(Settings.numOfKeysPos);
        numOfKeys = xBufChannel.getInt();
        MaxKey=new ContentMaxKey();
        xbufMarker=Settings.dataStartPos;
        bound=numOfKeys;
    }
    
    public void loadContentElementNode(int indexPage,String indexName) throws IOException, DBException 
    {
        //LOAD ExtendedbtreeNode: 
        this.indexPage=indexPage;
        xBufChannel = bufMngr.getPage(indexPage);
        xBufChannel.position(Settings.freeOffsetPos);//5
        freeOffset = xBufChannel.getInt();
        //byteBuf=xBuf.getByteBuffer();
        isRoot=xBufChannel.get();
        isLeaf = xBufChannel.get();
        //InternalBuffer.position(Settings.numOfKeysPos);
        numOfKeys = xBufChannel.getInt();        
        xbufMarker=Settings.dataStartPos;
        bound=numOfKeys;
    }

    public boolean IsLeaf() {
        if (isLeaf == 1) {
            return true;
        } else {
            return false;
        }
    }

    public boolean IsRoot() 
    {
        if (isRoot == 1) {
            return true;
        } else {
            return false;
        }
    }

    public int internalContentElemSearch(int CID,String qName,DeweyID deweyId) 
    {
        //page: pagestate,nextpage,freeoffset
        //ps,isroot,isleaf,number of keys in this page,freeoff,nextp(leftmost),deweyId,nextp,deweyId,nextp(rightmost)
        int pagePointer = -1;//null
        int recLength;
        byte[] curId;
        int curCID;
        int curQLen;
        String curQName;
        byte[] curQSeq;
        DeweyID curDID;
        xBufChannel.position(Settings.dataStartPos);         
        if (numOfKeys > 0)
        {
            for (int i = 0; i < numOfKeys; i++)
            {               
                pagePointer = xBufChannel.getInt();//next page adrs                
                curCID=xBufChannel.getInt();
                curQLen=xBufChannel.getInt();
                curQSeq=new byte[curQLen];
                xBufChannel.get(curQSeq, 0, curQLen);
                curQName=new String(curQSeq);
                recLength = xBufChannel.getInt(); //the length of DeweyID
                curId = new byte[recLength];
                xBufChannel.get(curId,0,recLength);                
                curDID=new DeweyID(curId);
                if (CID==curCID) 
                {
                    if(qName.equals(curQName))
                    {   if(!(curDID.notGreaterThan(deweyId)) || curDID.equals(deweyId))
                       {    //dID<curDID || DID==curDID
                    //key is inside the page which curCID is its maxkey                    
                        break;
                       }
                    }
                    else if(qName.compareTo(curQName)<0)
                        break;
                }
                else if(CID<curCID)
                {
                    //DID's are not comparable.
                    break;
                }
            }
            
        }
        return pagePointer;//page i ro bar migardune k result max un hast.
        //ki<k<ki+1

    }
 public int internalContentElemSearch(int CID) 
    {
        //page: pagestate,nextpage,freeoffset
        //ps,isroot,isleaf,number of keys in this page,freeoff,nextp(leftmost),deweyId,nextp,deweyId,nextp(rightmost)
        int pagePointer = -1;//null
        int recLength;
        byte[] curId;
        int curCID;
        int curQLen;
        String curQName;
        byte[] curQSeq;
        DeweyID curDID=null;;
        xBufChannel.position(Settings.dataStartPos); 
        //logManager.LogManager.log(6,"page:"+xBufChannel.getPageNumber());                
        if (numOfKeys > 0)
        {
            for (int i = 0; i < numOfKeys; i++)
            {
                //inja bayad nahveye khanadan taghir konad!!!!
                pagePointer = xBufChannel.getInt();//next page adrs                
                curCID=xBufChannel.getInt();
                curCID=xBufChannel.getInt();
                curQLen=xBufChannel.getInt();
                curQSeq=new byte[curQLen];
                xBufChannel.get(curQSeq, 0, curQLen);
                curQName=new String(curQSeq);
                recLength = xBufChannel.getInt();
                curId = new byte[recLength-Settings.sizeOfInt];
                xBufChannel.get(curId,0,recLength-Settings.sizeOfInt);
                curDID=new DeweyID(curId);
                if (CID<=curCID) 
                {
                    //key is inside the page which curCID is its maxkey
                    break;
                }
            }
            
        }
        return pagePointer;//page i ro bar migardune k result max un hast.
        //ki<k<ki+1
    }
    public boolean leafContentElemSearch(int CID,String qName,DeweyID deweyID)
    {
        //dar barg ha bayd pagePointer hamun null bashand,ya aslan nabashand.(tu method insert taiin mishe)
        int pagePointer = -1; //null
        int recLength;
        byte[] curId;
        int curCID;
        int curQLen;
        String curQName;
        byte[] curQSeq;
        DeweyID curDID;
        xBufChannel.position(Settings.dataStartPos);//3+4+4??????
        if(numOfKeys>0)
        for (int i = 0; i < numOfKeys; i++)
        {
            //pagePointer = xBufChannel.getInt();//next page adrs
                       
            curCID=xBufChannel.getInt();
            curQLen=xBufChannel.getInt();
            curQSeq=new byte[curQLen];
            xBufChannel.get(curQSeq, 0, curQLen);
            curQName=new String(curQSeq);
            recLength = xBufChannel.getInt();//=dId length
            curId = new byte[recLength];
            xBufChannel.get(curId, 0, recLength);
            curDID=new DeweyID(curId);            
            if (curCID==CID) 
            {
                if(qName.equals(curQName))
                {  
                     if(curDID.equals(deweyID))
                       {                                        
                           return true;
                       }
                }
                 else if(qName.compareTo(curQName)<0)//ehtemaln kamtar az 0 means koochiktar???
                      return false;
                
                else if(deweyID.notGreaterThan(curDID))
                {
                   return false;
                }
                //if(dId>curDId) there is still chances to find equal curDId in the next iterations
            } 
            else if (CID<curCID)
            {
                return false; //key not found
            }
            
            
        }
        return false;//not found

    }
    //used for scan:
     public int leafContentElemSearch(int CID)
    {
        //outputs record start pos in page 
        //dar barg ha bayd pagePointer hamun null bashand,ya aslan nabashand.(tu method insert taiin mishe)
        int pagePointer = -1; //null
        int recLength;
        byte[] curId=null;
        int curCID;
        int curQLen;
        String curQName;
        byte[] curQSeq;
        int offset;
        DeweyID curDID = null;
        xBufChannel.position(Settings.dataStartPos);//3+4+4??????
        if(numOfKeys>0)
        {
            for (int i = 0; i < numOfKeys; i++)
            {
                //pagePointer = xBufChannel.getInt();//next page adrs                       
                curCID=xBufChannel.getInt();
                curQLen=xBufChannel.getInt();
                curQSeq=new byte[curQLen];
                xBufChannel.get(curQSeq, 0, curQLen);
                curQName=new String(curQSeq);
                recLength = xBufChannel.getInt();//=dId length
                curId=new byte[recLength];
                xBufChannel.get(curId, 0,recLength);
//                curDID=new DeweyID();
//                curDID.setDeweyId(curId);
                curDID=new DeweyID(curId);
                //result = curCID;
                if (curCID==CID) 
                {
                    offset=xBufChannel.position()-(recLength+Settings.sizeOfInt);
                    return offset; //key found                
                } 
                else if (CID<curCID)
                {
                    return -1; //key not found
                }
            }
        }
        return -1;//not found

    }
     //USED FOR FINFDING LEFTMOST LEAVE,
     public int leftMostPointer() throws IOException
     {
        //outputs record start pos in page 
        //dar barg ha bayd pagePointer hamun null bashand,ya aslan nabashand.(tu method insert taiin mishe)
        int pagePointer = -1; //null
        int firstPP=-1;
        int p=0;
        xBufChannel.position(Settings.dataStartPos);//3+4+4  
//        for(int i=0;i<numOfKeys;i++)
//        {
          pagePointer = xBufChannel.getInt();//next page adrs 
//          if(i==0)
//              firstPP=pagePointer;
//          int dLen=xBufChannel.getInt();
//          int cid=xBufChannel.getInt();
//          byte[] dID=new byte[dLen-Settings.sizeOfInt];
//          xBufChannel.get(dID, 0, dLen-Settings.sizeOfInt);
//          DeweyID ddID=new DeweyID(dID);
//          ElementBTreeNode elemBTreeNode = new ElementBTreeNode(pagePointer, indexName);
//          elemBTreeNode.closeElementNode();
//          p++;
//        }
        return pagePointer;//not found   
         
     }
   public int insertIntoLeafContentElem(int CID,String cont,DeweyID deweyID,int rootPointer,Stack parents) throws IOException, DBException {

        ArrayList<Integer> CIDList = new ArrayList<>();
        ArrayList<String> contList=new ArrayList<>();
        ArrayList<DeweyID> dIDList = new ArrayList<>();
        //ArrayList<Integer> pagePointerList = new ArrayList<>();
        short[] elementPositionArray;
        int dLen;
        int contLen;
        int keyLength;
        int keyContLen;
        //int pagePointer;
        int curContLen;
        String curCont;
        byte[] curContSeq;
        byte[] curId;
        boolean keyInserted=false;
        boolean splitted=false;
        byte[] temp1;
        byte[] temp2;
        int[] midPoint;
        int curCid;
        DeweyID curDId;
        ContentMaxKey lastMaxKey =new ContentMaxKey();
        xBufChannel.position(Settings.dataStartPos);//3+4+4 ps+nextPage+freeOffset+isL+isR+numOf
        //in nextPage bayd hatman bashe b hatere yaftane akharin page
        if (numOfKeys > 0) 
        {
            for (int i = 0; i < numOfKeys; i++) 
            {
               //assumption:not have pagePointer in leaves                            
                curCid=xBufChannel.getInt();
                curContLen=xBufChannel.getInt();
                curContSeq=new byte[curContLen];
                xBufChannel.get(curContSeq,0,curContLen);
                curCont=new String(curContSeq);
                dLen=xBufChannel.getInt();//assumption:dLen=DeweyId size
                curId = new byte[dLen];              
                xBufChannel.get(curId,0,dLen);                
                curDId=new DeweyID(curId);
                if(i==numOfKeys-1)
                {
                    lastMaxKey.maxCID=curCid;
                    lastMaxKey.maxContent=curCont;
                    lastMaxKey.maxDeweyID=curDId;
                }                
                if (!keyInserted && (CID<=curCid)) 
                {
                    if(CID==curCid)
                    {
                       if(cont.equals(curCont))
                       {  
                           if(deweyID.notGreaterThan(curDId))
                           {                                        
                               CIDList.add(CID);//cheghad ghashang mishe inaro nevesht :)))
                               contList.add(cont);
                               dIDList.add(deweyID);
                               keyInserted=true;    
                           }
                    }
                   else if(cont.compareTo(curCont)<0)//ehtemaln kamtar az 0 means koochiktar???
                   {
                        CIDList.add(CID);
                        contList.add(cont);
                        dIDList.add(deweyID);
                        keyInserted=true;    
                   }  
                 } //end if(CID==curCid)
                 else 
                    {
                        CIDList.add(CID);//cheghad ghashang mishe inaro nevesht :)))
                        contList.add(cont);
                        dIDList.add(deweyID);
                        keyInserted=true;
                    }
                }// end if (!keyInserted && (CID<=curCid))
                CIDList.add(curCid);
                contList.add(curCont);
                dIDList.add(curDId);
            }
            //pagePointer = xBufChannel.getInt();//rightMost pointer NADARIM
            // pagePointerList.add(pagePointer);
            if (!keyInserted) 
            {
                
                CIDList.add(CID);
                contList.add(cont);
                dIDList.add(deweyID);
                this.MaxKey.maxCID=CID;
                this.MaxKey.maxContent=cont;
                this.MaxKey.maxDeweyID=deweyID;
                keyInserted=true;
                //else : lastMax is equal to max!
            }
            numOfKeys++;
            //Prepare buffer:
            xBufChannel.position(Settings.numOfKeysPos);
            xBufChannel.putInt(numOfKeys);
            //TODO: byte to short!!!
            //Note:LastNumOfKeys=numOfKeys-1
            keyLength = deweyID.getDeweyId().length;//dID DivNumber+cid
            keyContLen=cont.length();
            int availableSpace=Settings.pageSize - 1 - freeOffset - 2*(numOfKeys-1);//lastNumOfKeys=numOfKeys-1
            int requiredSpace=3*Settings.sizeOfInt+keyContLen+keyLength+2;//2bytes for postion
//            
            if (availableSpace <(requiredSpace+10))//+ 4+4
            {
                //reading starting position of every record inside the page
                //these positions are written at the end of the page,last time before unpinning(releasing) the page.
                elementPositionArray = new short[numOfKeys-1];
                
                xBufChannel.position(Settings.pageSize - 1 - (numOfKeys-1)* Settings.sizeOfShort);//az tahe page shoru mikonim b khundan
                for (int i = 0; i < (numOfKeys-1); i++) {
                    elementPositionArray[i] = xBufChannel.getShort();
                }
                midPoint = computeMidPoint(elementPositionArray);
                rootPointer=this.splitLeafContentElem(midPoint,CIDList,contList, dIDList,CID,cont, deweyID, rootPointer, parents);
                splitted=true;
            }
        
        
        //inja chon rewrite darim bayad dar noghteye data start bashim,yani bad az mahale num of keys
             //in bakhsh dar 2 halat ejra mishe:
            //1)vaghti split nadarim.
            //2)bad az split
           xBufChannel.position(Settings.dataStartPos);
           elementPositionArray = new short[numOfKeys];  
          ContentElementNode parentNode;
           int parentPointer;
           if(!splitted && parents.size()>0)//if split did not happen!:
           {
              if(lastMaxKey.maxCID<this.MaxKey.maxCID || (lastMaxKey.maxCID==this.MaxKey.maxCID && lastMaxKey.maxContent.compareTo(MaxKey.maxContent)<0) ||
                     (lastMaxKey.maxCID==this.MaxKey.maxCID && lastMaxKey.maxContent.compareTo(MaxKey.maxContent)==0 && !lastMaxKey.maxDeweyID.equals(this.MaxKey.maxDeweyID)&& 
                      lastMaxKey.maxDeweyID.notGreaterThan(this.MaxKey.maxDeweyID)))//SPLIT: last>max, without SPLIT:last<=max
              {
                      parentPointer=(int)parents.pop();
                      parentNode=new ContentElementNode(parentPointer,indexName);
                      rootPointer=parentNode.updateInternalContentElem(MaxKey, rootPointer, rootPointer, parents);
                      //parents.push(parentPointer);
                      parentNode.closeContentElemNode();
              }
           }
           
           //reset freeoffset
           freeOffset=Settings.dataStartPos;
           for (int i = 0; i < numOfKeys; i++)
           {
              //length=dIDList.get(i).getDeweyId().dLen;
              temp1=contList.get(i).getBytes();
              contLen=temp1.length;
              temp2 = dIDList.get(i).getDeweyId();     
              dLen = temp2.length;
              elementPositionArray[i]=(short)xBufChannel.position();
                           
              xBufChannel.putInt(CIDList.get(i));
              xBufChannel.putInt(contLen);
              xBufChannel.put(temp1,0, contLen);
              xBufChannel.putInt(dLen);
              xBufChannel.put(temp2, 0, dLen);
//              if(splitted) //&& (i==0 || i==numOfKeys-1))
//                 logManager.LogManager.log(6,CIDList.get(i).toString());             
          
              freeOffset+=3*Settings.sizeOfInt+contLen+dLen;
           }

           //for puting ZERO(erasing the rest of page)
          xBufChannel.erase(xBufChannel.position(),numOfKeys);
           
        //******************
        xBufChannel.position(Settings.pageSize - 1 - numOfKeys * Settings.sizeOfShort);//az tahe page shoru mikonim b khundan
        for(int i=0;i<numOfKeys;i++) //loop for setting elem positions at the end of page
        {             
             xBufChannel.putShort(elementPositionArray[i]);
        }
       }//****** endIF(num>0)
        else 
        {
            keyLength = deweyID.getDeweyId().length;//key DivNumber+ cid len
            keyContLen=cont.getBytes().length;
            //----------------
            numOfKeys++;
           //Prepare buffer:
            xBufChannel.position(Settings.numOfKeysPos);
            xBufChannel.putInt(numOfKeys);
            //--------------------------
            this.MaxKey.maxCID=CID;
            this.MaxKey.maxContent=cont;
            this.MaxKey.maxDeweyID=deweyID;
            
            short firstElementPosition;
            firstElementPosition=(short)xBufChannel.position();
            
            xBufChannel.putInt(CID);
            xBufChannel.putInt(keyContLen);
            temp1=cont.getBytes();
            xBufChannel.put(temp1,0,keyContLen);
            xBufChannel.putInt(keyLength);
            temp2 = deweyID.getDeweyId();
            xBufChannel.put(temp2,0,keyLength);
            //for puting ZERO(erasing the rest of page)
            xBufChannel.erase(xBufChannel.position(),numOfKeys);           
            xBufChannel.position(Settings.pageSize - 1 - numOfKeys * Settings.sizeOfShort);//az tahe page shoru mikonim b khundan
            xBufChannel.putShort(firstElementPosition);            
            freeOffset= freeOffset+3*Settings.sizeOfInt+keyContLen+keyLength;
            
        }
        xBufChannel.position(Settings.freeOffsetPos);
        xBufChannel.putInt(freeOffset);        
        logManager.LogManager.log(1,"CID: "+CID+" in page: "+ xBufChannel.getPageNumber());
        return rootPointer;
    }
   
   public int insertIntoInternalContentElem(int CID,String cont,DeweyID deweyID,int rootPointer,int childPointer,Stack parents) throws IOException, DBException 
   {
       //record structure in internal nodes: pagepointer,recLen,CID,deweyId
        ArrayList<Integer> CIDList = new ArrayList<>();
        ArrayList<String> contList=new ArrayList<>();
        ArrayList<DeweyID> dIDList = new ArrayList<>();
        ArrayList<Integer> pagePointerList = new ArrayList<>();
        short[] elementPositionArray;//?
        int dLen;
        int contLen;
        int keyLength;
        int keyContLen;
        int pagePointer;
        byte[] curId;
        boolean keyInserted=false;
        boolean splitted=false;
        byte[] temp1;
        byte[] temp2;
        int[] midPoint;
        int curCId;
        String curCont;
        int curContLen;
        byte[] curContSeq;
        DeweyID curDId;
        ContentMaxKey lastMaxKey=new ContentMaxKey();  
//        if(childPointer==10)
//        {   
//            logManager.LogManager.log(6,"insertInternal:4 in "+xBufChannel.getPageNumber());
//            clue=xBufChannel.getPageNumber();            
//        }   
        xBufChannel.position(Settings.dataStartPos);//3+4+4 ps+nextPage+freeOffset+isL+isR+numOf
        //in nextPage bayd hatman bashe b hatere yaftane akharin page
        if (numOfKeys > 0) 
        {
            for (int i = 0; i < numOfKeys; i++) 
            {
                pagePointer = xBufChannel.getInt();//next page adrs 
                curCId=xBufChannel.getInt();
                curContLen=xBufChannel.getInt();
                curContSeq=new byte[curContLen];
                xBufChannel.get(curContSeq,0,curContLen);
                curCont=new String(curContSeq);
                dLen = xBufChannel.getInt();
                curId = new byte[dLen];                
                xBufChannel.get(curId, 0, dLen);                
                curDId=new DeweyID(curId);
                if(i==numOfKeys-1)
                {
                    lastMaxKey.maxCID=curCId;
                    lastMaxKey.maxContent=curCont;
                    lastMaxKey.maxDeweyID=curDId;
                } 
                
                if (!keyInserted && (CID<=curCId)) 
                {
                    
                    if(CID==curCId)
                    {
                       if(cont.equals(curCont))
                       {  
                           if(deweyID.notGreaterThan(curDId))
                           {                                        
                               pagePointerList.add(childPointer);
                               CIDList.add(CID);//cheghad ghashang mishe inaro nevesht :)))
                               contList.add(cont);
                               dIDList.add(deweyID);
                               keyInserted=true;    
                           }
                       }
                       else if(cont.compareTo(curCont)<0)//ehtemaln kamtar az 0 means koochiktar???
                       {
                        pagePointerList.add(childPointer);
                        CIDList.add(CID);
                        contList.add(cont);
                        dIDList.add(deweyID);
                        keyInserted=true;    
                       }  
                    }//end if(CID==curCId)
                 else
                    {
                        pagePointerList.add(childPointer);                        
                        CIDList.add(CID);
                        contList.add(cont);
                        dIDList.add(deweyID);
                        keyInserted=true;
                    }
                }
                pagePointerList.add(pagePointer);
                CIDList.add(curCId);
                contList.add(curCont);
                dIDList.add(curDId);
            }
            //pagePointer = xBufChannel.getInt();//right most pointer NADARIM
            // pagePointerList.add(pagePointer);
            if (!keyInserted) 
            {
                pagePointerList.add(childPointer);
                CIDList.add(CID);
                contList.add(cont);
                dIDList.add(deweyID);
                this.MaxKey.maxCID=CID;
                this.MaxKey.maxContent=cont;
                this.MaxKey.maxDeweyID=deweyID;
                keyInserted=true;
            }
            numOfKeys++;
            //Prepare buffer:
            xBufChannel.position(Settings.numOfKeysPos);
            xBufChannel.putInt(numOfKeys);
            //TODO: byte to short!!!
            //Note:LastNumOfKeys=numOfKeys-1
            dLen = deweyID.getDeweyId().length;//DID DivNumber
            keyContLen=cont.getBytes().length;
            int availableSpace=Settings.pageSize - 1 - freeOffset - 2*(numOfKeys-1);//lastNumOfKeys=numOfKeys-1
            int requiredSpace=3*Settings.sizeOfInt +keyContLen+dLen + Settings.pagePointerSize+2; //2bytes for position      
            if (availableSpace <(requiredSpace+10))//+ 4+4
            {
                elementPositionArray = new short[numOfKeys-1];
                xBufChannel.position(Settings.pageSize - 1 - (numOfKeys-1)* Settings.sizeOfShort);//az tahe page shoru mikonim b khundan
                for (int i = 0; i < (numOfKeys-1); i++) {
                    elementPositionArray[i] = xBufChannel.getShort();
                }
                midPoint = computeMidPoint(elementPositionArray);
                rootPointer=this.splitInternalContentElem(midPoint, CIDList, contList, dIDList, pagePointerList, CID, cont, deweyID, rootPointer, parents);
            }
        
        
        //inja chon rewrite darim bayad dar noghteye data start bashim,yani bad az mahale num of keys
        //if(numOfKeys>1)//chon k ++ shode!
        //{
           xBufChannel.position(Settings.dataStartPos);
           elementPositionArray = new short[numOfKeys];  
           ContentElementNode parentNode;
           int parentPointer;
           if(!splitted && !parents.empty())
           {                
              if(lastMaxKey.maxCID<this.MaxKey.maxCID || (lastMaxKey.maxCID==this.MaxKey.maxCID && lastMaxKey.maxContent.compareTo(MaxKey.maxContent)<0) ||
                     (lastMaxKey.maxCID==this.MaxKey.maxCID && lastMaxKey.maxContent.compareTo(MaxKey.maxContent)==0 && !lastMaxKey.maxDeweyID.equals(this.MaxKey.maxDeweyID)&& 
                      lastMaxKey.maxDeweyID.notGreaterThan(this.MaxKey.maxDeweyID)))//SPLIT: last>max, without SPLIT:last<=max         
              {
                  parentPointer=(int)parents.pop();
                  parentNode=new ContentElementNode(parentPointer,indexName);
                  rootPointer=parentNode.updateInternalContentElem(this.MaxKey,rootPointer,this.indexPage,parents);
                  //parents.push(parentPointer);
                  parentNode.closeContentElemNode();
              }
//                  
           }
          
           //reset freeoffset
           freeOffset=Settings.dataStartPos;
           for (int i = 0; i < numOfKeys; i++)
           {
              temp1=contList.get(i).getBytes();
              curContLen=temp1.length;
              temp2 = dIDList.get(i).getDeweyId();        
              dLen = temp2.length;
              elementPositionArray[i]=(short)xBufChannel.position();
              xBufChannel.putInt(pagePointerList.get(i));              
              xBufChannel.putInt(CIDList.get(i));
//              if(splitted) //&& (i==0 || i==numOfKeys-1))
//                 logManager.LogManager.log(6,CIDList.get(i).toString());
              xBufChannel.putInt(curContLen);
              xBufChannel.put(temp1,0,curContLen);
              xBufChannel.putInt(dLen);
              xBufChannel.put(temp2,0,dLen);
              freeOffset+=4*Settings.sizeOfInt+curContLen+dLen;
           }

           // for putting zero(erasing the rest of page)
           xBufChannel.erase(xBufChannel.position(),numOfKeys);
        //******************
        xBufChannel.position(Settings.pageSize - 1 - numOfKeys * Settings.sizeOfShort);//az tahe page shoru mikonim b khundan
        for(int i=0;i<numOfKeys;i++) //loop for setting elem positions at the end of page
        {             
             xBufChannel.putShort(elementPositionArray[i]);
        }
        
        }
        //********************************************************
        else 
        {
            keyLength = deweyID.getDeweyId().length;//key DivNumber
            keyContLen=cont.getBytes().length;
            //----------------
            numOfKeys++;
           //Prepare buffer:
            xBufChannel.position(Settings.numOfKeysPos);
            xBufChannel.putInt(numOfKeys);
            //--------------------------
            this.MaxKey.maxCID=CID;
            this.MaxKey.maxContent=cont;
            this.MaxKey.maxDeweyID=deweyID;
            
            short firstElementPosition;
            firstElementPosition=(short)xBufChannel.position();
            xBufChannel.putInt(childPointer); 
            xBufChannel.putInt(CID);
            temp1=cont.getBytes();
            xBufChannel.putInt(keyContLen);
            xBufChannel.put(temp1,0,keyContLen);
            xBufChannel.putInt(keyLength);
            temp2 = deweyID.getDeweyId();
            xBufChannel.put(temp2,0,keyLength);
            xBufChannel.position(Settings.pageSize-1-numOfKeys * Settings.sizeOfShort);//az tahe page shoru mikonim b khundan
            xBufChannel.putShort(firstElementPosition);            
            freeOffset= freeOffset+4*(Settings.sizeOfInt)+keyContLen+keyLength;
            
        }
        xBufChannel.position(Settings.freeOffsetPos);
        xBufChannel.putInt(freeOffset);
        //bufMngr.unpinBuffer(xBufChannel, true);
        return rootPointer;
   }
   public int insUpdaterContentElem(ContentMaxKey upMaxKey,int upChildPointer,ContentMaxKey insMaxKey,int insChildPointer,int rootPointer,Stack parents) throws IOException, DBException
   {
       
        //record structure in internal nodes: pagepointer,recLen,CID,deweyId
        
        ArrayList<Integer> pagePointerList = new ArrayList<>();
        ArrayList<Integer> CIDList = new ArrayList<>();
        ArrayList<String> contList = new ArrayList<>();
        ArrayList<DeweyID> dIDList = new ArrayList<>();
        short[] elementPositionArray;//?
        //int dLen;
        int prevMaxLength=0;
        int upMaxKeyLength = 0;
        int insMaxKeyLength;
        int maxLength;
        int dLen;
        int curContLen;
        int lastFreeOffset;
        int estimatedFreeOffset;
        int pagePointer;
        int[] midPoint;
        byte[] curId;
        boolean splitted=false;
        boolean keyInserted=false;
         byte[] temp1;
        byte[] temp2;
        int curCId=-1;
        String curCont;
        byte[] curContSeq;
        DeweyID curDId;
        ContentMaxKey lastMaxKey=new ContentMaxKey();
        xBufChannel.position(Settings.dataStartPos);//3+4+4 ps+nextPage+freeOffset+isL+isR+numOf
        //in nextPage bayd hatman bashe b hatere yaftane akharin page
        if (numOfKeys > 1) 
        {
            for (int i = 0; i < numOfKeys; i++) 
            {
                pagePointer = xBufChannel.getInt();//next page adrs               
                curCId=xBufChannel.getInt();
                curContLen=xBufChannel.getInt();
                curContSeq=new byte[curContLen];
                xBufChannel.get(curContSeq, 0, curContLen);
                curCont=new String(curContSeq);
                dLen = xBufChannel.getInt();
                curId = new byte[dLen];                
                xBufChannel.get(curId, 0, dLen);                
                curDId=new DeweyID(curId);
                if(i==numOfKeys-1)
                {
                    lastMaxKey.maxCID=curCId;
                    lastMaxKey.maxContent=curCont;
                    lastMaxKey.maxDeweyID=curDId;
                }
//dar vaghe bayd vaghti SPLIT mikonim,chon max key kuchekatar ast bayd chek konim k dar jaye dorosti darj shavad
//vali dar in setup hatman max key in node az max node ghabli bishtar ast pas moshkeli nis.
                if (pagePointer==upChildPointer) 
                {
                    //yani b jaye current record darim newMaxkey ra varede list mikonim(khod b khod cur hazf mishe)
                    pagePointerList.add(upChildPointer);
                    CIDList.add(upMaxKey.maxCID);//DO NOT add curCID
                    contList.add(upMaxKey.maxContent);
                    dIDList.add(upMaxKey.maxDeweyID);
                    prevMaxLength=dLen+curContLen;
                   // upMaxKeyLength=upMaxKey.maxDeweyID.getDeweyId().length;//key DivNumber 
                    curCId=upMaxKey.maxCID;
                    curCont=upMaxKey.maxContent;
                    curDId=upMaxKey.maxDeweyID;
                }
                else 
                {
                   if (!keyInserted && (insMaxKey.maxCID<=curCId)) 
                   {
                    if(insMaxKey.maxCID==curCId)
                    {
                        //?????????????:/
                        if(insMaxKey.maxContent.equals(curCont))
                        {  
                           if(insMaxKey.maxDeweyID.notGreaterThan(curDId))
                           {
                                pagePointerList.add(insChildPointer);
                                CIDList.add(insMaxKey.maxCID);
                                contList.add(insMaxKey.maxContent);
                                dIDList.add(insMaxKey.maxDeweyID);
                                keyInserted=true;                                                       
                           }

                       }
                       else if(insMaxKey.maxContent.compareTo(curCont)<0)//ehtemaln kamtar az 0 means koochiktar???
                       {
                        pagePointerList.add(insChildPointer);
                        CIDList.add(insMaxKey.maxCID);
                        contList.add(insMaxKey.maxContent);
                        dIDList.add(insMaxKey.maxDeweyID);
                        keyInserted=true;    
                       } 

                        
                    }//end if(CID==curCId)
                    else //if(CID<curCID)
                    {
                        
                        pagePointerList.add(insChildPointer);
                        CIDList.add(insMaxKey.maxCID);//cheghad ghashang mishe inaro nevesht :)))
                        dIDList.add(insMaxKey.maxDeweyID);
                        keyInserted=true;
                    }
                  }               
                pagePointerList.add(pagePointer);
                CIDList.add(curCId);
                contList.add(curCont);
                dIDList.add(curDId);
                }
              }            
            //pagePointer = xBufChannel.getInt();//right most pointer NADARIM
            // pagePointerList.add(pagePointer);
            if (!keyInserted) 
            {
                pagePointerList.add(insChildPointer);
                CIDList.add(insMaxKey.maxCID);
                contList.add(insMaxKey.maxContent);
                dIDList.add(insMaxKey.maxDeweyID);
                this.MaxKey.maxCID=insMaxKey.maxCID;
                this.MaxKey.maxContent=insMaxKey.maxContent;
                this.MaxKey.maxDeweyID=insMaxKey.maxDeweyID;
                keyInserted=true;
            }
            numOfKeys++;             
            xBufChannel.position(Settings.numOfKeysPos);
            xBufChannel.putInt(numOfKeys);           
            //this.MaxKey.maxCID=CIDList.get(CIDList.size()-1);
            ///ADDED:             
            lastFreeOffset=freeOffset;
            upMaxKeyLength=upMaxKey.maxDeweyID.getDeweyId().length+upMaxKey.maxContent.length();
            insMaxKeyLength=insMaxKey.maxDeweyID.getDeweyId().length+insMaxKey.maxContent.length();
        //key ghabli hazf shode v key jadid ezafe shode:
           estimatedFreeOffset=lastFreeOffset-prevMaxLength+upMaxKeyLength+insMaxKeyLength+4*Settings.sizeOfInt;//Int for insCID

           if (estimatedFreeOffset>= Settings.pageSize-1-numOfKeys*Settings.sizeOfShort) //+5 is for security margin,sth like that.
            {
                elementPositionArray = new short[numOfKeys];
                xBufChannel.position(Settings.pageSize - 1 - (numOfKeys)* Settings.sizeOfShort);//az tahe page shoru mikonim b khundan
                for (int i = 0; i < (numOfKeys); i++) 
                {
                    elementPositionArray[i] = xBufChannel.getShort();
                }
                midPoint = computeMidPoint(elementPositionArray);
                rootPointer=this.splitInternalContentElem(midPoint, CIDList,contList, dIDList, pagePointerList, insMaxKey.maxCID,insMaxKey.maxContent, insMaxKey.maxDeweyID, rootPointer, parents);
                splitted=true;
            }
            
            ContentElementNode parentNode;
            int parentPointer;
            if(!splitted && !parents.empty())
                if(lastMaxKey.maxCID<this.MaxKey.maxCID || (lastMaxKey.maxCID==this.MaxKey.maxCID && lastMaxKey.maxContent.compareTo(MaxKey.maxContent)<0) ||
                     (lastMaxKey.maxCID==this.MaxKey.maxCID && lastMaxKey.maxContent.compareTo(MaxKey.maxContent)==0 && !lastMaxKey.maxDeweyID.equals(this.MaxKey.maxDeweyID)&& 
                      lastMaxKey.maxDeweyID.notGreaterThan(this.MaxKey.maxDeweyID)))//SPLIT: last>max, without SPLIT:last<=max         
                {                      
                    parentPointer=(int)parents.pop();
                    parentNode=new ContentElementNode(parentPointer,indexName);
                    rootPointer=parentNode.updateInternalContentElem(this.MaxKey,rootPointer,this.indexPage,parents);
                    parents.push(parentPointer);
                    parentNode.closeContentElemNode();
                }
            //pagneePointer = xBufChannel.getInt();//rightmost pointer NADARIM
            // pagePointerList.add(pagePointer);
            
           xBufChannel.position(Settings.dataStartPos);
           elementPositionArray=new short[numOfKeys]; 
           //reset freeoffset
           freeOffset=Settings.dataStartPos;
           for (int i=0;i<numOfKeys;i++)
           {
              temp1=contList.get(i).getBytes();
              curContLen=temp1.length;
              temp2 = dIDList.get(i).getDeweyId();                      
              dLen = temp2.length;
              elementPositionArray[i]=(short)xBufChannel.position();
              xBufChannel.putInt(pagePointerList.get(i));              
              xBufChannel.putInt(CIDList.get(i));
              xBufChannel.putInt(curContLen);
              xBufChannel.put(temp1,0,curContLen);
              xBufChannel.putInt(dLen);
              xBufChannel.put(temp2,0,temp2.length);
              freeOffset+=4*Settings.sizeOfInt+curContLen+dLen;
           }
           //for putting zero(erasing the rest of page){may new freeOffset be smaller than last one! }
           xBufChannel.erase(xBufChannel.position(),numOfKeys);
           
        //******************
        xBufChannel.position(Settings.pageSize - 1 - numOfKeys * Settings.sizeOfShort);//az tahe page shoru mikonim b khundan
        for(int i=0;i<numOfKeys;i++) //loop for setting elem positions at the end of page
        {             
             xBufChannel.putShort(elementPositionArray[i]);
        }
        
        }
        //********************************************************
        else if(numOfKeys==1)
        {
            xBufChannel.position(Settings.dataStartPos);
            pagePointer=xBufChannel.getInt();
            if(pagePointer!=upChildPointer)
                throw new DBException("Wrong Page:"+this.indexPage);
            short[] elementPosition=new short[2];
            
            numOfKeys++;
            xBufChannel.position(Settings.numOfKeysPos);
            xBufChannel.putInt(numOfKeys);
            //UPDATING:
            curContLen=upMaxKey.maxContent.length();
            dLen = upMaxKey.maxDeweyID.getDeweyId().length;//key DivNumber
            elementPosition[0]=(short)xBufChannel.position();
            xBufChannel.putInt(upChildPointer);            
            xBufChannel.putInt(upMaxKey.maxCID);
            xBufChannel.putInt(curContLen);
            temp1 = upMaxKey.maxContent.getBytes();
            xBufChannel.put(temp1,0,curContLen);//prevMaxKey gets deleted automatically
            xBufChannel.putInt(dLen);
            temp2 = upMaxKey.maxDeweyID.getDeweyId();
            xBufChannel.put(temp2,0,dLen);//prevMaxKey gets deleted automatically
            freeOffset+=4*(Settings.sizeOfInt)+curContLen+dLen;
            //INSERTING:
            curContLen=insMaxKey.maxContent.length();
            dLen=insMaxKey.maxDeweyID.getDeweyId().length;
            elementPosition[1]=(short)xBufChannel.position();
            xBufChannel.putInt(insChildPointer); 
            xBufChannel.putInt(insMaxKey.maxCID);
            xBufChannel.putInt(curContLen);
            temp1 = insMaxKey.maxContent.getBytes();
            xBufChannel.put(temp1,0,curContLen);//prevMaxKey gets deleted automatically
            xBufChannel.putInt(dLen);
            temp2 = insMaxKey.maxDeweyID.getDeweyId();
            xBufChannel.put(temp2,0,dLen);           
            xBufChannel.position(Settings.pageSize - 1 - numOfKeys * Settings.sizeOfShort);//az tahe page shoru mikonim b khundan
            xBufChannel.putShort(elementPosition[0]);
            xBufChannel.putShort(elementPosition[1]);
            freeOffset+= 4*(Settings.sizeOfInt)+curContLen+dLen;; 
            
        }
        else //if(numOfKeys==0
        {
            throw new DBException("There is no keys in page:"+this.indexPage+" to update!");
        }
        xBufChannel.position(Settings.freeOffsetPos);
        xBufChannel.putInt(freeOffset);
       // bufMngr.unpinBuffer(xBufChannel, true);
        return rootPointer;
   }
    public int updateInternalContentElem( ContentMaxKey newMaxKey,int rootPointer,int childPointer,Stack parents) throws IOException, DBException
    {
         //record structure in internal nodes: pagepointer,recLen,CID,deweyId
        ArrayList<DeweyID> dIDList = new ArrayList<>();
        ArrayList<String> contList = new ArrayList<>();
        ArrayList<Integer> pagePointerList = new ArrayList<>();
        ArrayList<Integer> CIDList = new ArrayList<>();
        short[] elementPositionArray;//?
        
        int prevMaxLength=0;
        int maxKeyLength;
        int maxLength;
        int dLen;
        int curContLen;
        int lastFreeOffset;
        int estimatedFreeOffset;
        int pagePointer;
        int[] midPoint;
        byte[] curId;
        String curCont;        
        byte[] curContSeq;
        int keyLen;
        int keyContLen;
        boolean splitted=false;
        byte[] temp1;
        byte[] temp2;
        int curCId=-1;
        DeweyID curDId;
        ContentMaxKey lastMaxKey=new ContentMaxKey();
        xBufChannel.position(Settings.dataStartPos);//3+4+4 ps+nextPage+freeOffset+isL+isR+numOf
        //in nextPage bayd hatman bashe b hatere yaftane akharin page
        if (numOfKeys > 1) 
        {
            for (int i = 0; i < numOfKeys; i++) 
            {
                pagePointer = xBufChannel.getInt();//next page adrs
                pagePointerList.add(pagePointer);                
                curCId=xBufChannel.getInt();
                curContLen = xBufChannel.getInt();
                curContSeq=new byte[curContLen];
                xBufChannel.get(curContSeq, 0, curContLen);
                curCont=new String(curContSeq);
                dLen = xBufChannel.getInt();
                curId = new byte[dLen];               
                xBufChannel.get(curId, 0, dLen);                
                curDId=new DeweyID(curId);
                if(i==numOfKeys-1)
                {
                    lastMaxKey.maxCID=curCId;
                    lastMaxKey.maxContent=curCont;
                    lastMaxKey.maxDeweyID=curDId;
            }
//dar vaghe bayd vaghti SPLIT mikonim,chon max key kuchekatar ast bayd chek konim k dar jaye dorosti darj shavad
//vali dar in setup hatman max key in node az max node ghabli bishtar ast pas moshkeli nis.
                if (pagePointer==childPointer) 
                {
                    
                    //yani b jaye current record darim newMaxkey ra varede list mikonim(khod b khod cur hazf mishe)
                    CIDList.add(newMaxKey.maxCID);//DO NOT add curCID
                    contList.add(newMaxKey.maxContent);
                    dIDList.add(newMaxKey.maxDeweyID);
                    prevMaxLength=dLen+curContLen; ///                 
                }
                else
                {
                    CIDList.add(curCId);
                    contList.add(curCont);
                    dIDList.add(curDId);
                }
                
            }
            this.MaxKey.maxCID=CIDList.get(CIDList.size()-1);
            this.MaxKey.maxContent=contList.get(contList.size()-1);
            this.MaxKey.maxDeweyID=dIDList.get(dIDList.size()-1);
            ///ADDED:
            maxKeyLength=newMaxKey.maxContent.getBytes().length+newMaxKey.maxDeweyID.getDeweyId().length;//key DivNumber 
            lastFreeOffset=freeOffset;
        //key ghabli hazf shode v key jadid ezafe shode:
           estimatedFreeOffset=lastFreeOffset-prevMaxLength+maxKeyLength;//old code:newMaxKey.getDeweyId().dLen;
       // }
         ///////////////////////////////////////////////////////////////////   
//            int availableSpace=Settings.pageSize - 1 - freeOffset - 2*(numOfKeys);//lastNumOfKeys=numOfKeys-1
//            int requiredSpace=2*Settings.sizeOfInt + dLen +strLength+ Settings.pagePointerSize+2; //2bytes for position
           
            if (estimatedFreeOffset>= Settings.pageSize-1-numOfKeys*Settings.sizeOfShort) //+5 is for security margin,sth like that.
            {
                elementPositionArray = new short[numOfKeys];
                xBufChannel.position(Settings.pageSize - 1 - (numOfKeys)* Settings.sizeOfShort);//az tahe page shoru mikonim b khundan
                for (int i = 0; i < (numOfKeys); i++) 
                {
                    elementPositionArray[i] = xBufChannel.getShort();
                }
                midPoint = computeMidPoint(elementPositionArray);
                rootPointer=this.splitInternalContentElem(midPoint,CIDList,contList,dIDList,pagePointerList,rootPointer,parents);
                splitted=true;
            }
            
            /////////////////////////////////////
            ContentElementNode parentNode;
            int parentPointer;
            if(!splitted && !parents.empty())
                if(lastMaxKey.maxCID<this.MaxKey.maxCID || (lastMaxKey.maxCID==this.MaxKey.maxCID && lastMaxKey.maxContent.compareTo(MaxKey.maxContent)<0) ||
                     (lastMaxKey.maxCID==this.MaxKey.maxCID && lastMaxKey.maxContent.compareTo(MaxKey.maxContent)==0 && !lastMaxKey.maxDeweyID.equals(this.MaxKey.maxDeweyID)&& 
                      lastMaxKey.maxDeweyID.notGreaterThan(this.MaxKey.maxDeweyID)))//SPLIT: last>max, without SPLIT:last<=max   
            {                      
                parentPointer=(int)parents.pop();
                parentNode=new ContentElementNode(parentPointer,indexName);
                rootPointer=parentNode.updateInternalContentElem(this.MaxKey,rootPointer,this.indexPage,parents);
                parents.push(parentPointer);
                parentNode.closeContentElemNode();
            }
            //pagneePointer = xBufChannel.getInt();//right most pointer NADARIM
            // pagePointerList.add(pagePointer);
            
           maxLength = newMaxKey.maxDeweyID.getDeweyId().length;//key DivNumber        
           //inja chon rewrite darim bayad dar noghteye data start bashim,yani bad az mahale num of keys
           //positions may change:
           xBufChannel.position(Settings.dataStartPos);
           elementPositionArray=new short[numOfKeys]; 
           //reset freeoffset
           freeOffset=Settings.dataStartPos;
           for (int i=0;i<numOfKeys;i++)
           {
              temp1=contList.get(i).getBytes();
              curContLen=temp1.length;
              temp2 = dIDList.get(i).getDeweyId();                      
              dLen = temp2.length;
              elementPositionArray[i]=(short)xBufChannel.position();
              
              xBufChannel.putInt(pagePointerList.get(i));              
              xBufChannel.putInt(CIDList.get(i));
              xBufChannel.putInt(curContLen);
              xBufChannel.put(temp1,0,curContLen);
              xBufChannel.putInt(dLen);
              xBufChannel.put(temp2,0,dLen);  
              freeOffset+=4*Settings.sizeOfInt+curContLen+dLen;
           }
           //for putting zero(erasing the rest of page){may new freeOffset be smaller than last one! }
           xBufChannel.erase(xBufChannel.position(),numOfKeys);
           
        //******************
        xBufChannel.position(Settings.pageSize - 1 - numOfKeys * Settings.sizeOfShort);//az tahe page shoru mikonim b khundan
        for(int i=0;i<numOfKeys;i++) //loop for setting elem positions at the end of page
        {             
             xBufChannel.putShort(elementPositionArray[i]);
        }              
               
        }
        //********************************************************
        else 
        {
            keyLen = newMaxKey.maxDeweyID.getDeweyId().length;//key DivNumber
            keyContLen=newMaxKey.maxContent.getBytes().length;
            short firstElementPosition;
            firstElementPosition=(short)xBufChannel.position();
            xBufChannel.putInt(childPointer);
            xBufChannel.putInt(newMaxKey.maxCID);
            temp1=newMaxKey.maxContent.getBytes();
            xBufChannel.putInt(keyContLen);
            xBufChannel.put(temp1,0,keyContLen);        
            temp2 = newMaxKey.maxDeweyID.getDeweyId();
            xBufChannel.putInt(keyLen);
            xBufChannel.put(temp2,0,keyLen);//prevMaxKey gets deleted automatically
            xBufChannel.position(Settings.pageSize - 1 - numOfKeys * Settings.sizeOfShort);//az tahe page shoru mikonim b khundan
            xBufChannel.putShort(firstElementPosition);            
            freeOffset=4*(Settings.sizeOfInt)+keyLen;
            
        }
        xBufChannel.position(Settings.freeOffsetPos);
        xBufChannel.putInt(freeOffset);
        return rootPointer;
    }
    public int splitLeafContentElem(int[] midPoint,ArrayList<Integer> CIDList,ArrayList<String> contList,ArrayList<DeweyID> deweyIDList,int CID,String cont,DeweyID deweyID,int rootPointer,Stack parents) throws IOException, DBException
    {
        ContentElementNode twinNode;
        ContentElementNode rootNode=null;
        int parentPointer=-1;
        ContentElementNode parentNode=null;
        ContentMaxKey prevMaxKey=new ContentMaxKey();
        boolean rootRenewed=false;
        boolean maxKeyChanged=false;               
        if(this.isRoot==1)
        { 
           rootRenewed=true;
           //inja creat static ???
           rootNode=new ContentElementNode(indexName,(byte)1,(byte)0, 0);//just built root has 2 keys
           //this.indexPage=rootPointer;//lazeme?
           rootPointer=rootNode.createContentElemNode();//bayad dar GMD neveshte shavad****
           //tu in noghte nextPage this taghir mikone,man chi kar konam?dobare az file bkhunam?
           //this.parentPointer=rootPointer;
           this.isRoot=0;
           xBufChannel.position(Settings.isRootPos);
           xBufChannel.put(isRoot);//chon this ba unpin write mishe.
           if(this.IsLeaf())
               twinNode=new ContentElementNode(indexName,(byte)0,(byte)1,-1);//numOfKeys is not valid yet
               //isRoot , isLeaf dar xbuf neveshte nashode and.:(dar setMD set mishavad.
           else
               twinNode=new ContentElementNode(indexName,(byte)0,(byte)0,-1);//numOfKeys is not valid yet
         }           
         else 
         {
             rootRenewed=false;
             //load parent node:
             parentPointer=(int)parents.pop();
             parentNode=new ContentElementNode(parentPointer,indexName);//shayd bhetare yek tabe load bnvisim.
             parents.push(parentPointer);
             if(this.IsLeaf())
                twinNode=new ContentElementNode(indexName,(byte)0,(byte)1, -1);//numOfKeys is not valid yet
             else
                 twinNode=new ContentElementNode(indexName,(byte)0,(byte)0, -1);//numOfKeys is not valid yet

         }
                
        //InternalBuffer twin = bufMngr.getPage(twinPageNumber);
        int dLen;
        int curContLen;
        byte[] temp1;
        byte[] temp2;
       ////////////////////////////////////////////////////////////////////
       this.freeOffset=Settings.dataStartPos;//initial value
       twinNode.freeOffset=Settings.dataStartPos; //initial value
       
       xBufChannel.clear();//vali engAR CLEAR NEMISHAVAD CHERA???
       xBufChannel.position(Settings.numOfKeysPos);
        // koja meta data ghabli az ru in pak mishe? 
        //CIDList: c1,c2,...,cid,...c[m1],...
       if (CIDList.indexOf(CID) < midPoint[1]) 
        {
            
            this.numOfKeys=midPoint[1] + 1;
            xBufChannel.putInt(midPoint[1] + 1); //numOfKeys=i+1
            
//                    if(this.MaxKey.maxCID!=CIDList.get(midPoint[1]))
//                    {   
                        prevMaxKey.maxCID=this.MaxKey.maxCID;
                        prevMaxKey.maxContent=this.MaxKey.maxContent;
                        prevMaxKey.maxDeweyID=this.MaxKey.maxDeweyID;
                        this.MaxKey.maxCID=CIDList.get(midPoint[1]);
                        this.MaxKey.maxContent=contList.get(midPoint[1]);
                        this.MaxKey.maxDeweyID=deweyIDList.get(midPoint[1]);
                        maxKeyChanged=true;
//                    }
                            
            //koja baghie metadata twin neveshte mishe?
            twinNode.numOfKeys=CIDList.size() - (midPoint[1] + 1);
            twinNode.xBufChannel.position(Settings.numOfKeysPos);
            twinNode.xBufChannel.putInt(CIDList.size() - (midPoint[1] + 1)); //twin's Num of keys

            for (int i = midPoint[1] + 1; i < deweyIDList.size(); i++) 
            {
                if(i==midPoint[1]+1)
                { 
                    for(int k=0;k<=midPoint[1];k++)
                    {
                        logManager.LogManager.log(5,CIDList.get(k).toString());
                    }
                    logManager.LogManager.log(6,"**************************");
                }
                if(i==CIDList.size()-1)
                 {   
                     twinNode.MaxKey.maxCID=CIDList.get(i);
                     twinNode.MaxKey.maxContent=contList.get(i);
                     twinNode.MaxKey.maxDeweyID=deweyIDList.get(i);                 
                 }
                temp1=contList.get(i).getBytes();
                curContLen=temp1.length;
                temp2 =deweyIDList.get(i).getDeweyId();                
                dLen = temp2.length;
                
                twinNode.xBufChannel.putInt((int)CIDList.get(i)); 
                //if(i==midPoint[1]+1 || i==CIDList.size()-1)
//                   logManager.LogManager.log(5,CIDList.get(i).toString());
                twinNode.xBufChannel.putInt(curContLen);
                twinNode.xBufChannel.put(temp1,0,curContLen);
                twinNode.xBufChannel.putInt(dLen);
                twinNode.xBufChannel.put(temp2,0,dLen);
                twinNode.freeOffset+=3*Settings.sizeOfInt+curContLen+dLen;
            }
        } 
        else 
        {
            this.numOfKeys=midPoint[1];
            xBufChannel.putInt(midPoint[1]); //numOfKeys=i
//            if(this.MaxKey.maxCID!=CIDList.get(midPoint[1]-1))
//            {   
                prevMaxKey.maxCID=this.MaxKey.maxCID;
                prevMaxKey.maxContent=this.MaxKey.maxContent;
                prevMaxKey.maxDeweyID=this.MaxKey.maxDeweyID;
                this.MaxKey.maxCID=CIDList.get(midPoint[1]-1);
                twinNode.MaxKey.maxContent=contList.get(midPoint[1]-1);
                this.MaxKey.maxDeweyID=deweyIDList.get(midPoint[1]-1);
                maxKeyChanged=true;
//            }

            twinNode.numOfKeys=CIDList.size()- midPoint[1];
            twinNode.xBufChannel.position(Settings.numOfKeysPos);
            twinNode.xBufChannel.putInt(twinNode.numOfKeys); //twin Num of keys

            for (int i = midPoint[1]; i < CIDList.size(); i++) 
            {
                if(i==midPoint[1])
                { 
                    for(int k=0;k<midPoint[1];k++)
                    {
                        logManager.LogManager.log(5,CIDList.get(k).toString()+" ");
                    }
                    logManager.LogManager.log(5,"********************");
                    
                }
                 if(i==CIDList.size()-1)//-1?********
                 {  
                     twinNode.MaxKey.maxCID=CIDList.get(i);
                     twinNode.MaxKey.maxContent=contList.get(i);
                     twinNode.MaxKey.maxDeweyID=deweyIDList.get(i);
                 }
               temp1=contList.get(i).getBytes();
               curContLen=temp1.length;
               temp2 = deweyIDList.get(i).getDeweyId();
               dLen = temp2.length;
               
               twinNode.xBufChannel.putInt(CIDList.get(i));
               twinNode.xBufChannel.putInt(curContLen);
               twinNode.xBufChannel.put(temp1, 0,curContLen);
               //if(i==midPoint[1] || i==CIDList.size()-1)
//                  logManager.LogManager.log(6,CIDList.get(i).toString());
               twinNode.xBufChannel.putInt(dLen);
               twinNode.xBufChannel.put(temp2, 0,dLen);
               twinNode.freeOffset+=3*Settings.sizeOfInt+curContLen+dLen;               
            }
        }
         this.xBufChannel.position(Settings.nextPagePos);         
         int nextPage=this.xBufChannel.getInt();
         twinNode.xBufChannel.position(Settings.nextPagePos);
         if(nextPage>0)//this node is already pointing to another page
         {  
            twinNode.xBufChannel.putInt(nextPage);
//            if(CID==35)
//                logManager.LogManager.log(6,"Special Case ,page: "+this.xBufChannel.getPageNumber());
         }
         if(nextPage==Settings.invalidValue)
         {
             twinNode.xBufChannel.putInt(Settings.invalidValue);        
         } 
         this.xBufChannel.position(Settings.nextPagePos);
         this.xBufChannel.putInt(twinNode.indexPage);         
         twinNode.xBufChannel.position(Settings.freeOffsetPos);
         twinNode.xBufChannel.putInt(twinNode.freeOffset);         
         twinNode.indexPage=twinNode.writeContentElemNode();
         //if(CID==35)
            logManager.LogManager.log(6,"this page(ElementIndx): "+this.indexPage+"'s twin is: "+ twinNode.indexPage);
            logManager.LogManager.log(6,"---------------------------------");
         //in this point we get twin's page no,so we should assign it to this.next page:
         
         //baraye parent v newRoot hargez nextP set nemikonim.
         
         if(rootRenewed==true)
         {
             //rootNode.loadElementBTreeNode(rootPointer, indexName); WE HAve it in memory
             rootPointer=rootNode.insertIntoInternalContentElem(this.MaxKey.maxCID,this.MaxKey.maxContent,this.MaxKey.maxDeweyID,rootPointer,this.indexPage,parents);//in chie?az OSTAD
             rootPointer=rootNode.insertIntoInternalContentElem(twinNode.MaxKey.maxCID,this.MaxKey.maxContent,twinNode.MaxKey.maxDeweyID,rootPointer,twinNode.indexPage,parents);
             rootNode.updateContentElemNode(rootPointer);//???che lozumi dare?
             rootNode.closeContentElemNode();
         }
         else 
         {            
             parentPointer=(int)parents.pop();             
//             rootPointer=parentNode.updateInternalElement(this.MaxKey, rootPointer,this.indexPage, parents);
//             rootPointer=parentNode.insertIntoInternalElement(twinNode.MaxKey.maxCID,twinNode.MaxKey.maxDeweyID,rootPointer,twinNode.indexPage,parents);
             rootPointer=parentNode.insUpdaterContentElem(this.MaxKey, this.indexPage, twinNode.MaxKey, twinNode.indexPage, rootPointer, parents);
             parents.push(parentPointer);
             parentNode.closeContentElemNode();
         }
         return rootPointer;
    }
    
    public int splitInternalContentElem(int[] midPoint,ArrayList<Integer> CIDList,ArrayList<String> contList,ArrayList<DeweyID> deweyIDList,ArrayList pagePointerList,int CID,String cont,DeweyID deweyID,int rootPointer,Stack parents) throws IOException, DBException
    {
        
        ContentElementNode twinNode;
        ContentElementNode rootNode=null;
        int parentPointer=-1;
        ContentElementNode parentNode=null;
        ContentMaxKey prevMaxKey=new ContentMaxKey();
        boolean rootRenewed=false;
        boolean maxKeyChanged=false;
               
        if(this.isRoot==1)
         { 
           rootRenewed=true;
          //inja creat static ???
           rootNode=new ContentElementNode(indexName,(byte)1,(byte)0, 0);//just built root has 2 keys
           //this.indexPage=rootPointer;//lazeme?
           rootPointer=rootNode.createContentElemNode();//bayad dar GMD neveshte shavad****
           //tu in noghte nextPage this taghir mikone,man chi kar konam?dobare az file bkhunam?
           //this.parentPointer=rootPointer;
           this.isRoot=0;
           xBufChannel.position(Settings.isRootPos);
           xBufChannel.put(isRoot);//chon this ba unpin write mishe.
           if(this.IsLeaf())
               twinNode=new ContentElementNode(indexName,(byte)0,(byte)1,-1);//numOfKeys is not valid yet
               //isRoot , isLeaf dar xbuf neveshte nashode and.:(dar setMD set mishavad.
           else
               twinNode=new ContentElementNode(indexName,(byte)0,(byte)0,-1);//numOfKeys is not valid yet
         }           
         else 
         {
             rootRenewed=false;
             //load parent node:
             parentPointer=(int)parents.pop();
             parentNode=new ContentElementNode(parentPointer,indexName);//shayd bhetare yek tabe load bnvisim.
             parents.push(parentPointer);
             if(this.IsLeaf())
             {   
                 twinNode=new ContentElementNode(indexName,(byte)0,(byte)1, -1);//numOfKeys is not valid yet
                 logManager.LogManager.log(5,"WRONG:"+this.indexPage+ " is not expected to be leaf");
             
             }
             else
                 twinNode=new ContentElementNode(indexName,(byte)0,(byte)0, -1);//numOfKeys is not valid yet

         }
                
        //InternalBuffer twin = bufMngr.getPage(twinPageNumber);
        int dLen;
        int curContLen;
        byte[] temp1;
        byte[] temp2;
       ////////////////////////////////////////////////////////////////////
       this.freeOffset=Settings.dataStartPos;//initial value
       twinNode.freeOffset=Settings.dataStartPos; //initial value
       
       xBufChannel.clear();//vali engAR CLEAR NEMISHAVAD CHERA???
       xBufChannel.position(Settings.numOfKeysPos);
        // koja meta data ghabli az ru in pak mishe? 
        //CIDList: c1,c2,...,cid,...c[m1],...
       if (CIDList.indexOf(CID) < midPoint[1]) 
        {
            
            this.numOfKeys=midPoint[1] + 1;
            xBufChannel.putInt(midPoint[1] + 1); //numOfKeys=i+1
            
//                    if(this.MaxKey.maxCID!=CIDList.get(midPoint[1]))
//                    {   
                        prevMaxKey.maxCID=this.MaxKey.maxCID;
                        prevMaxKey.maxContent=this.MaxKey.maxContent;
                        prevMaxKey.maxDeweyID=this.MaxKey.maxDeweyID;
                        this.MaxKey.maxCID=CIDList.get(midPoint[1]);
                        this.MaxKey.maxContent=contList.get(midPoint[1]);                        
                        this.MaxKey.maxDeweyID=deweyIDList.get(midPoint[1]);
                        maxKeyChanged=true;
//                    }
                            
            //koja baghie metadata twin neveshte mishe?
            twinNode.numOfKeys=CIDList.size() - (midPoint[1] + 1);
            twinNode.xBufChannel.position(Settings.numOfKeysPos);
            twinNode.xBufChannel.putInt(twinNode.numOfKeys); //twin's Num of keys

            for (int i = midPoint[1] + 1; i < deweyIDList.size(); i++) 
            {
                 if(i==CIDList.size()-1)
                 {   
                     twinNode.MaxKey.maxCID=CIDList.get(i);
                     twinNode.MaxKey.maxContent=contList.get(i);
                     twinNode.MaxKey.maxDeweyID=deweyIDList.get(i);                 
                 }
                temp1=contList.get(i).getBytes();
                curContLen=temp1.length;
                temp2 =deweyIDList.get(i).getDeweyId();
                dLen = temp2.length;
                twinNode.xBufChannel.putInt((int)pagePointerList.get(i));
                twinNode.xBufChannel.putInt((int)CIDList.get(i));   
                twinNode.xBufChannel.putInt(curContLen);
                twinNode.xBufChannel.put(temp1,0,curContLen);
                twinNode.xBufChannel.putInt(dLen);
                twinNode.xBufChannel.put(temp2,0,dLen);
                twinNode.freeOffset+=4*Settings.sizeOfInt+curContLen+dLen;
            }
        } 
        else 
        {
            this.numOfKeys=midPoint[1];
            xBufChannel.putInt(midPoint[1]); //numOfKeys=i
//            if(this.MaxKey.maxCID!=CIDList.get(midPoint[1]-1))
//            {   
                prevMaxKey.maxCID=this.MaxKey.maxCID;
                prevMaxKey.maxContent=this.MaxKey.maxContent;
                prevMaxKey.maxDeweyID=this.MaxKey.maxDeweyID;
                this.MaxKey.maxCID=CIDList.get(midPoint[1]-1);
                this.MaxKey.maxContent=contList.get(midPoint[1]-1);
                this.MaxKey.maxDeweyID=deweyIDList.get(midPoint[1]-1);
                maxKeyChanged=true;
//            }

            twinNode.numOfKeys=CIDList.size()- midPoint[1];
            twinNode.xBufChannel.position(Settings.numOfKeysPos);
            twinNode.xBufChannel.putInt(twinNode.numOfKeys); //twin Num of keys

            for (int i = midPoint[1]; i < CIDList.size(); i++) 
            {
                 if(i==CIDList.size()-1)//-1********
                 {  
                     twinNode.MaxKey.maxCID=CIDList.get(i);
                     twinNode.MaxKey.maxContent=contList.get(i);
                     twinNode.MaxKey.maxDeweyID=deweyIDList.get(i);
                 }
               temp1=contList.get(i).getBytes();
               curContLen=temp1.length;
               temp2 = deweyIDList.get(i).getDeweyId();
               dLen = temp2.length;
               twinNode.xBufChannel.putInt((int)pagePointerList.get(i));               
               twinNode.xBufChannel.putInt(CIDList.get(i));
//               if(i==midPoint[1] || i==CIDList.size()-1)
//                   logManager.LogManager.log(5,CIDList.get(i).toString());
               twinNode.xBufChannel.putInt(curContLen);
               twinNode.xBufChannel.put(temp1, 0, curContLen);
               twinNode.xBufChannel.putInt(dLen);
               twinNode.xBufChannel.put(temp2, 0, dLen);
               twinNode.freeOffset+=4*Settings.sizeOfInt+curContLen+dLen;
               
            }
        }
       
         twinNode.xBufChannel.position(Settings.freeOffsetPos);
         twinNode.xBufChannel.putInt(twinNode.freeOffset);         
         twinNode.indexPage=twinNode.writeContentElemNode();
         logManager.LogManager.log(6,"internal: "+this.indexPage+" twin:"+twinNode.indexPage);
         
         if(rootRenewed==true)
         {
             //rootNode.loadElementBTreeNode(rootPointer, indexName);WE HAVe IT in memory!
             rootPointer=rootNode.insertIntoInternalContentElem(this.MaxKey.maxCID,this.MaxKey.maxContent,this.MaxKey.maxDeweyID,rootPointer,this.indexPage,parents);//in chie?az OSTAD
             rootPointer=rootNode.insertIntoInternalContentElem(twinNode.MaxKey.maxCID,twinNode.MaxKey.maxContent,twinNode.MaxKey.maxDeweyID,rootPointer,twinNode.indexPage,parents);
             rootNode.updateContentElemNode(rootPointer);
             rootNode.closeContentElemNode();
         }
         else 
         {            
             parentPointer=(int)parents.pop();
//             rootPointer=parentNode.updateInternalElement(this.MaxKey, rootPointer,this.indexPage, parents);
//             rootPointer=parentNode.insertIntoInternalElement(twinNode.MaxKey.maxCID,twinNode.MaxKey.maxDeweyID,rootPointer,twinNode.indexPage,parents);
             rootPointer=parentNode.insUpdaterContentElem(this.MaxKey, this.indexPage, twinNode.MaxKey, twinNode.indexPage, rootPointer, parents);
             parents.push(parentPointer);
             parentNode.closeContentElemNode();
         }
         return rootPointer;
    }
    
       //Used for Update:
    public int splitInternalContentElem(int[] midPoint,ArrayList<Integer> CIDList,ArrayList<String> contList,ArrayList<DeweyID> deweyIDList,ArrayList pagePointerList,int rootPointer,Stack parents) throws IOException, DBException
    {
        ContentElementNode twinNode;
        ContentElementNode rootNode=null;
        int parentPointer=-1;
        ContentElementNode parentNode=null;
        ContentMaxKey prevMaxKey=new ContentMaxKey();
        boolean rootRenewed=false;
        boolean maxKeyChanged=false;
               
        if(this.isRoot==1)
         { 
           rootRenewed=true;
          //inja creat static ???
           rootNode=new ContentElementNode(indexName,(byte)1,(byte)0, 0);//just built root has 2 keys
           //this.indexPage=rootPointer;//lazeme?
           rootPointer=rootNode.createContentElemNode();//bayad dar GMD neveshte shavad****
           //tu in noghte nextPage this taghir mikone,man chi kar konam?dobare az file bkhunam?
           //this.parentPointer=rootPointer;
           this.isRoot=0;
           xBufChannel.position(Settings.isRootPos);
           xBufChannel.put(isRoot);//chon this ba unpin write mishe.
           //not used:since "this" is not leaf(it is internal node)
//           if(this.IsLeaf())
//               twinNode=new ElementBTreeNode(indexName,(byte)0,(byte)1,-1);//numOfKeys is not valid yet
//               //isRoot , isLeaf dar xbuf neveshte nashode and.:(dar setMD set mishavad.
//           else
           
               twinNode=new ContentElementNode(indexName,(byte)0,(byte)0,-1);//numOfKeys is not valid yet
               
 
         }           
         else 
         {
             rootRenewed=false;
             //load parent node:
             parentPointer=(int)parents.pop();
             parentNode=new ContentElementNode(parentPointer,indexName);//shayd bhetare yek tabe load bnvisim.
             parents.push(parentPointer);
             if(this.IsLeaf())
                twinNode=new ContentElementNode(indexName,(byte)0,(byte)1, -1);//numOfKeys is not valid yet
             else
                 twinNode=new ContentElementNode(indexName,(byte)0,(byte)0, -1);//numOfKeys is not valid yet

         }
                
        //InternalBuffer twin = bufMngr.getPage(twinPageNumber);
        int dLen;
        int curContLen;
        byte[] temp1;
        byte[] temp2;
       ////////////////////////////////////////////////////////////////////
       this.freeOffset=Settings.dataStartPos;//initial value
       twinNode.freeOffset=Settings.dataStartPos; //initial value
       
       xBufChannel.clear();//vali engAR CLEAR NEMISHAVAD CHERA???
       xBufChannel.position(Settings.numOfKeysPos);
        // koja meta data ghabli az ru in pak mishe? 
        //CIDList: c1,c2,...,cid,...c[m1],...
//       if (CIDList.indexOf(CID) < midPoint[1]) 
//        {
            
//            this.numOfKeys=midPoint[1] + 1;
//            xBufChannel.putInt(midPoint[1] + 1); //numOfKeys=i+1
//            
//                    if(this.MaxKey.maxCID!=CIDList.get(midPoint[1]))
//                    {   
//                        prevMaxKey.maxCID=this.MaxKey.maxCID;
//                        prevMaxKey.maxDeweyID=this.MaxKey.maxDeweyID;
//                        this.MaxKey.maxCID=CIDList.get(midPoint[1]);
//                        this.MaxKey.maxDeweyID=deweyIDList.get(midPoint[1]);
//                        maxKeyChanged=true;
//                    }
//                            
//            //koja baghie metadata twin neveshte mishe?
//            twinNode.numOfKeys=CIDList.size() - (midPoint[1] + 1);
//            twinNode.xBufChannel.position(Settings.numOfKeysPos);
//            twinNode.xBufChannel.putInt(CIDList.size() - (midPoint[1] + 1)); //twin's Num of keys
//
//            for (int i = midPoint[1] + 1; i < deweyIDList.size(); i++) 
//            {
//                 if(i==CIDList.size()-1)
//                 {   
//                     twinNode.MaxKey.maxCID=CIDList.get(i);
//                     twinNode.MaxKey.maxDeweyID=deweyIDList.get(i);                 
//                 }
//                //length=dIDList.get(i).getDeweyId().dLen;
//                temp2 =deweyIDList.get(i).getDeweyId();
//                twinNode.xBufChannel.putInt((int)pagePointerList.get(i));
//                dLen = temp2.length+Settings.sizeOfInt;
//                twinNode.xBufChannel.putInt(dLen);
//                twinNode.xBufChannel.putInt((int)CIDList.get(i));                
//                twinNode.xBufChannel.put(temp2,0,temp2.length);
//                twinNode.freeOffset+=2*Settings.sizeOfInt+dLen;
//            }
       /////////////////// } 
//        else 
//        {
            this.numOfKeys=midPoint[1];
            xBufChannel.putInt(midPoint[1]); //numOfKeys=i
//            if(this.MaxKey.maxCID!=CIDList.get(midPoint[1]-1))
//            {   
                prevMaxKey.maxCID=this.MaxKey.maxCID;
                prevMaxKey.maxContent=this.MaxKey.maxContent;
                prevMaxKey.maxDeweyID=this.MaxKey.maxDeweyID;
                this.MaxKey.maxCID=CIDList.get(midPoint[1]-1);
                this.MaxKey.maxContent=contList.get(midPoint[1]-1);
                this.MaxKey.maxDeweyID=deweyIDList.get(midPoint[1]-1);
                maxKeyChanged=true;
//            }

            twinNode.numOfKeys=CIDList.size()- midPoint[1];
            twinNode.xBufChannel.position(Settings.numOfKeysPos);
            twinNode.xBufChannel.putInt(twinNode.numOfKeys); //twin Num of keys

            for (int i = midPoint[1]; i < deweyIDList.size(); i++) 
            {
                 if(i==CIDList.size()-1)//-1?********
                 {  
                     twinNode.MaxKey.maxCID=CIDList.get(i);
                     twinNode.MaxKey.maxContent=contList.get(i);
                     twinNode.MaxKey.maxDeweyID=deweyIDList.get(i);
                 }
               temp1=contList.get(i).getBytes();
               curContLen=temp1.length;
               temp2 = deweyIDList.get(i).getDeweyId();
               dLen = temp2.length;
               twinNode.xBufChannel.putInt((int)pagePointerList.get(i));               
               twinNode.xBufChannel.putInt(CIDList.get(i));
               twinNode.xBufChannel.putInt(curContLen);
               twinNode.xBufChannel.put(temp1, 0,curContLen);
               if(i==midPoint[1] || i==CIDList.size()-1)
                   logManager.LogManager.log(6,CIDList.get(i).toString());
               twinNode.xBufChannel.putInt(dLen);
               twinNode.xBufChannel.put(temp2, 0, dLen);
               twinNode.freeOffset+=4*Settings.sizeOfInt+curContLen+dLen;
               
            }
//        }
       
         twinNode.xBufChannel.position(Settings.freeOffsetPos);
         twinNode.xBufChannel.putInt(twinNode.freeOffset);         
         twinNode.indexPage=twinNode.writeContentElemNode();
         logManager.LogManager.log(6,"internal: "+this.indexPage+" twin: "+twinNode.indexPage);
         if(rootRenewed==true)
         {
             //rootNode.loadElementBTreeNode(rootPointer, indexName);WE HAVe IT in memory!
             rootPointer=rootNode.insertIntoInternalContentElem(this.MaxKey.maxCID,this.MaxKey.maxContent,this.MaxKey.maxDeweyID,rootPointer,this.indexPage,parents);
             rootPointer=rootNode.insertIntoInternalContentElem(twinNode.MaxKey.maxCID,this.MaxKey.maxContent,twinNode.MaxKey.maxDeweyID,rootPointer,twinNode.indexPage,parents);
             rootNode.updateContentElemNode(rootPointer);
             rootNode.closeContentElemNode();
         }
         else 
         {            
             parentPointer=(int)parents.pop();
//             rootPointer=parentNode.updateInternalElement(this.MaxKey, rootPointer,this.indexPage, parents);
//             rootPointer=parentNode.insertIntoInternalElement(twinNode.MaxKey.maxCID,twinNode.MaxKey.maxDeweyID,rootPointer,twinNode.indexPage,parents);
             rootPointer=parentNode.insUpdaterContentElem(this.MaxKey, this.indexPage, twinNode.MaxKey, twinNode.indexPage, rootPointer, parents);
             parents.push(parentPointer);
             parentNode.closeContentElemNode();
         }
         return rootPointer;

    }
    public int[] computeMidPoint(short[] elementPlaces) 
    {
        int[] midPoint = new int[2];
        //Firstly we compute the distance of every record from the middle of the page:(pos=pageSize/2)
        midPoint[0] = Math.abs(elementPlaces[0] - Settings.pageSize/2); //midPoint value
        int minimum;
        //minimum = elementPlaces[0];
        //Secondly compute the minimum of distances from the middle
        for (int i = 0; i < elementPlaces.length; i++) 
        {            
            elementPlaces[i] = (short)Math.abs(elementPlaces[i] - Settings.pageSize/2);
            midPoint[0] = Math.min(midPoint[0], elementPlaces[i]);
            //keep the index of the record which its distance is minimum:
            if (elementPlaces[i] == midPoint[0]) 
            {
                midPoint[1] = i;
            }
        }
        //assumes pageSize to be even
        
        midPoint[0] = midPoint[0] + Settings.pageSize/2;
        /**
         * mid[0]=the minimum distance form the middle of the page
         * mid[1]=the index of the record with the min distance from the middle 
        */
        return midPoint; //makane shorue vasat tarin DID ra midahad
    }
    public int createContentElemNode() throws IOException
    {
        return logicFileMgr.creatRootPage(indexName, isRoot, isLeaf, numOfKeys, xBufChannel);
               // logicFileMgr.addPage(indexName,isRoot,isLeaf,numOfKeys);
        //must add some code to write page's data as well.
    } 
    public int writeContentElemNode() throws IOException
    {
        return logicFileMgr.createTwinPage(indexName, freeOffset, isRoot, isLeaf, numOfKeys, xBufChannel);
                //logicFileMgr.addPage(indexName,freeOffset,isRoot,isLeaf,numOfKeys,this.xBufChannel);
        
    } 
    public void updateContentElemNode(int pageNumber) throws IOException
    {
        //nemidunam k inaro bayd inja bnvisam ya tu log file mgr:/
        //SHAYADAM behtare inja free offset ro bnvisam...:((( nemidunm.
        xBufChannel.position(Settings.isRootPos);
        xBufChannel.put(isRoot);
        xBufChannel.put(isLeaf);
        xBufChannel.position(Settings.pageSize);
        xBufChannel.setPageNumber(pageNumber);
        xBufChannel.flip();
        logicFileMgr.updatePage(xBufChannel);//???in bayad ehtemalan unpin bashe.
    }
     public void closeNode()
    {
        closeContentElemNode();
    }
    public void closeContentElemNode()
    {
        bufMngr.unpinBuffer(this.xBufChannel);
    }
    
   /* public void open()
    {
          xBufChannel.position(Settings.dataStartPos);//3+4+4 ps+nextPage+freeOffset+isL+isR+numOf
        //in nextPage bayd hatman bashe b hatere yaftane akharin page
    }*/
//    public DeweyID next()
//    {
//        //we assume that during "next"ing no more keys are added.(otherwise bound should be updated somehow)
//        int dLen;
//        byte[] curId;
//        int curCid=-1;
//        DeweyID curDId=null;
//        if(bound>0)
//        {
//            xBufChannel.position(xbufMarker);
//            dLen=xBufChannel.getInt();
//            curCid=xBufChannel.getInt();
//            curId =new byte[dLen-Settings.sizeOfInt];
//            curDId=new DeweyID();
//            xBufChannel.get(curId,0,dLen-Settings.sizeOfInt);
//            curDId.setDeweyId(curId);
//            xbufMarker=xBufChannel.position();
//            bound--;
//        }
//                    
//        if(curDId!=null)
//        {
//            //check to make sure that curCID is not -1 otherwise return an exception?
//            curDId.setCID(curCid);
//        }
//        ///curDID=null means EOF
//        return curDId;
//    }
    public void prepare(int recPos)
    {
        xBufChannel.position(recPos);
        //FOR TEST:
//        xBufChannel.position(recPos,this);
       // logManager.LogManager.log("RecPos Changed: "+ recPos);
    }
    //We open sibling in this method but do not unpin them:
            
    /**
     *
     * @param CID
     * @return OutputWrapper <DeweyID>
     * @throws IOException
     */
     public OutputWrapper <ContentElementNode,DeweyID> next(int CID) throws IOException, DBException            
    {
        //we assume that during "next"ing no more keys are added.(otherwise bound should be updated somehow)
        int recLength;
        byte[] curId;
        int curCid=-1;
        DeweyID curDId=null;
        ContentElementNode sibling=null;
        //assumption: recPos is the correct start position of some record in page 
        //if(xBufChannel.position()<Settings.pageSize-numOfKeys*2)
        if(xBufChannel.position()<freeOffset && bound>0)
        {
            recLength=xBufChannel.getInt();
            logManager.LogManager.log("recLength: "+String.valueOf(recLength));
            curCid=xBufChannel.getInt();
            if(curCid==0)
               throw new DBException("CID is 0 at Element Index at page "+xBufChannel.getPageNumber() );
            curId =new byte[recLength-Settings.sizeOfInt];            
            //curDId=new DeweyID();
            xBufChannel.get(curId,0,recLength-Settings.sizeOfInt);
            //curDId.setDeweyId(curId); 
            curDId=new DeweyID(curId);
            if(curCid==38)
                logManager.LogManager.log(6,curDId.toString());
            bound--;
            logManager.LogManager.log(1,"bound: "+bound);
        }
        else
            {               
                   //goTOnextPAge;
                  xBufChannel.position(Settings.nextPagePos);
                  int nextPage=xBufChannel.getInt();
                  // This is done in iterator:
                  //bufMngr.unpinBuffer(xBufChannel, true);
                  if(nextPage>0)
                  {
                      sibling=new ContentElementNode(nextPage,indexName);
                  }
            }
                                      
        if(curDId!=null && CID!=0 && CID==curCid)
        {
            //For Partial Iteration:
            curDId.setCID(curCid);
            return new OutputWrapper<> (sibling,curDId);
        }
        if(curDId!=null && CID!=0 && CID!=curCid)
        {
            //For Partial Iteration:            
            return null;
        }
        if(curDId!=null && CID==0 && curCid!=0)
        {
            //For Complete Iteration
            curDId.setCID(curCid);
            return new OutputWrapper<> (sibling,curDId);
        }
        
//        curDID=null means EOF
        return new OutputWrapper<> (sibling,curDId);
        
    }
    //This is for test:
    public XBufferChannel getBuf()
    {
        return this.xBufChannel;
    }
    public int getIndexPage()
    {
        return indexPage;
    }
}



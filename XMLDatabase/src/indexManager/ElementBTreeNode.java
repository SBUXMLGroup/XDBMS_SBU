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

public class ElementBTreeNode
{
    // private byte pageState; //for future use
    private byte isRoot;
    private byte isLeaf;
    //private int parentPointer;
    private ElementMaxKey MaxKey;
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
    

    public ElementBTreeNode(String indexName,byte isRoot,byte isLeaf,int numOfKeys) throws IOException, DBException
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
        MaxKey=new ElementMaxKey();
        //init values:
        xbufMarker=Settings.dataStartPos;
        //bound=numOfKeys;
                
    }

    public ElementBTreeNode(int indexPage,String indexName) throws IOException, DBException 
    {
        //LOAD btreeNode:
        logicFileMgr = LogicalFileManager.Instance;//in tu ghesmate load lazem nis?
        this.indexName=indexName;
        this.indexPage=indexPage;
        bufMngr=BufferManager.getInstance();
        xBufChannel=bufMngr.getPage(indexPage); //inja wMra kharab mikone
        xBufChannel.position(Settings.freeOffsetPos);//5
        freeOffset = xBufChannel.getInt();
        //byteBuf=xBuf.getByteBuffer();
        isRoot=xBufChannel.get();
        isLeaf = xBufChannel.get();
        //InternalBuffer.position(Settings.numOfKeysPos);
        numOfKeys = xBufChannel.getInt();
        MaxKey=new ElementMaxKey();
        xbufMarker=Settings.dataStartPos;
        bound=numOfKeys;
    }
    
    public void loadElementBTreeNode(int indexPage,String indexName) throws IOException, DBException 
    {
        //LOAD ExtendedbtreeNode:
        
        //return bhtare k khodeshBNode bashe ya na?
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

    public int internalElementSearch(int CID,DeweyID deweyId) 
    {
        //page: pagestate,nextpage,freeoffset
        //ps,isroot,isleaf,number of keys in this page,freeoff,nextp(leftmost),deweyId,nextp,deweyId,nextp(rightmost)
        int pagePointer = -1;//null
        int recLength;
        byte[] curId;
        int curCID;
        DeweyID curDID;
        xBufChannel.position(Settings.dataStartPos); 
        
        if (numOfKeys > 0)
        {
            for (int i = 0; i < numOfKeys; i++)
            {
                //inja bayad nahveye khanadan taghir konad!!!!
                pagePointer = xBufChannel.getInt();//next page adrs
                recLength = xBufChannel.getInt();
                curCID=xBufChannel.getInt();
                curId = new byte[recLength-Settings.sizeOfInt];
                xBufChannel.get(curId,0,recLength-Settings.sizeOfInt);
                //curCId=new DeweyID();
                //curDID.setDeweyId(curId);
                curDID=new DeweyID(curId);
                if (CID==curCID) 
                {
                    if(!(curDID.notGreaterThan(deweyId)) || curDID.equals(deweyId))
                    {    //dID<curDID || DID==curDID
                    //key is inside the page which curCID is its maxkey                    
                        break;
                    }
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
 public int internalElementSearch(int CID) 
    {
        //page: pagestate,nextpage,freeoffset
        //ps,isroot,isleaf,number of keys in this page,freeoff,nextp(leftmost),deweyId,nextp,deweyId,nextp(rightmost)
        int pagePointer = -1;//null
        int recLength;
        byte[] curId;
        int curCID;
        DeweyID curDID=null;;
        xBufChannel.position(Settings.dataStartPos); 
        //logManager.LogManager.log(6,"page:"+xBufChannel.getPageNumber());                
        if (numOfKeys > 0)
        {
            for (int i = 0; i < numOfKeys; i++)
            {
                //inja bayad nahveye khanadan taghir konad!!!!
                pagePointer = xBufChannel.getInt();//next page adrs
                recLength = xBufChannel.getInt();
                curCID=xBufChannel.getInt();
//                if(curCID==350)
//                    logManager.LogManager.log(6,"cur:350");
                curId = new byte[recLength-Settings.sizeOfInt];
                xBufChannel.get(curId,0,recLength-Settings.sizeOfInt);
//                curDID=new DeweyID();
//                curDID.setDeweyId(curId);
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
    public boolean leafElementSearch(int CID,DeweyID deweyID)
    {
        //dar barg ha bayd pagePointer hamun null bashand,ya aslan nabashand.(tu method insert taiin mishe)
        int pagePointer = -1; //null
        int recLength;
        byte[] curId;
        int curCID; 
        DeweyID curDID;
        xBufChannel.position(Settings.dataStartPos);//3+4+4??????
        if(numOfKeys>0)
        for (int i = 0; i < numOfKeys; i++)
        {
            //pagePointer = xBufChannel.getInt();//next page adrs
            recLength = xBufChannel.getInt();//=dId length+4
            
            curCID=xBufChannel.getInt();
            //bastegi b hadafe srch dare age did bkhahim fargh mikone
            curId = new byte[recLength-Settings.sizeOfInt];
            xBufChannel.get(curId, 0, recLength-Settings.sizeOfInt);
//            curDID=new DeweyID();
//            curDID.setDeweyId(curId);
            curDID=new DeweyID(curId);
            //result = curCID;
            if (curCID==CID) 
            {
                if(curDID.equals(deweyID))
                {
                   return true; //key found
                }
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
     public int leafElementSearch(int CID)
    {
        //outputs record start pos in page 
        //dar barg ha bayd pagePointer hamun null bashand,ya aslan nabashand.(tu method insert taiin mishe)
        int pagePointer = -1; //null
        int recLength;
        byte[] curId=null;
        int curCID;
        int offset;
        DeweyID curDID = null;
        xBufChannel.position(Settings.dataStartPos);//3+4+4??????
        if(numOfKeys>0)
        {
            for (int i = 0; i < numOfKeys; i++)
            {
                //pagePointer = xBufChannel.getInt();//next page adrs
                recLength = xBufChannel.getInt();//=dIdlength+4            
                curCID=xBufChannel.getInt();
                //bastegi b hadafe srch dare age did bkhahim fargh mikone
                curId=new byte[recLength-Settings.sizeOfInt];
                xBufChannel.get(curId, 0,recLength-Settings.sizeOfInt );
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
        xBufChannel.position(Settings.dataStartPos);//3+4+4??????  
//        for(int i=0;i<numOfKeys;i++)
//        {
          pagePointer = xBufChannel.getInt();//next page adrs 
//          if(i==0)
//              firstPP=pagePointer;
//          int recLength=xBufChannel.getInt();
//          int cid=xBufChannel.getInt();
//          byte[] dID=new byte[recLength-Settings.sizeOfInt];
//          xBufChannel.get(dID, 0, recLength-Settings.sizeOfInt);
//          DeweyID ddID=new DeweyID(dID);
//          ElementBTreeNode elemBTreeNode = new ElementBTreeNode(pagePointer, indexName);
//          elemBTreeNode.closeElementNode();
//          p++;
//        }
        return pagePointer;//not found   
         
     }
   public int insertIntoLeafElement(int CID,DeweyID deweyID,int rootPointer,Stack parents) throws IOException, DBException {

        ArrayList<Integer> CIDList = new ArrayList<>();
        ArrayList<DeweyID> dIDList = new ArrayList<>();
        //ArrayList<Integer> pagePointerList = new ArrayList<>();
        short[] elementPositionArray;//?
        int recLength;
        int keyLength;
        //int pagePointer;
        byte[] curId;
        boolean keyInserted=false;
        boolean splitted=false;
        byte[] temp;
        int[] midPoint;
        int curCid;
        DeweyID curDId;
        ElementMaxKey lastMaxKey =new ElementMaxKey();
        xBufChannel.position(Settings.dataStartPos);//3+4+4 ps+nextPage+freeOffset+isL+isR+numOf
        //in nextPage bayd hatman bashe b hatere yaftane akharin page
        if (numOfKeys > 0) 
        {
            for (int i = 0; i < numOfKeys; i++) 
            {
               //assumption:not have pagePointer in leaves
               // pagePointer= xBufChannel.getInt();//next page adrs
               // pagePointerList.add(pagePointer);
                recLength=xBufChannel.getInt();//assumption:recLength=DeweyId size+4
                curCid=xBufChannel.getInt();
                curId = new byte[recLength-Settings.sizeOfInt];
                //curDId=new DeweyID();
                xBufChannel.get(curId,0,recLength-Settings.sizeOfInt);
                //curDId.setDeweyId(curId);
                curDId=new DeweyID(curId);
                if(i==numOfKeys-1)
                {
                    lastMaxKey.maxCID=curCid;
                    lastMaxKey.maxDeweyID=curDId;
                }                
                if (!keyInserted && (CID<=curCid)) 
                {
                    if(CID==curCid)
                    {
//                    do
//                    {
                        if(deweyID.notGreaterThan(curDId))
                        {
                            CIDList.add(CID);//cheghad ghashang mishe inaro nevesht :)))
                            dIDList.add(deweyID);
                            keyInserted=true;                                                       
                        }
                
                 } //end if(CID==curCid)
                 else 
                    {
                        // pagePointerList.add(Settings.nullPagePointer);
                        CIDList.add(CID);//cheghad ghashang mishe inaro nevesht :)))
                        dIDList.add(deweyID);
                        keyInserted=true;
                    }
                }// end if (!keyInserted && (CID<=curCid))
                CIDList.add(curCid);
                dIDList.add(curDId);
            }
            //pagePointer = xBufChannel.getInt();//rightMost pointer NADARIM
            // pagePointerList.add(pagePointer);
            if (!keyInserted) 
            {
                //pagePointerList.add(Settings.nullPagePointer);
                CIDList.add(CID);
                dIDList.add(deweyID);
                this.MaxKey.maxCID=CID;
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
            keyLength = deweyID.getDeweyId().length+Settings.sizeOfInt;//dID DivNumber+cid
            int availableSpace=Settings.pageSize - 1 - freeOffset - 2*(numOfKeys-1);//lastNumOfKeys=numOfKeys-1
            int requiredSpace=Settings.sizeOfInt+keyLength+2;//2bytes for postion
//            Stack parentsCopy=new Stack();  
//            parentsCopy=(Stack)parents.clone();
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
                rootPointer=this.splitLeafElement(midPoint,CIDList, dIDList,CID, deweyID, rootPointer, parents);
                splitted=true;
            }
        
        
        //inja chon rewrite darim bayad dar noghteye data start bashim,yani bad az mahale num of keys
             //in bakhsh dar 2 halat ejra mishe:
            //1)vaghti split nadarim.
            //2)bad az split
           xBufChannel.position(Settings.dataStartPos);
           elementPositionArray = new short[numOfKeys];  
           ElementBTreeNode parentNode;
           int parentPointer;
           if(!splitted && parents.size()>0)//if split did not happen!:
           {
              if(lastMaxKey.maxCID<this.MaxKey.maxCID || (lastMaxKey.maxCID==this.MaxKey.maxCID && !lastMaxKey.maxDeweyID.equals(this.MaxKey.maxDeweyID)&& lastMaxKey.maxDeweyID.notGreaterThan(this.MaxKey.maxDeweyID)))//SPLIT: last>max, without SPLIT:last<=max
              {
                      parentPointer=(int)parents.pop();
                      parentNode=new ElementBTreeNode(parentPointer,indexName);
                      rootPointer=parentNode.updateInternalElement(this.MaxKey,rootPointer,this.indexPage,parents);
                      //parents.push(parentPointer);
                      parentNode.closeElementNode();
              }
           }
           
           //reset freeoffset
           freeOffset=Settings.dataStartPos;
           for (int i = 0; i < numOfKeys; i++)
           {
              //length=dIDList.get(i).getDeweyId().recLength;
              temp = dIDList.get(i).getDeweyId();     
              recLength = temp.length+Settings.sizeOfInt;
              elementPositionArray[i]=(short)xBufChannel.position();
              //InternalBuffer.putInt(pagePointerList.get(i));
              xBufChannel.putInt(recLength);
              xBufChannel.putInt(CIDList.get(i));  
//              if(splitted) //&& (i==0 || i==numOfKeys-1))
//                 logManager.LogManager.log(6,CIDList.get(i).toString());
              xBufChannel.put(temp, 0, recLength-Settings.sizeOfInt);
              freeOffset+=Settings.sizeOfInt+recLength;
           }
//           if(splitted)               
//           {      logManager.LogManager.log(6,"This Leaf:"+this.indexPage);
//                  logManager.LogManager.log(6,"*****************");
//           }
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
            keyLength = deweyID.getDeweyId().length+Settings.sizeOfInt;//key DivNumber+ cid len
            //----------------
            numOfKeys++;
           //Prepare buffer:
            xBufChannel.position(Settings.numOfKeysPos);
            xBufChannel.putInt(numOfKeys);
            //--------------------------
            this.MaxKey.maxCID=CID;
            this.MaxKey.maxDeweyID=deweyID;
            //InternalBuffer.putInt(Settings.nullPagePointer);
            short firstElementPosition;
            firstElementPosition=(short)xBufChannel.position();
            xBufChannel.putInt(keyLength);
            xBufChannel.putInt(CID);
            temp = deweyID.getDeweyId();
            xBufChannel.put(temp, 0, keyLength-Settings.sizeOfInt);
            //for puting ZERO(erasing the rest of page)
            xBufChannel.erase(xBufChannel.position(),numOfKeys);           
            xBufChannel.position(Settings.pageSize - 1 - numOfKeys * Settings.sizeOfShort);//az tahe page shoru mikonim b khundan
            xBufChannel.putShort(firstElementPosition);            
            freeOffset= freeOffset+Settings.sizeOfInt+keyLength;
            
        }
        xBufChannel.position(Settings.freeOffsetPos);
        xBufChannel.putInt(freeOffset);
        //engar unpin doros anjam nemishe chon inja root=0,leaf=1 hast vali ghalat neveshte mishe:
        //bufMngr.unpinBuffer(xBufChannel,true);//???inja bayd R,L doros she!!!
        logManager.LogManager.log(1,"CID: "+CID+" in page: "+ xBufChannel.getPageNumber());
        return rootPointer;
    }
   
   public int insertIntoInternalElement(int CID,DeweyID deweyID,int rootPointer,int childPointer,Stack parents) throws IOException, DBException 
   {
       //record structure in internal nodes: pagepointer,recLen,CID,deweyId
        ArrayList<Integer> CIDList = new ArrayList<>();
        ArrayList<DeweyID> dIDList = new ArrayList<>();
        ArrayList<Integer> pagePointerList = new ArrayList<>();
        short[] elementPositionArray;//?
        int recLength;
        int keyLength;
        int pagePointer;
        byte[] curId;
        boolean keyInserted=false;
        boolean splitted=false;
        byte[] temp;
        int[] midPoint;
        int curCId;
        DeweyID curDId;
        ElementMaxKey lastMaxKey=new ElementMaxKey();
        
       
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
                //pagePointerList.add(pagePointer);
                recLength = xBufChannel.getInt();
                curCId=xBufChannel.getInt();
                curId = new byte[recLength-Settings.sizeOfInt];
                //curDId=new DeweyID();
                xBufChannel.get(curId, 0, recLength-Settings.sizeOfInt);
                //curDId.setDeweyId(curId);
                curDId=new DeweyID(curId);
                if(i==numOfKeys-1)
                {
                    lastMaxKey.maxCID=curCId;
                    lastMaxKey.maxDeweyID=curDId;
                } 
                
                if (!keyInserted && (CID<=curCId)) 
                {
                    if(CID==curCId)
                    {
//                    do
//                    {
                        if(deweyID.notGreaterThan(curDId))
                        {
                            pagePointerList.add(childPointer);
                            CIDList.add(CID);
                            dIDList.add(deweyID);
                            keyInserted=true;                                                       
                        }
                    }//end if(CID==curCId)
                    else
                    {
                        // pagePointerList.add(Settings.nullPagePointer);
                        pagePointerList.add(childPointer);
                        CIDList.add(CID);
                        dIDList.add(deweyID);
                        keyInserted=true;
                    }
                }
                pagePointerList.add(pagePointer);
                CIDList.add(curCId);
                dIDList.add(curDId);
            }
            //????rightmost darim ya na?
            //pagePointer = xBufChannel.getInt();//right most pointer NADARIM
            // pagePointerList.add(pagePointer);
            if (!keyInserted) 
            {
                pagePointerList.add(childPointer);
                CIDList.add(CID);
                dIDList.add(deweyID);
                this.MaxKey.maxCID=CID;
                this.MaxKey.maxDeweyID=deweyID;
                keyInserted=true;
            }
            numOfKeys++;
            //Prepare buffer:
            xBufChannel.position(Settings.numOfKeysPos);
            xBufChannel.putInt(numOfKeys);
            //TODO: byte to short!!!
            //Note:LastNumOfKeys=numOfKeys-1
            recLength = deweyID.getDeweyId().length+Settings.sizeOfInt;//key DivNumber+cid
            int availableSpace=Settings.pageSize - 1 - freeOffset - 2*(numOfKeys-1);//lastNumOfKeys=numOfKeys-1
            int requiredSpace=Settings.sizeOfInt + recLength + Settings.pagePointerSize+2; //2bytes for position
//            Stack parentsCopy=new Stack();
//            parentsCopy=(Stack)parents.clone();            
            if (availableSpace <(requiredSpace+10))//+ 4+4
            {
                elementPositionArray = new short[numOfKeys-1];
                xBufChannel.position(Settings.pageSize - 1 - (numOfKeys-1)* Settings.sizeOfShort);//az tahe page shoru mikonim b khundan
                for (int i = 0; i < (numOfKeys-1); i++) {
                    elementPositionArray[i] = xBufChannel.getShort();
                }
                midPoint = computeMidPoint(elementPositionArray);
                rootPointer=this.splitInternalElement(midPoint,CIDList,dIDList,pagePointerList,CID,deweyID,rootPointer,parents);
                splitted=true;
            }
        
        
        //inja chon rewrite darim bayad dar noghteye data start bashim,yani bad az mahale num of keys
        //if(numOfKeys>1)//chon k ++ shode!
        //{
           xBufChannel.position(Settings.dataStartPos);
           elementPositionArray = new short[numOfKeys];  
           ElementBTreeNode parentNode;
           int parentPointer;
           if(!splitted && !parents.empty())
           {                
              if(lastMaxKey.maxCID<this.MaxKey.maxCID || (lastMaxKey.maxCID==this.MaxKey.maxCID && !lastMaxKey.maxDeweyID.equals(this.MaxKey.maxDeweyID)&& lastMaxKey.maxDeweyID.notGreaterThan(this.MaxKey.maxDeweyID)))//SPLIT: last>max, without SPLIT:last<=max
              {
                  parentPointer=(int)parents.pop();
                  parentNode=new ElementBTreeNode(parentPointer,indexName);
                  rootPointer=parentNode.updateInternalElement(this.MaxKey,rootPointer,this.indexPage,parents);
                  //parents.push(parentPointer);
                  parentNode.closeElementNode();
              }
//                  
           }
          
           //reset freeoffset
           freeOffset=Settings.dataStartPos;
           for (int i = 0; i < numOfKeys; i++)
           {
              //length=dIDList.get(i).getDeweyId().recLength;
              temp = dIDList.get(i).getDeweyId();        
              recLength = temp.length+Settings.sizeOfInt;
              elementPositionArray[i]=(short)xBufChannel.position();
              xBufChannel.putInt(pagePointerList.get(i));
              xBufChannel.putInt(recLength);
              xBufChannel.putInt(CIDList.get(i));
//              if(splitted) //&& (i==0 || i==numOfKeys-1))
//                 logManager.LogManager.log(6,CIDList.get(i).toString());
              xBufChannel.put(temp,0,recLength-Settings.sizeOfInt);
              freeOffset+=2*Settings.sizeOfInt+recLength;//WROOOOONG!
           }
//            if(splitted)               
//            {     
//                logManager.LogManager.log(6,"This Internal:"+this.indexPage);
//                logManager.LogManager.log(6,"*********************");
//            }
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
            keyLength = deweyID.getDeweyId().length+Settings.sizeOfInt;//key DivNumber+intsize
            //----------------
            numOfKeys++;
           //Prepare buffer:
            xBufChannel.position(Settings.numOfKeysPos);
            xBufChannel.putInt(numOfKeys);
            //--------------------------
            this.MaxKey.maxCID=CID;
            this.MaxKey.maxDeweyID=deweyID;
                       
            short firstElementPosition;
            firstElementPosition=(short)xBufChannel.position();
            
            xBufChannel.putInt(childPointer);            
            xBufChannel.putInt(keyLength);
            xBufChannel.putInt(CID);
            temp = deweyID.getDeweyId();
            xBufChannel.put(temp,0,temp.length);
            xBufChannel.position(Settings.pageSize-1-numOfKeys * Settings.sizeOfShort);//az tahe page shoru mikonim b khundan
            xBufChannel.putShort(firstElementPosition);            
            freeOffset= freeOffset+2*(Settings.sizeOfInt)+keyLength;
            
        }
        xBufChannel.position(Settings.freeOffsetPos);
        xBufChannel.putInt(freeOffset);
        //bufMngr.unpinBuffer(xBufChannel, true);
        return rootPointer;
   }
   public int insUpdaterElement(ElementMaxKey upMaxKey,int upChildPointer,ElementMaxKey insMaxKey,int insChildPointer,int rootPointer,Stack parents) throws IOException, DBException
   {
        //record structure in internal nodes: pagepointer,recLen,CID,deweyId
        ArrayList<DeweyID> dIDList = new ArrayList<>();
        ArrayList<Integer> pagePointerList = new ArrayList<>();
        ArrayList<Integer> CIDList = new ArrayList<>();
        short[] elementPositionArray;//?
        //int recLength;
        int prevMaxLength=0;
        int upMaxKeyLength = 0;
        int insMaxKeyLength;
        int maxLength;
        int recLength;
        int lastFreeOffset;
        int estimatedFreeOffset;
        int pagePointer;
        int[] midPoint;
        byte[] curId;
        boolean splitted=false;
        boolean keyInserted=false;
        byte[] temp;
        int curCId=-1;
        DeweyID curDId;
        ElementMaxKey lastMaxKey=new ElementMaxKey();
        xBufChannel.position(Settings.dataStartPos);//3+4+4 ps+nextPage+freeOffset+isL+isR+numOf
        //in nextPage bayd hatman bashe b hatere yaftane akharin page
        if (numOfKeys > 1) 
        {
            for (int i = 0; i < numOfKeys; i++) 
            {
                pagePointer = xBufChannel.getInt();//next page adrs
               // pagePointerList.add(pagePointer);
                recLength = xBufChannel.getInt();
                curCId=xBufChannel.getInt();
                curId = new byte[recLength-Settings.sizeOfInt];
                //curDId=new DeweyID();
                xBufChannel.get(curId, 0, recLength-Settings.sizeOfInt);
                //curDId.setDeweyId(curId);
                curDId=new DeweyID(curId);
                if(i==numOfKeys-1)
                {
                    lastMaxKey.maxCID=curCId;
                    lastMaxKey.maxDeweyID=curDId;
                }
//dar vaghe bayd vaghti SPLIT mikonim,chon max key kuchekatar ast bayd chek konim k dar jaye dorosti darj shavad
//vali dar in setup hatman max key in node az max node ghabli bishtar ast pas moshkeli nis.
                if (pagePointer==upChildPointer) 
                {
                    //yani b jaye current record darim newMaxkey ra varede list mikonim(khod b khod cur hazf mishe)
                    pagePointerList.add(upChildPointer);
                    CIDList.add(upMaxKey.maxCID);//DO NOT add curCID
                    dIDList.add(upMaxKey.maxDeweyID);
                    prevMaxLength=recLength-Settings.sizeOfInt;
                   // upMaxKeyLength=upMaxKey.maxDeweyID.getDeweyId().length;//key DivNumber 
                    curCId=upMaxKey.maxCID;
                    curDId=upMaxKey.maxDeweyID;
                }
                else 
                {
                   if (!keyInserted && (insMaxKey.maxCID<=curCId)) 
                   {
                    if(insMaxKey.maxCID==curCId)
                    {
//                    do
//                    {
                        if(insMaxKey.maxDeweyID.notGreaterThan(curDId))
                        {
                            pagePointerList.add(insChildPointer);
                            CIDList.add(insMaxKey.maxCID);//cheghad ghashang mishe inaro nevesht :)))
                            dIDList.add(insMaxKey.maxDeweyID);
                            keyInserted=true;                                                       
                        }

                    }//end if(CID==curCId)
                    else //if(CID<curCID)
                    {
                        // pagePointerList.add(Settings.nullPagePointer);
                        pagePointerList.add(insChildPointer);
                        CIDList.add(insMaxKey.maxCID);//cheghad ghashang mishe inaro nevesht :)))
                        dIDList.add(insMaxKey.maxDeweyID);
                        keyInserted=true;
                    }
                  }               
                pagePointerList.add(pagePointer);
                CIDList.add(curCId);
                dIDList.add(curDId);
                }
              }
            //????rightmost darim ya na?
            //pagePointer = xBufChannel.getInt();//right most pointer NADARIM
            // pagePointerList.add(pagePointer);
            if (!keyInserted) 
            {
                pagePointerList.add(insChildPointer);
                CIDList.add(insMaxKey.maxCID);
                dIDList.add(insMaxKey.maxDeweyID);
                this.MaxKey.maxCID=insMaxKey.maxCID;
                this.MaxKey.maxDeweyID=insMaxKey.maxDeweyID;
                keyInserted=true;
            }
            numOfKeys++;             
            xBufChannel.position(Settings.numOfKeysPos);
            xBufChannel.putInt(numOfKeys);           
            //this.MaxKey.maxCID=CIDList.get(CIDList.size()-1);
            ///ADDED:             
            lastFreeOffset=freeOffset;
            upMaxKeyLength=upMaxKey.maxDeweyID.getDeweyId().length;
            insMaxKeyLength=insMaxKey.maxDeweyID.getDeweyId().length;
        //key ghabli hazf shode v key jadid ezafe shode:
           estimatedFreeOffset=lastFreeOffset-prevMaxLength+upMaxKeyLength+insMaxKeyLength+Settings.sizeOfInt;//Int for insCID
       
           if (estimatedFreeOffset>= Settings.pageSize-1-numOfKeys*Settings.sizeOfShort) //+5 is for security margin,sth like that.
            {
                elementPositionArray = new short[numOfKeys];
                xBufChannel.position(Settings.pageSize - 1 - (numOfKeys)* Settings.sizeOfShort);//az tahe page shoru mikonim b khundan
                for (int i = 0; i < (numOfKeys); i++) 
                {
                    elementPositionArray[i] = xBufChannel.getShort();
                }
                midPoint = computeMidPoint(elementPositionArray);
                rootPointer=this.splitInternalElement(midPoint, CIDList, dIDList, pagePointerList, insMaxKey.maxCID, insMaxKey.maxDeweyID, rootPointer, parents);
                splitted=true;
            }
            
            ElementBTreeNode parentNode;
            int parentPointer;
            if(!splitted && !parents.empty())
                if(lastMaxKey.maxCID<this.MaxKey.maxCID || (lastMaxKey.maxCID==this.MaxKey.maxCID
                   && !lastMaxKey.maxDeweyID.equals(this.MaxKey.maxDeweyID)&& lastMaxKey.maxDeweyID.notGreaterThan(this.MaxKey.maxDeweyID)))//SPLIT: last>max, without SPLIT:last<=max
                {                      
                    parentPointer=(int)parents.pop();
                    parentNode=new ElementBTreeNode(parentPointer,indexName);
                    rootPointer=parentNode.updateInternalElement(this.MaxKey,rootPointer,this.indexPage,parents);
                    parents.push(parentPointer);
                    parentNode.closeElementNode();
                }
            //pagneePointer = xBufChannel.getInt();//rightmost pointer NADARIM
            // pagePointerList.add(pagePointer);
            
           xBufChannel.position(Settings.dataStartPos);
           elementPositionArray=new short[numOfKeys]; 
           //reset freeoffset
           freeOffset=Settings.dataStartPos;
           for (int i=0;i<numOfKeys;i++)
           {
              //length=dIDList.get(i).getDeweyId().recLength;
              temp = dIDList.get(i).getDeweyId();                      
              recLength = temp.length+Settings.sizeOfInt;
              elementPositionArray[i]=(short)xBufChannel.position();
              xBufChannel.putInt(pagePointerList.get(i));
              xBufChannel.putInt(recLength);
              xBufChannel.putInt(CIDList.get(i));
              xBufChannel.put(temp,0,temp.length);
              freeOffset+=2*Settings.sizeOfInt+recLength;
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
            recLength = upMaxKey.maxDeweyID.getDeweyId().length+Settings.sizeOfInt;//key DivNumber
            elementPosition[0]=(short)xBufChannel.position();
            xBufChannel.putInt(upChildPointer); 
            xBufChannel.putInt(recLength);
            xBufChannel.putInt(upMaxKey.maxCID);
            temp = upMaxKey.maxDeweyID.getDeweyId();
            xBufChannel.put(temp,0,temp.length);//prevMaxKey gets deleted automatically
            freeOffset+=2*(Settings.sizeOfInt)+recLength;
            //INSERTING:
            recLength=insMaxKey.maxDeweyID.getDeweyId().length+Settings.sizeOfInt;
            elementPosition[1]=(short)xBufChannel.position();
            xBufChannel.putInt(insChildPointer);           
            xBufChannel.putInt(recLength);
            xBufChannel.putInt(insMaxKey.maxCID);
            temp = insMaxKey.maxDeweyID.getDeweyId();
            xBufChannel.put(temp,0,temp.length);
           
            xBufChannel.position(Settings.pageSize - 1 - numOfKeys * Settings.sizeOfShort);//az tahe page shoru mikonim b khundan
            xBufChannel.putShort(elementPosition[0]);
            xBufChannel.putShort(elementPosition[1]); 
            
            freeOffset+=2*(Settings.sizeOfInt)+recLength; 
            
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
    public int updateInternalElement( ElementMaxKey newMaxKey,int rootPointer,int childPointer,Stack parents) throws IOException, DBException
    {
         //record structure in internal nodes: pagepointer,recLen,CID,deweyId
        ArrayList<DeweyID> dIDList = new ArrayList<>();
        ArrayList<Integer> pagePointerList = new ArrayList<>();
        ArrayList<Integer> CIDList = new ArrayList<>();
        short[] elementPositionArray;//?
        //int recLength;
        int prevMaxLength=0;
        int maxKeyLength;
        int maxLength;
        int recLength;
        int lastFreeOffset;
        int estimatedFreeOffset;
        int pagePointer;
        int[] midPoint;
        byte[] curId;
        boolean splitted=false;
        byte[] temp;
        int curCId=-1;
        DeweyID curDId;
        ElementMaxKey lastMaxKey=new ElementMaxKey();
        xBufChannel.position(Settings.dataStartPos);//3+4+4 ps+nextPage+freeOffset+isL+isR+numOf
        //in nextPage bayd hatman bashe b hatere yaftane akharin page
        if (numOfKeys > 1) 
        {
            for (int i = 0; i < numOfKeys; i++) 
            {
                pagePointer = xBufChannel.getInt();//next page adrs
                pagePointerList.add(pagePointer);
                recLength = xBufChannel.getInt();
                curCId=xBufChannel.getInt();
                curId = new byte[recLength-Settings.sizeOfInt];
                //curDId=new DeweyID();
                xBufChannel.get(curId, 0, recLength-Settings.sizeOfInt);
                //curDId.setDeweyId(curId);
                curDId=new DeweyID(curId);
                if(i==numOfKeys-1)
                {
                    lastMaxKey.maxCID=curCId;
                    lastMaxKey.maxDeweyID=curDId;
            }
//dar vaghe bayd vaghti SPLIT mikonim,chon max key kuchekatar ast bayd chek konim k dar jaye dorosti darj shavad
//vali dar in setup hatman max key in node az max node ghabli bishtar ast pas moshkeli nis.
                if (pagePointer==childPointer) 
                {
                    //yani b jaye current record darim newMaxkey ra varede list mikonim(khod b khod cur hazf mishe)
                    CIDList.add(newMaxKey.maxCID);//DO NOT add curCID
                    dIDList.add(newMaxKey.maxDeweyID);
                    prevMaxLength=recLength-Settings.sizeOfInt;                    
                }
                else
                {
                    CIDList.add(curCId);
                    dIDList.add(curDId);
                }
                
            }
            this.MaxKey.maxCID=CIDList.get(CIDList.size()-1);
            this.MaxKey.maxDeweyID=dIDList.get(dIDList.size()-1);
            ///ADDED:
             maxKeyLength=newMaxKey.maxDeweyID.getDeweyId().length;//key DivNumber 
            lastFreeOffset=freeOffset;
        //key ghabli hazf shode v key jadid ezafe shode:
           estimatedFreeOffset=lastFreeOffset-prevMaxLength+maxKeyLength;//old code:newMaxKey.getDeweyId().recLength;
       // }
         ///////////////////////////////////////////////////////////////////   
//            int availableSpace=Settings.pageSize - 1 - freeOffset - 2*(numOfKeys);//lastNumOfKeys=numOfKeys-1
//            int requiredSpace=2*Settings.sizeOfInt + recLength +strLength+ Settings.pagePointerSize+2; //2bytes for position
           
            if (estimatedFreeOffset>= Settings.pageSize-1-numOfKeys*Settings.sizeOfShort) //+5 is for security margin,sth like that.
            {
                elementPositionArray = new short[numOfKeys];
                xBufChannel.position(Settings.pageSize - 1 - (numOfKeys)* Settings.sizeOfShort);//az tahe page shoru mikonim b khundan
                for (int i = 0; i < (numOfKeys); i++) 
                {
                    elementPositionArray[i] = xBufChannel.getShort();
                }
                midPoint = computeMidPoint(elementPositionArray);
                rootPointer=this.splitInternalElement(midPoint,CIDList,dIDList,pagePointerList,rootPointer,parents);
                splitted=true;
            }
            
            /////////////////////////////////////
            ElementBTreeNode parentNode;
            int parentPointer;
            if(!splitted && !parents.empty())
                if(lastMaxKey.maxCID<this.MaxKey.maxCID || (lastMaxKey.maxCID==this.MaxKey.maxCID
                   && !lastMaxKey.maxDeweyID.equals(this.MaxKey.maxDeweyID)&& lastMaxKey.maxDeweyID.notGreaterThan(this.MaxKey.maxDeweyID)))//SPLIT: last>max, without SPLIT:last<=max
     
            {                      
                parentPointer=(int)parents.pop();
                parentNode=new ElementBTreeNode(parentPointer,indexName);
                rootPointer=parentNode.updateInternalElement(this.MaxKey,rootPointer,this.indexPage,parents);
                parents.push(parentPointer);
                parentNode.closeElementNode();
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
              //length=dIDList.get(i).getDeweyId().recLength;
              temp = dIDList.get(i).getDeweyId();                      
              recLength = temp.length+Settings.sizeOfInt;
              elementPositionArray[i]=(short)xBufChannel.position();
              xBufChannel.putInt(pagePointerList.get(i));
              xBufChannel.putInt(recLength);
              xBufChannel.putInt(CIDList.get(i));
              try{
              xBufChannel.put(temp,0,temp.length);
              }
              catch(Exception e)
              {
                  logManager.LogManager.log(6,"");
              }
              freeOffset+=2*Settings.sizeOfInt+recLength;
           }
           //for putting zero(erasing the rest of page){may new freeOffset be smaller than last one! }
           xBufChannel.erase(xBufChannel.position(),numOfKeys);
           
        //******************
        xBufChannel.position(Settings.pageSize - 1 - numOfKeys * Settings.sizeOfShort);//az tahe page shoru mikonim b khundan
        for(int i=0;i<numOfKeys;i++) //loop for setting elem positions at the end of page
        {             
             xBufChannel.putShort(elementPositionArray[i]);
        }              
        //freeOffset=freeOffset-prevMaxLength+maxLength;//This is Wrong!       
        }
        //********************************************************
        else 
        {
            recLength = newMaxKey.maxDeweyID.getDeweyId().length+Settings.sizeOfInt;//key DivNumber
            xBufChannel.putInt(childPointer);
            short firstElementPosition;
            firstElementPosition=(short)xBufChannel.position();
            xBufChannel.putInt(recLength);
            xBufChannel.putInt(newMaxKey.maxCID);
            temp = newMaxKey.maxDeweyID.getDeweyId();
            xBufChannel.put(temp,0,temp.length);//prevMaxKey gets deleted automatically
            xBufChannel.position(Settings.pageSize - 1 - numOfKeys * Settings.sizeOfShort);//az tahe page shoru mikonim b khundan
            xBufChannel.putShort(firstElementPosition);            
            freeOffset=2*(Settings.sizeOfInt)+recLength;
            
        }
        xBufChannel.position(Settings.freeOffsetPos);
        xBufChannel.putInt(freeOffset);
       // bufMngr.unpinBuffer(xBufChannel, true);
        return rootPointer;
    }
    public int splitLeafElement(int[] midPoint,ArrayList<Integer> CIDList,ArrayList<DeweyID> deweyIDList,int CID,DeweyID deweyID,int rootPointer,Stack parents) throws IOException, DBException
    {
        
        ElementBTreeNode twinNode;
        ElementBTreeNode rootNode=null;
        int parentPointer=-1;
        ElementBTreeNode parentNode=null;
        ElementMaxKey prevMaxKey=new ElementMaxKey();
        boolean rootRenewed=false;
        boolean maxKeyChanged=false;
               
        if(this.isRoot==1)
         { 
           rootRenewed=true;
          //inja creat static ???
           rootNode=new ElementBTreeNode(indexName,(byte)1,(byte)0, 0);//just built root has 2 keys
           //this.indexPage=rootPointer;//lazeme?
           rootPointer=rootNode.createElementBNode();//bayad dar GMD neveshte shavad****
           //tu in noghte nextPage this taghir mikone,man chi kar konam?dobare az file bkhunam?
           //this.parentPointer=rootPointer;
           this.isRoot=0;
           xBufChannel.position(Settings.isRootPos);
           xBufChannel.put(isRoot);//chon this ba unpin write mishe.
           if(this.IsLeaf())
               twinNode=new ElementBTreeNode(indexName,(byte)0,(byte)1,-1);//numOfKeys is not valid yet
               //isRoot , isLeaf dar xbuf neveshte nashode and.:(dar setMD set mishavad.
           else
               twinNode=new ElementBTreeNode(indexName,(byte)0,(byte)0,-1);//numOfKeys is not valid yet
         }           
         else 
         {
             rootRenewed=false;
             //load parent node:
             parentPointer=(int)parents.pop();
             parentNode=new ElementBTreeNode(parentPointer,indexName);//shayd bhetare yek tabe load bnvisim.
             parents.push(parentPointer);
             if(this.IsLeaf())
                twinNode=new ElementBTreeNode(indexName,(byte)0,(byte)1, -1);//numOfKeys is not valid yet
             else
                 twinNode=new ElementBTreeNode(indexName,(byte)0,(byte)0, -1);//numOfKeys is not valid yet

         }
                
        //InternalBuffer twin = bufMngr.getPage(twinPageNumber);
        int recLength;
        byte[] temp;
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
                        prevMaxKey.maxDeweyID=this.MaxKey.maxDeweyID;
                        this.MaxKey.maxCID=CIDList.get(midPoint[1]);
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
                     twinNode.MaxKey.maxDeweyID=deweyIDList.get(i);                 
                 }
                //length=dIDList.get(i).getDeweyId().recLength;
                temp =deweyIDList.get(i).getDeweyId();
                //twinNode.xBufChannel.putInt((int)pagePointerList.get(i));
                recLength = temp.length+Settings.sizeOfInt;
                twinNode.xBufChannel.putInt(recLength);
                twinNode.xBufChannel.putInt((int)CIDList.get(i)); 
                //if(i==midPoint[1]+1 || i==CIDList.size()-1)
                   logManager.LogManager.log(5,CIDList.get(i).toString());
                twinNode.xBufChannel.put(temp,0,temp.length);
                twinNode.freeOffset+=Settings.sizeOfInt+recLength;
            }
        } 
        else 
        {
            this.numOfKeys=midPoint[1];
            xBufChannel.putInt(midPoint[1]); //numOfKeys=i
//            if(this.MaxKey.maxCID!=CIDList.get(midPoint[1]-1))
//            {   
                prevMaxKey.maxCID=this.MaxKey.maxCID;
                prevMaxKey.maxDeweyID=this.MaxKey.maxDeweyID;
                this.MaxKey.maxCID=CIDList.get(midPoint[1]-1);
                this.MaxKey.maxDeweyID=deweyIDList.get(midPoint[1]-1);
                maxKeyChanged=true;
//            }

            twinNode.numOfKeys=CIDList.size()- midPoint[1];
            twinNode.xBufChannel.position(Settings.numOfKeysPos);
            twinNode.xBufChannel.putInt(twinNode.numOfKeys); //twin Num of keys

            for (int i = midPoint[1]; i < deweyIDList.size(); i++) 
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
                     twinNode.MaxKey.maxDeweyID=deweyIDList.get(i);
                 }
               temp = deweyIDList.get(i).getDeweyId();
               recLength = temp.length+Settings.sizeOfInt;
               //twinNode.xBufChannel.putInt((int)pagePointerList.get(i));
               twinNode.xBufChannel.putInt(recLength);
               twinNode.xBufChannel.putInt(CIDList.get(i));
               //if(i==midPoint[1] || i==CIDList.size()-1)
                  logManager.LogManager.log(6,CIDList.get(i).toString());
               twinNode.xBufChannel.put(temp, 0,temp.length);
               twinNode.freeOffset+=Settings.sizeOfInt+recLength;
               
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
         twinNode.indexPage=twinNode.writeElementBNode();
         //if(CID==35)
            logManager.LogManager.log(6,"this page(ElementIndx): "+this.indexPage+"'s twin is: "+ twinNode.indexPage);
            logManager.LogManager.log(6,"---------------------------------");
         //in this point we get twin's page no,so we should assign it to this.next page:
         
         //baraye parent v newRoot hargez nextP set nemikonim.
         
         if(rootRenewed==true)
         {
             //rootNode.loadElementBTreeNode(rootPointer, indexName); WE HAve it in memory
             rootPointer=rootNode.insertIntoInternalElement(this.MaxKey.maxCID,this.MaxKey.maxDeweyID,rootPointer,this.indexPage,parents);//in chie?az OSTAD
             rootPointer=rootNode.insertIntoInternalElement(twinNode.MaxKey.maxCID,twinNode.MaxKey.maxDeweyID,rootPointer,twinNode.indexPage,parents);
             rootNode.updateElementBNode(rootPointer);//???che lozumi dare?
             rootNode.closeElementNode();
         }
         else 
         {            
             parentPointer=(int)parents.pop();             
//             rootPointer=parentNode.updateInternalElement(this.MaxKey, rootPointer,this.indexPage, parents);
//             rootPointer=parentNode.insertIntoInternalElement(twinNode.MaxKey.maxCID,twinNode.MaxKey.maxDeweyID,rootPointer,twinNode.indexPage,parents);
             rootPointer=parentNode.insUpdaterElement(this.MaxKey, this.indexPage, twinNode.MaxKey, twinNode.indexPage, rootPointer, parents);
             parents.push(parentPointer);
             parentNode.closeElementNode();
         }
         return rootPointer;
    }
    public int splitInternalElement(int[] midPoint,ArrayList<Integer> CIDList,ArrayList<DeweyID> deweyIDList,ArrayList pagePointerList,int CID,DeweyID deweyID,int rootPointer,Stack parents) throws IOException, DBException
    {
        
        ElementBTreeNode twinNode;
        ElementBTreeNode rootNode=null;
        int parentPointer=-1;
        ElementBTreeNode parentNode=null;
        ElementMaxKey prevMaxKey=new ElementMaxKey();
        boolean rootRenewed=false;
        boolean maxKeyChanged=false;
               
        if(this.isRoot==1)
         { 
           rootRenewed=true;
          //inja creat static ???
           rootNode=new ElementBTreeNode(indexName,(byte)1,(byte)0, 0);//just built root has 2 keys
           //this.indexPage=rootPointer;//lazeme?
           rootPointer=rootNode.createElementBNode();//bayad dar GMD neveshte shavad****
           //tu in noghte nextPage this taghir mikone,man chi kar konam?dobare az file bkhunam?
           //this.parentPointer=rootPointer;
           this.isRoot=0;
           xBufChannel.position(Settings.isRootPos);
           xBufChannel.put(isRoot);//chon this ba unpin write mishe.
           if(this.IsLeaf())
               twinNode=new ElementBTreeNode(indexName,(byte)0,(byte)1,-1);//numOfKeys is not valid yet
               //isRoot , isLeaf dar xbuf neveshte nashode and.:(dar setMD set mishavad.
           else
               twinNode=new ElementBTreeNode(indexName,(byte)0,(byte)0,-1);//numOfKeys is not valid yet
         }           
         else 
         {
             rootRenewed=false;
             //load parent node:
             parentPointer=(int)parents.pop();
             parentNode=new ElementBTreeNode(parentPointer,indexName);//shayd bhetare yek tabe load bnvisim.
             parents.push(parentPointer);
             if(this.IsLeaf())
             {   
                 twinNode=new ElementBTreeNode(indexName,(byte)0,(byte)1, -1);//numOfKeys is not valid yet
                 logManager.LogManager.log(5,"WRONG:"+this.indexPage+ " is not expected to be leaf");
             
             }
             else
                 twinNode=new ElementBTreeNode(indexName,(byte)0,(byte)0, -1);//numOfKeys is not valid yet

         }
                
        //InternalBuffer twin = bufMngr.getPage(twinPageNumber);
        int recLength;
        byte[] temp;
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
                        prevMaxKey.maxDeweyID=this.MaxKey.maxDeweyID;
                        this.MaxKey.maxCID=CIDList.get(midPoint[1]);
                        this.MaxKey.maxDeweyID=deweyIDList.get(midPoint[1]);
                        maxKeyChanged=true;
//                    }
                            
            //koja baghie metadata twin neveshte mishe?
            twinNode.numOfKeys=CIDList.size() - (midPoint[1] + 1);
            twinNode.xBufChannel.position(Settings.numOfKeysPos);
            twinNode.xBufChannel.putInt(CIDList.size() - (midPoint[1] + 1)); //twin's Num of keys

            for (int i = midPoint[1] + 1; i < deweyIDList.size(); i++) 
            {
                 if(i==CIDList.size()-1)
                 {   
                     twinNode.MaxKey.maxCID=CIDList.get(i);
                     twinNode.MaxKey.maxDeweyID=deweyIDList.get(i);                 
                 }
                //length=dIDList.get(i).getDeweyId().recLength;
                temp =deweyIDList.get(i).getDeweyId();
                twinNode.xBufChannel.putInt((int)pagePointerList.get(i));
                recLength = temp.length+Settings.sizeOfInt;
                twinNode.xBufChannel.putInt(recLength);
                twinNode.xBufChannel.putInt((int)CIDList.get(i));   
//                if(i==midPoint[1] || i==CIDList.size()-1)
//                   logManager.LogManager.log(5,CIDList.get(i).toString());
                twinNode.xBufChannel.put(temp,0,temp.length);
                twinNode.freeOffset+=2*Settings.sizeOfInt+recLength;
            }
        } 
        else 
        {
            this.numOfKeys=midPoint[1];
            xBufChannel.putInt(midPoint[1]); //numOfKeys=i
//            if(this.MaxKey.maxCID!=CIDList.get(midPoint[1]-1))
//            {   
                prevMaxKey.maxCID=this.MaxKey.maxCID;
                prevMaxKey.maxDeweyID=this.MaxKey.maxDeweyID;
                this.MaxKey.maxCID=CIDList.get(midPoint[1]-1);
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
                     twinNode.MaxKey.maxDeweyID=deweyIDList.get(i);
                 }
               temp = deweyIDList.get(i).getDeweyId();
               recLength = temp.length+Settings.sizeOfInt;
               twinNode.xBufChannel.putInt((int)pagePointerList.get(i));
               twinNode.xBufChannel.putInt(recLength);
               twinNode.xBufChannel.putInt(CIDList.get(i));
//               if(i==midPoint[1] || i==CIDList.size()-1)
//                   logManager.LogManager.log(5,CIDList.get(i).toString());
               twinNode.xBufChannel.put(temp, 0, temp.length);
               twinNode.freeOffset+=2*Settings.sizeOfInt+recLength;
               
            }
        }
       
         twinNode.xBufChannel.position(Settings.freeOffsetPos);
         twinNode.xBufChannel.putInt(twinNode.freeOffset);         
         twinNode.indexPage=twinNode.writeElementBNode();
         logManager.LogManager.log(6,"internal: "+this.indexPage+" twin:"+twinNode.indexPage);
         
         if(rootRenewed==true)
         {
             //rootNode.loadElementBTreeNode(rootPointer, indexName);WE HAVe IT in memory!
             rootPointer=rootNode.insertIntoInternalElement(this.MaxKey.maxCID,this.MaxKey.maxDeweyID,rootPointer,this.indexPage,parents);//in chie?az OSTAD
             rootPointer=rootNode.insertIntoInternalElement(twinNode.MaxKey.maxCID,twinNode.MaxKey.maxDeweyID,rootPointer,twinNode.indexPage,parents);
             rootNode.updateElementBNode(rootPointer);
             rootNode.closeElementNode();
         }
         else 
         {            
             parentPointer=(int)parents.pop();
//             rootPointer=parentNode.updateInternalElement(this.MaxKey, rootPointer,this.indexPage, parents);
//             rootPointer=parentNode.insertIntoInternalElement(twinNode.MaxKey.maxCID,twinNode.MaxKey.maxDeweyID,rootPointer,twinNode.indexPage,parents);
             rootPointer=parentNode.insUpdaterElement(this.MaxKey, this.indexPage, twinNode.MaxKey, twinNode.indexPage, rootPointer, parents);
             parents.push(parentPointer);
             parentNode.closeElementNode();
         }
         return rootPointer;
    }
       //Used for Update:
    public int splitInternalElement(int[] midPoint,ArrayList<Integer> CIDList,ArrayList<DeweyID> deweyIDList,ArrayList pagePointerList,int rootPointer,Stack parents) throws IOException, DBException
    {
        ElementBTreeNode twinNode;
        ElementBTreeNode rootNode=null;
        int parentPointer=-1;
        ElementBTreeNode parentNode=null;
        ElementMaxKey prevMaxKey=new ElementMaxKey();
        boolean rootRenewed=false;
        boolean maxKeyChanged=false;
               
        if(this.isRoot==1)
         { 
           rootRenewed=true;
          //inja creat static ???
           rootNode=new ElementBTreeNode(indexName,(byte)1,(byte)0, 0);//just built root has 2 keys
           //this.indexPage=rootPointer;//lazeme?
           rootPointer=rootNode.createElementBNode();//bayad dar GMD neveshte shavad****
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
           
               twinNode=new ElementBTreeNode(indexName,(byte)0,(byte)0,-1);//numOfKeys is not valid yet
               
 
         }           
         else 
         {
             rootRenewed=false;
             //load parent node:
             parentPointer=(int)parents.pop();
             parentNode=new ElementBTreeNode(parentPointer,indexName);//shayd bhetare yek tabe load bnvisim.
             parents.push(parentPointer);
             if(this.IsLeaf())
                twinNode=new ElementBTreeNode(indexName,(byte)0,(byte)1, -1);//numOfKeys is not valid yet
             else
                 twinNode=new ElementBTreeNode(indexName,(byte)0,(byte)0, -1);//numOfKeys is not valid yet

         }
                
        //InternalBuffer twin = bufMngr.getPage(twinPageNumber);
        int recLength;
        byte[] temp;
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
//                //length=dIDList.get(i).getDeweyId().recLength;
//                temp =deweyIDList.get(i).getDeweyId();
//                twinNode.xBufChannel.putInt((int)pagePointerList.get(i));
//                recLength = temp.length+Settings.sizeOfInt;
//                twinNode.xBufChannel.putInt(recLength);
//                twinNode.xBufChannel.putInt((int)CIDList.get(i));                
//                twinNode.xBufChannel.put(temp,0,temp.length);
//                twinNode.freeOffset+=2*Settings.sizeOfInt+recLength;
//            }
       /////////////////// } 
//        else 
//        {
            this.numOfKeys=midPoint[1];
            xBufChannel.putInt(midPoint[1]); //numOfKeys=i
//            if(this.MaxKey.maxCID!=CIDList.get(midPoint[1]-1))
//            {   
                prevMaxKey.maxCID=this.MaxKey.maxCID;
                prevMaxKey.maxDeweyID=this.MaxKey.maxDeweyID;
                this.MaxKey.maxCID=CIDList.get(midPoint[1]-1);
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
                     twinNode.MaxKey.maxDeweyID=deweyIDList.get(i);
                 }
               temp = deweyIDList.get(i).getDeweyId();
               recLength = temp.length+Settings.sizeOfInt;
               twinNode.xBufChannel.putInt((int)pagePointerList.get(i));
               twinNode.xBufChannel.putInt(recLength);
               twinNode.xBufChannel.putInt(CIDList.get(i));
               if(i==midPoint[1] || i==CIDList.size()-1)
                   logManager.LogManager.log(6,CIDList.get(i).toString());
               twinNode.xBufChannel.put(temp, 0, temp.length);
               twinNode.freeOffset+=2*Settings.sizeOfInt+recLength;
               
            }
//        }
       
         twinNode.xBufChannel.position(Settings.freeOffsetPos);
         twinNode.xBufChannel.putInt(twinNode.freeOffset);         
         twinNode.indexPage=twinNode.writeElementBNode();
         logManager.LogManager.log(6,"internal: "+this.indexPage+" twin: "+twinNode.indexPage);
         if(rootRenewed==true)
         {
             //rootNode.loadElementBTreeNode(rootPointer, indexName);WE HAVe IT in memory!
             rootPointer=rootNode.insertIntoInternalElement(this.MaxKey.maxCID,this.MaxKey.maxDeweyID,rootPointer,this.indexPage,parents);//in chie?az OSTAD
             rootPointer=rootNode.insertIntoInternalElement(twinNode.MaxKey.maxCID,twinNode.MaxKey.maxDeweyID,rootPointer,twinNode.indexPage,parents);
             rootNode.updateElementBNode(rootPointer);
             rootNode.closeElementNode();
         }
         else 
         {            
             parentPointer=(int)parents.pop();
//             rootPointer=parentNode.updateInternalElement(this.MaxKey, rootPointer,this.indexPage, parents);
//             rootPointer=parentNode.insertIntoInternalElement(twinNode.MaxKey.maxCID,twinNode.MaxKey.maxDeweyID,rootPointer,twinNode.indexPage,parents);
             rootPointer=parentNode.insUpdaterElement(this.MaxKey, this.indexPage, twinNode.MaxKey, twinNode.indexPage, rootPointer, parents);
             parents.push(parentPointer);
             parentNode.closeElementNode();
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
    public int createElementBNode() throws IOException
    {
        return logicFileMgr.creatRootPage(indexName, isRoot, isLeaf, numOfKeys, xBufChannel);
               // logicFileMgr.addPage(indexName,isRoot,isLeaf,numOfKeys);
        //must add some code to write page's data as well.
    } 
    public int writeElementBNode() throws IOException
    {
        return logicFileMgr.createTwinPage(indexName, freeOffset, isRoot, isLeaf, numOfKeys, xBufChannel);
                //logicFileMgr.addPage(indexName,freeOffset,isRoot,isLeaf,numOfKeys,this.xBufChannel);
        
    } 
    public void updateElementBNode(int pageNumber) throws IOException
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
        closeElementNode();
    }
    public void closeElementNode()
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
//        int recLength;
//        byte[] curId;
//        int curCid=-1;
//        DeweyID curDId=null;
//        if(bound>0)
//        {
//            xBufChannel.position(xbufMarker);
//            recLength=xBufChannel.getInt();
//            curCid=xBufChannel.getInt();
//            curId =new byte[recLength-Settings.sizeOfInt];
//            curDId=new DeweyID();
//            xBufChannel.get(curId,0,recLength-Settings.sizeOfInt);
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
     public OutputWrapper <ElementBTreeNode,DeweyID> next(int CID) throws IOException, DBException            
    {
        //we assume that during "next"ing no more keys are added.(otherwise bound should be updated somehow)
        int recLength;
        byte[] curId;
        int curCid=-1;
        DeweyID curDId=null;
        ElementBTreeNode sibling=null;
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
                      sibling=new ElementBTreeNode(nextPage,indexName);
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

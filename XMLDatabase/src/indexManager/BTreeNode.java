package indexManager;


import indexManager.util.OutputWrapper;
import LogicalFileManager.LogicalFileManager;
import bufferManager.BufferManager;
import bufferManager.InternalBuffer;
import bufferManager.XBufferChannel;
import java.io.IOException;
import xmlProcessor.DeweyID;
import java.util.ArrayList;
import java.util.Stack;
import commonSettings.Settings;
import indexManager.util.Record;
import xmlProcessor.DBServer.DBException;

public class BTreeNode
{
    // private byte pageState; //for future use

    private byte isRoot;
    private byte isLeaf;
    //private int parentPointer;
    private ElementMaxKey MaxKey;
    private String MaxQname;
    private int numOfKeys;
    private int freeOffset;
    //minkeys=20
    //private ByteBuffer byteBuf;
    private int indexPage;
    private XBufferChannel xBufChannel;
    private LogicalFileManager logicFileMgr;
    private String indexName;
    private BufferManager bufMngr;
    private int bound;//auxillary var for open and next funcs

    public BTreeNode(String indexName,byte isRoot,byte isLeaf,int numOfKeys) throws IOException, DBException//farz konim in internal noe bashe v y clase leafnode ham bayad bnvisam
    {
        /**creates a BTreenode object in memory*/
        
        this.indexName=indexName;
        this.isRoot=isRoot;
        this.isLeaf=isLeaf;
        this.numOfKeys=numOfKeys;
        this.MaxKey=new ElementMaxKey();
        logicFileMgr = LogicalFileManager.Instance;
        bufMngr = BufferManager.getInstance();
       // xBufChannel=new xBufChannel();//vasl bshe b buf mgr:getfreepage()
        xBufChannel=bufMngr.getFreePage();
        this.indexPage=xBufChannel.getPageNumber();
        xBufChannel.clear();
                
    }

    public BTreeNode(int indexPage,String indexName) throws IOException, DBException 
    {
        /**LOADs btreeNode from its page on disk into memory:*/
        logicFileMgr = LogicalFileManager.Instance;//in tu ghesmate load lazem nis?
        this.indexName=indexName;
        this.indexPage=indexPage;
        this.MaxKey=new ElementMaxKey();
        bufMngr = BufferManager.getInstance();
        xBufChannel = bufMngr.getPage(indexPage); //inja wMra kharab mikone
        xBufChannel.position(Settings.freeOffsetPos);//5
        freeOffset = xBufChannel.getInt();
        //byteBuf=xBuf.getByteBuffer();
        isRoot=xBufChannel.get();
        isLeaf = xBufChannel.get();
        //xBufChannel.position(Settings.numOfKeysPos);
        numOfKeys = xBufChannel.getInt();
        bound=numOfKeys;

    }
    
    public void loadBTreeNode(int indexPage,String indexName) throws IOException, DBException 
    {
        //LOAD btreeNode:
        
        //return bhtare k khodeshBNode bashe ya na?
        this.indexPage=indexPage;
        this.MaxKey=new ElementMaxKey();
        //Should be REVISED:no need to every time getPage():
        xBufChannel = bufMngr.getPage(indexPage);
        xBufChannel.position(Settings.freeOffsetPos);//5
        freeOffset = xBufChannel.getInt();
        //byteBuf=xBuf.getByteBuffer();
        isRoot=xBufChannel.get();
        isLeaf = xBufChannel.get();
        //InternalBuffer.position(Settings.numOfKeysPos);
        numOfKeys = xBufChannel.getInt();
        bound=numOfKeys;

    }

    public boolean IsLeaf() 
    {
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

    public int internalNodeSearch(DeweyID deweyId,int CID,String qName) 
    {
        //page: pagestate,nextpage,freeoffset
        //ps,isroot,isleaf,number of keys in this page,freeoff,nextp(leftmost),deweyId,nextp,deweyId,nextp(rightmost)
        int pagePointer = -1;//null
        int recLength;
//        int strLength;
        int curCID;
        byte[] curId;
        DeweyID curDId;
//        byte[] curQnameSeq;
//        String curQnameStr;
        xBufChannel.position(Settings.dataStartPos);        
        if (numOfKeys > 0)
        {
            for (int i = 0; i < numOfKeys; i++)
            {
                pagePointer = xBufChannel.getInt();//next page adrs
                recLength = xBufChannel.getInt();
//                strLength=xBufChannel.getInt();
                curCID=xBufChannel.getInt();
                curId = new byte[recLength-Settings.sizeOfInt];
                xBufChannel.get(curId, 0, recLength-Settings.sizeOfInt);
                //curDId=new DeweyID();
                //curDId.setDeweyId(curId);
                curDId=new DeweyID(curId);
//                curQnameSeq=new byte[strLength];
//                xBufChannel.get(curQnameSeq,0,strLength);
//                curQnameStr=new String(curQnameSeq);
                if (!(curDId.notGreaterThan(deweyId))) 
                {
                    break;
                }
            }
            //hata agar key az akharin DID bozorgtar bashad baz ham akharin pP i k khandim ra ret mikonim
        }
        //fk nakonam dg nizai b chek kardan bashe.
        return pagePointer;//page i ro bar migardune k result max un hast.
        //ki<k<ki+1

    }
    
    public int internalNodeSearch(DeweyID deweyId) 
    {
        //page: pagestate,nextpage,freeoffset
        //ps,isroot,isleaf,number of keys in this page,freeoff,nextp(leftmost),deweyId,nextp,deweyId,nextp(rightmost)
        int pagePointer = -1;//null
        int recLength;
//        int strLength;
        int curCID;
        byte[] curId;
        DeweyID curDId;
//        byte[] curQnameSeq;
//        String curQnameStr;
        xBufChannel.position(Settings.dataStartPos);        
        if (numOfKeys > 0)
        {
            for (int i = 0; i < numOfKeys; i++)
            {
                pagePointer = xBufChannel.getInt();//next page adrs
                recLength = xBufChannel.getInt();
//                strLength=xBufChannel.getInt();
                curCID=xBufChannel.getInt();
                curId = new byte[recLength-Settings.sizeOfInt];
                xBufChannel.get(curId, 0, recLength-Settings.sizeOfInt);
//                curDId=new DeweyID();
//                curDId.setDeweyId(curId);
                curDId=new DeweyID(curId);
                logManager.LogManager.log(5,curDId.toString());
//                curQnameSeq=new byte[strLength];
//                xBufChannel.get(curQnameSeq,0,strLength);
//                curQnameStr=new String(curQnameSeq);
                if (!(curDId.notGreaterThan(deweyId))) 
                {
                    break;
                }
            }
            //hata agar key az akharin DID bozorgtar bashad baz ham akharin pP i k khandim ra ret mikonim
        }
        //fk nakonam dg nizai b chek kardan bashe.
        return pagePointer;//page i ro bar migardune k result max un hast.
        //ki<k<ki+1

    }
    
    public boolean leafNodeSearch(DeweyID deweyId,int CID,String qName)
    {
        //dar barg ha bayd pagePo inter hamun null bashand,ya aslan nabashand.(tu method insert taiin mishe)
        int pagePointer = -1; //null
        int recLength;
        int strLength;
        byte[] curId;
        int curCID;
        byte[] curQnameSeq;
        String curQnameStr;
        DeweyID curDId ; 
        xBufChannel.position(Settings.dataStartPos);//3+4+4??????
        if(numOfKeys>0){
            for (int i = 0; i < numOfKeys; i++)
            {
                //pagePointer = xBufChannel.getInt();//next page adrs
                recLength = xBufChannel.getInt();//DIDLen+4(CID)
                strLength=xBufChannel.getInt();
                curCID=xBufChannel.getInt();
                curId = new byte[recLength- Settings.sizeOfInt];
                xBufChannel.get(curId,0,recLength-Settings.sizeOfInt);
                //curDId=new DeweyID();
                //curDId.setDeweyId(curId);
                curDId=new DeweyID(curId);
                curQnameSeq=new byte[strLength];
                xBufChannel.get(curQnameSeq,0,strLength);
                curQnameStr=new String(curQnameSeq);

             //  result = curDId;
                if (curDId.equals(deweyId)&& (curCID==CID)&&(qName==curQnameStr) )
                {
                    return true; //key found
                } else if (!(curDId.notGreaterThan(deweyId))) //!nnotGreaterThan=GreatreThan :)
                {
                    return false; //key not found
                }
            }
        }
        return false;//not found

    }
     public boolean leafNodeSearch(DeweyID deweyId)
    {
        //dar barg ha bayd pagePo inter hamun null bashand,ya aslan nabashand.(tu method insert taiin mishe)
        int pagePointer = -1; //null
        int recLength;
        int strLength;
        byte[] curId;
        int curCID;
        byte[] curQnameSeq;
        String curQnameStr;
        DeweyID curDId ; 
        xBufChannel.position(Settings.dataStartPos);//3+4+4??????
        if(numOfKeys>0){
            for (int i = 0; i < numOfKeys; i++)
            {
                //pagePointer = xBufChannel.getInt();//next page adrs
                recLength = xBufChannel.getInt();//DIDLen+4(CID)
                strLength=xBufChannel.getInt();
                curCID=xBufChannel.getInt();
                curId = new byte[recLength- Settings.sizeOfInt];
                xBufChannel.get(curId,0,recLength-Settings.sizeOfInt);
                //curDId=new DeweyID();
               // curDId.setDeweyId(curId);
                curDId=new DeweyID(curId);
                curQnameSeq=new byte[strLength];
                xBufChannel.get(curQnameSeq,0,strLength);
                curQnameStr=new String(curQnameSeq);

             //  result = curDId;
                if (curDId.equals(deweyId))
                {
                    return true; //key found
                } else if (!(curDId.notGreaterThan(deweyId))) //!nnotGreaterThan=GreatreThan :)
                {
                    return false; //key not found
                }
            }
        }
        return false;//not found

    }
    public int insertIntoLeaf(DeweyID deweyID,int CID,String qName,int rootPointer,Stack parents) throws IOException, DBException 
    {

        ArrayList<Integer> CIDList = new ArrayList<>();
        ArrayList<DeweyID> dIDList = new ArrayList<>();
        ArrayList<String> qNameList=new ArrayList<>();
        //ArrayList<Integer> pagePointerList = new ArrayList<>();
        short[] elementPositionArray;//?
        int recLength;
        int strLength;
        int keyLength;
        //int pagePointer;
        byte[] curId;
        byte[] curQnameSeq;
        String curQnameStr;
        boolean keyInserted=false;
        boolean splitted=false;
        byte[] temp1;
        byte[] temp2;
        int[] midPoint;
        int curCid;
        DeweyID curDId;
        ElementMaxKey lastMaxKey =new ElementMaxKey();
        String lastMaxQname;
        xBufChannel.position(Settings.dataStartPos);//3+4+4 ps+nextPage+freeOffset+isL+isR+numOf
        //in nextPage bayd hatman bashe b hatere yaftane akharin page
        if (numOfKeys > 0) 
        {
            for (int i = 0; i < numOfKeys; i++) 
            {
                //assumption:do not have pagePointer in leaves
               // pagePointer= xBufChannel.getInt();//next page adrs
               // pagePointerList.add(pagePointer);
                //assumption:recLen,strLen,Cid,deweyID,qName                
                recLength=xBufChannel.getInt();//assumption:recLength=DeweyIdsize+4(Cid)
                strLength=xBufChannel.getInt();
                curCid=xBufChannel.getInt();
                curId = new byte[recLength-Settings.sizeOfInt];
                //curDId=new DeweyID();
                xBufChannel.get(curId,0,recLength-Settings.sizeOfInt);
                //curDId.setDeweyId(curId);
                curDId=new DeweyID(curId);
                //logManager.LogManager.log(6,curDId.toString());
                curQnameSeq=new byte[strLength];                
                xBufChannel.get(curQnameSeq,0,strLength);
                curQnameStr=new String(curQnameSeq);
                
                if(i==numOfKeys-1)
                {
                    lastMaxKey.maxCID=curCid;
                    lastMaxKey.maxDeweyID=curDId;
                    lastMaxQname=curQnameStr;
                } 
                if(deweyID.equals(curDId))
                    {                                                                                                     
                       throw new DBException(("Page:"+this.indexPage)+" Repeated key!"); 
                    }//end if(deweyID.equals(curDId))
                if (!keyInserted && deweyID.notGreaterThan(curDId)) 
                {
                        // pagePointerList.add(Settings.nullPagePointer);
                        CIDList.add(CID);//cheghad ghashang mishe inaro nevesht :)))
                        dIDList.add(deweyID);
                        qNameList.add(qName);
                        keyInserted=true;                    
                }
                CIDList.add(curCid);
                dIDList.add(curDId);
                qNameList.add(curQnameStr);
                        
            }
            //pagePointer = xBufChannel.getInt();//rightMost pointer NADARIM
            // pagePointerList.add(pagePointer);
            if (!keyInserted) 
            {
                //pagePointerList.add(Settings.nullPagePointer);
                CIDList.add(CID);
                dIDList.add(deweyID);
                qNameList.add(qName);
                this.MaxKey.maxCID=CID;
                this.MaxKey.maxDeweyID=deweyID;
                this.MaxQname=qName;
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
            strLength=qName.length();
            int availableSpace=Settings.pageSize - 1 - freeOffset - 2*(numOfKeys-1);//lastNumOfKeys=numOfKeys-1
            int requiredSpace=2*Settings.sizeOfInt+keyLength+strLength+2;//2bytes for postion
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
                rootPointer=this.splitLeaf(midPoint,CIDList,dIDList,qNameList,CID,deweyID,qName,rootPointer,parents);
                splitted=true;
            }
        
        
        //inja chon rewrite darim bayad dar noghteye data start bashim,yani bad az mahale num of keys
             //in bakhsh dar 2 halat ejra mishe:
            //1)vaghti split nadarim.
            //2)bad az split
           xBufChannel.position(Settings.dataStartPos);
           elementPositionArray = new short[numOfKeys];  
           BTreeNode parentNode;
           int parentPointer;
           if(!splitted && parents.size()>0)//if split did not happen!:
           {
              if(!lastMaxKey.maxDeweyID.equals(this.MaxKey.maxDeweyID) && lastMaxKey.maxDeweyID.notGreaterThan(this.MaxKey.maxDeweyID))//SPLIT: last>new max, without SPLIT:last<max
              {
                      parentPointer=(int)parents.pop();
                      parentNode=new BTreeNode(parentPointer,indexName);
                      rootPointer=parentNode.updateInternalNode(this.MaxKey,rootPointer,this.indexPage,parents);
                      //parents.push(parentPointer);
                      parentNode.closeBTreeNode();
              }
           }
           //reset freeoffset
           freeOffset=Settings.dataStartPos;
           for (int i = 0; i < numOfKeys; i++)
           {
              //length=dIDList.get(i).getDeweyId().recLength;
              temp1 = dIDList.get(i).getDeweyId();     
              recLength = temp1.length+Settings.sizeOfInt;
              curQnameSeq=qNameList.get(i).getBytes();
              elementPositionArray[i]=(short)xBufChannel.position();
              //InternalBuffer.putInt(pagePointerList.get(i));
              xBufChannel.putInt(recLength);
              xBufChannel.putInt(curQnameSeq.length);
              xBufChannel.putInt(CIDList.get(i));              
              xBufChannel.put(temp1,0,temp1.length);              
              xBufChannel.put(curQnameSeq,0,curQnameSeq.length);
              freeOffset+=2*Settings.sizeOfInt+recLength+curQnameSeq.length;
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
            keyLength = deweyID.getDeweyId().length+Settings.sizeOfInt;//key DivNumber+ cid len
            strLength=qName.length();
            temp2=qName.getBytes();
            //----------------
            numOfKeys++;
           //Prepare buffer:
            xBufChannel.position(Settings.numOfKeysPos);
            xBufChannel.putInt(numOfKeys);
            //--------------------------
            this.MaxKey.maxCID=CID;
            this.MaxKey.maxDeweyID=deweyID;
            this.MaxQname=qName;
            //InternalBuffer.putInt(Settings.nullPagePointer);
            short firstElementPosition;
            firstElementPosition=(short)xBufChannel.position();
            xBufChannel.putInt(keyLength);
            xBufChannel.putInt(strLength);
            xBufChannel.putInt(CID);
            temp1 = deweyID.getDeweyId();
            xBufChannel.put(temp1, 0,temp1.length);
            xBufChannel.put(temp2,0,strLength);
            //for puting ZERO(erasing the rest of page)
            xBufChannel.erase(xBufChannel.position(),numOfKeys);
            xBufChannel.position(Settings.pageSize - 1 - numOfKeys * Settings.sizeOfShort);//az tahe page shoru mikonim b khundan
            xBufChannel.putShort(firstElementPosition);            
            freeOffset=freeOffset+2*Settings.sizeOfInt+keyLength+strLength;
            
        }
        xBufChannel.position(Settings.freeOffsetPos);
        xBufChannel.putInt(freeOffset);
        //engar unpin doros anjam nemishe chon inja root=0,leaf=1 hast vali ghalat neveshte mishe:
       // bufMngr.unpinBuffer(xBufChannel, true);//we may need this page still!
        
        return rootPointer;
    }
  
    public int insertIntoInternal(DeweyID deweyID,int CID,int rootPointer,int childPointer,Stack parents) throws IOException, DBException 
    {
        ArrayList<Integer> CIDList = new ArrayList<>();
        ArrayList<DeweyID> dIDList = new ArrayList<>();
        ArrayList<String> qNameList=new ArrayList<>();
        ArrayList<Integer> pagePointerList = new ArrayList<>();
        
        short[] elementPositionArray;//?
        int recLength;
//        int strLength;
        int pagePointer;
        byte[] curId;
        boolean keyInserted=false;
        boolean splitted=false;
        byte[] temp1;
        byte[]temp2;
        int[] midPoint;
        int curCId;
        DeweyID curDId;
//        String curQnameStr;
//        byte[] curQnameSeq;
        ElementMaxKey lastMaxKey=new ElementMaxKey();
//        String lastMaxQname;
        
        xBufChannel.position(Settings.dataStartPos);//3+4+4 ps+nextPage+freeOffset+isL+isR+numOf
        //in nextPage bayd hatman bashe b hatere yaftane akharin page
        if (numOfKeys > 0) 
        {
            for (int i = 0; i < numOfKeys; i++) 
            {
                pagePointer = xBufChannel.getInt();//next page adrs
                //pagePointerList.add(pagePointer);NOWAY! the source of buuuugggg!
                recLength = xBufChannel.getInt();
//                strLength=xBufChannel.getInt();
                curCId=xBufChannel.getInt();
                curId = new byte[recLength-Settings.sizeOfInt];
                //curDId=new DeweyID();
                xBufChannel.get(curId, 0, recLength-Settings.sizeOfInt);
                //curDId.setDeweyId(curId);
                curDId=new DeweyID(curId);
//                curQnameSeq=new byte[strLength];                
//                xBufChannel.get(curQnameSeq,0,strLength);                                  
//                curQnameStr=new String(curQnameSeq);
                if(i==numOfKeys-1)
                {
                    lastMaxKey.maxCID=curCId;
                    lastMaxKey.maxDeweyID=curDId;
//                    lastMaxQname=curQnameStr;
                } 
                
                if(deweyID.equals(curDId))
                    {                                                                                 
                       throw new DBException(("Page:"+this.indexPage)+" Repeated key!");
                     
                    }// 
                if (!keyInserted && (deweyID.notGreaterThan(curDId) && !deweyID.equals(curDId))) 
                {      
                        // pagePointerList.add(Settings.nullPagePointer);
                        pagePointerList.add(childPointer);
                        CIDList.add(CID);//cheghad ghashang mishe inaro nevesht :)))
                        dIDList.add(deweyID);
//                        qNameList.add(qName);
                        keyInserted=true;
                   
                }
                pagePointerList.add(pagePointer);
                CIDList.add(curCId);
                dIDList.add(curDId);
//                qNameList.add(curQnameStr);
            }
            //????rightmost darim ya na?
            //pagePointer = xBufChannel.getInt();//right most pointer NADARIM
            // pagePointerList.add(pagePointer);
            if (!keyInserted) 
            {
                pagePointerList.add(childPointer);
                CIDList.add(CID);
                dIDList.add(deweyID);
//                qNameList.add(qName);
                this.MaxKey.maxCID=CID;
                this.MaxKey.maxDeweyID=deweyID;
//                this.MaxQname=qName;
                keyInserted=true;
            }
            numOfKeys++;
            //Prepare buffer:
            xBufChannel.position(Settings.numOfKeysPos);
            xBufChannel.putInt(numOfKeys);
            //TODO: byte to short!!!
            //Note:LastNumOfKeys=numOfKeys-1
            recLength = deweyID.getDeweyId().length+Settings.sizeOfInt;//key DivNumber+ciden
//            strLength=qName.length();
            int availableSpace=Settings.pageSize - 1 - freeOffset - Settings.sizeOfShort*(numOfKeys-1);//lastNumOfKeys=numOfKeys-1
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
                rootPointer=this.splitInternal(midPoint,CIDList,dIDList,pagePointerList,CID,deweyID,rootPointer,parents);
                splitted=true;
            }
        
        
        //inja chon rewrite darim bayad dar noghteye data start bashim,yani bad az mahale num of keys
        //if(numOfKeys>1)//chon k ++ shode!
        //{
           xBufChannel.position(Settings.dataStartPos);
           elementPositionArray = new short[numOfKeys];  
           BTreeNode parentNode;
           int parentPointer;
           if(!splitted && !parents.empty())
           {                
              if(!lastMaxKey.maxDeweyID.equals(this.MaxKey.maxDeweyID) && lastMaxKey.maxDeweyID.notGreaterThan(this.MaxKey.maxDeweyID))               
              {
                  parentPointer=(int)parents.pop();
                  parentNode=new BTreeNode(parentPointer,indexName);
                  rootPointer=parentNode.updateInternalNode(this.MaxKey,rootPointer,this.indexPage,parents);
                  //parents.push(parentPointer);
                  parentNode.closeBTreeNode();
              }
//                  
           }
           //reset freeoffset
           freeOffset=Settings.dataStartPos;
           for (int i = 0; i < numOfKeys; i++)
           {
              //length=dIDList.get(i).getDeweyId().recLength;
              temp1 = dIDList.get(i).getDeweyId();  
              recLength = temp1.length+Settings.sizeOfInt;
//              temp2=qNameList.get(i).getBytes();
//              strLength=temp2.length;
              elementPositionArray[i]=(short)xBufChannel.position();
              xBufChannel.putInt(pagePointerList.get(i));
              xBufChannel.putInt(recLength);
//              xBufChannel.putInt(strLength);
              xBufChannel.putInt(CIDList.get(i));
              xBufChannel.put(temp1,0,temp1.length);
//              xBufChannel.put(temp2,0,strLength);
              freeOffset+=2*Settings.sizeOfInt+recLength;
           }
           //for puting zero(erasing the rest of page)
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
            recLength = deweyID.getDeweyId().length+Settings.sizeOfInt;//key DivNumber+CID size
            //strLength=qName.length();
            //----------------
            numOfKeys++;
           //Prepare buffer:
            xBufChannel.position(Settings.numOfKeysPos);
            xBufChannel.putInt(numOfKeys);
            //--------------------------
            this.MaxKey.maxCID=CID;
            this.MaxKey.maxDeweyID=deweyID;
            //this.MaxQname=qName;
            
            xBufChannel.putInt(childPointer);
            short firstElementPosition;
            firstElementPosition=(short)xBufChannel.position();
            xBufChannel.putInt(recLength);
            //xBufChannel.putInt(strLength);
            xBufChannel.putInt(CID);
            temp1 = deweyID.getDeweyId();
            xBufChannel.put(temp1,0,temp1.length);
            //temp2=qName.getBytes();
           // xBufChannel.put(temp2,0,strLength);
            xBufChannel.position(Settings.pageSize-1-numOfKeys * Settings.sizeOfShort);//az tahe page shoru mikonim b khundan
            xBufChannel.putShort(firstElementPosition);            
            freeOffset= freeOffset+2*(Settings.sizeOfInt)+recLength;            
        }
        xBufChannel.position(Settings.freeOffsetPos);
        xBufChannel.putInt(freeOffset);
        //bufMngr.unpinBuffer(xBufChannel, true);
        return rootPointer;
   }
    public int insUpdater(ElementMaxKey upMaxKey,int upChildPointer,ElementMaxKey insMaxKey,int insChildPointer,int rootPointer,Stack parents) throws IOException, DBException
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
                xBufChannel.get(curId,0,recLength-Settings.sizeOfInt);
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
                   if (!keyInserted && (insMaxKey.maxDeweyID.notGreaterThan(curDId) || insMaxKey.maxDeweyID.equals(curDId))) 
                   {
                    if(insMaxKey.maxDeweyID.equals(curDId))
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

                    }
                    else 
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
                rootPointer=this.splitInternal(midPoint, CIDList, dIDList, pagePointerList, insMaxKey.maxCID, insMaxKey.maxDeweyID, rootPointer, parents);
                splitted=true;
            }
            
            BTreeNode parentNode;
            int parentPointer;
            if(!splitted && !parents.empty())
                if(!lastMaxKey.maxDeweyID.equals(this.MaxKey.maxDeweyID)&& lastMaxKey.maxDeweyID.notGreaterThan(this.MaxKey.maxDeweyID))//SPLIT: last>max, without SPLIT:last<=max
                {                      
                    parentPointer=(int)parents.pop();
                    parentNode=new BTreeNode(parentPointer,indexName);
                    rootPointer=parentNode.updateInternalNode(this.MaxKey,rootPointer,this.indexPage,parents);
                    parents.push(parentPointer);
                    parentNode.closeBTreeNode();
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
            xBufChannel.putInt(upChildPointer);            
            elementPosition[0]=(short)xBufChannel.position();
            xBufChannel.putInt(recLength);
            xBufChannel.putInt(upMaxKey.maxCID);
            temp = upMaxKey.maxDeweyID.getDeweyId();
            xBufChannel.put(temp,0,temp.length);//prevMaxKey gets deleted automatically
            //INSERTING:
            recLength=insMaxKey.maxDeweyID.getDeweyId().length+Settings.sizeOfInt;
            xBufChannel.putInt(insChildPointer);
            elementPosition[1]=(short)xBufChannel.position();
            xBufChannel.putInt(recLength);
            xBufChannel.putInt(insMaxKey.maxCID);
            temp = insMaxKey.maxDeweyID.getDeweyId();
            xBufChannel.put(temp,0,temp.length);
           
            xBufChannel.position(Settings.pageSize - 1 - numOfKeys * Settings.sizeOfShort);//az tahe page shoru mikonim b khundan
            xBufChannel.putShort(elementPosition[0]);
            xBufChannel.putShort(elementPosition[1]); 
            
            freeOffset=2*(2*(Settings.sizeOfInt)+recLength); //2keys!
            
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
    public int updateInternalNode(ElementMaxKey newMaxKey,int rootPointer,int childPointer,Stack parents) throws IOException, DBException
    {
        
        ArrayList<DeweyID> dIDList = new ArrayList<>();
        ArrayList<Integer> pagePointerList = new ArrayList<>();
        ArrayList<Integer> CIDList = new ArrayList<>();
        //ArrayList<String> qNameList=new ArrayList<>();
        short[] elementPositionArray;
        int prevMaxLength=0;
        int maxKeyLength;
        int recLength;
        //int strLength;
        int keyLength;
        //int qLength;
        int lastFreeOffset;
        int estimatedFreeOffset;
        int pagePointer;
        byte[] curId;
        byte[] temp1;
        byte[] temp2;
        int curCId=0;
        DeweyID curDId;
        //byte[] curQnameSeq;
        //String curQnameStr;
        ElementMaxKey lastMaxKey=new ElementMaxKey();
        String lastMaxQname;
        boolean splitted=false;
        int midPoint[];
        
        xBufChannel.position(Settings.dataStartPos);//3+4+4 ps+nextPage+freeOffset+isL+isR+numOf
        //in nextPage bayd hatman bashe b hatere yaftane akharin page
        if (numOfKeys > 1) 
        {
            for (int i = 0; i < numOfKeys; i++) 
            {
                pagePointer = xBufChannel.getInt();//next page adrs
                pagePointerList.add(pagePointer);
                recLength = xBufChannel.getInt();
                //strLength=xBufChannel.getInt();
                curCId=xBufChannel.getInt();
                curId = new byte[recLength-Settings.sizeOfInt];
                //curDId=new DeweyID();
                xBufChannel.get(curId, 0, recLength-Settings.sizeOfInt);
                //curDId.setDeweyId(curId);
                curDId=new DeweyID(curId);
                //curQnameSeq=new byte[strLength];
                //xBufChannel.get(curQnameSeq,0,strLength);
                //curQnameStr=new String(curQnameSeq);
                
                if(i==numOfKeys-1)
                {
                    lastMaxKey.maxCID=curCId;
                    lastMaxKey.maxDeweyID=curDId;
                    //lastMaxQname=curQnameStr;
                }
//dar vaghe bayd vaghti SPLIT mikonim,chon max key kuchekatar ast bayd chek konim k dar jaye dorosti darj shavad
//vali dar in setup hatman max key in node az max node ghabli bishtar ast pas moshkeli nis.
                if (pagePointer==childPointer) 
                {
                    //yani b jaye current record darim newMaxkey ra varede list mikonim(khod b khod cur hazf mishe)
                    CIDList.add(newMaxKey.maxCID);//DO NOT add curCID
                    dIDList.add(newMaxKey.maxDeweyID);
                    //qNameList.add(newMaxQname);
                    prevMaxLength=recLength-Settings.sizeOfInt; //number of deweyId divs+chars (may change)  
                }
                else
                {
                    CIDList.add(curCId);
                    dIDList.add(curDId);
                    //qNameList.add(curQnameStr);
                }
                
            }
            this.MaxKey.maxCID=CIDList.get(CIDList.size()-1);
            this.MaxKey.maxDeweyID=dIDList.get(dIDList.size()-1);
            //this.MaxQname=qNameList.get(qNameList.size()-1);
            //*************
            //???vase chie?: 
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
                xBufChannel.position(Settings.pageSize - 1 - (numOfKeys)* Settings.sizeOfShort);//az entehaye page shoru mikonim b khundan
                for (int i = 0; i < (numOfKeys); i++) 
                {
                    elementPositionArray[i] = xBufChannel.getShort();
                }
                midPoint = computeMidPoint(elementPositionArray);
                rootPointer=this.splitInternal(midPoint,CIDList,dIDList,pagePointerList,rootPointer,parents);
                splitted=true;
            }
            ///////////////////////////////////////////////////////////////////////
            BTreeNode parentNode;
            int parentPointer;
            if(!splitted && !parents.empty() 
              && !lastMaxKey.maxDeweyID.equals(this.MaxKey.maxDeweyID) && lastMaxKey.maxDeweyID.notGreaterThan(this.MaxKey.maxDeweyID))
            {                      
                parentPointer=(int)parents.pop();
                parentNode=new BTreeNode(parentPointer,indexName);
                rootPointer=parentNode.updateInternalNode(this.MaxKey,rootPointer,this.indexPage,parents);
                //push(parentPointer);
                parentNode.closeBTreeNode();
            }
            //pagePointer = xBufChannel.getInt();//right most pointer NADARIM
            // pagePointerList.add(pagePointer);
           
           //inja chon rewrite darim bayad dar noghteye data start bashim,yani bad az mahale num of keys
           //positions may change:
           xBufChannel.position(Settings.dataStartPos);
           elementPositionArray=new short[numOfKeys]; 
           //reset freeoffset
           freeOffset=Settings.dataStartPos;
           for (int i=0;i<numOfKeys;i++)
           {
              //length=dIDList.get(i).getDeweyId().recLength;
              temp1 = dIDList.get(i).getDeweyId();                      
              recLength = temp1.length+Settings.sizeOfInt;
              //temp2=qNameList.get(i).getBytes();
              //strLength=temp2.length;
              elementPositionArray[i]=(short)xBufChannel.position();
              xBufChannel.putInt(pagePointerList.get(i));
              xBufChannel.putInt(recLength);
             // xBufChannel.putInt(strLength);
              xBufChannel.putInt(CIDList.get(i));
              xBufChannel.put(temp1,0,temp1.length);
             // xBufChannel.put(temp2,0,strLength);
              freeOffset+=2*Settings.sizeOfInt+recLength;
           }
           
           //for putting zero(erasing the rest of page){may new freeOffset be smaller than last one! }
           xBufChannel.erase(xBufChannel.position(),numOfKeys);
                //******************
        xBufChannel.position(Settings.pageSize -1-numOfKeys * Settings.sizeOfShort);//az tahe page shoru mikonim b khundan
        for(int i=0;i<numOfKeys;i++) //loop for setting elem positions at the end of page
        {             
             xBufChannel.putShort(elementPositionArray[i]);
        }
        
        }
        //********************************************************
        else 
        {
            keyLength = newMaxKey.maxDeweyID.getDeweyId().length+Settings.sizeOfInt;//key DivNumber
            //qLength=newMaxQname.length();
            xBufChannel.putInt(childPointer);
            short firstElementPosition;
            firstElementPosition=(short)xBufChannel.position();
            xBufChannel.putInt(keyLength);
            //xBufChannel.putInt(qLength);
            xBufChannel.putInt(newMaxKey.maxCID);
            temp1 = newMaxKey.maxDeweyID.getDeweyId();
            xBufChannel.put(temp1, 0,temp1.length);//prevMaxKey gets deleted automatically
            //temp2=newMaxQname.getBytes();
            //xBufChannel.put(temp2,0,qLength);
            xBufChannel.position(Settings.pageSize - 1 - numOfKeys * Settings.sizeOfShort);//az tahe page shoru mikonim b khundan
            xBufChannel.putShort(firstElementPosition);            
            freeOffset=2*(Settings.sizeOfInt)+keyLength;            
        }
        xBufChannel.position(Settings.freeOffsetPos);
        xBufChannel.putInt(freeOffset);
        //bufMngr.unpinBuffer(xBufChannel,true);
//        closeBTreeNode();
        return rootPointer;
    }
   /** public int split(int[] midPoint, ArrayList<DeweyID> deweyIDList,ArrayList pagePointerList,DeweyID key,int rootPointer,Stack parents) throws IOException
    {
        
        BTreeNode twinNode;
        BTreeNode rootNode=null;
        int parentPointer=-1;
        BTreeNode parentNode=null;
        int twinPageNumber;
        DeweyID prevMaxKey=null;
        boolean rootRenewed=false;
        boolean maxKeyChanged=false;
               
        if(this.isRoot==1)
         { 
           rootRenewed=true;
          //inja creat static ???
           rootNode=new BTreeNode(indexName,(byte)1,(byte)0, 0);//just built root has 2 keys
           this.indexPage=rootPointer;//lazeme?
           rootPointer=rootNode.createBNode();//bayad dar GMD neveshte shavad****
           //tu in noghte nextPage this taghir mikone,man chi kar konam?dobare az file bkhunam?
           //this.parentPointer=rootPointer;
           this.isRoot=0;
           xBufChannel.position(Settings.isRootPos);
           xBufChannel.put(isRoot);//chon this ba unpin write mishe.
           if(this.IsLeaf())
               twinNode=new BTreeNode(indexName,(byte)0,(byte)1, -1);//numOfKeys is not valid yet
               //isRoot , isLeaf dar xbuf neveshte nashode and.:(dar setMD set mishavad.
           else
               twinNode=new BTreeNode(indexName,(byte)0,(byte)0, -1);//numOfKeys is not valid yet
         }           
         else 
         {
             rootRenewed=false;
             //load parent node:
             parentPointer=(int)parents.pop();
             parentNode=new BTreeNode(parentPointer,indexName);//shayd bhetare yek tabe load bnvisim.
             parents.push(parentPointer);
             if(this.IsLeaf())
                twinNode=new BTreeNode(indexName,(byte)0,(byte)1, -1);//numOfKeys is not valid yet
             else
                 twinNode=new BTreeNode(indexName,(byte)0,(byte)0, -1);//numOfKeys is not valid yet

         }
                
        //InternalBuffer twin = bufMngr.getPage(twinPageNumber);
        int recLength;
        byte[] temp1;
       ////////////////////////////////////////////////////////////////////
       this.freeOffset=Settings.dataStartPos;//initial value
       twinNode.freeOffset=Settings.dataStartPos; //initial value
       
       xBufChannel.clear();//vali engAR CLEAR NEMISHAVAD CHERA???
       xBufChannel.position(Settings.numOfKeysPos);
        // koja meta data ghabli az ru in pak mishe? 
        if (deweyIDList.indexOf(key) < midPoint[1]) 
        {
            
            this.numOfKeys=midPoint[1] + 1;
            xBufChannel.putInt(midPoint[1] + 1); //numOfKeys=i+1
            //for (int i = 0; i < midPoint[1] + 1; i++)
            //{
                 //if(i==midPoint[1])
                // {
                    if(this.MaxKey!=deweyIDList.get(midPoint[1]))
                    {   
                        prevMaxKey=this.MaxKey;
                        this.MaxKey=deweyIDList.get(midPoint[1]);
                        maxKeyChanged=true;
                    }
                 //}  
                //length=deweyIDList.get(i).getDeweyId().recLength;
                //temp = deweyIDList.get(i).getDeweyId();
                //length = temp1.recLength;
//                xBufChannel.putInt(Settings.nullPagePointer);
//                xBufChannel.putInt(recLength);
//                InternalBuffer.put(temp1, 0, recLength);
//                freeOffset+=2*Settings.sizeOfInt+recLength;
            
            //koja baghie metadata twin neveshte mishe?
            twinNode.numOfKeys=deweyIDList.size() - (midPoint[1] + 1);
            twinNode.xBufChannel.position(Settings.numOfKeysPos);
            twinNode.xBufChannel.putInt(deweyIDList.size() - (midPoint[1] + 1)); //twin's Num of keys

            for (int i = midPoint[1] + 1; i < deweyIDList.size(); i++) 
            {
                 if(i==deweyIDList.size()-1)
                    twinNode.MaxKey=deweyIDList.get(i);
                //length=deweyIDList.get(i).getDeweyId().recLength;
                temp1 = deweyIDList.get(i).getDeweyId();
                recLength = temp1.recLength;
                twinNode.xBufChannel.putInt((int)pagePointerList.get(i));
                twinNode.xBufChannel.putInt(recLength);
                twinNode.xBufChannel.put(temp1, 0, recLength);
                twinNode.freeOffset+=2*Settings.sizeOfInt+recLength;
            }
        } 
        else 
        {
            this.numOfKeys=midPoint[1];
            xBufChannel.putInt(midPoint[1]); //numOfKeys=i
        //Acualad jabeja shod.
//            for (int i = 0; i < midPoint[1]; i++) 
//            {
//                if(i==midPoint[1]-1)
//                {
//     in kar dar khode insert anjam mishe:
                    if(this.MaxKey!=deweyIDList.get(midPoint[1]-1))
                    {   
                        prevMaxKey=this.MaxKey;
                        this.MaxKey=deweyIDList.get(midPoint[1]-1);
                        maxKeyChanged=true;
                    }
//                 }  
//            //length=deweyIDList.get(i).getDeweyId().recLength;
//            temp1 = deweyIDList.get(i).getDeweyId();
//            recLength = temp1.recLength;
//            xBufChannel.putInt(Settings.nullPagePointer);
//            xBufChannel.putInt(recLength);
//            InternalBuffer.put(temp1, 0, recLength);
//            this.freeOffset+=2*Settings.sizeOfInt+recLength;
//            }
            twinNode.numOfKeys=deweyIDList.size() - midPoint[1];
            twinNode.xBufChannel.position(Settings.numOfKeysPos);
            twinNode.xBufChannel.putInt(twinNode.numOfKeys); //twin Num of keys

            for (int i = midPoint[1]; i < deweyIDList.size(); i++) 
            {
                 if(i==deweyIDList.size()-1)//-1?********
                    twinNode.MaxKey=deweyIDList.get(i);
            //length=deweyIDList.get(i).getDeweyId().recLength;
               temp1 = deweyIDList.get(i).getDeweyId();
               recLength = temp1.recLength;
               twinNode.xBufChannel.putInt((int)pagePointerList.get(i));
               twinNode.xBufChannel.putInt(recLength);
               twinNode.xBufChannel.put(temp1, 0, recLength);
               twinNode.freeOffset+=2*Settings.sizeOfInt+recLength;
               
            }
        }
        ////////////////////////////////////////////////////////////////
//         this.xBufChannel.position(Settings.freeOffsetPos);
//         this.xBufChannel.putInt(this.freeOffset);
             twinNode.xBufChannel.position(Settings.freeOffsetPos);
         twinNode.xBufChannel.putInt(twinNode.freeOffset);         
         twinNode.indexPage = twinNode.writeBNode();
         
         if(rootRenewed==true)
         {
             rootNode.loadBTreeNode(rootPointer, indexName);
             rootPointer=rootNode.insertIntoInternalNode(this.MaxKey,rootPointer,this.indexPage,parents);//in chie?az OSTAD
             rootPointer=rootNode.insertIntoInternalNode(twinNode.MaxKey,rootPointer,twinNode.indexPage,parents);
             rootNode.updateBNode(rootPointer);//???che lozumi dare?
         }
         else 
         {            
//             if(maxKeyChanged)
//                 parentNode.updateInternalNode(prevMaxKey, MaxKey, rootPointer, this.indexPage, parents);
             //parentNode.update(this.max)
             parentPointer=(int)parents.pop();
             rootPointer=parentNode.updateInternalNode(this.MaxKey, rootPointer,this.indexPage, parents);
             rootPointer=parentNode.insertIntoInternalNode(twinNode.MaxKey,rootPointer,twinNode.indexPage,parents);
             parents.push(parentPointer);
         }
         //this.updateBNode(indexPage);//chon hanuzdorost nist,pP ra doorso nemikone.
         return rootPointer;
    }*/
    public int splitLeaf(int[] midPoint,ArrayList<Integer> CIDList,ArrayList<DeweyID> deweyIDList,ArrayList<String> qNameList,int CID,DeweyID deweyID,String qName,int rootPointer,Stack parents) throws IOException, DBException
    {
        BTreeNode twinNode;
        BTreeNode rootNode=null;
        int parentPointer=-1;
        BTreeNode parentNode=null;
        ElementMaxKey prevMaxKey=new ElementMaxKey();
        String prevMaxQname;
        boolean rootRenewed=false;
        boolean maxKeyChanged=false;
               
        if(this.isRoot==1)
         { 
           rootRenewed=true;
          //inja creat static ???
           rootNode=new BTreeNode(indexName,(byte)1,(byte)0, 0);//just built root has 2 keys
           //this.indexPage=rootPointer;//lazeme?!!!!
           rootPointer=rootNode.createBNode();//bayad dar GMD neveshte shavad****
           //tu in noghte nextPage this taghir mikone,man chi kar konam?dobare az file bkhunam?
           //this.parentPointer=rootPointer;
           this.isRoot=0;
           xBufChannel.position(Settings.isRootPos);
           xBufChannel.put(isRoot);//chon this ba unpin write mishe.
           if(this.IsLeaf())
               twinNode=new BTreeNode(indexName,(byte)0,(byte)1,-1);//numOfKeys is not valid yet
               //isRoot , isLeaf dar xbuf neveshte nashode and.:(dar setMD set mishavad.
           else
               twinNode=new BTreeNode(indexName,(byte)0,(byte)0,-1);//numOfKeys is not valid yet
         }           
         else 
         {
             rootRenewed=false;
             //load parent node:
             parentPointer=(int)parents.pop();
             parentNode=new BTreeNode(parentPointer,indexName);//shayd bhetare yek tabe load bnvisim.
             parents.push(parentPointer);             
             if(this.IsLeaf())
                twinNode=new BTreeNode(indexName,(byte)0,(byte)1, -1);//numOfKeys is not valid yet
             else
                 twinNode=new BTreeNode(indexName,(byte)0,(byte)0, -1);//numOfKeys is not valid yet

         }
                
        //InternalBuffer twin = bufMngr.getPage(twinPageNumber);
        int recLength;
        int strLength;
        byte[] temp1;
        byte[] temp2;
        short[] twinElemPositions;
       ////////////////////////////////////////////////////////////////////
       this.freeOffset=Settings.dataStartPos;//initial value
       twinNode.freeOffset=Settings.dataStartPos; //initial value
       
       xBufChannel.clear();
       xBufChannel.position(Settings.numOfKeysPos);
        //CIDList: c1,c2,...,cid,...c[m1],...
       if (deweyIDList.indexOf(deweyID) < midPoint[1]) 
        {
            this.numOfKeys=midPoint[1] + 1;
            xBufChannel.putInt(midPoint[1] + 1); //numOfKeys=i+1
//                if(this.MaxKey.maxDeweyID!=deweyIDList.get(midPoint[1]))
//                {   
            prevMaxKey.maxCID=this.MaxKey.maxCID;
            prevMaxKey.maxDeweyID=this.MaxKey.maxDeweyID;
            prevMaxQname=this.MaxQname;                        
            this.MaxKey.maxCID=CIDList.get(midPoint[1]);
            this.MaxKey.maxDeweyID=deweyIDList.get(midPoint[1]);
            this.MaxQname=qNameList.get(midPoint[1]);
            maxKeyChanged=true;
//                }
            //koja baghie metadata twin neveshte mishe?
            twinNode.numOfKeys=deweyIDList.size() - (midPoint[1] + 1);
            twinNode.xBufChannel.position(Settings.numOfKeysPos);
            twinNode.xBufChannel.putInt(twinNode.numOfKeys); //twin's Num of keys
            twinElemPositions=new short[twinNode.numOfKeys];
            for (int i = midPoint[1] + 1; i < deweyIDList.size(); i++) 
            {
                 if(i==deweyIDList.size()-1)
                 {   
                     twinNode.MaxKey.maxCID=CIDList.get(i);
                     twinNode.MaxKey.maxDeweyID=deweyIDList.get(i); 
                     twinNode.MaxQname=qNameList.get(i);
                 }
                //length=dIDList.get(i).getDeweyId().recLength;
                temp1=deweyIDList.get(i).getDeweyId();
                //twinNode.xBufChannel.putInt((int)pagePointerList.get(i));
                recLength = temp1.length+Settings.sizeOfInt;
                temp2=qNameList.get(i).getBytes();
                strLength=temp2.length;
                twinElemPositions[i-midPoint[1]-1]=(short)twinNode.xBufChannel.position();
                twinNode.xBufChannel.putInt(recLength);
                twinNode.xBufChannel.putInt(strLength);
                twinNode.xBufChannel.putInt((int)CIDList.get(i));                
                twinNode.xBufChannel.put(temp1,0,temp1.length);
                twinNode.xBufChannel.put(temp2,0,strLength);
                twinNode.freeOffset+=2*Settings.sizeOfInt+recLength+strLength;
            }
            twinNode.xBufChannel.erase(twinNode.xBufChannel.position(),twinNode.numOfKeys);        
            twinNode.xBufChannel.position(Settings.pageSize - 1 - twinNode.numOfKeys * Settings.sizeOfShort);//az tahe page shoru mikonim b khundan
            for(int i=0;i<twinNode.numOfKeys;i++) //loop for setting elem positions at the end of page
            {             
                 twinNode.xBufChannel.putShort(twinElemPositions[i]);
            }
        } 
        else 
        {
            this.numOfKeys=midPoint[1];
            xBufChannel.putInt(midPoint[1]); //numOfKeys=i
//            if(this.MaxKey.maxDeweyID!=deweyIDList.get(midPoint[1]-1))
//            {   
            prevMaxKey.maxCID=this.MaxKey.maxCID;
            prevMaxKey.maxDeweyID=this.MaxKey.maxDeweyID;
            prevMaxQname=this.MaxQname;
            this.MaxKey.maxCID=CIDList.get(midPoint[1]-1);
            this.MaxKey.maxDeweyID=deweyIDList.get(midPoint[1]-1);
            this.MaxQname=qNameList.get(midPoint[1]-1);
            maxKeyChanged=true;
//            }

            twinNode.numOfKeys=deweyIDList.size()- midPoint[1];
            twinNode.xBufChannel.position(Settings.numOfKeysPos);
            twinNode.xBufChannel.putInt(twinNode.numOfKeys); //twin Num of keys
            twinElemPositions=new short[twinNode.numOfKeys];
            for (int i = midPoint[1]; i < deweyIDList.size(); i++) 
            {
                 if(i==deweyIDList.size()-1)//-1?********
                 {  
                     twinNode.MaxKey.maxCID=CIDList.get(i);
                     twinNode.MaxKey.maxDeweyID=deweyIDList.get(i);
                     twinNode.MaxQname=qNameList.get(i);
                 }
               temp1 = deweyIDList.get(i).getDeweyId();
               recLength = temp1.length+Settings.sizeOfInt;
               //twinNode.xBufChannel.putInt((int)pagePointerList.get(i));
               temp2=qNameList.get(i).getBytes();
               strLength=temp2.length;
               twinElemPositions[i-midPoint[1]]=(short)twinNode.xBufChannel.position();
               twinNode.xBufChannel.putInt(recLength);
               twinNode.xBufChannel.putInt(strLength);
               twinNode.xBufChannel.putInt(CIDList.get(i));
               twinNode.xBufChannel.put(temp1,0,temp1.length);               
               twinNode.xBufChannel.put(temp2,0,strLength);               
               twinNode.freeOffset+=2*Settings.sizeOfInt+recLength+strLength;
               
            }
            twinNode.xBufChannel.erase(twinNode.xBufChannel.position(),twinNode.numOfKeys);        
            twinNode.xBufChannel.position(Settings.pageSize - 1 - twinNode.numOfKeys * Settings.sizeOfShort);//az tahe page shoru mikonim b khundan
            for(int i=0;i<twinNode.numOfKeys;i++) //loop for setting elem positions at the end of page
            {             
                 twinNode.xBufChannel.putShort(twinElemPositions[i]);
            }
        }
        //in this point we get twin's page no,so we should assign it to this.next page:
         this.xBufChannel.position(Settings.nextPagePos);
         int nextPage=this.xBufChannel.getInt();
         twinNode.xBufChannel.position(Settings.nextPagePos);
         if(nextPage>0)//this node is already pointing to another page
         {  
            twinNode.xBufChannel.putInt(nextPage);
            logManager.LogManager.log(5,"Special Case in BTree Index:**********at page: "+this.xBufChannel.getPageNumber());
         }
         if(nextPage==Settings.invalidValue)
         {
             twinNode.xBufChannel.putInt(Settings.invalidValue);        
         } 
         this.xBufChannel.position(Settings.nextPagePos);
         this.xBufChannel.putInt(twinNode.indexPage);      
       
         twinNode.xBufChannel.position(Settings.freeOffsetPos);
         twinNode.xBufChannel.putInt(twinNode.freeOffset);
         //NO NEED:cause it already has pageNumber;
         twinNode.indexPage=twinNode.writeBNode();  
         
         logManager.LogManager.log(6,"twinNode.indexPage: "+ twinNode.indexPage);
//baraye parent v newRoot hargez nextP set nemikonim.
         
         if(rootRenewed==true)
         {
             //rootNode.loadBTreeNode(rootPointer, indexName);Because we already have rootNOde in memory
             rootPointer=rootNode.insertIntoInternal(this.MaxKey.maxDeweyID,this.MaxKey.maxCID,rootPointer,this.indexPage,parents);//in chie?az OSTAD
             rootPointer=rootNode.insertIntoInternal(twinNode.MaxKey.maxDeweyID,twinNode.MaxKey.maxCID,rootPointer,twinNode.indexPage,parents);
             rootNode.updateBNode(rootPointer);//vali unpin ham haminkaro mikone!MUST BE REPLACED
             rootNode.closeBTreeNode();
         }
         else 
         {            
             parentPointer=(int)parents.pop();
             rootPointer=parentNode.insUpdater(this.MaxKey, this.indexPage, twinNode.MaxKey, twinNode.indexPage, rootPointer, parents);
//             rootPointer=parentNode.updateInternalNode(this.MaxKey, this.MaxQname,rootPointer,this.indexPage, parents);
//             rootPointer=parentNode.insertIntoInternal(twinNode.MaxKey.maxDeweyID,twinNode.MaxKey.maxCID,rootPointer,twinNode.indexPage,parents);
             parents.push(parentPointer);
             parentNode.closeBTreeNode();
         }
         return rootPointer;
    }
    //Used for Insert:
    public int splitInternal(int[] midPoint,ArrayList<Integer> CIDList,ArrayList<DeweyID> deweyIDList,ArrayList pagePointerList,int CID,DeweyID deweyID,int rootPointer,Stack parents) throws IOException, DBException
    {
        BTreeNode twinNode;
        BTreeNode rootNode=null;
        int parentPointer=-1;
        BTreeNode parentNode=null;
        ElementMaxKey prevMaxKey=new ElementMaxKey();
        //String prevMaxQname;
        boolean rootRenewed=false;
        boolean maxKeyChanged=false;
               
        if(this.isRoot==1)
         { 
           rootRenewed=true;
          //creat new ROOT:
           rootNode=new BTreeNode(indexName,(byte)1,(byte)0, 0);//just built root has 2 keys
           rootPointer=rootNode.createBNode();//bayad dar GMD neveshte shavad****           
           //this.parentPointer=rootPointer;
           this.isRoot=0;
           xBufChannel.position(Settings.isRootPos);
           xBufChannel.put(isRoot);//chon this ba unpin write mishe.
           //SHOULD NOT BE CHECKED:REDUNDANT:
           if(this.IsLeaf())
               twinNode=new BTreeNode(indexName,(byte)0,(byte)1,-1);//numOfKeys is not valid yet
               //isRoot , isLeaf dar xbuf neveshte nashode and.:(dar setMD set mishavad.
           else
               twinNode=new BTreeNode(indexName,(byte)0,(byte)0,-1);//numOfKeys is not valid yet
         }           
         else 
         {
             rootRenewed=false;
             //load parent node:
             parentPointer=(int)parents.pop();
             parentNode=new BTreeNode(parentPointer,indexName);
             parents.push(parentPointer);
             if(this.IsLeaf())
                twinNode=new BTreeNode(indexName,(byte)0,(byte)1, -1);//numOfKeys is not valid yet
             else
                 twinNode=new BTreeNode(indexName,(byte)0,(byte)0, -1);//numOfKeys is not valid yet

         }
                
        //InternalBuffer twin = bufMngr.getPage(twinPageNumber);
        int recLength;
        //int strLength;
        byte[] temp1;
       // byte[] temp2;
        short[] twinElemPositions;
       ////////////////////////////////////////////////////////////////////
       this.freeOffset=Settings.dataStartPos;//initial value
       twinNode.freeOffset=Settings.dataStartPos; //initial value       
       xBufChannel.clear();
       xBufChannel.position(Settings.numOfKeysPos);
        // koja meta data ghabli az ru in pak mishe? 
        //CIDList: c1,c2,...,cid,...c[m1],...
       if (deweyIDList.indexOf(deweyID) < midPoint[1]) 
        {
            this.numOfKeys=midPoint[1] + 1;
            xBufChannel.putInt(midPoint[1] + 1); //numOfKeys=i+1
            
//                    if(this.MaxKey.maxDeweyID!=deweyIDList.get(midPoint[1]))
//                    {   
            prevMaxKey.maxCID=this.MaxKey.maxCID;
            prevMaxKey.maxDeweyID=this.MaxKey.maxDeweyID;
            //prevMaxQname=this.MaxQname;                        
            this.MaxKey.maxCID=CIDList.get(midPoint[1]);
            this.MaxKey.maxDeweyID=deweyIDList.get(midPoint[1]);
            //this.MaxQname=qNameList.get(midPoint[1]);
            maxKeyChanged=true;
//                    }
                            
            //koja baghie metadata twin neveshte mishe?
            twinNode.numOfKeys=deweyIDList.size() - (midPoint[1] + 1);
            twinNode.xBufChannel.position(Settings.numOfKeysPos);
            twinNode.xBufChannel.putInt(deweyIDList.size() - (midPoint[1] + 1)); //twin's Num of keys
            twinElemPositions=new short[twinNode.numOfKeys];
            for (int i = midPoint[1] + 1; i < deweyIDList.size(); i++) 
            {
                 if(i==deweyIDList.size()-1)
                 {   
                     twinNode.MaxKey.maxCID=CIDList.get(i);
                     twinNode.MaxKey.maxDeweyID=deweyIDList.get(i); 
//                   twinNode.MaxQname=qNameList.get(i);
                 }
                //length=dIDList.get(i).getDeweyId().recLength;
                temp1=deweyIDList.get(i).getDeweyId();
                twinElemPositions[i-midPoint[1]-1]=(short)twinNode.xBufChannel.position();
                twinNode.xBufChannel.putInt((int)pagePointerList.get(i));
                recLength = temp1.length+Settings.sizeOfInt;
//                temp2=qNameList.get(i).getBytes();
                //strLength=temp2.length;
                twinNode.xBufChannel.putInt(recLength);
                //twinNode.xBufChannel.putInt(strLength);
                twinNode.xBufChannel.putInt(CIDList.get(i));                
                twinNode.xBufChannel.put(temp1,0,temp1.length);
                //twinNode.xBufChannel.put(temp2,0,strLength);
                twinNode.freeOffset+=2*Settings.sizeOfInt+recLength;
            }
            twinNode.xBufChannel.erase(twinNode.xBufChannel.position(),twinNode.numOfKeys);        
            twinNode.xBufChannel.position(Settings.pageSize - 1 - twinNode.numOfKeys * Settings.sizeOfShort);//az tahe page shoru mikonim b khundan
            for(int i=0;i<twinNode.numOfKeys;i++) //loop for setting elem positions at the end of page
            {             
                 twinNode.xBufChannel.putShort(twinElemPositions[i]);
            }
        } 
        else 
        {
            this.numOfKeys=midPoint[1];
            xBufChannel.putInt(midPoint[1]); //numOfKeys=i
//            if(this.MaxKey.maxDeweyID!=deweyIDList.get(midPoint[1]-1))
//            {   
            prevMaxKey.maxCID=this.MaxKey.maxCID;
            prevMaxKey.maxDeweyID=this.MaxKey.maxDeweyID;
            //prevMaxQname=this.MaxQname;
            this.MaxKey.maxCID=CIDList.get(midPoint[1]-1);
            this.MaxKey.maxDeweyID=deweyIDList.get(midPoint[1]-1);
            //this.MaxQname=qNameList.get(midPoint[1]-1);
            maxKeyChanged=true;
//            }

            twinNode.numOfKeys=deweyIDList.size()- midPoint[1];
            twinNode.xBufChannel.position(Settings.numOfKeysPos);
            twinNode.xBufChannel.putInt(twinNode.numOfKeys); //twin Num of keys
            twinElemPositions=new short[twinNode.numOfKeys];
            for (int i = midPoint[1]; i < deweyIDList.size(); i++) 
            {
                 if(i==deweyIDList.size()-1)//-1?********
                 {  
                     twinNode.MaxKey.maxCID=CIDList.get(i);
                     twinNode.MaxKey.maxDeweyID=deweyIDList.get(i);
                    // twinNode.MaxQname=qNameList.get(i);
                 }
               temp1 = deweyIDList.get(i).getDeweyId();
               recLength = temp1.length+Settings.sizeOfInt;
               twinElemPositions[i-midPoint[1]]=(short)twinNode.xBufChannel.position();
               twinNode.xBufChannel.putInt((int)pagePointerList.get(i));
               //temp2=qNameList.get(i).getBytes();
               //strLength=temp2.length;
               twinNode.xBufChannel.putInt(recLength);
              // twinNode.xBufChannel.putInt(strLength);
               twinNode.xBufChannel.putInt(CIDList.get(i));
               twinNode.xBufChannel.put(temp1,0,temp1.length);
              // twinNode.xBufChannel.put(temp2,0,strLength);
               twinNode.freeOffset+=2*Settings.sizeOfInt+recLength;               
            }
            twinNode.xBufChannel.erase(twinNode.xBufChannel.position(),twinNode.numOfKeys);        
            twinNode.xBufChannel.position(Settings.pageSize - 1 - twinNode.numOfKeys * Settings.sizeOfShort);//az tahe page shoru mikonim b khundan
            for(int i=0;i<twinNode.numOfKeys;i++) //loop for setting elem positions at the end of page
            {             
                 twinNode.xBufChannel.putShort(twinElemPositions[i]);
            }
        }
       
         twinNode.xBufChannel.position(Settings.freeOffsetPos);
         twinNode.xBufChannel.putInt(twinNode.freeOffset);         
         twinNode.indexPage=twinNode.writeBNode();
 
         if(rootRenewed==true)
         {
             //rootNode.loadBTreeNode(rootPointer, indexName);We have built it in memory
             rootPointer=rootNode.insertIntoInternal(this.MaxKey.maxDeweyID,this.MaxKey.maxCID,rootPointer,this.indexPage,parents);//in chie?az OSTAD
             rootPointer=rootNode.insertIntoInternal(twinNode.MaxKey.maxDeweyID,twinNode.MaxKey.maxCID,rootPointer,twinNode.indexPage,parents);
             rootNode.updateBNode(rootPointer);//unpin: MUST BE REPLACED
             rootNode.closeBTreeNode();
         }
         else 
         {            
             parentPointer=(int)parents.pop();
             rootPointer=parentNode.insUpdater(this.MaxKey, this.indexPage, twinNode.MaxKey, twinNode.indexPage, rootPointer, parents);
//           rootPointer=parentNode.updateInternalNode(this.MaxKey,rootPointer,this.indexPage, parents);
//           rootPointer=parentNode.insertIntoInternal(twinNode.MaxKey.maxDeweyID,twinNode.MaxKey.maxCID,rootPointer,twinNode.indexPage,parents);
             parents.push(parentPointer);
             parentNode.closeBTreeNode();
             
         }
         return rootPointer;
    }
    //Used for Update:
 public int splitInternal(int[] midPoint,ArrayList<Integer> CIDList,ArrayList<DeweyID> deweyIDList,ArrayList pagePointerList,int rootPointer,Stack parents) throws IOException, DBException
    {
        BTreeNode twinNode;
        BTreeNode rootNode=null;
        int parentPointer=-1;
        BTreeNode parentNode=null;
        ElementMaxKey prevMaxKey=new ElementMaxKey();
        //String prevMaxQname;
        boolean rootRenewed=false;
        boolean maxKeyChanged=false;
               
        if(this.isRoot==1)
         { 
           rootRenewed=true;
          //creat new ROOT:
           rootNode=new BTreeNode(indexName,(byte)1,(byte)0, 0);//just built root has 2 keys
          // this.indexPage=rootPointer;//lazeme?INO PAK KARDAM DOROS SHOD!!!!
           rootPointer=rootNode.createBNode();//bayad dar GMD neveshte shavad****
           //tu in noghte nextPage this taghir mikone,man chi kar konam?dobare az file bkhunam?
           //this.parentPointer=rootPointer;
           this.isRoot=0;
           if(this.indexPage==469356)
               System.out.println("new ROOT: "+ rootPointer);
           xBufChannel.position(Settings.isRootPos);
           xBufChannel.put(isRoot);//chon this ba unpin write mishe.
           //SHOULD NOT BE CHECKED:REDUNDANT:
//           if(this.IsLeaf())
//               twinNode=new BTreeNode(indexName,(byte)0,(byte)1,-1);//numOfKeys is not valid yet
//               //isRoot , isLeaf dar xbuf neveshte nashode and.:(dar setMD set mishavad.
//           else
           twinNode=new BTreeNode(indexName,(byte)0,(byte)0,-1);//numOfKeys is not valid yet
           
         }           
         else 
         {
             rootRenewed=false;
             //load parent node:
             try{
             parentPointer=(int)parents.pop();
             }
             catch(Exception e)
             {
                 System.out.println("Exception occured: " +this.indexPage);
             }
             parentNode=new BTreeNode(parentPointer,indexName);//loads parent
             parents.push(parentPointer);
             twinNode=new BTreeNode(indexName,(byte)0,(byte)0, -1);//numOfKeys is not valid yet

         }
                
        //InternalBuffer twin = bufMngr.getPage(twinPageNumber);
        int recLength;
        //int strLength;
        byte[] temp1;
        //byte[] temp2;
        short[] twinElemPositions;
       ////////////////////////////////////////////////////////////////////
       this.freeOffset=Settings.dataStartPos;//initial value
       twinNode.freeOffset=Settings.dataStartPos; //initial value
       
       xBufChannel.clear();//vali engAR CLEAR NEMISHAVAD CHERA???
       xBufChannel.position(Settings.numOfKeysPos);
        //       if (deweyIDList.indexOf(deweyID) < midPoint[1]) 
//        {
            this.numOfKeys=midPoint[1];
            xBufChannel.putInt(midPoint[1]); //numOfKeys=i+1
            
//            if(this.MaxKey.maxDeweyID!=deweyIDList.get(midPoint[1]-1))
//            {   
                prevMaxKey.maxCID=this.MaxKey.maxCID;
                prevMaxKey.maxDeweyID=this.MaxKey.maxDeweyID;
                //prevMaxQname=this.MaxQname;                        
                this.MaxKey.maxCID=CIDList.get(midPoint[1]);
                this.MaxKey.maxDeweyID=deweyIDList.get(midPoint[1]);
                //this.MaxQname=qNameList.get(midPoint[1]);
                maxKeyChanged=true;
//            }

            //koja baghie metadata twin neveshte mishe?
            twinNode.numOfKeys=deweyIDList.size() - midPoint[1];
            twinNode.xBufChannel.position(Settings.numOfKeysPos);
            twinNode.xBufChannel.putInt(twinNode.numOfKeys); //twin's Num of keys
            twinElemPositions=new short[twinNode.numOfKeys];
            for (int i = midPoint[1]; i < deweyIDList.size(); i++) 
            {
                 if(i==deweyIDList.size()-1)
                 {   
                     twinNode.MaxKey.maxCID=CIDList.get(i);
                     twinNode.MaxKey.maxDeweyID=deweyIDList.get(i); 
                     //twinNode.MaxQname=qNameList.get(i);
                 }
                //length=dIDList.get(i).getDeweyId().recLength;
                temp1=deweyIDList.get(i).getDeweyId();
                twinElemPositions[i-midPoint[1]]=(short)twinNode.xBufChannel.position();
                twinNode.xBufChannel.putInt((int)pagePointerList.get(i));
                recLength = temp1.length+Settings.sizeOfInt;
               // temp2=qNameList.get(i).getBytes();
                //strLength=temp2.length;
                twinNode.xBufChannel.putInt(recLength);
               // twinNode.xBufChannel.putInt(strLength);
                twinNode.xBufChannel.putInt(CIDList.get(i));                
                twinNode.xBufChannel.put(temp1,0,temp1.length);
                //twinNode.xBufChannel.put(temp2,0,strLength);
                twinNode.freeOffset+=2*Settings.sizeOfInt+recLength;
            }
            twinNode.xBufChannel.erase(twinNode.xBufChannel.position(),twinNode.numOfKeys);        
            twinNode.xBufChannel.position(Settings.pageSize - 1 - twinNode.numOfKeys * Settings.sizeOfShort);//az tahe page shoru mikonim b khundan
            for(int i=0;i<twinNode.numOfKeys;i++) //loop for setting elem positions at the end of page
            {             
                 twinNode.xBufChannel.putShort(twinElemPositions[i]);
            }
         twinNode.xBufChannel.position(Settings.freeOffsetPos);
         twinNode.xBufChannel.putInt(twinNode.freeOffset);         
         twinNode.indexPage=twinNode.writeBNode();
 
         if(rootRenewed==true)
         {
             //rootNode.loadBTreeNode(rootPointer, indexName);We have built it in memory
             rootPointer=rootNode.insertIntoInternal(this.MaxKey.maxDeweyID,this.MaxKey.maxCID,rootPointer,this.indexPage,parents);//in chie?az OSTAD
             rootPointer=rootNode.insertIntoInternal(twinNode.MaxKey.maxDeweyID,twinNode.MaxKey.maxCID,rootPointer,twinNode.indexPage,parents);
             rootNode.updateBNode(rootPointer);//unpin: MUST BE REPLACED
             rootNode.closeBTreeNode();
         }
         else 
         {            
             parentPointer=(int)parents.pop();
             rootPointer=parentNode.insUpdater(this.MaxKey, this.indexPage, twinNode.MaxKey, twinNode.indexPage, rootPointer, parents);
//             rootPointer=parentNode.updateInternalNode(this.MaxKey, this.MaxQname,rootPointer,this.indexPage, parents);
//             rootPointer=parentNode.insertIntoInternal(twinNode.MaxKey.maxDeweyID,twinNode.MaxKey.maxCID,rootPointer,twinNode.indexPage,parents);
             parents.push(parentPointer);
             parentNode.closeBTreeNode();
             
         }
         return rootPointer;
    }

     public int[] computeMidPoint(short[] elementPlaces) 
    {
        int[] midPoint = new int[2];
        midPoint[0] = Math.abs(elementPlaces[0] - Settings.pageSize/2); //midPoint value
        int minimum;
        //minimum = elementPlaces[0];
        for (int i = 0; i < elementPlaces.length; i++) 
        {
            
            elementPlaces[i] = (short)Math.abs(elementPlaces[i] - Settings.pageSize/2);
            midPoint[0] = Math.min(midPoint[0], elementPlaces[i]);
            if (elementPlaces[i] == midPoint[0]) {
                midPoint[1] = i;
            }
        }
        //assumes pageSize to be even
        
        midPoint[0] = midPoint[0] + Settings.pageSize/2;
        return midPoint; //makane shorue vasat tarin DID ra midahad
    }
    public int createBNode() throws IOException
    {
        //used for creating rootNode while creating index or splitting
        return logicFileMgr.creatRootPage(indexName, isRoot, isLeaf, numOfKeys, xBufChannel);
                //logicFileMgr.addPage(indexName,isRoot,isLeaf,numOfKeys);
        //must add some code to write page's data as well.
    } 
    public int writeBNode() throws IOException
    {
        //used for writing twinNode on disk after creation in memory
        return logicFileMgr.createTwinPage(indexName, freeOffset, isRoot, isLeaf, numOfKeys, xBufChannel);
                //logicFileMgr.addPage(indexName,freeOffset,isRoot,isLeaf,numOfKeys,this.xBufChannel);
        
    } 
    public void updateBNode(int pageNumber) throws IOException
    {
        xBufChannel.position(Settings.isRootPos);
        xBufChannel.put(isRoot);
        xBufChannel.put(isLeaf);
        xBufChannel.position(Settings.pageSize);
        xBufChannel.setPageNumber(pageNumber);
        xBufChannel.flip();
        logicFileMgr.updatePage(xBufChannel);
    }
    public XBufferChannel getBuf()
    {
        return this.xBufChannel;
    }
    public void closeBTreeNode()
    {
        bufMngr.unpinBuffer(this.xBufChannel);
    }
    
    public void prepare()
    {
        xBufChannel.position(Settings.dataStartPos);       
    }
    public OutputWrapper<BTreeNode,Record> next() throws IOException, DBException
    {
       //we assume that during "next"ing no more keys are added.(otherwise bound should be updated somehow)
        int recLength;
        int strLength;
        byte[] curId;
        byte[] curQnameSeq;
        String curQnameStr = null;
        int curCid=0;
        DeweyID curDId=null;
        BTreeNode sibling=null;
        Record result = null;
        //assumption: recPos is the correct start position of some record in page 
        //if(xBufChannel.position()<Settings.pageSize-numOfKeys*2)
        if(xBufChannel.position()<freeOffset && bound>0)
        {
                recLength=xBufChannel.getInt();//assumption:recLength=DeweyIdsize+4(Cid)
                strLength=xBufChannel.getInt();
                curCid=xBufChannel.getInt();
                curId = new byte[recLength-Settings.sizeOfInt];
                xBufChannel.get(curId,0,recLength-Settings.sizeOfInt);
                curDId=new DeweyID(curId);
                curDId.setCID(curCid);
                curQnameSeq=new byte[strLength];                
                xBufChannel.get(curQnameSeq,0,strLength);
                curQnameStr=new String(curQnameSeq);       
                bound--;
                result=new Record(curDId,curCid,curQnameStr);
                return new OutputWrapper(sibling,result);
        }
//        else if(bound==0)
//        {
//            //in vase halati k pag ha tamum 
//            return null;
//        }
        else
            {               
                   //goTOnextPAge;
                  xBufChannel.position(Settings.nextPagePos);
                  int nextPage=xBufChannel.getInt();
                  //Closing is done in BTreeIterator
                  //this.closeBTreeNode();
                  //bufMngr.unpinBuffer(xBufChannel, true);
                  if(nextPage>0)
                  {
                      sibling=new BTreeNode(nextPage,indexName);
                      return new OutputWrapper(sibling,result);
                  }
                  else if(nextPage==-1)
                  {
                      return null;//THIS is END OF FILE
                  }
            }
                                      
//        if(curDId!=null)
//        {                   
//            result=new Record(curDId,curCid,curQnameStr);
//            return new OutputWrapper(sibling,result);
//        }
       
        return null;
        
    }
}

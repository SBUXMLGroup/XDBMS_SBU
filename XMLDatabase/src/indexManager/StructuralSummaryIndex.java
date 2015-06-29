package indexManager;

import LogicalFileManager.LogicalFileManager;
import bufferManager.BufferManager;
import bufferManager.XBufferChannel;
import commonSettings.Settings;
//import commonSettings.summarySettings;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import indexManager.Iterators.StructuralSummaryIterator;
import xmlProcessor.DBServer.DBException;
//ostad goft k badan bayad kari koni k hamunaj k yek page ro mikhune un y page ro write kone k arraylist kheili bozorg nashe.
public class StructuralSummaryIndex 
{
    
    ArrayList<StructuralSummaryNode> nodeList;
    private int numOfKeys;
    private int freeOffset;
    int unTakenNodes;
    //minkeys=20
    private int indexPage;//is the root of ssIndx,its first page
    int workingPage;//is the page which is loaded into XBufferChannel,may differ from indxpage,when we do next...
    private XBufferChannel xBufferChannel;
    private LogicalFileManager logicFileMgr;
    private String indexName;
    private BufferManager bufMngr;
    
    public StructuralSummaryIndex(String indexName) throws IOException, DBException
    {
        //usage: creation of nonExisting index
        this.indexName=indexName;
        this.numOfKeys=0;
        nodeList=new ArrayList();
        logicFileMgr = LogicalFileManager.Instance;
        bufMngr = BufferManager.getInstance();
        xBufferChannel=bufMngr.getFreePage();
        this.indexPage=createSSIndexPage();
        this.workingPage=this.indexPage;
       // XBufferChannel=bufMngr.getPage(indexPage);//vasl bshe b buf mgr:getfreepage()
        //XBufferChannel.clear();   
        int keysAdded=0;
        freeOffset=Settings.dataStartPos;//initialize
        xBufferChannel.position(Settings.dataStartPos);
    }
 
//    public StructuralSummaryIndex(String indexName,int numOfKeys)
//    {
//        //this.qNameList=qNamelist; 
//        //this.nodeList=nodeList;
//        this.indexName=indexName;
//        this.numOfKeys=numOfKeys;
//        logicFileMgr = LogicalFileManager.Instance;
//        bufMngr = BufferManager.getInstance();
//        XBufferChannel=new XBufferChannel();//vasl bshe b buf mgr:getfreepage()
//        XBufferChannel.clear();        
//    }
    public StructuralSummaryIndex(int indexPage,String indexName) throws IOException, DBException
    {
        //usage: openning of existing SSIndex object.
        this.indexName=indexName;
        this.indexPage=indexPage; //always the same
        this.workingPage=indexPage;//may change
        logicFileMgr = LogicalFileManager.Instance;
        bufMngr = BufferManager.getInstance();
        xBufferChannel=new XBufferChannel();//new ham lazem nis //vasl bshe b buf mgr:getfreepage()
       // xBufferChannel.clear(); //LaZEM NIs
        xBufferChannel = bufMngr.getPage(indexPage); //may change
        xBufferChannel.position(Settings.freeOffsetPos);//5
        freeOffset = xBufferChannel.getInt(); //init
        xBufferChannel.position(Settings.numOfKeysPos);
        numOfKeys = xBufferChannel.getInt(); //init
        unTakenNodes=numOfKeys;
    }
    /** 
     * @param workingPage
     * @throws IOException 
     * renews page,freeOffset and numOfKeys
     */
    private void changeWorkingPageTo(XBufferChannel hlpr) throws IOException
    {
        xBufferChannel = hlpr; //may change
        freeOffset = Settings.dataStartPos; //init
        numOfKeys = 0; //init this is zero in this point
        logicFileMgr.creatSibling(hlpr);
//        xBufferChannel.position(Settings.freeOffsetPos);//5
//        xBufferChannel.putInt(freeOffset);
//        xBufferChannel.position(Settings.numOfKeysPos);
//        xBufferChannel.putInt(numOfKeys);
        unTakenNodes=numOfKeys;//???what is this for?
    }
    
    public int closeSSIndex()
    {
        System.out.println("SS First Page P: "+indexPage);
        return this.indexPage;
    }
    public String getIndexName()
    {
        return this.indexName;
    }
    private int createSSIndexPage() throws IOException
    {
        //we use pages as in BTreeType just we use 2bytes more(for isRoot n isLeaf are invalid here)
        return logicFileMgr.creatRootPage(indexName, (byte)Settings.invalidValue, (byte)Settings.invalidValue, 0,xBufferChannel);
                //logicFileMgr.addPage(indexName,(byte)Settings.invalidValue,(byte)Settings.invalidValue,numOfKeys);
    }
    
    public void open()//opens access to data for Iterator
    {
        xBufferChannel.position(Settings.dataStartPos);
    }
    public StructuralSummaryNode next() throws IOException, DBException//for Iterator
    {
        //read record from file
        //transform into ssNode
        int CID;
        int qNameLen;
        String qName;
        byte[] qNameSeq;
        int parentCID;
        StructuralSummaryNode currentNode=new StructuralSummaryNode();
        int nextPage;//needed in case that data is written in more than one page
        while(true)
        {
            //if(unTakenNodes!=0 &&XBufferChannel.position()+2*Settings.sizeOfInt<=Settings.pageSize)
            //agar dorost bkhunim har bar position sare yek record mi iste:
            if(unTakenNodes!=0 && xBufferChannel.position()<freeOffset)
            {
                //bayd daghigh tar chek bshe.
                //stroage format in ssIndex: cid(int),qlen(int),qSeq(byte array),parentCId(int)
                 
                CID=xBufferChannel.getInt();
                logManager.LogManager.log(1,"CID: "+CID);
                qNameLen=xBufferChannel.getInt();
                int reqSpace=qNameLen+Settings.sizeOfInt;
                int capacity=Settings.pageSize-xBufferChannel.position();
                if(reqSpace<=capacity)
                {
                    qNameSeq=new byte[qNameLen];
                    xBufferChannel.get(qNameSeq,0,qNameLen);
                    qName=new String(qNameSeq);
                    parentCID=xBufferChannel.getInt();
                    currentNode.setCID(CID);
                    currentNode.setParentCID(parentCID);
                    currentNode.setQName(qName);
                    unTakenNodes--;
                    return currentNode;//return breaks loop!
                }
            }

            else
            {

//               if(unTakenNodes==0)//in k ghalate baz :(
//               {   return null;  }                                                                                                                                                                                                              
//               else //if(XBufferChannel.position()+2*Settings.sizeOfInt>Settings.pageSize)
//               {    //goTOnextPAge;
                  xBufferChannel.position(Settings.nextPagePos);
                  nextPage=xBufferChannel.getInt();
                  bufMngr.unpinBuffer(xBufferChannel);
                  if(nextPage>0)
                  {   
                      loadSSPage(nextPage);//prepares XBufferChannel and loads page into it.
//                      System.out.println("loaded:"+nextPage);
                  
                  }
                  else 
                      return null;
                  workingPage=nextPage;
                  xBufferChannel.position(Settings.dataStartPos);//instead of calling open(somehow opens nextpage)              
//               }         
            }
    }//end while
    }
   
    private void loadSSPage(int PageNo) throws IOException, DBException 
    {
        //LOADs a page into memory:
        xBufferChannel.clear();
        xBufferChannel = bufMngr.getPage(PageNo);
        xBufferChannel.position(Settings.freeOffsetPos);//5
        freeOffset = xBufferChannel.getInt();
        xBufferChannel.position(Settings.numOfKeysPos);
        numOfKeys = xBufferChannel.getInt();
        unTakenNodes=numOfKeys;
    }
    
//    public void add(int CID,String qName,int parentCID)
//    {
//        boolean keyFound=false;
//        for(int i=nodeList.size()-1;i>=0;i--)
//        {
//            if(nodeList.get(i).getCID()==CID)
//            {
//                keyFound=true;
//                break;
//            }  
//        }
//        if(!keyFound)
//        {     
//            StructuralSummaryNode newNode=new StructuralSummaryNode(CID,qName,parentCID);
//            nodeList.add(newNode);
//        }
//    }
    
    public void add(int CID,String qName,int parentCID) throws IOException, DBException
    {
        boolean keyFound=false;        
        byte[] qNameArray;
        int qLength;
        //we need this in case that keys being added exceed page size:        
        //storage format in ssIndex: cid(int),qlen(int),qSeq(byte array),parentCId(int) 
       
        qNameArray=qName.getBytes(); 
        qLength=qNameArray.length;//qname only
        xBufferChannel.position(freeOffset);//Every Iteration it must be set.
        int capacity=Settings.pageSize-freeOffset;
        int reqSpace=qLength+3*Settings.sizeOfInt;
        if(reqSpace<=capacity)
        {
            
            xBufferChannel.putInt(CID);//CID             
//            logManager.LogManager.log(0,"QNAME: "+ qName);
//            logManager.LogManager.log(0,"Position: "+ xBufferChannel.position());
            xBufferChannel.putInt(qLength);
            xBufferChannel.put(qNameArray,0,qLength); 
            xBufferChannel.putInt(parentCID);//parentCId
            numOfKeys++;
            freeOffset+=(qLength+3*Settings.sizeOfInt);
        }
        else
        {
           
            //must add page
            //twinNode=new BTreeNode(indexName,(byte)0,(byte)1, -1);//numOfKeys is not valid yet
            XBufferChannel hlpr=bufMngr.getFreePage();
            workingPage=hlpr.getPageNumber();
            logManager.LogManager.log(5,"Structural Sum P: "+ workingPage);
           // numOfKeys=keysAdded;
            //unTakenRecs=numOfKeys;maybe we need this in the future
            xBufferChannel.position(Settings.nextPagePos);
            xBufferChannel.putInt(workingPage);
//            if(workingPage==19995)
//                logManager.LogManager.log(5, "nextPage:19915"+"workingPage: "+xBufferChannel.getPageNumber());
            xBufferChannel.position(Settings.freeOffsetPos); //not needed
            xBufferChannel.putInt(freeOffset);
           // if(freeOffset==511)
//            logManager.LogManager.log(5,"freeOffset: "+ freeOffset);
            xBufferChannel.position(Settings.numOfKeysPos);
            xBufferChannel.putInt(numOfKeys);
            //if(numOfKeys==26)
//            logManager.LogManager.log(5,"numOfKeys: "+ numOfKeys);
            bufMngr.unpinBuffer(xBufferChannel, true);
            changeWorkingPageTo(hlpr);
            //write remaining record:
            xBufferChannel.putInt(CID);//CID 
            xBufferChannel.putInt(qLength);
            xBufferChannel.put(qNameArray,0,qLength); 
            xBufferChannel.putInt(parentCID);//parentCId
            numOfKeys++;
            freeOffset+=(qLength+3*Settings.sizeOfInt);                
        }
            //unTakenRecs=numOfKeys;maybe we need this in the future
             //here nexP should be -1 cause there is no page after!
            xBufferChannel.position(Settings.freeOffsetPos);
            xBufferChannel.putInt(freeOffset);
            xBufferChannel.position(Settings.numOfKeysPos);
            xBufferChannel.putInt(numOfKeys);
           /// bufMngr.unpinBuffer(xBufferChannel, true);                         
        
    }
    public void endDoc() throws IOException
    {
       bufMngr.unpinBuffer(xBufferChannel, true); 
    }
    
//    public void pushQNamelist() throws IOException
//    {
//        //??? agar bozorgtar az y safhe bshe chi?bayad mohasebe konim...
//        //felan farz mikonim k faghat y safhe ja migire()age bkhaim dblp ro bdim bayd chek she
//        //write all nodes
//        byte[] qNameArray;
//        int qLength;
//        //we need this in case that keys being added exceed page size:
//        int keysAdded=0;
//        //boolean write=true;
//        //storage format in ssIndex: cid(int),qlen(int),qSeq(byte array),parentCId(int) 
//        freeOffset=Settings.dataStartPos;//initialize
//        xBufferChannel.position(Settings.dataStartPos);
//        for(int i=0;i<nodeList.size();i++)
//        {
//            qNameArray=((StructuralSummaryNode)nodeList.get(i)).getQName().getBytes(); 
//            qLength=qNameArray.length;//qname only
//            int capacity=Settings.pageSize-xBufferChannel.position();
//            int reqSpace=qLength+3*Settings.sizeOfInt;
//            if(reqSpace<=capacity)
//            {
//                xBufferChannel.putInt(nodeList.get(i).getCID());//CID 
//                xBufferChannel.putInt(qLength);
//                xBufferChannel.put(qNameArray,0,qLength); 
//                xBufferChannel.putInt(nodeList.get(i).getParentCID());//parentCId
//                keysAdded++;
//                freeOffset+=(qLength+3*Settings.sizeOfInt);
//            }
//            else
//            {
//                System.out.println("SS Page:"+workingPage);
//                //must add page
//                workingPage=createSSIndexPage();//after closing index working p must channge
//                numOfKeys=keysAdded;
//                //unTakenRecs=numOfKeys;maybe we need this in the future
//                xBufferChannel.position(Settings.nextPagePos);
//                xBufferChannel.putInt(workingPage);
//                xBufferChannel.position(Settings.freeOffsetPos); //not needed
//                xBufferChannel.putInt(freeOffset);
//                xBufferChannel.position(Settings.numOfKeysPos);
//                xBufferChannel.putInt(numOfKeys);
//                bufMngr.unpinBuffer(xBufferChannel, true);
//                changeWorkingPageTo(workingPage); 
//                //write remaining record:
//                keysAdded=0;
//                xBufferChannel.putInt(nodeList.get(i).getCID());//CID 
//                xBufferChannel.putInt(qLength);
//                xBufferChannel.put(qNameArray,0,qLength); 
//                xBufferChannel.putInt(nodeList.get(i).getParentCID());//parentCId
//                keysAdded++;
//                freeOffset+=(qLength+3*Settings.sizeOfInt);
//                
//            }
//        } 
//             numOfKeys=keysAdded;
//            //unTakenRecs=numOfKeys;maybe we need this in the future
//             //here nexP should be -1 cause there is no page after!
//            xBufferChannel.position(Settings.freeOffsetPos);
//            xBufferChannel.putInt(freeOffset);
//            xBufferChannel.position(Settings.numOfKeysPos);
//            xBufferChannel.putInt(numOfKeys);
//            bufMngr.unpinBuffer(xBufferChannel, true);
//        
//    }
    //instead of search we have pull:
    public ArrayList<StructuralSummaryNode> pullQNamelist() throws IOException
    {
        ArrayList<StructuralSummaryNode> fetchedNodeList=new ArrayList<>();
        byte[] qNameArray;
        int recLength;
        int CID;
        String qName;
        int parentCID;
        indexManager.StructuralSummaryNode helperNode;
        xBufferChannel.position(Settings.dataStartPos);
        for(int i=0;i<numOfKeys;i++)
        {                           
            recLength=xBufferChannel.getInt();
            qNameArray=new byte[recLength-2*Settings.sizeOfInt];
            CID=xBufferChannel.getInt();//CID      
            parentCID=xBufferChannel.getInt();//parent
            xBufferChannel.get(qNameArray,0,qNameArray.length);
            qName=new String(qNameArray);
            helperNode=new indexManager.StructuralSummaryNode(CID, qName, parentCID);
            fetchedNodeList.add(helperNode);
        } 
        //bufMngr.unpinBuffer(XBufferChannel, true);//is it needed?
        return fetchedNodeList;
    }
   public StructuralSummaryIterator getStream() throws IOException
     {
         
         StructuralSummaryIterator ssStream=new StructuralSummaryIterator(this);
         return ssStream;
     }
}

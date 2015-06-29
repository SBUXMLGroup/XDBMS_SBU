//package indexManager.backup;
//
//import LogicalFileManager.LogicalFileManager;
//import bufferManager.BufferManager;
//import bufferManager.InternalBuffer;
//import commonSettings.Settings;
//import commonSettings.summarySettings;
//import java.io.IOException;
//import java.util.ArrayList;
//public class StructuralSummaryIndex
//{
//    //what if we need 2 2 pages????***
//    //ArrayList<String> qNameList;
//    ArrayList<indexManager.StructuralSummaryNode> nodeList;
//    private int numOfKeys;
//    private int freeOffset;
//    //minkeys=20
//    //private ByteBuffer byteBuf;
//    private int indexPage;
//    private InternalBuffer InternalBuffer;
//    private LogicalFileManager logicFileMgr;
//    private String indexName;
//    private BufferManager bufMngr;
//    
//    public StructuralSummaryIndex(String indexName,int numOfKeys)
//    {
//        //this.qNameList=qNamelist; 
//        //this.nodeList=nodeList;
//        this.indexName=indexName;
//        this.numOfKeys=numOfKeys;
//        logicFileMgr = LogicalFileManager.Instance;
//        bufMngr = BufferManager.getInstance();
//        InternalBuffer=new InternalBuffer();//vasl bshe b buf mgr:getfreepage()
//        InternalBuffer.clear();        
//    }
//    public StructuralSummaryIndex(int indexPage,String indexName) throws IOException
//    {
//        //to open a SSIndex object.
//        this.indexPage=indexPage;
//        InternalBuffer = bufMngr.getPage(indexPage);
//        InternalBuffer.position(Settings.freeOffsetPos);//5
//        freeOffset = InternalBuffer.getInt();
//        numOfKeys = InternalBuffer.getInt();   
//    }
//   
//    public void loadSSPage(int indexPage,String indexName) throws IOException 
//    {
//        //LOAD ExtendedbtreeNode:
//        this.indexPage=indexPage;
//        InternalBuffer = bufMngr.getPage(indexPage);
//        InternalBuffer.position(Settings.freeOffsetPos);//5
//        freeOffset = InternalBuffer.getInt();
//        numOfKeys = InternalBuffer.getInt();
//    }
//    public void pushQNamelist(ArrayList nodeList) throws IOException
//    {
//        //write all nodes
//        byte[] qNameArray;
//        int recLength;
//        
//        this.nodeList=nodeList;
//        numOfKeys=nodeList.size();
//        //Prepare buffer:
//        InternalBuffer.position(summarySettings.numOfKeysPos);
//        InternalBuffer.putInt(numOfKeys);
//        freeOffset=summarySettings.dataStartPos;//initialize
//        for(int i=0;i<numOfKeys;i++)
//        {
//            qNameArray=nodeList.get(i).getQName().getBytes(); 
//            recLength=qName.length+2*summarySettings.sizeOfInt;//qname+cid+parent            
//            InternalBuffer.putInt(recLength);
//            InternalBuffer.putInt(nodeList.get(i).getCID());//CID 
//            InternalBuffer.putInt(nodeList.get(i).getParent());//parent
//            InternalBuffer.put(qNameArray,0,qNameArray.length); 
//            freeOffset+=(recLength+summarySettings.sizeOfInt);
//        } 
//        
//        InternalBuffer.position(summarySettings.freeOffsetPos);
//        InternalBuffer.putInt(freeOffset);
//        bufMngr.unpinBuffer(InternalBuffer, true);
//        
//    }
//    //instead of search we have pull:
//    public ArrayList<indexManager.StructuralSummaryNode> pullQNamelist() throws IOException
//    {
//        ArrayList<indexManager.StructuralSummaryNode> fetchedNodeList=new ArrayList<>();
//        byte[] qNameArray;
//        int recLength;
//        int CID;
//        String qName;
//        int parentCID;
//        indexManager.StructuralSummaryNode helperNode;
//        InternalBuffer.position(summarySettings.dataStartPos);
//        for(int i=0;i<numOfKeys;i++)
//        {                           
//            recLength=InternalBuffer.getInt();
//            qNameArray=new byte[recLength-2*summarySettings.sizeOfInt];
//            CID=InternalBuffer.getInt();//CID      
//            parentCID=InternalBuffer.getInt();//parent
//            InternalBuffer.get(qNameArray,0,qNameArray.length);
//            qName=new String(qNameArray);
//            helperNode=new indexManager.StructuralSummaryNode(CID, qName, parentCID);
//            fetchedNodeList.add(helperNode);
//        } 
//        //bufMngr.unpinBuffer(InternalBuffer, true);//is it needed?
//        return fetchedNodeList;
//    }
//    
//}

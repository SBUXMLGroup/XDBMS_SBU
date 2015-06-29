//
//package indexManager;
//
//import LogicalFileManager.LogicalFileManager;
//import bufferManager.BufferManager;
//import bufferManager.InternalBuffer;
//import commonSettings.Settings;
//import java.io.IOException;
//import java.util.ArrayList;
//import xmlProcessor.DeweyID;
//
//
//public class BTreeLeaf
//{
//    // private byte pageState; //for future use
//
//    private byte isRoot;
//    private byte isLeaf;
//    private int numOfKeys;
//    private int freeOffset;
//    //minkeys=20
//    //private ByteBuffer byteBuf;
//    private InternalBuffer InternalBuffer;
//    private LogicalFileManager logicFileMgr;
//    private String indexName;
//    private BufferManager bufMngr;
//    private int testPos;
//
//    public BTreeLeaf()//farz konim in internal noe bashe v y clase leafnode ham bayad bnvisam
//    {
//    }
//
//    public BTreeLeaf(InternalBuffer xBuf,String indexName) 
//    {
//        
//        logicFileMgr = LogicalFileManager.Instance;
//        this.indexName=indexName;
//        bufMngr = BufferManager.getInstance();
//        InternalBuffer = xBuf;
//        InternalBuffer.position(Settings.freeOffsetPos);//5
//        testPos=InternalBuffer.position();
//        freeOffset = InternalBuffer.getInt();
//        //byteBuf=xBuf.getByteBuffer();
//        InternalBuffer.position(Settings.isLeafPos);
//        testPos=InternalBuffer.position();
//        
//        isLeaf = InternalBuffer.get();
//        //InternalBuffer.position(Settings.numOfKeysPos);
//        numOfKeys = InternalBuffer.getInt();
//
//    }
//
//    public boolean IsLeaf() {
//        if (isLeaf == 1) {
//            return true;
//        } else {
//            return false;
//        }
//    }
//
//    public boolean IsRoot() {
//        if (isRoot == 1) {
//            return true;
//        } else {
//            return false;
//        }
//    }
//public boolean leafNodeSearch(DeweyID deweyId) {
//        //dar barg ha bayd pagePointer hamun null bashand,ya aslan nabashand.(tu method insert taiin mishe)
//        int pagePointer = -1; //null
//        int length;
//        byte[] curId;
//        DeweyID curDId ; 
//        DeweyID result = null;
//        InternalBuffer.position(Settings.dataStartPos);//3+4+4??????
//        if(numOfKeys>0)
//        for (int i = 0; i < numOfKeys; i++)
//        {
//            pagePointer = InternalBuffer.getInt();//next page adrs
//            length = InternalBuffer.getInt();
//            curId = new byte[length];
//            curDId=new DeweyID();
//            InternalBuffer.get(curId, 0, length);
//            curDId.setDeweyId(curId);
//            result = curDId;
//            if (curDId.equals(deweyId)) {
//                return true; //key found
//            } else if (!(curDId.lessThan(deweyId))) {
//                return false; //key not found
//            }
//        }
//        return false;//not found
//
//    }
//
//    public void insertIntoLeaf(DeweyID key) throws IOException {
//
//        //ehtemalan bayad tamame page haro mesle ham bgirm chon agar k leaf internal node
//        //she ya int. node bkhad leaf she moshkel pish miad.
//        ArrayList<DeweyID> deweyIDList = new ArrayList<DeweyID>();
//        ArrayList<Integer> pagePointerList = new ArrayList<Integer>();
//        int[] elementPositionArray;//?
//        int length;
//        int keyLength;
//        int pagePointer;
//        byte[] curId;
//        byte[] temp;
//        int[] midPoint;
//        final long pageSize = Settings.pageSize;//package common:settings final static
//        DeweyID curDId;
//        InternalBuffer.position(Settings.dataStartPos);//3+4+4 ps+nextPage+freeOffset+isL+isR+numOf
//        //in nextPage bayd hatman bashe b hatere yaftane akharin page
//        if (numOfKeys > 0) 
//        {
//            for (int i = 0; i < numOfKeys; i++) 
//            {
//                pagePointer = InternalBuffer.getInt();//next page adrs
//                pagePointerList.add(pagePointer);
//                length = InternalBuffer.getInt();
//                curId = new byte[length];
//                curDId=new DeweyID();
//                InternalBuffer.get(curId, 0, length);
//                curDId.setDeweyId(curId);
//                if (!(curDId.lessThan(key))) {
//                    deweyIDList.add(key);//cheghad ghashang mishe inaro nevesht :)))
//                    pagePointerList.add(Settings.nullPagePointer);
//                }
//                deweyIDList.add(curDId);
//            }
//            pagePointer = InternalBuffer.getInt();//the right most pointer
//            pagePointerList.add(pagePointer);
//            if (!deweyIDList.contains(key)) {
//                deweyIDList.add(key);
//            }
//
//            keyLength = key.getDeweyId().length;//key DivNumber
//            int availableSpace=Settings.pageSize - 1 - freeOffset - numOfKeys;
//            int requiredSpace=Settings.sizeOfInt + keyLength + Settings.pagePointerSize;
//            if (availableSpace <(requiredSpace+10))//+ 4+4
//            {
//                elementPositionArray = new int[numOfKeys];
//                InternalBuffer.position(Settings.pageSize - 1 - numOfKeys * Settings.sizeOfByte);//az tahe page shoru mikonim b khundan
//                for (int i = 0; i < numOfKeys; i++) {
//                    elementPositionArray[i] = InternalBuffer.get();
//                }
//                midPoint = computeMidPoint(elementPositionArray);
//                this.split(midPoint, InternalBuffer, deweyIDList, key);
//            }
//        }
//        numOfKeys++;
//        //Prepare buffer:
//        InternalBuffer.position(Settings.numOfKeysPos);
//        InternalBuffer.putInt(numOfKeys);
//        //inja chon rewrite darim bayad dar noghteye data start bashim,yani bad az mahale num of keys
//        if(numOfKeys>1)//chon k ++ shode!
//        {
//           elementPositionArray = new int[numOfKeys];  
//           for (int i = 0; i < numOfKeys; i++)
//           {
//              //length=deweyIDList.get(i).getDeweyId().length;
//              temp = deweyIDList.get(i).getDeweyId();
//              length = temp.length;
//              elementPositionArray[i]=InternalBuffer.position();
//              InternalBuffer.putInt(pagePointerList.get(i));
//              InternalBuffer.putInt(length);
//              InternalBuffer.put(temp, 0, length);
//        }
//        InternalBuffer.putInt(Settings.nullPagePointer);
//        //******************
//        InternalBuffer.position(Settings.pageSize - 1 - numOfKeys * Settings.sizeOfByte);//az tahe page shoru mikonim b khundan
//        for(int i=0;i<numOfKeys;i++) //loop for setting elem positions at the end of page
//        {             
//             InternalBuffer.put((byte)elementPositionArray[i]);
//        }
//        //*************
//        freeOffset=freeOffset+Settings.sizeOfInt+key.getDeweyId().length;
//        }
//        else 
//        {
//            keyLength = key.getDeweyId().length;//key DivNumber
//            InternalBuffer.position(freeOffset);
//            InternalBuffer.putInt(Settings.nullPagePointer);
//            int rootPosition;
//            rootPosition=InternalBuffer.position();
//            InternalBuffer.putInt(keyLength);
//            temp = key.getDeweyId();
//            InternalBuffer.put(temp, 0, keyLength);
//            InternalBuffer.putInt(Settings.nullPagePointer);
//            InternalBuffer.position(Settings.pageSize - 1 - numOfKeys * Settings.sizeOfByte);//az tahe page shoru mikonim b khundan
//            InternalBuffer.put((byte)rootPosition);
//            
//            freeOffset= freeOffset+3*(Settings.sizeOfInt)+keyLength;
//            
//        }
//        InternalBuffer.position(Settings.freeOffsetPos);
//        InternalBuffer.putInt(freeOffset);
//        bufMngr.unpinBuffer(InternalBuffer, true);
//
//    }
//
//    public void split(int[] midPoint, InternalBuffer InternalBuffer, ArrayList<DeweyID> deweyIDList, DeweyID key) throws IOException {
//        //ps+isL+isR+freeOff+numOfKeys
//        int twinPageNumber = logicFileMgr.addPage(indexName);//bayad pageNum ro bde.
//        InternalBuffer twin = bufMngr.getPage(twinPageNumber);
//        //InternalBuffer twinByteBuf = null;
//        //twinByteBuf.allocate(1024);
//        //twinByteBuf=twin.getByteBuffer();
//        int length;
//        byte[] curId;
//        DeweyID curdId = null;
//        byte[] temp;
//        InternalBuffer.position(7);
//        // koja meta data ghabli az ru in pak mishe? 
//        if (deweyIDList.indexOf(key) < midPoint[1]) {
//            InternalBuffer.putInt(midPoint[1] + 1); //numOfKeys=i+1
//            for (int i = 0; i < midPoint[1] + 1; i++) {
//                //length=deweyIDList.get(i).getDeweyId().length;
//                temp = deweyIDList.get(i).getDeweyId();
//                length = temp.length;
//                InternalBuffer.putInt(length);
//                InternalBuffer.put(temp, 0, length);
//
//            }
//            //koja baghie metadata twin neveshte mishe?
//            twin.position(7);
//            twin.putInt(deweyIDList.size() - (midPoint[1] + 1)); //twin Num of keys
//
//            for (int i = midPoint[1] + 1; i < deweyIDList.size(); i++) {
//                //length=deweyIDList.get(i).getDeweyId().length;
//                temp = deweyIDList.get(i).getDeweyId();
//                length = temp.length;
//                twin.putInt(length);
//                twin.put(temp, 0, length);
//
//            }
//        } else {
//            InternalBuffer.putInt(midPoint[1]); //numOfKeys=i
//        }
//        for (int i = 0; i < midPoint[1]; i++) {
//            //length=deweyIDList.get(i).getDeweyId().length;
//            temp = deweyIDList.get(i).getDeweyId();
//            length = temp.length;
//            InternalBuffer.putInt(length);
//            InternalBuffer.put(temp, 0, length);
//        }
//        twin.position(7);
//        twin.putInt(deweyIDList.size() - (midPoint[1])); //twin Num of keys
//
//        for (int i = midPoint[1]; i < deweyIDList.size(); i++) {
//            //length=deweyIDList.get(i).getDeweyId().length;
//            temp = deweyIDList.get(i).getDeweyId();
//            length = temp.length;
//            twin.putInt(length);
//            twin.put(temp, 0, length);
//        }
//    }
//
//    public void split(int[] midPoint, InternalBuffer InternalBuffer, ArrayList<DeweyID> deweyIDList, DeweyID key, InternalBuffer twinBuf) throws IOException {
//        //ps+isL+isR+freeOff+numOfKeys
//        int twinPageNumber = logicFileMgr.addPage(null);//bayad pageNum ro bde.
//        InternalBuffer twin = bufMngr.getPage(twinPageNumber);
//        //InternalBuffer twinByteBuf = null;
//        //twinByteBuf.allocate(1024);
//        //twinByteBuf=twin.getByteBuffer();
//        int length;
//        byte[] curId;
//        DeweyID curdId = null;
//        byte[] temp;
//        InternalBuffer.position(7);
//        // koja meta data ghabli az ru in pak mishe?
//        if (deweyIDList.indexOf(key) < midPoint[1]) {
//            InternalBuffer.putInt(midPoint[1] + 1); //numOfKeys=i+1
//            for (int i = 0; i < midPoint[1] + 1; i++) {
//                //length=deweyIDList.get(i).getDeweyId().length;
//                temp = deweyIDList.get(i).getDeweyId();
//                length = temp.length;
//                InternalBuffer.putInt(length);
//                InternalBuffer.put(temp, 0, length);
//
//            }
//            //koja baghie metadata twin neveshte mishe?
//            twin.position(7);
//            twin.putInt(deweyIDList.size() - (midPoint[1] + 1)); //twin Num of keys
//
//            for (int i = midPoint[1] + 1; i < deweyIDList.size(); i++) {
//                //length=deweyIDList.get(i).getDeweyId().length;
//                temp = deweyIDList.get(i).getDeweyId();
//                length = temp.length;
//                twin.putInt(length);
//                twin.put(temp, 0, length);
//
//            }
//        } else {
//            InternalBuffer.putInt(midPoint[1]); //numOfKeys=i
//        }
//        for (int i = 0; i < midPoint[1]; i++) {
//            //length=deweyIDList.get(i).getDeweyId().length;
//            temp = deweyIDList.get(i).getDeweyId();
//            length = temp.length;
//            InternalBuffer.putInt(length);
//            InternalBuffer.put(temp, 0, length);
//        }
//        twin.position(7);
//        twin.putInt(deweyIDList.size() - (midPoint[1])); //twin Num of keys
//
//        for (int i = midPoint[1]; i < deweyIDList.size(); i++) {
//            //length=deweyIDList.get(i).getDeweyId().length;
//            temp = deweyIDList.get(i).getDeweyId();
//            length = temp.length;
//            twin.putInt(length);
//            twin.put(temp, 0, length);
//        }
//    }
//
//    public int[] computeMidPoint(int[] elementPlaces) {
//        int[] midPoint = new int[2];
//        midPoint[0] = Math.abs(elementPlaces[0] - 512); //midPoint value
//        int minimum;
//        minimum = elementPlaces[0];
//        for (int i = 0; i < elementPlaces.length; i++) {
//            elementPlaces[i] = Math.abs(elementPlaces[i] - 512);
//            midPoint[0] = Math.min(minimum, elementPlaces[i]);
//            if (elementPlaces[i] == minimum) {
//                midPoint[1] = i;
//            }
//        }
//        midPoint[0] = midPoint[0] + 512;
//        return midPoint; //makane shorue vasat tarin DID ra midahad
//    }
//}
//
//

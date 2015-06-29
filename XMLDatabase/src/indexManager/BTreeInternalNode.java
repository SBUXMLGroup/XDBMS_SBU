
package indexManager;

import LogicalFileManager.LogicalFileManager;
import bufferManager.BufferManager;
import bufferManager.InternalBuffer;
import commonSettings.Settings;
import xmlProcessor.DeweyID;


public class BTreeInternalNode 
{
   // private byte pageState; //for future use

    private byte isRoot;
    private byte isLeaf;
    private int numOfKeys;
    private int freeOffset;
    //minkeys=20
    //private ByteBuffer byteBuf;
    private InternalBuffer InternalBuffer;
    private LogicalFileManager logicFileMgr;
    private String indexName;
    private BufferManager bufMngr;
    private int testPos;

    public BTreeInternalNode()//farz konim in internal noe bashe v y clase leafnode ham bayad bnvisam
    {
    }

    public BTreeInternalNode(InternalBuffer xBuf,String indexName) 
    {
        
        logicFileMgr = LogicalFileManager.Instance;
        this.indexName=indexName;
        bufMngr = BufferManager.getInstance();
        InternalBuffer = xBuf;
        InternalBuffer.position(Settings.freeOffsetPos);//5
        testPos=InternalBuffer.position();
        freeOffset = InternalBuffer.getInt();
        //byteBuf=xBuf.getByteBuffer();
        InternalBuffer.position(Settings.isLeafPos);
        testPos=InternalBuffer.position();
        
        isLeaf = InternalBuffer.get();
        //InternalBuffer.position(Settings.numOfKeysPos);
        numOfKeys = InternalBuffer.getInt();

    }

    public boolean IsLeaf() {
        if (isLeaf == 1) {
            return true;
        } else {
            return false;
        }
    }

    public boolean IsRoot() {
        if (isRoot == 1) {
            return true;
        } else {
            return false;
        }
    }

    public int internalNodeSearch(DeweyID deweyId) 
    {
        //page: pagestate,nextpage,freeoffset
        //ps,isroot,isleaf,number of keys in this page,freeoff,nextp(leftmost),deweyId,nextp,deweyId,nextp(rightmost)
        int pagePointer = -1;//null
        int length;
        byte[] curId;
        DeweyID curDId = null;
        DeweyID result = null;
        InternalBuffer.position(Settings.dataStartPos);//3+4+4 ps+isL+isR+freeOff+numOf
        //if(IsRoot() & IsLeaf()& freeOffset==Settings.dataStartPos)
        if (numOfKeys > 0)
        {
            for (int i = 0; i < numOfKeys; i++)
            {
                pagePointer = InternalBuffer.getInt();//next page adrs
                length = InternalBuffer.getInt();
                curId = new byte[length];
                InternalBuffer.get(curId, 0, length);
                curDId=new DeweyID();
                curDId.setDeweyId(curId);
                result = curDId;
                if (!(curDId.notGreaterThan(deweyId))) {
                    break;
                }
            }
            if (curDId.notGreaterThan(deweyId))//baraye akharin curID(rightMost)
            {
                pagePointer = InternalBuffer.getInt(); //the last (right most)pointer
            }
        }
        return pagePointer;//page i ro bar migardune k result max un hast.
        //ki<k<ki+1

    } 
   
   }



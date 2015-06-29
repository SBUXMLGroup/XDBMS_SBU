
package bufferManager;

import java.util.HashMap;
import java .math.RoundingMode;
import java.util.Map;
import commonSettings.Settings;
import java.util.Collection;


public class BufferPool
{
    public static InternalBuffer[] bufArray=new InternalBuffer[Settings.buffers];

    /**
     *
     */
    public static Map <Integer,InternalBuffer> bufMap= new HashMap<>();
    
    public static Map <Integer,Integer> arrayMap= new HashMap<Integer, Integer>();//for Mapping pageNo to bufArrayIndx
    private int freeIndex; 
    private InternalBuffer tmpBuf;
    public BufferPool()
    {
        for(int i=0;i<Settings.buffers;i++)
        {
           tmpBuf=new InternalBuffer();
           //bufMap.put(i,tmpBuf);
           bufArray[i]=tmpBuf;
        }
        freeIndex=0; //as buffers get allocated, freeIndex goes ahead.Clock Arrow
    }
    public int size()
    {
        return bufArray.length;
    }
    public InternalBuffer getBuffer(int bufIndex)
    {
        return bufArray[bufIndex];
    }
    public int getFreeIndex()
    {
        return freeIndex;
    }
    public void advanceFreeIndex()
    {
        freeIndex++;
        freeIndex=freeIndex%Settings.buffers;
        //key 0 mishe?
        //freeIndex=newFreeIndex;//ya ++?
    }
    void setPgeNo(int pageNo)//pack access
    {}
    public void add(int pageNum,InternalBuffer buffer)
    {
        bufMap.put(pageNum,buffer);
        
    }
    public boolean containsPage(int pageNum)
    {
        return bufMap.containsKey(pageNum);
    }
    
    public InternalBuffer getMappedBuffer(int pageNum)
    {
       InternalBuffer buffer;
       buffer=bufMap.get(pageNum);
       // nullpointerexception:,agar hamchin entry nadashte bashim.
       return buffer;
       
    }
    public InternalBuffer removeMapEntry(int pageNumber)
    {
        InternalBuffer buffer;
        buffer=bufMap.remove(pageNumber);
        return buffer;
    }
    public HashMap getStatus()
    {
        HashMap<Integer,Integer> status=new HashMap<>();
        Collection<InternalBuffer> col=bufMap.values();
         for(int i=0;i<col.size();i++)
         {
             status.put(i, bufArray[i].getPinCount());
              
         }
         return status;
    }
}

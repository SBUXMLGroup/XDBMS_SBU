
package bufferManager;
import commonSettings.Settings;
import xmlProcessor.DBServer.DBException;


public class Clock implements IReplacementStrategy
{
    private BufferPool bufPool;
    public Clock(BufferPool bfPool)
    {
        bufPool=bfPool;
    }
    public BufWrapper selectBuffer() throws DBException
    {
        BufWrapper output=new BufWrapper();
//        if(bufPool.getFreeIndex()<Settings.buffers)
//        {
//            int freeIndex=bufPool.getFreeIndex();
//            bufPool.advanceFreeIndex();           
//            logManager.LogManager.log("Allocated buffer:buffer["+String.valueOf(freeIndex)+"]");
//            output.setIndx(freeIndex);
//            output.setInBuf(bufPool.getBuffer(freeIndex));                        
//            //return bufPool.getBuffer(i);
//            return output;
//        }

        while(true)
        {   for (int i=0;i<Settings.buffers;i++)
           {
            if(!bufPool.getBuffer(i).isPin()) 
            {           
                if(bufPool.getBuffer(i).getRef())
                    bufPool.getBuffer(i).setRef(false);          
                else   
                {    
                   //logManager.LogManager.log("Allocated buffer:buffer["+String.valueOf(i)+"]");
                    output.setIndx(i);
                    output.setInBuf(bufPool.getBuffer(i));                        
                    //return bufPool.getBuffer(i);
                    return output;
                }                
            }
            }//end for    
        throw new DBException("All Pages in local buffer pool are pinned.");
        }
    }//end method
        
}//end class


package bufferManager;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import xmlProcessor.DBServer.DBException;


public interface IBufferManager 
{
        //public void flushBuffer(InternalBuffer buffer,int bufferIndex) throws IOException;
	public void flushAllBuffers();
        public XBufferChannel getPage(int newPageNumber) throws IOException,DBException;
	public void unpinBuffer(InternalBuffer buf);
        public void unpinBuffer(InternalBuffer buf,boolean flush)throws IOException,DBException;
        
}

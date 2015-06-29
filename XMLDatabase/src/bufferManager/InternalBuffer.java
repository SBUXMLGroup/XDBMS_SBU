
package bufferManager;

import commonSettings.Settings;
import java.nio.Buffer;
import java.nio.ByteBuffer;


public class InternalBuffer //remove public
{
       private ByteBuffer byteBuffer;
       private boolean valid = false;
       private boolean ref= false;
       private boolean dirty = false;//modified
       private boolean pin=false;
       private int pinCount = 0;//May not be necessary
       private int pageNumber;
        //isField method
	/**
     *
     */
    public InternalBuffer()
    {
        this.byteBuffer=ByteBuffer.allocate(Settings.pageSize);
        pageNumber=-1;                
    }
    public InternalBuffer(ByteBuffer bf)
    {
	   this.byteBuffer=ByteBuffer.allocate(Settings.pageSize);
           byteBuffer=bf;
    }
    synchronized void  Pin()
    {
//            pin=true;
         pinCount++;
    }
    synchronized void Unpin()
    {
            if(pinCount>0)
             pinCount--;
           
//            if(pinCount==0)
//            {
//                pin=false;
//            }
     }
        public synchronized boolean  isPin()
        {
            return pinCount>0;
        }
        public synchronized int getPinCount()
        {
            return pinCount;            
        }
        public synchronized void Dirty()
        {
            dirty=true;
        }
        public synchronized void Undirty()
        {
            dirty=false; //not modified yet
        }
        public synchronized boolean isDirty()
        {
            return dirty;
        }
        public synchronized ByteBuffer getByteBuffer()
        {
            return byteBuffer;
        }
        public synchronized void setByteBuffer(ByteBuffer buf)
        {
            this.byteBuffer=buf;
        }
        public synchronized boolean getRef()
        {
            return ref;
        }
        public synchronized void setRef(boolean rValue)
        {
            ref=rValue;
        }
        
        public synchronized void setPageNumber(int pageNumber)
        {
            this.pageNumber=pageNumber;   
        
        }
        public synchronized int getPageNumber()
        {
            return this.pageNumber;
        }
        public synchronized boolean isField()
        {
            return pageNumber!=-1 & pageNumber>=0;
        }
        public synchronized Buffer clear()
        {
            return byteBuffer.clear();
        }
        //-----------------------------
        public synchronized InternalBuffer erase(int pos,int no)
        {
            for(int i=pos;i<(Settings.pageSize - 1 - no * Settings.sizeOfShort);i++)
           {
               byteBuffer.put((byte)0);
           }
            return this;
        }
        //---------------------------------
        public synchronized final Buffer flip()
        {
            return byteBuffer.flip();
        }
        
        public synchronized byte get(int position)
        {
            //pas chera tu tarifesh abstracte?
            this.position(position);
            return byteBuffer.get();
        }
        public synchronized byte get()
        {
             return byteBuffer.get();
        }
        public synchronized int getInt(int position)
        { 
            this.position(position);
            return byteBuffer.getInt();
        }
        public synchronized int getInt()
        { 
           return byteBuffer.getInt();
        }
        public synchronized InternalBuffer get(byte[] dst,int offset,int length,int position)
        {
            this.position(position);
            try{
               
            byteBuffer=byteBuffer.get(dst, offset, length);
            return this;
            }
            catch(Exception e) 
            {
                logManager.LogManager.log(6,dst+""+offset+""+length);
                throw  e;
            }           
            
        }
         public synchronized InternalBuffer get(byte[] dst,int offset,int length)
        {
            this.position();
            try{
               
            byteBuffer=byteBuffer.get(dst, offset, length);
            return this; }
            catch(Exception e) 
            {
                logManager.LogManager.log(dst+""+offset+""+length);
                throw  e;
            }           
            
        }
        public synchronized short getShort(int position)
        { 
             this.position(position);
            return byteBuffer.getShort();
        }
        public synchronized short getShort()
        { 
           return byteBuffer.getShort();
        }
        
        public synchronized final int position()
        {
            return byteBuffer.position();
        }
        
        public synchronized final Buffer position(int newPosition)
        {
            return byteBuffer.position(newPosition);
        }
        public synchronized InternalBuffer put(byte b,int position)
        {
            if(!isDirty()) 
                Dirty();
            this.position(position);
            byteBuffer=byteBuffer.put(b);
            return this;
        }
        public synchronized InternalBuffer put(byte b)
        {
            if(!isDirty()) 
                Dirty();
            byteBuffer=byteBuffer.put(b);
            return this;
        }
        public synchronized InternalBuffer putInt(int value,int position)
        {
            if(!isDirty())
                Dirty();
            this.position(position);
            byteBuffer=byteBuffer.putInt(value);
            return this;
        }
        public synchronized InternalBuffer putInt(int value)
        {
            if(!isDirty())
                Dirty();
            byteBuffer=byteBuffer.putInt(value);
            return this;
        }
        public synchronized InternalBuffer putChar(char value,int position)
        {
            if(!isDirty())
                Dirty();
            this.position(position);
            byteBuffer=byteBuffer.putChar(value);
            return this;
        }
        public synchronized InternalBuffer putChar(char value)
        {
            if(!isDirty())
                Dirty();
            byteBuffer=byteBuffer.putChar(value);
            return this;
        }
        public synchronized InternalBuffer put(byte[] src,int offset,int length,int position)
        {
            if(!isDirty())
                Dirty();
            this.position(position);
            byteBuffer=byteBuffer.put(src,offset,length);
            return this;
        }
        public synchronized InternalBuffer put(byte[] src,int offset,int length)
        {
            if(!isDirty())
                Dirty();
            byteBuffer=byteBuffer.put(src,offset,length);
            return this;
        }
        public synchronized InternalBuffer putShort(short value,int position)
        {
            if(!isDirty())
                Dirty();
            this.position(position);
            byteBuffer=byteBuffer.putShort(value);
            return this;            
        }
        public synchronized InternalBuffer putShort(short value)
        {
            if(!isDirty())
                Dirty();
            byteBuffer=byteBuffer.putShort(value);
            return this;            
        }
        
       
    
}

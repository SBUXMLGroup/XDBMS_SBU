//package bufferManager;
//
//import java.nio.ByteBuffer;
//import java.nio.Buffer;
//import java.util.ArrayList;
//import commonSettings.Settings;
//public class InternalBuffer 
//{        
//        private ByteBuffer byteBuffer;
//        private boolean valid = false;
//	private boolean ref= false;
//	private boolean dirty = false;//modified
//        private boolean pin=false;
//	private int pinCount = 0;
//	private int pageNumber;
//        //isField method
//	/**
//     *
//     */
//    public InternalBuffer()
//    {
//        this.byteBuffer=ByteBuffer.allocate(Settings.pageSize);
//        pageNumber=-1;                
//    }
//    public InternalBuffer(ByteBuffer bf)
//    {
//	   this.byteBuffer=ByteBuffer.allocate(Settings.pageSize);
//           byteBuffer=bf;
//    }
//        void  Pin()
//        {
////            pin=true;
//            pinCount++;
//        }
//        void Unpin()
//        {
//            if(pinCount>0)
//                pinCount--;
////            if(pinCount==0)
////            {
////                pin=false;
////            }
//        }
//        public boolean isPin()
//        {
//            return pinCount>0;
//        }
//        public int getPinCount()
//        {
//            return pinCount;            
//        }
//        public void Dirty()
//        {
//            dirty=true;
//        }
//        public void Undirty()
//        {
//            dirty=false; //not modified yet
//        }
//        public boolean isDirty()
//        {
//            return dirty;
//        }
//        public ByteBuffer getByteBuffer()
//        {
//            return byteBuffer;
//        }
//        public void setByteBuffer(ByteBuffer buf)
//        {
//            this.byteBuffer=buf;
//        }
//        public boolean getRef()
//        {
//            return ref;
//        }
//        public void setRef(boolean rValue)
//        {
//            ref=rValue;
//        }
//        public void setPageNumber(int pageNumber)
//        {
//            this.pageNumber=pageNumber;   
//        
//        }
//        public int getPageNumber()
//        {
//            return this.pageNumber;
//        }
//        public boolean isField()
//        {
//            return pageNumber!=-1 & pageNumber>=0;
//        }
//        public Buffer clear()
//        {
//            return byteBuffer.clear();
//        }
//        public InternalBuffer erase(int pos,int no)
//        {
//            for(int i=pos;i<(Settings.pageSize - 1 - no * Settings.sizeOfShort);i++)
//           {
//               byteBuffer.put((byte)0);
//           }
//            return this;
//        }
//        public final Buffer flip()
//        {
//            return byteBuffer.flip();
//        }
//        public byte get()
//        {
//            //pas chera tu tarifesh abstracte?
//            return byteBuffer.get();
//        }
//        public int getInt()
//        { 
//            //exception ha chi?(underflow)
//            return byteBuffer.getInt();
//        }
//        public InternalBuffer get(byte[] dst,int offset,int length)
//        {
//            byteBuffer=byteBuffer.get(dst, offset, length);
//            return this; 
//        }
//        public short getShort()
//        { 
//            //exception ha chi?(underflow)
//            return byteBuffer.getShort();
//        }
//        
//        public final int position()
//        {
//            return byteBuffer.position();
//        }
//        public final Buffer position(int newPosition)
//        {
//            return byteBuffer.position(newPosition);
//        }
//        public InternalBuffer put(byte b)
//        {
//            if(!isDirty()) //bayd koja bashe?
//                Dirty();
//            byteBuffer=byteBuffer.put(b);
//            return this;
//        }
//        public InternalBuffer putInt(int value)
//        {
//            if(!isDirty())
//                Dirty();
//            byteBuffer=byteBuffer.putInt(value);
//            return this;
//        }
//        public InternalBuffer put(byte[] src,int offset,int length)
//        {
//            if(!isDirty())
//                Dirty();
//            byteBuffer=byteBuffer.put(src,offset,length);
//            return this;
//        }
//        public InternalBuffer putShort(short value)
//        {
//            if(!isDirty())
//                Dirty();
//            byteBuffer=byteBuffer.putShort(value);
//            return this;            
//        }
//        
//        public ArrayList xbufToArraylist()
//        {
//            int curPosition;
//            byte pageState;
//            int nextPage;
//            int freeOffset;
//            byte isRoot;
//            byte isLeaf;
//            int numOfKeys;
//            ArrayList xbufContent=new ArrayList();
//            xbufContent.add(pin);
//            xbufContent.add(dirty);
//            xbufContent.add(pageNumber);
//            xbufContent.add(pin);
//            curPosition=byteBuffer.position();
//            byteBuffer.position(0);
//            pageState=byteBuffer.get();
//            xbufContent.add(pageState);            
//            nextPage=byteBuffer.getInt();
//            xbufContent.add(nextPage); 
//            freeOffset=byteBuffer.getInt();
//            xbufContent.add(freeOffset); 
//            isRoot=byteBuffer.get();
//            xbufContent.add(isRoot); 
//            isLeaf=byteBuffer.get();
//            xbufContent.add(isLeaf); 
//            numOfKeys=byteBuffer.getInt();
//            xbufContent.add(numOfKeys); 
//            return xbufContent;
//        
//        }
//        
//
//}

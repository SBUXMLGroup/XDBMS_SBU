/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bufferManager;

import commonSettings.Settings;
import indexManager.ElementBTreeNode;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;


public class XBufferChannel 
{
        private InternalBuffer inBuf;
        private boolean valid = false;
        private boolean ref= false;
        private boolean dirty = false;//modified
       // private boolean pin=false;
        private int pageNumber;
        private int position;
        //isField method
	/**
     *
     */
    public XBufferChannel()
    {
//      this.inBuf=ByteBuffer.allocate(Settings.pageSize);
        
        pageNumber=-1; 
        position=0;
    }
    public XBufferChannel(InternalBuffer inBuf)
    {
//      this.inBuf=ByteBuffer.allocate(Settings.pageSize);
        this.inBuf=inBuf;
        pageNumber=inBuf.getPageNumber(); 
        position=0;
    }
//    public XBufferChannel(ByteBuffer bf)
//    {
////       this.inBuf=ByteBuffer.allocate(Settings.pageSize);
//       inBuf=bf;
//    }
    void Pin()
    {
        //since its a channel allocated to one user,it never should go further than one.
        inBuf.Pin();
    }
    void Unpin()
    {
        inBuf.Unpin();
    }
    public boolean isPin()
    {
        return inBuf.isPin();
    }
    public int getPinCount()
    {
        return inBuf.getPinCount();            
    }
    public void Dirty()
    {
        //if channel be for writing purpose.
       inBuf.Dirty();
    }
    public void Undirty()
    {
       inBuf.Undirty();//???is it necessary?
    }
    public boolean isDirty()
    {
        return dirty;
    }
//        public void setPageNumber(int pageNumber)
//        {
//            this.pageNumber=pageNumber;   
//        
//        }
    public int getPageNumber()
    {
        return inBuf.getPageNumber();
    }
    public InternalBuffer getInternalBuffer()
    {
        return inBuf;
    }

    public boolean isField()
    {
        return pageNumber!=-1 & pageNumber>=0;
    }
    //for WRITE:
    public Buffer clear()
    {
        position=0;
        return inBuf.clear();
    }
    public XBufferChannel erase(int pos,int no)
    {
        for(int i=pos;i<(Settings.pageSize - 1 - no * Settings.sizeOfShort);i++)
       {
           inBuf.put((byte)0,i);
       }
        return this;
    }
        public void setPageNumber(int pageNumber)
        {
            //this.pageNumber=pageNumber;
            inBuf.setPageNumber(pageNumber);
        
        }
        
        public final Buffer flip()
        {
            return inBuf.flip();
        }
        public XBufferChannel put(byte b)
        {
            if(!isDirty()) //bayd koja bashe?
                Dirty();
           // inBuf.position(this.position);
            inBuf=inBuf.put(b,this.position);
            position++;
               if(position==85)
             logManager.LogManager.log("Error"); 
            return this;
        }
        public XBufferChannel putInt(int value)
        {
            if(!isDirty())
                Dirty();
            //inBuf.position(this.position);
            inBuf=inBuf.putInt(value,this.position);
            position+=4;              
            return this;
        }
        public XBufferChannel putChar(char value)
        {
            if(!isDirty())
                Dirty();
            //inBuf.position(this.position);
            inBuf=inBuf.putChar(value,this.position);
            position+=4;              
            return this;
        }
        public XBufferChannel put(byte[] src,int offset,int length)
        {
            if(!isDirty())
                Dirty();
            //inBuf.position(this.position);
            inBuf=inBuf.put(src,offset,length,this.position);
            position+=length;
            if(position==85)
             logManager.LogManager.log("Error"); 
            return this;
        }
        public XBufferChannel putShort(short value)
        {
            if(!isDirty())
                Dirty();
           // inBuf.position(this.position);
            inBuf=inBuf.putShort(value,this.position);
            position+=2;
            if(position==85)
             logManager.LogManager.log("Error"); 
            return this;            
        }
        
        //************************************
        //for READ:
        public byte get()
        {
            //inBuf.position(this.position);
            byte getter=inBuf.get(this.position);            
            position++;
            return getter;
        }
        public int getInt()
        { 
            //exception ha chi?(underflow)
            //inBuf.position(this.position);
            int getter=inBuf.getInt(this.position);
            position+=4;
            return getter;
        }
        public XBufferChannel get(byte[] dst,int offset,int length)
        {
            //inBuf.position(this.position);
            inBuf=inBuf.get(dst, offset, length,this.position);
            //che juri???mage call by value nist?
            position+=length;
            return this; 
        }
        public short getShort()
        { 
            //exception ha chi?(underflow)
            //inBuf.position(this.position);
            short getter=inBuf.getShort(this.position);
            position+=2;
            return getter;
        }
        //***********************************************
        public final int position()
        {
            return position;
        }
        public final Buffer position(int newPosition)
        {
            //??????///
            position=newPosition;
//            if(position==85)
//             logManager.LogManager.log("Error in position() ");  
            logManager.LogManager.log("Position Changed: "+ position);
            return inBuf.position(newPosition);
        }
//        public final Buffer position(int newPosition,ElementBTreeNode changer)
//        {
//            //??????///
//            position=newPosition;
//            if(position==85)
//             logManager.LogManager.log("WRONG " + changer);   
//            logManager.LogManager.log("Position Changed to: "+ position+" by " + changer);
//            return inBuf.position(newPosition);
//        }
        public InternalBuffer getInBuf()
        {
            return inBuf;            
        }
        //*************************************************
        public ArrayList printXBuf()
        {
            int curPosition;
            byte pageState;
            int nextPage;
            int freeOffset;
            byte isRoot;
            byte isLeaf;
            int numOfKeys;
            ArrayList xbufContent=new ArrayList();
            logManager.LogManager.log(0,"p.N:"+String.valueOf(pageNumber));
            this.position(0);
            pageState=this.get();
            logManager.LogManager.log(0,"p.State:"+String.valueOf(pageState));            
            nextPage=getInt();
            logManager.LogManager.log(0,"next Page:"+String.valueOf(nextPage)); 
            freeOffset=getInt();
            logManager.LogManager.log(0,"free offset:"+String.valueOf(freeOffset));
            isRoot=get();
            logManager.LogManager.log(0,"isRoot:"+String.valueOf(isRoot));
            isLeaf=get();
            logManager.LogManager.log(0,"isLeaf:"+String.valueOf(isLeaf)); 
            numOfKeys=getInt();
            logManager.LogManager.log(0,"numOfKeys"+String.valueOf(numOfKeys));
            return xbufContent;
        
        }
       
    
}

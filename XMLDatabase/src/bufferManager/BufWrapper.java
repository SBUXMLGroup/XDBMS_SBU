/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bufferManager;


public class BufWrapper 
{
        private int bufIndx;
        private InternalBuffer inBuf;
        public void setIndx(int indx)
        {
            bufIndx=indx;
        }
        public void setInBuf(InternalBuffer inBuf)
        {
            this.inBuf=inBuf;
        }
        public int getIndx()
        {
            return bufIndx;
        }
        public InternalBuffer getInBuf()
        {
            return inBuf;
        }
    
}

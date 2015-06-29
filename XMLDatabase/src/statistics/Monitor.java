/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package statistics;

/**
 *
 * @author Micosoft
 */
public class Monitor 
{
    private static long  IOTime;
    private static int readPages;
    private static int readPagesAttempt;
    private static int numberOfElementsRead;
    private final static Monitor Instance=new Monitor();
    private Monitor()
    {
       IOTime=0;
       readPages=0;
       readPagesAttempt=0;
       numberOfElementsRead=0;       
    }
    public static Monitor getInstance()
    {
        return Instance;
    }
    public void increaseIOTime(long t)
    {
        IOTime+=t;
    }
    public void addElementsRead()
    {
        numberOfElementsRead++;
    }
    public void addReadPages()
    {
        readPages++;
    }
    public void addReadPagesAttempt()
    {
        readPagesAttempt++;
    }
    public long getIOTime()
    {
        return IOTime;
    }
    public long getReadPages()
    {
        return readPages;
    }
    public long getReadPagesAttempt()
    {
        return readPagesAttempt;
    }
    public long getElementsRead()
    {
        return numberOfElementsRead;
    }
    public void reset()
    {
        IOTime=0;
        numberOfElementsRead=0;
        readPages=0;
        readPagesAttempt=0;
    }
    
}

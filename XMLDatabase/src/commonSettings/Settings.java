/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package commonSettings;


public class Settings 
{
    public static int tCount=0;
    public static final int pageSize=4096;
//    public static final int pageSize=512;
    public static final int sizeOfInt=4;
    public static final int sizeOfByte=1;
    public static final int sizeOfShort=2;
    public static final int sizeOfLong=8;
    
    public static final int pageStatePos=0;//byte
    public static final int nextPagePos=1;//4
    public static final int freeOffsetPos=5;//int
    //public static final int parentPointerPos=9;//int
    public static final int isRootPos=9;//byte
    public static final int isLeafPos=10;//byte
    public static final int numOfKeysPos=11;//int
    public static final int dataStartPos=15;
    public static final int pagePointerSize=4;
    public static final int nullPagePointer=-1;
    public static final int invalidValue=-1;
    //*************************************
    //GMD:
    public static final int GMDPage=0;
    public static final int waterMarkPos=13;
    //**************************************
    //index types:
    public static final int BTreeType=1;
    public static final int ElementType=2;
    public static final int SSType=3;
    
    //BufferMgr:
    public static final int buffers=400;
    //public static final int resBuf=10;
    
    
}

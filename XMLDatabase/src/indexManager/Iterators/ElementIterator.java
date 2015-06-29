
package indexManager.Iterators;

import indexManager.ElementBTreeNode;
import indexManager.ElementBTreeNodeWrapper;
import indexManager.util.OutputWrapper;
import xmlProcessor.DeweyID;
import java.io.IOException;
import java.util.ArrayList;
import xmlProcessor.DBServer.DBException;


public class ElementIterator
{
   //this is an Iterator on ElementBtreeNode
    private ElementBTreeNode container;
    private ElementBTreeNodeWrapper containerWrapper;
    private int CID;
    private boolean justChanged=false;
    private int numberOfReadElements;
    private ArrayList openPages;
    private boolean completeIter=false;//sich for determining whether iteration should be done based on one CID or over the whole Index
    private static int testCounter=0;
    /**public ElementIterator(ElementIndex elementIndex)
    {
        this.elemIndex=elementIndex;
    }*/
//    public ElementIterator(ElementBTreeNode containerNode,int CID)
//    {
//        this.container=containerNode;
//        this.CID=CID;
//        numberOfReadElements=0;
//    }
    public ElementIterator(ElementBTreeNodeWrapper containerWrapper)
    {
        this.containerWrapper=containerWrapper;
        this.container=containerWrapper.getCore();
        this.CID=0;
        setCompleteIter(true);
        numberOfReadElements=0;
        openPages=new ArrayList<>();
        openPages.add(container);
    }
    public ElementIterator(ElementBTreeNodeWrapper containerWrapper,int CID)
    {
        this.containerWrapper=containerWrapper;
        this.container=containerWrapper.getCore();
        this.CID=CID;
        //if(CID==38)            
           logManager.LogManager.log(6,"CID: "+CID);
        setCompleteIter(false);
        numberOfReadElements=0;
        openPages=new ArrayList<>();
        openPages.add(container);
    }
    private void setCompleteIter(boolean value)
    {
        completeIter=value;        
    }
    private void changeContainer(ElementBTreeNode cont)
    {
        this.container.closeElementNode();//unpin last container;
        this.container=cont;
        this.containerWrapper.reset(cont);
        openPages.add(cont);
        justChanged=true;
    }
    public void open() throws IOException
    {
        //needed for RG class,what does it do?
        container.prepare(containerWrapper.getRecordPOs());
    }
    public DeweyID next() throws IOException, DBException
    {        
        DeweyID nextDID=null;
        
        OutputWrapper <ElementBTreeNode,DeweyID> outputWrapper;
        if(justChanged)
        {
            open();
            justChanged=false;
        }
        outputWrapper=container.next(CID);
        while(outputWrapper!=null)
        {
            nextDID =(DeweyID) outputWrapper.getOutput(); 
//            if(nextDID!=null)
//            {
//                logManager.LogManager.log(6,"CID:"+nextDID.getCID());
//               // logManager.LogManager.log(6,"DID:"+nextDID.toString());
//            }
            if(outputWrapper.getContainer()!=null)
            {  
               changeContainer(outputWrapper.getContainer());
               logManager.LogManager.log(7,"Elem Indx Page: "+outputWrapper.getContainer().getIndexPage());
               open();
               justChanged=false;
               outputWrapper=container.next(CID);//THIS IS NOT TESTED YET, NECESSARY TO LOOK UP NEXT PAGE
            }    
            else
                break;
        }
        if(outputWrapper==null)
            nextDID=null;
        if(nextDID!=null)
            numberOfReadElements++;
        
        return nextDID;
        //return checkCID(nextDID);       
    }
    public void closeIterator()
    {
        
    }
    public int getNumberOfReadElements()
    {
        return numberOfReadElements;
    }
//    private DeweyID checkCID(DeweyID nextDID)
//    {
//        if(nextDID.getCID()==this.CID)
//        {
//            return nextDID;
//        }
//        else //if(nextDID==null || !=this.CID)
//        {
//            return null;            
//        }
//    }
    public int getIOTime()
    {
      return 0;  
    }
    
}

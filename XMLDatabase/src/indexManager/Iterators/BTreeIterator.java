/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package indexManager.Iterators;

import indexManager.BTreeIndex;
import indexManager.BTreeNode;
import indexManager.ElementBTreeNode;
import indexManager.ElementBTreeNodeWrapper;
import indexManager.util.OutputWrapper;
import indexManager.util.Record;
import java.io.IOException;
import java.util.ArrayList;
import xmlProcessor.DBServer.DBException;
import xmlProcessor.DeweyID;
/**
 *
 * @author Micosoft
 */
public class BTreeIterator
{
    private BTreeNode container;
    private boolean justChanged=false;
    private int numberOfReadElements=0;
    public BTreeIterator(BTreeNode container)
    {
      this.container=container;  
    }
//   public BTreeIterator(BTreeNodeWrapper containerWrapper,int CID)
//    {
//        this.containerWrapper=containerWrapper;
//        this.container=containerWrapper.getCore();
//        this.CID=CID;
//        numberOfReadElements=0;
//        openPages=new ArrayList<>();
//        openPages.add(container);
//    }
    private void changeContainer(BTreeNode cont)
    {
        this.container.closeBTreeNode();//unpin last container;
        this.container=cont;
        logManager.LogManager.log(6,"Page:"+container.getBuf().getPageNumber());
        //this.containerWrapper.reset(cont);
        //openPages.add(cont);
        justChanged=true;
    }
    public void open() throws IOException
    {
         container.prepare();
        
    }
    public Record next() throws IOException, DBException
    {
        Record res=null;
               
        OutputWrapper<BTreeNode,Record> outputWrapper;
        if(justChanged)
        {
            open();
            justChanged=false;
        }
        outputWrapper=container.next();
        while(outputWrapper!=null)
        {
            res=outputWrapper.getOutput();        
             if(outputWrapper.getContainer()!=null)
            {  
               changeContainer(outputWrapper.getContainer());
               open();
               justChanged=false;
               outputWrapper=container.next();//THIS IS NOT TESTED YET, NECESSARY TO LOOK UP NEXT PAGE
            }    
            else
                break;
        }        
        if(res!=null)
            numberOfReadElements++;
        //This is not needed:
//        if(outputWrapper==null)        
//            res=null;
        
        return res;
           
    }
    
}

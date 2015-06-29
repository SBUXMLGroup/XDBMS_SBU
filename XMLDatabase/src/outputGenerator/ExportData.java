/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and exporter the template in the editor.
 */

package outputGenerator;

import bufferManager.XBufferChannel;
import bufferManager.InternalBuffer;
import indexManager.ElementIndex;
import indexManager.IndexManager;
import indexManager.Iterators.ElementIterator;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Stack;
import diskManager.DiskManager;
import indexManager.Iterators.BTreeIterator;
import indexManager.BTreeIndex;
import indexManager.util.Record;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.nio.channels.FileChannel;
import xmlProcessor.DeweyID;

/**
 *
 * @author Micosoft
 */
public class ExportData 
{
    private IndexManager indxMgr;
    private ElementIterator inputStream;
    private DiskManager diskmgr;
    private BTreeIterator btStream;
    private BTreeIndex btIndx;
    private Stack<Element> branch;
    private String indexName;
    private int recCounter=0;
    private boolean outterReached=false;
    String outFilename="d:/DBExport.txt";
//    File aFile = new File(outFilename); // File object for the file path       
//    FileOutputStream outputFileStream;       
//    FileChannel outChannel;
   PrintWriter printWriter;
    public ExportData(String indexName) throws IOException
    {
        diskmgr=DiskManager.Instance;
        indxMgr=IndexManager.Instance;
        branch=new Stack<>();
//        aFile.createNewFile();
//        outputFileStream = new FileOutputStream(aFile);
//        outChannel = outputFileStream.getChannel();
        printWriter=new PrintWriter(outFilename);
        
        this.indexName=indexName;
    }
    public void exporter(boolean dispDID) throws IOException,Exception
    {
       if(dispDID)
           exporterDID();
       else
           exporter();
    }
    public void exporter() throws IOException,Exception
    {
        Record nextRec;
        DeweyID nextDID = null;
        Element current = null;
        byte[] qnSeq;
        byte[] attrSeq;
        byte[] valueSeq;
        String qName;
        String curQName;
        byte[] curSeq;
        DeweyID curDID;
        Element nextElem = null;
        Attribute attrib = new Attribute();
        int dif;
       // ElementIndex input=indxMgr.openElementIndex(indexName);
       // XBufferChannel xChannel=input.export();
       // diskmgr.export(xChannel.getPageNumber(), xChannel.getInBuf()); 
        btIndx=indxMgr.openBTreeIndex(indexName);
        //*************
        btStream=btIndx.getStream();
        nextRec=btStream.next();
        while(nextRec!=null )
        {
            //**************************************************************************************************
           
//            if(nextElem!=null)
//            {
//                    //First we enter previous element(it is like inserting element at end element event)
//                 
//        }
            //************************************************************************************************
            nextDID=nextRec.getDeweyID();
            qName=nextRec.getQname();
            if(!nextDID.isRootDID() && nextDID.getFirsttDiv()==1)
            {
                if(nextDID.getAttrDiv()==3)
                 {  
                     //THIS is value
//                     attrib.setValue(qName);
                     printWriter.print("\""+qName+"\"");
                 }
            
                else
                {
                    //This is content
                    if(!outterReached)
                    {   printWriter.print(">"); //if there was no content,closes openning tag
                        outterReached=true;
                    }
                    //nextElem.setContent(qName);
                    printWriter.print(qName);
                }
            }
            else
            {
                if(nextDID.getAttrDiv()==3)
                {   //attr
//                    attrib=new Attribute(qName);
//                    nextElem.addAttribute(attrib);
                    printWriter.print(" ");//                                                         
                    printWriter.print(qName);                               
                    printWriter.print("=");                                                                   
                      
                }
                
                else
                { 
                    //element                  
                    nextElem=new Element(qName,nextDID,null);
                    if(branch.isEmpty())
                    {   
                        branch.push(nextElem);
                        logManager.LogManager.log(5,"Push: "+ nextElem.getDeweyID().toString());
                        printWriter.print("<");
                        printWriter.print(qName);
                        
                    }
                    else
                    {   
                        current=branch.pop();
                        logManager.LogManager.log(5,"Pop: "+ current.getDeweyID().toString());
                        curQName=current.getQName();
                        curDID=current.getDeweyID();
                        if(curDID.getLevel()==nextDID.getLevel() && curDID.isSiblingOf(nextDID))
                        {
                              //sibling
                              if(!outterReached)
                                printWriter.print(">"); //if there was no content,closes openning tag of previous elem
                              else
                                  outterReached=false;//for nextElement
                              //branch.pop();//current
                              branch.push(nextElem);
                              logManager.LogManager.log(5,"Push: "+ nextElem.getDeweyID().toString());
                              for(int i=0;i<branch.size()*3 ;i++)
                                  printWriter.print(' ');
                              printWriter.print("<");
                              printWriter.print("/");                              
                              printWriter.print(curQName);//cur                              
                              printWriter.print(">");
                              printWriter.println();
                              for(int i=0;i<branch.size()*3 ;i++)
                                  printWriter.print(' ');
                              printWriter.print("<");
                              printWriter.print(nextElem.getQName()); 
                             
                        }   
                        else if(nextDID.getLevel()> curDID.getLevel() && curDID.isParentOf(nextDID))
                        {
                            //its child
                            branch.push(current);
                            logManager.LogManager.log(5,"Push: "+ current.getDeweyID().toString());
                            branch.push(nextElem);  
                            logManager.LogManager.log(5,"Push: "+ nextElem.getDeweyID().toString());
                            if(!outterReached)
                                printWriter.print(">"); //if there was no content,closes openning tag
                             printWriter.println();
                             for(int i=0;i<branch.size()*3 ;i++)
                                  printWriter.print(' ');
                            printWriter.print("<");
                            printWriter.print(nextElem.getQName()); //next
                                                      
                        }
                        else if(nextDID.getLevel()< curDID.getLevel()) //next branch
                        {
                            dif=curDID.getLevel()- nextDID.getLevel();
                            if(!outterReached)
                                printWriter.print(">"); //if there was no content,closes openning tag
                            for(int i=0;i<branch.size()*3 ;i++)
                                  printWriter.print(' ');
                            printWriter.print("<");
                            printWriter.print("/");
                            printWriter.print(curQName);                                                     
                            printWriter.print(">");
                            printWriter.println();
                            while(dif>0)
                            {
                                current=branch.pop();
                                logManager.LogManager.log(5,"Pop: "+ current.getDeweyID().toString());
                                curQName=current.getQName();  
                                for(int i=0;i<(branch.size()+1)*3 ;i++)
                                  printWriter.print(' ');
                                printWriter.print("<");
                                printWriter.print("/");
                                printWriter.print(curQName);
                                printWriter.print(">");
                                printWriter.println();
                                dif--;
                            }
                            branch.push(nextElem);
                            logManager.LogManager.log(5,"Push: "+ nextElem.getDeweyID().toString());
                            printWriter.println();
                            for(int i=0;i<branch.size()*3 ;i++)
                                  printWriter.print(' ');
                            printWriter.print("<");
                            printWriter.print(qName); //next
                            
                        } 
                        else
                            throw new Exception("DeweyID produced is not in the expected order");
                      } 
            }
                    
                }
            
     
           logManager.LogManager.log(6,"Record: "+recCounter++);
           logManager.LogManager.log(6,nextDID.toString());
           logManager.LogManager.log(6,qName);
           nextRec=btStream.next(); 
        }        
            
        while(branch.isEmpty()==false)
            {
                current=branch.pop();
                logManager.LogManager.log(5,"Pop: "+ current.getDeweyID().toString());
                curQName=current.getQName();
                if(!outterReached)
                    printWriter.print(">"); //if there was no content,closes openning tag
                outterReached=true;
                printWriter.print("<");
                printWriter.print("/");
                printWriter.print(curQName);
                printWriter.print(">");
                printWriter.println();
            }
            
        
    }
    
    public void exporterDID() throws IOException,Exception
    {
        Record nextRec;
        DeweyID nextDID = null;
        Element current = null;
        byte[] qnSeq;
        byte[] attrSeq;
        byte[] valueSeq;
        String qName;
        String curQName;
        byte[] curSeq;
        DeweyID curDID;
        Element nextElem = null;
        Attribute attrib = new Attribute();
        int dif;
       // ElementIndex input=indxMgr.openElementIndex(indexName);
       // XBufferChannel xChannel=input.export();
       // diskmgr.export(xChannel.getPageNumber(), xChannel.getInBuf()); 
        btIndx=indxMgr.openBTreeIndex(indexName);
        //*************
        btStream=btIndx.getStream();
        nextRec=btStream.next();
        while(nextRec!=null )
        {
            //**************************************************************************************************
           
//            if(nextElem!=null)
//            {
//                    //First we enter previous element(it is like inserting element at end element event)
//                 
//        }
            //************************************************************************************************
            nextDID=nextRec.getDeweyID();
            qName=nextRec.getQname();
            if(!nextDID.isRootDID() && nextDID.getFirsttDiv()==1)
            {
                if(nextDID.getAttrDiv()==3)
                 {  
                     //THIS is value
//                     attrib.setValue(qName);
                     printWriter.print("\""+qName+"\"");
                 }
            
                else
                {
                    //This is content
                    if(!outterReached)
                    {   printWriter.print(">"); //if there was no content,closes openning tag
                        outterReached=true;
                    }
                    //nextElem.setContent(qName);
                    printWriter.print(qName);
                }
            }
            else
            {
                if(nextDID.getAttrDiv()==3)
                {   //attr
//                    attrib=new Attribute(qName);
//                    nextElem.addAttribute(attrib);
                    printWriter.print(" ");//                                                         
                    printWriter.print(qName);                               
                    printWriter.print("=");                                                                   
                      
                }
                
                else
                { 
                    //element                  
                    nextElem=new Element(qName,nextDID,null);
                    if(branch.isEmpty())
                    {   
                        branch.push(nextElem);
                        logManager.LogManager.log(5,"Push: "+ nextElem.getDeweyID().toString());
                        printWriter.print("<");
                        printWriter.print(qName);
                        
                        /////ADD DEWEYID:
                        printWriter.print(" ");//                                                         
                        printWriter.print("DID");                               
                        printWriter.print("=");
                        printWriter.print(nextDID.getCID()+": "+nextDID.toString());
                        
                    }
                    else
                    {   
                        current=branch.pop();
                        logManager.LogManager.log(5,"Pop: "+ current.getDeweyID().toString());
                        curQName=current.getQName();
                        curDID=current.getDeweyID();
                        if(curDID.getLevel()==nextDID.getLevel() && curDID.isSiblingOf(nextDID))
                        {
                              //sibling
                              if(!outterReached)
                                printWriter.print(">"); //if there was no content,closes openning tag of previous elem
                              else
                                  outterReached=false;//for nextElement
                              //branch.pop();//current
                              branch.push(nextElem);
                              logManager.LogManager.log(5,"Push: "+ nextElem.getDeweyID().toString());
                              for(int i=0;i<branch.size()*3 ;i++)
                                  printWriter.print(' ');
                              printWriter.print("<");
                              printWriter.print("/");                              
                              printWriter.print(curQName);//cur                              
                              printWriter.print(">");
                              printWriter.println();
                              for(int i=0;i<branch.size()*3 ;i++)
                                  printWriter.print(' ');
                              printWriter.print("<");
                              printWriter.print(nextElem.getQName()); 
                              /////ADD DEWEYID:
                                printWriter.print(" ");                                                        
                                printWriter.print("DID");                               
                                printWriter.print("=");
                                printWriter.print(nextDID.getCID()+": "+nextDID.toString());
                      
                        }   
                        else if(nextDID.getLevel()> curDID.getLevel() && curDID.isParentOf(nextDID))
                        {
                            //its child
                            branch.push(current);
                            logManager.LogManager.log(5,"Push: "+ current.getDeweyID().toString());
                            branch.push(nextElem);  
                            logManager.LogManager.log(5,"Push: "+ nextElem.getDeweyID().toString());
                            if(!outterReached)
                                printWriter.print(">"); //if there was no content,closes openning tag
                             printWriter.println();
                             for(int i=0;i<branch.size()*3 ;i++)
                                  printWriter.print(' ');
                            printWriter.print("<");
                            printWriter.print(nextElem.getQName()); //next
                            
                            /////ADD DEWEYID:
                        printWriter.print(" ");//                                                         
                        printWriter.print("DID");                               
                        printWriter.print("=");
                        printWriter.print(nextDID.getCID()+": "+nextDID.toString());
//                            
                        }
                        else if(nextDID.getLevel()< curDID.getLevel()) //next branch
                        {
                            dif=curDID.getLevel()- nextDID.getLevel();
                            if(!outterReached)
                                printWriter.print(">"); //if there was no content,closes openning tag
                            for(int i=0;i<branch.size()*3 ;i++)
                                  printWriter.print(' ');
                            printWriter.print("<");
                            printWriter.print("/");
                            printWriter.print(curQName);                                                     
                            printWriter.print(">");
                            printWriter.println();
                            while(dif>0)
                            {
                                current=branch.pop();
                                logManager.LogManager.log(5,"Pop: "+ current.getDeweyID().toString());
                                curQName=current.getQName();  
                                for(int i=0;i<(branch.size()+1)*3 ;i++)
                                  printWriter.print(' ');
                                printWriter.print("<");
                                printWriter.print("/");
                                printWriter.print(curQName);
                                printWriter.print(">");
                                printWriter.println();
                                dif--;
                            }
                            branch.push(nextElem);
                            logManager.LogManager.log(5,"Push: "+ nextElem.getDeweyID().toString());
                            printWriter.println();
                            for(int i=0;i<branch.size()*3 ;i++)
                                  printWriter.print(' ');
                            printWriter.print("<");
                            printWriter.print(nextElem.getQName()); //next
                            /////ADD DEWEYID:
                            printWriter.print(" ");//                                                         
                            printWriter.print("DID");                               
                            printWriter.print("=");
                            printWriter.print(nextDID.getCID()+": "+nextDID.toString());

                        } 
                        else
                            throw new Exception("DeweyID produced is not in the expected order");
                      } 
            }
                    
                }
            
     
           logManager.LogManager.log(6,"Record: "+recCounter++);
           logManager.LogManager.log(5,nextDID.toString());
           nextRec=btStream.next(); 
        }        
            
        while(branch.isEmpty()==false)
            {
                current=branch.pop();
                logManager.LogManager.log(5,"Pop: "+ current.getDeweyID().toString());
                curQName=current.getQName();
                if(!outterReached)
                    printWriter.print(">"); //if there was no content,closes openning tag
                outterReached=true;
                printWriter.print("<");
                printWriter.print("/");
                printWriter.print(curQName);
                printWriter.print(">");
                printWriter.println();
            }
            
        
    }
    public void close() throws IOException
    {
        //here we shouldnt write rootPointer so we dont need to close.
//        indxMgr.closeIndexes(indexName);
         System.out.println("Done");               
         printWriter.close();
        
    }
}
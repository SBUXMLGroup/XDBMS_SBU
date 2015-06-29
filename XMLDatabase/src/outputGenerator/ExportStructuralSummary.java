//
//package outputGenerator;
//
//import diskManager.DiskManager;
//import indexManager.BTreeIndex;
//import indexManager.IndexManager;
//import indexManager.Iterators.StructuralSummaryIterator;
//import indexManager.Iterators.ElementIterator;
//import indexManager.StructuralSummaryIndex;
//import indexManager.StructuralSummaryNode;
//import indexManager.util.Record;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.Stack;
//import xmlProcessor.DeweyID;
//
//
//public class ExportStructuralSummary 
//{
//    private IndexManager indxMgr;
//    private ElementIterator inputStream;
//    private DiskManager diskmgr;
//    private StructuralSummaryIterator ssStream;
//    private StructuralSummaryIndex ssIndx;
//    private Stack<Element> branch;
//    private String indexName;
//    private int recCounter=0;
//    private boolean outterReached=false;
//    String outFilename="d:/SSExport.txt";
//    PrintWriter printWriter;
//    public ExportStructuralSummary(String indexName) throws IOException
//    {
//        diskmgr=DiskManager.Instance;
//        indxMgr=IndexManager.Instance;
//        branch=new Stack<>();
//        printWriter=new PrintWriter(outFilename);
//        this.indexName=indexName;
//    }
//    public void open() throws IOException,Exception
//    {
//        Record nextRec;
//        DeweyID nextDID = null;
//        Element current = null;
//        byte[] qnSeq;
//        byte[] attrSeq;
//        byte[] valueSeq;
//        String qName;
//        String curQName;
//        byte[] curSeq;
//        DeweyID curDID;
//        Element nextElem = null;
//        Attribute attrib = new Attribute();
//        int dif;
//        
//        ssIndx=indxMgr.openSSIndex(indexName);
//        //*************
//        ssStream=ssIndx.getStream();
//        ssNode=ssStream.next();
//       if(ssNode!=null && ssNode.getCID()==1)
//       {
//           ssRoot=new StructuralSummaryNode();//we can replace these two lines with one instruction
//           setUPRoot(ssNode.getCID(), ssNode.getQName(), ssNode.getParentCID(),1);
//       }
//       int counter=0;
//       do //chon y bar ghablan next zadim,moshkeli nis.
//       {
//           ssNode=ssStream.next();
//        
//        while(nextRec!=null )
//        {
//           //***********************************************
//            nextDID=nextRec.getDeweyID();
//            qName=nextRec.getQname();
//            if(!nextDID.isRootDID() && nextDID.getFirsttDiv()==1)
//            {
//                if(nextDID.getAttrDiv()==3)
//                 {  
//                     //THIS is value
////                     attrib.setValue(qName);
//                     printWriter.print("\""+qName+"\"");
//                 }
//            
//                else
//                {
//                    //This is content
//                    if(!outterReached)
//                    {   printWriter.print(">"); //if there was no content,closes openning tag
//                        outterReached=true;
//                    }
//                    //nextElem.setContent(qName);
//                    printWriter.print(qName);
//                }
//            }
//            else
//            {
//                if(nextDID.getAttrDiv()==3)
//                {   //attr
////                    attrib=new Attribute(qName);
////                    nextElem.addAttribute(attrib);
//                    printWriter.print(" ");//                                                         
//                    printWriter.print(qName);                               
//                    printWriter.print("=");                                                                   
//                      
//                }
//                
//                else
//                { 
//                    //element                  
//                    nextElem=new Element(qName,nextDID,null);
//                    if(branch.isEmpty())
//                    {   
//                        branch.push(nextElem);
//                        logManager.LogManager.log(5,"Push: "+ nextElem.getDeweyID().toString());
//                        printWriter.print("<");
//                        printWriter.print(qName);                    
//                    }
//                    else
//                    {   
//                        current=branch.pop();
//                        logManager.LogManager.log(5,"Pop: "+ current.getDeweyID().toString());
//                        curQName=current.getQName();
//                        curDID=current.getDeweyID();
//                        if(curDID.getLevel()==nextDID.getLevel() && curDID.isSiblingOf(nextDID))
//                        {
//                              //sibling
//                              if(!outterReached)
//                                printWriter.print(">"); //if there was no content,closes openning tag of previous elem
//                              else
//                                  outterReached=false;//for nextElement
//                              //branch.pop();//current
//                              branch.push(nextElem);
//                              logManager.LogManager.log(5,"Push: "+ nextElem.getDeweyID().toString());
//                              printWriter.print("<");
//                              printWriter.print("/");                              
//                              printWriter.print(curQName);//cur                              
//                              printWriter.print(">");
//                              printWriter.println();
//                             
//                              printWriter.print("<");
//                              printWriter.print(nextElem.getQName());                          
//                      
//                        }   
//                        else if(nextDID.getLevel()> curDID.getLevel() && curDID.isParentOf(nextDID))
//                        {
//                            //its child
//                            branch.push(current);
//                            logManager.LogManager.log(5,"Push: "+ current.getDeweyID().toString());
//                            branch.push(nextElem);  
//                            logManager.LogManager.log(5,"Push: "+ nextElem.getDeweyID().toString());
//                            if(!outterReached)
//                                printWriter.print(">"); //if there was no content,closes openning tag
//                            printWriter.print("<");
//                            printWriter.print(nextElem.getQName()); //next
////                            
//                        }
//                        else if(nextDID.getLevel()< curDID.getLevel()) //next branch
//                        {
//                            dif=curDID.getLevel()- nextDID.getLevel();
//                            if(!outterReached)
//                                printWriter.print(">"); //if there was no content,closes openning tag
//                            printWriter.print("<");
//                            printWriter.print("/");
//                            printWriter.print(curQName);                                                     
//                            printWriter.print(">");
//                            printWriter.println();
//                            while(dif>0)
//                            {
//                                current=branch.pop();
//                                logManager.LogManager.log(5,"Pop: "+ current.getDeweyID().toString());
//                                curQName=current.getQName();                                
//                                printWriter.print("<");
//                                printWriter.print("/");
//                                printWriter.print(curQName);
//                                printWriter.print(">");
//                                printWriter.println();
//                                dif--;
//                            }
//                            branch.push(nextElem);
//                            logManager.LogManager.log(5,"Push: "+ nextElem.getDeweyID().toString());
//                            printWriter.print("<");
//                            printWriter.print(nextElem.getQName()); //next
//                          
//                        } 
//                        else
//                            throw new Exception("DeweyID produced is not in the expected order");
//                      } 
//            }
//                    
//                }
//            
//     
//           logManager.LogManager.log(5,"Record: "+recCounter++);
//           logManager.LogManager.log(5,nextDID.toString());
//           nextRec=btStream.next(); 
//        }        
//            
//        while(branch.isEmpty()==false)
//            {
//                current=branch.pop();
//                logManager.LogManager.log(5,"Pop: "+ current.getDeweyID().toString());
//                curQName=current.getQName();
//                if(!outterReached)
//                    printWriter.print(">"); //if there was no content,closes openning tag
//                outterReached=true;
//                printWriter.print("<");
//                printWriter.print("/");
//                printWriter.print(curQName);
//                printWriter.print(">");
//                printWriter.println();
//            }
//            
//        
//       
//    public void close() throws IOException
//    {
//        //here we shouldnt write rootPointer so we dont need to close.
////        indxMgr.closeIndexes(indexName);
//         printWriter.close();
//        
//    }
//}
//}

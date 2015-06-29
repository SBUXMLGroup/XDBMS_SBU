

package xmlBuilder;

import documentManager.Document;
import java.util.Stack;
import logManager.LogManager;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import xmlProcessor.DeweyID;


public class ChildCounterHandler extends DefaultHandler
{
   
     Stack elements;
     Stack childNumerator; //for keeping curPart in every level
     //every element in the stack is the child of the node below it.
     //so top element is a leave,and the stack is holding a path from root to this leave.
     boolean lastMovement;//push:true,pop:false
     //String curID;
//     DeweyID curDeweyID;
//     DeweyID attrDeweyID;
     int curPart;
     int Children=0;
     boolean mark=false;
     int elemCounter=0;
     private Document doc;
     
     public ChildCounterHandler(Document doc)
     {
        this.doc=doc;
        elements=new Stack();
        childNumerator=new Stack();
        lastMovement=true;
        //curID="";
//        curDeweyID=new DeweyID();
//        attcrDeweyID=new DeweyID();
//        curDeweyID.toString();
        curPart=1;
        
     }   
     
     @Override
    public void startElement(String uri, String localName,
    	            String qName, Attributes attributes)
    	            throws SAXException 
    {
         	  LogManager.log(String.valueOf("Element: "+ ++elemCounter+" ,"+qName));
                  elements.push(uri);
                  //first time:we have dewey id=curID!
                  if(lastMovement)
                  {
                    // Children=1;//
                     //First child:
                    curPart=1;
                  //  curDeweyID.addDivision((byte)curPart);                    
                    childNumerator.push(curPart);
                    //if(childNumerator.size()==2)
                    
                    //if(Children>255)
                        
                    //curID=curID+String.valueOf(curPart);
                  }
                  else //New child not first:
                  {   
                      //Children++;
                      curPart=(int)childNumerator.pop();
                      curPart++;
                     // curDeweyID.addDivision((byte)curPart);
                      childNumerator.push(curPart);
                    //curID=curID.substring(0, curID.lastIndexOf(".")+1) +String.valueOf(curPart);
                  }
//                      String test=curDeweyID.toString();
//                  System.out.println(qName+" : "+test); 
//                  if(attributes.getLength()>0)
//              {
//
//                  attrDeweyID=new DeweyID(curDeweyID);//initialize
//                  attrDeweyID.addDivision((byte)attrDiv);//once for all attribs of the current element(in the following loop)
//                  for(int i=0;i<attributes.getLength();i++)
//                  {
//                      //this part should be revised+level computation in DEWEYID class
//
//                      attrDeweyID.addDivision((byte)(2*i+5));
//
//                      doc.addAttribute(attrDeweyID,attributes.getQName(i),parentCID);////?CID??????
//                     DeweyID valID=new DeweyID(attrDeweyID);
//                     valID.addDivision((byte)contentDiv);
//                     doc.addValue(valID,attributes.getValue(i),CID);//the attribs CID is its values ParentCID
//                     attrDeweyID.removeLastDivision();//prepare for next iteration
//                     attrDeweyID.setCID(-1);//invalidate CID
//                  }
//         try {
//             // byte[] curIDBytes=curID.getBytes();
//              doc.addElement(curDeweyID);
//         } catch (IOException ex) {
//             Logger.getLogger(SimpleHandler.class.getName()).log(Level.SEVERE, null, ex);
//         } catch (Exception ex) {
//             Logger.getLogger(SimpleHandler.class.getName()).log(Level.SEVERE, null, ex);
//         }
                      
                  //curID=curID.concat(".");
                  
                  lastMovement=true; //last movement is push.
                   
                  
    }
     @Override
    public void endElement(String uri, String localName,
    	                String qName)
    	                throws SAXException
    {
 
    	 // System.out.println("End Element :" + qName);
          elements.pop();          
//          curDeweyID.removeLastDivision();
          if(!lastMovement)
          {    
              Children=(int)childNumerator.pop();//(>,>): last curPart bayad remove shavad
               LogManager.log(0,"Children of "+qName+" are: "+Children);
          }
         //else ://curID=curID.substring(0, curID.lastIndexOf(".")-1);
                                  
          lastMovement=false;//Now last movement is pop.
 
    }
     @Override
    public void characters(char ch[], int start, int length)
    	            throws SAXException 
    {
 
    	// System.out.println(new String(ch, start, length));
         
     }
    
 }

    


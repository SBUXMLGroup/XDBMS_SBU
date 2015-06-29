
package xmlBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import xmlProcessor.DeweyID;
import indexManager.StructuralSummaryNode;
import indexManager.StructuralSummaryTree;
import logManager.LogManager;
import xmlProcessor.DBServer.DBException;



//import documentManager.Document;

public class NewPrime extends DefaultHandler 
{
      private Stack DeweyIDPath;
      private int elemCOunter=0;
      private Stack<Short> DivStack; //for keeping curPart in every level
      //every element in the stack is the child of the node below it.
      //so top exzlement is a leave,and the stack is holding a path from root to this leave.
      private boolean lastMovement;//push:true,pop:false
      private boolean rootVisited;
      //String curID;
      private DeweyID curDeweyID;
      private DeweyID attrDeweyID;
      private DeweyID contentDeweyID;
      private short DcurDiv;
      short attrDiv;
      short contentDiv;
      int parentCID;
      //***********************FOR SAVING MEMORY:******
      private boolean qNameFound= false; 
      private boolean valueFound=false;
      private int retrievedCID=-1;
      StructuralSummaryNode newBorn;
      StructuralSummaryNode newBornAttrib;
      //***********************************************
      /////////////////////////////
      private int CID;
      private StringBuilder contentBuilder;
      //private int CcurDiv;
      /////////////
      private Stack<StructuralSummaryNode> summaryPath;
      private StructuralSummaryNode ssParent;
      private StructuralSummaryTree ssTree;
//      private DocumentBuilderFactory docBuilderFactory;
//      private DocumentBuilder docBuilder;
//      private Document structSum;
      private TransformerFactory tFactory;
      private Transformer transformer;
//      private DOMSource source ;
//      private StreamResult result;
      private documentManager.Document doc;
      private logManager.LogManager logmgr;
      
    public NewPrime(documentManager.Document document )throws ParserConfigurationException, TransformerConfigurationException       
    {
              
            this.doc=document;
            DeweyIDPath=new Stack();
            DivStack=new Stack();
            lastMovement=true;   
            rootVisited=false;
            curDeweyID=new DeweyID();
            DcurDiv=1; 
            attrDiv=3;
            contentDiv=1;
            /////////////////
            CID=-1;
            parentCID=-1;
            //CcurDiv=1;
            /////////////////
            summaryPath = new Stack();
            contentBuilder=new StringBuilder();
//            docBuilderFactory = DocumentBuilderFactory.newInstance();
//            //throws exception:
//            docBuilder = docBuilderFactory.newDocumentBuilder();//Documentbuilder object encapsulates
            //a DOM parser.
//            structSum = docBuilder.newDocument();
            tFactory = TransformerFactory.newInstance();
            //throws:
            transformer = tFactory.newTransformer();
//            source = new DOMSource(structSum);
//            result = new StreamResult(System.out);
            ssTree=new StructuralSummaryTree();
            logmgr=new LogManager();
    }
    @Override
    //xml 1.0: elems and attribs have qNames
    //sax2 adds namespaces.with namespaces elems and attribs have 2 part names: URI + localName
    
    public void startElement(String uri, String localName,
                                String qName,Attributes attributes) 
                                throws SAXException
    {       
        ++elemCOunter;
//        if(elemCOunter%1000==0 || elemCOunter>=9584000)
        System.out.println("Element: " + String.valueOf(elemCOunter));
        qNameFound = false;
        int index;//it is USED!//4bytes

        // int retrievedCID=-1;
        retrievedCID = -1;
        if (contentBuilder.length()!=0) {            
            //***********************************
            if (lastMovement) 
            {
                 //First content after openning tag <elem1>cont1            
                DcurDiv =3;
                curDeweyID.addDivision((short) DcurDiv);
                contentDeweyID = new DeweyID(curDeweyID);
                contentDeweyID.addDivision((short) contentDiv);
                DivStack.push(DcurDiv);
           } 
            else //second or third content:<elem1> cont1<elem2/>cont2 <elem3>cont3 </elem1>
           {
                DcurDiv = DivStack.pop();//remembers what DcurDiv was.
                DcurDiv += 2;
                curDeweyID.addDivision((short) DcurDiv);//virtual DID
                contentDeweyID = new DeweyID(curDeweyID);
                contentDeweyID.addDivision((short) contentDiv);
                DivStack.push(DcurDiv);
           }
            contentDeweyID.setCID(-2);           

            try {
                doc.addContent(contentDeweyID, contentBuilder,CID);
            } catch (IOException ex) {
                throw new SAXException(ex);
            } catch (Exception ex) {
                throw new SAXException(ex);
            }
            contentDeweyID.removeLastDivision();
            contentBuilder = new StringBuilder();
        }
        //******************************************************************************
        DeweyIDPath.push(qName);
        StructuralSummaryNode parent;
        //first time:we have deweyId=curID!
        if (!rootVisited) {
            DcurDiv = 1;
            curDeweyID.addDivision((short) DcurDiv);
            DivStack.push(DcurDiv);
        } else if (lastMovement) {
             //First child:
            //CHANGED:!!! x.1 and x.3 are reserved.
            DcurDiv = 5;
            curDeweyID.addDivision((short) DcurDiv);
            DivStack.push(DcurDiv);
        } else //New child not first:
        {
            DcurDiv = DivStack.pop();//remembers what DcurDiv was.
            DcurDiv += 2;
            curDeweyID.addDivision((short) DcurDiv);
            DivStack.push(DcurDiv);
        }
        lastMovement = true; //last movement is push.

        StructuralSummaryNode ssRoot;
        List<StructuralSummaryNode> PChildren;
        if (summaryPath.isEmpty())//OR : !rootVisited
        {
            CID = 1;
            curDeweyID.setCID(CID);
            ssTree = new StructuralSummaryTree();
            ssTree.setUPRoot(1, qName, -1, curDeweyID.getLevel());
            ssRoot = ssTree.getRoot();
            summaryPath.push(ssRoot);
            ssParent = ssRoot;//??? b che dardi mikhore?

        } else {
            parent = (StructuralSummaryNode) summaryPath.pop();
            summaryPath.push(parent);//we still need it!                
            PChildren = parent.getChildren();
            parentCID = parent.getCID();
            //WAIT:summaryPath.push(domElement);
            if (PChildren != null) {
                for (int i = 0; !qNameFound && i < PChildren.size(); i++) {
                    if (qName.equals((PChildren.get(i)).getQName()))//i want to ret CID //(StructuralSummaryNode):type cast
                    {
                        //retrieve
                        qNameFound = true;
                        index = i;
                        retrievedCID = (PChildren.get(i)).getCID();   //type cast:(StructuralSummaryNode)                                     
                        summaryPath.push(PChildren.get(i)); //(StructuralSummaryNode)
                        curDeweyID.setCID(retrievedCID);//********
                    }
                }
            }
            if (!qNameFound) {

                newBorn = new StructuralSummaryNode(qName);
                    //newBorn is appended when it's clear that no sibling with the same qName it has
                //generate CID
                newBorn.setParent(parent);//lazeme?
                newBorn.setLevel(curDeweyID.getLevel());
                CID++;
                //Store CID in Dom ELement
                newBorn.setCID(CID);
                //****???parentesh ina
                parent.appendChild(newBorn);
                summaryPath.push(newBorn);
                curDeweyID.setCID(CID);
            }
        }

        rootVisited = true;
        try {

            doc.addElement(curDeweyID, qName, parentCID, qNameFound);
            qNameFound = false;//unset for attribs part
            if (attributes.getLength() > 0) {

                attrDeweyID = new DeweyID(curDeweyID);//initialize
                attrDeweyID.addDivision((short) attrDiv);//once for all attribs of the current element(in the following loop)
                for (int i = 0; i < attributes.getLength(); i++) {
                    //this part should be revised+level computation in DEWEYID class
                    attrDeweyID.addDivision((short) (2 * i + 5));

                      //newParent(new born) does not have children yet
                    //for child :parent(retrieved).getChildren()
                    //if(child.getNodeType==2(attrib))
                    //check to see if attrib[j]'s qname is equall to them,
                    //if not,add attr[j] to children of parent                      
                    parent = (StructuralSummaryNode) summaryPath.pop();
                    summaryPath.push(parent);//we still need it!                       
                    PChildren = parent.getChildren();
                    parentCID = parent.getCID();
                    //WAIT:summaryPath.push(domElement);
                    if (PChildren != null) {
                        for (int j = 0; !qNameFound && j < PChildren.size(); j++) {
                            if (attributes.getQName(i).equals(((StructuralSummaryNode) PChildren.get(j)).getQName()))//i want to ret CID
                            {
                                //retrieve
                                qNameFound = true;
                                index = j;
                                retrievedCID = ((StructuralSummaryNode) PChildren.get(j)).getCID();
                                //summaryPath.push((StructuralSummaryNode) PChildren.get(j)); //MUST NOT PUSH ATTRs!
                                attrDeweyID.setCID(retrievedCID);//********
                                break;
                            }
                        }
                    }
                    if (!qNameFound) {
                        newBornAttrib = new StructuralSummaryNode(attributes.getQName(i));
                            //newBorn is appended when it's clear that no sibling with the same qName it has
                        //generate CID
                        newBornAttrib.setParent(parent);//lazeme?
                        newBornAttrib.setLevel(curDeweyID.getLevel());
                        CID++;
                        //Store CID in Dom ELement
                        newBornAttrib.setCID(CID);
                        parent.appendChild(newBornAttrib);
                      //  summaryPath.push(newBornAttrib);THIS MUST NOT BE DONE
                        attrDeweyID.setCID(CID);//********
                    }
                    //**********Disable Insert:
                    doc.addAttribute(attrDeweyID, attributes.getQName(i), parentCID, qNameFound);
                    qNameFound = false;
                    // *************************VALUES:
                    DeweyID valID = new DeweyID(attrDeweyID);
                    valID.addDivision((short) contentDiv);
                    doc.addValue(valID, attributes.getValue(i), CID);//the attribs CID is its values ParentCID
                    attrDeweyID.removeLastDivision();//prepare for next iteration
                    attrDeweyID.setCID(-1);//invalidate CID
                }

            }
        } catch (IOException ex) {
            //Logger.getLogger(NewHandler.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("IO exception happened due to addelement() or addAttribute()");
            throw new SAXException(ex);

        } catch (Exception ex) {
            //Logger.getLogger(NewHandler.class.getName()).log(Level.SEVERE, null, ex);
            throw new SAXException(ex);
        }

}
                    
      @Override
      public void characters(char ch[], int start, int length)
      {
          //we have assumed that no tag intervenes content like: <A>h g <B>c d </B> j k </A>
//          if(contentBuilder!=null)
//          {
              contentBuilder.append(ch);  
//          }
          
      }
      
      @Override
    public void endElement(String uri, String localName,String qName)throws SAXException 
    {
       
        //chera 2bar?
        if(contentBuilder.length()!=0)
          {
            if (lastMovement) 
            {
                 //First content after openning tag <elem1>cont1            
                DcurDiv =3;
                curDeweyID.addDivision((short) DcurDiv);
                contentDeweyID = new DeweyID(curDeweyID);
                contentDeweyID.addDivision((short) contentDiv);
                DivStack.push(DcurDiv);
           } 
            else //last content:<elem1> cont1<elem2/>cont2 <elem3>cont(n) </elem1>
           {
                DcurDiv = DivStack.pop();//remembers what DcurDiv was.
                DcurDiv += 2;
                curDeweyID.addDivision((short) DcurDiv);//virtual DID
                contentDeweyID = new DeweyID(curDeweyID);
                contentDeweyID.addDivision((short) contentDiv);
                DivStack.push(DcurDiv);
           }        
            
              contentDeweyID.setCID(-2);
                try 
                {
                    doc.addContent(contentDeweyID,contentBuilder,CID);//pas 0 chi?
                } 
                catch (IOException ex) {
                    throw new SAXException(ex);
                } catch (Exception ex) {
                    throw new SAXException(ex);
                }
              contentDeweyID.removeLastDivision();
              contentBuilder=new StringBuilder();
          }
         DeweyIDPath.pop();          
         curDeweyID.removeLastDivision();
          if(!lastMovement)
              DivStack.pop();//(>,>): last curPart bayad remove shavad          
                                         
          lastMovement=false;//Now last movement is pop.
         curDeweyID.setCID(-1);//unset CID
       
         summaryPath.pop();
         

    }
    

    
      @Override
       public void endDocument() throws SAXException 
       {
           System.out.println(String.valueOf(elemCOunter)+"elements got inserted.");
          try {
              doc.endOfDocSignal();
          } catch (IOException ex) {
              throw new SAXException(ex);
          } catch (DBException ex) {
              throw new SAXException(ex);
          }
       }
   
}

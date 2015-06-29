//
//package xmlBuilder;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Stack;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import javax.xml.parsers.ParserConfigurationException;
//import javax.xml.transform.Transformer;
//import javax.xml.transform.TransformerConfigurationException;
//import javax.xml.transform.TransformerFactory;
//import javax.xml.transform.dom.DOMSource;
//import javax.xml.transform.stream.StreamResult;
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;
//import org.xml.sax.Attributes;
//import org.xml.sax.SAXException;
//import org.xml.sax.helpers.DefaultHandler;
//import xmlProcessor.DeweyID;
////import documentManager.Document;
//
//public class Handler extends DefaultHandler 
//{
//      private Stack DeweyIDPath;
//      private Stack DchildNumerator; //for keeping curPart in every level
//      //every element in the stack is the child of the node below it.
//      //so top exzlement is a leave,and the stack is holding a path from root to this leave.
//      private boolean lastMovement;//push:true,pop:false
//      //String curID;
//      private DeweyID curDeweyID;
//      private int DcurDiv; 
//      /////////////////////////////
//      private int CID;
//      //private int CcurDiv;
//      /////////////
//      
//      private Stack domElements;
//      private Element ssParent;
//      private DocumentBuilderFactory docBuilderFactory;
//      private DocumentBuilder docBuilder;
//      private Document structSum;
//      private TransformerFactory tFactory;
//      private Transformer transformer;
//      private DOMSource source ;
//      private StreamResult result;
//      private documentManager.Document doc;
//      
//    public Handler(documentManager.Document document )throws ParserConfigurationException, TransformerConfigurationException       
//    {
//              
//            this.doc=document;
//            DeweyIDPath=new Stack();
//            DchildNumerator=new Stack();
//            lastMovement=true;           
//            curDeweyID=new DeweyID();
//            DcurDiv=1;
//            /////////////////
//            CID=0;
//            //CcurDiv=1;
//            /////////////////
//            domElements = new Stack();
//            docBuilderFactory = DocumentBuilderFactory.newInstance();
//            //throws exception:
//            docBuilder = docBuilderFactory.newDocumentBuilder();//Documentbuilder object encapsulates
//            //a DOM parser.
//            structSum = docBuilder.newDocument();
//            tFactory = TransformerFactory.newInstance();
//            //throws:
//            transformer = tFactory.newTransformer();
//            source = new DOMSource(structSum);
//            result = new StreamResult(System.out);
//            
//    }
//    @Override
//    //xml 1.0: elems and attribs have qNames
//    //sax2 adds namespaces.with namespaces elems and attribs have 2 part names: URI + localName
//    
//    public void startElement(String uri, String localName,
//                                String qName,Attributes attributes)
//                                throws SAXException 
//    {
//
//          boolean qNameFound= false;  
//          int index;
//          String CIDAttrib;
//          DeweyIDPath.push(uri);
//          //first time:we have deweyId=curID!
//          if(lastMovement)
//          {
//             //First child:
//              //CHANGED:!!! x.1 and x.3 are reserved.
//            DcurDiv=5; 
//            curDeweyID.addDivision((byte)DcurDiv);
//            DchildNumerator.push(DcurDiv);
//          }
//          else //New child not first:
//          {   
//              DcurDiv=(int)DchildNumerator.pop();//remembers what DcurDiv was.
//              DcurDiv+=2;
//              curDeweyID.addDivision((byte)DcurDiv);
//              DchildNumerator.push(DcurDiv);
//          }                
//        
//            lastMovement=true; //last movement is push.
//              ////////////////////                  
//            Element domElement;
//            Element rootDomElement;
//            NodeList PChildren;
//            if (domElements.isEmpty())
//            {
//                CID=1;
//                rootDomElement = structSum.createElement(qName);
//                rootDomElement.setAttribute("CID",Integer.toString(CID));                
//                structSum.appendChild(rootDomElement);
//                domElements.push(rootDomElement);
//                ssParent = rootDomElement;
//                
//            } 
//            else 
//            {
//                //Element parent = (Element) domElements.lastElement();
//                Element parent=(Element)domElements.pop();
//                domElement =structSum.createElement(qName);
//                PChildren =parent.getChildNodes();
//                List<Node> PChildrenList=new ArrayList<Node>();
//                PChildrenList=(List)PChildren;
//                //WAIT:domElements.push(domElement);
//                for(int i=0;i<PChildrenList.size();i++)
//                {
//                    if(qName==PChildrenList.get(i).getNodeName())//i want to ret CID
//                    {
//                        qNameFound=true;
//                        index=i;
//                        Element temp=(Element)PChildrenList.get(i);
//                        CIDAttrib=temp.getAttribute("CID");
//                        CID=Integer.parseInt(CIDAttrib);                      
//                        //retrieve
//                    }
//                        
//                }
//                if (!qNameFound) 
//                {
//                                   
//                   //generate CID
//                   Element lastChild=(Element)parent.getLastChild();
//                   CIDAttrib=lastChild.getAttribute("CID");
//                   CID=Integer.parseInt(CIDAttrib);
//                   CID++;
//                                       
//                    //Store CID in Dom ELement
//                    domElement.setAttribute("CID", Integer.toString(CID));
//                    //should somehow add the cid to its node,should overload toString()
//                    //???ino k nemikhad na?
//                    //structSum.appendChild(domElement);//chi kar konAM k branch bzane?
//                    //add node to dom: 
//                   parent.appendChild(domElement);  
//        
//                }
//                domElements.push(domElement);
//            }
//            try 
//         {
//             // byte[] curIDBytes=curID.getBytes();
//              doc.addElement(CID,curDeweyID,qName);
//              
//         } catch (IOException ex) 
//         {
//             Logger.getLogger(SimpleHandler.class.getName()).log(Level.SEVERE, null, ex);
//         } catch (Exception ex) {
//             Logger.getLogger(SimpleHandler.class.getName()).log(Level.SEVERE, null, ex);
//         }
//    }
//      @Override
//      public void characters(char ch[], int start, int length){}
//      
//      @Override
//    public void endElement(String uri, String localName,String qName)throws SAXException 
//    {
//         DeweyIDPath.pop();          
//         curDeweyID.removeLastDivision();
//          if(!lastMovement)
//              DchildNumerator.pop();//(>,>): last curPart bayad remove shavad
//          
//         //else ://curID=curID.substring(0, curID.lastIndexOf(".")-1);
//                                  
//          lastMovement=false;//Now last movement is pop.
//          
//         domElements.pop();
//    }
//
//   
//}


package xmlBuilder;


import java.io.StringReader;
import java.io.File;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import documentManager.Document;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import javax.xml.transform.TransformerConfigurationException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class XmlParser 
{
    SAXParserFactory factory; //some factory class that produces saxParser objects :)
    InputSource is;
    NewHandler handler;
    //Handler ssHandler;
    public static XmlParser Instance=new XmlParser();
  private XmlParser()
  {
  }
  public void parseXmlString(String xmlText,Document doc) throws ParserConfigurationException, TransformerConfigurationException
  {
      // parse
      
        try
        {       
        	XMLReader saxParser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
        	saxParser.setFeature("http://xml.org/sax/features/validation", false); 
        	saxParser.setFeature("http://xml.org/sax/features/namespaces", false);
                
            saxParser.setContentHandler(new NewHandler(doc));
            
            //FROM FILE:
//             saxParser.parse (new InputSource(new InputStreamReader(new FileInputStream(new File("D:\\dataset4.xml")))));
//               saxParser.parse (new InputSource(new InputStreamReader(new FileInputStream(new File("D:\\Datasets\\ds2.xml")))));  
//                saxParser.parse (new InputSource(new InputStreamReader(new FileInputStream(new File("D:\\Datasets\\ds3.xml"))))); 
//            saxParser.parse (new InputSource(new InputStreamReader(new FileInputStream(new File("G:\\Project\\Benchmarks\\DBLP\\dblp.xml")))));
//               saxParser.parse (new InputSource(new InputStreamReader(new FileInputStream(new File("G:\\Project\\Benchmarks\\XMark\\m1.xml")))));//100kb
//            saxParser.parse (new InputSource(new InputStreamReader(new FileInputStream(new File("G:\\Project\\Benchmarks\\XMark\\m6.xml")))));//700meg
//            saxParser.parse (new InputSource(new InputStreamReader(new FileInputStream(new File("C:\\Users\\Micosoft\\Documents\\NetBeansProjects\\XMLDatabase\\dblp1.xml")))));
//            saxParser.parse (new InputSource(new InputStreamReader(new FileInputStream(new File("G:\\\\Project\\\\Benchmarks\\\\XMark\\\\m5.xml")))));//600meg
//            saxParser.parse (new InputSource(new InputStreamReader(new FileInputStream(new File("G:\\\\Project\\\\Benchmarks\\\\XMark\\\\XMark5Mirror.xml")))));//600meg
//             saxParser.parse (new InputSource(new InputStreamReader(new FileInputStream(new File("G:\\Project\\Benchmarks\\SwissProt\\SwissProt.xml")))));//112meg
              saxParser.parse (new InputSource(new InputStreamReader(new FileInputStream(new File("G:\\Project\\Benchmarks\\Nasa\\nasa.xml")))));//24meg
//            saxParser.parse (new InputSource(new InputStreamReader(new FileInputStream(new File("G:\\\\Project\\\\Benchmarks\\\\XMark\\\\mark1.xml")))));//11meg
//            saxParser.parse (new InputSource(new InputStreamReader(new FileInputStream(new File("G:\\\\Project\\\\Benchmarks\\\\XMark\\\\XmPrime1.xml")))));//11meg
              //FROM STRING READER:
//            StringReader stringReader = new StringReader(xmlText);
//            saxParser.parse (new InputSource(stringReader));
           // new InputSource(inputStream);//
//            new InputSource(new InputStreamReader(new FileInputStream(new File("c:..."))));
            System.out.println("Done");
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        }
//        factory = SAXParserFactory.newInstance();
//        is = new InputSource(new StringReader(xmlText));
//        handler=new NewHandler(doc); 
//        
//            
//        try 
//        {
//            SAXParser saxParser = factory.newSAXParser();
//                saxParser.parse(is, handler);
//        } 
//        catch (ParserConfigurationException ex)
//        {
//            Logger.getLogger(XmlParser.class.getName()).log(Level.SEVERE, null, ex);
//            System.out.println("ParserConfig error");
//        } 
//        catch (SAXException ex) 
//        {
//            Logger.getLogger(XmlParser.class.getName()).log(Level.SEVERE, null, ex);
//            System.out.println("SAXException : xml not well formed");
//        }
//        catch (IOException ex) 
//        {
//            Logger.getLogger(XmlParser.class.getName()).log(Level.SEVERE, null, ex);
//             System.out.println("IO error");
//        }
 }
//   public void parseXmlString(File xmlFile,Document doc) throws ParserConfigurationException, TransformerConfigurationException
//  {
//      // parse
//      
//        try
//        {
//        	XMLReader saxParser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
//        	saxParser.setFeature("http://xml.org/sax/features/validation", false); 
//        	saxParser.setFeature("http://xml.org/sax/features/namespaces", false);
//                StringReader stringReader = new StringReader(null);
//            saxParser.setContentHandler(new NewHandler(doc));
//            
//            saxParser.parse (new InputSource(stringReader));
//            saxParser.parse(new InputSource(stringReader));
//        }
//        catch (Exception e)
//        {
//        	e.printStackTrace();
//        }
//
// }
  


}
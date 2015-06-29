
package databaseManager;
import bufferManager.BufferManager;
import diskManager.DiskManager;
import indexManager.IndexManager;
import documentManager.DocumentManager;
import documentManager.Document;
import xmlBuilder.XmlParser;
import java.nio.Buffer;
import LogicalFileManager.LogicalFileManager;
import indexManager.BTreeIndex;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import xmlProcessor.DBServer.DBException;

public class DatabaseManager
{   
   // Buffer buf;
    //singleton:    
    public static DatabaseManager Instance = new DatabaseManager();
    
    /* in bara vaghti k parametr niaz dare v sazandeye y clase dg bayad initesh kone.
     * public static void CreateInstance()
    {
        if(Instance==null)
        {
            
        }
    }*/
           
    private DocumentManager docMgr=DocumentManager.Instance;
    private IndexManager indxMgr=IndexManager.Instance;
    //private bufferManager.BufferManager bufMgr = new BufferManager();
    
    //start up:buffrManager,...
    private DatabaseManager()
    {
        
        
    }
    public BufferManager getBufferManager()
    {
        return BufferManager.getInstance();
    }
    public DiskManager getDiskManager()
    {
        return DiskManager.Instance;
    }
    public DocumentManager getDocumentManager()
    {
        return docMgr;
    }
    public IndexManager getIndexManager()
    {
        return IndexManager.Instance;//b nazaram ziad lazem nis,chon ma tu har clasi mitunim
                                  //az in instance estefade konim.
    }
    public Document importDB(String DbName,String xmlText) throws IOException, ParserConfigurationException, TransformerConfigurationException, DBException//import(ame,file)
    {
        
        XmlParser parser=XmlParser.Instance;
        Document doc;
        LogicalFileManager logFileMgr=LogicalFileManager.Instance;
        logFileMgr.createPhysicalFile(DbName);
        //creates indexes on physical layer:
        doc=docMgr.createDoc(DbName);
//        try
//        {
            //parses doc and insers elements into indexes:
            parser.parseXmlString(xmlText,doc);
//        }
//        catch(Exception e)
//        {
//            System.out.println(e.getMessage());
//        }//bayad doc ro bhehs pas bdam ta tusho por kone.
        //khorujie parser void ast,engar k faghat method haye handler ra(start,end,...)ruye 
        //file ya input stream ejra mikonad
        return doc;    
    }
    public void exportDB(String DbName) throws IOException, DBException
    {
        BTreeIndex BIndx=indxMgr.openBTreeIndex(DbName);
        
      
    }
    {/* public Document importIndex(String DBName,int type)
    {
        Document doc;
      doc=docMgr.loadIndex(DBName,1);
        return doc;
    }*/
   /* public Document importDB(String DbName,String xmlText)//import(ame,file)
    {
        
        XmlParser parser=XmlParser.Instance;
        Document doc;
        doc=docMgr.createDoc(DbName);
        parser.parseXmlString(xmlText,doc); //bayad doc ro bhehs pas bdam ta tusho por kone.
        //khorujie parser void ast,engar k faghat method haye handler ra(start,end,...)ruye 
        //file ya input stream ejra mikonad
        
        return doc;    
    }*/}
    
    
    
}

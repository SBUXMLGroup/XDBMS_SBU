

package documentManager;

import java.io.IOException;
import xmlProcessor.DBServer.DBException;

public interface IDocumentManager 
{
    public Document createDoc(String DbName) throws IOException,DBException;   
   
}

package indexManager.Iterators;
import indexManager.StructuralSummaryIndex;
import indexManager.StructuralSummaryNode;
import xmlProcessor.DeweyID;
import java.io.IOException;
import xmlProcessor.DBServer.DBException;
public class StructuralSummaryIterator 
{
    StructuralSummaryIndex ssIndx;
    public StructuralSummaryIterator(StructuralSummaryIndex ssIndx)
    {
      this.ssIndx=ssIndx; 
      
    }
    public void open()
    {
        ssIndx.open();
    }
    public StructuralSummaryNode next() throws IOException, DBException
    {
        return ssIndx.next();
    }
    
}

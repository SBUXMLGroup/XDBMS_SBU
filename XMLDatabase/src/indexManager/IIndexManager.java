
package indexManager;
import java.io.IOException;
import xmlProcessor.DBServer.DBException;
import xmlProcessor.DeweyID;


public interface IIndexManager 
{
    public BTreeIndex createBTreeIndex(String indexName)throws IOException,DBException;
    public ElementIndex createElementIndex(String indexName)throws IOException,DBException;
    public StructuralSummaryIndex createSSIndex(String indexName) throws IOException,DBException;
    public BTreeIndex openBTreeIndex(String indexName)throws IOException,DBException;// haman
    public ElementIndex openElementIndex(String indexName) throws IOException,DBException; 
    public StructuralSummaryIndex openSSIndex(String indexName) throws IOException,DBException; 
    public void closeIndexes(String indexName) throws IOException,DBException;
    public int search(DeweyID key);
    public void insert(int key,int recAdrs);
    public void delete(int key,int recAdrs); 
    public void update(int key,int recAdrs);
}


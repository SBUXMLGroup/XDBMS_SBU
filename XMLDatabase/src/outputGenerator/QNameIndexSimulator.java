

package outputGenerator;
import java.util.ArrayList;
import xmlProcessor.DeweyID;
import indexManager.IndexManager;
import indexManager.StructuralSummaryIndex;
import indexManager.StructuralSummaryTree;
import indexManager.StructuralSummaryNode;
import indexManager.Iterators.ElementIterator;
import indexManager.ElementIndex;
import java.io.IOException;
import java.util.HashMap;
import xmlProcessor.DBServer.DBException;

public class QNameIndexSimulator 
{
    private StructuralSummaryTree ssTree;
    private IndexManager indxMgr;
    private StructuralSummaryIndex ssIndx;
    private ElementIndex elemIndx;
    private String indexName;
    private  ElementIterator elemIterator;
    
    public QNameIndexSimulator(String indexName) throws IOException, DBException
    {
        this.indexName=indexName;
        indxMgr=IndexManager.Instance;
        ssIndx=indxMgr.openSSIndex(indexName);
        ssTree=new StructuralSummaryTree(ssIndx);
        elemIndx=indxMgr.openElementIndex(indexName);
        
    }
    public ArrayList<DeweyID> simulate(String qName) throws IOException, DBException
    {
        ArrayList <StructuralSummaryNode> ssNodeList; 
        int generatorNum;
        HashMap<Integer,ArrayList<DeweyID>> streams = new HashMap<>();
        ArrayList<DeweyID> outputStream;
        ssTree.setRangeIDs();
        ssNodeList=ssTree.getNodeIndex(qName);
        generatorNum=ssNodeList.size();
        if(generatorNum==0)
        {
            System.err.println("No such qName entry found!");
            return null;
        }
                
        for(int i=0;i<generatorNum;i++)
        {
            elemIterator=elemIndx.getStream(ssNodeList.get(i).getCID());
            elemIterator.open();
            ArrayList<DeweyID> stream=new ArrayList<>();            
            DeweyID nextDID=elemIterator.next();
            while(nextDID!=null)
            {
                //if we need its CID we can add it too!
                stream.add(nextDID);
                nextDID=elemIterator.next();                
            }
            streams.put(i, stream);    
        }
        outputStream=streams.get(0);
        for(int i=1;i<generatorNum;i++)
        {
           outputStream= merge(outputStream,streams.get(i));
        }
        return outputStream;
       
    } 
    private ArrayList<DeweyID> merge(ArrayList<DeweyID> lStream,ArrayList<DeweyID> rStream)
    {
       ArrayList<DeweyID> result=new ArrayList<>();
       int i=0;
       int j=0;
       while(i<lStream.size() && j<rStream.size())
       {    if(lStream.get(i).compareTo(rStream.get(j))==-1)//less than
              result.add(lStream.get(i++));
           else if(lStream.get(i).compareTo(rStream.get(j))==1)
               result.add(rStream.get(j++));
       }
       for(;i<lStream.size();i++)
           result.add(lStream.get(i));
       for(;j<rStream.size();j++)
           result.add(rStream.get(j));
       return result;
    }
    public String toString(ArrayList<DeweyID> outputStream)
    {
        String result=new String();
        for(int i=0;i<outputStream.size();i++)
            result = result.concat(outputStream.get(i).toString()+"\n"); //+System.lineSeparator()
        return result;
    }
    public void close() throws IOException
    {
        //indxMgr.closeIndexes(indexName);
    }
}


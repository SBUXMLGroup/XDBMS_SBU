
package xmlProcessor.DBServer.operators;
import java.io.IOException;
import xmlProcessor.DBServer.utils.QueryTuple;
import xmlProcessor.DBServer.DBException;

public interface QueryOperator 
{
    public void open() throws DBException, IOException;
    public QueryTuple next() throws DBException, IOException;
    public void close()throws DBException;
}

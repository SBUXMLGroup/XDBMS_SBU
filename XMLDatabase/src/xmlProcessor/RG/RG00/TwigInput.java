
package xmlProcessor.RG.RG00;
//package xtc.server.xmlSvc.xqueryOperators.Twigs.RG.RG00;

//import xtc.server.DBException;
import java.io.IOException;
import xmlProcessor.DBServer.DBException;
import xmlProcessor.QTP.QTPNode;

public interface TwigInput 
{
	public TwigResult next() throws DBException,IOException;

	public void open() throws DBException,IOException;

	public void close() throws DBException,IOException;
	
	public QTPNode getRightLeaf();

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xmlProcessor.RG.RG01;

import java.io.IOException;
import xmlProcessor.DBServer.DBException;
import xmlProcessor.QTP.QTPNode;

public interface TwigInput 
{
	public TwigResult next() throws DBException, IOException;

	public void open() throws DBException,IOException;

	public void close() throws DBException;
	
	public QTPNode getRightLeaf();
	
	public long getIOTime();
	
	public long getNumberOfReadElements();


}


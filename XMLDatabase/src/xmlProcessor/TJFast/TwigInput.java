/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package xmlProcessor.TJFast;


import java.io.IOException;
import xmlProcessor.DBServer.DBException;
//import xtc.server.xmlSvc.xqueryOperators.Twigs.QTP.QTPNode;
import xmlProcessor.QTP.QTPNode;
public interface TwigInput 
{
	public TwigResult next() throws DBException,IOException;

	public void open() throws DBException;

	public void close() throws DBException;
	
	public QTPNode getRightLeaf();
}

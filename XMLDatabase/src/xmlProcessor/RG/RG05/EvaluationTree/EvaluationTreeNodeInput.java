/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xmlProcessor.RG.RG05.EvaluationTree;

import java.util.ArrayList;

import xmlProcessor.DBServer.DBException;
import java.io.IOException;
import xmlProcessor.DBServer.utils.TwinObject;
import xmlProcessor.DBServer.operators.twigs.QueryStatistics;
import xmlProcessor.QTP.QTPNode;
import xmlProcessor.RG.RG05.TwigResult;

/**
 * 
 * @author Izadi
 *
 */
public interface EvaluationTreeNodeInput extends QueryStatistics
{
	public void open() throws DBException,IOException;
	
	public TwigResult getHead();
	
	public void next() throws DBException,IOException;
	
	public boolean isFinished();
	
	//public void setParent(EvaluationTreeNode parent);

	public ArrayList<TwinObject<QTPNode, Integer>> getComps();
	
	public ArrayList<EvaluationTreeNodeInput> getChildren();
	
	public ArrayList<EvaluationTreeNodeInput> getNOTChildren();
	
	public QTPNode getMainNode();

	
	//public void advance() throws DBException;
	
	//public ArrayList<TwigResult> getSubSet();

}

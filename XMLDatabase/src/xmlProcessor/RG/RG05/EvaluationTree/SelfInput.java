/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xmlProcessor.RG.RG05.EvaluationTree;

import java.io.IOException;
import java.util.ArrayList;

import xmlProcessor.DBServer.DBException;
import xmlProcessor.DBServer.utils.TwinObject;
import xmlProcessor.QTP.QTPNode;
import xmlProcessor.RG.RG05.InputWrapper;
import xmlProcessor.RG.RG05.TwigResult;

public class SelfInput implements EvaluationTreeNodeInput
{

	private InputWrapper selfInput;
	
	public SelfInput(InputWrapper selfInput)
	{
		this.selfInput = selfInput;
	}
	@Override
	public ArrayList<EvaluationTreeNodeInput> getChildren()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<TwinObject<QTPNode, Integer>> getComps()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TwigResult getHead()
	{
		// TODO Auto-generated method stub
		return this.selfInput.getHead();
	}

	@Override
	public QTPNode getMainNode()
	{
		// TODO Auto-generated method stub
		return this.selfInput.getQTPNode();
	}

	@Override
	public ArrayList<EvaluationTreeNodeInput> getNOTChildren()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isFinished()
	{
		// TODO Auto-generated method stub
		return this.selfInput.isFinished();
	}

	@Override
	public void next() throws DBException, IOException
	{
		// TODO Auto-generated method stub
		this.selfInput.next();
	}

	@Override
	public void open() throws DBException, IOException
	{
		// TODO Auto-generated method stub
		this.selfInput.open();
	}
	public String toString()
	{
		return this.selfInput.getQTPNode().toString();
	}
	
	public InputWrapper getInputWrapper()
	{
		return this.selfInput;
	}
	
    public long getIOTime()
    {
    	return this.selfInput.getIOTime();
    }
	
	public long getNumberOfReadElements()
	{
		return this.selfInput.getNumberOfReadElements();
	}

}

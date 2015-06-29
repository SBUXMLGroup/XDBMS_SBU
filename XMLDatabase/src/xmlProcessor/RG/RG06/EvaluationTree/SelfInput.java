/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xmlProcessor.RG.RG06.EvaluationTree;

import java.util.ArrayList;
import java.util.Set;
import xmlProcessor.DBServer.DBException;
import indexManager.StructuralSummaryNode;
import java.io.IOException;
import xmlProcessor.DBServer.utils.TwinObject;
import xmlProcessor.QTP.QTPNode;
import xmlProcessor.RG.RG06.InputWrapper;
import xmlProcessor.RG.RG06.TwigResult;

public class SelfInput implements EvaluationTreeNodeInput
{

	private InputWrapper selfInputWrapper;
	
	public SelfInput(InputWrapper selfInput)
	{
            //    commonSettings.Settings.tCount++;
           // logManager.LogManager.log("Thread"+commonSettings.Settings.tCount );
		this.selfInputWrapper = selfInput;
	}
	@Override
	public ArrayList<EvaluationTreeNodeInput> getChildren()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<TwinObject<QTPNode, Set<StructuralSummaryNode>>> getComps()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TwigResult getHead()
	{
		// TODO Auto-generated method stub
		return this.selfInputWrapper.getHead();
	}

	@Override
	public QTPNode getMainNode()
	{
		// TODO Auto-generated method stub
		return this.selfInputWrapper.getQTPNode();
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
		return this.selfInputWrapper.isFinished();
	}

	@Override
	public void next() throws DBException, IOException
	{
            logManager.LogManager.log("SelfInput next()");
		// TODO Auto-generated method stub
		this.selfInputWrapper.next();
	}

	@Override
	public void open() throws DBException, IOException
	{
            logManager.LogManager.log("SelfInput Open()");
		// TODO Auto-generated method stub
		this.selfInputWrapper.open();
	}
	
	@Override
	public void close() throws DBException
	{
		this.selfInputWrapper.close();
	}
	
	public InputWrapper getInputWrapper()
	{
		return this.selfInputWrapper;
	}

	public long getIOTime()
	{
		return this.selfInputWrapper.getIOTime();
	}
	
	public long getNumberOfReadElements()
	{
		return this.selfInputWrapper.getNumberOfReadElements();
	}
        

}

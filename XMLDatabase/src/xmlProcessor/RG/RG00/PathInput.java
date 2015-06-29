package xmlProcessor.RG.RG00;
//package xtc.server.xmlSvc.xqueryOperators.Twigs.RG.RG00;

import java.io.IOException;
import java.util.ArrayList;

import xmlProcessor.DBServer.DBException;
import xmlProcessor.QTP.QTPNode;
import statistics.Monitor;
public class PathInput {
	private TwigInput input;
	private ArrayList<TwigResult> subSet;
	private TwigResult nextTuple;
	private boolean consumed = false;
	private TwigResult key;
	private boolean inputConsumed = false;
        private Monitor monitor=Monitor.getInstance();
	
	public PathInput(TwigInput input) {
		this.input = input;
		this.subSet = new ArrayList<TwigResult>();
	}
	
	public void open() throws DBException, IOException
        {
		input.open();
		inputNext();
	}
	
	private void inputNext() throws DBException, IOException 
        {
		this.nextTuple = input.next();
                monitor.addElementsRead();
		if (nextTuple == null) {
			if (inputConsumed) {
				consumed = true; 
			} else {
				this.inputConsumed = true;
			}
		}
	}
	
	public QTPNode getRightLeaf()
	{
		return input.getRightLeaf();
	}
	
	public boolean consumed() 
        {
		return this.consumed ;
	}
	

	public void close() throws DBException, IOException //ouldouz: throws DBException 
	{
		input.close();
	}
	
	
	public void advance(int level) throws DBException, IOException 
        {
		
		if (inputConsumed) { 
			consumed = true;
			return;
		}
		
		key = nextTuple;
		subSet.clear();
		
		while(!inputConsumed && (key.compareUpTo(level, nextTuple) == 0))
		{
			subSet.add(nextTuple);
			inputNext();
		}
	}
	
	public TwigResult key() 
	{
		return this.key;
	}
	
	public ArrayList<TwigResult> xProduct(PathInput right) throws DBException 
        {
		ArrayList<TwigResult> res = new ArrayList<TwigResult>(); 

		for (TwigResult leftTuple : this.subSet) 
		{
			for (TwigResult rightTuple : right.subSet) 
			{
				res.add(leftTuple.mergeWithLast(rightTuple));
			}
		}		
		return res;
	}	
}

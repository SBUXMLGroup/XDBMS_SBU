package indexManager.structuralSummaryMgr.twigStack;

import java.util.ArrayList;

import xmlProcessor.DBServer.DBException;

public class PathInput
{
	private TwigInput input;
	private ArrayList<TwigResult> subSet;
	private TwigResult nextTuple;
	private boolean consumed = false;
	private TwigResult key;
	private boolean inputConsumed = false;
	
	public PathInput(TwigInput input) 
        {
		this.input = input;
		this.subSet = new ArrayList<TwigResult>();
	}
	
	public void open() throws DBException 
        {
		input.open();
		inputNext();
	}
	
	private void inputNext() throws DBException 
        {
		this.nextTuple = input.next();
		if (nextTuple == null) {
			if (inputConsumed) {
				consumed = true; 
			} else {
				this.inputConsumed = true;
			}
		}
	}
	
	public boolean consumed() {
		return this.consumed ;
	}
	

	public void close() throws DBException 
	{
		input.close();
	}
	
	
	public void advance(int level) throws DBException {
		
		if (inputConsumed) { 
			consumed = true;
			return;
		}
		
		key = nextTuple;
		subSet.clear();
		
		while(!inputConsumed && key.equalUpTo(level, nextTuple) == 0)
		{
			subSet.add(nextTuple);
			inputNext();
		}
	}
	
	public TwigResult key() 
	{
		return this.key;
	}
	
	public ArrayList<TwigResult> xProduct(PathInput right) throws DBException {
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

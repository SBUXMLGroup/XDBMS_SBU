/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package xmlProcessor.TJFast;

import java.io.IOException;
import java.util.ArrayList;

import xmlProcessor.DBServer.DBException;
import xmlProcessor.DBServer.operators.twigs.QueryStatistics;

public class PathInput 
{
	private TwigInput input;
	private ArrayList<TwigResult> subSet;
	private TwigResult nextTuple;
	private boolean consumed = false;
	private TwigResult key;
	private boolean inputConsumed = false;
	
	public PathInput(TwigInput input) {
		this.input = input;
		this.subSet = new ArrayList<TwigResult>();
	}
	
	public void open() throws DBException {
		input.open();
		inputNext();
	}
	
	private void inputNext() throws DBException, IOException {
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
			//if (this.input.getClass().getSimpleName().equals("InputWrapper")) 
				//System.out.println(nextTuple.toString());
			
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

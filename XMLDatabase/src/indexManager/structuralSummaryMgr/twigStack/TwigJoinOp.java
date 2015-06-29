package indexManager.structuralSummaryMgr.twigStack;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import xmlProcessor.DBServer.DBException;
import xmlProcessor.DBServer.operators.QueryOperator;
//import xtc.server.xmlSvc.xqueryOperators.Twigs.XTCxqueryTwigStk02MJInput;
//import xtc.server.xmlSvc.xqueryUtils.XTCxqueryLevelCounters;
import xmlProcessor.DBServer.utils.QueryTuple;

public class TwigJoinOp implements TwigInput
{
	private Queue<TwigResult> bufferQueue = null;
	private PathInput left;
	private PathInput right;
	private int level;
	private boolean emptyJoin;
	private boolean singlePath = false;
	private Queue<TwigResult> path;

	public TwigJoinOp()
	{
		this.emptyJoin = true;
	}
	
	public TwigJoinOp(Queue<TwigResult> path)
	{
		this.path = path;
		this.singlePath = true;
		this.emptyJoin = false;
	}
	
	public TwigJoinOp(TwigInput left, TwigInput right, int level) {
		this.left = new PathInput(left);
		this.right = new PathInput(right);
		this.level = level;
		bufferQueue = new LinkedList<TwigResult>();
		this.emptyJoin = false;
	}

	 
	//Consider that TwigSatck has been guaranteed that all members are possible to be joined 
	//so the algorithm is different from the general mergejoin algorithm  
	private void mergejoin() throws DBException 
	{		
		left.advance(level);
		right.advance(level);
		while (!left.consumed() && !right.consumed()) 
		{
			int c = left.key().equalUpTo(level, right.key());
			if (c == 0) 
			{
				// add cross product to bufferQueue
				bufferQueue.addAll(left.xProduct(right));
				return; // for ONC protocol uncomment
			}
			else if (c < 0)
			{
				left.advance(level);
			}
			else //(c > 0)
			{
				right.advance(level);
			}
				
		}
	}

	public TwigResult next() throws DBException 
	{
		if (emptyJoin) return null;
		if (singlePath) return path.poll();
		
		while (bufferQueue.size() == 0 && !left.consumed() && !right.consumed()) 
		{
			mergejoin();
		}
		return bufferQueue.poll();
	}

	public void open() throws DBException 
	{
		if (singlePath) return;
		
		left.open();
		right.open();
	}

	public void close() throws DBException 
	{
		if (singlePath) return;
		
		left.close();
		right.close();
	}	
}

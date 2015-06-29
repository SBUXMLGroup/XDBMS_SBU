
package xmlProcessor.RG.RG00;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;

import xmlProcessor.DBServer.DBException;
//import xtc.server.DBException;
//import xtc.server.xmlSvc.xqueryOperators.XTCxqueryOperator;
 import xmlProcessor.QTP.QTPNode;
//import xtc.server.xmlSvc.xqueryUtils.XTCxqueryLevelCounters;
//import xmlProcessor.DBServer.utils.QueryTuple;

public class TwigJoinOp implements TwigInput
{
	private Queue<TwigResult> bufferQueue = null;
	private PathInput left;
	private PathInput right;
	private int level;
	private TwigInput path;
	private boolean singlePath = false;
	
	public TwigJoinOp(TwigInput left, TwigInput right, int level) {
		this.left = new PathInput(left);
		this.right = new PathInput(right);
		this.level = level;
		bufferQueue = new LinkedList<TwigResult>();
	}
	
	public TwigJoinOp(TwigInput path)
	{
		this.path = path;
		this.singlePath = true;
	}


	//Consider that TwigSatck has been guaranteed that all members are possible to be joined 
	//so the algorithm is different from the general mergejoin algorithm  
	private void mergejoin() throws DBException, IOException 
        {
		left.advance(level);
		right.advance(level);
		while (!left.consumed() && !right.consumed()) 
		{
			int c;
			c = left.key().compareUpTo(level, right.key());
			if (c == 0) 
			{
				// add cross product to bufferQueue
				//bufferQueue.addAll(left.xProduct(right));
				ArrayList<TwigResult> xProduct = left.xProduct(right);
				TwigResult[] sortedXProduct = new TwigResult[xProduct.size()];
				sortedXProduct = xProduct.toArray(sortedXProduct);
				Arrays.sort(sortedXProduct, new XProductResultComparator(right.getRightLeaf()));
				for (int i = 0; i < sortedXProduct.length; i++) 
				{
					bufferQueue.offer(sortedXProduct[i]);
				}

				return; // for ONC protocol uncomment
			}
			else if (c < 0)
			{
				left.advance(level);
			}
			else //(c>0)	
			{
				right.advance(level);
			}
				
		}
	}

	public QTPNode getRightLeaf()
	{
		return right.getRightLeaf();
	}
	
	public TwigResult next() throws DBException,IOException 
	{
		if (singlePath)
		{
			return path.next();
		}
		
		while (bufferQueue.size() == 0 && !left.consumed() && !right.consumed()) 
		{
			mergejoin();
		}
		return bufferQueue.poll();
	}

	public void open() throws DBException,IOException 
	{
		if (singlePath)
		{
			path.open();
			return;
		}

		left.open();
		right.open();
	}

        @Override
	public void close() throws DBException,IOException 
        {
		if (singlePath)
		{
			path.close();
			return;
		}

		left.close();
		right.close();
	}	
	
	///////////////////////////////////////////////////////////////////////////
	private class XProductResultComparator implements Comparator<TwigResult>
	{
		private QTPNode node;
		
		public XProductResultComparator(QTPNode node)
		{
			this.node = node;
		}
		
		public int compare(TwigResult res1, TwigResult res2)
		{
				int c = res1.getRes(node).compareTo(res2.getRes(node));
				if (c != 0)
				{
					return c;
				}
			return 0;
		}
	}

}

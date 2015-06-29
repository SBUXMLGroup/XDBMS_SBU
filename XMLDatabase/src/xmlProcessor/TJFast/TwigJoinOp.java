

package xmlProcessor.TJFast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;

import xmlProcessor.DBServer.DBException;
import xmlProcessor.DBServer.operators.QueryOperator;
import xmlProcessor.DBServer.operators.twigs.QueryTwigStk02MJInput;
import xmlProcessor.QTP.QTPNode;
//import xtc.server.xmlSvc.xqueryUtils.XTCxqueryLevelCounters;
//import xtc.server.xmlSvc.xqueryUtils.XTCxqueryTuple;

public class TwigJoinOp implements TwigInput
{
	private Queue<TwigResult> bufferQueue = null;
	private PathInput left;
	private PathInput right;
	private QTPNode rightLeaf;
	private int level;
	private ArrayList<QTPNode> rightLeafQueryPath;
	
	private boolean emptyJoin;
	private boolean singlePath = false;
	private TwigInput path;

	
	public TwigJoinOp(TwigInput left, TwigInput right, int level) {
		this.left = new PathInput(left);
		this.right = new PathInput(right);
		this.level = level;
		bufferQueue = new LinkedList<TwigResult>();
		rightLeaf = right.getRightLeaf();
		rightLeafQueryPath = new ArrayList<QTPNode>();
		QTPNode node = rightLeaf;		
		while (node != null)
		{
			rightLeafQueryPath.add(0, node);
			node = node.getParent();
		}
		this.emptyJoin = false;
	}


	public TwigJoinOp(TwigInput path)
	{
		this.path = path;
		this.singlePath = true;
		this.emptyJoin = false;
	}

	public TwigJoinOp()
	{
		this.emptyJoin = true;
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
				ArrayList<TwigResult> xProduct = left.xProduct(right);
				TwigResult[] sortedXProduct = new TwigResult[xProduct.size()];
				sortedXProduct = xProduct.toArray(sortedXProduct);
				Arrays.sort(sortedXProduct, new XProductResultComparator(rightLeafQueryPath));
				for (int i = 0; i < sortedXProduct.length; i++) 
				{
					bufferQueue.offer(sortedXProduct[i]);
				}
				//bufferQueue.addAll();
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

	public TwigResult next() throws DBException, IOException 
	{
		if (emptyJoin) return null;
		if (singlePath) return path.next();
		
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
	
	public QTPNode getRightLeaf()
	{
		return rightLeaf;
	}
	
	///////////////////////////////////////////////////////////////////////////
	private class XProductResultComparator implements Comparator<TwigResult>
	{
		private ArrayList<QTPNode> nodes;
		
		public XProductResultComparator(ArrayList<QTPNode> nodes)
		{
			this.nodes = nodes;
		}
		
		public int compare(TwigResult res1, TwigResult res2)
		{
			for (QTPNode qNode : nodes)
			{
				int c = res1.getRes(qNode).compareTo(res2.getRes(qNode));
				if (c != 0)
				{
					return c;
				}
			}
			
			return 0;
		}
	}

}


package indexManager.RangeID;


import xmlProcessor.DBServer.DBException;

/**
 * @author Kamyar
 *
 */
public class RangeID implements Comparable<RangeID> 
{
	private int lRange;
	private int rRange;
	private int level;
	
	public RangeID()
	{
		this.lRange = -1;
		this.rRange = -1;
		this.level = -1;
		
	}
	public RangeID(int lRange, int rRange, int level)
	{
		this.lRange = lRange;
		this.rRange = rRange;
		this.level = level;
	}
	
	public void setLeft(int lRange)
	{
		this.lRange = lRange;
	}

	public int getLeft()
	{
		return this.lRange;
	}

	public void setRight(int rRange)
	{
		this.rRange = rRange;
	}

	public int getRight()
	{
		return this.rRange;
	}
	
	public void setLevel(int level)
	{
		this.level = level;
	}

	public int getLevel()
	{
		return this.level;		
	}
	
	public int compareTo(RangeID rangeID)
	{
		if (this.lRange == rangeID.getLeft()) return 0;
		if (this.lRange < rangeID.getLeft()) return -1;
		return 1;		
	}
	
	public boolean isSelfOf (RangeID rangeID) 
	{		
		return this.compareTo(rangeID) == 0;
	}

	public boolean isAncestorOf(RangeID rangeID)
	{
		if ((this.lRange < rangeID.getLeft()) && (this.rRange > rangeID.getRight())) return true;
		
		return false;
	} // isAncestorOf

	public boolean isAncestorOrSelfOf(RangeID rangeID) throws DBException
	{
		if(this.isSelfOf(rangeID))
			return true;

		return isAncestorOf(rangeID);
	} // isAncestorOrSelfOf

	public boolean isParentOf (RangeID rangeID) throws DBException
	{
		// docID is checked in isAncestorOf

		return (this.isAncestorOf(rangeID))
				&& (this.level == rangeID.getLevel() - 1);
	}

	public boolean isPrecedingOf (RangeID rangeID) throws DBException
	{
		if (this.rRange < rangeID.getLeft()) return true;
		
		return false;
	} // isPrecedingOf

	public boolean isFollowingOf (RangeID rangeID) throws DBException
	{
		if (this.lRange > rangeID.getRight()) return true;
		
		return false;
	}

	public boolean isChildOf (RangeID rangeID) throws DBException
	{
		return rangeID.isParentOf(this);
	}

	public boolean isDescendantOf (RangeID rangeID) throws DBException
	{
		if ((this.lRange > rangeID.getLeft()) && (this.rRange < rangeID.getRight())) return true;
		
		return false;		
	}

	public boolean isDescendantOrSelfOf (RangeID rangeID) throws DBException
	{
		if(this.isSelfOf(rangeID))
			return true;

		return isDescendantOf(rangeID);		
	} // isDescendantOrSelfOf
	
	
}
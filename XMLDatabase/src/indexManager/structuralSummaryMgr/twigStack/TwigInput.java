package indexManager.structuralSummaryMgr.twigStack;

import xmlProcessor.DBServer.DBException;

public interface TwigInput 
{
	public TwigResult next() throws DBException;

	public void open() throws DBException;

	public void close() throws DBException;

}

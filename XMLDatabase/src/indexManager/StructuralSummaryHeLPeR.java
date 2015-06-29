//
//package indexManager;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.concurrent.locks.ReentrantLock;
//
//public class StructuralSummaryHeLPeR 
//{   
//
//	private StructSumNode root;
//	// insert new nodes as children of this node
//	
//    // CID count
//    private int CID 		= 0;
//       
//    // dynamic CIDName
//    private int CIDName		= 0;
//    
//    
//    public StructuralSummaryHeLPeR(int idxNo, PathSynopsisConverter pathSynopsisConverter, IndexServices idxSvc) 
//    {
//    }
//
//	
//    public String getPath(StructSumNode node)
//	{	
//	}
//		
//	public int appendNode(XTCtransaction transaction, int parentPCR, int vocId, byte nodeType)
//	{	
//	
//	}
//	
//	
//	/**
//	 * Gets a list of nodes conforming to the attributes in the two arrays. 
//	 * The first entries of the two arrays must be the current's attributes.
//	 * 
//	 * @param vocIds - vocabulary ids identifying nodes in the tree
//	 * @param nodeTypes - types of node vocId + nodeType >> unique
//	 * @return a List of nodes representing the path
//	 */
//	
//	private int appendPath(int parentPCR, byte[] nodeTypes, boolean rootDown) 
//	{
//	}
//	
//	/**
//	 * Gets the node associated with the given CID.
//	 * 
//	 * @param CID - identifies a node with this
//	 * @return the associated node 
//	 */
//	public StructSumNode getNodeByCid(int CID)
//	{
//		Integer pcrAsInt = new Integer(pcr);
//		if(!pcr_hash.containsKey(pcrAsInt))
//			return null;
//		return pcr_hash.get(pcrAsInt);
//	}
//	
//	
//	
//
//	
//	
//	
//
//	
//	
//	
//	
//	
//
//	
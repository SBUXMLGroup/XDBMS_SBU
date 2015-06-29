
package indexManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import xmlProcessor.DBServer.DBException;
//import xtc.server.accessSvc.XTClocator;
import xmlProcessor.DeweyID;
//import xtc.server.taSvc.XTCtransaction;
//import xtc.server.util.TwinArrayList;
import xmlProcessor.DBServer.utils.TwinObject;
import xmlProcessor.QTP.QTPNode;
import xmlProcessor.QTP.QueryTreePattern;
import xmlProcessor.RG.executionPlan.RGExecutionPlan;
/**
 * Handles access, loading and storing 
 * of the path synopsis.
 * 
 * 
 * @author martinmeiringer
 * @author krusty
 */

public interface StructuralSummaryManager 
{
   // the three types of node values
    public static final byte NODETYPE_ATTRIBUTE    = 1;
    public static final byte NODETYPE_ELEMENT      = 2;
    
    // the thirteen xquery axes 
    public static final byte AXIS_SELF = 0;
    public static final byte AXIS_PARENT = 1;
    public static final byte AXIS_CHILD = 2;
    
    public static final byte AXIS_ANCESTOR = 3;
    public static final byte AXIS_ANCESTOR_OR_SELF = 4;
    
    public static final byte AXIS_DESCENDANT = 5;
    public static final byte AXIS_DESCENDANT_OR_SELF = 6;
    
    public static final byte AXIS_PRECEDING = 7;
    public static final byte AXIS_FOLLOWING = 8;
    
    public static final byte AXIS_ATTRIBUTE = 9;

    public static final byte AXIS_PRECEDING_SIBLINGS = 10;
    public static final byte AXIS_FOLLOWING_SIBLINGS = 11;
    
    public static final byte AXIS_NAMESPACE = 12;    
//     -----------commented by ouldouz
//	
//	/**
//	 * When a shutdown is signalled, the path synopses, that are stored are 
//	 * written back at their position.
//	 * @throws DBException
//	 */
//	public void shutdown() throws DBException;
//    /**
//     * Creates a string representing a path synopsis
//     * 
//     * @param transaction - use this transaction for reading
//     * @param docID - represent this object
//     * @return a created String representing the document given
//     * @throws DBException
//     */
//	public String 	toString(XTCtransaction transaction, int docID) throws DBException;
//	
//	/**
//	 * @param transaction
//	 * @param docID - the document on which this query is to be evaluated on
//	 * @param pcr 	- the pcr of the node from which the axis is evaluated
//	 * @param axis 	- the axis to evaluate (see this interfaces' constants for more information)
//	 * @return a set of node pcrs belonging to the xpath axis used as second parameter
//	 * @throws DBException 
//	 */	
////	public Set<Integer> getPCRsForPath(XTCtransaction transaction, int docID, int pcr, byte axis) throws DBException;
//	/**
//	 * @param transaction
//	 * @param docID 		- the document on which this query is to be evaluated on
//	 * @param queryString 	- the xpath query to evaluate 
//	 * @return a set of node pcrs, which confine to the query given
//	 */
//	public Set<Integer> getPCRsForPath(XTCtransaction transaction, int docID, String queryString) throws DBException;
//    /**
//     * @param transaction - perform locks on this transaction
//     * @param docID - get paths from this document
//     * @param path 	- get paths confining to this path (vocIds)
//     * @return a set of pcrs of nodes of this path
//     * @throws DBException
//     */
//    public Set<Integer> getPCRsForPath(XTCtransaction transaction, int docID, int[] path) throws DBException;
//    
//    /**
//     * @param transaction - perform locks on this transaction
//     * @param docID - get paths from this document
//     * @param path 	- get paths confining to this path
//     * @param type 	- to each vocId a path to use to find the successor (predecessor respectively)
//     * @return a set of pcrs of nodes of this path (vocIds)
//     * @throws DBException
//     */
////    public Set<Integer> getPCRsForPath(XTCtransaction transaction, int docID, int[] path, byte[] type) throws DBException; 
//    /**
//     * Elementless storing maps one pcr to more than one deweyID. This means the correct pcr
//     * needs to selected before the correct vocabulary ID can be retrieved.
//     * 
//     * @param transaction	- the transaction to lock on / compensate
//     * @param docID			- the identifier of the document from this path synposis
//     * @param deweyID		- the dewey representation
//     * @return the vocabulary id of the given node pcr	
//     * @throws DBException 
//     */
//    public int getVocID(XTCtransaction transaction, int docID, DeweyID deweyID) throws DBException;
//    /**
//     * @param transaction	- the transaction to lock on / compensate
//     * @param docID			- the identifier of the document from this path synposis
//     * @param pcr			- the pcr of the node
//     * @return the vocabulary id of the given node pcr		
//     */
//    // public int getVocID(XTCtransaction transaction, int docID, int pcr) throws DBException;
//    /**
//     * @param transaction	- the transaction to lock on / compensate
//     * @param docID			- the identifier of the document from this path synposis
//     * @param pcr			- the pcr of the node
//     * @return the count of the corresponding node
//     */
//    public int getCount(XTCtransaction transaction, int docID, int pcr) throws DBException;
//	
//    /**
//     * @param transaction	- the transaction to lock on / compensate
//     * @param docID			- the identifier of the document from this path synposis
//     * @param pcr			- the pcr of the node
//     * @return the pcr of the node's parent	
//     */
//	public int 	 	getParentPCR(XTCtransaction transaction, int docID, int pcr) throws DBException;
//	
//	/** 
//	 * @param transaction TODO
//	 * @param docID TODO
//	 * @param pcr
//	 * @return
//	 * @throws DBException
//	 */
//	public int getAncestorPCR(XTCtransaction transaction, int docID, int pcr, int level) throws DBException;
//	
//	/**
//	 * @param transaction	- the transaction to lock on / compensate
//     * @param docID			- the identifier of the document from this path synposis
//     * @param pcr			- the pcr of the node
//  	 * @return the pcrs of the given node's children
//	 */
//	public int[] 	getChildrenPCR(XTCtransaction transaction, int docID, int pcr) throws DBException;
//	
//	/**
//	 * Returns the pcr of a child node with label <code>vocID</code> and type <code>nodeType</code> of pcr <code>parentPcr</code>.
//	 * If such a child does not exist it is created and the new PCR is returned.
//	 * @param transaction transaction requesting the pcr
//	 * @param docID document identifier
//	 * @param parentPcr pcr of the parent node
//	 * @param vocID vocID of the child node
//	 * @param nodeType type of the child 
//	 * @return pcr of a child node with label <code>vocID</code> and type <code>nodeType</code> of pcr <code>parentPcr</code>, 
//	 * <code>newPCR</code> if there was no such child
//	 * @throws DBException
//	 */
//	public int getChildPCR(XTCtransaction transaction, int docID, int parentPcr, int vocID, byte nodeType) throws DBException;
//	
//	/**
//	 * Deletes the subtree represented by the pcr beeing the root of the subtree
//	 * (xml document -> all occurences of node no longer exist)
//	 * 
//	 * @param transaction	- the transaction to lock on / compensate
//     * @param docID			- the identifier of the document from this path synposis
//     * @param pcr			- the pcr of the node
//	 */
//	public void 	delete(XTCtransaction transaction, int docID, int pcr) throws DBException;
//
//	/**
//	 * Deletes the path synopsis from the hash of referenced synopses
//	 * Attention - the physical index is erased elsewhere!
//	 * 
//	 * @param transaction	- the transaction to lock on / compensate
//     * @param docID			- the identifier of the document from this path synposis
//	 */
//	
//	public void 	delete(XTCtransaction transaction, int docID) throws DBException;
//	/**
//	 * Adds given 'value' to count of node corresponding to given pcr
//	 * 
//	 * @param transaction	- the transaction to lock on / compensate
//     * @param docID			- the identifier of the document from this path synposis
//     * @param pcr			- the pcr of the node
//     * @param value			- this is what is added to the value of count
//	 */
//	public void 	increaseCount(XTCtransaction transaction, int docID, int pcr, int value) throws DBException;
//	/**
//	 * Subtracts given 'value' from count of node corresponding to given pcr
//	 * 
//	 * @param transaction	- the transaction to lock on / compensate
//     * @param docID			- the identifier of the document from this path synposis
//     * @param pcr			- the pcr of the node
//     * @param value			- this is what is added to the value of count
//	 */
//	public void 	decreaseCount(XTCtransaction transaction, int docID, int pcr, int value) throws DBException;
//
//	/**
//	 * Exchanges the vocID of a node.
//	 * 
//	 * @param transaction	- the transaction to lock on / compensate
//     * @param docID			- the identifier of the document from this path synposis
//	 * @param pcr			- pcr of the node to rename
//	 * @param vocID 		- this is the new vocID
//	 * @throws DBException 
//	 */
//	public void 	renameNode(XTCtransaction transaction, int docID, int pcr, int vocID) throws DBException;
//	/**
//	 * Appends a path to the given node, incrementing only the last node's counter as 
//	 * consequence.
//	 * 
//	 * @param transaction	- the transaction to lock on / compensate
//     * @param docID			- the identifier of the document from this path synposis
//     * @param pcr			- the pcr of the node, that is the dedicated root of the path
//	 * @param vocIDs		- the vocabulary ids of the nodes to traverse
//	 * @param nodeTypes		- types of node, to find the correct path
//	 * @return the pcr of the last node			
//	 * @throws DBException 
//	 */
//	public int appendPath(XTCtransaction transaction, int docID, int pcr, int[]vocIDs, byte[] nodeTypes) throws DBException;
//	/**
//	 * Appends a node to a given node (its pcr) of the tree
//	 * 
//	 * @param transaction	- the transaction to lock on / compensate
//     * @param docID			- the identifier of the document from this path synposis
//	 * @param parentPCR		- the pcr of the parent of the new node
//	 * @param vocID			- the vocabulary id of the new node
//	 * @param nodeType		- the type of the new node
//	 * @return the pcr of the created node
//	 * TODO: prepare path synopsis to bulk insert subtrees
//	 *       problem of locking (long/short), pcr counter, and concurrency
//	 * @throws DBException 
//	 */
//	public int appendNode(XTCtransaction transaction, int docID, int parentPCR, int vocID, byte nodeType) throws DBException;
//	/**
//	 * Appends a node to a given node (its pcr) of the tree and directly logs the modification operation
//	 * 
//	 * @param transaction	- the transaction to lock on / compensate
//     * @param docID			- the identifier of the document from this path synposis
//	 * @param parentPCR		- the pcr of the parent of the new node
//	 * @param vocID			- the vocabulary id of the new node
//	 * @param nodeType		- the type of the new node
//	 * @return the pcr of the created node
//	 * TODO: prepare path synopsis to bulk insert subtrees
//	 *       problem of locking (long/short), pcr counter, and concurrency
//	 * @throws DBException 
//	 */
//	public int appendNodeLogged(XTCtransaction transaction, int docID, int parentPCR, int vocID, byte nodeType) throws DBException;
//
//	/**
//	 * Checks the path from the given pcr upwards, for the given vocID, returning
//	 * a list of the levels of the corresponding nodes, that were found.
//	 * 
//	 * e.g.: path: 1/1/2/1/1(pcr:6)
//	 * call: getLevelsForPCRandVocID(t, docID, 6, 1)
//	 * returns: 4,3,1,0
//	 * 
//	 * @param transaction	- the transaction to lock on / compensate
//     * @param docID			- the identifier of the document from this path synposis
//	 * @param pcr 			- the pcr of the lowest elment to check for the vocID
//	 * @param vocID			- the vocabulary id to search for in the path
//	 * @return a list of levels corresponding to the nodes found with the given vocID
//	 * @throws DBException 
//	 */
//	public List<Integer> getLevelsForPCRandVocID(XTCtransaction transaction, int docID, int pcr, int vocID) throws DBException;
//	
//	public PcrVocID getPcrVocID(XTCtransaction transaction, int docID, DeweyID deweyID) throws DBException;
//	
//	public int getPCRSize(XTCtransaction transaction, int docID) throws DBException;

	///////////////////////
	//start of inclusion by Kamyar at 5.12.07
	////////////////////////////

	public RGExecutionPlan getRGExecutionPlan(String indexName,QueryTreePattern QTP) throws DBException, IOException;
//	 -----------commented by Ouldouz to the end   
//	public String getXMLPathSynopsis(XTCtransaction transaction, XTClocator doc) throws DBException;
//	
//	/**
//	 * Returns list of (Level and PCR) of pathsynopsis node from  root to PCR node that matched the pathExpr
//	 * @param transaction
//	 * @param doc
//	 * @param PCR
//	 * @param pathExpr
//	 * @return
//	 * @throws DBException
//	 */
//	public TwinArrayList<Integer, Integer> findNodeMatches(XTCtransaction transaction, XTClocator doc, int PCR, String pathExpr) throws DBException;
//	
//	/**
//	 * Returns levels and PCRs of path synopsis nodes match path parameter in the root to PCR node path
//	 * 
//	 * @param path - list of VocID in the path ordered regards to their position in the main path
//	 * @param PCR - it is the pathsynopsis node that matching process is done from the root to this node
//	 */
//	public ArrayList<ArrayList<TwinObject<Integer, Integer>>> findPathMatches(XTCtransaction transaction, XTClocator doc, int PCR, ArrayList<Integer> path) throws DBException;
//	
//	/**
//	 * 
//	 * @param transaction
//	 * @param doc
//	 * @param vocID
//	 * @return number of pathsynopsis nodes have the same vocabulary ID as vocID 
//	 * @throws DBException
//	 */
//	public int getNodeCount(XTCtransaction transaction, XTClocator doc, int vocID) throws DBException;
//	
//	/**
//	 * @param transaction
//	 * @param docID 		- the document on which this query is to be evaluated on
//	 * @param queryString 	- the xpath query to evaluate 
//	 * @return a set of node pcrs, which confine to the query given
//	 */
//	public Set<Integer> getPCRsForPath(XTCtransaction transaction, XTClocator doc, QTPNode qNode) throws DBException;
//
//	///////////////////////
//	//end of inclusion by Kamyar at 5.12.07
//	////////////////////////////
//	
//
//}
}

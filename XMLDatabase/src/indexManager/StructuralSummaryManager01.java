package indexManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
//import java.util.Iterator;
import java.util.List;
//import java.util.Set;
import java.util.Stack;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


//import xtc.server.XTCdbMgr;
import xmlProcessor.DBServer.DBException;
//import xtc.server.accessSvc.XTClocator;
//import xtc.server.config.ConfigManager;
//import xtc.server.metaData.catalog.DocumentCatalogAccess;
//import xtc.server.metaData.manager.MetaDataMgr;
//--------------------------must be replaced---------------------------
////import xtc.server.metaData.pathSynopsis.PathSynopsis;
////import xtc.server.metaData.pathSynopsis.converter.PathSynopsisConverter;
//-----------------------------
import indexManager.IndexManager;
import indexManager.StructuralSummaryNode;
import indexManager.StructuralSummaryTree;
import indexManager.StructuralSummaryIndex;
import indexManager.structuralSummaryMgr.twigStack.TwigResult;
import indexManager.structuralSummaryMgr.twigStack.TwigJoinOp;
import indexManager.structuralSummaryMgr.twigStack.TwigStack;
import java.io.IOException;
import xmlProcessor.DeweyID;
//import xtc.server.propCtrl.XTCbufferMgr;
//import xtc.server.taSvc.XTCcompensationOperation;
//import xtc.server.taSvc.XTCtaMgr;
//import xtc.server.taSvc.XTCtransaction;
//import xtc.server.util.DisplacementContainer;
//import xtc.server.util.TwinArrayList;
//import xtc.server.util.XTCcalc;
//import xtc.server.util.XTCdiagMgr;
import xmlProcessor.DBServer.utils.SvrCfg;
import xmlProcessor.DBServer.utils.TwinObject;
import xmlProcessor.QTP.QTPNode;
import xmlProcessor.QTP.QueryTreePattern;
import xmlProcessor.RG.executionPlan.RGExecutionPlan;
import xmlProcessor.RG.executionPlan.RGExecutionPlanTuple;

public class StructuralSummaryManager01 implements StructuralSummaryManager
{
 //	private final XTCdbMgr dbMgr;
//	private final MetaDataMgr metaDataMgr;
//	private final XTCtaMgr taMgr;
    private IndexManager indexMgr;
    //privtate hashmap()string->stsumTree
    private static HashMap<String,StructuralSummaryTree> summaryTreeStorage=new HashMap<>();
    // the stored path synopses. (i,o) = (docID, pathSynopsis) 
   // private DisplacementContainer<Integer,PathSynopsis> 	synopses;
    private int	maxPathSynopses;
    private final String MAX_PATHSYNOPSES = "metaDataMgr/maxPathSynopses";
    private static StructuralSummaryManager01 Instance=new StructuralSummaryManager01();
    public static StructuralSummaryManager01 getInctance()
    {
        return Instance;        
    }
    /**
     * Default constructor.
     * 
     * @param dbMgr			- database manager, parent of this object
     */
    public StructuralSummaryManager01()
    {
        indexMgr=IndexManager.Instance;
        //summaryTreeStorage=new HashMap<>();
        
    }
//-------------------------------------commented by ouldouz------------------
//	public StructuralSummaryManager01(XTCdbMgr dbMgr, MetaDataMgr metaDataMgr, XTCtaMgr taMgr)
//	{
//		this.dbMgr = dbMgr;
//        XTCdiagMgr.startStop (XTCdiagMgr.STARTSTOPTYPE_START, "XTCpsMgr01", "XTCpsMgr01", "");
//		this.maxPathSynopses		= ConfigManager.getInt(MAX_PATHSYNOPSES);
//		this.metaDataMgr			= metaDataMgr;
//		this.taMgr					= taMgr;
//		this.synopses = new DisplacementContainer<Integer, PathSynopsis>("", this.maxPathSynopses, DisplacementContainer.LRU);
//		XTCdiagMgr.startStop (XTCdiagMgr.STARTSTOPTYPE_OK, "XTCpsMgr01", "XTCps	Mgr01", "");
//	}
//	-----------------------------------------------------------------
	/**
	 * Whenever a path synopsis needs to be loaded first check if 
	 * the maximal number of path synopsis is reached yet and 
	 * write back the one least recently used, if necessary.
	 * 
	 * @param transaction - on this transaction the write back and load commands are performed
	 * @param docID - the document identifier of the document to add
	 * @return the loaded PathSynopsis either from map or mem
	 * @throws DBException
	 */

	private StructuralSummaryTree getStructuralSummary(String indexName) throws DBException, IOException
	{
		//synopses is a storage(Buffer) for pathsynopsis of all docIDs:(docID,pathsynopsis)
               //PathSynopsis ps = synopses.get(indexName);
                StructuralSummaryIndex ssIndx;
                StructuralSummaryTree ssTree;
                ssIndx=indexMgr.openSSIndex(indexName);
                if(summaryTreeStorage.containsKey(indexName))
                     return summaryTreeStorage.get(indexName);//chera mishe this pas bdim???!
                
                ssTree=new StructuralSummaryTree(ssIndx);//bayd indexo bgire
                summaryTreeStorage.put(indexName,ssTree);
                
		///////////////////////
		// inclusion by Kamyar at 8.12.07
		///////////////////////
		//setRangeID sets the labels needed to run twigstack on path synopsis
		// mod by karsten
//                 --------------------------------
//		if (SvrCfg.rangeEncoding) ps.setRangeIDs();
//               ---------------------------------------
		////////////////////////
		// end of inclusion by Kamyar at 8.12.07
		///////////////////////
                ssTree.setRangeIDs();
		if(ssTree!=null)
                    return ssTree;
		throw new DBException("IndexManager could not open structural sum index for [indexName: "+indexName+"]");
	}
//      -------------------------------commented by ouldouz--------------------	
//	/**
//	 * Loads a path synopsis from storage.
//	 * 
//	 * @param docID - identifier of the document, that is to be loaded
//	 * @param transaction - the transaction that is currently performing the action
//	 * @return true, if path synopsis loaded, false, if no path synopsis found
//	 */
//	private PathSynopsis loadPSfromStorage(int docID, XTCtransaction transaction) throws DBException
//	{
//		boolean transactionInit = false;
//		if(transaction == null)
//		{
//			transaction = this.taMgr.beginWork(XTCtransaction.ISOLATIONLEVEL_SERIALIZABLE, XTCtransaction.LOCKDEPTH_MAX, XTCtransaction.ISOLATIONLEVEL_SERIALIZABLE, "PSMgr","loadPSfromStorage",null, true);
//			transactionInit = true;
//		}
//		XTCbufferMgr xtcBufferMgr = this.dbMgr.getXTCbufferMgr(XTCcalc.getByte3(docID));
//    	int bufferPosCatalog;
//    	bufferPosCatalog = xtcBufferMgr.fetchPage (transaction, docID, XTCbufferMgr.FIXMODE_READ);
//    	int psIdxNo      = DocumentCatalogAccess.getDocumentPsIdxNo(xtcBufferMgr, bufferPosCatalog);
//    	xtcBufferMgr.unfixReadBufferPos (transaction, bufferPosCatalog);
//    	PathSynopsis ps = null;
//    	if (psIdxNo > 0) 
//    	{  //only valid path synopsis-indices can be accessed
//    		//System.out.println("PathSynopsisMgr [loadPSfromStorage] loading synopsis from index " + psIdxNo);
//    		PathSynopsisConverter psc = this.metaDataMgr.getPathSynopsisConverter();
//    		ps = psc.loadPathSynopsis(transaction, psIdxNo);
//    		//ps.setTransactionManager(this.taMgr);
//    	}
//    	else
//    		throw new DBException("PathSynopsisMgr [loadPSfromStorage] could not find path synopsis for document " + docID);
//    	if(transactionInit)
//    		this.taMgr.commitWork(transaction);
//    	return ps;
//	}
//	
//	/**
//	 * Tells the diagnosis manager that the pathsynopsis manager has shutdown
//	 * @throws DBException 
//	 */
//	public void shutdown() throws DBException
//	{
//        XTCdiagMgr.startStop (XTCdiagMgr.STARTSTOPTYPE_STOP, "XTCpsMgr01", "shutdown", "");
//        XTCtransaction transaction = this.taMgr.beginWork(XTCtransaction.ISOLATIONLEVEL_SERIALIZABLE, XTCtransaction.LOCKDEPTH_MAX, XTCtransaction.TRANSACTIONTYPE_READWRITE, "PSMgr","shutdown",null, true);
//        // make a key set for the iterator
//        Set<Integer> keys = synopses.keySet();
//        Iterator<Integer> iterator = keys.iterator();
//        Integer key;
//        PathSynopsis current;
//        while(iterator.hasNext())
//        {	// iterate over all keys
//        	key = iterator.next();
//        	current = synopses.get(key);
//        	if(current.writeBack())
//        	{	// write back
//            	//psc.storePathSynopsis(transaction, current, current.getIndexNumber(), true);
//        	}
//        }
//        taMgr.commitWork(transaction);
//        XTCdiagMgr.startStop (XTCdiagMgr.STARTSTOPTYPE_OK, "XTCpsMgr01", "shutdown", "");
//	}
//
//	/**
//	 * @param transaction
//	 * @param docID
//	 * @return a String representing the pathSynopsis given (docID)
//	 * @throws DBException 
//	 */
//	public String toString(XTCtransaction transaction, int docID) throws DBException 
//	{	
//		PathSynopsis ps = getStructuralSummary(transaction, docID);		
//		return ps.toString();
//	}
//
//	/**
//	 * Returns as many pcrs as fit to the given path
//	 * 
//	 * @param transaction - current TA
//	 * @param docID - id of document
//	 * @param path - array of vocabulary identifiers, direction: root down
//	 * 
//	 * @return a set of pcrs of the nodes that are last element on the path (last element in array)
//	 */
//	public HashSet<Integer> getPCRsForPath(XTCtransaction transaction, int docID, int[] path) throws DBException {
//		PathSynopsis ps = getStructuralSummary(transaction, docID);
//		if (ps == null) // no path synopsis available
//			return new HashSet<Integer>();
//		return ps.getPCRsForPath(path);
//	}
//
//	/**
//	 * Returns as many pcrs as fit to the given path
//	 * 
//	 * @param transaction - current TA
//	 * @param docID - id of document
//	 * @param path - array of vocabulary identifiers, direction: root down
//	 * @param type - array of type identifiers, direction: root down
//	 * @return a set of pcrs of the nodes that are last element on the path (last element in array)
//	 */
//	public Set<Integer> getPCRsForPath(XTCtransaction transaction, int docID, int[] path, byte[] type) throws DBException
//	{
//		PathSynopsis ps = getStructuralSummary(transaction, docID);
//		if (ps == null) // no path synopsis available
//			return new HashSet<Integer>();
//		return ps.getPCRsForPath(path, type);
//	}
//	
//	/**
//	 * Gets the nodes confining to the query.
//	 * 
//	 * @param docID - the id of the document
//	 * @param queryString - a string representing an xpath query
//	 * @return a set of nodes, confining to the query  
//	 */
//	public Set<Integer> getPCRsForPath(XTCtransaction transaction, int docID, String queryString) throws DBException
//	{	// check if function can be executed
//		PathSynopsis ps = getStructuralSummary(transaction, docID);
//		if (ps == null) // no path synopsis available
//			return new HashSet<Integer>();
//		return ps.getPCRsForPath(queryString);
//	}
//	
//	/**
//	 * Given a node and an axis (see constants above for encoding) returns the
//	 * according nodes confining to the given xpath axis.
//	 * 
//	 * WARNING: - Nodes on following and preceding 
//	 * 			  (resepctively following-siblings and preceding-siblings) axis
//	 * 			  do not conform to nodes of an actual xml document.
//	 * 			- Siblings of the path synopsis may actually appear as single children in
//	 * 			  the original document. The ordering is not preserved.
//	 * 		  	- function is preserved for index functionality (remove false positives later)
//	 * 			- namespace axis is not applicable to path synopsis as text nodes are not stored here
//	 * 
//	 * @param node - the node to use as starting point
//	 * @param axis - the axis to evaluate
//	 * @return a set of nodes confining to the given axis
//	 * @throws DBException 
//	 */
//	public synchronized Set<Integer> getPCRsForPath(XTCtransaction transaction, int docID, int pcr, byte axis) throws DBException
//	{
//		PathSynopsis ps 				= getStructuralSummary(transaction, docID);
//		StructuralSummaryNode node 			= ps.getNodeByPcr(pcr);
//		Set<Integer> set 				= new HashSet<Integer>();
//		StructuralSummaryNode parent 		= node.getParent();
//		StructuralSummaryNode currentChild 	= null;
//		List<StructuralSummaryNode> 	psnList = null;
//		int indexOf						= 0;
//		
//		if (ps == null) // no path synopsis available
//			return set;
//		
//		switch(axis)
//		{
//			case AXIS_CHILD: // all children
//				psnList = node.getChildren();
//				for(int i = 0; i < psnList.size(); i++)
//				{
//					set.add(psnList.get(i).getPCR());
//				}
//				break;
//			case AXIS_PARENT: // the direct parent
//				set.add(node.getParent().getPCR());
//				break;
//			case AXIS_ANCESTOR_OR_SELF: // self, parent and parent's parents up to root
//				set.add(node.getPCR());
//			case AXIS_ANCESTOR: // parent, parent's parents up to root
//				while(node.getParent() != null)
//				{
//					set.add(node.getParent().getPCR());
//					node = node.getParent();
//				}
//				break;
//			case AXIS_DESCENDANT_OR_SELF: // self, children and children's children down to leafs
//				set.add(node.getParent().getPCR());
//			case AXIS_DESCENDANT: // children, and children's children down to leafs
//				set.addAll(getDescendantsNotSelf(node));
//				break;
//			case AXIS_FOLLOWING: // every node that has a node number greater than given node (in pre-order)
//				if(parent == null)
//				{
//					set.addAll(getDescendantsNotSelf(node));
//					break;
//				}
//				while(node.getParent() != null)
//				{
//					psnList = parent.getChildren();
//					indexOf = psnList.indexOf(node);
//					for(int i = indexOf + 1; i < psnList.size(); i++)
//					{
//						set.addAll(getDescendants(psnList.get(i), true));
//					}
//					node = node.getParent();
//				}
//				break;
//			case AXIS_PRECEDING: // every node that has a node number smaller than given noe (pre-order)
//				if(parent == null)
//				{
//					break;
//				}
//				while(node.getParent() != null)
//				{
//					psnList = parent.getChildren();
//					indexOf = psnList.indexOf(node);
//					for(int i = 0; i < indexOf; i++)
//					{
//						set.addAll(getDescendants(psnList.get(i), true));
//					}
//					node = node.getParent();
//				}
//				break;
//			case AXIS_FOLLOWING_SIBLINGS: // every node that is sibling of given node and follows given
//				if(parent != null)
//				{
//					indexOf = parent.getChildren().indexOf(node);
//					psnList = parent.getChildren();
//					for(int i = indexOf + 1; i < psnList.size(); i++)
//					{
//						set.add(psnList.get(i).getPCR());
//					}
//				}
//				break;
//			case AXIS_PRECEDING_SIBLINGS: // every node that is sibling of given node and precedes given
//				if(parent != null)
//				{
//					indexOf = parent.getChildren().indexOf(node);
//					for(int i = 0; i < indexOf; i++)
//					{
//						set.add(parent.getChildren().get(i).getPCR());
//					}
//				}
//				break;
//			case AXIS_NAMESPACE: // all text children of given node (not recursively)
//				// there are no text nodes in the path synopsis
//				break;
//			case AXIS_ATTRIBUTE: // all attribute children of given node (not recursively)
//				psnList = node.getChildren();
//				for(int i = 0; i < psnList.size(); i++)
//				{
//					currentChild = psnList.get(i);
//					if(currentChild.getNodeType() == PathSynopsisMgr.NODETYPE_ATTRIBUTE)
//						set.add(currentChild.getPCR());
//				}
//				break;
//			case AXIS_SELF: // the node itself
//			default: 
//				set.add(node.getPCR());
//		}
//		return set;
//	}
//	
//	/**
//	 * Gets descendants recursively starting from the node given (included)
//	 * 
//	 * @param node - the starting node
//	 * @return a list of nodes, all marked as descendants of starting node (and the node itself)
//	 */
//	private List<Integer> getDescendants(StructuralSummaryNode node, boolean getSelf)
//	{
//		List<Integer> list = new ArrayList<Integer>();
//		if(getSelf)
//			list.add(node.getPCR());
//		List<StructuralSummaryNode> psNodes = node.getChildren();
//		for(int i = 0; i < psNodes.size(); i++)
//		{
//			list.addAll(getDescendants(psNodes.get(i), true));
//		}
//		return list;
//	}
//	
//	/**
//	 * Gets descendants recursively starting from the children of the node given (included)
//	 * 
//	 * @param node - the starting node
//	 * @return a list of nodes, all marked as descendants of starting node (and the node itself)
//	 */
//	private List<Integer> getDescendantsNotSelf(StructuralSummaryNode node)
//	{
//		return getDescendants(node, true);
//	}	
//	
//	public int appendNode(XTCtransaction transaction, int docID, int parentPCR, int vocID, byte nodeType) throws DBException 
//	{
//		PathSynopsis ps = getStructuralSummary(transaction, docID);
//		return ps.appendNode(transaction, parentPCR, vocID, nodeType);
//	}
//
//	public int appendNodeLogged(XTCtransaction transaction, int docID, int parentPCR, int vocID, byte nodeType) throws DBException 
//	{
//		PathSynopsis ps = getStructuralSummary(transaction, docID);
//		return ps.appendNodeLogged(transaction, parentPCR, vocID, nodeType);
//	}
//		
//	public void increaseCount(XTCtransaction transaction, int docID, int pcr, int value)  throws DBException
//	{
//		PathSynopsis ps = getStructuralSummary(transaction, docID);
//		StructuralSummaryNode psN = ps.getNodeByPcr(pcr);
//		psN.setCount(psN.getCount() + value);
//	}
//
//	public void decreaseCount(XTCtransaction transaction, int docID, int pcr, int value) throws DBException 
//	{
//		PathSynopsis ps = getStructuralSummary(transaction, docID);
//		StructuralSummaryNode psN = ps.getNodeByPcr(pcr);
//		psN.setCount(psN.getCount() - value);
//	}
//
//	public void delete(XTCtransaction transaction, int docID, int pcr) throws DBException
//	{
//		PathSynopsis ps = getStructuralSummary(transaction, docID);
//		ps.deleteSubtree(ps.getNodeByPcr(pcr));
//	}
//
//	/* (non-Javadoc)
//	 * @see xtc.server.metaData.pathSynopsis.manager.PathSynopsisMgr#getChildPCR(xtc.server.taSvc.XTCtransaction, int, int, int, byte)
//	 */
//	public int getChildPCR(XTCtransaction transaction, int docID, int parentPcr, int vocID, byte nodeType) throws DBException
//	{
//		PathSynopsis ps = getStructuralSummary(transaction, docID);
//		//System.out.println("Hole Node fÃ¼r PCR " + parentPcr);
//		StructuralSummaryNode psN = ps.getNodeByPcr(parentPcr);
//		//System.out.println(toString(transaction, docID));
//		List<StructuralSummaryNode> psList = psN.getChildren();
//		for (StructuralSummaryNode node : psList)
//		{
//			//System.out.println(String.format("Vergleiche PCR %s : %s == %s ?  %s == %s?", node.getPCR(), node.getVocID(), vocID, node.getNodeType(), nodeType));
//			if ((node.getVocID() == vocID) && (node.getNodeType() == nodeType))
//				return node.getPCR();
//		}
//		//append new Node!
//		return this.appendNodeLogged(transaction, docID, parentPcr, vocID, nodeType);		
//		//System.out.println(String.format("Child PCR von %s mit vocID=%s und type=%s nicht gefunden", parentPcr, vocID, nodeType));
//		//return -1;
//	}
//
//	public int[] getChildrenPCR(XTCtransaction transaction, int docID, int pcr) throws DBException
//	{
//		PathSynopsis ps = getStructuralSummary(transaction, docID);
//		StructuralSummaryNode psN = ps.getNodeByPcr(pcr);
//		List<StructuralSummaryNode> psList = psN.getChildren();
//		int[] childrenList = new int[psList.size()];
//		for(int i = 0; i < psList.size(); i++)
//		{
//			childrenList[i] = psList.get(i).getPCR();
//		}
//		return childrenList; 
//	}
//
//	public int getCount(XTCtransaction transaction, int docID, int pcr) throws DBException
//	{
//		PathSynopsis ps = getStructuralSummary(transaction, docID);
//		StructuralSummaryNode psN = ps.getNodeByPcr(pcr);
//		return psN.getCount();
//	}	
//	
//	public int getParentPCR(XTCtransaction transaction, int docID, int pcr) throws DBException
//	{
//		PathSynopsis ps = getStructuralSummary(transaction, docID);
//		StructuralSummaryNode psN = ps.getNodeByPcr(pcr);
//		return psN.getParent().getPCR();
//	}
//	
//	
//	public int getAncestorPCR(XTCtransaction transaction, int docID, int pcr, int level) throws DBException
//	{
//		PathSynopsis ps = getStructuralSummary(transaction, docID);
//		StructuralSummaryNode psN = ps.getNodeByPcr(pcr);
//		
//		if (level < 0)
//			throw new DBException(getClass(), "getAncestorPCR", "Invalid level: %s", level);
//			
//		if (psN.getLevel() < level)
//			throw new DBException(getClass(), "getAncestorPCR", "Requested level %s is higher than the level of PCR %s of document %s: %s", level, pcr, docID, psN.getLevel());
//		
//		while (psN.getLevel() > level)
//		{
//			psN = psN.getParent();
//		}
//		
//		return psN.getPCR();
//	}
//
//	public void renameNode(XTCtransaction transaction, int docID, int pcr, int vocID) throws DBException
//	{
//		PathSynopsis ps = getStructuralSummary(transaction, docID);
//		StructuralSummaryNode psN = ps.getNodeByPcr(pcr);
//		PathSynopsisConverter psc = this.metaDataMgr.getPathSynopsisConverter();
//		byte[] oldBytes = psc.getNodeAsBytes(new byte[psc.getNodeSize()], psN, ps.getPCRSize());
//		psN.setVocID(vocID);
//		byte[] newBytes = psc.getNodeAsBytes(new byte[psc.getNodeSize()], psN, ps.getPCRSize());
//		this.taMgr.addCompensationOperation(transaction, XTCcompensationOperation.OPERATIONTYPE_UPDATEINDEX, ps.getIndexNumber(), XTCcalc.getBytes(psN.getPCR()), oldBytes, newBytes);
//	}
//
//	public int appendPath(XTCtransaction transaction, int docID, int pcr, int[] vocIDs, byte[] nodeTypes) throws DBException
//	{
//		if(vocIDs.length != nodeTypes.length)
//			throw new DBException("The lengths of the two given arrays are not equal. This is no valid input");
//		PathSynopsis ps = getStructuralSummary(transaction, docID);
//		StructuralSummaryNode psN = ps.getNodeByPcr(pcr);
//		StructuralSummaryNode current = psN;
//		for(int i = 0; i < vocIDs.length; i++)
//		{
//			current = psN.hasChild(vocIDs[i], nodeTypes[i]);
//			if(current == null)
//			{
//				current = ps.getNewNode(vocIDs[i], nodeTypes[i]);
//				psN.addChild(current);
//			} 
//			psN = current;
//		}
//		return psN.getPCR();
//		// TODO: compensate operations
//	}
//	
//	/**
//	 * getVocID delivers a valid VocID for deweyIDs referring to an element or attribute name node
//	 * due to content/text deweyIDs are leveled deeper (having higher level counter) they return -1
//	 * attention - attribute nodes are attached to a virtual attribute root node which is represented
//	 *             within the related deweyID (that's the reason for this +1 hack
//	 */
//	public int getVocID(XTCtransaction transaction, int docID, DeweyID deweyID) throws DBException
//	{
////		System.out.println("get VocID for deweyID="+deweyID.toString()+" docID="+docID);
//		PathSynopsis ps = getStructuralSummary(transaction, docID);
//		StructuralSummaryNode psN = ps.getNode(deweyID);
////		if (psN == null ) System.out.println("got null node in path synopsis");
//		
//		//System.out.println("psN " + psN + " deweyID " + deweyID);
//		if (psN.getLevel() < deweyID.getLevel()) 
//		{
//			if(psN.getNodeType() == PathSynopsisMgr.NODETYPE_ATTRIBUTE && psN.getLevel() + 1 == deweyID.getLevel())
//				return psN.getVocID();
//			else
//				return -1;
//		}
//		else
//		return psN.getVocID();
//	}
//
//	public List<Integer> getLevelsForPCRandVocID(XTCtransaction transaction, int docID, int pcr, int vocID) throws DBException {
//		PathSynopsis ps = getStructuralSummary(transaction, docID);
//		return ps.getLevelsForPCRandVocID(pcr, vocID);
//	}
//
//	public PcrVocID getPcrVocID(XTCtransaction transaction, int docID, DeweyID deweyID) throws DBException {
//		PathSynopsis ps = getStructuralSummary(transaction, docID);
//		StructuralSummaryNode psN = ps.getNode(deweyID);
//		if (psN.getLevel() < deweyID.getLevel()) 
//		{
//			if(psN.getNodeType() == PathSynopsisMgr.NODETYPE_ATTRIBUTE && psN.getLevel() + 1 == deweyID.getLevel())
//				return new PcrVocID(psN.getPCR(),psN.getVocID());
//			else
//				return null;
//		}
//		else
//  		  return new PcrVocID(psN.getPCR(),psN.getVocID());
//	}
//
//	public void delete(XTCtransaction transaction, int docID) throws DBException {
//		this.synopses.remove(docID);
//	}
//
//	public int getPCRSize(XTCtransaction transaction, int docID) throws DBException {
//		return getStructuralSummary(transaction, docID).getPCRSize();
//	}
//      --------------------------------------------------------------------------
	///////////////////////
	//start of inclusion by Kamyar at 5.12.07
	////////////////////////////
	/**
	 * 
	 * 
	 */
	
public RGExecutionPlan getRGExecutionPlan(String indexName,QueryTreePattern QTP) throws DBException,IOException
	{
//		long st = System.currentTimeMillis();
//		long s = System.currentTimeMillis();
		StructuralSummaryTree ssTree = getStructuralSummary(indexName);//opens ssIndex
                
//		
                long e = System.currentTimeMillis();
//		System.out.println("Loading PathSynopsis: " + (e-s));
//		
		
//		s = System.currentTimeMillis();
		TwigStack twigStack = new TwigStack(indexName,QTP,ssTree);
		
		
		TwigJoinOp twRes = twigStack.Execute();
//		e = System.currentTimeMillis();
//		System.out.println("Executing query on PathSynopsis: " + (e-s));
		RGExecutionPlan execPlan = new RGExecutionPlan();

//		s = System.currentTimeMillis();
		TwigResult twigResult=twRes.next();
		int r = 0;
		
		ArrayList<QTPNode> qNodes=QTP.getNodes();
		
		HashMap<QTPNode, HashSet<Integer>> targetCIDs = new HashMap<QTPNode, HashSet<Integer>>(qNodes.size());
		for (int i = 0; i < qNodes.size(); i++)
		{
			targetCIDs.put(qNodes.get(i), new HashSet<Integer>());
		}
				
		while (twigResult != null)
		{	
			HashMap<QTPNode,StructuralSummaryNode> res =twigResult.getRes();
			RGExecutionPlanTuple execTuple = new RGExecutionPlanTuple(QTP, res);
			execPlan.add(execTuple);

			//targetPCRs calculates the number of distinct PCRs for all
			//nodes while some of them are not really target!!!
			for (QTPNode qNode: qNodes)
			{
				HashSet<Integer> pcrSet = targetCIDs.get(qNode);
				pcrSet.add(res.get(qNode).getCID());//az koja midunim k hamun karo mkinoe???
			}
			
			
//			for (Iterator<QTPNode> iterator2 = qNodes.iterator(); iterator2.hasNext();)
//			{
//				QTPNode node = iterator2.next();
//				String str = "[" + node.getName() + ": " + res.get(node).getPCR() + "] ";
//				System.out.print(str);				
//			}
//			System.out.println();
			++r;
			
			twigResult = twRes.next();
		}
		execPlan.setTargetCIDs(targetCIDs);
                logManager.LogManager.log(6,"Execution Plans are ready(line 6688 in class ssSum01)");
//		e = System.currentTimeMillis();
//		System.out.println("Execution plan construction: " + (e-s));
//
		//System.out.println("Number of plans: " + r);
//		long et = System.currentTimeMillis();
//		System.out.println("Total in function: " + (et-st));
		return execPlan;
	}//getRGExecutionPlan()
	 
//	private String getStructuralSummaryNodeName(String indexName, StructuralSummaryNode psNode) throws DBException
//	{
//		return metaDataMgr.resolveVocID(indexName, psNode.);
//	}//getStructuralSummaryNodeName
//	
	/**
	 * returns the path synopsis of a document in an XMl format
	 */
	public String getXMLStructuralSummary(String indexName) throws DBException
	{
            StructuralSummaryTree ps;
		try
		{
                    if(summaryTreeStorage.containsKey(indexName))
                         ps=summaryTreeStorage.get(indexName);
                    else 
                         ps = getStructuralSummary(indexName); 
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			DOMImplementation impl = builder.getDOMImplementation();

			Document xmlDoc = impl.createDocument(null,null,null);
			
			//int vocID = -1;
			Element elm = null;
			Stack<StructuralSummaryNode> psStack = new Stack<StructuralSummaryNode>();
			Stack<Element> elmStack = new Stack<Element>();
			StructuralSummaryNode psNode = ps.getRoot();
			//vocID = psNode.getVocID();
                                                     
			elm = xmlDoc.createElement(psNode.getQName());
			xmlDoc.appendChild(elm);
			//elm.setAttribute("VocID", Integer.toString(psNode.getVocID()));
                        elm.setAttribute("QulifiedName", psNode.getQName());
			elm.setAttribute("CID", Integer.toString(psNode.getCID()));
			psStack.push(psNode);			
			elmStack.push(elm);
			while (!psStack.isEmpty())
			{
				psNode = psStack.pop();
				elm = elmStack.pop();
				List<StructuralSummaryNode> chList = psNode.getChildren();
				int i = 0;
				StructuralSummaryNode chPsNode = null;
				Element chElm = null;
				for(; i < chList.size(); ++i)
				{
					chPsNode = chList.get(i);
					chElm = xmlDoc.createElement(chPsNode.getQName());
					elm.appendChild(chElm);
					chElm.setAttribute("QulifiedName", psNode.getQName());
					chElm.setAttribute("CID", Integer.toString(chPsNode.getCID()));
					psStack.push(chPsNode);
					elmStack.push(chElm);
				}				
			}

			// transform the Document into a String
			DOMSource domSource = new DOMSource(xmlDoc);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			//transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.ENCODING,"ISO-8859-1");
			//transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			java.io.StringWriter sw = new java.io.StringWriter();
			StreamResult sr = new StreamResult(sw);
			transformer.transform(domSource, sr);
			String xml = sw.toString();

			return xml;
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
			throw new DBException("Error in converting pathsynopsis to XML");
		}
	}// getXMLStructuralSummary()
	
//	/**
//	 * Returns list of (Level and PCR) of pathsynopsis node from  root to PCR node that matched the pathExpr
//	 * @param transaction
//	 * @param doc
//	 * @param PCR
//	 * @param pathExpr
//	 * @return
//	 * @throws DBException
//	 */
//	public TwinArrayList<Integer, Integer> findNodeMatches(XTCtransaction transaction, XTClocator doc, int PCR, String pathExpr) throws DBException
//	{
//		PathSynopsis ps = getStructuralSummary(transaction, doc.getID());
//		return ps.findNodeMatches(PCR, pathExpr);
//	}// findNodeMatches()
//	
//	/**
//	 * Returns levels of path synopsis nodes match path parameter in the root to PCR node path
//	 * 
//	 * @param path - list of VocID in the path ordered regards to their position in the main path
//	 * @param @param PCR - it is the pathsynopsis node that matching process is done from the root to this node
//	 */
//	public ArrayList<ArrayList<TwinObject<Integer, Integer>>> findPathMatches(XTCtransaction transaction, XTClocator doc, int PCR, ArrayList<Integer> path) throws DBException
//	{
//		PathSynopsis ps = getStructuralSummary(transaction, doc.getID());
//		return ps.findPathMatches(path, PCR);
//	}
//
//	/**
//	 * 
//	 * @param transaction
//	 * @param doc
//	 * @param vocID
//	 * @return number of pathsynopsis nodes have the same vocabulary ID as vocID 
//	 * @throws DBException
//	 */
//	public int getNodeCount(XTCtransaction transaction, XTClocator doc, int vocID) throws DBException
//	{
//		PathSynopsis ps = getStructuralSummary(transaction, doc.getID());
//		return ps.getNodeCount(vocID);
//	}
//
//	public Set<Integer> getPCRsForPath(XTCtransaction transaction, XTClocator doc, QTPNode qNode) throws DBException
//	{
//		if (qNode == null) return new HashSet<Integer>(0);
//		
//		HashSet<Integer> result = new HashSet<Integer>();
//		
//		QTPNode newQNode = null;
//		ArrayList<QTPNode> qNodePath = new ArrayList<QTPNode>(qNode.getLevel() + 1);
//		while (qNode != null)
//		{						
//			qNodePath.add(qNode);
//			qNode = qNode.getParent();
//		}
//		
//		qNode = qNodePath.get(qNodePath.size() - 1);
//		QTPNode root = newQNode = new QTPNode(qNode.getName(), qNode.getOpType(), null, qNode.getAxis());		
//		for (int i = qNodePath.size() - 2; i >=0; i--) 
//		{
//			qNode = qNodePath.get(i);
//			newQNode = new QTPNode(qNode.getName(), qNode.getOpType(), newQNode, qNode.getAxis());			
//		}
//		
//		QTPNode newQTPLeaf = newQNode;
//		
//		QueryTreePattern QTP = new QueryTreePattern(root);
//		
//		PathSynopsis ps = getStructuralSummary(transaction, doc.getID());
//
//		TwigStack twigStack = new TwigStack(transaction, doc, QTP, metaDataMgr, ps);
//		
//		TwigJoinOp twRes = twigStack.Execute();
//		
//		TwigResult twigResult = twRes.next();
//		
//		while (twigResult != null)
//		{
//			HashMap<QTPNode, StructuralSummaryNode> res = twigResult.getRes();
//			result.add(res.get(newQTPLeaf).getPCR());
//			twigResult = twRes.next();
//		}
//
//		return result;		
//	}

	///////////////////////
	//end of inclusion by Kamyar at 5.12.07
	////////////////////////////
		   

    
}

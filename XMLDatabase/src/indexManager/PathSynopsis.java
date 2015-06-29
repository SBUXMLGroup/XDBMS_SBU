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
//import xmlProcessor.xtcServer.DBException;
//import xtc.server.accessSvc.idxSvc.IndexServices;
//import xtc.server.metaData.pathSynopsis.converter.PathSynopsisConverter;
//import indexManager.StructuralSummaryManager; import indexManager.StructuralSummaryManager01;
//import xtc.server.metaData.pathSynopsis.manager.SimplePathStack.SimplePathStack;
//import indexManager.StructuralSummaryNode;
//import xtc.server.metaData.pathSynopsis.util.PathSynopsisNode01;
//import xmlProcessor.DeweyID;
//import xtc.server.taSvc.XTCtransaction;
//import xtc.server.util.PathRegExpProcessor;
//import xtc.server.util.TwinArrayList;
//import xmlProcessor.DBServer.utils.TwinObject;
//
//
///**
// * Realizes the path synopsis as a tree.
// * 
// * A path synopsis is a collection of path classes of 
// * an xml document. It is organized as a tree counting 
// * the number of the same classes in each node. 
// * A path class is a collection of the same types of 
// * parent-child related xml nodes. The names of the
// * xml nodes (attributes respectively) are stored as
// * ids referencing the actual names. These can be
// * resolved in the vocabulary manager.
// * 
// * Every node has a CID.
// * 
// * @author martinmeiringer
// * @author ks
// */
//
//public class PathSynopsis 
//{
//
//	private PathSynopsisNode root;
//	// insert new nodes as children of this node
//	private PathSynopsisNode current;
//	// maps pcrs to the corresponding nodes
//    private Map<Integer,PathSynopsisNode> pcr_hash = new HashMap<Integer,PathSynopsisNode>();
//    // counter for nodes...used for creating ids for nodes
//    private int nodeCount = 0;
//    //private final ReentrantLock nodeCountLock = new ReentrantLock();  // used to synchronize path synopsis modification and nodeCount
//    
//    // CID count
//    private int CID = 0;
//    
//    private IndexServices idxSvc = null;
//        
//    // dynamic CIDSize
//    private int CIDSize= 0;
//    
//    // What happens, when a node is deleted? 
//    // When a node is deleted one of two actions can be expected:
//    // 1. the node is deleted as root of a subtree (relationship between parent and node is 
//    // deleted.
//    // 2. the node is deleted and the children of the node are become children of the
//    // node's parent (for all nodes except root)
//    // At this moment the first method is used.
//    
//    private int psIdxNo = -1;
//    private String indexName;
//   
//    private final PathSynopsisConverter pathSynopsisConverter;
//    
//    ////////////////////////////
//    //inclusion by Kamyar 
//    ///////////////////////////////
//    //nodeIndexes index pathsynopsis node based on their VocID
//    //like an element index used for executing query on pathsynopsis
//    private HashMap<String, ArrayList<PathSynopsisNode>> nodeIndexes;
//    ////////////////////////////
//    //end of inclusion by Kamyar 
//    ///////////////////////////////
//
//    
//    public PathSynopsis(String indexName, PathSynopsisConverter pathSynopsisConverter, IndexServices idxSvc) 
//    {
//    	//this.psIdxNo = idxNo;
//        this.indexName=indexName;
//    	this.pathSynopsisConverter = pathSynopsisConverter;
//		this.idxSvc = idxSvc;
//		this.pcr_hash.clear();
//	    ////////////////////////////
//	    //inclusion by Kamyar 
//	    ///////////////////////////////
//	    nodeIndexes = new HashMap<String, ArrayList<PathSynopsisNode>>();
//	    ////////////////////////////
//	    //end of inclusion by Kamyar 
//	    ///////////////////////////////
//
//	}
//
//	/**
//	 * @return true, if write back function is enabled (idx and service set)
//	 * 		   that means changes in nodes are written back physically.	
//	 */
//	public synchronized boolean writeBack()
//	{
//		if(idxSvc != null)
//			return true;
//		return false;
//	}
//	
//	public void setCurrent(PathSynopsisNode psN)
//	{
//		this.current = psN;
//	}
//	
//	/**
//	 * @param node - node for which the path is created 
//	 * @return a String representation of the absolute path 
//	 * 		   of the given node up to the root node, starting
//	 * 		   from the root node down to the given node.
//	 */
//	public synchronized String getPath(PathSynopsisNode node)
//	{	// use string buffer
//		List<PathSynopsisNode> psNlist = new ArrayList<PathSynopsisNode>();
//		while(node != null)
//		{
//			psNlist.add(node);
//			node = node.getParent();
//		}
//		StringBuffer sBuff = new StringBuffer();
//		for(int i = psNlist.size() - 1; i >= 0; i--)
//		{	
//			node = psNlist.get(i);
//			switch(node.getNodeType()) 
//			{	// do something differently depending on the axis
//				case PathSynopsisMgr.NODETYPE_ATTRIBUTE:
//					sBuff.append('a');
//					break;
//				default:
//				case PathSynopsisMgr.NODETYPE_ELEMENT:
//					sBuff.append('c');
//					break;
//			}
//			sBuff.append(node.getVocID());
//			// switch to check 
//		}
//		return sBuff.toString();
//	}
//	
//	public synchronized PathSynopsisNode getNewNode(int vocId, byte nodeType)
//	{
//		PathSynopsisNode node = getNewNode(++CID, vocId, nodeType);
//		return node;
//	}
//
//	/**
//	 * @param CID - the id of the node to be created
//	 * @param vocId - the vocabulary id information
//	 * @param nodeType - the type of the node 
//	 * @return a node with the given information
//	 */
//	// depending on the implementation used a different type of node is returned
//	public synchronized PathSynopsisNode getNewNode(int pcr, int vocId, byte nodeType)
//	{
//		PathSynopsisNode psN = null;
//		psN = new PathSynopsisNode01(vocId, pcr, nodeType);	
//		pcr_hash.put(pcr, psN);
//		return psN;
//	}
//	
//	private PathSynopsisNode getNewNode(int vocId, byte nodeType, PathSynopsisNode parent)
//	{
//		PathSynopsisNode node = getNewNode(vocId, nodeType);
//		node.setParent(parent);
//		if(parent != null) parent.addChild(node);
//		return node;
//	}
//	
//	/**
//	 * Creates a node from the information given and appends to specific node.
//	 * 
//	 * @param transaction - the transaction to use when writing back
//	 * @param parentPCR - used as CID parent of created node
//	 * @param vocId - vocabulary id for node
//	 * @param nodeType - type of node
//	 * @return CID of the appended node
//	 */
//	public int appendNode(XTCtransaction transaction, int parentPCR, int vocId, byte nodeType)
//	{	
//		PathSynopsisNode parent = pcr_hash.get(parentPCR);
//		PathSynopsisNode node = null;
//		if(parent != null)
//		{	// create node with new id
//			node = parent.hasChild(vocId, nodeType);
//		} 
//		else
//		{
//			if(root != null)
//			{
//				if(root.getVocID() == vocId && root.getNodeType() == nodeType)
//					node = root;
//			}
//		}
//		if(node != null)
//		{ 
//			node.updateCount(1); // increment
//		}
//		else
//		{
//			node = getNewNode(vocId, nodeType, parent);
//			if(parent == null && root == null) 
//			{
//				root = node;
//			} 
//		}
//		// create new node
//		if(nodeType == PathSynopsisMgr.NODETYPE_ELEMENT)
//		{
//			current = node;
//		}
//		return node.getPCR();
//	}
//	
//	/**
//	 * if node still exists - only increment counter, but no logging
//	 * if node has to be appended - also logg / update path synopsis index structure
//	 * @param transaction
//	 * @param parentPCR
//	 * @param vocId
//	 * @param nodeType
//	 * @return
//	 * @throws DBException
//	 */
//	public int appendNodeLogged(XTCtransaction transaction, int parentPCR, int vocId, byte nodeType) throws DBException
//	{	
//		PathSynopsisNode node = null;
//		nodeCountLock.lock();
//	     try {
//	    	 PathSynopsisNode parent = pcr_hash.get(parentPCR);
//	    	 if(parent != null)
//	    	 {	// create node with new id
//	    		 node = parent.hasChild(vocId, nodeType);
//	    	 } 
//	    	 else
//	    	 {
//	    		 throw new DBException("PathSynopsis [appendNodeLogged]: No parent node found to append new node.");
//	    	 }
//	    	 if(node != null)
//	    	 { 
//	    		 node.updateCount(1); // increment
//	    		 // no path synopsis storage, due to lazzy updates of statistical information
//	    	 }
//	    	 else
//	    	 {
//	    		 node = getNewNode(vocId, nodeType, parent);
//	    		 this.pathSynopsisConverter.appendNode(transaction, node, this.psIdxNo, this.CIDSize);
//	    	 }
//	     } finally {
//	    	 nodeCountLock.unlock();
//	     }
//	     return node.getPCR();
//	}
//
//	/**
//	 * For a DeweyID (e.g. 1.3.5) get the node representing the CID
//	 * associated to the deweyId. (uses the CID)
//	 * 
//	 * @param id - a valid DeweyID of the document tree that has already been processed
//	 * @return id of the vocabulary entry
//	 * @throws DBException 
//	 */
//	public PathSynopsisNode getNode(DeweyID id) throws DBException
//	{
//		// if the pcr_hash is not existent we cannot find the vocId using the
//		// CID
//		if(pcr_hash == null)
//		{
//			throw new DBException("PathSynopsisNode [getNode] PCR hash is null");
//		}		
//		
//		// get the CID (key)
//		int key = id.getPCR();
//		if(key == -1)
//			throw new DBException("PathSynopsisNode [getNode] Passed deweyID " + id + " has no PCR");
//		
//		PathSynopsisNode node = pcr_hash.get(key);
//		if (node == null) // due to no node equal null should be in the hash map we can do this
//			throw new DBException("PathSynopsisNode [getNode] PCR hash does not contain key " + key);
//    	// if the node's level is smaller than the deweyId's then 
//    	// no parent (or self) of node can be 
//    	// a representation of the deweyId
//    	// check all parents (or self) if level is the same 
////		System.out.println("got node with CID="+node.getPCR() + " level="+node.getLevel()+ " vocID="+node.getVocID() +" lookup PCR="+key);
////		System.out.println("node.lvl="+ node.getLevel()+" id.lvl="+id.getLevel());
//		// TODO: can a node.getParent() delivers null in case of semantic correct deweyIDs??
//    	while(/*node.getParent() != null && */node.getLevel() > id.getLevel()) 
//    	{
////        	System.out.println("step up to parent...  node.lvl="+ node.getLevel()+" id.lvl="+id.getLevel());
//    		node = node.getParent();
//    	}
//    	return node;
//	}
//		
//	/**
//	 * Removes the node from the tree and deletes all registered
//	 * pcrs.
//	 * 
//	 * @param node - the root of the tree to be deleted
//	 * @return the given node, updated
//	 */
//	public PathSynopsisNode deleteSubtree(PathSynopsisNode node)
//	{	// first remove the double linked relation between child and parent
//		node.getParent().removeChild(node);
//		node.unsetParent();
//		// then delete all pcrs from the child's subtree
//		deletePcrsFromSubtree(node);
//		return node;
//	}
//	
//	/**
//	 * Removes all pcrs of the given subtree rekursively.
//	 * 
//	 * @param node - subtree of path synopsis tree.
//	 */
//	public void deletePcrsFromSubtree(PathSynopsisNode node)
//	{	// delete CID of current node
//		if(pcr_hash.containsKey(node.getPCR())) pcr_hash.remove(node.getPCR());
//				
//		PathSynopsisNode currentChild;
//		List<PathSynopsisNode> psnList = node.getChildren();
//		for(int i = 0; i < psnList.size(); i++)
//		{	//iterate over the children of the node, deleting their pcrs if exist
//			currentChild = psnList.get(i);
//			deletePcrsFromSubtree(currentChild);
//		}
//	}
//	
//	/**
//	 * Deletes given node from tree. Children of node are added at end of parents'
//	 * children list.
//	 * 
//	 * @param node - a node of the tree
//	 * @return deleted node
//	 */
//	public PathSynopsisNode deleteNodeFromTree(PathSynopsisNode node) 
//	{	// check for special cases...
//		if(node.equals(current))
//		{
//			if(node.getParent() != null)
//				current = node.getParent();
//			else 
//				current = root;
//		}
//		if(node.equals(root))
//		{
//			root = null;
//			current = null;
//		}	
//		// CID handling
//		if(this.pcr_hash.containsKey(node.getPCR()))
//			this.pcr_hash.remove(node.getPCR());
//		if(node.hasChildren()) 
//		{	// if the node to be deleted has children, append them at the correct position 
//			// of the parent's childlist
//			LinkedList<PathSynopsisNode> newList = new LinkedList<PathSynopsisNode>();
//			PathSynopsisNode parent = node.getParent();
//			List<PathSynopsisNode> psnList = parent.getChildren();
//			PathSynopsisNode child = null;
//			// first add all children 
//			for(int i = 0; i < psnList.size();i++)
//			{
//				child = psnList.get(i);
//				if(!child.equals(node))
//				{
//					newList.add(child);	
//				}
//			}
//			// then add all children of the node that is to be deleted
//			psnList = node.getChildren();
//			for(int i = 0; i < psnList.size();i++)
//			{
//				child = psnList.get(i);
//				newList.add(child);
//			}
//			parent.setChildList(newList);
//		}
//		// delete the parent-child relationship
//		node.setParent(null);
//		// delete the node's children-parent relationship
//		node.unsetChildren();
//		return node;
//	}
//	
//	/**
//	 * Gets a list of nodes conforming to the attributes in the two arrays. 
//	 * The first entries of the two arrays must be the current's attributes.
//	 * 
//	 * @param vocIds - vocabulary ids identifying nodes in the tree
//	 * @param nodeTypes - types of node vocId + nodeType >> unique
//	 * @return a List of nodes representing the path
//	 */
//	// The reason for the attributes of the startnode being included is that
//	// the first insert of a path can create a root making the path start from
//	// the root instead of every next inserted path starting at child level of the root.
//	// This results in ambigiouity. 
//	public List<PathSynopsisNode> getPathIfExists(int[] vocIds, byte[] nodeTypes)
//	{
//		return getPathIfExists(current, vocIds, nodeTypes);
//	}
//	
//	/**
//	 * Gets a list of nodes conforming to the attributes in the two arrays. 
//	 * The first entries of the two arrays must be startNode's attributes
//	 * 
//	 * @param startNode - the node to start searching from
//	 * @param vocIds - vocabulary ids identifying nodes in the tree
//	 * @param nodeTypes - types of node vocId + nodeType >> unique
//	 * @return a List of nodes representing the path
//	 */
//	public List<PathSynopsisNode> getPathIfExists(PathSynopsisNode startNode, int[] vocIds, byte[] nodeTypes)
//	{
//		if(vocIds.length != nodeTypes.length) return null;
//		if(startNode == null) return null;
//		int length = vocIds.length;
//		List<PathSynopsisNode> list = new LinkedList<PathSynopsisNode>();
//		// check startNode...
//		if(startNode.getVocID() != vocIds[0] || startNode.getNodeType() != nodeTypes[0]) return null; 
//		list.add(startNode);
//		// then check all descendants
//		for(int i = 1; i < length; i++)
//		{
//			startNode = startNode.hasChild(vocIds[i], nodeTypes[i]);
//			if(startNode == null) return null;
//			list.add(startNode);
//		}
//		return list;
//	}
//	
//	/**
//	 * The <b>last</b> entry of the vocIds array represents the node to be inserted directly
//	 * underneath the current node. The <b>first</b> entry is given a CID.
//	 * 
//	 * @param transaction - the transaction to use when writing back
//	 * @param parent - the path is appended to this node
//	 * @param vocIds - array of vocabulary identifiers
//	 * @param nodeTypes - array of according node types
//	 * @return the distributed CID
//	 * @throws DBException
//	 */
//	public int appendPathUpward(XTCtransaction transaction, int parentPCR, int[] vocIds, byte[] nodeTypes) throws DBException
//	{
//		return appendPath(transaction, parentPCR, vocIds, nodeTypes, false);
//	}
//	
//	/**
//	 * The <b>first</b> entry of the vocIds array represents the node to be inserted directly
//	 * underneath the current node. The <b>last</b> entry is given a CID.
//	 * 
//	 * @param transaction - the transaction to use when writing back
//	 * @param parent - the path is appended to this node
//	 * @param vocIds - array of vocabulary identifiers
//	 * @param nodeTypes - array of according node types
//	 * @return the distributed CID
//	 * @throws DBException
//	 */
//	public int appendPathDownward(XTCtransaction transaction, int parentPCR, int[] vocIds, byte[] nodeTypes) throws DBException
//	{
//		return appendPath(transaction, parentPCR, vocIds, nodeTypes, true);
//	}	
//	
//	/**
//	 * Creates a complete path with a CID for the last element of the array. 
//	 * 
//	 * @param transaction - the transaction to use when writing back
//	 * @param parent - begin appending at this element
//	 * @param vocIds - the ids to append
//	 * @param nodeTypes - the types of nodes to append
//	 * @param rootDown - in which order is the array sorted? root down, or leaf up?
//	 * @return the distributed CID for the last element
//	 * @throws DBException - when the array lengths differ
//	 */
//	private int appendPath(XTCtransaction transaction, int parentPCR, int[] vocIds, byte[] nodeTypes, boolean rootDown) throws DBException
//	{
//		if(vocIds.length != nodeTypes.length) throw new DBException("[XTCpathSynopsisTree] - appendPath: Pathlength cannot be determined.");
//		int length = vocIds.length;
//		// walk through array
//		int index;
//		if(rootDown)
//			index = 0; // start with this
//		else
//			index = length - 1; // start with this
//
//		PathSynopsisNode parent;
//		while(rootDown && index < length || !rootDown && index >= 0)
//		{	// traverse through array either up or down
//			parentPCR = appendNode(transaction, parentPCR, vocIds[index], nodeTypes[index]);
//			parent = pcr_hash.get(parentPCR);
//			if(parent == null)
//				System.out.println("Node was not registered!");
//			if(rootDown) index++;
//			else index--;
//		}
//		// the last node is given a CID
//		return parentPCR;
//	}
//	
//	/**
//	 * Gets the node associated with the given CID.
//	 * 
//	 * @param CID - identifies a node with this
//	 * @return the associated node 
//	 */
//	public PathSynopsisNode getNodeByPcr(int pcr)
//	{
//		Integer pcrAsInt = new Integer(pcr);
//		if(!pcr_hash.containsKey(pcrAsInt))
//			return null;
//		return pcr_hash.get(pcrAsInt);
//	}
//	
//	/**
//	 * Returns a set of pcrs conforming to the given path.
//	 * 
//	 * @param path - 	first entry's vocId is equal to leaf node with CID  
//	 * @return	a set of nodes qualifying as beginning of the requested path
//	 */
//	public synchronized HashSet<Integer> getPCRsForPath(int[] path)
//	{
//		return getPCRsForPath(path, null);
//	}
//	
//	/**
//	 * Returns a set of pcrs conforming to the given path.
//	 * Path starts at highest element (common ancestor to every node in path).
//	 * 
//	 * @param path 		- 	first entry's vocId is equal to leaf node with CID  
//	 * @param nodeType 	- 	to each path entry an axis (a nodeType) to match the node's type
//	 * @return	a set of nodes qualifying as beginning of the requested path
//	 */
//    public HashSet<Integer> getPCRsForPath(int[] path, byte[] nodeType)
//	{	// the return set...set is returned empty if no nodes found...
//		HashSet<Integer> returnSet = new HashSet<Integer>();
//		Collection<PathSynopsisNode> pcr_hash = this.pcr_hash.values();
//		Iterator<PathSynopsisNode> it = pcr_hash.iterator();  // create an iterator on the pcr_hash
//		PathSynopsisNode node;
//		boolean candidateIsGood; // a CID is a candidate as long as the tested path 
//		// is still equal to the given path
//		int stepInPath; // number of elements of array are equal to the given path's
//		int pathLength = path.length;
//		while(it.hasNext()) 
//		{	// initialize step
//			stepInPath = 1; // offset from array LENGTH
//			candidateIsGood = true;
//			node = it.next(); // get a leaf node
//			int pcr_candidate = node.getPCR(); // set CID as candidate
//			while(node != null && candidateIsGood && stepInPath < pathLength)
//			{	// path stays valid only if the according vocIds are equal
//				if(node.getVocID() != path[pathLength - stepInPath]) 
//					candidateIsGood = false;
//				else if(nodeType != null)
//					if(node.getNodeType() != nodeType[pathLength - stepInPath]) 
//						candidateIsGood = false;
//				stepInPath++; // step up
//				node = node.getParent();
//			}
//			if(stepInPath == path.length && candidateIsGood)
//			{
//				returnSet.add(pcr_candidate); // the pcr_candidate becomes a winner
//			} 
//		} // return the resulting pcrs as set
//		return returnSet;
//	}
//
//    public HashSet<Integer> getSubVocIDsForVocID(int vocID)
//    {
//    	return getSubVocIDsForVocID(vocID,false,this.getRoot());
//    }
//    
//    public HashSet<Integer> getSubVocIDsForVocID(int vocID, boolean collect, PathSynopsisNode startNode)
//    {
//		HashSet<Integer> returnSet = new HashSet<Integer>();
//		
//		if (collect) returnSet.add(startNode.getVocID());
//		
//		if (startNode.getVocID() == vocID) collect = true;
//		
//		if (startNode.hasChildren())
//		{
//			List<PathSynopsisNode> children = startNode.getChildren();
//			Iterator<PathSynopsisNode> child_it = children.iterator();
//			while (child_it.hasNext())
//			{
//				returnSet.addAll(getSubVocIDsForVocID(vocID,collect,child_it.next()));
//			}
//		}	
//		return returnSet;
//	}
//    
//    public Set<Integer> getPCRsForPath(String queryString) 
//    {
//		Set<Integer> returnSet = new HashSet<Integer>();
//		// get CID map
//		Map<Integer, PathSynopsisNode> map = this.pcr_hash;
//		Set<Integer> keySet = map.keySet();
//		Iterator<Integer> iterator = keySet.iterator();
//		Integer currentKey;
//		PathSynopsisNode currentNode;
//		String currentPath;
//		PathRegExpProcessor regMatcher = new PathRegExpProcessor();
//		// set path as pattern
////		System.out.println("RegPath: "+queryString);
//		regMatcher.setPattern(queryString);
////		System.out.println("RegMatcher Pattern: "+regMatcher.getPatternString());
//		while(iterator.hasNext())
//		{	// get each node
//			currentKey = iterator.next();
//			currentNode = map.get(currentKey);
//			currentPath = this.getPath(currentNode);
////			System.out.println("currentPath for "+currentKey+": "+currentPath);
//			if(regMatcher.match(currentPath))
//			{	// if the paths match add this node to the return set
//				returnSet.add(currentNode.getPCR());
////				System.out.println("matched "+currentKey);
//			}
//		}
//		return returnSet;
//	}
//    
//    // getters and setters
//	public PathSynopsisNode getRoot()
//	{
//		return root;
//	}
//
//	public PathSynopsisNode getCurrent()
//	{
//		return current;
//	}
//
//	public int getNodeCount()
//	{
//		return nodeCount;
//	}
//
//	public int getPcr() {
//		return CID;
//	}
//
//	public void setPcr(int pcr) {
//		this.CID = pcr;
//	}
//
//	public int getPCRSize() {
//		return CIDSize;
//	}
//
//	public void setPCRSize(int pcrSize) {
//		this.CIDSize = pcrSize;
//	}
//
//	public void setRoot(PathSynopsisNode root) {
//		this.root = root;
//	}
//	
//	public void setNodeCount(int nodeCount)
//	{
//		this.nodeCount = nodeCount;
//	}
//	
//	public void setIndexServices(IndexServices idxSvc)
//	{
//		this.idxSvc = idxSvc;
//	}
//	
//	public void setIndexNumber(int idxNo)
//	{
//		this.psIdxNo = idxNo;
//	}
//
//	public int getIndexNumber() {
//		return psIdxNo;
//	}
//
//	public static List<PathSynopsisNode> traverseRecursively(List<PathSynopsisNode> returnNodes, PathSynopsisNode node)
//	{
//		if(returnNodes == null)
//		{
//			returnNodes = new ArrayList<PathSynopsisNode>();
//		}
//		if(node.hasChildren())
//		{
//			List<PathSynopsisNode> children = node.getChildren();
//			for(int i = 0; i < children.size(); i++)
//			{
//				returnNodes.addAll(traverseRecursively(null, children.get(i)));
//			}
//		} 
//		returnNodes.add(node);
//		return returnNodes;
//	}
//	
//	public void printHashedNodes()
//	{
//		Set<Integer> 	  keySet 		 = this.pcr_hash.keySet();
//		Iterator<Integer> keySetIterator = keySet.iterator();
//		Integer current;
//		PathSynopsisNode currentNode;
//		while(keySetIterator.hasNext())
//		{
//			current 	= keySetIterator.next();
//			currentNode = pcr_hash.get(current);
//			System.out.println(currentNode.toString());
//		}
//	}
//	
//	public List<Integer> getLevelsForPCRandVocID(int pcr, int vocID) throws DBException
//	{
//		PathSynopsisNode node = this.pcr_hash.get(pcr);
//		if(node == null)
//			throw new DBException("PathSynopsis[getLevelsForPCRandVocID] - pcr "+pcr+" did not retrieve a ");
//		List<Integer> levelList = new ArrayList<Integer>();
//		while(node != null)
//		{
//			if(node.getVocID() == vocID)
//				levelList.add(node.getLevel());
//			node = node.getParent();
//		}
//		return levelList;
//	}
//
//	public int appendNodeCreate(int vocID, byte nodeType) {
//		if (root == null)
//		{
//			root = getNewNode(vocID, nodeType, current);
//			current = root;
//			return current.getPCR();
//		}
//		PathSynopsisNode node = null;
//		node = current.hasChild(vocID, nodeType);
//		if(node != null)
//		{ 
//			node.updateCount(1);
//		}
//		else
//		{
//			node = getNewNode(vocID, nodeType, current);
//		}
//		if(nodeType == PathSynopsisMgr.NODETYPE_ELEMENT)
//		{
//			current = node;
//		}
//		return node.getPCR();
//		
//	}
//	
//	public String toString()
//	{
//		StringBuffer buffer = new StringBuffer("path synopsis (Format [PCR:VocID:Count] ):");
//		
//		ArrayList<StringBuffer> levelBuffers= new ArrayList<StringBuffer>();
//		printNode(root, levelBuffers);
//		
//		for (StringBuffer buf : levelBuffers)
//		{
//			buffer.append("\n");
//			buffer.append(buf);
//		}
//		buffer.append("\n Max PCR="+this.CID);
//		return buffer.toString();
//	}
//	
//	private void printNode(PathSynopsisNode node, ArrayList<StringBuffer> levelBuffers)
//	{
//		if (levelBuffers.size() -1 < node.getLevel())
//		{
//			levelBuffers.add(new StringBuffer());
//		}
//		
//		for (PathSynopsisNode child : node.getChildren())
//		{
//			printNode(child, levelBuffers);
//		}
//		
//		StringBuffer buf = levelBuffers.get(node.getLevel());
//		
//		String str = String.format("[%s:%s:%s]  ", node.getPCR(), (node.getNodeType() == PathSynopsisMgr.NODETYPE_ATTRIBUTE) ? "@" + node.getVocID() : node.getVocID(), node.getCount());
//		buf.append(str);
//		
//		if (levelBuffers.size() -1 > node.getLevel()) // child level exists
//		{
//			int indent = levelBuffers.get(node.getLevel() + 1).length() - buf.length();
//			for (int i = indent + 2; i > 0; i--)
//				buf.append(" ");
//		}
//	}
//
//	///////////////////////
//	//Start of inclusion by Kamyar
//	////////////////////////////
//	public void setRangeIDs()
//	{
//		int lRange = 0;
//		int level = 0;
//		PathSynopsisNode psNode = getRoot();
//		int rPos = setRangeIDs(psNode, lRange, level);
//	}
//
//	//This function labels pathsynopsis tree by walking in pre-order as described in TwigStack papers 
//	private int setRangeIDs(PathSynopsisNode psNode, int lRange, int level)
//	{
//		if (!psNode.hasChildren())
//		{
//			psNode.getRangeID().setLeft(lRange);
//			psNode.getRangeID().setRight(lRange);
//			psNode.getRangeID().setLevel(level);			
//			ArrayList<PathSynopsisNode> nodeIndex = nodeIndexes.get(psNode.getVocID());
//			if (nodeIndex == null)
//			{
//				nodeIndex = new ArrayList<PathSynopsisNode>();
//				nodeIndexes.put(psNode.getVocID(), nodeIndex);//getName
//			}
//			nodeIndex.add(psNode);
//
//			return lRange;
//		}
//		
//		psNode.getRangeID().setLeft(lRange);
//		psNode.getRangeID().setLevel(level);
//		
//		ArrayList<PathSynopsisNode> nodeIndex = nodeIndexes.get(psNode.getVocID());
//		if (nodeIndex == null)
//		{
//			nodeIndex = new ArrayList<PathSynopsisNode>();
//			nodeIndexes.put(psNode.getVocID(), nodeIndex);
//		}
//		nodeIndex.add(psNode);
//		
//		List<PathSynopsisNode> children = psNode.getChildren();
//		
//		for (int i = 0; i < children.size(); i++)
//		{
//			lRange = setRangeIDs(children.get(i), lRange + 1, level + 1);
//		}
//		
//		lRange++;
//		psNode.getRangeID().setRight(lRange);		
//		return lRange;
//	}
//	
//	/**
//	 * Returns list of (Level and PCR) of pathsynopsis node from  root to PCR node that matched the pathExpr   
//	 * @param PCR
//	 * @param pathExpr
//	 * @return
//	 */
//	public TwinArrayList<Integer, Integer> findNodeMatches(int PCR, String pathExpr)
//	{		
//		TwinArrayList<Integer, Integer> result = new TwinArrayList<Integer, Integer>();
//		PathSynopsisNode psNode = pcr_hash.get(PCR);
//		
//		
//		PathRegExpProcessor regMatcher = new PathRegExpProcessor();
//		// set path as pattern
//		regMatcher.setPattern(pathExpr);
//		
//		while(psNode != null)
//		{
//			String path = this.getPath(psNode);
//			if(regMatcher.match(path))
//			{	// if the paths match add node level to the return set
//				result.add(0, psNode.getLevel(), psNode.getPCR());
//			}
//			psNode = psNode.getParent();
//		}
//
//		return result;
//	} //findNodeMatches
//	
//	/**
//	 * Returns levels of path synopsis nodes match path parameter in the root to PCR node path 
//	 * 
//	 * @param path - list of VocID in the path ordered regards to their position in the main path
//	 * @param PCR - it is the pathsynopsis node that matching process is done from the root to this node
//	 */
//	public ArrayList<ArrayList<TwinObject<Integer, Integer>>> findPathMatches(ArrayList<Integer> path, int PCR) throws DBException
//	{
//		PathSynopsisNode psNode = getNodeByPcr(PCR);
//		ArrayList<PathSynopsisNode> data = new ArrayList<PathSynopsisNode>(psNode.getLevel() + 1);
//		while (psNode != null)
//		{			
//			data.add(0, psNode);
//			psNode = psNode.getParent();
//		}
//
//		SimplePathStack pathStack = new SimplePathStack(path, data);
//		return pathStack.execute();
//	}
//	
//	public ArrayList<PathSynopsisNode> getNodeIndex(int vocID)
//	{
//		return nodeIndexes.get(vocID);
//	}
//
//	public int getNodeCount(int vocID)
//	{
//		ArrayList<PathSynopsisNode> res = nodeIndexes.get(vocID);
//		if (res != null) return res.size();
//		
//		return 0;
//	}
//
//	////////////////////////////
//	//end of inclusion by Kamyar
//	////////////////////////////
//
//}
//
//    
//

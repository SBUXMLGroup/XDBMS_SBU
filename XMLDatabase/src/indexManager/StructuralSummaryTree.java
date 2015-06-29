package indexManager;

import com.sun.org.apache.xml.internal.dtm.ref.DTMDefaultBaseIterators;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import indexManager.Iterators.StructuralSummaryIterator;
import java.io.IOException;
import java.lang.NullPointerException;
import xmlProcessor.DBServer.DBException;
public class StructuralSummaryTree
{
    private indexManager.StructuralSummaryNode ssRoot;
    //nodeIndexes index structural summary node based on their qName
    //like an element index used for executing query on structural summary
    private HashMap<String, ArrayList<StructuralSummaryNode>> nodeIndexes;

    //private ArrayList<StructuralSummaryNode> childrenList;
    
   public StructuralSummaryTree()
   {
       ssRoot=new StructuralSummaryNode();
       nodeIndexes = new HashMap<String, ArrayList<StructuralSummaryNode>>();
      //should first pull data from ssIndex and then build a tree on top of it.  
   }
   public StructuralSummaryTree(StructuralSummaryIndex ssIndx) throws IOException, DBException
   {
       //creates a ssTree from Index readen from file
       //StructuralSummaryIterator ssNodeProducer=new StructuralSummaryIterator(ssIndx);
       StructuralSummaryIterator ssNodeProducer=ssIndx.getStream();
       StructuralSummaryNode ssNode;
       StructuralSummaryNode parent;
       ArrayList<StructuralSummaryNode> appended=new ArrayList<>();
       ssNodeProducer.open();//Iterator opens access to ssIndx;
       ssNode=ssNodeProducer.next();
       if(ssNode!=null && ssNode.getCID()==1)
       {
           ssRoot=new StructuralSummaryNode();//we can replace these two lines with one instruction
           setUPRoot(ssNode.getCID(), ssNode.getQName(), ssNode.getParentCID(),1);
       }
       int counter=0;
       do //chon y bar ghablan next zadim,moshkeli nis.
       {
           ssNode=ssNodeProducer.next();
           if(ssNode!=null)// Oure ka!
           {
               if(ssNode.getParentCID()==1)
               {   
                   if(!this.ssRoot.hasChild(ssNode))
                   {    this.ssRoot.appendChild(ssNode);
                       appended.add(ssNode);
                   }
                   //else ssNode does not get appended
               }
               else
               {
                   //String temp=ssNode.getQName();
                   parent=lookUp(ssNode.getParentCID(),appended);
                   if(parent==null)
                       System.out.println("caught!");
                   if(!parent.hasChild(ssNode))
                   {
//                       if(counter>504)
//                          System.out.println("Iteration:"+ ++counter);
                       //System.out.println(ssNode.getCID()+" "+ssNode.getQName()+" "+ssNode.getParentCID());
                       parent.appendChild(ssNode);//???Is it necessary to find its path?or no?
                       appended.add(ssNode);
                   }
               }
           } 
           //else
             //  throw new NullPointerException();
         
       }while(ssNode!=null );//&& counter!=504
            
       nodeIndexes = new HashMap<String, ArrayList<StructuralSummaryNode>>();
      
   }
   public void setUPRoot(int CID,String qName,int parentCID,int level)
   {
       //////shak daram:???
       ssRoot.setCID(CID);
       ssRoot.setParentCID(parentCID);
       ssRoot.setQName(qName);
       ssRoot.setLevel(level);
   }
   private StructuralSummaryNode lookUp(int CID,ArrayList<StructuralSummaryNode> appended)
   {
       
       for (int i=0;i<appended.size();i++)
       {
           if(appended.get(i).getCID()==CID)
               return appended.get(i);
           
       }
       return null;
   }
      
    public StructuralSummaryNode getRoot()
   {
       return ssRoot;
   }
   public void appendChild(StructuralSummaryNode childNode)
   {
       //I have implemented this func in ssnode,
       //why is this func for?
   }
   
   public void getCidByName(String qName){}
  /**unused:
   * public qNameList getNameListByCid(int CID)
   {
       
   }
   public getPathbyCID()
   {
       
   }*/
   public ArrayList<StructuralSummaryNode> getNodeIndex(String qName)
	{
                //changed qName to vocID:
		return nodeIndexes.get(qName);
	}
   public void setRangeIDs()
	{
		int lRange = 0;
		int level = 0;
		StructuralSummaryNode psNode = getRoot();
		int rPos = setRangeIDs(psNode, lRange, level);
	}
  
   private int setRangeIDs(StructuralSummaryNode psNode, int lRange, int level)
	{
	   try{	
                if (psNode.getChildren().isEmpty())
		{
			psNode.getRangeID().setLeft(lRange);
			psNode.getRangeID().setRight(lRange);
			psNode.getRangeID().setLevel(level);			
			ArrayList<StructuralSummaryNode> nodeIndex = nodeIndexes.get(psNode.getQName());
			if (nodeIndex == null)
			{
				nodeIndex = new ArrayList<StructuralSummaryNode>();
				nodeIndexes.put(psNode.getQName(), nodeIndex);//getName
			}
			nodeIndex.add(psNode);//yani baray qname=firstName gereye firstname ra set mikonim

			return lRange;
		}
		
		psNode.getRangeID().setLeft(lRange);
		psNode.getRangeID().setLevel(level);
		
		ArrayList<StructuralSummaryNode> nodeIndex = nodeIndexes.get(psNode.getQName());
		if (nodeIndex == null)
		{
			nodeIndex = new ArrayList<StructuralSummaryNode>();
			nodeIndexes.put(psNode.getQName(), nodeIndex);
		}
		nodeIndex.add(psNode);
		
		List<StructuralSummaryNode> children = psNode.getChildren();
		
		for (int i = 0; i < children.size(); i++)
		{
			lRange = setRangeIDs(children.get(i), lRange + 1, level + 1);
		}
		
		lRange++;
		psNode.getRangeID().setRight(lRange);		
		return lRange;
           }
           catch(Exception e)
           {
               logManager.LogManager.log("Excep happened");
               throw e;
           }
	}
	
}

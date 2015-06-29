package indexManager;

import commonSettings.Settings;
import java.util.ArrayList;
import xmlProcessor.DeweyID;
import indexManager.RangeID.RangeID;


public class StructuralSummaryNode 
{
  private String qName;
  private int CID;
  private int parentCID;
  private StructuralSummaryNode parent;
  protected boolean levelComputed = false;
  //private DeweyID deweyID;
  private int level;
  /////////FOR Handling attribs and contents:
  //private int nodeType;//{element:1 , attrib:2 , content:3}
  //private ArrayList<StructuralSummaryNode> attribs;
  /////////////
  private ArrayList<StructuralSummaryNode> childrenList;
 
//    public StructuralSummaryNode(String qName, int CID, int parentCID, ArrayList<StructuralSummaryNode> childrenList)
//    {
//        this.qName = qName;
//        this.CID = CID;
//        this.parentCID = parentCID;
//        this.childrenList=childrenList;
//    }
 
  
  public StructuralSummaryNode(int CID,String qName,int parentCID)
  {
      this.CID=CID;
      this.qName=qName;
      this.parentCID=parentCID;    
  }
  /**
   * not being used already
   * public StructuralSummaryNode(DeweyID deweyID,String qName,int parentID)
  { 
      this.deweyID=deweyID;
      this.CID=deweyID.getCID();
      this.qName=qName;
      this.parentCID=parentID;    
       
  }
  //improved:
   public StructuralSummaryNode(DeweyID deweyID,String qName,StructuralSummaryNode parent)
  { 
      this.deweyID=deweyID;
      this.CID=deweyID.getCID();
      this.qName=qName;
      this.parent=parent;
      this.parentCID=parent.getCID();    
       
  }*/
  public StructuralSummaryNode()
  {
      this.CID=0;
      this.qName=null;
      this.parentCID=-1; 
      childrenList=new ArrayList<>();
  }
    public StructuralSummaryNode(String qName)
  {
      this.CID=0;
      this.qName=qName;
      this.parentCID=-1; 
      childrenList=new ArrayList<>();
  }
    

  ////////////copied/////////////
  private RangeID rangeID = null;
    
    public void setRangeID(RangeID rangeID)
    {
    	this.rangeID = rangeID;
    }
    
    public RangeID getRangeID()
    {
    	if (this.rangeID == null) this.rangeID = new RangeID();
    	return this.rangeID;
    }
    ////end of copied//////////////////////////////////
  public void appendChild(StructuralSummaryNode childNode)
  {
      childNode.setParent(this);
      
      childrenList.add(childNode);      
  }
  public boolean hasChild(StructuralSummaryNode newborn)
   {
       boolean present=false; //newborn exists in list or not
       for(StructuralSummaryNode child : childrenList )
       {
           if(child.getCID()==newborn.getCID() && (child.getQName() == null ? newborn.getQName() == null : child.getQName().equals(newborn.getQName())))
           {   
               present=true;
               break;
           }
       }
       return present;
   }
  //////////Getters & Setters:
  public void setParentCID(int parentCID)
  {
      this.parentCID=parentCID;
  }
  public int getParentCID()
  {
      return parentCID;
  }
  
  public StructuralSummaryNode getParent()
  {
      return parent;
  }
  public void setParent(StructuralSummaryNode parent)
  {
    this.parent = parent;
    this.parentCID=parent.getCID();
    this.levelComputed = false;
  }
  public void setCID(int CID)
  {
      this.CID=CID;
  }
  public int getCID()
  {
      return CID;
  }
  public void setQName(String qName)
  {
      this.qName=qName;
  }
  public String getQName()
  {
      return qName;
  }
  public indexManager.StructuralSummaryNode getLastChild()
  {
      int lastIndex=childrenList.size()-1;
      return childrenList.get(lastIndex);
  }
  //////////////////
  public ArrayList<StructuralSummaryNode> getChildren()
  {
      return childrenList;
  }
  /**
  public boolean leafNodeSearch(DeweyID deweyId) 
  {
        //dar barg ha bayd pagePointer hamun null bashand,ya aslan nabashand.(tu method insert taiin mishe)
        int pagePointer = -1; //null
        int length;
        byte[] curId;
        DeweyID curDId ; 
        DeweyID result = null;
        InternalBuffer.position(Settings.dataStartPos);//3+4+4??????
        if(numOfKeys>0)
        for (int i = 0;i < numOfKeys; i++)
        {
            pagePointer = InternalBuffer.getInt();//next page adrs
            length = InternalBuffer.getInt();
            curId = new byte[length];
            curDId=new DeweyID();
            InternalBuffer.get(curId, 0, length);
            curDId.setDeweyId(curId);
            result = curDId;
            if (curDId.equals(deweyId)) {
                return true; //key found
            } else if (!(curDId.lessThan(deweyId))) {
                return false; //key not found
            }
        }
        return false;//not found

    }  
  //***MUST COMPLETe:
  public String qNameSearch(DeweyID deweyId)
  {
        //dar barg ha bayd pagePointer hamun null bashand,ya aslan nabashand.(tu method insert taiin mishe)
        int pagePointer = -1; //null
        int length;
        byte[] curId;
        DeweyID curDId ; 
        DeweyID result = null;
        InternalBuffer.position(Settings.dataStartPos);//3+4+4??????
        if(numOfKeys>0)
        for (int i = 0; i < numOfKeys; i++)
        {
            pagePointer = InternalBuffer.getInt();//next page adrs
            length = InternalBuffer.getInt();
            curId = new byte[length];
            curDId=new DeweyID();
            InternalBuffer.get(curId, 0, length);
            curDId.setDeweyId(curId);
            result = curDId;
            if (curDId.equals(deweyId)) {
                return qName; //key found
            } else if (!(curDId.lessThan(deweyId))) {
                return null; //key not found
            }
        }
        return null;//not found

    }
  */
  public void setLevel(int level)
  {
      
  }
  public int getLevel()
{
    if(levelComputed)
	return level;		
    StructuralSummaryNode node = this;
    int level = 0;
    while(node.getParent()!= null) 
    {
            level++;
            node = node.getParent();
    }
    levelComputed = true;
    this.level = level;
    return level;
}
  public String toString()
  {
      return this.getQName()+ ":" + String.valueOf(getCID());
  }
  }




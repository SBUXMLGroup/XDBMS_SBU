///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package indexManager.backup;
//
//import commonSettings.Settings;
//import java.util.ArrayList;
//import xmlProcessor.DeweyID;
//
//
//public class StructuralSummaryNode 
//{
//  String qName;
//  int CID;
//  int parentCID;
//
//    public StructuralSummaryNode(String qName, int CID, int parentCID, ArrayList<indexManager.StructuralSummaryNode> childrenList) {
//        this.qName = qName;
//        this.CID = CID;
//        this.parentCID = parentCID;
//        this.childrenList = childrenList;
//    }
//  indexManager.StructuralSummaryNode parent;
//  DeweyID deweyID;
//  ArrayList<indexManager.StructuralSummaryNode> childrenList;
//  public StructuralSummaryNode(int CID,String qName,int parentCID)
//  {
//      this.CID=CID;
//      this.qName=qName;
//      this.parentCID=parentCID;      
//  }
//  /**
//   * not being used already
//   * public StructuralSummaryNode(DeweyID deweyID,String qName,int parentID)
//  { 
//      this.deweyID=deweyID;
//      this.CID=deweyID.getCID();
//      this.qName=qName;
//      this.parentCID=parentID;    
//       
//  }
//  //improved:
//   public StructuralSummaryNode(DeweyID deweyID,String qName,StructuralSummaryNode parent)
//  { 
//      this.deweyID=deweyID;
//      this.CID=deweyID.getCID();
//      this.qName=qName;
//      this.parent=parent;
//      this.parentCID=parent.getCID();    
//       
//  }*/
//  public StructuralSummaryNode()
//  {
//      this.CID=0;
//      this.qName=null;
//      this.parentCID=-1;      
//  }
//  public void appendChild(indexManager.StructuralSummaryNode childNode)
//  {
//      childrenList.add(childNode);      
//  }
//  //////////Getters & Setters:
//  public void setParentCID(int parentCID)
//  {
//      this.parentCID=parentCID;
//  }
//  public int getParentCID()
//  {
//      return parentCID;
//  }
//  public void setParent(indexManager.StructuralSummaryNode parent)
//  {
//      this.parent=parent;
//  }
//  public indexManager.StructuralSummaryNode getParent()
//  {
//      return parent;
//  }
//  public void setCID(int CID)
//  {
//      this.CID=CID;
//  }
//  public int getCID()
//  {
//      return CID;
//  }
//  public void setQName(String qName)
//  {
//      this.qName=qName;
//  }
//  public String getQName()
//  {
//      return qName;
//  }
//  public indexManager.StructuralSummaryNode getLastChild()
//  {
//      int lastIndex=childrenList.size()-1;
//      return childrenList.get(lastIndex);
//  }
//  //////////////////
//  public ArrayList<indexManager.StructuralSummaryNode> getChildNodes()
//  {
//      return childrenList;
//  }
//  public boolean leafNodeSearch(DeweyID deweyId) 
//  {
//        //dar barg ha bayd pagePointer hamun null bashand,ya aslan nabashand.(tu method insert taiin mishe)
//        int pagePointer = -1; //null
//        int length;
//        byte[] curId;
//        DeweyID curDId ; 
//        DeweyID result = null;
//        InternalBuffer.position(Settings.dataStartPos);//3+4+4??????
//        if(numOfKeys>0)
//        for (int i = 0;i < numOfKeys; i++)
//        {
//            pagePointer = InternalBuffer.getInt();//next page adrs
//            length = InternalBuffer.getInt();
//            curId = new byte[length];
//            curDId=new DeweyID();
//            InternalBuffer.get(curId, 0, length);
//            curDId.setDeweyId(curId);
//            result = curDId;
//            if (curDId.equals(deweyId)) {
//                return true; //key found
//            } else if (!(curDId.lessThan(deweyId))) {
//                return false; //key not found
//            }
//        }
//        return false;//not found
//
//    }  
//  //***MUST COMPLETe:
//  public String qNameSearch(DeweyID deweyId)
//  {
//        //dar barg ha bayd pagePointer hamun null bashand,ya aslan nabashand.(tu method insert taiin mishe)
//        int pagePointer = -1; //null
//        int length;
//        byte[] curId;
//        DeweyID curDId ; 
//        DeweyID result = null;
//        InternalBuffer.position(Settings.dataStartPos);//3+4+4??????
//        if(numOfKeys>0)
//        for (int i = 0; i < numOfKeys; i++)
//        {
//            pagePointer = InternalBuffer.getInt();//next page adrs
//            length = InternalBuffer.getInt();
//            curId = new byte[length];
//            curDId=new DeweyID();
//            InternalBuffer.get(curId, 0, length);
//            curDId.setDeweyId(curId);
//            result = curDId;
//            if (curDId.equals(deweyId)) {
//                return qName; //key found
//            } else if (!(curDId.lessThan(deweyId))) {
//                return null; //key not found
//            }
//        }
//        return null;//not found
//
//    }
//  public int getLevel()
//{
//    return deweyID.getLevel();
//}
//}
//
//

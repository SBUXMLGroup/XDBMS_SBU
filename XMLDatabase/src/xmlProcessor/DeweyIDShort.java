///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//
//package xmlProcessor;
//
///**
// *
// * @author Micosoft
// */
//public class DeweyIDShort {
//    
//   
//    private ArrayList<Short> deweyID;
//    private int CID;
//    public DeweyID()
//    {
//       deweyID = new ArrayList<Short>();
//       CID=0;
//       //CID=0 is used for attributes and contents
//    }
//    public DeweyID(ArrayList<Short> deweyID)
//    {
//        this.deweyID=deweyID;
//        CID=0;
//    }
//    public DeweyID (DeweyID another)
//    {
//        deweyID = new ArrayList<Short>();
//        this.setDeweyId(another.getDeweyId());
//        //since it is used for attributes we set CID as -1;
//        CID=0;
//    }
//    public DeweyID (byte[] dID)
//    {
//        deweyID = new ArrayList<Short>();
//        setDeweyId(dID);
//        CID=0;
//    }
//    
//    public byte[] getDeweyId()
//    {
//        //deweyID is Arraylist<Integer>!we should transform it into some byte array:
//        byte[] deweyIDArray = new byte[deweyID.size()*2];
//        short temp;
//        for (int i=0; i < deweyID.size(); i++)
//        {
//            temp=deweyID.get(i);
//            deweyIDArray[2*i] = (byte) (temp & 0xff);
//            deweyIDArray[2*i+1]= (byte) ((temp>>>8)& 0xff);
//            
//        }
//        return deweyIDArray;        
//    }
////    public byte[] getDeweyId()
////    {
////        //deweyID is Arraylist<Integer>!we should transform it into some byte array:
////        byte[] deweyIDArray = new byte[deweyID.size()*4];
////        Integer temp;
////        for (int i=0; i < deweyID.size(); i++)
////        {
////            temp=deweyID.get(i);
////            deweyIDArray[4*i] = (byte) (temp & 0xff);
////            deweyIDArray[4*i+1]= (byte) ((temp>>>8)& 0xff);
////            deweyIDArray[4*i+2]=(byte) ((temp>>>16)& 0xff);
////            deweyIDArray[4*i+3]=(byte) ((temp>>>24)& 0xff);
////        }
////        return deweyIDArray;        
////    }
//    public short getFirsttDiv()
//    {
//        return deweyID.get(deweyID.size()-1);
//    }
//    public short getAttrDiv()
//    {
//        if(this.getFirsttDiv()==1) //Attr Value:
//        {  
//            if(deweyID.size()>=4 )
//               return deweyID.get(deweyID.size()-3);//division which indicates attribute from element.
//        }
//        else 
//              if(deweyID.size()>=3) //Attr:
//                  return deweyID.get(deweyID.size()-2);//division which indicates attribute from element.
//        return -1;
//    }
////    public byte[] getBytes()
////    {
////      //int[] transformedDID=new int[this.deweyID.size()];
////        for(int i=0;i<deweyID.size();i++)
////        {
////           deweyID.get(i).
////        }
////      
////    }
//    public void setDeweyId(byte[] deweyIdArray)
//    {
//        short temp=0;
//        for (int i=0; i < deweyIdArray.length; i++)
//        {
//            if(i%2==0)
//            {   
//                if(i>0)
//                {   
//                    deweyID.add(temp); //temp prepared in last iteration
//                    temp=0;
//                }
//                temp=(short)(deweyIdArray[i] & 0xff);                
//                    
//            }
//            else//(i%2==1)
//                temp=(short)(temp+(Integer)((deweyIdArray[i] & 0xff) <<8));      
//        
//        }            
//        deweyID.add(temp); 
//    }
////    public void setDeweyId(byte[] deweyIdArray)
////    {
////        Integer temp=0;
////        for (int i=0; i < deweyIdArray.length; i++)
////        {
////            if(i%4==0)
////            {   
////                if(i>0)
////                {   
////                    deweyID.add(temp); //temp prepared in last iteration
////                    temp=0;
////                }
////                temp=(Integer)(deweyIdArray[i] & 0xff);                
////                    
////            }
////            else if(i%4==1)
////                temp=(Integer)(temp+(Integer)((deweyIdArray[i] & 0xff) <<8));      
////            else if(i%4==2)
////                temp=(Integer)(temp+(Integer)((deweyIdArray[i] & 0xff) <<16));   
////            else if (i%4==3)
////                temp=(Integer)(temp+(Integer)((deweyIdArray[i] & 0xff) <<24));   
////        }            
////        deweyID.add(temp); 
////    }
//    public int getCID()
//    {
//        return this.CID;
//    }
//    public void setCID(int CID)
//    {
//        this.CID=CID;
//    }
//    public boolean notGreaterThan(DeweyID anotherDeweyId)
//    {
//        //bayad joda test shavad:
//        //irad dare:compare: 1.5,1.5.7,or if:size=0
//        int sizeOfThis=this.deweyID.size();
//        int sizeOfAnother=anotherDeweyId.deweyID.size();
//        
//        for(int i=0;i< Math.min(sizeOfThis,sizeOfAnother );i++)
//        {   
//            if(this.deweyID.get(i)<anotherDeweyId.deweyID.get(i))
//                return true;
//            else if(this.deweyID.get(i)>anotherDeweyId.deweyID.get(i))
//                return false;
//        }
//        if(sizeOfThis<=sizeOfAnother)
//            return true;//when they are equal
//        else
//            return false;
//    }
//    
//     public boolean equals(DeweyID anotherDeweyId)
//    {
//        if(this.deweyID.size()!=anotherDeweyId.deweyID.size())
//            return false;
//        else
//            for(int i=0;i< this.deweyID.size();i++)
//            {   
//                if(this.deweyID.get(i)!=anotherDeweyId.deweyID.get(i))
//                    return false; //some division doesn't match its peer
//            }
//        return true;//when they are equal
//    }
//    
//    public void addDivision(Short curPart)
//    {
//        this.deweyID.add(curPart);
//               
//    }
//    public void removeLastDivision()
//    {
//        this.deweyID.remove(deweyID.size()-1);
//               
//    }
//    public ArrayList parent(ArrayList deweyID)
//    { 
//        ArrayList<Byte>parent;
//        parent=deweyID;
//        parent.remove(parent.size()-1);
//        return parent;        
//    }
//    public boolean isAttrib(ArrayList deweyID)
//    {
//        //not defined yet
//        return true; 
//    }
//    @Override
//    public String toString()
//    {
//        String deweyStr="";
//        
//        for(int i=0;i<deweyID.size();i++)
//            deweyStr=deweyStr+String.valueOf(deweyID.get(i))+".";//string builder
//        if(deweyID.size()>=1)
//            deweyStr=deweyStr.substring(0,deweyStr.length()-1);//removing last dot
//        return deweyStr;
//    }
//    ///------------------------------------------------------
//    public int getLevel()
//    {
//        //even ha hazf,age attrib bashe 1 ezafi hazf.
//        return deweyID.size()-1;
////        int level=-1;
////        int len=deweyID.size();
////        for(int i=0;i<=len;i++)
////        {
////            if(deweyID.get(i)%2!=0 && deweyID.get(i)!=3 )//if div value is even it is not counted as level.
////            level++;                                //also '3' is reserved for attrs and contents.they have one more in depth
////                            
////        }
//        
//    }
//    //usage in RG:
//    public DeweyID getAncestorDeweyID(int level)
//    {
//        DeweyID anc;
//        ArrayList<Short> ancDivs=new ArrayList<>();
//       // for(int i=deweyID.size()-1;i>=;i--)//i=0 up to <level i=x down to >x-level
//        int len=deweyID.size();
//        for(int i=0;i<=level;i++)//because level starts from zero
//        {
//            if(deweyID.get(i)%2==0)//if div value is even it is not counted as level.
//               level++;
//            ancDivs.add(i,deweyID.get(i)); 
//                
//        }
//         anc=new DeweyID(ancDivs);
//         return anc;         
//
//    }
//    public int compareTo(DeweyID anotherDeweyID)
//    {
//        //func: to compare twe IDs up to some level
//        int sizeOfThis=this.deweyID.size();
//        int sizeOfAnother=anotherDeweyID.deweyID.size();
//        
//            for(int i=0;i< Math.min(sizeOfThis,sizeOfAnother) ;i++)
//            {   
//                if(this.deweyID.get(i)<anotherDeweyID.deweyID.get(i))
//                    return -1; //less
//                else if(this.deweyID.get(i)>anotherDeweyID.deweyID.get(i))
//                    return 1; //greater                
//            }
//            if(sizeOfThis==sizeOfAnother)
//                return 0;
//            else if(sizeOfThis<sizeOfAnother)
//                return -1;
//            else 
//                return 1;
//        
//        //-2;//something is wrong!
////        else 
//            //throw new DBException("level should be less than size of DeweyIDs");
//        //System.err.println("comparison ended with error");
//        //return -2;
//    }
//    public int compareTo(int level,DeweyID anotherDeweyID)// throws DBException
//    {
//        //func: to compare twe IDs up to some level
//        int sizeOfThis=this.deweyID.size();
//        int sizeOfAnother=anotherDeweyID.deweyID.size();
//        if(level<Math.min(sizeOfThis,sizeOfAnother))//shoud be absouloutely less!right???
//        {
//            for(int i=0;i<= level ;i++)
//            {   
//                if(this.deweyID.get(i)<anotherDeweyID.deweyID.get(i))
//                    return -1;
//                else if(this.deweyID.get(i)>anotherDeweyID.deweyID.get(i))
//                    return 1; 
//            }
//            return 0;
////            
////            if(sizeOfThis==sizeOfAnother)
////                return 0;
////            else if(sizeOfThis<sizeOfAnother)
////                return -1;
////            else 
////                return 1;
//        }
//        //-2;//something is wrong!
////        else 
//            //throw new DBException("level should be less than size of DeweyIDs");
//        System.err.println("comparison ended with error");
//        return -2;
//            
//    } 
//    public boolean isSelfOf (DeweyID newDeweyID)
//    {
//		// docID is checked in compareTo
//		return this.compareTo(newDeweyID) == 0;
//    }
//     public boolean isParentOf (DeweyID newDeweyID)
//     {
//	// docID is checked in compareTo
//	return this.compareTo(this.getLevel(),newDeweyID) == 0 && this.getLevel()<newDeweyID.getLevel();
//     }
//     public boolean isSiblingOf (DeweyID newDeweyID)
//     {
//	
//	return this.getAncestorDeweyID(this.getLevel()-1).compareTo(newDeweyID.getAncestorDeweyID(newDeweyID.getLevel()-1))==0;
//     }
//     public boolean isRootDID ()
//     {
//	
//	return this.toString().equals("1");
//     }
//}
//
//    

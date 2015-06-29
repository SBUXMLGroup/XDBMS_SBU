
package xmlProcessor.DBServer.utils;

import xmlProcessor.QTP.QTPNode;
import xmlProcessor.DeweyID;
import java.util.HashMap;
import java.util.ArrayList;
//this was: QueryTuple
public class QueryTuple
{    
   //private HashMap<QTPNode,DeweyID> queryTuple;
    private ArrayList<DeweyID> queryTuple;//felan arraylist migirim.
   
   public  QueryTuple()
   {
      //xqueryTuple=new HashMap<QTPNode, DeweyID>();
       queryTuple=new ArrayList<>();
   }
   
   public  QueryTuple(DeweyID deweyID)
   {
       if(deweyID!=null)
       {
      // queryTuple=new HashMap<QTPNode, DeweyID>();
       queryTuple=new ArrayList<>();
       queryTuple.add(deweyID);
       }//else: queryTuple should become null.
       
        
   }
   
   /**public DeweyID getValue(QTPNode key)
   {
       return this.queryTuple.get(key);
   }
   
   public void putValue(QTPNode key,DeweyID value)
   {
       this.queryTuple.put(key, value);
   }
   */
   
   
    public DeweyID getIDForPos(int pos)
    {
        return queryTuple.get(pos);
    }
//    public void addDeweyID(DeweyID deweyID)
//    {
//    }
//    public void addDeweyID(int sth,ArrayList<DeweyID> addition)
//    {
//        //sth is probably position
//    }
	public void addDeweyID(ArrayList<DeweyID> ids) {
		queryTuple.addAll(ids);
		//type.add(name);
	}
	
	public void addDeweyID(int index, DeweyID id) {
		queryTuple.add(index, id);
		//type.add(name);
	}

	public void addDeweyID(int index, ArrayList<DeweyID> ids) {
		queryTuple.addAll(index, ids);
		//type.add(name);
	}

    	public void addDeweyID(DeweyID id) {
		queryTuple.add(id);
		//type.add(name);
	}

    public int size()
    {
        return queryTuple.size();
    }
    public static QueryTuple merge(QueryTuple lTuple,QueryTuple rTuple)
    {
        QueryTuple res = new QueryTuple();
	res.queryTuple.addAll(lTuple.queryTuple);
	res.queryTuple.addAll(rTuple.queryTuple);
	return res;

    }
    
    public QueryTuple merge(QueryTuple tuple)
    {		
	this.queryTuple.addAll(tuple.queryTuple);
	return this;
    }	
    public String toString() {
        ///////Original:
		StringBuffer sb = new StringBuffer();
		sb.append("<");
		for(int i = 0; i < queryTuple.size()-1; i++) {
			//sb.append(type.get(i) + "--" + value.get(i) + " ");
                        sb.append(queryTuple.get(i) + " ");
		}
		//sb.append(type.get(type.size()-1) + "--" + value.get(value.size()-1) + ">");
		//sb.append("\n"                
		sb.append(queryTuple.get(queryTuple.size()-1) + ">");
		return sb.toString();
	}
//	public String toString() {
    ////////////////Modified to show CIDs:
//		StringBuffer sb = new StringBuffer();
//		sb.append("<");
//		for(int i = 0; i < queryTuple.size()-1; i++) {
//			//sb.append(type.get(i) + "--" + value.get(i) + " ");
//                        sb.append(queryTuple.get(i).getCID()+": ");
//			sb.append(queryTuple.get(i) + "  ");
//		}
//		//sb.append(type.get(type.size()-1) + "--" + value.get(value.size()-1) + ">");
//		//sb.append("\n"
//                sb.append(queryTuple.get(queryTuple.size()-1).getCID()+": ");
//		sb.append(queryTuple.get(queryTuple.size()-1) + ">");
//		return sb.toString();
//	}

	public void print() {
		System.out.println(toString());
	}
    public ArrayList<DeweyID> getValues()
    {
        return queryTuple;
    }
    public void project(int pos) 
    {
		if(pos < queryTuple.size()) {
			queryTuple.remove(pos);
			//type.remove(pos);
		}
	}
    public void project(int[] pos)
    {
		for(int i = pos.length-1; i >= 0; i--) {
			queryTuple.remove(pos[i]);
			//type.remove(pos[i]);
		}
	}
}


package xmlProcessor.DBServer.utils;

/**
 * This class implements a class to contain to related object 
 *   
 * @author Kamyar
 *
 */

public class TwinObject <O1, O2>{
	private O1 o1;
	private O2 o2;
	
	public TwinObject()
	{
		this.o1 = null;
		this.o2 = null;		
	}
	public TwinObject(O1 o1, O2 o2)
	{
		this.o1 = o1;
		this.o2 = o2;		
	}
	
	public O1 getO1()
	{
		return o1;
	}
	
	public void setO1(O1 o1)
	{
		this.o1 = o1;
	}
	
	public O2 getO2()
	{
		return o2;
	}	
	
	public void setO2(O2 o2)
	{
		this.o2 = o2;
	}

	public void set(O1 o1, O2 o2)
	{
		this.o1 = o1;
		this.o2 = o2;
	}
	
	public boolean equals(Object anObject)
	{
		if (this == anObject) {
		    return true;
		}
		if (anObject instanceof TwinObject) 
		{
			TwinObject<O1, O2> anTwinObject = (TwinObject<O1, O2>)anObject;
			if (anTwinObject.getO1().equals(this.o1) && anTwinObject.getO2().equals(this.o2))
			{
				return true;
			}
		}
		return false;
	}
	
	//hashCode() should be overridden to return same hash number for object which are equal regard to the equals() method 
	public int hashCode()
	{
		return o1.hashCode()*31 + o2.hashCode();
	}
}



//public class TwinObject {
//private int o1;
//private String o2;
//
//public TwinObject()
//{
//	this.o1 = 0;
//	this.o2 = null;		
//}
//public TwinObject(int o1, String o2)
//{
//	this.o1 = o1;
//	this.o2 = o2;	
//	int K = Integer.valueOf(o1).hashCode();
//	int M = this.hashCode();
//	++K;
//
//}
//
//public int getO1()
//{
//	return o1;
//}
//
//public void setO1(int o1)
//{
//	this.o1 = o1;
//}
//
//public String getO2()
//{
//	return o2;
//}	
//
//public void setO2(String o2)
//{
//	this.o2 = o2;
//}
//
//public void set(int o1, String o2)
//{
//	this.o1 = o1;
//	this.o2 = o2;
//	int K = Integer.valueOf(o1).hashCode();
//	int M = this.hashCode();
//	++K;
//	
//}
//
//public boolean equals(Object anObject)
//{
//	if (this == anObject) {
//	    return true;
//	}
//	if (anObject instanceof TwinObject) 
//	{
//		TwinObject anTwinObject = (TwinObject)anObject;
//		if (anTwinObject.getO1() == o1 && anTwinObject.getO2().equals(this.o2))
//		{
//			return true;
//		}
//	}
//	return false;
//}
//
//public int hashCode()
//{
//	return Integer.valueOf(o1).hashCode();
//}
//}


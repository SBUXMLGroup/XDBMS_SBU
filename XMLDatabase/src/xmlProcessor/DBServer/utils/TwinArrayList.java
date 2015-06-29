

package xmlProcessor.DBServer.utils;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * This class implements an ArrayList which could manage two related object
 * 
 *   
 * @author Kamyar
 *
 */
public class TwinArrayList<O1, O2> {

	private ArrayList<O1> L1;
	private ArrayList<O2> L2;
	private Comparator<O1> comparator1;
	private Comparator<O2> comparator2;
	
	public TwinArrayList(int initialCapacity)
	{
		L1 = new ArrayList<O1>(initialCapacity);
		L2 = new ArrayList<O2>(initialCapacity);
	}
	
	public TwinArrayList()
	{
		this(10);
	}
	
	public boolean add(O1 o1, O2 o2)
	{
		L1.add(o1);
		L2.add(o2);
		return true; 
	}
	
	public boolean add(int index, O1 o1, O2 o2)
	{
		L1.add(index, o1);
		L2.add(index, o2);
		return true; 
	}

	/**
	 * gets from first list
	 */
	public O1 getL1(int index)
	{
		return L1.get(index);
	}
	
	/**
	 * gets from second list
	 */
	public O2 getL2(int index)
	{
		return L2.get(index);
	}
	
	public void setL1(int index, O1 element)
	{
		L1.set(index, element);
	}
	
	public void setL2(int index, O2 element)
	{
		L2.set(index, element);
	}
	
	/**
	 * returns first list
	 */
	public ArrayList<O1> getL1()
	{
		return L1;
	}
	
	/**
	 * returns second list
	 */
	public ArrayList<O2> getL2()
	{
		return L2;
	}

	public TwinObject<O1, O2> get(int index)
	{
		return new TwinObject<O1, O2>(L1.get(index), L2.get(index));
	}
	
	public int size()
	{
		return L1.size(); //L1.size should be equal to L2.size() !!!
	}

    public boolean isEmpty() 
    {
    	return L1.size() == 0;
    }

	public void clear()
	{
		L1.clear();
		L2.clear();
	}
	
	/**
	 * remove objects from index position and returns removed object of first list
	 */
	public O1 remove(int index)
	{
		L2.remove(index);
		return L1.remove(index);
	}
	
	/**
	 * returns index of o1 in the first list
	 */
	public int indexOfL1(O1 o1)
	{
		return L1.indexOf(o1);
	}

	/**
	 * returns index of o2 in the first list
	 */
	public int indexOfL2(O2 o2)
	{
		return L2.indexOf(o2);
	}

    public boolean containsL1(O1 o1) 
    {
    	return L1.indexOf(o1) >= 0;
    }

    public boolean containsL2(O2 o2) 
    {
    	return L2.indexOf(o2) >= 0;
    }
    
    public boolean equals(Object anObject)
    {
		if (this == anObject) {
		    return true;
		}
		if (anObject instanceof TwinArrayList) 
		{
			TwinArrayList<O1, O2> aTwinArrayList = (TwinArrayList<O1, O2>)anObject;
			return this.getL1().equals(aTwinArrayList.getL1()) && this.getL2().equals(aTwinArrayList.getL2());
		}
		return false;
    }
    
    public int hashCode()
    {
    	return this.L1.hashCode();
    }
    public void sortBasedOnL1(Comparator<O1> comparator)
    {
    	this.comparator1 = comparator;
    	sortArrayList1(L1);
    }

    public void sortBasedOnL2(Comparator<O2> comparator)
    {
    	this.comparator2 = comparator;
    	sortArrayList2(L2);
    }
  
	////////////////////////////////////////////////////////////
	///Sort Based On L1
	///////////////////////////////////////////////////////////

	private void sortArrayList1(ArrayList<O1> list)
	{
		qsort1(list, 0, list.size() - 1);
	}

	private void swap1(ArrayList<O1> list, int i, int j)
	{
		O1 tmp = list.get(i);
		list.set(i, list.get(j));
		list.set(j, tmp);

		O2 tmp2 = L2.get(i);
		L2.set(i, L2.get(j));
		L2.set(j, tmp2);

	}

	private int partition1(ArrayList<O1> list, int begin, int end)
	{
		int index = begin + ((end - begin) / 2);
		O1 pivot = list.get(index);
		swap1(list, index, end);
		for (int i = index = begin; i < end; ++i)
		{
			if (comparator1.compare(list.get(i), pivot) <= 0)
			{
				swap1(list, index++, i);
			}
		}
		swap1(list, index, end);
		return (index);
	}

	private void qsort1(ArrayList<O1> list, int begin, int end)
	{
		if (end > begin)
		{
			int index = partition1(list, begin, end);
			qsort1(list, begin, index - 1);
			qsort1(list, index + 1, end);
		}
	}

	
	////////////////////////////////////////////////////////////
	///Sort Based On L2
	///////////////////////////////////////////////////////////
	private void sortArrayList2(ArrayList<O2> list)
	{
		qsort2(list, 0, list.size() - 1);
	}

	private void swap2(ArrayList<O2> list, int i, int j)
	{
		O2 tmp = list.get(i);
		list.set(i, list.get(j));
		list.set(j, tmp);

		O1 tmp2 = L1.get(i);
		L1.set(i, L1.get(j));
		L1.set(j, tmp2);

	}

	private int partition2(ArrayList<O2> list, int begin, int end)
	{
		int index = begin + ((end - begin) / 2);
		O2 pivot = list.get(index);
		swap2(list, index, end);
		for (int i = index = begin; i < end; ++i)
		{
			if (comparator2.compare(list.get(i), pivot) <= 0)
			{
				swap2(list, index++, i);
			}
		}
		swap2(list, index, end);
		return (index);
	}

	private void qsort2(ArrayList<O2> list, int begin, int end)
	{
		if (end > begin)
		{
			int index = partition2(list, begin, end);
			qsort2(list, begin, index - 1);
			qsort2(list, index + 1, end);
		}
	}

}

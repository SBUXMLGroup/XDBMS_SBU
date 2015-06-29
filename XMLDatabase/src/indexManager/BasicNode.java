
package indexManager;
import java.util.Arrays;

public class BasicNode
{
    int[]keys;
    int maxSize;
    int elemCount;
    public BasicNode()
    {
        maxSize=1024;
        elemCount=0;
        keys=new int[maxSize];
        
    }
    public int insert(int key)
    {
        for (int i=0;i<maxSize;i++)
            if(key==keys[i]) 
                return 0; //key already in
        if(elemCount==maxSize)
            return -1; //no room for extra keys
        keys[elemCount]=key; //put key into array keys
        Arrays.sort(keys);
        elemCount++;
        return 1;
    }
}

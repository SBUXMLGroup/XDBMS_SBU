
package indexManager.util;

import indexManager.ElementBTreeNode;
import xmlProcessor.DeweyID;

/**
 * 
 * @author Micosoft
 * @param <C>
 * @param <T>
 * <C> is Node Type(BTree or Element)
 * <T> is output type
 */
public class OutputWrapper <C,T>
{
    private C container;
    private T output;
    public OutputWrapper(C container,T myType)
    {
        this.container=container;
        this.output=myType;                
    }
    public C getContainer()
    {
        return container;
    }
    public T getOutput()
    {
        return output;
    }
    
}

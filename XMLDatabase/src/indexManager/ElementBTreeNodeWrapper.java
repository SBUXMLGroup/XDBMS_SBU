
package indexManager;

import commonSettings.Settings;


public class ElementBTreeNodeWrapper 
{
    private ElementBTreeNode core;
    private int recordPos;
    public ElementBTreeNodeWrapper(ElementBTreeNode core,int recordPos)
    {
        this.core=core;
        this.recordPos=recordPos;
                
    }
    public int getRecordPOs()
    {
        return this.recordPos;
    }
    public ElementBTreeNode getCore()
    {
        return this.core;
    }
    public void reset(ElementBTreeNode newCore)
    {
        this.core=newCore;
        this.recordPos= Settings.dataStartPos;//THIS IS RIGHT!:)
        
    }
}

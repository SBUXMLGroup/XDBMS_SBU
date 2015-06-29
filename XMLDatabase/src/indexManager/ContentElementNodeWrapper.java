/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package indexManager;

import commonSettings.Settings;

/**
 *
 * @author Micosoft
 */
public class ContentElementNodeWrapper
{
    private ContentElementNode core;
    private int recordPos;
    public ContentElementNodeWrapper(ContentElementNode core,int recordPos)
    {
        this.core=core;
        this.recordPos=recordPos;
                
    }
    public int getRecordPOs()
    {
        return this.recordPos;
    }
    public ContentElementNode getCore()
    {
        return this.core;
    }
    public void reset(ContentElementNode newCore)
    {
        this.core=newCore;
        this.recordPos= Settings.dataStartPos;//THIS IS RIGHT!:)
        
    }
}

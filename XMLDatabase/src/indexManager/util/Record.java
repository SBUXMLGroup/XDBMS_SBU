/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package indexManager.util;

import xmlProcessor.DeweyID;

/**
 *
 * @author Micosoft
 */
public class Record 
{
    private DeweyID DID;
    private int CID;
    private String qName;
    public Record()
    {
        this.DID=null;
        this.CID=0;
        this.qName=null;
        
    }
    public Record(DeweyID DID,int CID,String qName)
    {
        this.DID=DID;
        this.CID=CID;
        this.qName=qName;
        
    }
    public DeweyID getDeweyID()
    {
        return this.DID;
    }
    public int getCID()
    {
        return this.CID;
    }
    public String getQname()
    {
        return this.qName;
    }
}

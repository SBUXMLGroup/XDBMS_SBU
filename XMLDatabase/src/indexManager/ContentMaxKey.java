/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package indexManager;

import xmlProcessor.DeweyID;

/**
 *
 * @author Micosoft
 */
public class ContentMaxKey 
{
        public int maxCID;
        public String maxContent;
        public DeweyID maxDeweyID;
        public  ContentMaxKey()
        {
            maxCID=-1;
            maxContent=null;
            maxDeweyID=new DeweyID();
        }
    
}

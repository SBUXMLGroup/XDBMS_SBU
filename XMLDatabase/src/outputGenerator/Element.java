
package outputGenerator;
import java.util.ArrayList;
import xmlProcessor.DeweyID;

public class Element 
{
    private String qName;
    private DeweyID DID;
    private ArrayList<Attribute> attributes;
    private String content;
   // private ArrayList contents;
    public Element(String qName,DeweyID DID,ArrayList attributes)
    {
       this.qName=qName;
       this.attributes=attributes;
       this.DID=DID;
    }
    public void addAttribute(Attribute attrib)
    {
        attributes.add(attrib);
    }
    public void setContent(String content)
    {
        this.content=content;
    }
    public String getQName()
    {
        return qName;
    }
    public DeweyID getDeweyID()
    {
        return DID;
    }
    public ArrayList getAttributes()
    {
        return attributes;       
        
    }
    public Attribute getAttribute(int index)
    {
        return attributes.get(index);
    }
}

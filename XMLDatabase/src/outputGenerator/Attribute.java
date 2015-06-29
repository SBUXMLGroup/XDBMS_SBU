
package outputGenerator;


public class Attribute 
{
    private String qName;
    private String value;
    
    public Attribute()
    {
        this.qName=null;
        this.value=null;
    }
    public Attribute(String qName)
    {
        this.qName=qName;
        this.value=null;
    }
    public void setValue(String value)
    {
        this.value=value;
    }
    public String getValue()
    {
       return value;
    }
    public void setQName(String qName)
    {
        this.qName=qName;
    }
          
    public String getQName()
    {
        return qName;
    }
}

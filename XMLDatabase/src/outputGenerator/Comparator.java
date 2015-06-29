
package outputGenerator;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.LineNumberReader;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
public class Comparator 
{ 
    String srcFileName="D:\\Res-RG06.txt";
    String dstFileName="D:\\Res-RG05.txt";
    String outputTxt="d:/Diff.txt";
    FileReader srcReader;
    FileReader dstReader;
    BufferedReader srcBF;
    BufferedReader dstBF;
    PrintWriter printWr;
    LineNumberReader  srcNReader;
    LineNumberReader  dstNReader;
    
    String res="D:\\compareRes.txt";
    FileWriter fw;
    PrintWriter pw;
    BufferedReader bufReader;
    List<String> myList=new ArrayList<String>();
    HashMap<String,Integer> myHashMap = new HashMap<>();

    boolean[] marker;
    
    public Comparator() throws FileNotFoundException, IOException
    {
        File of=new File(outputTxt);
        if(of.exists())
            of.delete();
        of.createNewFile();
        srcReader=new FileReader(srcFileName); 
        srcBF=new BufferedReader(srcReader);
        srcNReader=new LineNumberReader(srcBF);
        dstReader=new FileReader(dstFileName);
        dstBF=new BufferedReader(dstReader);
        dstNReader=new LineNumberReader(dstBF);
        printWr=new PrintWriter(of);
        File of2=new File(res);
        if(of2.exists())
            of2.delete();
        of2.createNewFile();
        fw=new FileWriter(of2, true);
        pw=new PrintWriter(fw);
        
    }
    public void compare() throws IOException
    {
        String line1=srcNReader.readLine();
        String line2=dstNReader.readLine();
        System.out.println("Starting");
        while(line1!=null && line2!=null)
        {
            if(!line1.equals(line2))
            {     
                printWr.println(/*"File \"" + srcFileName + "\" and file \"" +
                    dstFileName +*/ "\" differ at line " + srcNReader.getLineNumber() +
                    ":" + "\n" + line1 + "\n" + line2);
               // break;
            }
            line1=srcNReader.readLine();
            line2=dstNReader.readLine();
        }
        while(line1==null && line2!=null)
        {    printWr.println("File \"" + dstFileName + "\" has extra lines at line " +
                dstNReader.getLineNumber() + ":\n" + line2);
             line2=dstNReader.readLine();
        }
        while(line1!=null && line2==null)    
        {   printWr.println("File \"" + srcFileName + "\" has extra lines at line " +
                srcNReader.getLineNumber() + ":\n" + line1);                
            line1=srcNReader.readLine();
        }
        System.out.println("Closing.");
        srcBF.close();
        dstBF.close();
        printWr.close();
    }
    public void open(FileReader inFile) throws IOException
    {
        BufferedReader bufR=new BufferedReader(inFile);
        String line;
        int entryCounter=0;
         while((line=bufR.readLine())!=null)
        {
            entryCounter++;
            while(line.charAt(0)!='<')
                line=line.substring(1);
            myList.add(line);
            myHashMap.put(line,entryCounter);
            
        }
         marker=new boolean[myList.size()];
         bufR.close();
    }
    
    public void compare(String input,long matchCount) throws IOException
    {
        
        String line;
        boolean sw=false;
        bufReader=null;
        
//        bufReader=new BufferedReader(reader);
//        while((line=bufReader.readLine())!=null)
//        {   
//            line=line.substring(3);//remove line number
//            if(line.equals(input))
//            {    
//                pw.append("T");
//                sw=true;
//                break;
//            }           
//        }
//        if(sw==false)
//            pw.append("F");
//        bufReader.close();
        String temp;
        input=input.trim();
        for(int i=0;i<myList.size();i++)
        {
            temp=myList.get(i);
//            while(temp.charAt(0)!='<')
//                temp = temp.trim();
//            if(temp.charAt(0)!='<')
//                temp=myList.get(i).substring(4);  
            
//                if(matchCount==11)
//                {
//                    for(int j=0;j<input.length();j++)
//                       System.out.println(j+": inp: "+input.charAt(j)+" temp: "+temp.charAt(j));
//                    if(input.length()<temp.length())
//                       for(int j=input.length();j<temp.length();j++)
//                          System.out.println(j+"The rest of temp: "+temp.charAt(j));
//                }
                if(input.length()==temp.length())
                if(input.equals(temp))
                {   
                    //marker[i]=true;//this entry in RG05-Res is found in RG06-Res!
                    myList.remove(i);                   
                    pw.println(matchCount+" T ");
                    sw=true;                
                    break;
                }            
        }
        if(!sw)
        {   
            pw.println(matchCount+" F ");            
        }
        
    }
    public void close()
    {
        for(int i=0;i<myList.size();i++)
//            if(!marker[i])
                pw.println(myHashMap.get(myList.get(i))+" "+myList.get(i));
        pw.close();
    }
}

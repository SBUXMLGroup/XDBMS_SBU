/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package logManager;
import java.lang.Thread.State;

/**
 *
 * @author Micosoft
 */
public class LogManager 
{
    public void logManager()
    {}
    synchronized public void logger(String msg,int time)
    {
        System.err.println("....");
        //harja k mikhaym brkpoint bashe.in tabe ro migzarim.
    }
    public static void log(String msg)
    {
        return;
        //System.err.println(msg);
    }
    
    private static boolean logFlag[] = new boolean[11];
    static
    {
//        logFlag[6] = true;
//          logFlag[9] = true;
//          logFlag[10]=true;
    }
    
    public static void log(int code, String msg)
    {   
        if(logFlag[code])
            System.err.println(msg);
        
        return;
    }
    
    public static void log(String msg,int color)
    {
//        if(color==0)
//            System.err.println(msg);
//        else
//            System.out.println(msg);
        return;
    }
}

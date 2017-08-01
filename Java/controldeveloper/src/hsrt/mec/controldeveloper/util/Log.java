package hsrt.mec.controldeveloper.util;

import java.util.Calendar;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Helper class for logging information on the console. 
 * <br/><br/>
 * The class is implemented as a singleton.    
 */
public final class Log{
  private static Log log;
  public DateFormat timestampFormat= new SimpleDateFormat("HH:mm:ss.SSS");//("yyMMdd-HHmmss");
  
  private Log(){
  }
  
  /**
   * Static access to the singleton object.
   * @return Log Singleton object of type Log.
   */
  private static Log getLog(){
    if(log==null)
      log= new Log();
    return log;
  }
  
  
  /**
   * Writes the log message to the console.
   * @param aMessage The message, which shall be written on the console.
   */
  public static void log(String aMessage){
    if (Globals.ISLOG)
      System.out.println ("LOG "+getLog().stamp(aMessage));
  }
  

  /**
   * Addes a timestamp and the actual thread to the message.
   * @param aText The message.
   * @return The text, which shall be written on the console with additional
   * information.
   */  
  protected String stamp(String aText){
    Date date= Calendar.getInstance().getTime();
    try{
      return timestampFormat.format(date)+"; "+Thread.currentThread().getName()+": "+aText;
    }catch(Throwable th){
      th.printStackTrace();
      return aText;
    }
  }
}
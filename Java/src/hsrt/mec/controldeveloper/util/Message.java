package hsrt.mec.controldeveloper.util;

/**
 * Helper class for having a central point for message definitions.
 */
public final class Message {
  public static final String mMISSING_COM_PORT  = "Could not find COM port.";
  public static final String mREAD_DATA_ERROR   = "Could not read data.";
  public static final String mWRITE_DATA_ERROR  = "Could not write data.";
  public static final String mIO_DATA_ERROR     = "No data available.";
  public static final String mMISSING_FILE      = "No file available.";
  public static final String mTHREAD_SLEEP_ERROR= "Thread sleep error.";
  public static final String mTHREAD_EXEC_ERROR = "Thread execution error.";
  public static final String mFINISH            = "Finish ...";
 
  /**
   * Strings of messages can be defined with format elements (e.g. compare 
   * to {@code printf("%d", x)} in C program language). The format element 
   * used here is "{@code %v}". Every message can have a number of format elements. 
   * The appropriate values are stored in an array of strings.
   * <br/><br/>
   * Format elements in the message are replaced by array elements regarding 
   * the number of format elements and the number of array elements.
   * 
   * @param message The message with or without format elements.
   * @param value Array of strings with values which replaces the format elements.
   * @return Message string with replaced format elements.
   */
  public static String createMessage( String message, String[] value){
    int counter= 0;
    while ( message.contains( "%v" ) && counter<value.length ){ 
      message= message.replaceFirst( "%v", value[counter] );
      counter++;
    }
    
    return message;
  }
  
}

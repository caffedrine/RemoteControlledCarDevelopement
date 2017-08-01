package hsrt.mec.controldeveloper.util;

/**
 * Helper class for logging status information. Status information is implemented as 
 * "last-is-best". This means, only one status item is stored using this class. 
 * A new status item overwrites the existing one.
 * <br/><br/>
 * The class is implemented as a singleton.    
 */
public final class Status {
  /**
   * Defines the error free status with constant value {@value #E_OK} 
   */
  public static final int E_OK = 0;
  /**
   * Defines the error status with constant value {@value #E_NOK} 
   */
  public static final int E_NOK= 1;
  private static Status instance= null;
  
  private String message= "";
  private int    id     = 0;
  

  private Status(){
  }
  
  /**
   * Static access to the singleton object.
   * @return Status Singleton object of type Status.
   */
  public static Status getInstance(){
    if (instance==null)
      instance= new Status();
    return instance;
  }

  
  /**
   * Sets the status.
   * @param id either {@link hsrt.mec.controldeveloper.util.Status#E_OK} or
   * {@link hsrt.mec.controldeveloper.util.Status#E_OK}.
   * @param message the string representing the message out of class 
   * {@link hsrt.mec.controldeveloper.util.Message}
   */
  public void setStatus(final int id, final String message){
    this.id = id;
    this.message= message;
  }
  
  
  /**
   * Returns the id of the actual program status.
   * @return either {@link hsrt.mec.controldeveloper.util.Status#E_OK} or
   * {@link hsrt.mec.controldeveloper.util.Status#E_OK}.
   */
  public int getId(){
    return id;
  }

  
  /**
   * Returns the message of the actual program status.
   * @return message out of class {@link hsrt.mec.controldeveloper.util.Message}
   */
  public String getMessage(){
    return message;
  }
}

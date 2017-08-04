package hsrt.mec.controldeveloper.core.com.command;

/**
 * Interface {@code IGear} defines the interface for a gear command, i.e. the speed and the 
 * duraration of the mobile device.
 * <br/><br/>
 * This interface is used for evaluation and execution of concrete commands, e.g. in  
 * {@link hsrt.mec.controldeveloper.core.com.ComHandler}.
 * 
 * @see hsrt.mec.controldeveloper.core.com.ComHandler
 */
public interface IGear extends ICommand{
  /**
   * Returns the speed of the given gear command, with which speed the mobile device shall be
   * moved
   * @return Speed of the given gear command. 
   */ 
  public int getSpeed();
  
  /**
   * Returns the duration of the given gear command, how long the mobile device shall be moved.
   * @return Duration of the given gear command. 
   */   
  public double getDuration();
}

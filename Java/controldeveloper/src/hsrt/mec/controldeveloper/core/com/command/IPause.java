package hsrt.mec.controldeveloper.core.com.command;

/**
 * Interface {@code IPause} defines the interface for a pause command, i.e. the duraration, how long 
 * the mobile device shall be paused.
 * <br/><br/>
 * This interface is used for evaluation and execution of concrete commands, e.g. in  
 * {@link hsrt.mec.controldeveloper.core.com.ComHandler}.
 * 
 * @see hsrt.mec.controldeveloper.core.com.ComHandler
 */
public interface IPause extends ICommand {
  /**
   * Returns the duration of the given pause command, how long the mobile device shall be paused.
   * @return Duration of the given pause command. 
   */   
  public double getDuration();
}

package hsrt.mec.controldeveloper.core.com.command;

/**
 * Interface {@code IDirection} defines the interface for a direction command, i.e. the degree, 
 * in which the mobile device shall be turned. 
 * <br/><br/>
 * This interface is used for evaluation and execution of concrete commands, e.g. in  
 * {@link hsrt.mec.controldeveloper.core.com.ComHandler}.
 * 
 * @see hsrt.mec.controldeveloper.core.com.ComHandler
 */
public interface IDirection extends ICommand{
  /**
   * Returns the degree of a direction command, in which the mobile device shall be turned.
   * @return Degree of a direction command. 
   */
  public int getDegree();
}

package hsrt.mec.controldeveloper.core.com;

import hsrt.mec.controldeveloper.core.com.command.ICommand;


/**
 * Interface IComListener defines the listener which is called by the 
 * {@link hsrt.mec.controldeveloper.core.com.ComHandler} object when
 * a command of the execution process is executed.
 * <br/><br/>
 * The class that is interested in being informed implements this interface, 
 * and the object created with that class is registered with the
 * {@link hsrt.mec.controldeveloper.core.com.ComHandler} object, using the 
 * objects {@link hsrt.mec.controldeveloper.core.com.ComHandler#register(IComListener)} 
 * method. When a new command is executed out of the control process, that object's 
 * {@link #commandPerformed(ICommand)} method is invoked.
 * 
 * @see hsrt.mec.controldeveloper.core.com.ComHandler
 */
public interface IComListener {
  /**
   * Invoked when a command is executed.
   * @param c Information of the executed command.
   */
  public void commandPerformed(ICommand c);
}

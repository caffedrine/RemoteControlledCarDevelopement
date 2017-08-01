package hsrt.mec.controldeveloper.core.com.command;

/**
 * Interface {@code ICommand} defines the method for naming commands. Classes
 * writing commands to a communication port, like 
 * {@link hsrt.mec.controldeveloper.core.com.ComHandler}, only know command 
 * objects using the interface {@code ICommand}.
 * 
 * @see hsrt.mec.controldeveloper.core.com.ComHandler
 */
public interface ICommand {
  /**
   * Returns the name of a concrete command.
   * @return Name of a given command 
   */
  public String getName();
}

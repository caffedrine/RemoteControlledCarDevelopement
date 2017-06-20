package hsrt.mec.controldeveloper.core.com.command;

/**
 * Interface {@code IRepetition} defines the interface for a repetition command, i.e. the 
 * number of commands, which have been previously executed and the number of 
 * repetitions how often these commands shall be executed in order to repeat 
 * movements of the mobile device.
 * <br/><br/>
 * This interface is used for evaluation and execution of concrete commands, e.g. in  
 * {@link hsrt.mec.controldeveloper.core.com.ComHandler}.
 * <br/><br/>
 * ATTENTION: The implementation of class {@link hsrt.mec.controldeveloper.core.com.ComHandler} 
 * prohibits nested loops when defining repetitions at run-time.
 *
 * @see hsrt.mec.controldeveloper.core.com.ComHandler
 */
public interface IRepetition extends ICommand{
  /**
   * Returns the number of steps which have been perviously executed and shall be executed
   * again.
   * @return Number of steps to be executed again. 
   */   
  public int getNrSteps();

  /**
   * Returns the number of repetitions of the steps which shall be executed again.
   * @return Number of repetitions. 
   */  
  public int getNrRepetitions();
}

package hsrt.mec.controldeveloper.io;

import java.util.Vector;

/**
 * Interface {@code IOType} defines the methods for reading and writing
 * data into a data stream. The kind of data stream (e.g. keyboard,
 * file, etc.) is defined in the class which implements this interface.   
 * <br/><br/> 
 * The data from an input or output stream is provided by an object
 * of type Vector<String>.
 */
public interface IOType 
{
  /**
   * Reads data from an input stream. The data is provided by the {@link java.util.Vector} 
   * object given as a parameter.
   * @param content The vector in which the input data is written in a string format
   * @return {@code false}, in case of an error; {@code true}, otherwise.
   */
  public boolean read(Vector<String> content);

  /**
   * Writes data into an output stream
   * @param content The vector that contains the output data in a string format
   * @return {@code false}, in case of an error; {@code true}, otherwise.
   */
  public boolean write(Vector<String> content);
  
  /**
   * Closes the stream and releases any system resources associated with it. 
   * Closing a previously closed stream has no effect.
   * @return {@code false}, in case of an error; {@code true}, otherwise.
   */
  public boolean close();
}



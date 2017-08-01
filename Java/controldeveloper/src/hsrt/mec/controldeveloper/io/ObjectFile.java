package hsrt.mec.controldeveloper.io;

import hsrt.mec.controldeveloper.util.Log;
import hsrt.mec.controldeveloper.util.Message;
import hsrt.mec.controldeveloper.util.Status;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Vector;

/**
 * {@code ObjectFile} serializes the strings given by the {@link java.util.Vector} and writes them into the 
 * given file or reads them out of the file, respectively. important is that the classes,
 * whose objects shall be serialized, implement the interface {@link java.io.Serializable} (what the 
 * class String does). {@link java.io.Serializable} is a flag interface, i.e. it has no methods.   
 */
public class ObjectFile implements IOType 
{
  private File f= null;
  private ObjectInputStream  oIS= null;
  private ObjectOutputStream oOS= null;
  
  private boolean append= false;
  
  /**
   * Creates a new {@code ObjectFile} object based on a given {@link java.io.File} object. 
   * A file can be created in the actual working directory using: <br/><br/>
   * {@code new File( System.getProperty("user.dir") + "\\<filename>.<suffix>" );}

   * @param f The given file.
   * @param append {@code true}, if data shall be appended to the given file;
   * {@code false}, if a potentially existing file shall be overwritten.
   * 
   * @see java.io.File
   */
  public ObjectFile(File f, boolean append)
  {
   this.append= append;
   this.f= f;
  }
  
  
  /**
   * Reads data from the specified file. The data is provided by the {@link java.util.Vector} 
   * object given as an input parameter.
   * @param data The vector that contains the input data in a string format.
   * @return {@code false}, in case of an error; {@code true}, otherwise.
   *    
   * In case of an error information about the error can be accessed using class 
   * {@link hsrt.mec.controldeveloper.util.Status}.
   * 
   * @see hsrt.mec.controldeveloper.util.Status
   */
  public boolean read(Vector<String> data)
  {
    if (f==null){
      Status.getInstance().setStatus(Status.E_NOK, Message.mMISSING_FILE);
      return false;
    }
    if (oOS!=null){
      Status.getInstance().setStatus(Status.E_NOK, Message.mREAD_DATA_ERROR);
      return false;
    }  
    if (data==null){
      Status.getInstance().setStatus(Status.E_NOK, Message.mIO_DATA_ERROR);
      return false;
    }
         
    boolean status= true;
    try {
      // FileInputStream repr�sentiert einen strom von bytes. ObjectInputStream 
      // deserialisiert die vorab mittels der klasse ObjectOutputStream geschriebenen 
      // objekte.
      if (oIS==null)
        oIS= new ObjectInputStream( new FileInputStream( f ) );
     
      Object obj= null;
      // lese ein objekt nach dem anderen aus dem inputstream. das letzte 
      // object, welches gelesen wird, ist null. dieses muss allerdings explizit
      // geschrieben worden sein; andernfalls wird eine EOFException geworfen.
      while ( (obj= oIS.readObject()) != null )
        data.addElement( ((String) obj) );
    } catch (Exception e) {
      status= false;
     
      Status.getInstance().setStatus(Status.E_NOK, e.getMessage());
      Log.log(Status.getInstance().getMessage());
    }
   
    return status;
  }
  
  
  /**
   * Writes data to the specified file.
   * @param data The vector that contains the output data in a string format.
   * @return {@code false}, in case of an error; {@code true}, otherwise.
   * 
   * In case of an error information about the error can be accessed using class 
   * {@link hsrt.mec.controldeveloper.util.Status}.
   * 
   * @see hsrt.mec.controldeveloper.util.Status
   */  
  public boolean write(Vector<String> data){
    if (f==null){
      Status.getInstance().setStatus(Status.E_NOK, Message.mMISSING_FILE);
      return false;
    }
    if (oIS!=null){
      Status.getInstance().setStatus(Status.E_NOK, Message.mWRITE_DATA_ERROR);
      return false;
    }  
    if (data==null){
      Status.getInstance().setStatus(Status.E_NOK, Message.mIO_DATA_ERROR);
      return false;
    }
         
    boolean status= true;
    try {
      // FileOutputStream repr�sentiert einen strom von bytes. ObjectOutputStream 
      // serialisiert objekte und schreibt diese in den outputstream.
      if (oOS==null)
        oOS= new ObjectOutputStream(new FileOutputStream(f, append));
     
      // die zu schreibenden zeichenketten werden als String-objekte in die datei 
      // geschrieben.
      for (int i=0; i<data.size(); i++)
        oOS.writeObject(data.elementAt(i));
    } catch (Exception e){
      status= false;
      
      Status.getInstance().setStatus(Status.E_NOK, e.getMessage());
      Log.log(Status.getInstance().getMessage());
    }
   
    return status;
  }
  
  /**
   * Closes the specified file. Closing a previously closed file has 
   * no effect.
   * 
   * In case of an error information about the error can be accessed using class 
   * {@link hsrt.mec.controldeveloper.util.Status}.
   * 
   * @see hsrt.mec.controldeveloper.util.Status
   */
  public boolean close()
  {
    boolean status= true;
    
    try
    {
      if (oIS!=null)
      {
        oIS.close();
      }
      
      if (oOS!=null){
        // als letztes objekt wird "null" geschrieben, damit beim einlesen das
        // dateiende erkannt wird. 
        oOS.writeObject(null);
      
        // schreibe eventuell noch gepufferte daten. 
        oOS.flush();
        oOS.close();
      }
    } 
    catch (Exception e)
    {
      status= false;
      
      Status.getInstance().setStatus(Status.E_NOK, e.getMessage());
      Log.log(Status.getInstance().getMessage());
    } 
    finally 
    {
      oIS= null;
      oOS= null;
    }
    
    return status;
  }
  
  
  public static void main(String[] s)
  {
    File f= new File( System.getProperty("user.dir") + "\\test.txt" );
    TextFile tF = new TextFile(f, true);

    Vector<String> v= new Vector<String>();
    v.addElement("Datensatz 1");
    v.addElement("Datensatz 2");

    tF.write(v);
    v.removeAllElements();
    v.addElement("Datensatz 3");
    tF.write(v);
  }
}

package hsrt.mec.controldeveloper.io;

import hsrt.mec.controldeveloper.util.Message;
import hsrt.mec.controldeveloper.util.Status;
import hsrt.mec.controldeveloper.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.Vector;

/**
 * {@code TextFile} writes strings into a plain text file. After every string a carriage 
 * return is written. This enables to read every single row out of the file.
 */
public class TextFile implements IOType {
  private File f= null;
  private BufferedReader bR;
  private BufferedWriter bW;
  
  private boolean append= false;
  
  /**
   * Creates a new {@code TextFile} object based on a given {@link java.io.File} object. 
   * A file can be created in the actual working directory using: <br/><br/>
   * {@code new File( System.getProperty("user.dir") + "\\<filename>.<suffix>" );}

   * @param f The given file.
   * @param append {@code true}, if data shall be appended to the given file;
   * {@code false}, if a potentially existing file shall be overwritten.
   * 
   * @see java.io.File
   */
  public TextFile(File f, boolean append){
    this.f= f;
    this.append= append;
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
  public boolean read(Vector<String> data){
    if (f==null){
      Status.getInstance().setStatus(Status.E_NOK, Message.mMISSING_FILE);
      return false;
    }
    if (bW!=null){
      Status.getInstance().setStatus(Status.E_NOK, Message.mREAD_DATA_ERROR);
      return false;
    }  
    if (data==null){
      Status.getInstance().setStatus(Status.E_NOK, Message.mIO_DATA_ERROR);
      return false;
    }
         
    boolean status= true;
    try {
      // FileReader ist eine hilfsklasse zum auslesen von dateien mit character 
      // zeichen. BufferedReader liest diese zeichen zeilenweise aus dem zeichen-
      // strom.
      if (bR==null)
        bR= new BufferedReader(new FileReader(f));
      
      String zeile= null;
      // lese den inputstream zeilenweise bis zu einem carriage-return aus.
      while ((zeile= bR.readLine()) != null)
        data.addElement(zeile);
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
    if (bR!=null){
      Status.getInstance().setStatus(Status.E_NOK, Message.mWRITE_DATA_ERROR);
      return false;
    }  
    if (data==null){
      Status.getInstance().setStatus(Status.E_NOK, Message.mIO_DATA_ERROR);
      return false;
    }
      
    boolean status= true;
    try {
      // FileWriter ist eine hilfsklasse zum schreiben von character-dateien. 
      // BufferedWriter schreibt text in einen output-stream.
      if (bW==null)
        bW= new BufferedWriter(new FileWriter(f, append));
      
      for (int i=0; i<data.size(); i++){
        // die zu schreibenden zahlen werden aus dem vector gelesen und  
        // in einen puffer geschrieben.
        bW.write(data.elementAt(i));
        // die zeichenfolge wird mit einem carriage-return abgeschlossen.
        bW.newLine();
      }
    } catch (Exception e){
      status= false;
      
      Status.getInstance().setStatus(Status.E_NOK, Message.mWRITE_DATA_ERROR);
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
  public boolean close(){
    boolean status= true;
    
    try{
      if (bR!=null){
        bR.close();
      }
      
      if (bW!=null){
        // schreibe eventuell noch gepufferte daten.
        bW.flush();
        bW.close();
      }
    } catch (Exception e){
      status= false;
      
      Status.getInstance().setStatus(Status.E_NOK, e.getMessage());
      Log.log(Status.getInstance().getMessage());
    } finally {
      bR= null;
      bW= null;
    }
    
    return status;  
  }
  
  
  public static void main(String[] s){
    File f= new File( System.getProperty("user.dir") + "\\<filename>.<suffix>" );
    TextFile tF= new TextFile(f, true);

    Vector<String> v= new Vector<String>();
    v.addElement("Datensatz 1");
    v.addElement("Datensatz 2");

    tF.write(v);
    v.removeAllElements();
    v.addElement("Datensatz 3");
    tF.write(v);
  }
}

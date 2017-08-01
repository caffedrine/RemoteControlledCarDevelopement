package hsrt.mec.controldeveloper.io;

import hsrt.mec.controldeveloper.util.Message;
import hsrt.mec.controldeveloper.util.Status;
import hsrt.mec.controldeveloper.util.Log;

import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Vector;

/**
 * {@code Console} reads strings from the keyboard and writes strings to 
 * the console. Every string is written with a carriage return at the end.
 */
public class Console implements IOType {
  private BufferedReader bR;
  private BufferedWriter bW;
  
  public Console(){
  }
  

  /**
   * Reads data from from the keyboard. The data is provided by the {@link java.util.Vector} 
   * object given as an input parameter.
   * @param data The vector in which the input data is written in a string format
   * @return {@code false}, in case of an error; {@code true}, otherwise
   * In case of an error information about the error can be accessed using 
   * class {@link hsrt.mec.controldeveloper.util.Status}.
   * 
   * @see hsrt.mec.controldeveloper.util.Status
   */
  public boolean read(Vector<String> data){
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
      // System.in = klassenvariable vom typ InputStream = stream von bytes.
      // die klasse InputStreamReader konvertiert bytes in char zeichen
      // die klasse BufferedReader dient dem effizienten lesen (gepuffert) von 
      // zeichen. die eingabe wird durch eine leere eingabe ("") beendet.
      bR= new BufferedReader( new InputStreamReader(System.in) );

      String s= " ";
      while( s.length()>0 ){
        s= bR.readLine();
        data.addElement(s);
      }
    } catch (Exception e) {
      status= false;
      
      Status.getInstance().setStatus(Status.E_NOK, e.getMessage());
      Log.log(Status.getInstance().getMessage());
    }
    
    return status;
  }
  
  /**
   * Writes data to the console. In case of an error information about the error 
   * can be accessed using class {@link hsrt.mec.controldeveloper.util.Status}.
   * @param data The vector that contains the output data in a string format
   * @return {@code false}, in case of an error; {@code true}, otherwise.
   * 
   * In case of an error information about the error can be accessed using class 
   * {@link hsrt.mec.controldeveloper.util.Status}.
   *  
   * @see hsrt.mec.controldeveloper.util.Status
   */
  public boolean write(Vector<String> data){
    if (data==null){
      Status.getInstance().setStatus(Status.E_NOK, Message.mIO_DATA_ERROR);
      return false;
    }
          
    for (int i=0; i<data.size(); i++){
      // die zu schreibenden zeichenkette werden aus dem vector zeilenweise
      // auf die konsole ausgegeben und mit einem carriage-return abgeschlossen.
      System.out.println(data.elementAt(i));
    }
    
    return true;
  }
  

  /**
   * Closes the input stream and releases any system resources 
   * associated with it. Closing a previously closed stream has 
   * no effect.
   * @return {@code false}, in case of an error; {@code true}, otherwise.
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
    } catch (Exception e){
      status= false;
      
      Status.getInstance().setStatus(Status.E_NOK, e.getMessage());
      Log.log(Status.getInstance().getMessage());
    } finally {
      bR= null;
    }

    return status;  
  }
  
  
  public static void main(String[] s){
    System.out.println("Zeicheneingabe: ");
    Console tF= new Console();

    Vector<String> v= new Vector<String>();
    tF.read(v);
    
    System.out.println("Zeichenausgabe: ");
    tF.write(v);
  }
  
}

package hsrt.mec.controldeveloper.io;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import java.util.Vector;

import hsrt.mec.controldeveloper.core.com.ComPort;
import hsrt.mec.controldeveloper.util.Log;
import hsrt.mec.controldeveloper.util.Message;
import hsrt.mec.controldeveloper.util.Status;

import java.util.Enumeration;

/**
 * {@code SerialUSB} serializes the strings given by the vector and writes them
 * to the usb interface. For a proper function the class needs a
 * {@link hsrt.mec.controldeveloper.core.com.ComPort} object which can be
 * retrieved using a {@link hsrt.mec.controldeveloper.core.com.ComPortHandler}. <br/>
 * <br/>
 * The {@code SerialUSB} object is used by
 * {@link hsrt.mec.controldeveloper.core.com.ComHandler} in order to start the
 * command execution process.
 */
public final class SerialUSB implements SerialPortEventListener, IOType
{
	private SerialPort serialPort; // port normally going to use
	
	private OutputStream comOutputStream;
	private BufferedReader comInputStream;
	
	private final int TIME_OUT = 2000; // ms to block while waiting for port
										// open
	private final int DATA_RATE = 9600; // default bits per second for COM port
	
	/**
	 * Creates a new {@code SerialUSB} object based on a given communication
	 * port (object of class {@link hsrt.mec.controldeveloper.core.com.ComPort}
	 * ). Available {@link hsrt.mec.controldeveloper.core.com.ComPort} objects
	 * are provided through class
	 * {@link hsrt.mec.controldeveloper.core.com.ComPortHandler}.
	 * 
	 * @param selPort
	 *            the selected communication port.
	 * 
	 * @see hsrt.mec.controldeveloper.core.com.ComPort
	 * @see hsrt.mec.controldeveloper.core.com.ComPortHandler
	 * @see hsrt.mec.controldeveloper.core.com.ComHandler
	 */
	public SerialUSB(ComPort selPort)
	{
		if (selPort == null)
		{
			Status.getInstance().setStatus(Status.E_NOK,
					Message.mMISSING_COM_PORT);
			return;
		}
		
		try
		{
			Log.log("Assigning Com-Port");
			Enumeration<?> portIDs = CommPortIdentifier.getPortIdentifiers();
			CommPortIdentifier portID = null;
			while (portIDs.hasMoreElements())
			{
				CommPortIdentifier actPortID = (CommPortIdentifier) portIDs
						.nextElement();
				
				if (actPortID.getName().equals(selPort.getName()))
				{
					portID = actPortID;
					break;
				}
			}
			
			// open serial port and use class name for the appName
			String className = this.getClass().getName();
			serialPort = (SerialPort) portID.open(className, TIME_OUT);
			// set port parameters
			serialPort.setSerialPortParams(DATA_RATE, SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			
			// open input and output com stream and write first char
			comInputStream = new BufferedReader(new InputStreamReader(
					serialPort.getInputStream()));
			comOutputStream = serialPort.getOutputStream();
			char ch = 1;
			comOutputStream.write(ch);
			
			// add event listeners
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
			Status.getInstance().setStatus(Status.E_OK,
					"SerialUSB Port established...");
		}
		catch (Throwable e)
		{
			Status.getInstance().setStatus(Status.E_NOK, e.toString());
			close();
		}
		finally
		{
			Log.log(Status.getInstance().getMessage());
		}
	}
	
	/**
	 * Writes data to the selected serial communication port.
	 * 
	 * @param data
	 *            The vector that contains the output data in a string format.
	 * @return {@code false}, in case of an error; {@code true}, otherwise.
	 * 
	 *         In case of an error information about the error can be accessed
	 *         using class {@link hsrt.mec.controldeveloper.util.Status}.
	 * 
	 * @see hsrt.mec.controldeveloper.util.Status
	 */
	public boolean write(Vector<String> data)
	{
		if (data == null)
		{
			Status.getInstance()
					.setStatus(Status.E_NOK, Message.mIO_DATA_ERROR);
			return false;
		}
		
		try
		{
			for (String element : data)
				comOutputStream.write(element.getBytes());
			
		}
		catch (Exception e)
		{
			Status.getInstance().setStatus(Status.E_NOK, e.toString());
			return false;
		}
		
		return true;
	}
	
	/**
	 * Reads data from the specified serial communication port. The data is
	 * provided by the {@link java.util.Vector} object given as an input
	 * parameter.
	 * 
	 * @param data
	 *            The vector that contains the input data in a string format.
	 * @return {@code false}, in case of an error; {@code true}, otherwise.
	 * 
	 *         In case of an error information about the error can be accessed
	 *         using class {@link hsrt.mec.controldeveloper.util.Status}.
	 * 
	 * @see hsrt.mec.controldeveloper.util.Status
	 */
	public boolean read(Vector<String> data)
	{
		if (data == null)
		{
			Status.getInstance()
					.setStatus(Status.E_NOK, Message.mIO_DATA_ERROR);
			return false;
		}
		
		try
		{
			String zeile = null;
			// lese den inputstream zeilenweise bis zu einem carriage-return
			// aus.
			while ((zeile = comInputStream.readLine()) != null)
				data.addElement(zeile);
		}
		catch (Exception e)
		{
			return false;
		}
		
		return true;
	}
	
	/**
	 * Closes the specified serial communication port. Closing a previously
	 * closed communication port has no effect.
	 * 
	 * In case of an error information about the error can be accessed using
	 * class {@link hsrt.mec.controldeveloper.util.Status}.
	 * 
	 * @see hsrt.mec.controldeveloper.util.Status
	 */
	public boolean close()
	{
		if (serialPort == null)
			return false;
		
		try
		{
			serialPort.removeEventListener();
			serialPort.close();
		}
		catch (Exception e)
		{
			Status.getInstance().setStatus(Status.E_NOK, e.getMessage());
			Log.log(Status.getInstance().getMessage());
			
			return false;
		}
		
		return true;
	}
	
	/**
	 * Implementation of interface {@code SerialPortEventListener}. The listener
	 * is called in case of new input data. <br/>
	 * <br/>
	 * In this version there is no implementation here.
	 * 
	 * @param serialPortEvent
	 *            Event of type SerialPortEvent
	 */
	public void serialEvent(SerialPortEvent serialPortEvent)
	{
		if (serialPortEvent.getEventType() != SerialPortEvent.DATA_AVAILABLE)
			return;
		
		try
		{
			// for future purpose
			// String inputData = comInputStream.readLine();
		}
		catch (Exception e)
		{
			Status.getInstance().setStatus(Status.E_NOK, e.toString());
		}
		
		Status.getInstance().setStatus(Status.E_OK, "");
	}
}